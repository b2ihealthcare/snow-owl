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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.b2international.snowowl.fhir.core.codesystems.ConceptMapEquivalence;
import com.b2international.snowowl.fhir.core.model.conceptmap.Match;
import com.b2international.snowowl.fhir.core.model.conceptmap.Product;
import com.b2international.snowowl.fhir.core.model.conceptmap.TranslateResult;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.FhirDataType;
import com.b2international.snowowl.fhir.core.model.dt.Parameter;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Fhir;
import com.b2international.snowowl.fhir.tests.FhirParameterMatcher;
import com.b2international.snowowl.fhir.tests.FhirTest;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.fhir.SnomedUri;
import com.jayway.restassured.path.json.JsonPath;

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
	public void falseResultTest() throws Exception {

		TranslateResult translateResult = TranslateResult.builder().build();
		
		Fhir fhirParameters = new Parameters.Fhir(translateResult);
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(fhirParameters));
		
		assertThat(jsonPath.getString("resourceType"), equalTo("Parameters"));
		assertThat(jsonPath.getList("parameter").size(), is(1));
		assertThat(jsonPath, FhirParameterMatcher.hasParameter("result", FhirDataType.BOOLEAN, false));
	}
	
	@Test
	public void validResultTest() throws Exception {
		
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
			.message("This is a test result")
			.addMatch(match)
			.build();
		
		Fhir fhirParameters = new Parameters.Fhir(translateResult);
		
		printPrettyJson(fhirParameters);
		
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
			.build();
		
		Fhir fhirParameters = new Parameters.Fhir(translateResult);
		
		printPrettyJson(fhirParameters);
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(fhirParameters));
		
		assertThat(jsonPath.getString("resourceType"), equalTo("Parameters"));
		assertThat(jsonPath.getList("parameter").size(), is(1));
		assertThat(jsonPath, FhirParameterMatcher.hasParameter("result", FhirDataType.BOOLEAN, false));
	}
	
}