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

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.fhir.core.model.ValidateCodeResult;
import com.b2international.snowowl.fhir.core.operations.ValueSetValidateCodeParameters;

/**
 * @since 8.0
 */
@FunctionalInterface
public interface FhirValueSetCodeValidator {

	FhirValueSetCodeValidator NOOP = (context, valueSet, request) -> ValidateCodeResult.builder().message("N/A").build();
	
	/**
	 * Validates whether the code described in the given {@link ValueSetValidateCodeParameters} conforms to the {@link ValueSet} or not.
	 * 
	 * @param context
	 * @param valueSet
	 * @param parameters
	 * @return the code validation result, never <code>null</code>.
	 */
	ValidateCodeResult validateCode(ServiceProvider context, ValueSet valueSet, ValueSetValidateCodeParameters request);
	
}
