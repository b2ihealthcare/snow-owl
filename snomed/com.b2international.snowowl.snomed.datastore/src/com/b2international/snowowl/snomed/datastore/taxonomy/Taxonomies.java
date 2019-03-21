/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.taxonomy;

import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument.Expressions.active;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry.Expressions.characteristicTypeId;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry.Expressions.destinationIds;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry.Expressions.sourceIds;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry.Expressions.typeId;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.CompareUtils;
import com.b2international.commons.collect.LongSets;
import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedOWLRelationshipDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedOWLExpressionConverter;
import com.google.common.collect.ImmutableList;

/**
 * @since 4.7
 */
public final class Taxonomies {

	private Taxonomies() {
	}
	
	public static Taxonomy inferred(RevisionSearcher searcher, SnomedOWLExpressionConverter expressionConverter, ICDOCommitChangeSet commitChangeSet, LongCollection conceptIds, boolean checkCycles) {
		return buildTaxonomy(searcher, expressionConverter, commitChangeSet, conceptIds, CharacteristicType.INFERRED_RELATIONSHIP, checkCycles);
	}
	
	public static Taxonomy stated(RevisionSearcher searcher, SnomedOWLExpressionConverter expressionConverter, ICDOCommitChangeSet commitChangeSet, LongCollection conceptIds, boolean checkCycles) {
		return buildTaxonomy(searcher, expressionConverter, commitChangeSet, conceptIds, CharacteristicType.STATED_RELATIONSHIP, checkCycles);
	}

	private static Taxonomy buildTaxonomy(RevisionSearcher searcher, SnomedOWLExpressionConverter expressionConverter, ICDOCommitChangeSet commitChangeSet, LongCollection conceptIds, CharacteristicType characteristicType, boolean checkCycles) {
		try {
			// merge stated relationships and OWL axiom relationships into a single array
			ImmutableList.Builder<String[]> isaStatementsBuilder = ImmutableList.builder();
			
			final String characteristicTypeId = characteristicType.getConceptId();
			final Query<String[]> activeStatedISARelationshipsQuery = Query.select(String[].class)
					.from(SnomedRelationshipIndexEntry.class)
					.fields(SnomedRelationshipIndexEntry.Fields.SOURCE_ID, SnomedRelationshipIndexEntry.Fields.DESTINATION_ID)
					.where(Expressions.builder()
							.filter(active())
							.filter(typeId(Concepts.IS_A))
							.filter(characteristicTypeId(characteristicTypeId))
							.filter(sourceIds(LongSets.toStringSet(conceptIds)))
							.filter(destinationIds(LongSets.toStringSet(conceptIds)))
							.build())
					.limit(Integer.MAX_VALUE)
					.build();
			Hits<String[]> activeIsaRelationships = searcher.search(activeStatedISARelationshipsQuery);
			activeIsaRelationships.forEach(isaStatementsBuilder::add);
			activeIsaRelationships = null;
			
			if (Concepts.STATED_RELATIONSHIP.equals(characteristicTypeId)) {
				// search existing axioms defined for the given set of conceptIds
				final Query<SnomedRefSetMemberIndexEntry> activeAxiomISARelationshipsQuery = Query.select(SnomedRefSetMemberIndexEntry.class)
						.where(Expressions.builder()
								.filter(active())
								.filter(SnomedRefSetMemberIndexEntry.Expressions.referencedComponentIds(LongSets.toStringSet(conceptIds)))
								.filter(Expressions.nestedMatch(SnomedRefSetMemberIndexEntry.Fields.CLASS_AXIOM_RELATIONSHIP, 
										Expressions.builder()
											.filter(typeId(Concepts.IS_A))
											.filter(destinationIds(LongSets.toStringSet(conceptIds)))
										.build()
										))
								.build())
						.limit(Integer.MAX_VALUE)
						.build();
				Hits<SnomedRefSetMemberIndexEntry> activeAxiomISARelationships = searcher.search(activeAxiomISARelationshipsQuery);
				activeAxiomISARelationships.forEach(owlMember -> {
					if (!CompareUtils.isEmpty(owlMember.getClassAxiomRelationships())) {
						for (SnomedOWLRelationshipDocument classAxiom : owlMember.getClassAxiomRelationships()) {
							if (Concepts.IS_A.equals(classAxiom.getTypeId())) {
								isaStatementsBuilder.add(new String[] { owlMember.getReferencedComponentId(), classAxiom.getDestinationId() });
							}
						}
					}
				});
				activeAxiomISARelationships = null;
			}
			
			
			Collection<String[]> isaStatements = isaStatementsBuilder.build();
			
			final SnomedTaxonomyBuilder oldTaxonomy = new SnomedTaxonomyBuilder(conceptIds, isaStatements);
			oldTaxonomy.setCheckCycles(checkCycles);
			final SnomedTaxonomyBuilder newTaxonomy = new SnomedTaxonomyBuilder(conceptIds, isaStatements);
			newTaxonomy.setCheckCycles(checkCycles);
			isaStatements = null;
			
			oldTaxonomy.build();
			SnomedTaxonomyUpdateRunnable taxonomyUpdate = new SnomedTaxonomyUpdateRunnable(searcher, expressionConverter, commitChangeSet, newTaxonomy, characteristicTypeId);
			taxonomyUpdate.run();
			final LongSet newKeys = newTaxonomy.getEdges().keySet();
			final LongSet oldKeys = oldTaxonomy.getEdges().keySet();
			
			// new edges
			final LongSet newEdges = LongSets.difference(newKeys, oldKeys);
			// changed edges
			final LongIterator pcEdges = LongSets.intersection(newKeys, oldKeys).iterator();
			final LongSet changedEdges = PrimitiveSets.newLongOpenHashSet();
			while (pcEdges.hasNext()) {
				final long nextEdge = pcEdges.next();
				long[] oldValue = oldTaxonomy.getEdges().get(nextEdge);
				long[] newValue = newTaxonomy.getEdges().get(nextEdge);
				if (!Arrays.equals(oldValue, newValue)) {
					changedEdges.add(nextEdge);
				}
			}
			
			// detached edges
			final LongSet detachedEdges = LongSets.difference(oldKeys, newKeys);
			
			return new Taxonomy(newTaxonomy, oldTaxonomy, taxonomyUpdate.getTaxonomyBuilderResult(), newEdges, changedEdges, detachedEdges);
		} catch (IOException e) {
			throw new SnowowlRuntimeException(e);
		}
	}
	
}
