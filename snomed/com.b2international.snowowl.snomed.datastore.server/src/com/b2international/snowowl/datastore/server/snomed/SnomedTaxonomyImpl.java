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
package com.b2international.snowowl.datastore.server.snomed;

import static com.b2international.commons.StringUtils.isEmpty;
import static com.b2international.commons.graph.GraphUtils.getLongestPath;
import static com.b2international.commons.pcj.LongSets.newLongSet;
import static com.b2international.commons.pcj.LongSets.toStringList;
import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator.REPOSITORY_UUID;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.base.Suppliers.memoize;
import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.HashMultimap.create;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.emptyList;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.BitSet;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;

import bak.pcj.IntCollection;
import bak.pcj.IntIterator;
import bak.pcj.LongCollection;
import bak.pcj.LongIterator;
import bak.pcj.list.IntArrayDeque;
import bak.pcj.list.IntArrayList;
import bak.pcj.list.LongArrayList;
import bak.pcj.map.LongKeyMap;
import bak.pcj.map.LongKeyMapIterator;
import bak.pcj.set.IntBitSet;
import bak.pcj.set.IntOpenHashSet;
import bak.pcj.set.IntSet;
import bak.pcj.set.LongSet;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.arrays.LongBidiMapWithInternalId;
import com.b2international.commons.concurrent.equinox.ForkJoinUtils;
import com.b2international.commons.time.TimeUtil;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.tasks.TaskManager;
import com.b2international.snowowl.dsl.escg.EscgUtils;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.IsAStatement.Statement;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedStatementBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedTaxonomy;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.StatementCollectionMode;
import com.b2international.snowowl.snomed.datastore.escg.IEscgQueryEvaluatorService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.dsl.query.ast.AndClause;
import com.b2international.snowowl.snomed.dsl.query.ast.AttributeClause;
import com.b2international.snowowl.snomed.dsl.query.ast.ConceptRef;
import com.b2international.snowowl.snomed.dsl.query.ast.NotClause;
import com.b2international.snowowl.snomed.dsl.query.ast.NumericDataClause;
import com.b2international.snowowl.snomed.dsl.query.ast.NumericDataGroupClause;
import com.b2international.snowowl.snomed.dsl.query.ast.OrClause;
import com.b2international.snowowl.snomed.dsl.query.ast.RValue;
import com.b2international.snowowl.snomed.dsl.query.ast.RefSet;
import com.b2international.snowowl.snomed.dsl.query.ast.SubExpression;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Stopwatch;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Multimap;

/**
 * Highly customized taxonomy for supplying an ephemeral
 * semantic store when evaluating ESCG expressions.
 * <p>The construction time and the memory consumption of each 
 * instance of this class is quite expensive. Clients should cache instances
 * if would like to reuse it.
 * <p>The current implementation is caching the result of the ESCG expression after evaluating.
 * <br>Each expression will be automatically removed from the cache once {@value #EXPIRE_TIME} 
 * minutes has elapsed after the expression is first looked up, or its last accessed.
 *
 */
public class SnomedTaxonomyImpl implements SnomedTaxonomy {

	private static final Logger LOGGER = getLogger(SnomedTaxonomyImpl.class);
	private static final long EXPIRE_TIME = 5L;
	private static final long IS_A = Long.parseLong(Concepts.IS_A); 
	
	private final IBranchPath branchPath;

	private int descendants[][];
	private int ancestors[][];
	private int incomings[][][];
	private int outgoings[][][];
	private LongBidiMapWithInternalId concepts;
	private LongKeyMap refSetMap;
	private final AtomicReference<InitializationState> state = new AtomicReference<InitializationState>(InitializationState.UNINITIALIZED);
	
	private final LoadingCache<String, Collection<String>> expressionCache = CacheBuilder.newBuilder()
			.expireAfterAccess(EXPIRE_TIME, TimeUnit.MINUTES)
			.build(new CacheLoader<String, Collection<String>>() {
				@Override
				public Collection<String> load(final String expression) throws Exception {
					return getIds(evaluateInternalIds(EscgUtils.INSTANCE.parseRewrite(expression)));
				}
			});

	public SnomedTaxonomyImpl() {
		this(getServiceForClass(TaskManager.class).getActiveBranch(REPOSITORY_UUID));
	}
	
	public SnomedTaxonomyImpl(final IBranchPath branchPath) {
		this.branchPath = checkNotNull(branchPath, "branchPath");
	}

	@Override
	public boolean isActive(final String conceptId) {
		if (isInitialized()) {
			return getInternalId(conceptId) >= 0;
		} else {
			initializeTaxonomyInBackgroud();
			return getTerminologyBrowser().getConcept(branchPath, conceptId).isActive();
		}
	}
	
	@Override
	public String getSnomedRoot() {
		return Concepts.ROOT_CONCEPT;
	}

	@Override
	public Collection<String> getSubtypes(final String conceptId) {
		if (isInitialized()) {
			final int internalId = getInternalId(conceptId);
			if (internalId < 0) {
				return emptyList();
			}
			return getIds(descendants[internalId]);
		} else {
			initializeTaxonomyInBackgroud();
			return toStringList(getTerminologyBrowser().getSubTypeIds(branchPath, Long.parseLong(conceptId)));
		}
	}

	@Override
	public Collection<String> getAllSubtypes(final String conceptId) {
		if (isInitialized()) {
			final int internalId = getInternalId(conceptId);
			if (internalId < 0) {
				return emptyList();
			}
			return getIds(getAllSubTypeInternalIds(internalId));
		} else {
			initializeTaxonomyInBackgroud();
			return toStringList(getTerminologyBrowser().getAllSubTypeIds(branchPath, Long.parseLong(conceptId)));
		}
	}

	@Override
	public int getSubtypesCount(final String conceptId) {
		if (isInitialized()) {
			final int internalId = getInternalId(conceptId);
			if (internalId < 0) {
				return 0;
			}
			return descendants[internalId].length;
		} else {
			initializeTaxonomyInBackgroud();
			return getTerminologyBrowser().getSubTypeCountById(branchPath, conceptId);
		}
	}

	@Override
	public int getAllSubtypesCount(final String conceptId) {
		if (isInitialized()) {
			final int internalId = getInternalId(conceptId);
			if (internalId < 0) {
				return 0;
			}
			return getAllSubTypeInternalIds(internalId).size();
		} else {
			initializeTaxonomyInBackgroud();
			return getTerminologyBrowser().getAllSubTypeCountById(branchPath, conceptId);
		}
	}

	@Override
	public Collection<String> getSupertypes(final String conceptId) {
		if (isInitialized()) {
			final int internalId = getInternalId(conceptId);
			if (internalId < 0) {
				return emptyList();
			}
			return getIds(ancestors[internalId]);
		} else {
			initializeTaxonomyInBackgroud();
			return toStringList(getTerminologyBrowser().getSuperTypeIds(branchPath, Long.parseLong(conceptId)));
		}
	}

	@Override
	public Collection<String> getAllSupertypes(final String conceptId) {
		if (isInitialized()) {
			final int internalId = getInternalId(conceptId);
			if (internalId < 0) {
				return emptyList();
			}
			return getIds(getAllSuperTypeInternalIds(internalId));
		} else {
			initializeTaxonomyInBackgroud();
			return toStringList(getTerminologyBrowser().getAllSuperTypeIds(branchPath, Long.parseLong(conceptId)));
		}
	}

	@Override
	public int getSupertypesCount(final String conceptId) {
		if (isInitialized()) {
			final int internalId = getInternalId(conceptId);
			if (internalId < 0) {
				return 0;
			}
			return ancestors[internalId].length;
		} else {
			initializeTaxonomyInBackgroud();
			return getTerminologyBrowser().getSuperTypeCountById(branchPath, conceptId);
		}
	}

	@Override
	public int getAllSupertypesCount(final String conceptId) {
		if (isInitialized()) {
			final int internalId = getInternalId(conceptId);
			if (internalId < 0) {
				return 0;
			}
			return getAllSuperTypeInternalIds(internalId).size();
		} else {
			initializeTaxonomyInBackgroud();
			return getTerminologyBrowser().getAllSuperTypeCountById(branchPath, conceptId);
		}
	}

	@Override
	public int getOutboundConceptsCount(final String conceptId) {
		if (isInitialized()) {
			final int internalId = getInternalId(conceptId);
			if (internalId < 0) {
				return 0;
			}
			
			final int ids[][] = outgoings[internalId];
			final int isATypeInternalId = getInternalId(IS_A);
			
			int count = 0;
			for (final int[] id : ids) {
				if (id[1] != isATypeInternalId) {
					count++;
				}
			}
			return count;
		} else {
			initializeTaxonomyInBackgroud();
			return getOutboundRelationships(branchPath, conceptId).size();
		}
	}
	
	@Override
	public Collection<String> getOutboundConcepts(final String conceptId) {
		if (isInitialized()) {
			final int internalId = getInternalId(conceptId);
			if (internalId < 0) {
				return emptyList();
			}
			final int ids[][] = outgoings[internalId];
			final IntCollection internalIds = new IntArrayList();
			final int isATypeInternalId = getInternalId(IS_A);
			
			for (final int[] id : ids) {
				if (id[1] != isATypeInternalId) {
					internalIds.add(id[0]);
				}
			}
			return getIds(internalIds);
		} else {
			initializeTaxonomyInBackgroud();
			return newArrayList(transform(getOutboundRelationships(branchPath, conceptId), GET_TARGET_FUNCTION));
		}
	}

	@Override
	public Collection<String> getOutboundConcepts(final String conceptId, final String typeId) {
		if (isInitialized()) {
			final int internalId = getInternalId(conceptId);
			if (internalId < 0 || Concepts.IS_A.equals(typeId)) {
				return emptyList();
			}
			final int ids[][] = outgoings[internalId];
			final int typeInternalId = getInternalId(typeId);
			final IntCollection internalIds = new IntArrayList();
			for (final int[] id : ids) {
				if (typeInternalId == id[1]) {
					internalIds.add(id[0]);
				}
			}
			return getIds(internalIds);
		} else {
			initializeTaxonomyInBackgroud();
			return newArrayList(transform(getOutboundRelationships(branchPath, conceptId, typeId), GET_TARGET_FUNCTION));
		}
	}
	
	@Override
	public Collection<String> getAllOutboundConcepts(final String conceptId) {
		if (isInitialized()) {
			final int internalId = getInternalId(conceptId);
			if (internalId < 0) {
				return emptyList();
			}
			final int ids[][] = outgoings[internalId];
			final IntCollection internalIds = new IntArrayList();
			
			for (final int[] id : ids) {
				internalIds.add(id[0]);
			}
			return getIds(internalIds);
		} else {
			initializeTaxonomyInBackgroud();
			return newArrayList(transform(getOutboundRelationships(branchPath, conceptId, true), GET_TARGET_FUNCTION));
		}
	}

	@Override
	public boolean hasOutboundRelationshipOfType(final String conceptId, final String typeId) {
		return Concepts.IS_A.equals(typeId) ? !isEmpty(getSupertypes(conceptId)) : !isEmpty(getOutboundConcepts(conceptId, typeId));
	}
	
	@Override
	public Collection<String> getOutboundRelationshipTypes(final String conceptId) {
		if (isInitialized()) {
			final int internalId = getInternalId(conceptId);
			if (internalId < 0) {
				return emptyList();
			}
			
			final int ids[][] = outgoings[internalId];
			final IntCollection internalIds = new IntArrayList();
			final int isATypeInternalId = getInternalId(IS_A);
			
			for (final int[] id : ids) {
				final int typeId = id[1];
				if (typeId != isATypeInternalId) {
					internalIds.add(typeId);
				}
			}
			
			return newArrayList(newHashSet(getIds(internalIds)));
		} else {
			initializeTaxonomyInBackgroud();
			return newArrayList(newHashSet(transform(getOutboundRelationships(branchPath, conceptId), GET_TYPE_FUNCTION)));
		}
	}
	
	@Override
	public Collection<String> getInboundConcepts(final String conceptId) {
		if (isInitialized()) {
			final int internalId = getInternalId(conceptId);
			if (internalId < 0) {
				return emptyList();
			}
			final int ids[][] = incomings[internalId];
			final IntCollection internalIds = new IntArrayList();
			final int isATypeInternalId = getInternalId(IS_A);
			
			for (final int[] id : ids) {
				if (id[1] != isATypeInternalId) {
					internalIds.add(id[0]);
				}
			}
			return getIds(internalIds);
		} else {
			initializeTaxonomyInBackgroud();
			return newArrayList(transform(getInboundRelationships(branchPath, conceptId), GET_SOURCE_FUNCTION));
		}
	}

	@Override
	public Collection<String> getInboundConcepts(final String conceptId, final String typeId) {
		if (isInitialized()) {
			final int internalId = getInternalId(conceptId);
			if (internalId < 0 || Concepts.IS_A.equals(typeId)) {
				return emptyList();
			}
			final int ids[][] = incomings[internalId];
			final int typeInternalId = getInternalId(typeId);
			final IntCollection internalIds = new IntArrayList();
			for (final int[] id : ids) {
				if (typeInternalId == id[1]) {
					internalIds.add(id[0]);
				}
			}
			return getIds(internalIds);
		} else {
			initializeTaxonomyInBackgroud();
			return newArrayList(transform(getInboundRelationships(branchPath, conceptId, typeId), GET_SOURCE_FUNCTION));
		}
	}
	
	@Override
	public Collection<String> getAllInboundConcepts(final String conceptId) {
		if (isInitialized()) {
			final int internalId = getInternalId(conceptId);
			if (internalId < 0) {
				return emptyList();
			}
			final int ids[][] = incomings[internalId];
			final IntCollection internalIds = new IntArrayList();
			
			for (final int[] id : ids) {
				internalIds.add(id[0]);
			}
			return getIds(internalIds);
		} else {
			initializeTaxonomyInBackgroud();
			return newArrayList(transform(getInboundRelationships(branchPath, conceptId, true), GET_SOURCE_FUNCTION));
		}
	}
	
	@Override
	public boolean hasInboundRelationshipOfType(final String conceptId, final String typeId) {
		return Concepts.IS_A.equals(typeId) ? !isEmpty(getSubtypes(conceptId)) : !isEmpty(getInboundConcepts(conceptId, typeId));
	}

	@Override
	public boolean isLeaf(final String conceptId) {
		return getSubtypesCount(conceptId) < 1;
	}
	
	@Override
	public Collection<String> getContainerRefSetIds(final String conceptId) {
		if (isInitialized()) {
			if (isEmpty(conceptId)) {
				return emptyList();
			}
			
			try {
				
				final long conceptIdL = Long.parseLong(conceptId);
				final LongSet containerRefSetIds = newLongSet();
				
				for (final LongKeyMapIterator itr = refSetMap.entries(); itr.hasNext(); /**/) {
					itr.next();
					
					final Object value = itr.getValue();
					if (value instanceof LongCollection) {
						if (((LongCollection) value).contains(conceptIdL)) {
							containerRefSetIds.add(itr.getKey());
						}
					}
					
					
				}
				return toStringList(containerRefSetIds);
			} catch (final NumberFormatException e) {
				return emptyList(); 
			}
		} else {
			initializeTaxonomyInBackgroud();
			final Set<String> containerIds = newHashSet(getRefSetBrowser().getContainerRefSetIds(branchPath, conceptId));
			containerIds.addAll(getRefSetBrowser().getContainerMappingRefSetIds(branchPath, conceptId));
			return containerIds;
		}
	}
	
	@Override
	public Collection<String> evaluateEscg(final String expression) {
		if (isInitialized()) {
			try {
				return expressionCache.get(expression);
			} catch (final ExecutionException e) {
				LOGGER.error("Error while evaluating expression: " + expression);
				return getIds(evaluateInternalIds(EscgUtils.INSTANCE.parseRewrite(expression)));
			}
		} else {
			initializeTaxonomyInBackgroud();
			return toStringList(getEscgQueryEvaluator().evaluateConceptIds(branchPath, expression));
		}
	}
	
	@Override
	public int getDepth(final String conceptId) {
		if (isInitialized()) {
			final Multimap<String, String> parentageMap = create();
			for (final String supertypeId : getAllSupertypes(conceptId)) {
				parentageMap.putAll(supertypeId, getSubtypes(supertypeId));
			}
			return getLongestPath(parentageMap).size() - 1;
		} else {
			initializeTaxonomyInBackgroud();
			return getTerminologyBrowser().getDepth(branchPath, conceptId);
		}
	}
	
	@Override
	public int getHeight(final String conceptId) {
		if (isInitialized()) {
			final Multimap<String, String> parentageMap = create();
			for (final String subtypeId : getAllSubtypes(conceptId)) {
				parentageMap.putAll(subtypeId, getSupertypes(subtypeId));
			}
			return getLongestPath(parentageMap).size();
		} else {
			initializeTaxonomyInBackgroud();
			return getTerminologyBrowser().getHeight(branchPath, conceptId);
		}
	}
	
	private synchronized void build(final IBranchPath branchPath) {
		
		if (!InitializationState.isUninitialized(state.get())) {
			return;
		} else {
			state.set(state.get().nextState());
		}
		
		final Stopwatch stopwatch = Stopwatch.createStarted();
		
		final AtomicReference<Statement[]> statementsReference = new AtomicReference<Statement[]>(new Statement[0]);
		
		final Runnable initStatementsRunnable = new Runnable() {
			@Override public void run() {
				final Statement[] statements = getServiceForClass(SnomedStatementBrowser.class) //
						.getActiveStatements(branchPath, StatementCollectionMode.ALL_TYPES_NO_IDS);
				statementsReference.set(statements);
			}
		};
		
		final Runnable initConceptsRunnable = new Runnable() {
			@Override public void run() {
				final LongCollection conceptIds = getServiceForClass(SnomedTerminologyBrowser.class).getAllActiveConceptIds(branchPath);
				concepts = new LongBidiMapWithInternalId(conceptIds.size());
				for (final LongIterator itr = conceptIds.iterator(); itr.hasNext(); /**/) {
					final long id = itr.next();
					concepts.put(id, id);
				}
			}
		};
		
		final Supplier<LongKeyMap> refSetMapSupplier = memoize(new Supplier<LongKeyMap>() {
			@Override
			public LongKeyMap get() {
				return getServiceForClass(SnomedRefSetBrowser.class).getReferencedConceptIds(branchPath);
		}});
		
		new Thread(new Runnable() {
			@Override public void run() {
				refSetMapSupplier.get();
			}
		}).start();
		
		ForkJoinUtils.runInParallel(initConceptsRunnable, initStatementsRunnable);
		
		final int conceptCount = concepts.size();

		// allocate data
		final int[] incomingIsaHistogram = new int[conceptCount];
		final int[] incomingOtherHistorgram = new int[conceptCount];
		final int[] outgoingIsaHistogram = new int[conceptCount];
		final int[] outgoingOtherHistorgram = new int[conceptCount];

		descendants = new int[conceptCount][];
		ancestors = new int[conceptCount][];
		incomings = new int[conceptCount][][];
		outgoings = new int[conceptCount][][];

		//2D integer array for storing concept internal IDs and for saving ~1M additional internal ID lookup via hash code
		//0 index source/subject concept internal ID
		//1 index destination/object concept internal ID
		//2 index type/attribute concept internal ID
		final int[][] statementConceptInternalIdMappings  = new int[statementsReference.get().length][3];
		int count = 0;
		int isATypeInternalId = -1;
		
		// refresh all RelationshipMini concepts, since they may have been modified
		for (final Statement statement : statementsReference.get()) {
			
			final long destinationId = statement.getDestinationId();
			final long sourceId = statement.getSourceId();
			final long typeId = statement.getTypeId();

			final int sourceConceptInternalId = concepts.getInternalId(sourceId);
			if (sourceConceptInternalId < 0) {
				throw new IllegalStateException(String.format("Cannot find internal source concept ID for %s on branch: %s", sourceId, branchPath));
			}
			final int destinationConceptInternalId = concepts.getInternalId(destinationId);
			if (destinationConceptInternalId < 0) {
				throw new IllegalStateException(String.format("Cannot find internal destination concept ID for %s on branch: %s", destinationId, branchPath));
			}
			final int typeConceptInternalId = concepts.getInternalId(typeId);
			if (destinationConceptInternalId < 0) {
				throw new IllegalStateException(String.format("Cannot find internal type concept ID for %s on branch: %s ", typeId, branchPath));
			}

			incomingOtherHistorgram[destinationConceptInternalId]++;
			outgoingOtherHistorgram[sourceConceptInternalId]++;
			if (IS_A == typeId) {
				incomingIsaHistogram[destinationConceptInternalId]++;
				outgoingIsaHistogram[sourceConceptInternalId]++;
				isATypeInternalId = typeConceptInternalId;
			}

			statementConceptInternalIdMappings[count][0] = sourceConceptInternalId;
			statementConceptInternalIdMappings[count][1] = destinationConceptInternalId;
			statementConceptInternalIdMappings[count][2] = typeConceptInternalId;
			count++;
			
		}

		for (int i = 0; i < conceptCount; i++) {
			descendants[i] = new int[incomingIsaHistogram[i]];
			ancestors[i] = new int[outgoingIsaHistogram[i]];
			incomings[i] = new int[incomingOtherHistorgram[i]][];
			outgoings[i] = new int[outgoingOtherHistorgram[i]][];
		}

		// create index matrices for relationships
		final int[] tailsIncomingIsas = new int[conceptCount];
		final int[] tailIncomingOthers = new int[conceptCount];
		final int[] tailOutgoingIsas = new int[conceptCount];
		final int[] tailOutgoingOthers = new int[conceptCount];

		for (int i = 0; i < statementConceptInternalIdMappings.length; i++) {
			
			final int sourceId = statementConceptInternalIdMappings[i][0];
			final int destinationId = statementConceptInternalIdMappings[i][1];
			final int typeId = statementConceptInternalIdMappings[i][2];
			
			if (isATypeInternalId == typeId) {
				descendants[destinationId][tailsIncomingIsas[destinationId]++] = sourceId;
				ancestors[sourceId][tailOutgoingIsas[sourceId]++] = destinationId;
			}
			
			incomings[destinationId][tailIncomingOthers[destinationId]++] = new int[] { sourceId, typeId };
			outgoings[sourceId][tailOutgoingOthers[sourceId]++] = new int[] { destinationId, typeId };
			
		}
		
		refSetMap = refSetMapSupplier.get();
		
		state.set(state.get().nextState());

		LOGGER.info("SNOMED CT taxonomy service has been successfully initialized on '{}'. [{}]", branchPath, TimeUtil.toString(stopwatch));
	}
	
	private IntSet evaluateInternalIds(final com.b2international.snowowl.snomed.dsl.query.RValue expression) {
		if (expression instanceof ConceptRef) {
			final ConceptRef concept = (ConceptRef) expression;
			final int internalId = getInternalId(concept.getConceptId());

			if (-1 == internalId) {
				throw new IllegalArgumentException(concept.getConceptId() + " concept does not exist in the database.");
			}
			switch (concept.getQuantifier()) {
				case SELF:
					return new IntBitSet(new int[] { internalId } );
				case ANY_SUBTYPE:
					return getAllSubTypeInternalIds(internalId);
				case SELF_AND_ANY_SUBTYPE:
					return getAllSubTypeAndSelfInternalIds(internalId);
				default:
					throw new IllegalArgumentException("Unknown qualifier type: " + concept.getQuantifier());
			}
		} else if (expression instanceof AttributeClause) {
			final IntSet predicateIds = evaluateInternalIds(((AttributeClause) expression).getLeft());
			final IntSet objectIds = evaluateInternalIds(((AttributeClause) expression).getRight());
			return getByAttributes(predicateIds, objectIds);
		} else if (expression instanceof RefSet) {
			return getMemberConceptInternalIds(((RefSet) expression).getId());

		} else if (expression instanceof SubExpression) {
			return evaluateInternalIds(((SubExpression) expression).getValue());

		} else if (expression instanceof OrClause) {
			final OrClause clause = (OrClause) expression;
			final IntBitSet results = new IntBitSet(getConceptCount());
			results.addAll(evaluateInternalIds(clause.getLeft()));
			results.addAll(evaluateInternalIds(clause.getRight()));
			return results;

		} else if (expression instanceof AndClause) {
			final AndClause clause = (AndClause) expression;

			if (clause.getRight() instanceof NotClause) {
				return handleAndNotInternalIds(clause.getLeft(), (NotClause) clause.getRight());

			} else if (clause.getLeft() instanceof NotClause) {
				return handleAndNotInternalIds(clause.getRight(), (NotClause) clause.getLeft());

			} else {

				final IntBitSet results = new IntBitSet(getConceptCount());
				final IntBitSet leftValueIds = new IntBitSet(evaluateInternalIds(clause.getLeft()));
				final IntBitSet rightValueIds = new IntBitSet(evaluateInternalIds(clause.getRight()));

				for (final IntIterator itr = leftValueIds.iterator(); itr.hasNext(); /* */) {
					final int leftValue = itr.next();
					if (rightValueIds.contains(leftValue)) {
						results.add(leftValue);
					}
				}

				return results;
			}

		} else if (expression instanceof NumericDataClause) {
			//TODO this is not supported now
			return new IntBitSet();
		} else if (expression instanceof NumericDataGroupClause) {
			//TODO this is not supported now
			return new IntBitSet();
		} else if (expression instanceof NotClause) {
			throw new UnsupportedOperationException("Cannot NOT yet: " + expression);
		}

		throw new IllegalArgumentException("Don't know how to expand: " + expression);
	}


	private IntSet handleAndNotInternalIds(final RValue notNegated, final NotClause negated) {
		if (notNegated instanceof NotClause) {
			throw new UnsupportedOperationException("Cannot AND two NOT clauses yet");
		}

		final IntBitSet notNegatedInternalIds = new IntBitSet(evaluateInternalIds(notNegated));
		final IntBitSet negatedInternalIds = new IntBitSet(evaluateInternalIds(negated.getValue()));

		for (final IntIterator itr = negatedInternalIds.iterator(); itr.hasNext(); /* */) {
			notNegatedInternalIds.remove(itr.next());
		}
		return notNegatedInternalIds;
	}

	private Collection<String> getIds(final int[] internalIds) {
		return getIds(new IntArrayDeque(internalIds));
	}
	
	private Collection<String> getIds(final IntCollection internalIds) {
		final LongArrayList ids = new LongArrayList(internalIds.size());
		for (final IntIterator itr = internalIds.iterator(); itr.hasNext(); /**/) {
			ids.add(concepts.get(itr.next()));
		}
		return toStringList(ids);
	}
	
	private int getInternalId(final String conceptId) {
		if (null == conceptId) {
			return -1;
		}
		return getInternalId(Long.parseLong(conceptId));
	}
	
	private int getInternalId(final long conceptId) {
		return concepts.getInternalId(conceptId);
	}

	private int getConceptCount() {
		return concepts.size();
	}

	/**
	 * <p>Returns all objects where the following relationship exists {object, attribute[i], value[j]} for
	 * each attributeId, valueId pair from the specified sets.</p>
	 *
	 * <p>For example for the following attributeIds {a1, a2} and valueIds {v1, v2}, an objectId o1 will be
	 * returned if there is a relationship: {o1, a1, v1} OR {o1, a1, v2} OR {o1, a2, v1} OR {o1, a2, v2}</p>
	 * 
	 * @param attributes collection of attributes for the query
	 * @param values collection of values for the query
	 * @return a set of all objects that match the query
	 */
	private IntSet getByAttributes(final IntSet typeIds, final IntSet destinationConceptIds) {
		
		final IntSet $ = new IntOpenHashSet();
		
		for (final IntIterator itr = destinationConceptIds.iterator(); itr.hasNext(); /**/) {
			final int destinationId = itr.next();
			for (final int[] sourceTypeInternalIds : incomings[destinationId]) {
				final int sourceId = sourceTypeInternalIds[0];
				final int typeId = sourceTypeInternalIds[1];
				if (typeIds.contains(typeId)) {
					$.add(sourceId);
				}
				
			}
		}
		
		return $;
		
	}

	private IntSet getAllSuperTypeInternalIds(final int internalId) {
		final BitSet superTypeMap = new BitSet(getConceptCount());
		collectAncestors(internalId, superTypeMap);
		return processElements(superTypeMap);
	}
	
	private IntSet getAllSubTypeInternalIds(final int internalId) {
		final BitSet subTypeMap = new BitSet(getConceptCount());
		collectDescendants(internalId, subTypeMap);
		return processElements(subTypeMap);
	}

	private IntSet getAllSubTypeAndSelfInternalIds(final int internalId) {
		final BitSet subTypeMap = new BitSet(getConceptCount());
		collectDescendants(internalId, subTypeMap);
		subTypeMap.set(internalId);
		return processElements(subTypeMap);
	}

	private IntSet getMemberConceptInternalIds(final String id) {
		final Object object = refSetMap.get(Long.parseLong(id));
		if (object instanceof LongSet) {
			if (((LongSet) object).isEmpty()) {
				return new IntOpenHashSet();
			}
			final IntSet internalIds = new IntOpenHashSet(((LongSet) object).size());
			for (final LongIterator itr = ((LongSet) object).iterator(); itr.hasNext(); /**/) {
				internalIds.add(getInternalId(itr.next()));
			}
			return internalIds;
		}
		return new IntOpenHashSet();
	}
	
	private void collectAncestors(final int type, final BitSet ancestors) {

		final int[] relationships = this.ancestors[type];
		if (relationships != null) {
			for (int i = 0; i < relationships.length; i++) {
				if (!ancestors.get(relationships[i])) {
					ancestors.set(relationships[i]); //set to true
					collectAncestors(relationships[i], ancestors);
				}
			}
		}
	}
	
	private void collectDescendants(final int type, final BitSet descendants) {

		final int[] relationships = this.descendants[type];
		if (relationships != null) {
			for (int i = 0; i < relationships.length; i++) {
				if (!descendants.get(relationships[i])) {
					descendants.set(relationships[i]); //set to true
					collectDescendants(relationships[i], descendants);
				}
			}
		}
	}
	
	private IntSet processElements(final BitSet bitSet) {
		if (CompareUtils.isEmpty(bitSet)) {
			return new IntOpenHashSet();
		}
		final int count = bitSet.cardinality();
	
		final IntSet $ = new IntOpenHashSet(count);
		for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i + 1)) {
			$.add(i);
		}

		return $;
	}
	
	private boolean isInitialized() {
		return InitializationState.isInitialized(state.get());
	}
	
	private void initializeTaxonomyInBackgroud() {
		if (InitializationState.isUninitialized(state.get())) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					build(branchPath);
				}
			}).start();
		}
	}
	
	private Collection<SnomedRelationshipIndexEntry> getOutboundRelationships(final IBranchPath branchPath, final String conceptId) {
		return getOutboundRelationships(branchPath, conceptId, false);
	}
	
	private Collection<SnomedRelationshipIndexEntry> getOutboundRelationships(final IBranchPath branchPath, final String conceptId, final boolean includeIsas) {
		return filter(getStatementBrowser().getOutboundStatementsById(branchPath, conceptId), includeIsas ? ACTIVE_RELATIONSHIP_PREDICATE : Predicates.and(ACTIVE_RELATIONSHIP_PREDICATE, NON_ISA_RELATIONSHIP_PREDICATE));
	}

	private Collection<SnomedRelationshipIndexEntry> getOutboundRelationships(final IBranchPath branchPath, final String conceptId, final String typeId) {
		return filter(getOutboundRelationships(branchPath, conceptId), new Predicate<SnomedRelationshipIndexEntry>() {
			@Override
			public boolean apply(final SnomedRelationshipIndexEntry relationship) {
				return nullToEmpty(typeId).equals(relationship.getAttributeId());
			}
		});
	}
	
	private Collection<SnomedRelationshipIndexEntry> getInboundRelationships(final IBranchPath branchPath, final String conceptId) {
		return getInboundRelationships(branchPath, conceptId, false);
	}
	
	private Collection<SnomedRelationshipIndexEntry> getInboundRelationships(final IBranchPath branchPath, final String conceptId, final boolean includeIsas) {
		return filter(getStatementBrowser().getInboundStatementsById(branchPath, conceptId), includeIsas ? ACTIVE_RELATIONSHIP_PREDICATE : Predicates.and(ACTIVE_RELATIONSHIP_PREDICATE, NON_ISA_RELATIONSHIP_PREDICATE));
	}
	
	private Collection<SnomedRelationshipIndexEntry> getInboundRelationships(final IBranchPath branchPath, final String conceptId, final String typeId) {
		return filter(getInboundRelationships(branchPath, conceptId), new Predicate<SnomedRelationshipIndexEntry>() {
			@Override
			public boolean apply(final SnomedRelationshipIndexEntry relationship) {
				return nullToEmpty(typeId).equals(relationship.getAttributeId());
			}
		});
	}
	
	private static final Predicate<SnomedRelationshipIndexEntry> ACTIVE_RELATIONSHIP_PREDICATE = new Predicate<SnomedRelationshipIndexEntry>() {
		@Override
		public boolean apply(final SnomedRelationshipIndexEntry relationship) {
			return relationship.isActive();
		}
	};
	
	private static final Predicate<SnomedRelationshipIndexEntry> NON_ISA_RELATIONSHIP_PREDICATE = new Predicate<SnomedRelationshipIndexEntry>() {
		@Override
		public boolean apply(final SnomedRelationshipIndexEntry relationship) {
			return !Concepts.IS_A.equals(relationship.getAttributeId());
		}
	};
	
	private static final Function<SnomedRelationshipIndexEntry, String> GET_TARGET_FUNCTION = new Function<SnomedRelationshipIndexEntry, String>() {
		@Override
		public String apply(final SnomedRelationshipIndexEntry relationship) {
			return relationship.getValueId();
		}
	};
	
	private static final Function<SnomedRelationshipIndexEntry, String> GET_SOURCE_FUNCTION = new Function<SnomedRelationshipIndexEntry, String>() {
		@Override
		public String apply(final SnomedRelationshipIndexEntry relationship) {
			return relationship.getObjectId();
		}
	};
	
	private static final Function<SnomedRelationshipIndexEntry, String> GET_TYPE_FUNCTION = new Function<SnomedRelationshipIndexEntry, String>() {
		@Override
		public String apply(final SnomedRelationshipIndexEntry relationship) {
			return relationship.getAttributeId();
		}
	};
	
	private SnomedTerminologyBrowser getTerminologyBrowser() {
		return getServiceForClass(SnomedTerminologyBrowser.class);
	}
	
	private IEscgQueryEvaluatorService getEscgQueryEvaluator() {
		return getServiceForClass(IEscgQueryEvaluatorService.class);
	}
	
	private SnomedStatementBrowser getStatementBrowser() {
		return getServiceForClass(SnomedStatementBrowser.class);
	}
	
	private SnomedRefSetBrowser getRefSetBrowser() {
		return getServiceForClass(SnomedRefSetBrowser.class);
	}
	
}