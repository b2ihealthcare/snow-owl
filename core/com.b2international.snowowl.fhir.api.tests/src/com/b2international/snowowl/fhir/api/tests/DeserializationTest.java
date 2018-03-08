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

import java.io.IOException;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import com.b2international.snowowl.fhir.api.model.serialization.DeserializableLookupRequest;
import com.b2international.snowowl.fhir.api.model.serialization.SerializableLookupResult;
import com.b2international.snowowl.fhir.api.model.serialization.SerializableParameter;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;

public class DeserializationTest extends FhirTest {
	
	@Test
	public void lookupRequestTest() throws JsonParseException, JsonMappingException, IOException {
		String json = "{\"resourceType\":\"Parameters\","
				+ "\"parameter\":["
					+ "{\"name\":\"name\",\"valueString\":\"LOINC\"},"
					+ "{\"name\":\"version\",\"valueString\":\"2.48\"},"
					+ "{\"name\":\"designation\",\"valueString\":\"Bicarbonate[Moles/volume] in Serum\"},"
					+ "{\"name\":\"abstract\",\"valueString\":\"false\"},"
					+ "{\"name\":\"designation\",\"part\":["
						+ "{\"name\":\"value\",\"valueString\":\"Bicarbonate [Moles/volume] in Serum\"}"
						+ "]}"
					+ "]";
		
		String jsonMini = "{\"resourceType\":\"Parameters\","
				+ "\"parameter\":["
					+ "{\"name\":\"paramName\",\"valueString\":\"LOINC\"},"
					+ "{\"name\":\"version\",\"valueString\":\"20180131\"},"
					+ "{\"name\":\"abstract\",\"valueBoolean\":\"false\"}"
					+ "]}";
		
		DeserializableLookupRequest request = new ObjectMapper().readValue(jsonMini, DeserializableLookupRequest.class);
		Collection<SerializableParameter> parameters = request.getParameters();
		parameters.forEach(p -> {
			System.out.println(p);
			System.out.println(p.getType());
		});
	}
	
}

