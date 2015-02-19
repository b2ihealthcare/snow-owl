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

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.BytesRef;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.index.IGroupingIndexQueryAdapter;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.core.api.index.IIndexQueryAdapter;
import com.b2international.snowowl.core.api.index.IIndexService;
import com.google.common.base.Function;
import com.google.common.collect.Multimap;

/**
 * Abstract superclass for {@link IIndexService} implementations which provides a Lucene-specific (non-API) interface and routes
 * all search requests through the supplied query adapter.
 * 
 * @param E the {@link IIndexEntry} subtype this index service uses
 * 
 */
public abstract class AbstractIndexService<E extends IIndexEntry> implements IIndexService<E> {

	@Override
	public final <E2 extends E> List<E2> search(final IBranchPath branchPath, final IIndexQueryAdapter<E2> queryAdapter) {
		return queryAdapter.search(this, branchPath);
	}

	@Override
	public final <E2 extends E> List<E2> search(final IBranchPath branchPath, final IIndexQueryAdapter<E2> queryAdapter, final int limit) {
		return queryAdapter.search(this, branchPath, limit);
	}
	
	@Override
	public final <E2 extends E> List<E2> search(final IBranchPath branchPath, final IIndexQueryAdapter<E2> queryAdapter, final int offset, final int limit) {
		return queryAdapter.search(this, branchPath, offset, limit);
	}

	@Override
	public <E2 extends E> Collection<E2> searchUnsorted(final IBranchPath branchPath, final IIndexQueryAdapter<E2> queryAdapter) {
		return queryAdapter.searchUnsorted(this, branchPath);
	}
	
	@Override
	public <E2 extends E> Collection<String> searchUnsortedIds(final IBranchPath branchPath, final IIndexQueryAdapter<E2> queryAdapter) {
		return queryAdapter.searchUnsortedIds(this, branchPath);
	}

	@Override
	public <E2 extends E, G> Multimap<G, String> searchUnsortedIdGroups(final IBranchPath branchPath, final IGroupingIndexQueryAdapter<E2, G> queryAdapter) {
		return queryAdapter.searchUnsortedIdGroups(this, branchPath);
	}

	@Override
	public final <E2 extends E> int getHitCount(final IBranchPath branchPath, final IIndexQueryAdapter<E2> queryAdapter) {
		return queryAdapter.getHitCount(this, branchPath);
	}
	
	/**
	 * (non-API)
	 * 
	 * @param branchPath
	 * @param query
	 * @param filter
	 * @param sort
	 * @param limit
	 * 
	 * @return
	 */
	public abstract List<DocumentWithScore> search(final IBranchPath branchPath, final Query query, final Filter filter, 
			final Sort sort, int limit);
	
	/**
	 * (non-API)
	 * 
	 * @param branchPath
	 * @param query
	 * @param filter
	 * @param sort
	 * @param offset
	 * @param limit
	 * 
	 * @return
	 */
	public abstract List<DocumentWithScore> search(final IBranchPath branchPath, final Query query, final Filter filter, 
			final Sort sort, int offset, int limit);
	
	/**
	 * (non-API)
	 * 
	 * @param branchPath
	 * @param query
	 * @param filter
	 * 
	 * @return
	 */
	public abstract Collection<DocumentWithScore> searchUnordered(final IBranchPath branchPath, final Query query, final Filter filter);

	/**
	 * (non-API)
	 * 
	 * @param branchPath
	 * @param query
	 * @param filter
	 * 
	 * @return
	 */
	public abstract int getHitCount(final IBranchPath branchPath, final Query query, final Filter filter);
	
	/**
	 * (non-API)
	 * 
	 * @param branchPath
	 * @param docId
	 * @param fieldsToLoad
	 * @return
	 */
	public abstract Document document(final IBranchPath branchPath, final int docId, final Set<String> fieldsToLoad);

	/**
	 * (non-API)
	 * 
	 * @param searcher
	 * @param docId
	 * @param fieldsToLoad
	 */
	public abstract Document document(final IndexSearcher searcher, final int docId, final Set<String> fieldsToLoad); 
	
	/**
	 * (non-API)
	 * 
	 * @param branchPath
	 * @param query
	 * @return
	 */
	public abstract int getTotalHitCount(final IBranchPath branchPath, final Query query);
	
	/**
	 * (non-API)
	 * 
	 * @param branchPath
	 * @param query
	 * @param limit
	 * @return
	 */
	public abstract TopDocs search(final IBranchPath branchPath, final Query query, final int limit);
	
	/**
	 * (non-API)
	 * 
	 * @param branchPath
	 * @return
	 */
	public abstract int maxDoc(final IBranchPath branchPath);
	
	/**
	 * (non-API)
	 * 
	 * @param branchPath
	 * @param query
	 * @param collector
	 * @return
	 */
	public abstract Collector search(final IBranchPath branchPath, final Query query, final Collector collector);
	
	/**
	 * (non-API)
	 * 
	 * @param branchPath
	 * @param query
	 * @param filter
	 * @param collector
	 * @return
	 */
	public abstract Collector search(final IBranchPath branchPath, final Query query, final Filter filter, final Collector collector);
	
	/**
	 * (non-API)
	 * 
	 * @param branchPath
	 * @param document
	 */
	public abstract void addDocument(final IBranchPath branchPath, final Document document);

	/**
	 * (non-API)
	 * 
	 * @param branchPath
	 * @param createQuery
	 * @param createFilter
	 * @return
	 */
	public abstract Collection<String> searchUnorderedIds(final IBranchPath branchPath, final Query createQuery, final Filter createFilter);
	
	/**
	 * (non-API)
	 * 
	 * @param branchPath
	 * @param query
	 * @param filter
	 * @param groupField
	 * @param valueFields
	 * @param groupFieldConverter 
	 * @return
	 */
	public abstract <T> Multimap<T, String> searchUnorderedIdGroups(final IBranchPath branchPath, final Query query, @Nullable final Filter filter, 
			final String groupField, 
			final Set<String> valueFields,
			final Function<BytesRef, T> groupFieldConverter);
	
	/**
	 * (non-API)
	 * 
	 * @param branchPath
	 * @param query
	 * @param filter
	 * @param sort
	 * @param limit
	 * @return
	 */
	public abstract List<String> searchIds(final IBranchPath branchPath, final Query query, final @Nullable Filter filter, final @Nullable Sort sort, final int limit);
	
	/**
	 * (non-API)
	 * 
	 * @param branchPath
	 * @return
	 */
	public abstract ReferenceManager<IndexSearcher> getManager(final IBranchPath branchPath);
}