/*
 * Copyright 2011-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.branch;

import java.util.SortedSet;

import com.b2international.commons.options.Metadata;
import com.b2international.index.revision.BaseRevisionBranching;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.snowowl.core.authorization.AccessControl;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.identity.Permission;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 5.0
 */
public final class BranchUpdateRequest extends BranchBaseRequest<Boolean> implements AccessControl {

	private static final long serialVersionUID = 1L;

	@JsonProperty
	private Metadata metadata;
	
	@JsonProperty
	private SortedSet<String> nameAliases;
	
	BranchUpdateRequest(String branchPath) {
		super(branchPath);
	}
	
	void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}
	
	void setNameAliases(SortedSet<String> nameAliases) {
		this.nameAliases = nameAliases;
	}
	
	@Override
	public Boolean execute(RepositoryContext context) {
		BaseRevisionBranching branching = context.service(BaseRevisionBranching.class);
		RevisionBranch branch = branching.getBranch(getBranchPath());
		return branching.commit(writer -> {
			boolean changed = false;
			RevisionBranch.Builder updated = branch.toBuilder();
			changed |= branching.updateMetadata(branch, updated, metadata);
			changed |= branching.updateNameAliases(branch, updated, nameAliases);
			
			if (changed) {
				writer.put(updated.build());
			}
			
			return changed;
		});
	}
	
	@Override
	public String getOperation() {
		return Permission.OPERATION_BROWSE;
	}

}
