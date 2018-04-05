/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.request.version;

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions.CREATE_VERSION;
import static com.b2international.snowowl.datastore.version.TagConfigurationBuilder.createForToolingId;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.size;
import static com.google.common.collect.Iterables.tryFind;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static java.text.MessageFormat.format;
import static org.eclipse.core.runtime.SubMonitor.convert;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.collections.Collections3;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CodeSystemUtils;
import com.b2international.snowowl.datastore.ICodeSystemVersion;
import com.b2international.snowowl.datastore.cdo.CDOServerCommitBuilder;
import com.b2international.snowowl.datastore.cdo.CDOTransactionAggregator;
import com.b2international.snowowl.datastore.cdo.ICDOTransactionAggregator;
import com.b2international.snowowl.datastore.oplock.IOperationLockManager;
import com.b2international.snowowl.datastore.oplock.OperationLockException;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContext;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreOperationLockException;
import com.b2international.snowowl.datastore.oplock.impl.IDatastoreOperationLockManager;
import com.b2international.snowowl.datastore.oplock.impl.SingleRepositoryAndBranchLockTarget;
import com.b2international.snowowl.datastore.remotejobs.RemoteJob;
import com.b2international.snowowl.datastore.version.ITagConfiguration;
import com.b2international.snowowl.datastore.version.ITagService;
import com.b2international.snowowl.datastore.version.IVersioningManager;
import com.b2international.snowowl.datastore.version.PublishOperationConfiguration;
import com.b2international.snowowl.datastore.version.VersioningManagerBroker;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemVersionSearchRequestBuilder;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @since 5.7
 */
final class CodeSystemVersionCreateRequest implements Request<ServiceProvider, Boolean> {

	private static final long serialVersionUID = 1L;

	private static final int TASK_WORK_STEP = 6;
	private static final String NEW_VERSION_COMMIT_COMMENT_TEMPLATE = "Created new version ''{0}'' for {1}.";
	private static final String ADJUST_EFFECTIVE_TIME_COMMIT_COMMENT_TEMPLATE = "Adjusted effective time to ''{0}'' for {1} version ''{2}''.";
	
	@NotEmpty
	@JsonProperty
	private String versionId;
	
	@JsonProperty
	private String description;
	
	@JsonProperty
	private String parentBranchPath;
	
	@NotNull
	@JsonProperty
	private Date effectiveTime;
	
	@NotEmpty
	@JsonProperty
	private String codeSystemShortName;
	
	@NotEmpty
	@JsonProperty
	private String primaryToolingId;
	
	private Collection<String> toolingIds = Collections.emptySet();

	// lock props
	private DatastoreLockContext lockContext;
	private Map<String, SingleRepositoryAndBranchLockTarget> lockTargets;
	
	@Override
	public Boolean execute(ServiceProvider context) {
		final IProgressMonitor monitor = context.service(IProgressMonitor.class);
		final RemoteJob job = context.service(RemoteJob.class);
		final String user = job.getUser();
		
		acquireLocks(context, user);
		
		try ( final ICDOTransactionAggregator aggregator = CDOTransactionAggregator.create(Lists.<CDOTransaction>newArrayList()); ) {
			final IProgressMonitor subMonitor = convert(monitor, TASK_WORK_STEP * size(toolingIds) + 1);
			
			final Map<String, Collection<ICodeSystemVersion>> existingVersions = getExistingVersions(context);
			final Map<String, Boolean> performTagPerToolingFeatures = getTagPreferences(existingVersions);
			
			subMonitor.worked(1);
			
			// create version managers
			final Map<String, IVersioningManager> versioningManagers = Maps.toMap(toolingIds, new Function<String, IVersioningManager>() {
				@Override
				public IVersioningManager apply(String toolingId) {
					return VersioningManagerBroker.INSTANCE.createVersioningManager(toolingId);
				}
			});
			
			doPublish(aggregator, versioningManagers, subMonitor);
			doCommitChanges(user, aggregator, subMonitor, existingVersions);
			doTag(user, performTagPerToolingFeatures, subMonitor);
			postCommit(aggregator, versioningManagers, monitor);
		} catch (final SnowowlServiceException e) {
			throw new SnowowlRuntimeException("Error occurred during versioning.", e);
		} finally {
			releaseLocks(context);
			if (null != monitor) {
				monitor.done();
			}
		}
		return Boolean.TRUE;
	}
	
	void setCodeSystemShortName(String codeSystemShortName) {
		this.codeSystemShortName = codeSystemShortName;
	}
	
	void setEffectiveTime(Date effectiveTime) {
		this.effectiveTime = effectiveTime;
	}
	
	void setParentBranchPath(String parentBranchPath) {
		this.parentBranchPath = parentBranchPath;
	}
	
	void setPrimaryToolingId(String primaryToolingId) {
		this.primaryToolingId = primaryToolingId;
	}
	
	void setToolingIds(Collection<String> toolingIds) {
		this.toolingIds = Collections3.toImmutableSet(toolingIds);
	}
	
	void setDescription(String description) {
		this.description = description;
	}
	
	void setVersionId(String versionId) {
		this.versionId = versionId;
	}
	
	private void acquireLocks(ServiceProvider context, String user) {
		try {
			this.lockContext = createLockContext(user);
			this.lockTargets = createLockTargets();
			context.service(IDatastoreOperationLockManager.class).lock(lockContext, IOperationLockManager.IMMEDIATE, lockTargets.values());
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

	private Map<String, SingleRepositoryAndBranchLockTarget> createLockTargets() {
		final Builder<String, SingleRepositoryAndBranchLockTarget> lockTargetsBuilder = ImmutableMap.builder();
		for (final String toolingId : toolingIds) {
			lockTargetsBuilder.put(toolingId, createLockTarget(toolingId));
		}
		final Map<String,SingleRepositoryAndBranchLockTarget> lockTargets = lockTargetsBuilder.build();
		return lockTargets;
	}
	
	private void releaseLocks(ServiceProvider context) {
		context.service(IDatastoreOperationLockManager.class).unlock(lockContext, lockTargets.values());
	}
	
	private void doPublish(final ICDOTransactionAggregator aggregator, final Map<String, IVersioningManager> versioningManagers, final IProgressMonitor monitor) throws SnowowlServiceException {
		final PublishOperationConfiguration config = new PublishOperationConfiguration(codeSystemShortName, versionId, description, effectiveTime, parentBranchPath);
		for (final String toolingId : toolingIds) {
			versioningManagers.get(toolingId).publish(aggregator, toolingId, config, monitor);
		}
	}

	private DatastoreLockContext createLockContext(final String userId) {
		return new DatastoreLockContext(userId, CREATE_VERSION);
	}
	
	private SingleRepositoryAndBranchLockTarget createLockTarget(final String toolingId) {
		return createLockTarget(checkNotNull(toolingId, "toolingId"), BranchPathUtils.createPath(parentBranchPath));
	}
	
	private SingleRepositoryAndBranchLockTarget createLockTarget(final String toolingId, final IBranchPath branchPath) {
		return new SingleRepositoryAndBranchLockTarget(
				getRepositoryUuid(checkNotNull(toolingId, "toolingId")), 
				checkNotNull(branchPath, "branchPath"));
	}
	
	private String getRepositoryUuid(final String toolingId) {
		return CodeSystemUtils.getRepositoryUuid(checkNotNull(toolingId, "toolingId"));
	}
	
	/**Commits the change set based on the transaction aggregator content. */
	private void doCommitChanges(
			final String user,
			final ICDOTransactionAggregator aggregator,
			final IProgressMonitor monitor,
			final Map<String, Collection<ICodeSystemVersion>> existingVersions) throws SnowowlServiceException {
		try {
			new CDOServerCommitBuilder(user, getCommitComment(existingVersions), aggregator)
				.parentContextDescription(CREATE_VERSION)
				.commit();
		} catch (final CommitException e) {
			throw new SnowowlServiceException(e.getMessage(), e);
		} finally {
			if (null != monitor) {
				monitor.worked(toolingIds.size() * 2);
			}
		}
	}
	
	/**Returns with the commit comment for the version operation. */
	private String getCommitComment(final Map<String, Collection<ICodeSystemVersion>> existingVersions) {
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
	private void doTag(final String user, final Map<String, Boolean> performTagPerToolingFeatures, final IProgressMonitor monitor) {
		final ITagService tagService = getServiceForClass(ITagService.class);

		for (final String toolingId : toolingIds) {
			if (performTagPerToolingFeatures.get(toolingId)) {
				final ITagConfiguration tagConfiguration = createTagConfiguration(user, toolingId);
				tagService.tag(tagConfiguration);
				monitor.worked(1);
			}
		}
	}
	
	/*
	 * Performs actions after the successful commit. 
	 */
	private void postCommit(final ICDOTransactionAggregator aggregator, final Map<String, IVersioningManager> versioningManagers, final IProgressMonitor monitor) throws SnowowlServiceException {
		for (final String toolingId : toolingIds) {
			versioningManagers.get(toolingId).postCommit();
			if (null != monitor) {
				monitor.worked(1);
			}
		}
	}
	
	private ITagConfiguration createTagConfiguration(final String user, final String toolingId) {
		checkNotNull(toolingId, "toolingId");
		return createForToolingId(toolingId, versionId)
				.setUserId(user)
				.setBranchPath(BranchPathUtils.createPath(parentBranchPath))
				.build();
	}
	
	private Map<String, Collection<ICodeSystemVersion>> getExistingVersions(ServiceProvider context) {
		final Map<String, Collection<ICodeSystemVersion>> existingVersions = Maps.newHashMap();
		
		for (final String toolingId : toolingIds) {
			final String repositoryId = CodeSystemUtils.getRepositoryUuid(toolingId);
			
			final CodeSystemVersionSearchRequestBuilder requestBuilder = CodeSystemRequests.prepareSearchCodeSystemVersion();
			
			if (toolingId.equals(primaryToolingId)) {
				requestBuilder.filterByCodeSystemShortName(codeSystemShortName);
			}
			
			final Set<ICodeSystemVersion> versions = newHashSet();
			versions.addAll(requestBuilder
					.all()
					.build(repositoryId)
					.execute(context.service(IEventBus.class))
					.getSync()
					.getItems());
			
			existingVersions.put(toolingId, versions);
		}
		
		return existingVersions;
	}
	
	private Map<String, Boolean> getTagPreferences(final Map<String, Collection<ICodeSystemVersion>> existingVersions) {
		final Map<String, Boolean> shouldPerformTagPerToolingFeature = newHashMap();
		
		for (final String toolingId : toolingIds) {
			shouldPerformTagPerToolingFeature.put(toolingId, !tryFind(existingVersions.get(toolingId), new Predicate<ICodeSystemVersion>() {
				@Override public boolean apply(final ICodeSystemVersion version) {
					return checkNotNull(version).getVersionId().equals(versionId);
				}
			}).isPresent());
		}
		
		return shouldPerformTagPerToolingFeature;
	}
	
	private String getToolingName(final String toolingId) {
		return CoreTerminologyBroker.getInstance().getTerminologyName(toolingId);
	}
	
}
