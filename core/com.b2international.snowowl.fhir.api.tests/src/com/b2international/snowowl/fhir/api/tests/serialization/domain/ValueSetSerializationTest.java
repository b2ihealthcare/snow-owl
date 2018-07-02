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
package com.b2international.snowowl.fhir.api.tests.serialization.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.b2international.snowowl.fhir.api.tests.FhirTest;
import com.b2international.snowowl.fhir.core.model.Extension;
import com.b2international.snowowl.fhir.core.model.IntegerExtension;

/**
 * Test for checking the valueset serialization
 * @since 6.3
 */
public class ValueSetSerializationTest extends FhirTest {
	
	@Test
	public void extensionTest() throws Exception {
		
		Extension<Integer> integerExtension = new IntegerExtension("testUri", 1);
				
		printPrettyJson(integerExtension);
		
		String expectedJson =  "{\"url\":\"testUri\","
					+ "\"valueInteger\":1}";
		
		assertEquals(expectedJson, objectMapper.writeValueAsString(integerExtension));
	}

}
