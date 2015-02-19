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

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IBranchPoint;
import com.b2international.snowowl.datastore.BranchPointUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;

/**
 * Strategy for providing two {@link IBranchPoint branch points}. Source branch point 
 * could be any arbitrary point in time, target is the HEAD of the given branch.
 *
 */
public class SourceToHeadBranchPointCalculationStrategy extends BranchPointCalculationStrategy {

	private static final long serialVersionUID = -8842458387206711900L;
	
	private final long sourceTimestamp;
	
	public SourceToHeadBranchPointCalculationStrategy(final ICDOConnection connection, final IBranchPath branchPath, final long sourceTimestamp) {
		super(connection, branchPath);
		this.sourceTimestamp = sourceTimestamp;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.delta.IBranchPointCalculationStrategy#getSourceBranchPath()
	 */
	@Override
	public IBranchPoint getSourceBranchPoint() {
		return createBranchPoint(sourceTimestamp);
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