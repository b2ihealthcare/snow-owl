/*
 * Copyright 2011-2023 B2i Healthcare, https://b2ihealthcare.com
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
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongCollections;
import com.b2international.collections.longs.LongIterator;
import com.b2international.commons.CompareUtils;
import com.b2international.commons.collect.LongSets;
import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.index.revision.StagingArea;
import com.b2international.index.revision.StagingArea.RevisionPropertyDiff;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedOWLRelationshipDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedOWLExpressionConverter;
import com.b2international.snowowl.snomed.datastore.request.SnomedOWLExpressionConverterResult;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

/**
 * @since 4.7
 */
public final class Taxonomies {

	private static final Logger LOGGER = LoggerFactory.getLogger("repository");
	
	private Taxonomies() {
	}
	
	public static Taxonomy inferred(RevisionSearcher searcher, SnomedOWLExpressionConverter expressionConverter, StagingArea staging, LongCollection conceptIds, boolean checkCycles) {
		return buildTaxonomy(searcher, expressionConverter, staging, conceptIds, Concepts.INFERRED_RELATIONSHIP, checkCycles);
	}
	
	public static Taxonomy stated(RevisionSearcher searcher, SnomedOWLExpressionConverter expressionConverter, StagingArea staging, LongCollection conceptIds, boolean checkCycles) {
		return buildTaxonomy(searcher, expressionConverter, staging, conceptIds, Concepts.STATED_RELATIONSHIP, checkCycles);
	}

	private static Taxonomy buildTaxonomy(RevisionSearcher searcher, SnomedOWLExpressionConverter expressionConverter, StagingArea staging, LongCollection conceptIds, String characteristicTypeId, boolean checkCycles) {
		try {
			Collection<Object[]> isaStatements = getStatements(searcher, conceptIds, characteristicTypeId, true);
			
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
			
			final TaxonomyGraphStatus status = updateTaxonomy(searcher, expressionConverter, staging, newTaxonomy, characteristicTypeId); 
			
			final Set<String> newKeys = newTaxonomy.getEdgeIds();
			final Set<String> oldKeys = oldTaxonomy.getEdgeIds();
			
			// new edges
			final Set<String> newEdges = Sets.difference(newKeys, oldKeys);
			// changed edges
			final Set<String> changedEdges = Sets.newHashSet();
			for (String nextEdge : Sets.intersection(newKeys, oldKeys)) {
				Edges oldValue = oldTaxonomy.getEdge(nextEdge);
				Edges newValue = newTaxonomy.getEdge(nextEdge);
				if (!oldValue.equals(newValue)) {
					changedEdges.add(nextEdge);
				}
			}
			
			// detached edges
			final Set<String> detachedEdges = Sets.difference(oldKeys, newKeys);
			
			return new Taxonomy(newTaxonomy, oldTaxonomy, status, newEdges, changedEdges, detachedEdges);
		} catch (IOException e) {
			throw new SnowowlRuntimeException(e);
		}
	}

	private static TaxonomyGraphStatus updateTaxonomy(RevisionSearcher searcher, 
			SnomedOWLExpressionConverter expressionConverter, 
			StagingArea staging, 
			TaxonomyGraph graphToUpdate, 
			String characteristicTypeId) throws IOException {

		LOGGER.trace("Processing changes taxonomic information.");
		
		staging.getNewObjects(SnomedRelationshipIndexEntry.class)
			.filter(relationship -> characteristicTypeId.equals(relationship.getCharacteristicTypeId()))
			.forEach(newRelationship -> updateEdge(newRelationship, graphToUpdate));
		
		final Set<String> relationshipsToExcludeFromReactivatedConcepts = newHashSet();
		
		staging.getChangedRevisions(SnomedRelationshipIndexEntry.class)
			.map(diff -> (SnomedRelationshipIndexEntry) diff.newRevision)
			.filter(relationship -> characteristicTypeId.equals(relationship.getCharacteristicTypeId()))
			.forEach(dirtyRelationship -> {
				relationshipsToExcludeFromReactivatedConcepts.add(dirtyRelationship.getId());
				updateEdge(dirtyRelationship, graphToUpdate);
			});
		
		staging.getRemovedObjects(SnomedRelationshipIndexEntry.class)
			.filter(relationship -> characteristicTypeId.equals(relationship.getCharacteristicTypeId()))
			.forEach(relationship -> {
				relationshipsToExcludeFromReactivatedConcepts.add(relationship.getId());
				graphToUpdate.removeEdge(relationship.getId());
			});
		
		if (Concepts.STATED_RELATIONSHIP.equals(characteristicTypeId)) {
			staging.getNewObjects(SnomedRefSetMemberIndexEntry.class)
				.filter(member -> SnomedRefSetType.OWL_AXIOM == member.getReferenceSetType())
				.forEach(member -> updateEdge(member, graphToUpdate, expressionConverter));
			
			staging.getChangedRevisions(SnomedRefSetMemberIndexEntry.class)
				.map(diff -> (SnomedRefSetMemberIndexEntry) diff.newRevision)
				.filter(member -> SnomedRefSetType.OWL_AXIOM == member.getReferenceSetType())
				.forEach(member -> updateEdge(member, graphToUpdate, expressionConverter));
			
			staging.getRemovedObjects(SnomedRefSetMemberIndexEntry.class)
				.filter(member -> SnomedRefSetType.OWL_AXIOM == member.getReferenceSetType())
				.map(SnomedRefSetMemberIndexEntry::getId)
				.forEach(graphToUpdate::removeEdge);
		}
		
		staging
			.getNewObjects(SnomedConceptDocument.class)
			.forEach(newConcept -> updateConcept(newConcept, graphToUpdate));
		
		staging.getRemovedObjects(SnomedConceptDocument.class)
			.forEach(concept -> graphToUpdate.removeNode(concept.getId()));
		
		final Set<String> conceptWithPossibleMissingRelationships = newHashSet();
		
		staging.getChangedRevisions(SnomedConceptDocument.class, Collections.singleton(SnomedConceptDocument.Fields.ACTIVE))
			.forEach(diff -> {
				final RevisionPropertyDiff propDiff = diff.getRevisionPropertyDiff(SnomedConceptDocument.Fields.ACTIVE);
				final boolean oldValue = Boolean.parseBoolean(propDiff.getOldValue());
				final boolean newValue = Boolean.parseBoolean(propDiff.getNewValue());
				final String conceptId = diff.newRevision.getId();
				if (!oldValue && newValue) {
					// make sure the node is part of the new tree
					graphToUpdate.addNode(conceptId);
					conceptWithPossibleMissingRelationships.add(conceptId);
				}
			});
		
		if (!conceptWithPossibleMissingRelationships.isEmpty()) {
			Hits<String[]> possibleMissingRelationships = searcher.search(Query.select(String[].class)
					.from(SnomedRelationshipIndexEntry.class)
					.fields(SnomedRelationshipIndexEntry.Fields.ID, SnomedRelationshipIndexEntry.Fields.SOURCE_ID, SnomedRelationshipIndexEntry.Fields.DESTINATION_ID)
					.where(Expressions.bool()
							.filter(SnomedRelationshipIndexEntry.Expressions.active())
							.filter(SnomedRelationshipIndexEntry.Expressions.characteristicTypeId(characteristicTypeId))
							.filter(SnomedRelationshipIndexEntry.Expressions.typeId(Concepts.IS_A))
							.filter(SnomedRelationshipIndexEntry.Expressions.sourceIds(conceptWithPossibleMissingRelationships))
							.mustNot(SnomedRelationshipIndexEntry.Expressions.ids(relationshipsToExcludeFromReactivatedConcepts))
							.build()
					)
					.limit(Integer.MAX_VALUE)
					.build());
			
			for (String[] relationship : possibleMissingRelationships) {
				graphToUpdate.addNode(relationship[2]);
				graphToUpdate.addEdge(relationship[0], Long.parseLong(relationship[1]), new long[] { Long.parseLong(relationship[2]) });
			}
		}
		
		LOGGER.trace("Rebuilding taxonomic information based on the changes.");
		return graphToUpdate.update();
	}
	
	private static void updateConcept(SnomedConceptDocument concept, TaxonomyGraph graphToUpdate) {
		if (concept.isActive()) {
			graphToUpdate.addNode(concept.getId());
		}
	}

	private static void updateEdge(SnomedRelationshipIndexEntry relationship, TaxonomyGraph graphToUpdate) {
		if (!relationship.isActive()) {
			graphToUpdate.removeEdge(relationship.getId());
		} else if (Concepts.IS_A.equals(relationship.getTypeId())) {
			// XXX: IS A relationships are expected to have a destination ID, not a value
			checkState(!relationship.hasValue(), "IS A relationship found with value: %s", relationship.getId());
			
			graphToUpdate.addEdge(
				relationship.getId(),
				Long.parseLong(relationship.getSourceId()),
				new long[] { Long.parseLong(relationship.getDestinationId()) }
			);
		}
	}
	
	private static void updateEdge(SnomedRefSetMemberIndexEntry member, TaxonomyGraph graphToUpdate, SnomedOWLExpressionConverter expressionConverter) {
		if (member.isActive()) {
			SnomedOWLExpressionConverterResult result = expressionConverter.toSnomedOWLRelationships(member.getReferencedComponentId(), member.getOwlExpression());
			if (!CompareUtils.isEmpty(result.getClassAxiomRelationships())) {
				/*
				 * XXX: IS A relationships are expected to have a destination ID, not a value,
				 * but we do not check this explicitly here -- Long#parseLong will throw a
				 * NumberFormatException if it encounters a null value.
				 */
				final long[] destinationIds = result.getClassAxiomRelationships().stream()
					.filter(r -> Concepts.IS_A.equals(r.getTypeId()))
					.map(SnomedOWLRelationshipDocument::getDestinationId)
					.mapToLong(Long::parseLong)
					.toArray();
				graphToUpdate.addEdge(member.getId(), Long.parseLong(member.getReferencedComponentId()), destinationIds);
			} else {
				graphToUpdate.removeEdge(member.getId());
			}
		} else {
			graphToUpdate.removeEdge(member.getId());
		}
	}

	private static Collection<Object[]> getStatements(RevisionSearcher searcher, LongCollection conceptIds, String characteristicTypeId, boolean filterByConceptIds) throws IOException {
		// merge stated relationships and OWL axiom relationships into a single array
		ImmutableList.Builder<Object[]> isaStatementsBuilder = ImmutableList.builder();
		
		final Set<String> concepts = LongSets.toStringSet(conceptIds);
		
		ExpressionBuilder activeIsaRelationshipQuery = Expressions.bool()
				.filter(active())
				.filter(typeId(Concepts.IS_A))
				.filter(characteristicTypeId(characteristicTypeId));

		if (filterByConceptIds) {
			activeIsaRelationshipQuery
				.filter(sourceIds(concepts))
				.filter(destinationIds(concepts));
		}
		
		final Query<String[]> activeStatedISARelationshipsQuery = Query.select(String[].class)
				.from(SnomedRelationshipIndexEntry.class)
				.fields(SnomedRelationshipIndexEntry.Fields.ID, SnomedRelationshipIndexEntry.Fields.SOURCE_ID, SnomedRelationshipIndexEntry.Fields.DESTINATION_ID)
				.where(activeIsaRelationshipQuery.build())
				.limit(Integer.MAX_VALUE)
				.build();
		Hits<String[]> activeIsaRelationships = searcher.search(activeStatedISARelationshipsQuery);
		activeIsaRelationships.forEach(activeIsaRelationship -> {
			isaStatementsBuilder.add(new Object[] { activeIsaRelationship[0], Long.parseLong(activeIsaRelationship[1]), new long[] { Long.parseLong(activeIsaRelationship[2]) } });
		});
		activeIsaRelationships = null;
		
		if (Concepts.STATED_RELATIONSHIP.equals(characteristicTypeId)) {
			// search existing axioms defined for the given set of conceptIds
			ExpressionBuilder activeOwlAxiomMemberQuery = Expressions.bool()
					.filter(active());
			
			if (filterByConceptIds) {
				activeOwlAxiomMemberQuery
					.filter(SnomedRefSetMemberIndexEntry.Expressions.referencedComponentIds(concepts))
					.filter(
						Expressions.nestedMatch(SnomedRefSetMemberIndexEntry.Fields.CLASS_AXIOM_RELATIONSHIP, 
							Expressions.bool()
								.filter(typeId(Concepts.IS_A))
								.filter(destinationIds(concepts))
							.build()
						)
					);
			} else {
				activeOwlAxiomMemberQuery.filter(
					Expressions.nestedMatch(SnomedRefSetMemberIndexEntry.Fields.CLASS_AXIOM_RELATIONSHIP, 
						Expressions.bool()
							.filter(typeId(Concepts.IS_A))
						.build()
					)
				);
			}
			
			final Query<SnomedRefSetMemberIndexEntry> activeAxiomISARelationshipsQuery = Query.select(SnomedRefSetMemberIndexEntry.class)
					.where(activeOwlAxiomMemberQuery.build())
					.limit(Integer.MAX_VALUE)
					.build();
			Hits<SnomedRefSetMemberIndexEntry> activeAxiomISARelationships = searcher.search(activeAxiomISARelationshipsQuery);
			activeAxiomISARelationships.forEach(owlMember -> {
				if (!CompareUtils.isEmpty(owlMember.getClassAxiomRelationships())) {
					// XXX: breaks with a NumberFormatException if any of the IS A relationships has a value
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
		
		return isaStatementsBuilder.build();
	}

	public static Collection<Object[]> getAllStatements(RevisionSearcher searcher, String characteristicTypeId) throws IOException {
		return getStatements(searcher, LongCollections.emptySet(), characteristicTypeId, false);
	}
	
}
