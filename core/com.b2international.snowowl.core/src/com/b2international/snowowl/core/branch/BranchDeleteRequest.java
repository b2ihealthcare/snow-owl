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
package com.b2international.snowowl.core.branch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.index.revision.BaseRevisionBranching;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.authorization.AccessControl;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.repository.PathTerminologyResourceResolver;

/**
 * @since 4.1
 */
public final class BranchDeleteRequest extends BranchBaseRequest<Boolean> implements AccessControl {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(BranchDeleteRequest.class);

	public BranchDeleteRequest(final String branchPath) {
		super(branchPath);
	}

	@Override
	public Boolean execute(RepositoryContext context) {
		try {
			//Remove branch
			context.service(BaseRevisionBranching.class).delete(getBranchPath());
			
			try {
				//Schedule a job to remove issues corresponding to this branch
				TerminologyResource resource = context.service(PathTerminologyResourceResolver.class).resolve(context, context.info().id(), getBranchPath());
				String resourceURI = resource.getResourceURI(getBranchPath()).toString();
				context.service(ValidationCleanupService.class).removeStaleIssues(context, resourceURI);				
			} catch (Exception e) {
				LOGGER.trace("Failed to remove validation issues associated with deleted branch {}", getBranchPath(), e);
			}
			
		} catch (NotFoundException e) {
			// ignore
		}
		return true;
	}
	
	@Override
	public String getOperation() {
		return Permission.OPERATION_EDIT;
	}
	
}
