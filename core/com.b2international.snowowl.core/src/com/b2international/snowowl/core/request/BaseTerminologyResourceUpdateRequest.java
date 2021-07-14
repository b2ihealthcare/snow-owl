/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.b2international.commons.exceptions.AlreadyExistsException;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.internal.ResourceDocument.Builder;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.version.Version;
import com.google.common.collect.Maps;

/**
 * @since 8.0
 */
public abstract class BaseTerminologyResourceUpdateRequest extends BaseResourceUpdateRequest {

	private static final long serialVersionUID = 2L;
	
	// generic terminology resource update properties
	private String oid;
	private String branchPath;
	private ResourceURI extensionOf;
	private Map<String, Object> settings;
//	private String iconPath; // TODO should we support custom icons for resources?? branding??
	
	public final void setOid(String oid) {
		this.oid = oid;
	}
	
	public final void setBranchPath(String branchPath) {
		this.branchPath = branchPath;
	}
	
	public final void setExtensionOf(ResourceURI extensionOf) {
		this.extensionOf = extensionOf;
	}
	
	public final void setSettings(Map<String, Object> settings) {
		this.settings = settings;
	}
	
//	public final void setIconPath(final String iconPath) {
//		this.iconPath = iconPath;
//	}
	
	public BaseTerminologyResourceUpdateRequest(String componentId) {
		super(componentId);
	}
	
	@Override
	protected boolean updateSpecializedProperties(TransactionContext context, ResourceDocument resource, Builder updated) {
		boolean changed = false;
		
		changed |= updateOid(context, resource.getOid(), updated);
		changed |= updateBranchPath(context, updated, resource.getBranchPath());
		changed |= updateExtensionOf(context, updated, resource.getExtensionOf(), resource.getId());
		changed |= updateSettings(resource, updated);
		
//		changed |= updateProperty(iconPath, codeSystem::getIconPath, updated::iconPath);
		return changed;
	}

	private boolean updateOid(TransactionContext context, String oldOid, Builder updated) {
		if (oid == null || oid.equals(oldOid)) {
			return false;
		}
		
		final boolean oidExist = ResourceRequests.prepareSearch()
				.filterByOid(oid)
				.build()
				.execute(context)
				.getTotal() > 0;
		
		if (oidExist) {
			new AlreadyExistsException("Resource", "oid", oid);
		}
		
		updated.oid(oid);
		return true;
	}

//	private boolean updateLocales(final ResourceDocument codeSystem, final ResourceDocument.Builder updated) {
//		// Don't update if no list was given
//		if (locales == null) {
//			return false;
//		}
//		
//		final List<ExtendedLocale> currentLocales = codeSystem.getLocales();
//
//		// Also don't update if the lists contain the same elements in the same order
//		if (Objects.equals(currentLocales, locales)) {
//			return false;
//		}
//		
//		updated.locales(ImmutableList.copyOf(locales));
//		return true;
//	}

	private boolean updateSettings(final ResourceDocument codeSystem, final ResourceDocument.Builder updated) {
		if (settings == null || settings.isEmpty()) {
			return false;
		}
		
		// Get mutable copy of existing settings, or an empty map for starters
		final Map<String, Object> updatedSettings = Optional.ofNullable(codeSystem.getSettings())
				.map(Maps::newHashMap)
				.orElse(Maps.newHashMap());
		
		boolean changed = false;
		
		// Remove null values from map
		final Set<String> keysToRemove = Maps.filterValues(settings, v -> v == null).keySet();
		for (final String key : keysToRemove) {
			changed |= (updatedSettings.remove(key) != null);
		}

		// Merge (add or modify) non-null values
		final Set<String> keysToUpdate = Maps.filterValues(settings, v -> v != null).keySet();
		for (final String key : keysToUpdate) {
			changed |= updateProperty(settings.get(key), 			// value 
					() -> updatedSettings.get(key),                 // getter
					value -> updatedSettings.put(key, value));      // setter 
		}
		
		if (changed) {
			updated.settings(updatedSettings);
		}
		
		return changed;
	}

	private boolean updateExtensionOf(final TransactionContext context, 
			final ResourceDocument.Builder codeSystem, 
			final ResourceURI currentExtensionOf, 
			final String resourceId) {
		
		if (extensionOf != null && !extensionOf.equals(currentExtensionOf)) {
			
			if (extensionOf.isHead() || extensionOf.isLatest()) {
				throw new BadRequestException("Base code system version was not expicitly given (can not be empty, "
						+ "LATEST or HEAD) in extensionOf URI %s.", extensionOf);
			}
			
			final String versionId = extensionOf.getPath();
			
			final Optional<Version> extensionOfVersion = ResourceRequests.prepareSearchVersion()
					.one()
					.filterByResource(extensionOf.withoutPath())
					.filterByVersionId(versionId)
					.build()
					.execute(context)
					.first();
			
			if (!extensionOfVersion.isPresent()) {
				throw new BadRequestException("Couldn't find base resource version for extensionOf URI %s.", extensionOf);
			}
			
			// The working branch prefix is determined by the extensionOf resource version's path
			final String newResourceBranchPath = Branch.get(extensionOfVersion.get().getBranchPath(), resourceId);
			
			if (branchPath != null && !branchPath.equals(newResourceBranchPath)) {
				throw new BadRequestException("Branch path is inconsistent with extensionOf URI ('%s' given, should be '%s').",
						branchPath, newResourceBranchPath);
			}

			codeSystem.extensionOf(extensionOf);
			codeSystem.branchPath(newResourceBranchPath);
			return true;
		}
		
		return false;
	}

	private boolean updateBranchPath(final TransactionContext context, 
			final ResourceDocument.Builder codeSystem, 
			final String currentBranchPath) {
		
		// if extensionOf is set, branch path changes are already handled in updateExtensionOf
		if (extensionOf == null && branchPath != null && !currentBranchPath.equals(branchPath)) {
			try {
				final Branch branch = RepositoryRequests
						.branching()
						.prepareGet(branchPath)
						.build()
						.execute(context);
				
				if (branch.isDeleted()) {
					throw new BadRequestException("Branch with identifier '%s' is deleted.", branchPath);
				}
				
			} catch (NotFoundException e) {
				throw e.toBadRequestException();
			}
			

			// TODO: check if update branch path coincides with a version working path 
			// and update extensionOf accordingly?
			codeSystem.extensionOf(null);
			codeSystem.branchPath(branchPath);
			return true;
		}
		
		return false;
	}
}
