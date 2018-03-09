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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import com.b2international.snowowl.fhir.api.model.LookupRequest;
import com.b2international.snowowl.fhir.api.model.dt.Code;
import com.b2international.snowowl.fhir.api.model.serialization.SerializableLookupResult;
import com.b2international.snowowl.fhir.api.model.serialization.SerializableParameter;

public class ModelDeserializationTest extends FhirTest {
	
	@Test
	public void lookupRequestTest() throws Exception {
		
		String jsonMini = "{\"resourceType\":\"Parameters\","
				+ "\"parameter\":["
					+ "{\"name\":\"code\",\"valueCode\":\"abcd\"}"
					+ "]}";
		
		LookupRequest request = objectMapper.readValue(jsonMini, LookupRequest.class);
		
		Optional<SerializableParameter> optionalParameter = request.getParameters().stream()
				.filter(p -> p.getName().equals("code")).findFirst();
		assertTrue(optionalParameter.isPresent());
		SerializableParameter param = optionalParameter.get();
		assertEquals("valueCode", param.getType());
		assertEquals(new Code("abcd"), param.getValue());
		assertEquals(Code.class, param.getValueType());
		
		request.toModelObject();
		System.out.println("Request: " + request);
	}
	
	//@Test
	public void lookupRoundTrip() throws Exception {
		String json = "{\"resourceType\":\"Parameters\","
				+ "\"parameter\":["
					+ "{\"name\":\"name\",\"valueString\":\"LOINC\"},"
					+ "{\"name\":\"designation\",\"part\":["
						+ "{\"name\":\"value\",\"valueString\":\"Bicarbonate [Moles/volume] in Serum\"},"
						+ "{\"name\":\"language\",\"valueString\":\"en_uk\"}"
						+ "]}"
					+ "]}";
		
		SerializableLookupResult parameterModel = objectMapper.readValue(json, SerializableLookupResult.class);
		String serializedModel = objectMapper.writeValueAsString(parameterModel);
		Assert.assertEquals(json, serializedModel);
	}

}
