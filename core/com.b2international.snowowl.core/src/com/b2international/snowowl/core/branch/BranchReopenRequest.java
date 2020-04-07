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
package com.b2international.snowowl.core.branch;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.index.revision.BaseRevisionBranching;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.snowowl.core.authorization.RepositoryAccessControl;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.identity.Permission;

/**
 * @since 4.1
 */
public final class BranchReopenRequest extends BranchBaseRequest<Boolean> implements RepositoryAccessControl {
	
	public BranchReopenRequest(final String path) {
		super(path);
	}
	
	@Override
	public Boolean execute(RepositoryContext context) {
		final BaseRevisionBranching branching = context.service(BaseRevisionBranching.class);
		final RevisionBranch branch = branching.getBranch(getBranchPath());
		
		try {
			final RevisionBranch parentBranch = branching.getBranch(branch.getParentPath());
			branching.reopen(parentBranch, branch.getName(), branch.metadata());
			return true;
		} catch (NotFoundException e) {
			// if parent not found, convert it to BadRequestException
			throw e.toBadRequestException();
		}
	}
	
	@Override
	public String getOperation() {
		return Permission.EDIT;
	}
	
}
