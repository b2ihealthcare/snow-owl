/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.repository;

import com.b2international.commons.exceptions.ConflictException;
import com.b2international.snowowl.datastore.index.RevisionDocument;

/**
 * @since 7.0
 */
public interface ComponentDeletionPolicy {

	/**
	 * Allows the deletion of all components regardless of their state.
	 */
	ComponentDeletionPolicy ALLOW = doc -> true;

	/**
	 * Returns <code>true</code> if the given {@link RevisionDocument} can be deleted safely in regards of the underlying terminology's policy,
	 * otherwise returns <code>false</code>. The implementation should throw a {@link ConflictException} when the given component cannot be deleted
	 * and the default error message is not enough to fully describe the issue of the deletion.
	 * 
	 * @param revision
	 * @return
	 * @see RepositoryTransactionContext#delete(Object, boolean)
	 */
	boolean canDelete(RevisionDocument revision);

}
