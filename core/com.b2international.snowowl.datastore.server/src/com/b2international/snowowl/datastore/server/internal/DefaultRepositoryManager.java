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
package com.b2international.snowowl.datastore.server.internal;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.RepositoryManager;
import com.google.common.collect.ImmutableList;

/**
 * @since 4.5
 */
public final class DefaultRepositoryManager implements RepositoryManager {

	private final ConcurrentMap<String, Repository> repositories = new ConcurrentHashMap<>();
	
	@Override
	public Repository get(String repositoryId) {
		return repositories.get(repositoryId);
	}

	@Override
	public Collection<Repository> repositories() {
		return ImmutableList.copyOf(repositories.values());
	}
	
	public RepositoryBuilder prepareCreate(String repositoryId) {
		return new RepositoryBuilder(repositoryId);
	}

}
