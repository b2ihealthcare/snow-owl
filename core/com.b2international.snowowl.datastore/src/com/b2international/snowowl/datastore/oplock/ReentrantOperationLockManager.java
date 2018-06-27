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
package com.b2international.snowowl.datastore.oplock;

import java.io.Serializable;

/**
 * An {@link AbstractOperationLockManager} implementation that uses {@link ReentrantOperationLock}s.
 * 
 */
public abstract class ReentrantOperationLockManager<C extends Serializable> extends AbstractOperationLockManager<C> {

	@Override
	protected ReentrantOperationLock<C> createLock(final int id, final IOperationLockTarget target) {
		return new ReentrantOperationLock<C>(id, target);
	}

	@Override
	protected OperationLockInfo<C> createLockInfo(final IOperationLock<C> existingLock) {
		final ReentrantOperationLock<C> reentrantLock = (ReentrantOperationLock<C>) existingLock;
		return new OperationLockInfo<C>(reentrantLock.getId(), reentrantLock.getLevel(), 
				reentrantLock.getCreationDate(), reentrantLock.getTarget(), reentrantLock.getContext());
	}
}