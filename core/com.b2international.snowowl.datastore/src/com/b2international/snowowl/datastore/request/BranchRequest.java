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
package com.b2international.snowowl.datastore.request;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import org.eclipse.emf.cdo.common.branch.CDOBranch;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.BranchContextProvider;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

/**
 * @since 4.5
 */
public final class BranchRequest<B> extends DelegatingRequest<RepositoryContext, BranchContext, B> {

	@JsonProperty
	private final String branchPath;
	
	public BranchRequest(String branchPath, Request<BranchContext, B> next) {
		super(next);
		this.branchPath = checkNotNull(branchPath, "branchPath");
	}
	
	@Override
	public B execute(RepositoryContext context) {
		final Branch branch = ensureAvailability(context);
		return next(context.service(BranchContextProvider.class).get(context, branch, branchPath));
	}
	
	private Branch ensureAvailability(RepositoryContext context) {
		final ICDOConnectionManager connectionManager = context.service(ICDOConnectionManager.class);
		
		final List<String> branchesToCheck = newArrayList();
		if (RevisionIndex.isBaseRefPath(branchPath)) {
			branchesToCheck.add(branchPath.substring(0, branchPath.length() - 1));
		} else if (RevisionIndex.isRevRangePath(branchPath)) {
			branchesToCheck.addAll(ImmutableList.copyOf(RevisionIndex.getRevisionRangePaths(branchPath)));
		} else {
			branchesToCheck.add(branchPath);
		}
		
		Branch branch = null; 
		for (String branchToCheck : branchesToCheck) {
			branch = RepositoryRequests.branching().prepareGet(branchToCheck).build().execute(context);
			
			if (branch.isDeleted()) {
				throw new BadRequestException("Branch '%s' has been deleted and cannot accept further modifications.", branchToCheck);
			}
			
			final ICDOConnection connection = connectionManager.getByUuid(context.id());
			final CDOBranch cdoBranch = connection.getBranch(branch.branchPath());
			if (cdoBranch == null) {
				throw new NotFoundException("Branch", branchToCheck);
			}
		}
		
		return branch;
	}
	
}
