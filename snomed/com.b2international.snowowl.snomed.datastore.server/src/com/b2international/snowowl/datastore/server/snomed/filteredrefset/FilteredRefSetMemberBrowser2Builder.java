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
package com.b2international.snowowl.datastore.server.snomed.filteredrefset;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.lucene.search.Query;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.ints.IntIterator;
import com.b2international.collections.ints.IntKeyMap;
import com.b2international.collections.ints.IntSet;
import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongKeyMap;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.StopWatch;
import com.b2international.commons.StringUtils;
import com.b2international.commons.arrays.BidiMapWithInternalId;
import com.b2international.commons.concurrent.equinox.ForkJoinUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.server.index.IndexServerService;
import com.b2international.snowowl.datastore.server.snomed.index.SnomedServerTerminologyBrowser;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.filteredrefset.FilteredRefSetMemberBrowser2;
import com.b2international.snowowl.snomed.datastore.filteredrefset.IRefSetMemberNode;
import com.b2international.snowowl.snomed.datastore.filteredrefset.IRefSetMemberOperation;
import com.b2international.snowowl.snomed.datastore.filteredrefset.TopLevelRefSetMemberNode;
import com.b2international.snowowl.snomed.datastore.index.SnomedConceptIndexQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.SnomedConceptReducedQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.SnomedHierarchy;

/**
 * @deprecated - unsupported will be removed in 4.7
 */
public class FilteredRefSetMemberBrowser2Builder {

	private static final int EXPECTED_REFERENCE_SET_SIZE = 10000;

	private static final boolean REPORT_PERFORMANCE = false;

	private final IBranchPath branchPath;
	private final long refSetId;
	private final boolean includeInactive;
	private final String filterExpression;
	private final List<IRefSetMemberOperation> pendingOperations;
	private final IndexServerService<?> indexService;
	private final SnomedServerTerminologyBrowser terminologyBrowser;

	public FilteredRefSetMemberBrowser2Builder(final IBranchPath branchPath, 
			final long refSetId, 
			final String filterExpression, 
			final boolean includeInactive,
			final List<IRefSetMemberOperation> pendingOperations,
			final IndexServerService<?> indexService,
			final SnomedServerTerminologyBrowser terminologyBrowser) {
		
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		checkNotNull(refSetId, "Reference set identifier concept ID argument cannot be null.");
		
		this.branchPath = branchPath;
		this.refSetId = refSetId;
		this.includeInactive = includeInactive;
		this.filterExpression = filterExpression;
		this.pendingOperations = pendingOperations;
		this.indexService = indexService;
		this.terminologyBrowser = terminologyBrowser;
	}

	public FilteredRefSetMemberBrowser2 build() {

		long t;
		
		if (REPORT_PERFORMANCE) {
			t = System.currentTimeMillis();
		}

		final AtomicReference<LongCollection> filteredMemberConceptIds = new AtomicReference<LongCollection>();
		final AtomicReference<LongCollection> filteredCandidateConceptIds = new AtomicReference<LongCollection>();
		final AtomicReference<SnomedHierarchy> hierarchyReference = new AtomicReference<SnomedHierarchy>();
		
		final Query labelQuery;
		
		if (!StringUtils.isEmpty(filterExpression)) {
			labelQuery = new SnomedConceptReducedQueryAdapter(filterExpression, 
					SnomedConceptIndexQueryAdapter.SEARCH_BY_CONCEPT_ID
					| SnomedConceptIndexQueryAdapter.SEARCH_BY_FSN
					| SnomedConceptIndexQueryAdapter.SEARCH_BY_LABEL
					| SnomedConceptIndexQueryAdapter.SEARCH_BY_SYNONYM
					| SnomedConceptIndexQueryAdapter.SEARCH_BY_OTHER).createQuery();		
		} else {
			labelQuery = null;
		}

		final int maxDoc = indexService.maxDoc(branchPath);
		final List<Runnable> runnables = newArrayList();
		
		runnables.add(new CollectMembersRunnable(labelQuery,
				filteredMemberConceptIds, 
				maxDoc,
				true, 
				indexService, 
				branchPath, 
				refSetId));
		
		if (!StringUtils.isEmpty(filterExpression) && containsAddition()) {
			
			runnables.add(new CollectMembersRunnable(labelQuery,
					filteredCandidateConceptIds,
					maxDoc,
					false,
					indexService,
					branchPath,
					refSetId));
		}

		runnables.add(new InitTaxonomyRunnable(maxDoc, hierarchyReference, indexService, branchPath));

		ForkJoinUtils.runInParallel(runnables);

		if (REPORT_PERFORMANCE) {
			StopWatch.timeErr("Initializing taxonomy builder and loading members", t);
			t = System.currentTimeMillis();
		}

		// Apply pending operations - the final referenced component ID set will be filteredExistingMemberConceptIds
		for (final IRefSetMemberOperation operation : pendingOperations) {
			operation.apply(filteredCandidateConceptIds.get(), filteredMemberConceptIds.get(), hierarchyReference.get());
		}

		final BidiMapWithInternalId<IRefSetMemberNode, IRefSetMemberNode> refSetMemberNodes = new BidiMapWithInternalId<IRefSetMemberNode, IRefSetMemberNode>(EXPECTED_REFERENCE_SET_SIZE);
		final LongKeyMap<Set<IRefSetMemberNode>> referencedComponentToNodeMap = PrimitiveMaps.newLongKeyOpenHashMapWithExpectedSize(EXPECTED_REFERENCE_SET_SIZE);
		
		final Runnable collectReferencedComponentMapRunnable = new CollectReferencedComponentMapRunnable(maxDoc, 
				filteredMemberConceptIds.get(), 
				refSetMemberNodes, 
				referencedComponentToNodeMap,
				includeInactive, 
				branchPath, 
				indexService, 
				refSetId, 
				terminologyBrowser);
		
		collectReferencedComponentMapRunnable.run(); 
		
		final FilteredRefSetMemberBrowser2 postFilterMembers = postFilterMembers(hierarchyReference.get(), refSetMemberNodes, referencedComponentToNodeMap);
		
		if (REPORT_PERFORMANCE) {
			StopWatch.timeErr("Building hierarchy", t);
		}
		
		return postFilterMembers;
	}

	/**
	 * @param hierarchy
	 * @param filteredConceptIds
	 * @param filteredNewConceptIds
	 * @return
	 */
	private FilteredRefSetMemberBrowser2 postFilterMembers(final SnomedHierarchy hierarchy, 
			final BidiMapWithInternalId<IRefSetMemberNode, IRefSetMemberNode> refSetMemberNodes, 
			final LongKeyMap<Set<IRefSetMemberNode>> referencedComponentToNodeMap) {
		
		final IntKeyMap<IntSet> subTypeMap = PrimitiveMaps.newIntKeyOpenHashMapWithExpectedSize(refSetMemberNodes.size());
		final IntKeyMap<IntSet> superTypeMap = PrimitiveMaps.newIntKeyOpenHashMapWithExpectedSize(refSetMemberNodes.size());

		addTopLevels(hierarchy,
				refSetMemberNodes,
				-1,
				Long.valueOf(Concepts.ROOT_CONCEPT),
				subTypeMap, 
				superTypeMap, 
				2,
				referencedComponentToNodeMap);

		for (final IRefSetMemberNode node : refSetMemberNodes.getElements()) {
			processComponentForTree(hierarchy, node, subTypeMap, superTypeMap, refSetMemberNodes, referencedComponentToNodeMap);
		}

		trimTopLevels(-1, 2, subTypeMap, superTypeMap, refSetMemberNodes);

		return new FilteredRefSetMemberBrowser2(refSetMemberNodes, subTypeMap, superTypeMap);
	}

	private void addTopLevels(final SnomedHierarchy hierarchy, 
			final BidiMapWithInternalId<IRefSetMemberNode, IRefSetMemberNode> refSetMemberNodes,
			final int parentInternalId,
			final long conceptId, 
			final IntKeyMap<IntSet> subTypeMap, 
			final IntKeyMap<IntSet> superTypeMap, 
			final int level,
			final LongKeyMap<Set<IRefSetMemberNode>> referencedComponentToNodeMap) {
		
		if (level < 1) {
			return;
		}

		if (!referencedComponentToNodeMap.containsKey(conceptId)) {
			final IRefSetMemberNode node = new TopLevelRefSetMemberNode(conceptId, terminologyBrowser.getConcept(branchPath, Long.toString(conceptId)).getLabel());
			refSetMemberNodes.put(node, node);
			put(referencedComponentToNodeMap, conceptId, node);
			
			final int nodeInternalId = refSetMemberNodes.getInternalId(node);
			put(subTypeMap, parentInternalId, nodeInternalId);
			put(superTypeMap, nodeInternalId, parentInternalId);
			
			for (final LongIterator itr = terminologyBrowser.getSubTypeIds(branchPath, conceptId).iterator(); itr.hasNext(); /* empty */) {
				addTopLevels(hierarchy, refSetMemberNodes, nodeInternalId, itr.next(), subTypeMap, superTypeMap, level - 1, referencedComponentToNodeMap);
			}
		}
	}

	private void processComponentForTree(final SnomedHierarchy hierarchy, 
			final IRefSetMemberNode node, 
			final IntKeyMap<IntSet> subTypeMap,
			final IntKeyMap<IntSet> superTypeMap,
			final BidiMapWithInternalId<IRefSetMemberNode, IRefSetMemberNode> refSetMemberNodes, 
			final LongKeyMap<Set<IRefSetMemberNode>> referencedComponentToNodeMap) {

		final LongSet superTypeIds = hierarchy.getSuperTypeIds(node.getConceptId());
		processConceptSuperTypes(hierarchy, node, superTypeIds, referencedComponentToNodeMap, refSetMemberNodes, subTypeMap, superTypeMap);
	}

	private void processConceptSuperTypes(final SnomedHierarchy hierarchy, 
			final IRefSetMemberNode node,
			final LongSet superTypeIds, 
			final LongKeyMap<Set<IRefSetMemberNode>> referencedComponentToNodeMap, 
			final BidiMapWithInternalId<IRefSetMemberNode, IRefSetMemberNode> refSetMemberNodes, 
			final IntKeyMap<IntSet> subTypeMap, 
			final IntKeyMap<IntSet> superTypeMap) {

		if (superTypeIds.isEmpty()) {
			return;
		}

		for (final LongIterator itr = superTypeIds.iterator(); itr.hasNext(); /* empty */) {

			final long parentId = itr.next();
			
			final Set<IRefSetMemberNode> parentNodes = referencedComponentToNodeMap.get(parentId);
			
			if (null != parentNodes) {
				final int childInternalId = refSetMemberNodes.getInternalId(node);
				for (IRefSetMemberNode parent : parentNodes) {
					final int parentInternalId = refSetMemberNodes.getInternalId(parent);
					put(subTypeMap, parentInternalId, childInternalId);
					put(superTypeMap, childInternalId, parentInternalId);
				}
				
				itr.remove();
			}
		}
		
		// If there are any remaining supertypes, continue searching for refset members in those directions until we hit a mock member or the set becomes empty
		processConceptSuperTypes(hierarchy, node, superTypeIds, referencedComponentToNodeMap, refSetMemberNodes, subTypeMap, superTypeMap);
	}

	private void put(final IntKeyMap<IntSet> map, final int key, final int value) {
		IntSet values = map.get(key);
		if (values == null) {
			values = PrimitiveSets.newIntOpenHashSet();
			map.put(key, values);
		}
		values.add(value);
	}

	private boolean trimTopLevels(final int internalId, 
			final int level, 
			final IntKeyMap<IntSet> subTypeMap, 
			final IntKeyMap<IntSet> superTypeMap, 
			final BidiMapWithInternalId<IRefSetMemberNode, IRefSetMemberNode> refSetMemberNodes) {

		// dig down recursively to the specified levels
		if (level >= 1) {
			final IntSet subTypeIds = subTypeMap.get(internalId);
			if (null != subTypeIds) {
				final IntIterator it = subTypeIds.iterator();
				while (it.hasNext()) {
					final int childInternalId = it.next();
					if (trimTopLevels(childInternalId, level - 1, subTypeMap, superTypeMap, refSetMemberNodes)) {
						it.remove();
					}
				}
			}
		}

		if (internalId != -1L) {
			final IntSet subTypeIds = subTypeMap.get(internalId);
			if (null == subTypeIds) {
				superTypeMap.remove(internalId);
				refSetMemberNodes.remove(refSetMemberNodes.get(internalId));
				return true;
			}
		}
		
		return false;
	}

	private boolean containsAddition() {
		for (final IRefSetMemberOperation operation : pendingOperations) {
			if (operation.isAddition()) {
				return true;
			}
		}
		return false;
	}
	
	private void put(final LongKeyMap<Set<IRefSetMemberNode>> referencedComponentToNodeMap, final long conceptId, final IRefSetMemberNode node) {
		Set<IRefSetMemberNode> values = referencedComponentToNodeMap.get(conceptId);
		if (values == null) {
			values = newHashSet();
			referencedComponentToNodeMap.put(conceptId, values);
		}
		values.add(node);
	}
}