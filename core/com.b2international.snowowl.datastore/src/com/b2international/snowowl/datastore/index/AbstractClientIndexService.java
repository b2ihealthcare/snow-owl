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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.List;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.index.IClientIndexService;
import com.b2international.snowowl.core.api.index.IGroupingIndexQueryAdapter;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.core.api.index.IIndexQueryAdapter;
import com.b2international.snowowl.core.api.index.IIndexService;
import com.b2international.snowowl.datastore.BranchPathAwareService;
import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;

/**
 * Abstract superclass for {@link IClientIndexService} implementations.
 * 
 * @param E the {@link IIndexEntry} subtype this index service uses
 * 
 */
public abstract class AbstractClientIndexService<E extends IIndexEntry> implements IClientIndexService<E>, BranchPathAwareService {

	private final IIndexService<E> wrappedService;
	
	/**
	 * Creates a new {@link AbstractClientIndexService} with the specified {@link IBranchPath}
	 * 
	 * @param wrappedService the wrapped {@link IIndexService} (may not be {@code null})
	 */
	public AbstractClientIndexService(final IIndexService<E> wrappedService) {
		this.wrappedService = checkNotNull(wrappedService, "wrappedService");
	}
	
	@Override
	public final <E2 extends E> List<E2> search(final IIndexQueryAdapter<E2> queryAdapter) {
		return wrappedService.search(getBranchPath(), Preconditions.checkNotNull(queryAdapter, "Query adapter argument cannot be null."));
	}

	@Override
	public final <E2 extends E> List<E2> search(final IIndexQueryAdapter<E2> queryAdapter, final int limit) {
		return wrappedService.search(getBranchPath(), Preconditions.checkNotNull(queryAdapter, "Query adapter argument cannot be null."), limit);
	}

	@Override
	public <E2 extends E> Collection<E2> searchUnsorted(final IIndexQueryAdapter<E2> queryAdapter) {
		return wrappedService.searchUnsorted(getBranchPath(), Preconditions.checkNotNull(queryAdapter, "Query adapter argument cannot be null."));
	}
	
	@Override
	public <E2 extends E> Collection<String> searchUnsortedIds(final IIndexQueryAdapter<E2> queryAdapter) {
		return wrappedService.searchUnsortedIds(getBranchPath(), Preconditions.checkNotNull(queryAdapter, "Query adapter argument cannot be null."));
	}
	
	@Override
	public <E2 extends E, G> Multimap<G, String> searchUnsortedIdGroups(final IGroupingIndexQueryAdapter<E2, G> queryAdapter) {
		return wrappedService.searchUnsortedIdGroups(getBranchPath(), Preconditions.checkNotNull(queryAdapter, "Query adapter argument cannot be null."));
	}

	@Override
	public final <E2 extends E> int getHitCount(final IIndexQueryAdapter<E2> queryAdapter) {
		return wrappedService.getHitCount(getBranchPath(), Preconditions.checkNotNull(queryAdapter, "Query adapter argument cannot be null."));
	}
	
	/**
	 * @return the wrapped index service
	 */
	public IIndexService<E> getWrappedService() {
		return wrappedService;
	}
}