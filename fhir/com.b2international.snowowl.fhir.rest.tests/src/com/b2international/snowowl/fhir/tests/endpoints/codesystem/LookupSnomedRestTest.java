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
package com.b2international.snowowl.fhir.tests.endpoints.codesystem;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import com.b2international.snowowl.fhir.core.model.Designation;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupRequest;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupResult;
import com.b2international.snowowl.fhir.core.model.codesystem.Property;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Fhir;
import com.b2international.snowowl.fhir.tests.FhirRestTest;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;

/**
 * CodeSystem $lookup operation REST end-point test cases
 * 
 * @since 6.6
 */
public class LookupSnomedRestTest extends FhirRestTest {
	
	//GET SNOMED CT with parameters, default properties
	@Test
	public void lookupDefaultPropertiesTest() throws Exception {
		
		String responseString = givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("system", "http://snomed.info/sct")
			.param("code", Concepts.IS_A)
			.param("property", "designation", "sufficientlyDefined", "inactive", "effectiveTime")
			.param("_format", "json")
			.when().get("/CodeSystem/$lookup")
			.then()
			.assertThat()
			.statusCode(200)
			.extract()
			.body()
			.asString();
		
		LookupResult result = convertToResult(responseString);
		
		assertEquals("SNOMED CT", result.getName());
		assertEquals("Is a", result.getDisplay());
		
		//Designations
		Collection<Designation> designations = result.getDesignation();
		
		Designation ptDesignation = designations.stream()
			.filter(d -> "Is a".equals(d.getValue()))
			.findFirst()
			.get();
		
		assertThat(ptDesignation.getUse().getCodeValue()).isEqualTo(Concepts.SYNONYM);
		assertThat(ptDesignation.getUse().getDisplay()).isEqualTo("Synonym");
		
		Designation fsnDesignation = designations.stream()
				.filter(d -> d.getValue().equals("Is a (attribute)"))
				.findFirst()
				.get();
		
		assertThat(fsnDesignation.getUse().getCodeValue()).isEqualTo(Concepts.FULLY_SPECIFIED_NAME);
		assertThat(fsnDesignation.getUse().getDisplay()).isEqualTo("Fully specified name");
		
		//Properties
		Collection<Property> properties = result.getProperty();
		
		properties.forEach(System.out::println);
		
		Property definitionProperty = getProperty(properties, "sufficientlyDefined");
		assertThat(definitionProperty.getValue()).isEqualTo(false);
		
		Property statusProperty = getProperty(properties, "inactive");
		assertThat(statusProperty.getValue()).isEqualTo(false);
		
		Property effectiveTimeProperty = getProperty(properties, "effectiveTime");
		assertThat(effectiveTimeProperty.getValue()).isEqualTo("20110131");

		Set<String> codeValues = properties.stream().map(p -> p.getCode()).collect(Collectors.toSet());
		assertThat(codeValues).doesNotContain("parent");
		
	}
	
	//GET SNOMED CT with properties
	@Test
	public void lookupSnomedCodeSystemCodeTest() throws Exception {
		
		String responseString = givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("system", "http://snomed.info/sct")
			.param("code", "128927009") //procedure by method
			.param("property", "inactive")
			.param("property", "http://snomed.info/id/260686004") //method
			.param("_format", "json")
			.when().get("/CodeSystem/$lookup")
			.then()
			.assertThat()
			.statusCode(200)
			.extract()
			.body()
			.asString();
		
		LookupResult result = convertToResult(responseString);
		
		//Mandatory parameters
		assertEquals("SNOMED CT", result.getName());
		assertEquals("Procedure by method", result.getDisplay());
		
		assertNull(result.getVersion());
		
		//Designations
		Collection<Designation> designations = result.getDesignation();
		assertTrue(designations.isEmpty());
		
		//Properties
		Collection<Property> properties = result.getProperty();
		assertEquals(2, properties.size());
		
		Property inactiveProperty = getProperty(properties, "inactive");
		assertThat(inactiveProperty.getValue()).isEqualTo(false);
		
		Property associatedMProperty = getProperty(properties, Concepts.METHOD);
		assertThat(associatedMProperty.getValue()).isEqualTo("129264002"); //method = Action
	}
	
	//GET SNOMED CT with properties
	@Test
	public void lookupSnomedCodeSystemCodeInvalidProperyTest() throws Exception {
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("system", "http://snomed.info/sct")
			.param("code", "263495000")
			.param("property", "INACTIVE")
			.param("_format", "json")
			.when().get("/CodeSystem/$lookup")
			.then()
			.body("resourceType", equalTo("OperationOutcome"))
			.body("issue.severity", hasItem("error"))
			.body("issue.code", hasItem("invalid"))
			.body("issue.details.text", hasItem("Parameter 'LookupRequest.property' content is invalid"))
			.statusCode(400);
	}
	
	@Test
	public void lookupVersionTest() throws Exception {
		
		String responseString = givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("system", "http://snomed.info/sct/900000000000207008/version/20170131")
			.param("code", Concepts.IS_A)
			.param("property", "version")
			.when().get("/CodeSystem/$lookup")
			.then()
			.assertThat()
			.statusCode(200)
			.extract()
			.body()
			.asString();
		
		LookupResult result = convertToResult(responseString);
		
		assertEquals("SNOMED CT", result.getName());
		assertEquals("20170131", result.getVersion());
	}
	
	@Test
	public void lookupVersionPostTest() throws Exception {
		
		Coding coding = Coding.builder()
			.system("http://snomed.info/sct/900000000000207008/version/20170131")
			.code(Concepts.IS_A)
			.build();

		LookupRequest request = LookupRequest.builder()
				.coding(coding)
				.build();
		
		Fhir fhirParameters = new Parameters.Fhir(request);
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.contentType(APPLICATION_FHIR_JSON)
			.body(fhirParameters)
			.when().post("/CodeSystem/$lookup")
			.then()
			.statusCode(200)
			.body("resourceType", equalTo("Parameters"))
			.body("parameter.size()", is(2))
			.body("parameter[0].name", equalTo("name"))
			.body("parameter[0].valueString", equalTo("SNOMED CT"))
			.body("parameter[1].name", equalTo("display"))
			.body("parameter[1].valueString", equalTo("Is a"));
	}
	
	@Test
	public void lookupInvalidVersionTest() throws Exception {
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("system", "http://snomed.info/sct/900000000000207008/version/20170130")
			.param("code", "263495000")
			.param("property", "INACTIVE")
			.when().get("/CodeSystem/$lookup")
			.then()
			.body("resourceType", equalTo("OperationOutcome"))
			.body("issue.severity", hasItem("error"))
			.body("issue.code", hasItem("invalid"))
			.body("issue.details.text", hasItem("Parameter 'CodeSystem.system' content is invalid"))
			.statusCode(400);
	}
	
	@Test
	public void lookupInvalidVersionTest2() throws Exception {
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("system", "http://snomed.info/sct/900000000000207008/version/abcd")
			.param("code", "263495000")
			.param("property", "INACTIVE")
			.when().get("/CodeSystem/$lookup")
			.then()
			.body("resourceType", equalTo("OperationOutcome"))
			.body("issue.severity", hasItem("error"))
			.body("issue.code", hasItem("invalid"))
			.body("issue.details.text", hasItem("Parameter 'CodeSystem$lookup.system' content is invalid"))
			.statusCode(400);
	}

	private Property getProperty(Collection<Property> properties, String codeValue) {
		Optional<Property> propertyOptional = properties.stream()
			.filter(p -> p.getCode().equals(codeValue))
			.findFirst();
		return propertyOptional.orElseThrow(() -> new RuntimeException("Could not find property " + codeValue + "."));
	}
	
}