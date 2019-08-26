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

import java.lang.reflect.InvocationTargetException;

import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContext;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockTarget;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * Contains utility methods for executing a {@link Runnable} while holding one or more operation locks.
 *
 */
public class OperationLockRunner {

	private final IOperationLockManager manager;

	/**
	 * Creates a new instance with the specified operation lock manager.
	 * 
	 * @param manager the lock manager to use (may not be {@code null})
	 */
	public static OperationLockRunner with(final IOperationLockManager manager) {
		return new OperationLockRunner(manager);
	}
	
	private OperationLockRunner(final IOperationLockManager manager) {
		this.manager = Preconditions.checkNotNull(manager, "Lock manager reference may not be null.");
	}
	
	/**
	 * Ensures that the specified locks are acquired for the given context while a {@link Runnable} is executing; cleans
	 * up acquired locks if the runnable finishes or an exception is thrown from it.
	 * 
	 * @param runnable the runnable to call with the locks held (may not be {@code null})
	 * @param context the lock context (may not be {@code null})
	 * @param timeoutMillis the maximum allowed time in milliseconds in which this call may block (must be {@link #NO_TIMEOUT}, zero or positive)
	 * @param firstTarget the first (or only) target to hold while running (may not be {@code null})
	 * @param restTargets subsequent targets to hold while running (may not be {@code null}; individual elements may not be {@code null})
	 * @throws OperationLockException when one or more locks for the given targets could not be acquired or released for some reason
	 * @throws InterruptedException if waiting for the requested locks to be acquired is interrupted
	 * @throws InvocationTargetException if the specified runnable throws an exception
	 */
	public void run(final Runnable runnable, final DatastoreLockContext context, final long timeoutMillis, final DatastoreLockTarget firstTarget, final DatastoreLockTarget... restTargets) 
			throws OperationLockException, InterruptedException, InvocationTargetException {
		
		run(runnable, context, timeoutMillis, Lists.asList(firstTarget, restTargets));
	}
	
	/**
	 * Ensures that the specified locks are acquired for the given context while a {@link Runnable} is executing; cleans
	 * up acquired locks if the runnable finishes or an exception is thrown from it.
	 * 
	 * @param runnable the runnable to call with the locks held (may not be {@code null})
	 * @param context the lock context (may not be {@code null})
	 * @param timeoutMillis the maximum allowed time in milliseconds in which this call may block (must be {@link #NO_TIMEOUT}, zero or positive)
	 * @param targets the targets to hold while running (may not be {@code null}; can be empty)
	 * @throws OperationLockException when one or more locks for the given targets could not be acquired or released for some reason
	 * @throws InterruptedException if waiting for the requested locks to be acquired is interrupted
	 * @throws InvocationTargetException if the specified runnable throws an exception
	 */
	public void run(final Runnable runnable, final DatastoreLockContext context, final long timeoutMillis, final Iterable<DatastoreLockTarget> targets) 
			throws OperationLockException, InterruptedException, InvocationTargetException {

		Throwable caught = null;
		manager.lock(context, timeoutMillis, targets);
		
		try {
			runnable.run();
		} catch (final Throwable t) {
			caught = t;
			throw new InvocationTargetException(t);
		} finally {
			try {
				manager.unlock(context, targets);
			} catch (final OperationLockException e) {
				if (null != caught) {
					caught.addSuppressed(e);
				} else {
					throw e;
				}
			}
		}
	}
}