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
package com.b2international.snowowl.datastore.server.snomed.merge.rules;

import static com.google.common.collect.Lists.newArrayList;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.cdo.transaction.CDOTransaction;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongSet;
import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.RevisionIndexRead;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.merge.ConflictingAttribute;
import com.b2international.snowowl.core.merge.ConflictingAttributeImpl;
import com.b2international.snowowl.core.merge.MergeConflict;
import com.b2international.snowowl.core.merge.MergeConflict.ConflictType;
import com.b2international.snowowl.core.merge.MergeConflictImpl;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.taxonomy.InvalidRelationship;
import com.b2international.snowowl.snomed.datastore.taxonomy.InvalidRelationship.MissingConcept;
import com.b2international.snowowl.snomed.datastore.taxonomy.SnomedTaxonomyBuilder;
import com.b2international.snowowl.snomed.datastore.taxonomy.SnomedTaxonomyStatus;
import com.b2international.snowowl.snomed.datastore.taxonomy.SnomedTaxonomyUpdateRunnable;
import com.google.common.collect.ImmutableList;

/**
 * @since 4.7
 */
public class SnomedInvalidTaxonomyMergeConflictRule extends AbstractSnomedMergeConflictRule {

	private final RevisionIndex index;

	public SnomedInvalidTaxonomyMergeConflictRule(RevisionIndex index) {
		this.index = index;
	}
	
	@Override
	public Collection<MergeConflict> validate(final CDOTransaction transaction) {
		final IBranchPath branchPath = BranchPathUtils.createPath(transaction);
		return index.read(branchPath.getPath(), new RevisionIndexRead<List<MergeConflict>>() {
			@Override
			public List<MergeConflict> execute(final RevisionSearcher searcher) throws IOException {
				final Query<String> allConceptsQuery = Query.select(String.class)
						.from(SnomedConceptDocument.class)
						.fields(SnomedDocument.Fields.ID)
						.where(Expressions.matchAll())
						.limit(Integer.MAX_VALUE)
						.build();
				
				final Hits<String> allConcepts = searcher.search(allConceptsQuery);
				final LongSet conceptIds = PrimitiveSets.newLongOpenHashSet(allConcepts.getTotal());
				
				for (String conceptId : allConcepts) {
					conceptIds.add(Long.parseLong(conceptId));
				}
				
				final List<MergeConflict> conflicts = newArrayList();
				
				for (final String characteristicTypeId : ImmutableList.of(Concepts.STATED_RELATIONSHIP, Concepts.INFERRED_RELATIONSHIP)) {
					final Collection<String[]> statements = getActiveStatements(searcher, characteristicTypeId);
					final SnomedTaxonomyBuilder taxonomyBuilder = new SnomedTaxonomyBuilder(conceptIds, statements);
					final SnomedTaxonomyUpdateRunnable taxonomyRunnable = new SnomedTaxonomyUpdateRunnable(searcher, transaction, taxonomyBuilder, characteristicTypeId);
					taxonomyRunnable.run();
					
					final SnomedTaxonomyStatus status = taxonomyRunnable.getTaxonomyBuilderResult();
					if (!status.getStatus().isOK()) {
						for (InvalidRelationship invalidRelationship : status.getInvalidRelationships()) {
							
							String relationshipId = String.valueOf(invalidRelationship.getRelationshipId());
							String sourceId = String.valueOf(invalidRelationship.getSourceId());
							String destinationId = String.valueOf(invalidRelationship.getDestinationId());
							
							ConflictingAttribute attribute;
							
							if (invalidRelationship.getMissingConcept() == MissingConcept.SOURCE) {
								attribute = ConflictingAttributeImpl.builder().property("sourceId").value(sourceId).build();
							} else {
								attribute = ConflictingAttributeImpl.builder().property("destinationId").value(destinationId).build();
							}
							
							conflicts.add(MergeConflictImpl.builder()
								.componentId(relationshipId)
								.componentType("Relationship")
								.conflictingAttribute(attribute)
								.type(ConflictType.HAS_INACTIVE_REFERENCE)
								.build());
						}
					}
				}
				
				return conflicts;
			}
		});
	}
	
	private Collection<String[]> getActiveStatements(RevisionSearcher searcher, String characteristicTypeId) throws IOException {
		final Query<String[]> query = Query.select(String[].class)
				.from(SnomedRelationshipIndexEntry.class)
				.fields(SnomedDocument.Fields.ID, SnomedRelationshipIndexEntry.Fields.SOURCE_ID, SnomedRelationshipIndexEntry.Fields.DESTINATION_ID)
				.where(Expressions.builder()
						.filter(SnomedRelationshipIndexEntry.Expressions.active())
						.filter(SnomedRelationshipIndexEntry.Expressions.typeId(Concepts.IS_A))
						.filter(SnomedRelationshipIndexEntry.Expressions.characteristicTypeId(characteristicTypeId))
						.build())
				.limit(Integer.MAX_VALUE)
				.build();
		return searcher.search(query).getHits();
	}

}
