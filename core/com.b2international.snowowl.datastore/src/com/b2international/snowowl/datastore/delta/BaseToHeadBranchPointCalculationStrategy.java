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
package com.b2international.snowowl.datastore.delta;

import org.eclipse.emf.cdo.common.branch.CDOBranch;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IBranchPoint;
import com.b2international.snowowl.core.api.IBranchPoint.NullBranchPoint;
import com.b2international.snowowl.datastore.BranchPointUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;

/**
 * Strategy providing two {@link IBranchPoint branch point}s for given {@link IBranchPath branch}.
 * The source {@link IBranchPoint branch point} points to the base of the branch while the target {@link IBranchPoint point}
 * indicates the HEAD of the given branch. 
 *
 */
public class BaseToHeadBranchPointCalculationStrategy extends BranchPointCalculationStrategy {

	private static final long serialVersionUID = -2168977276365282785L;

	/**
	 * Creates a new strategy instance based on the underlying {@link IBranchPath}. 
	 * @param connection the CDO connection where the branch points have to be opened.
	 * @param branchPath the branch path where the calculation should be performed.
	 */
	public BaseToHeadBranchPointCalculationStrategy(final ICDOConnection connection, final IBranchPath branchPath) {
		super(connection, branchPath);
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.delta.IBranchPointCalculationStrategy#getSourceBranchPath()
	 */
	@Override
	public IBranchPoint getSourceBranchPoint() {
		
		final CDOBranch sourceBranch = getConnection().getBranch(getBranchPath());
		
		//branch does not exist yet.
		if (null == sourceBranch) {
			return NullBranchPoint.INSTANCE;
		}
		
		final long timestamp = sourceBranch.getBase().getTimeStamp(); 
		
		return createBranchPoint(timestamp - 1L);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.delta.IBranchPointCalculationStrategy#getTargetBranchPath()
	 */
	@Override
	public IBranchPoint getTargetBranchPoint() {
		return BranchPointUtils.create(getConnection(), getBranchPath());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Utils.toString(this);
	}

}