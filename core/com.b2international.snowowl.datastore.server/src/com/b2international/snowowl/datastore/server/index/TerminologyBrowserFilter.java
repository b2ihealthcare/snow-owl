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
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ReferenceManager;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.EmptyTerminologyBrowser;
import com.b2international.snowowl.core.api.FilteredTerminologyBrowser;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.browser.FilterTerminologyBrowserType;
import com.b2international.snowowl.core.api.browser.IFilterClientTerminologyBrowser;
import com.b2international.snowowl.core.api.browser.ITerminologyBrowser;
import com.b2international.snowowl.core.api.index.CommonIndexConstants;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.core.api.index.IndexException;
import com.b2international.snowowl.datastore.index.ComponentIdStringField;
import com.b2international.snowowl.datastore.index.DocIdCollector;
import com.b2international.snowowl.datastore.index.DocIdCollector.DocIdsIterator;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Generic terminology browser filter.
 * 
 */
public class TerminologyBrowserFilter<E extends IIndexEntry> {

	private final IndexServerService<E> indexService;
	private final AbstractIndexTerminologyBrowser<E> terminologyBrowser;
	
	public TerminologyBrowserFilter(final AbstractIndexTerminologyBrowser<E> terminologyBrowser, final IndexServerService<E> indexService) {
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
		
		monitor = null == monitor ? new NullProgressMonitor() : monitor;
		
		final ReferenceManager<IndexSearcher> manager = indexService.getManager(branchPath);
		IndexSearcher searcher = null;
		try {

			searcher = manager.acquire();
			
			doBeforeSearch(branchPath, searcher);
			
			final Query query = createQuery(expression);
	
			final int maxDoc = indexService.maxDoc(branchPath);
			final DocIdCollector collector = DocIdCollector.create(maxDoc);
			doSearch(branchPath, query, collector);
	
			final DocIdsIterator itr = collector.getDocIDs().iterator();
			
			final Map<String, Document> componentIdDocMap = Maps.newHashMap();
			final Map<String, Collection<String>> componentIdParentComponentIdMap = Maps.newHashMap();
			
			final Map<String, E> componentMap = Maps.newHashMap();
			final Map<String, Set<String>> subTypeMap = Maps.newHashMap();
			final Map<String, Set<String>> superTypeMap = Maps.newHashMap();
			final Set<String> filteredComponents = Sets.newHashSet();
			
			final int topLevel = getTopLevelDepth();
			
			while (itr.next()) {
				
				final int docId = itr.getDocID();
				final Document doc = searcher.doc(docId);
				
				final IndexableField[] parentFields = doc.getFields(CommonIndexConstants.COMPONENT_PARENT);
				
				final Set<String> parentIds = CompareUtils.isEmpty(parentFields) 
						? Collections.<String>emptySet() 
						: Sets.<String>newLinkedHashSetWithExpectedSize(parentFields.length);
				
				for (final IndexableField field : parentFields) {
					
					parentIds.add(field.stringValue());
				}
				
				
				final String componentId = ComponentIdStringField.getString(doc);
				componentIdDocMap.put(componentId, doc);
				
				componentIdParentComponentIdMap.put(componentId, parentIds);

				filteredComponents.add(componentId);
				
			}
			
			if (monitor.isCanceled()) {
				return EmptyTerminologyBrowser.getInstance();
			}
			
			addTopLevels(branchPath, null, getRootIds(branchPath), componentMap, subTypeMap, superTypeMap, topLevel);
			
			for (final Entry<String, Document> entry : componentIdDocMap.entrySet()) {
				final String componentId = entry.getKey();
				
				processComponentForTree(branchPath, componentId, filteredComponents, componentMap, subTypeMap, superTypeMap, componentIdParentComponentIdMap, componentIdDocMap);
				
			}

			trimTopLevels(null, topLevel, subTypeMap, superTypeMap, componentMap, filteredComponents);
			
			if (monitor.isCanceled()) {
				return EmptyTerminologyBrowser.getInstance();
			}
			
			return new FilteredTerminologyBrowser<E, String>(componentMap, subTypeMap, superTypeMap, FilterTerminologyBrowserType.HIERARCHICAL, filteredComponents);
			
			
		} catch (final IOException e) {
			throw new IndexException("Error while building taxonomy.", e);
		} finally {
			if (searcher != null)
				try {
					manager.release(searcher);
				} catch (final IOException e) {
					throw new IndexException(e);
				}
		}
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
	protected IndexServerService<E> getIndexService() {
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
	
	private void processComponentForTree(final IBranchPath branchPath, final String componentId, 
			final Set<String> filteredComponents, final Map<String, E> componentMap, final Map<String, Set<String>> subTypeMap,  final Map<String, Set<String>> superTypeMap, 
			final Map<String, Collection<String>> componentIdParentComponentIdMap, final Map<String, Document> componentIdDocMap) {

		//check for already processed concepts
		if (componentMap.containsKey(componentId)) {
			return;
		}
		
		final Document doc = componentIdDocMap.get(componentId);
		componentMap.put(componentId, terminologyBrowser.createResultObject(branchPath, doc));
		
		Collection<String> superTypeIds = componentIdParentComponentIdMap.get(componentId);
		
		if (null == superTypeIds) {
			
			superTypeIds = terminologyBrowser.getSuperTypeIds(branchPath, componentId);
			componentIdParentComponentIdMap.put(componentId, superTypeIds);
			
		}
		
		
		processConceptSuperTypes(branchPath, componentId, superTypeIds, filteredComponents, componentMap, subTypeMap, superTypeMap, componentIdParentComponentIdMap, componentIdDocMap);
		
	}
	
	
	private void processConceptSuperTypes(final IBranchPath branchPath, final String componentId, @Nonnull final Collection<String> superTypeIds, final Set<String> filteredComponents, final Map<String, E> componentMap, final Map<String, Set<String>> subTypeMap,  final Map<String, Set<String>> superTypeMap, 
			final Map<String, Collection<String>> componentIdParentComponentIdMap, final Map<String, Document> componentIdDocMap) {


		if (superTypeIds.isEmpty()) {
			return;
		}
		
		for (final String parentId : superTypeIds) {

			if (filteredComponents.contains(parentId)) {
				processComponentForTree(branchPath, parentId, filteredComponents, componentMap, subTypeMap, superTypeMap, componentIdParentComponentIdMap, componentIdDocMap);
			}

			if (componentMap.containsKey(parentId)) {
				
				put(subTypeMap, parentId, componentId);
				put(superTypeMap, componentId, parentId);

				continue;
			}

			Collection<String> parentSuperTypeIds = componentIdParentComponentIdMap.get(parentId);
			
			if (null == parentSuperTypeIds) {
				
				parentSuperTypeIds = terminologyBrowser.getSuperTypeIds(branchPath, parentId);
				componentIdParentComponentIdMap.put(parentId, parentSuperTypeIds);
				
			}
			
			
			processConceptSuperTypes(branchPath, componentId, parentSuperTypeIds, filteredComponents, componentMap, subTypeMap, superTypeMap, componentIdParentComponentIdMap, componentIdDocMap);
		}
	}

	private boolean trimTopLevels(final String conceptId, final int level, final Map<String, Set<String>> subTypeMap,  final Map<String, Set<String>> superTypeMap, final Map<String, E> componentMap, final Set<String> filteredComponents) {

		// go down recursively to the specified levels
		if (level >= 1) {
			final Set<String> subTypeIds = subTypeMap.get(conceptId);
			if (!CompareUtils.isEmpty(subTypeIds)) {
				final Iterator<String> it = subTypeIds.iterator();
				while (it.hasNext()) {
					final String id = it.next();
					if (trimTopLevels(id, level - 1, subTypeMap, superTypeMap, componentMap, filteredComponents)) {
						it.remove();
					}
				}
			}
		}

		if (conceptId != null) {
			final Set<String> subTypeIds = subTypeMap.get(conceptId);
			if (CompareUtils.isEmpty(subTypeIds) && !filteredComponents.contains(conceptId)) {
				superTypeMap.remove(conceptId);
				componentMap.remove(conceptId);
				return true;
			}
		}
		return false;
	}
	
	private void addTopLevels(final IBranchPath branchPath, final String parentId, final Collection<String> subTypeIds, 
			final Map<String, E> componentMap, final Map<String, Set<String>> subTypeMap,  final Map<String, Set<String>> superTypeMap, final int level) {
		
		
		if (level < 1) {
			return;
		}
		
		for (final String subTypeId : subTypeIds) {
			
			final E subType = terminologyBrowser.getConcept(branchPath, subTypeId);
			
			componentMap.put(subTypeId, subType);
			put(subTypeMap, parentId, subTypeId);
			
			if (parentId != null) {
				put(superTypeMap, subTypeId, parentId);
			}
			
			addTopLevels(branchPath, subTypeId, terminologyBrowser.getSubTypeIds(branchPath, subTypeId), componentMap, subTypeMap, superTypeMap, level - 1);
		}
	}
	
	private void put(final Map<String, Set<String>> map, final String key, final String value) {
		Set<String> values = map.get(key);
		
		if (values == null) {
		
			values = Sets.newHashSet();
			map.put(key, values);
			
		} else {
			
			if (values.contains(value)) {
				return;
			}
			
		}
		
		values.add(value);
	}
}