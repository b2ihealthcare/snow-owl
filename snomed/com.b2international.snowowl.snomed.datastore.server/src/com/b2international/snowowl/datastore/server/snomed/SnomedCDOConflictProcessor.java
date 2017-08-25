/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.snowowl.datastore.server.snomed;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta.Type;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.ChangedInSourceAndDetachedInTargetConflict;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.ChangedInSourceAndTargetConflict;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.ChangedInTargetAndDetachedInSourceConflict;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.Conflict;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.Pair;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.core.merge.ConflictingAttribute;
import com.b2international.snowowl.core.merge.ConflictingAttributeImpl;
import com.b2international.snowowl.core.merge.MergeConflict;
import com.b2international.snowowl.core.merge.MergeConflict.ConflictType;
import com.b2international.snowowl.core.merge.MergeConflictImpl;
import com.b2international.snowowl.datastore.server.cdo.AbstractCDOConflictProcessor;
import com.b2international.snowowl.datastore.server.cdo.AddedInSourceAndDetachedInTargetConflict;
import com.b2international.snowowl.datastore.server.cdo.AddedInSourceAndTargetConflict;
import com.b2international.snowowl.datastore.server.cdo.AddedInTargetAndDetachedInSourceConflict;
import com.b2international.snowowl.datastore.server.cdo.ICDOConflictProcessor;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

/**
 * An {@link ICDOConflictProcessor} implementation handling conflicts specific to the SNOMED CT terminology model.
 */
public class SnomedCDOConflictProcessor extends AbstractCDOConflictProcessor {

	private static final Set<EClass> COMPONENT_CLASSES = ImmutableSet.of(
			SnomedPackage.Literals.CONCEPT, 
			SnomedPackage.Literals.DESCRIPTION, 
			SnomedPackage.Literals.RELATIONSHIP);

	private static final Multimap<EClass, EStructuralFeature> DETACHED_FEATURE_MAP = ImmutableMultimap.<EClass, EStructuralFeature>builder()
			.put(SnomedPackage.Literals.RELATIONSHIP, SnomedPackage.Literals.RELATIONSHIP__SOURCE)
			.put(SnomedPackage.Literals.RELATIONSHIP, SnomedPackage.Literals.RELATIONSHIP__TYPE)
			.put(SnomedPackage.Literals.RELATIONSHIP, SnomedPackage.Literals.RELATIONSHIP__DESTINATION)
			.put(SnomedPackage.Literals.DESCRIPTION, SnomedPackage.Literals.DESCRIPTION__CONCEPT)
			.put(SnomedPackage.Literals.DESCRIPTION, SnomedPackage.Literals.DESCRIPTION__TYPE)
			.build();

	private static final Map<EClass, EAttribute> RELEASED_ATTRIBUTE_MAP = ImmutableMap.of(
			SnomedPackage.Literals.COMPONENT, 
			SnomedPackage.Literals.COMPONENT__RELEASED,
			SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER, 
			SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER__RELEASED);
	
	private static final Set<EStructuralFeature> EFFECTIVE_TIME_FEATURES = ImmutableSet.<EStructuralFeature>of(
			SnomedPackage.Literals.COMPONENT__EFFECTIVE_TIME,
			SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER__EFFECTIVE_TIME,
			SnomedRefSetPackage.Literals.SNOMED_MODULE_DEPENDENCY_REF_SET_MEMBER__SOURCE_EFFECTIVE_TIME,
			SnomedRefSetPackage.Literals.SNOMED_MODULE_DEPENDENCY_REF_SET_MEMBER__TARGET_EFFECTIVE_TIME);

	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedCDOConflictProcessor.class);
	
	private Map<String, CDOID> newComponentIdsInTarget;
	private Set<CDOID> detachedTargetIds;

	private Multimap<CDOID, Pair<EStructuralFeature, CDOID>> newSourceRevisionIdToFeatureIdMap;
	private Multimap<CDOID, Pair<EStructuralFeature, CDOID>> newTargetRevisionIdToFeatureIdMap;

	public SnomedCDOConflictProcessor() {
		super(SnomedDatastoreActivator.REPOSITORY_UUID, RELEASED_ATTRIBUTE_MAP);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * SNOMED CT-specific conflict processing will report a conflict if:
	 * <ul>
	 * <li>a new object on target has the same SNOMED CT component identifier as the new object on source 
	 * <li>a detached object on target is referenced by the new object on source
	 * </ul>
	 * The addition is allowed through in all other cases.
	 */
	@Override
	public Object addedInSource(final CDORevision sourceRevision, final Map<CDOID, Object> targetMap) {
		Conflict conflict = checkDuplicateComponentIds(sourceRevision, newComponentIdsInTarget);
		
		if (conflict != null) {
			return conflict;
		}
		
		conflict = checkDetachedReferences(sourceRevision.getID(), newSourceRevisionIdToFeatureIdMap.get(sourceRevision.getID()));
		
		if (conflict != null) {
			return conflict;
		}
		
		return super.addedInSource(sourceRevision, targetMap);
	}
	
	@Override
	public Object detachedInSource(CDOID id) {
		Conflict conflict = checkDetachedReferences(id);
		
		if (conflict != null) {
			return conflict;
		}
		
		return super.detachedInSource(id);
	}
	
	@Override
	public CDOFeatureDelta changedInSourceAndTargetSingleValued(CDOFeatureDelta targetFeatureDelta, CDOFeatureDelta sourceFeatureDelta) {
		final EStructuralFeature feature = targetFeatureDelta.getFeature();
		
		if (EFFECTIVE_TIME_FEATURES.contains(feature)) {
		
			if (Type.UNSET.equals(targetFeatureDelta.getType())) {
				return targetFeatureDelta;
			} else if (Type.UNSET.equals(sourceFeatureDelta.getType())) {
				return sourceFeatureDelta;
			}
			
			// Fall-through
		}
		
		return super.changedInSourceAndTargetSingleValued(targetFeatureDelta, sourceFeatureDelta);
	}
	
	@Override
	protected MergeConflict convert(final ChangedInSourceAndTargetConflict conflict, final CDOView sourceView, final CDOView targetView) {
		return MergeConflictImpl.builder()
				.componentId(getComponentId(targetView, conflict.getTargetDelta().getID()))
				.componentType(getType(targetView, conflict.getTargetDelta().getID()))
				.conflictingAttributes(getConflictingAttributes(conflict.getTargetDelta(), targetView))
				.type(ConflictType.CONFLICTING_CHANGE)
				.build();
	}

	@Override
	protected MergeConflict convert(final ChangedInSourceAndDetachedInTargetConflict conflict, final CDOView sourceView, final CDOView targetView) {
		return MergeConflictImpl.builder()
				.componentId(getComponentId(sourceView, conflict.getSourceDelta().getID()))
				.componentType(getType(sourceView, conflict.getSourceDelta().getID()))
				.conflictingAttributes(getConflictingAttributes(conflict.getSourceDelta(), sourceView))
				.type(ConflictType.DELETED_WHILE_CHANGED)
				.build();
	}

	@Override
	protected MergeConflict convert(final ChangedInTargetAndDetachedInSourceConflict conflict, final CDOView sourceView, final CDOView targetView) {
		return MergeConflictImpl.builder()
				.componentId(getComponentId(targetView, conflict.getTargetDelta().getID()))
				.componentType(getType(targetView, conflict.getTargetDelta().getID()))
				.conflictingAttributes(getConflictingAttributes(conflict.getTargetDelta(), targetView))
				.type(ConflictType.CHANGED_WHILE_DELETED)
				.build();
	}

	@Override
	protected MergeConflict convert(final AddedInSourceAndTargetConflict conflict, final CDOView sourceView, final CDOView targetView) {
		return MergeConflictImpl.builder()
				.componentId(getComponentId(targetView, conflict.getTargetId()))
				.componentType(getType(targetView, conflict.getTargetId()))
				.conflictingAttribute(ConflictingAttributeImpl.builder().property("id").build())
				.type(ConflictType.CONFLICTING_CHANGE)
				.build();
	}

	@Override
	protected MergeConflict convert(final AddedInSourceAndDetachedInTargetConflict conflict, final CDOView sourceView, final CDOView targetView) {
		return MergeConflictImpl.builder()
				.componentId(getComponentId(sourceView, conflict.getSourceId()))
				.componentType(getType(sourceView, conflict.getSourceId()))
				.type(ConflictType.CAUSES_MISSING_REFERENCE)
				.build();
	}
	
	@Override
	protected MergeConflict convert(final AddedInTargetAndDetachedInSourceConflict conflict, final CDOView sourceView, final CDOView targetView) {
		return MergeConflictImpl.builder()
				.componentId(getComponentId(targetView, conflict.getTargetId()))
				.componentType(getType(targetView, conflict.getTargetId()))
				.conflictingAttributes(
						Strings.isNullOrEmpty(conflict.getFeatureName()) ? Collections.<ConflictingAttribute> emptyList() : 
							Collections.<ConflictingAttribute> singletonList(ConflictingAttributeImpl.builder().property(conflict.getFeatureName()).build()))
				.type(ConflictType.HAS_MISSING_REFERENCE)
				.build();
	}

	private static String getComponentId(final CDOView view, final CDOID id) {
		try {
			final CDOObject object = view.getObject(id);
			if (object != null) {
				if (object instanceof Component) {
					return ((Component) object).getId();
				} else if (object instanceof SnomedRefSetMember) {
					return ((SnomedRefSetMember) object).getUuid();
				}
			}
		} catch (final ObjectNotFoundException e) {
			// fall through
		}
		return id.toString();
	}

	private static String getType(final CDOView view, final CDOID id) {
		try {
			final CDOObject object = view.getObject(id);
			return object.eClass().getName();
		} catch (final ObjectNotFoundException e) {
			// fall through
		}
		return null;
	}
	
	private static List<ConflictingAttribute> getConflictingAttributes(final CDORevisionDelta cdoRevisionDelta, final CDOView view) {
		return FluentIterable.from(cdoRevisionDelta.getFeatureDeltas()).transform(new Function<CDOFeatureDelta, ConflictingAttribute>() {
			@Override 
			public ConflictingAttribute apply(CDOFeatureDelta featureDelta) {
				
				String property = featureDelta.getFeature().getName();
				String oldValue = null;
				String newValue = null;
				
				if (featureDelta instanceof CDOSetFeatureDelta) {
					CDOSetFeatureDelta setFeatureDelta = (CDOSetFeatureDelta) featureDelta;
					newValue = convertValue(setFeatureDelta.getValue(), view);
					oldValue = convertValue(setFeatureDelta.getOldValue(), view);
				}
				return ConflictingAttributeImpl.builder()
							.property(property)
							.oldValue(oldValue)
							.value(newValue)
							.build();
			}

		}).toList();
	}

	private static String convertValue(final Object value, final CDOView view) {
		if (value instanceof CDOID) {
			// try to resolve id
			CDOID cdoId = (CDOID) value;
			String componentId = getComponentId(view, cdoId);
			if (!componentId.equals(cdoId.toString())) {
				return componentId;
			}
		} else {
			if (value instanceof Date) {
				return Dates.formatByHostTimeZone(value, DateFormats.SHORT);
			} else if (value != null) {
				return value.toString();
			}
		}
		return null;
	}

	
	@Override
	public void preProcess(final Map<CDOID, Object> sourceMap, final Map<CDOID, Object> targetMap) {
		Collection<InternalCDORevision> newSourceComponentRevisions = extractNewComponentRevisions(sourceMap);
		Collection<InternalCDORevision> newTargetComponentRevisions = extractNewComponentRevisions(targetMap);

		newSourceRevisionIdToFeatureIdMap = extractNewRevisionIdToFeatureIdMap(newSourceComponentRevisions);
		newTargetRevisionIdToFeatureIdMap = extractNewRevisionIdToFeatureIdMap(newTargetComponentRevisions);
		
		newComponentIdsInTarget = extractNewComponentIds(newTargetComponentRevisions);
		detachedTargetIds = getDetachedIdsInTarget(targetMap);
	}
	
	private Multimap<CDOID, Pair<EStructuralFeature, CDOID>> extractNewRevisionIdToFeatureIdMap(Collection<InternalCDORevision> newComponentRevisions) {
		Multimap<CDOID, Pair<EStructuralFeature, CDOID>> revisionToFeatureIdMap = HashMultimap.<CDOID, Pair<EStructuralFeature, CDOID>>create();
		for (InternalCDORevision internalCDORevision : newComponentRevisions) {
			Collection<EStructuralFeature> featuresToCheck = DETACHED_FEATURE_MAP.get(internalCDORevision.getEClass());
			for (EStructuralFeature feature : featuresToCheck) {
				CDOID id = (CDOID) internalCDORevision.getValue(feature);
				if (id != null) {
					revisionToFeatureIdMap.put(internalCDORevision.getID(), Pair.of(feature, id));
				}
			}
		}
		return revisionToFeatureIdMap;
	}

	private Collection<InternalCDORevision> extractNewComponentRevisions(final Map<CDOID, Object> revisionMap) {
		return FluentIterable.from(getNewRevisionsInTarget(revisionMap)).filter(new Predicate<InternalCDORevision>() {
			@Override public boolean apply(InternalCDORevision input) {
				return isComponent(input);
			}
		}).toSet();
	}
	
	private Map<String, CDOID> extractNewComponentIds(Collection<InternalCDORevision> newComponentRevisions) {
		final Map<String, CDOID> newComponentIdsMap = Maps.newHashMap();
		for (final InternalCDORevision revision : newComponentRevisions) {
			newComponentIdsMap.put(getComponentId(revision), revision.getID());
		}
		return newComponentIdsMap;
	}
	
	@Override
	protected void unlinkObject(final CDOObject object) {
	
		if (object instanceof Relationship) {
			((Relationship) object).setSource(null);
			((Relationship) object).setDestination(null);
		} else if (object instanceof SnomedRefSetMember) {
			super.unlinkObject(object);
		} else {
			LOGGER.warn("Unexpected CDO object not unlinked: {}.", object);
		}
	}

	private Conflict checkDuplicateComponentIds(final CDORevision revision, final Map<String, CDOID> newComponentIdsMap) {

		if (isComponent(revision)) {
			final String newComponentId = getComponentId((InternalCDORevision) revision);
			final CDOID conflictingNewId = newComponentIdsMap.get(newComponentId);

			if (null != conflictingNewId) {
				return new AddedInSourceAndTargetConflict(revision.getID(), conflictingNewId, String.format(
						"Two SNOMED CT %ss are using the same '%s' identifier.", revision.getEClass().getName(), newComponentId));
			}
		}

		return null;
	}

	private boolean isComponent(final CDORevision revision) {
		return isComponent(revision.getEClass());
	}

	private boolean isComponent(final EClass eClass) {
		return COMPONENT_CLASSES.contains(eClass);
	}

	private String getComponentId(final InternalCDORevision revision) {
		return (String) revision.getValue(SnomedPackage.Literals.COMPONENT__ID);
	}

	private Conflict checkDetachedReferences(final CDOID addedSourceId, final Collection<Pair<EStructuralFeature, CDOID>> featureIds) {
		for (Pair<EStructuralFeature, CDOID> featureAndId : featureIds) {
			if (detachedTargetIds.contains(featureAndId.getB())) {
				return new AddedInSourceAndDetachedInTargetConflict(addedSourceId, featureAndId.getB(), featureAndId.getA().getName());
			}
		}
		return null;
	}
	
	private Conflict checkDetachedReferences(CDOID detachedSourceId) {
		for (Entry<CDOID, Collection<Pair<EStructuralFeature, CDOID>>> entry : newTargetRevisionIdToFeatureIdMap.asMap().entrySet()) {
			for (Pair<EStructuralFeature, CDOID> featureAndId : entry.getValue()) {
				if (featureAndId.getB().equals(detachedSourceId)) {
					return new AddedInTargetAndDetachedInSourceConflict(detachedSourceId, entry.getKey(), featureAndId.getA().getName());
				}
			}
		}
		return null;
	}

}
