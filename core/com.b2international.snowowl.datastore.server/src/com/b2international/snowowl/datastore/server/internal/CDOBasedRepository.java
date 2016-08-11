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
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;

import com.b2international.collections.PrimitiveCollectionModule;
import com.b2international.commons.platform.Extensions;
import com.b2international.index.DefaultIndex;
import com.b2international.index.Index;
import com.b2international.index.IndexClient;
import com.b2international.index.IndexClientFactory;
import com.b2international.index.IndexWrite;
import com.b2international.index.Indexes;
import com.b2international.index.Writer;
import com.b2international.index.mapping.Mappings;
import com.b2international.index.query.slowlog.SlowLogConfig;
import com.b2international.index.revision.DefaultRevisionIndex;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.index.revision.RevisionBranchProvider;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.core.ClassLoaderProvider;
import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.branch.BranchManager;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.domain.DelegatingServiceProvider;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.domain.RepositoryContextProvider;
import com.b2international.snowowl.core.events.util.ApiRequestHandler;
import com.b2international.snowowl.core.merge.MergeService;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CDOEditingContext;
import com.b2international.snowowl.datastore.CodeSystemEntry;
import com.b2international.snowowl.datastore.CodeSystemVersionEntry;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.cdo.ICDORepository;
import com.b2international.snowowl.datastore.cdo.ICDORepositoryManager;
import com.b2international.snowowl.datastore.config.IndexConfiguration;
import com.b2international.snowowl.datastore.config.RepositoryConfiguration;
import com.b2international.snowowl.datastore.index.MappingProvider;
import com.b2international.snowowl.datastore.replicate.BranchReplicator;
import com.b2international.snowowl.datastore.review.ConceptChanges;
import com.b2international.snowowl.datastore.review.Review;
import com.b2international.snowowl.datastore.review.ReviewManager;
import com.b2international.snowowl.datastore.server.CDOServerUtils;
import com.b2international.snowowl.datastore.server.EditingContextFactory;
import com.b2international.snowowl.datastore.server.EditingContextFactoryProvider;
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
import com.b2international.snowowl.datastore.server.internal.review.ConceptChangesImpl;
import com.b2international.snowowl.datastore.server.internal.review.ReviewImpl;
import com.b2international.snowowl.datastore.server.internal.review.ReviewManagerImpl;
import com.b2international.snowowl.eventbus.EventBusUtil;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.Pipe;
import com.b2international.snowowl.terminologymetadata.CodeSystem;
import com.b2international.snowowl.terminologymetadata.CodeSystemVersion;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Ordering;
import com.google.inject.Provider;

/**
 * @since 4.1
 */
public final class CDOBasedRepository extends DelegatingServiceProvider implements InternalRepository, RepositoryContextProvider {

	private static final String REINDEX_KEY = "snowowl.reindex";
	
	private final String toolingId;
	private final String repositoryId;
	private final IEventBus handlers;
	
	CDOBasedRepository(String repositoryId, String toolingId, int numberOfWorkers, int mergeMaxResults, Environment env) {
		super(env);
		checkArgument(numberOfWorkers > 0, "At least one worker thread must be specified");
		
		this.toolingId = toolingId;
		this.repositoryId = repositoryId;
		this.handlers = EventBusUtil.getWorkerBus(repositoryId, numberOfWorkers);

		final ObjectMapper mapper = JsonSupport.getDefaultObjectMapper();
		mapper.registerModule(new PrimitiveCollectionModule());
		initIndex(mapper);
		initializeBranchingSupport(mergeMaxResults);
		initializeRequestSupport(numberOfWorkers);
		reindex();
		bind(Repository.class, this);
		bind(ObjectMapper.class, mapper);
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
		return getDelegate().service(ICDOConnectionManager.class).getByUuid(repositoryId);
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
	public Index getIndex() {
		return service(Index.class);
	}
	
	@Override
	public RevisionIndex getRevisionIndex() {
		return service(RevisionIndex.class);
	}
	
	@Override
	public ICDORepository getCdoRepository() {
		return getDelegate().service(ICDORepositoryManager.class).getByUuid(repositoryId);
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
		bind(RepositoryContextProvider.class, this);
	}

	private ClassLoaderProvider getClassLoaderProvider() {
		return getDelegate().service(RepositoryClassLoaderProviderRegistry.class).get(repositoryId);
	}
	
	@Override
	public RepositoryContext get(ServiceProvider context, String repositoryId) {
		return new DefaultRepositoryContext(context, repositoryId);
	}

	private void initializeBranchingSupport(int mergeMaxResults) {
		final CDOBranchManagerImpl branchManager = new CDOBranchManagerImpl(this);
		bind(BranchManager.class, branchManager);
		bind(BranchReplicator.class, branchManager);
		
		
		final ReviewConfiguration reviewConfiguration = getDelegate().service(SnowOwlConfiguration.class).getModuleConfig(ReviewConfiguration.class);
		final ReviewManagerImpl reviewManager = new ReviewManagerImpl(this, reviewConfiguration);
		bind(ReviewManager.class, reviewManager);

		events().registerHandler(address("/branches/changes") , reviewManager.getStaleHandler());
		
		final MergeServiceImpl mergeService = new MergeServiceImpl(this, mergeMaxResults);
		bind(MergeService.class, mergeService);
	}

	private void initIndex(final ObjectMapper mapper) {
		final Collection<Class<?>> types = newHashSet();
		types.add(CDOMainBranchImpl.class);
		types.add(CDOBranchImpl.class);
		types.add(InternalBranch.class);
		types.add(Review.class);
		types.add(ReviewImpl.class);
		types.add(ConceptChanges.class);
		types.add(ConceptChangesImpl.class);
		types.add(CodeSystemEntry.class);
		types.add(CodeSystemVersionEntry.class);
		types.addAll(getToolingTypes(toolingId));
		
		final Map<String, Object> settings = initIndexSettings();
		final IndexClient indexClient = Indexes.createIndexClient(repositoryId, mapper, new Mappings(types), settings);
		final Index index = new DefaultIndex(indexClient);
		final Provider<BranchManager> branchManager = provider(BranchManager.class);
		final RevisionIndex revisionIndex = new DefaultRevisionIndex(index, new RevisionBranchProvider() {
			@Override
			public RevisionBranch getBranch(String branchPath) {
				final InternalCDOBasedBranch branch = (InternalCDOBasedBranch) branchManager.get().getBranch(branchPath);
				final Set<Integer> segments = newHashSet();
				segments.addAll(branch.segments());
				segments.addAll(branch.parentSegments());
				return new RevisionBranch(branchPath, branch.segmentId(), segments);
			}
			
			@Override
			public RevisionBranch getParentBranch(String branchPath) {
				final InternalCDOBasedBranch branch = (InternalCDOBasedBranch) branchManager.get().getBranch(branchPath);
				return new RevisionBranch(branch.parent().path(), Ordering.natural().max(branch.parentSegments()), branch.parentSegments());
			}

		});
		// register index and revision index access, the underlying index is the same
		bind(Index.class, index);
		bind(RevisionIndex.class, revisionIndex);
		// initialize the index
		index.admin().create();
		
	}

	private Map<String, Object> initIndexSettings() {
		final ImmutableMap.Builder<String, Object> builder = ImmutableMap.<String, Object>builder();
		builder.put(IndexClientFactory.DIRECTORY, getDelegate().getDataDirectory() + "/indexes");
		
		final IndexConfiguration config = service(SnowOwlConfiguration.class)
				.getModuleConfig(RepositoryConfiguration.class).getIndexConfiguration();
		
		builder.put(IndexClientFactory.COMMIT_INTERVAL_KEY, config.getCommitInterval());
		builder.put(IndexClientFactory.TRANSLOG_SYNC_INTERVAL_KEY, config.getTranslogSyncInterval());
		
		final SlowLogConfig slowLog = createSlowLogConfig(config);
		builder.put(IndexClientFactory.SLOW_LOG_KEY, slowLog);
		
		return builder.build();
	}

	private SlowLogConfig createSlowLogConfig(final IndexConfiguration config) {
		final ImmutableMap.Builder<String, Object> builder = ImmutableMap.<String, Object>builder();
		builder.put(SlowLogConfig.FETCH_DEBUG_THRESHOLD, config.getFetchDebugThreshold());
		builder.put(SlowLogConfig.FETCH_INFO_THRESHOLD, config.getFetchInfoThreshold());
		builder.put(SlowLogConfig.FETCH_TRACE_THRESHOLD, config.getFetchTraceThreshold());
		builder.put(SlowLogConfig.FETCH_WARN_THRESHOLD, config.getFetchWarnThreshold());
		builder.put(SlowLogConfig.QUERY_DEBUG_THRESHOLD, config.getQueryDebugThreshold());
		builder.put(SlowLogConfig.QUERY_INFO_THRESHOLD, config.getQueryInfoThreshold());
		builder.put(SlowLogConfig.QUERY_TRACE_THRESHOLD, config.getQueryTraceThreshold());
		builder.put(SlowLogConfig.QUERY_WARN_THRESHOLD, config.getQueryWarnThreshold());
		
		return new SlowLogConfig(builder.build());
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
	
	private void reindex() {
		final boolean reindex = Boolean.parseBoolean(System.getProperty(REINDEX_KEY, "false"));
		if (reindex) {
			reindexCodeSystems();
		}
	}
	
	private void reindexCodeSystems() {
		final EditingContextFactoryProvider contextFactoryProvider = getDelegate().service(EditingContextFactoryProvider.class);
		final EditingContextFactory contextFactory = contextFactoryProvider.get(repositoryId);
		
		try (final CDOEditingContext editingContext = contextFactory.createEditingContext(BranchPathUtils.createMainPath())) {
			final List<CodeSystem> codeSystems = editingContext.getCodeSystems();
			
			getIndex().write(new IndexWrite<Void>() {
				@Override
				public Void execute(Writer index) throws IOException {
					for (final CodeSystem codeSystem : codeSystems) {
						final CodeSystemEntry entry = CodeSystemEntry.builder(codeSystem).build();
						index.put(Long.toString(entry.getStorageKey()), entry);
						
						for (final CodeSystemVersion codeSystemVersion : codeSystem.getCodeSystemVersions()) {
							final CodeSystemVersionEntry versionEntry = CodeSystemVersionEntry.builder(codeSystemVersion).build();
							index.put(Long.toString(versionEntry.getStorageKey()), versionEntry);
						}
					}
					index.commit();
					return null;
				}
			});
		}
	}
	
	@Override
	protected Environment getDelegate() {
		return (Environment) super.getDelegate();
	}
	
}
