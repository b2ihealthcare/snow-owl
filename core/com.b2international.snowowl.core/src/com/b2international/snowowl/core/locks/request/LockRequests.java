/*
 * Copyright 2022-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.locks.request;

/**
 * @since 8.1.0
 */
public final class LockRequests {

	public static final LockChangeRequestBuilder prepareLock() {
		return new LockChangeRequestBuilder(true);
	}
	
	public static final LockChangeRequestBuilder prepareUnlock() {
		return new LockChangeRequestBuilder(false);
	}
	
	public static final ResourceLockChangeRequestBuilder prepareResourceLock() {
		return new ResourceLockChangeRequestBuilder(true);
	}
	
	public static final ResourceLockChangeRequestBuilder prepareResourceUnlock() {
		return new ResourceLockChangeRequestBuilder(false);
	}
	
	private LockRequests() {
		// This class should not be instantiated
	}
}
