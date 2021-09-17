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
package com.b2international.snowowl.fhir.tests.domain;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.b2international.snowowl.fhir.core.codesystems.ExtensionType;
import com.b2international.snowowl.fhir.core.model.Extension;
import com.b2international.snowowl.fhir.core.model.IntegerExtension;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Test for checking the serialization from model->JSON.
 * @since 6.3
 */
public class ExtensionTest extends FhirTest {
	
	@Test
	public void build() throws Exception {
		
		Extension<Integer> extension = IntegerExtension.builder().url("ID").value(1).build();
		
		printPrettyJson(extension);
		
		assertEquals(new Uri("ID"), extension.getUrl());
		assertEquals(Integer.valueOf(1), extension.getValue());
		assertEquals(ExtensionType.INTEGER, extension.getExtensionType());
		
	}
	
	@Test
	public void serialize() throws Exception {
		
		Extension<Integer> extension = IntegerExtension.builder().url("ID").value(1).build();
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(extension));
		
		assertThat(jsonPath.getString("url"), equalTo("ID"));
		assertThat(jsonPath.getInt("valueInteger"), equalTo(1));
	}
	
	
	@Test
	public void deserialize() throws Exception {
		
		Extension<Integer> extension = IntegerExtension.builder().url("ID").value(1).build();
		
		printPrettyJson(extension);
		
		Extension<?> readExtension = objectMapper.readValue(objectMapper.writeValueAsString(extension), Extension.class);
		
		assertTrue(readExtension instanceof IntegerExtension);
		
	}

}
