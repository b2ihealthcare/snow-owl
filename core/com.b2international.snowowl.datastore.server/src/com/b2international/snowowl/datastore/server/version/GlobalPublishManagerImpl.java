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
package com.b2international.snowowl.datastore.server.version;

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.datastore.cdo.CDOTransactionAggregator.create;
import static com.b2international.snowowl.datastore.oplock.IOperationLockManager.IMMEDIATE;
import static com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions.CREATE_VERSION;
import static com.b2international.snowowl.datastore.version.TagConfigurationBuilder.createForToolingId;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.getFirst;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.size;
import static com.google.common.collect.Iterables.tryFind;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.text.MessageFormat.format;
import static org.eclipse.core.runtime.Status.OK_STATUS;
import static org.eclipse.core.runtime.SubMonitor.convert;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.slf4j.Logger;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CodeSystemUtils;
import com.b2international.snowowl.datastore.ICodeSystemVersion;
import com.b2international.snowowl.datastore.cdo.ICDOTransactionAggregator;
import com.b2international.snowowl.datastore.oplock.OperationLockException;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContext;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreOperationLockException;
import com.b2international.snowowl.datastore.oplock.impl.IDatastoreOperationLockManager;
import com.b2international.snowowl.datastore.oplock.impl.SingleRepositoryAndBranchLockTarget;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobUtils;
import com.b2international.snowowl.datastore.server.CDOServerCommitBuilder;
import com.b2international.snowowl.datastore.server.remotejobs.AbstractRemoteJob;
import com.b2international.snowowl.datastore.version.GlobalPublishManager;
import com.b2international.snowowl.datastore.version.IPublishOperationConfiguration;
import com.b2international.snowowl.datastore.version.ITagConfiguration;
import com.b2international.snowowl.datastore.version.ITagService;
import com.b2international.snowowl.datastore.version.IVersioningManager;
import com.b2international.snowowl.datastore.version.VersioningManagerBroker;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemVersionSearchRequestBuilder;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Server-side {@link GlobalPublishManager} service implementation.
 *
 */
public class GlobalPublishManagerImpl implements GlobalPublishManager {

	private static final Logger LOGGER = getLogger(GlobalPublishManagerImpl.class);
	
	private static final int TASK_WORK_STEP = 6;
	private static final String NEW_VERSION_COMMIT_COMMENT_TEMPLATE = "Created new version ''{0}'' for {1}.";
	private static final String ADJUST_EFFECTIVE_TIME_COMMIT_COMMENT_TEMPLATE = "Adjusted effective time to ''{0}'' for {1} version ''{2}''.";

	@Override
	public void publish(final IPublishOperationConfiguration configuration) throws SnowowlServiceException {
		
		
		final AbstractRemoteJob job = new AbstractRemoteJob(buildTaskName(configuration)) {
			protected IStatus runWithListenableMonitor(final IProgressMonitor monitor) {
				ConfigurationThreadLocal.setConfiguration(configuration);
				final DatastoreLockContext lockContext = createLockContext(configuration.getUserId());
				final Map<String, SingleRepositoryAndBranchLockTarget> lockTargets = createLockTargets(configuration);
				
				acquireLocks(lockContext, lockTargets);
				
				try ( final ICDOTransactionAggregator aggregator = create(Lists.<CDOTransaction>newArrayList()); ) {
					final IProgressMonitor subMonitor = convert(monitor, TASK_WORK_STEP * size(configuration) + 1);
					
					final Map<String, Collection<ICodeSystemVersion>> existingVersions = getExistingVersions();
					final Map<String, Boolean> performTagPerToolingFeatures = getTagPreferences(existingVersions);
					
					subMonitor.worked(1);
					
					// create version managers
					final Map<String, IVersioningManager> versioningManagers = Maps.toMap(configuration.getToolingIds(), new Function<String, IVersioningManager>() {
						@Override
						public IVersioningManager apply(String toolingId) {
							return VersioningManagerBroker.INSTANCE.createVersioningManager(toolingId);
						}
					});
					
					doPublish(aggregator, versioningManagers, subMonitor);
					doCommitChanges(aggregator, subMonitor, existingVersions);
					doTag(performTagPerToolingFeatures, subMonitor);
					postCommit(aggregator, versioningManagers, monitor);
					
					return OK_STATUS;
				} catch (final SnowowlServiceException e) {
					LOGGER.error("Error occurred during versioning.", e);
					throw new SnowowlRuntimeException("Error occurred during versioning.", e);
				} finally {
					getLockManager().unlock(lockContext, lockTargets.values());
					ConfigurationThreadLocal.reset();
					if (null != monitor) {
						monitor.done();
					}
				}
			}

			private void acquireLocks(final DatastoreLockContext lockContext, final Map<String, SingleRepositoryAndBranchLockTarget> lockTargets) {
				try {
					getLockManager().lock(lockContext, IMMEDIATE, lockTargets.values());
				} catch (final OperationLockException e) {
					if (e instanceof DatastoreOperationLockException) {
						throw new DatastoreOperationLockException(String.format("Failed to acquire locks for versioning because %s.", e.getMessage())); 
					} else {
						throw new DatastoreOperationLockException("Error while trying to acquire lock on repository for versioning.");
					}
				} catch (final InterruptedException e) {
					throw new SnowowlRuntimeException(e);
				}
			}

			private Map<String, SingleRepositoryAndBranchLockTarget> createLockTargets(final IPublishOperationConfiguration configuration) {
				final Builder<String, SingleRepositoryAndBranchLockTarget> lockTargetsBuilder = ImmutableMap.builder();
				for (final String toolingId : configuration) {
					lockTargetsBuilder.put(toolingId, createLockTarget(toolingId));
				}
				final Map<String,SingleRepositoryAndBranchLockTarget> lockTargets = lockTargetsBuilder.build();
				return lockTargets;
			}

		};
		
		RemoteJobUtils.configureProperties(job, configuration.getUserId(), null, configuration.getRemoteJobId());
		job.schedule();
	}
	
	private String buildTaskName(final IPublishOperationConfiguration configuration) {
		checkState(!isEmpty(configuration));
		final List<String> toolingIds = newArrayList(configuration.getToolingIds());
		final String versionId = configuration.getVersionId();
		final StringBuilder sb = new StringBuilder("Creating version '");
		sb.append(versionId);
		sb.append("' for");
		if (toolingIds.size() == 1) {
			sb.append(" ");
			sb.append(getToolingName(toolingIds.get(0)));
		} else {
			for (int i = 0; i < toolingIds.size(); i++) {
				sb.append(" ");
				sb.append(getToolingName(toolingIds.get(i)));
				if (toolingIds.size() - 2 == i) {
					sb.append(" and");
				} else if (toolingIds.size() - 2 > i) {
					sb.append(",");
				}
			}
		}
		sb.append(".");
		return sb.toString();
	}
	
	private void doPublish(final ICDOTransactionAggregator aggregator, final Map<String, IVersioningManager> versioningManagers, final IProgressMonitor monitor) throws SnowowlServiceException {
		final IPublishOperationConfiguration configuration = ConfigurationThreadLocal.getConfiguration();
		for (final String toolingId : configuration) {
			versioningManagers.get(toolingId).publish(aggregator, toolingId, configuration, monitor);
		}
	}

	private DatastoreLockContext createLockContext(final String userId) {
		return new DatastoreLockContext(userId, CREATE_VERSION);
	}
	
	private SingleRepositoryAndBranchLockTarget createLockTarget(final String toolingId) {
		final IPublishOperationConfiguration configuration = ConfigurationThreadLocal.getConfiguration();
		final String branchPath = configuration.getParentBranchPath();
		return createLockTarget(checkNotNull(toolingId, "toolingId"), BranchPathUtils.createPath(branchPath));
	}
	
	private SingleRepositoryAndBranchLockTarget createLockTarget(final String toolingId, final IBranchPath branchPath) {
		return new SingleRepositoryAndBranchLockTarget(
				getRepositoryUuid(checkNotNull(toolingId, "toolingId")), 
				checkNotNull(branchPath, "branchPath"));
	}
	
	private String getRepositoryUuid(final String toolingId) {
		return CodeSystemUtils.getRepositoryUuid(checkNotNull(toolingId, "toolingId"));
	}
	
	private IDatastoreOperationLockManager getLockManager() {
		return getServiceForClass(IDatastoreOperationLockManager.class);
	}
	
	/**Commits the change set based on the transaction aggregator content. */
	private void doCommitChanges(final ICDOTransactionAggregator aggregator, final IProgressMonitor monitor,
			final Map<String, Collection<ICodeSystemVersion>> existingVersions) throws SnowowlServiceException {
		try {
			LOGGER.info("Persisting changes...");
			new CDOServerCommitBuilder(ConfigurationThreadLocal.getConfiguration().getUserId(), getCommitComment(existingVersions), aggregator)
				.parentContextDescription(CREATE_VERSION)
				.commit();
			LOGGER.info("Changes have been successfully persisted.");
		} catch (final CommitException e) {
			throw new SnowowlServiceException(e.getMessage(), e);
		} finally {
			if (null != monitor) {
				monitor.worked(size(ConfigurationThreadLocal.getConfiguration()) * 2);
			}
		}
	}
	
	/**Returns with the commit comment for the version operation. */
	private String getCommitComment(final Map<String, Collection<ICodeSystemVersion>> existingVersions) {
		final IPublishOperationConfiguration configuration = ConfigurationThreadLocal.getConfiguration();
		final String primaryToolingId = checkNotNull(getFirst(configuration.getToolingIds(), null),
				"No tooling ID were available for the publication process.");
		
		final Date effectiveTime = configuration.getEffectiveTime();
		final String versionId = configuration.getVersionId();
		final String toolingName = getToolingName(primaryToolingId);
		
		final Optional<ICodeSystemVersion> optional = FluentIterable
				.from(existingVersions.get(primaryToolingId))
				.firstMatch(new Predicate<ICodeSystemVersion>() {
					@Override
					public boolean apply(ICodeSystemVersion input) {
						return input.getVersionId().equals(versionId);
					}
				});
		
		if (optional.isPresent()) {
			return format(ADJUST_EFFECTIVE_TIME_COMMIT_COMMENT_TEMPLATE, EffectiveTimes.format(effectiveTime), toolingName, versionId);
		} else {
			return format(NEW_VERSION_COMMIT_COMMENT_TEMPLATE, versionId, toolingName);
		}
	}
	
	/**Performs the tagging in the repository. Creates the corresponding branches and sets up the index infrastructure.*/
	private void doTag(final Map<String, Boolean> performTagPerToolingFeatures, final IProgressMonitor monitor) {
		final IPublishOperationConfiguration publishConfiguration = ConfigurationThreadLocal.getConfiguration();
		final Collection<String> toolingIds = publishConfiguration.getToolingIds();
		final ITagService tagService = getServiceForClass(ITagService.class);

		for (final String toolingId : toolingIds) {
			if (performTagPerToolingFeatures.get(toolingId)) {
				final ITagConfiguration tagConfiguration = createTagConfiguration(toolingId);
				tagService.tag(tagConfiguration);
				monitor.worked(1);
			}
		}
	}
	
	/*
	 * Performs actions after the successful commit. 
	 */
	private void postCommit(final ICDOTransactionAggregator aggregator, final Map<String, IVersioningManager> versioningManagers, final IProgressMonitor monitor) throws SnowowlServiceException {
		final IPublishOperationConfiguration configuration = ConfigurationThreadLocal.getConfiguration();
		for (final String toolingId : configuration) {
			versioningManagers.get(toolingId).postCommit();
			if (null != monitor) {
				monitor.worked(1);
			}
		}
	}
	
	private ITagConfiguration createTagConfiguration(final String toolingId) {
		checkNotNull(toolingId, "toolingId");
		
		final IPublishOperationConfiguration configuration = ConfigurationThreadLocal.getConfiguration();
		final String branchPath = configuration.getParentBranchPath();
		
		return createForToolingId(toolingId, configuration.getVersionId())
				.setUserId(configuration.getUserId())
				.setBranchPath(BranchPathUtils.createPath(branchPath))
				.build();
	}
	
	private Map<String, Collection<ICodeSystemVersion>> getExistingVersions() {
		final Map<String, Collection<ICodeSystemVersion>> existingVersions = Maps.newHashMap();
		final IPublishOperationConfiguration configuration = ConfigurationThreadLocal.getConfiguration();
		
		for (final String toolingId : configuration.getToolingIds()) {
			final String shortName = ConfigurationThreadLocal.getConfiguration().getCodeSystemShortName();
			final String repositoryUuid = CodeSystemUtils.getRepositoryUuid(toolingId);
			
			final CodeSystemVersionSearchRequestBuilder requestBuilder = new CodeSystemRequests(repositoryUuid)
					.prepareSearchCodeSystemVersion();
			
			if (toolingId.equals(configuration.getPrimaryToolingId())) {
				requestBuilder.setCodeSystemShortName(shortName);
			}
			
			final List<ICodeSystemVersion> versions = requestBuilder
					.build(IBranchPath.MAIN_BRANCH)
					.executeSync(getEventBus())
					.getItems();
			
			existingVersions.put(toolingId, versions);
		}
		
		return existingVersions;
	}
	
	private Map<String, Boolean> getTagPreferences(final Map<String, Collection<ICodeSystemVersion>> existingVersions) {
		final IPublishOperationConfiguration configuration = ConfigurationThreadLocal.getConfiguration();
		final Map<String, Boolean> shouldPerformTagPerToolingFeature = newHashMap();
		
		for (final String toolingId : configuration.getToolingIds()) {
			shouldPerformTagPerToolingFeature.put(toolingId, !tryFind(existingVersions.get(toolingId), new Predicate<ICodeSystemVersion>() {
				@Override public boolean apply(final ICodeSystemVersion version) {
					return checkNotNull(version).getVersionId().equals(configuration.getVersionId());
				}
			}).isPresent());
		}
		
		return shouldPerformTagPerToolingFeature;
	}
	
	private IEventBus getEventBus() {
		return ApplicationContext.getInstance().getService(IEventBus.class);
	}

	private String getToolingName(final String toolingId) {
		return CoreTerminologyBroker.getInstance().getTerminologyName(toolingId);
	}
	
	/**Class for storing the configuration in the thread local.*/
	static final class ConfigurationThreadLocal {
		
		private static final ThreadLocal<IPublishOperationConfiguration> CONFIGURATION_THREAD_LOCAL = // 
				new ThreadLocal<IPublishOperationConfiguration>();
		
		static void setConfiguration(final IPublishOperationConfiguration configuration) {
			CONFIGURATION_THREAD_LOCAL.set(configuration);
		}
		
		static IPublishOperationConfiguration getConfiguration() {
			return CONFIGURATION_THREAD_LOCAL.get();
		}
		
		static void reset() {
			CONFIGURATION_THREAD_LOCAL.set(null);
		}
	}
	
}