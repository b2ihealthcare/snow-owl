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
package com.b2international.snowowl.datastore.server.internal;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;

import com.b2international.snowowl.core.api.index.IIndexServerServiceManager;
import com.b2international.snowowl.core.api.index.IIndexUpdater;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.cdo.ICDORepository;
import com.b2international.snowowl.datastore.cdo.ICDORepositoryManager;
import com.b2international.snowowl.datastore.server.CDOServerUtils;
import com.b2international.snowowl.datastore.server.cdo.CDOConflictProcessorBroker;
import com.b2international.snowowl.datastore.server.cdo.ICDOConflictProcessor;

/**
 * @since 4.1
 */
public class RepositoryWrapper implements IRepository {

	private final String repositoryId;
	private final ICDOConnectionManager connectionManager;
	private final IIndexServerServiceManager indexServerServiceManager;
	private ICDORepositoryManager repositoryManager;

	public RepositoryWrapper(String repositoryId, 
			ICDOConnectionManager connectionManager,
			ICDORepositoryManager repositoryManager, 
			IIndexServerServiceManager indexServerServiceManager) {

		this.repositoryId = repositoryId;
		this.connectionManager = connectionManager;
		this.repositoryManager = repositoryManager;
		this.indexServerServiceManager = indexServerServiceManager;
	}

	@Override
	public ICDOConnection getConnection() {
		return connectionManager.getByUuid(repositoryId);
	}
	
	@Override
	public CDOBranch getCdoMainBranch() {
		return getConnection().getMainBranch();
	}
	
	@Override
	public CDOBranchManager getCdoBranchManager() {
		return getCdoMainBranch().getBranchManager();
	}
	
	@Override
	public IIndexUpdater<?> getIndexUpdater() {
		return indexServerServiceManager.getByUuid(repositoryId);
	}
	
	@Override
	public ICDORepository getCdoRepository() {
		return repositoryManager.getByUuid(repositoryId);
	}
	
	@Override
	public String getCdoRepositoryId() {
		return repositoryId;
	}
	
	@Override
	public ICDOConflictProcessor getConflictProcessor() {
		return CDOConflictProcessorBroker.INSTANCE.getProcessor(repositoryId);
	}
	
	@Override
    public long getBaseTimestamp(CDOBranch branch) {
        return branch.getBase().getTimeStamp();
    }
	
	@Override
	public long getHeadTimestamp(CDOBranch branch) {
		return Math.max(getBaseTimestamp(branch), CDOServerUtils.getLastCommitTime(branch));
	}
}
