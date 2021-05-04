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
package com.b2international.snowowl.fhir.core.search;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.exceptions.FhirException;

/**
 * Class to represent a FHIR URI filter parameter.
 * 
 * @since 7.14
 *
 */
public class FhirFilterParameter extends FhirParameter {

	public FhirFilterParameter(FhirUriFilterParameterDefinition supportedFilterParameter, Collection<String> values) {
		super(supportedFilterParameter, values);
	}

	@Override
	public void validate() {
		
		//No restriction in the definition
		Set<String> supportedValues = parameterDefinition.getSupportedValues();
		if (supportedValues.isEmpty()) return;
		
		Set<String> uppercaseValues = values.stream().map(String::toUpperCase).collect(Collectors.toSet());
		if (supportedValues.containsAll(uppercaseValues)) return;
			
		throw FhirException.createFhirError(String.format("Filter parameter value %s is not supported. Supported parameter values are %s.", Arrays.toString(values.toArray()), Arrays.toString(supportedValues.toArray())), OperationOutcomeCode.MSG_PARAM_UNKNOWN, "SEARCH_REQUEST_PARAMETER_MARKER");
	}

}
