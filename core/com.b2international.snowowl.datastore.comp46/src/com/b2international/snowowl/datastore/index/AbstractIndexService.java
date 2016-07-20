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

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.index.IGroupingIndexQueryAdapter;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.core.api.index.IIndexQueryAdapter;
import com.b2international.snowowl.core.api.index.IIndexService;
import com.google.common.collect.Multimap;

/**
 * Abstract superclass for {@link IIndexService} implementations which provides a Lucene-specific (non-API) interface and routes
 * all search requests through the supplied query adapter.
 * 
 * @param E the {@link IIndexEntry} subtype this index service uses
 */
public abstract class AbstractIndexService<E extends IIndexEntry> implements InternalIndexService<E> {

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
	public final <E2 extends E> Collection<E2> searchUnsorted(final IBranchPath branchPath, final IIndexQueryAdapter<E2> queryAdapter) {
		return queryAdapter.searchUnsorted(this, branchPath);
	}

	@Override
	public final <E2 extends E> Collection<String> searchUnsortedIds(final IBranchPath branchPath, final IIndexQueryAdapter<E2> queryAdapter) {
		return queryAdapter.searchUnsortedIds(this, branchPath);
	}

	@Override
	public final <E2 extends E, G> Multimap<G, String> searchUnsortedIdGroups(final IBranchPath branchPath, final IGroupingIndexQueryAdapter<E2, G> queryAdapter) {
		return queryAdapter.searchUnsortedIdGroups(this, branchPath);
	}

	@Override
	public final <E2 extends E> int getHitCount(final IBranchPath branchPath, final IIndexQueryAdapter<E2> queryAdapter) {
		return queryAdapter.getHitCount(this, branchPath);
	}
}
