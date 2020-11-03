/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.repository;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.google.common.collect.ImmutableList;

/**
 * @since 4.5
 */
public final class DefaultRepositoryManager implements RepositoryManager {

	private static final Logger LOG = LoggerFactory.getLogger(RepositoryManager.class);
	private final ConcurrentMap<String, Repository> repositories = new ConcurrentHashMap<>();
	private AtomicBoolean disposed = new AtomicBoolean(false);
	
	DefaultRepositoryManager() {
	}
	
	@Override
	public Repository get(String repositoryId) {
		checkState(!isDisposed(), "Repository Manager is not available");
		return repositories.get(repositoryId);
	}
	
	@Override
	public RepositoryContext getContext(String repositoryId) {
		final Repository repository = get(repositoryId);
		if (repository == null) {
			throw new BadRequestException("Repository '%s' is unavailable.", repositoryId);
		}
		return new DefaultRepositoryContext(repository, repository.status());
	}

	@Override
	public Collection<Repository> repositories() {
		checkState(!isDisposed(), "Repository Manager is not available");
		return ImmutableList.copyOf(repositories.values());
	}
	
	/*package*/ void put(String repositoryId, Repository repository) {
		checkNotNull(repositoryId, "repositoryId");
		checkNotNull(repository, "repository");
		repositories.put(repositoryId, repository);
	}
	
	public RepositoryBuilder prepareCreate(String repositoryId) {
		return new RepositoryBuilder(this, repositoryId);
	}
	
	@Override
	public void dispose() {
		if (disposed.compareAndSet(false, true)) {
			for (Repository repository : repositories.values()) {
				try {
					repository.dispose();
				} catch (Exception e) {
					LOG.error("Failed to close repository: " + repository.id(), e);
				}
			}
		}
	}
	
	@Override
	public boolean isDisposed() {
		return disposed.get();
	}

}
