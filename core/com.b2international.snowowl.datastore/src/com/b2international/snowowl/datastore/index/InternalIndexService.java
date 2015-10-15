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
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.core.api.index.IIndexService;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

/**
 * Internal interface allowing access to implementation-specific constructs in {@link IIndexService}s.
 * 
 * @param E the {@link IIndexEntry} subtype this index service uses
 */
public interface InternalIndexService<E extends IIndexEntry> extends IIndexService<E> {

	List<DocumentWithScore> search(IBranchPath branchPath, Query query, Filter filter, 
			Sort sort, 
			int limit);

	List<DocumentWithScore> search(IBranchPath branchPath, Query query, Filter filter, 
			Sort sort, 
			int offset, 
			int limit);

	Collection<DocumentWithScore> searchUnordered(IBranchPath branchPath, Query query, Filter filter);

	int getTotalHitCount(IBranchPath branchPath, Query query);

	Collector search(IBranchPath branchPath, Query query, Collector collector);

	Collector search(IBranchPath branchPath, Query query, Filter filter, Collector collector);

	List<String> searchIds(IBranchPath branchPath, Query query, Filter filter, Sort sort, int limit);

	Collection<String> searchUnorderedIds(IBranchPath branchPath, Query createQuery, Filter createFilter);

	<T> Multimap<T, String> searchUnorderedIdGroups(IBranchPath branchPath, Query query, Filter filter, 
			String groupField, 
			Set<String> valueFields,
			Function<BytesRef, T> groupFieldConverter);

	ImmutableMultimap<String, DocumentWithScore> multiSearch(IBranchPath branchPath, ImmutableMap<String, Query> namedQueries, 
			Sort sort, 
			int limit);

	<T> T executeReadTransaction(IBranchPath branchPath, IndexRead<T> read);

	boolean hasDocuments(IBranchPath branchPath);

	@Deprecated
	int getHitCount(IBranchPath branchPath, Query query, Filter filter);

	@Deprecated
	Document document(IBranchPath branchPath, int docId, Set<String> fieldsToLoad);

	@Deprecated
	Document document(IndexSearcher searcher, int docId, Set<String> fieldsToLoad);

	@Deprecated
	ReferenceManager<IndexSearcher> getManager(IBranchPath branchPath);

	@Deprecated
	TopDocs search(IBranchPath branchPath, Query query, int limit);

	@Deprecated
	int maxDoc(IBranchPath branchPath);
}
