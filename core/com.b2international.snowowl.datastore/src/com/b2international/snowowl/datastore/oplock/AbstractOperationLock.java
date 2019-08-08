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

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Date;
import java.util.Deque;

import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContext;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockTarget;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

/**
 * An abstract lock implementation which supports basic methods of {@link IOperationLock}.
 * 
 * @param C the lock context type
 * @see IOperationLock
 */
public abstract class AbstractOperationLock implements IOperationLock {

	private static final String CONTEXT_NOT_IN_STACK_MESSAGE = "Context is not registered as a lock owner.";
	
	private final int id;
	private final Date creationDate = new Date();
	private final Deque<DatastoreLockContext> contextStack = new ArrayDeque<DatastoreLockContext>(); 
	private final DatastoreLockTarget target;

	/**
	 * Creates a new abstract lock instance.
	 * 
	 * @param id the lock identifier
	 * @param target the lock target (may not be {@code null})
	 */
	protected AbstractOperationLock(final int id, final DatastoreLockTarget target) {
		Preconditions.checkNotNull(target, "Lock target may not be null.");
		
		this.id = id;
		this.target = target; 
	}

	private void pushContext(DatastoreLockContext context) {
		contextStack.push(context);
	}

	private void removeContext(DatastoreLockContext otherContext) {
		if (!contextStack.remove(otherContext)) {
			throw new OperationLockException(CONTEXT_NOT_IN_STACK_MESSAGE);
		}
	}

	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public Date getCreationDate() {
		return creationDate;
	}
	
	@Override
	public DatastoreLockContext getContext() {
		return contextStack.peek();
	}
	
	@Override
	public Collection<DatastoreLockContext> getAllContexts() {
		return ImmutableList.copyOf(contextStack);
	}

	@Override
	public DatastoreLockTarget getTarget() {
		return target;
	}

	@Override
	public boolean targetConflicts(final DatastoreLockTarget otherTarget) {
		return target.conflicts(otherTarget);
	}
	
	@Override
	public boolean targetEquals(final DatastoreLockTarget otherTarget) {
		return target.equals(otherTarget);
	}
	
	@Override
	public final void acquire(final DatastoreLockContext otherContext) throws OperationLockException {
		pushContext(otherContext);
		doAcquire();
	}

	@Override
	public final void release(final DatastoreLockContext otherContext) throws OperationLockException {
		doRelease();
		removeContext(otherContext);
	}

	protected void doAcquire() throws OperationLockException { }
	
	protected void doRelease() throws OperationLockException { }
}