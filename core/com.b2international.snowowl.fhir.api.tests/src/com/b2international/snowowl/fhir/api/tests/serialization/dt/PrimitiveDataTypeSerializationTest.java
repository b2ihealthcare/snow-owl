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
package com.b2international.snowowl.fhir.api.tests.serialization.dt;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.b2international.snowowl.fhir.api.tests.FhirTest;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Id;
import com.b2international.snowowl.fhir.core.model.dt.Uri;

/**
 * 
 * Tests for primitive data type serialization
 * 
 * @see https://www.hl7.org/fhir/datatypes.html
 * @since 6.6
 */
public class PrimitiveDataTypeSerializationTest extends FhirTest {
	
	@Test
	public void codeTest() throws Exception {
		Code code = new Code("value");
		printPrettyJson(code);
		String expectedJson = "\"value\"";
		assertEquals(expectedJson, objectMapper.writeValueAsString(code));
	}
	
	@Test
	public void idTest() throws Exception {
		Id id = new Id("value");
		printPrettyJson(id);
		String expectedJson = "\"value\"";
		assertEquals(expectedJson, objectMapper.writeValueAsString(id));
	}
	
	@Test
	public void uriTest() throws Exception {
		Uri uri = new Uri("value");
		printPrettyJson(uri);
		String expectedJson = "\"value\"";
		assertEquals(expectedJson, objectMapper.writeValueAsString(uri));
	}

}
