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
package com.b2international.snowowl.datastore.server.snomed.index;

import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument.Expressions.active;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument.Expressions.modules;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry.Expressions.refSetTypes;
import static com.google.common.collect.Lists.newArrayListWithExpectedSize;

import java.text.MessageFormat;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.collections.PrimitiveLists;
import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.ints.IntIterator;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongKeyIntMap;
import com.b2international.collections.longs.LongKeyMap;
import com.b2international.collections.longs.LongList;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.CompareUtils;
import com.b2international.commons.collect.ArrayIntIterator;
import com.b2international.commons.collect.BitSetIntIterator;
import com.b2international.commons.time.TimeUtil;
import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.revision.Revision;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.ConcreteDomainFragment;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;

/**
 * Represents a snapshot of the ontology for reasoner input and normal form generation.
 */
public final class ReasonerTaxonomyBuilder {
	
	private static final Logger LOGGER = LoggerFactory.getLogger("reasoner-taxonomy");

	// Long version of "IS A" relationship type ID
	private static final long IS_A_ID = Long.parseLong(Concepts.IS_A);
	
	private static final Set<String> RELATIONSHIP_CHARACTERISTIC_TYPE_IDS = ImmutableSet.of(
			Concepts.STATED_RELATIONSHIP, 
			Concepts.INFERRED_RELATIONSHIP,
			Concepts.ADDITIONAL_RELATIONSHIP);
	
	private static final Set<String> CD_CHARACTERISTIC_TYPE_IDS = ImmutableSet.of(
			Concepts.STATED_RELATIONSHIP, 
			Concepts.INFERRED_RELATIONSHIP,
			Concepts.ADDITIONAL_RELATIONSHIP);
	
	private static final int SCROLL_LIMIT = 50_000;

	private static void entering(final String taskName) {
		LOGGER.info(">>> {}", taskName);
	}

	private static void checkpoint(final String taskName, final String message, final Stopwatch stopwatch) {
		LOGGER.info("--- {}: {} [{}]", taskName, message, TimeUtil.toString(stopwatch));
	}

	private static void leaving(final String taskName, final Stopwatch stopwatch) {
		LOGGER.info("<<< {} [{}]", taskName, TimeUtil.toString(stopwatch));
	}

	private static <T> void addToLongMultimap(final LongKeyMap<Collection<T>> multimap, final long key, final T value) {
		Collection<T> fragments = multimap.get(key);
		if (fragments == null) {
			fragments = newArrayListWithExpectedSize(1);
			multimap.put(key, fragments);
		}
		
		fragments.add(value);
	}

	private final Stopwatch stopwatch;

	/** A set containing all fully defined concept IDs. */
	private LongSet fullyDefinedConceptIds;

	/** A set containing all exhaustive concept IDs. */
	private LongSet exhaustiveConceptIds;

	/** Maps concept IDs to the associated stated active outbound relationships. */
	private LongKeyMap<Collection<StatementFragment>> statedStatementMap;

	/** Maps concept IDs to the associated inferred active outbound relationships. */
	private LongKeyMap<Collection<StatementFragment>> inferredStatementMap;

	/** Maps concept IDs to the associated additional active outbound relationships, excluding items with a group number of 0. */
	private LongKeyMap<Collection<StatementFragment>> additionalGroupedStatementMap;

	/** Matrix for storing concept ancestors by internal IDs. */
	private int[][] superTypes;

	/** Matrix for storing concept descendants by internal IDs. */
	private int[][] subTypes;
	
	/** Maps internal IDs to SCTIDs. */
	private LongList internalIdToconceptId;

	/** Maps SCTIDs to internal IDs. */
	private LongKeyIntMap conceptIdToInternalId;

	/** Maps concept IDs to the associated stated concrete domain members. */
	private LongKeyMap<Collection<ConcreteDomainFragment>> statedConcreteDomainMap;

	/** Maps concept IDs to the associated inferred concrete domain members. */
	private LongKeyMap<Collection<ConcreteDomainFragment>> inferredConcreteDomainMap;

	/** Maps concept IDs to the associated additional concrete domain members, excluding items with a group number of 0. */
	private LongKeyMap<Collection<ConcreteDomainFragment>> additionalGroupedConcreteDomainMap;

	/** Maps concept IDs to the term used in one of the concept's active fully specified names. */
	private LongKeyMap<String> fullySpecifiedNameMap;

	public ReasonerTaxonomyBuilder(final RevisionSearcher searcher, final boolean collectConcreteDomains) {
		this(searcher, collectConcreteDomains, true, false, Concepts.UK_MODULES_NOCLASSIFY);
	}

	/**
	 * Creates a taxonomy builder instance.
	 *
	 * @param searcher - an active revision searcher on a branch where this reasoner taxonomy builder should collect data from
	 * @param collectConcreteDomains - if concrete domain reference set members should be populated
	 * @param collectInferredComponents - if the inferred counterparts of collected components should also be collected
	 * @param collectFullySpecifiedNames - if an active FSN for each participating active concepts should also be collected
	 * @param excludedModules - the set of modules to exclude from classification
	 */
	public ReasonerTaxonomyBuilder(final RevisionSearcher searcher,
			final boolean collectConcreteDomains,
			final boolean collectInferredComponents,
			final boolean collectFullySpecifiedNames,
			final Set<String> excludedModules) {
		
		this.stopwatch = Stopwatch.createStarted();
		
		final String taskName = MessageFormat.format("Building reasoner taxonomy for branch path ''{0}''", searcher.branch());
		entering(taskName);

		final LongSet activeConceptIds = initConceptIdSets(taskName, searcher, excludedModules);
		initStatements(taskName, searcher, excludedModules, collectInferredComponents, activeConceptIds.size());
		initTaxonomy(taskName, activeConceptIds);
		
		if (collectConcreteDomains) {
			initConcreteDomains(taskName, searcher, excludedModules, collectInferredComponents);
		}
		
		if (collectFullySpecifiedNames) {
			initActiveFsns(taskName, searcher, excludedModules);
		}
		
		leaving(taskName, stopwatch);
	}
	
	private LongSet initConceptIdSets(final String taskName, final RevisionSearcher searcher, final Set<String> excludedModules) {
		
		final ExpressionBuilder builder = Expressions.builder()
				.filter(active());
		
		if (!CompareUtils.isEmpty(excludedModules)) {
			builder.mustNot(modules(excludedModules));
		}
				
		final Query<String[]> query = Query.select(String[].class)
				.from(SnomedConceptDocument.class)
				.fields(SnomedConceptDocument.Fields.ID, 
						SnomedConceptDocument.Fields.PRIMITIVE, 
						SnomedConceptDocument.Fields.EXHAUSTIVE)
				.where(builder.build())
				.limit(SCROLL_LIMIT)
				.build();
		
		final Iterable<Hits<String[]>> scrolledHits = searcher.scroll(query);
		LongSet activeConceptIds = null;
		
		for (final Hits<String[]> page : scrolledHits) {
			if (activeConceptIds == null) {
				activeConceptIds = PrimitiveSets.newLongOpenHashSetWithExpectedSize(page.getTotal());
				fullyDefinedConceptIds = PrimitiveSets.newLongOpenHashSetWithExpectedSize(page.getTotal());
				exhaustiveConceptIds = PrimitiveSets.newLongOpenHashSetWithExpectedSize(page.getTotal());
			}
			
			for (final String[] conceptFields : page) {
				final long conceptId = Long.parseLong(conceptFields[0]);
				activeConceptIds.add(conceptId);
				
				if (!Boolean.parseBoolean(conceptFields[1])) {
					fullyDefinedConceptIds.add(conceptId);
				}
				
				if (Boolean.parseBoolean(conceptFields[2])) {
					exhaustiveConceptIds.add(conceptId);
				}
			}
		}
		
		// Create empty sets if the dataset did not contain a single active concept of interest
		if (activeConceptIds == null) {
			activeConceptIds = PrimitiveSets.newLongOpenHashSetWithExpectedSize(4);
			fullyDefinedConceptIds = PrimitiveSets.newLongOpenHashSetWithExpectedSize(4);
			exhaustiveConceptIds = PrimitiveSets.newLongOpenHashSetWithExpectedSize(4);
		}
		
		checkpoint(taskName, "active concept IDs collection", stopwatch);
		return activeConceptIds;
	}

	private void initStatements(final String taskName, 
			final RevisionSearcher searcher, 
			final Set<String> excludedModules,
			final boolean collectInferredComponents, 
			final int conceptCount) {
		
		final ExpressionBuilder builder = Expressions.builder()
				.filter(active());

		if (!CompareUtils.isEmpty(excludedModules)) {
			builder.mustNot(modules(excludedModules));
		}

		if (collectInferredComponents) {
			builder.filter(SnomedRelationshipIndexEntry.Expressions.characteristicTypeIds(RELATIONSHIP_CHARACTERISTIC_TYPE_IDS));
		} else {
			builder.filter(SnomedRelationshipIndexEntry.Expressions.characteristicTypeId(Concepts.STATED_RELATIONSHIP));
		}
		
		final Query<String[]> query = Query.select(String[].class)
				.from(SnomedRelationshipIndexEntry.class)
				.fields(SnomedRelationshipIndexEntry.Fields.ID, // 0
						Revision.STORAGE_KEY, // 1
						SnomedRelationshipIndexEntry.Fields.SOURCE_ID, // 2
						SnomedRelationshipIndexEntry.Fields.TYPE_ID, // 3
						SnomedRelationshipIndexEntry.Fields.DESTINATION_ID, // 4 
						SnomedRelationshipIndexEntry.Fields.DESTINATION_NEGATED, // 5
						SnomedRelationshipIndexEntry.Fields.GROUP, // 6
						SnomedRelationshipIndexEntry.Fields.UNION_GROUP, // 7
						SnomedRelationshipIndexEntry.Fields.MODIFIER_ID, // 8
						SnomedRelationshipIndexEntry.Fields.CHARACTERISTIC_TYPE_ID) // 9
				.where(builder.build())
				.limit(SCROLL_LIMIT)
				.build();
		
		final Iterable<Hits<String[]>> scrolledHits = searcher.scroll(query);
		
		for (final Hits<String[]> page : scrolledHits) {
			if (statedStatementMap == null) {
				statedStatementMap = PrimitiveMaps.newLongKeyOpenHashMapWithExpectedSize(conceptCount);
				inferredStatementMap = PrimitiveMaps.newLongKeyOpenHashMapWithExpectedSize(conceptCount);
				additionalGroupedStatementMap = PrimitiveMaps.newLongKeyOpenHashMapWithExpectedSize(conceptCount);
			}
			
			for (final String[] statementFields : page) {
				final int group = Integer.parseInt(statementFields[6]);
				final long statementId = Long.parseLong(statementFields[0]);
				
				final StatementFragment statement = new StatementFragment(
						Long.parseLong(statementFields[3]),
						Long.parseLong(statementFields[4]),
						Boolean.parseBoolean(statementFields[5]),
						group,
						Integer.parseInt(statementFields[7]),
						Concepts.UNIVERSAL_RESTRICTION_MODIFIER.equals(statementFields[8]),
						statementId,
						Long.parseLong(statementFields[1]));
				
				final long sourceId = Long.parseLong(statementFields[2]);
				final String characteristicTypeId = statementFields[9];
				
				switch (characteristicTypeId) {
					case Concepts.STATED_RELATIONSHIP: 
						addToLongMultimap(statedStatementMap, sourceId, statement);
						break;
					case Concepts.ADDITIONAL_RELATIONSHIP:
						if (group > 0) { addToLongMultimap(additionalGroupedStatementMap, sourceId, statement); }
						break;
					case Concepts.INFERRED_RELATIONSHIP:
						addToLongMultimap(inferredStatementMap, sourceId, statement);
						break;
					default:
						throw new IllegalStateException("Unexpected characteristic type '" + characteristicTypeId + "' on relationship '" + statementId + "'.");
				}
			}
		}
		
		// Create empty sets if the dataset did not contain a single active relationship of interest
		if (statedStatementMap == null) {
			statedStatementMap = PrimitiveMaps.newLongKeyOpenHashMapWithExpectedSize(4);
			inferredStatementMap = PrimitiveMaps.newLongKeyOpenHashMapWithExpectedSize(4);
			additionalGroupedStatementMap = PrimitiveMaps.newLongKeyOpenHashMapWithExpectedSize(4);
		}
		
		checkpoint(taskName, "collecting statements", stopwatch);
	}

	private void initConcreteDomains(final String taskName, 
			final RevisionSearcher searcher, 
			final Set<String> excludedModules,
			final boolean collectInferredComponents) {
	
		final ExpressionBuilder builder = Expressions.builder()
				.filter(active())
				.filter(refSetTypes(Collections.singleton(SnomedRefSetType.CONCRETE_DATA_TYPE)));

		if (!CompareUtils.isEmpty(excludedModules)) {
			builder.mustNot(modules(excludedModules));
		}

		if (collectInferredComponents) {
			builder.filter(SnomedRelationshipIndexEntry.Expressions.characteristicTypeIds(CD_CHARACTERISTIC_TYPE_IDS));
		} else {
			builder.filter(SnomedRelationshipIndexEntry.Expressions.characteristicTypeId(Concepts.STATED_RELATIONSHIP));
		}
		
		final Query<SnomedRefSetMemberIndexEntry> query = Query.select(SnomedRefSetMemberIndexEntry.class)
				.where(builder.build())
				.limit(SCROLL_LIMIT)
				.build();
				
		final Iterable<Hits<SnomedRefSetMemberIndexEntry>> scrolledHits = searcher.scroll(query);
		statedConcreteDomainMap = PrimitiveMaps.newLongKeyOpenHashMapWithExpectedSize(4);
		inferredConcreteDomainMap = PrimitiveMaps.newLongKeyOpenHashMapWithExpectedSize(4);
		additionalGroupedConcreteDomainMap = PrimitiveMaps.newLongKeyOpenHashMapWithExpectedSize(4);
		
		for (final Hits<SnomedRefSetMemberIndexEntry> page : scrolledHits) {
			for (final SnomedRefSetMemberIndexEntry entry : page) {
				final long referencedComponentId = Long.parseLong(entry.getReferencedComponentId());
				final long refsetId = Long.parseLong(entry.getReferenceSetId());
				final long typeId = Long.parseLong(entry.getTypeId());
				final String serializedValue = SnomedRefSetUtil.serializeValue(entry.getDataType(), entry.getValue());
				
				final ConcreteDomainFragment fragment = new ConcreteDomainFragment(serializedValue, 
						typeId, 
						entry.getStorageKey(), 
						refsetId,
						entry.getGroup());
				
				switch (entry.getCharacteristicTypeId()) {
					case Concepts.STATED_RELATIONSHIP: 
						addToLongMultimap(statedConcreteDomainMap, referencedComponentId, fragment);
						break;
					case Concepts.ADDITIONAL_RELATIONSHIP:
						if (entry.getGroup() > 0) { addToLongMultimap(additionalGroupedConcreteDomainMap, referencedComponentId, fragment); }
						break;
					case Concepts.INFERRED_RELATIONSHIP:
						addToLongMultimap(inferredConcreteDomainMap, referencedComponentId, fragment);
						break;
					default:
						throw new IllegalStateException("Unexpected characteristic type '" + entry.getCharacteristicTypeId() + "' on CD member '" + entry.getId() + "'.");
				}
			}
		}

		checkpoint(taskName, "collecting concrete domain reference set members...", stopwatch);
	}

	private void initTaxonomy(final String taskName, final LongSet activeConceptIds) {
		final int conceptCount = activeConceptIds.size();

		internalIdToconceptId = PrimitiveLists.newLongArrayListWithExpectedSize(conceptCount);
		conceptIdToInternalId = PrimitiveMaps.newLongKeyIntOpenHashMapWithExpectedSize(conceptCount);

		LongIterator iterator = activeConceptIds.iterator();
		while (iterator.hasNext()) {
			final long conceptId = iterator.next();
			internalIdToconceptId.add(conceptId);
			conceptIdToInternalId.put(conceptId, internalIdToconceptId.size() - 1);
		}

		final int[] outboundIsACount = new int[conceptCount];
		final int[] inboundIsACount = new int[conceptCount];

		superTypes = new int[conceptCount][];
		subTypes = new int[conceptCount][];

		// Count how many elements in the arrays we need for subtypes and supertypes
		iterator = activeConceptIds.iterator();
		while (iterator.hasNext()) {
			final long sourceId = iterator.next();
			final int sourceInternalId = getInternalId(sourceId);
			
			final Collection<StatementFragment> relationships = statedStatementMap.get(sourceId);
			if (relationships == null) {
				continue;
			}
			
			relationships.stream()
				.filter(s -> s.getTypeId() == IS_A_ID)
				.forEach(isAStatement -> {
					final long destinationId = isAStatement.getDestinationId();
					final int destinationInternalId = getInternalId(destinationId);

					outboundIsACount[sourceInternalId]++;
					inboundIsACount[destinationInternalId]++;
				});
		}

		for (int i = 0; i < conceptCount; i++) {
			superTypes[i] = new int[outboundIsACount[i]];
			subTypes[i] = new int[inboundIsACount[i]];
		}

		// Create last used index matrices for IS A relationships (initialized to 0 for all concepts)
		final int[] lastSuperTypeIdx = new int[conceptCount];
		final int[] lastSubTypeIdx = new int[conceptCount];

		// Register IS A relationships as subtype and supertype internal IDs
		iterator = activeConceptIds.iterator();
		while (iterator.hasNext()) {
			final long sourceId = iterator.next();
			final int sourceInternalId = getInternalId(sourceId);
			
			final Collection<StatementFragment> relationships = statedStatementMap.get(sourceId);
			if (relationships == null) {
				continue;
			}
			
			relationships.stream()
				.filter(s -> s.getTypeId() == IS_A_ID)
				.forEach(isAStatement -> {
					final long destinationId = isAStatement.getDestinationId();
					final int destinationInternalId = getInternalId(destinationId);
					
					superTypes[sourceInternalId][lastSuperTypeIdx[sourceInternalId]++] = destinationInternalId;
					subTypes[destinationInternalId][lastSubTypeIdx[destinationInternalId]++] = sourceInternalId;
				});
		}

		checkpoint(taskName, "building taxonomy", stopwatch);
	}

	private void initActiveFsns(final String taskName, final RevisionSearcher searcher, final Set<String> excludedModules) {
		final ExpressionBuilder builder = Expressions.builder()
				.filter(active())
				.filter(SnomedDescriptionIndexEntry.Expressions.type(Concepts.FULLY_SPECIFIED_NAME));

		if (!CompareUtils.isEmpty(excludedModules)) {
			builder.mustNot(modules(excludedModules));
		}
		
		final Query<String[]> query = Query.select(String[].class)
				.from(SnomedDescriptionIndexEntry.class)
				.fields(SnomedDescriptionIndexEntry.Fields.ID, 
						SnomedDescriptionIndexEntry.Fields.CONCEPT_ID,
						SnomedDescriptionIndexEntry.Fields.TERM)
				.where(builder.build())
				.limit(SCROLL_LIMIT)
				.build();
		
		final Iterable<Hits<String[]>> scrolledHits = searcher.scroll(query);
		
		for (final Hits<String[]> page : scrolledHits) {
			if (fullySpecifiedNameMap == null) {
				fullySpecifiedNameMap = PrimitiveMaps.newLongKeyOpenHashMapWithExpectedSize(page.getTotal());
			}
			
			for (final String[] conceptFields : page) {
				final long conceptId = Long.parseLong(conceptFields[1]);
				fullySpecifiedNameMap.put(conceptId, conceptFields[2]);
			}
		}
		
		if (fullySpecifiedNameMap == null) {
			fullySpecifiedNameMap = PrimitiveMaps.newLongKeyOpenHashMapWithExpectedSize(4);
		}
	}

	/**
	 * Returns with all the active source relationships of a concept given by its unique ID.
	 * @param conceptId the ID of the SNOMED&nbsp;CT concept.
	 * @return the active source relationships.
	 */
	public Collection<StatementFragment> getStatedStatementFragments(final long conceptId) {
		final Collection<StatementFragment> statedStatements = statedStatementMap.get(conceptId);
		if (statedStatements != null) {
			 return statedStatements;
		} else {
			return Collections.emptySet();
		}
	}

	/**
	 * Returns with all *NON* IS_A active source relationships of a concept given by its unique ID.
	 * @param conceptId the ID of the SNOMED&nbsp;CT concept.
	 * @return the active *NON* IS_A source relationships.
	 */
	public Collection<StatementFragment> getStatedNonIsAFragments(final long conceptId) {
		return Collections2.filter(
				getStatedStatementFragments(conceptId), 
				fragment -> IS_A_ID != fragment.getTypeId()); 
	}

	public Collection<StatementFragment> getInferredStatementFragments(final long conceptId) {
		final Collection<StatementFragment> inferredStatements = inferredStatementMap.get(conceptId);
		if (inferredStatements != null) {
			 return inferredStatements;
		} else {
			return Collections.emptySet();
		}
	}
	
	public Collection<StatementFragment> getAdditionalGroupedStatementFragments(final long conceptId) {
		final Collection<StatementFragment> additionalGroupedStatementFragments = additionalGroupedStatementMap.get(conceptId);
		if (additionalGroupedStatementFragments != null) {
			 return additionalGroupedStatementFragments;
		} else {
			return Collections.emptySet();
		}
	}

	public Collection<ConcreteDomainFragment> getStatedConcreteDomainFragments(final long conceptId) {
		if (statedConcreteDomainMap == null) {
			return Collections.emptySet();
		}
		
		final Collection<ConcreteDomainFragment> statedConcreteDomainFragments = statedConcreteDomainMap.get(conceptId);
		if (statedConcreteDomainFragments != null) {
			 return statedConcreteDomainFragments;
		} else {
			return Collections.emptySet();
		}
	}
	
	public Collection<ConcreteDomainFragment> getInferredConcreteDomainFragments(final long conceptId) {
		if (inferredConcreteDomainMap == null) {
			return Collections.emptySet();
		}
		
		final Collection<ConcreteDomainFragment> inferredConcreteDomainFragments = inferredConcreteDomainMap.get(conceptId);
		if (inferredConcreteDomainFragments != null) {
			 return inferredConcreteDomainFragments;
		} else {
			return Collections.emptySet();
		}
	}
	
	public Collection<ConcreteDomainFragment> getAdditionalGroupedConcreteDomainFragments(final long conceptId) {
		if (additionalGroupedConcreteDomainMap == null) {
			return Collections.emptySet();
		}
		
		final Collection<ConcreteDomainFragment> additionalGroupedConcreteDomainFragments = additionalGroupedConcreteDomainMap.get(conceptId);
		if (additionalGroupedConcreteDomainFragments != null) {
			 return additionalGroupedConcreteDomainFragments;
		} else {
			return Collections.emptySet();
		}
	}

	public boolean isActive(final long conceptId) {
		return conceptIdToInternalId.containsKey(conceptId);
	}

	/**
	 * Returns {@code true} if the concept given by its ID is exhaustive, otherwise returns with {@code false}.
	 * @param conceptId the unique ID of the SNOMED&nbsp;CT concept to check.
	 * @return {@code true} if the concept is exhaustive, otherwise {@code false}.
	 */
	public boolean isExhaustive(final long conceptId) {
		return exhaustiveConceptIds.contains(conceptId);
	}

	/**
	 * Returns {@code true} if the concept given by its ID is primitive, otherwise returns with {@code false}.
	 * @param conceptId the unique ID of the SNOMED&nbsp;CT concept to check.
	 * @return {@code true} if the concept is primitive, otherwise {@code false}.
	 */
	public boolean isPrimitive(final long conceptId) {
		return !fullyDefinedConceptIds.contains(conceptId);
	}

	/**
	 * Returns with a set of active SNOMED&nbsp;CT concept IDs.
	 * @return a set of concept IDs.
	 */
	public LongSet getConceptIdSet() {
		return conceptIdToInternalId.keySet();
	}

	/**
	 * Returns {@code true} if the current builder instance does not contain any concepts.
	 * Other wise returns {@code false}.
	 * @return {@code true} if the current instance is empty. Otherwise {@code false}.
	 */
	public boolean isEmpty() {
		return conceptIdToInternalId.isEmpty();
	}

	/**
	 * Returns with a set of IDs of the direct descendants of a SNOMED&nbsp;CT concept given by its unique ID.
	 * @param conceptId the ID of the SNOMED&nbsp;CT concept.
	 * @return a set of concept IDs representing the direct descendants of a concept.
	 */
	public LongSet getSubTypeIds(final long conceptId) {

		if (!isActive(conceptId)) {
			return PrimitiveSets.newLongOpenHashSet();
		}

		final int id = getInternalId(conceptId);

		final int[] subtypes = subTypes[id];

		if (CompareUtils.isEmpty(subtypes)) { //guard against lower bound cannot be negative: 0
			return PrimitiveSets.newLongOpenHashSet();
		}

		return convertToConceptIds(new ArrayIntIterator(subtypes));
	}

	/**
	 * Returns with a set of IDs of the direct ancestors of a SNOMED&nbsp;CT concept given by its unique ID.
	 * @param conceptId the ID of the SNOMED&nbsp;CT concept.
	 * @return a set of concept IDs representing the direct ancestors of a concept.
	 */
	public LongSet getSuperTypeIds(final long conceptId) {

		if (!isActive(conceptId)) {
			return PrimitiveSets.newLongOpenHashSet();
		}

		final int id = getInternalId(conceptId);

		final int[] supertypes = superTypes[id];

		if (CompareUtils.isEmpty(supertypes)) { //guard against lower bound cannot be negative: 0
			return PrimitiveSets.newLongOpenHashSet();
		}

		return convertToConceptIds(new ArrayIntIterator(supertypes));
	}

	/**
	 * Returns with a set of IDs of all descendants of a SNOMED&nbsp;CT concept given by its unique ID.
	 * @param conceptId the ID of the SNOMED&nbsp;CT concept.
	 * @return a set of concept IDs representing all descendants of a concept.
	 */
	public LongSet getAllSubTypesIds(final long conceptId) {

		if (!isActive(conceptId)) {
			return PrimitiveSets.newLongOpenHashSet();
		}

		final int conceptCount = internalIdToconceptId.size();
		final int id = getInternalId(conceptId);

		final BitSet subTypeMap = new BitSet(conceptCount);

		collectSubTypes(id, subTypeMap);
		return convertToConceptIds(new BitSetIntIterator(subTypeMap));
	}

	/**
	 * Returns with a set of IDs of all ancestors of a SNOMED&nbsp;CT concept given by its unique ID.
	 * @param conceptId the ID of the SNOMED&nbsp;CT concept.
	 * @return a set of concept IDs representing all ancestors of a concept.
	 */
	public LongSet getAllSuperTypeIds(final long conceptId) {

		if (!isActive(conceptId)) {
			return PrimitiveSets.newLongOpenHashSet();
		}

		final int conceptCount = internalIdToconceptId.size();
		final int id = getInternalId(conceptId);

		final BitSet superTypeMap = new BitSet(conceptCount);

		collectSuperTypes(id, superTypeMap);
		return convertToConceptIds(new BitSetIntIterator(superTypeMap));

	}

	private void collectSuperTypes(final int internalId, final BitSet superTypes) {
		final int[] relationships = this.superTypes[internalId];

		if (relationships != null) {
			for (int i = 0; i < relationships.length; i++) {
				if (!superTypes.get(relationships[i])) {
					superTypes.set(relationships[i]); //set to true
					collectSuperTypes(relationships[i], superTypes);
				}
			}
		}
	}

	private void collectSubTypes(final int internalId, final BitSet subTypes) {
		final int[] relationships = this.subTypes[internalId];

		if (relationships != null) {
			for (int i = 0; i < relationships.length; i++) {
				if (!subTypes.get(relationships[i])) {
					subTypes.set(relationships[i]); //set to true
					collectSubTypes(relationships[i], subTypes);
				}
			}
		}
	}

	private long getConceptId(final int internalId) {
		return internalIdToconceptId.get(internalId);
	}

	private int getInternalId(final long conceptId) {
		return conceptIdToInternalId.get(conceptId);
	}

	private LongSet convertToConceptIds(final IntIterator it) {
		final LongSet result = PrimitiveSets.newLongOpenHashSet();

		while (it.hasNext()) {
			result.add(getConceptId(it.next()));
		}

		return result;
	}

	public String getFullySpecifiedName(final long conceptId) {
		if (fullySpecifiedNameMap != null && fullySpecifiedNameMap.containsKey(conceptId)) {
			return fullySpecifiedNameMap.get(conceptId);
		} else {
			return Long.toString(conceptId);
		}
	}
}
