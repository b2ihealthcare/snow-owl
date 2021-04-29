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
package com.b2international.snowowl.core.codesystem;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.index.revision.TimestampProvider;
import com.b2international.index.revision.RevisionBranch.BranchState;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.internal.ResourceRepository;
import com.b2international.snowowl.core.repository.RepositoryRequests;

/**
 * @since 7.15.0
 */
final class CodeSystemCompleteUpgradeRequest implements Request<ServiceProvider, Boolean> {

	private static final long serialVersionUID = 1L;
	
	@NotEmpty
	private String codeSystemId;

	void setCodeSystemId(String codeSystemId) {
		this.codeSystemId = codeSystemId;
	}
	
	@Override
	public Boolean execute(ServiceProvider context) {
		CodeSystem codeSystem = CodeSystemRequests.prepareGetCodeSystem(codeSystemId)
				.setExpand(CodeSystem.Expand.UPGRADE_OF_BRANCH_INFO + "()")
				.build()
				.execute(context);
		
		if (codeSystem.getUpgradeOfBranchInfo() == null) {
			throw new BadRequestException("Code System '%s' is not an upgrade Code System", codeSystemId);
		} else {
			if (codeSystem.getUpgradeOfBranchInfo().getState() == BranchState.DIVERGED || codeSystem.getUpgradeOfBranchInfo().getState() == BranchState.BEHIND) {
				throw new BadRequestException("Cannot complete upgrade '%s' because it is not in sync with the original source", codeSystemId);
			}
		}
		
		ResourceRepository resourceRepository = context.service(ResourceRepository.class);
		
		// TODO create ResourceContext, ResourceRequest and ResourceTransactionContext 
		// mark the upgrade Code System completed by removing it from the index and updating the upgradeOf CodeSystem branch to the branch of the Upgrade
		final long timestamp = context.service(TimestampProvider.class).getTimestamp();
		resourceRepository.prepareCommit()
			.commit(timestamp, author, String.format("Complete upgrade of %s to %s", codeSystem.getUpgradeOf().getResourceId(), codeSystem.getExtensionOf()));
		
		CodeSystemRequests.prepareUpdateCodeSystem(codeSystem.getUpgradeOf().getResourceId())
			.setBranchPath(codeSystem.getBranchPath())
			.setExtensionOf(codeSystem.getExtensionOf())
			.build()
			.execute(tx);
	
		tx.delete(codeSystem);
		return Boolean.TRUE;
	}

}
