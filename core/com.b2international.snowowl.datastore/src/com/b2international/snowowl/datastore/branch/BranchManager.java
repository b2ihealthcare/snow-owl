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
package com.b2international.snowowl.datastore.branch;

import java.util.Collection;

import com.b2international.snowowl.core.exceptions.NotFoundException;

/**
 * @since 4.1
 */
public interface BranchManager {

	/**
	 * Returns the MAIN branch.
	 * 
	 * @return a never <code>null</code> {@link Branch} instance representing the one and only MAIN branch.
	 */
	Branch getMainBranch();

	/**
	 * Returns the {@link Branch} represented with the given path.
	 * 
	 * @param path
	 * @return
	 * @throws NotFoundException
	 *             - if the branch does not exists with the given path
	 */
	Branch getBranch(String path);

	/**
	 * Returns all branches associated with this {@link BranchManager}.
	 * 
	 * @return
	 */
	Collection<? extends Branch> getBranches();

}