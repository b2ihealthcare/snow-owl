/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.oplock;

import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContext;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockTarget;

/**
 * Represents an exclusive lock manager that allows contexts of arbitrary type to lock potentially nested
 * {@link IOperationLockTarget}s. The first context to acquire a lock for a certain target may add multiple following locks to
 * the same or any {@link IOperationLockTarget#conflicts(IOperationLockTarget) included} target, while all lock requests will be refused
 * if another context has already acquired the lock for a target overlapping with the requested target.
 */
public interface IOperationLockManager {

	/**
	 * Special timeout value indicating that the specified lock attempt should never throw a {@link OperationLockException}
	 * because of an exceeded time limit.
	 */
	long NO_TIMEOUT = -1L;

	/**
	 * Special timeout value indicating that the specified lock attempt should immediately throw {@link OperationLockException}
	 * if the requested locks can not be acquired.
	 */
	long IMMEDIATE = 0L;
	
	/**
	 * Locks one or more {@link IOperationLockTarget targets} for the specified lock context.
	 * 
	 * @param context the lock context (may not be {@code null})
	 * @param timeoutMillis the maximum allowed time in milliseconds in which this call may block (must be {@link #NO_TIMEOUT}, zero or positive)
	 * @param firstTarget the first (or only) target to lock (may not be {@code null})
	 * @param restTargets subsequent targets to lock (may not be {@code null}; individual elements may not be {@code null})
	 * @throws OperationLockException when one or more locks for the given targets can not be acquired for some reason
	 * @throws InterruptedException if waiting for the requested locks to be acquired is interrupted 
	 */
	void lock(DatastoreLockContext context, long timeoutMillis, DatastoreLockTarget firstTarget, DatastoreLockTarget... restTargets) throws OperationLockException, InterruptedException;

	/**
	 * Locks one or more {@link IOperationLockTarget targets} for the specified lock context.
	 * 
	 * @param context the lock context (may not be {@code null})
	 * @param timeoutMillis the maximum allowed time in milliseconds in which this call may block (must be {@link #NO_TIMEOUT}, zero or positive)
	 * @param targets the targets to lock (may not be {@code null}; can be empty)
	 * @throws OperationLockException when one or more locks for the given targets can not be acquired for some reason
	 * @throws InterruptedException if waiting for the requested locks to be acquired is interrupted
	 */
	void lock(DatastoreLockContext context, long timeoutMillis, Iterable<DatastoreLockTarget> targets) throws OperationLockException, InterruptedException;

	/**
	 * Unlocks one or more {@link IOperationLockTarget targets} with the specified lock context.
	 * 
	 * @param context the lock context (may not be {@code null})
	 * @param firstTarget the first (or only) target to unlock (may not be {@code null})
	 * @param restTargets subsequent targets to unlock (may not be {@code null}; individual elements may not be {@code null})
	 * @throws OperationLockException when one or more locks for the given targets could not be released for some reason
	 */
	void unlock(DatastoreLockContext context, DatastoreLockTarget firstTarget, DatastoreLockTarget... restTargets) throws OperationLockException;

	/**
	 * Unlocks one or more {@link IOperationLockTarget targets} with the specified lock context.
	 * 
	 * @param context the lock context (may not be {@code null})
	 * @param targets the targets to unlock (may not be {@code null}; can be empty)
	 * @throws OperationLockException when one or more locks for the given targets could not be released for some reason
	 */
	void unlock(DatastoreLockContext context, Iterable<DatastoreLockTarget> targets) throws OperationLockException;
}