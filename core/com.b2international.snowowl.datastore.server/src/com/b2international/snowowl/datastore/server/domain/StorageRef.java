/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.domain;

import org.eclipse.emf.cdo.common.branch.CDOBranch;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.eventbus.IEventBus;

/**
 * @since 1.0
 * @deprecated - will be removed in 4.7, use {@link Request} API instead
 */
public class StorageRef implements InternalStorageRef {

	private static final long DEFAULT_ASYNC_TIMEOUT_DELAY = 5000;

	private static ICDOConnectionManager getConnectionManager() {
		return ApplicationContext.getServiceForClass(ICDOConnectionManager.class);
	}

	private static IEventBus getEventBus() {
		return ApplicationContext.getServiceForClass(IEventBus.class);
	}

	private final String repositoryId;
	private final String branchPath;
	
	private Branch branch;

	public StorageRef(String repositoryId, String branchPath) {
		this.repositoryId = repositoryId;
		this.branchPath = branchPath;
	}
	
	@Override
	public String getBranchPath() {
		return branchPath;
	}

	@Override
	public String getRepositoryId() {
		return repositoryId;
	}

	protected final void setBranch(Branch branch) {
		this.branch = branch;
	}
	
	@Override
	public Branch getBranch() {
		if (branch == null) {
			branch = RepositoryRequests
						.branching(getRepositoryId())
						.prepareGet(branchPath)
						.executeSync(getEventBus(), DEFAULT_ASYNC_TIMEOUT_DELAY);
		}
		if (branch == null) {
			throw new NotFoundException("Branch", getBranchPath());
		}
		return branch;
	}

	@Override
	public CDOBranch getCdoBranch() {
		final CDOBranch cdoBranch = getCdoBranchOrNull();
		if (null != cdoBranch) {
			return cdoBranch;
		}

		throw new NotFoundException("Branch", getBranchPath());
	}

	private CDOBranch getCdoBranchOrNull() {
		return getConnectionManager().getByUuid(getRepositoryId()).getBranch(getBranch().branchPath());
	}

	@Override
	public final void checkStorageExists() {
		if (getBranch().isDeleted()) {
			throw new BadRequestException("Branch '%s' has been deleted and cannot accept further modifications.", getBranchPath());
		}
		getCdoBranch();
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("StorageRef [repositoryId=");
		builder.append(repositoryId);
		builder.append(", branchPath=");
		builder.append(branchPath);
		builder.append("]");
		return builder.toString();
	}
}