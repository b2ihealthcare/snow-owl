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

import java.util.TimeZone;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;

import com.b2international.snowowl.api.codesystem.domain.ICodeSystemVersionProperties;
import com.b2international.snowowl.fhir.api.ICodeSystemVersionPropertiesMixin;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.datatype.guava.GuavaModule;

public class FhirTest {
	
	protected static ObjectMapper objectMapper = new ObjectMapper();
	
	@BeforeClass
	public static void setup() {
		System.out.println(" --- Setting up object mapper --- ");
		objectMapper.setVisibility(PropertyAccessor.CREATOR, Visibility.ANY);
		objectMapper.registerModule(new GuavaModule());
		objectMapper.setSerializationInclusion(Include.NON_EMPTY);
		final ISO8601DateFormat df = new ISO8601DateFormat();
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		objectMapper.setDateFormat(df);
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		objectMapper.addMixIn(ICodeSystemVersionProperties.class, ICodeSystemVersionPropertiesMixin.class);
	}
	
	@Rule 
	public TestName testName = new TestName();
	
	@Before
	public void dmdTestSetup() throws Exception {
		System.out.println("--- Test method started: " + this.getClass().getSimpleName() + ":" + testName.getMethodName() + " ---");
	}

	@After
	public void dmdTearDown() throws Exception {
		System.out.println("--- Test method completed: " + this.getClass().getSimpleName() + ":" + testName.getMethodName() + " ---\n");
	}
	
	protected void printPrettyJson(Object object) throws Exception {
		String result = objectMapper.writeValueAsString(object);
		Object json = objectMapper.readValue(result, Object.class);
		String prettyPrint = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
		System.out.println(prettyPrint);
	}
	
	protected void printJson(Object object) throws Exception {
		String result = objectMapper.writeValueAsString(object);
		System.out.println(result);
	}

}
