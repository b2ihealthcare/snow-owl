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

import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.core.StringStartsWith.startsWith;

import java.util.Collection;

import javax.validation.ValidationException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.b2international.snowowl.fhir.api.codesystems.IssueSeverity;
import com.b2international.snowowl.fhir.api.codesystems.IssueType;
import com.b2international.snowowl.fhir.api.model.Designation;
import com.b2international.snowowl.fhir.api.model.Issue;
import com.b2international.snowowl.fhir.api.model.LookupResult;
import com.b2international.snowowl.fhir.api.model.OperationOutcome;
import com.b2international.snowowl.fhir.api.model.Property;
import com.b2international.snowowl.fhir.api.model.Property.Builder;
import com.b2international.snowowl.fhir.api.model.SubProperty;
import com.b2international.snowowl.fhir.api.model.dt.Code;
import com.b2international.snowowl.fhir.api.model.dt.Coding;
import com.b2international.snowowl.fhir.api.model.serialization.SerializableParameter;
import com.b2international.snowowl.fhir.api.tests.FhirTest;

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
		
		//ObjectMapper om = new ObjectMapper();
//		SimpleModule testModule = new SimpleModule("MyModule");
//	    testModule.addSerializer(Code.class, new MyCustomSerializer(Code.class));
//	    testModule.addSerializer(Code.class, new ToStringSerializer(Code.class));
		
		String jsonString = objectMapper.writeValueAsString(coding);
		
		String expected = "{\"code\":\"1234\","
				+ "\"system\":\"http://snomed.info/sct\","
				+ "\"version\":\"20180131\",\"userSelected\":false}";
		
		Assert.assertEquals(expected, jsonString);
	}
	
	@Test
	public void codingEmptyParametersTest() throws Exception {
		
		exception.expect(ValidationException.class);
		exception.expectMessage(startsWith("{violations=['code.codeValue' may not be empty (was ''),"));
<<<<<<< Upstream, based on branch 'feature/fhir_api' of https://github.com/b2ihealthcare/snow-owl.git
		exception.expectMessage(endsWith("'system' may not be empty (was 'null')]}"));
=======
		exception.expectMessage(endsWith("'system' may not be null (was 'null')]}"));
>>>>>>> 3acbefb SO-2917 URI references replaced.
		
		Coding.builder()
			.code("")
			.version("20180131")
			.build();
	}
	
	@Test
	public void codingInvalidSystemTest() throws Exception {
		
		exception.expect(ValidationException.class);
		exception.expectMessage(startsWith("{violations=['system.uriValue' uri is invalid (was 'sys tem')]}"));
		
		Coding.builder()
			.code("1233")
			.system("sys tem")
			.version("20180131")
			.build();
	}
	
	@Test
	public void subPropertyTest() throws Exception {

		Collection<SerializableParameter> parameters = SubProperty.builder()
			.code("123")
			.value(2.1)
			.description("propertyDescription")
			.build()
			.toParameters();
		
		printPrettyJson(parameters);
		
		String jsonString = objectMapper.writeValueAsString(parameters);
		System.out.println(jsonString);
		String expected = "[{\"name\":\"code\",\"valueCode\":\"123\"},"
				+ "{\"name\":\"value\",\"valueDecimal\":2.1},"
				+ "{\"name\":\"description\",\"valueString\":\"propertyDescription\"}]";
		
		Assert.assertEquals(expected, jsonString);
	}
	
	@Test
	public void subPropertyMissingValueTest() throws Exception {
		
		exception.expect(ValidationException.class);
		exception.expectMessage(startsWith("{violations=['code' may not be null (was 'null'), 'value' may not be null (was 'null')]}"));
		
		SubProperty.builder()
			//.code("123")
			//.value(2.1)
			.description("propertyDescription")
			.build()
			.toParameters();
	}
	
	@Test
	public void propertyDecimalValueTest() throws Exception {

		Collection<SerializableParameter> parameters = Property.builder()
			.code("123")
			.value(2.1)
			.description("propertyDescription")
			.addSubProperty(SubProperty.builder()
				.code("subCode")
				.description("subDescription")
				.value(1)
				.build())
			.build()
			.toParameters();
		
		printPrettyJson(parameters);
		
		String jsonString = objectMapper.writeValueAsString(parameters);
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
		
		//value = null
		Collection<SerializableParameter> parameters = basePropertyBuilder.build().toParameters();
		String jsonString = objectMapper.writeValueAsString(parameters);
		
		String expected = "[{\"name\":\"code\",\"valueCode\":\"123\"}]";
		Assert.assertEquals(expected, jsonString);
		
		parameters = basePropertyBuilder.value(true).build().toParameters();
		jsonString = objectMapper.writeValueAsString(parameters);
		
		expected = "[{\"name\":\"code\",\"valueCode\":\"123\"},"
				+ "{\"name\":\"value\",\"valueBoolean\":true}]";
		Assert.assertEquals(expected, jsonString);
		
		parameters = basePropertyBuilder.value(1).build().toParameters();
		jsonString = objectMapper.writeValueAsString(parameters);
		
		expected = "[{\"name\":\"code\",\"valueCode\":\"123\"},"
				+ "{\"name\":\"value\",\"valueInteger\":1}]";
		
		parameters = basePropertyBuilder.value(1l).build().toParameters();
		jsonString = objectMapper.writeValueAsString(parameters);
		
		expected = "[{\"name\":\"code\",\"valueCode\":\"123\"},"
				+ "{\"name\":\"value\",\"valueDecimal\":1}]";
		Assert.assertEquals(expected, jsonString);
		
		parameters = basePropertyBuilder.value("test").build().toParameters();
		jsonString = objectMapper.writeValueAsString(parameters);
		
		expected = "[{\"name\":\"code\",\"valueCode\":\"123\"},"
				+ "{\"name\":\"value\",\"valueString\":\"test\"}]";
		Assert.assertEquals(expected, jsonString);
		
		parameters = basePropertyBuilder.value(new Code("code")).build().toParameters();
		jsonString = objectMapper.writeValueAsString(parameters);
		
		expected = "[{\"name\":\"code\",\"valueCode\":\"123\"},"
				+ "{\"name\":\"value\",\"valueCode\":\"code\"}]";
		Assert.assertEquals(expected, jsonString);
		
		System.out.println(jsonString);
	}
	
	@Test
	public void propertyMissingCodeTest() throws Exception {

		exception.expect(ValidationException.class);
		exception.expectMessage(startsWith("{violations=['code' may not be null"));
		
		Property.builder()
			//.code("123")
			.value(2)
			.description("propertyDescription")
			.addSubProperty(SubProperty.builder()
				.code("subCode")
				.description("subDescription")
				.value(1)
				.build())
			.build()
			.toParameters();
	}
	
	@Test
	public void propertyEmptyCodeTest() throws Exception {

		exception.expect(ValidationException.class);
		exception.expectMessage(startsWith("{violations=['code.codeValue' may not be empty"));
		
		Property.builder()
			.code("")
			.value(2)
			.description("propertyDescription")
			.addSubProperty(SubProperty.builder()
				.code("subCode")
				.description("subDescription")
				.value(1)
				.build())
			.build()
			.toParameters();
	}
	
	@Test
	public void designationTest() throws Exception {
		
		Coding coding = Coding.builder()
			.code("1234")
			.system("http://snomed.info/sct")
			.version("20180131")
			.build();
		
		Collection<SerializableParameter> designationParams = Designation.builder()
			.languageCode("en_uk")
			.use(coding)
			.value("dValue")
			.build().toParameters();
		
		printPrettyJson(designationParams);
		
		String jsonString = objectMapper.writeValueAsString(designationParams);
		
		String expected = "[{\"name\":\"language\",\"valueCode\":\"en_uk\"},"
				+ "{\"name\":\"use\","
				+ "\"valueCoding\":{\"code\":\"1234\","
				+ "\"system\":\"http://snomed.info/sct\","
				+ "\"version\":\"20180131\",\"userSelected\":false}},"
				+ "{\"name\":\"value\",\"valueString\":\"dValue\"}]";
		
		Assert.assertEquals(expected, jsonString);
	}
	
	@Test
	public void designationMissingValueTest() throws Exception {
		
		exception.expect(ValidationException.class);
		exception.expectMessage(startsWith("{violations=['value' may not be empty"));
		
		Coding coding = Coding.builder()
			.code("1234")
			.system("http://snomed.info/sct")
			.version("20180131")
			.build();
		
		Designation.builder()
			.languageCode("en_uk")
			.use(coding)
			//.value("dValue")
			.build().toParameters();
	}
	
	@Test
	public void designationEmptyValueTest() throws Exception {
		
		exception.expect(ValidationException.class);
		exception.expectMessage(startsWith("{violations=['value' may not be empty"));
		
		Coding coding = Coding.builder()
			.code("1234")
			.system("http://snomed.info/sct")
			.version("20180131")
			.build();
		
		Designation.builder()
			.languageCode("en_uk")
			.use(coding)
			.value("")
			.build()
			.toParameters();
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
		
		printJson(lookupResult);
		printPrettyJson(lookupResult);
	}

}
