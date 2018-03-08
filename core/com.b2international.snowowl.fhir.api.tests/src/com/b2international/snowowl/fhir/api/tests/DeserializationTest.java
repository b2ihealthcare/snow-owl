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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import com.b2international.snowowl.fhir.api.model.serialization.DeserializableLookupRequest;
import com.b2international.snowowl.fhir.api.model.serialization.SerializableLookupResult;
import com.b2international.snowowl.fhir.api.model.serialization.SerializableParameter;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;

public class DeserializationTest extends FhirTest {
	
	@Test
	public void parameterTest() throws Exception {
		
		String jsonParam = "{\"name\":\"paramName\",\"valueString\":\"LOINC\"}";
		
		SerializableParameter param = objectMapper.readValue(jsonParam, SerializableParameter.class);
		assertEquals("valueString", param.getType());
		assertEquals("LOINC", param.getValue());
		assertEquals(String.class, param.getValueType());
	}
	
	@Test
	public void parameterWithPartsTest() throws Exception {
		
		String jsonParams = "[{\"name\":\"paramName\",\"valueString\":\"LOINC\"},"
						 + "{\"name\":\"designation\",\"part\":["
				   			+ "{\"name\":\"value\",\"valueString\":\"Bicarbonate [Moles/volume] in Serum\"}"
				   			+ "]"
						+ "}]";
		
		List<SerializableParameter> params = objectMapper.readValue(jsonParams, new TypeReference<Collection<SerializableParameter>>(){});
		
		Optional<SerializableParameter> optionalParameter = params.stream()
				.filter(p -> p.getName().equals("paramName"))
				.findFirst();
		
		assertTrue(optionalParameter.isPresent());
		
		SerializableParameter param = optionalParameter.get();

		assertEquals("valueString", param.getType());
		assertEquals("LOINC", param.getValue());
		assertEquals(String.class, param.getValueType());
		
		optionalParameter = params.stream()
				.filter(p -> p.getName().equals("designation"))
				.findFirst();
		
		assertTrue(optionalParameter.isPresent());
		param = optionalParameter.get();
		
		System.out.println(param);
		assertEquals("part", param.getType());
		
		@SuppressWarnings("unchecked")
		Collection<SerializableParameter> embeddedParams = (Collection) param.getValue();
		Optional<SerializableParameter> optionalEmbeddedParameter = embeddedParams.stream()
				.filter(p -> p.getName().equals("value"))
				.findFirst();
		
		assertTrue(optionalEmbeddedParameter.isPresent());
		
		SerializableParameter embeddedParam = optionalEmbeddedParameter.get();
		
		assertEquals("Bicarbonate [Moles/volume] in Serum", embeddedParam.getValue());
		assertTrue(Collection.class.isAssignableFrom(param.getValueType()));
		
		System.out.println(params);
	}
	
	@Test
	public void parameterWithCodingTest() throws Exception {
		
		String jsonParams = "[{\"name\":\"language\",\"valueCode\":\"en_uk\"},"
							+ "{\"name\":\"use\", \"valueCoding\":{\"code\":\"1234\","
								+ "\"system\":\"http://snomed.info/sct\","
								+ "\"version\":\"20180131\",\"userSelected\":false}}]";
		
		List<SerializableParameter> params = objectMapper.readValue(jsonParams, new TypeReference<Collection<SerializableParameter>>(){});
		
		System.out.println(params);
		
		Optional<SerializableParameter> optionalParameter = params.stream()
				.filter(p -> p.getName().equals("paramName"))
				.findFirst();
		
		assertTrue(optionalParameter.isPresent());
		
		SerializableParameter param = optionalParameter.get();

		assertEquals("valueString", param.getType());
		assertEquals("LOINC", param.getValue());
		assertEquals(String.class, param.getValueType());
		
		optionalParameter = params.stream()
				.filter(p -> p.getName().equals("designation"))
				.findFirst();
		
		assertTrue(optionalParameter.isPresent());
		param = optionalParameter.get();
		
		System.out.println(param);
		assertEquals("part", param.getType());
		
		@SuppressWarnings("unchecked")
		Collection<SerializableParameter> embeddedParams = (Collection) param.getValue();
		Optional<SerializableParameter> optionalEmbeddedParameter = embeddedParams.stream()
				.filter(p -> p.getName().equals("value"))
				.findFirst();
		
		assertTrue(optionalEmbeddedParameter.isPresent());
		
		SerializableParameter embeddedParam = optionalEmbeddedParameter.get();
		
		assertEquals("Bicarbonate [Moles/volume] in Serum", embeddedParam.getValue());
		assertTrue(Collection.class.isAssignableFrom(param.getValueType()));
		
		System.out.println(params);
	}
	
	
	@Test
	public void lookupRequestTest() throws JsonParseException, JsonMappingException, IOException {
		
		String jsonMini = "{\"resourceType\":\"Parameters\","
				+ "\"parameter\":["
					+ "{\"name\":\"paramName\",\"valueString\":\"LOINC\"},"
					+ "{\"name\":\"version\",\"valueString\":\"20180131\"},"
					+ "{\"name\":\"abstract\",\"valueBoolean\":\"false\"}"
					+ "]}";
		
		DeserializableLookupRequest request = objectMapper.readValue(jsonMini, DeserializableLookupRequest.class);
		Collection<SerializableParameter> parameters = request.getParameters();
		
		Optional<SerializableParameter> optionalParameter = request.getParameters().stream().filter(p -> p.getName().equals("paramName")).findFirst();
		assertTrue(optionalParameter.isPresent());
		SerializableParameter param = optionalParameter.get();
		assertEquals("valueString", param.getType());
		assertEquals("LOINC", param.getValue());
		assertEquals(String.class, param.getValueType());
		
		//assertEquals(, actual);
		
		
		parameters.forEach(p -> {
			System.out.println(p);
			System.out.println(p.getValueType());
		});
	}
	
	@Test
	public void lookupRequestTest2() throws JsonParseException, JsonMappingException, IOException {
		String json = "{\"resourceType\":\"Parameters\","
				+ "\"parameter\":["
					+ "{\"name\":\"name\",\"valueString\":\"LOINC\"},"
					+ "{\"name\":\"designation\",\"part\":["
						+ "{\"name\":\"value\",\"valueString\":\"Bicarbonate [Moles/volume] in Serum\"},"
						+ "{\"name\":\"language\",\"valueString\":\"en_uk\"}"
						+ "]}"
					+ "]}";
		
		DeserializableLookupRequest request = objectMapper.readValue(json, DeserializableLookupRequest.class);
		Collection<SerializableParameter> parameters = request.getParameters();
		parameters.forEach(p -> {
			System.out.println(p);
			//System.out.println(p.getType());
		});
	}
	
	@Test
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

