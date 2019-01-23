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
import static com.google.common.collect.Lists.newArrayListWithExpectedSize;
import static com.google.common.collect.Sets.newHashSetWithExpectedSize;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.collections.PrimitiveLists;
import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongCollections;
import com.b2international.collections.longs.LongList;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.time.TimeUtil;
import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.query.SortBy;
import com.b2international.index.query.SortBy.Order;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.RelationshipModifier;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SubclassDefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.ConcreteDomainFragment;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.taxonomy.InternalIdMultimap.Builder;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Builds a snapshot of the ontology for reasoner input and normal form generation.
 * 
 * @since
 */
public final class ReasonerTaxonomyBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger("reasoner-taxonomy");

	private static final Set<String> INFERRED_RELATIONSHIP_CHARACTERISTIC_TYPE_IDS = ImmutableSet.of(
			Concepts.STATED_RELATIONSHIP, 
			Concepts.INFERRED_RELATIONSHIP);
	
	private static final Set<String> CD_CHARACTERISTIC_TYPE_IDS = ImmutableSet.of(
			Concepts.STATED_RELATIONSHIP, 
			Concepts.INFERRED_RELATIONSHIP);
	
	private static final int SCROLL_LIMIT = 50_000;

	private final Stopwatch stopwatch;
	private final Set<String> excludedModuleIds;
	private final InternalIdMap.Builder conceptMap;

	private InternalIdEdges.Builder statedAncestors;
	private InternalIdEdges.Builder statedDescendants;

	private InternalSctIdSet.Builder fullyDefinedConcepts;
	private InternalSctIdSet.Builder exhaustiveConcepts;

	private InternalIdMultimap.Builder<StatementFragment> statedNonIsARelationships;
	private InternalIdMultimap.Builder<StatementFragment> additionalGroupedRelationships;
	private InternalIdMultimap.Builder<StatementFragment> existingInferredRelationships;
	
	private InternalIdMultimap.Builder<String> statedAxioms;
	private final LongSet neverGroupedIds = PrimitiveSets.newLongOpenHashSetWithExpectedSize(4);
	
	private ImmutableMultimap.Builder<String, ConcreteDomainFragment> statedConcreteDomainMembers;
	private ImmutableMultimap.Builder<String, ConcreteDomainFragment> additionalGroupedConcreteDomainMembers;
	private ImmutableMultimap.Builder<String, ConcreteDomainFragment> inferredConcreteDomainMembers;

	private InternalIdMap builtConceptMap;

	public ReasonerTaxonomyBuilder() {
		this(ImmutableSet.<String>of());
	}
	
	public ReasonerTaxonomyBuilder(final Set<String> excludedModuleIds) {
		this.stopwatch = Stopwatch.createStarted();
		this.excludedModuleIds = ImmutableSet.copyOf(checkNotNull(excludedModuleIds, "excludedModuleIds"));
		
		// This is the only builder that can be initialized in the constructor
		this.conceptMap = InternalIdMap.builder();
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
		
		final Query<String> query = Query.select(String.class)
				.from(SnomedConceptDocument.class)
				.fields(SnomedConceptDocument.Fields.ID)
				.where(whereExpressionBuilder.build())
				.limit(SCROLL_LIMIT)
				.build();

		final Iterable<Hits<String>> scrolledHits = searcher.scroll(query);
		for (final Hits<String> hits : scrolledHits) {
			conceptMap.addAll(hits.getHits());
		}

		leaving("Registering active concept IDs using revision searcher");
		return this;
	}

	public ReasonerTaxonomyBuilder addActiveConceptIds(final Stream<SnomedConcept> concepts) {
		entering("Registering concept IDs from stream");

		Stream<SnomedConcept> filteredConcepts = concepts.filter(c -> c.isActive() 
				&& !excludedModuleIds.contains(c.getModuleId()));

		for (final Collection<SnomedConcept> chunk : Iterables.partition(filteredConcepts::iterator, SCROLL_LIMIT)) {
			final Collection<String> sctIds = Collections2.transform(chunk, SnomedConcept::getId);
			conceptMap.addAll(sctIds);
		}

		leaving("Registering concept IDs from stream");
		return this;
	}

	public ReasonerTaxonomyBuilder finishConcepts() {
		// First stage completed, now all other builders can use the map
		builtConceptMap = conceptMap.build();

		this.statedAncestors = InternalIdEdges.builder(builtConceptMap);
		this.statedDescendants = InternalIdEdges.builder(builtConceptMap);
		
		this.fullyDefinedConcepts = InternalSctIdSet.builder(builtConceptMap);
		this.exhaustiveConcepts = InternalSctIdSet.builder(builtConceptMap);
		
		this.statedNonIsARelationships = InternalIdMultimap.builder(builtConceptMap);
		this.additionalGroupedRelationships = InternalIdMultimap.builder(builtConceptMap);
		this.existingInferredRelationships = InternalIdMultimap.builder(builtConceptMap);
		
		this.statedAxioms = InternalIdMultimap.builder(builtConceptMap);
		
		// Concrete domain member builders are not backed by the internal map
		this.statedConcreteDomainMembers = ImmutableMultimap.builder();
		this.additionalGroupedConcreteDomainMembers = ImmutableMultimap.builder();
		this.inferredConcreteDomainMembers = ImmutableMultimap.builder();

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
				.fields(SnomedRelationshipIndexEntry.Fields.ID, // 0 (required)
						SnomedRelationshipIndexEntry.Fields.SOURCE_ID, // 1
						SnomedRelationshipIndexEntry.Fields.DESTINATION_ID) // 2
				.where(whereExpressionBuilder.build())
				.limit(SCROLL_LIMIT)
				.build();

		final Iterable<Hits<String[]>> scrolledHits = searcher.scroll(query);
		final List<String> sourceIds = newArrayListWithExpectedSize(SCROLL_LIMIT);
		final List<String> destinationIds = newArrayListWithExpectedSize(SCROLL_LIMIT);

		for (final Hits<String[]> hits : scrolledHits) {
			for (final String[] relationship : hits) {
				sourceIds.add(relationship[1]);
				destinationIds.add(relationship[2]);
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
				&& Concepts.IS_A.equals(r.getTypeId())
				&& CharacteristicType.STATED_RELATIONSHIP.equals(r.getCharacteristicType())
				&& !excludedModuleIds.contains(r.getModuleId()));
		
		final List<String> sourceIds = newArrayListWithExpectedSize(SCROLL_LIMIT);
		final List<String> destinationIds = newArrayListWithExpectedSize(SCROLL_LIMIT);

		for (final Iterable<SnomedRelationship> chunk : Iterables.partition(filteredRelationships::iterator, SCROLL_LIMIT)) {
			for (final SnomedRelationship relationship : chunk) {
				sourceIds.add(relationship.getSourceId());
				destinationIds.add(relationship.getDestinationId());
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

		final ExpressionBuilder whereExpressionBuilder = Expressions.builder()
				.filter(active())
				.should(defining())
				.should(exhaustive())
				.setMinimumNumberShouldMatch(1); // required because we also have a filter
		
		if (!excludedModuleIds.isEmpty()) {
			whereExpressionBuilder.mustNot(modules(excludedModuleIds));
		}
		
		final Query<String[]> query = Query.select(String[].class)
				.from(SnomedConceptDocument.class)
				.fields(SnomedConceptDocument.Fields.ID, // 0
						SnomedConceptDocument.Fields.PRIMITIVE, // 1
						SnomedConceptDocument.Fields.EXHAUSTIVE) // 2
				.where(whereExpressionBuilder.build())
				.limit(SCROLL_LIMIT)
				.build();

		final Iterable<Hits<String[]>> scrolledHits = searcher.scroll(query);
		final Set<String> fullyDefinedIds = newHashSetWithExpectedSize(SCROLL_LIMIT);
		final Set<String> exhaustiveIds = newHashSetWithExpectedSize(SCROLL_LIMIT);

		for (final Hits<String[]> hits : scrolledHits) {
			for (final String[] concept : hits) {
				if (!Boolean.parseBoolean(concept[1])) { fullyDefinedIds.add(concept[0]); }
				if (Boolean.parseBoolean(concept[2])) { exhaustiveIds.add(concept[0]); }
			}

			fullyDefinedConcepts.addAll(fullyDefinedIds);
			exhaustiveConcepts.addAll(exhaustiveIds);
			fullyDefinedIds.clear();
			exhaustiveIds.clear();
		}

		leaving("Registering active concept flags (fully defined, exhaustive) using revision searcher");
		return this;
	}

	public ReasonerTaxonomyBuilder addConceptFlags(final Stream<SnomedConcept> concepts) {
		entering("Registering active concept flags (exhaustive, fully defined) using concept ID stream");

		Stream<SnomedConcept> filteredConcepts = concepts.filter(c -> {
			final boolean fullyDefined = DefinitionStatus.FULLY_DEFINED.equals(c.getDefinitionStatus());
			final boolean exhaustive = SubclassDefinitionStatus.DISJOINT_SUBCLASSES.equals(c.getSubclassDefinitionStatus());
			return c.isActive() 
					&& (fullyDefined || exhaustive)
					&& !excludedModuleIds.contains(c.getModuleId());
		});

		final Set<String> fullyDefinedIds = newHashSetWithExpectedSize(SCROLL_LIMIT);
		final Set<String> exhaustiveIds = newHashSetWithExpectedSize(SCROLL_LIMIT);

		for (final List<SnomedConcept> chunk : Iterables.partition(filteredConcepts::iterator, SCROLL_LIMIT)) {
			for (final SnomedConcept concept : chunk) {
				if (DefinitionStatus.FULLY_DEFINED.equals(concept.getDefinitionStatus())) { fullyDefinedIds.add(concept.getId()); }
				if (SubclassDefinitionStatus.DISJOINT_SUBCLASSES.equals(concept.getSubclassDefinitionStatus())) { exhaustiveIds.add(concept.getId()); }
			}

			fullyDefinedConcepts.addAll(fullyDefinedIds);
			exhaustiveConcepts.addAll(exhaustiveIds);
			fullyDefinedIds.clear();
			exhaustiveIds.clear();
		}

		leaving("Registering active concept flags (exhaustive, fully definied) using concept ID stream");
		return this;
	}

	public ReasonerTaxonomyBuilder addActiveStatedNonIsARelationships(final RevisionSearcher searcher) {
		entering("Registering active stated non-IS A relationships using revision searcher");

		final ExpressionBuilder whereExpressionBuilder = Expressions.builder()
				.filter(active())
				.filter(characteristicTypeId(Concepts.STATED_RELATIONSHIP))
				.mustNot(typeId(Concepts.IS_A));
		
		if (!excludedModuleIds.isEmpty()) {
			whereExpressionBuilder.mustNot(modules(excludedModuleIds));
		}
		
		addRelationships(searcher, whereExpressionBuilder, statedNonIsARelationships);

		leaving("Registering active stated non-IS A relationships using revision searcher");
		return this;
	}

	public ReasonerTaxonomyBuilder addActiveStatedNonIsARelationships(final Stream<SnomedRelationship> sortedRelationships) {
		entering("Registering active stated non-IS A relationships using relationship stream");
	
		Predicate<SnomedRelationship> predicate = relationship -> relationship.isActive() 
				&& CharacteristicType.STATED_RELATIONSHIP.equals(relationship.getCharacteristicType())
				&& !Concepts.IS_A.equals(relationship.getTypeId())
				&& !excludedModuleIds.contains(relationship.getModuleId());
		
		addRelationships(sortedRelationships, predicate, statedNonIsARelationships::putAll);
	
		leaving("Registering active stated non-IS A relationships using relationship stream");
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
				&& CharacteristicType.ADDITIONAL_RELATIONSHIP.equals(relationship.getCharacteristicType())
				&& relationship.getGroup() > 0
				&& !excludedModuleIds.contains(relationship.getModuleId());
		
		addRelationships(sortedRelationships, predicate, additionalGroupedRelationships::putAll);
	
		leaving("Registering active additional grouped relationships using relationship stream");
		return this;
	}

	public ReasonerTaxonomyBuilder addActiveInferredRelationships(final RevisionSearcher searcher) {
		entering("Registering active inferred relationships using revision searcher");
		
		// Fetch both stated and inferred relationships to see if a stated pair exists
		final ExpressionBuilder whereExpressionBuilder = Expressions.builder()
				.filter(active())
				.filter(characteristicTypeIds(INFERRED_RELATIONSHIP_CHARACTERISTIC_TYPE_IDS));
		
		if (!excludedModuleIds.isEmpty()) {
			whereExpressionBuilder.mustNot(modules(excludedModuleIds));
		}
		
		final Query<String[]> query = Query.select(String[].class)
				.from(SnomedRelationshipIndexEntry.class)
				.fields(SnomedRelationshipIndexEntry.Fields.ID, // 0
						SnomedRelationshipIndexEntry.Fields.SOURCE_ID, // 1
						SnomedRelationshipIndexEntry.Fields.TYPE_ID, // 2
						SnomedRelationshipIndexEntry.Fields.DESTINATION_ID, // 3 
						SnomedRelationshipIndexEntry.Fields.DESTINATION_NEGATED, // 4
						SnomedRelationshipIndexEntry.Fields.GROUP, // 5
						SnomedRelationshipIndexEntry.Fields.UNION_GROUP, // 6
						SnomedRelationshipIndexEntry.Fields.MODIFIER_ID, // 7
						SnomedRelationshipIndexEntry.Fields.CHARACTERISTIC_TYPE_ID) // 8
				.where(whereExpressionBuilder.build())
				.sortBy(SortBy.builder()
						.sortByField(SnomedRelationshipIndexEntry.Fields.SOURCE_ID, Order.ASC)
						.sortByField(SnomedRelationshipIndexEntry.Fields.GROUP, Order.ASC)
						.sortByField(SnomedRelationshipIndexEntry.Fields.UNION_GROUP, Order.ASC)
						.sortByField(SnomedRelationshipIndexEntry.Fields.TYPE_ID, Order.ASC)
						.sortByField(SnomedRelationshipIndexEntry.Fields.DESTINATION_ID, Order.ASC)
						.sortByField(SnomedRelationshipIndexEntry.Fields.MODIFIER_ID, Order.ASC)
						.sortByField(SnomedRelationshipIndexEntry.Fields.CHARACTERISTIC_TYPE_ID, Order.ASC)
						.sortByField(SnomedRelationshipIndexEntry.Fields.ID, Order.ASC)
						.build())
				.limit(SCROLL_LIMIT)
				.build();
		
		final Iterable<Hits<String[]>> scrolledHits = searcher.scroll(query);
		final List<StatementFragment> fragments = newArrayListWithExpectedSize(SCROLL_LIMIT);
		String lastSourceId = "";
		String[] lastStatedRelationship = null;
		
		for (final Hits<String[]> hits : scrolledHits) {
			for (final String[] relationship : hits) {
				
				// Store stated relationship; its inferred pair should come immediately afterwards due to sorting
				if (Concepts.STATED_RELATIONSHIP.equals(relationship[8])) {
					lastStatedRelationship = relationship;
					continue;
				}
				
				final String sourceId = relationship[1];
		
				if (lastSourceId.isEmpty()) {
					lastSourceId = sourceId;
				} else if (!lastSourceId.equals(sourceId)) {
					existingInferredRelationships.putAll(lastSourceId, fragments);
					fragments.clear();
					lastSourceId = sourceId;
				}
		
				final long statementId = Long.parseLong(relationship[0]);
				final long typeId = Long.parseLong(relationship[2]);
				final long destinationId = Long.parseLong(relationship[3]);
				final boolean destinationNegated = Boolean.parseBoolean(relationship[4]);
				final int group = Integer.parseInt(relationship[5]);
				final int unionGroup = Integer.parseInt(relationship[6]);
				final boolean universal = Concepts.UNIVERSAL_RESTRICTION_MODIFIER.equals(relationship[7]);
				final boolean hasStatedPair = lastStatedRelationship != null
						&& lastStatedRelationship[1].equals(relationship[1]) // source
						&& lastStatedRelationship[2].equals(relationship[2]) // type
						&& lastStatedRelationship[3].equals(relationship[3]) // destination
						&& lastStatedRelationship[5].equals(relationship[5]); // group
				
				final StatementFragment statement = new StatementFragment(
						typeId,
						destinationId,
						destinationNegated,
						group,
						unionGroup,
						universal,
						statementId,
						hasStatedPair);
		
				fragments.add(statement);
			}
		}
		
		if (!lastSourceId.isEmpty()) {
			existingInferredRelationships.putAll(lastSourceId, fragments);
			fragments.clear();
		}
				
		leaving("Registering active inferred relationships using revision searcher");
		return this;
	}

	public ReasonerTaxonomyBuilder addActiveInferredRelationships(final Stream<SnomedRelationship> sortedRelationships) {
		entering("Registering active inferred relationships using relationship stream");
		
		final Predicate<SnomedRelationship> predicate = relationship -> relationship.isActive() 
				&& CharacteristicType.INFERRED_RELATIONSHIP.equals(relationship.getCharacteristicType())
				&& !excludedModuleIds.contains(relationship.getModuleId());
		
		addRelationships(sortedRelationships, predicate, existingInferredRelationships::putAll);
		
		leaving("Registering active inferred relationships using relationship stream");
		return this;
	}

	private void addRelationships(final RevisionSearcher searcher, final ExpressionBuilder whereExpressionBuilder, final Builder<StatementFragment> fragmentBuilder) {
		
		final Query<String[]> query = Query.select(String[].class)
				.from(SnomedRelationshipIndexEntry.class)
				.fields(SnomedRelationshipIndexEntry.Fields.ID, // 0
						SnomedRelationshipIndexEntry.Fields.SOURCE_ID, // 1
						SnomedRelationshipIndexEntry.Fields.TYPE_ID, // 2
						SnomedRelationshipIndexEntry.Fields.DESTINATION_ID, // 3 
						SnomedRelationshipIndexEntry.Fields.DESTINATION_NEGATED, // 4
						SnomedRelationshipIndexEntry.Fields.GROUP, // 5
						SnomedRelationshipIndexEntry.Fields.UNION_GROUP, // 6
						SnomedRelationshipIndexEntry.Fields.MODIFIER_ID) // 7
				.where(whereExpressionBuilder.build())
				.sortBy(SortBy.builder()
						.sortByField(SnomedRelationshipIndexEntry.Fields.SOURCE_ID, Order.ASC)
						.sortByField(SnomedRelationshipIndexEntry.Fields.GROUP, Order.ASC)
						.sortByField(SnomedRelationshipIndexEntry.Fields.UNION_GROUP, Order.ASC)
						.sortByField(SnomedRelationshipIndexEntry.Fields.TYPE_ID, Order.ASC)
						.sortByField(SnomedRelationshipIndexEntry.Fields.DESTINATION_ID, Order.ASC)
						.sortByField(SnomedRelationshipIndexEntry.Fields.MODIFIER_ID, Order.ASC)
						.sortByField(SnomedRelationshipIndexEntry.Fields.ID, Order.ASC)
						.build())
				.limit(SCROLL_LIMIT)
				.build();
		
		final Iterable<Hits<String[]>> scrolledHits = searcher.scroll(query);
		final List<StatementFragment> fragments = newArrayListWithExpectedSize(SCROLL_LIMIT);
		String lastSourceId = "";
		
		for (final Hits<String[]> hits : scrolledHits) {
			for (final String[] relationship : hits) {
				final String sourceId = relationship[1];
		
				if (lastSourceId.isEmpty()) {
					lastSourceId = sourceId;
				} else if (!lastSourceId.equals(sourceId)) {
					fragmentBuilder.putAll(lastSourceId, fragments);
					fragments.clear();
					lastSourceId = sourceId;
				}
				
				final long statementId = Long.parseLong(relationship[0]);
				final long typeId = Long.parseLong(relationship[2]);
				final long destinationId = Long.parseLong(relationship[3]);
				final boolean destinationNegated = Boolean.parseBoolean(relationship[4]);
				final int group = Integer.parseInt(relationship[5]);
				final int unionGroup = Integer.parseInt(relationship[6]);
				final boolean universal = Concepts.UNIVERSAL_RESTRICTION_MODIFIER.equals(relationship[7]);
		
				final StatementFragment statement = new StatementFragment(typeId,
						destinationId,
						destinationNegated,
						group,
						unionGroup,
						universal,
						statementId,
						false); // Relationships added through this method have no stated pair
				
				fragments.add(statement);
			}
		}
		
		if (!lastSourceId.isEmpty()) {
			fragmentBuilder.putAll(lastSourceId, fragments);
			fragments.clear();
		}
	}

	/*
	 * XXX: sortedRelationships should be sorted by source ID; we can not verify this in advance
	 */
	private void addRelationships(final Stream<SnomedRelationship> sortedRelationships,
			final Predicate<SnomedRelationship> predicate,
			final BiConsumer<String, List<StatementFragment>> consumer) {
		
		final List<StatementFragment> fragments = newArrayListWithExpectedSize(SCROLL_LIMIT);
		String lastSourceId = "";

		for (final List<SnomedRelationship> chunk : Iterables.partition(sortedRelationships::iterator, SCROLL_LIMIT)) {
			for (final SnomedRelationship relationship : chunk) {
				if (predicate.test(relationship)) {
					final String sourceId = relationship.getSourceId();
					
					if (lastSourceId.isEmpty()) {
						lastSourceId = sourceId;
					} else if (!lastSourceId.equals(relationship.getSourceId())) {
						consumer.accept(lastSourceId, fragments);
						fragments.clear();
					}

					final long statementId = Long.parseLong(relationship.getId());
					final long typeId = Long.parseLong(relationship.getTypeId());
					final long destinationId = Long.parseLong(relationship.getDestinationId());
					final boolean destinationNegated = relationship.isDestinationNegated();
					final int group = relationship.getGroup();
					final int unionGroup = relationship.getUnionGroup();
					final boolean universal = RelationshipModifier.UNIVERSAL.equals(relationship.getModifier());

					final StatementFragment statement = new StatementFragment(
							typeId,
							destinationId,
							destinationNegated,
							group,
							unionGroup,
							universal,
							statementId,
							false); // XXX: "injected" concepts will not set the flag correctly, but they are
									// usually only added for equivalence checks

					fragments.add(statement);
				}
			}
		}

		if (!lastSourceId.isEmpty()) {
			consumer.accept(lastSourceId, fragments);
			fragments.clear();
		}
	}
	
	public ReasonerTaxonomyBuilder addActiveAxioms(final RevisionSearcher searcher) {
		entering("Registering active stated OWL axioms using revision searcher");

		final ExpressionBuilder whereExpressionBuilder = Expressions.builder()
				.filter(SnomedRefSetMemberIndexEntry.Expressions.active())
				.filter(SnomedRefSetMemberIndexEntry.Expressions.referenceSetId(Concepts.REFSET_OWL_AXIOM));
		
		if (!excludedModuleIds.isEmpty()) {
			whereExpressionBuilder.mustNot(modules(excludedModuleIds));
		}
		
		final Query<String[]> query = Query.select(String[].class)
				.from(SnomedRefSetMemberIndexEntry.class)
				.fields(SnomedRefSetMemberIndexEntry.Fields.ID, // 0
						SnomedRefSetMemberIndexEntry.Fields.REFERENCED_COMPONENT_ID, // 1
						SnomedRefSetMemberIndexEntry.Fields.OWL_EXPRESSION) // 2
				.where(whereExpressionBuilder.build())
				.sortBy(SortBy.builder()
						.sortByField(SnomedRefSetMemberIndexEntry.Fields.REFERENCED_COMPONENT_ID, Order.ASC)
						.sortByField(SnomedRefSetMemberIndexEntry.Fields.ID, Order.ASC)
						.build())
				.limit(SCROLL_LIMIT)
				.build();
		
		final Iterable<Hits<String[]>> scrolledHits = searcher.scroll(query);
		final List<String> fragments = newArrayListWithExpectedSize(SCROLL_LIMIT);
		String lastReferencedComponentId = "";
		
		for (final Hits<String[]> hits : scrolledHits) {
			for (final String[] member : hits) {
				final String referencedComponentId = member[1];
				final String expression = member[2];
				
				if (lastReferencedComponentId.isEmpty()) {
					lastReferencedComponentId = referencedComponentId;
				} else if (!lastReferencedComponentId.equals(referencedComponentId)) {
					statedAxioms.putAll(lastReferencedComponentId, fragments);
					fragments.clear();
					lastReferencedComponentId = referencedComponentId;
				}
				
				fragments.add(expression);
			}
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
		
		final Query<String[]> query = Query.select(String[].class)
				.from(SnomedRefSetMemberIndexEntry.class)
				.fields(SnomedRefSetMemberIndexEntry.Fields.ID, // 0
						SnomedRefSetMemberIndexEntry.Fields.REFERENCED_COMPONENT_ID) // 1
				.where(whereExpressionBuilder.build())
				.sortBy(SortBy.builder()
						.sortByField(SnomedRefSetMemberIndexEntry.Fields.REFERENCED_COMPONENT_ID, Order.ASC)
						.sortByField(SnomedRefSetMemberIndexEntry.Fields.ID, Order.ASC)
						.build())
				.limit(SCROLL_LIMIT)
				.build();
		
		final Iterable<Hits<String[]>> scrolledHits = searcher.scroll(query);
		final LongList fragments = PrimitiveLists.newLongArrayListWithExpectedSize(SCROLL_LIMIT);
		
		for (final Hits<String[]> hits : scrolledHits) {
			for (final String[] member : hits) {
				final String referencedComponentId = member[1];
				fragments.add(Long.parseLong(referencedComponentId));
			}
			
			neverGroupedIds.addAll(fragments);
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
						.sortByField(SnomedRefSetMemberIndexEntry.Fields.TYPE_ID, Order.ASC)
						// not sorting by value
						.sortByField(SnomedRefSetMemberIndexEntry.Fields.RELATIONSHIP_GROUP, Order.ASC)
						.sortByField(SnomedRefSetMemberIndexEntry.Fields.ID, Order.ASC)
						.build())
				.limit(SCROLL_LIMIT)
				.build();

		final Iterable<Hits<SnomedRefSetMemberIndexEntry>> scrolledHits = searcher.scroll(query);

		final List<ConcreteDomainFragment> statedFragments = newArrayListWithExpectedSize(SCROLL_LIMIT);
		final List<ConcreteDomainFragment> inferredFragments = newArrayListWithExpectedSize(SCROLL_LIMIT);
		final List<ConcreteDomainFragment> additionalGroupedFragments = newArrayListWithExpectedSize(SCROLL_LIMIT);
		final String lastReferencedComponentId = "";

		for (final Hits<SnomedRefSetMemberIndexEntry> hits : scrolledHits) {
			for (final SnomedRefSetMemberIndexEntry member : hits) {
				final String referencedComponentId = member.getReferencedComponentId();

				if (!lastReferencedComponentId.equals(referencedComponentId)) {
					statedConcreteDomainMembers.putAll(lastReferencedComponentId, statedFragments);
					inferredConcreteDomainMembers.putAll(lastReferencedComponentId, inferredFragments);
					additionalGroupedConcreteDomainMembers.putAll(lastReferencedComponentId, additionalGroupedFragments);
					statedFragments.clear();
					inferredFragments.clear();
					additionalGroupedFragments.clear();
				}

				final String memberId = member.getId();
				final long refsetId = Long.parseLong(member.getReferenceSetId());
				final String serializedValue = SnomedRefSetUtil.serializeValue(member.getDataType(), member.getValue());
				final Integer group = member.getRelationshipGroup();
				final long typeId = Long.parseLong(member.getTypeId());

				final ConcreteDomainFragment fragment = new ConcreteDomainFragment(memberId, 
						refsetId,
						group,
						serializedValue,
						typeId);

				if (Concepts.STATED_RELATIONSHIP.equals(member.getCharacteristicTypeId())) {
					statedFragments.add(fragment);
				} else if (Concepts.ADDITIONAL_RELATIONSHIP.equals(member.getCharacteristicTypeId()) && member.getRelationshipGroup() > 0) {
					additionalGroupedFragments.add(fragment);
				} else {
					inferredFragments.add(fragment);
				}
			}
		}

		if (!lastReferencedComponentId.isEmpty()) {
			statedConcreteDomainMembers.putAll(lastReferencedComponentId, statedFragments);
			inferredConcreteDomainMembers.putAll(lastReferencedComponentId, inferredFragments);
			additionalGroupedConcreteDomainMembers.putAll(lastReferencedComponentId, additionalGroupedFragments);
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

		final List<ConcreteDomainFragment> statedFragments = newArrayListWithExpectedSize(SCROLL_LIMIT);
		final List<ConcreteDomainFragment> inferredFragments = newArrayListWithExpectedSize(SCROLL_LIMIT);
		final List<ConcreteDomainFragment> additionalGroupedFragments = newArrayListWithExpectedSize(SCROLL_LIMIT);
		final String lastReferencedComponentId = "";

		for (final List<SnomedReferenceSetMember> chunk : Iterables.partition(sortedMembers::iterator, SCROLL_LIMIT)) {
			for (final SnomedReferenceSetMember member : chunk) {
				final String characteristicTypeId = (String) member.getProperties().get(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID);

				if (member.isActive() 
						&& SnomedRefSetUtil.getConcreteDomainRefSetMap().containsValue(member.getReferenceSetId())
						&& CD_CHARACTERISTIC_TYPE_IDS.contains(characteristicTypeId)
						&& !excludedModuleIds.contains(member.getModuleId())) {

					final String referencedComponentId = member.getReferencedComponent().getId();

					if (!lastReferencedComponentId.equals(referencedComponentId)) {
						statedConcreteDomainMembers.putAll(lastReferencedComponentId, statedFragments);
						inferredConcreteDomainMembers.putAll(lastReferencedComponentId, inferredFragments);
						additionalGroupedConcreteDomainMembers.putAll(lastReferencedComponentId, additionalGroupedFragments);
						statedFragments.clear();
						inferredFragments.clear();
						additionalGroupedFragments.clear();
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
							typeId);

					if (Concepts.STATED_RELATIONSHIP.equals(characteristicTypeId)) {
						statedFragments.add(fragment);
					} else if (Concepts.ADDITIONAL_RELATIONSHIP.equals(characteristicTypeId) && group > 0) {
						additionalGroupedFragments.add(fragment);
					} else {
						inferredFragments.add(fragment);
					}
				}
			}
		}

		if (!lastReferencedComponentId.isEmpty()) {
			statedConcreteDomainMembers.putAll(lastReferencedComponentId, statedFragments);
			inferredConcreteDomainMembers.putAll(lastReferencedComponentId, inferredFragments);
			additionalGroupedConcreteDomainMembers.putAll(lastReferencedComponentId, additionalGroupedFragments);
			statedFragments.clear();
			inferredFragments.clear();
			additionalGroupedFragments.clear();
		}

		leaving("Registering active concrete domain members using stream");
		return this;
	}

	public ReasonerTaxonomy build() {
		checkState(builtConceptMap != null, "finishConcepts() method was not called on taxonomy builder.");

		return new ReasonerTaxonomy(builtConceptMap,
				statedAncestors.build(),
				statedDescendants.build(),
				
				fullyDefinedConcepts.build(),
				exhaustiveConcepts.build(),
				
				statedNonIsARelationships.build(),
				existingInferredRelationships.build(),
				additionalGroupedRelationships.build(),
				
				statedAxioms.build(),
				LongCollections.unmodifiableSet(neverGroupedIds),
				
				statedConcreteDomainMembers.build(),
				additionalGroupedConcreteDomainMembers.build(),
				inferredConcreteDomainMembers.build(),
				
				null, 
				null,
				null,
				null);
	}
}
