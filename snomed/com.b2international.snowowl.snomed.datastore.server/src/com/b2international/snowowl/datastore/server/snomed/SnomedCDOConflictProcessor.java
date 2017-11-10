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

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta.Type;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.Conflict;

import com.b2international.commons.Pair;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.merge.MergeConflict;
import com.b2international.snowowl.datastore.cdo.AbstractCDOConflictProcessor;
import com.b2international.snowowl.datastore.cdo.AddedInSourceAndDetachedInTargetConflict;
import com.b2international.snowowl.datastore.cdo.AddedInSourceAndTargetConflict;
import com.b2international.snowowl.datastore.cdo.AddedInTargetAndDetachedInSourceConflict;
import com.b2international.snowowl.datastore.cdo.ICDOConflictProcessor;
import com.b2international.snowowl.datastore.cdo.IMergeConflictRule;
import com.b2international.snowowl.datastore.server.snomed.merge.SnomedMergeConflictMapper;
import com.b2international.snowowl.datastore.server.snomed.merge.rules.SnomedDonatedComponentResolverRule;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

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

	private boolean isRebase;
	
	private Map<String, InternalCDORevision> newComponentIdsInSource;
	private Multimap<CDOID, Pair<EStructuralFeature, CDOID>> newSourceRevisionIdToFeatureIdMap;
	private Set<CDOID> detachedSourceIds;
	
	private Map<String, InternalCDORevision> newComponentIdsInTarget;
	private Multimap<CDOID, Pair<EStructuralFeature, CDOID>> newTargetRevisionIdToFeatureIdMap;
	private Set<CDOID> detachedTargetIds;

	private Map<String, Pair<CDOID, CDOID>> donatedComponentsMap = newHashMap();
	private Set<String> donatedComponentIds = newHashSet();

	public SnomedCDOConflictProcessor() {
		super(SnomedDatastoreActivator.REPOSITORY_UUID, RELEASED_ATTRIBUTE_MAP);
	}
	
	@Override
	public void preProcess(final Map<CDOID, Object> sourceMap, final Map<CDOID, Object> targetMap, CDOBranch sourceBranch, CDOBranch targetBranch, boolean isRebase) {
		
		this.isRebase = isRebase;
		
		Map<CDOID, InternalCDORevision> newSourceComponentRevisions = extractNewComponentRevisions(sourceMap);
		Map<CDOID, InternalCDORevision> newTargetComponentRevisions = extractNewComponentRevisions(targetMap);

		newSourceRevisionIdToFeatureIdMap = extractNewRevisionIdToFeatureIdMap(newSourceComponentRevisions.values());
		newTargetRevisionIdToFeatureIdMap = extractNewRevisionIdToFeatureIdMap(newTargetComponentRevisions.values());
		
		newComponentIdsInSource = extractNewComponentIds(newSourceComponentRevisions.values());
		newComponentIdsInTarget = extractNewComponentIds(newTargetComponentRevisions.values());
		
		collectDonatedComponents(newSourceComponentRevisions, newTargetComponentRevisions, sourceBranch, targetBranch);
		
		detachedSourceIds = getDetachedIds(sourceMap);
		detachedTargetIds = getDetachedIds(targetMap);
	}

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
	public Collection<IMergeConflictRule> getConflictRules() {
		if (!donatedComponentsMap.isEmpty()) {
			super.getConflictRules().add(new SnomedDonatedComponentResolverRule(donatedComponentsMap.values()));
		}
		return super.getConflictRules();
	}
	
	private void collectDonatedComponents(Map<CDOID, InternalCDORevision> newSourceComponentRevisions, Map<CDOID, InternalCDORevision> newTargetComponentRevisions, CDOBranch sourceBranch, CDOBranch targetBranch) {
		
		Set<String> donatedDescriptionIdCandidates = newHashSet();
		Set<String> donatedRelationshipIdCandidates = newHashSet();
		
		for (String id : Sets.intersection(newComponentIdsInSource.keySet(), newComponentIdsInTarget.keySet())) {
			
			InternalCDORevision sourceRevision = newComponentIdsInSource.get(id);
			InternalCDORevision targetRevision = newComponentIdsInTarget.get(id);
			
			if (!CDOIDUtil.equals(sourceRevision.getID(), targetRevision.getID())) {
				
				CDOID sourceModule = (CDOID) sourceRevision.getValue(SnomedPackage.Literals.COMPONENT__MODULE);
				CDOID targetModule = (CDOID) targetRevision.getValue(SnomedPackage.Literals.COMPONENT__MODULE);
				
				if (!CDOIDUtil.equals(sourceModule, targetModule)) {
					
					if (isDescription(sourceRevision) && isDescription(targetRevision)) {
						
						donatedDescriptionIdCandidates.add(id);
						
					} else if (isRelationship(sourceRevision) && isRelationship(targetRevision)) {
						
						donatedRelationshipIdCandidates.add(id);
						
					} else { // must be concepts
						
						donatedComponentsMap.put(id, Pair.of(sourceRevision.getID(), targetRevision.getID()));
						
					}
				}
			}
		}
		
		if (!donatedDescriptionIdCandidates.isEmpty()) {
			
			Map<String, String> descriptionToConceptIdOnSourceMap = SnomedRequests.prepareSearchDescription()
				.filterByIds(donatedDescriptionIdCandidates)
				.all()
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, sourceBranch.getPathName())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.then( descriptions -> {
					return descriptions.getItems().stream().collect(toMap(SnomedDescription::getId, d -> d.getConceptId()));
				}).getSync();
			
			Map<String, String> descriptionToConceptIdOnTargetMap = SnomedRequests.prepareSearchDescription()
				.filterByIds(donatedDescriptionIdCandidates)
				.all()
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, targetBranch.getPathName())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.then( descriptions -> {
					return descriptions.getItems().stream().collect(toMap(SnomedDescription::getId, d -> d.getConceptId()));
				}).getSync();
			
			for (Entry<String, String> entry : descriptionToConceptIdOnSourceMap.entrySet()) {
				
				String descriptionId = entry.getKey();
				String conceptIdOnSource = entry.getValue();
				String conceptIdOnTarget = descriptionToConceptIdOnTargetMap.get(descriptionId);
				
				if (conceptIdOnSource.equals(conceptIdOnTarget)) {
					
					if (!donatedComponentsMap.keySet().contains(conceptIdOnSource)) {
						donatedComponentsMap.put(descriptionId,
								Pair.of(newComponentIdsInSource.get(descriptionId).getID(), newComponentIdsInTarget.get(descriptionId).getID()));
					} else {
						donatedComponentIds.add(descriptionId); // to avoid duplicate ID conflict
					}
					
				}
			}
			
		}
		
		if (!donatedRelationshipIdCandidates.isEmpty()) {
			
			Map<String, String> relationshipToSourceConceptIdOnSourceMap = SnomedRequests.prepareSearchRelationship()
				.filterByIds(donatedRelationshipIdCandidates)
				.all()
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, sourceBranch.getPathName())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.then( relationships -> {
					return relationships.getItems().stream().collect(toMap(SnomedRelationship::getId, d -> d.getSourceId()));
				}).getSync();
			
			Map<String, String> relationshipToSourceConceptIdOnTargetMap = SnomedRequests.prepareSearchRelationship()
				.filterByIds(donatedRelationshipIdCandidates)
				.all()
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, targetBranch.getPathName())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.then( relationships -> {
					return relationships.getItems().stream().collect(toMap(SnomedRelationship::getId, d -> d.getSourceId()));
				}).getSync();
			
			for (Entry<String, String> entry : relationshipToSourceConceptIdOnSourceMap.entrySet()) {
				
				String relationshipId = entry.getKey();
				String conceptIdOnSource = entry.getValue();
				String conceptIdOnTarget = relationshipToSourceConceptIdOnTargetMap.get(relationshipId);
				
				if (conceptIdOnSource.equals(conceptIdOnTarget)) {
					
					if (!donatedComponentsMap.keySet().contains(conceptIdOnSource)) {
						donatedComponentsMap.put(relationshipId,
								Pair.of(newComponentIdsInSource.get(relationshipId).getID(), newComponentIdsInTarget.get(relationshipId).getID()));
					} else {
						donatedComponentIds.add(relationshipId); // to avoid duplicate ID conflict
					}
					
				}
			}
			
		}
		
		donatedComponentIds.addAll(donatedComponentsMap.keySet());
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

	private Map<CDOID, InternalCDORevision> extractNewComponentRevisions(final Map<CDOID, Object> revisionMap) {
		return getNewRevisions(revisionMap).stream()
				.filter(revision -> isComponent(revision))
				.collect(toMap(InternalCDORevision::getID, r -> r));
	}
	
	private Map<String, InternalCDORevision> extractNewComponentIds(Collection<InternalCDORevision> newComponentRevisions) {
		return newComponentRevisions.stream().collect(toMap(revision -> getComponentId(revision), r -> r));
	}
	
	private Conflict checkDuplicateComponentIds(final CDORevision revision, final Map<String, InternalCDORevision> newComponentIdsToRevisionsMap) {

		if (isComponent(revision)) {
			
			final String revisionComponentId = getComponentId((InternalCDORevision) revision);
			final InternalCDORevision newComponentRevision = newComponentIdsToRevisionsMap.get(revisionComponentId);

			if (null != newComponentRevision && !donatedComponentIds.contains(revisionComponentId)) {
				return new AddedInSourceAndTargetConflict(revision.getID(), newComponentRevision.getID(),
						String.format("Two SNOMED CT %ss are using the same '%s' identifier", revision.getEClass().getName(), revisionComponentId));
			}
			
		}

		return null;
	}

	private boolean isComponent(final CDORevision revision) {
		return isComponent(revision.getEClass());
	}
	
	private boolean isDescription(final CDORevision revision) {
		return revision.getEClass().equals(SnomedPackage.Literals.DESCRIPTION);
	}
	
	private boolean isRelationship(final CDORevision revision) {
		return revision.getEClass().equals(SnomedPackage.Literals.RELATIONSHIP);
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