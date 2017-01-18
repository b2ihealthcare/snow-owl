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
package com.b2international.snowowl.snomed.datastore.id.action;

import com.b2international.snowowl.eventbus.IEventBus;
import com.google.inject.Provider;

/**
 * @since 4.5
 */
abstract class AbstractIdAction<I> implements IdAction<I> {
	
	protected final Provider<IEventBus> bus;

	private I result;
	
	AbstractIdAction(final Provider<IEventBus> bus) {
		this.bus = bus;
	}
	
	@Override
	public final I execute() {
		if (result != null) {
			throw new IllegalStateException("Action was already executed.");
		}
		
		try {
			result = doExecute();
			LOGGER.debug("Action {} executed successfully, result: {}.", this, result);
			return result;
		} catch (final Exception e) {
			LOGGER.debug("Exception caught while executing action {}.", this, e);
			throw e;
		}
	}

	@Override
	public final void commit() {
		if (result != null) {
			doCommit(result);
		}
	}

	@Override
	public final void rollback() {
		if (result != null) {
			doRollback(result);
		}
	}
	
	protected abstract I doExecute();

	protected void doCommit(final I storedResult) {
		// Empty by default, subclasses should override
	}

	protected void doRollback(final I storedResult) {
		// There's usually an insufficient amount of information to roll back most of the actions, so the default
		// implementation just adds a warning to the log.
		LOGGER.warn("Action {} can not be rolled back.", this);
	}

}
