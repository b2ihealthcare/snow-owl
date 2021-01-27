/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Map;

import javax.annotation.Nullable;

import com.b2international.snowowl.core.internal.locks.DatastoreLockContext;
import com.b2international.snowowl.core.internal.locks.DatastoreLockContextDescriptions;
import com.b2international.snowowl.core.internal.locks.DatastoreLockTarget;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;

/**
 * Common exception superclass for reporting issues related to locking.
 */
public final class OperationLockException extends RuntimeException {

	private static final long serialVersionUID = 2L;

	private final Map<DatastoreLockTarget, DatastoreLockContext> targetMap;

	/**
	 * Creates a new instance with the specified detail message.
	 * 
	 * @param message the detail message
	 */
	public OperationLockException(final @Nullable String message) {
		this(message, Map.of());
	}

	/**
	 * Creates a new instance with the specified detail message and lock targets which could not be acquired or released.
	 * 
	 * @param message the detail message
	 * @param targetMap a map keyed by lock targets with issues; the corresponding values are the lock contexts
	 * currently holding the requested lock, or a conflicting lock (may not be {@code null})
	 */
	public OperationLockException(final @Nullable String message, final Map<DatastoreLockTarget, DatastoreLockContext> targetMap) {
		super(message);
		Preconditions.checkNotNull(targetMap, "Lock target map may not be null.");
		this.targetMap = Map.copyOf(targetMap);
	}
	
	/**
	 * Returns the current owner of the requested lock target, or {@code null} if no issue has been recorded for this target.
	 * 
	 * @param target the lock target to check (may not be {@code null})
	 * @return the current owner for the specified target, or {@code null}
	 */
	public DatastoreLockContext getContext(final DatastoreLockTarget target) {
		Preconditions.checkNotNull(target, "Lock target to check may not be null.");
		return targetMap.get(target);
	}
	
	@Override
	public String getMessage() {
		final FluentIterable<DatastoreLockContext> contexts = FluentIterable.from(targetMap.values());
		final Optional<DatastoreLockContext> rootContext = contexts.firstMatch(input -> DatastoreLockContextDescriptions.ROOT.equals(input.getParentDescription()));
		
		DatastoreLockContext context = null;
		if (rootContext.isPresent()) {
			context = rootContext.get();
		} else {
			if (contexts.first().isPresent()) {
				context = contexts.first().get();
			} else {
				return super.getMessage();
			}
		}
		
		return String.join(" ", super.getMessage(), context.getUserId(), "is", context.getDescription());
	}
}