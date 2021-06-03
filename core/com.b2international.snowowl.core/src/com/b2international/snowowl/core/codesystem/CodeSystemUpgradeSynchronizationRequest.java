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

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.uri.CodeSystemURI;

public final class CodeSystemUpgradeSynchronizationRequest implements Request<RepositoryContext, Boolean> {

	private static final long serialVersionUID = 1L;
	
	@NotNull
	private CodeSystem codeSystem;
	@NotNull
	private CodeSystemURI sourceURI;
	@NotEmpty
	private String sourceBranchPath;

	void setCodeSystem(CodeSystem codeSystem) {
		this.codeSystem = codeSystem;
	}
	
	public void setSource(CodeSystemURI sourceURI) {
		this.sourceURI = sourceURI;
	}
	
	public void setSourceBranchPath(String sourceBranchPath) {
		this.sourceBranchPath = sourceBranchPath;
	}
	
	@Override
	public Boolean execute(RepositoryContext context) {

		final String messageTemplate = String.format("Merge %s into %s", sourceURI, codeSystem.getCodeSystemURI());

		RepositoryRequests.merging()
			.prepareCreate()
			.setSource(sourceBranchPath) // Upgrade Of latest state will be the source branch
			.setTarget(codeSystem.getBranchPath()) // the current CodeSystem is the Upgrade CodeSystem
			.setUserId(context.service(User.class).getUsername())
			.setCommitComment(messageTemplate)
			.setSquash(false)
			.build()
			.execute(context);

		return RepositoryRequests.prepareCommit()
				.setCommitComment(String.format("Update upgradeOf Code System %s to %s", codeSystem.getUpgradeOf(), sourceURI))
				.setBody((tx) -> {
					CodeSystemRequests.prepareUpdateCodeSystem(codeSystem.getShortName())
					.setUpgradeOf(sourceURI)
					.build()
					.execute(tx);

					return Boolean.TRUE;
				})
				.build(codeSystem.getCodeSystemURI())
				.getRequest()
				.execute(context)
				.getResultAs(Boolean.class);
	}

}
