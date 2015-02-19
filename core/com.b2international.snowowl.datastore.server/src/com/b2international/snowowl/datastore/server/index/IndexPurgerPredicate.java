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

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import org.apache.lucene.index.IndexCommit;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.index.IndexException;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.server.CDOServerUtils;

/**
 * Default index purger predicate implementation.
 *
 */
public class IndexPurgerPredicate implements IIndexPurgerPredicate, IBranchIndexServiceProvider {

	private final String repositoryUuid;
	private final IBranchIndexServiceProvider provider;

	public IndexPurgerPredicate(final String repositoryUuid, final IBranchIndexServiceProvider provider) {
		this.repositoryUuid = checkNotNull(repositoryUuid, "repositoryUuid");
		this.provider = checkNotNull(provider, "provider");
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.server.index.IIndexPurgerPredicate#apply(com.b2international.snowowl.core.api.IBranchPath)
	 */
	@Override
	public boolean apply(final IBranchPath branchPath) {
		
		if (!getServiceForClass(ICDOConnectionManager.class).uuidKeySet().contains(repositoryUuid)) {
			return true;
		}
		
		//MAIN index directory cannot be purged
		if (BranchPathUtils.isMain(branchPath)) {
			return false;
		}
		
		
		final ICDOConnectionManager connectionManager = ApplicationContext.getInstance().getService(ICDOConnectionManager.class);
		final ICDOConnection connection = connectionManager.getByUuid(repositoryUuid);
		
		if (null == connection) {
			return false;
		}
		
		try {
		
			final IndexCommit commit = getBranchService(BranchPathUtils.createMainPath()).getIndexCommit(branchPath);
			if (null != commit) {
				
				if (!CompareUtils.isEmpty(commit.getUserData())) {
					
					final String tagValue = commit.getUserData().get(IndexUtils.INDEX_TAG_KEY);
					if (!StringUtils.isEmpty(tagValue)) {
						
						if (Boolean.parseBoolean(tagValue)) {
							
							//check if patched or not, if patched (has modification) it cannot be deleted
							if (Long.MIN_VALUE != CDOServerUtils.getLastCommitTime(connection.getBranch(branchPath))) {
								return false;
							}
							
							//check if has an task associated index commit
							//can happen when tagged branch does not have any modification but task is created on tagged version branch
							if (getBranchService(branchPath).hasSnapshotIndexCommit()) {
								return false;
							}
							
						}
						
					}
					
				}
				
			}
			
		} catch (final IOException e) {
				throw new IndexException(e);
			}
			
			return true;
		}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.server.index.IBranchIndexServiceProvider#getBranchService(com.b2international.snowowl.core.api.IBranchPath)
	 */
	@Override
	public IndexBranchService getBranchService(final IBranchPath branchPath) {
		return provider.getBranchService(branchPath);
	}

}