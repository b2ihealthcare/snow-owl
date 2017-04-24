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
package com.b2international.snowowl.datastore.server.index;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.FilteredTerminologyBrowser;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.browser.FilterTerminologyBrowserType;
import com.b2international.snowowl.core.api.browser.IFilterClientTerminologyBrowser;
import com.b2international.snowowl.core.api.browser.ITerminologyBrowser;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.datastore.index.DocIdCollector;
import com.b2international.snowowl.datastore.index.DocIdCollector.DocIdsIterator;
import com.b2international.snowowl.datastore.index.IndexRead;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.google.common.base.Functions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

/**
 * Generic terminology browser filter.
 */
public class TerminologyBrowserFilter<E extends IIndexEntry> {

	private final IndexServerService<? extends E> indexService;
	private final AbstractIndexTerminologyBrowser<? extends E> terminologyBrowser;
	
	private Map<String, Document> componentIdDocMap;
	private SetMultimap<String, String> componentIdParentComponentIdMap;
	private Map<String, E> componentMap;
	private SetMultimap<String, String> subTypeMap;
	private SetMultimap<String, String> superTypeMap;
	private Set<String> filteredComponents;
	private int topLevelDepth;
	
	public TerminologyBrowserFilter(final AbstractIndexTerminologyBrowser<? extends E> terminologyBrowser, final IndexServerService<? extends E> indexService) {
		this.terminologyBrowser = terminologyBrowser;
		this.indexService = indexService;
	}
	
	/**
	 * Returns a {@link IFilterClientTerminologyBrowser filtered client terminology browser}.
	 * 
	 * @param branchPath the branch path
	 * @param expression the filter expression
	 * @param monitor the progress monitor
	 * @return the filtered client terminology browser
	 */
	public IFilterClientTerminologyBrowser<E, String> filterTerminologyBrowser(final IBranchPath branchPath, @Nullable final String expression, @Nullable IProgressMonitor monitor) {
		return indexService.executeReadTransaction(branchPath, new IndexRead<IFilterClientTerminologyBrowser<E, String>>() {
			@Override
			public IFilterClientTerminologyBrowser<E, String> execute(IndexSearcher index) throws IOException {
				doBeforeSearch(branchPath, index);
				
				final Query query = createQuery(expression);
				final int maxDoc = index.getIndexReader().maxDoc();
				final DocIdCollector collector = DocIdCollector.create(maxDoc);
				doSearch(branchPath, query, collector);
		
				componentIdDocMap = Maps.newHashMap();
				componentIdParentComponentIdMap = HashMultimap.create();
				
				componentMap = Maps.newHashMap();
				subTypeMap = HashMultimap.create();
				superTypeMap = HashMultimap.create();
				filteredComponents = Sets.newHashSet();
				
				topLevelDepth = getTopLevelDepth();
				
				final DocIdsIterator itr = collector.getDocIDs().iterator();
				while (itr.next()) {
					final int docId = itr.getDocID();
					final Document doc = index.doc(docId);
					final Collection<String> parentIds = getParent(doc);
					final String componentId = Mappings.id().getValue(doc);
					
					filteredComponents.add(componentId);
					componentIdDocMap.put(componentId, doc);
					componentIdParentComponentIdMap.putAll(componentId, parentIds);
				}
				
				//fetch the labels in one query
				Collection<String> rootIds = getRootIds(branchPath);
				addTopLevels(branchPath, null, rootIds, topLevelDepth, Maps.uniqueIndex(rootIds, Functions.<String>identity()));
				
				//fetch the labels in one query
				Map<String, String> idToLabelMap = fetchLabels(branchPath, componentIdDocMap);
				
				for (final String componentId : componentIdDocMap.keySet()) {
					processComponentForTree(branchPath, componentId, idToLabelMap);
				}

				trimTopLevels(null, topLevelDepth);
				
				return new FilteredTerminologyBrowser<E, String>(componentMap, subTypeMap, superTypeMap, FilterTerminologyBrowserType.HIERARCHICAL, filteredComponents);
			}
		});
	}

	protected List<String> getParent(final Document doc) {
		return Mappings.parent().getValues(doc);
	}
	
	/**
	 * @param branchPath
	 * @param componentIdDocMap
	 * @return
	 */
	protected Map<String, String> fetchLabels(IBranchPath branchPath, Map<String, Document> componentIdDocMap) {
		return extractLabels(componentIdDocMap);
	}

	private Map<String, String> extractLabels(Map<String, Document> componentIdDocMap) {
		Map<String, String> result = Maps.newHashMap();
		for (Entry<String, Document> entry : componentIdDocMap.entrySet()) {
			String label = Mappings.label().getValue(entry.getValue());
			result.put(entry.getKey(), label);
		}
		return result;
	}

	/**
	 * Returns with tree depth. This number will be used when trimming nodes up to the root node.
	 * <p>By default it falls back to {@link CoreTerminologyBroker#getTopLevelDepth(String)}
	 * for the underling terminology.
	 * @return the tree depth.
	 */
	protected int getTopLevelDepth() {
		final CoreTerminologyBroker terminologyBroker = CoreTerminologyBroker.getInstance();
		final short terminologyComponentIdAsShort = terminologyBrowser.getConceptTerminologyComponentId();
		final String terminologyComponentId = terminologyBroker.getTerminologyComponentId(terminologyComponentIdAsShort);
		final String terminologyId = terminologyBroker.getTerminologyId(terminologyComponentId);
		return terminologyBroker.getTopLevelDepth(terminologyId);
	}

	/**
	 * Creates the query that will be evaluated before building the filtered terminology
	 * browser.
	 * @param expression the filter expression.
	 * @return the query to evaluate.
	 */
	protected Query createQuery(final String expression) {
		return terminologyBrowser.createFilterTerminologyBrowserQuery(expression);
	}

	/**
	 * Performs any arbitrary operation before running the search for building the
	 * filtered terminology browser instance.
	 * <p>Does nothing by default.
	 * @param the branch path for the visibility.
	 * @param searcher the initialized index searcher. Clients must not release the searcher instance.
	 * @throws IOException if low level I/O error occurs.
	 */
	protected void doBeforeSearch(final IBranchPath branchPath, final IndexSearcher searcher) throws IOException {
		//does nothing by default.
	}

	/**
	 * Performs the actual search with the given query and collector on the branch path.
	 * @param branchPath the branch path for visibility
	 * @param query the query to perform.
	 * @param collector the collector for the results.
	 */
	protected void doSearch(final IBranchPath branchPath, final Query query, final Collector collector) {
		final Filter filter = getFilter();
		if (null == filter) {
			indexService.search(branchPath, query, collector);
		} else {
			indexService.search(branchPath, query, filter, collector);
		}
	}
	
	/**
	 * Returns with the index service.
	 * @return the underlying index service.
	 */
	protected IndexServerService<? extends E> getIndexService() {
		return indexService;
	}
	
	/**
	 * Returns with the filter used for the query.
	 * <p>Could be {@code null}. If {@code null} no filtering will
	 * be performed. By default it returns with {@code null}.
	 * @return the filter. Or {@code null}.
	 */
	protected Filter getFilter() {
		return null;
	}
	
	private void processComponentForTree(final IBranchPath branchPath, final String componentId, final Map<String, String> idToLabelMap) {

		//check for already processed concepts
		if (componentMap.containsKey(componentId)) {
			return;
		}
		
		final Document doc = componentIdDocMap.get(componentId);
		String label = idToLabelMap.get(componentId);
		E resultObject = createResultObject(branchPath, doc);
		
		
		//set the label from outside if necessary (see subclass)
		setLabel(resultObject, label);

		componentMap.put(componentId, resultObject);
		
		Collection<String> superTypeIds = componentIdParentComponentIdMap.get(componentId);
		if (!componentIdParentComponentIdMap.containsKey(componentId)) {
			superTypeIds = getSuperTypeIds(branchPath, componentId);
			componentIdParentComponentIdMap.putAll(componentId, superTypeIds);
		}
		
		processConceptSuperTypes(branchPath, componentId, superTypeIds, idToLabelMap);
	}

	protected void setLabel(IIndexEntry resultObject, String label) {
		//do nothing
	}

	private void processConceptSuperTypes(final IBranchPath branchPath, final String componentId, final Collection<String> superTypeIds, final Map<String, String> idToLabelMap) {

		if (superTypeIds.isEmpty()) {
			return;
		}
		
		boolean hasResultParent = false;
		
		for (final String parentId : superTypeIds) {
			if (filteredComponents.contains(parentId)) {
				hasResultParent = true;
				break;
			}
		}
		
		for (final String parentId : superTypeIds) {

			/* 
			 * An actual search result should only be connected to another search result 
			 * as their parent, if any exist.
			 */
			if (filteredComponents.contains(parentId)) {
				processComponentForTree(branchPath, parentId, idToLabelMap);
			} else if (hasResultParent) {
				continue;
			}

			if (componentMap.containsKey(parentId)) {
				subTypeMap.put(parentId, componentId);
				superTypeMap.put(componentId, parentId);
				continue;
			}

			Collection<String> parentSuperTypeIds = componentIdParentComponentIdMap.get(parentId);
			
			if (!componentIdParentComponentIdMap.containsKey(parentId)) {
				parentSuperTypeIds = getSuperTypeIds(branchPath, parentId);
				componentIdParentComponentIdMap.putAll(parentId, parentSuperTypeIds);
			}
			
			processConceptSuperTypes(branchPath, componentId, parentSuperTypeIds, idToLabelMap);
		}
	}

	private void addTopLevels(final IBranchPath branchPath, final String parentId, final Collection<String> childrenIds, int level, Map<String, String> childrenIdtoLabelMap) {
		
		// Works from top to bottom
		if (level < 1) {
			return;
		}
		
		for (final String childId : childrenIds) {
			
			final E childConcept = getConcept(branchPath, childId);
			
			//explicitely set the label if needed (see subclass)
			setLabel(childConcept, childrenIdtoLabelMap.get(childId));
			
			componentMap.put(childId, childConcept);
			subTypeMap.put(parentId, childId);
			
			if (parentId != null) {
				superTypeMap.put(childId, parentId);
			}
			
			Collection<String> nextChildrenIds = getSubTypeIds(branchPath, childId);
			addTopLevels(branchPath, childId, nextChildrenIds, level - 1, childrenIdtoLabelMap);
		}
	}

	private boolean trimTopLevels(final String candidateId, final int level) {

		// Works from bottom to top
		if (level >= 0) {
			final Set<String> childrenIds = subTypeMap.get(candidateId);
			final Iterator<String> childItr = childrenIds.iterator();
			while (childItr.hasNext()) {
				final String childId = childItr.next();
				if (trimTopLevels(childId, level - 1)) {
					childItr.remove();
				}
			}
		}

		// If all children have been removed by the block above, we can remove this component as well
		// -- except if it a search result
		if (filteredComponents.contains(candidateId)) {
			return false;
		}

		if (candidateId != null) {
			final Set<String> childrenIds = subTypeMap.get(candidateId);
			if (CompareUtils.isEmpty(childrenIds)) {
				superTypeMap.removeAll(candidateId);
				componentMap.remove(candidateId);
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns with the root node IDs.
	 * <p>By default it uses the {@link ITerminologyBrowser#getRootConceptIds(IBranchPath)}.
	 * @param branchPath the branch path for the visibility.
	 * @return a collection of root node IDs.
	 */
	protected Collection<String> getRootIds(final IBranchPath branchPath) {
		return terminologyBrowser.getRootConceptIds(branchPath);
	}

	protected Collection<String> getSuperTypeIds(final IBranchPath branchPath, final String componentId) {
		return terminologyBrowser.getSuperTypeIds(branchPath, componentId);
	}

	protected Collection<String> getSubTypeIds(final IBranchPath branchPath, final String componentId) {
		return terminologyBrowser.getSubTypeIds(branchPath, componentId);
	}

	protected E getConcept(final IBranchPath branchPath, final String componentId) {
		return terminologyBrowser.getConcept(branchPath, componentId);
	}

	protected E createResultObject(final IBranchPath branchPath, final Document doc) {
		return terminologyBrowser.createResultObject(branchPath, doc);
	}
	
	protected AbstractIndexTerminologyBrowser<? extends E> getTerminologyBrowser() {
		return terminologyBrowser;
	}
}
