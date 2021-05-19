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
import com.b2international.snowowl.fhir.core.model.codesystem.ValidateCodeRequest;
import com.b2international.snowowl.fhir.core.model.dt.*;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Fhir;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Json;
import com.b2international.snowowl.fhir.tests.FhirExceptionIssueMatcher;
import com.b2international.snowowl.fhir.tests.FhirTest;

/**
 * CodeSystem$validate-code request deserialization test
 * 
 * @since 7.17.0
 */
public class ValidateCodeSystemCodeRequestTest extends FhirTest {
	
	@Test
	public void missingCodeTest() {
		
		Builder builder = Issue.builder()
			.code(IssueType.INVALID)
			.severity(IssueSeverity.ERROR)
			.diagnostics("1 validation error");
		
		Issue expectedIssue = builder.addLocation("ValidateCodeRequest.codeMissing")
			.codeableConceptWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'codeMissing' content is invalid [false]."
					+ " Violation: No code is provided to validate.")
			.build();
		
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		ValidateCodeRequest.builder().build();
	}
	
	@Test
	public void missingSystemTest() {
		
		Builder builder = Issue.builder()
			.code(IssueType.INVALID)
			.severity(IssueSeverity.ERROR)
			.diagnostics("1 validation error");
		
		Issue expectedIssue = builder.addLocation("ValidateCodeRequest.systemMissing")
			.codeableConceptWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'systemMissing' content is invalid [false]."
					+ " Violation: System is missing for provided code.")
			.build();
		
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		ValidateCodeRequest.builder()
			.code("A")
			.build();
	}
	
	@Test
	public void missingCodeWithDisplayTest() {
		
		Builder builder = Issue.builder()
			.code(IssueType.INVALID)
			.severity(IssueSeverity.ERROR)
			.diagnostics("1 validation error");
		
		Issue expectedIssue = builder.addLocation("ValidateCodeRequest.codeProvidedWithDisplay")
			.codeableConceptWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'codeProvidedWithDisplay' content is invalid [false]."
					+ " Violation: Code is missing while display is provided.")
			.build();
		
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		ValidateCodeRequest.builder()
			.display("TermA")
			.url("systemURI")
			.coding(Coding.builder().code("A").system("systemURI").build())
			.build();
	}
	
	@Test
	public void multipleCodesTest() {
		
		Builder builder = Issue.builder()
			.code(IssueType.INVALID)
			.severity(IssueSeverity.ERROR)
			.diagnostics("1 validation error");
		
		Issue expectedIssue = builder.addLocation("ValidateCodeRequest.tooManyCodesDefined")
			.codeableConceptWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'tooManyCodesDefined' content is invalid [false]."
					+ " Violation: Either code, coding or codeable can be defined at a time.")
			.build();
		
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		ValidateCodeRequest.builder()
			.code("B")
			.url("systemURI")
			.coding(Coding.builder().code("A").system("systemURI").build())
			.build();
	}
	
	@Test
	public void differentSystemsTest() {
		
		Builder builder = Issue.builder()
			.code(IssueType.INVALID)
			.severity(IssueSeverity.ERROR)
			.diagnostics("1 validation error");
		
		Issue expectedIssue = builder.addLocation("ValidateCodeRequest.systemsDifferent")
			.codeableConceptWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'systemsDifferent' content is invalid [false]."
					+ " Violation: System URL and Coding.system are different.")
			.build();
		
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		ValidateCodeRequest.builder()
			.url("systemURI")
			.coding(Coding.builder().code("A").system("systemURI2").build())
			.build();
	}
	
	@Test
	public void differentSystemsInCodeableTest() {
		
		Builder builder = Issue.builder()
			.code(IssueType.INVALID)
			.severity(IssueSeverity.ERROR)
			.diagnostics("1 validation error");
		
		Issue expectedIssue = builder.addLocation("ValidateCodeRequest.invalidCodeableSystem")
			.codeableConceptWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'invalidCodeableSystem' content is invalid [false]."
					+ " Violation: System URL and a Coding.system in Codeable are different.")
			.build();
		
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		ValidateCodeRequest.builder()
			.url("systemURI")
			.codeableConcept(CodeableConcept.builder()
					.addCoding(Coding.builder().code("A").system("systemURI2").build())
					.build())
			.build();
	}
	
	@Test
	public void fullCircleTest() throws Exception {
		
		Coding coding = Coding.builder()
			.system("http://hl7.org/fhir/issue-severity")
			.code("fatal")
			.build();

		ValidateCodeRequest request = ValidateCodeRequest.builder()
				.url("http://hl7.org/fhir/issue-severity")
				.coding(coding)
				.isAbstract(true)
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
		ValidateCodeRequest validateRequest = objectMapper.convertValue(json, ValidateCodeRequest.class);
		System.out.println("... and back to the object representation we started from:" + validateRequest);
	}
	
	@Test
	public void testDeserialization() {

		Coding coding = Coding.builder()
				.system("http://hl7.org/fhir/issue-severity")
				.code("fatal")
				.build();

		ValidateCodeRequest request = ValidateCodeRequest.builder()
				.url("http://hl7.org/fhir/issue-severity")
				.coding(coding)
				.isAbstract(true)
				.build();

		Fhir fhirParameters = new Parameters.Fhir(request);
		Optional<Parameter> findFirst = fhirParameters.getParameters().stream()
				.filter(p -> {
					Uri uri = (Uri) p.getValue();
					return uri.getUriValue().equals("http://hl7.org/fhir/issue-severity");
				})
				.findFirst();

		assertTrue(findFirst.isPresent());
	}

}
