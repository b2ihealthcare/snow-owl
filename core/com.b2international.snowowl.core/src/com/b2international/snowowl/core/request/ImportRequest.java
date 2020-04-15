/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request;

import com.b2international.commons.exceptions.ApiException;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.internal.locks.DatastoreLockContextDescriptions;
import com.b2international.snowowl.core.locks.Locks;

/**
 * @since 7.6
 */
public abstract class ImportRequest<C extends RepositoryContext, R> implements Request<C, R> {

	private static final long serialVersionUID = 1L;
		
	protected abstract R doImport(C context);
	
	protected String parentLockContext() {
		return DatastoreLockContextDescriptions.IMPORT;
	}
	
	@Override
	public R execute(C context) {
		try (Locks locks = Locks.on(context).lock(parentLockContext())) {
			return doImport(context);
		} catch (Exception e) {
			if (e instanceof ApiException) {
				throw (ApiException) e;
			}
			throw SnowowlRuntimeException.wrap(e);
		}
	}

}
