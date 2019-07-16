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

import com.b2international.snowowl.core.domain.RepositoryContext;

/**
 * @since 4.5
 */
abstract class AbstractIdAction<I> implements IdAction<I> {
	
	private I result;
	
	@Override
	public final I execute(final RepositoryContext context) {
		if (result != null) {
			throw new IllegalStateException("Action was already executed.");
		}
		
		try {
			result = doExecute(context);
			LOGGER.debug("Action {} executed successfully, result: {}.", this, result);
			return result;
		} catch (final Exception e) {
			LOGGER.debug("Exception caught while executing action {}.", this, e);
			throw e;
		}
	}

	@Override
	public final void commit(final RepositoryContext context) {
		if (result != null) {
			doCommit(context, result);
		}
	}

	@Override
	public final void rollback(final RepositoryContext context) {
		if (result != null) {
			doRollback(context, result);
		}
	}
	
	protected abstract I doExecute(RepositoryContext context);

	protected void doCommit(final RepositoryContext context, final I storedResult) {
		// Empty by default, subclasses should override
	}

	protected void doRollback(final RepositoryContext context, final I storedResult) {
		// There's usually an insufficient amount of information to roll back most of the actions, so the default
		// implementation just adds a warning to the log.
		LOGGER.warn("Action {} can not be rolled back.", this);
	}

}
