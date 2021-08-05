/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.tests.domain.capabilitystatement;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.codesystems.ResourceType;
import com.b2international.snowowl.fhir.core.codesystems.RestfulCapabilityMode;
import com.b2international.snowowl.fhir.core.codesystems.SearchParamType;
import com.b2international.snowowl.fhir.core.model.capabilitystatement.*;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.CodeableConcept;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link Rest}
 * @since 8.0.0
 */
public class RestTest extends FhirTest {
	

	private Rest rest;

	@Before
	public void setup() throws Exception {
		
		rest = Rest.builder()
				.mode(RestfulCapabilityMode.CLIENT)
				.documentation("documentation")
				.addInteraction(Interaction.builder()
						.code("code")
						.documentation("documentation")
						.build())
				.addResource(Resource.builder()
						.type(ResourceType.CODESYSTEM)
						.profile("profile")
						.addOperation(Operation.builder()
								.name("name")
								.definition("definition")
								.documentation("documentation")
								.build())
						.addReferencePolicy(new Code("referencePolicy"))
						.addSearchInclude("searchInclude")
						.addSearchParam(SearchParam.builder()
								.definition("definition")
								.documentation("documentation")
								.name("name")
								.type(SearchParamType.STRING)
								.build())
						.addSearchRevInclude("searchRevInclude")
						.addSearchInclude("searchInclude")
						.addInteraction(Interaction.builder()
								.code("code")
								.documentation("documentation")
								.build())
						.addSupportedProfile(new Uri("supportedProfile"))
						.conditionalCreate(true)
						.conditionalDelete(new Code("conditionalDelete"))
						.conditionalRead(new Code("conditionalRead"))
						.conditionalUpdate(true)
						.documentation("documentation")
						.readHistory(false)
						.updateCreate(true)
						.versioning("versioning")
						.build())
				.security(Security.builder()
						.addService(CodeableConcept.builder()
								.addCoding(Coding.builder()
										.code("serviceCode")
										.display("serviceDisplay")
										.build())
								.text("codingTest")
								.build())
						.cors(true)
						.description("serviceDescription")
						.build())
				.addSearchParam(SearchParam.builder()
						.definition("definition")
						.documentation("documentation")
						.name("name")
						.type(SearchParamType.STRING)
						.build())
				.addOperation(Operation.builder()
						.name("name")
						.definition("definition")
						.documentation("documentation")
						.build())
				.addCompartment(new Uri("compartment"))
				.build();
	
	}
	
	private void validate(Rest rest) {
		assertEquals("documentation", rest.getDocumentation());
		assertEquals(RestfulCapabilityMode.CLIENT.getCode(), rest.getMode());
		assertEquals("serviceDescription", rest.getSecurity().getDescription());
		CodeableConcept serviceConcept = rest.getSecurity().getServices().iterator().next();
		assertEquals("codingTest", serviceConcept.getText());
		Resource restResource = rest.getResources().iterator().next();
		assertEquals("profile", restResource.getProfile().getUriValue());
		assertEquals("name", restResource.getOperations().iterator().next().getName());
		assertEquals("compartment", rest.getCompartments().iterator().next().getUriValue());
		assertEquals("name", rest.getSearchParams().iterator().next().getName());
	}
	
	@Test
	public void serialize() throws Exception {
		
		printPrettyJson(rest);
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(rest));
		assertThat(jsonPath.getString("documentation"), equalTo("documentation"));
	}
	
	@Test
	public void deserialize() throws Exception {
		Rest readRest = objectMapper.readValue(objectMapper.writeValueAsString(rest), Rest.class);
		validate(readRest);
	}
}
