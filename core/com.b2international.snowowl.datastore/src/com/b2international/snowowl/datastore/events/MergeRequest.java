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
package com.b2international.snowowl.datastore.events;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.Branch.BranchState;
import com.b2international.snowowl.core.branch.BranchManager;
import com.b2international.snowowl.core.branch.BranchMergeException;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.BaseRequest;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.ConflictException;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.core.users.SpecialUserStore;
import com.b2international.snowowl.datastore.oplock.IOperationLockTarget;
import com.b2international.snowowl.datastore.oplock.OperationLockException;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContext;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreOperationLockException;
import com.b2international.snowowl.datastore.oplock.impl.IDatastoreOperationLockManager;
import com.b2international.snowowl.datastore.oplock.impl.SingleRepositoryAndBranchLockTarget;
import com.b2international.snowowl.datastore.review.Review;
import com.b2international.snowowl.datastore.review.ReviewManager;
import com.google.common.base.Strings;

/**
 * @since 4.1
 */
public final class MergeRequest extends BaseRequest<RepositoryContext, Branch> {

	private static class LockWrapper {

		private final String repositoryId;
		private final IDatastoreOperationLockManager lockManager;
		private final DatastoreLockContext lockContext;
		private final Map<String, IOperationLockTarget> lockTargets;
		
		private LockWrapper(RepositoryContext context, Branch source, Branch target) {
			repositoryId = context.id();
			lockManager = context.service(IDatastoreOperationLockManager.class);
			
			// FIXME: Using "System" user and "synchronize" description until a more suitable pair can be specified here
			lockContext = new DatastoreLockContext(SpecialUserStore.SYSTEM_USER_NAME, DatastoreLockContextDescriptions.SYNCHRONIZE);
		
			lockTargets = newHashMap();
			lockTargets.put(source.path(), new SingleRepositoryAndBranchLockTarget(repositoryId, source.branchPath()));
			lockTargets.put(target.path(), new SingleRepositoryAndBranchLockTarget(repositoryId, target.branchPath()));
		}

		public void lock() throws OperationLockException, InterruptedException {
			lockManager.lock(lockContext, IDatastoreOperationLockManager.IMMEDIATE, lockTargets.values());			
		}
		
		public void unlock(String path) {
			if (lockTargets.containsKey(path)) {
				lockManager.unlock(lockContext, lockTargets.get(path));
				lockTargets.remove(path); // Not reached if an exception is thrown above
			}
		}

		public void unlockAll() {
			if (!lockTargets.isEmpty()) {
				lockManager.unlock(lockContext, lockTargets.values());
				lockTargets.clear(); // Not reached if an exception is thrown above
			}
		}
	}
	
	private enum Type {
		
		/**
		 * Merge source into target (only if target is the most recent version of source's parent).
		 */
		MERGE {
			@Override
			protected Branch executeLocked(Branch source, Branch target, LockWrapper lockWrapper, String commitMessage) {
				try {
					return target.merge(source, commitMessage);
				} catch (BranchMergeException e) {
					throw new ConflictException("Cannot merge source '%s' into target '%s'.", source.path(), target.path(), e);
				}
			}
		}, 
		
		/**
		 * Rebases {@code target} on {@code source}.
		 * <p>
		 * The branch represented by {@code target} is not modified; instead, a new branch representing the rebased {@code target} will be created on
		 * top of {@code source}, preserving {@code target}'s path. This ensures that any changes that were made to {@code source} in the meantime
		 * will be visible on the rebased target branch.
		 * <p>
		 * Commits available on the previous instance of {@code target}, if present, will also be visible on the resulting {@link Branch} after a
		 * successful rebase.
		 */
		REBASE {
			@Override
			protected Branch executeLocked(Branch source, Branch target, LockWrapper lockWrapper, String commitMessage) {
				try {
					final BranchState targetState = target.state(source);

					if (targetState == BranchState.BEHIND || targetState == BranchState.DIVERGED || targetState == BranchState.STALE) {
						checkParentConflicts(source, target, commitMessage);
						final Branch reopenedTarget = target.reopen();
						releaseLockOnSource(source, lockWrapper);
						return applyChangesOnReopenedTarget(reopenedTarget, target, commitMessage);
					} else {
						return target;
					}

				} catch (BranchMergeException e) {
					throw new ConflictException("Cannot rebase target '%s' with source '%s'.", target.path(), source.path(), e);
				}
			}

			private void checkParentConflicts(Branch source, Branch target, String commitMessage) {
				source.applyChangeSet(target, true, commitMessage);
			}

			private void releaseLockOnSource(Branch source, LockWrapper lockWrapper) {
				try {
					lockWrapper.unlock(source.path());
				} catch (OperationLockException e) {
					LOG.warn("Failed to unlock source branch in MergeRequest; continuing.", e);
				}
			}

			private Branch applyChangesOnReopenedTarget(final Branch reopenedTarget, Branch target, String commitMessage) {
				if (target.headTimestamp() > target.baseTimestamp()) {
					return reopenedTarget.applyChangeSet(target, false, commitMessage); // Implicit notification (reopen & commit)
				} else {
					return reopenedTarget.notifyChanged(); // Explicit notification (reopen)
				}
			}
		};

		protected abstract Branch executeLocked(Branch source, Branch target, LockWrapper wrapper, String commitMessage);
	}

	private static final Logger LOG = LoggerFactory.getLogger(MergeRequest.class);
	
	private final String sourcePath;
	private final String targetPath;
	private final String commitMessage;
	private final String reviewId;

	private static String defaultMessage(final String sourcePath, final String targetPath) {
		return String.format("Merge branch '%s' into '%s'", sourcePath, targetPath);
	}

	public MergeRequest(final String sourcePath, final String targetPath, final String commitMessage, String reviewId) {
		this.sourcePath = sourcePath;
		this.targetPath = targetPath;
		this.commitMessage = Strings.isNullOrEmpty(commitMessage) ? defaultMessage(sourcePath, targetPath) : commitMessage;
		this.reviewId = reviewId;
	}

	@Override
	public Branch execute(RepositoryContext context) {
		try {
			final BranchManager branchManager = context.service(BranchManager.class);
			final ReviewManager reviewManager = context.service(ReviewManager.class);
			
			final Branch source = branchManager.getBranch(sourcePath);
			final Branch target = branchManager.getBranch(targetPath);
			
			if (reviewId != null) {
				Review review = reviewManager.getReview(reviewId);
				com.b2international.snowowl.datastore.review.BranchState sourceState = review.source();
				com.b2international.snowowl.datastore.review.BranchState targetState = review.target();
				
				if (!sourceState.matches(source)) {
					throw new ConflictException("Source branch '%s' did not match with stored state on review identifier '%s'.", source.path(), reviewId);
				}
				
				if (!targetState.matches(target)) {
					throw new ConflictException("Target branch '%s' did not match with stored state on review identifier '%s'.", target.path(), reviewId);
				}
			}
			
			final Type type;
			
			if (source.parent().equals(target)) {
				type = Type.MERGE;
			} else if (target.parent().equals(source)) {
				type = Type.REBASE;
			} else {
				throw new BadRequestException("Cannot merge source '%s' into target '%s', because there is no relation between them.", source.path(), target.path());
			}
			
			final LockWrapper lockWrapper = new LockWrapper(context, source, target);
			
			try {
				lockWrapper.lock();
			} catch (DatastoreOperationLockException e) {
				throw new ConflictException("Cannot merge source '%s' into target '%s'. %s", source.path(), target.path(), e.getMessage());
			} catch (InterruptedException e) {
				throw new ConflictException("Cannot merge source '%s' into target '%s', the lock obtaining process was interrupted.", source.path(), target.path());
			}

			try {
				return type.executeLocked(source, target, lockWrapper, commitMessage);
			} finally {
				try {
					lockWrapper.unlockAll();
				} catch (OperationLockException e) {
					LOG.error("Failed to unlock locked targets in MergeRequest.", e);
				}
			}
			
		} catch (NotFoundException e) {
			throw e.toBadRequestException();
		}
	}
	
	@Override
	protected Class<Branch> getReturnType() {
		return Branch.class;
	}
	
	@Override
	public String toString() {
		return String.format("{type:%s, source:%s, target:%s, commitMessage:%s, reviewId:%s}", 
				getClass().getSimpleName(),
				sourcePath,
				targetPath,
				commitMessage,
				reviewId);
	}
}
