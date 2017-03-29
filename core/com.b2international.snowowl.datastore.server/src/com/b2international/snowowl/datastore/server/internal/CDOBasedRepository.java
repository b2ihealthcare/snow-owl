/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;

import com.b2international.commons.platform.Extensions;
import com.b2international.index.DefaultIndex;
import com.b2international.index.Index;
import com.b2international.index.IndexClient;
import com.b2international.index.IndexClientFactory;
import com.b2international.index.IndexRead;
import com.b2international.index.Indexes;
import com.b2international.index.Searcher;
import com.b2international.index.mapping.Mappings;
import com.b2international.index.query.slowlog.SlowLogConfig;
import com.b2international.index.revision.DefaultRevisionIndex;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.index.revision.RevisionBranchProvider;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.BranchManager;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.domain.DelegatingRepositoryContext;
import com.b2international.snowowl.core.domain.DelegatingServiceProvider;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.RepositoryEvent;
import com.b2international.snowowl.core.merge.MergeService;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CodeSystemEntry;
import com.b2international.snowowl.datastore.CodeSystemVersionEntry;
import com.b2international.snowowl.datastore.cdo.CDOCommitInfoUtils;
import com.b2international.snowowl.datastore.cdo.CDOCommitInfoUtils.CDOCommitInfoQuery;
import com.b2international.snowowl.datastore.cdo.CDOCommitInfoUtils.ConsumeAllCommitInfoHandler;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.cdo.ICDORepository;
import com.b2international.snowowl.datastore.cdo.ICDORepositoryManager;
import com.b2international.snowowl.datastore.commitinfo.CommitInfo;
import com.b2international.snowowl.datastore.commitinfo.CommitInfoDocument;
import com.b2international.snowowl.datastore.commitinfo.CommitInfos;
import com.b2international.snowowl.datastore.config.IndexConfiguration;
import com.b2international.snowowl.datastore.config.RepositoryConfiguration;
import com.b2international.snowowl.datastore.events.RepositoryCommitNotification;
import com.b2international.snowowl.datastore.index.MappingProvider;
import com.b2international.snowowl.datastore.replicate.BranchReplicator;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.datastore.review.ConceptChanges;
import com.b2international.snowowl.datastore.review.Review;
import com.b2international.snowowl.datastore.review.ReviewManager;
import com.b2international.snowowl.datastore.server.CDOServerUtils;
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
import com.b2international.snowowl.eventbus.IEventBus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Ordering;
import com.google.inject.Provider;

/**
 * @since 4.1
 */
public final class CDOBasedRepository extends DelegatingServiceProvider implements InternalRepository, CDOCommitInfoHandler, RepositoryMetadata {

	private static final String REINDEX_DIAGNOSIS_TEMPLATE = "Run reindex with console command to synchronize '%s' repository with its database: 'snowowl reindex %s%s'";
	private static final String RESTORE_DIAGNOSIS = "Inconsistent database. Shutdown and restore '%s' database and indexes from a backup";

	private final String toolingId;
	private final String repositoryId;
	private final Map<Long, RepositoryCommitNotification> commitNotifications = new MapMaker().makeMap();
	private Health health = Health.RED;
	private String diagnosis;
	
	CDOBasedRepository(String repositoryId, String toolingId, int mergeMaxResults, Environment env) {
		super(env);
		this.toolingId = toolingId;
		this.repositoryId = repositoryId;
		getCdoRepository().getRepository().addCommitInfoHandler(this);
		final ObjectMapper mapper = service(ObjectMapper.class);
		initIndex(mapper);
		initializeBranchingSupport(mergeMaxResults);
		bind(Repository.class, this);
		checkHealth();
	}

	@Override
	public void checkHealth() {
		calculateHealth(Branch.MAIN_PATH);
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
	
	private void initializeBranchingSupport(int mergeMaxResults) {
		final CDOBranchManagerImpl branchManager = new CDOBranchManagerImpl(this);
		bind(BranchManager.class, branchManager);
		bind(BranchReplicator.class, branchManager);
		
		
		final ReviewConfiguration reviewConfiguration = getDelegate().service(SnowOwlConfiguration.class).getModuleConfig(ReviewConfiguration.class);
		final ReviewManagerImpl reviewManager = new ReviewManagerImpl(this, reviewConfiguration);
		bind(ReviewManager.class, reviewManager);

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
		types.add(CommitInfoDocument.class);
		
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
	public void doDispose() {
		getCdoRepository().getRepository().removeCommitInfoHandler(this);
		getIndex().admin().close();
	}
	
	
	@Override
	protected Environment getDelegate() {
		return (Environment) super.getDelegate();
	}
	
	
	@Override
	@SuppressWarnings("restriction")
	public void handleCommitInfo(CDOCommitInfo commitInfo) {
		if (!(commitInfo instanceof org.eclipse.emf.cdo.internal.common.commit.FailureCommitInfo)) {
			final CDOBranch branch = commitInfo.getBranch();
			final long commitTimestamp = commitInfo.getTimeStamp();
			((CDOBranchManagerImpl) service(BranchManager.class)).handleCommit(branch.getID(), commitTimestamp);
			// send out the currently enqueued commit notification, if there is any (import might skip sending commit notifications until a certain point)
			RepositoryCommitNotification notification = commitNotifications.remove(commitTimestamp);
			if (notification == null) {
				// make sure we always send out commit notification
				// required in case of manual commit notifications via CDO API
				notification = new RepositoryCommitNotification(id(),
					CDOCommitInfoUtils.getUuid(commitInfo),
					branch.getPathName(),
					commitInfo.getTimeStamp(),
					commitInfo.getUserID(),
					commitInfo.getComment(),
					Collections.emptyList(),
					Collections.emptyList(),
					Collections.emptyList());
			}
			notification.publish(events());
        }
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
	
	private void calculateHealth(String branch) { 
		final List<CDOCommitInfo> cdoCommits = getCDOCommitInfos(branch);
		final CommitInfos indexCommits = getIndexCommits(branch);
		
		boolean emptyDatabase = cdoCommits.isEmpty();
		boolean emptyIndex = indexCommits.isEmpty();

		if (emptyDatabase && emptyIndex) {
			// empty dataset, OK
			setHealth(Health.GREEN, null); 
		} else if (emptyDatabase && !emptyIndex) {
			setHealth(Health.RED, String.format(RESTORE_DIAGNOSIS, id()));
		} else if (!emptyDatabase && emptyIndex) {
			setHealth(Health.RED, String.format(REINDEX_DIAGNOSIS_TEMPLATE, id(), id(), ""));
		} else if (!emptyDatabase && !emptyIndex) {
			final String diagnosis = validateCommitConsistency(cdoCommits, indexCommits);
			if (Strings.isNullOrEmpty(diagnosis)) {
				setHealth(Health.GREEN, null);	
			} else {
				setHealth(Health.RED, diagnosis);
			}
		} else {
			throw new IllegalStateException("Should not happen");
		}
	}

	private String validateCommitConsistency(List<CDOCommitInfo> cdoCommits, CommitInfos indexCommits) {
		final TreeMap<Long, CDOCommitInfo> cdoCommitsByTimestamp = new TreeMap<>();
		final TreeMap<Long, CommitInfo> indexCommitsByTimestamp = new TreeMap<>();
		
		cdoCommits.forEach(c -> cdoCommitsByTimestamp.put(c.getTimeStamp(), c));
		indexCommits.forEach(c -> indexCommitsByTimestamp.put(c.getTimeStamp(), c));

		long firstMissingCdoCommitTimestamp = -1;
		
		for (Long nextCdoCommitTimestamp : cdoCommitsByTimestamp.navigableKeySet()) {
			final CDOCommitInfo nextCdoCommit = cdoCommitsByTimestamp.get(nextCdoCommitTimestamp);
			
			final Entry<Long, CommitInfo> firstIndexCommit = indexCommitsByTimestamp.firstEntry();
			if (firstIndexCommit != null) {
				final Long nextIndexCommitTimestamp = firstIndexCommit.getKey();
				final CommitInfo indexCommit = firstIndexCommit.getValue();
				if (nextIndexCommitTimestamp.equals(nextCdoCommitTimestamp)) {
					// cdo commit is present in the index remove index commit from the treemap
					indexCommitsByTimestamp.remove(nextIndexCommitTimestamp);
				} else if (nextIndexCommitTimestamp > nextCdoCommitTimestamp) {
					// importers can create batch commits using the same UUID in cdo which overrides subsequent index commits and results in a single index commit at the end
					final String commitId = CDOCommitInfoUtils.getUuid(nextCdoCommit.getComment());
					if (!Objects.equals(commitId, indexCommit.getId())) {
						return String.format(RESTORE_DIAGNOSIS, id());
					}
				} else if (nextIndexCommitTimestamp < nextCdoCommitTimestamp) {
					return String.format(RESTORE_DIAGNOSIS, id());
				}
			} else {
				// first cdo commit from where reindex should be invoked  
				if (firstMissingCdoCommitTimestamp == -1) {
					firstMissingCdoCommitTimestamp = nextCdoCommitTimestamp;
				}
			}
		}
		
		if (firstMissingCdoCommitTimestamp != -1) {
			return String.format(REINDEX_DIAGNOSIS_TEMPLATE, id(), id(), firstMissingCdoCommitTimestamp);
		}
		
		return null;
	}

	@Override
	public long getHeadTimestampForDatabase() {
		List<CDOCommitInfo> cdoCommitInfos = getCDOCommitInfos(IBranchPath.MAIN_BRANCH);
		return cdoCommitInfos.isEmpty() ? 0 : Iterables.getLast(cdoCommitInfos).getTimeStamp();
	}
	
	@Override
	public long getHeadTimestampForIndex() {
		CommitInfos indexCommitInfos = getIndexCommits(IBranchPath.MAIN_BRANCH);
		return indexCommitInfos.getTotal() == 0 ? 0 : Iterables.getLast(indexCommitInfos).getTimeStamp();
	}
	
	private List<CDOCommitInfo> getCDOCommitInfos(String mainBranchPath) {
		
		long baseTimestamp = getBaseTimestamp(getCdoMainBranch());
		long headTimestamp = getHeadTimestamp(getCdoMainBranch());
		Map<String, IBranchPath> branchPathMap = ImmutableMap.of(repositoryId, BranchPathUtils.createPath(mainBranchPath));

		final CDOCommitInfoQuery query = new CDOCommitInfoQuery(branchPathMap)
											.setStartTime(baseTimestamp)
											.setEndTime(headTimestamp);
		
		final ConsumeAllCommitInfoHandler handler = new ConsumeAllCommitInfoHandler();
		CDOCommitInfoUtils.getCommitInfos(query, handler);
		
		return handler.getInfos();
	}

	private CommitInfos getIndexCommits(final String branch) {
		final RepositoryContext repositoryContext = new DefaultRepositoryContext(this, this);
		return getIndex().read(new IndexRead<CommitInfos>() {
			@Override
			public CommitInfos execute(Searcher index) throws IOException {
				return RepositoryRequests.commitInfos().prepareSearchCommitInfo()
					.all()
					.filterByBranch(branch)
					.build()
					.execute(DelegatingRepositoryContext
							.basedOn(repositoryContext)
							.bind(Searcher.class, index)
							.build());
			}
		});
	}
}
