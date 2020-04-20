/*
 * Copyright 2017-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.branch.compare;

import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.revision.ObjectId;
import com.b2international.index.revision.RevisionCompare;
import com.b2international.index.revision.RevisionCompareDetail;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.authorization.RepositoryAccessControl;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.codesystem.CodeSystemEntry;
import com.b2international.snowowl.core.codesystem.CodeSystemVersionEntry;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.repository.TerminologyComponents;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 5.9
 */
final class BranchCompareRequest implements Request<RepositoryContext, BranchCompareResult>, RepositoryAccessControl {

	@JsonProperty
	private String base;
	
	@NotEmpty
	@JsonProperty
	private String compare;
	
	@Min(0)
	@JsonProperty
	private int limit;
	
	BranchCompareRequest() {
	}
	
	void setBaseBranch(String baseBranch) {
		this.base = baseBranch;
	}
	
	void setCompareBranch(String compareBranch) {
		this.compare = compareBranch;
	}
	
	void setLimit(int limit) {
		this.limit = limit;
	}
	
	@Override
	public BranchCompareResult execute(RepositoryContext context) {
		final RevisionIndex index = context.service(RevisionIndex.class);
		final Branch branchToCompare = RepositoryRequests.branching().prepareGet(compare).build().execute(context);
		final long compareHeadTimestamp = branchToCompare.headTimestamp();
		
		final RevisionCompare compareResult;
		final String baseBranchPath;
		if (base != null) {
			compareResult = index.compare(base, compare, limit);
			baseBranchPath = base;
		} else {
			compareResult = index.compare(compare, limit);
			baseBranchPath = branchToCompare.parentPath();
		}
		
		final BranchCompareResult.Builder result = BranchCompareResult.builder(baseBranchPath, compare, compareHeadTimestamp)
				.totalNew(compareResult.getTotalAdded())
				.totalChanged(compareResult.getTotalChanged())
				.totalDeleted(compareResult.getTotalRemoved());
		
		for (RevisionCompareDetail detail : compareResult.getDetails()) {
			final ObjectId affectedId;
			if (detail.isComponentChange()) {
				affectedId = detail.getComponent();
			} else {
				affectedId = detail.getObject();
			}
			final short terminologyComponentId = context.service(TerminologyComponents.class).getTerminologyComponentId(DocumentMapping.getClass(affectedId.type()));
			if (CodeSystemEntry.TERMINOLOGY_COMPONENT_ID == terminologyComponentId || CodeSystemVersionEntry.TERMINOLOGY_COMPONENT_ID == terminologyComponentId) {
				continue;
			}
			
			final ComponentIdentifier identifier = ComponentIdentifier.of(terminologyComponentId, affectedId.id());
			
			switch (detail.getOp()) {
			case ADD:
				result.putNewComponent(identifier);
				break;
			case CHANGE:
				result.putChangedComponent(identifier);
				break;
			case REMOVE:
				result.putDeletedComponent(identifier);
				break;
			}
		}
		
		return result.build();
	}
	
	@Override
	public String getOperation() {
		return Permission.BROWSE;
	}
	
}
