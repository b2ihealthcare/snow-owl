/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request.resource;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.b2international.commons.exceptions.AlreadyExistsException;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.Dependency;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.internal.DependencyDocument;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.internal.ResourceDocument.Builder;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.request.BaseResourceUpdateRequest;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.version.Version;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 8.12.0
 */
public abstract class BaseTerminologyResourceUpdateRequest extends BaseResourceUpdateRequest {

	private static final long serialVersionUID = 2L;
	
	// generic terminology resource update properties
	@JsonProperty
	private String oid;
	
	@JsonProperty
	private String branchPath;
	
	@JsonProperty
	private List<Dependency> dependencies;
	
//	private String iconPath; // TODO should we support custom icons for resources?? branding??
	
	public final void setOid(String oid) {
		this.oid = oid;
	}
	
	public final void setBranchPath(String branchPath) {
		this.branchPath = branchPath;
	}
	
	public final void setDependencies(List<Dependency> dependencies) {
		this.dependencies = dependencies;
	}
	
//	public final void setIconPath(final String iconPath) {
//		this.iconPath = iconPath;
//	}
	
	public BaseTerminologyResourceUpdateRequest(String componentId) {
		super(componentId);
	}

	@Override
	@OverridingMethodsMustInvokeSuper
	protected boolean updateSpecializedProperties(TransactionContext context, ResourceDocument resource, Builder updated) {
		boolean changed = false;
		
		changed |= updateOid(context, resource.getOid(), updated);
		changed |= updateBranchPath(context, updated, resource.getBranchPath(), resource.getToolingId());
		changed |= updateDependencies(context, updated, resource.getDependencies(), resource.getId());
		
//		changed |= updateProperty(iconPath, codeSystem::getIconPath, updated::iconPath);
		return changed;
	}
	
	private boolean updateDependencies(TransactionContext context, Builder resource, SortedSet<DependencyDocument> oldDependencies, String resourceId) {
		// handle extensionOf dependency if configured
		Optional<Dependency> extensionOfDependency = dependencies != null ? dependencies.stream().filter(Dependency::isExtensionOf).findFirst() : Optional.empty();
		var extensionOfUri = extensionOfDependency.map(Dependency::getUri).orElse(null);
		
		if (extensionOfUri != null) {
			if (extensionOfUri.isHead() || extensionOfUri.isLatest()) {
				throw new BadRequestException("Base terminology resource version was not expicitly given (can not be empty, "
					+ "LATEST or HEAD) in 'extensionOf' dependency %s.", extensionOfUri);
			}
			
			final String versionId = extensionOfUri.getResourceUri().getPath();
			
			final Optional<Version> extensionOfVersion = ResourceRequests.prepareSearchVersion()
					.one()
					.filterByResource(extensionOfUri.getResourceUri().withoutPath())
					.filterByVersionId(versionId)
					.build()
					.execute(context)
					.first();
			
			if (!extensionOfVersion.isPresent()) {
				throw new BadRequestException("Couldn't find base terminology resource version for 'extensionOf' dependency %s.", extensionOfUri);
			}
			
			// The working branch prefix is determined by the extensionOf code system version's path
			final String newResourceBranchPath = Branch.get(extensionOfVersion.get().getBranchPath(), resourceId);
			
			if (branchPath != null && !branchPath.equals(newResourceBranchPath)) {
				throw new BadRequestException("Branch path is inconsistent with 'extensionOf' dependency ('%s' given, should be '%s').", branchPath, newResourceBranchPath);
			}
			
			resource.branchPath(newResourceBranchPath);
			
			// XXX no need to return here, we are only able to get here by having at least one extensionOf entry in the dependencies array, and that will be handled in the subsequent if block
		}
		
		final SortedSet<DependencyDocument> newDependencies = dependencies != null ? dependencies.stream().map(Dependency::toDocument).collect(Collectors.toCollection(TreeSet::new)) : null;
		if (newDependencies != null && !Objects.equals(newDependencies, oldDependencies)) {
			// check duplicates in new dependency array
			BaseTerminologyResourceCreateRequest.checkDuplicateDependencies(context, dependencies);
			// verify references to new dependency array
			BaseTerminologyResourceCreateRequest.checkNonExtensionOfDependencyReferences(context, dependencies);
			
			// make sure we auto-migrate the old fields to the new value
			resource.dependencies(newDependencies);
			
			return true;
		}
		
		return false;
	}
	
	private boolean updateOid(TransactionContext context, String oldOid, Builder updated) {
		if (oid == null || oid.equals(oldOid)) {
			return false;
		}
		
		if (!oid.isBlank()) {
			final boolean oidExist = ResourceRequests.prepareSearch()
					.setLimit(0)
					.filterByOid(oid)
					.build()
					.executeAsAdmin(context)
					.getTotal() > 0;
			
			if (oidExist) {
				throw new AlreadyExistsException("Resource", "oid", oid);
			}
		}
		
		updated.oid(oid);
		return true;
	}

	private boolean updateBranchPath(final TransactionContext context, 
			final ResourceDocument.Builder resource, 
			final String currentBranchPath,
			final String toolingId) {
		
		// if extensionOf is set, branch path changes are already handled in updateExtensionOf
		final Optional<Dependency> extensionOf = Dependency.find(dependencies, TerminologyResource.DependencyScope.EXTENSION_OF);
		if (extensionOf.isEmpty() && branchPath != null && !currentBranchPath.equals(branchPath)) {
			try {
				final Branch branch = RepositoryRequests
						.branching()
						.prepareGet(branchPath)
						.build(toolingId)
						.getRequest()
						.execute(context);
				
				if (branch.isDeleted()) {
					throw new BadRequestException("Branch with identifier '%s' is deleted.", branchPath);
				}
				
			} catch (NotFoundException e) {
				throw e.toBadRequestException();
			}
			

			// TODO: check if update branch path coincides with a version working path and update extensionOf accordingly?
			resource.branchPath(branchPath);
			return true;
		}
		
		return false;
	}
}
