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

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.BaseRequest;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.datastore.CodeSystems;
import com.b2international.snowowl.terminologymetadata.CodeSystem;

/**
 * @since 4.7
 */
final class CodeSystemUpdateRequest extends BaseRequest<TransactionContext, Void> {

	private static final long serialVersionUID = 1L;

	private final String uniqueId;
	private final String repositoryId;

	private String oid;
	private String name;
	private String shortName;
	private String link;
	private String language;
	private String citation;
	private String branchPath;
	private String iconPath;
	private String terminologyId;

	CodeSystemUpdateRequest(final String repositoryId, String uniqueId) {
		this.repositoryId = repositoryId;
		this.uniqueId = uniqueId;
	}

	void setOid(final String oid) {
		this.oid = oid;
	}

	void setName(final String name) {
		this.name = name;
	}

	void setShortName(final String shortName) {
		this.shortName = shortName;
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

	void setTerminologyId(final String terminologyId) {
		this.terminologyId = terminologyId;
	}

	@Override
	public Void execute(final TransactionContext context) {
		final CodeSystem codeSystem = context.lookup(uniqueId, CodeSystem.class);
		
		if (codeSystem == null) {
			throw new BadRequestException("Code System with unique ID %s was not found.", uniqueId);
		}

		updateOid(codeSystem, context);
		updateName(codeSystem);
		updateShortName(codeSystem, context);
		updateLink(codeSystem);
		updateLanguage(codeSystem);
		updateCitation(codeSystem);
		updateBranchPath(codeSystem);
		updateIconPath(codeSystem);
		updateTerminologyId(codeSystem);

		return null;
	}

	private void updateOid(final CodeSystem codeSystem, final TransactionContext context) {
		if (oid == null) {
			return;
		}

		if (!codeSystem.getCodeSystemOID().equals(oid)) {
			final CodeSystems codeSystems = new CodeSystemRequests(repositoryId)
					.prepareSearchCodeSystem()
					.setOid(oid)
					.build(IBranchPath.MAIN_BRANCH)
					.execute(context);
			
			if (codeSystems.getItems().isEmpty()) {
				codeSystem.setCodeSystemOID(oid);
			} else {
				throw new BadRequestException("Code System OID %s is not unique.", oid);
			}
		}
	}

	private void updateName(final CodeSystem codeSystem) {
		if (name == null) {
			return;
		}

		if (!codeSystem.getName().equals(name)) {
			codeSystem.setName(name);
		}
	}

	private void updateShortName(final CodeSystem codeSystem, final TransactionContext context) {
		if (shortName == null) {
			return;
		}

		if (!codeSystem.getShortName().equals(shortName)) {
			final CodeSystems codeSystems = new CodeSystemRequests(repositoryId)
					.prepareSearchCodeSystem()
					.setShortName(shortName)
					.build(IBranchPath.MAIN_BRANCH)
					.execute(context);
			
			if (codeSystems.getItems().isEmpty()) {
				codeSystem.setShortName(shortName);
			} else {
				throw new BadRequestException("Code System short name %s is not unique.", shortName);
			}
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

	private void updateBranchPath(final CodeSystem codeSystem) {
		if (branchPath == null) {
			return;
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

	private void updateTerminologyId(final CodeSystem codeSystem) {
		if (terminologyId == null) {
			return;
		}

		if (!codeSystem.getTerminologyComponentId().equals(terminologyId)) {
			codeSystem.setTerminologyComponentId(terminologyId);
		}
	}

	@Override
	protected Class<Void> getReturnType() {
		return Void.class;
	}

}
