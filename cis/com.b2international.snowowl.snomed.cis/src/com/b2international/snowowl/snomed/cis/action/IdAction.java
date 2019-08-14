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
package com.b2international.snowowl.snomed.cis.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.domain.RepositoryContext;

/**
 * Encapsulates an atomic action related to generating identifiers.
 * <p>
 * Actions can be aggregated into a log, in which identifier state changes resulting from these actions can either be finalized,
 * or rolled back if necessary.
 * 
 * @since 4.5
 */
public interface IdAction<T> {

	Logger LOGGER = LoggerFactory.getLogger("id-action");
	
	/**
	 * @return the computed result for this action
	 */
	T execute(RepositoryContext context);

	/**
	 * Attempts to issue a compensating (rollback) request for this action, if it failed to execute.
	 */
	void rollback(RepositoryContext context);

	/**
	 * Issues additional request(s) for this action to finalize changes, if executing the action succeeded.
	 */
	void commit(RepositoryContext context);

}
