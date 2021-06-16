/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.rest.tests.codesystem;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.*;

import java.util.List;
import java.util.UUID;

import org.junit.Test;

import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.tests.FhirRestTest;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;

/**
 * FHIR /CodeSystem Resource API Tests
 * 
 * @since 6.6
 */
public class FhirCodeSystemApiTest extends FhirRestTest {
	
	private static final String CODESYSTEM = "/CodeSystem";
	private static final String CODESYSTEM_ID = "/CodeSystem/{id}";

	@Test
	public void GET_CodeSystem() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.when().get(CODESYSTEM)
			.then()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("type", equalTo("searchset"))
			.body("meta.tag.code", not(hasItem(Coding.CODING_SUBSETTED.getCodeValue())))
			.body("total", equalTo(1))
			.body("entry[0].resource.url", equalTo(SnomedTerminologyComponentConstants.SNOMED_URI_BASE))
			.body("entry[0].resource.count", equalTo(0));
	}
	
	@Test
	public void GET_CodeSystem_IdFilter_NoMatch() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("_id", "non-existent")
			.when().get("/CodeSystem")
			.then()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("type", equalTo("searchset"))
			.body("meta.tag.code", not(hasItem(Coding.CODING_SUBSETTED.getCodeValue())))
			.body("total", equalTo(0));
	}
	
	@Test
	public void GET_CodeSystem_IdFilter_Match() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("_id", getTestCodeSystemId())
			.when().get("/CodeSystem")
			.then()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("type", equalTo("searchset"))
			.body("meta.tag.code", not(hasItem(Coding.CODING_SUBSETTED.getCodeValue())))
			.body("total", equalTo(1))
			.body("entry[0].resource.url", equalTo(SnomedTerminologyComponentConstants.SNOMED_URI_BASE));
	}
	
	@Test
	public void GET_CodeSystem_IdFilter_Match_Multi() throws Exception {
		String anotherCodeSystemId = createCodeSystem(UUID.randomUUID().toString());
		String thirdCodeSystemId = createCodeSystem(UUID.randomUUID().toString());
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("_id", getTestCodeSystemId(), anotherCodeSystemId)
			.when().get("/CodeSystem")
			.then()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("type", equalTo("searchset"))
			.body("meta.tag.code", not(hasItem(Coding.CODING_SUBSETTED.getCodeValue())))
			.body("total", equalTo(2))
			.body("entry.resource.id", hasItems(getTestCodeSystemId(), anotherCodeSystemId))
			.body("entry.resource.url", hasItem(SnomedTerminologyComponentConstants.SNOMED_URI_BASE));
	}
	
	@Test
	public void GET_CodeSystem_NameFilter_NoMatch() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("_name", "unknown name")
			.when().get("/CodeSystem")
			.then()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("type", equalTo("searchset"))
			.body("meta.tag.code", not(hasItem(Coding.CODING_SUBSETTED.getCodeValue())))
			.body("total", equalTo(0));
	}
	
	@Test
	public void GET_CodeSystem_NameFilter_Match_Single() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("_name", getTestCodeSystemId())
			.when().get("/CodeSystem")
			.then()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("type", equalTo("searchset"))
			.body("meta.tag.code", not(hasItem(Coding.CODING_SUBSETTED.getCodeValue())))
			.body("total", equalTo(1))
			.body("entry[0].resource.id", equalTo(getTestCodeSystemId()))
			.body("entry[0].resource.url", equalTo(SnomedTerminologyComponentConstants.SNOMED_URI_BASE));
	}
	
	@Test
	public void GET_CodeSystem_NameFilter_Match_Multiple() {
		String anotherCodeSystemId = createCodeSystem(UUID.randomUUID().toString());
		String thirdCodeSystemId = createCodeSystem(UUID.randomUUID().toString());
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("_name", getTestCodeSystemId(), anotherCodeSystemId)
			.when().get("/CodeSystem")
			.then()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("meta.tag.code", not(hasItem(Coding.CODING_SUBSETTED.getCodeValue())))
			.body("type", equalTo("searchset"))
			.body("total", equalTo(2))
			.body("entry.resource.id", hasItems(getTestCodeSystemId(), anotherCodeSystemId))
			.body("entry.resource.url", hasItem(SnomedTerminologyComponentConstants.SNOMED_URI_BASE));
	}
	
	@Test
	public void GET_CodeSystem_Summary_True() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("_summary", true)
			.when().get(CODESYSTEM)
			.then()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("meta.tag.code", hasItem(Coding.CODING_SUBSETTED.getCodeValue()))
			.body("type", equalTo("searchset"))
			.body("total", equalTo(1))
			//no concept definitions are part of the summary
			.body("entry.resource", not(hasItem("concept")));
	}
	
	@Test
	public void GET_CodeSystem_Summary_Text() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("_summary", "text")
			.when().get(CODESYSTEM)
			.then()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("meta.tag.code", hasItem(Coding.CODING_SUBSETTED.getCodeValue()))
			.body("type", equalTo("searchset"))
			.body("total", equalTo(1))
			// only text, id, meta and mandatory
			.body("entry[0].resource.id", equalTo(getTestCodeSystemId()))
			.body("entry[0].resource.status", equalTo("unknown"))
			.body("entry[0].resource.content", equalTo("complete"))
			.body("entry[0].resource.meta", notNullValue())
			.body("entry[0].resource.text", notNullValue())
			.body("entry[0].resource.count", nullValue())
			.body("entry[0].resource.name", nullValue())
			.body("entry[0].resource.concept", nullValue()) 
			.body("entry[0].resource.copyright", nullValue()) 
			.body("entry[0].resource.url", nullValue());
	}
	
	@Test
	public void GET_CodeSystem_Summary_Data() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("_summary", "data")
			.when().get(CODESYSTEM)
			.then()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("meta.tag.code", hasItem(Coding.CODING_SUBSETTED.getCodeValue()))
			.body("type", equalTo("searchset"))
			.body("total", equalTo(1))
			// only id, meta and mandatory
			.body("entry[0].resource.id", notNullValue())
			.body("entry[0].resource.status", equalTo("unknown"))
			.body("entry[0].resource.content", equalTo("complete"))
			// other fields should be null
			.body("entry[0].resource.text", nullValue())
			.body("entry[0].resource.url", nullValue())
			.body("entry[0].resource.name", nullValue())
			.body("entry[0].resource.copyright", nullValue())
			.body("entry[0].resource.count", nullValue())
			.body("entry[0].resource.text", nullValue());
	}
	
	@Test
	public void GET_CodeSystem_Summary_Count() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("_summary", "count")
			.when().get(CODESYSTEM)
			.then()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("meta.tag.code", not(hasItem(Coding.CODING_SUBSETTED.getCodeValue())))
			.body("total", equalTo(1))
			.body("type", equalTo("searchset"))
			.body("entry", equalTo(List.of()));
	}
	
	@Test
	public void GET_CodeSystem_Summary_False() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("_summary", false)
			.when().get(CODESYSTEM)
			.then()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("meta.tag.code", not(hasItem(Coding.CODING_SUBSETTED.getCodeValue())))
			.body("total", equalTo(1))
			.body("type", equalTo("searchset"))
			.body("entry[0].resource.id", equalTo(getTestCodeSystemId()))
			.body("entry[0].resource.url", equalTo(SnomedTerminologyComponentConstants.SNOMED_URI_BASE));
	}
	
	@Test
	public void GET_CodeSystem_Elements() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("_elements", "name", "url")
			.when().get(CODESYSTEM)
			.then() 
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("meta.tag.code", hasItem(Coding.CODING_SUBSETTED.getCodeValue()))
			.body("total", equalTo(1))
			.body("type", equalTo("searchset"))
			// mandatory fields
			.body("entry[0].resource.status", equalTo("unknown"))
			.body("entry[0].resource.content", equalTo("complete"))
			.body("entry[0].resource.id", equalTo(getTestCodeSystemId()))
			// summary and optional fields
			.body("entry[0].resource.text", nullValue())
			.body("entry[0].resource.count", nullValue())
			.body("entry[0].resource.concept", nullValue()) 
			.body("entry[0].resource.copyright", nullValue()) 
			// requested fields
			.body("entry[0].resource.name", equalTo(getTestCodeSystemId()))
			.body("entry[0].resource.url", equalTo(SnomedTerminologyComponentConstants.SNOMED_URI_BASE));
	}
	
	@Test
	public void GET_CodeSystem_Elements_Unrecognized() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("_elements", "xyz", "abcs")
			.when().get(CODESYSTEM)
			.then()
			.statusCode(400)
			.body("resourceType", equalTo("OperationOutcome"))
			.body("issue.severity", hasItem("error"))
			.body("issue.code", hasItem("invalid"));
	}
	
	@Test
	public void GET_CodeSystemId() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.when().get(CODESYSTEM_ID, getTestCodeSystemId())
			.then()
			.statusCode(200)
			.body("resourceType", equalTo("CodeSystem"))
			.body("id", equalTo(getTestCodeSystemId()))
			.body("url", equalTo(SnomedTerminologyComponentConstants.SNOMED_URI_BASE))
			.body("status", equalTo("unknown"));
	}
	
	//Summary-count should not be allowed for non-search type operations?
	//https://www.hl7.org/fhir/search.html#summary
	@Test
	public void GET_CodeSystemId_Summary_Count_BadRequest() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("_summary", "count")
			.when().get(CODESYSTEM_ID, getTestCodeSystemId())
			.then()
			.statusCode(400)
			.body("resourceType", equalTo("OperationOutcome"))
			.body("issue.severity", hasItem("error"))
			.body("issue.code", hasItem("invalid"));
	}

}