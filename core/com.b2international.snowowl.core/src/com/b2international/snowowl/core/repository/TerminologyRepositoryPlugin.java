/*
 * Copyright 2018-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.index.revision.Hooks;
import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.RepositoryInfo;
import com.b2international.snowowl.core.RepositoryInfo.Health;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.branch.BranchPathUtils;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.codesystem.version.VersioningRequestBuilder;
import com.b2international.snowowl.core.config.RepositoryConfiguration;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.domain.ContextConfigurer;
import com.b2international.snowowl.core.merge.ComponentRevisionConflictProcessor;
import com.b2international.snowowl.core.merge.IMergeConflictRule;
import com.b2international.snowowl.core.request.ConceptSearchRequest;
import com.b2international.snowowl.core.request.ConceptSearchRequestBuilder;
import com.b2international.snowowl.core.request.ConceptSearchRequestEvaluator;
import com.b2international.snowowl.core.request.QueryOptimizer;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.setup.Plugin;
import com.b2international.snowowl.core.terminology.Terminology;
import com.b2international.snowowl.core.terminology.TerminologyComponent;
import com.b2international.snowowl.core.terminology.TerminologyRegistry;
import com.google.common.primitives.Ints;

/**
 * @since 7.0
 */
public abstract class TerminologyRepositoryPlugin extends Plugin implements Terminology {

	private static final Logger LOG = LoggerFactory.getLogger("repository");
	
	@Override
	public final void run(SnowOwlConfiguration configuration, Environment env) throws Exception {
		// register terminology and component definitions
		TerminologyRegistry registry = env.service(TerminologyRegistry.class);
		registry.register(this);
		getAdditionalTerminologyComponents().values().forEach(additionalTerminologyComponent -> registry.register(getId(), additionalTerminologyComponent));
		
		if (env.isServer()) {
			final DefaultRepositoryManager repositories = (DefaultRepositoryManager) env.service(RepositoryManager.class);
			final RepositoryConfiguration repositoryConfig = configuration.getModuleConfig(RepositoryConfiguration.class);
			final RepositoryBuilder builder = repositories.prepareCreate(getRepositoryId());
			
			
			final Repository repo = builder
					.withInitializer(getTerminologyRepositoryInitializer())
					.withPreCommitHook(getTerminologyRepositoryPreCommitHook(builder.log()))
					.setMergeMaxResults(repositoryConfig.getMergeMaxResults())
					.addTerminologyComponents(getTerminologyComponents())
					.addTerminologyComponents(getAdditionalTerminologyComponents())
					.addMappings(getAdditionalMappings())
					.bind(ComponentDeletionPolicy.class, getComponentDeletionPolicy())
					.bind(VersioningRequestBuilder.class, getVersioningRequestBuilder())
					.bind(ComponentRevisionConflictProcessor.class, getComponentRevisionConflictProcessor())
					.bind(ConceptSearchRequestEvaluator.class, getConceptSearchRequestEvaluator())
					.bind(QueryOptimizer.class, getQueryOptimizer())
					.bind(ContentAvailabilityInfoProvider.class, getContentAvailabilityInfoProvider())
					.bind(ContextConfigurer.class, getRequestConfigurer())
					.bind(RepositoryCodeSystemProvider.class, (referenceBranch) -> {
						final IBranchPath referencePath = BranchPathUtils.createPath(referenceBranch);
						return CodeSystemRequests.getAllCodeSystems(env)
							.stream()
							.filter(cs -> getId().equals(cs.getTerminologyId()))
							// sort by longest working branch path
							.sorted((cs1, cs2) -> -1 * Ints.compare(cs1.getBranchPath().length(), cs2.getBranchPath().length()))
							// check whether the working branch path is either the parent of the reference branch or is the reference branch
							.filter(cs -> {
								final IBranchPath codeSystemPath = BranchPathUtils.createPath(cs.getBranchPath());
								return BranchPathUtils.isDescendantOf(codeSystemPath, referencePath);
							})
							.findFirst()
							.orElseThrow(() -> new BadRequestException("No relative CodeSystem has been found for reference branch '%s'.", referenceBranch));
					})
					.build(env);
			RepositoryInfo status = repo.status();
			if (status.health() == Health.GREEN) {
				LOG.info("Started repository '{}' with status '{}'", repo.id(), status.health());
			} else {
				LOG.warn("Started repository '{}' with status '{}'. Diagnosis: {}.", status.id(), status.health(), status.diagnosis());
			}
		}
		afterRun(configuration, env);
	}
	
	/**
	 * Subclasses may override to provide a terminology specific request configurer to configure incoming requests. The default implementation of this method returns a no-op request configurer.
	 * 
	 * @return 
	 */
	protected ContextConfigurer getRequestConfigurer() {
		return ContextConfigurer.NOOP;
	}

	protected abstract ContentAvailabilityInfoProvider getContentAvailabilityInfoProvider();
	
	/**
	 * An evaluator that can evaluate generic {@link ConceptSearchRequest concept search requests}. 
	 * @return a {@link ConceptSearchRequestEvaluator} instance
	 * @see ConceptSearchRequestBuilder
	 * @see ConceptSearchRequest
	 */
	protected abstract ConceptSearchRequestEvaluator getConceptSearchRequestEvaluator();

	/**
	 * Subclasses may override to provide a customized {@link QueryOptimizer} for the underlying terminology tooling and query language.
	 * <p>
	 * The default implementation does not suggest changes for any incoming queries.
	 * @return
	 */
	protected QueryOptimizer getQueryOptimizer() {
		return QueryOptimizer.NOOP;
	}
	
	/**
	 * Subclasses may override to provide customized {@link ComponentDeletionPolicy} for the underlying repository.
	 * @return
	 */
	protected ComponentDeletionPolicy getComponentDeletionPolicy() {
		return ComponentDeletionPolicy.ALLOW;
	}

	/**
	 * Additional mappings can be registered on top of the existing {@link TerminologyComponent} assigned to this terminology tooling.
	 * @return
	 */
	protected Collection<Class<?>> getAdditionalMappings() {
		return Collections.emptyList();
	}
	
	/**
	 * @return the additional terminology components for this terminology.
	 */
	protected Map<Class<?>, TerminologyComponent> getAdditionalTerminologyComponents() {
		return Collections.emptyMap();
	}

	/**
	 * Subclasses may override to provide additional service configuration via this {@link TerminologyRepositoryPlugin} after the initialization of the repository.
	 * @param configuration
	 * @param env
	 * @throws Exception
	 */
	protected void afterRun(SnowOwlConfiguration configuration, Environment env) throws Exception {
	}

	protected abstract TerminologyRepositoryInitializer getTerminologyRepositoryInitializer();
	
	/**
	 * Subclasses may override and provide a custom precommit hook to be installed on the underlying repository. {@link BaseRepositoryPreCommitHook}
	 * is a good candidate to extend and use for any particular terminology plugin.
	 * 
	 * @param log
	 * @return
	 * @see BaseRepositoryPreCommitHook
	 */
	protected Hooks.PreCommitHook getTerminologyRepositoryPreCommitHook(Logger log) {
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
	
}
