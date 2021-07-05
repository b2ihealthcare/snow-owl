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

import com.b2international.commons.CompareUtils;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.fhir.core.model.Entry;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.request.FhirRequests;

/**
 * @since 8.0
 * @param <R>
 */
public abstract class FhirRequest<R> implements Request<ServiceProvider, R> {

	private static final long serialVersionUID = 1L;
	
	private final String system;
	
	private final String version;

	public FhirRequest(String system, String version) {
		this.system = system;
		this.version = version;
	}
	
	@Override
	public final R execute(ServiceProvider context) {
		CodeSystem codeSystem = FhirRequests
				.codeSystems().prepareSearch()
				.one()
				.filterByUrl(system)
				.filterByVersion(version)
				.buildAsync()
				.getRequest()
				.execute(context)
				.first()
				.map(Entry::getResource)
				.map(CodeSystem.class::cast)
				.orElseThrow(() -> new NotFoundException("CodeSystem", system));
		
		return doExecute(context, codeSystem);
	}
	
	protected String extractLocales(Code displayLanguage) {
		String locales = displayLanguage != null ? displayLanguage.getCodeValue() : null;
		if (CompareUtils.isEmpty(locales)) {
			locales = "en";
		}
		return locales;
	}

	protected abstract R doExecute(ServiceProvider context, CodeSystem codeSystem);

}
