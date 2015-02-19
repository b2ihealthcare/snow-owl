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
 * Strategy providing the following branch points for a given {@link IBranchPath branch path}:
 * <p>
 * <ul>
 * <li>{@link #getSourceBranchPoint() <em>Source</em>}: the HEAD of the branch.</li>
 * <li>{@link #getTargetBranchPoint() <em>Target</em>}: the base of the branch.</li>
 * </ul>
 * </p>
 */
public class HeadToBaseBranchPointCalculationStrategy extends BranchPointCalculationStrategy {

	private static final long serialVersionUID = 2219151117523987699L;
	
	public HeadToBaseBranchPointCalculationStrategy(final ICDOConnection connection, final IBranchPath branchPath) {
		super(connection, branchPath);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.delta.IBranchPointCalculationStrategy#getSourceBranchPoint()
	 */
	@Override
	public IBranchPoint getSourceBranchPoint() {
		return BranchPointUtils.create(getConnection(), getBranchPath());
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.delta.IBranchPointCalculationStrategy#getTargetBranchPoint()
	 */
	@Override
	public IBranchPoint getTargetBranchPoint() {
		
		final CDOBranch targetBranch = getConnection().getBranch(getBranchPath());
		
		//branch does not exist yet.
		if (null == targetBranch) {
			return NullBranchPoint.INSTANCE;
		}
		
		final long timestamp = targetBranch.getBase().getTimeStamp(); 
		
		return createBranchPoint(timestamp - 1L);
	}

}