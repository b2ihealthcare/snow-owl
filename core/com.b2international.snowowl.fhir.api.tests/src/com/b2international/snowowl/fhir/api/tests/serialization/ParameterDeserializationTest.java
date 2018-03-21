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
package com.b2international.snowowl.fhir.api.tests.serialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import com.b2international.snowowl.fhir.api.tests.FhirTest;
import com.b2international.snowowl.fhir.core.model.LookupRequest;
import com.b2international.snowowl.fhir.core.model.LookupResult;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.DateFormats;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.serialization.SerializableParameter;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * @since 6.3
 *
 */
public class ParameterDeserializationTest extends FhirTest {
	
	@Test
	public void stringParameterTest() throws Exception {
		
		String jsonParam = "{\"name\":\"paramName\",\"valueString\":\"LOINC\"}";
		
		SerializableParameter param = objectMapper.readValue(jsonParam, SerializableParameter.class);
		assertEquals("valueString", param.getType());
		assertEquals("LOINC", param.getValue());
		assertEquals(String.class, param.getValueType());
		System.out.println(param);
	}
	
	@Test
	public void booleanParameterTest() throws Exception {
		
		String jsonParam = "{\"name\":\"booleanParameter\",\"valueBoolean\":true}";
		
		SerializableParameter param = objectMapper.readValue(jsonParam, SerializableParameter.class);
		System.out.println(param);
		assertEquals("valueBoolean", param.getType());
		assertEquals(true, param.getValue());
		assertEquals(Boolean.class, param.getValueType());
	}
	
	@Test
	public void integerParameterTest() throws Exception {
		
		String jsonParam = "{\"name\":\"integerParameter\",\"valueInteger\":1}";
		
		SerializableParameter param = objectMapper.readValue(jsonParam, SerializableParameter.class);
		System.out.println(param);
		assertEquals("valueInteger", param.getType());
		assertEquals(1, param.getValue());
		assertEquals(Integer.class, param.getValueType());
	}
	
	@Test
	public void decimalParameterTest() throws Exception {
		
		String jsonParam = "{\"name\":\"decimalParameter\",\"valueDecimal\":1.21}";
		
		SerializableParameter param = objectMapper.readValue(jsonParam, SerializableParameter.class);
		System.out.println(param);
		assertEquals("valueDecimal", param.getType());
		assertEquals(1.21, param.getValue());
		assertEquals(Double.class, param.getValueType());
	}
	
	@Test
	public void codeParameterTest() throws Exception {
		
		String jsonParam = "{\"name\":\"codeParameter\",\"valueCode\":\"abcd\"}";
		
		SerializableParameter param = objectMapper.readValue(jsonParam, SerializableParameter.class);
		System.out.println(param);
		assertEquals("valueCode", param.getType());
		assertEquals(new Code("abcd"), param.getValue());
		assertEquals(Code.class, param.getValueType());
	}
	
	@Test
	public void arrayParameterTest() throws Exception {
		
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
				.filter(p -> p.getName().equals("language"))
				.findFirst();
		
		assertTrue(optionalParameter.isPresent());
	}
	
	@Test
	public void lookupParametersTest() throws Exception {
		
		String jsonMini = "{\"resourceType\":\"Parameters\","
				+ "\"parameter\":["
					+ "{\"name\":\"system\",\"valueUri\":\"LOINC\"},"
					+ "{\"name\":\"version\",\"valueString\":\"20180131\"},"
					+ "{\"name\":\"code\",\"valuecode\":\"1234\"}"
					+ "]}";
		
		LookupRequest request = objectMapper.readValue(jsonMini, LookupRequest.class);
		
		Optional<SerializableParameter> optionalParameter = request.getParameters().stream()
				.filter(p -> p.getName().equals("system"))
				.findFirst();
		assertTrue(optionalParameter.isPresent());
		SerializableParameter param = optionalParameter.get();
		assertEquals("valueUri", param.getType());
		assertEquals(new Uri("LOINC"), param.getValue());
		assertEquals(Uri.class, param.getValueType());
	}
	
	@Test
	public void lookupRequestTest() throws Exception {
		
		String jsonMini = "{\"resourceType\":\"Parameters\","
				+ "\"parameter\":["
					+ "{\"name\":\"code\",\"valueCode\":\"abcd\"},"
					+ "{\"name\":\"system\",\"valueUri\":\"http://snomed.info/sct\"},"
					+ "{\"name\":\"version\",\"valueString\":\"20180131\"},"
					+ "{\"name\":\"date\",\"valueDateTime\":\"2018-03-09T20:50:21+0100\"},"
					+ "{\"name\":\"coding\", \"valueCoding\":{\"code\":\"1234\","
							+ "\"system\":\"http://snomed.info/sct\","
							+ "\"version\":\"20180131\",\"userSelected\":false}}"
					+ "]}";
		
		LookupRequest request = objectMapper.readValue(jsonMini, LookupRequest.class);
		
		Optional<SerializableParameter> optionalParameter = request.getParameters().stream()
				.filter(p -> p.getName().equals("code")).findFirst();
		assertTrue(optionalParameter.isPresent());
		SerializableParameter param = optionalParameter.get();
		assertEquals("valueCode", param.getType());
		assertEquals(new Code("abcd"), param.getValue());
		assertEquals(Code.class, param.getValueType());
		
		System.out.println("Request: " + request);
		assertEquals(new Code("abcd"), request.getCode());
		assertEquals(new Uri("http://snomed.info/sct"), request.getSystem());
		assertEquals("20180131", request.getVersion());
		assertEquals(new SimpleDateFormat(DateFormats.DATE_TIME_FORMAT).parse("2018-03-09T20:50:21+0100"), request.getDate());
		assertEquals(new Code("1234"), request.getCoding().getCode());
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
			
			LookupResult parameterModel = objectMapper.readValue(json, LookupResult.class);
			String serializedModel = objectMapper.writeValueAsString(parameterModel);
			Assert.assertEquals(json, serializedModel);
		}
	
}

