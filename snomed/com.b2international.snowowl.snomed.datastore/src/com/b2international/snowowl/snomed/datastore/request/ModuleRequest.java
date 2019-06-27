/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request;

import java.util.function.Function;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.snomed.Component;

/**
 * @since 6.17
 */
public final class ModuleRequest<R> extends DelegatingRequest<TransactionContext, TransactionContext, R> {

	public interface ModuleIdFunction extends Function<Component, String> { }

	private static final long serialVersionUID = 1L;
	
	private final ModuleIdFunction moduleIdFunction;

	public ModuleRequest(final Request<TransactionContext, R> next) {
		this(next, null);
	}
	
	public ModuleRequest(final Request<TransactionContext, R> next, final String defaultModuleId) {
		super(next);
		
		// Use the component's module ID if no default value has been given
		if (defaultModuleId == null) {
			this.moduleIdFunction = c -> c.getModule().getId();	
		} else {
			this.moduleIdFunction = c -> defaultModuleId;	
		}
	}

	@Override
	public R execute(final TransactionContext context) {
		return next(context.inject()
				.bind(ModuleIdFunction.class, moduleIdFunction)
				.build());
	}
}
