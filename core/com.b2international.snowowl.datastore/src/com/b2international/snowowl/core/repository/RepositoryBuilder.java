/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.domain.RepositoryContextProvider;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.datastore.request.IndexReadRequest;

/**
 * @since 4.5
 */
public final class RepositoryBuilder {
	
	private final String toolingId;
	private final String repositoryId;
	private final DefaultRepositoryManager manager;
	
	private int mergeMaxResults;
	private TerminologyRepositoryInitializer initializer;

	RepositoryBuilder(DefaultRepositoryManager defaultRepositoryManager, String repositoryId, String toolingId) {
		this.manager = defaultRepositoryManager;
		this.repositoryId = repositoryId;
		this.toolingId = toolingId;
	}

	public RepositoryBuilder setMergeMaxResults(int mergeMaxResults) {
		this.mergeMaxResults = mergeMaxResults;
		return this;
	}
	
	public RepositoryBuilder withInitializer(TerminologyRepositoryInitializer initializer) {
		this.initializer = initializer;
		return this;
	}
	
	public Repository build(Environment env) {
		final TerminologyRepository repository = new TerminologyRepository(repositoryId, toolingId, mergeMaxResults, env);
		// TODO support additional service registration and terminology repository configuration via other plugins
		repository.activate();
		manager.put(repositoryId, repository);
		
		// execute initialization steps
		new IndexReadRequest<Void>((context) -> {
			initializer.initialize(context);
			return null;
		}).execute(env.service(RepositoryContextProvider.class).get(repositoryId));
		
		return repository;
	}

}
