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
package com.b2international.snowowl.datastore.internal.branch;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.ConcurrentMap;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;

import com.b2international.snowowl.datastore.branch.Branch;
import com.b2international.snowowl.datastore.branch.BranchManager;
import com.google.common.collect.MapMaker;

/**
 * {@link BranchManager} implementation based on {@link CDOBranch} functionality.
 * 
 * @since 4.1
 */
public class CDOBranchManagerImpl extends BranchManagerImpl {

	private final ConcurrentMap<String, Integer> branches = new MapMaker().makeMap();
	private final InternalCDOBranchManager cdoBranchManager;
	
	public CDOBranchManagerImpl(final InternalCDOBranchManager cdoBranchManager) {
		super(getBasetimestamp(cdoBranchManager.getMainBranch()), new CDOBasedTimestampProvider(cdoBranchManager.getTimeProvider()));
		this.cdoBranchManager = checkNotNull(cdoBranchManager, "cdoBranchManager");
		registerCDOBranch(this.cdoBranchManager.getMainBranch());
	}
	
	CDOBranch getCDOBranch(Branch branch) {
		final Integer branchId = branches.get(branch.path());
		return loadCDOBranch(branchId);
	}
	
	private CDOBranch loadCDOBranch(Integer branchId) {
		if (branchId != null) {
			return cdoBranchManager.getBranch(branchId);
		} else {
			return null;
		}
	}

	@Override
	protected BranchImpl reopen(BranchImpl parent, String name) {
		final CDOBranch childCDOBranch = createCDOBranch(parent, name);
		registerCDOBranch(childCDOBranch);
		return super.reopen(parent, name);
	}
	
	@Override
	protected void postDelete(BranchImpl branch) {
		super.postDelete(branch);
		branches.remove(branch.path());
	}
	
	private CDOBranch createCDOBranch(BranchImpl parent, String name) {
		return getCDOBranch(parent).createBranch(name);
	}

	private void registerCDOBranch(CDOBranch branch) {
		final String path = branch.getPathName();
		final int branchId = branch.getID();
		branches.put(path, branchId);
	}
	
	private static long getBasetimestamp(CDOBranch branch) {
		return branch.getBase().getTimeStamp();
	}
	
}
