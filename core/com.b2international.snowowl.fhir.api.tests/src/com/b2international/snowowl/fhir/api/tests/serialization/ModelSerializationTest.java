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

import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.b2international.snowowl.fhir.api.tests.FhirTest;
import com.b2international.snowowl.fhir.core.codesystems.IssueSeverity;
import com.b2international.snowowl.fhir.core.codesystems.IssueType;
import com.b2international.snowowl.fhir.core.model.Designation;
import com.b2international.snowowl.fhir.core.model.Issue;
import com.b2international.snowowl.fhir.core.model.LookupResult;
import com.b2international.snowowl.fhir.core.model.LookupResultConverter;
import com.b2international.snowowl.fhir.core.model.OperationOutcome;
import com.b2international.snowowl.fhir.core.model.ParametersModel;
import com.b2international.snowowl.fhir.core.model.Property;
import com.b2international.snowowl.fhir.core.model.Property.Builder;
import com.b2international.snowowl.fhir.core.model.PropertyConverter;
import com.b2international.snowowl.fhir.core.model.SerializableParametersConverter;
import com.b2international.snowowl.fhir.core.model.SubProperty;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.serialization.SerializableParameter;

/**
 * 
 * Test for checking the serialization from model->JSON.
 * @since 6.3
 */
public class ModelSerializationTest extends FhirTest {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void operationOutcomeTest() throws Exception {
		OperationOutcome ou = new OperationOutcome();
		
		Issue issue = Issue.builder()
				.severity(IssueSeverity.ERROR)
				.code(IssueType.REQUIRED)
				.build();
		System.out.println("Issue: " + issue);
		ou.addIssue(issue);
		
		printPrettyJson(ou);
	}
	
	@Test
	public void codingTest() throws Exception {
		
		Coding coding = Coding.builder()
			.code("1234")
			.system("http://snomed.info/sct")
			.version("20180131")
			.build();
		
		String jsonString = objectMapper.writeValueAsString(coding);
		
		String expected = "{\"code\":\"1234\","
				+ "\"system\":\"http://snomed.info/sct\","
				+ "\"version\":\"20180131\",\"userSelected\":false}";
		
		Assert.assertEquals(expected, jsonString);
	}
	
	@Test
	public void subPropertyTest() throws Exception {
		
		SerializableParametersConverter converter = new SerializableParametersConverter();

		Collection<SerializableParameter> parameters = converter.convert(SubProperty.builder()
			.code("123")
			.value(2.1)
			.description("propertyDescription")
			.build()).getParameters();
		
		printPrettyJson(parameters);
		
		String jsonString = objectMapper.writeValueAsString(parameters);
		System.out.println(jsonString);
		String expected = "[{\"name\":\"code\",\"valueCode\":\"123\"},"
				+ "{\"name\":\"value\",\"valueDecimal\":2.1},"
				+ "{\"name\":\"description\",\"valueString\":\"propertyDescription\"}]";
		
		Assert.assertEquals(expected, jsonString);
	}
	
	@Test
	public void propertyDecimalValueTest() throws Exception {

		Property property = Property.builder()
			.code("123")
			.value(2.1)
			.description("propertyDescription")
			.addSubProperty(SubProperty.builder()
				.code("subCode")
				.description("subDescription")
				.value(1)
				.build())
			.build();
		 
		PropertyConverter converter = new PropertyConverter();
		ParametersModel parametersModel = converter.convert(property);
			
		Collection<SerializableParameter> serializableParameters = parametersModel.getParameters();
		
		printPrettyJson(serializableParameters);
		
		String jsonString = objectMapper.writeValueAsString(serializableParameters);
		System.out.println(jsonString);
		
		String expected = "[{\"name\":\"code\",\"valueCode\":\"123\"},"
				+ "{\"name\":\"value\",\"valueDecimal\":2.1},"
				+ "{\"name\":\"description\",\"valueString\":\"propertyDescription\"},"
				+ "{\"name\":\"subproperty\","
				+ "\"part\":[{\"name\":\"code\",\"valueCode\":\"subCode\"},"
					+ "{\"name\":\"value\",\"valueInteger\":1},"
					+ "{\"name\":\"description\",\"valueString\":\"subDescription\"}]"
				+ "}]";
		
		Assert.assertEquals(expected, jsonString);
	}
	
	/**
	 * /*
	 * The value of the property can be code | Coding | string | integer | boolean | dateTime
	 * @throws Exception
	 */
	@Test
	public void propertyValueTypeTest() throws Exception {

		Builder basePropertyBuilder = Property.builder()
			.code("123");
		
		
		PropertyConverter converter = new PropertyConverter();
		//value = null
		Collection<SerializableParameter> parameters = converter.convert(basePropertyBuilder.build()).getParameters();
		String jsonString = objectMapper.writeValueAsString(parameters);
		
		String expected = "[{\"name\":\"code\",\"valueCode\":\"123\"}]";
		Assert.assertEquals(expected, jsonString);
		
		parameters = converter.convert(basePropertyBuilder.value(true).build()).getParameters();
		jsonString = objectMapper.writeValueAsString(parameters);
		
		expected = "[{\"name\":\"code\",\"valueCode\":\"123\"},"
				+ "{\"name\":\"value\",\"valueBoolean\":true}]";
		Assert.assertEquals(expected, jsonString);
		
		parameters = converter.convert(basePropertyBuilder.value(1).build()).getParameters();
		jsonString = objectMapper.writeValueAsString(parameters);
		
		expected = "[{\"name\":\"code\",\"valueCode\":\"123\"},"
				+ "{\"name\":\"value\",\"valueInteger\":1}]";
		
		parameters = converter.convert(basePropertyBuilder.value(1l).build()).getParameters();
		jsonString = objectMapper.writeValueAsString(parameters);
		
		expected = "[{\"name\":\"code\",\"valueCode\":\"123\"},"
				+ "{\"name\":\"value\",\"valueDecimal\":1}]";
		Assert.assertEquals(expected, jsonString);
		
		parameters = converter.convert(basePropertyBuilder.value("test").build()).getParameters();
		jsonString = objectMapper.writeValueAsString(parameters);
		
		expected = "[{\"name\":\"code\",\"valueCode\":\"123\"},"
				+ "{\"name\":\"value\",\"valueString\":\"test\"}]";
		Assert.assertEquals(expected, jsonString);
		
		parameters = converter.convert(basePropertyBuilder.value(new Code("code")).build()).getParameters();
		jsonString = objectMapper.writeValueAsString(parameters);
		
		expected = "[{\"name\":\"code\",\"valueCode\":\"123\"},"
				+ "{\"name\":\"value\",\"valueCode\":\"code\"}]";
		Assert.assertEquals(expected, jsonString);
		
		System.out.println(jsonString);
	}
	
	@Test
	public void designationTest() throws Exception {
		
		Coding coding = Coding.builder()
			.code("1234")
			.system("http://snomed.info/sct")
			.version("20180131")
			.build();
		
		Designation designation = Designation.builder()
			.languageCode("en_uk")
			.use(coding)
			.value("dValue")
			.build();
			
		ParametersModel parametersModel = new SerializableParametersConverter().convert(designation);
		
		List<SerializableParameter> parameters = parametersModel.getParameters();
		printPrettyJson(parameters);
		
		String jsonString = objectMapper.writeValueAsString(parameters);
		
		String expected = "[{\"name\":\"language\",\"valueCode\":\"en_uk\"},"
				+ "{\"name\":\"use\","
				+ "\"valueCoding\":{\"code\":\"1234\","
				+ "\"system\":\"http://snomed.info/sct\","
				+ "\"version\":\"20180131\",\"userSelected\":false}},"
				+ "{\"name\":\"value\",\"valueString\":\"dValue\"}]";
		
		Assert.assertEquals(expected, jsonString);
	}
	
	@Test
	public void lookupResultTest() throws Exception {
		LookupResult lookupResult = LookupResult.builder()
			.name("test")
			.addDesignation(Designation.builder()
					.value("dValue")
					.languageCode("uk").build())
			.addProperty(Property.builder()
					.code("1234")
					.description("propDescription")
					.value("sds")
					.addSubProperty(SubProperty.builder()
						.code("subCode")
						.description("subDescription")
						.value(1)
						.build())
					.build())
			.build();
		
		LookupResultConverter resultConverter = new LookupResultConverter();
		ParametersModel paramtersModel = resultConverter.convert(lookupResult);
		
		printJson(paramtersModel);
		printPrettyJson(paramtersModel);
	}

}
