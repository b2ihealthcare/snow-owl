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
package com.b2international.snowowl.fhir.tests.filter;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.Test;

import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.search.RawRequestParameter;
import com.b2international.snowowl.fhir.core.search.SupportedFhirUriParameterDefinitions;
import com.b2international.snowowl.fhir.core.search.SupportedFilterParameter;
import com.b2international.snowowl.fhir.core.search.FhirRequestParameterDefinition.FhirRequestParameterType;

public class SupportedFhirUriParameterDefinitionsTest {
	
	@Test
	public void supportedParameterDefinitionsTest() {
		
		SupportedFhirUriParameterDefinitions supportedDefinitions = SupportedFhirUriParameterDefinitions.createDefinitions(CodeSystem.class);
		
		System.out.println(supportedDefinitions);
		
		
		Map<String, SupportedFilterParameter> supportedFilterParameters = supportedDefinitions.getSupportedFilterParameters();
		
		Set<String> supportedFilterKeys = supportedFilterParameters.keySet();
		
		for (String filterKey : supportedFilterKeys) {
			System.out.println("Filter key: " + filterKey);
		}
		
		assertFalse(supportedFilterKeys.isEmpty());
		
		Optional<String> summaryFilterOptional = supportedFilterKeys.stream().filter(f -> f.equals(SupportedFilterParameter.FhirFilterParameterKey._summary.name())).findFirst();
		assertTrue(summaryFilterOptional.isPresent());
		
		SupportedFilterParameter summaryFilterParameter = supportedFilterParameters.get(summaryFilterOptional.get());
		
		assertThat(summaryFilterParameter.getType(), equalTo(FhirRequestParameterType.STRING));
	}
	
	@Test
	public void parameterValidationTest() {
		
		SupportedFhirUriParameterDefinitions supportedDefinitions = SupportedFhirUriParameterDefinitions.createDefinitions(CodeSystem.class);
		
		RawRequestParameter requestParameter = new RawRequestParameter("_name", Collections.emptySet());
		
		supportedDefinitions.validateSearchParameter(requestParameter);
		
		requestParameter = new RawRequestParameter("_name:exact", Collections.emptySet());
		
		supportedDefinitions.validateSearchParameter(requestParameter);
		
		
		
	}

}
