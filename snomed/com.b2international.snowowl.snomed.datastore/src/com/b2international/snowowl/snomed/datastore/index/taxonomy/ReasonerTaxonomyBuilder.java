/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.index.taxonomy;

import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.defining;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.exhaustive;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument.Expressions.active;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument.Expressions.modules;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry.Expressions.refSetTypes;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry.Expressions.characteristicTypeId;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry.Expressions.characteristicTypeIds;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry.Expressions.group;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry.Expressions.typeId;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Sets.newHashSetWithExpectedSize;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.collections.PrimitiveLists;
import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongCollections;
import com.b2international.collections.longs.LongKeyMap;
import com.b2international.collections.longs.LongList;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.CompareUtils;
import com.b2international.commons.time.TimeUtil;
import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.query.SortBy;
import com.b2international.index.query.SortBy.Order;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.*;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.*;
import com.b2international.snowowl.snomed.datastore.index.entry.*;
import com.b2international.snowowl.snomed.datastore.index.taxonomy.InternalIdMultimap.Builder;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Builds a snapshot of the ontology for reasoner input and normal form generation.
 * 
 * @since
 */
public final class ReasonerTaxonomyBuilder {

	private static final int AXIOM_GROUP_BASE = 10_000;

	private static final Logger LOGGER = LoggerFactory.getLogger("reasoner-taxonomy");

	private static final Set<String> CD_CHARACTERISTIC_TYPE_IDS = ImmutableSet.of(
			Concepts.STATED_RELATIONSHIP, 
			Concepts.ADDITIONAL_RELATIONSHIP,
			Concepts.INFERRED_RELATIONSHIP);
	
	private static final int SCROLL_LIMIT = 50_000;

	private final Stopwatch stopwatch;
	private final Set<String> excludedModuleIds;
	
	private InternalIdMap.Builder conceptMapBuilder;
	private InternalIdMap conceptMap;

	private InternalIdEdges.Builder statedAncestors;
	private InternalIdEdges.Builder statedDescendants;
	private LongKeyMap<String> fullySpecifiedNames;

	private InternalSctIdSet.Builder definingConcepts;
	private InternalSctIdSet.Builder exhaustiveConcepts;

	// Holds statement fragments collected from stated relationships (including IS A relationships)
	private InternalIdMultimap.Builder<StatementFragment> statedRelationships;
	// Holds statement fragments extracted from OWL axioms (_not_ including IS A, as we only need this information for the normal form)
	private InternalIdMultimap.Builder<StatementFragment> axiomNonIsaRelationships;
	private InternalIdMultimap.Builder<StatementFragment> existingInferredRelationships;
	private InternalIdMultimap.Builder<StatementFragment> additionalGroupedRelationships;

	// Holds OWL axioms related to the referenced component
	private InternalIdMultimap.Builder<String> statedAxioms;
	private LongSet neverGroupedTypeIds;
	private ImmutableSet.Builder<PropertyChain> propertyChains;
	
	private InternalIdMultimap.Builder<ConcreteDomainFragment> statedConcreteDomainMembers;
	private InternalIdMultimap.Builder<ConcreteDomainFragment> additionalGroupedConcreteDomainMembers;
	private InternalIdMultimap.Builder<ConcreteDomainFragment> inferredConcreteDomainMembers;

	public ReasonerTaxonomyBuilder() {
		this(ImmutableSet.<String>of());
	}
	
	public ReasonerTaxonomyBuilder(final Set<String> excludedModuleIds) {
		this.stopwatch = Stopwatch.createStarted();
		this.excludedModuleIds = ImmutableSet.copyOf(checkNotNull(excludedModuleIds, "excludedModuleIds"));
		this.conceptMapBuilder = InternalIdMap.builder();
	}			

	private void entering(final String taskName) {
		LOGGER.info(">>> {}", taskName);
	}

	private void leaving(final String taskName) {
		LOGGER.info("<<< {} [@{}]", taskName, TimeUtil.toString(stopwatch));
	}

	public ReasonerTaxonomyBuilder addActiveConceptIds(final RevisionSearcher searcher) {
		entering("Registering active concept IDs using revision searcher");

		final ExpressionBuilder whereExpressionBuilder = Expressions.builder().filter(active());
		
		if (!excludedModuleIds.isEmpty()) {
			whereExpressionBuilder.mustNot(modules(excludedModuleIds));
		}
		
		final Query<String[]> query = Query.select(String[].class)
				.from(SnomedConceptDocument.class)
				.fields(SnomedConceptDocument.Fields.ID)
				.where(whereExpressionBuilder.build())
				.limit(SCROLL_LIMIT)
				.build();

		final List<String> conceptIds = new ArrayList<>(SCROLL_LIMIT);
		for (final Hits<String[]> hits : searcher.scroll(query)) {
			for (String[] hit : hits) {
				conceptIds.add(hit[0]);
			}
			conceptMapBuilder.addAll(conceptIds);
			conceptIds.clear();
		}

		leaving("Registering active concept IDs using revision searcher");
		return this;
	}

	public ReasonerTaxonomyBuilder addActiveConceptIds(final Stream<SnomedConcept> concepts) {
		entering("Registering concept IDs from stream");

		Stream<SnomedConcept> filteredConcepts = concepts.filter(c -> c.isActive() 
				&& !excludedModuleIds.contains(c.getModuleId()));

		final Collection<String> conceptIds = new ArrayList<>(SCROLL_LIMIT);
		for (final Collection<SnomedConcept> chunk : Iterables.partition(filteredConcepts::iterator, SCROLL_LIMIT)) {
			for (SnomedConcept concept : chunk) {
				conceptIds.add(concept.getId());
			}
			conceptMapBuilder.addAll(conceptIds);
			conceptIds.clear();
		}

		leaving("Registering concept IDs from stream");
		return this;
	}

	public ReasonerTaxonomyBuilder finishConcepts() {
		conceptMap = conceptMapBuilder.build();
		conceptMapBuilder = null;
		// fullySpecifiedNames is lazily initialized

		statedAncestors = InternalIdEdges.builder(conceptMap);
		statedDescendants = InternalIdEdges.builder(conceptMap);
		
		definingConcepts = InternalSctIdSet.builder(conceptMap);
		exhaustiveConcepts = InternalSctIdSet.builder(conceptMap);
		
		statedRelationships = InternalIdMultimap.builder(conceptMap);
		axiomNonIsaRelationships = InternalIdMultimap.builder(conceptMap);
		additionalGroupedRelationships = InternalIdMultimap.builder(conceptMap);
		existingInferredRelationships = InternalIdMultimap.builder(conceptMap);
		
		statedAxioms = InternalIdMultimap.builder(conceptMap);
		neverGroupedTypeIds = PrimitiveSets.newLongOpenHashSetWithExpectedSize(4);
		propertyChains = ImmutableSet.builder();
		
		statedConcreteDomainMembers = InternalIdMultimap.builder(conceptMap);
		additionalGroupedConcreteDomainMembers = InternalIdMultimap.builder(conceptMap);
		inferredConcreteDomainMembers = InternalIdMultimap.builder(conceptMap);
		
		return this;
	}
	
	public ReasonerTaxonomyBuilder addFullySpecifiedNames(final RevisionSearcher searcher) {
		entering("Registering fully specified names using revision searcher");
		checkState(fullySpecifiedNames == null, "Fully specified names should only be collected once");

		fullySpecifiedNames = PrimitiveMaps.newLongKeyOpenHashMapWithExpectedSize(conceptMap.size());
		
		final ExpressionBuilder whereExpressionBuilder = Expressions.builder()
				.filter(SnomedDescriptionIndexEntry.Expressions.active())
				.filter(SnomedDescriptionIndexEntry.Expressions.type(Concepts.FULLY_SPECIFIED_NAME));
		
		if (!excludedModuleIds.isEmpty()) {
			whereExpressionBuilder.mustNot(modules(excludedModuleIds));
		}
		
		final Query<String[]> query = Query.select(String[].class)
				.from(SnomedDescriptionIndexEntry.class)
				.fields(SnomedDescriptionIndexEntry.Fields.CONCEPT_ID, // 0
						SnomedDescriptionIndexEntry.Fields.TERM) // 1
				.where(whereExpressionBuilder.build())
				.limit(SCROLL_LIMIT)
				.build();
		
		final List<String> conceptIds = new ArrayList<>(SCROLL_LIMIT);
		final List<String> terms = new ArrayList<>(SCROLL_LIMIT);
		for (final Hits<String[]> hits : searcher.scroll(query)) {
			for (final String[] description : hits) {
				if (conceptMap.containsKey(description[0])) {
					conceptIds.add(description[0]);
					terms.add(description[1]);
				} else {
					LOGGER.debug("Not registering FSN as its concept {} is inactive.", description[0]);
				}
			}

			for (int i = 0; i < conceptIds.size(); i++) {
				fullySpecifiedNames.put(Long.parseLong(conceptIds.get(i)), terms.get(i));
			}
			
			conceptIds.clear();
			terms.clear();
		}
		
		leaving("Registering fully specified names using revision searcher");
		return this;
	}

	public ReasonerTaxonomyBuilder addActiveStatedEdges(final RevisionSearcher searcher) {
		entering("Registering active stated IS A graph edges using revision searcher");

		final ExpressionBuilder whereExpressionBuilder = Expressions.builder()
				.filter(active())
				.filter(typeId(Concepts.IS_A))
				.filter(characteristicTypeId(Concepts.STATED_RELATIONSHIP));
		
		if (!excludedModuleIds.isEmpty()) {
			whereExpressionBuilder.mustNot(modules(excludedModuleIds));
		}
		
		final Query<String[]> query = Query.select(String[].class)
				.from(SnomedRelationshipIndexEntry.class)
				.fields(SnomedRelationshipIndexEntry.Fields.SOURCE_ID, // 0
						SnomedRelationshipIndexEntry.Fields.DESTINATION_ID) // 1
				.where(whereExpressionBuilder.build())
				.limit(SCROLL_LIMIT)
				.build();

		final List<String> sourceIds = new ArrayList<>(SCROLL_LIMIT);
		final List<String> destinationIds = new ArrayList<>(SCROLL_LIMIT);
		for (final Hits<String[]> hits : searcher.scroll(query)) {
			for (final String[] relationship : hits) {
				if (conceptMap.containsKey(relationship[0]) && conceptMap.containsKey(relationship[1])) {
					sourceIds.add(relationship[0]);
					destinationIds.add(relationship[1]);
				} else {
					LOGGER.debug("Not registering IS A relationship as its source {} and/or destination {} is inactive.",
							relationship[0],
							relationship[1]);
				}
			}

			statedAncestors.addEdges(sourceIds, destinationIds);
			statedDescendants.addEdges(destinationIds, sourceIds);
			sourceIds.clear();
			destinationIds.clear();
		}

		leaving("Registering active stated IS A graph edges using revision searcher");
		return this;
	}

	public ReasonerTaxonomyBuilder addActiveStatedEdges(final Stream<SnomedRelationship> relationships) {
		entering("Registering active stated IS A graph edges from relationship stream");

		Stream<SnomedRelationship> filteredRelationships = relationships.filter(r -> r.isActive()
				&& !r.hasValue() // Not strictly needed for IS A-s, just to be sure
				&& Concepts.IS_A.equals(r.getTypeId())
				&& Concepts.STATED_RELATIONSHIP.equals(r.getCharacteristicTypeId())
				&& !excludedModuleIds.contains(r.getModuleId()));
		
		final List<String> sourceIds = new ArrayList<>(SCROLL_LIMIT);
		final List<String> destinationIds = new ArrayList<>(SCROLL_LIMIT);
		for (final Iterable<SnomedRelationship> chunk : Iterables.partition(filteredRelationships::iterator, SCROLL_LIMIT)) {
			for (final SnomedRelationship relationship : chunk) {
				if (conceptMap.containsKey(relationship.getSourceId()) && conceptMap.containsKey(relationship.getDestinationId())) {
					sourceIds.add(relationship.getSourceId());
					destinationIds.add(relationship.getDestinationId());
				} else {
					LOGGER.debug("Not registering IS A relationship {} as its source {} or destination {} is inactive.",
							relationship.getId(),
							relationship.getSourceId(),
							relationship.getDestinationId());
				}
			}

			statedAncestors.addEdges(sourceIds, destinationIds);
			statedDescendants.addEdges(destinationIds, sourceIds);
			sourceIds.clear();
			destinationIds.clear();
		}

		leaving("Registering active stated IS A graph edges from relationship stream");
		return this;
	}

	public ReasonerTaxonomyBuilder addConceptFlags(final RevisionSearcher searcher) {
		entering("Registering active concept flags (fully defined, exhaustive) using revision searcher");

		addConceptFlags(searcher, defining(), definingConcepts);
		addConceptFlags(searcher, exhaustive(), exhaustiveConcepts);

		leaving("Registering active concept flags (fully defined, exhaustive) using revision searcher");
		return this;
	}

	private void addConceptFlags(final RevisionSearcher searcher, final Expression expression, final InternalSctIdSet.Builder sctIdSet) {
		final ExpressionBuilder whereExpressionBuilder = Expressions.builder()
				.filter(active())
				.filter(expression); 
		
		if (!excludedModuleIds.isEmpty()) {
			whereExpressionBuilder.mustNot(modules(excludedModuleIds));
		}
		
		final Query<String[]> query = Query.select(String[].class)
				.from(SnomedConceptDocument.class)
				.fields(SnomedConceptDocument.Fields.ID)
				.where(whereExpressionBuilder.build())
				.limit(SCROLL_LIMIT)
				.build();

		/*
		 * XXX: For HashSets, Guava's factory method over-allocates by the expected
		 * amount so that the expected number of elements can be inserted without
		 * expanding the backing data structure.
		 */
		final Set<String> sctIds = newHashSetWithExpectedSize(SCROLL_LIMIT);
		for (final Hits<String[]> hits : searcher.scroll(query)) {
			for (final String[] concept : hits) {
				sctIds.add(concept[0]);
			}

			sctIdSet.addAll(sctIds);
			sctIds.clear();
		}
	}

	public ReasonerTaxonomyBuilder addConceptFlags(final Stream<SnomedConcept> concepts) {
		entering("Registering active concept flags (fully defined, exhaustive) using concept ID stream");

		Stream<SnomedConcept> filteredConcepts = concepts.filter(c -> {
			final boolean fullyDefined = !c.isPrimitive();
			final boolean exhaustive = SubclassDefinitionStatus.DISJOINT_SUBCLASSES.equals(c.getSubclassDefinitionStatus());
			
			return c.isActive() 
					&& (fullyDefined || exhaustive)
					&& !excludedModuleIds.contains(c.getModuleId());
		});

		final Set<String> definingIds = newHashSetWithExpectedSize(SCROLL_LIMIT);
		final Set<String> exhaustiveIds = newHashSetWithExpectedSize(SCROLL_LIMIT);
		for (final List<SnomedConcept> chunk : Iterables.partition(filteredConcepts::iterator, SCROLL_LIMIT)) {
			for (final SnomedConcept concept : chunk) {
				if (SubclassDefinitionStatus.DISJOINT_SUBCLASSES.equals(concept.getSubclassDefinitionStatus())) {
					exhaustiveIds.add(concept.getId()); 
				}
				
				if (!concept.isPrimitive()) {
					definingIds.add(concept.getId());
				}
			}

			definingConcepts.addAll(definingIds);
			exhaustiveConcepts.addAll(exhaustiveIds);
			definingIds.clear();
			exhaustiveIds.clear();
		}

		leaving("Registering active concept flags (fully definied, exhaustive) using concept ID stream");
		return this;
	}

	public ReasonerTaxonomyBuilder addActiveStatedRelationships(final RevisionSearcher searcher) {
		entering("Registering active stated relationships using revision searcher");

		final ExpressionBuilder whereExpressionBuilder = Expressions.builder()
				.filter(active())
				.filter(characteristicTypeId(Concepts.STATED_RELATIONSHIP));
		
		if (!excludedModuleIds.isEmpty()) {
			whereExpressionBuilder.mustNot(modules(excludedModuleIds));
		}
		
		addRelationships(searcher, whereExpressionBuilder, statedRelationships);

		leaving("Registering active stated relationships using revision searcher");
		return this;
	}

	public ReasonerTaxonomyBuilder addActiveStatedRelationships(final Stream<SnomedRelationship> sortedRelationships) {
		entering("Registering active stated relationships using relationship stream");
	
		Predicate<SnomedRelationship> predicate = relationship -> relationship.isActive() 
				&& Concepts.STATED_RELATIONSHIP.equals(relationship.getCharacteristicTypeId())
				&& !excludedModuleIds.contains(relationship.getModuleId());
		
		addRelationships(sortedRelationships.filter(predicate), statedRelationships);
	
		leaving("Registering active stated relationships using relationship stream");
		return this;
	}

	public ReasonerTaxonomyBuilder addActiveAdditionalGroupedRelationships(final RevisionSearcher searcher) {
		entering("Registering active additional grouped relationships using revision searcher");
	
		final ExpressionBuilder whereExpressionBuilder = Expressions.builder()
				.filter(active())
				.filter(group(1, Integer.MAX_VALUE))
				.filter(characteristicTypeId(Concepts.ADDITIONAL_RELATIONSHIP));
		
		if (!excludedModuleIds.isEmpty()) {
			whereExpressionBuilder.mustNot(modules(excludedModuleIds));
		}
		
		addRelationships(searcher, whereExpressionBuilder, additionalGroupedRelationships);
	
		leaving("Registering active additional grouped relationships using revision searcher");
		return this;
	}

	public ReasonerTaxonomyBuilder addActiveAdditionalGroupedRelationships(final Stream<SnomedRelationship> sortedRelationships) {
		entering("Registering active additional grouped relationships using relationship stream");
	
		Predicate<SnomedRelationship> predicate = relationship -> relationship.isActive() 
				&& Concepts.ADDITIONAL_RELATIONSHIP.equals(relationship.getCharacteristicTypeId())
				&& relationship.getGroup() > 0
				&& !excludedModuleIds.contains(relationship.getModuleId());
		
		addRelationships(sortedRelationships.filter(predicate), additionalGroupedRelationships);
	
		leaving("Registering active additional grouped relationships using relationship stream");
		return this;
	}

	public ReasonerTaxonomyBuilder addActiveInferredRelationships(final RevisionSearcher searcher) {
		entering("Registering active inferred relationships using revision searcher");
		
		final ExpressionBuilder whereExpressionBuilder = Expressions.builder()
				.filter(active())
				.filter(characteristicTypeId(Concepts.INFERRED_RELATIONSHIP));
		
		if (!excludedModuleIds.isEmpty()) {
			whereExpressionBuilder.mustNot(modules(excludedModuleIds));
		}
		
		addRelationships(searcher, whereExpressionBuilder, existingInferredRelationships);
				
		leaving("Registering active inferred relationships using revision searcher");
		return this;
	}

	public ReasonerTaxonomyBuilder addActiveInferredRelationships(final Stream<SnomedRelationship> sortedRelationships) {
		entering("Registering active inferred relationships using relationship stream");
		
		final Predicate<SnomedRelationship> predicate = relationship -> relationship.isActive() 
				&& Concepts.INFERRED_RELATIONSHIP.equals(relationship.getCharacteristicTypeId())
				&& !excludedModuleIds.contains(relationship.getModuleId());
		
		addRelationships(sortedRelationships.filter(predicate), existingInferredRelationships);
		
		leaving("Registering active inferred relationships using relationship stream");
		return this;
	}

	private static <T, U> U ifNotNull(final T value, final Function<T, U> mapper) {
		if (value != null) {
			return mapper.apply(value);
		} else {
			return null;
		}
	}

	private void addRelationships(final RevisionSearcher searcher, final ExpressionBuilder whereExpressionBuilder, final Builder<StatementFragment> fragmentBuilder) {
		
		final Query<String[]> query = Query.select(String[].class)
				.from(SnomedRelationshipIndexEntry.class)
				.fields(SnomedRelationshipIndexEntry.Fields.ID,                  // 0
						SnomedRelationshipIndexEntry.Fields.SOURCE_ID,           // 1
						SnomedRelationshipIndexEntry.Fields.TYPE_ID,             // 2
						SnomedRelationshipIndexEntry.Fields.DESTINATION_ID,      // 3 
						SnomedRelationshipIndexEntry.Fields.DESTINATION_NEGATED, // 4
						SnomedRelationshipIndexEntry.Fields.VALUE_TYPE,          // 5
						SnomedRelationshipIndexEntry.Fields.INTEGER_VALUE,       // 6
						SnomedRelationshipIndexEntry.Fields.DECIMAL_VALUE,       // 7
						SnomedRelationshipIndexEntry.Fields.STRING_VALUE,        // 8
						SnomedRelationshipIndexEntry.Fields.GROUP,               // 9
						SnomedRelationshipIndexEntry.Fields.UNION_GROUP,         // 10
						SnomedRelationshipIndexEntry.Fields.MODIFIER_ID,         // 11
						SnomedRelationshipIndexEntry.Fields.RELEASED)            // 12
				.where(whereExpressionBuilder.build())
				.sortBy(SortBy.builder()
						// XXX: Need to group relationships by source ID
						.sortByField(SnomedRelationshipIndexEntry.Fields.SOURCE_ID, Order.ASC) 
						.sortByField(SnomedRelationshipIndexEntry.Fields.ID, Order.ASC)
						.build())
				.limit(SCROLL_LIMIT)
				.build();
		
		final List<StatementFragment> fragments = new ArrayList<>(SCROLL_LIMIT);
		String lastSourceId = "";
		for (final Hits<String[]> hits : searcher.scroll(query)) {
			for (final String[] relationship : hits) {
				final String sourceId = relationship[1];
		
				if (lastSourceId.isEmpty()) {
					lastSourceId = sourceId;
				} else if (!lastSourceId.equals(sourceId)) {
					if (conceptMap.containsKey(lastSourceId)) {
						fragmentBuilder.putAll(lastSourceId, fragments);
					} else {
						LOGGER.debug("Not registering {} relationships for source concept {} as it is inactive.",
								fragments.size(),
								lastSourceId);
					}
					fragments.clear();
					lastSourceId = sourceId;
				}
				
				final long statementId = Long.parseLong(relationship[0]);
				// final String sourceId = relationship[1];
				final long typeId = Long.parseLong(relationship[2]);
				final Long destinationId = ifNotNull(relationship[3], Long::valueOf);
				final boolean destinationNegated = Boolean.parseBoolean(relationship[4]);
				final RelationshipValueType valueType = ifNotNull(relationship[5], RelationshipValueType::valueOf);
				final Integer integerValue = ifNotNull(relationship[6], Integer::valueOf);
				final Double decimalValue = ifNotNull(relationship[7], Double::valueOf);
				final String stringValue = relationship[8];
				final int group = Integer.parseInt(relationship[9]);
				final int unionGroup = Integer.parseInt(relationship[10]);
				final boolean universal = Concepts.UNIVERSAL_RESTRICTION_MODIFIER.equals(relationship[11]);
				final boolean released = Boolean.parseBoolean(relationship[12]);
				
				final StatementFragment statement;
				if (destinationId != null) {
					statement = new StatementFragmentWithDestination(
						typeId, group, unionGroup, universal, statementId, -1L, released, destinationId, destinationNegated);
				} else {
					final RelationshipValue value = RelationshipValue.fromTypeAndObjects(
						valueType, integerValue, decimalValue, stringValue);
					
					statement = new StatementFragmentWithValue(
						typeId, group, unionGroup, universal, statementId, -1L, released, value.toLiteral());
				}
				
				fragments.add(statement);
			}
		}
		
		if (!lastSourceId.isEmpty()) {
			if (conceptMap.containsKey(lastSourceId)) {
				fragmentBuilder.putAll(lastSourceId, fragments);
			} else {
				LOGGER.debug("Not registering {} relationships for source concept {} as it is inactive.",
						fragments.size(),
						lastSourceId);
			}
			fragments.clear();
		}
	}

	/*
	 * XXX: sortedRelationships should be sorted by source ID; we can not verify this in advance
	 */
	private void addRelationships(final Stream<SnomedRelationship> sortedRelationships, final Builder<StatementFragment> builder) {
		
		final List<StatementFragment> fragments = new ArrayList<>(SCROLL_LIMIT);
		String lastSourceId = "";

		for (final List<SnomedRelationship> chunk : Iterables.partition(sortedRelationships::iterator, SCROLL_LIMIT)) {
			for (final SnomedRelationship relationship : chunk) {
				final String sourceId = relationship.getSourceId();
				
				if (lastSourceId.isEmpty()) {
					lastSourceId = sourceId;
				} else if (!lastSourceId.equals(sourceId)) {
					if (conceptMap.containsKey(lastSourceId)) {
						builder.putAll(lastSourceId, fragments);
					} else {
						LOGGER.debug("Not registering {} relationships for source concept {} as it is inactive.",
								fragments.size(),
								lastSourceId);
					}
					fragments.clear();
					lastSourceId = sourceId;
				}

				final long statementId = Long.parseLong(relationship.getId());
				final long typeId = Long.parseLong(relationship.getTypeId());
				final Long destinationId = ifNotNull(relationship.getDestinationId(), Long::valueOf);
				final boolean destinationNegated = relationship.isDestinationNegated();
				final RelationshipValue value = relationship.getValueAsObject();
				final int group = relationship.getGroup();
				final int unionGroup = relationship.getUnionGroup();
				final boolean universal = Concepts.UNIVERSAL_RESTRICTION_MODIFIER.equals(relationship.getModifierId());

				final StatementFragment statement;
				if (destinationId != null) {
					statement = new StatementFragmentWithDestination(
						typeId, group, unionGroup, universal, statementId, -1L, false, destinationId, destinationNegated);
				} else {
					statement = new StatementFragmentWithValue(
						typeId, group, unionGroup, universal, statementId, -1L, false, value.toLiteral());
				}

				fragments.add(statement);
			}
		}

		if (!lastSourceId.isEmpty()) {
			if (conceptMap.containsKey(lastSourceId)) {
				builder.putAll(lastSourceId, fragments);
			} else {
				LOGGER.debug("Not registering {} relationships for source concept {} as it is inactive.",
						fragments.size(),
						lastSourceId);
			}
			fragments.clear();
		}
	}
	
	public ReasonerTaxonomyBuilder addActiveAxioms(final RevisionSearcher searcher) {
		entering("Registering active stated OWL axioms using revision searcher");

		final ExpressionBuilder whereExpressionBuilder = Expressions.builder()
				.filter(SnomedRefSetMemberIndexEntry.Expressions.active())
				.filter(SnomedRefSetMemberIndexEntry.Expressions.refSetTypes(Collections.singleton(SnomedRefSetType.OWL_AXIOM)));
		
		if (!excludedModuleIds.isEmpty()) {
			whereExpressionBuilder.mustNot(modules(excludedModuleIds));
		}
		
		final Query<SnomedRefSetMemberIndexEntry> query = Query.select(SnomedRefSetMemberIndexEntry.class)
				.from(SnomedRefSetMemberIndexEntry.class)
				.where(whereExpressionBuilder.build())
				.sortBy(SortBy.builder()
						.sortByField(SnomedRefSetMemberIndexEntry.Fields.REFERENCED_COMPONENT_ID, Order.ASC)
						.sortByField(SnomedRefSetMemberIndexEntry.Fields.ID, Order.ASC)
						.build())
				.limit(SCROLL_LIMIT)
				.build();
		
		// XXX: we can only guess the lower limit here (1 relationship for each OWL axiom)
		final List<StatementFragment> nonIsAFragments = new ArrayList<>(SCROLL_LIMIT);
		final List<String> axioms = new ArrayList<>(SCROLL_LIMIT);
		String lastReferencedComponentId = "";
		int groupOffset = AXIOM_GROUP_BASE;
		
		for (final Hits<SnomedRefSetMemberIndexEntry> hits : searcher.scroll(query)) {
			for (final SnomedRefSetMemberIndexEntry member : hits) {
				final String referencedComponentId = member.getReferencedComponentId();
				
				if (lastReferencedComponentId.isEmpty()) {
					lastReferencedComponentId = referencedComponentId;
				} else if (!lastReferencedComponentId.equals(referencedComponentId)) {
					if (conceptMap.containsKey(lastReferencedComponentId)) {
						axiomNonIsaRelationships.putAll(lastReferencedComponentId, nonIsAFragments);
						statedAxioms.putAll(lastReferencedComponentId, axioms);
					} else {
						LOGGER.debug("Not registering OWL axioms for concept {} as it is inactive.", lastReferencedComponentId);
					}
					nonIsAFragments.clear();
					axioms.clear();
					
					lastReferencedComponentId = referencedComponentId;
					groupOffset = AXIOM_GROUP_BASE;
				}
				
				if (!conceptMap.containsKey(referencedComponentId)) {
					LOGGER.debug(
						"Not registering OWL axiom member for concept {} as the source is inactive.",
						referencedComponentId);
					continue;
				}

				final String expression = member.getOwlExpression();
				final StringTokenizer tok = new StringTokenizer(expression.toLowerCase(Locale.ENGLISH), "(): ");

				// OWL axiom types that we are expecting here, only two of which requires special handling:
				// 
				// [ ] SubClassOf(...)
				// [ ] EquivalentClasses(...)
				// [+] SubObjectPropertyOf(ObjectPropertyChain(:246093002 :738774007) :246093002)
				// [+] TransitiveObjectProperty(:774081006)
				// [ ] ReflexiveObjectProperty(...)
				try {
					
					final String firstToken = tok.nextToken();
					if ("transitiveobjectproperty".equals(firstToken)) {
						long propertyId = Long.parseLong(tok.nextToken());
						propertyChains.add(new PropertyChain(propertyId, propertyId, propertyId));
					} else if ("subobjectpropertyof".equals(firstToken)) {
						String nextToken = tok.nextToken();
						if ("objectpropertychain".equals(nextToken)) {
							long sourceType = Long.parseLong(tok.nextToken());
							long destinationType = Long.parseLong(tok.nextToken());
							long inferredType = Long.parseLong(tok.nextToken());
							propertyChains.add(new PropertyChain(sourceType, destinationType, inferredType));
						}
					}
					
					// Always collect the OWL axiom, regardless of type
					axioms.add(expression);
					
				} catch (NoSuchElementException | NumberFormatException e) {
					// skip
				}
				
				if (!CompareUtils.isEmpty(member.getClassAxiomRelationships())) {
					for (SnomedOWLRelationshipDocument relationship : member.getClassAxiomRelationships()) {
						if (relationship.getGroup() >= AXIOM_GROUP_BASE) {
							throw new IllegalStateException("OWL member has too many groups");
						}
						
						if (relationship.hasValue()) {
							// Add relationship with value
							nonIsAFragments.add(relationship.toStatementFragment(groupOffset));
							continue;
						}
							
						if (!conceptMap.containsKey(relationship.getDestinationId())) {
							LOGGER.debug(
								"Not registering OWL axiom relationship for concept {} as destination concept {} is inactive.",
								referencedComponentId, relationship.getDestinationId());
							continue;

						}
							
						if (!relationship.getTypeId().equals(Concepts.IS_A)) {
							// Add non-IS_A relationships with destination
							nonIsAFragments.add(relationship.toStatementFragment(groupOffset));
							continue;
						}
					}
				}

				/*
				 * The next OWL member's group numbers will be shifted (it should not have group
				 * numbers greater than AXIOM_GROUP_BASE)
				 */
				groupOffset += AXIOM_GROUP_BASE;
			}
		}
		
		if (!lastReferencedComponentId.isEmpty()) {
			if (conceptMap.containsKey(lastReferencedComponentId)) {
				axiomNonIsaRelationships.putAll(lastReferencedComponentId, nonIsAFragments);
				statedAxioms.putAll(lastReferencedComponentId, axioms);
			} else {
				LOGGER.debug("Not registering OWL axioms for concept {} as it is inactive.", lastReferencedComponentId);
			}
			nonIsAFragments.clear();
			axioms.clear();
		}
		
		leaving("Registering active stated OWL axioms using revision searcher");
		return this;
	}
	
	public ReasonerTaxonomyBuilder addNeverGroupedTypeIds(final RevisionSearcher searcher) {
		entering("Registering 'never grouped' type IDs using revision searcher");
		
		final ExpressionBuilder whereExpressionBuilder = Expressions.builder()
				.filter(SnomedRefSetMemberIndexEntry.Expressions.active())
				.filter(SnomedRefSetMemberIndexEntry.Expressions.referenceSetId(Concepts.REFSET_MRCM_ATTRIBUTE_DOMAIN_INTERNATIONAL))
				.filter(SnomedRefSetMemberIndexEntry.Expressions.mrcmGrouped(false));
		
		if (!excludedModuleIds.isEmpty()) {
			whereExpressionBuilder.mustNot(modules(excludedModuleIds));
		}
		
		final Query<String> query = Query.select(String.class)
				.from(SnomedRefSetMemberIndexEntry.class)
				.fields(SnomedRefSetMemberIndexEntry.Fields.REFERENCED_COMPONENT_ID)
				.where(whereExpressionBuilder.build())
				.sortBy(SortBy.builder()
						.sortByField(SnomedRefSetMemberIndexEntry.Fields.REFERENCED_COMPONENT_ID, Order.ASC)
						.sortByField(SnomedRefSetMemberIndexEntry.Fields.ID, Order.ASC)
						.build())
				.limit(SCROLL_LIMIT)
				.build();
		
		final LongList fragments = PrimitiveLists.newLongArrayListWithExpectedSize(SCROLL_LIMIT);
		for (final Hits<String> hits : searcher.scroll(query)) {
			for (final String referencedComponentId : hits) {
				fragments.add(Long.parseLong(referencedComponentId));
			}
			
			neverGroupedTypeIds.addAll(fragments);
			fragments.clear();
		}
		
		leaving("Registering 'never grouped' type IDs using revision searcher");
		return this;
	}
	
	public ReasonerTaxonomyBuilder addActiveConcreteDomainMembers(final RevisionSearcher searcher) {
		entering("Registering active concrete domain members using revision searcher");

		final ExpressionBuilder whereExpressionBuilder = Expressions.builder()
				.filter(active())
				.filter(refSetTypes(Collections.singleton(SnomedRefSetType.CONCRETE_DATA_TYPE)))
				.filter(characteristicTypeIds(CD_CHARACTERISTIC_TYPE_IDS));
		
		if (!excludedModuleIds.isEmpty()) {
			whereExpressionBuilder.mustNot(modules(excludedModuleIds));
		}

		final Query<SnomedRefSetMemberIndexEntry> query = Query.select(SnomedRefSetMemberIndexEntry.class)
				.where(whereExpressionBuilder.build())
				.sortBy(SortBy.builder()
						.sortByField(SnomedRefSetMemberIndexEntry.Fields.REFERENCED_COMPONENT_ID, Order.ASC)
						.sortByField(SnomedRefSetMemberIndexEntry.Fields.ID, Order.ASC)
						.build())
				.limit(SCROLL_LIMIT)
				.build();

		final List<ConcreteDomainFragment> statedFragments = new ArrayList<>(SCROLL_LIMIT);
		final List<ConcreteDomainFragment> inferredFragments = new ArrayList<>(SCROLL_LIMIT);
		final List<ConcreteDomainFragment> additionalGroupedFragments = new ArrayList<>(SCROLL_LIMIT);
		String lastReferencedComponentId = "";

		for (final Hits<SnomedRefSetMemberIndexEntry> hits : searcher.scroll(query)) {
			for (final SnomedRefSetMemberIndexEntry member : hits) {
				final String referencedComponentId = member.getReferencedComponentId();

				if (lastReferencedComponentId.isEmpty()) {
					lastReferencedComponentId = referencedComponentId;
				} else if (!lastReferencedComponentId.equals(referencedComponentId)) {
					if (conceptMap.containsKey(lastReferencedComponentId)) {
						statedConcreteDomainMembers.putAll(lastReferencedComponentId, statedFragments);
						inferredConcreteDomainMembers.putAll(lastReferencedComponentId, inferredFragments);
						additionalGroupedConcreteDomainMembers.putAll(lastReferencedComponentId, additionalGroupedFragments);
					} else {
						LOGGER.debug("Not registering CD members for concept {} as it is inactive.", lastReferencedComponentId);
					}
					statedFragments.clear();
					inferredFragments.clear();
					additionalGroupedFragments.clear();
					lastReferencedComponentId = referencedComponentId;
				}

				final String memberId = member.getId();
				final long refsetId = Long.parseLong(member.getReferenceSetId());
				final String serializedValue = SnomedRefSetUtil.serializeValue(member.getDataType(), member.getValue());
				final Integer group = member.getRelationshipGroup();
				final long typeId = Long.parseLong(member.getTypeId());
				final boolean released = member.isReleased();

				final ConcreteDomainFragment fragment = new ConcreteDomainFragment(memberId, 
						refsetId,
						group,
						serializedValue,
						typeId,
						released);

				if (Concepts.STATED_RELATIONSHIP.equals(member.getCharacteristicTypeId())) {
					statedFragments.add(fragment);
				} else if (Concepts.ADDITIONAL_RELATIONSHIP.equals(member.getCharacteristicTypeId()) && member.getRelationshipGroup() > 0) {
					additionalGroupedFragments.add(fragment);
				} else if (Concepts.INFERRED_RELATIONSHIP.equals(member.getCharacteristicTypeId())) {
					inferredFragments.add(fragment);
				}
			}
		}

		if (!lastReferencedComponentId.isEmpty()) {
			if (conceptMap.containsKey(lastReferencedComponentId)) {
				statedConcreteDomainMembers.putAll(lastReferencedComponentId, statedFragments);
				inferredConcreteDomainMembers.putAll(lastReferencedComponentId, inferredFragments);
				additionalGroupedConcreteDomainMembers.putAll(lastReferencedComponentId, additionalGroupedFragments);
			} else {
				LOGGER.debug("Not registering CD members for concept {} as it is inactive.", lastReferencedComponentId);
			}
			statedFragments.clear();
			inferredFragments.clear();
			additionalGroupedFragments.clear();
		}

		leaving("Registering active concrete domain members using revision searcher");
		return this;
	}

	/*
	 * XXX: sortedMembers should be sorted by referenced component ID; we can not verify this in advance
	 */
	public ReasonerTaxonomyBuilder addActiveConcreteDomainMembers(final Stream<SnomedReferenceSetMember> sortedMembers) {
		entering("Registering active concrete domain members using stream");

		final List<ConcreteDomainFragment> statedFragments = new ArrayList<>(SCROLL_LIMIT);
		final List<ConcreteDomainFragment> inferredFragments = new ArrayList<>(SCROLL_LIMIT);
		final List<ConcreteDomainFragment> additionalGroupedFragments = new ArrayList<>(SCROLL_LIMIT);
		String lastReferencedComponentId = "";

		for (final List<SnomedReferenceSetMember> chunk : Iterables.partition(sortedMembers::iterator, SCROLL_LIMIT)) {
			for (final SnomedReferenceSetMember member : chunk) {
				final String characteristicTypeId = (String) member.getProperties().get(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID);

				if (member.isActive() 
						&& SnomedRefSetUtil.getConcreteDomainRefSetMap().containsValue(member.getReferenceSetId())
						&& CD_CHARACTERISTIC_TYPE_IDS.contains(characteristicTypeId)
						&& !excludedModuleIds.contains(member.getModuleId())) {

					final String referencedComponentId = member.getReferencedComponent().getId();

					if (lastReferencedComponentId.isEmpty()) {
						lastReferencedComponentId = referencedComponentId;
					} else if (!lastReferencedComponentId.equals(referencedComponentId)) {
						if (conceptMap.containsKey(lastReferencedComponentId)) {
							statedConcreteDomainMembers.putAll(lastReferencedComponentId, statedFragments);
							inferredConcreteDomainMembers.putAll(lastReferencedComponentId, inferredFragments);
							additionalGroupedConcreteDomainMembers.putAll(lastReferencedComponentId, additionalGroupedFragments);
						} else {
							LOGGER.debug("Not registering CD members for concept {} as it is inactive.", lastReferencedComponentId);
						}
						statedFragments.clear();
						inferredFragments.clear();
						additionalGroupedFragments.clear();
						lastReferencedComponentId = referencedComponentId;
					}

					final String memberId = member.getId();
					final long refsetId = Long.parseLong(member.getReferenceSetId());
					final String serializedValue = (String) member.getProperties().get(SnomedRf2Headers.FIELD_VALUE);
					final Integer group = (Integer) member.getProperties().get(SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP);
					final long typeId = Long.parseLong((String) member.getProperties().get(SnomedRf2Headers.FIELD_TYPE_ID));

					final ConcreteDomainFragment fragment = new ConcreteDomainFragment(memberId,
							refsetId,
							group,
							serializedValue,
							typeId,
							false); // XXX: "injected" CD members will not set this flag correctly, but they should only be used in equivalence checks

					if (Concepts.STATED_RELATIONSHIP.equals(characteristicTypeId)) {
						statedFragments.add(fragment);
					} else if (Concepts.ADDITIONAL_RELATIONSHIP.equals(characteristicTypeId) && group > 0) {
						additionalGroupedFragments.add(fragment);
					} else if (Concepts.INFERRED_RELATIONSHIP.equals(characteristicTypeId)) {
						inferredFragments.add(fragment);
					}
				}
			}
		}

		if (!lastReferencedComponentId.isEmpty()) {
			if (conceptMap.containsKey(lastReferencedComponentId)) {
				statedConcreteDomainMembers.putAll(lastReferencedComponentId, statedFragments);
				inferredConcreteDomainMembers.putAll(lastReferencedComponentId, inferredFragments);
				additionalGroupedConcreteDomainMembers.putAll(lastReferencedComponentId, additionalGroupedFragments);
			} else {
				LOGGER.debug("Not registering CD members for concept {} as it is inactive.", lastReferencedComponentId);
			}
			statedFragments.clear();
			inferredFragments.clear();
			additionalGroupedFragments.clear();
		}

		leaving("Registering active concrete domain members using stream");
		return this;
	}

	public ReasonerTaxonomy build() {
		checkState(conceptMap != null, "finishConcepts() method was not called on taxonomy builder.");
		
		return new ReasonerTaxonomy(
				conceptMap, 
				fullySpecifiedNames,
				
				statedAncestors.build(),
				statedDescendants.build(),
				
				definingConcepts.build(),
				exhaustiveConcepts.build(),
				
				statedRelationships.build(),
				axiomNonIsaRelationships.build(),
				existingInferredRelationships.build(),
				additionalGroupedRelationships.build(),
				
				statedAxioms.build(),
				LongCollections.unmodifiableSet(neverGroupedTypeIds),
				propertyChains.build(),
				
				statedConcreteDomainMembers.build(),
				inferredConcreteDomainMembers.build(),
				additionalGroupedConcreteDomainMembers.build(),
				
				null, 
				null,
				null,
				null);
	}
}
