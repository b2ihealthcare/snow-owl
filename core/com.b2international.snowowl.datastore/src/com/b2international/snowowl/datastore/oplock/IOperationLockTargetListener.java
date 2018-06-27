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

/**
 * Represents a lock target listener that receives notifications of an appearing or disappearing {@link IOperationLockTarget} in
 * an {@link AbstractOperationLockManager} instance.
 * 
 */
public interface IOperationLockTargetListener<C> {

	/**
	 * Called when a lock for a target is granted in an {@link AbstractOperationLockManager}.
	 * 
	 * @param target the added lock target (may not be {@code null})
	 * @param context the lock context (may not be {@code null})
	 */
	void targetAcquired(IOperationLockTarget target, C context);

	/**
	 * Called when a lock for a target is released in an {@link AbstractOperationLockManager}.
	 * 
	 * @param target the removed lock target (may not be {@code null})
	 * @param context the lock context (may not be {@code null})
	 */
	void targetReleased(IOperationLockTarget target, C context);
}