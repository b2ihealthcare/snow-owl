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
package com.b2international.snowowl.fhir.tests.dt;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Id;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.tests.FhirTest;

/**
 * Tests for primitive data types
 * 
 * @see https://www.hl7.org/fhir/datatypes.html
 * @since 6.6
 */
public class PrimitiveDataTypeTest extends FhirTest {
	
	@Test
	public void serializeCode() throws Exception {
		Code code = new Code("value");
		String expectedJson = "\"value\"";
		assertEquals(expectedJson, objectMapper.writeValueAsString(code));
	}
	
	@Test
	public void serializeId() throws Exception {
		Id id = new Id("value");
		String expectedJson = "\"value\"";
		assertEquals(expectedJson, objectMapper.writeValueAsString(id));
	}
	
	@Test
	public void serializeUri() throws Exception {
		Uri uri = new Uri("value");
		String expectedJson = "\"value\"";
		assertEquals(expectedJson, objectMapper.writeValueAsString(uri));
	}
	
	

}
