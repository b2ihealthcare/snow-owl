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
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry.Expressions.characteristicTypeIds;
import static com.google.common.collect.Lists.newArrayListWithExpectedSize;

import java.text.MessageFormat;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.ConcreteDomainFragment;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;

/**
 * Represents a snapshot of the ontology for reasoner input and normal form generation.
 */
public final class ReasonerTaxonomyBuilder {
	
	private static final Logger LOGGER = LoggerFactory.getLogger("reasoner-taxonomy");

	// Long version of "IS A" relationship type ID
	private static final long IS_A_ID = Long.parseLong(Concepts.IS_A);
	
	private static final Set<String> CHARACTERISTIC_TYPE_IDS = ImmutableSet.of(Concepts.STATED_RELATIONSHIP, Concepts.INFERRED_RELATIONSHIP);
	
	private static final int SCROLL_LIMIT = 50_000;

	private static final class GetConceptIdsRunnable implements Runnable {

		private final RevisionSearcher searcher;
		private final String taskName;
		private final Stopwatch stopwatch;
		private final AtomicReference<LongSet> conceptIdsReference;
		private final AtomicReference<LongSet> exhaustiveConceptIdsReference;
		private final AtomicReference<LongSet> fullyDefinedConceptIdsReference;

		private GetConceptIdsRunnable(final RevisionSearcher searcher, 
				final String taskName,
				final Stopwatch stopwatch,
				final AtomicReference<LongSet> conceptIdsReference, 
				final AtomicReference<LongSet> exhaustiveConceptIdsReference, 
				final AtomicReference<LongSet> fullyDefinedConceptIdsReference) {

			this.searcher = searcher;
			this.taskName = taskName;
			this.stopwatch = stopwatch;
			this.conceptIdsReference = conceptIdsReference;
			this.exhaustiveConceptIdsReference = exhaustiveConceptIdsReference;
			this.fullyDefinedConceptIdsReference = fullyDefinedConceptIdsReference;
		}

		@Override
		public void run() {
			final Query<String[]> query = Query.select(String[].class)
					.from(SnomedConceptDocument.class)
					.fields(SnomedConceptDocument.Fields.ID, 
							SnomedConceptDocument.Fields.PRIMITIVE, 
							SnomedConceptDocument.Fields.EXHAUSTIVE)
					.where(Expressions.builder()
							.filter(active())
							.mustNot(modules(Concepts.UK_MODULES_NOCLASSIFY))
							.build())
					.limit(SCROLL_LIMIT)
					.build();
			
			final Iterable<Hits<String[]>> scrolledHits = searcher.scroll(query);
			LongSet conceptIds = null;
			LongSet exhaustiveIds = null;
			LongSet fullyDefinedConceptIds = null;
			
			for (Hits<String[]> page : scrolledHits) {
				if (conceptIds == null) {
					conceptIds = PrimitiveSets.newLongOpenHashSetWithExpectedSize(page.getTotal());
					exhaustiveIds = PrimitiveSets.newLongOpenHashSetWithExpectedSize(page.getTotal());
					fullyDefinedConceptIds = PrimitiveSets.newLongOpenHashSetWithExpectedSize(page.getTotal());
				}
				
				for (String[] conceptFields : page) {
					long conceptId = Long.parseLong(conceptFields[0]);
					conceptIds.add(conceptId);
					
					if (!Boolean.parseBoolean(conceptFields[1])) {
						fullyDefinedConceptIds.add(conceptId);
					}
					
					if (Boolean.parseBoolean(conceptFields[2])) {
						exhaustiveIds.add(conceptId);
					}
				}
			}
			
			conceptIdsReference.set(conceptIds);
			exhaustiveConceptIdsReference.set(exhaustiveIds);
			fullyDefinedConceptIdsReference.set(fullyDefinedConceptIds);
			
			checkpoint(taskName, "active concept IDs collection", stopwatch);
		}
	}

	private static final class GetStatementsRunnable implements Runnable {
	
		private final RevisionSearcher searcher;
		private final String taskName;
		private final Stopwatch stopwatch;
		private final int conceptCount;
		private final AtomicReference<LongKeyMap<Collection<StatementFragment>>> statedFragmentsReference;
		private final AtomicReference<LongKeyMap<Collection<StatementFragment>>> inferredFragmentsReference;
	
		private GetStatementsRunnable(final RevisionSearcher searcher, 
				final String taskName, 
				final Stopwatch stopwatch,
				final int conceptCount,
				final AtomicReference<LongKeyMap<Collection<StatementFragment>>> statedFragmentsReference,
				final AtomicReference<LongKeyMap<Collection<StatementFragment>>> inferredFragmentsReference) {
	
			this.searcher = searcher;
			this.taskName = taskName;
			this.stopwatch = stopwatch;
			this.conceptCount = conceptCount;
			this.statedFragmentsReference = statedFragmentsReference;
			this.inferredFragmentsReference = inferredFragmentsReference;
		}
	
		@Override
		public void run() {
			final Query<String[]> query = Query.select(String[].class)
					.from(SnomedRelationshipIndexEntry.class)
					.fields(SnomedRelationshipIndexEntry.Fields.ID, // 0
							SnomedRelationshipIndexEntry.Fields.STORAGE_KEY, // 1
							SnomedRelationshipIndexEntry.Fields.SOURCE_ID, // 2
							SnomedRelationshipIndexEntry.Fields.TYPE_ID, // 3
							SnomedRelationshipIndexEntry.Fields.DESTINATION_ID, // 4 
							SnomedRelationshipIndexEntry.Fields.DESTINATION_NEGATED, // 5
							SnomedRelationshipIndexEntry.Fields.GROUP, // 6
							SnomedRelationshipIndexEntry.Fields.UNION_GROUP, // 7
							SnomedRelationshipIndexEntry.Fields.MODIFIER_ID, // 8
							SnomedRelationshipIndexEntry.Fields.CHARACTERISTIC_TYPE_ID) // 9
					.where(Expressions.builder()
							.filter(active())
							.filter(SnomedRelationshipIndexEntry.Expressions.characteristicTypeIds(CHARACTERISTIC_TYPE_IDS))
							.mustNot(modules(Concepts.UK_MODULES_NOCLASSIFY))
							.build())
					.limit(SCROLL_LIMIT)
					.build();
			
			final Iterable<Hits<String[]>> scrolledHits = searcher.scroll(query);
			LongKeyMap<Collection<StatementFragment>> statedFragments = null;
			LongKeyMap<Collection<StatementFragment>> inferredFragments = null;
			
			for (Hits<String[]> page : scrolledHits) {
				if (statedFragments == null) {
					statedFragments = PrimitiveMaps.newLongKeyOpenHashMapWithExpectedSize(conceptCount);
				}
				
				if (inferredFragments == null) {
					inferredFragments = PrimitiveMaps.newLongKeyOpenHashMapWithExpectedSize(conceptCount);
				}
				
				for (String[] statementFields : page) {
					final StatementFragment statement = new StatementFragment(
							Long.parseLong(statementFields[3]),
							Long.parseLong(statementFields[4]),
							Boolean.parseBoolean(statementFields[5]),
							Integer.parseInt(statementFields[6]),
							Integer.parseInt(statementFields[7]),
							Concepts.UNIVERSAL_RESTRICTION_MODIFIER.equals(statementFields[8]),
							Long.parseLong(statementFields[0]),
							Long.parseLong(statementFields[1]));
					
					long sourceId = Long.parseLong(statementFields[2]);
					
					if (Concepts.STATED_RELATIONSHIP.equals(statementFields[9])) {
						addToLongMultimap(statedFragments, sourceId, statement);
					} else {
						addToLongMultimap(inferredFragments, sourceId, statement);
					}

				}
			}
			
			statedFragmentsReference.set(statedFragments);
			inferredFragmentsReference.set(inferredFragments);
			
			checkpoint(taskName, "collecting statements", stopwatch);
		}
	}

	private static final class GetConcreteDomainRunnable implements Runnable {
	
		private final RevisionSearcher searcher;
		private final String taskName;
		private final Stopwatch stopwatch;
		private final AtomicReference<LongKeyMap<Collection<ConcreteDomainFragment>>> statedConcreteDomainsReference;
		private final AtomicReference<LongKeyMap<Collection<ConcreteDomainFragment>>> inferredConcreteDomainsReference;
	
		private GetConcreteDomainRunnable(final RevisionSearcher searcher, 
				final String taskName,
				final Stopwatch stopwatch,
				final AtomicReference<LongKeyMap<Collection<ConcreteDomainFragment>>> statedConcreteDomainsReference,
				final AtomicReference<LongKeyMap<Collection<ConcreteDomainFragment>>> inferredConcreteDomainsReference) {
	
			this.searcher = searcher;
			this.taskName = taskName;
			this.stopwatch = stopwatch;
			this.statedConcreteDomainsReference = statedConcreteDomainsReference;
			this.inferredConcreteDomainsReference = inferredConcreteDomainsReference;
		}
	
		@Override
		public void run() {
			final Query<SnomedRefSetMemberIndexEntry> query = Query.select(SnomedRefSetMemberIndexEntry.class)
					.where(Expressions.builder()
							.filter(active())
							.filter(refSetTypes(Collections.singleton(SnomedRefSetType.CONCRETE_DATA_TYPE)))
							.filter(characteristicTypeIds(CHARACTERISTIC_TYPE_IDS))
							.mustNot(modules(Concepts.UK_MODULES_NOCLASSIFY))
							.build())
					.limit(SCROLL_LIMIT)
					.build();
					
			final Iterable<Hits<SnomedRefSetMemberIndexEntry>> scrolledHits = searcher.scroll(query);
			LongKeyMap<Collection<ConcreteDomainFragment>> statedFragments = null;
			LongKeyMap<Collection<ConcreteDomainFragment>> inferredFragments = null;
			
			for (Hits<SnomedRefSetMemberIndexEntry> page : scrolledHits) {
				if (statedFragments == null) {
					statedFragments = PrimitiveMaps.newLongKeyOpenHashMapWithExpectedSize(4);
				}
				
				if (inferredFragments == null) {
					inferredFragments = PrimitiveMaps.newLongKeyOpenHashMapWithExpectedSize(4);
				}
				
				for (SnomedRefSetMemberIndexEntry entry : page) {
					final long referencedComponentId = Long.parseLong(entry.getReferencedComponentId());
					final long refsetId = Long.parseLong(entry.getReferenceSetId());
					final byte dataType = (byte) entry.getDataType().ordinal();
					final long unitId = Strings.isNullOrEmpty(entry.getUnitId()) ? -1L : Long.parseLong(entry.getUnitId());
					final String serializedValue = SnomedRefSetUtil.serializeValue(entry.getDataType(), entry.getValue());
					
					final ConcreteDomainFragment fragment = new ConcreteDomainFragment(serializedValue, 
							entry.getAttributeName(), 
							dataType,
							unitId, 
							entry.getStorageKey(), 
							refsetId);
					
					if (Concepts.STATED_RELATIONSHIP.equals(entry.getCharacteristicTypeId())) {
						addToLongMultimap(statedFragments, referencedComponentId, fragment);
					} else {
						addToLongMultimap(inferredFragments, referencedComponentId, fragment);
					}
				}
			}
	
			statedConcreteDomainsReference.set(statedFragments);
			inferredConcreteDomainsReference.set(inferredFragments);

			checkpoint(taskName, "collecting concrete domain reference set members...", stopwatch);
		}
	}

	private final class TaxonomyBuilderRunnable implements Runnable {

		private final String taskName;
		private final AtomicReference<LongSet> conceptIdsReference;
		private final AtomicReference<LongKeyMap<Collection<StatementFragment>>> statedFragmentsReference;

		private TaxonomyBuilderRunnable(final String taskName, 
				final AtomicReference<LongSet> conceptIdsReference,
				final AtomicReference<LongKeyMap<Collection<StatementFragment>>> statedFragmentsReference) {

			this.taskName = taskName;
			this.conceptIdsReference = conceptIdsReference;
			this.statedFragmentsReference = statedFragmentsReference;
		}

		@Override
		public void run() {

			final LongSet conceptIds = conceptIdsReference.get();
			final int conceptCount = conceptIds.size();

			internalIdToconceptId = PrimitiveLists.newLongArrayListWithExpectedSize(conceptCount);
			conceptIdToInternalId = PrimitiveMaps.newLongKeyIntOpenHashMapWithExpectedSize(conceptCount);

			LongIterator iterator = conceptIds.iterator();
			while (iterator.hasNext()) {
				final long conceptId = iterator.next();
				internalIdToconceptId.add(conceptId);
				conceptIdToInternalId.put(conceptId, internalIdToconceptId.size() - 1);
			}

			final int[] outboundIsACount = new int[conceptCount];
			final int[] inboundIsACount = new int[conceptCount];

			superTypes = new int[conceptCount][];
			subTypes = new int[conceptCount][];

			LongKeyMap<Collection<StatementFragment>> statedFragments = statedFragmentsReference.get();
			
			// Count how many elements in the arrays we need for subtypes and supertypes
			iterator = conceptIds.iterator();
			while (iterator.hasNext()) {
				final long sourceId = iterator.next();
				final int sourceInternalId = getInternalId(sourceId);
				
				final Collection<StatementFragment> relationships = statedFragments.get(sourceId);
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
			iterator = conceptIds.iterator();
			while (iterator.hasNext()) {
				final long sourceId = iterator.next();
				final int sourceInternalId = getInternalId(sourceId);
				
				final Collection<StatementFragment> relationships = statedFragments.get(sourceId);
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
	}

	private static <T> AtomicReference<T> createAtomicReference() {
		return new AtomicReference<T>();
	}

	private static void entering(final String taskName) {
		LOGGER.info(MessageFormat.format(">>> {0}", taskName));
	}

	private static void checkpoint(final String taskName, final String message, final Stopwatch stopwatch) {
		LOGGER.info(MessageFormat.format("--- {0}: {1} [{2}]", taskName, message, TimeUtil.toString(stopwatch)));
	}

	private static void leaving(final String taskName, final Stopwatch stopwatch) {
		LOGGER.info(MessageFormat.format("<<< {0} [{1}]", taskName, TimeUtil.toString(stopwatch)));
	}

	private static <T> void addToLongMultimap(LongKeyMap<Collection<T>> multimap, long key, T value) {
		Collection<T> fragments = multimap.get(key);
		if (fragments == null) {
			fragments = newArrayListWithExpectedSize(1);
			multimap.put(key, fragments);
		}
		
		fragments.add(value);
	}

	private final Stopwatch stopwatch;
	
	/** Matrix for storing concept ancestors by internal IDs. */
	private int[][] superTypes;

	/** Matrix for storing concept descendants by internal IDs. */
	private int[][] subTypes;
	
	/** Maps internal IDs to SCTIDs. */
	private LongList internalIdToconceptId;

	/** Maps SCTIDs to internal IDs. */
	private LongKeyIntMap conceptIdToInternalId;

	/** A set containing all exhaustive concept IDs. */
	private LongSet exhaustiveConceptIds;

	/** A set containing all fully defined concept IDs. */
	private LongSet fullyDefinedConceptIds;

	/** Maps concept IDs to the associated stated active outbound relationships. */
	private LongKeyMap<Collection<StatementFragment>> statedStatementMap;

	/** Maps concept IDs to the associated inferred active outbound relationships. */
	private LongKeyMap<Collection<StatementFragment>> inferredStatementMap;
	
	/** Maps component IDs to the associated stated concrete domain members. */
	private LongKeyMap<Collection<ConcreteDomainFragment>> statedConcreteDomainMap;

	/** Maps component IDs to the associated inferred concrete domain members. */
	private LongKeyMap<Collection<ConcreteDomainFragment>> inferredConcreteDomainMap;

	/**
	 * Creates a taxonomy builder instance.
	 *
	 * @param searcher - an active revision searcher on a branch where this reasoner taxonomy builder should collect data from
	 * @param concreteDomainSupportEnabled - if concrete domain reference set members should be populated
	 */
	public ReasonerTaxonomyBuilder(final RevisionSearcher searcher, final boolean concreteDomainSupportEnabled) {
		
		this.stopwatch = Stopwatch.createStarted();
		
		final String taskName = MessageFormat.format("Building reasoner taxonomy for branch path ''{0}''", searcher.branch());
		entering(taskName);

		final AtomicReference<LongSet> conceptIdsReference = createAtomicReference();
		final AtomicReference<LongSet> exhaustiveConceptIdsReference = createAtomicReference();
		final AtomicReference<LongSet> fullyDefinedConceptIdsReference = createAtomicReference();

		new GetConceptIdsRunnable(searcher, taskName, stopwatch,
				conceptIdsReference, 
				exhaustiveConceptIdsReference, 
				fullyDefinedConceptIdsReference).run();
		
		final AtomicReference<LongKeyMap<Collection<StatementFragment>>> statedStatementsReference = createAtomicReference();
		final AtomicReference<LongKeyMap<Collection<StatementFragment>>> inferredStatementsReference = createAtomicReference();
		
		new GetStatementsRunnable(searcher, taskName, stopwatch,
				conceptIdsReference.get().size(),
				statedStatementsReference, 
				inferredStatementsReference).run();
		
		new TaxonomyBuilderRunnable(taskName, 
				conceptIdsReference, 
				statedStatementsReference).run();
		
		if (concreteDomainSupportEnabled) {
			final AtomicReference<LongKeyMap<Collection<ConcreteDomainFragment>>> statedConcreteDomainsReference = createAtomicReference();
			final AtomicReference<LongKeyMap<Collection<ConcreteDomainFragment>>> inferredConcreteDomainsReference = createAtomicReference();
			
			new GetConcreteDomainRunnable(searcher, taskName, stopwatch, 
					statedConcreteDomainsReference, 
					inferredConcreteDomainsReference).run();
			
			statedConcreteDomainMap = statedConcreteDomainsReference.get();
			inferredConcreteDomainMap = inferredConcreteDomainsReference.get();
		}
		
		exhaustiveConceptIds = exhaustiveConceptIdsReference.get();
		fullyDefinedConceptIds = fullyDefinedConceptIdsReference.get();
		statedStatementMap = statedStatementsReference.get();
		inferredStatementMap = inferredStatementsReference.get();
		
		leaving(taskName, stopwatch);
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

	public Collection<ConcreteDomainFragment> getStatedConcreteDomainFragments(final long componentId) {
		if (statedConcreteDomainMap == null) {
			return Collections.emptySet();
		}
		
		final Collection<ConcreteDomainFragment> statedConcreteDomainFragments = statedConcreteDomainMap.get(componentId);
		if (statedConcreteDomainFragments != null) {
			 return statedConcreteDomainFragments;
		} else {
			return Collections.emptySet();
		}
	}
	
	public Set<String> getAllStatedConcreteDomainLabels() {
		 return statedConcreteDomainMap.values()
		 	.stream()
		 	.flatMap(Collection::stream)
		 	.map(ConcreteDomainFragment::getLabel)
			.collect(Collectors.toSet());
	}
	
	public Collection<ConcreteDomainFragment> getInferredConcreteDomainFragments(final long componentId) {
		if (inferredConcreteDomainMap == null) {
			return Collections.emptySet();
		}
		
		final Collection<ConcreteDomainFragment> inferredConcreteDomainFragments = inferredConcreteDomainMap.get(componentId);
		if (inferredConcreteDomainFragments != null) {
			 return inferredConcreteDomainFragments;
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
}
