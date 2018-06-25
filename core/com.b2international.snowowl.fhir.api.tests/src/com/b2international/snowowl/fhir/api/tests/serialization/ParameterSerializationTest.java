/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.junit.Assert;
import org.junit.Test;

import com.b2international.snowowl.fhir.api.tests.FhirTest;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Fhir;
import com.b2international.snowowl.fhir.core.model.dt.Property;
import com.b2international.snowowl.fhir.core.model.dt.SubProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @since 6.4
 */
public class ParameterSerializationTest extends FhirTest {
	
	@Test
	public void stringParameterTest() throws Exception {
		
		@SuppressWarnings("unused")
		@JsonPropertyOrder({"parameterName"})
		class StringTestParameterObject {
			
			private String parameterName = "test";

			public String getParameterName() {
				return parameterName;
			}

			public void setParameterName(String parameterName) {
				this.parameterName = parameterName;
			}
		}
		
		String expected = 
				"{\"resourceType\":\"Parameters\","
					+ "\"parameter\":["
					+ "{\"name\":\"parameterName\",\"valueString\":\"test\"}"
					+ "]"
				+ "}";
		
		Fhir fhirParameters = new Parameters.Fhir(new StringTestParameterObject());
		String serializedParameters = objectMapper.writeValueAsString(fhirParameters);
		printPrettyJson(serializedParameters);
		
		Assert.assertEquals(expected, serializedParameters);
	}
	
	@Test
	public void integerParameterTest() throws Exception {
		
		@SuppressWarnings("unused")
		@JsonPropertyOrder({"parameterName"})
		class Test {
			
			private Integer parameterName = 1;

			public Integer getParameterName() {
				return parameterName;
			}

			public void setParameterName(Integer parameterName) {
				this.parameterName = parameterName;
			}
			
		}
		
		String expected = 
				"{\"resourceType\":\"Parameters\","
					+ "\"parameter\":["
					+ "{\"name\":\"parameterName\",\"valueInteger\":1}"
					+ "]"
				+ "}";
		
		Fhir fhirParameters = new Parameters.Fhir(new Test());
		String serializedParameters = objectMapper.writeValueAsString(fhirParameters);
		printPrettyJson(serializedParameters);
		
		Assert.assertEquals(expected, serializedParameters);
	}
	
	/**
	 * The value of the property can be code | Coding | string | integer | boolean | dateTime
	 * @throws Exception
	 */
//	@Test
//	public void propertyValueTypeTest() throws Exception {
//
//		Builder basePropertyBuilder = Property.builder()
//			.code("123");
//		
//		
//		PropertyConverter converter = new PropertyConverter();
//		//value = null
//		Collection<SerializableParameter> parameters = converter.convert(basePropertyBuilder.build()).getParameters();
//		String jsonString = objectMapper.writeValueAsString(parameters);
//		
//		String expected = "[{\"name\":\"code\",\"valueCode\":\"123\"}]";
//		Assert.assertEquals(expected, jsonString);
//		
//		parameters = converter.convert(basePropertyBuilder.value(true).build()).getParameters();
//		jsonString = objectMapper.writeValueAsString(parameters);
//		
//		expected = "[{\"name\":\"code\",\"valueCode\":\"123\"},"
//				+ "{\"name\":\"value\",\"valueBoolean\":true}]";
//		Assert.assertEquals(expected, jsonString);
//		
//		parameters = converter.convert(basePropertyBuilder.value(1).build()).getParameters();
//		jsonString = objectMapper.writeValueAsString(parameters);
//		
//		expected = "[{\"name\":\"code\",\"valueCode\":\"123\"},"
//				+ "{\"name\":\"value\",\"valueInteger\":1}]";
//		
//		parameters = converter.convert(basePropertyBuilder.value(1l).build()).getParameters();
//		jsonString = objectMapper.writeValueAsString(parameters);
//		
//		expected = "[{\"name\":\"code\",\"valueCode\":\"123\"},"
//				+ "{\"name\":\"value\",\"valueDecimal\":1}]";
//		Assert.assertEquals(expected, jsonString);
//		
//		parameters = converter.convert(basePropertyBuilder.value("test").build()).getParameters();
//		jsonString = objectMapper.writeValueAsString(parameters);
//		
//		expected = "[{\"name\":\"code\",\"valueCode\":\"123\"},"
//				+ "{\"name\":\"value\",\"valueString\":\"test\"}]";
//		Assert.assertEquals(expected, jsonString);
//		
//		parameters = converter.convert(basePropertyBuilder.value(new Code("code")).build()).getParameters();
//		jsonString = objectMapper.writeValueAsString(parameters);
//		
//		expected = "[{\"name\":\"code\",\"valueCode\":\"123\"},"
//				+ "{\"name\":\"value\",\"valueCode\":\"code\"}]";
//		Assert.assertEquals(expected, jsonString);
//		
//		System.out.println(jsonString);
//	}
	
//	@Test
//	public void designationTest() throws Exception {
//		
//		Coding coding = Coding.builder()
//			.code("1234")
//			.system("http://snomed.info/sct")
//			.version("20180131")
//			.build();
//		
//		ParameterizedDesignation designation = ParameterizedDesignation.builder()
//			.languageCode("en_uk")
//			.use(coding)
//			.value("dValue")
//			.build();
//			
//		ParametersModel parametersModel = new SerializableParametersConverter().convert(designation);
//		
//		List<SerializableParameter> parameters = parametersModel.getParameters();
//		printPrettyJson(parameters);
//		
//		String jsonString = objectMapper.writeValueAsString(parameters);
//		
//		String expected = "[{\"name\":\"language\",\"valueCode\":\"en_uk\"},"
//				+ "{\"name\":\"use\","
//				+ "\"valueCoding\":{\"code\":\"1234\","
//				+ "\"system\":\"http://snomed.info/sct\","
//				+ "\"version\":\"20180131\",\"userSelected\":false}},"
//				+ "{\"name\":\"value\",\"valueString\":\"dValue\"}]";
//		
//		Assert.assertEquals(expected, jsonString);
//	}
	
//	@Test
//	public void lookupResultTest() throws Exception {
//		LookupResult lookupResult = LookupResult.builder()
//			.name("test")
//			.addDesignation(ParameterizedDesignation.builder()
//					.value("dValue")
//					.languageCode("uk").build())
//			.addProperty(Property.builder()
//					.code("1234")
//					.description("propDescription")
//					.value("sds")
//					.addSubProperty(SubProperty.builder()
//						.code("subCode")
//						.description("subDescription")
//						.value(1)
//						.build())
//					.build())
//			.build();
//		
//		LookupResultConverter resultConverter = new LookupResultConverter();
//		ParametersModel paramtersModel = resultConverter.convert(lookupResult);
//		
//		printJson(paramtersModel);
//		printPrettyJson(paramtersModel);
//	}
	
//	@Test
//	public void subPropertyTest() throws Exception {
//		
//		SerializableParametersConverter converter = new SerializableParametersConverter();
//
//		Collection<SerializableParameter> parameters = converter.convert(SubProperty.builder()
//			.code("123")
//			.value(2.1)
//			.description("propertyDescription")
//			.build()).getParameters();
//		
//		printPrettyJson(parameters);
//		
//		String jsonString = objectMapper.writeValueAsString(parameters);
//		System.out.println(jsonString);
//		String expected = "[{\"name\":\"code\",\"valueCode\":\"123\"},"
//				+ "{\"name\":\"value\",\"valueDecimal\":2.1},"
//				+ "{\"name\":\"description\",\"valueString\":\"propertyDescription\"}]";
//		
//		Assert.assertEquals(expected, jsonString);
//	}
	
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
