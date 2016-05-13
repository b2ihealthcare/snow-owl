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
package com.b2international.snowowl.core.api.index;

import java.util.Collection;
import java.util.List;

import com.b2international.snowowl.core.api.IBranchPath;
import com.google.common.collect.Multimap;

/**
 * Common interface for index searchers on the server.
 * 
 * @param E the {@link IIndexEntry} subtype this index service uses
 * @deprecated - As of 4.7 release, nested index services are not supported 
 */
public interface IIndexService<E extends IIndexEntry> {
	
	/**
	 * Executes a query and returns the results.
	 * 
	 * @param branchPath the branch path reference limiting visibility to a particular branch (may not be {@code null})
	 * @param queryAdapter the adapter to use for representing a query (may not be {@code null})
	 * 
	 * @return the list of {@link IIndexEntry index entries} matching the query represented by the adapter
	 */
	public <E2 extends E> List<E2> search(final IBranchPath branchPath, final IIndexQueryAdapter<E2> queryAdapter);
	
	/**
	 * Executes a query and returns a limited number of results.
	 * 
	 * @param branchPath the branch path reference limiting visibility to a particular branch (may not be {@code null})
	 * @param queryAdapter the adapter to use for translating between UI and underlying index engine (may not be {@code null})
	 * @param limit the maximum number of results to return (may not be 0 or negative)
	 * 
	 * @return a limited list of {@link IIndexEntry index entries} matching the query represented by the adapter
	 */
	public <E2 extends E> List<E2> search(final IBranchPath branchPath, final IIndexQueryAdapter<E2> queryAdapter, final int limit);
	
	/**
	 * Executes a query and returns a limited number of results with a starting offset.
	 * 
	 * @param branchPath the branch path reference limiting visibility to a particular branch (may not be {@code null})
	 * @param queryAdapter the adapter to use for translating between UI and underlying index engine (may not be {@code null})
	 * @param offset the number of leading results to ignore (may not be negative)
	 * @param limit the maximum number of results to return (may not be 0 or negative)
	 * 
	 * @return a limited list of {@link IIndexEntry index entries} matching the query represented by the adapter
	 */
	public <E2 extends E> List<E2> search(final IBranchPath branchPath, final IIndexQueryAdapter<E2> queryAdapter, final int offset, final int limit);
	
	/**
	 * Executes a query and returns with the results.
	 * <p><b>NOTE:&nbsp;</b>The order of the matching results is undefined.</p>
	 * @param branchPath the branch path reference limiting visibility to a particular branch (may not be {@code null})
	 * @param queryAdapter the adapter to use for representing a query (may not be {@code null})
	 * @return a collection of {@link IIndexEntry index entries} representing the matching results. 
	 */
	public <E2 extends E> Collection<E2> searchUnsorted(final IBranchPath branchPath, final IIndexQueryAdapter<E2> queryAdapter);
	
	/**
	 * Executes a query and returns with IDs of the matching results.
	 * <p><b>NOTE:&nbsp;</b>The order of the matching results is undefined.</p>
	 * @param branchPath the branch path reference limiting visibility to a particular branch (may not be {@code null})
	 * @param queryAdapter the adapter to use for representing a query (may not be {@code null})
	 * @return a collection of unique IDs representing the matching {@link IIndexEntry results}. 
	 */
	public <E2 extends E> Collection<String> searchUnsortedIds(final IBranchPath branchPath, final IIndexQueryAdapter<E2> queryAdapter);
	
	/**
	 * Performs a search and groups the collected identifiers by a predefined field (determined by the query adapter
	 * implementation).
	 * 
	 * @param branchPath the branch path reference limiting visibility to a particular branch (may not be {@code null})
	 * @param queryAdapter the adapter to use for representing a grouping query (may not be {@code null})
	 * @return a {@link Multimap} of entries mapping converted grouping field values to identifiers, or an empty
	 * Multimap if no search results could be found. Search results which don't have any particular group value are
	 * registered under the {@code null} key.
	 */
	public <E2 extends E, G> Multimap<G, String> searchUnsortedIdGroups(final IBranchPath branchPath, final IGroupingIndexQueryAdapter<E2, G> queryAdapter);
	
	/**
	 * Executes a query and returns the number of matching entries.
	 * 
	 * @param branchPath the branch path reference limiting visibility to a particular branch (may not be {@code null})
	 * @param queryAdapter the adapter to use for representing a query (may not be {@code null})
	 * 
	 * @return the total number of index entries matching the specific query
	 */
	public <E2 extends E> int getHitCount(final IBranchPath branchPath, final IIndexQueryAdapter<E2> queryAdapter);
}
