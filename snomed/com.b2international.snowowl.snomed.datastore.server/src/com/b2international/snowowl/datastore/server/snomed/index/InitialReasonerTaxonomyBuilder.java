/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Sets.newHashSet;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.ClassUtils;
import com.b2international.commons.concurrent.equinox.ForkJoinUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.index.LongDocValuesCollector;
import com.b2international.snowowl.datastore.server.index.IndexServerService;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.ConcreteDomainFragment;
import com.b2international.snowowl.snomed.datastore.IsAStatement;
import com.b2international.snowowl.snomed.datastore.StatementCollectionMode;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedQueryBuilder;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterables;

import bak.pcj.LongIterator;
import bak.pcj.list.LongArrayList;
import bak.pcj.map.LongKeyIntOpenHashMap;
import bak.pcj.map.LongKeyLongMap;
import bak.pcj.map.LongKeyLongOpenHashMap;
import bak.pcj.map.LongKeyMap;
import bak.pcj.map.LongKeyMapIterator;
import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;

/**
 * Class for building the bare minimum representation of the state of the SNOMED&nbsp;CT ontology before processing changes.
 * <p>
 * This class should be used to compare the ontology state with the outcome of the classification process.
 */
public class InitialReasonerTaxonomyBuilder extends AbstractReasonerTaxonomyBuilder {

	private final class GetActiveIsAStatementsRunnable implements Runnable {

		private final String taskName;
		private final AtomicReference<IsAStatement[]> isAStatementsReference;

		private GetActiveIsAStatementsRunnable(final String taskName, 
				final AtomicReference<IsAStatement[]> isAStatementsReference) {
			
			this.taskName = taskName;
			this.isAStatementsReference = isAStatementsReference;
		}

		@Override
		public void run() {
			final Query allowedCharacteristicTypeQuery = createRelationshipCharacteristicTypeQuery(getAllowedCharacteristicTypes());
			final Query statementsQuery = SnomedMappings.newQuery()
					.active()
					.type(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER)
					.relationshipType(Concepts.IS_A)
					.and(allowedCharacteristicTypeQuery)
					.matchAll();

			final int hitCount = getIndexServerService().getHitCount(branchPath, statementsQuery, null);
			final StatementCollector statementCollector = new StatementCollector(hitCount, StatementCollectionMode.NO_IDS);
			getIndexServerService().search(branchPath, statementsQuery, statementCollector);

			isAStatementsReference.set(statementCollector.getStatements());

			checkpoint(taskName, "active IS A statements collection", stopwatch);
		}
	}

	private final class GetConceptIdsRunnable implements Runnable {

		private final String taskName;
		private final AtomicReference<LongSet> conceptIdsReference;
		private final TermQuery additionalClause;

		private GetConceptIdsRunnable(final String taskName,
				final AtomicReference<LongSet> conceptIdsReference, 
				final TermQuery additionalClause) {

			this.taskName = taskName;
			this.conceptIdsReference = conceptIdsReference;
			this.additionalClause = additionalClause;
		}

		@Override
		public void run() {
			final SnomedQueryBuilder qb = SnomedMappings.newQuery().active().type(SnomedTerminologyComponentConstants.CONCEPT_NUMBER);
			if (null != additionalClause) {
				qb.and(additionalClause);
			}
			final Query query = qb.matchAll();
			final int hitCount = getIndexServerService().getHitCount(branchPath, query, null);
			final LongDocValuesCollector collector = new LongDocValuesCollector(SnomedMappings.id().fieldName(), hitCount);
			getIndexServerService().search(branchPath, query, collector);
			conceptIdsReference.set(new LongOpenHashSet(collector.getValues()));
			checkpoint(taskName, MessageFormat.format("active concept IDs collection ({0})", additionalClause.getTerm().field()), stopwatch);
		}
	}

	private final class GetConceptIdsAndKeysRunnable implements Runnable {

		private final String taskName;
		private final AtomicReference<long[][]> conceptIdsReference;

		private GetConceptIdsAndKeysRunnable(final String taskName,
				final AtomicReference<long[][]> conceptIdsReference) {

			this.taskName = taskName;
			this.conceptIdsReference = conceptIdsReference;
		}

		@Override
		public void run() {
			final Query query = SnomedMappings.newQuery().active().type(SnomedTerminologyComponentConstants.CONCEPT_NUMBER).matchAll();
			final int hitCount = getIndexServerService().getHitCount(branchPath, query, null);
			final ConceptIdStorageKeyCollector collector = new ConceptIdStorageKeyCollector(hitCount);
			getIndexServerService().search(branchPath, query, collector);

			conceptIdsReference.set(collector.getIds());

			checkpoint(taskName, "active concept IDs and storage keys collection", stopwatch);
		}
	}

	private final class TaxonomyBuilderRunnable implements Runnable {

		private final String taskName;
		private final AtomicReference<long[][]> conceptIdsReference;
		private final AtomicReference<IsAStatement[]> isAStatementsReference;

		private TaxonomyBuilderRunnable(final String taskName, 
				final AtomicReference<long[][]> conceptIdsReference,
				final AtomicReference<IsAStatement[]> isAStatementsReference) {

			this.taskName = taskName;
			this.conceptIdsReference = conceptIdsReference;
			this.isAStatementsReference = isAStatementsReference;
		}

		@Override
		public void run() {

			final long[][] conceptIds = conceptIdsReference.get();
			final int conceptCount = conceptIds.length;

			internalIdToconceptId = new LongArrayList(conceptCount);
			conceptIdToInternalId = new LongKeyIntOpenHashMap(conceptCount);

			for (final long[] conceptIdAndKey : conceptIds) {
				final long conceptId = conceptIdAndKey[0];
				final long storageKey = conceptIdAndKey[1];

				synchronized (storageKeyLock) {
					componentStorageKeyToConceptId.put(storageKey, conceptId);
				}
				
				internalIdToconceptId.add(conceptId);
				conceptIdToInternalId.put(conceptId, internalIdToconceptId.size() - 1);
			}

			final int[] outboundIsACount = new int[conceptCount];
			final int[] inboundIsACount = new int[conceptCount];

			superTypes = new int[conceptCount][];
			subTypes = new int[conceptCount][];

			final IsAStatement[] isAStatements = isAStatementsReference.get();

			// Count how many elements in the arrays we need for subtypes and supertypes
			for (final IsAStatement isAStatement : isAStatements) {

				final long sourceId = isAStatement.getSourceId();
				final long destinationId = isAStatement.getDestinationId();

				final int sourceInternalId = getInternalId(sourceId);
				final int destinationInternalId = getInternalId(destinationId);

				outboundIsACount[sourceInternalId]++;
				inboundIsACount[destinationInternalId]++;
			}

			for (int i = 0; i < conceptCount; i++) {
				superTypes[i] = new int[outboundIsACount[i]];
				subTypes[i] = new int[inboundIsACount[i]];
			}

			// Create last used index matrices for IS A relationships (initialized to 0 for all concepts)
			final int[] lastSuperTypeIdx = new int[conceptCount];
			final int[] lastSubTypeIdx = new int[conceptCount];

			// Register IS A relationships as subtype and supertype internal IDs
			for (final IsAStatement isAStatement : isAStatements) {

				final long sourceId = isAStatement.getSourceId();
				final long destinationId = isAStatement.getDestinationId();

				final int sourceInternalId = getInternalId(sourceId);
				final int destinationInternalId = getInternalId(destinationId);

				superTypes[sourceInternalId][lastSuperTypeIdx[sourceInternalId]++] = destinationInternalId;
				subTypes[destinationInternalId][lastSubTypeIdx[destinationInternalId]++] = sourceInternalId;
			}

			checkpoint(taskName, "building taxonomy", stopwatch);
		}
	}

	private final class GetConcreteDomainRunnable implements Runnable {

		private final String taskName;
		private final LongSet componentIds;
		private final short referencedComponentType;
		private final Collection<String> characteristicTypes;
		private final AtomicReference<LongKeyMap> concreteDomainMapReference;

		private GetConcreteDomainRunnable(final String taskName,
				final LongSet componentIds,
				final short referencedComponentType,
				final Collection<String> characteristicTypes,
				final AtomicReference<LongKeyMap> concreteDomainMapReference) {

			this.taskName = taskName;
			this.componentIds = componentIds;
			this.referencedComponentType = referencedComponentType;
			this.characteristicTypes = characteristicTypes;
			this.concreteDomainMapReference = concreteDomainMapReference;
		}

		@Override
		public void run() {
			final Query getConceptConcreteDomainQuery = SnomedMappings.newQuery()
					.active()
					.memberRefSetType(SnomedRefSetType.CONCRETE_DATA_TYPE)
					.memberReferencedComponentType(referencedComponentType)
					.and(createMemberCharacteristicTypeQuery(characteristicTypes))
					.matchAll();

			final int hitCount = getIndexServerService().getHitCount(branchPath, getConceptConcreteDomainQuery, null);
			final ConcreteDomainFragmentCollector collector = new ConcreteDomainFragmentCollector(hitCount);
			getIndexServerService().search(branchPath, getConceptConcreteDomainQuery, collector);

			final LongKeyMap concreteDomainMap = collector.getDataTypeMap();

			for (final LongKeyMapIterator itr = concreteDomainMap.entries(); itr.hasNext(); /* not much */) {
				itr.next();

				final long componentId = itr.getKey();
				if (!componentIds.contains(componentId)) {
					itr.remove(); //no matching active SNOMED CT component
				}
			}

			concreteDomainMapReference.set(concreteDomainMap);
			checkpoint(taskName, MessageFormat.format("collecting concrete domain reference set members (referenced component type: {0})", referencedComponentType), stopwatch);
		}
	}

	private final class StatementMapperRunnable implements Runnable {

		private final String taskName;
		private final LongSet conceptIds;
		private final AtomicReference<LongKeyLongMap> statementIdToConceptIdReference;

		private StatementMapperRunnable(final String taskName,
				final LongSet conceptIds,
				final AtomicReference<LongKeyLongMap> statementIdToConceptIdReference) {

			this.taskName = taskName;
			this.conceptIds = conceptIds;
			this.statementIdToConceptIdReference = statementIdToConceptIdReference;
		}

		@Override
		public void run() {
			
			conceptIdToStatedStatements = getStatements(getAllowedCharacteristicTypes());
			final LongKeyLongMap statementIdToConceptIds = new LongKeyLongOpenHashMap(conceptIdToStatedStatements.size());

			for (final LongIterator itr = conceptIdToStatedStatements.keySet().iterator(); itr.hasNext(); /* nothing */) {

				final long sourceConceptId = itr.next();
				final Collection<StatementFragment> fragments = getStatedStatementFragments(sourceConceptId);

				for (final StatementFragment fragment : fragments) {
					final long statementStorageKey = fragment.getStorageKey();
					final long statemendId = fragment.getStatementId();
					
					synchronized (storageKeyLock) {
						componentStorageKeyToConceptId.put(statementStorageKey, sourceConceptId);
					}
					
					statementIdToConceptIds.put(statemendId, sourceConceptId);
				}
			}

			statementIdToConceptIdReference.set(statementIdToConceptIds);
			conceptIdToInferredStatements = getStatements(Collections.singleton(Concepts.INFERRED_RELATIONSHIP));
			checkpoint(taskName, "mapping statements for classification", stopwatch);
		}

		private LongKeyMap getStatements(final Collection<String> characteristicTypes) {
			final SnomedQueryBuilder qb = SnomedMappings.newQuery().active().type(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER);
			if (characteristicTypes.size() == 1) {
				qb.relationshipCharacteristicType(Iterables.getOnlyElement(characteristicTypes));
			} else {
				qb.and(createRelationshipCharacteristicTypeQuery(characteristicTypes));
			}
			final Query statementQuery = qb.matchAll();
			final StatementFragmentCollector collector = new StatementFragmentCollector();
			getIndexServerService().search(branchPath, statementQuery, collector);
			final LongKeyMap statementMap = collector.getStatementMap();
			for (final LongKeyMapIterator itr = statementMap.entries(); itr.hasNext(); /* nothing */) {
				itr.next();
				final long sourceConceptId = itr.getKey();
				if (!conceptIds.contains(sourceConceptId)) { // active relationship, but source concept is not active?
					itr.remove();
				}
			}
			return statementMap;
		}
	}

	public static final Logger LOGGER = LoggerFactory.getLogger(InitialReasonerTaxonomyBuilder.class);

	private static <T> AtomicReference<T> createAtomicReference() {
		return new AtomicReference<T>();
	}

	private static void entering(final String taskName) {
		LOGGER.info(MessageFormat.format(">>> {0}", taskName));
	}

	private static void checkpoint(final String taskName, final String message, final Stopwatch stopwatch) {
		LOGGER.info(MessageFormat.format("--- {0}: {1} [{2}]", taskName, message, stopwatch));
	}

	private static void leaving(final String taskName, final Stopwatch stopwatch) {
		LOGGER.info(MessageFormat.format("<<< {0} [{1}]", taskName, stopwatch));
	}

	private static TermQuery createIntTermQuery(final String fieldName, final int intValue) {
		return (TermQuery) SnomedMappings.newQuery().field(fieldName, intValue).matchAll();
	}

	/* returns with the server side index service for SNOMED CT. */
	@SuppressWarnings("rawtypes")
	private static IndexServerService getIndexServerService() {
		return ClassUtils.checkAndCast(ApplicationContext.getInstance().getService(SnomedIndexService.class), IndexServerService.class);
	}

	private final Object storageKeyLock = new Object();
	private final IBranchPath branchPath;
	private final Stopwatch stopwatch;
	
	/**
	 * Creates a taxonomy builder instance.
	 *
	 * @param branchPath the branch path where the instance should be constructed
	 * @param type the mode of operation for this taxonomy builder (intended for classification or change processing)
	 */
	public InitialReasonerTaxonomyBuilder(final IBranchPath branchPath, final Type type) {
		super(type);
		
		this.branchPath = branchPath;
		this.stopwatch = Stopwatch.createStarted();
		
		final String taskName = MessageFormat.format("Building reasoner taxonomy for branch path ''{0}''", branchPath);
		entering(taskName);

		final AtomicReference<long[][]> conceptIdsReference = createAtomicReference();
		final AtomicReference<IsAStatement[]> isAStatementsReference = createAtomicReference();

		final Runnable getConceptIdsRunnable = new GetConceptIdsAndKeysRunnable(taskName, conceptIdsReference);
		final Runnable getIsAStatementsRunnable = new GetActiveIsAStatementsRunnable(taskName, isAStatementsReference);

		ForkJoinUtils.runInParallel(getIsAStatementsRunnable, getConceptIdsRunnable);

		final LongSet conceptIds = new LongOpenHashSet();
		for (final long[] conceptIdAndKey : conceptIdsReference.get()) {
			conceptIds.add(conceptIdAndKey[0]);
		}

		componentStorageKeyToConceptId = new LongKeyLongOpenHashMap(conceptIds.size()); // Lower bound estimate
		
		final AtomicReference<LongSet> exhaustiveConceptIdsReference = createAtomicReference();
		final AtomicReference<LongSet> fullyDefinedConceptIdsReference = createAtomicReference();
		final AtomicReference<LongKeyMap> conceptConcreteDomainReference = createAtomicReference();
		final AtomicReference<LongKeyMap> inferredConceptConcreteDomainReference = createAtomicReference();
		final AtomicReference<LongKeyMap> relationshipConcreteDomainReference = createAtomicReference();
		final AtomicReference<LongKeyLongMap> statementIdToConceptIdReference = createAtomicReference();

		new StatementMapperRunnable(taskName, conceptIds, statementIdToConceptIdReference).run();
		
		final Runnable taxonomyBuilderRunnable = new TaxonomyBuilderRunnable(taskName, conceptIdsReference, isAStatementsReference);

		final Collection<String> allowedCharacteristicTypes = getAllowedCharacteristicTypes();
		final Runnable getConceptConcreteDomainsRunnable = new GetConcreteDomainRunnable(taskName, 
				conceptIds,
				SnomedTerminologyComponentConstants.CONCEPT_NUMBER,
				allowedCharacteristicTypes,
				conceptConcreteDomainReference);

		final LongSet relationshipIds = statementIdToConceptIdReference.get().keySet();

		// XXX: Change processor mode needs additional concrete domain members as well (relationships only)
		if (!isReasonerMode()) {
			allowedCharacteristicTypes.add(Concepts.ADDITIONAL_RELATIONSHIP);
		}
		
		final Runnable getStatementConcreteDomainsRunnable = new GetConcreteDomainRunnable(taskName, 
				relationshipIds,
				SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, 
				allowedCharacteristicTypes,
				relationshipConcreteDomainReference);

		final Runnable getInferredConceptConcreteDomainsRunnable = new GetConcreteDomainRunnable(taskName, 
				conceptIds,
				SnomedTerminologyComponentConstants.CONCEPT_NUMBER,
				Collections.singleton(Concepts.INFERRED_RELATIONSHIP),
				inferredConceptConcreteDomainReference);
		
		final Runnable getExhaustiveConceptIdsRunnable = new GetConceptIdsRunnable(taskName,
				exhaustiveConceptIdsReference, 
				SnomedMappings.exhaustive().toQuery(1));

		final Runnable getFullyDefinedConceptIdsRunnable = new GetConceptIdsRunnable(taskName,
				fullyDefinedConceptIdsReference, 
				SnomedMappings.primitive().toQuery(0));

		ForkJoinUtils.runInParallel(
				taxonomyBuilderRunnable,
				getConceptConcreteDomainsRunnable,
				getInferredConceptConcreteDomainsRunnable,
				getStatementConcreteDomainsRunnable,
				getExhaustiveConceptIdsRunnable,
				getFullyDefinedConceptIdsRunnable);

		exhaustiveConceptIds = exhaustiveConceptIdsReference.get();
		fullyDefinedConceptIds = fullyDefinedConceptIdsReference.get();
		conceptIdToStatedConcreteDomains = conceptConcreteDomainReference.get();
		conceptIdToInferredConcreteDomains = inferredConceptConcreteDomainReference.get();
		statementIdToConcreteDomain = relationshipConcreteDomainReference.get();

		for (final LongIterator itr = conceptIdToStatedConcreteDomains.keySet().iterator(); itr.hasNext(); /* empty */) {
			final long conceptId = itr.next();
			final Collection<ConcreteDomainFragment> fragments = getConceptConcreteDomainFragments(conceptId);

			for (final ConcreteDomainFragment fragment : fragments) {
				synchronized (storageKeyLock) {
					componentStorageKeyToConceptId.put(fragment.getStorageKey(), conceptId);
				}
			}
		}

		for (final LongIterator itr = statementIdToConcreteDomain.keySet().iterator(); itr.hasNext(); /* empty */) {
			final long statementId = itr.next();
			final long conceptId = statementIdToConceptIdReference.get().get(statementId);
			final Collection<ConcreteDomainFragment> fragments = getConceptConcreteDomainFragments(statementId);

			for (final ConcreteDomainFragment fragment : fragments) {
				synchronized (storageKeyLock) {
					componentStorageKeyToConceptId.put(fragment.getStorageKey(), conceptId);
				}
			}
		}

		leaving(taskName, stopwatch);
	}
	
	private Collection<String> getAllowedCharacteristicTypes() {
		final Collection<String> result = newHashSet();
		result.add(Concepts.STATED_RELATIONSHIP);
		// XXX: Change processor mode requires all defining information, not just stated ones
		if (!isReasonerMode()) {
			result.add(Concepts.INFERRED_RELATIONSHIP);
			result.add(Concepts.DEFINING_RELATIONSHIP);
		}
		return result;
	}
	
	private Query createRelationshipCharacteristicTypeQuery(Collection<String> characteristicTypes) {
		final SnomedQueryBuilder qb = SnomedMappings.newQuery();
		for (String characteristicType : characteristicTypes) {
			qb.relationshipCharacteristicType(characteristicType);
		}
		return qb.matchAny();
	}
	
	private Query createMemberCharacteristicTypeQuery(Collection<String> characteristicTypes) {
		final SnomedQueryBuilder qb = SnomedMappings.newQuery();
		for (String characteristicType : characteristicTypes) {
			qb.memberCharacteristicTypeId(Long.valueOf(characteristicType));
		}
		return qb.matchAny();
	}
}
