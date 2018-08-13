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
package com.b2international.snowowl.snomed.core.taxonomy;

import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.defining;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.exhaustive;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument.Expressions.active;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument.Expressions.modules;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry.Expressions.refSetTypes;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry.Expressions.characteristicTypeId;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry.Expressions.characteristicTypeIds;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry.Expressions.typeId;
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

import com.b2international.commons.time.TimeUtil;
import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.query.SortBy;
import com.b2international.index.query.SortBy.Order;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.RelationshipModifier;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.ConcreteDomainFragment;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
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

	// Long versions of concept SCTIDs
	private static final long DEFAULT_UNIT = Long.parseLong(Concepts.DEFAULT_UNIT);
	private static final Set<String> CHARACTERISTIC_TYPE_IDS = ImmutableSet.of(Concepts.STATED_RELATIONSHIP, Concepts.INFERRED_RELATIONSHIP);
	private static final int SCROLL_LIMIT = 50_000;

	private final Stopwatch stopwatch;
	private final InternalIdMap.Builder conceptMap;

	private InternalIdEdges.Builder statedAncestors;
	private InternalIdEdges.Builder statedDescendants;

	private InternalSctIdSet.Builder fullyDefinedConcepts;
	private InternalSctIdSet.Builder exhaustiveConcepts;

	private InternalIdMultimap.Builder<StatementFragment> statedNonIsARelationships;
	private InternalIdMultimap.Builder<StatementFragment> existingInferredRelationships;
	private ImmutableMultimap.Builder<String, ConcreteDomainFragment> statedConcreteDomainMembers;
	private ImmutableMultimap.Builder<String, ConcreteDomainFragment> inferredConcreteDomainMembers;

	private InternalIdMap builtConceptMap;

	public ReasonerTaxonomyBuilder() {
		this.stopwatch = Stopwatch.createStarted();
		// As all other builders rely on this map, it is the only one that can be initialized here
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

		final Query<String> query = Query.select(String.class)
				.from(SnomedConceptDocument.class)
				.fields(SnomedConceptDocument.Fields.ID)
				.where(Expressions.builder()
						.filter(active())
						.mustNot(modules(Concepts.UK_MODULES_NOCLASSIFY))
						.build())
				.limit(SCROLL_LIMIT)
				.build();

		final Iterable<Hits<String>> scrolledHits = searcher.scroll(query);
		for (final Hits<String> hits : scrolledHits) {
			conceptMap.addAll(hits.getHits());
		}

		leaving("Registering active concept IDs using revision searcher");
		return this;
	}

	public ReasonerTaxonomyBuilder addActiveConceptIds(final Stream<String> activeConceptIds) {
		entering("Registering concept IDs from stream");

		for (final Collection<String> chunk : Iterables.partition(activeConceptIds::iterator, SCROLL_LIMIT)) {
			conceptMap.addAll(chunk);
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
		this.existingInferredRelationships = InternalIdMultimap.builder(builtConceptMap);
		
		// Concrete domain member builders are not backed by the internal map
		this.statedConcreteDomainMembers = ImmutableMultimap.builder();
		this.inferredConcreteDomainMembers = ImmutableMultimap.builder();

		return this;
	}

	public ReasonerTaxonomyBuilder addActiveStatedEdges(final RevisionSearcher searcher) {
		entering("Registering active IS A graph edges using revision searcher");

		final Query<String[]> query = Query.select(String[].class)
				.from(SnomedRelationshipIndexEntry.class)
				.fields(SnomedRelationshipIndexEntry.Fields.ID, // 0 (required)
						SnomedRelationshipIndexEntry.Fields.SOURCE_ID, // 1
						SnomedRelationshipIndexEntry.Fields.DESTINATION_ID) // 2
				.where(Expressions.builder()
						.filter(active())
						.filter(typeId(Concepts.IS_A))
						.filter(characteristicTypeId(Concepts.STATED_RELATIONSHIP))
						.mustNot(modules(Concepts.UK_MODULES_NOCLASSIFY))
						.build())
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

		leaving("Registering active IS A graph edges using revision searcher");
		return this;
	}

	public ReasonerTaxonomyBuilder addActiveStatedEdges(final Stream<SnomedRelationship> relationships) {
		entering("Registering active IS A graph edges from relationship stream");

		final List<String> sourceIds = newArrayListWithExpectedSize(SCROLL_LIMIT);
		final List<String> destinationIds = newArrayListWithExpectedSize(SCROLL_LIMIT);

		for (final Iterable<SnomedRelationship> chunk : Iterables.partition(relationships::iterator, SCROLL_LIMIT)) {
			for (final SnomedRelationship relationship : chunk) {
				if (relationship.isActive() 
						&& Concepts.IS_A.equals(relationship.getTypeId())
						&& CharacteristicType.STATED_RELATIONSHIP.equals(relationship.getCharacteristicType())
						&& !Concepts.UK_MODULES_NOCLASSIFY.contains(relationship.getModuleId())) {

					sourceIds.add(relationship.getSourceId());
					destinationIds.add(relationship.getDestinationId());
				}
			}

			statedAncestors.addEdges(sourceIds, destinationIds);
			statedDescendants.addEdges(destinationIds, sourceIds);
			sourceIds.clear();
			destinationIds.clear();
		}

		leaving("Registering active IS A graph edges from relationship stream");
		return this;
	}

	public ReasonerTaxonomyBuilder addConceptFlags(final RevisionSearcher searcher) {
		entering("Registering active concept flags (fully defined, exhaustive) using revision searcher");

		final Query<String[]> query = Query.select(String[].class)
				.from(SnomedConceptDocument.class)
				.fields(SnomedConceptDocument.Fields.ID, // 0
						SnomedConceptDocument.Fields.PRIMITIVE, // 1
						SnomedConceptDocument.Fields.EXHAUSTIVE) // 2
				.where(Expressions.builder()
						.filter(active())
						.should(defining())
						.should(exhaustive())
						.mustNot(modules(Concepts.UK_MODULES_NOCLASSIFY))
						.setMinimumNumberShouldMatch(1) // required because we also have a filter
						.build())
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

	public ReasonerTaxonomyBuilder addConceptFlags(final Stream<String> fullyDefinedConceptIds) {
		entering("Registering active concept flags (fully defined) using concept ID stream");

		for (final List<String> chunk : Iterables.partition(fullyDefinedConceptIds::iterator, SCROLL_LIMIT)) {
			fullyDefinedConcepts.addAll(chunk);
		}

		leaving("Registering active concept flags (fully definied) using concept ID stream");
		return this;
	}

	public ReasonerTaxonomyBuilder addActiveStatedNonIsARelationships(final RevisionSearcher searcher) {
		entering("Registering active stated non-IS A relationships using revision searcher");

		final Expression activeStatedNonIsAExpression = Expressions.builder()
				.filter(active())
				.filter(characteristicTypeId(Concepts.STATED_RELATIONSHIP))
				.mustNot(typeId(Concepts.IS_A))
				.mustNot(modules(Concepts.UK_MODULES_NOCLASSIFY))
				.build();
		
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
				.where(activeStatedNonIsAExpression)
				.sortBy(SortBy.builder()
						.sortByField(SnomedRelationshipIndexEntry.Fields.SOURCE_ID, Order.ASC)
						.sortByField(SnomedRelationshipIndexEntry.Fields.TYPE_ID, Order.ASC)
						.sortByField(SnomedRelationshipIndexEntry.Fields.DESTINATION_ID, Order.ASC)
						.sortByField(SnomedRelationshipIndexEntry.Fields.GROUP, Order.ASC)
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
					statedNonIsARelationships.putAll(lastSourceId, fragments);
					fragments.clear();
					lastSourceId = sourceId;
				}
		
				final long statementId = Long.parseLong(relationship[0]);
				final long typeId1 = Long.parseLong(relationship[2]);
				final long destinationId = Long.parseLong(relationship[3]);
				final boolean destinationNegated = Boolean.parseBoolean(relationship[4]);
				final int group = Integer.parseInt(relationship[5]);
				final int unionGroup = Integer.parseInt(relationship[6]);
				final boolean universal = Concepts.UNIVERSAL_RESTRICTION_MODIFIER.equals(relationship[7]);
				
				final StatementFragment statement = new StatementFragment(
						typeId1,
						destinationId,
						destinationNegated,
						group,
						unionGroup,
						universal,
						statementId,
						false); // Stated relationships have no stated pair
		
				fragments.add(statement);
			}
		}
		
		if (!lastSourceId.isEmpty()) {
			statedNonIsARelationships.putAll(lastSourceId, fragments);
			fragments.clear();
		}

		leaving("Registering active stated non-IS A relationships using revision searcher");
		return this;
	}
	
	public ReasonerTaxonomyBuilder addActiveInferredRelationships(final RevisionSearcher searcher) {
		entering("Registering active inferred relationships using revision searcher");
		
		final Expression activeInferredExpression = Expressions.builder()
				.filter(active())
				.filter(characteristicTypeIds(CHARACTERISTIC_TYPE_IDS))
				.mustNot(modules(Concepts.UK_MODULES_NOCLASSIFY))
				.build();
		
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
				.where(activeInferredExpression)
				.sortBy(SortBy.builder()
						.sortByField(SnomedRelationshipIndexEntry.Fields.SOURCE_ID, Order.ASC)
						.sortByField(SnomedRelationshipIndexEntry.Fields.TYPE_ID, Order.ASC)
						.sortByField(SnomedRelationshipIndexEntry.Fields.DESTINATION_ID, Order.ASC)
						.sortByField(SnomedRelationshipIndexEntry.Fields.GROUP, Order.ASC)
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

	public ReasonerTaxonomyBuilder addActiveStatedNonIsARelationships(final Stream<SnomedRelationship> sortedRelationships) {
		entering("Registering active stated non-IS A relationships using relationship stream");

		final Predicate<SnomedRelationship> predicate = relationship -> relationship.isActive() 
				&& CharacteristicType.STATED_RELATIONSHIP.equals(relationship.getCharacteristicType())
				&& !Concepts.IS_A.equals(relationship.getTypeId())
				&& !Concepts.UK_MODULES_NOCLASSIFY.contains(relationship.getModuleId());
		
		addRelationships(sortedRelationships, predicate, statedNonIsARelationships::putAll);

		leaving("Registering active stated non-IS A relationships using relationship stream");
		return this;
	}
	
	public ReasonerTaxonomyBuilder addActiveInferredRelationships(final Stream<SnomedRelationship> sortedRelationships) {
		entering("Registering active inferred relationships using relationship stream");
		
		final Predicate<SnomedRelationship> predicate = relationship -> relationship.isActive() 
				&& CharacteristicType.INFERRED_RELATIONSHIP.equals(relationship.getCharacteristicType())
				&& !Concepts.UK_MODULES_NOCLASSIFY.contains(relationship.getModuleId());
		
		addRelationships(sortedRelationships, predicate, statedNonIsARelationships::putAll);
		
		leaving("Registering active inferred relationships using relationship stream");
		return this;
	}

	/*
	 * XXX: Relationships with the same source ID should be consecutive in the
	 * Stream! We can not verify this in advance.
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
	
	public ReasonerTaxonomyBuilder addActiveConcreteDomainMembers(final RevisionSearcher searcher) {
		entering("Registering active concrete domain members using revision searcher");

		final Query<SnomedRefSetMemberIndexEntry> query = Query.select(SnomedRefSetMemberIndexEntry.class)
				.where(Expressions.builder()
						.filter(active())
						.filter(refSetTypes(Collections.singleton(SnomedRefSetType.CONCRETE_DATA_TYPE)))
						.filter(SnomedRefSetMemberIndexEntry.Expressions.characteristicTypeIds(CHARACTERISTIC_TYPE_IDS))
						.mustNot(modules(Concepts.UK_MODULES_NOCLASSIFY))
						.build())
				.sortBy(SortBy.field(SnomedRefSetMemberIndexEntry.Fields.REFERENCED_COMPONENT_ID, Order.ASC))
				.limit(SCROLL_LIMIT)
				.build();

		final Iterable<Hits<SnomedRefSetMemberIndexEntry>> scrolledHits = searcher.scroll(query);

		final List<ConcreteDomainFragment> statedFragments = newArrayListWithExpectedSize(SCROLL_LIMIT);
		final List<ConcreteDomainFragment> inferredFragments = newArrayListWithExpectedSize(SCROLL_LIMIT);
		final String lastReferencedComponentId = "";

		for (final Hits<SnomedRefSetMemberIndexEntry> hits : scrolledHits) {
			for (final SnomedRefSetMemberIndexEntry member : hits) {
				final String referencedComponentId = member.getReferencedComponentId();

				if (!lastReferencedComponentId.equals(referencedComponentId)) {
					statedConcreteDomainMembers.putAll(lastReferencedComponentId, statedFragments);
					inferredConcreteDomainMembers.putAll(lastReferencedComponentId, inferredFragments);
					statedFragments.clear();
					inferredFragments.clear();
				}

				final String value = SnomedRefSetUtil.serializeValue(member.getDataType(), member.getValue());
				final String label = member.getAttributeName();
				final byte type = (byte) member.getDataType().ordinal();
				final long uomId = Strings.isNullOrEmpty(member.getUnitId()) ? DEFAULT_UNIT : Long.parseLong(member.getUnitId());
				final long refsetId = Long.parseLong(member.getReferenceSetId());
				final String memberId = member.getId();

				final ConcreteDomainFragment fragment = new ConcreteDomainFragment(value, 
						label, 
						type,
						uomId, 
						refsetId,
						memberId);

				if (Concepts.STATED_RELATIONSHIP.equals(member.getCharacteristicTypeId())) {
					statedFragments.add(fragment);
				} else {
					inferredFragments.add(fragment);
				}
			}
		}

		if (!lastReferencedComponentId.isEmpty()) {
			statedConcreteDomainMembers.putAll(lastReferencedComponentId, statedFragments);
			inferredConcreteDomainMembers.putAll(lastReferencedComponentId, inferredFragments);
			statedFragments.clear();
			inferredFragments.clear();
		}

		leaving("Registering active concrete domain members using revision searcher");
		return this;
	}

	/*
	 * XXX: Members with the same referenced component ID should be consecutive in
	 * the Stream! We can not verify this in advance.
	 */
	public ReasonerTaxonomyBuilder addActiveConcreteDomainMembers(final Stream<SnomedReferenceSetMember> sortedMembers) {
		entering("Registering active concrete domain members using stream");

		final List<ConcreteDomainFragment> statedFragments = newArrayListWithExpectedSize(SCROLL_LIMIT);
		final List<ConcreteDomainFragment> inferredFragments = newArrayListWithExpectedSize(SCROLL_LIMIT);
		final String lastReferencedComponentId = "";

		for (final List<SnomedReferenceSetMember> chunk : Iterables.partition(sortedMembers::iterator, SCROLL_LIMIT)) {
			for (final SnomedReferenceSetMember member : chunk) {
				final String characteristicTypeId = (String) member.getProperties().get(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID);

				if (member.isActive() 
						&& SnomedRefSetUtil.isConcreteDomain(member.getReferenceSetId())
						&& CHARACTERISTIC_TYPE_IDS.contains(characteristicTypeId)
						&& !Concepts.UK_MODULES_NOCLASSIFY.contains(member.getModuleId())) {

					final String referencedComponentId = member.getReferencedComponent().getId();

					if (!lastReferencedComponentId.equals(referencedComponentId)) {
						statedConcreteDomainMembers.putAll(lastReferencedComponentId, statedFragments);
						inferredConcreteDomainMembers.putAll(lastReferencedComponentId, inferredFragments);
						statedFragments.clear();
						inferredFragments.clear();
					}

					final String value = (String) member.getProperties().get(SnomedRf2Headers.FIELD_VALUE);
					final String label = (String) member.getProperties().get(SnomedRf2Headers.FIELD_ATTRIBUTE_NAME);
					final byte type = (byte) SnomedRefSetUtil.getDataType(member.getReferenceSetId()).ordinal();
					final String uomId = (String) member.getProperties().get(SnomedRf2Headers.FIELD_UNIT_ID); 
					final long uomIdLong = Strings.isNullOrEmpty(uomId) ? DEFAULT_UNIT : Long.parseLong(uomId);
					final long refsetId = Long.parseLong(member.getReferenceSetId());
					final String memberId = member.getId();

					final ConcreteDomainFragment fragment = new ConcreteDomainFragment(value, 
							label, 
							type,
							uomIdLong, 
							refsetId,
							memberId);

					if (Concepts.STATED_RELATIONSHIP.equals(characteristicTypeId)) {
						statedFragments.add(fragment);
					} else {
						inferredFragments.add(fragment);
					}
				}
			}
		}

		if (!lastReferencedComponentId.isEmpty()) {
			statedConcreteDomainMembers.putAll(lastReferencedComponentId, statedFragments);
			inferredConcreteDomainMembers.putAll(lastReferencedComponentId, inferredFragments);
			statedFragments.clear();
			inferredFragments.clear();
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
				statedConcreteDomainMembers.build(),
				inferredConcreteDomainMembers.build(),
				null, 
				null,
				null,
				null);
	}
}