/*
 * Copyright 2021-2024 B2i Healthcare, https://b2ihealthcare.com
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

import org.hl7.fhir.r5.model.ValueSet;

import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.fhir.core.operations.ValueSetValidateCodeParameters;
import com.b2international.snowowl.fhir.core.operations.ValueSetValidateCodeResultParameters;
import com.b2international.snowowl.fhir.core.request.FhirRequests;

/**
 * @since 8.0
 */
final class FhirValueSetValidateCodeRequest implements Request<ServiceProvider, ValueSetValidateCodeResultParameters> {

	private static final long serialVersionUID = 2L;
	
	private final ValueSetValidateCodeParameters parameters;
	
	public FhirValueSetValidateCodeRequest(ValueSetValidateCodeParameters parameters) {
		this.parameters = parameters;
	}
	
	@Override
	public ValueSetValidateCodeResultParameters execute(ServiceProvider context) {
		final ValueSet valueSet = FhirRequests.valueSets().prepareGet(parameters.getUrl().asStringValue()).buildAsync().execute(context);
		return context.service(RepositoryManager.class)
				.get(valueSet.getUserString(TerminologyResource.Fields.TOOLING_ID))
				.optionalService(FhirValueSetCodeValidator.class)
				.orElse(FhirValueSetCodeValidator.NOOP)
				.validateCode(context, valueSet, parameters);
	}

}
