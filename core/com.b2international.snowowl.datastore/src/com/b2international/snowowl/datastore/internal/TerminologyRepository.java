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
package com.b2international.snowowl.datastore.internal;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.extension.Extensions;
import com.b2international.index.DefaultIndex;
import com.b2international.index.Index;
import com.b2international.index.IndexClient;
import com.b2international.index.IndexClientFactory;
import com.b2international.index.Indexes;
import com.b2international.index.mapping.Mappings;
import com.b2international.index.revision.BaseRevisionBranching;
import com.b2international.index.revision.Commit;
import com.b2international.index.revision.DefaultRevisionBranching;
import com.b2international.index.revision.DefaultRevisionIndex;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.domain.DelegatingContext;
import com.b2international.snowowl.core.events.RepositoryEvent;
import com.b2international.snowowl.core.merge.MergeService;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.datastore.CodeSystemEntry;
import com.b2international.snowowl.datastore.CodeSystemVersionEntry;
import com.b2international.snowowl.datastore.config.IndexConfiguration;
import com.b2international.snowowl.datastore.config.IndexSettings;
import com.b2international.snowowl.datastore.config.RepositoryConfiguration;
import com.b2international.snowowl.datastore.events.RepositoryCommitNotification;
import com.b2international.snowowl.datastore.index.MappingProvider;
import com.b2international.snowowl.datastore.internal.merge.MergeServiceImpl;
import com.b2international.snowowl.datastore.review.ConceptChanges;
import com.b2international.snowowl.datastore.review.Review;
import com.b2international.snowowl.datastore.review.ReviewConfiguration;
import com.b2international.snowowl.datastore.review.ReviewManager;
import com.b2international.snowowl.datastore.review.ReviewManagerImpl;
import com.b2international.snowowl.eventbus.IEventBus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.MapMaker;

/**
 * @since 4.1
 */
public final class TerminologyRepository extends DelegatingContext implements InternalRepository {

	private final String toolingId;
	private final String repositoryId;
	private final Map<Long, RepositoryCommitNotification> commitNotifications = new MapMaker().makeMap();
	private final int mergeMaxResults;
	private final Logger logger;
	
	private Health health = Health.RED;
	private String diagnosis;
	
	TerminologyRepository(String repositoryId, String toolingId, int mergeMaxResults, Environment env) {
		super(env);
		this.toolingId = toolingId;
		this.repositoryId = repositoryId;
		this.mergeMaxResults = mergeMaxResults;
		this.logger = LoggerFactory.getLogger("repository."+repositoryId);
	}
	
	public void activate() {
		bind(Logger.class, logger);
		
		final ObjectMapper mapper = service(ObjectMapper.class);
		BaseRevisionBranching branching = initializeBranchingSupport(mergeMaxResults);
		RevisionIndex index = initIndex(mapper, branching);
		bind(Repository.class, this);
		bind(ClassLoader.class, getDelegate().plugins().getCompositeClassLoader());
		// initialize the index
		index.admin().create();
		checkHealth();
		if (health == Health.GREEN) {
			// TODO repository init
		}
	}

	@Override
	public void checkHealth() {
		// TODO support health services
		setHealth(Health.GREEN, null);
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
	
	private BaseRevisionBranching initializeBranchingSupport(int mergeMaxResults) {
		final BaseRevisionBranching branchManager = new DefaultRevisionBranching(provider(Index.class), service(ObjectMapper.class));
		bind(BaseRevisionBranching.class, branchManager);
//		bind(BranchReplicator.class, branchManager);
		
		final ReviewConfiguration reviewConfiguration = getDelegate().service(SnowOwlConfiguration.class).getModuleConfig(ReviewConfiguration.class);
		final ReviewManagerImpl reviewManager = new ReviewManagerImpl(this, reviewConfiguration);
		bind(ReviewManager.class, reviewManager);

		final MergeServiceImpl mergeService = new MergeServiceImpl(this, mergeMaxResults);
		bind(MergeService.class, mergeService);
		
		return branchManager;
	}

	private RevisionIndex initIndex(final ObjectMapper mapper, BaseRevisionBranching branching) {
		final Collection<Class<?>> types = newArrayList();
		types.add(Review.class);
		types.add(ConceptChanges.class);
		types.add(CodeSystemEntry.class);
		types.add(CodeSystemVersionEntry.class);
		types.addAll(getToolingTypes(toolingId));
		types.add(Commit.class);
		
		final Map<String, Object> indexSettings = newHashMap(getDelegate().service(IndexSettings.class));
		final IndexConfiguration repositoryIndexConfiguration = getDelegate().service(SnowOwlConfiguration.class).getModuleConfig(RepositoryConfiguration.class).getIndexConfiguration();
		indexSettings.put(IndexClientFactory.NUMBER_OF_SHARDS, repositoryIndexConfiguration.getNumberOfShards());
		final IndexClient indexClient = Indexes.createIndexClient(repositoryId, mapper, new Mappings(types), indexSettings);
		final Index index = new DefaultIndex(indexClient);
		final RevisionIndex revisionIndex = new DefaultRevisionIndex(index, branching, mapper);
		// register index and revision index access, the underlying index is the same
		bind(Index.class, index);
		bind(RevisionIndex.class, revisionIndex);
		return revisionIndex;
	}

	private Collection<Class<?>> getToolingTypes(String toolingId) {
		final Collection<Class<?>> types = newHashSet();
		final Collection<MappingProvider> providers = Extensions.getExtensions("com.b2international.snowowl.datastore.mappingProvider", MappingProvider.class);
		for (MappingProvider provider : providers) {
			if (provider.getToolingId().equals(toolingId)) {
				types.addAll(provider.getMappings());
			}
		}
		return types;
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
	public void setHealth(Health health, String diagnosis) {
		this.health = health;
		if (Health.GREEN != health) {
			checkState(!Strings.isNullOrEmpty(diagnosis), "Diagnosis required for health status %s", health);
		}
		this.diagnosis = diagnosis;
	}
	
	@Override
	public Health health() {
		return health;
	}
	
	@Override
	public String diagnosis() {
		return diagnosis;
	}
	
}
