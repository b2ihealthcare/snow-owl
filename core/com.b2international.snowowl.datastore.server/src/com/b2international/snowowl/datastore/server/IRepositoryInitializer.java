/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server;

import com.b2international.snowowl.datastore.cdo.ICDORepository;

/**
 * Ensures that a particular terminology repository is in the expected state at the beginning of its lifecycle.
 * <p>
 * Implementations are typically performing the following operations:
 * <ul>
 * <li>Register the repository's primary code system;
 * <li>Add CDO resources that hold terminology content and any auxiliary data;
 * <li>Add DB indexes to tables, if required;
 * <li>Inject supporting terminology content, if required.
 * </ul>
 * <p>
 * Operations are free to check on each startup if they have to be executed, or be 
 * bound to first-time initialization of the repository. 
 */
public interface IRepositoryInitializer {

	/**
	 * Executes initialization checks for the corresponding repository.
	 * 
	 * @param repository  the repository to initialize (may not be {@code null}) 
	 */
	void initialize(ICDORepository repository);
}
