/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.r5;

import com.b2international.snowowl.fhir.core.model.codesystem.SubsumptionResult;

/**
 * @since 9.0
 */
public class FhirSubsumptionConverter {

	public static org.hl7.fhir.r5.model.Parameters toParameters(final SubsumptionResult subsumptionResult) {
		final String code = subsumptionResult.getOutcome().getResultString();
		
		final var parameters = new org.hl7.fhir.r5.model.Parameters();
		parameters.addParameter("outcome", new org.hl7.fhir.r5.model.CodeType(code));
		return parameters;
	}
}
