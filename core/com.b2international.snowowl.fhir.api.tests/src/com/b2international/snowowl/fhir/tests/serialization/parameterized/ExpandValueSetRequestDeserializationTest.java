/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.tests.serialization.parameterized;

import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;

import com.b2international.snowowl.fhir.core.codesystems.IssueSeverity;
import com.b2international.snowowl.fhir.core.codesystems.IssueType;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.exceptions.ValidationException;
import com.b2international.snowowl.fhir.core.model.Issue;
import com.b2international.snowowl.fhir.core.model.Issue.Builder;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupRequest;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Parameter;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Fhir;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Json;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.valueset.ExpandValueSetRequest;
import com.b2international.snowowl.fhir.tests.FhirExceptionIssueMatcher;
import com.b2international.snowowl.fhir.tests.FhirTest;

/**
 * Expand Value set request deserialization test
 * @since 6.19
 */
public class ExpandValueSetRequestDeserializationTest extends FhirTest {
	
	@Test
	public void deserializationTest() throws Exception {
		
		
		ExpandValueSetRequest request = ExpandValueSetRequest.builder()
				.url("http://valueset.url")
				.valueSetVersion("20190101")
				.contextDirection("direction-code")
				.count(1)
				.addDesignation("uk_en")
				.addDesignation("us_en")
				.date(TEST_DATE_STRING)
				.filter("filter")
				.build();
		
		Json json = new Parameters.Json(request);
		System.out.println("JSON params:" + json);
		
		Fhir fhirParameters = new Parameters.Fhir(request);
		fhirParameters.getParameters().forEach(p -> System.out.println(p));
		Optional<Parameter> findFirst = fhirParameters.getParameters().stream()
				.filter(p -> {
					Uri url = (Uri) p.getValue();
					return url.getUriValue().equals("http://valueset.url");
				})
				.findFirst();

		assertTrue(findFirst.isPresent());
		
		Fhir fhir = new Parameters.Fhir(json.parameters());
		printPrettyJson(fhir);
		String fhirJson = objectMapper.writeValueAsString(fhir);
		System.out.println("This is the JSON request from the client: " + fhirJson);
	}
	
	//@Test
	public void fullCircleTest() throws Exception {
		
		Coding coding = Coding.builder()
			.system("http://hl7.org/fhir/issue-severity")
			.code("fatal")
			.build();

		System.out.println("Building the lookup request object.");
		LookupRequest request = LookupRequest.builder()
				.coding(coding)
				.build();
		
		Json json1 = new Parameters.Json(request);
		System.out.println("JSON params:" + json1);
		
		Fhir fhir = new Parameters.Fhir(json1.parameters());
		String fhirJson = objectMapper.writeValueAsString(fhir);
		System.out.println("This is the JSON request from the client: " + fhirJson);
		
		System.out.println("This is happening in the server-side...");
		Fhir parameters = objectMapper.readValue(fhirJson, Parameters.Fhir.class);
		System.out.println("Deserialized into FHIR parameters..." + parameters.getParameters());
		
		System.out.println("Back to Domain JSON...");
		Json json = new Parameters.Json(parameters);
		LookupRequest lookupRequest = objectMapper.convertValue(json, LookupRequest.class);
		System.out.println("... and back to the object representation we started from:" + lookupRequest);
		
	}
	
	//@Test
		public void missingCodeTest() {
			
			Builder builder = Issue.builder()
				.code(IssueType.INVALID)
				.severity(IssueSeverity.ERROR)
				.diagnostics("1 validation error");
			
			Issue expectedIssue = builder.addLocation("LookupRequest.codeMissing")
				.codeableConceptWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'codeMissing' content is invalid [false]."
						+ " Violation: Code is not provided for the system.")
				.build();
			
			exception.expect(ValidationException.class);
			exception.expectMessage("1 validation error");
			exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
			
			System.out.println("Building the lookup request object.");
			LookupRequest.builder()
				.system("system").build();
				
		}
	
	//@Test
	public void testDeserialization() {

		Coding coding = Coding.builder()
				.system("http://hl7.org/fhir/issue-severity")
				.code("fatal")
				.build();

		LookupRequest request = LookupRequest.builder().coding(coding).build();

		Fhir fhirParameters = new Parameters.Fhir(request);
		fhirParameters.getParameters().forEach(p -> System.out.println(p));
		Optional<Parameter> findFirst = fhirParameters.getParameters().stream()
				.filter(p -> {
					Coding pCoding = (Coding) p.getValue();
					return pCoding.getSystemValue().equals("http://hl7.org/fhir/issue-severity");
				})
				.findFirst();

		assertTrue(findFirst.isPresent());
	}

}
