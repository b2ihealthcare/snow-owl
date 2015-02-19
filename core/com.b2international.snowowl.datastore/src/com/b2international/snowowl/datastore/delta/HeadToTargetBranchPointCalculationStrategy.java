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

import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;

import com.b2international.snowowl.core.api.IBranchPoint;
import com.b2international.snowowl.datastore.BranchPointUtils;
import com.google.common.base.Preconditions;

/**
 * Strategy providing the HEAD of the specified branch as the {@link #getSourceBranchPoint() source} branch point
 * and any arbitrary {@link IBranchPoint#getTimestamp() point in time} as the {@link #getTargetBranchPoint() target} branch point.
 * <p>This strategy is mainly used for generating inverse {@link CDOChangeSetData change set data} for a revert operation.
 *
 */
public class HeadToTargetBranchPointCalculationStrategy extends BranchPointCalculationStrategy {

	private static final long serialVersionUID = 7876006919478262269L;
	private final IBranchPoint targetBranchPoint;

	public HeadToTargetBranchPointCalculationStrategy(final IBranchPoint targetBranchPoint) {
		super(targetBranchPoint.getUuid(), targetBranchPoint.getBranchPath());
		this.targetBranchPoint = Preconditions.checkNotNull(targetBranchPoint, "Target branch point argument cannot be null.");
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.delta.IBranchPointCalculationStrategy#getSourceBranchPath()
	 */
	@Override
	public IBranchPoint getSourceBranchPoint() {
		return BranchPointUtils.create(getConnection(), getBranchPath());
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.delta.IBranchPointCalculationStrategy#getTargetBranchPath()
	 */
	@Override
	public IBranchPoint getTargetBranchPoint() {
		return targetBranchPoint;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Utils.toString(this);
	}
	
}