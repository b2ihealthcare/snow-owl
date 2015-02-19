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

import com.b2international.snowowl.core.api.IBranchPath;
import com.google.common.collect.Multimap;

/**
 * An {@link IIndexQueryAdapter} which also provides grouping capabilities. 
 * 
 * @param E the {@link IIndexEntry} subtype this index service uses
 * @param G the grouping field type
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * 
 */
public interface IGroupingIndexQueryAdapter<E extends IIndexEntry, G> extends IIndexQueryAdapter<E> {

	/**
	 * Performs a search and groups the collected identifiers by a predefined field (determined by the query adapter
	 * implementation).
	 * 
	 * @param indexService the index service to use for searching (may not be {@code null})
	 * @param branchPath the branch path reference limiting visibility to a particular branch (may not be {@code null})
	 * @return a {@link Multimap} of entries mapping converted grouping field values to identifiers, or an empty
	 * Multimap if no search results could be found. Search results which don't have any particular group value are
	 * registered under the {@code null} key.
	 */
	public Multimap<G, String> searchUnsortedIdGroups(final IIndexService<? super E> indexService, final IBranchPath branchPath);
}