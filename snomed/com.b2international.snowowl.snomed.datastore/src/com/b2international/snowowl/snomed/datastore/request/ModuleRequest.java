/*
 * Copyright 2019-2021 B2i Healthcare, https://b2ihealthcare.com
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

import java.io.Serializable;
import java.util.function.Function;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;

/**
 * @since 6.17
 */
public final class ModuleRequest<C extends BranchContext, R> extends DelegatingRequest<C, C, R> {

	public interface ModuleIdProvider extends Function<SnomedDocument, String>, Serializable { }

	private static final long serialVersionUID = 1L;
	
	private final ModuleIdProvider moduleIdFunction;

	public ModuleRequest(final Request<C, R> next) {
		this(next, null);
	}
	
	public ModuleRequest(final Request<C, R> next, final String defaultModuleId) {
		super(next);
		
		// Use the component's module ID if no default value has been given
		if (defaultModuleId == null) {
			this.moduleIdFunction = c -> c.getModuleId();	
		} else {
			this.moduleIdFunction = c -> defaultModuleId;	
		}
	}

	@Override
	public R execute(final C context) {
		return next((C) context.inject()
				.bind(ModuleIdProvider.class, moduleIdFunction)
				.build());
	}
}
