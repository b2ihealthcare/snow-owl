/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Maps.newHashMap;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Map;

import org.slf4j.Logger;

import com.b2international.commons.exceptions.RequestTimeoutException;
import com.b2international.index.*;
import com.b2international.index.es.client.EsClusterStatus;
import com.b2international.index.mapping.Mappings;
import com.b2international.index.revision.BaseRevisionBranching;
import com.b2international.index.revision.DefaultRevisionIndex;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.TimestampProvider;
import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.RepositoryInfo;
import com.b2international.snowowl.core.RepositoryInfo.Health;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.branch.BranchChangedEvent;
import com.b2international.snowowl.core.config.IndexSettings;
import com.b2international.snowowl.core.config.RepositoryConfiguration;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.context.ServiceContext;
import com.b2international.snowowl.core.setup.Plugins;
import com.b2international.snowowl.eventbus.IEventBus;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;

/**
 * @since 4.1
 */
public final class TerminologyRepository extends ServiceContext implements Repository {

	private final String repositoryId;
	private final Mappings mappings;
	private final Logger log;
	
	TerminologyRepository(final String repositoryId, final Mappings mappings, final Logger log) {
		super();
		this.repositoryId = repositoryId;
		this.mappings = mappings;
		this.log = log;
	}
	
	public void activate(ServiceProvider context) {
		bind(Logger.class, log);
		
		RevisionIndex index = initIndex(context, mappings);
		bind(Repository.class, this);
		bind(Mappings.class, mappings);
		bind(ClassLoader.class, context.service(Plugins.class).getCompositeClassLoader());
		// initialize the index
		index.admin().create();
	}

	@Override
	public String id() {
		return repositoryId;
	}
	
	private RevisionIndex initIndex(final ServiceProvider context, Mappings mappings) {
		final ObjectMapper mapper = context.service(ObjectMapper.class);
		
		final Map<String, Object> indexSettings = newHashMap(context.service(IndexSettings.class));
		indexSettings.put(IndexClientFactory.NUMBER_OF_SHARDS, context.service(SnowOwlConfiguration.class).getModuleConfig(RepositoryConfiguration.class).getIndexConfiguration().getNumberOfShards());
		final IndexClient indexClient = Indexes.createIndexClient(repositoryId, mapper, mappings, indexSettings);
		final Index index = new DefaultIndex(indexClient);
		final RevisionIndex revisionIndex = new DefaultRevisionIndex(index, context.service(TimestampProvider.class), mapper);
		revisionIndex.branching().addBranchChangeListener(path -> {
			new BranchChangedEvent(repositoryId, path).publish(context.service(IEventBus.class));
		});
		// register IndexClient per terminology
		bind(IndexClient.class, indexClient);
		// register index and revision index access, the underlying index is the same
		bind(Index.class, index);
		bind(RevisionIndex.class, revisionIndex);
		// register branching services
		bind(BaseRevisionBranching.class, revisionIndex.branching());
		return revisionIndex;
	}

	@Override
	public RepositoryInfo status() {
		// by default assume it is in GREEN status with no diagnosis
		Health health = Health.GREEN;
		String diagnosis = "";
		final String[] indices = service(Index.class).admin().indices();
		final EsClusterStatus status = service(IndexClient.class).client().status(indices);
		if (!status.isAvailable()) {
			// check if cluster is available or not, and report RED state if not along with index diagnosis
			health = Health.RED;
			diagnosis = status.getDiagnosis();
		} else if (!status.isHealthy(indices)) {
			// check if index is healthy and report RED if not along with diagnosis
			health = Health.RED;
			diagnosis = String.format("Repository indices '%s' are not healthy.", Arrays.toString(indices));
		}
		return RepositoryInfo.of(id(), health, diagnosis, status.getIndices());
	}

	public void waitForHealth(RepositoryInfo.Health desiredHealth, long seconds) {
		final RetryPolicy<Health> retryPolicy = new RetryPolicy<Health>()
				.handleResult(Health.RED)
				.withMaxAttempts(-1)
				.withMaxDuration(Duration.of(seconds, ChronoUnit.SECONDS))
				.withBackoff(1, Math.max(2, seconds / 3), ChronoUnit.SECONDS);
		final Health finalHealth = Failsafe.with(retryPolicy).get(() -> status().health());
		if (finalHealth != desiredHealth) {
			throw new RequestTimeoutException("Repository health status couldn't reach '%s' in '%s' seconds.", desiredHealth, seconds);
		}
	}
	
}
