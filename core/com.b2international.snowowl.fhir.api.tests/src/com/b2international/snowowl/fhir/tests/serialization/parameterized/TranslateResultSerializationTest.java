/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.b2international.snowowl.fhir.core.codesystems.ConceptMapEquivalence;
import com.b2international.snowowl.fhir.core.codesystems.IssueSeverity;
import com.b2international.snowowl.fhir.core.codesystems.IssueType;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.exceptions.ValidationException;
import com.b2international.snowowl.fhir.core.model.Issue;
import com.b2international.snowowl.fhir.core.model.Issue.Builder;
import com.b2international.snowowl.fhir.core.model.conceptmap.Match;
import com.b2international.snowowl.fhir.core.model.conceptmap.Product;
import com.b2international.snowowl.fhir.core.model.conceptmap.TranslateResult;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Parameter;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Fhir;
import com.b2international.snowowl.fhir.tests.FhirExceptionIssueMatcher;
import com.b2international.snowowl.fhir.tests.FhirTest;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.fhir.SnomedUri;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Test for serializing the @see {@link TranslateResult} class.
 * see ConceptMap$translate
 * 
 * @since 6.6
 */
public class TranslateResultSerializationTest extends FhirTest {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void missingResultTest() throws Exception {

		Builder builder = Issue.builder()
			.code(IssueType.INVALID)
			.severity(IssueSeverity.ERROR)
			.diagnostics("1 validation error");
		
		Issue expectedIssue = builder.addLocation("TranslateResult.result")
				.codeableConceptWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, 
						"Parameter 'result' content is invalid [null]. Violation: may not be null.")
				.build();
		
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		TranslateResult.builder().build();
	}
	
	@Test
	public void validResultTest() throws JsonProcessingException {
		
		Match match = Match.builder()
			.equivalence(ConceptMapEquivalence.EQUAL)
			.concept(Coding.builder()
					.system(SnomedUri.SNOMED_BASE_URI_STRING)
					.code(Concepts.CAUSATIVE_AGENT)
					.display("Causutive agent")
					.build())
			.addProduct(Product.builder()
					.concept("code", "system", "codeTerm")
					.build())
			.build();
		
		TranslateResult translateResult = TranslateResult.builder()
			.result(true)
			.message("This is a test result")
			.addMatch(match)
			.build();
		
		Fhir fhirParameters = new Parameters.Fhir(translateResult);
		
		System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(fhirParameters));
		
		Parameter parameter = fhirParameters.getByName("result").get();
		Boolean result = (Boolean) parameter.getValue();
		assertEquals(true, result.booleanValue());
		
		parameter = fhirParameters.getByName("message").get();
		String message = (String) parameter.getValue();
		assertEquals("This is a test result", message);
		
		parameter = fhirParameters.getByName("match").get();
		Parameters matchParameters = (Parameters) parameter.getValue();
		parameter = matchParameters.getByName("equivalence").get();
		
		assertEquals("equal", ((Code) parameter.getValue()).getCodeValue());
	}
	
	@Test
	public void parameterizedTest() throws Exception {
		
		TranslateResult translateResult = TranslateResult.builder()
			.result(true)
			.build();
		
		Fhir fhirParameters = new Parameters.Fhir(translateResult);
		
		System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(fhirParameters));
		
		String expected ="{\"resourceType\":\"Parameters\","
				+ "\"parameter\":[{\"name\":\"result\",\"valueBoolean\":true}]}";
		Assert.assertEquals(expected, objectMapper.writeValueAsString(fhirParameters));
		
	}
	
}