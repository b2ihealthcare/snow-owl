/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.ClassUtils;
import com.b2international.commons.CompareUtils;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.index.revision.StagingArea;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.repository.BaseRepositoryPreCommitHook;
import com.b2international.snowowl.core.repository.ChangeSetProcessor;
import com.b2international.snowowl.core.request.BranchRequest;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedOWLRelationshipDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedOWLExpressionConverter;
import com.b2international.snowowl.snomed.datastore.request.SnomedOWLExpressionConverterResult;
import com.b2international.snowowl.snomed.datastore.request.rf2.SnomedRf2Requests;
import com.b2international.snowowl.snomed.datastore.taxonomy.Taxonomies;
import com.b2international.snowowl.snomed.datastore.taxonomy.Taxonomy;
import com.b2international.snowowl.snomed.icons.SnomedIconProvider;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

/**
 * Repository precommit hook implementation for SNOMED CT repository.
 * @see BaseRepositoryPreCommitHook
 */
public final class SnomedRepositoryPreCommitHook extends BaseRepositoryPreCommitHook {

	public SnomedRepositoryPreCommitHook(Logger log) {
		super(log);
	}
	
	@Override
	protected void preUpdateDocuments(StagingArea staging, RevisionSearcher index) throws IOException {
		final RepositoryContext context = ClassUtils.checkAndCast(staging.getContext(), RepositoryContext.class);
		
		if (!context.isJobRunning(SnomedRf2Requests.importJobKey(index.branch()))) {
			doProcess(ImmutableList.of(new ComponentInactivationChangeProcessor(), new DetachedContainerChangeProcessor()), staging, index);
		}
	}
	
	@Override
	protected Collection<ChangeSetProcessor> getChangeSetProcessors(StagingArea staging, RevisionSearcher index) throws IOException {
		final RepositoryContext context = ClassUtils.checkAndCast(staging.getContext(), RepositoryContext.class);
		// initialize OWL Expression converter on the current branch
		final SnomedOWLExpressionConverter expressionConverter = new BranchRequest<>(staging.getBranchPath(), branchContext -> {
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
				collectIds(statedSourceIds, statedDestinationIds, detachedOwlMember.getReferencedComponentId(), detachedOwlMember.getOwlExpression(), expressionConverter);
			});
		
		final LongSet statedConceptIds = PrimitiveSets.newLongOpenHashSet();
		final LongSet inferredConceptIds = PrimitiveSets.newLongOpenHashSet();
		
		
		if (!statedDestinationIds.isEmpty()) {
			final Query<SnomedConceptDocument> statedDestinationConceptsQuery = Query.select(SnomedConceptDocument.class)
					.where(SnomedDocument.Expressions.ids(statedDestinationIds))
					.limit(statedDestinationIds.size())
					.build();
			
			for (SnomedConceptDocument statedDestinationConcept : index.search(statedDestinationConceptsQuery)) {
				statedConceptIds.add(Long.parseLong(statedDestinationConcept.getId()));
				if (statedDestinationConcept.getStatedParents() != null) {
					statedConceptIds.addAll(statedDestinationConcept.getStatedParents());
				}
				if (statedDestinationConcept.getStatedAncestors() != null) {
					statedConceptIds.addAll(statedDestinationConcept.getStatedAncestors());
				}
			}
		}
		
		if (!inferredDestinationIds.isEmpty()) {
			final Query<SnomedConceptDocument> inferredDestinationConceptsQuery = Query.select(SnomedConceptDocument.class)
					.where(SnomedDocument.Expressions.ids(inferredDestinationIds))
					.limit(inferredDestinationIds.size())
					.build();
			
			for (SnomedConceptDocument inferredDestinationConcept : index.search(inferredDestinationConceptsQuery)) {
				inferredConceptIds.add(Long.parseLong(inferredDestinationConcept.getId()));
				if (inferredDestinationConcept.getParents() != null) {
					inferredConceptIds.addAll(inferredDestinationConcept.getParents());
				}
				if (inferredDestinationConcept.getAncestors() != null) {
					inferredConceptIds.addAll(inferredDestinationConcept.getAncestors());
				}
			}
		}
		
		if (!statedSourceIds.isEmpty()) {
			final Query<SnomedConceptDocument> statedSourceConceptsQuery = Query.select(SnomedConceptDocument.class)
					.where(Expressions.builder()
							.should(SnomedConceptDocument.Expressions.ids(statedSourceIds))
							.should(SnomedConceptDocument.Expressions.statedParents(statedSourceIds))
							.should(SnomedConceptDocument.Expressions.statedAncestors(statedSourceIds))
							.build())
					.limit(Integer.MAX_VALUE)
					.build();
			
			for (SnomedConceptDocument statedSourceConcept : index.search(statedSourceConceptsQuery)) {
				statedConceptIds.add(Long.parseLong(statedSourceConcept.getId()));
				if (statedSourceConcept.getStatedParents() != null) {
					statedConceptIds.addAll(statedSourceConcept.getStatedParents());
				}
				if (statedSourceConcept.getStatedAncestors() != null) {
					statedConceptIds.addAll(statedSourceConcept.getStatedAncestors());
				}
			}
		}
		
		if (!inferredSourceIds.isEmpty()) {
			final Query<SnomedConceptDocument> inferredSourceConceptsQuery = Query.select(SnomedConceptDocument.class)
					.where(Expressions.builder()
							.should(SnomedConceptDocument.Expressions.ids(inferredSourceIds))
							.should(SnomedConceptDocument.Expressions.parents(inferredSourceIds))
							.should(SnomedConceptDocument.Expressions.ancestors(inferredSourceIds))
							.build())
					.limit(Integer.MAX_VALUE)
					.build();
			
			for (SnomedConceptDocument inferredSourceConcept : index.search(inferredSourceConceptsQuery)) {
				inferredConceptIds.add(Long.parseLong(inferredSourceConcept.getId()));
				if (inferredSourceConcept.getParents() != null) {
					inferredConceptIds.addAll(inferredSourceConcept.getParents());
				}
				if (inferredSourceConcept.getAncestors() != null) {
					inferredConceptIds.addAll(inferredSourceConcept.getAncestors());
				}
			}
		}
		
		staging.getNewObjects(SnomedConceptDocument.class).forEach(newConcept -> {
			long longId = Long.parseLong(newConcept.getId());
			statedConceptIds.add(longId);
			inferredConceptIds.add(longId);
		});

		log.trace("Retrieving taxonomic information from store...");

		final boolean checkCycles = !context.isJobRunning(SnomedRf2Requests.importJobKey(index.branch()));
		
		final Taxonomy inferredTaxonomy = Taxonomies.inferred(index, expressionConverter, staging, inferredConceptIds, checkCycles);
		final Taxonomy statedTaxonomy = Taxonomies.stated(index, expressionConverter, staging, statedConceptIds, checkCycles);

		// XXX change processor execution order is important!!!
		return ImmutableList.<ChangeSetProcessor>builder()
				// execute description change processor to get proper acceptabilityMap values before executing other change processors
				// those values will be used in the ConceptChangeProcessor for example to properly compute the preferredDescriptions derived field
				.add(new DescriptionChangeProcessor())
				.add(new ConceptChangeProcessor(DoiDataProvider.INSTANCE, SnomedIconProvider.INSTANCE.getAvailableIconIds(), statedTaxonomy, inferredTaxonomy))
				.add(new RelationshipChangeProcessor())
				.build();
	}
	
	@Override
	protected void postUpdateDocuments(StagingArea staging, RevisionSearcher index) throws IOException {
		final RepositoryContext context = ClassUtils.checkAndCast(staging.getContext(), RepositoryContext.class);
		
		if (!context.isJobRunning(SnomedRf2Requests.importJobKey(index.branch()))) {
			final long branchBaseTimestamp = index.get(RevisionBranch.class, staging.getBranchPath()).getBaseTimestamp();
			// XXX effective time restore should be the last processing unit before we send the changes to commit
			doProcess(Collections.singleton(new ComponentEffectiveTimeRestoreChangeProcessor(log, branchBaseTimestamp)), staging, index);
		}
	}
	
	private void collectIds(final Set<String> sourceIds, final Set<String> destinationIds, Stream<SnomedRelationshipIndexEntry> newRelationships, String characteristicTypeId) {
		newRelationships
			.filter(newRelationship -> Concepts.IS_A.equals(newRelationship.getTypeId()))
			.filter(newRelationship -> newRelationship.getCharacteristicTypeId().equals(characteristicTypeId))
			.forEach(newRelationship -> {
				sourceIds.add(newRelationship.getSourceId());
				destinationIds.add(newRelationship.getDestinationId());
			});
	}
	
	private void collectIds(Set<String> sourceIds, Set<String> destinationIds, Stream<SnomedRefSetMemberIndexEntry> owlMembers, SnomedOWLExpressionConverter expressionConverter) {
		owlMembers.forEach(owlMember -> {
			collectIds(sourceIds, destinationIds, owlMember.getReferencedComponentId(), owlMember.getOwlExpression(), expressionConverter);
		});
	}

	private void collectIds(Set<String> sourceIds, Set<String> destinationIds, String referencedComponentId, String owlExpression, SnomedOWLExpressionConverter expressionConverter) {
		SnomedOWLExpressionConverterResult result = expressionConverter.toSnomedOWLRelationships(referencedComponentId, owlExpression);
		if (!CompareUtils.isEmpty(result.getClassAxiomRelationships())) {
			for (SnomedOWLRelationshipDocument classAxiom : result.getClassAxiomRelationships()) {
				if (Concepts.IS_A.equals(classAxiom.getTypeId())) {
					sourceIds.add(referencedComponentId);
					destinationIds.add(classAxiom.getDestinationId());
				}
			}
		}
	}

}
