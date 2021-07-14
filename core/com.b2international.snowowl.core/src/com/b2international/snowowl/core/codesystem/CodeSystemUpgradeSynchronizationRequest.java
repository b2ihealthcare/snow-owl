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

import java.util.List;

import javax.validation.constraints.NotNull;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.uri.CodeSystemURI;
import com.b2international.snowowl.core.uri.ResourceURIPathResolver;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 7.17
 */
public final class CodeSystemUpgradeSynchronizationRequest implements Request<RepositoryContext, Boolean> {

	private static final long serialVersionUID = 1L;
	
	@JsonProperty
	@NotNull
	private CodeSystemURI codeSystemId;
	
	@JsonProperty
	@NotNull
	private CodeSystemURI source;

	CodeSystemUpgradeSynchronizationRequest(CodeSystemURI codeSystemId, CodeSystemURI source) {
		this.codeSystemId = codeSystemId;
		this.source = source;
	}
	
	@Override
	public Boolean execute(RepositoryContext context) {
		final String message = String.format("Merge %s into %s", source, codeSystemId);
		
		CodeSystem codeSystem = CodeSystemRequests.prepareGetCodeSystem(codeSystemId.getCodeSystem()).build().execute(context);
		
		if (codeSystem.getUpgradeOf() == null) {
			throw new BadRequestException("Code System '%s' is not an Upgrade Code System. It cannot be synchronized with '%s'.", codeSystemId, source);
		} 
		
		final String sourceBranchPath = context.service(ResourceURIPathResolver.class).resolve(context, List.of(source)).stream().findFirst().get();
		// merge all changes from the source to the current upgrade of branch
		RepositoryRequests.merging()
			.prepareCreate()
			.setSource(sourceBranchPath) 
			.setTarget(codeSystem.getBranchPath())
			.setUserId(context.service(User.class).getUsername())
			.setCommitComment(message)
			.setSquash(false)
			.build()
			.execute(context);

		if (!codeSystem.getUpgradeOf().equals(source)) {
			return RepositoryRequests.prepareCommit()
					.setCommitComment(String.format("Update upgradeOf from '%s' to '%s'", codeSystem.getUpgradeOf(), source))
					.setBody((tx) -> {
						CodeSystemEntry entry = tx.lookup(codeSystemId.getCodeSystem(), CodeSystemEntry.class);
						tx.add(CodeSystemEntry.builder(entry).upgradeOf(source).build());
						tx.commit();
						return Boolean.TRUE;
					})
					.build(codeSystem.getCodeSystemURI())
					.getRequest()
					.execute(context)
					.getResultAs(Boolean.class);
		} else {
			return Boolean.TRUE;
		}
	}

}
