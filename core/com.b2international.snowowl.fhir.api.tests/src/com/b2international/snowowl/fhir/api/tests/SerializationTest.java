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

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import com.b2international.snowowl.fhir.api.model.serialization.SerializableLookupResult;
import com.b2international.snowowl.fhir.api.model.serialization.SerializableParameter;
import com.google.common.collect.Sets;

public class SerializationTest extends FhirTest {
	
	//@Test
	public void fhirParameterTest() throws Exception {
		SerializableParameter parameter = new SerializableParameter("parameterName", "valueString", "test");
		
		printPrettyJson(parameter);
		
		String expected = "{\"name\":\"parameterName\",\"valueString\":\"test\"}";
		Assert.assertEquals(expected, objectMapper.writeValueAsString(parameter));
	}
	
	@Test
	public void fhirLookupResultsTest() throws Exception {
		SerializableLookupResult lookupResults = new SerializableLookupResult();
		SerializableParameter parameter = new SerializableParameter("fieldName", "type", "value");
		lookupResults.add(parameter);
		parameter = new SerializableParameter("fieldName2", "type2", "value2");
		lookupResults.add(parameter);
		
		Collection<SerializableParameter> designationParameters = Sets.newHashSet();
		SerializableParameter designationParameter = new SerializableParameter("dFieldName2", "dType2", "dValue2");
		designationParameters.add(designationParameter);

		SerializableParameter dParameter = new SerializableParameter("designation", "part", designationParameters);
		lookupResults.add(dParameter);
		printPrettyJson(lookupResults);
	}
	
	//@Test
	public void lookupResultsTest3() throws Exception {
		SerializableLookupResult lookupResults = new SerializableLookupResult();
		SerializableParameter parameter = new SerializableParameter("fieldName", "type", "value");
		lookupResults.add(parameter);
		parameter = new SerializableParameter("fieldName2", "type2", "value2");
		lookupResults.add(parameter);
		
		Collection<SerializableParameter> designationParameters = Sets.newHashSet();
		SerializableParameter designationParameter = new SerializableParameter("dFieldName", "dType", "dValue");
		designationParameters.add(designationParameter);
		designationParameter = new SerializableParameter("dFieldName2", "dType2", "dValue2");
		designationParameters.add(designationParameter);
		
		SerializableParameter lookupParameter = new SerializableParameter("designation", "part", designationParameters);
		lookupResults.add(lookupParameter);
		printPrettyJson(lookupResults);
	}
	
	//@Test
	public void lookupResultsTest2() throws Exception {
		
		//LookupResult lookupResults = new LookupResult();
		
	}
	
}
