/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.terminologyregistry.core.request;

import java.util.List;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.authorization.RepositoryAccessControl;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.request.UpdateRequest;
import com.b2international.snowowl.datastore.CodeSystem;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.identity.domain.Permission;

/**
 * @since 4.7
 */
final class CodeSystemUpdateRequest extends UpdateRequest implements RepositoryAccessControl {

	private static final long serialVersionUID = 1L;

	private String name;
	private String link;
	private String language;
	private String citation;
	private String branchPath;
	private String iconPath;
	private List<String> uris;

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
	
	void setUris(List<String> uris) {
		this.uris = uris;
	}

	@Override
	public Boolean execute(final TransactionContext context) {
		CodeSystem codeSystem = context.lookup(componentId(), CodeSystem.class);
		final CodeSystem.Builder updated = CodeSystem.builder(codeSystem);

		boolean changed = false;
		changed |= updateProperty(name, codeSystem::getName, updated::name);
		changed |= updateProperty(link, codeSystem::getOrgLink, updated::orgLink);
		changed |= updateProperty(language, codeSystem::getLanguage, updated::language);
		changed |= updateProperty(citation, codeSystem::getCitation, updated::citation);
		changed |= updateProperty(iconPath, codeSystem::getIconPath, updated::iconPath);
		changed |= updateBranchPath(context, updated, codeSystem.getBranchPath());
		changed |= updateProperty(uris, codeSystem::getUris, updated::uris);
		
		if (changed) {
			context.add(updated.build());
		}

		return changed;
	}

	private boolean updateBranchPath(final TransactionContext context, final CodeSystem.Builder codeSystem, final String currentBranchPath) {
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
