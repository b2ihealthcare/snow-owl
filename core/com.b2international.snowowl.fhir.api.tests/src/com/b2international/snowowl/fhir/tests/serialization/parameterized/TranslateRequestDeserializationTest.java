/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.b2international.snowowl.fhir.core.codesystems.IssueSeverity;
import com.b2international.snowowl.fhir.core.codesystems.IssueType;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.exceptions.ValidationException;
import com.b2international.snowowl.fhir.core.model.Issue;
import com.b2international.snowowl.fhir.core.model.conceptmap.Dependency;
import com.b2international.snowowl.fhir.core.model.conceptmap.TranslateRequest;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.CodeableConcept;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Parameter;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Fhir;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.tests.FhirExceptionIssueMatcher;
import com.b2international.snowowl.fhir.tests.FhirTest;
import com.b2international.snowowl.snomed.fhir.SnomedUri;

/**
 * Translate request deserialization test
 * @since 7.1
 */
public class TranslateRequestDeserializationTest extends FhirTest {
	
	//@Test
	public void missingSystemTest() {
		
		Issue expectedIssue = Issue.builder()
			.code(IssueType.INVALID)
			.severity(IssueSeverity.ERROR)
			.diagnostics("1 validation error")
			.addLocation("TranslateRequest.codeAndSystemValid")
			.codeableConceptWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'codeAndSystemValid' content is invalid [false]."
					+ " Violation: Both code and system needs to be provided.")
			.build();
		
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		TranslateRequest.builder()
			.code("fatal")
			.target("target")
			.build();
		
	}

	//@Test
	public void invalidSourceTest() {
		
		Issue expectedIssue = Issue.builder()
			.code(IssueType.INVALID)
			.severity(IssueSeverity.ERROR)
			.diagnostics("1 validation error")
			.addLocation("TranslateRequest.sourceValid")
			.codeableConceptWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'sourceValid' content is invalid [false]. "
					+ "Violation: Source needs to be set either via code/system or code or codeable concept.")
			.build();
		
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		Coding coding = Coding.builder()
			.system("http://hl7.org/fhir/issue-severity")
			.code("fatal")
			.build();
		
		TranslateRequest.builder()
			.code("fatal")
			.system("http://hl7.org/fhir/issue-severity")
			.version("3.0.1")
			.coding(coding)
			.target("http://target.codesystem.uri")
			.build();
		
	}
	
	//@Test
	public void missingSourceTest() {
		
		Issue expectedIssue = Issue.builder()
			.code(IssueType.INVALID)
			.severity(IssueSeverity.ERROR)
			.diagnostics("1 validation error")
			.addLocation("TranslateRequest.sourceValid")
			.codeableConceptWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'sourceValid' content is invalid [false]. "
					+ "Violation: Source needs to be set either via code/system or code or codeable concept.")
			.build();
		
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		TranslateRequest.builder()
			.target("target")
			.build();
		
	}
	
	//@Test
	public void missingTargetTest() {
		
		Issue expectedIssue = Issue.builder()
			.code(IssueType.INVALID)
			.severity(IssueSeverity.ERROR)
			.diagnostics("1 validation error")
			.addLocation("TranslateRequest.targetValid")
			.codeableConceptWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'targetValid' content is invalid [false]. "
				+ "Violation: Target or target system needs to be provided.")
			.build();
		
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		TranslateRequest.builder()
			.code("fatal")
			.system("http://hl7.org/fhir/issue-severity")
			.build();
		
	}
	
	//@Test
	public void validRequestTest() throws Exception {
		
		TranslateRequest request = TranslateRequest.builder()
			.code("fatal")
			.system("http://hl7.org/fhir/issue-severity")
			.targetSystem(SnomedUri.SNOMED_BASE_URI)
			.build();
	
		Fhir fhirParameters = new Parameters.Fhir(request);
		fhirParameters.getParameters().forEach(p -> System.out.println(p));
		
		printPrettyJson(fhirParameters);
		
		Parameter parameter = fhirParameters.getByName("code").get();
		assertEquals("fatal", ((Code) parameter.getValue()).getCodeValue());
		
		parameter = fhirParameters.getByName("system").get();
		assertEquals("http://hl7.org/fhir/issue-severity", ((Uri) parameter.getValue()).getUriValue());
		
		parameter = fhirParameters.getByName("targetsystem").get();
		assertEquals(SnomedUri.SNOMED_BASE_URI_STRING, ((Uri) parameter.getValue()).getUriValue());
		
	}
	
	//@Test
	public void validRequestWithCodingTest() throws Exception {
		
		TranslateRequest request = TranslateRequest.builder()
			.coding(Coding.builder()
				.system("http://hl7.org/fhir/issue-severity")
				.code("fatal")
				.build())
			.targetSystem(SnomedUri.SNOMED_BASE_URI)
			.build();
	
		Fhir fhirParameters = new Parameters.Fhir(request);
		fhirParameters.getParameters().forEach(p -> System.out.println(p));
		
		printPrettyJson(fhirParameters);
		
		Parameter parameter = fhirParameters.getByName("coding").get();
		Coding coding = (Coding) parameter.getValue();
		assertEquals("fatal", coding.getCodeValue());
		
		assertEquals("http://hl7.org/fhir/issue-severity", coding.getSystem().getUriValue());
		
		parameter = fhirParameters.getByName("targetsystem").get();
		assertEquals(SnomedUri.SNOMED_BASE_URI_STRING, ((Uri) parameter.getValue()).getUriValue());
		
	}
	
	//@Test
	public void validRequestWithDependencyTest() throws Exception {
		
		Coding dependencyCoding = Coding.builder()
			.code("1234")
			.system("http://snomed.info/sct")
			.version("20180131")
			.build();
		
		CodeableConcept cc = CodeableConcept.builder()
			.addCoding(dependencyCoding)
			.text("text")
			.build();
		
		TranslateRequest request = TranslateRequest.builder()
			.coding(Coding.builder()
				.system("http://hl7.org/fhir/issue-severity")
				.code("fatal")
				.build())
			.targetSystem(SnomedUri.SNOMED_BASE_URI)
			.addDependency(Dependency.builder()
				.element("element")
				.concept(cc)
				.build())
			.build();
	
		Fhir fhirParameters = new Parameters.Fhir(request);
		fhirParameters.getParameters().forEach(p -> System.out.println(p));
		
		printPrettyJson(fhirParameters);
		
		Parameter parameter = fhirParameters.getByName("coding").get();
		Coding coding = (Coding) parameter.getValue();
		assertEquals("fatal", coding.getCodeValue());
		
		assertEquals("http://hl7.org/fhir/issue-severity", coding.getSystem().getUriValue());
		
		parameter = fhirParameters.getByName("targetsystem").get();
		assertEquals(SnomedUri.SNOMED_BASE_URI_STRING, ((Uri) parameter.getValue()).getUriValue());
		
		parameter = fhirParameters.getByName("dependency").get();
		Parameters dependencyParameters = (Parameters) parameter.getValue();
		parameter = dependencyParameters.getByName("concept").get();
		
		assertEquals("text", ((CodeableConcept) parameter.getValue()).getText());
	}
	
	//If the system is SNOMED, no version information is accepted as it is part of the URI
	//@see SnomedUri
	@Test
	public void invalidSnomedVersionTest() {
		
		Issue expectedIssue = Issue.builder()
				.code(IssueType.INVALID)
				.severity(IssueSeverity.ERROR)
				.diagnostics("1 validation error")
				.addLocation("TranslateRequest.versionValid")
				.codeableConceptWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'versionValid' content is invalid [false]. "
					+ "Violation: SNOMED CT version is defined as part of the system URI.")
				.build();
			
			exception.expect(ValidationException.class);
			exception.expectMessage("1 validation error");
			exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		TranslateRequest.builder()
			.code("1234")
			.system(SnomedUri.SNOMED_BASE_URI_STRING + "/20180131")
			.version("OTHER_VERSION")
			.targetSystem(SnomedUri.SNOMED_BASE_URI)
			.build();
		
	}
	
	//If the system is SNOMED, no version information is accepted as it is part of the URI
	//@see SnomedUri
	@Test
	public void invalidSnomedCodingTest() {
		
		Issue expectedIssue = Issue.builder()
			.code(IssueType.INVALID)
			.severity(IssueSeverity.ERROR)
			.diagnostics("1 validation error")
			.addLocation("Coding.versionValid")
			.codeableConceptWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'versionValid' content is invalid [false]. "
				+ "Violation: SNOMED CT version is defined as part of the system URI.")
			.build();
		
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		TranslateRequest.builder()
			.coding(Coding.builder()
				.system(SnomedUri.SNOMED_BASE_URI_STRING)
				.version("OTHER_VERSION")
				.code("fatal")
				.build())
			.version("OTHER_VERSION")
			.targetSystem(SnomedUri.SNOMED_BASE_URI)
			.build();
		}

}
