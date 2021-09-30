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
package com.b2international.snowowl.fhir.tests.domain;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.b2international.snowowl.fhir.core.codesystems.*;
import com.b2international.snowowl.fhir.core.exceptions.ValidationException;
import com.b2international.snowowl.fhir.core.model.*;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupRequest;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupResult;
import com.b2international.snowowl.fhir.core.model.codesystem.Property;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Json;
import com.b2international.snowowl.fhir.tests.FhirExceptionIssueMatcher;
import com.b2international.snowowl.fhir.tests.FhirTest;

/**
 * Test for validating the {@link Entry} model object.
 * @since 8.0.0
 */
public class BundleEntryTest extends FhirTest {
	
	@Test
	public void buildInvalidParametersEntry() {
		
		ValidationException exception = assertThrows(ValidationException.class, () -> {
			
			LookupRequest lookupRequest = LookupRequest.builder()
					.code("23245-4")
					.system("http://loinc.org")
					.build();
			
			Json json1 = new Parameters.Json(lookupRequest);
			
			ParametersRequestEntry entry = ParametersRequestEntry.builder()
					.request(BatchRequest.createGetRequest("CodeSystem/$lookup"))
					.resource(new Parameters.Fhir(json1.parameters()))
					.build();
		});
		
		Issue expectedIssue = validationErrorIssueBuilder
				.addLocation("ParametersRequestEntry.post")
				.detailsWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'post' content is invalid [false]. Violation: Only POST requests can be parameter-based.")
				.build();
		
		assertThat(exception, FhirExceptionIssueMatcher.issue(expectedIssue));
		
	}
	
	@Test
	public void requestEntryWithoutResourceTest() throws Exception {
	
		RequestEntry entry = RequestEntry.builder()
				.fullUrl("test_url")
				.request(BatchRequest.createGetRequest("/CodeSystem/$lookup?code=1234"))
				.build();
		
		String json = objectMapper.writeValueAsString(entry);
		Entry readEntry = objectMapper.readValue(json, Entry.class);
		assertTrue(readEntry instanceof RequestEntry);
		assertEquals("test_url", readEntry.getFullUrl().getUriValue());
		
	}
	
	@Test 
	public void requestEntryWithResourceTest() throws Exception {
		
		CodeSystem resource = CodeSystem.builder()
				.status(PublicationStatus.ACTIVE)
				.content(CodeSystemContentMode.COMPLETE)
				.build();
		
		ResourceRequestEntry entry = ResourceRequestEntry.builder()
				.fullUrl("test_url")
				.request(BatchRequest.createPostRequest("/CodeSystem"))
				.resource(resource)
				.build();
		
		String json = objectMapper.writeValueAsString(entry);
		Entry readEntry = objectMapper.readValue(json, Entry.class);
		assertTrue(readEntry instanceof ResourceRequestEntry);
		assertEquals("test_url", readEntry.getFullUrl().getUriValue());
	}
	
	@Test 
	public void requestEntryWithParametersTest() throws Exception {
		
		LookupRequest lookupRequest = LookupRequest.builder()
				.code("23245-4")
				.system("http://loinc.org")
				.build();
		
		Json json1 = new Parameters.Json(lookupRequest);
		
		ParametersRequestEntry entry = ParametersRequestEntry.builder()
				.request(BatchRequest.createPostRequest("CodeSystem/$lookup"))
				.resource(new Parameters.Fhir(json1.parameters()))
				.build();
		
		String json = objectMapper.writeValueAsString(entry);
		Entry readEntry = objectMapper.readValue(json, Entry.class);
		assertTrue(readEntry instanceof ParametersRequestEntry);
	}
	
	@Test 
	public void responseEntryWithResourceTest() throws Exception {
		
		CodeSystem resource = CodeSystem.builder()
				.status(PublicationStatus.ACTIVE)
				.content(CodeSystemContentMode.COMPLETE)
				.build();
		
		ResourceResponseEntry entry = ResourceResponseEntry.builder()
				.fullUrl("test_url")
				.response(BatchResponse.createOkResponse())
				.resource(resource)
				.build();
		
		String json = objectMapper.writeValueAsString(entry);
		Entry readEntry = objectMapper.readValue(json, Entry.class);
		assertTrue(readEntry instanceof ResourceResponseEntry);
		assertEquals("test_url", readEntry.getFullUrl().getUriValue());
	}
	
	@Test 
	public void responseEntryWithParametersTest() throws Exception {
		
		LookupResult lookupResult = LookupResult.builder()
				.name("test")
				.display("display")
				.addDesignation(Designation.builder()
						.value("dValue")
						.languageCode("uk").build())
				.addProperty(Property.builder()
						.code("1234")
						.description("propDescription")
						.valueString("stringValue")
						.build())
				.build();
		
		Json json1 = new Parameters.Json(lookupResult);
		System.out.println("JSON params:" + json1);
		
		ParametersResponseEntry entry = ParametersResponseEntry.builder()
				.fullUrl("test_url")
				.response(BatchResponse.createOkResponse())
				.resource(new Parameters.Fhir(json1.parameters()))
				.build();
		
		String json = objectMapper.writeValueAsString(entry);
		Entry readEntry = objectMapper.readValue(json, Entry.class);
		assertTrue(readEntry instanceof ParametersResponseEntry);
		assertEquals("test_url", readEntry.getFullUrl().getUriValue());
	}
	
	@Test
	public void responseEntryWithOperationOutcomeTest() throws Exception {
		
		OperationOutcome operationOutcome = OperationOutcome.builder()
				.addIssue(Issue.builder()
						.code(IssueType.CODE_INVALID)
						.diagnostics("Invalid code")
						.severity(IssueSeverity.ERROR)
						.build())
				.build();
		
		OperationOutcomeEntry operationOutcomeEntry = OperationOutcomeEntry.builder()
				.operationOutcome(operationOutcome)
				.response(new BatchResponse("404"))
				.build();
		
		String json = objectMapper.writeValueAsString(operationOutcomeEntry);
		Entry readEntry = objectMapper.readValue(json, Entry.class);
		assertTrue(readEntry instanceof OperationOutcomeEntry);
	}
	
}
