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

import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.session.CDORepositoryInfo;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IBranchPoint;
import com.b2international.snowowl.datastore.BranchPointUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.google.common.base.Preconditions;


/**
 * Abstract base class of the {@link IBranchPointCalculationStrategy}.
 *
 */
public abstract class BranchPointCalculationStrategy implements IBranchPointCalculationStrategy {

	private static final long serialVersionUID = -6930499979846840567L;

	private final String uuid;
	private final IBranchPath branchPath;

	/**
	 * Creates a new {@link IBranchPointCalculationStrategy} instance.
	 * @param connection the connection where the branch points have to be calculated.
	 * @param branchPath branch path for the strategy.
	 */
	protected BranchPointCalculationStrategy(final ICDOConnection connection, final IBranchPath branchPath) {
		this(Preconditions.checkNotNull(connection, "CDO connection argument cannot be null.").getUuid(), branchPath);
	}

	/**
	 * Creates a new {@link IBranchPointCalculationStrategy} instance.
	 * @param connection the connection where the branch points have to be calculated.
	 * @param branchPath branch path for the strategy.
	 */
	protected BranchPointCalculationStrategy(final String uuid, final IBranchPath branchPath) {
		this.branchPath = Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		this.uuid = Preconditions.checkNotNull(uuid, "UUID of the CDO connection was null.");
	}
	
	/**Returns with the CDO connection.*/
	protected ICDOConnection getConnection() {
		return Preconditions.checkNotNull(ApplicationContext.getInstance().getService(ICDOConnectionManager.class).getByUuid(uuid));
	}
	
	/**
	 * Returns with the branch path.
	 */
	protected IBranchPath getBranchPath() {
		return branchPath;
	}
	
	/**Creates and returns with a branch point with the given time stamp.*/
	protected IBranchPoint createBranchPoint(final long timestamp) {
		
		final CDONet4jSession session = getConnection().getSession();
		final CDORepositoryInfo repositoryInfo = session.getRepositoryInfo();
		final long creationTime = repositoryInfo.getCreationTime();
		
		return BranchPointUtils.create(getConnection(), getBranchPath(), creationTime > timestamp ? creationTime : timestamp);
	}
	
}