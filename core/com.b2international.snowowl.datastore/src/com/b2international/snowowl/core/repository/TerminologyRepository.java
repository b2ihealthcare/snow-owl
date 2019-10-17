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

import static com.google.common.collect.Maps.newHashMap;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.b2international.index.DefaultIndex;
import com.b2international.index.Index;
import com.b2international.index.IndexClient;
import com.b2international.index.IndexClientFactory;
import com.b2international.index.Indexes;
import com.b2international.index.es.client.EsClusterStatus;
import com.b2international.index.mapping.Mappings;
import com.b2international.index.revision.BaseRevisionBranching;
import com.b2international.index.revision.DefaultRevisionIndex;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.TimestampProvider;
import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.RepositoryInfo;
import com.b2international.snowowl.core.RepositoryInfo.Health;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.domain.DelegatingContext;
import com.b2international.snowowl.core.events.RepositoryEvent;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.datastore.config.IndexConfiguration;
import com.b2international.snowowl.datastore.config.IndexSettings;
import com.b2international.snowowl.datastore.config.RepositoryConfiguration;
import com.b2international.snowowl.datastore.events.BranchChangedEvent;
import com.b2international.snowowl.datastore.events.RepositoryCommitNotification;
import com.b2international.snowowl.datastore.review.ReviewConfiguration;
import com.b2international.snowowl.datastore.review.ReviewManager;
import com.b2international.snowowl.datastore.review.ReviewManagerImpl;
import com.b2international.snowowl.eventbus.IEventBus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.MapMaker;

/**
 * @since 4.1
 */
public final class TerminologyRepository extends DelegatingContext implements Repository {

	private final String repositoryId;
	private final Mappings mappings;
	private final Logger log;
	private final Map<Long, RepositoryCommitNotification> commitNotifications = new MapMaker().makeMap();
	
	TerminologyRepository(String repositoryId, int mergeMaxResults, Environment env, Mappings mappings, Logger log) {
		super(env);
		this.repositoryId = repositoryId;
		this.mappings = mappings;
		this.log = log;
	}
	
	public void activate() {
		bind(Logger.class, log);
		
		final ObjectMapper mapper = service(ObjectMapper.class);
		initializeServices();
		RevisionIndex index = initIndex(mapper, mappings);
		bind(Repository.class, this);
		bind(ClassLoader.class, getDelegate().plugins().getCompositeClassLoader());
		// initialize the index
		index.admin().create();
	}

	@Override
	public String id() {
		return repositoryId;
	}
	
	@Override
	public IEventBus events() {
		return getDelegate().service(IEventBus.class);
	}
	
	@Override
	public void sendNotification(RepositoryEvent event) {
		if (event instanceof RepositoryCommitNotification) {
			final RepositoryCommitNotification notification = (RepositoryCommitNotification) event;
			// enqueue and wait until the actual CDO commit notification arrives
			commitNotifications.put(notification.getCommitTimestamp(), notification);
		} else {
			event.publish(events());
		}
	}
	
	private void initializeServices() {
		final ReviewConfiguration reviewConfiguration = getDelegate().service(SnowOwlConfiguration.class).getModuleConfig(ReviewConfiguration.class);
		final ReviewManagerImpl reviewManager = new ReviewManagerImpl(this, reviewConfiguration);
		bind(ReviewManager.class, reviewManager);
	}

	private RevisionIndex initIndex(final ObjectMapper mapper, Mappings mappings) {
		final Map<String, Object> indexSettings = newHashMap(getDelegate().service(IndexSettings.class));
		final IndexConfiguration repositoryIndexConfiguration = getDelegate().service(SnowOwlConfiguration.class).getModuleConfig(RepositoryConfiguration.class).getIndexConfiguration();
		indexSettings.put(IndexClientFactory.NUMBER_OF_SHARDS, repositoryIndexConfiguration.getNumberOfShards());
		final IndexClient indexClient = Indexes.createIndexClient(repositoryId, mapper, mappings, indexSettings);
		final Index index = new DefaultIndex(indexClient);
		final RevisionIndex revisionIndex = new DefaultRevisionIndex(index, service(TimestampProvider.class), mapper);
		revisionIndex.branching().addBranchChangeListener(path -> {
			sendNotification(new BranchChangedEvent(repositoryId, path));
		});
		// register index and revision index access, the underlying index is the same
		bind(IndexClient.class, indexClient);
		bind(Index.class, index);
		bind(RevisionIndex.class, revisionIndex);
		bind(BaseRevisionBranching.class, revisionIndex.branching());
		return revisionIndex;
	}

	@Override
	public void doDispose() {
		service(RevisionIndex.class).admin().close();
	}
	
	@Override
	protected Environment getDelegate() {
		return (Environment) super.getDelegate();
	}

	@Override
	public RepositoryInfo status() {
		// by default assume it is in GREEN status with no diagnosis
		Health health = Health.GREEN;
		String diagnosis = "";
		final EsClusterStatus status = service(IndexClient.class).client().status();
		final String[] indices = service(Index.class).admin().indices();
		if (!status.isAvailable()) {
			// check if cluster is available or not, and report RED state if not along with index diagnosis
			health = Health.RED;
			diagnosis = status.getDiagnosis();
		} else if (status.isHealthy()) {
			// check if index is healthy and report RED if not along with diagnosis
			health = Health.RED;
			diagnosis = String.format("Repository indices '%s' are not healthy.", Arrays.toString(indices));
		}
		return RepositoryInfo.of(id(), health, diagnosis);
	}

	public void waitForHealth(RepositoryInfo.Health health, long timeout, TimeUnit unit) {
		// TODO implement
	}
	
}
