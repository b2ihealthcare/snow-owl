/*
 * Copyright 2017-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request.version;

import static com.b2international.snowowl.core.internal.locks.DatastoreLockContextDescriptions.CREATE_VERSION;

import java.time.LocalDate;
import java.util.*;
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
import com.b2international.snowowl.core.*;
import com.b2international.snowowl.core.authorization.AccessControl;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.internal.locks.DatastoreLockContext;
import com.b2international.snowowl.core.internal.locks.DatastoreLockTarget;
import com.b2international.snowowl.core.locks.IOperationLockManager;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.request.*;
import com.b2international.snowowl.core.request.SearchResourceRequest.SortField;
import com.b2international.snowowl.core.terminology.TerminologyRegistry;
import com.b2international.snowowl.core.version.Version;
import com.b2international.snowowl.core.version.VersionDocument;
import com.b2international.snowowl.eventbus.IEventBus;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * @since 5.7
 */
public final class VersionCreateRequest implements Request<ServiceProvider, Boolean>, AccessControl {

	private static final long serialVersionUID = 1L;
	private static final int TASK_WORK_STEP = 4;
	
	@NotEmpty
	@JsonProperty
	String version;
	
	@JsonProperty
	String description;
	
	@NotNull
	@JsonProperty
	LocalDate effectiveTime;
	
	@NotEmpty
	@JsonProperty
	ResourceURI resource;
	
	@JsonProperty
	boolean force;
	
	// local execution variables
	private transient Multimap<DatastoreLockContext, DatastoreLockTarget> lockTargetsByContext;
	private transient Map<String, CodeSystem> resourcesById;
	
	@Override
	public Boolean execute(ServiceProvider context) {
		final String user = context.service(User.class).getUsername();
		
		if (resourcesById == null) {
			resourcesById = fetchAllResources(context);
		}
		
		final CodeSystem codeSystem = resourcesById.get(resource);
		if (codeSystem == null) {
			throw new ResourceNotFoundException(resource);
		}
		
		// validate new path
		RevisionBranch.BranchNameValidator.DEFAULT.checkName(version);
		
		final List<CodeSystem> resourcesToVersion = codeSystem.getDependenciesAndSelf()
				.stream()
				.map(resourcesById::get)
				.collect(Collectors.toList());
		
		resourcesToVersion.stream()
			.filter(cs -> cs.getUpgradeOf() != null)
			.findAny()
			.ifPresent(cs -> {
				throw new BadRequestException("Upgrade resource '%s' can not be versioned.", cs.getResourceURI());				
			});
		
		for (CodeSystem cs : resourcesToVersion) {
			// check that the new versionId does not conflict with any other currently available branch
			final String newVersionPath = String.join(Branch.SEPARATOR, cs.getBranchPath(), version);
			final String repositoryId = cs.getToolingId();
			
			if (!force) {
				// branch needs checking in the non-force cases only
				try {
					
					Branch branch = RepositoryRequests.branching()
						.prepareGet(newVersionPath)
						.build(repositoryId)
						.execute(context.service(IEventBus.class))
						.getSync(1, TimeUnit.MINUTES);
					
					if (!branch.isDeleted()) {
						throw new ConflictException("An existing branch with path '%s' conflicts with the specified version identifier.", newVersionPath);
					}

				} catch (NotFoundException e) {
					// branch does not exist, ignore
				}
			} else {
				
				// if there is no conflict, delete the branch (the request also ignores non-existent branches)
				deleteBranch(context, newVersionPath, repositoryId);
			}
		}
		
		acquireLocks(context, user, resourcesToVersion);
		
		final IProgressMonitor monitor = SubMonitor.convert(context.service(IProgressMonitor.class), TASK_WORK_STEP);
		try {
			
			resourcesToVersion.forEach(resourceToVersion -> {
				// check that the specified effective time is valid in this code system
				validateVersion(context, codeSystem);
				new RepositoryRequest<>(resourceToVersion.getToolingId(),
					new BranchRequest<>(codeSystem.getBranchPath(),
						new RevisionIndexReadRequest<CommitResult>(
							context.service(RepositoryManager.class).get(resourceToVersion.getToolingId())
								.service(VersioningRequestBuilder.class)
								.build(new VersioningConfiguration(user, resourceToVersion.getResourceURI(), version, description, effectiveTime, force))
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
	
	private boolean deleteBranch(ServiceProvider context, String path, String repositoryId) {
		return RepositoryRequests.branching()
				.prepareDelete(path)
				.build(repositoryId)
				.execute(context.service(IEventBus.class))
				.getSync(1, TimeUnit.MINUTES);
	}
	
	// TODO add generic ResourceRequests
	private Map<String, Resource> fetchAllResources(ServiceProvider context) {
//		final RepositoryManager repositoryManager = context.service(RepositoryManager.class);
//		final Collection<Repository> repositories = repositoryManager.repositories();
//		return repositories.stream()
//			.map(repository -> fetchCodeSystems(context, repository.id()))
//			.flatMap(Collection::stream)
//			.collect(Collectors.toMap(CodeSystem::getShortName, Function.identity()));
	}
	
	private List<CodeSystem> fetchResources(ServiceProvider context, String repositoryId) {
//		return CodeSystemRequests.prepareSearchCodeSystem()
//			.all()
//			.build(repositoryId)
//			.getRequest()
//			.execute(context)
//			.getItems();
	}
	
	private void validateVersion(ServiceProvider context, CodeSystem codeSystem) {
		if (!context.service(TerminologyRegistry.class).getTerminology(codeSystem.getToolingId()).isEffectiveTimeSupported()) {
			return;
		}

		Optional<Version> mostRecentVersion = getMostRecentVersion(context, codeSystem);

		mostRecentVersion.ifPresent(mrv -> {
			LocalDate mostRecentVersionEffectiveTime = mostRecentVersion.map(Version::getEffectiveTime).orElse(LocalDate.EPOCH);

			if (force) {
				if (!Objects.equals(version, mrv.getVersion())) {
					throw new BadRequestException("Force creating version requires the same versionId ('%s') to be used", version);
				}
				
				// force recreating an existing version should use the same or later effective date value, allow same here
				if (effectiveTime.equals(mostRecentVersionEffectiveTime)) {
					return;
				}
			}
			
			if (!effectiveTime.isAfter(mostRecentVersionEffectiveTime)) {
				throw new BadRequestException("The specified '%s' effective time is invalid. Date should be after '%s'.", effectiveTime, mostRecentVersionEffectiveTime);
			}
		});
		
	}
	
	private Optional<Version> getMostRecentVersion(ServiceProvider context, CodeSystem codeSystem) {
		return CodeSystemRequests.prepareSearchVersion()
			.one()
			.filterByResource(codeSystem.getResourceURI())
			.sortBy(SortField.descending(VersionDocument.Fields.EFFECTIVE_TIME))
			.build()
			.execute(context)
			.stream()
			.findFirst();
	}
	
	private void acquireLocks(ServiceProvider context, String user, Collection<CodeSystem> codeSystems) {
		this.lockTargetsByContext = HashMultimap.create();
		
		final DatastoreLockContext lockContext = new DatastoreLockContext(user, CREATE_VERSION);
		for (CodeSystem codeSystem : codeSystems) {
			final DatastoreLockTarget lockTarget = new DatastoreLockTarget(codeSystem.getToolingId(), codeSystem.getBranchPath());
			
			context.service(IOperationLockManager.class).lock(lockContext, IOperationLockManager.IMMEDIATE, lockTarget);

			lockTargetsByContext.put(lockContext, lockTarget);
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
			.setName(version)
			.build(codeSystem.getToolingId())
			.execute(context.service(IEventBus.class))
			.getSync(1, TimeUnit.MINUTES);
		monitor.worked(1);
	}
	
	@Override
	public String getResource(ServiceProvider context) {
		if (resourcesById == null) {
			resourcesById = fetchAllResources(context);
		}
		// TODO support multi repository version authorization
		return resourcesById.get(resource).getToolingId();
	}
	
	@Override
	public String getOperation() {
		return Permission.OPERATION_VERSION;
	}
	
}
