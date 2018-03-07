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
package com.b2international.snowowl.fhir.api.tests;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import com.b2international.snowowl.fhir.api.model.Coding;
import com.b2international.snowowl.fhir.api.model.Designation;
import com.b2international.snowowl.fhir.api.model.LookupResult;
import com.b2international.snowowl.fhir.api.model.serialization.FhirLookupResult;
import com.b2international.snowowl.fhir.api.model.serialization.FhirParameter;

public class ModelSerializationTest extends FhirTest {
	
	@Test
	public void designationTest() throws Exception {
		Coding coding = new Coding("1234", "http://snomed.info/sct", "20180131");
		
		Collection<FhirParameter> designationParams = Designation.builder()
				.langaugeCode("en_uk")
				.use(coding)
				.value("dValue")
				.build().toSerializedBean();
		
		printJson(designationParams);
		
		String jsonString = objectMapper.writeValueAsString(designationParams);
		
		String expected = "[{\"name\":\"language\",\"valueString\":\"en_uk\"},"
				+ "{\"name\":\"use\","
				+ "\"valueCoding\":{\"code\":\"1234\","
				+ "\"system\":\"http://snomed.info/sct\","
				+ "\"version\":\"20180131\",\"userSelected\":false}},"
				+ "{\"name\":\"value\",\"valueString\":\"dValue\"}]";
		
		Assert.assertEquals(expected, jsonString);
	}
	
	@Test
	public void lookupResultTest() throws Exception {
		FhirLookupResult fhirLookupResult = LookupResult.builder()
				.name("name")
				.addDesignation(Designation.builder().langaugeCode("dsds").build())
				.buildSerializableBean();
		
		printJson(fhirLookupResult);
	}

}
