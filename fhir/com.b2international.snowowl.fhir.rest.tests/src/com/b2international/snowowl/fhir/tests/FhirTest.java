/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.tests;

import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.springframework.http.converter.json.MappingJacksonValue;

import com.b2international.snowowl.core.rest.SnowOwlApiConfig;
import com.b2international.snowowl.fhir.core.codesystems.IssueSeverity;
import com.b2international.snowowl.fhir.core.codesystems.IssueType;
import com.b2international.snowowl.fhir.core.model.Issue;
import com.b2international.snowowl.fhir.core.model.Issue.Builder;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupResult;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Fhir;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Json;
import com.b2international.snowowl.test.commons.TestMethodNameRule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import io.restassured.path.json.JsonPath;

/**
 * Superclass for common test functionality
 * @since 6.3
 */
public class FhirTest {
	
	protected static final String TEST_DATE_STRING = "2018-03-23T07:49:40.000+0000"; //$NON-NLS-N$
	
	protected static final ObjectMapper objectMapper = SnowOwlApiConfig.createObjectMapper();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Rule
	public TestMethodNameRule methodNameRule = new TestMethodNameRule();
	
	protected Builder validationErrorIssueBuilder = Issue.builder()
			.code(IssueType.INVALID)
			.severity(IssueSeverity.ERROR)
			.diagnostics("1 validation error");
	
	protected void printPrettyJson(Object object) throws Exception {
		String result = objectMapper.writeValueAsString(object);
		Object json = objectMapper.readValue(result, Object.class);
		String prettyPrint = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
		System.out.println(prettyPrint);
	}
	
	protected void applyFilter(Object filteredObject) {
		SimpleFilterProvider filterProvider = new SimpleFilterProvider().setFailOnUnknownId(false);
		MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(filteredObject);
		mappingJacksonValue.setFilters(filterProvider);
		objectMapper.setFilterProvider(filterProvider);
	}
	
	protected JsonPath getJsonPath(Object object) throws JsonProcessingException {
		return new JsonPath(objectMapper.writeValueAsString(object));
	}
	
	/**
	 * Converts the parameter-formatted response string to a {@link LookupResult} object
	 * @param responseString
	 * @return
	 * @throws Exception
	 */
	protected LookupResult convertToResult(String responseString) throws Exception {
		Fhir parameters = objectMapper.readValue(responseString, Parameters.Fhir.class);
		Json json = new Parameters.Json(parameters);
		return objectMapper.convertValue(json, LookupResult.class);
	}
	
	
	protected String toFhirParameters(Object request) throws Exception {
		Fhir fhirParameters = new Parameters.Fhir(request);
		return objectMapper.writeValueAsString(fhirParameters);
	}

}
