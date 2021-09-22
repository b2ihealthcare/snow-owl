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
package com.b2international.snowowl.fhir.core.request.valueset;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.fhir.core.model.valueset.ExpandValueSetRequest;
import com.b2international.snowowl.fhir.core.model.valueset.ValueSet;
import com.b2international.snowowl.fhir.core.request.FhirRequests;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * @since 8.0
 */
final class FhirValueSetExpandRequest implements Request<ServiceProvider, ValueSet> {

	private static final long serialVersionUID = 1L;
	
	@NotNull
	@Valid
	@JsonProperty
	@JsonUnwrapped
	private ExpandValueSetRequest request;

	public FhirValueSetExpandRequest(ExpandValueSetRequest request) {
		this.request = request;
	}
	
	@Override
	public ValueSet execute(ServiceProvider context) {
		final ValueSet valueSet = FhirRequests.valueSets().prepareGet(request.getUrl().getUriValue()).buildAsync().execute(context);
		return context.optionalService(FhirValueSetExpander.class).orElse(FhirValueSetExpander.NOOP).expand(valueSet);
	}

}
