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

import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.core.api.index.IIndexServerServiceManager;
import com.b2international.snowowl.core.api.index.IIndexUpdater;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * {@link IIndexServerServiceManager} implementation.
 */
public enum IndexServerServiceManager implements IIndexServerServiceManager {

	INSTANCE;
	
	@Override
	@SuppressWarnings("unchecked")
	public <E extends IIndexEntry> IIndexUpdater<E> getByUuid(final String repositoryUuid) {
		return checkNotNull(Iterables.tryFind(getServices(), new Predicate<IIndexUpdater>() {
			@Override
			public boolean apply(IIndexUpdater input) {
				return repositoryUuid.equals(input.getRepositoryUuid());
			}
		}).orNull(), "Cannot find index service for repository: %s", repositoryUuid);
	}
	
	private Iterable<IIndexUpdater> getServices() {
		return ApplicationContext.getInstance().getServices(IIndexUpdater.class);
	}
	
}
