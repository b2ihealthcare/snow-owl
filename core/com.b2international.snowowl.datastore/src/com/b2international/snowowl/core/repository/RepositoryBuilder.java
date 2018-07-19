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

import java.util.Collection;

import org.slf4j.Logger;

import com.b2international.index.mapping.Mappings;
import com.b2international.index.revision.Hooks;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.RepositoryInfo.Health;
import com.b2international.snowowl.core.domain.RepositoryContextProvider;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.datastore.CodeSystemEntry;
import com.b2international.snowowl.datastore.CodeSystemVersionEntry;
import com.b2international.snowowl.datastore.request.IndexReadRequest;
import com.b2international.snowowl.datastore.review.ConceptChanges;
import com.b2international.snowowl.datastore.review.Review;

/**
 * @since 4.5
 */
public final class RepositoryBuilder {
	
	private final String toolingId;
	private final String repositoryId;
	private final DefaultRepositoryManager manager;
	
	private int mergeMaxResults;
	private TerminologyRepositoryInitializer initializer;
	private Hooks.PreCommitHook hook;
	private final Mappings mappings = new Mappings(
		Review.class, 
		ConceptChanges.class, 
		CodeSystemEntry.class, 
		CodeSystemVersionEntry.class
	);
	private Logger log;

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
	
	public RepositoryBuilder addMappings(Collection<Class<?>> mappings) {
		mappings.forEach(this.mappings::putMapping);
		return this;
	}
	
	public RepositoryBuilder withPreCommitHook(Hooks.PreCommitHook hook) {
		this.hook = hook;
		return this;
	}
	
	public RepositoryBuilder logger(Logger log) {
		this.log = log;
		return this;
	}
	
	public Repository build(Environment env) {
		final TerminologyRepository repository = new TerminologyRepository(repositoryId, toolingId, mergeMaxResults, env, mappings, log);
		// TODO support additional service registration and terminology repository configuration via other plugins
		repository.activate();
		repository.service(RevisionIndex.class).hooks().addHook(hook);
		manager.put(repositoryId, repository);
		
		// execute initialization steps
		if (repository.health() == Health.GREEN) {
			new IndexReadRequest<Void>((context) -> {
				initializer.initialize(context);
				return null;
			}).execute(env.service(RepositoryContextProvider.class).get(repositoryId));
		}
		
		return repository;
	}

}
