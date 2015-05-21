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
package com.b2international.snowowl.datastore.cdo;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Comparator;

import javax.annotation.Nullable;

import org.eclipse.emf.cdo.common.branch.CDOBranch;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IBranchPoint;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.google.common.base.Predicate;
import com.google.common.primitives.Longs;

/**
 * Interface for a CDO-specific, client-side branch action manager.
 * <p>
 * Branch actions are performed in bulk, after all affected locks are acquired. Any caught exception will cause the method to
 * return, and the corresponding locks will be released.
 * 
 * @see BranchNamePredicate
 * @see BranchPathPredicate
 */
public interface ICDOBranchActionManager {

	/**
	 * Comparator for comparing branches based on their base timestamps.
	 */
	Comparator<CDOBranch> BRANCH_BASE_COMPARATOR = new Comparator<CDOBranch>() {
		@Override public int compare(final CDOBranch o1, final CDOBranch o2) {
			return Longs.compare(o1.getBase().getTimeStamp(), o2.getBase().getTimeStamp());
		}
	};

	/**
	 * Predicate for comparing branches by their names.
	 * 
	 * @see ICDOBranchActionManager
	 */
	public static class BranchNamePredicate implements Predicate<CDOBranch> {
		
		private final String branchName;

		public BranchNamePredicate(final String branchName) {
			checkNotNull(branchName, "Branch name cannot be null.");
			this.branchName = branchName;
		}

		@Override public boolean apply(final CDOBranch branch) {
			checkNotNull(branch, "CDO branch cannot be null.");
			checkNotNull(branch.getName(), "Branch name cannot be null for branch: %s", branch);
			return branchName.equals(branch.getName());
		}
	}

	/**
	 * Predicate for comparing branches by their paths.
	 * 
	 * @see ICDOBranchActionManager
	 */
	public static class BranchPathPredicate implements Predicate<CDOBranch> {
		
		private final String branchPath;

		public BranchPathPredicate(final IBranchPath branchPath) {
			checkNotNull(branchPath, "Branch path argument cannot be null.");
			this.branchPath = branchPath.getPath();
		}

		@Override public boolean apply(final CDOBranch branch) {
			checkNotNull(branch, "CDO branch cannot be null.");
			checkNotNull(branch.getPathName(), "Branch path cannot be null for branch: %s", branch);
			return branchPath.equals(branch.getPathName());
		}
	}

	/**
	 * Initializes infrastructure of the underlying lightweight stores for the specified set of branches.
	 * 
	 * @param branchPathMap the destination branch path map indicating the set of branches to change to.
	 * @param userId the unique ID of the user performing the branch change.
	 * @return returns with the error (if any) occurred while performing the branch switching. Returns with {@code null} if the operation
	 *         was successful.
	 */
	@Nullable Throwable prepare(final IBranchPathMap branchPathMap, final String userId);

	/**
	 * Synchronizes the specified set of branches with their corresponding parent branches; after synchronization completes,
	 * all changes that have happened on the parent branches since the last synchronization will be added to the task branches, 
	 * and all changes that have happened on the task branches will be preserved as much as possible.
	 * 
	 * @param branchPathMap the set of branch paths to synchronize.
	 * @param userId the unique ID of the user who's performing the branch synchronization.
	 * @param commitComment the commit comment for the synchronization.
	 * @return returns with the error (if any) occurred while performing the branch synchronization. Returns with {@code null} if the
	 *         operation was successful.
	 * @see CustomConflictException - occurres if the synchronization failed with conflicts
	 */
	@Nullable Throwable synchronize(final IBranchPathMap branchPathMap, final String userId, final String commitComment);

	/**
	 * Checks whether any of the branches specified by their paths should be synchronized with the corresponding parent branch.
	 * <p>
	 * This method gets the base of the current branch and checks if there any changes on the parent branch with the current branch's
	 * timestamp and the HEAD of the parent branch.
	 * </p>
	 * 
	 * @param branchPathMap the set of branch paths to check, keyed by repository UUID.
	 * @return returns {@code true} if all branches are synchronized with their corresponding parent branch. Otherwise returns with
	 *         {@code false}.
	 */
	boolean isSynchronized(final IBranchPathMap branchPathMap);

	/**
	 * Returns with the last commit time made on the given branch.
	 * <br>This method will return with {@link Long#MIN_VALUE} if no modification has been
	 * made on the given branch, hence no commit time is associated with it.  
	 * @param repositoryUuid the unique ID of the repository to check for last modification. 
	 * @param branchPath the branch path.
	 * @return the timestamp for the last modification made on the given branch or {@link Long#MIN_VALUE} if no
	 * modifications have been made on the given branch yet.
	 */
	long getLastCommitTime(final String repositoryUuid, final IBranchPath branchPath);
	
	/**
	 * Promotes changes made on any of the specified branches to their corresponding parent branch, typically at the end of an authoring
	 * task.
	 * 
	 * @param branchPathMap the set of branch paths to promote, keyed by repository UUID.
	 * @param userId the unique ID of the user performing the promotion.
	 * @param commitComment the commit comment for the promotion.
	 * @return returns with the error (if any) occurred while promoting changes to the parent branch. Returns with {@code null} if the
	 *         operation was successful.
	 */
	@Nullable Throwable promote(final IBranchPathMap branchPathMap, final String userId, final String commitComment);

	/**
	 * Reverts all modifications on the specified branch made between the HEAD of the given branch and the corresponding branch point
	 * timestamp.
	 * <p>
	 * Essentially this creates and inverse change set between the HEAD and a branch point given as the target time stamp and applies the
	 * inverse change set on the HEAD.
	 * </p>
	 * 
	 * @param branchPoint the branch point to revert to the repository state.
	 * @param targetTimestamp the target timestamp.
	 * @param userId the unique ID of the user who's performing the revert operation.
	 * @return exception indicating the reason why the operation failed. May return with {@code null} if the operation was successful.
	 */
	@Nullable Throwable revert(final IBranchPoint branchPoint, final String userId);
	
	/**
	 * Reverts all changes on a given branch identified by the unique branch path in all specified repositories. 
	 * @param branchPath the path of the branch to revert all changes.
	 * @param userId the user ID.
	 * @param commitComment commit comment for the revert. Optional, can be {@code null}.
	 * @param repositoryUuid the unique repository ID.
	 * @return with {@code null} if the revert was successful, otherwise returns with a {@link Throwable} indicating the
	 * cause of the failure.
	 */
	@Nullable Throwable revertAllChangesOnBranch(final IBranchPath branchPath, final String userId, 
			@Nullable final String commitComment, 
			final String repositoryUuid);

	/**
	 * Reverts all changes on a given branch identified by the unique branch path in all specified repositories. 
	 * @param branchPath the path of the branch to revert all changes.
	 * @param userId the user ID.
	 * @param commitComment commit comment for the revert. Optional, can be {@code null}.
	 * @param repositoryUuid the unique repository ID.
	 * @param parentContextDescription the parent lock context description
	 * @return with {@code null} if the revert was successful, otherwise returns with a {@link Throwable} indicating the
	 * cause of the failure.
	 */
	@Nullable Throwable revertAllChangesOnBranchWithContext(final IBranchPath branchPath, final String userId, 
			@Nullable final String commitComment, 
			final String repositoryUuid,
			final String parentContextDescription);
}
