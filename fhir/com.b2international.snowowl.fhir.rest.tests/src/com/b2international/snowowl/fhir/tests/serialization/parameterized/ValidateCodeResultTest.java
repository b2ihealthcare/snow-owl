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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.b2international.snowowl.fhir.core.model.ValidateCodeResult;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Fhir;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Json;
import com.b2international.snowowl.fhir.tests.FhirTest;

/**
 * Test for serializing the @see {@link ValidateCodeResult} class.
 * see CodeSystem-validate-code
 * 
 * @since 7.17.0
 */
public class ValidateCodeResultTest extends FhirTest {
	
	@Test
	public void okResultTest() {
		
		ValidateCodeResult result = ValidateCodeResult.builder()
				.okResult("Test")
				.build();
		
		assertTrue(result.getResult());
		
		result = ValidateCodeResult.builder()
				.okMessage()
				.build();
		
		assertFalse(result.getResult());
	}
	
	@Test
	public void fullCircleTest() throws Exception {
		
		ValidateCodeResult request = ValidateCodeResult.builder()
				.artefactNotFoundResult("http://hl7.org/fhir/issue-severity")
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
		ValidateCodeResult validateRequest = objectMapper.convertValue(json, ValidateCodeResult.class);
		System.out.println("... and back to the object representation we started from:" + validateRequest);
	}
	
}
