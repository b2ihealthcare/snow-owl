/*
 * Copyright 2011-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.index.change;

import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.ClassUtils;
import com.b2international.commons.CompareUtils;
import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.index.revision.StagingArea;
import com.b2international.index.revision.StagingArea.RevisionPropertyDiff;
import com.b2international.snowowl.core.config.IndexConfiguration;
import com.b2international.snowowl.core.config.RepositoryConfiguration;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.repository.BaseRepositoryPreCommitHook;
import com.b2international.snowowl.core.repository.ChangeSetProcessor;
import com.b2international.snowowl.core.request.BranchSnapshotContentRequest;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.datastore.index.entry.*;
import com.b2international.snowowl.snomed.datastore.request.SnomedOWLExpressionConverter;
import com.b2international.snowowl.snomed.datastore.request.SnomedOWLExpressionConverterResult;
import com.b2international.snowowl.snomed.datastore.request.rf2.importer.Rf2ImportConfiguration;
import com.b2international.snowowl.snomed.datastore.request.rf2.importer.Rf2TransactionContext;
import com.b2international.snowowl.snomed.datastore.taxonomy.Taxonomies;
import com.b2international.snowowl.snomed.datastore.taxonomy.Taxonomy;
import com.b2international.snowowl.snomed.icons.SnomedIconProvider;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * Repository precommit hook implementation for SNOMED CT repository.
 * @see BaseRepositoryPreCommitHook
 */
public final class SnomedRepositoryPreCommitHook extends BaseRepositoryPreCommitHook {

	private static final Set<String> ACTIVE_AND_TERM_FIELDS = Set.of(SnomedDescriptionIndexEntry.Fields.ACTIVE, SnomedDescriptionIndexEntry.Fields.TERM);

	public SnomedRepositoryPreCommitHook(Logger log) {
		super(log);
	}
	
	@Override
	protected void preUpdateDocuments(StagingArea staging, RevisionSearcher index) throws IOException {
		final RepositoryContext context = ClassUtils.checkAndCast(staging.getContext(), RepositoryContext.class);
		
		if (!(context instanceof Rf2TransactionContext)) {
			final ImmutableList.Builder<ChangeSetProcessor> processors = ImmutableList.builder();
			processors.add(new DetachedContainerChangeProcessor());
			if (!staging.isMerge()) {
				processors.add(new ComponentInactivationChangeProcessor());
			}
			doProcess(processors.build(), staging, index);
		}
	}
	
	@Override
	protected Collection<ChangeSetProcessor> getChangeSetProcessors(StagingArea staging, RevisionSearcher index) throws IOException {
		final RepositoryContext context = ClassUtils.checkAndCast(staging.getContext(), RepositoryContext.class);
		final RepositoryConfiguration repositoryConfiguration = context.service(RepositoryConfiguration.class);
		final IndexConfiguration indexConfiguration = repositoryConfiguration.getIndexConfiguration();
		final int partitionSize = indexConfiguration.getTermPartitionSize();
		final int pageSize = context.getPageSize();
		
		// initialize OWL Expression converter on the current branch
		final SnomedOWLExpressionConverter expressionConverter = new BranchSnapshotContentRequest<>(staging.getBranchPath(), branchContext -> {
			return new SnomedOWLExpressionConverter(branchContext.inject()
					.bind(RevisionSearcher.class, index)
					.build());
		}).execute(context);
		
		final Set<String> statedSourceIds = Sets.newHashSet();
		final Set<String> statedDestinationIds = Sets.newHashSet();
		final Set<String> inferredSourceIds = Sets.newHashSet();
		final Set<String> inferredDestinationIds = Sets.newHashSet();
		
		collectIds(statedSourceIds, statedDestinationIds, staging.getNewObjects(SnomedRelationshipIndexEntry.class), Concepts.STATED_RELATIONSHIP);
		collectIds(statedSourceIds, statedDestinationIds, staging.getChangedRevisions(SnomedRelationshipIndexEntry.class).map(diff -> (SnomedRelationshipIndexEntry) diff.newRevision), Concepts.STATED_RELATIONSHIP);
		collectIds(inferredSourceIds, inferredDestinationIds, staging.getNewObjects(SnomedRelationshipIndexEntry.class), Concepts.INFERRED_RELATIONSHIP);
		collectIds(inferredSourceIds, inferredDestinationIds, staging.getChangedRevisions(SnomedRelationshipIndexEntry.class).map(diff -> (SnomedRelationshipIndexEntry) diff.newRevision), Concepts.INFERRED_RELATIONSHIP);
		collectIds(statedSourceIds, statedDestinationIds, staging.getNewObjects(SnomedRefSetMemberIndexEntry.class), expressionConverter);
		collectIds(statedSourceIds, statedDestinationIds, staging.getChangedRevisions(SnomedRefSetMemberIndexEntry.class).map(diff -> (SnomedRefSetMemberIndexEntry) diff.newRevision), expressionConverter);
		
		staging.getRemovedObjects(SnomedRelationshipIndexEntry.class)
			.filter(detachedRelationship -> Concepts.IS_A.equals(detachedRelationship.getTypeId()))
			.forEach(detachedRelationship -> {
				// XXX: IS A relationships are expected to have a destination ID, not a value
				checkState(!detachedRelationship.hasValue(), "IS A relationship found with value: %s", detachedRelationship.getId());
				
				if (Concepts.STATED_RELATIONSHIP.equals(detachedRelationship.getCharacteristicTypeId())) {
					statedSourceIds.add(detachedRelationship.getSourceId());
					statedDestinationIds.add(detachedRelationship.getDestinationId());
				} else if (Concepts.INFERRED_RELATIONSHIP.equals(detachedRelationship.getCharacteristicTypeId())) {
					inferredSourceIds.add(detachedRelationship.getSourceId());
					inferredDestinationIds.add(detachedRelationship.getDestinationId());
				}
			});
		
		staging.getRemovedObjects(SnomedRefSetMemberIndexEntry.class)
			.filter(detachedMember -> SnomedRefSetType.OWL_AXIOM == detachedMember.getReferenceSetType())
			.forEach(detachedOwlMember -> {
				collectIds(statedSourceIds, statedDestinationIds, detachedOwlMember, expressionConverter);
			});
		
		final LongSet statedConceptIds = PrimitiveSets.newLongOpenHashSet();
		final LongSet inferredConceptIds = PrimitiveSets.newLongOpenHashSet();
		
		if (!statedDestinationIds.isEmpty()) {
			for (List<String> statedDestinationIdsPartition : Iterables.partition(statedDestinationIds, partitionSize)) {
				Query.select(SnomedConceptDocument.class)
					// make sure we only load the necessary parent arrays, not everything
					.fields(SnomedConceptDocument.Fields.ID, SnomedConceptDocument.Fields.STATED_PARENTS, SnomedConceptDocument.Fields.STATED_ANCESTORS)
					.where(SnomedConceptDocument.Expressions.ids(statedDestinationIdsPartition))
					.limit(statedDestinationIdsPartition.size())
					.build()
					.search(index)
					.forEach(statedDestinationConcept -> {
						statedConceptIds.add(Long.parseLong(statedDestinationConcept.getId()));
						if (statedDestinationConcept.getStatedParents() != null) {
							statedConceptIds.addAll(statedDestinationConcept.getStatedParents());
						}
						if (statedDestinationConcept.getStatedAncestors() != null) {
							statedConceptIds.addAll(statedDestinationConcept.getStatedAncestors());
						}
					});
			}
		}
		
		if (!inferredDestinationIds.isEmpty()) {
			for (List<String> inferredDestinationIdsPartition : Iterables.partition(inferredDestinationIds, partitionSize)) {
				Query.select(SnomedConceptDocument.class)
					// make sure we only load the necessary parent arrays, not everything
					.fields(SnomedConceptDocument.Fields.ID, SnomedConceptDocument.Fields.PARENTS, SnomedConceptDocument.Fields.ANCESTORS)
					.where(SnomedConceptDocument.Expressions.ids(inferredDestinationIdsPartition))
					.limit(inferredDestinationIdsPartition.size())
					.build()
					.search(index)
					.forEach(inferredDestinationConcept -> {
						inferredConceptIds.add(Long.parseLong(inferredDestinationConcept.getId()));
						if (inferredDestinationConcept.getParents() != null) {
							inferredConceptIds.addAll(inferredDestinationConcept.getParents());
						}
						if (inferredDestinationConcept.getAncestors() != null) {
							inferredConceptIds.addAll(inferredDestinationConcept.getAncestors());
						}
					});
			}
		}
		
		staging.getRemovedObjects(SnomedDescriptionIndexEntry.class).forEach(removedDescription -> {
			if (removedDescription.isFsn() && removedDescription.isActive()) {
				statedSourceIds.add(removedDescription.getConceptId());
				inferredSourceIds.add(removedDescription.getConceptId());
			}
		});
		
		staging.getChangedRevisions(SnomedDescriptionIndexEntry.class)
			.filter(diff -> ((SnomedDescriptionIndexEntry) diff.newRevision).isFsn())
			.filter(diff -> diff.hasRevisionPropertyChanges(ACTIVE_AND_TERM_FIELDS))
			.forEach(diff -> {
				SnomedDescriptionIndexEntry newRevision = (SnomedDescriptionIndexEntry) diff.newRevision;
				statedSourceIds.add(newRevision.getConceptId());
				inferredSourceIds.add(newRevision.getConceptId());
			});

		staging.getNewObjects(SnomedDescriptionIndexEntry.class)
			.filter(newDescription -> newDescription.isFsn() && newDescription.isActive())
			.forEach(newDescription -> {
				statedSourceIds.add(newDescription.getConceptId());
				inferredSourceIds.add(newDescription.getConceptId());
			});

		if (!statedSourceIds.isEmpty()) {
			for (List<String> statedSourceIdsPartition : Iterables.partition(statedSourceIds, partitionSize)) {
				Query.select(SnomedConceptDocument.class)
					// make sure we only load the necessary parent arrays, not everything
					.fields(SnomedConceptDocument.Fields.ID, SnomedConceptDocument.Fields.STATED_PARENTS, SnomedConceptDocument.Fields.STATED_ANCESTORS)
					.where(Expressions.bool()
							.should(SnomedConceptDocument.Expressions.ids(statedSourceIdsPartition))
							.should(SnomedConceptDocument.Expressions.statedParents(statedSourceIdsPartition))
							.should(SnomedConceptDocument.Expressions.statedAncestors(statedSourceIdsPartition))
							.build())
					.limit(pageSize)
					.build()
					.stream(index)
					.flatMap(Hits::stream)
					.forEach(statedSourceConcept -> {
						statedConceptIds.add(Long.parseLong(statedSourceConcept.getId()));
						if (statedSourceConcept.getStatedParents() != null) {
							statedConceptIds.addAll(statedSourceConcept.getStatedParents());
						}
						if (statedSourceConcept.getStatedAncestors() != null) {
							statedConceptIds.addAll(statedSourceConcept.getStatedAncestors());
						}
					});
			}
		}
		
		if (!inferredSourceIds.isEmpty()) {
			for (List<String> inferredSourceIdsPartition : Iterables.partition(inferredSourceIds, partitionSize)) {
				Query.select(SnomedConceptDocument.class)
					.fields(SnomedConceptDocument.Fields.ID, SnomedConceptDocument.Fields.PARENTS, SnomedConceptDocument.Fields.ANCESTORS)
					.where(Expressions.bool()
							.should(SnomedConceptDocument.Expressions.ids(inferredSourceIdsPartition))
							.should(SnomedConceptDocument.Expressions.parents(inferredSourceIdsPartition))
							.should(SnomedConceptDocument.Expressions.ancestors(inferredSourceIdsPartition))
							.build())
					.limit(pageSize)
					.build()
					.stream(index)
					.flatMap(Hits::stream)
					.forEach(inferredSourceConcept -> {
						inferredConceptIds.add(Long.parseLong(inferredSourceConcept.getId()));
						if (inferredSourceConcept.getParents() != null) {
							inferredConceptIds.addAll(inferredSourceConcept.getParents());
						}
						if (inferredSourceConcept.getAncestors() != null) {
							inferredConceptIds.addAll(inferredSourceConcept.getAncestors());
						}
					});
			}
		}
		
		staging.getNewObjects(SnomedConceptDocument.class).forEach(newConcept -> {
			long longId = Long.parseLong(newConcept.getId());
			statedConceptIds.add(longId);
			inferredConceptIds.add(longId);
		});
		
		// collect all reactivated concepts for the taxonomy to properly re-register them in the tree even if they don't carry stated/inferred information in this commit, but they have something in the index
		staging.getChangedRevisions(SnomedConceptDocument.class, Set.of(SnomedRf2Headers.FIELD_ACTIVE))
			.forEach(diff -> {
				RevisionPropertyDiff propertyDiff = diff.getRevisionPropertyDiff(SnomedRf2Headers.FIELD_ACTIVE);
				if ("false".equals(propertyDiff.getOldValue()) && "true".equals(propertyDiff.getNewValue())) {
					long longId = Long.parseLong(diff.newRevision.getId());
					statedConceptIds.add(longId);
					inferredConceptIds.add(longId);
				}
			});
		
		log.trace("Retrieving taxonomic information from store...");

		final boolean checkCycles = !(context instanceof Rf2TransactionContext);
		
		final Taxonomy inferredTaxonomy = Taxonomies.inferred(index, expressionConverter, staging, inferredConceptIds, checkCycles);
		final Taxonomy statedTaxonomy = Taxonomies.stated(index, expressionConverter, staging, statedConceptIds, checkCycles);

		// XXX change processor execution order is important!!!
		return List.of(
			// execute description change processor to get proper acceptabilityMap values before executing other change processors
			// those values will be used in the ConceptChangeProcessor for example to properly compute the preferredDescriptions derived field
			new DescriptionChangeProcessor(),
			new ConceptChangeProcessor(DoiDataProvider.INSTANCE, SnomedIconProvider.INSTANCE.getAvailableIconIds(), statedTaxonomy, inferredTaxonomy),
			new RelationshipChangeProcessor()
		);
	}
	
	@Override
	protected void postUpdateDocuments(StagingArea staging, RevisionSearcher index) throws IOException {
		final RepositoryContext context = ClassUtils.checkAndCast(staging.getContext(), RepositoryContext.class);
		
		if (canRestoreEffectiveTime(context) && !staging.isMerge()) {
			// XXX effective time restore should be the last processing unit before we send the changes to commit
			doProcess(Collections.singleton(new ComponentEffectiveTimeRestoreChangeProcessor(log)), staging, index);
		}
	}
	
	/*
	 * Restore effective time should run during normal commits and delta RF2 imports
	 */
	private boolean canRestoreEffectiveTime(RepositoryContext context) {
		if (context instanceof Rf2TransactionContext) {
			return context.service(Rf2ImportConfiguration.class).getReleaseType() == Rf2ReleaseType.DELTA;
		} else {
			return true;
		}
	}

	private void collectIds(final Set<String> sourceIds, final Set<String> destinationIds, Stream<SnomedRelationshipIndexEntry> relationships, String characteristicTypeId) {
		relationships
			.filter(relationship -> Concepts.IS_A.equals(relationship.getTypeId()))
			.filter(relationship -> relationship.getCharacteristicTypeId().equals(characteristicTypeId))
			.forEach(relationship -> {
				// XXX: IS A relationships are expected to have a destination ID, not a value
				checkState(!relationship.hasValue(), "IS A relationship found with value: %s", relationship.getId());
				
				sourceIds.add(relationship.getSourceId());
				destinationIds.add(relationship.getDestinationId());
			});
	}
	
	private void collectIds(Set<String> sourceIds, Set<String> destinationIds, Stream<SnomedRefSetMemberIndexEntry> owlMembers, SnomedOWLExpressionConverter expressionConverter) {
		owlMembers.forEach(owlMember -> {
			collectIds(sourceIds, destinationIds, owlMember, expressionConverter);
		});
	}

	private void collectIds(Set<String> sourceIds, Set<String> destinationIds, SnomedRefSetMemberIndexEntry owlMember, SnomedOWLExpressionConverter expressionConverter) {
		final String memberId = owlMember.getId();
		final String referencedComponentId = owlMember.getReferencedComponentId();
		final String owlExpression = owlMember.getOwlExpression();
		
		SnomedOWLExpressionConverterResult result = expressionConverter.toSnomedOWLRelationships(referencedComponentId, owlExpression);
		if (!CompareUtils.isEmpty(result.getClassAxiomRelationships())) {
			for (SnomedOWLRelationshipDocument owlRelationship : result.getClassAxiomRelationships()) {
				if (Concepts.IS_A.equals(owlRelationship.getTypeId())) {
					// XXX: IS A relationships are expected to have a destination ID, not a value
					checkState(!owlRelationship.hasValue(), "IS A relationship found with value on OWL member: %s", memberId);
					
					sourceIds.add(referencedComponentId);
					destinationIds.add(owlRelationship.getDestinationId());
				}
			}
		}
	}

}
