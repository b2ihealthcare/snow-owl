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
package com.b2international.snowowl.datastore.index;

import static com.b2international.commons.ClassUtils.checkAndCast;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.core.api.index.IIndexQueryAdapter;
import com.b2international.snowowl.core.api.index.IIndexService;
import com.b2international.snowowl.core.api.index.IndexException;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.google.common.collect.ImmutableSet;

/**
 * Abstract base implementation of the {@link IIndexQueryAdapter} interface.
 * <p>
 * Additional features:
 * <ul>
 * <li>{@link QueryDslIndexQueryAdapter#createQuery() <em>Creates the query to execute.</em>}</li>
 * <li>{@link QueryDslIndexQueryAdapter#createFilter() <em>Creates the filter for the index query.</em>}</li>
 * <li>{@link QueryDslIndexQueryAdapter#createSort() <em>Creates the sort for the index query.</em>}</li>
 * <li>{@link QueryDslIndexQueryAdapter#buildSearchResult(Document, float) <em>Builds the result from the matching index document.</em>}</li>
 * </ul>
 * 
 * @param E the {@link IIndexEntry} subtype this index service uses
 * 
 * @see IIndexQueryAdapter
 * 
 */
public abstract class QueryDslIndexQueryAdapter<E extends IIndexEntry> extends AbstractIndexQueryAdapter<E> {

	private static final long serialVersionUID = -2055946390891747633L;
	
	protected final String[] componentIds;

	protected QueryDslIndexQueryAdapter(final @Nullable String searchString, final int searchFlags, final @Nullable String[] componentIds) {
		super(searchString, searchFlags);
		this.componentIds = componentIds;
	}

	@Override
	public void validate() throws IndexException {
		createQuery();
	}
	
	/**
	 * Creates the query that has to executed against the index service.
	 * 
	 * @return the query to execute
	 */
	public Query createQuery() {
		return createIndexQueryBuilder().toQuery();
	}

	@Nullable
	protected Filter createFilter() {
		return (componentIds != null) ? Mappings.id().createTermsFilter(ImmutableSet.copyOf(componentIds)) : null;
	}

	/**
	 * Creates the sort for the current index query adapter.
	 * <p>
	 * Returns with {@code null} by default. Clients may override to add different behavior.
	 * 
	 * @return the sort for the index query
	 */
	protected @Nullable Sort createSort() {
		return null;
	}

	/**
	 * Creates a {@link IndexQueryBuilder query builder}. Subclasses may override and call
	 * {@code super.createIndexQueryBuilder()} to build upon a parent implementation.
	 * 
	 * @return an {@link IndexQueryBuilder} that combines the search expression with some flags to produce a Lucene query
	 */
	@OverridingMethodsMustInvokeSuper
	protected IndexQueryBuilder createIndexQueryBuilder() {
		return new IndexQueryBuilder();
	}

	@Override
	protected List<DocumentWithScore> doSearch(final IIndexService<? super E> indexService, final IBranchPath branchPath, final int limit) {
		final AbstractIndexService<?> abstractIndexService = checkAndCast(indexService, AbstractIndexService.class); 
		final List<DocumentWithScore> documents = abstractIndexService.search(branchPath, createQuery(), createFilter(), createSort(), limit);
		return documents;
	}
	
	@Override
	protected List<DocumentWithScore> doSearch(final IIndexService<? super E> indexService, final IBranchPath branchPath, final int offset, final int limit) {
		final AbstractIndexService<?> abstractIndexService = checkAndCast(indexService, AbstractIndexService.class); 
		final List<DocumentWithScore> documents = abstractIndexService.search(branchPath, createQuery(), createFilter(), createSort(), offset, limit);
		return documents;
	}
	
	@Override
	public Collection<DocumentWithScore> doSearchUnsorted(final IIndexService<? super E> indexService, final IBranchPath branchPath) {
		final AbstractIndexService<?> abstractIndexService = checkAndCast(indexService, AbstractIndexService.class); 
		return abstractIndexService.searchUnordered(branchPath, createQuery(), createFilter());
	}
	
	@Override
	protected Collection<String> doSearchUnsortedIds(final IIndexService<? super E> indexService, final IBranchPath branchPath) {
		final AbstractIndexService<?> abstractIndexService = checkAndCast(indexService, AbstractIndexService.class); 
		return abstractIndexService.searchUnorderedIds(branchPath, createQuery(), createFilter());
	}
	
	@Override
	public int getHitCount(final IIndexService<? super E> indexService, final IBranchPath branchPath) {
		return checkAndCast(indexService, AbstractIndexService.class).getHitCount(branchPath, createQuery(), createFilter());
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.index.AbstractIndexQueryAdapter#doSearchIds(com.b2international.snowowl.core.api.index.IIndexService, com.b2international.snowowl.core.api.IBranchPath, int)
	 */
	@Override
	protected List<String> doSearchIds(final IIndexService<? super E> indexService, final IBranchPath branchPath, final int limit) {
		final AbstractIndexService<?> abstractIndexService = checkAndCast(indexService, AbstractIndexService.class);
		return abstractIndexService.searchIds(branchPath, createQuery(), createFilter(), createSort(), limit);
	}
	
}