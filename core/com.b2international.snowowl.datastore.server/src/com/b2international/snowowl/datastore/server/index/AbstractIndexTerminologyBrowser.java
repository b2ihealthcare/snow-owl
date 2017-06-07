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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.api.ExtendedComponent;
import com.b2international.snowowl.core.api.ExtendedComponentImpl;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IComponentWithChildFlag;
import com.b2international.snowowl.core.api.browser.IFilterClientTerminologyBrowser;
import com.b2international.snowowl.core.api.browser.ITerminologyBrowser;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.core.api.index.IIndexService;
import com.b2international.snowowl.core.api.index.IndexException;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.index.DocIdCollector;
import com.b2international.snowowl.datastore.index.DocIdCollector.DocIdsIterator;
import com.b2international.snowowl.datastore.index.IndexQueryBuilder;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.index.mapping.FieldsToLoadBuilderBase.FieldsToLoadBuilder;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Abstract superclass for index based terminology browsers.
 * 
 */
public abstract class AbstractIndexTerminologyBrowser<E extends IIndexEntry> extends AbstractIndexBrowser<E> implements ITerminologyBrowser<E, String> {

	public AbstractIndexTerminologyBrowser(final IIndexService<?> service) {
		super(service);
	}

	@Override
	public final ExtendedComponent getExtendedComponent(IBranchPath branchPath, long storageKey) {
		checkNotNull(branchPath, "branchPath");
		checkArgument(storageKey > CDOUtils.NO_STORAGE_KEY);
		
		final TopDocs topDocs = service.search(branchPath, Mappings.newQuery().storageKey(storageKey).matchAll(), 1);
		
		if (IndexUtils.isEmpty(topDocs)) {
			return null;
		}
		
		final Document doc = service.document(branchPath, topDocs.scoreDocs[0].doc, getExtendedComponentFieldsToLoad());
		return convertDocToExtendedComponent(branchPath, doc);
	}

	protected ExtendedComponent convertDocToExtendedComponent(final IBranchPath branchPath, final Document doc) {
		return new ExtendedComponentImpl(
				Mappings.id().getValue(doc), 
				Mappings.label().getValue(doc), 
				Mappings.iconId().getValue(doc), 
				Mappings.type().getShortValue(doc));
	}
	
	protected Set<String> getExtendedComponentFieldsToLoad() {
		return Mappings.fieldsToLoad().id().type().label().iconId().build();
	}
	
	@Override
	protected Set<String> getFieldNamesToLoad() {
		final FieldsToLoadBuilder defaultFieldsToLoad = Mappings.fieldsToLoad().id().label().iconId().storageKey().parent();
		addAdditionalFieldsToLoad(defaultFieldsToLoad);
		return defaultFieldsToLoad.build();
	}
	
	protected void addAdditionalFieldsToLoad(FieldsToLoadBuilder fieldsToLoad) {
	}

	@Override
	public long getStorageKey(final IBranchPath branchPath, final String conceptId) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		checkNotNull(conceptId, "Concept ID argument cannot be null.");
		final Query query = Mappings.newQuery()
				.and(getTerminologyComponentTypeQuery())
				.id(conceptId)
				.matchAll();
		return getStorageKey(branchPath, query);
	}

	protected long getStorageKey(final IBranchPath branchPath, final Query query) {
		// FIXME: service.search and service.document in separate calls
		final TopDocs topDocs = service.search(branchPath, query, 1);
		
		if (IndexUtils.isEmpty(topDocs)) {
			return CDOUtils.NO_STORAGE_KEY;
		}
		
		final Document doc = service.document(branchPath, topDocs.scoreDocs[0].doc, Mappings.fieldsToLoad().storageKey().build());
		return Mappings.storageKey().getValue(doc);
	}	
		
	@Override
	public Collection<E> getRootConcepts(final IBranchPath branchPath) {
		checkNotNull(branchPath, "Branch path must not be null.");
		final List<E> rootConcepts = Lists.newArrayList();
		// TODO: maybe this could become a cached filter, since the search criteria don't change
		final Query query = getRootConceptsQuery();
		final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
		service.search(branchPath, query, collector);
		DocIdsIterator iterator;
		try {
			iterator = collector.getDocIDs().iterator();
			while (iterator.next()) {
				final Document doc = service.document(branchPath, iterator.getDocID(), getFieldNamesToLoad());
				final E component = createResultObject(branchPath, doc);
				rootConcepts.add(component);
			}
		} catch (final IOException e) {
			throw new IndexException("Error when querying root concepts.", e);
		}
		return rootConcepts;
	}

	@Override
	public Collection<String> getRootConceptIds(final IBranchPath branchPath) {
		checkNotNull(branchPath, "Branch path must not be null.");
		final List<String> rootConceptIds = Lists.newArrayList();
		// TODO: maybe this could become a cached filter, since the search criteria don't change
		final Query query = getRootConceptsQuery();
		final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
		service.search(branchPath, query, collector);
		try {
			final DocIdsIterator iterator = collector.getDocIDs().iterator();
			while (iterator.next()) {
				final Document doc = service.document(branchPath, iterator.getDocID(), Mappings.fieldsToLoad().id().build());
				rootConceptIds.add(Mappings.id().getValue(doc));
			}
		} catch (final IOException e) {
			throw new IndexException("Error when querying root concepts.", e);
		}
		return rootConceptIds;
	}
	
	protected Query getRootConceptsQuery() {
		return new IndexQueryBuilder()
			.require(Mappings.parent().toQuery(Mappings.ROOT_ID_STRING))
			.require(getRootTerminologyComponentTypeQuery()).toQuery();
	}

	@Override
	public Collection<String> getSuperTypeIds(final IBranchPath branchPath, final String componentId) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		checkNotNull(componentId, "Component ID argument cannot be null.");
		final TopDocs topDocs = service.search(branchPath, getConceptByIdQueryBuilder(componentId), 1);
		if (CompareUtils.isEmpty(topDocs.scoreDocs)) {
			return Collections.emptyList();
		}
		final Document document = service.document(branchPath, topDocs.scoreDocs[0].doc, Mappings.fieldsToLoad().parent().build());
		return Mappings.parent().getValues(document);
	}
	
	@Override
	public E getConcept(final IBranchPath branchPath, final String conceptId) {
		checkNotNull(branchPath, "Branch path must not be null.");
		checkNotNull(conceptId, "conceptId");
		checkState(!conceptId.isEmpty(), "conceptId is empty.");
		
		return getConcept(branchPath, getConceptByIdQueryBuilder(conceptId));
	}

	protected Query getConceptByIdQueryBuilder(final String conceptId) {
		return Mappings.newQuery().id(conceptId).matchAll();
	}

	@Override
	public Collection<E> getSuperTypesById(final IBranchPath branchPath, final String id) {
		checkNotNull(branchPath, "Branch path must not be null.");
		checkNotNull(id, "ID must not be null.");
		
		final TopDocs topDocs = service.search(branchPath, getConceptByIdQueryBuilder(id), 1);
		if (CompareUtils.isEmpty(topDocs.scoreDocs)) {
			return Collections.emptyList();
		}
		final Document document = service.document(branchPath, topDocs.scoreDocs[0].doc, Mappings.fieldsToLoad().parent().build());
		final Collection<String> parents = Mappings.parent().getValues(document);
		final Builder<E> builder = ImmutableList.builder();
		for (final String parent : parents) {
			builder.add(getConcept(branchPath, parent));
		}
		return builder.build();
	}

	@Override
	public Collection<E> getSubTypesById(final IBranchPath branchPath, final String id) {
		checkNotNull(branchPath, "Branch path must not be null.");
		checkNotNull(id, "ID must not be null.");
		
		final Query query = getSubTypesQuery(id);
		try {
			final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
			service.search(branchPath, query, collector);
			final DocIdsIterator docIdsIterator = collector.getDocIDs().iterator();
			return createResultObjects(branchPath, docIdsIterator);
		} catch (final IOException e) {
			throw new RuntimeException("Error when retrieving sub types of " + id + ".", e);
		}
	}

	protected Collection<String> getSubTypeIds(final IBranchPath branchPath, final String id) {
		checkNotNull(branchPath, "Branch path must not be null.");
		checkNotNull(id, "ID must not be null.");
		
		final Query query = getSubTypesQuery(id);
		final Set<String> subTypeIds = Sets.newHashSet();
		
		try {
			
			final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
			service.search(branchPath, query, collector);
			final DocIdsIterator iterator = collector.getDocIDs().iterator();
			
			while (iterator.next()) {
				final Document doc = service.document(branchPath, iterator.getDocID(), Mappings.fieldsToLoad().id().build());
				subTypeIds.add(Mappings.id().getValue(doc));
			}
			
			return subTypeIds;
			
		} catch (final IOException e) {
			throw new RuntimeException("Error when retrieving sub types of " + id + ".", e);
		}
	}
	
	protected Query getSubTypesQuery(final String id) {
		return Mappings.newQuery()
				.and(getTerminologyComponentTypeQuery())
				.parent(id)
				.matchAll();
	}
	
	abstract protected short getConceptTerminologyComponentId();

	/**
	 * Returns with the query that should be performed when building the filtered terminology browser.
	 * @param expression the search expression.
	 * @return the query.
	 */
	protected abstract Query createFilterTerminologyBrowserQuery(final String expression);

	@Override
	public int getSubTypeCountById(final IBranchPath branchPath, final String id) {
		checkNotNull(branchPath, "Branch path must not be null.");
		checkNotNull(id, "ID must not be null.");
		return getQueryResultCount(branchPath, getSubTypesQuery(id));
	}
	
	@Override
	public IFilterClientTerminologyBrowser<E, String> filterTerminologyBrowser(final IBranchPath branchPath, @Nullable final String expression, @Nullable final IProgressMonitor monitor) {
		final TerminologyBrowserFilter<E> terminologyBrowserFilter = new TerminologyBrowserFilter<E>(this, service);
		return terminologyBrowserFilter.filterTerminologyBrowser(branchPath, expression, monitor);
	}

	@Override
	public Collection<IComponentWithChildFlag<String>> getSubTypesWithChildFlag(final IBranchPath branchPath, final E concept) {
		// get direct subtypes
		final Collection<E> subTypes = getSubTypesById(branchPath, concept.getId());
		final List<IComponentWithChildFlag<String>> results = Lists.newArrayList();
		for (final E subTypeIndexEntry : subTypes) {
			results.add(createComponentWithChildFlag(subTypeIndexEntry, getSubTypeCountById(branchPath, subTypeIndexEntry.getId()) > 0));
		}
		return results;		
	}

	@Override
	public boolean exists(final IBranchPath branchPath, final String componentId) {
		
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		Preconditions.checkNotNull(componentId, "Component ID argument cannot be null.");
		
		final BooleanQuery query = new BooleanQuery(true);
		query.add(getComponentIdQuery(componentId), Occur.MUST);
		query.add(getTerminologyComponentTypeQuery(), Occur.MUST);

		return exists(branchPath, query);
	}

	protected boolean exists(final IBranchPath branchPath, final Query query) {
		return service.getTotalHitCount(branchPath, query) > 0;
	}
	
	@Override
	public Map<String, Boolean> exist(final IBranchPath branchPath, final Collection<String> componentIds) {
		final Map<String, Boolean> result = Maps.newHashMap();
		for (final String componentId : componentIds) {
			result.put(componentId, exists(branchPath, componentId));
		}
		return result;
	}

	// Even when we accept multiple component types, we only want to display root concepts from a particular type -- see Icd10AmServerTerminologyBrowser
	private Query getRootTerminologyComponentTypeQuery() {
		return getDefaultTerminologyComponentTypeQuery();
	}
	
	/**Returns with the query that will be run against the application specific terminology component ID of the component.*/
	protected Query getTerminologyComponentTypeQuery() {
		return getDefaultTerminologyComponentTypeQuery();
	}

	// The default implementation restricts the query to a single component type only
	private Query getDefaultTerminologyComponentTypeQuery() {
		return Mappings.newQuery()
				.type(getConceptTerminologyComponentId())
				.matchAll();
	}

	/**
	 * Template method for creating an {@link IComponentWithChildFlag}.
	 * 
	 * @param entry the original index entry
	 * @param hasChildren the child flag
	 */
	abstract protected IComponentWithChildFlag<String> createComponentWithChildFlag(E entry, boolean hasChildren);
}
