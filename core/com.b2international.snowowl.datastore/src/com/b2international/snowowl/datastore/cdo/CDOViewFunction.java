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
package com.b2international.snowowl.datastore.cdo;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IBranchPoint;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.BranchPointUtils;
import com.google.common.base.Preconditions;

/**
 * Function for performing an operation on an underlying {@link CDOView view}.
 * <p>It's advantage is that the client should not take care about releasing the underlying {@link CDOView resource}.
 *
 */
public abstract class CDOViewFunction<T, V extends CDOView> implements CDOFunction<T> {

	private long timeStamp;
	/*default*/ CDOBranch branch;
	/*default*/ ICDOConnection connection;

	/**Creates a function that can make any arbitrary operation in a {@link CDOView view} opened on the HEAD of the given {@link CDOBranch branch}.*/
	public CDOViewFunction(final CDOBranch branch) {
		this(check(getConnection(branch)), BranchPathUtils.createPath(Preconditions.checkNotNull(branch, "CDO branch argument cannot be null.")));
	}
	
	/**Creates a function that can make any operation in a {@link CDOView view} opened on the HEAD of a branch given by its unique {@link IBranchPath branch path}.*/
	public CDOViewFunction(final ICDOConnection connection, final IBranchPath branchPath) {
		this(BranchPointUtils.create(connection, Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.")));
	}
	
	/**Sugar for {@link #CDOViewFunction(ICDOConnection, IBranchPath)}.<br>Creates a new view function opened on the HEAD of the current branch in a repository.*/
	public CDOViewFunction(final String repositoryUuid, final IBranchPath branchPath) {
		this(check(getConnection(repositoryUuid)), Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null."));
	}

	/**Creates a function that can make any operation in a {@link CDOView view} opened on a branch in a {@link IBranchPoint#getTimestamp() point in time}.*/
	public CDOViewFunction(final IBranchPoint branchPoint) {
		this(BranchPointUtils.convert(Preconditions.checkNotNull(branchPoint, "Branch point argument cannot be null.")));
	}
	
	/**Creates a function that can make apply an operation in a {@link CDOView view} opened on a {@link CDOBranchPoint branch point}.*/
	public CDOViewFunction(final CDOBranchPoint branchPoint) {
		Preconditions.checkNotNull(branchPoint, "Branch point argument cannot be null.");
		branch = Preconditions.checkNotNull(branchPoint.getBranch(), "Branch was null for: " + branchPoint);
		timeStamp = branchPoint.getTimeStamp();
		connection = Preconditions.checkNotNull(getConnectionManager().get(branch));
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.cdo.CDOFunction#apply()
	 */
	@Override
	public final T apply() {

		V view = null;
		
		try {
			
			view = init();
			
			return apply(view);
			
		} finally {
			
			LifecycleUtil.deactivate(view);
			
		}
		
	}

	/**
	 * Applies the function to an object of type {@code V}.
	 */
	protected abstract T apply(final V view);
	
	/**
	 * Initialize the view and returns with it.
	 * @return the initialized view.
	 */
	@SuppressWarnings("unchecked")
	protected V init() {
		return (V) (isHead(timeStamp) ? connection.createView(branch) : connection.createView(branch, timeStamp));
	}

	/**Returns with the connection manager wrapping a bunch of CDO Net4j session.*/
	protected static ICDOConnectionManager getConnectionManager() {
		return ApplicationContext.getInstance().getService(ICDOConnectionManager.class);
	}
	
	/**Checks and validates the connection argument, then returns with it.*/
	protected static ICDOConnection check(final ICDOConnection connection) {
		return Preconditions.checkNotNull(connection, "CDO connection argument cannot be null.");
	}
	
	/*returns with the CDO connection, specified as the branch loader for the given branch.*/
	protected static ICDOConnection getConnection(final CDOBranch branch) {
		return getConnectionManager().get(Preconditions.checkNotNull(branch, "CDO branch argument cannot be null."));
	}
	
	/*returns with the CDO connection, specified as the associated package.*/
	protected static ICDOConnection getConnection(final EPackage ePackage) {
		return getConnectionManager().get(Preconditions.checkNotNull(ePackage, "EPackage argument cannot be null."));
	}
	
	/*returns with the CDO connection, specified with its unique UUID.*/
	protected static ICDOConnection getConnection(final String repositoryUuid) {
		return getConnectionManager().getByUuid(Preconditions.checkNotNull(repositoryUuid, "Repository UUID argument cannot be null."));
	}

	/*returns true if the given timestamp is unspecified hence it represents the HEAD*/
	private static boolean isHead(final long timeStamp) {
		return CDOBranchPoint.UNSPECIFIED_DATE == timeStamp;
	}
	
}