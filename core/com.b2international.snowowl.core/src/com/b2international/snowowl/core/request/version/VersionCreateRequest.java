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

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.ConflictException;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.snowowl.core.*;
import com.b2international.snowowl.core.authorization.AccessControl;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.context.ResourceRepositoryCommitRequestBuilder;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.RepositoryContext;
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
import com.b2international.snowowl.core.uri.ResourceURLSchemaSupport;
import com.b2international.snowowl.core.version.Version;
import com.b2international.snowowl.core.version.VersionDocument;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * @since 5.7
 */
public final class VersionCreateRequest implements Request<RepositoryContext, Boolean>, AccessControl {

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
	
	@NotNull
	@JsonProperty
	ResourceURI resource;
	
	@JsonProperty
	boolean force;
	
	@JsonProperty
	String commitComment;
	
	// local execution variables
	private transient Multimap<DatastoreLockContext, DatastoreLockTarget> lockTargetsByContext;
	private transient Map<ResourceURI, TerminologyResource> resourcesById;
	
	@Override
	public Boolean execute(RepositoryContext context) {
		final String user = context.service(User.class).getUsername();
		
		if (!resource.isHead()) {
			throw new BadRequestException("Version '%s' cannot be created on unassigned branch '%s'.", version, resource)
				.withDeveloperMessage("Did you mean to version '%s'?", resource.withoutPath());
		}
		
		if (resourcesById == null) {
			resourcesById = fetchResources(context);
		}
		
		if (!resourcesById.containsKey(resource)) {
			context.log().warn("Resource cannot be found during versioning: " + resourcesById + ", uri: " + resource);
			throw new NotFoundException("Resource", resource.getResourceId());
		}
		
		// validate new path
		RevisionBranch.BranchNameValidator.DEFAULT.checkName(version);
		
		TerminologyResource resourceToVersion = resourcesById.get(resource);
		
		// TODO resurrect or eliminate tooling dependencies
		final List<TerminologyResource> resourcesToVersion = List.of(resourcesById.get(resource));
//		final List<CodeSystem> resourcesToVersion = codeSystem.getDependenciesAndSelf()
//				.stream()
//				.map(resourcesById::get)
//				.collect(Collectors.toList());
		
		resourcesToVersion.stream()
			.filter(cs -> cs.getUpgradeOf() != null)
			.findAny()
			.ifPresent(cs -> {
				throw new BadRequestException("Upgrade resource '%s' can not be versioned.", cs.getResourceURI());				
			});
		
		for (TerminologyResource terminologyResource : resourcesToVersion) {
			// check that the new versionId does not conflict with any other currently available branch
			final String newVersionPath = String.join(Branch.SEPARATOR, terminologyResource.getBranchPath(), version);
			final String repositoryId = terminologyResource.getToolingId();
			
			if (!force) {
				// branch needs checking in the non-force cases only
				try {
					
					Branch branch = RepositoryRequests.branching()
						.prepareGet(newVersionPath)
						.build(repositoryId)
						.execute(context);
					
					if (!branch.isDeleted()) {
						throw new ConflictException("An existing version or branch with path '%s' conflicts with the specified version identifier.", newVersionPath);
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
			
//			resourcesToVersion.forEach(resourceToVersion -> {
				// check that the specified effective time is valid in this code system
				validateVersion(context, resourceToVersion);
				// version components in the given repository
				new RepositoryRequest<>(resourceToVersion.getToolingId(),
					new BranchRequest<>(resourceToVersion.getBranchPath(),
						new RevisionIndexReadRequest<CommitResult>(
							context.service(RepositoryManager.class).get(resourceToVersion.getToolingId())
								.service(VersioningRequestBuilder.class)
								.build(new VersioningConfiguration(user, resourceToVersion.getResourceURI(), version, description, effectiveTime, force))
						)
					)
				).execute(context);
				
				// tag the repository
				doTag(context, resourceToVersion, monitor);
//			});
			
			// create a version for the resource
			return new BranchRequest<>(Branch.MAIN_PATH,
				new ResourceRepositoryCommitRequestBuilder()
				.setBody(tx -> {
					tx.add(VersionDocument.builder()
							.id(resource.withPath(version).withoutResourceType())
							.version(version)
							.description(description)
							.effectiveTime(EffectiveTimes.getEffectiveTime(effectiveTime))
							.resource(resource)
							.branchPath(resourceToVersion.getRelativeBranchPath(version))
							.author(user)
							.createdAt(Instant.now().toEpochMilli())
							.toolingId(resourceToVersion.getToolingId())
							.url(buildVersionUrl(context, resourceToVersion))
							.build());
					return Boolean.TRUE;
				})
				.setCommitComment(CompareUtils.isEmpty(commitComment)? String.format("Version '%s' as of '%s'", resource, version) : commitComment)
				.build()
			).execute(context).getResultAs(Boolean.class);
		} finally {
			releaseLocks(context);
			if (null != monitor) {
				monitor.done();
			}
		}
	}
	
	private String buildVersionUrl(RepositoryContext context, TerminologyResource resourceToVersion) {
		Request<RepositoryContext, String> withVersionReq = ctx -> {
			return ctx.service(ResourceURLSchemaSupport.class).withVersion(resourceToVersion.getUrl(), version, effectiveTime);
		};
		return new RepositoryRequest<>(resourceToVersion.getToolingId(), withVersionReq).execute(context);
	}

	private boolean deleteBranch(ServiceProvider context, String path, String repositoryId) {
		return RepositoryRequests.branching()
				.prepareDelete(path)
				.build(repositoryId)
				.execute(context);
	}
	
	private Map<ResourceURI, TerminologyResource> fetchResources(ServiceProvider context) {
		return ResourceRequests.prepareSearch()
			.one()
			.filterById(resource.getResourceId())
			.buildAsync()
			.execute(context)
			.stream()
			.filter(TerminologyResource.class::isInstance)
			.map(TerminologyResource.class::cast)
			.collect(Collectors.toMap(Resource::getResourceURI, r -> r));
	}
	
	private void validateVersion(RepositoryContext context, TerminologyResource codeSystem) {
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
	
	private Optional<Version> getMostRecentVersion(RepositoryContext context, TerminologyResource codeSystem) {
		return ResourceRequests.prepareSearchVersion()
			.one()
			.filterByResource(codeSystem.getResourceURI())
			.sortBy(SortField.descending(VersionDocument.Fields.EFFECTIVE_TIME))
			.build()
			.execute(context)
			.stream()
			.findFirst();
	}
	
	private void acquireLocks(ServiceProvider context, String user, Collection<TerminologyResource> codeSystems) {
		this.lockTargetsByContext = HashMultimap.create();
		
		final DatastoreLockContext lockContext = new DatastoreLockContext(user, CREATE_VERSION);
		for (TerminologyResource codeSystem : codeSystems) {
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
	
	private void doTag(ServiceProvider context, TerminologyResource codeSystem, IProgressMonitor monitor) {
		RepositoryRequests
			.branching()
			.prepareCreate()
			.setParent(codeSystem.getBranchPath())
			.setName(version)
			.build(codeSystem.getToolingId())
			.execute(context);
		monitor.worked(1);
	}
	
	@Override
	public List<Permission> getPermissions(ServiceProvider context, Request<ServiceProvider, ?> req) {
		if (resourcesById == null) {
			resourcesById = fetchResources(context);
		}
		List<Permission> permissions = new ArrayList<>(resourcesById.size());
		for (ResourceURI accessedResourceURI : resourcesById.keySet()) {
			permissions.add(Permission.requireAny(
				getOperation(), 
				resourcesById.get(accessedResourceURI).getToolingId(),
				resourcesById.get(accessedResourceURI).getResourceURI().toString(),
				resourcesById.get(accessedResourceURI).getId()
			));
		}
		return permissions;
	}
	
	@Override
	public String getOperation() {
		return Permission.OPERATION_VERSION;
	}
}
