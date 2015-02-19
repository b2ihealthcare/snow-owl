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
import com.b2international.snowowl.datastore.cdo.ICDOConnection;

/**
 * Strategy for providing a source and target {@link IBranchPoint branch point}s. Both branch points
 * are given with a precise timestamp.
 *
 */
public class SourceToTargetBranchPointCalculationStrategy extends BranchPointCalculationStrategy {

	private static final long serialVersionUID = -4125955871694808958L;

	private final long sourceTimestamp;
	private final long targetTimestamp;

	public SourceToTargetBranchPointCalculationStrategy(final ICDOConnection connection, final IBranchPath branchPath, final long sourceTimestamp, final long targetTimestamp) {
		super(connection, branchPath);
		this.sourceTimestamp = sourceTimestamp;
		this.targetTimestamp = targetTimestamp;
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.delta.IBranchPointCalculationStrategy#getSourceBranchPoint()
	 */
	@Override
	public IBranchPoint getSourceBranchPoint() {
		return createBranchPoint(sourceTimestamp - 1L);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.delta.IBranchPointCalculationStrategy#getTargetBranchPoint()
	 */
	@Override
	public IBranchPoint getTargetBranchPoint() {
		return createBranchPoint(targetTimestamp);
	}

}