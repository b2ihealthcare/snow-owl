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
import java.util.Collection;
import java.util.Set;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.ints.IntIterator;
import com.b2international.collections.ints.IntSet;
import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongIterator;
import com.b2international.commons.CompareUtils;
import com.b2international.commons.collect.LongSets;
import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.IComponent;
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
			ImmutableList.Builder<Object[]> isaStatementsBuilder = ImmutableList.builder();
			
			final String characteristicTypeId = characteristicType.getConceptId();
			final Set<String> concepts = LongSets.toStringSet(conceptIds);
			
			final Query<String[]> activeStatedISARelationshipsQuery = Query.select(String[].class)
					.from(SnomedRelationshipIndexEntry.class)
					.fields(SnomedRelationshipIndexEntry.Fields.ID, SnomedRelationshipIndexEntry.Fields.SOURCE_ID, SnomedRelationshipIndexEntry.Fields.DESTINATION_ID)
					.where(Expressions.builder()
							.filter(active())
							.filter(typeId(Concepts.IS_A))
							.filter(characteristicTypeId(characteristicTypeId))
							.filter(sourceIds(concepts))
							.filter(destinationIds(concepts))
							.build())
					.limit(Integer.MAX_VALUE)
					.build();
			Hits<String[]> activeIsaRelationships = searcher.search(activeStatedISARelationshipsQuery);
			activeIsaRelationships.forEach(activeIsaRelationship -> {
				isaStatementsBuilder.add(new Object[] { activeIsaRelationship[0], Long.parseLong(activeIsaRelationship[1]), new long[] { Long.parseLong(activeIsaRelationship[2]) } });
			});
			activeIsaRelationships = null;
			
			if (Concepts.STATED_RELATIONSHIP.equals(characteristicTypeId)) {
				// search existing axioms defined for the given set of conceptIds
				final Query<SnomedRefSetMemberIndexEntry> activeAxiomISARelationshipsQuery = Query.select(SnomedRefSetMemberIndexEntry.class)
						.where(Expressions.builder()
								.filter(active())
								.filter(SnomedRefSetMemberIndexEntry.Expressions.referencedComponentIds(concepts))
								.filter(Expressions.nestedMatch(SnomedRefSetMemberIndexEntry.Fields.CLASS_AXIOM_RELATIONSHIP, 
										Expressions.builder()
											.filter(typeId(Concepts.IS_A))
											.filter(destinationIds(concepts))
										.build()
										))
								.build())
						.limit(Integer.MAX_VALUE)
						.build();
				Hits<SnomedRefSetMemberIndexEntry> activeAxiomISARelationships = searcher.search(activeAxiomISARelationshipsQuery);
				activeAxiomISARelationships.forEach(owlMember -> {
					if (!CompareUtils.isEmpty(owlMember.getClassAxiomRelationships())) {
						long[] destinationIds = owlMember.getClassAxiomRelationships()
							.stream()
							.filter(classAxiom -> Concepts.IS_A.equals(classAxiom.getTypeId()))
							.map(SnomedOWLRelationshipDocument::getDestinationId)
							.mapToLong(Long::parseLong)
							.toArray();
						isaStatementsBuilder.add(new Object[] { owlMember.getId(), Long.parseLong(owlMember.getReferencedComponentId()), destinationIds });
					}
				});
				activeAxiomISARelationships = null;
			}
			
			
			Collection<Object[]> isaStatements = isaStatementsBuilder.build();
			
			final TaxonomyGraph oldTaxonomy = new TaxonomyGraph(conceptIds.size(), isaStatements.size());
			oldTaxonomy.setCheckCycles(checkCycles);
			final TaxonomyGraph newTaxonomy = new TaxonomyGraph(conceptIds.size(), isaStatements.size());
			newTaxonomy.setCheckCycles(checkCycles);
			
			// populate nodes
			LongIterator conceptIdsIt = conceptIds.iterator();
			while (conceptIdsIt.hasNext()) {
				long nodeId = conceptIdsIt.next();
				if (IComponent.ROOT_IDL == nodeId) {
					continue;
				}
				oldTaxonomy.addNode(nodeId);
				newTaxonomy.addNode(nodeId);
			}
			
			// populate edges
			for (Object[] isaStatement : isaStatements) {
				oldTaxonomy.addEdge((String) isaStatement[0], (long) isaStatement[1], (long[]) isaStatement[2]);
				newTaxonomy.addEdge((String) isaStatement[0], (long) isaStatement[1], (long[]) isaStatement[2]);
			}
			
			isaStatements = null;
			
			oldTaxonomy.update();
			
			final TaxonomyGraphUpdater updater = new TaxonomyGraphUpdater(searcher, expressionConverter, commitChangeSet, characteristicTypeId);
			final TaxonomyGraphStatus updateResult = updater.update(newTaxonomy);
			final IntSet newKeys = newTaxonomy.getEdgeIds();
			final IntSet oldKeys = oldTaxonomy.getEdgeIds();
			
			// new edges
			final IntSet newEdges = PrimitiveSets.difference(newKeys, oldKeys);
			// changed edges
			final IntIterator pcEdges = PrimitiveSets.intersection(newKeys, oldKeys).iterator();
			final IntSet changedEdges = PrimitiveSets.newIntOpenHashSet();
			while (pcEdges.hasNext()) {
				final int nextEdge = pcEdges.next();
				Edges oldValue = oldTaxonomy.getEdge(nextEdge);
				Edges newValue = newTaxonomy.getEdge(nextEdge);
				if (!oldValue.equals(newValue)) {
					changedEdges.add(nextEdge);
				}
			}
			
			// detached edges
			final IntSet detachedEdges = PrimitiveSets.difference(oldKeys, newKeys);
			
			return new Taxonomy(newTaxonomy, oldTaxonomy, updateResult, newEdges, changedEdges, detachedEdges);
		} catch (IOException e) {
			throw new SnowowlRuntimeException(e);
		}
	}
	
}
