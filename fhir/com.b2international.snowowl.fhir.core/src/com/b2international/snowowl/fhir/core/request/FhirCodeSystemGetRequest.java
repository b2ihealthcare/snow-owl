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
package com.b2international.snowowl.fhir.core.request;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.exceptions.FhirException;
import com.b2international.snowowl.fhir.core.model.Entry;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 7.2
 */
public final class FhirCodeSystemGetRequest implements Request<ServiceProvider, CodeSystem> {

	@JsonProperty
	@NotEmpty
	private String logicalId;
	
	FhirCodeSystemGetRequest(String logicalId) {
		this.logicalId = logicalId;
	}
	
	@Override
	public CodeSystem execute(ServiceProvider context) {
		return FhirRequests.prepareSearchCodeSystem()
			.one()
			.filterById(logicalId)
			.build()
			.execute(context)
			.getEntries()
			.stream()
			.findFirst()
			.map(Entry::getResource)
			.map(CodeSystem.class::cast)
			.orElseThrow(() -> FhirException.createFhirError(String.format("No code system version found for code system %s", logicalId), OperationOutcomeCode.MSG_PARAM_INVALID, "CodeSystem"));
	}

}
