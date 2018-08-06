/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.index.revision.Hooks;
import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.RepositoryInfo.Health;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.merge.ComponentRevisionConflictProcessor;
import com.b2international.snowowl.core.merge.IMergeConflictRule;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.setup.Plugin;
import com.b2international.snowowl.core.terminology.Terminology;
import com.b2international.snowowl.core.terminology.TerminologyRegistry;
import com.b2international.snowowl.datastore.config.RepositoryConfiguration;
import com.b2international.snowowl.datastore.index.BaseRepositoryPreCommitHook;
import com.b2international.snowowl.datastore.version.VersioningRequestBuilder;

/**
 * @since 7.0
 */
public abstract class TerminologyRepositoryPlugin extends Plugin implements Terminology {

	private static final Logger LOG = LoggerFactory.getLogger("repository");
	
	@Override
	public final void run(SnowOwlConfiguration configuration, Environment env) throws Exception {
		// register terminology and component definitions
		TerminologyRegistry registry = env.service(TerminologyRegistry.class);
		registry
			.registerTerminology(this);
		
		if (env.isEmbedded() || env.isServer()) {
			final DefaultRepositoryManager repositories = (DefaultRepositoryManager) env.service(RepositoryManager.class);
			final RepositoryConfiguration repositoryConfig = configuration.getModuleConfig(RepositoryConfiguration.class);
			final Repository repo = repositories.prepareCreate(getRepositoryId(), getId())
					.withInitializer(getTerminologyRepositoryInitializer())
					.withPreCommitHook(getTerminologyRepositoryPreCommitHook())
					.setMergeMaxResults(repositoryConfig.getMergeMaxResults())
					.addMappings(getMappings())
					.logger(log())
					.withComponentDeletionPolicy(getComponentDeletionPolicy())
					.withVersioningRequestBuilder(getVersioningRequestBuilder())
					.build(env);
			if (repo.health() == Health.GREEN) {
				LOG.info("Started repository '{}' with status '{}'", repo.id(), repo.health());
			} else {
				LOG.warn("Started repository '{}' with status '{}'. Diagnosis: {}.", repo.id(), repo.health(), repo.diagnosis());
			}
		}
		afterRun(configuration, env);
	}
	
	/**
	 * Subclasses may override to provide customized {@link ComponentDeletionPolicy} for the underlying repository.
	 * @return
	 */
	protected ComponentDeletionPolicy getComponentDeletionPolicy() {
		return ComponentDeletionPolicy.ALLOW;
	}

	protected abstract Collection<Class<?>> getMappings();

	/**
	 * Subclasses may override to provide additional service configuration via this {@link TerminologyRepositoryPlugin} after the initialization of the repository.
	 * @param configuration
	 * @param env
	 * @throws Exception
	 */
	protected void afterRun(SnowOwlConfiguration configuration, Environment env) throws Exception {
	}

	protected TerminologyRepositoryInitializer getTerminologyRepositoryInitializer() {
		return null;
	}
	
	/**
	 * Subclasses may override and provide a custom precommit hook to be installed on the underlying repository. {@link BaseRepositoryPreCommitHook}
	 * is a good candidate to extend and use for any particular terminology plugin.
	 * 
	 * @return
	 * @see BaseRepositoryPreCommitHook
	 */
	protected Hooks.PreCommitHook getTerminologyRepositoryPreCommitHook() {
		return staging -> {};
	}
	
	/**
	 * Subclasses may override this method to provide custom
	 * {@link VersioningRequestBuilder} instances to customize the versioning
	 * process in the underlying terminology.
	 * 
	 * @return
	 */
	protected VersioningRequestBuilder getVersioningRequestBuilder() {
		return VersioningRequestBuilder.DEFAULT;
	}
	
	/**
	 * Subclasses may override this method to provide custom conflict processor implementation along with additional {@link IMergeConflictRule}s. By
	 * default the repository will use a {@link ComponentRevisionConflictProcessor} without any {@link IMergeConflictRule}s.
	 * 
	 * @return
	 */
	protected ComponentRevisionConflictProcessor getComponentRevisionConflictProcessor() {
		return new ComponentRevisionConflictProcessor(Collections.emptyList());
	}

	/**
	 * @return the associated unique repository ID to use for the repository.
	 */
	protected abstract String getRepositoryId();
	
	protected final Logger log() {
		return LoggerFactory.getLogger("repository."+getRepositoryId());
	}
	
}
