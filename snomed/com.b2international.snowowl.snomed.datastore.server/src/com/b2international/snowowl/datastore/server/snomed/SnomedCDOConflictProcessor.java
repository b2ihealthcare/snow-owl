/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta.Type;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.Conflict;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongSet;
import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.RevisionIndexRead;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.SnowOwlApplication;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.exceptions.MergeConflictException;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.index.RevisionDocument;
import com.b2international.snowowl.datastore.server.cdo.AbstractCDOConflictProcessor;
import com.b2international.snowowl.datastore.server.cdo.AddedInSourceAndDetachedInTargetConflict;
import com.b2international.snowowl.datastore.server.cdo.AddedInSourceAndTargetConflict;
import com.b2international.snowowl.datastore.server.cdo.GenericConflict;
import com.b2international.snowowl.datastore.server.cdo.ICDOConflictProcessor;
import com.b2international.snowowl.datastore.utils.ComponentUtils2;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.taxonomy.InvalidRelationship;
import com.b2international.snowowl.snomed.datastore.taxonomy.SnomedTaxonomyBuilder;
import com.b2international.snowowl.snomed.datastore.taxonomy.SnomedTaxonomyBuilderResult;
import com.b2international.snowowl.snomed.datastore.taxonomy.SnomedTaxonomyUpdateRunnable;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.inject.Provider;

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
			SnomedPackage.Literals.COMPONENT, SnomedPackage.Literals.COMPONENT__RELEASED,
			SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER, SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER__RELEASED);

	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedCDOConflictProcessor.class);
	
	private Map<String, CDOID> newComponentIdsInSource;
	private Set<CDOID> detachedSourceIds;
	private Map<String, CDOID> newComponentIdsInTarget;
	private Set<CDOID> detachedTargetIds;

	private Map<CDOID, Object> targetMap;

	private final RevisionIndex index;
	private final Provider<IEventBus> bus;

	public SnomedCDOConflictProcessor() {
		super(SnomedDatastoreActivator.REPOSITORY_UUID, RELEASED_ATTRIBUTE_MAP);
		this.bus = SnowOwlApplication.INSTANCE.getEnviroment().provider(IEventBus.class);
		this.index = ApplicationContext.getServiceForClass(RepositoryManager.class)
				.get(getRepositoryUuid())
				.service(RevisionIndex.class);
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
		// FIXME
		Conflict conflict = checkDuplicateComponentIds(sourceRevision, targetMap == this.targetMap ? newComponentIdsInTarget : newComponentIdsInSource);
		
		if (conflict != null) {
			return conflict;
		}

		conflict = checkDetachedReferences(sourceRevision, targetMap == this.targetMap ? detachedTargetIds : detachedSourceIds);
		
		if (conflict != null) {
			return conflict;
		}
		
		return super.addedInSource(sourceRevision, targetMap);
	}
	
	@Override
	public CDOFeatureDelta changedInSourceAndTargetSingleValued(CDOFeatureDelta targetFeatureDelta, CDOFeatureDelta sourceFeatureDelta) {
		final EStructuralFeature feature = targetFeatureDelta.getFeature();
		
		if (SnomedPackage.Literals.COMPONENT__EFFECTIVE_TIME.equals(feature) 
				|| SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER__EFFECTIVE_TIME.equals(feature)
				|| SnomedRefSetPackage.Literals.SNOMED_MODULE_DEPENDENCY_REF_SET_MEMBER__SOURCE_EFFECTIVE_TIME.equals(feature)
				|| SnomedRefSetPackage.Literals.SNOMED_MODULE_DEPENDENCY_REF_SET_MEMBER__TARGET_EFFECTIVE_TIME.equals(feature)) {
		
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
	public void preProcess(final Map<CDOID, Object> sourceMap, final Map<CDOID, Object> targetMap) {
		this.targetMap = targetMap;

		newComponentIdsInSource = extractNewComponentIds(sourceMap);
		detachedSourceIds = getDetachedIdsInTarget(sourceMap);
		newComponentIdsInTarget = extractNewComponentIds(targetMap);
		detachedTargetIds = getDetachedIdsInTarget(targetMap);
	}

	private Map<String, CDOID> extractNewComponentIds(final Map<CDOID, Object> revisionMap) {
		final Iterable<InternalCDORevision> newRevisionsInTarget = getNewRevisionsInTarget(revisionMap);
		final Map<String, CDOID> newComponentIdsMap = Maps.newHashMap();

		for (final InternalCDORevision targetRevision : newRevisionsInTarget) {
			if (isComponent(targetRevision)) {
				newComponentIdsMap.put(getComponentId(targetRevision), targetRevision.getID());
			}
		}

		return newComponentIdsMap;
	}

	private Conflict checkDuplicateComponentIds(final CDORevision sourceRevision, final Map<String, CDOID> newComponentIdsMap) {

		if (!isComponent(sourceRevision)) {
			return null;
		}

		final String newComponentIdInSource = getComponentId((InternalCDORevision) sourceRevision);
		final CDOID conflictingNewInTarget = newComponentIdsMap.get(newComponentIdInSource);
		
		if (null != conflictingNewInTarget) {
			final String sourceType = sourceRevision.getEClass().getName();
			return new AddedInSourceAndTargetConflict(sourceRevision.getID(), conflictingNewInTarget,
					"Two SNOMED CT %ss are using the same '%s' identifier.", sourceType, newComponentIdInSource);
		} else {
			return null;
		}
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

	private Conflict checkDetachedReferences(final CDORevision sourceRevision, final Set<CDOID> detachedIds) {
		final InternalCDORevision internalSourceRevision = (InternalCDORevision) sourceRevision;
		final EClass eClass = internalSourceRevision.getEClass();

		final Conflict conflict;

		if (isComponent(eClass)) {
			conflict = checkDetachedComponentReferences(internalSourceRevision, detachedIds, DETACHED_FEATURE_MAP.get(eClass));
		} else {
			conflict = null;
		}

		return conflict;
	}

	private Conflict checkDetachedComponentReferences(final InternalCDORevision internalSourceRevision, final Set<CDOID> detachedTargetIds, final Collection<EStructuralFeature> featuresToCheck) {

		for (final EStructuralFeature feature : featuresToCheck) {
			final CDOID targetId = (CDOID) internalSourceRevision.getValue(feature);
			if (detachedTargetIds.contains(targetId)) {
				return new AddedInSourceAndDetachedInTargetConflict(internalSourceRevision.getID(), targetId);
			}
		}

		return null;
	}

	@Override
	public void postProcess(CDOTransaction transaction) {
		super.postProcess(transaction);
		
		final ImmutableMultimap.Builder<String, Object> conflictingItems = ImmutableMultimap.builder();
		postProcessLanguageRefSetMembers(transaction, conflictingItems);
		postProcessTaxonomy(transaction, conflictingItems);
		postProcessRefSetMembers(transaction, conflictingItems);
		
		Map<String, Object> result = ImmutableMap.<String, Object>copyOf(conflictingItems.build().asMap());
		if (!result.isEmpty()) {
			throw new MergeConflictException(result, "Conflicts detected on %s concept(s) while post-processing changes.", result.size());
		}
	}
	
	private void postProcessRefSetMembers(CDOTransaction transaction, Builder<String, Object> conflictingItems) {
		final Set<String> detachedMemberIds = FluentIterable.from(ComponentUtils2.getDetachedObjects(transaction, SnomedRefSetMember.class)).transform(new Function<SnomedRefSetMember, String>() {
			@Override
			public String apply(SnomedRefSetMember input) {
				return input.getUuid();
			}
		}).toSet();
		final Set<String> detachedCoreComponentIds = FluentIterable.from(ComponentUtils2.getDetachedObjects(transaction, Component.class)).transform(new Function<Component, String>() {
			@Override
			public String apply(Component input) {
				return input.getId();
			}
		}).toSet();
		if (!detachedCoreComponentIds.isEmpty()) {
			final SnomedReferenceSetMembers membersReferencingDetachedComponents = SnomedRequests
					.prepareSearchMember()
					.filterByReferencedComponent(detachedCoreComponentIds)
					.setLimit(detachedCoreComponentIds.size())
					.build(BranchPathUtils.createPath(transaction).getPath())
					.executeSync(ApplicationContext.getInstance().getService(IEventBus.class));
			
			for (SnomedReferenceSetMember member : membersReferencingDetachedComponents) {
				if (!detachedMemberIds.contains(member.getId())) {
					conflictingItems.put(member.getReferencedComponent().getId(), new GenericConflict("Member '%s' is referencing detached component '%s'", member.getId(), member.getReferencedComponent().getId())); 
				}
			}
		}
	}

	private void postProcessLanguageRefSetMembers(CDOTransaction transaction, ImmutableMultimap.Builder<String, Object> conflictingItems) {
		final IBranchPath branchPath = BranchPathUtils.createPath(transaction);
		final Set<String> synonymAndDescendantIds = SnomedRequests.prepareGetSynonyms()
				.build(branchPath.getPath())
				.execute(bus.get())
				.then(new Function<SnomedConcepts, Set<String>>() {
					@Override
					public Set<String> apply(SnomedConcepts input) {
						return FluentIterable.from(input).transform(IComponent.ID_FUNCTION).toSet();
					}
				})
				.getSync();
		
		final Set<SnomedLanguageRefSetMember> membersToRemove = newHashSet();
		
		label:
		for (CDOObject newObject : transaction.getNewObjects().values()) {
			
			if (!(newObject instanceof SnomedLanguageRefSetMember)) {
				continue;
			}
			
			SnomedLanguageRefSetMember newLanguageRefSetMember = (SnomedLanguageRefSetMember) newObject;
			
			if (!newLanguageRefSetMember.isActive()) {
				continue;
			}
			
			Description description = (Description) newObject.eContainer();
			
			if (!description.isActive()) {
				continue;
			}
			
			String acceptabilityId = newLanguageRefSetMember.getAcceptabilityId();
			String typeId = description.getType().getId();
			String languageRefSetId = newLanguageRefSetMember.getRefSetIdentifierId(); 
			
			Concept concept = description.getConcept();
			
			for (Description conceptDescription : concept.getDescriptions()) {
				
				if (!conceptDescription.isActive()) {
					continue;
				}
				
				String conceptDescriptionTypeId = conceptDescription.getType().getId();
				
				if (!typeId.equals(conceptDescriptionTypeId) && !(synonymAndDescendantIds.contains(typeId) && synonymAndDescendantIds.contains(conceptDescriptionTypeId))) {
					continue;
				}
				
				for (SnomedLanguageRefSetMember conceptDescriptionMember : conceptDescription.getLanguageRefSetMembers()) {
					
					if (!conceptDescriptionMember.isActive()) {
						continue;
					}
					
					if (!languageRefSetId.equals(conceptDescriptionMember.getRefSetIdentifierId())) {
						continue;
					}
					
					if (conceptDescriptionMember.equals(newLanguageRefSetMember)) {
						continue;
					}
					
					if (acceptabilityId.equals(conceptDescriptionMember.getAcceptabilityId())) {
						if (description.equals(conceptDescription)) {
							membersToRemove.add(newLanguageRefSetMember);
							continue label;
						} else if (Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED.equals(acceptabilityId)) {
							conflictingItems.put(concept.getId(), new AddedInSourceAndTargetConflict(newLanguageRefSetMember.cdoID(), 
									conceptDescriptionMember.cdoID(),
									"Two SNOMED CT Descriptions selected as preferred terms. %s <-> %s",
									description.getId(), conceptDescription.getId()));
						}
					} else {
						if (description.equals(conceptDescription)) {
							conflictingItems.put(concept.getId(), new AddedInSourceAndTargetConflict(
									newLanguageRefSetMember.cdoID(), 
									conceptDescriptionMember.cdoID(),
									"Different acceptability selected for the same description, %s", description.getId()));
						}
					}
				}
			}
		}
		
		for (SnomedLanguageRefSetMember memberToRemove : membersToRemove) {
			unlinkObject(memberToRemove);
		}
	}
	
	private void postProcessTaxonomy(final CDOTransaction transaction, final ImmutableMultimap.Builder<String, Object> conflictingItems) {
		final IBranchPath branchPath = BranchPathUtils.createPath(transaction);
		index.read(branchPath.getPath(), new RevisionIndexRead<Void>() {
			@Override
			public Void execute(final RevisionSearcher searcher) throws IOException {
				final Query<RevisionDocument.Views.IdOnly> allConceptsQuery = Query.selectPartial(RevisionDocument.Views.IdOnly.class, SnomedConceptDocument.class)
						.where(Expressions.matchAll())
						.limit(Integer.MAX_VALUE)
						.build();
				
				final Hits<RevisionDocument.Views.IdOnly> allConcepts = searcher.search(allConceptsQuery);
				final LongSet conceptIds = PrimitiveSets.newLongOpenHashSet(allConcepts.getTotal());
				
				for (RevisionDocument.Views.IdOnly conceptId : allConcepts) {
					conceptIds.add(Long.parseLong(conceptId.getId()));
				}
				
				for (final String characteristicTypeId : ImmutableList.of(Concepts.STATED_RELATIONSHIP, Concepts.INFERRED_RELATIONSHIP)) {
					final Collection<SnomedRelationshipIndexEntry.Views.StatementWithId> statements = getActiveStatements(searcher, characteristicTypeId);
					final SnomedTaxonomyBuilder taxonomyBuilder = new SnomedTaxonomyBuilder(conceptIds, statements);
					final SnomedTaxonomyUpdateRunnable taxonomyRunnable = new SnomedTaxonomyUpdateRunnable(searcher, transaction, taxonomyBuilder, characteristicTypeId);
					taxonomyRunnable.run();
					
					final SnomedTaxonomyBuilderResult result = taxonomyRunnable.getTaxonomyBuilderResult();
					if (!result.getStatus().isOK()) {
						for (InvalidRelationship invalidRelationship : result.getInvalidRelationships()) {
							conflictingItems.put(Long.toString(invalidRelationship.getMissingConceptId()), invalidRelationship);
						}
					}
				}
				
				return null;
			}
		});
	}

	private Collection<SnomedRelationshipIndexEntry.Views.StatementWithId> getActiveStatements(RevisionSearcher searcher, String characteristicTypeId) throws IOException {
		final Query<SnomedRelationshipIndexEntry.Views.StatementWithId> query = Query.selectPartial(SnomedRelationshipIndexEntry.Views.StatementWithId.class, SnomedRelationshipIndexEntry.class)
				.where(Expressions.builder()
						.must(SnomedRelationshipIndexEntry.Expressions.active())
						.must(SnomedRelationshipIndexEntry.Expressions.typeId(Concepts.IS_A))
						.must(SnomedRelationshipIndexEntry.Expressions.characteristicTypeId(characteristicTypeId))
						.build())
				.limit(Integer.MAX_VALUE)
				.build();
		return searcher.search(query).getHits();
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
}
