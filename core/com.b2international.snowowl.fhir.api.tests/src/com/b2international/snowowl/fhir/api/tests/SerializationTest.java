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

import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.b2international.snowowl.fhir.api.model.Coding;
import com.b2international.snowowl.fhir.api.model.Designation;
import com.b2international.snowowl.fhir.api.model.LookupResults;
import com.b2international.snowowl.fhir.api.model.serialization.FhirDesignation;
import com.b2international.snowowl.fhir.api.model.serialization.FhirLookupResult;
import com.b2international.snowowl.fhir.api.model.serialization.FhirParameter;
import com.b2international.snowowl.fhir.api.model.serialization.FhirParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;

public class SerializationTest {
	
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
	
	//@Test
	public void designationTest() throws Exception {
		Coding coding = new Coding("code", "systemUri", "version");
		
		FhirDesignation serializableBean = Designation.builder()
				.langaugeCode("lang")
				.use(coding)
				.value("value")
				.buildSerializableBean();
		
		serializeAsJson(serializableBean);
	}
	

	//@Test
	public void parametersTest() throws Exception {
		FhirParameters params = new FhirParameters();
		FhirParameter parameter = new FhirParameter("fieldName", "type", "value");
		params.add(parameter);
		parameter = new FhirParameter("fieldName2", "type2", "value2");
		params.add(parameter);
		serializeAsJson(params);
	}
	
	@Test
	public void lookupResultsTest() throws Exception {
		FhirLookupResult lookupResults = new FhirLookupResult();
		FhirParameter parameter = new FhirParameter("fieldName", "type", "value");
		lookupResults.add(parameter);
		parameter = new FhirParameter("fieldName2", "type2", "value2");
		lookupResults.add(parameter);
		
		FhirDesignation fhirDesignation = new FhirDesignation();
		FhirParameter designationParameter2 = new FhirParameter("dFieldName", "dType", "dValue");
		fhirDesignation.add(designationParameter2);
		designationParameter2 = new FhirParameter("dFieldName2", "dType2", "dValue2");
		fhirDesignation.add(designationParameter2);

		FhirParameter designationParameter = new FhirParameter("designation", "part", fhirDesignation.getParameters());
		lookupResults.add(designationParameter);
		serializeAsJson(lookupResults);
		
	}
	
	@Test
	public void lookupResultsTest3() throws Exception {
		FhirLookupResult lookupResults = new FhirLookupResult();
		FhirParameter parameter = new FhirParameter("fieldName", "type", "value");
		lookupResults.add(parameter);
		parameter = new FhirParameter("fieldName2", "type2", "value2");
		lookupResults.add(parameter);
		
		Collection<FhirParameter> designationParameters = Sets.newHashSet();
		FhirParameter designationParameter = new FhirParameter("dFieldName", "dType", "dValue");
		designationParameters.add(designationParameter);
		designationParameter = new FhirParameter("dFieldName2", "dType2", "dValue2");
		designationParameters.add(designationParameter);
		
		FhirParameter lookupParameter = new FhirParameter("designation", "part", designationParameters);
		lookupResults.add(lookupParameter);
		serializeAsJson(lookupResults);
	}
	
	//@Test
	public void lookupResultsTest2() throws Exception {
		
		LookupResults lookupResults = new LookupResults();
		
	}
	
	private void serializeAsJson(Object object) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		String result = objectMapper.writeValueAsString(object);
		Object json = objectMapper.readValue(result, Object.class);
		String prettyPrint = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
		System.out.println(prettyPrint);
	}

}
