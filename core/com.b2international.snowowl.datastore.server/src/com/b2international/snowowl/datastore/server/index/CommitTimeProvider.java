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
package com.b2international.snowowl.datastore.server.index;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.emf.cdo.common.branch.CDOBranch;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.server.CDOServerUtils;

/**
 * Default commit time provider. This implementation uses CDO for getting the latest commit time
 * from the underlying store.
 *
 */
public class CommitTimeProvider implements ICommitTimeProvider {

	private final String repositoryUuid;

	public CommitTimeProvider(final String repositoryUuid) {
		this.repositoryUuid = checkNotNull(repositoryUuid, "repositoryUuid");
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.server.index.ICommitTimeProvider#getCommitTime(com.b2international.snowowl.core.api.IBranchPath)
	 */
	@Override
	public long getCommitTime(final IBranchPath branchPath) {
		final ICDOConnectionManager connectionManager = ApplicationContext.getInstance().getService(ICDOConnectionManager.class);
		final ICDOConnection connection = connectionManager.getByUuid(repositoryUuid);
		
		final CDOBranch branch = connection.getBranch(branchPath);
		long commitTime = CDOServerUtils.getLastCommitTime(branch);
		if (Long.MIN_VALUE == commitTime) {
			commitTime = branch.getBase().getTimeStamp();
		}
		
		return commitTime;
		
	}

}