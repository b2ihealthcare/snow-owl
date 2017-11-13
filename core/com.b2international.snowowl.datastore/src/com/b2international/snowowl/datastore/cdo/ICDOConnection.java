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
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IBranchPoint;

/**
 * Service interface for wrapping a {@link CDONet4jSession session}.
 */
public interface ICDOConnection extends ICDOManagedItem<ICDOConnection> {

	/**
	 * Creates a CDO view on the MAIN branch with unspecified HEAD branch point.
	 * @return the CDO view on the HEAD of the MAIN.
	 */
	CDOView createView();

	/**
	 * Creates a CDO view on the specified {@link IBranchPoint#getBranchPath() branch } with a given {@link IBranchPoint#getTimestamp() timestamp}. 
	 * @param branchPoint the branch point.
	 * @return the CDO view.
	 */
	CDOView createView(final IBranchPoint branchPoint);

	/**
	 * Creates a CDO view on onto the HEAD of a branch given with its unique {@link IBranchPath branch path}. 
	 * @param branchPath the branch path.
	 * @return the CDO view.
	 */
	CDOView createView(final IBranchPath branchPath);
	
	/**
	 * Creates a non-audit CDO view on a branch with unspecified HEAD branch point. 
	 * @param branch the CDO branch. 
	 * @return the non-audit CDO view instance. 
	 */
	CDOView createView(final CDOBranch branch);
	
	/**
	 * Creates a non-audit CDO view on branch with a given branch point.
	 * @param branch the CDO branch.
	 * @param timeStamp the timestamp for the branch point.
	 * @return the non-audit CDO view instance.
	 */
	CDOView createView(final CDOBranch branch, final long timestamp);
	
	/**
	 * Creates a non-audit CDO view on a specified branch with the given branch point. If clients should configure <i>shouldInvalidate</i>
	 * as {@code false} if CDO invalidation events are to be ignored.
	 * @param branch the CDO branch.
	 * @param timeStamp the timestamp for the branch point.
	 * @param shouldInvalidate {@code false} if invalidation events to be ignored. Otherwise {@code false}.
	 * @return the non-audit CDO view.
	 */
	CDOView createView(final CDOBranch branch, final long timestamp, final boolean shouldInvalidate);
	
	/**
	 * Creates a audit CDO view for the specified branch. The HEAD of the transaction will be 
	 * {@link org.eclipse.emf.cdo.common.branch.CDOBranchPoint#UNSPECIFIED_DATE unspecified}.
	 * <p>
	 * <b>NOTE:&nbsp;</b>the CDO revision cache will use weak references.
	 * @param branch the branch where the transaction has to be opened.
	 * @return the clean audit CDO view instance.
	 */
	CDOTransaction createTransaction(final CDOBranch branch);

	/**
	 * Creates a transaction to the HEAD of the branch given by a {@link IBranchPath branch path}.
	 * @param branchPath the branch path uniquely identifying the branch where the transaction has to be opened.
	 * @return the clean CDO transaction.
	 */
	CDOTransaction createTransaction(final IBranchPath branchPath);
	
	/**
	 * Returns with the Net4j session for the current connection instance.
	 * @return the session.
	 */
	CDONet4jSession getSession();
	
	/**
	 * Returns with the MAIN branch.
	 * @return the MAIN branch with the HEAD.
	 */
	CDOBranch getMainBranch();
	
	/**
	 * Returns the most recently created branch with the specified its branch path. The
	 * main branch is returned for the fixed branch name {@link CDOBranch#MAIN_BRANCH_NAME}.
	 * @param branchPath the path of the branch to retrieve.
	 * @return the most recent branch with the specified path, or {@code null} if no such branch could be found.
	 */
	CDOBranch getBranch(final IBranchPath branchPath);
	
	/**
	 * Returns with {@link CDOBranch branch} which is associated with the branch path argument.
	 * <p>Multiple CDO branch with identical name but different ID could be associated with
	 * a single {@link IBranchPath branch path} (due to task context synchronization). This will return
	 * with the very first (oldest) branch. 
	 * @param branchPath the path of the branch to retrieve.
	 * @return the oldest branch associated with the branch path argument. 
	 */
	CDOBranch getOldestBranch(final IBranchPath branchPath);
	
	/**
	 * Returns the session configuration from the session backing this connection.
	 * @return session configuration
	 */
	CDONet4jSessionConfiguration getSessionConfiguration();
	
}