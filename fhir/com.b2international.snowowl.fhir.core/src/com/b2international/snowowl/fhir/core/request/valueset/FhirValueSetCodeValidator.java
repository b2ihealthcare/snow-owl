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

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.fhir.core.model.ValidateCodeResult;
import com.b2international.snowowl.fhir.core.model.valueset.ValidateCodeRequest;
import com.b2international.snowowl.fhir.core.model.valueset.ValueSet;

/**
 * @since 8.0
 */
@FunctionalInterface
public interface FhirValueSetCodeValidator {

	FhirValueSetCodeValidator NOOP = (context, valueSet, request) -> ValidateCodeResult.builder().message("N/A").build();
	
	/**
	 * Validates whether the given Value Set conforms to the given {@link ValidateCodeRequest} or not.
	 * @param context
	 * @param valueSet
	 * @param request
	 * @return the code validation result, never <code>null</code>.
	 */
	ValidateCodeResult validateCode(ServiceProvider context, ValueSet valueSet, ValidateCodeRequest request);
	
}
