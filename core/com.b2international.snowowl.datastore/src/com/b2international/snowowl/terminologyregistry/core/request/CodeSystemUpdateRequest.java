/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.terminologymetadata.CodeSystem;

/**
 * @since 4.7
 */
final class CodeSystemUpdateRequest implements Request<TransactionContext, Boolean> {

	private static final long serialVersionUID = 1L;

	private final String uniqueId;

	private String name;
	private String link;
	private String language;
	private String citation;
	private String branchPath;
	private String iconPath;

	CodeSystemUpdateRequest(final String uniqueId) {
		this.uniqueId = uniqueId;
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

	@Override
	public Boolean execute(final TransactionContext context) {
		final CodeSystem codeSystem = context.lookup(uniqueId, CodeSystem.class);

		updateName(codeSystem);
		updateLink(codeSystem);
		updateLanguage(codeSystem);
		updateCitation(codeSystem);
		updateBranchPath(codeSystem, context);
		updateIconPath(codeSystem);

		return Boolean.TRUE;
	}

	private void updateName(final CodeSystem codeSystem) {
		if (name == null) {
			return;
		}

		if (!codeSystem.getName().equals(name)) {
			codeSystem.setName(name);
		}
	}

	private void updateLink(final CodeSystem codeSystem) {
		if (link == null) {
			return;
		}

		if (!codeSystem.getMaintainingOrganizationLink().equals(link)) {
			codeSystem.setMaintainingOrganizationLink(link);
		}
	}

	private void updateLanguage(final CodeSystem codeSystem) {
		if (language == null) {
			return;
		}

		if (!codeSystem.getLanguage().equals(language)) {
			codeSystem.setLanguage(language);
		}
	}

	private void updateCitation(final CodeSystem codeSystem) {
		if (citation == null) {
			return;
		}

		if (!codeSystem.getCitation().equals(citation)) {
			codeSystem.setCitation(citation);
		}
	}

	private void updateBranchPath(final CodeSystem codeSystem, final TransactionContext context) {
		if (branchPath == null) {
			return;
		}
		
		final Branch branch = RepositoryRequests
				.branching()
				.prepareGet(branchPath)
				.build()
				.execute(context);
		
		if (branch.isDeleted()) {
			throw new BadRequestException("Branch with identifier %s is deleted.", branchPath);
		}

		if (!codeSystem.getBranchPath().equals(branchPath)) {
			codeSystem.setBranchPath(branchPath);
		}
	}

	private void updateIconPath(final CodeSystem codeSystem) {
		if (iconPath == null) {
			return;
		}

		if (!codeSystem.getIconPath().equals(iconPath)) {
			codeSystem.setIconPath(iconPath);
		}
	}

}
