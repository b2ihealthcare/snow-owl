/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.api.admin;

import com.b2international.snowowl.api.admin.exception.LockException;
import com.b2international.snowowl.api.admin.exception.RepositoryNotFoundException;

/**
 * Implementations of the repository service allow creating on-line backups of terminology content repositories by
 * preventing further modifications while the backup takes place.
 */
public interface IRepositoryService {

	/**
	 * Places a global lock, which prevents other users from making changes to any of the repositories while a backup is
	 * created. The call may block up to the specified timeout to acquire the lock; if {@code timeoutMillis} is set to
	 * {@code 0}, it returns immediately.
	 * 
	 * @param timeoutMillis lock timeout in milliseconds (may not be negative)
	 * 
	 * @throws LockException if the global lock could not be acquired for any reason (including cases when a conflicting
	 *                       lock is already held by someone else)
	 */
	void lockGlobal(int timeoutMillis);

	/**
	 * Releases a previously acquired global lock.
	 * 
	 * @throws LockException if the global lock could not be released for any reason (including cases when it was not held)
	 */
	void unlockGlobal();

	/**
	 * Places a repository-level lock, which prevents other users from making changes to the repository identified by
	 * {@code repositoryUuid}. The call may wait up to the specified timeout to acquire the lock; if
	 * {@code timeoutMillis} is set to {@code 0}, it returns immediately.
	 * 
	 * @param repositoryUuid a unique identifier pointing to a particular repository (may not be {@code null})
	 * @param timeoutMillis  lock timeout in milliseconds (may not be negative)
	 * 
	 * @throws RepositoryNotFoundException if the specified repository UUID does not correspond to any repository
	 * @throws LockException               if the repository lock could not be acquired for any reason (including cases when a
	 *                                     conflicting lock is already held by someone else)
	 */
	void lockRepository(String repositoryUuid, int timeoutMillis);

	/**
	 * Releases a previously acquired repository-level lock on the repository identified by {@code repositoryUuid}.
	 * 
	 * @param repositoryUuid a unique identifier pointing to a particular repository (may not be {@code null})
	 * 
	 * @throws RepositoryNotFoundException if the specified repository UUID does not correspond to any repository
	 * @throws LockException               if the repository lock could not be released for any reason (including cases when it was
	 *                                     not held)
	 */
	void unlockRepository(String repositoryUuid);

}
