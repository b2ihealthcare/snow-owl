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
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import com.b2international.snowowl.fhir.api.tests.FhirTest;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.FhirDataType;
import com.b2international.snowowl.fhir.core.model.dt.Parameter;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Fhir;
import com.b2international.snowowl.fhir.core.model.lookup.LookupResult;

/**
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
	
//	@Test
//	public void lookupParametersTest() throws Exception {
//		
//		String jsonMini = "{\"resourceType\":\"Parameters\","
//				+ "\"parameter\":["
//					+ "{\"name\":\"system\",\"valueUri\":\"LOINC\"},"
//					+ "{\"name\":\"version\",\"valueString\":\"20180131\"},"
//					+ "{\"name\":\"code\",\"valuecode\":\"1234\"}"
//					+ "]}";
//		
//		LookupRequest request = objectMapper.readValue(jsonMini, LookupRequest.class);
//		
//		Optional<SerializableParameter> optionalParameter = request.getParameters().stream()
//				.filter(p -> p.getName().equals("system"))
//				.findFirst();
//		assertTrue(optionalParameter.isPresent());
//		SerializableParameter param = optionalParameter.get();
//		assertEquals("valueUri", param.getType());
//		assertEquals(new Uri("LOINC"), param.getValue());
//		assertEquals(Uri.class, param.getValueType());
//	}
	
//	@Test
//	public void lookupRequestTest() throws Exception {
//		
//		String jsonMini = "{\"resourceType\":\"Parameters\","
//				+ "\"parameter\":["
//					+ "{\"name\":\"code\",\"valueCode\":\"abcd\"},"
//					+ "{\"name\":\"system\",\"valueUri\":\"http://snomed.info/sct\"},"
//					+ "{\"name\":\"version\",\"valueString\":\"20180131\"},"
//					+ "{\"name\":\"date\",\"valueDateTime\":\"2018-03-09T20:50:21+0100\"},"
//					+ "{\"name\":\"coding\", \"valueCoding\":{\"code\":\"1234\","
//							+ "\"system\":\"http://snomed.info/sct\","
//							+ "\"version\":\"20180131\",\"userSelected\":false}}"
//					+ "]}";
//		
//		LookupRequest request = objectMapper.readValue(jsonMini, LookupRequest.class);
//		
//		Optional<SerializableParameter> optionalParameter = request.getParameters().stream()
//				.filter(p -> p.getName().equals("code")).findFirst();
//		assertTrue(optionalParameter.isPresent());
//		SerializableParameter param = optionalParameter.get();
//		assertEquals("valueCode", param.getType());
//		assertEquals(new Code("abcd"), param.getValue());
//		assertEquals(Code.class, param.getValueType());
//		
//		System.out.println("Request: " + request);
//		assertEquals(new Code("abcd"), request.getCode());
//		assertEquals(new Uri("http://snomed.info/sct"), request.getSystem());
//		assertEquals("20180131", request.getVersion());
//		assertEquals(new SimpleDateFormat(FhirConstants.DATE_TIME_FORMAT).parse("2018-03-09T20:50:21+0100"), request.getDate());
//		assertEquals(new Code("1234"), request.getCoding().getCode());
//	}
	
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

