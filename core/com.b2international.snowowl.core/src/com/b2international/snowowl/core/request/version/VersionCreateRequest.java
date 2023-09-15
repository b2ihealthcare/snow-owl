/*
 * Copyright 2017-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.index.revision.RevisionBranch.BranchNameValidator;
import com.b2international.snowowl.core.*;
import com.b2international.snowowl.core.authorization.AccessControl;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.collection.TerminologyResourceCollection;
import com.b2international.snowowl.core.collection.TerminologyResourceCollectionToolingSupport;
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
import com.b2international.snowowl.core.request.SearchResourceRequest.Sort;
import com.b2international.snowowl.core.terminology.TerminologyRegistry;
import com.b2international.snowowl.core.uri.ResourceURLSchemaSupport;
import com.b2international.snowowl.core.version.Version;
import com.b2international.snowowl.core.version.VersionDocument;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
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
	@JsonFormat(pattern = "yyyy-MM-dd")
	LocalDate effectiveTime;
	
	@NotNull
	@JsonProperty
	ResourceURI resource;
	
	@JsonProperty
	boolean force;
	
	@JsonProperty
	String commitComment;
	
	@JsonProperty
	String author;
	
	// local execution variables
	private transient Multimap<DatastoreLockContext, DatastoreLockTarget> lockTargetsByContext;
	private transient Map<ResourceURI, TerminologyResource> resourcesById;
	
	@Override
	public Boolean execute(RepositoryContext context) {
		final String submitter = context.service(User.class).getUserId();
		if (Strings.isNullOrEmpty(author)) {
			author = submitter;			
		}
		
		if (!resource.isHead()) {
			throw new BadRequestException("Version '%s' cannot be created on unassigned branch '%s'", version, resource)
				.withDeveloperMessage("Did you mean to version '%s'?", resource.withoutPath());
		}
		
		if (resourcesById == null) {
			resourcesById = fetchResources(context);
		}
		
		if (!resourcesById.containsKey(resource)) {
			context.log().warn("Resource cannot be found during versioning: " + resourcesById + ", uri: " + resource);
			throw new BadRequestException("Resource '%s' does not exist in the system.", resource);
		}
		
		// validate new path
		context.service(BranchNameValidator.class).checkName(version);
		
		final Collection<TerminologyResource> resourcesToVersion = resourcesById.values();
		
		for (TerminologyResource resourceToVersion : resourcesToVersion) {
			if (TerminologyResourceCommitRequestBuilder.READ_ONLY_STATUSES.contains(resourceToVersion.getStatus())) {
				throw new BadRequestException("Resource '%s' cannot be versioned in its current status '%s'", resourceToVersion.getTitle(), resourceToVersion.getStatus());
			}
			// check that the specified effective time is valid in this code system
			// XXX ensure we verify version on all codesystems first before we delete any branches
			validateVersion(context, resourceToVersion);
			// check that the new versionId does not conflict with any other currently available branch
			final String newVersionPath = Branch.get(resourceToVersion.getBranchPath(), version);
			final String repositoryId = resourceToVersion.getToolingId();
			
			if (!force) {
				// branch needs checking in the non-force cases only
				try {
					
					Branch branch = RepositoryRequests.branching()
						.prepareGet(newVersionPath)
						.build(repositoryId)
						.execute(context);
					
					if (!branch.isDeleted()) {
						throw new ConflictException("An existing version or branch with path '%s' conflicts with the specified version identifier", newVersionPath);
					}

				} catch (NotFoundException e) {
					// branch does not exist, ignore
				}
			}
		}
		
		acquireLocks(context, submitter, resourcesToVersion);
		
		final IProgressMonitor monitor = SubMonitor.convert(context.service(IProgressMonitor.class), TASK_WORK_STEP);
		try {
			
			// create a version for the resource
			return new BranchSnapshotContentRequest<>(Branch.MAIN_PATH,
					new ResourceRepositoryCommitRequestBuilder()
					.setBody(tx -> {
						// perform tooling/content versions first for earch resource to version
						resourcesToVersion.forEach(resourceToVersion -> {
							// version components in the given repository
							new RepositoryRequest<CommitResult>(resourceToVersion.getToolingId(),
								new BranchSnapshotContentRequest<CommitResult>(resourceToVersion.getBranchPath(),
										context.service(RepositoryManager.class).get(resourceToVersion.getToolingId())
											.service(VersioningRequestBuilder.class)
											.build(new VersioningConfiguration(author, resourceToVersion.getResourceURI(), version, description, effectiveTime, force))
								)
							).execute(context);
							
							// tag the repository
							RepositoryRequests
								.branching()
								.prepareCreate()
								.setParent(resourceToVersion.getBranchPath())
								.setName(version)
								// delete the branch if force requested and we get to this point
								.force(force)
								.build(resourceToVersion.getToolingId())
								.execute(context);
							monitor.worked(1);
							
							// generate version document for each resource 
							tx.add(VersionDocument.builder()
									.id(resourceToVersion.getResourceURI().withPath(version).withoutResourceType())
									.version(version)
									.description(description)
									.effectiveTime(EffectiveTimes.getEffectiveTime(effectiveTime))
									.resource(resourceToVersion.getResourceURI())
									.branchPath(resourceToVersion.getRelativeBranchPath(version))
									.author(author)
									.createdAt(Instant.now().toEpochMilli())
									.updatedAt(Instant.now().toEpochMilli())
									.toolingId(resourceToVersion.getToolingId())
									.url(buildVersionUrl(context, resourceToVersion))
									.resourceSnapshot(resourceToVersion)
									.build());
						});
						return Boolean.TRUE;
					})
					.setCommitComment(CompareUtils.isEmpty(commitComment)? String.format("Version '%s' as of '%s'", resource, version) : commitComment)
					.setAuthor(author)
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

	private Map<ResourceURI, TerminologyResource> fetchResources(ServiceProvider context) {
		final Map<ResourceURI, TerminologyResource> resourcesToVersion = new HashMap<>();
		
		ResourceRequests.prepareSearch()
			.one()
			.filterById(resource.getResourceId())
			.buildAsync()
			.execute(context)
			.stream()
			.filter(TerminologyResource.class::isInstance)
			.map(TerminologyResource.class::cast)
			.forEach(resource -> {
				resourcesToVersion.put(resource.getResourceURI(), resource);
			});
		
		// if the resource to version is a collection URI then version all child resources as well
		TerminologyResource terminologyResource = resourcesToVersion.get(resource);
		if (terminologyResource instanceof TerminologyResourceCollection resourceCollection) {
			final var registry = context.service(TerminologyResourceCollectionToolingSupport.Registry.class);
			final Set<String> childResourceTypes = registry.getAllByToolingId(resourceCollection.getToolingId())
				.stream()
				.flatMap(toolingSupport -> toolingSupport.getSupportedChildResourceTypes().stream())
				.collect(Collectors.toSet());
			
			ResourceRequests.prepareSearch()
				.filterByResourceCollectionAncestor(resourceCollection.getId())
				.filterByResourceTypes(childResourceTypes)
				.setLimit(1_000)
				.streamAsync(context, req -> req.buildAsync())
				.flatMap(Resources::stream)
				.filter(TerminologyResource.class::isInstance)
				.map(TerminologyResource.class::cast)
				.forEach(resource -> {
					resourcesToVersion.put(resource.getResourceURI(), resource);
				});
		}
		
		return resourcesToVersion;
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
					throw new BadRequestException("Force creating the latest version requires the same versionId ('%s') to be used.", mrv.getVersion());
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
			.sortBy(Sort.fieldDesc(VersionDocument.Fields.EFFECTIVE_TIME))
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
	
	@Override
	public List<Permission> getPermissions(ServiceProvider context, Request<ServiceProvider, ?> req) {
		return List.of(
			Permission.requireAny(
				getOperation(), 
				resource.toString(),
				resource.withoutResourceType()
			)
		);
	}
	
	@Override
	public String getOperation() {
		return Permission.OPERATION_VERSION;
	}
}
