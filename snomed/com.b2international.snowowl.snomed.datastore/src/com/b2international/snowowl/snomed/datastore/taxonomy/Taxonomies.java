/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Collections;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.collect.LongSets;
import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.index.revision.StagingArea;
import com.b2international.index.revision.StagingArea.RevisionPropertyDiff;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.taxonomy.ISnomedTaxonomyBuilder.TaxonomyBuilderEdge;
import com.b2international.snowowl.snomed.datastore.taxonomy.ISnomedTaxonomyBuilder.TaxonomyBuilderNode;

/**
 * @since 4.7
 */
public final class Taxonomies {

	private static final Logger LOGGER = LoggerFactory.getLogger("repository");
	
	private Taxonomies() {
	}
	
	public static Taxonomy inferred(RevisionSearcher searcher, StagingArea staging, LongCollection conceptIds, boolean checkCycles) {
		return buildTaxonomy(searcher, staging, conceptIds, CharacteristicType.INFERRED_RELATIONSHIP, checkCycles);
	}
	
	public static Taxonomy stated(RevisionSearcher searcher, StagingArea staging, LongCollection conceptIds, boolean checkCycles) {
		return buildTaxonomy(searcher, staging, conceptIds, CharacteristicType.STATED_RELATIONSHIP, checkCycles);
	}

	private static Taxonomy buildTaxonomy(RevisionSearcher searcher, StagingArea staging, LongCollection conceptIds, CharacteristicType characteristicType, boolean checkCycles) {
		try {
			final String characteristicTypeId = characteristicType.getConceptId();
			final Query<String[]> query = Query.select(String[].class)
					.from(SnomedRelationshipIndexEntry.class)
					.fields(SnomedDocument.Fields.ID, SnomedRelationshipIndexEntry.Fields.SOURCE_ID, SnomedRelationshipIndexEntry.Fields.DESTINATION_ID)
					.where(Expressions.builder()
							.filter(active())
							.filter(typeId(Concepts.IS_A))
							.filter(characteristicTypeId(characteristicTypeId))
							.filter(sourceIds(LongSets.toStringSet(conceptIds)))
							.filter(destinationIds(LongSets.toStringSet(conceptIds)))
							.build())
					.limit(Integer.MAX_VALUE)
					.build();
			final Hits<String[]> hits = searcher.search(query);
			
			final SnomedTaxonomyBuilder oldTaxonomy = new SnomedTaxonomyBuilder(conceptIds, hits.getHits());
			oldTaxonomy.setCheckCycles(checkCycles);
			final SnomedTaxonomyBuilder newTaxonomy = new SnomedTaxonomyBuilder(conceptIds, hits.getHits());
			newTaxonomy.setCheckCycles(checkCycles);
			oldTaxonomy.build();

			SnomedTaxonomyStatus status = updateTaxonomy(searcher, staging, newTaxonomy, characteristicTypeId);
			
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
			
			return new Taxonomy(newTaxonomy, oldTaxonomy, status, newEdges, changedEdges, detachedEdges);
		} catch (IOException e) {
			throw new SnowowlRuntimeException(e);
		}
	}

	private static SnomedTaxonomyStatus updateTaxonomy(RevisionSearcher searcher, StagingArea staging, SnomedTaxonomyBuilder taxonomyBuilder, String characteristicTypeId) {
		LOGGER.trace("Processing changes taxonomic information.");
		
		final Iterable<SnomedConceptDocument> newConcepts = staging.getNewObjects(SnomedConceptDocument.class)
				.collect(Collectors.toList());
		final Iterable<SnomedConceptDocument> deletedConcepts = staging.getRemovedObjects(SnomedConceptDocument.class)
				.collect(Collectors.toSet());
		final Iterable<SnomedRelationshipIndexEntry> newRelationships = staging.getNewObjects(SnomedRelationshipIndexEntry.class)
				.filter(relationship -> characteristicTypeId.equals(relationship.getCharacteristicTypeId()))
				.collect(Collectors.toList());
		final Iterable<SnomedRelationshipIndexEntry> dirtyRelationships = staging.getChangedRevisions(SnomedRelationshipIndexEntry.class)
				.map(diff -> (SnomedRelationshipIndexEntry) diff.newRevision)
				.filter(relationship -> characteristicTypeId.equals(relationship.getCharacteristicTypeId()))
				.collect(Collectors.toList());
		
		final Iterable<SnomedRelationshipIndexEntry> deletedRelationships = staging.getRemovedObjects(SnomedRelationshipIndexEntry.class)
				.filter(relationship -> characteristicTypeId.equals(relationship.getCharacteristicTypeId()))
				.collect(Collectors.toList());
		
		for (final SnomedRelationshipIndexEntry newRelationship : newRelationships) {
			taxonomyBuilder.addEdge(createEdge(newRelationship));
		}
		
		for (final SnomedRelationshipIndexEntry dirtyRelationship : dirtyRelationships) {
			taxonomyBuilder.addEdge(createEdge(dirtyRelationship));
		}
		
		for (final SnomedRelationshipIndexEntry relationship : deletedRelationships) {
			taxonomyBuilder.removeEdge(createEdge(relationship));
		}
		for (final SnomedConceptDocument newConcept : newConcepts) {
			taxonomyBuilder.addNode(createNode(newConcept));
		}
		
		for (final SnomedConceptDocument concept : deletedConcepts) {
			taxonomyBuilder.removeNode(createDeletedNode(concept.getId()));
		}
		
		staging.getChangedRevisions(SnomedConceptDocument.class, Collections.singleton(SnomedConceptDocument.Fields.ACTIVE))
			.forEach(diff -> {
				final RevisionPropertyDiff propDiff = diff.getRevisionPropertyDiff(SnomedConceptDocument.Fields.ACTIVE);
				final boolean oldValue = Boolean.parseBoolean(propDiff.getOldValue());
				final boolean newValue = Boolean.parseBoolean(propDiff.getNewValue());
				final String conceptId = diff.newRevision.getId();
				if (oldValue && !newValue) {
					// inactivation
					//we do not need this concept. either it was deactivated now or sometime earlier.
					taxonomyBuilder.removeNode(createNode(conceptId, true));
				} else if (!oldValue && newValue) {
					// consider reverting reactivation
					if (!taxonomyBuilder.containsNode(conceptId)) {
						taxonomyBuilder.addNode(createNode((SnomedConceptDocument) diff.newRevision));
					}
				}
			});
		
		LOGGER.trace("Rebuilding taxonomic information based on the changes.");
		return taxonomyBuilder.build();
	}
	
	
	/*creates a taxonomy edge instance based on the given SNOMED CT relationship*/
	private static TaxonomyBuilderEdge createEdge(final SnomedRelationshipIndexEntry relationship) {
		return new TaxonomyBuilderEdge() {
			@Override public boolean isCurrent() {
				return relationship.isActive();
			}
			@Override public String getId() {
				return relationship.getId();
			}
			@Override public boolean isValid() {
				return Concepts.IS_A.equals(relationship.getTypeId());
			}
			@Override public String getSoureId() {
				return relationship.getSourceId();
			}
			@Override public String getDestinationId() {
				return relationship.getDestinationId();
			}
		};
	}
	
	/*creates and returns with a new taxonomy node instance based on the given SNOMED CT concept*/
	private static TaxonomyBuilderNode createNode(final SnomedConceptDocument concept) {
		return new TaxonomyBuilderNode() {
			@Override public boolean isCurrent() {
				return concept.isActive();
			}
			@Override public String getId() {
				return concept.getId();
			}
		};
	}

	/*creates and returns with a new taxonomy node instance based on the given SNOMED CT concept*/
	private static TaxonomyBuilderNode createNode(final String id, final boolean active) {
		return new TaxonomyBuilderNode() {
			@Override public boolean isCurrent() {
				return active;
			}
			@Override public String getId() {
				return id;
			}
		};
	}
	
	private static TaxonomyBuilderNode createDeletedNode(final String id) {
		return new TaxonomyBuilderNode() {
			@Override public boolean isCurrent() {
				throw new UnsupportedOperationException("This method should not be called when removing taxonomy nodes.");
			}
			@Override public String getId() {
				return id;
			}
		};
	}
	
}
