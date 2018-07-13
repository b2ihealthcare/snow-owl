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
package com.b2international.snowowl.datastore.server.snomed.index;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.StoreThreadLocal;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.concurrent.equinox.ForkJoinUtils;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.ft.FeatureToggles;
import com.b2international.snowowl.core.ft.Features;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.index.BaseCDOChangeProcessor;
import com.b2international.snowowl.datastore.index.ChangeSetProcessor;
import com.b2international.snowowl.datastore.index.RevisionDocument;
import com.b2international.snowowl.datastore.server.CDOServerUtils;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedIconProvider;
import com.b2international.snowowl.snomed.datastore.index.change.ConceptChangeProcessor;
import com.b2international.snowowl.snomed.datastore.index.change.ConstraintChangeProcessor;
import com.b2international.snowowl.snomed.datastore.index.change.DescriptionChangeProcessor;
import com.b2international.snowowl.snomed.datastore.index.change.RefSetMemberChangeProcessor;
import com.b2international.snowowl.snomed.datastore.index.change.RelationshipChangeProcessor;
import com.b2international.snowowl.snomed.datastore.index.constraint.SnomedConstraintDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.taxonomy.Taxonomies;
import com.b2international.snowowl.snomed.datastore.taxonomy.Taxonomy;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

/**
 * Change processor implementation for SNOMED&nbsp;CT ontology.
 * @see BaseCDOChangeProcessor
 * @see ICDOCommitChangeSet
 */
public final class SnomedCDOChangeProcessor extends BaseCDOChangeProcessor {

	private Taxonomy inferredTaxonomy;
	private Taxonomy statedTaxonomy;
	
	SnomedCDOChangeProcessor(final IBranchPath branchPath, final RevisionIndex index) {
		super(branchPath, index);
	}
	
	/*updates the documents in the indexes based on the dirty, detached and new components.*/
	
	@Override
	protected void preUpdateDocuments(ICDOCommitChangeSet commitChangeSet, RevisionSearcher index) throws IOException {
		final Set<String> statedSourceIds = Sets.newHashSet();
		final Set<String> statedDestinationIds = Sets.newHashSet();
		final Set<String> inferredSourceIds = Sets.newHashSet();
		final Set<String> inferredDestinationIds = Sets.newHashSet();
		
		collectIds(statedSourceIds, statedDestinationIds, commitChangeSet.getNewComponents(Relationship.class), CharacteristicType.STATED_RELATIONSHIP);
		collectIds(statedSourceIds, statedDestinationIds, commitChangeSet.getDirtyComponents(Relationship.class), CharacteristicType.STATED_RELATIONSHIP);
		collectIds(inferredSourceIds, inferredDestinationIds, commitChangeSet.getNewComponents(Relationship.class), CharacteristicType.INFERRED_RELATIONSHIP);
		collectIds(inferredSourceIds, inferredDestinationIds, commitChangeSet.getDirtyComponents(Relationship.class), CharacteristicType.INFERRED_RELATIONSHIP);
		
		final List<SnomedRelationshipIndexEntry> detachedRelationships = commitChangeSet.getDetachedComponents(SnomedPackage.Literals.RELATIONSHIP, SnomedRelationshipIndexEntry.class);
		
		for (SnomedRelationshipIndexEntry detachedRelationship : detachedRelationships) {
			if (detachedRelationship.getCharacteristicType().equals(CharacteristicType.STATED_RELATIONSHIP)) {
				statedSourceIds.add(detachedRelationship.getSourceId());
				statedDestinationIds.add(detachedRelationship.getDestinationId());
			} else {
				inferredSourceIds.add(detachedRelationship.getSourceId());
				inferredDestinationIds.add(detachedRelationship.getDestinationId());
			}
		}
		
		final LongSet statedConceptIds = PrimitiveSets.newLongOpenHashSet();
		final LongSet inferredConceptIds = PrimitiveSets.newLongOpenHashSet();
		
		
		if (!statedDestinationIds.isEmpty()) {
			final Query<SnomedConceptDocument> statedDestinationConceptsQuery = Query.select(SnomedConceptDocument.class)
					.fields(SnomedConceptDocument.Fields.ID, SnomedConceptDocument.Fields.STATED_PARENTS, SnomedConceptDocument.Fields.STATED_ANCESTORS)
					.where(SnomedDocument.Expressions.ids(statedDestinationIds))
					.limit(statedDestinationIds.size())
					.build();
			
			for (SnomedConceptDocument statedDestinationConcept : index.search(statedDestinationConceptsQuery)) {
				statedConceptIds.add(Long.parseLong(statedDestinationConcept.getId()));
				statedConceptIds.addAll(statedDestinationConcept.getStatedParents());
				statedConceptIds.addAll(statedDestinationConcept.getStatedAncestors());
			}
		}
		
		if (!inferredDestinationIds.isEmpty()) {
			final Query<SnomedConceptDocument> inferredDestinationConceptsQuery = Query.select(SnomedConceptDocument.class)
					.fields(SnomedConceptDocument.Fields.ID, SnomedConceptDocument.Fields.PARENTS, SnomedConceptDocument.Fields.ANCESTORS)
					.where(SnomedDocument.Expressions.ids(inferredDestinationIds))
					.limit(inferredDestinationIds.size())
					.build();
			
			for (SnomedConceptDocument inferredDestinationConcept : index.search(inferredDestinationConceptsQuery)) {
				inferredConceptIds.add(Long.parseLong(inferredDestinationConcept.getId()));
				inferredConceptIds.addAll(inferredDestinationConcept.getParents());
				inferredConceptIds.addAll(inferredDestinationConcept.getAncestors());
			}
		}
		
		if (!statedSourceIds.isEmpty()) {
			final Query<SnomedConceptDocument> statedSourceConceptsQuery = Query.select(SnomedConceptDocument.class)
					.fields(SnomedConceptDocument.Fields.ID, SnomedConceptDocument.Fields.STATED_PARENTS, SnomedConceptDocument.Fields.STATED_ANCESTORS)
					.where(Expressions.builder()
							.should(SnomedConceptDocument.Expressions.ids(statedSourceIds))
							.should(SnomedConceptDocument.Expressions.statedParents(statedSourceIds))
							.should(SnomedConceptDocument.Expressions.statedAncestors(statedSourceIds))
							.build())
					.limit(Integer.MAX_VALUE)
					.build();
			
			for (SnomedConceptDocument statedSourceConcept : index.search(statedSourceConceptsQuery)) {
				statedConceptIds.add(Long.parseLong(statedSourceConcept.getId()));
				statedConceptIds.addAll(statedSourceConcept.getStatedParents());
				statedConceptIds.addAll(statedSourceConcept.getStatedAncestors());
			}
		}
		
		if (!inferredSourceIds.isEmpty()) {
			final Query<SnomedConceptDocument> inferredSourceConceptsQuery = Query.select(SnomedConceptDocument.class)
					.fields(SnomedConceptDocument.Fields.ID, SnomedConceptDocument.Fields.PARENTS, SnomedConceptDocument.Fields.ANCESTORS)
					.where(Expressions.builder()
							.should(SnomedConceptDocument.Expressions.ids(inferredSourceIds))
							.should(SnomedConceptDocument.Expressions.parents(inferredSourceIds))
							.should(SnomedConceptDocument.Expressions.ancestors(inferredSourceIds))
							.build())
					.limit(Integer.MAX_VALUE)
					.build();
			
			for (SnomedConceptDocument inferredSourceConcept : index.search(inferredSourceConceptsQuery)) {
				inferredConceptIds.add(Long.parseLong(inferredSourceConcept.getId()));
				inferredConceptIds.addAll(inferredSourceConcept.getParents());
				inferredConceptIds.addAll(inferredSourceConcept.getAncestors());
			}
		}
		
		for (Concept newConcept : commitChangeSet.getNewComponents(Concept.class)) {
			long longId = Long.parseLong(newConcept.getId());
			statedConceptIds.add(longId);
			inferredConceptIds.add(longId);
		}
		
		prepareTaxonomyBuilders(commitChangeSet, index, statedConceptIds, inferredConceptIds);	
	}
	
	@Override
	protected short getTerminologyComponentId(RevisionDocument revision) {
		if (revision instanceof SnomedConceptDocument) {
			return SnomedTerminologyComponentConstants.CONCEPT_NUMBER;
		} else if (revision instanceof SnomedDescriptionIndexEntry) {
			return SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER;
		} else if (revision instanceof SnomedRelationshipIndexEntry) {
			return SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER;
		} else if (revision instanceof SnomedConstraintDocument) {
			return SnomedTerminologyComponentConstants.CONSTRAINT_NUMBER;
		} else if (revision instanceof SnomedRefSetMemberIndexEntry) {
			return SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER;
		}
		throw new UnsupportedOperationException("Unsupported revision document: " + revision);
	}
	
	@Override
	protected Collection<ChangeSetProcessor> getChangeSetProcessors() {
		return ImmutableList.<ChangeSetProcessor>builder()
				.add(new ConceptChangeProcessor(DoiDataProvider.INSTANCE, SnomedIconProvider.getInstance().getAvailableIconIds(), statedTaxonomy, inferredTaxonomy))
				.add(new DescriptionChangeProcessor())
				.add(new RelationshipChangeProcessor())
				.add(new RefSetMemberChangeProcessor())
				.add(new ConstraintChangeProcessor())
				.build();
	}
	
	private void collectIds(final Set<String> sourceIds, final Set<String> destinationIds, Iterable<Relationship> newRelationships, CharacteristicType characteristicType) {
		for (Relationship newRelationship : newRelationships) {
			if (newRelationship.getCharacteristicType().getId().equals(characteristicType.getConceptId())) {
				sourceIds.add(newRelationship.getSource().getId());
				destinationIds.add(newRelationship.getDestination().getId());
			}
		}
	}

	/**
	 * Prepares the taxonomy builder. One for representing the previous state of the ontology.
	 * One for the new state.   
	 * @param inferredConceptIds 
	 */
	private void prepareTaxonomyBuilders(final ICDOCommitChangeSet commitChangeSet, final RevisionSearcher searcher, final LongSet statedConceptIds, final LongSet inferredConceptIds) {
		log().trace("Retrieving taxonomic information from store.");
		final IStoreAccessor accessor = StoreThreadLocal.getAccessor();
		
		final FeatureToggles featureToggles = ApplicationContext.getServiceForClass(FeatureToggles.class);
		final boolean importRunning = featureToggles.isEnabled(Features.getImportFeatureToggle(SnomedDatastoreActivator.REPOSITORY_UUID, searcher.branch()));
		final boolean reindexRunning = featureToggles.isEnabled(Features.getReindexFeatureToggle(SnomedDatastoreActivator.REPOSITORY_UUID));
		final boolean checkCycles = !importRunning && !reindexRunning;
		
		final Runnable inferredRunnable = CDOServerUtils.withAccessor(new Runnable() {
			@Override
			public void run() {
				inferredTaxonomy = Taxonomies.inferred(searcher, commitChangeSet, inferredConceptIds, checkCycles);
			}
		}, accessor);
		
		final Runnable statedRunnable = CDOServerUtils.withAccessor(new Runnable() {
			@Override
			public void run() {
				statedTaxonomy = Taxonomies.stated(searcher, commitChangeSet, statedConceptIds, checkCycles);
			}
		}, accessor);
		
		ForkJoinUtils.runInParallel(inferredRunnable, statedRunnable);
	}
	
}
