/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.domain;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.core.RepositoryInfo;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.repository.RepositoryRequests;

/**
 * Execution context for {@link Request requests} targeting a repository.
 *
 * @since 4.5
 */
public interface RepositoryContext extends ServiceProvider {

	default RepositoryInfo info() {
		return service(RepositoryInfo.class);
	}
	
	@Override
	default DelegatingContext.Builder<? extends RepositoryContext> inject() {
		return new DelegatingContext.Builder<>(RepositoryContext.class, this);
	}
	
	default BranchContext openBranch(RepositoryContext context, String path) {
		return context.service(ContextConfigurer.class).configure(new RepositoryBranchContext(context, path, ensureAvailability(context, path)));
	}

	private Branch ensureAvailability(RepositoryContext context, String path) {
		final List<String> branchesToCheck = newArrayList();
		if (RevisionIndex.isBranchAtPath(path)) {
			branchesToCheck.add(path.split(RevisionIndex.AT_CHAR)[0]);
		} else if (RevisionIndex.isBaseRefPath(path)) {
			branchesToCheck.add(path.substring(0, path.length() - 1));
		} else if (RevisionIndex.isRevRangePath(path)) {
			branchesToCheck.addAll(List.of(RevisionIndex.getRevisionRangePaths(path)));
		} else {
			branchesToCheck.add(path);
		}
		
		Branch branch = null;
		for (String branchToCheck : branchesToCheck) {
			branch = RepositoryRequests.branching().prepareGet(branchToCheck).build().execute(context);
			
			if (branch.isDeleted()) {
				throw new BadRequestException("Branch '%s' has been deleted and cannot accept further modifications.", branchToCheck);
			}
		}
		
		return branch;
	}
}
