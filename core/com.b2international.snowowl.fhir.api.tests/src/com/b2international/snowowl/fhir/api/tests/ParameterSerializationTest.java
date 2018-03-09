/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.junit.Assert;
import org.junit.Test;

import com.b2international.snowowl.fhir.api.model.serialization.SerializableParameter;

public class ParameterSerializationTest extends FhirTest {
	
	@Test
	public void parameterTest() throws Exception {
		SerializableParameter parameter = new SerializableParameter("parameterName", "valueString", "test");
		
		printPrettyJson(parameter);
		
		String expected = "{\"name\":\"parameterName\",\"valueString\":\"test\"}";
		Assert.assertEquals(expected, objectMapper.writeValueAsString(parameter));
	}
	
//	@Test
//	public void fhirLookupResultsTest() throws Exception {
//		LookupResult lookupResults = new LookupResult();
//		SerializableParameter parameter = new SerializableParameter("fieldName", "type", "value");
//		lookupResults.add(parameter);
//		parameter = new SerializableParameter("fieldName2", "type2", "value2");
//		lookupResults.add(parameter);
//		
//		Collection<SerializableParameter> designationParameters = Sets.newHashSet();
//		SerializableParameter designationParameter = new SerializableParameter("dFieldName2", "dType2", "dValue2");
//		designationParameters.add(designationParameter);
//
//		SerializableParameter dParameter = new SerializableParameter("designation", "part", designationParameters);
//		lookupResults.add(dParameter);
//		
//		String expectedJson = "{\"resourceType\":\"Parameters\","
//				+ "\"parameter\":[{\"name\":\"fieldName\",\"type\":\"value\"},"
//				+ "{\"name\":\"fieldName2\",\"type2\":\"value2\"},"
//				+ "{\"name\":\"designation\",\"part\":["
//					+ "{\"name\":\"dFieldName2\",\"dType2\":\"dValue2\"}]"
//					+ "}"
//				+ "]}";
//		
//		Assert.assertEquals(expectedJson, objectMapper.writeValueAsString(lookupResults));
//	}
	
}
