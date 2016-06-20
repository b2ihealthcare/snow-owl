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
package com.b2international.snowowl.datastore.version;

import static com.b2international.commons.status.Statuses.serializableOk;
import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.datastore.BranchPathUtils.createMainPath;
import static com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions.CONFIGURE_VERSION;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Suppliers.memoize;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.getFirst;
import static com.google.common.collect.Iterables.tryFind;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.uniqueIndex;
import static java.text.MessageFormat.format;
import static java.util.Collections.unmodifiableMap;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.collections.Procedure;
import com.b2international.commons.concurrent.ConcurrentCollectionUtils;
import com.b2international.commons.status.SerializableStatus;
import com.b2international.commons.status.Statuses;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CodeSystemUtils;
import com.b2international.snowowl.datastore.ContentAvailabilityInfoManager;
import com.b2international.snowowl.datastore.DatastoreActivator;
import com.b2international.snowowl.datastore.ICodeSystemVersion;
import com.b2international.snowowl.datastore.LatestCodeSystemVersionUtils;
import com.b2international.snowowl.datastore.cdo.ICDOBranchActionManager;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.oplock.IOperationLockManager;
import com.b2international.snowowl.datastore.oplock.OperationLockException;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContext;
import com.b2international.snowowl.datastore.oplock.impl.IDatastoreOperationLockManager;
import com.b2international.snowowl.datastore.oplock.impl.SingleRepositoryAndBranchLockTarget;
import com.b2international.snowowl.datastore.remotejobs.IRemoteJobManager;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobEventBusHandler;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobUtils;
import com.b2international.snowowl.datastore.tasks.TaskManager;
import com.b2international.snowowl.datastore.validation.TimeValidator;
import com.b2international.snowowl.eventbus.IEventBus;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

/**
 * Stateful versioning service implementation.
 * <p>Although this service is responsible for the whole versioning process it does not ensure locking on the underlying repositories.
 * <br>Clients are responsible to acquire lock in the desired repository.
 *
 */
public class VersioningService implements IVersioningService {

	private static final Logger LOGGER = LoggerFactory.getLogger(VersioningService.class);
	private static final String NO_DETAILS_ERROR_MSG = "Cannot create new version.";
	private static final String NO_CHANGES_TEMPLATE = "Cannot create new version.\nNo changes have been made on ''{0}'' version for {1}.";
	private static final String NO_CONTENT_TEMPLATE = "Cannot create new version.\nContent is not available for {0}.";
	private static final String SUCCESS_TEMPLATE = "{0} has been successfully versioned with ''{1}''.";
	private static final String EFFECTIVE_TIME_ADJUSTED_TEMPLATE = "Effective time has been successfully adjusted on unpublished components for {0}.";
	/**Application specific reserved words.*/
	private static final Collection<String> APPLICATION_RESERVED_WORDS = Collections.unmodifiableSet(Sets.newHashSet(
			IBranchPath.MAIN_BRANCH,
			ICodeSystemVersion.UNVERSIONED,
			ICodeSystemVersion.INITIAL_STATE
			));
	
	
	private final PublishOperationConfiguration configuration;
	private final Map<String, Collection<ICodeSystemVersion>> existingVersions;
	private Map<String, DatastoreLockContext> lockContexts;
	private Map<String, SingleRepositoryAndBranchLockTarget> lockTargets;
	private final Map<String, Supplier<ICodeSystemVersion>> currentVersionSuppliers;
	private final AtomicBoolean locked; 
	
	/**Creates a new versioning service for the given tooling feature.*/
	public VersioningService(final String toolingId, final String... otherToolingIds) {
		
		
		
		configuration = new PublishOperationConfiguration(toolingId, otherToolingIds);
		existingVersions = initExistingVersions(configuration.getToolingIds());
		currentVersionSuppliers = initCurrentVersionSuppliers(configuration.getToolingIds());
		lockContexts = newHashMap();
		lockTargets = newHashMap();
		locked = new AtomicBoolean();
	}
	
	public VersioningService(final String toolingId, final Map<String, Collection<ICodeSystemVersion>> existingVersions, final String... otherToolingIds) {
		this.configuration = new PublishOperationConfiguration(toolingId, otherToolingIds);
		this.existingVersions = existingVersions;
		currentVersionSuppliers = initCurrentVersionSuppliers(configuration.getToolingIds());
		lockContexts = newHashMap();
		lockTargets = newHashMap();
		locked = new AtomicBoolean();
	}

	@Override
	public Collection<ICodeSystemVersion> getExistingVersions(final String toolingId) {
		checkNotNull(toolingId, "toolingId");
		return existingVersions.get(toolingId);
	}

	@Override
	public IStatus configureNewVersionId(final String versionId, final boolean ignoreValidation) {
		try {
			Branch.BranchNameValidator.DEFAULT.checkName(versionId);
		} catch (BadRequestException e) {
			return Statuses.error(e.getMessage());
		}
		
		if (APPLICATION_RESERVED_WORDS.contains(versionId)) {
			return Statuses.error(String.format("Version name '%s' is reserved word.", versionId));
		}
		
		for (final String toolingId : getToolingIds()) {
			if (!ignoreValidation) {
				final Collection<String> existingVersions = Collections2.transform(VersioningService.this.getExistingVersions(toolingId), IVersionCollector.GET_VERSION_NAME_FUNC);
				if (existingVersions.contains(versionId)) {
					return Statuses.error("Name should be unique.");
				}
			}
		}
		configuration.setVersionId(versionId);
		return okStatus();
	}
	
	@Override
	public IStatus configureParentBranchPath(String parentBranchPath) {
		configuration.setParentBranchPath(parentBranchPath);
		return okStatus();
	}
	
	@Override
	public IStatus configureCodeSystemShortName(String codeSystemShortName) {
		configuration.setCodeSystemShortName(codeSystemShortName);
		return okStatus();
	}
	
	@Override
	public IStatus configureEffectiveTime(final Date effectiveTime) {
		for (final String toolingId : getToolingIds()) {
			final IStatus status = validateEffectiveTimeForVersion(toolingId, effectiveTime, getVersionId());
			if (!status.isOK()) {
				return status;
			}
		}
		configuration.setEffectiveTime(effectiveTime);
		return okStatus();
	}

	@Override
	public IStatus configureDescription(@Nullable final String description) {
		configuration.setDescription(Strings.nullToEmpty(description));
		return okStatus();
	}

	@Override
	public IStatus tag() {
		
		//TODO: this check should be performed within the publishComponents() method
		final Map<String, Boolean> performTagPerToolingFeatures = shouldPerformTag();
		try {
			publishComponents();
			return handleVersioningSuccess(performTagPerToolingFeatures.values().contains(Boolean.TRUE));
		} catch (final SnowowlServiceException e) {
			return handleVersioningFailure(e);
		}
	}

	@Override
	public String getVersionId() {
		return configuration.getVersionId();
	}
	
	@Override
	public void acquireLock() throws SnowowlServiceException {
		try {
			if (!locked.get()) {
				tryAcquireLock();
				locked.set(true);
			}
		} catch (final OperationLockException e) {
			handleException(e);
		} catch (final InterruptedException e) {
			handleException(e);
		}
	}
	
	@Override
	public void releaseLock() throws SnowowlServiceException {
		try {
			if (locked.get()) {
				tryReleaseLock();
				locked.set(false);
			}
		} catch (final OperationLockException e) {
			handleException(e);
		}
	}

	@Override
	public IStatus canCreateNewVersion() {
		try {
			return tryCheckCanCreateNewVersion();
		} catch (final VersioningException e) {
			return handleVersionException(e);
		}
	}
	
	/**
	 * (non-API)
	 * 
	 * Returns with {@code true} if effective time adjustment is supported at least on of the configured tooling features.
	 */
	public boolean isEffectiveTimeAdjustmentSupported() {
		for (final String toolingId : getToolingIds()) {
			if (CoreTerminologyBroker.getInstance().isEffectiveTimeSupported(toolingId)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * (non-API)
	 * 
	 * Returns with the primary tooling ID associated with the service.
	 */
	public String getPrimaryToolingId() {
		return checkNotNull(getFirst(getToolingIds(), null), "No tooling ID was configured for the version process.");
	}

	private IStatus validateEffectiveTimeForVersion(final String toolingId, final Date effectiveTime, final String versionId) {
		
		if (!CoreTerminologyBroker.getInstance().isEffectiveTimeSupported(toolingId)) {
			return okStatus();
		}
		
		
		if (CompareUtils.isEmpty(existingVersions.get(toolingId))) {
			return new TimeValidator().validate(effectiveTime);
		}
		
		if (IBranchPath.MAIN_BRANCH.equals(versionId) || BranchPathUtils.isMain(getCurrentBranchPath(toolingId))) {
			return new TimeValidator(getMostRecentVersionEffectiveDateTime(toolingId)).validate(effectiveTime);
		}
		
		final List<ICodeSystemVersion> allTagsWithHead = getAllVersionsWithHead(toolingId);

		final ICodeSystemVersion version = checkNotNull(find(allTagsWithHead, new Predicate<ICodeSystemVersion>() {
			@Override public boolean apply(final ICodeSystemVersion version) {
				return checkNotNull(versionId, "versionId").equals(version.getVersionId());
			}
		}), "Version cannot be found for ID: " + versionId);
		
		return new TimeValidator(version.getEffectiveDate()).validate(effectiveTime);
		
	}

	private List<ICodeSystemVersion> getAllVersionsWithHead(final String toolingId) {
		String repositoryUuid = getRepositoryUuid(toolingId);
		final List<ICodeSystemVersion> allTagsWithHead = newArrayList(existingVersions.get(toolingId));
		

		// creating HEAD CSV refs.
		Set<IBranchPath> codeSystemBranchPaths = Multimaps.index(allTagsWithHead, ICodeSystemVersion.TO_PARENT_BRANCH_PATH_FUNC).keySet();
		for (IBranchPath csBranchPath : codeSystemBranchPaths) {
			allTagsWithHead.add(0, LatestCodeSystemVersionUtils.createLatestCodeSystemVersion(repositoryUuid, csBranchPath.getPath()));
		}
		return allTagsWithHead;
	}
	
	private void checkForChanges(final String toolingId, final IBranchPath branchPath) throws NoChangesException {
		
		checkNotNull(toolingId, "toolingId");
		String branchPathName = branchPath.getPath();
		
		final long lastModificationOnBranch = getLastModificationBranch(toolingId, branchPath);
		
		if (IBranchPath.MAIN_BRANCH.equals(branchPathName)) {
			branchPathName = IBranchPath.MAIN_BRANCH + IBranchPath.SEPARATOR_CHAR + getAllVersionsWithHead(toolingId).get(1).getVersionId();
		} 
		
		final long versionBranchCreationTime = getVersionBranchCreationTime(toolingId,  branchPathName);
		
		if (lastModificationOnBranch <= versionBranchCreationTime) {
			throw new NoChangesException(getRepositoryUuid(toolingId));
		}
		
		final ICodeSystemVersion version = getVersion(toolingId, branchPath.lastSegment());
		if (null == version) {
			return;
		}
		
		if (null != version && (lastModificationOnBranch <= version.getLastUpdateDate())) {
			throw new NoChangesException(getRepositoryUuid(toolingId));
		}
	}

	private long getVersionBranchCreationTime(final String toolingId, final String versionPath) {
		
		CDOBranch branch = null;
		
		ICDOConnection cdoConnection = getServiceForClass(ICDOConnectionManager.class).getByUuid(getRepositoryUuid(toolingId));
		
		if (IBranchPath.MAIN_BRANCH.equals(versionPath)) {
			branch = cdoConnection.getMainBranch();
		} else {
			final IBranchPath versionBranchPath = BranchPathUtils.createPath(versionPath);
			branch = cdoConnection.getBranch(versionBranchPath);
			Preconditions.checkNotNull(branch, "Branch '" + versionBranchPath + "' does not exist in '" + getRepositoryUuid(toolingId) + "'.");
		}
		
		return branch.getBase().getTimeStamp();
	}
	
	@Nullable 
	private ICodeSystemVersion getVersion(final String toolingId, final String versionId) {
		final ICodeSystemVersion version = uniqueIndex(getAllVersionsWithHead(toolingId), new Function<ICodeSystemVersion, String>() {
			public String apply(final ICodeSystemVersion version) {
				return checkNotNull(version, "version").getVersionId();
			}
		}).get(versionId);
		return version;
	}
	
	private long getMostRecentVersionEffectiveDateTime(final String toolingId) {
		final List<ICodeSystemVersion> versions = newArrayList(existingVersions.get(toolingId));
		Collections.sort(versions, Collections.reverseOrder(ICodeSystemVersion.VERSION_EFFECTIVE_DATE_COMPARATOR));
		return get(versions, 0).getEffectiveDate();
	}
	
	private long getLastModificationBranch(final String toolingId, @Nullable final IBranchPath branchPath) {
		
		checkNotNull(toolingId, "toolingId");
		checkNotNull(branchPath, "branchPath");
		
		final String repositoryUuid = getRepositoryUuid(toolingId);
		ICDOBranchActionManager branchActionManager = getServiceForClass(ICDOBranchActionManager.class);
		return branchActionManager.getLastCommitTime(repositoryUuid, branchPath);
	}
	
	private HashMap<String, Collection<ICodeSystemVersion>> initExistingVersions(final Iterable<String> toolingIds) {
		final Map<String , Collection<ICodeSystemVersion>> versions = Maps.newConcurrentMap();
		ConcurrentCollectionUtils.forEach(toolingIds, new Procedure<String>() {
			protected void doApply(final String toolingId) {
				versions.put(toolingId, new VersionCollector(toolingId).getVersions());
			}
		});
		return newHashMap(versions);
	}
	
	private Map<String, Supplier<ICodeSystemVersion>> initCurrentVersionSuppliers(final Iterable<String> toolingIds) {
		final Map<String, Supplier<ICodeSystemVersion>> currentVersionSuppliers = newHashMap();
		for (final String toolingId : toolingIds) {
			final String versionIdForTooling = getActiveBranchForToolingFeature(toolingId).lastSegment();
			currentVersionSuppliers.put(toolingId, memoize(new Supplier<ICodeSystemVersion>() {
				public ICodeSystemVersion get() {
					return find(getExistingVersions(toolingId), new Predicate<ICodeSystemVersion>() {
						public boolean apply(final ICodeSystemVersion codeSystemVersion) {
							return versionIdForTooling.equals(codeSystemVersion.getVersionId());
						}
					}, LatestCodeSystemVersionUtils.createLatestCodeSystemVersion(getRepositoryUuid(toolingId)));
				}
			}));
		}
		return unmodifiableMap(currentVersionSuppliers);
	}

	/**Publishes the unpublished and changed components.*/
	private void publishComponents() throws SnowowlServiceException {
		releaseLock();

		final CountDownLatch latch = new CountDownLatch(1);
		final UUID remoteJobId = configuration.getRemoteJobId();
		final String jobAddress = RemoteJobUtils.getJobSpecificAddress(IRemoteJobManager.ADDRESS_REMOTE_JOB_COMPLETED, remoteJobId);
		getServiceForClass(IEventBus.class).registerHandler(jobAddress, new RemoteJobEventBusHandler(remoteJobId) {
			@Override
			protected void handleResult(UUID jobId, boolean cancelRequested) {
				latch.countDown();
			}
		});

		getServiceForClass(GlobalPublishManager.class).publish(configuration);
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			throw new SnowowlServiceException(e);
		}
	}
	
	/*
	 * Checks if the version id for the requested repositories (identified by their tooling id) exists or not.
	 */
	private Map<String, Boolean> shouldPerformTag() {
		final Map<String, Boolean> shouldPerformTagPerToolingFeature = newHashMap(); 
		for (final String toolingId : getToolingIds()) {
			shouldPerformTagPerToolingFeature.put(toolingId, !tryFind(existingVersions.get(toolingId), new Predicate<ICodeSystemVersion>() {
				@Override public boolean apply(final ICodeSystemVersion version) {
					return checkNotNull(version).getVersionId().equals(configuration.getVersionId());
				}
			}).isPresent());
		}
		return shouldPerformTagPerToolingFeature;
	}
	
	private IStatus handleVersionException(final VersioningException e) {
		final String toolingId = CodeSystemUtils.getSnowOwlToolingId(e.getRepositoryUuid());
		if (e instanceof NoContentException) {
			return createErrorStatus(format(NO_CONTENT_TEMPLATE, getToolingName(toolingId))); 
		} else if (e instanceof NoChangesException) {
			return createErrorStatus(format(NO_CHANGES_TEMPLATE, getCurrentVersionId(toolingId), getToolingName(toolingId)));
		} else {
			return createErrorStatus(NO_DETAILS_ERROR_MSG);
		}
	}

	private IStatus tryCheckCanCreateNewVersion() throws VersioningException {
		String toolingId = getPrimaryToolingId();
		
		checkNotNull(toolingId, "toolingId");
		if (CompareUtils.isEmpty(existingVersions.get(toolingId))) {
			
			if (!ContentAvailabilityInfoManager.INSTANCE.isAvailable(getRepositoryUuid(toolingId))) {
				throw new NoContentException(getRepositoryUuid(toolingId));
			}
		} else {
			checkForChanges(toolingId, getCurrentBranchPath(toolingId));
		}	
		return okStatus();
	}

	private IStatus okStatus() {
		return serializableOk();
	}
	
	private String getCurrentVersionId(final String toolingId) {
		final Supplier<ICodeSystemVersion> versionIdSupplier = currentVersionSuppliers.get(checkNotNull(toolingId, "toolingId"));
		return checkNotNull(versionIdSupplier, "Current version ID supplier does not exist for: " + toolingId).get().getVersionId();
	}
	
	//TODO: This is the only access to versioning from the REST API
	private IBranchPath getCurrentBranchPath(final String toolingId) {
		checkNotNull(toolingId, "toolingId");
		return BranchPathUtils.createPath(getCurrentVersionId(toolingId));
	}

	private void tryReleaseLock() throws OperationLockException {
		for (final String toolingId : getToolingIds()) {
			if (checkIsLocked(toolingId)) {
				getLockManager().unlock(lockContexts.get(toolingId), lockTargets.get(toolingId));
			}
		}
	}
	
	private boolean checkIsLocked(final String toolingId) {
		return null != lockContexts.get(checkNotNull(toolingId, "toolingId")) && null != lockTargets.get(toolingId);
	}
	
	private void tryAcquireLock() throws OperationLockException, InterruptedException {
		for (final String toolingId : getToolingIds()) {
			createLockContextAndTarget(toolingId);
			getLockManager().lock(lockContexts.get(toolingId), IOperationLockManager.IMMEDIATE, lockTargets.get(toolingId));
		}
	}

	private void handleException(final Exception e) throws SnowowlServiceException {
		tearDownLockContextAndTarget();
		throw new SnowowlServiceException(e.getMessage());
	}

	private void tearDownLockContextAndTarget() {
		if (null != lockContexts) {
			lockContexts.clear();
			lockContexts = null;
		}
		if (null != lockTargets) {
			lockTargets.clear();
			lockTargets = null;
		}
	}
	
	private void createLockContextAndTarget(final String toolingId) {
		lockContexts.put(checkNotNull(toolingId, "toolingId"), createLockContext());
		lockTargets.put(toolingId, createLockTarget(toolingId));
	}
	
	private DatastoreLockContext createLockContext() {
		return createLockContext(getUserId());
	}

	private SingleRepositoryAndBranchLockTarget createLockTarget(final String toolingId) {
		return createLockTarget(checkNotNull(toolingId, "toolingId"), createMainPath());
	}

	private IDatastoreOperationLockManager getLockManager() {
		return getServiceForClass(IDatastoreOperationLockManager.class);
	}
	
	private IBranchPath getActiveBranchForToolingFeature(final String toolingId) {
		return getTaskManager().getActiveBranch(getRepositoryUuid(checkNotNull(toolingId, "toolingId")));
	}

	private TaskManager getTaskManager() {
		return getServiceForClass(TaskManager.class);
	}
	
	private SingleRepositoryAndBranchLockTarget createLockTarget(final String toolingId, final IBranchPath branchPath) {
		return new SingleRepositoryAndBranchLockTarget(
				getRepositoryUuid(checkNotNull(toolingId, "toolingId")), 
				checkNotNull(branchPath, "branchPath"));
	}

	private String getRepositoryUuid(final String toolingId) {
		return CodeSystemUtils.getRepositoryUuid(checkNotNull(toolingId, "toolingId"));
	}
	
	private Collection<String> getToolingIds() {
		return configuration.getToolingIds();
	}

	private DatastoreLockContext createLockContext(final String userId) {
		return new DatastoreLockContext(userId, CONFIGURE_VERSION);
	}
	
	private String getUserId() {
		return getServiceForClass(ICDOConnectionManager.class).getUserId();
	}

	private IStatus handleVersioningSuccess(final boolean performTag) {
		return createOkStatus(performTag 
				? format(SUCCESS_TEMPLATE, getToolingName(), getVersionId()) 
				: format(EFFECTIVE_TIME_ADJUSTED_TEMPLATE, getToolingName()));
	}

	private IStatus createOkStatus(final String message) {
		return new SerializableStatus(IStatus.OK, DatastoreActivator.PLUGIN_ID, message);
	}

	private IStatus handleVersioningFailure(final SnowowlServiceException e) {
		LOGGER.error("Version creation failed for " + getToolingName() + ".", e);
		ApplicationContext.handleException(DatastoreActivator.getContext().getBundle(), e, "Version creation failed for " + getToolingName() + ".");
		return createErrorStatus("Version creation failed for " + getToolingName() + ".");
	}
	
	private IStatus createErrorStatus(final String message) {
		return new SerializableStatus(IStatus.ERROR, DatastoreActivator.PLUGIN_ID, Strings.nullToEmpty(message));
	}
	
	private String getToolingName() {
		return getToolingName(getPrimaryToolingId());
	}
	
	private String getToolingName(final String toolingId) {
		return CoreTerminologyBroker.getInstance().getTerminologyInformation(checkNotNull(toolingId, "toolingId")).getName();
	}
	
	
}