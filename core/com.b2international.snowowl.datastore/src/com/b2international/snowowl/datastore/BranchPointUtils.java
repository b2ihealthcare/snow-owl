/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore;

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.eclipse.emf.cdo.common.branch.CDOBranchPoint.UNSPECIFIED_DATE;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IBranchPoint;
import com.b2international.snowowl.core.api.IBranchPoint.NullBranchPoint;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.google.common.base.Preconditions;

/**
 * Utility class for {@link IBranchPoint}s.
 */
public abstract class BranchPointUtils {

	/**
	 * Creates a new branch point instance with a given branch and a timestamp.
	 * @param connection the CDO connection where the branch point should be applied.
	 * @param branchPath the unique path of the branch. 
	 * @param timestamp the point in time on the branch.
	 * @return the branch point.
	 */
	public static IBranchPoint create(final ICDOConnection connection, final IBranchPath branchPath, final long timestamp) {
		return new BranchPoint(connection, branchPath, timestamp);
	}

	/**
	 * Creates a new branch point instance representing the HEAD of the branch.
	 * @param connection connection to the remote repository.
	 * @param branchPath the unique path of the branch. 
	 * @return the branch point.
	 */
	public static IBranchPoint create(final ICDOConnection connection, final IBranchPath branchPath) {
		final CDOBranch branch = checkNotNull(connection, "connection").getBranch(checkNotNull(branchPath, "branchPath"));
		
		//branch does not exist yet
		return null == branch 
			? NullBranchPoint.INSTANCE 
			: create(connection, branchPath, UNSPECIFIED_DATE);
		
	}

	/**
	 * Counter part of the {@link #create(CDOBranchPoint)}.
	 * @param branchPoint the branch point.
	 * @return the CDO specific branch point.
	 */
	public static CDOBranchPoint convert(final IBranchPoint branchPoint) {
		Preconditions.checkNotNull(branchPoint, "Branch point argument cannot be null.");
		
		final ICDOConnection connection = getConnectionManager().getByUuid(branchPoint.getUuid());
		final CDOBranch branch = connection.getBranch(branchPoint.getBranchPath());
		
		Preconditions.checkNotNull(branch, "Branch does not exist. Path: '" + branchPoint.getBranchPath() + "'.");
		
		return branch.getPoint(branchPoint.getTimestamp());
		
	}
	
	/*returns with the CDO connection manager*/
	private static ICDOConnectionManager getConnectionManager() {
		return getServiceForClass(ICDOConnectionManager.class);
	}
	
	private BranchPointUtils() { /*suppress instantiation*/ }
	
}