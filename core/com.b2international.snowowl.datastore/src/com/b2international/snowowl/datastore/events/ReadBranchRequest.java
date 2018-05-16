/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.events;

import com.b2international.index.revision.BaseRevisionBranching;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.index.revision.RevisionBranch.BranchState;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.BranchData;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.datastore.BranchPathUtils;

/**
 * @since 4.1
 */
public final class ReadBranchRequest extends BranchRequest<Branch> {

	public ReadBranchRequest(final String branchPath) {
		super(branchPath);
	}
	
	@Override
	public Branch execute(RepositoryContext context) {
		final BaseRevisionBranching branching = context.service(BaseRevisionBranching.class);
		final RevisionBranch branch = branching.getBranch(getBranchPath());
		final BranchState state = branching.getBranchState(branch);
		return new BranchData(branch, state, BranchPathUtils.createPath(branch.getPath()));
	}
	
}
