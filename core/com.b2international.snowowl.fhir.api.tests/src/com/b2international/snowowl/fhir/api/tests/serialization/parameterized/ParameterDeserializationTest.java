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
package com.b2international.snowowl.fhir.api.tests.serialization.parameterized;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Optional;

import org.junit.Test;

import com.b2international.snowowl.fhir.api.tests.FhirTest;
import com.b2international.snowowl.fhir.core.FhirConstants;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.FhirDataType;
import com.b2international.snowowl.fhir.core.model.dt.Parameter;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Fhir;
import com.b2international.snowowl.fhir.core.model.lookup.LookupRequest;

/**
 * Parameter deserialization tests.
 * @since 6.6
 */
public class ParameterDeserializationTest extends FhirTest {
	
	@Test
	public void stringParameterTest() throws Exception {
		
		String jsonParam = "{\"resourceType\":\"Parameters\"," + 
				"\"parameter\":["
					+ "{\"name\":\"paramName\",\"valueString\":\"LOINC\"}"
				+ "]}";
		
		assertParameter(jsonParam, "paramName", FhirDataType.STRING, "LOINC");
	}
	
	@Test
	public void booleanParameterTest() throws Exception {
		
		String jsonParam = "{\"resourceType\":\"Parameters\"," + 
				"\"parameter\":["
					+ "{\"name\":\"paramName\",\"valueBoolean\":true}"
				+ "]}";
		
		
		assertParameter(jsonParam, "paramName", FhirDataType.BOOLEAN, true);
	}
	
	@Test
	public void integerParameterTest() throws Exception {
		
		String jsonParam = "{\"resourceType\":\"Parameters\"," + 
				"\"parameter\":["
					+ "{\"name\":\"paramName\",\"valueInteger\":1}"
				+ "]}";
		
		assertParameter(jsonParam, "paramName", FhirDataType.INTEGER, 1);
	}
	
	@Test
	public void decimalParameterTest() throws Exception {
		
		String jsonParam = "{\"resourceType\":\"Parameters\"," + 
				"\"parameter\":["
					+ "{\"name\":\"paramName\",\"valueDecimal\":1.21}"
				+ "]}";
		
		assertParameter(jsonParam, "paramName", FhirDataType.DECIMAL, 1.21);
	}
	
	//@Test
	public void decimalParameterTest2() throws Exception {
		
		String jsonParam = "{\"resourceType\":\"Parameters\"," + 
				"\"parameter\":["
					+ "{\"name\":\"paramName\",\"valueDecimal\":1.21}"
				+ "]}";
		
		assertParameter(jsonParam, "paramName", FhirDataType.DECIMAL, 1.21f);
	}
	
	@Test
	public void codeParameterTest() throws Exception {
		
		String jsonParam = "{\"resourceType\":\"Parameters\"," + 
				"\"parameter\":["
					+ "{\"name\":\"paramName\",\"valueCode\":\"code\"}"
				+ "]}";
		
		assertParameter(jsonParam, "paramName", FhirDataType.CODE, "code");
	}
	
	@Test
	public void codingParameterTest() throws Exception {
		
		String jsonParam = "{\"resourceType\":\"Parameters\","
				+ "\"parameter\":["
						+ "{\"name\":\"use\", \"valueCoding\":{\"code\":\"1234\","
							+ "\"system\":\"http://snomed.info/sct\","
							+ "\"version\":\"20180131\",\"userSelected\":false}"
					+ "}]}";
		
		Fhir fhirParameters = objectMapper.readValue(jsonParam, Parameters.Fhir.class);
		
		Optional<Parameter> parameterOptional = fhirParameters.getParameters().stream().findFirst();
		
		assertTrue(parameterOptional.isPresent());
		
		Parameter parameter = parameterOptional.get();
		
		assertEquals("use", parameter.getName());
		assertEquals(FhirDataType.CODING, parameter.getType());
		Coding coding = (Coding) parameter.getValue();
		
		assertEquals("1234", coding.getCodeValue());
		assertEquals("http://snomed.info/sct", coding.getSystemValue());
		assertEquals("20180131", coding.getVersion());
		assertEquals(false, coding.isUserSelected());
	}
	
	@Test
	public void arrayParameterTest() throws Exception {
		
		String jsonParam = "{\"resourceType\":\"Parameters\","
					+ "\"parameter\":["
						 + "{\"name\":\"designation\",\"part\":["
				   			+ "{\"name\":\"value\",\"valueString\":\"Bicarbonate [Moles/volume] in Serum\"}"
				   			+ "]"
						+ "}]}";
		
		Fhir fhirParameters = objectMapper.readValue(jsonParam, Parameters.Fhir.class);
		
		Optional<Parameter> parameterOptional = fhirParameters.getParameters().stream().findFirst();
		
		assertTrue(parameterOptional.isPresent());
		
		Parameter parameter = parameterOptional.get();
		
		assertEquals("designation", parameter.getName());
		assertEquals(FhirDataType.PART, parameter.getType());
		
		Parameters parameters = (Parameters) parameter.getValue();
		Parameter designationParamater = parameters.getParameters().stream().findFirst().get();
		
		assertEquals("value", designationParamater.getName());
		assertEquals(FhirDataType.STRING, designationParamater.getType());
		assertEquals("Bicarbonate [Moles/volume] in Serum", designationParamater.getValue());
	}
	
	@Test
	public void lookupRequestParametersTest() throws Exception {
		
		String jsonParam = "{\"resourceType\":\"Parameters\","
				+ "\"parameter\":["
					+ "{\"name\":\"system\",\"valueUri\":\"LOINC\"},"
					+ "{\"name\":\"version\",\"valueString\":\"20180131\"},"
					+ "{\"name\":\"code\",\"valueCode\":\"1234\"}"
					+ "]}";
		
		//Magic to turn the FHIR params -> Parameters -> LookupRequest
		Parameters.Fhir fhirParameters = objectMapper.readValue(jsonParam, Parameters.Fhir.class);
		LookupRequest lookupRequest = objectMapper.convertValue(fhirParameters.toJson(), LookupRequest.class);
		
		assertEquals("LOINC", lookupRequest.getSystem());
		assertEquals("20180131", lookupRequest.getVersion());
		assertEquals("1234", lookupRequest.getCode());
		assertTrue(lookupRequest.getProperties().isEmpty());
	}
	
	@Test
	public void lookupRequestCodingTest() throws Exception {
		
		String jsonParam = "{\"resourceType\":\"Parameters\","
				+ "\"parameter\":["
					+ "{\"name\":\"code\",\"valueCode\":\"1234\"},"
					+ "{\"name\":\"system\",\"valueUri\":\"http://snomed.info/sct\"},"
					+ "{\"name\":\"version\",\"valueString\":\"20180131\"},"
					+ "{\"name\":\"date\",\"valueDateTime\":\"2018-03-09T20:50:21+0100\"},"
					+ "{\"name\":\"displayLanguage\",\"valueCode\":\"us-en\"},"
					+ "{\"name\":\"property\",\"valueCode\":\"prop1\"},"
					+ "{\"name\":\"property\",\"valueCode\":\"prop2\"},"
					+ "{\"name\":\"coding\", \"valueCoding\":"
							+ "{\"code\":\"1234\","
							+ "\"system\":\"http://snomed.info/sct\","
							+ "\"version\":\"20180131\",\"userSelected\":false}}"
					+ "]}";
		
		//Magic to turn the FHIR params -> Parameters -> LookupRequest
		Parameters.Fhir fhirParameters = objectMapper.readValue(jsonParam, Parameters.Fhir.class);
		LookupRequest lookupRequest = objectMapper.convertValue(fhirParameters.toJson(), LookupRequest.class);
		
		assertEquals("1234", lookupRequest.getCode());
		assertEquals("http://snomed.info/sct", lookupRequest.getSystem());
		assertEquals("20180131", lookupRequest.getVersion());
		
		DateFormat df = new SimpleDateFormat(FhirConstants.DATE_TIME_FORMAT);
		assertEquals(df.parse("2018-03-09T20:50:21+0100"), lookupRequest.getDate());
		
		assertEquals("us-en", lookupRequest.getDisplayLanguage());
		
		Coding coding = lookupRequest.getCoding();
		assertEquals("1234", coding.getCodeValue());
		assertEquals("http://snomed.info/sct", coding.getSystemValue());
		assertEquals("20180131", coding.getVersion());
		assertEquals(false, coding.isUserSelected());
		
		assertFalse(lookupRequest.getProperties().isEmpty());
		Collection<String> properties = lookupRequest.getProperties();
		assertTrue(properties.stream().filter(p -> p.equals("prop1")).findFirst().isPresent());
	}

	private void assertParameter(String jsonParam, String paramName, FhirDataType fhirDataType, Object paramValue) throws Exception {

		Fhir fhirParameters = objectMapper.readValue(jsonParam, Parameters.Fhir.class);
		
		Optional<Parameter> parameterOptional = fhirParameters.getParameters().stream().findFirst();
		
		assertTrue(parameterOptional.isPresent());
		
		Parameter parameter = parameterOptional.get();
		
		assertEquals(paramName, parameter.getName());
		assertEquals(fhirDataType, parameter.getType());
		assertEquals(paramValue, parameter.getValue());
		
	}
	
}

