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

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.b2international.snowowl.core.api.IBranchPath;

/**
 * Translates the supplied query (usually a search string and a set of flags controlling fields to match) to an index specific query
 * and converts indexed document hits to {@link IIndexEntry index entries}.
 * 
 * @param E the {@link IIndexEntry} subtype this index service uses
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * 
 */
public interface IIndexQueryAdapter<E extends IIndexEntry> extends Serializable {

	/**
	 * Accepts an index service and performs the search, also converting the index-specific output to
	 * {@link IIndexEntry} instances.
	 * 
	 * @param indexService the index service to use for searching (may not be {@code null})
	 * @param branchPath the branch path reference limiting visibility to a particular branch (may not be {@code null})
	 * 
	 * @return the list of entries matching the query represented by this adapter
	 */
	public List<E> search(final IIndexService<? super E> indexService, IBranchPath branchPath);

	/**
	 * Accepts an index service and performs the search, also converting the index-specific output to
	 * {@link IIndexEntry} instances.
	 * 
	 * @param indexService the index service to use for searching (may not be {@code null})
	 * @param branchPath the branch path reference limiting visibility to a particular branch (may not be {@code null})
	 * @param limit the maximum number of results to return (may not be 0 or negative)
	 * 
	 * @return the list of entries matching the query represented by this adapter
	 */
	public List<E> search(final IIndexService<? super E> indexService, IBranchPath branchPath, int limit);
	
	/**
	 * Accepts an index service and performs the search, also converting the index-specific output to
	 * {@link IIndexEntry} instances.
	 * 
	 * @param indexService the index service to use for searching (may not be {@code null})
	 * @param branchPath the branch path reference limiting visibility to a particular branch (may not be {@code null})
	 * @param offset the number of leading results to ignore (may not be negative)
	 * @param limit the maximum number of results to return (may not be 0 or negative)
	 * 
	 * @return the list of entries matching the query represented by this adapter
	 */
	public List<E> search(final IIndexService<? super E> indexService, IBranchPath branchPath, int offset, int limit);

	/**
	 * Executes an index query on the specified service and returns with the results.
	 * 
	 * @param indexService the index service to use for searching (may not be {@code null})
	 * @param branchPath the branch path reference limiting visibility to a particular branch (may not be {@code null})
	 * @param limit the maximum number of results to return (may not be 0 or negative)
	 * 
	 * @return an ordered list of component unique IDs representing the matching results.
	 */
	public List<String> searchIds(final IIndexService<? super E> indexService, IBranchPath branchPath, int limit);
	
	/**
	 * Executes an index query on the specified service and returns with the results.
	 * <p><b>NOTE:&nbsp;</b>The order of the matching results should undefined.</p>
	 * @param indexService the index service to use for counting matching entries (may not be {@code null})
	 * @param branchPath the branch path reference limiting visibility to a particular branch (may not be {@code null})
	 * @return a collection of {@link IIndexEntry index entries} representing the matching results. 
	 */
	public Collection<E> searchUnsorted(final IIndexService<? super E> indexService, final IBranchPath branchPath);
	
	/**
	 * Executes an index query on the specified service and returns with the results.
	 * <p><b>NOTE:&nbsp;</b>The order of the matching results should undefined.</p>
	 * @param indexService the index service to use for counting matching entries (may not be {@code null})
	 * @param branchPath the branch path reference limiting visibility to a particular branch (may not be {@code null})
	 * @return a collection of component unique IDs representing the matching results. 
	 */
	public Collection<String> searchUnsortedIds(final IIndexService<? super E> indexService, final IBranchPath branchPath);
	
	/**
	 * Accepts an index service and performs document hit counting.
	 * 
	 * @param indexService the index service to use for counting matching entries (may not be {@code null})
	 * @param branchPath the branch path reference limiting visibility to a particular branch (may not be {@code null})
	 * 
	 * @return the number of entries matching the query represented by this adapter
	 */
	public int getHitCount(final IIndexService<? super E> indexService, IBranchPath branchPath);

	/**
	 * Checks if the query encapsulated by this adapter is correct.
	 * 
	 * @throws IndexException wrapping an indexing framework-specific exception on query validation errors
	 */
	public void validate() throws IndexException;
}