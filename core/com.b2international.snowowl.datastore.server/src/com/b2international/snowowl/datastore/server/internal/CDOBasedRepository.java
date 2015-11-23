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

import static com.google.common.collect.Maps.newHashMap;

import java.io.File;
import java.nio.file.Paths;
import java.util.Map;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;

import com.b2international.snowowl.core.ClassLoaderProvider;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.api.index.IIndexServerServiceManager;
import com.b2international.snowowl.core.api.index.IIndexUpdater;
import com.b2international.snowowl.core.branch.BranchManager;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.domain.RepositoryContextProvider;
import com.b2international.snowowl.core.events.util.ApiRequestHandler;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.cdo.ICDORepository;
import com.b2international.snowowl.datastore.cdo.ICDORepositoryManager;
import com.b2international.snowowl.datastore.index.IndexTransactionProvider;
import com.b2international.snowowl.datastore.review.ReviewManager;
import com.b2international.snowowl.datastore.server.CDOServerUtils;
import com.b2international.snowowl.datastore.server.RepositoryClassLoaderProviderRegistry;
import com.b2international.snowowl.datastore.server.ReviewConfiguration;
import com.b2international.snowowl.datastore.server.cdo.CDOConflictProcessorBroker;
import com.b2international.snowowl.datastore.server.cdo.ICDOConflictProcessor;
import com.b2international.snowowl.datastore.server.index.SingleDirectoryIndexManager;
import com.b2international.snowowl.datastore.server.internal.branch.BranchSerializer;
import com.b2international.snowowl.datastore.server.internal.branch.CDOBranchManagerImpl;
import com.b2international.snowowl.datastore.server.internal.branch.InternalBranch;
import com.b2international.snowowl.datastore.server.internal.review.ConceptChangesImpl;
import com.b2international.snowowl.datastore.server.internal.review.ReviewImpl;
import com.b2international.snowowl.datastore.server.internal.review.ReviewManagerImpl;
import com.b2international.snowowl.datastore.server.internal.review.ReviewSerializer;
import com.b2international.snowowl.datastore.store.IndexStore;
import com.b2international.snowowl.eventbus.EventBusUtil;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.Pipe;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Provider;

/**
 * @since 4.1
 */
public final class CDOBasedRepository implements InternalRepository, RepositoryContextProvider {

	private final String repositoryId;
	private final Environment env;
	private final int numberOfWorkers;
	private final IEventBus handlers;
	
	private final Map<Class<?>, Object> registry = newHashMap();

	CDOBasedRepository(String repositoryId, int numberOfWorkers, Environment env) {
		this.repositoryId = repositoryId;
		this.numberOfWorkers = numberOfWorkers;
		this.env = env;
		this.handlers = EventBusUtil.getBus(repositoryId, numberOfWorkers);
		initializeBranchingSupport();
		initializeRequestSupport();
	}

	@Override
	public String id() {
		return repositoryId;
	}
	
	@Override
	public IEventBus events() {
		return env.service(IEventBus.class);
	}
	
	@Override
	public IEventBus handlers() {
		return handlers;
	}
	
	private String address() {
		return String.format("/%s", repositoryId);
	}
	
	private String address(String path) {
		return String.format("%s%s", address(), path);
	}
	
	@Override
	public ICDOConnection getConnection() {
		return env.service(ICDOConnectionManager.class).getByUuid(repositoryId);
	}
	
	@Override
	public CDOBranch getCdoMainBranch() {
		return getConnection().getMainBranch();
	}
	
	@Override
	public CDOBranchManager getCdoBranchManager() {
		return getCdoMainBranch().getBranchManager();
	}
	
	@Override
	public IIndexUpdater<?> getIndexUpdater() {
		return env.service(IIndexServerServiceManager.class).getByUuid(repositoryId);
	}
	
	@Override
	public ICDORepository getCdoRepository() {
		return env.service(ICDORepositoryManager.class).getByUuid(repositoryId);
	}
	
	@Override
	public ICDOConflictProcessor getConflictProcessor() {
		return CDOConflictProcessorBroker.INSTANCE.getProcessor(repositoryId);
	}
	
	@Override
    public long getBaseTimestamp(CDOBranch branch) {
        return branch.getBase().getTimeStamp();
    }
	
	@Override
	public long getHeadTimestamp(CDOBranch branch) {
		return Math.max(getBaseTimestamp(branch), CDOServerUtils.getLastCommitTime(branch));
	}
	
	private void initializeRequestSupport() {
		final ClassLoaderProvider classLoaderProvider = env.service(RepositoryClassLoaderProviderRegistry.class).get(repositoryId);
		for (int i = 0; i < numberOfWorkers; i++) {
			handlers().registerHandler(address(), new ApiRequestHandler(services(), classLoaderProvider));
		}
		
		// register event bridge/pipe between events and handlers
		events().registerHandler(address(), new Pipe(handlers(), address()));
		// register RepositoryContextProvider
		registry.put(RepositoryContextProvider.class, this);
		registry.put(IndexTransactionProvider.class, getIndexUpdater());
	}
	
	private ServiceProvider services() {
		return new ServiceProvider() {
			@Override
			public <T> T service(Class<T> type) {
				if (registry.containsKey(type)) {
					return (T) registry.get(type);
				}
				return env.service(type);
			}
			
			@Override
			public <T> Provider<T> provider(Class<T> type) {
				return env.provider(type);
			}
		};
	}
	
	@Override
	public RepositoryContext get(ServiceProvider context, String repositoryId) {
		return new DefaultRepositoryContext(context, repositoryId);
	}

	private void initializeBranchingSupport() {
		final BranchSerializer branchSerializer = env.service(BranchSerializer.class);
		final ReviewSerializer reviewSerializer = env.service(ReviewSerializer.class);
		final ReviewConfiguration reviewConfiguration = env.service(SnowOwlConfiguration.class).getModuleConfig(ReviewConfiguration.class);
		
		final IndexStore<InternalBranch> branchStore = createIndex("branches", branchSerializer, InternalBranch.class);
		final IndexStore<ReviewImpl> reviewStore = createIndex("reviews", reviewSerializer, ReviewImpl.class);
		final IndexStore<ConceptChangesImpl> conceptChangesStore = createIndex("concept_changes", reviewSerializer, ConceptChangesImpl.class);
		
		registry.put(BranchManager.class, new CDOBranchManagerImpl(this, branchStore));
		final ReviewManagerImpl reviewManager = new ReviewManagerImpl(this, reviewStore, conceptChangesStore, reviewConfiguration);
		registry.put(ReviewManager.class, reviewManager);

		events().registerHandler(address("/branches/changes") , reviewManager.getStaleHandler());
		
		// register stores to index manager
		final SingleDirectoryIndexManager indexManager = env.service(SingleDirectoryIndexManager.class);
		indexManager.registerIndex(branchStore);
		indexManager.registerIndex(reviewStore);
		indexManager.registerIndex(conceptChangesStore);
	}
	
	private <T> IndexStore<T> createIndex(final String name, ObjectMapper mapper, Class<T> type) {
		// TODO consider moving from index layout /feature/repositoryId to /repositoryId/feature
		final File dir = env.getDataDirectory()
				.toPath()
				.resolve(Paths.get("indexes", name, repositoryId))
				.toFile();
		return new IndexStore<>(dir, mapper, type);
	}
	
}
