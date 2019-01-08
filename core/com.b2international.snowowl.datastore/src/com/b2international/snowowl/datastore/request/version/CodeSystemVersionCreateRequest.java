/*
 * Copyright 2017-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions.CREATE_VERSION;

import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.exceptions.CodeSystemNotFoundException;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.ConflictException;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CodeSystemEntry;
import com.b2international.snowowl.datastore.CodeSystemVersionEntry;
import com.b2international.snowowl.datastore.oplock.IOperationLockManager;
import com.b2international.snowowl.datastore.oplock.OperationLockException;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContext;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreOperationLockException;
import com.b2international.snowowl.datastore.oplock.impl.IDatastoreOperationLockManager;
import com.b2international.snowowl.datastore.oplock.impl.SingleRepositoryAndBranchLockTarget;
import com.b2international.snowowl.datastore.remotejobs.RemoteJob;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.datastore.version.IVersioningManager;
import com.b2international.snowowl.datastore.version.PublishOperationConfiguration;
import com.b2international.snowowl.datastore.version.VersioningManagerBroker;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

/**
 * @since 5.7
 */
final class CodeSystemVersionCreateRequest implements Request<ServiceProvider, Boolean> {

	private static final long serialVersionUID = 1L;
	private static final int TASK_WORK_STEP = 4;
	
	@NotEmpty
	@JsonProperty
	String versionId;
	
	@JsonProperty
	String description;
	
	@NotNull
	@JsonProperty
	Date effectiveTime;
	
	@NotEmpty
	@JsonProperty
	String codeSystemShortName;
	
	// lock props
	private transient Multimap<DatastoreLockContext, SingleRepositoryAndBranchLockTarget> lockTargetsByContext;
	
	@Override
	public Boolean execute(ServiceProvider context) {
		final RemoteJob job = context.service(RemoteJob.class);
		final String user = job.getUser();
		
		final Map<String, CodeSystemEntry> codeSystemsByShortName = fetchAllCodeSystems(context);
		
		final CodeSystemEntry codeSystem = codeSystemsByShortName.get(codeSystemShortName);
		if (codeSystem == null) {
			throw new CodeSystemNotFoundException(codeSystemShortName);
		}

		// check that the new versionId does not conflict with any other currently available branch
		final String newVersionPath = String.format("%s%s%s", codeSystem.getBranchPath(), Branch.SEPARATOR, versionId);
		
		// validate new path
		Branch.BranchNameValidator.DEFAULT.checkName(versionId);
		
		try {
			RepositoryRequests.branching()
				.prepareGet(newVersionPath)
				.build(codeSystem.getRepositoryUuid())
				.execute(context.service(IEventBus.class))
				.getSync();
			throw new ConflictException("An existing branch with path '%s' conflicts with the specified version identifier.", newVersionPath);
		} catch (NotFoundException e) {
			// ignore
		}

		final Map<CodeSystemEntry, IVersioningManager> versioningManagersByCodeSystem = createVersioningManagers(context, codeSystemsByShortName, codeSystem);

		try {
			acquireLocks(context, user, versioningManagersByCodeSystem.keySet());
		
			for (Entry<CodeSystemEntry, IVersioningManager> entry : versioningManagersByCodeSystem.entrySet()) {
				createVersion(context, entry.getValue(), entry.getKey(), user);
			}
			
			return Boolean.TRUE;
		} finally {
			releaseLocks(context);
		}
		
	}
	
	private Map<CodeSystemEntry, IVersioningManager> createVersioningManagers(final ServiceProvider context, Map<String, CodeSystemEntry> codeSystemsByShortName, final CodeSystemEntry baseCodeSystem) {
		final Map<CodeSystemEntry, IVersioningManager> versioningManagersByCodeSystem  = Maps.newHashMap();
		baseCodeSystem.getDependenciesAndSelf()
			.stream()
			.map(affectedCodeSystemShortName -> codeSystemsByShortName.get(affectedCodeSystemShortName))
			.forEach(affectedCodeSystem -> {
				final IVersioningManager affectedVersioningManager = VersioningManagerBroker.INSTANCE.createVersioningManager(affectedCodeSystem.getTerminologyComponentId());
				versioningManagersByCodeSystem.put(affectedCodeSystem, affectedVersioningManager);
			});

		return versioningManagersByCodeSystem;
	}
	
	private void createVersion(ServiceProvider context, IVersioningManager versioningManager, CodeSystemEntry codeSystem, String user) {
		validateEffectiveTime(context, codeSystem);
		final IProgressMonitor monitor = SubMonitor.convert(context.service(IProgressMonitor.class), TASK_WORK_STEP);
		try {
			monitor.worked(1);
			// execute publication process via the tooling specific VersioningManager 
			versioningManager.publish(new PublishOperationConfiguration(user, codeSystem.getShortName(), codeSystem.getBranchPath(), versionId, description, effectiveTime), monitor);
			monitor.worked(1);
			// tag the repository
			doTag(context, codeSystem, monitor);
			// execute any post commit steps defined by the versioning manager
			postCommit(versioningManager, monitor);
		} catch (SnowowlServiceException e) {
			throw new SnowowlRuntimeException("Error occurred during versioning.", e);
		} finally {
			if (null != monitor) {
				monitor.done();
			}
		}
	}
	
	private Map<String, CodeSystemEntry> fetchAllCodeSystems(ServiceProvider context) {
		final RepositoryManager repositoryManager = context.service(RepositoryManager.class);
		final Collection<Repository> repositories = repositoryManager.repositories();
		return repositories.stream()
			.map(repository -> fetchCodeSystem(context, repository.id()))
			.flatMap(Collection::stream)
			.collect(Collectors.toMap(CodeSystemEntry::getShortName, Function.identity()));
	}
	
	private List<CodeSystemEntry> fetchCodeSystem(ServiceProvider context, String repositoryId) {
		return CodeSystemRequests.prepareSearchCodeSystem()
			.all()
			.build(repositoryId)
			.getRequest()
			.execute(context)
			.getItems();
	}
	
	private void validateEffectiveTime(ServiceProvider context, CodeSystemEntry codeSystem) {
		if (!CoreTerminologyBroker.getInstance().isEffectiveTimeSupported(codeSystem.getTerminologyComponentId())) {
			return;
		}

		Instant mostRecentVersionEffectiveTime = getMostRecentVersionEffectiveDateTime(context, codeSystem);
		
		if (!effectiveTime.toInstant().isAfter(mostRecentVersionEffectiveTime)) {
			throw new BadRequestException("The specified '%s' effective time is invalid. Date should be after epoch.", effectiveTime, mostRecentVersionEffectiveTime);
		}
	}
	
	private Instant getMostRecentVersionEffectiveDateTime(ServiceProvider context, CodeSystemEntry codeSystem) {
		return CodeSystemRequests.prepareSearchCodeSystemVersion()
			.all()
			.filterByCodeSystemShortName(codeSystem.getShortName())
			.build(codeSystem.getRepositoryUuid())
			.execute(context.service(IEventBus.class))
			.getSync()
			.stream()
			.max(CodeSystemVersionEntry.VERSION_EFFECTIVE_DATE_COMPARATOR)
			.map(CodeSystemVersionEntry::getEffectiveDate)
			.map(Instant::ofEpochMilli)
			.orElse(Instant.EPOCH);
	}
	
	private void acquireLocks(ServiceProvider context, String user, Collection<CodeSystemEntry> codeSystems) {
		try {
			this.lockTargetsByContext = HashMultimap.create();
			
			final DatastoreLockContext lockContext = new DatastoreLockContext(user, CREATE_VERSION);
			for (CodeSystemEntry codeSystem : codeSystems) {
				final SingleRepositoryAndBranchLockTarget lockTarget = new SingleRepositoryAndBranchLockTarget(
						codeSystem.getRepositoryUuid(),
						BranchPathUtils.createPath(codeSystem.getBranchPath()));
				
				context.service(IDatastoreOperationLockManager.class).lock(lockContext, IOperationLockManager.IMMEDIATE, lockTarget);

				lockTargetsByContext.put(lockContext, lockTarget);
			}
			
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
	
	private void releaseLocks(ServiceProvider context) {
		for (Entry<DatastoreLockContext, SingleRepositoryAndBranchLockTarget> entry : lockTargetsByContext.entries()) {
			context.service(IDatastoreOperationLockManager.class).unlock(entry.getKey(), entry.getValue());
		}
	}
	
	private void doTag(ServiceProvider context, CodeSystemEntry codeSystem, IProgressMonitor monitor) {
		RepositoryRequests
			.branching()
			.prepareCreate()
			.setParent(codeSystem.getBranchPath())
			.setName(versionId)
			.build(codeSystem.getRepositoryUuid())
			.execute(context.service(IEventBus.class))
			.getSync();
		monitor.worked(1);
	}

	/*
	 * Performs actions after the successful commit. 
	 */
	private void postCommit(final IVersioningManager versioningManager, final IProgressMonitor monitor) throws SnowowlServiceException {
		versioningManager.postCommit();
		if (null != monitor) {
			monitor.worked(1);
		}
	}
	
}
