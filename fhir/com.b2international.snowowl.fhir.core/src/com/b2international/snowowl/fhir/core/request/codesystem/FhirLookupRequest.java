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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.domain.Concept;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupRequest;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupResult;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * Performs the lookup operation based on the parameter-based lookup request.
 * 
 * <p>
 * From the spec:
 * If no properties are specified, the server chooses what to return. The following properties are defined for all code systems: url, name, version (code system info) 
 * and code information: display, definition, designation, parent and child, and for designations, lang.X where X is a designation language code. 
 * Some of the properties are returned explicit in named parameters (when the names match), and the rest (except for lang.X) in the property parameter group
 * </p>
 * @see LookupRequest
 * @see LookupResult
 * @since 8.0
 */
final class FhirLookupRequest extends FhirRequest<LookupResult> {

	private static final long serialVersionUID = 1L;
	
	@NotNull
	@Valid
	@JsonProperty
	@JsonUnwrapped
	private LookupRequest request;

	FhirLookupRequest(LookupRequest request) {
		super(request.getSystem());
		this.request = request;
	}

	@Override
	protected LookupResult doExecute(ServiceProvider context, CodeSystem codeSystem) {
		String locales = request.getDisplayLanguage() != null ? request.getDisplayLanguage().getCodeValue() : null;
		if (CompareUtils.isEmpty(locales)) {
			locales = "en";
		}
		
		Concept concept = CodeSystemRequests.prepareSearchConcepts()
			.one()
			.filterById(request.getCode())
			.setLocales(locales)
			.build(codeSystem.getResourceURI())
			.getRequest()
			.execute(context)
			.first()
			.orElseThrow(() -> new NotFoundException("Concept", request.getCode()));
		
		return LookupResult.builder()
				.name(codeSystem.getId())
				.display(concept.getTerm())
				.build();
	}

}
