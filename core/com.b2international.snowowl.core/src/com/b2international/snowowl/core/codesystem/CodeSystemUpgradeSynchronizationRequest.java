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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.b2international.commons.exceptions.ApiError;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.context.ResourceRepositoryCommitRequestBuilder;
import com.b2international.commons.exceptions.ConflictException;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.merge.Merge;
import com.b2international.snowowl.core.merge.MergeConflict;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.request.BranchRequest;
import com.b2international.snowowl.core.uri.ResourceURIPathResolver;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 7.17
 */
public final class CodeSystemUpgradeSynchronizationRequest implements Request<RepositoryContext, Boolean> {

	private static final long serialVersionUID = 1L;
	
	@JsonProperty
	@NotNull
	private ResourceURI codeSystemId;
	
	@JsonProperty
	@NotNull
	private ResourceURI source;

	CodeSystemUpgradeSynchronizationRequest(ResourceURI codeSystemId, ResourceURI source) {
		this.codeSystemId = codeSystemId;
		this.source = source;
	}
	
	@Override
	public Boolean execute(RepositoryContext context) {
		final String message = String.format("Merge %s into %s", source, codeSystemId);
		
		CodeSystem codeSystem = CodeSystemRequests.prepareGetCodeSystem(codeSystemId.getResourceId()).build().execute(context);
		
		if (codeSystem.getUpgradeOf() == null) {
			throw new BadRequestException("Code System '%s' is not an Upgrade Code System. It cannot be synchronized with '%s'.", codeSystemId, source);
		} 
		
		final String sourceBranchPath = context.service(ResourceURIPathResolver.class).resolve(context, List.of(source)).stream().findFirst().get();
		// merge all changes from the source to the current upgrade of branch
		final Merge merge = RepositoryRequests.merging()
			.prepareCreate()
			.setSource(sourceBranchPath) 
			.setTarget(codeSystem.getBranchPath())
			.setUserId(context.service(User.class).getUsername())
			.setCommitComment(message)
			.setSquash(false)
			.build(codeSystem.getToolingId())
			.getRequest()
			.execute(context);
		
		if (merge.getStatus() != Merge.Status.COMPLETED) {
			// report conflicts
			ApiError apiError = merge.getApiError();
			Collection<MergeConflict> conflicts = merge.getConflicts();
			context.log().error("Failed to sync source CodeSystem content to upgrade CodeSystem. Error: {}. Conflicts: {}", apiError.getMessage(), conflicts);
			throw new ConflictException("Upgrade code system synchronization can not be performed due to conflicting content errors.")
				.withAdditionalInfo(Map.of(
					"conflicts", conflicts,
					"mergeError", apiError.getMessage()
				));
		}

		if (!codeSystem.getUpgradeOf().equals(source)) {
			return new BranchRequest<>(Branch.MAIN_PATH,
				new ResourceRepositoryCommitRequestBuilder()
					.setBody((tx) -> {
						ResourceDocument entry = tx.lookup(codeSystemId.getResourceId(), ResourceDocument.class);
						tx.add(ResourceDocument.builder(entry).upgradeOf(source).build());
						tx.commit(String.format("Update upgradeOf from '%s' to '%s'", codeSystem.getUpgradeOf(), source));
						return Boolean.TRUE;
					})
					.setCommitComment(String.format("Complete upgrade of %s to %s", codeSystem.getUpgradeOf().getResourceId(), codeSystem.getExtensionOf()))
					.build()
			).execute(context).getResultAs(Boolean.class);
		} else {
			return Boolean.TRUE;
		}
	}

}
