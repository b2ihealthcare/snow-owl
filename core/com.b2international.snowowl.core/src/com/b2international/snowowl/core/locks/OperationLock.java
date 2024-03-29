/*
 * Copyright 2011-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.locks;

import java.text.MessageFormat;

/**
 * Represents a reentrant lock that can be acquired and released in a balanced fashion multiple times by the same context.
 *
 * @param C the lock context type
 * @see IOperationLock
 */
public class OperationLock extends AbstractOperationLock {

	private static final String LOCK_NOT_HELD_MESSAGE = "Can''t release lock for {0} because it is not held.";

	/**
	 * Creates a new reentrant lock instance.
	 * 
	 * @param id the lock identifier
	 * @param target the lock target (may not be {@code null})
	 */
	public OperationLock(final int id, final Lockable target) {
		super(id, target);
	}

	@Override
	public int getLevel() {
		return getAllContexts().size();
	}

	@Override
	public boolean isLocked() {
		return getLevel() > 0;
	}

	@Override
	protected void doRelease() {
		if (!isLocked()) {
			throw new IllegalArgumentException(MessageFormat.format(LOCK_NOT_HELD_MESSAGE, getTarget()));
		}
	}
}