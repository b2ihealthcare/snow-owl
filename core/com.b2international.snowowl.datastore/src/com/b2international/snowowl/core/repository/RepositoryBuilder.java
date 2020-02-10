/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.extension.ClassPathScanner;
import com.b2international.index.mapping.Mappings;
import com.b2international.index.revision.Hooks;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.RepositoryInfo.Health;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.merge.ComponentRevisionConflictProcessor;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.terminology.Terminology;
import com.b2international.snowowl.core.terminology.TerminologyComponent;
import com.b2international.snowowl.datastore.CodeSystemEntry;
import com.b2international.snowowl.datastore.CodeSystemVersionEntry;
import com.b2international.snowowl.datastore.request.RepositoryRequest;
import com.b2international.snowowl.datastore.review.ConceptChanges;
import com.b2international.snowowl.datastore.review.Review;
import com.b2international.snowowl.datastore.version.VersioningRequestBuilder;

/**
 * @since 4.5
 */
public final class RepositoryBuilder {
	
	private final String repositoryId;
	private final DefaultRepositoryManager manager;
	private final Logger log;
	
	private int mergeMaxResults;
	private TerminologyRepositoryInitializer initializer;
	private Hooks.PreCommitHook hook;
	private VersioningRequestBuilder versioningRequestBuilder;
	private ComponentDeletionPolicy deletionPolicy;
	
	private final Mappings mappings = new Mappings(
		Review.class, 
		ConceptChanges.class, 
		CodeSystemEntry.class, 
		CodeSystemVersionEntry.class
	);
	private final TerminologyComponents terminologyComponents;
	private ComponentRevisionConflictProcessor componentRevisionConflictProcessor;

	RepositoryBuilder(DefaultRepositoryManager manager, String repositoryId) {
		this.manager = manager;
		this.repositoryId = repositoryId;
		this.log = LoggerFactory.getLogger("repository."+repositoryId);
		this.terminologyComponents = new TerminologyComponents(this.log);
	}
	
	public Logger log() {
		return log;
	}

	public RepositoryBuilder setMergeMaxResults(int mergeMaxResults) {
		this.mergeMaxResults = mergeMaxResults;
		return this;
	}
	
	public RepositoryBuilder withInitializer(TerminologyRepositoryInitializer initializer) {
		this.initializer = initializer;
		return this;
	}
	
	public RepositoryBuilder addTerminologyComponents(List<Class<? extends IComponent>> terminologyComponents) {
		for (Class<? extends IComponent> terminologyComponent : terminologyComponents) {
			TerminologyComponent tc = Terminology.getAnnotation(terminologyComponent);
			checkNotNull(tc.docType(), "Document must be specified for terminology component: %s", terminologyComponent);
			this.terminologyComponents.add(tc);
			this.mappings.putMapping(tc.docType());
		}
		return this;
	}
	
	public RepositoryBuilder addTerminologyComponents(Map<Class<?>, TerminologyComponent> terminologyComponents) {
		for (Entry<Class<?>, TerminologyComponent> terminologyComponent : terminologyComponents.entrySet()) {
			final Class<?> docType = terminologyComponent.getKey();
			this.terminologyComponents.add(terminologyComponent.getValue());
			this.mappings.putMapping(docType);
		}
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
	
	public RepositoryBuilder withVersioningRequestBuilder(VersioningRequestBuilder versioningRequestBuilder) {
		this.versioningRequestBuilder = versioningRequestBuilder;
		return this;
	}
	
	public RepositoryBuilder withComponentDeletionPolicy(ComponentDeletionPolicy deletionPolicy) {
		this.deletionPolicy = deletionPolicy;
		return this;
	}

	public RepositoryBuilder withComponentRevisionConflictProcessor(ComponentRevisionConflictProcessor componentRevisionConflictProcessor) {
		this.componentRevisionConflictProcessor = componentRevisionConflictProcessor;
		return this;
	}
	
	public Repository build(Environment env) {
		// get all repository configuration plugins and apply them to customize the repository
		List<TerminologyRepositoryConfigurer> repositoryConfigurers = ClassPathScanner.INSTANCE.getComponentsByInterface(TerminologyRepositoryConfigurer.class)
			.stream()
			.filter(configurer -> repositoryId.equals(configurer.getRepositoryId()))
			.collect(Collectors.toList());
		
		repositoryConfigurers
			.forEach(configurer -> {
				configurer.getAdditionalMappings().forEach(mappings::putMapping);
			});
		
		final TerminologyRepository repository = new TerminologyRepository(repositoryId, mergeMaxResults, env, mappings, log);
		repository.bind(VersioningRequestBuilder.class, versioningRequestBuilder);
		repository.bind(ComponentDeletionPolicy.class, deletionPolicy);
		repository.bind(ComponentRevisionConflictProcessor.class, componentRevisionConflictProcessor);
		repository.bind(TerminologyComponents.class, terminologyComponents);
		repository.activate();
		repository.service(RevisionIndex.class).hooks().addHook(hook);
		manager.put(repositoryId, repository);
		
		// execute initialization steps
		repository.waitForHealth(Health.GREEN, 3 * 60L /*wait 3 minutes for GREEN repository status*/);
		new RepositoryRequest<>(repositoryId, initializer).execute(env);
		
		return repository;
	}

}
