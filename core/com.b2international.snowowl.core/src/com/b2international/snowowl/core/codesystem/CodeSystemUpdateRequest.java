/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.codesystem;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.authorization.RepositoryAccessControl;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.request.UpdateRequest;
import com.google.common.collect.Maps;

/**
 * @since 4.7
 */
final class CodeSystemUpdateRequest extends UpdateRequest<TransactionContext> implements RepositoryAccessControl {

	private static final long serialVersionUID = 1L;

	private String name;
	private String link;
	private String language;
	private String citation;
	private String branchPath;
	private String iconPath;
	private Map<String, Object> additionalProperties;

	CodeSystemUpdateRequest(final String uniqueId) {
		super(uniqueId);
	}

	void setName(final String name) {
		this.name = name;
	}

	void setLink(final String link) {
		this.link = link;
	}

	void setLanguage(final String language) {
		this.language = language;
	}

	void setCitation(final String citation) {
		this.citation = citation;
	}

	void setBranchPath(final String branchPath) {
		this.branchPath = branchPath;
	}

	void setIconPath(final String iconPath) {
		this.iconPath = iconPath;
	}
	
	void setAdditionalProperties(final Map<String, Object> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}

	@Override
	public Boolean execute(final TransactionContext context) {
		CodeSystemEntry codeSystem = context.lookup(componentId(), CodeSystemEntry.class);
		final CodeSystemEntry.Builder updated = CodeSystemEntry.builder(codeSystem);

		boolean changed = false;
		changed |= updateProperty(name, codeSystem::getName, updated::name);
		changed |= updateProperty(link, codeSystem::getOrgLink, updated::orgLink);
		changed |= updateProperty(language, codeSystem::getLanguage, updated::language);
		changed |= updateProperty(citation, codeSystem::getCitation, updated::citation);
		changed |= updateProperty(iconPath, codeSystem::getIconPath, updated::iconPath);
		changed |= updateAdditionalProperties(codeSystem, updated);
		changed |= updateBranchPath(context, updated, codeSystem.getBranchPath());
		
		if (changed) {
			context.add(updated.build());
		}

		return changed;
	}

	private boolean updateAdditionalProperties(final CodeSystemEntry codeSystem, final CodeSystemEntry.Builder updated) {
		if (additionalProperties == null || additionalProperties.isEmpty()) {
			return false;
		}
		
		// Get mutable copy of existing properties, or an empty map for starters
		final Map<String, Object> updatedProperties = Optional.ofNullable(codeSystem.getAdditionalProperties())
				.map(Maps::newHashMap)
				.orElse(Maps.newHashMap());
		
		boolean changed = false;
		
		// Remove null values from map
		final Set<String> keysToRemove = Maps.filterValues(additionalProperties, v -> v == null).keySet();
		for (final String key : keysToRemove) {
			changed |= (updatedProperties.remove(key) != null);
		}

		// Merge (add or modify) non-null values
		final Set<String> keysToUpdate = Maps.filterValues(additionalProperties, v -> v != null).keySet();
		for (final String key : keysToUpdate) {
			changed |= updateProperty(additionalProperties.get(key), // value 
					() -> updatedProperties.get(key),                // getter
					value -> updatedProperties.put(key, value));     // setter 
		}
		
		if (changed) {
			updated.additionalProperties(updatedProperties);
		}
		
		return changed;
	}

	private boolean updateBranchPath(final TransactionContext context, final CodeSystemEntry.Builder codeSystem, final String currentBranchPath) {
		if (branchPath != null && !currentBranchPath.equals(branchPath)) {
			final Branch branch = RepositoryRequests
					.branching()
					.prepareGet(branchPath)
					.build()
					.execute(context);
			
			if (branch.isDeleted()) {
				throw new BadRequestException("Branch with identifier %s is deleted.", branchPath);
			}

			codeSystem.branchPath(branchPath);
			return true;
		}
		return false;
	}

	@Override
	public String getOperation() {
		return Permission.EDIT;
	}
}
