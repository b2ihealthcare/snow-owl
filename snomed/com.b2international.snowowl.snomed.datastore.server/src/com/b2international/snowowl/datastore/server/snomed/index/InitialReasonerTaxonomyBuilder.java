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

import java.text.MessageFormat;
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
import com.b2international.snowowl.snomed.datastore.IsAStatement;
import com.b2international.snowowl.snomed.datastore.StatementCollectionMode;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedQueryBuilder;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Stopwatch;

import bak.pcj.LongIterator;
import bak.pcj.list.LongArrayList;
import bak.pcj.map.LongKeyIntOpenHashMap;
import bak.pcj.map.LongKeyMap;
import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;

/**
 * Class for building the bare minimum representation of the state of the SNOMED&nbsp;CT ontology before processing changes.
 * <p>
 * This class should be used to compare the ontology state with the outcome of the classification process.
 */
public class InitialReasonerTaxonomyBuilder extends AbstractReasonerTaxonomyBuilder {

	private final class GetActiveStatedIsAStatementsRunnable implements Runnable {

		private final String taskName;
		private final AtomicReference<IsAStatement[]> isAStatementsReference;

		private GetActiveStatedIsAStatementsRunnable(final String taskName, 
				final AtomicReference<IsAStatement[]> isAStatementsReference) {
			
			this.taskName = taskName;
			this.isAStatementsReference = isAStatementsReference;
		}

		@Override
		public void run() {
			final Query statementsQuery = SnomedMappings.newQuery()
					.active()
					.type(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER)
					.relationshipType(Concepts.IS_A)
					.relationshipCharacteristicType(Concepts.STATED_RELATIONSHIP)
					.matchAll();

			final int hitCount = getIndexServerService().getHitCount(branchPath, statementsQuery, null);
			final StatementCollector statementCollector = new StatementCollector(hitCount, StatementCollectionMode.NO_IDS);
			getIndexServerService().search(branchPath, statementsQuery, statementCollector);

			isAStatementsReference.set(statementCollector.getStatements());

			checkpoint(taskName, "active stated IS A statements collection", stopwatch);
		}
	}

	private final class GetConceptIdsRunnable implements Runnable {

		private final String taskName;
		private final AtomicReference<LongSet> conceptIdsReference;
		private final TermQuery additionalClause;

		private GetConceptIdsRunnable(final String taskName, final AtomicReference<LongSet> conceptIdsReference) {
			this(taskName, conceptIdsReference, null);
		}
		
		private GetConceptIdsRunnable(final String taskName,
				final AtomicReference<LongSet> conceptIdsReference, 
				final TermQuery additionalClause) {

			this.taskName = taskName;
			this.conceptIdsReference = conceptIdsReference;
			this.additionalClause = additionalClause;
		}

		@Override
		public void run() {
			final SnomedQueryBuilder qb = SnomedMappings.newQuery()
					.active()
					.type(SnomedTerminologyComponentConstants.CONCEPT_NUMBER);
			
			if (null != additionalClause) {
				qb.and(additionalClause);
			}
			
			final Query conceptsQuery = qb.matchAll();
			final int hitCount = getIndexServerService().getHitCount(branchPath, conceptsQuery, null);
			final LongDocValuesCollector collector = new LongDocValuesCollector(SnomedMappings.id().fieldName(), hitCount);
			getIndexServerService().search(branchPath, conceptsQuery, collector);
			
			conceptIdsReference.set(new LongOpenHashSet(collector.getValues()));
			
			if (additionalClause != null) {
				checkpoint(taskName, MessageFormat.format("active concept IDs collection ({0})", additionalClause.getTerm().field()), stopwatch);
			} else {
				checkpoint(taskName, "active concept IDs collection", stopwatch);
			}
		}
	}

	private final class TaxonomyBuilderRunnable implements Runnable {

		private final String taskName;
		private final AtomicReference<LongSet> conceptIdsReference;
		private final AtomicReference<IsAStatement[]> isAStatementsReference;

		private TaxonomyBuilderRunnable(final String taskName, 
				final AtomicReference<LongSet> conceptIdsReference,
				final AtomicReference<IsAStatement[]> isAStatementsReference) {

			this.taskName = taskName;
			this.conceptIdsReference = conceptIdsReference;
			this.isAStatementsReference = isAStatementsReference;
		}

		@Override
		public void run() {

			final LongSet conceptIds = conceptIdsReference.get();
			final int conceptCount = conceptIds.size();

			internalIdToconceptId = new LongArrayList(conceptCount);
			conceptIdToInternalId = new LongKeyIntOpenHashMap(conceptCount);

			for (final LongIterator itr = conceptIds.iterator(); itr.hasNext(); /* empty */) {
				final long conceptId = itr.next();
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
		private final String characteristicTypeId;
		private final AtomicReference<LongKeyMap> concreteDomainMapReference;

		private GetConcreteDomainRunnable(final String taskName,
				final String characteristicTypeId,
				final AtomicReference<LongKeyMap> concreteDomainMapReference) {

			this.taskName = taskName;
			this.characteristicTypeId = characteristicTypeId;
			this.concreteDomainMapReference = concreteDomainMapReference;
		}

		@Override
		public void run() {
			final Query getConceptConcreteDomainQuery = SnomedMappings.newQuery()
					.active()
					.memberRefSetType(SnomedRefSetType.CONCRETE_DATA_TYPE)
					.memberCharacteristicTypeId(Long.parseLong(characteristicTypeId))
					.matchAll();

			final int hitCount = getIndexServerService().getHitCount(branchPath, getConceptConcreteDomainQuery, null);
			final ConcreteDomainFragmentCollector collector = new ConcreteDomainFragmentCollector(hitCount);
			getIndexServerService().search(branchPath, getConceptConcreteDomainQuery, collector);

			final LongKeyMap concreteDomainMap = collector.getDataTypeMap();
			concreteDomainMapReference.set(concreteDomainMap);
			
			checkpoint(taskName, MessageFormat.format("collecting concrete domain reference set members (characteristic type: {0})", characteristicTypeId), stopwatch);
		}
	}

	private final class StatementMapperRunnable implements Runnable {

		private final String taskName;
		private final String characteristicTypeId; 
		private final AtomicReference<LongKeyMap> statementMapReference;

		private StatementMapperRunnable(final String taskName,
				final String characteristicTypeId,
				final AtomicReference<LongKeyMap> statementMapReference) {

			this.taskName = taskName;
			this.characteristicTypeId = characteristicTypeId;
			this.statementMapReference = statementMapReference;
		}

		@Override
		public void run() {

			final Query statementQuery = SnomedMappings.newQuery()
				.active()
				.type(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER)
				.relationshipCharacteristicType(characteristicTypeId)
				.matchAll();

			final StatementFragmentCollector collector = new StatementFragmentCollector();
			getIndexServerService().search(branchPath, statementQuery, collector);
			final LongKeyMap statementMap = collector.getStatementMap();

			statementMapReference.set(statementMap);
			
			checkpoint(taskName, "mapping statements for classification", stopwatch);
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

	/* returns with the server side index service for SNOMED CT. */
	@SuppressWarnings("rawtypes")
	private static IndexServerService getIndexServerService() {
		return ClassUtils.checkAndCast(ApplicationContext.getInstance().getService(SnomedIndexService.class), IndexServerService.class);
	}

	private final IBranchPath branchPath;
	private final Stopwatch stopwatch;
	
	/**
	 * Creates a taxonomy builder instance.
	 *
	 * @param branchPath the branch path where the instance should be constructed
	 * @param type the mode of operation for this taxonomy builder (intended for classification or change processing)
	 */
	public InitialReasonerTaxonomyBuilder(final IBranchPath branchPath) {
		this.branchPath = branchPath;
		this.stopwatch = Stopwatch.createStarted();
		
		final String taskName = MessageFormat.format("Building reasoner taxonomy for branch path ''{0}''", branchPath);
		entering(taskName);

		final AtomicReference<LongSet> conceptIdsReference = createAtomicReference();
		final AtomicReference<IsAStatement[]> isAStatementsReference = createAtomicReference();

		final Runnable conceptIdsRunnable = new GetConceptIdsRunnable(taskName, conceptIdsReference);
		final Runnable isAStatementsRunnable = new GetActiveStatedIsAStatementsRunnable(taskName, isAStatementsReference);

		ForkJoinUtils.runInParallel(isAStatementsRunnable, conceptIdsRunnable);

		final AtomicReference<LongKeyMap> statedRelationshipsReference = createAtomicReference();
		final AtomicReference<LongKeyMap> inferredRelationshipsReference = createAtomicReference();
		final AtomicReference<LongSet> exhaustiveConceptIdsReference = createAtomicReference();
		final AtomicReference<LongSet> fullyDefinedConceptIdsReference = createAtomicReference();
		final AtomicReference<LongKeyMap> statedConcreteDomainsReference = createAtomicReference();
		final AtomicReference<LongKeyMap> inferredConcreteDomainsReference = createAtomicReference();
		
		final Runnable taxonomyBuilderRunnable = new TaxonomyBuilderRunnable(taskName, conceptIdsReference, isAStatementsReference);

		final Runnable statedRelationshipsRunnable = new StatementMapperRunnable(taskName, Concepts.STATED_RELATIONSHIP, statedRelationshipsReference);
		final Runnable inferredRelationshipsRunnable = new StatementMapperRunnable(taskName, Concepts.INFERRED_RELATIONSHIP, inferredRelationshipsReference);

		final Runnable statedConcreteDomainsRunnable = new GetConcreteDomainRunnable(taskName, Concepts.STATED_RELATIONSHIP, statedConcreteDomainsReference);
		final Runnable inferredConcreteDomainsRunnable = new GetConcreteDomainRunnable(taskName, Concepts.INFERRED_RELATIONSHIP, inferredConcreteDomainsReference);
		
		final Runnable exhaustiveConceptIdsRunnable = new GetConceptIdsRunnable(taskName, exhaustiveConceptIdsReference, SnomedMappings.exhaustive().toQuery(1));
		final Runnable fullyDefinedConceptIdsRunnable = new GetConceptIdsRunnable(taskName, fullyDefinedConceptIdsReference, SnomedMappings.primitive().toQuery(0));

		ForkJoinUtils.runInParallel(
				taxonomyBuilderRunnable,
				statedRelationshipsRunnable,
				inferredRelationshipsRunnable,
				statedConcreteDomainsRunnable,
				inferredConcreteDomainsRunnable,
				exhaustiveConceptIdsRunnable,
				fullyDefinedConceptIdsRunnable);

		exhaustiveConceptIds = exhaustiveConceptIdsReference.get();
		fullyDefinedConceptIds = fullyDefinedConceptIdsReference.get();
		conceptIdToStatedStatements = statedRelationshipsReference.get();
		conceptIdToInferredStatements = inferredRelationshipsReference.get();
		componentIdToStatedConcreteDomains = statedConcreteDomainsReference.get();
		componentIdToInferredConcreteDomains = inferredConcreteDomainsReference.get();

		leaving(taskName, stopwatch);
	}
}
