/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta.Type;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.Conflict;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.Pair;
import com.b2international.snowowl.core.merge.MergeConflict;
import com.b2international.snowowl.datastore.cdo.AbstractCDOConflictProcessor;
import com.b2international.snowowl.datastore.cdo.AddedInSourceAndDetachedInTargetConflict;
import com.b2international.snowowl.datastore.cdo.AddedInSourceAndTargetConflict;
import com.b2international.snowowl.datastore.cdo.AddedInTargetAndDetachedInSourceConflict;
import com.b2international.snowowl.datastore.cdo.ICDOConflictProcessor;
import com.b2international.snowowl.datastore.server.snomed.merge.SnomedMergeConflictMapper;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
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
public class SnomedCDOConflictProcessor extends AbstractCDOConflictProcessor implements ICDOConflictProcessor {

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
	
	private Map<String, CDOID> newComponentIdsInSource;
	private Set<CDOID> detachedSourceIds;
	private Map<String, CDOID> newComponentIdsInTarget;
	private Set<CDOID> detachedTargetIds;

	private boolean isRebase;

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
		if (isRebase) {
			Conflict conflict = checkDuplicateComponentIds(sourceRevision, newComponentIdsInTarget);
			
			if (conflict != null) {
				return conflict;
			}
			
			conflict = checkDetachedReferences(sourceRevision.getID(), newSourceRevisionIdToFeatureIdMap.get(sourceRevision.getID()), detachedTargetIds);
			
			if (conflict != null) {
				return conflict;
			}
		}
		
		return super.addedInSource(sourceRevision, targetMap);
	}
	
	@Override
	public Object addedInTarget(final CDORevision targetRevision, final Map<CDOID, Object> sourceMap) {
		
		if (!isRebase) {
			
			Conflict conflict = checkDuplicateComponentIds(targetRevision, newComponentIdsInSource);
			
			if (conflict != null) {
				return conflict;
			}
			
			conflict = checkDetachedReferences(targetRevision.getID(), newTargetRevisionIdToFeatureIdMap.get(targetRevision.getID()), detachedSourceIds);
			
			if (conflict != null) {
				return conflict;
			}
			
		}
		
		return super.addedInTarget(targetRevision, sourceMap);
	}
	
	@Override
	public Object detachedInSource(CDOID id) {
		
		if (isRebase) {
			Conflict conflict = checkDetachedReferences(newTargetRevisionIdToFeatureIdMap, id);
			
			if (conflict != null) {
				return conflict;
			}
		}
		
		return super.detachedInSource(id);
	}
	
	@Override
	public Object detachedInTarget(CDOID id) {
		
		if (!isRebase) {
			Conflict conflict = checkDetachedReferences(newSourceRevisionIdToFeatureIdMap, id);
			
			if (conflict != null) {
				return conflict;
			}
		}
		
		return super.detachedInTarget(id);
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
	public Collection<MergeConflict> handleCDOConflicts(final CDOView sourceView, final CDOView targetView, final Map<CDOID, Conflict> conflicts) {
		if (!conflicts.isEmpty()) {
			return FluentIterable.from(conflicts.values()).transform(new Function<Conflict, MergeConflict>() {
				@Override public MergeConflict apply(Conflict input) {
					return SnomedMergeConflictMapper.convert(input, sourceView, targetView);
				}
			}).toList();
		}
		return super.handleCDOConflicts(sourceView, targetView, conflicts);
	}
	
	@Override
	public void preProcess(final Map<CDOID, Object> sourceMap, final Map<CDOID, Object> targetMap, boolean isRebase) {
		this.isRebase = isRebase;
		
		Collection<InternalCDORevision> newSourceComponentRevisions = extractNewComponentRevisions(sourceMap);
		Collection<InternalCDORevision> newTargetComponentRevisions = extractNewComponentRevisions(targetMap);

		newSourceRevisionIdToFeatureIdMap = extractNewRevisionIdToFeatureIdMap(newSourceComponentRevisions);
		newTargetRevisionIdToFeatureIdMap = extractNewRevisionIdToFeatureIdMap(newTargetComponentRevisions);
		
		newComponentIdsInSource = extractNewComponentIds(newSourceComponentRevisions);
		newComponentIdsInTarget = extractNewComponentIds(newTargetComponentRevisions);
		
		detachedSourceIds = getDetachedIdsInTarget(sourceMap);
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

	private Conflict checkDetachedReferences(final CDOID revisionId, final Collection<Pair<EStructuralFeature, CDOID>> featureIds, final Set<CDOID> detachedIds) {
		for (Pair<EStructuralFeature, CDOID> featureAndId : featureIds) {
			if (detachedIds.contains(featureAndId.getB())) {
				if (isRebase) {
					return new AddedInSourceAndDetachedInTargetConflict(revisionId, featureAndId.getB(), featureAndId.getA().getName());
				} else {
					return new AddedInTargetAndDetachedInSourceConflict(featureAndId.getB(), revisionId, featureAndId.getA().getName());
				}
			}
		}
		return null;
	}
	
	private Conflict checkDetachedReferences(Multimap<CDOID, Pair<EStructuralFeature, CDOID>> newRevisionIdToFeatureIdMap, CDOID id) {
		for (Entry<CDOID, Collection<Pair<EStructuralFeature, CDOID>>> entry : newRevisionIdToFeatureIdMap.asMap().entrySet()) {
			for (Pair<EStructuralFeature, CDOID> featureAndId : entry.getValue()) {
				if (featureAndId.getB().equals(id)) {
					if (isRebase) {
						return new AddedInTargetAndDetachedInSourceConflict(id, entry.getKey(), featureAndId.getA().getName());
					} else {
						return new AddedInSourceAndDetachedInTargetConflict(entry.getKey(), id, featureAndId.getA().getName());
					}
				}
			}
		}
		return null;
	}

}
