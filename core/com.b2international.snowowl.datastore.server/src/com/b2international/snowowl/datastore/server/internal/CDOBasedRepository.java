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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;

import com.b2international.collections.PrimitiveCollectionModule;
import com.b2international.commons.platform.Extensions;
import com.b2international.index.DefaultIndex;
import com.b2international.index.Index;
import com.b2international.index.IndexClient;
import com.b2international.index.IndexClientFactory;
import com.b2international.index.Indexes;
import com.b2international.index.mapping.Mappings;
import com.b2international.index.revision.DefaultRevisionIndex;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.index.revision.RevisionBranchProvider;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.core.ClassLoaderProvider;
import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.api.index.IIndexServerServiceManager;
import com.b2international.snowowl.core.api.index.IIndexUpdater;
import com.b2international.snowowl.core.branch.BranchManager;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.domain.RepositoryContextProvider;
import com.b2international.snowowl.core.events.util.ApiRequestHandler;
import com.b2international.snowowl.core.merge.MergeService;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.datastore.CodeSystemEntry;
import com.b2international.snowowl.datastore.CodeSystemVersionEntry;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.cdo.ICDORepository;
import com.b2international.snowowl.datastore.cdo.ICDORepositoryManager;
import com.b2international.snowowl.datastore.index.MappingProvider;
import com.b2international.snowowl.datastore.replicate.BranchReplicator;
import com.b2international.snowowl.datastore.review.ConceptChanges;
import com.b2international.snowowl.datastore.review.Review;
import com.b2international.snowowl.datastore.review.ReviewManager;
import com.b2international.snowowl.datastore.server.CDOServerUtils;
import com.b2international.snowowl.datastore.server.RepositoryClassLoaderProviderRegistry;
import com.b2international.snowowl.datastore.server.ReviewConfiguration;
import com.b2international.snowowl.datastore.server.cdo.CDOConflictProcessorBroker;
import com.b2international.snowowl.datastore.server.cdo.ICDOConflictProcessor;
import com.b2international.snowowl.datastore.server.internal.branch.CDOBranchImpl;
import com.b2international.snowowl.datastore.server.internal.branch.CDOBranchManagerImpl;
import com.b2international.snowowl.datastore.server.internal.branch.CDOMainBranchImpl;
import com.b2international.snowowl.datastore.server.internal.branch.InternalBranch;
import com.b2international.snowowl.datastore.server.internal.branch.InternalCDOBasedBranch;
import com.b2international.snowowl.datastore.server.internal.merge.MergeServiceImpl;
import com.b2international.snowowl.datastore.server.internal.review.ReviewManagerImpl;
import com.b2international.snowowl.eventbus.EventBusUtil;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.Pipe;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Provider;

/**
 * @since 4.1
 */
public final class CDOBasedRepository implements InternalRepository, RepositoryContextProvider, ServiceProvider {

	private final String toolingId;
	private final String repositoryId;
	private final Environment env;
	private final IEventBus handlers;
	
	private final Map<Class<?>, Object> registry = newHashMap();

	CDOBasedRepository(String repositoryId, String toolingId, int numberOfWorkers, int mergeMaxResults, Environment env) {
		checkArgument(numberOfWorkers > 0, "At least one worker thread must be specified");
		
		this.toolingId = toolingId;
		this.repositoryId = repositoryId;
		this.env = env;
		this.handlers = EventBusUtil.getWorkerBus(repositoryId, numberOfWorkers);

		final ObjectMapper mapper = JsonSupport.getDefaultObjectMapper();
		mapper.registerModule(new PrimitiveCollectionModule());
		initIndex(mapper);
		initializeBranchingSupport(mergeMaxResults);
		initializeRequestSupport(numberOfWorkers);
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
	public Index getIndex() {
		return (Index) registry.get(Index.class);
	}
	
	@Override
	public RevisionIndex getRevisionIndex() {
		return (RevisionIndex) registry.get(RevisionIndex.class);
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
	
	private void initializeRequestSupport(int numberOfWorkers) {
		final ClassLoaderProvider classLoaderProvider = getClassLoaderProvider();
		for (int i = 0; i < numberOfWorkers; i++) {
			handlers().registerHandler(address(), new ApiRequestHandler(this, classLoaderProvider));
		}
		
		// register number of cores event bridge/pipe between events and handlers
		for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
			events().registerHandler(address(), new Pipe(handlers(), address()));
		}
		// register RepositoryContextProvider
		registry.put(RepositoryContextProvider.class, this);
	}

	private ClassLoaderProvider getClassLoaderProvider() {
		return env.service(RepositoryClassLoaderProviderRegistry.class).get(repositoryId);
	}
	
	@Override
	public <T> T service(Class<T> type) {
		if (Repository.class.isAssignableFrom(type)) {
			return type.cast(this);
		}
		if (registry.containsKey(type)) {
			return (T) registry.get(type);
		}
		return env.service(type);
	}

	@Override
	public <T> Provider<T> provider(final Class<T> type) {
		return new Provider<T>() {
			@Override
			public T get() {
				return service(type);
			}
		};
	}
	
	@Override
	public RepositoryContext get(ServiceProvider context, String repositoryId) {
		return new DefaultRepositoryContext(context, repositoryId);
	}

	private void initializeBranchingSupport(int mergeMaxResults) {
		final CDOBranchManagerImpl branchManager = new CDOBranchManagerImpl(this);
		registry.put(BranchManager.class, branchManager);
		registry.put(BranchReplicator.class, branchManager);
		
		
		final ReviewConfiguration reviewConfiguration = env.service(SnowOwlConfiguration.class).getModuleConfig(ReviewConfiguration.class);
		final ReviewManagerImpl reviewManager = new ReviewManagerImpl(this, reviewConfiguration);
		registry.put(ReviewManager.class, reviewManager);

		events().registerHandler(address("/branches/changes") , reviewManager.getStaleHandler());
		
		final MergeServiceImpl mergeService = new MergeServiceImpl(this, mergeMaxResults);
		registry.put(MergeService.class, mergeService);
	}

	private void initIndex(final ObjectMapper mapper) {
		final Collection<Class<?>> types = newHashSet();
		// register the known types others will get registered dynamically
		types.add(CDOMainBranchImpl.class);
		types.add(CDOBranchImpl.class);
		types.add(Review.class);
		types.add(ConceptChanges.class);
		types.add(InternalBranch.class);
		types.add(CodeSystemEntry.class);
		types.add(CodeSystemVersionEntry.class);
		types.addAll(getToolingTypes(toolingId));
		final Map<String, Object> settings = ImmutableMap.<String, Object>of(IndexClientFactory.DIRECTORY, 
				env.getDataDirectory() + "/indexes");
		final IndexClient indexClient = Indexes.createIndexClient(repositoryId, mapper, new Mappings(types), settings);
		final Index index = new DefaultIndex(indexClient);
		final Provider<BranchManager> branchManager = provider(BranchManager.class);
		final RevisionIndex revisionIndex = new DefaultRevisionIndex(index, new RevisionBranchProvider() {
			@Override
			public RevisionBranch getBranch(String branchPath) {
				final InternalCDOBasedBranch branch = (InternalCDOBasedBranch) branchManager.get().getBranch(branchPath);
				return new RevisionBranch(branchPath, branch.segmentId(), branch.segments());
			}

		});
		// register index and revision index access, the underlying index is the same
		registry.put(Index.class, index);
		registry.put(RevisionIndex.class, revisionIndex);
		// initialize the index
		index.admin().create();
		
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
	public void close() throws IOException {
		getIndex().admin().close();
	}
	
}
