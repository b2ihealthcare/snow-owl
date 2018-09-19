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
package com.b2international.snowowl.fhir.api.tests.endpoints.codesystem;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;

import org.junit.Test;

import com.b2international.snowowl.fhir.api.tests.FhirRestTest;

/**
 * CodeSystem REST end-point test cases
 * @since 6.6
 */
public class CodeSystemRestTest extends FhirRestTest {
	
	private static final String FHIR_ISSUE_TYPE_CODESYSTEM_ID = "fhir:issue-type";
	
	//@Test
	public void pingTest() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.when().get("/CodeSystem/ping")
			.then().assertThat().statusCode(200);
	}
	
	//@Test
	public void printAllCodesystems() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
		.when().get("/CodeSystem").prettyPrint();
	}
	
	//All code systems fully detailed
	@Test
	public void getAllFullCodeSystemsTest() {
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.when().get("/CodeSystem")
			.then()
			.body("resourceType", equalTo("Bundle"))
			.body("total", notNullValue())
			
			//SNOMED CT
			.body("entry.resource.url", hasItem("http://hl7.org/fhir/operation-outcome"))
			.root("entry.resource.find { it.url == 'http://snomed.info/sct/version/20170731'}")
			.body("property.size()", equalTo(116))
			
			//FHIR issue type code system has children
			.root("entry.resource.find { it.url == 'http://hl7.org/fhir/issue-type'}")
			.body("concept.size()", equalTo(29))
			.statusCode(200);
	}
	
	@Test
	public void getCodeSystemsSummaryTest() {
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("_summary", true)
			.when().get("/CodeSystem").then()
			.body("resourceType", equalTo("Bundle"))
			.body("total", notNullValue())
			.body("type", equalTo("searchset"))
			
			//no concept definitions are part of the summary
			.body("entry.resource", not(hasItem("concept"))) 
			.statusCode(200);
	}
	
	//Id parameter
	@Test
	public void getCodeSystemsIdParamTest() {
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("_id", FHIR_ISSUE_TYPE_CODESYSTEM_ID)
			.when().get("/CodeSystem")
			.then()
			.body("resourceType", equalTo("Bundle"))
			.body("total", equalTo(1))
			.body("type", equalTo("searchset"))
			.body("entry[0].resource.concept", notNullValue())
			.statusCode(200);
	}
	
	//Invalid ID parameter
	@Test
	public void getCodeSystemsInvalidIdParamTest() {
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("_id", "whatever")
			.when().get("/CodeSystem").then()
			.body("resourceType", equalTo("OperationOutcome"))
			.body("issue.severity", hasItem("error"))
			.body("issue.code", hasItem("invalid"))
			.statusCode(400);
	}
	
	//Fully detailed SNOMED CT code system
	@Test
	public void getSnomedCodeSystemTest() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
		 	.pathParam("id", "snomedStore:MAIN/2018-01-31") 
			.when().get("/CodeSystem/{id}")
			.then()
			.body("resourceType", equalTo("CodeSystem"))
			.body("content", equalTo("not-present"))
			.body("status", equalTo("active"))
			.statusCode(200);
	}
	
	//Full FHIR code system
	@Test
	public void getFhirCodeSystemTest() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("_summary", false)
		 	.pathParam("id", FHIR_ISSUE_TYPE_CODESYSTEM_ID) 
			.when().get("/CodeSystem/{id}")
			.then()
			.body("resourceType", equalTo("CodeSystem"))
			.body("status", equalTo("active")) //mandatory
			.body("name", equalTo("IssueType")) //summary
			.body("concept", notNullValue()) //optional
			.body("copyright", containsString("2011+ HL7")) //optional
			.statusCode(200);
	}
	
	//Summary-only FHIR code system
	@Test
	public void getFhirCodeSystemSummaryTest() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("_summary", true)
		 	.pathParam("id", FHIR_ISSUE_TYPE_CODESYSTEM_ID) 
			.when().get("/CodeSystem/{id}")
			.then()
			.body("resourceType", equalTo("CodeSystem"))
			.body("meta.tag.code", hasItem("SUBSETTED"))
			.body("status", equalTo("active"))
			//NOT part of the summary
			.body("name", nullValue())
			.body("concept", nullValue()) 
			.body("copyright", nullValue()) 
			.statusCode(200);
	}
	
	//Summary-count should not be allowed for non-search type operations
	@Test
	public void getFhirCodeSystemCountTest() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("_summary", "count")
		 	.pathParam("id", FHIR_ISSUE_TYPE_CODESYSTEM_ID) 
			.when().get("/CodeSystem/{id}")
			.then()
			.body("resourceType", equalTo("OperationOutcome"))
			.body("issue.severity", hasItem("error"))
			.body("issue.code", hasItem("invalid"))
			.statusCode(400);
	}
	
	//Summary-data FHIR code system (remove text element)
	@Test
	public void getFhirCodeSystemDataTest() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("_summary", "data")
		 	.pathParam("id", FHIR_ISSUE_TYPE_CODESYSTEM_ID) 
			.when().get("/CodeSystem/{id}")
			.then()
			.body("resourceType", equalTo("CodeSystem"))
			.body("meta.tag.code", hasItem("SUBSETTED"))
			//only text, id, meta and mandatory
			.body("text", nullValue())
			.body("status", equalTo("active"))
			.body("id", notNullValue())
			.body("count", notNullValue())
			.body("name", notNullValue())
			.body("concept", notNullValue()) 
			.body("copyright", notNullValue()) 
			.body("url", notNullValue()) 
			.statusCode(200);
	}
	
	//Summary-text FHIR code system (text, id, meta, mandatory)
	@Test
	public void getFhirCodeSystemTextTest() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("_summary", "text")
		 	.pathParam("id", FHIR_ISSUE_TYPE_CODESYSTEM_ID) 
			.when().get("/CodeSystem/{id}")
			.then()
			.body("resourceType", equalTo("CodeSystem"))
			.body("meta.tag.code", hasItem("SUBSETTED"))
			//only text, id, meta and mandatory
			.body("text.div", equalTo("<div>A code that describes the type of issue.</div>"))
			.body("status", equalTo("active"))
			.body("id", notNullValue())
			.body("count", nullValue())
			.body("name", nullValue())
			.body("concept", nullValue()) 
			.body("copyright", nullValue()) 
			.body("url", nullValue()) 
			.statusCode(200);
	}
	
	/*
	 * ?elements=name, url means
	 */
	@Test
	public void getFhirCodeSystemElementsTest() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("_elements", "name", "url")
		 	.pathParam("id", FHIR_ISSUE_TYPE_CODESYSTEM_ID) 
			.when().get("/CodeSystem/{id}")
			.then()
			//mandatory fields
			.body("resourceType", equalTo("CodeSystem"))
			.body("meta.tag.code", hasItem("SUBSETTED"))
			.body("status", equalTo("active"))
			.body("content", equalTo("complete"))
			.body("id", equalTo("issue-type"))
			//summary and optional fields
			.body("text", nullValue())
			.body("count", nullValue())
			.body("concept", nullValue()) 
			.body("copyright", nullValue()) 
			//requested fields
			.body("name", notNullValue())
			.body("url", notNullValue()) 
			.statusCode(200);
	}
	
	/*
	 * Incorrect elements should be ignored.
	 */
	@Test
	public void getFhirCodeSystemIncorrectElementsTest() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("_elements", "xyz", "abcs")
		 	.pathParam("id", FHIR_ISSUE_TYPE_CODESYSTEM_ID) 
			.when().get("/CodeSystem/{id}")
			.then()
			//mandatory fields
			.body("resourceType", equalTo("CodeSystem"))
			.body("meta.tag.code", hasItem("SUBSETTED"))
			.body("status", equalTo("active"))
			.body("content", equalTo("complete"))
			.body("id", equalTo("issue-type"))
			//summary and optional fields
			.body("text", nullValue())
			.body("count", nullValue())
			.body("concept", nullValue()) 
			.body("copyright", nullValue()) 
			//requested fields
			.body("name",  nullValue())
			.body("url",  nullValue()) 
			.statusCode(200);
	}
	
}