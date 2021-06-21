/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.request.codesystem;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 8.0
 * @param <R>
 */
public abstract class FhirRequest<R> implements Request<ServiceProvider, R> {

	private static final long serialVersionUID = 1L;
	
	private final String system;

	public FhirRequest(String system) {
		this.system = system;
	}
	
	@Override
	public final R execute(ServiceProvider context) {
		CodeSystem codeSystem = CodeSystemRequests
				.prepareSearchCodeSystem()
				.one()
				.filterByUrl(system)
				.buildAsync()
				.getRequest()
				.execute(context)
				.first()
				.orElseThrow(() -> new NotFoundException("CodeSystem", system));
		
		// TODO support searching versions 
		
		return doExecute(context, codeSystem);
	}

	protected abstract R doExecute(ServiceProvider context, CodeSystem codeSystem);

}
