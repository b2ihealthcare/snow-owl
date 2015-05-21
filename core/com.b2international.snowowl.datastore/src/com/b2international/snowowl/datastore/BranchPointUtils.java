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
package com.b2international.snowowl.datastore;

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.datastore.BranchPathUtils.createActivePath;
import static com.b2international.snowowl.datastore.BranchPathUtils.createPath;
import static com.b2international.snowowl.datastore.BranchPathUtils.isMain;
import static com.b2international.snowowl.datastore.cdo.CDOUtils.check;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.eclipse.emf.cdo.common.branch.CDOBranchPoint.UNSPECIFIED_DATE;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.view.CDOView;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IBranchPoint;
import com.b2international.snowowl.core.api.IBranchPoint.NullBranchPoint;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.tasks.TaskManager;
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

	/**Sugar for {@link #create(ICDOConnection, IBranchPath, long)}.*/
	public static IBranchPoint create(final String repositoryUuid, final IBranchPath branchPath, final long timestamp) {
		return new BranchPoint(getConnectionManager().getByUuid(repositoryUuid), branchPath, timestamp);
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

	/**Sugar for {@link #create(ICDOConnection, IBranchPath)}.*/
	public static IBranchPoint create(final String repositoryUuid, final IBranchPath branchPath) {
		return create(
				getConnectionManager().getByUuid(checkNotNull(repositoryUuid, "Repository UUID argument cannot be null.")), 
				checkNotNull(branchPath, "branchPath"));
	}
	
	/**
	 * Creates a new {@link IBranchPoint branch point} representing the base of the branch.
	 * @param repositoryUuid the repository UUID.
	 * @param branchPath the branch path identifying the branch.
	 * @return the branch point representing the base of the branch.
	 */
	public static IBranchPoint createBase(final String repositoryUuid, final IBranchPath branchPath) {
		checkArgument(!isMain(checkNotNull(branchPath, "branchPath")), "Base branch path cannot be the MAIN path.");
		final CDOBranchPoint branchPoint = convert(create(
				getConnectionManager().getByUuid(checkNotNull(repositoryUuid, "Repository UUID argument cannot be null.")), 
				branchPath));
		
		final long baseTimestamp = branchPoint.getBranch().getBase().getTimeStamp();
		return create(repositoryUuid, branchPath, baseTimestamp);
	}
	
	/**
	 * Creates a branch point which is identical to the {@link CDOView object's view}.
	 * @param object the CDO object.
	 * @return the branch point where the object lives.
	 */
	public static IBranchPoint create(final CDOObject object) {
		return create(check(object).cdoView());
	}
	
	/**
	 * Creates a new branch point instance which is identical the {@link CDOView#getBranch() view's branch}
	 * and {@link CDOView#getTimeStamp() view's point in time}.
	 * @param view the CDO view.
	 * @return the branch point.
	 */
	public static IBranchPoint create(final CDOView view) {
		return create(getConnectionManager().get(view), createPath(check(view)), check(view).getTimeStamp());
	}
	
	/**
	 * Creates a new branch point instance which is identical the {@link CDORevision#getBranch() revision's branch}
	 * and {@link CDORevision#getTimeStamp() revisions's point in time}.
	 * @param revision the CDO revision.
	 * @return the branch point.
	 */
	public static IBranchPoint create(final CDORevision revision) {
		checkNotNull(revision, "CDO revision argument cannot be null.");
		return create(getConnectionManager().get(revision), createPath(revision.getBranch()), revision.getTimeStamp());
	}
	
	/**
	 * Creates a new branch point instance based on the given CDO specific {@link CDOBranchPoint branch point}.
	 * @param branchPoint the CDO branch point.
	 * @return the branch point.
	 */
	public static IBranchPoint create(final CDOBranchPoint branchPoint) {
		checkNotNull(branchPoint, "branchPoint");
		return create(getConnectionManager().get(branchPoint.getBranch()), createPath(branchPoint.getBranch()), branchPoint.getTimeStamp());
	}
	
	/**
	 * Creates and returns a branch point representing the HEAD of the currently active branch.
	 * <p>Currently active branch is specified via {@link TaskManager#getActiveBranch()}.
	 * @param connection connection to the remote repository.
	 * @return the branch point representing the HEAD of the currently active branch.
	 */
	public static IBranchPoint createActiveHead(final ICDOConnection connection) {
		return create(connection, createActivePath(connection.getUuid()));
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