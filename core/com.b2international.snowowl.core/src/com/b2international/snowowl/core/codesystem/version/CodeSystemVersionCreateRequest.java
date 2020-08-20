/*
 * Copyright 2017-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.codesystem.version;

import static com.b2international.snowowl.core.internal.locks.DatastoreLockContextDescriptions.CREATE_VERSION;

import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.ConflictException;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.authorization.AccessControl;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.codesystem.CodeSystemVersionEntry;
import com.b2international.snowowl.core.domain.exceptions.CodeSystemNotFoundException;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.internal.locks.DatastoreLockContext;
import com.b2international.snowowl.core.internal.locks.DatastoreLockTarget;
import com.b2international.snowowl.core.internal.locks.DatastoreOperationLockException;
import com.b2international.snowowl.core.jobs.RemoteJob;
import com.b2international.snowowl.core.locks.IOperationLockManager;
import com.b2international.snowowl.core.locks.OperationLockException;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.request.BranchRequest;
import com.b2international.snowowl.core.request.CommitResult;
import com.b2international.snowowl.core.request.RepositoryRequest;
import com.b2international.snowowl.core.request.RevisionIndexReadRequest;
import com.b2international.snowowl.core.terminology.TerminologyRegistry;
import com.b2international.snowowl.eventbus.IEventBus;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * @since 5.7
 */
final class CodeSystemVersionCreateRequest implements Request<ServiceProvider, Boolean>, AccessControl {

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
	
	// local execution variables
	private transient Multimap<DatastoreLockContext, DatastoreLockTarget> lockTargetsByContext;
	private transient Map<String, CodeSystem> codeSystemsByShortName;
	
	@Override
	public Boolean execute(ServiceProvider context) {
		final RemoteJob job = context.service(RemoteJob.class);
		final String user = job.getUser();
		
		if (codeSystemsByShortName == null) {
			codeSystemsByShortName = fetchAllCodeSystems(context);
		}
		
		final CodeSystem codeSystem = codeSystemsByShortName.get(codeSystemShortName);
		if (codeSystem == null) {
			throw new CodeSystemNotFoundException(codeSystemShortName);
		}
		
		// check that the new versionId does not conflict with any other currently available branch
		final String newVersionPath = String.join(Branch.SEPARATOR, codeSystem.getBranchPath(), versionId);
		final String repositoryId = codeSystem.getRepositoryId();
		
		// validate new path
		RevisionBranch.BranchNameValidator.DEFAULT.checkName(versionId);
		
		try {
			Branch branch = RepositoryRequests.branching()
				.prepareGet(newVersionPath)
				.build(repositoryId)
				.execute(context.service(IEventBus.class))
				.getSync(1, TimeUnit.MINUTES);
			// allow deleted version branches to be reused for versioning
			if (!branch.isDeleted()) {
				throw new ConflictException("An existing branch with path '%s' conflicts with the specified version identifier.", newVersionPath);
			}
		} catch (NotFoundException e) {
			// ignore
		}
		
		final List<CodeSystem> codeSystemsToVersion = codeSystem.getDependenciesAndSelf()
			.stream()
			.map(codeSystemsByShortName::get)
			.collect(Collectors.toList());
		
		acquireLocks(context, user, codeSystemsToVersion);
		
		final IProgressMonitor monitor = SubMonitor.convert(context.service(IProgressMonitor.class), TASK_WORK_STEP);
		try {
			
			codeSystemsToVersion.forEach(codeSystemToVersion -> {
				// check that the specified effective time is valid in this code system
				validateEffectiveTime(context, codeSystem);
				new RepositoryRequest<>(repositoryId,
					new BranchRequest<>(codeSystem.getBranchPath(),
						new RevisionIndexReadRequest<CommitResult>(
							context.service(RepositoryManager.class).get(codeSystemToVersion.getRepositoryId())
								.service(VersioningRequestBuilder.class)
								.build(new VersioningConfiguration(user, codeSystemToVersion.getShortName(), versionId, description, effectiveTime))
						)
					)
				).execute(context);
				
				// tag the repository
				doTag(context, codeSystem, monitor);
			});
			
			return Boolean.TRUE;
		} finally {
			releaseLocks(context);
			if (null != monitor) {
				monitor.done();
			}
		}
	}
	
	private Map<String, CodeSystem> fetchAllCodeSystems(ServiceProvider context) {
		final RepositoryManager repositoryManager = context.service(RepositoryManager.class);
		final Collection<Repository> repositories = repositoryManager.repositories();
		return repositories.stream()
			.map(repository -> fetchCodeSystem(context, repository.id()))
			.flatMap(Collection::stream)
			.collect(Collectors.toMap(CodeSystem::getShortName, Function.identity()));
	}
	
	private List<CodeSystem> fetchCodeSystem(ServiceProvider context, String repositoryId) {
		return CodeSystemRequests.prepareSearchCodeSystem()
			.all()
			.build(repositoryId)
			.getRequest()
			.execute(context)
			.getItems();
	}
	
	private void validateEffectiveTime(ServiceProvider context, CodeSystem codeSystem) {
		if (!context.service(TerminologyRegistry.class).getTerminology(codeSystem.getTerminologyId()).isEffectiveTimeSupported()) {
			return;
		}

		Instant mostRecentVersionEffectiveTime = getMostRecentVersionEffectiveDateTime(context, codeSystem);
		Instant requestEffectiveTime = effectiveTime.toInstant();

		if (!requestEffectiveTime.isAfter(mostRecentVersionEffectiveTime)) {
			throw new BadRequestException("The specified '%s' effective time is invalid. Date should be after '%s'.", requestEffectiveTime, mostRecentVersionEffectiveTime);
		}
	}
	
	private Instant getMostRecentVersionEffectiveDateTime(ServiceProvider context, CodeSystem codeSystem) {
		return CodeSystemRequests.prepareSearchCodeSystemVersion()
			.all()
			.filterByCodeSystemShortName(codeSystem.getShortName())
			.build(codeSystem.getRepositoryId())
			.execute(context.service(IEventBus.class))
			.getSync(1, TimeUnit.MINUTES)
			.stream()
			.max(CodeSystemVersionEntry.VERSION_EFFECTIVE_DATE_COMPARATOR)
			.map(CodeSystemVersionEntry::getEffectiveDate)
			.map(Instant::ofEpochMilli)
			.orElse(Instant.EPOCH);
	}
	
	private void acquireLocks(ServiceProvider context, String user, Collection<CodeSystem> codeSystems) {
		try {
			this.lockTargetsByContext = HashMultimap.create();
			
			final DatastoreLockContext lockContext = new DatastoreLockContext(user, CREATE_VERSION);
			for (CodeSystem codeSystem : codeSystems) {
				final DatastoreLockTarget lockTarget = new DatastoreLockTarget(codeSystem.getRepositoryId(), codeSystem.getBranchPath());
				
				context.service(IOperationLockManager.class).lock(lockContext, IOperationLockManager.IMMEDIATE, lockTarget);

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
		for (Entry<DatastoreLockContext, DatastoreLockTarget> entry : lockTargetsByContext.entries()) {
			context.service(IOperationLockManager.class).unlock(entry.getKey(), entry.getValue());
		}
	}
	
	private void doTag(ServiceProvider context, CodeSystem codeSystem, IProgressMonitor monitor) {
		RepositoryRequests
			.branching()
			.prepareCreate()
			.setParent(codeSystem.getBranchPath())
			.setName(versionId)
			.build(codeSystem.getRepositoryId())
			.execute(context.service(IEventBus.class))
			.getSync(1, TimeUnit.MINUTES);
		monitor.worked(1);
	}
	
	@Override
	public String getResource(ServiceProvider context) {
		if (codeSystemsByShortName == null) {
			codeSystemsByShortName = fetchAllCodeSystems(context);
		}
		// TODO support multi repository version authorization
		return codeSystemsByShortName.get(codeSystemShortName).getRepositoryId();
	}
	
	@Override
	public String getOperation() {
		return Permission.OPERATION_VERSION;
	}
	
}
