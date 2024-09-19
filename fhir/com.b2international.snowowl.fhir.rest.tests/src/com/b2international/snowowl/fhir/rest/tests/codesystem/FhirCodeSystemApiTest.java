/*
 * Copyright 2011-2024 B2i Healthcare, https://b2ihealthcare.com
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

import static com.b2international.snowowl.fhir.rest.tests.FhirRestTest.Endpoints.CODESYSTEM;
import static com.b2international.snowowl.fhir.rest.tests.FhirRestTest.Endpoints.CODESYSTEM_ID;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.*;

import java.util.UUID;

import org.junit.Test;

import com.b2international.commons.json.Json;
import com.b2international.fhir.FhirCodeSystems;
import com.b2international.snowowl.fhir.core.R5ObjectFields;
import com.b2international.snowowl.fhir.rest.tests.FhirRestTest;
import com.b2international.snowowl.test.commons.codesystem.CodeSystemRestRequests;
import com.b2international.snowowl.test.commons.rest.RestExtensions;

/**
 * FHIR /CodeSystem Resource API Tests
 * 
 * @since 6.6
 */
public class FhirCodeSystemApiTest extends FhirRestTest {
	
	@Test
	public void GET_CodeSystem() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.when().get(CODESYSTEM)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("type", equalTo("searchset"))
			.body("meta.tag.code", not(hasItem(FhirCodeSystems.CODING_SUBSETTED.getCode())))
			.body("total", notNullValue()) // actual number depends on test data, just verify existence
			.body("entry[0].resource.id", equalTo(getTestCodeSystemId()))
			.body("entry[0].resource.url", equalTo(getTestCodeSystemUrl()))
			.body("entry[0].resource.count", equalTo(1943)); // base RF2 package count
	}
	
	@Test
	public void GET_CodeSystem_IdFilter_NoMatch() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("_id", "non-existent")
			.when().get(CODESYSTEM)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("type", equalTo("searchset"))
			.body("meta.tag.code", not(hasItem(FhirCodeSystems.CODING_SUBSETTED.getCode())))
			.body("total", equalTo(0));
	}
	
	@Test
	public void GET_CodeSystem_IdFilter_Match() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("_id", getTestCodeSystemId())
			.when().get(CODESYSTEM)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("type", equalTo("searchset"))
			.body("meta.tag.code", not(hasItem(FhirCodeSystems.CODING_SUBSETTED.getCode())))
			.body("total", equalTo(1))
			.body("entry[0].resource.url", equalTo(getTestCodeSystemUrl()));
	}
	
	@Test
	public void GET_CodeSystem_IdFilter_Match_Multi() throws Exception {
		String anotherCodeSystemId = createCodeSystem(UUID.randomUUID().toString());
		String thirdCodeSystemId = createCodeSystem(UUID.randomUUID().toString());
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("_id", getTestCodeSystemId(), anotherCodeSystemId)
			.when().get(CODESYSTEM)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("type", equalTo("searchset"))
			.body("meta.tag.code", not(hasItem(FhirCodeSystems.CODING_SUBSETTED.getCode())))
			.body("total", equalTo(2))
			.body("entry.resource.id", hasItems(getTestCodeSystemId(), anotherCodeSystemId))
			.body("entry.resource.url", hasItem(getTestCodeSystemUrl()));
	}
	
	@Test
	public void GET_CodeSystem_NameFilter_NoMatch() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("name", RestExtensions.encodeQueryParameter("unknown name"))
			.when().get(CODESYSTEM)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("type", equalTo("searchset"))
			.body("meta.tag.code", not(hasItem(FhirCodeSystems.CODING_SUBSETTED.getCode())))
			.body("total", equalTo(0));
	}
	
	@Test
	public void GET_CodeSystem_NameFilter_Match_Single() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("name", getTestCodeSystemId())
			.when().get(CODESYSTEM)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("type", equalTo("searchset"))
			.body("meta.tag.code", not(hasItem(FhirCodeSystems.CODING_SUBSETTED.getCode())))
			.body("total", equalTo(1))
			.body("entry[0].resource.id", equalTo(getTestCodeSystemId()))
			.body("entry[0].resource.url", equalTo(getTestCodeSystemUrl()));
	}
	
	@Test
	public void GET_CodeSystem_NameFilter_Match_Multiple() {
		String anotherCodeSystemId = createCodeSystem(UUID.randomUUID().toString());
		String thirdCodeSystemId = createCodeSystem(UUID.randomUUID().toString());
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("name", getTestCodeSystemId(), anotherCodeSystemId)
			.when().get(CODESYSTEM)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("meta.tag.code", not(hasItem(FhirCodeSystems.CODING_SUBSETTED.getCode())))
			.body("type", equalTo("searchset"))
			.body("total", equalTo(2))
			.body("entry.resource.id", hasItems(getTestCodeSystemId(), anotherCodeSystemId))
			.body("entry.resource.url", hasItem(getTestCodeSystemUrl()));
	}
	
	@Test
	public void GET_CodeSystem_Summary_True() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("_id", getTestCodeSystemId())
			.queryParam("_summary", true)
			.when().get(CODESYSTEM)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("meta.tag.code", hasItem(FhirCodeSystems.CODING_SUBSETTED.getCode()))
			.body("type", equalTo("searchset"))
			.body("total", equalTo(1))
			.body("entry[0].resource.id", equalTo(getTestCodeSystemId()))
			.body("entry[0].resource.title", equalTo("Title of " + getTestCodeSystemId()))
			.body("entry[0].resource.property", notNullValue())
			.body("entry[0].resource.filter", notNullValue())
			//no concept definitions are part of the summary
			.body("entry.resource", not(hasItem("concept")));
	}
	
	@Test
	public void GET_CodeSystem_Summary_Text() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("_id", getTestCodeSystemId())
			.queryParam("_summary", "text")
			.when().get(CODESYSTEM)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("meta.tag.code", hasItem(FhirCodeSystems.CODING_SUBSETTED.getCode()))
			.body("type", equalTo("searchset"))
			.body("total", equalTo(1))
			// only text, id, meta and mandatory
			.body("entry[0].resource.id", equalTo(getTestCodeSystemId()))
			.body("entry[0].resource.status", equalTo("draft"))
			.body("entry[0].resource.content", equalTo("not-present"))
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
			.queryParam("_id", getTestCodeSystemId())
			.queryParam("_summary", "data")
			.when().get(CODESYSTEM)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("meta.tag.code", hasItem(FhirCodeSystems.CODING_SUBSETTED.getCode()))
			.body("type", equalTo("searchset"))
			.body("total", equalTo(1))
			// only id, meta and mandatory
			.body("entry[0].resource.id", notNullValue())
			.body("entry[0].resource.status", equalTo("draft"))
			.body("entry[0].resource.content", equalTo("not-present"))
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
			.queryParam("_id", getTestCodeSystemId())
			.queryParam("_summary", "count")
			.when().get(CODESYSTEM)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("meta.tag.code", not(hasItem(FhirCodeSystems.CODING_SUBSETTED.getCode())))
			.body("total", equalTo(1))
			.body("type", equalTo("searchset"))
			.body("entry", equalTo(null));
	}
	
	@Test
	public void GET_CodeSystem_Summary_False() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("_id", getTestCodeSystemId())
			.queryParam("_summary", false)
			.when().get(CODESYSTEM)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("meta.tag.code", not(hasItem(FhirCodeSystems.CODING_SUBSETTED.getCode())))
			.body("total", equalTo(1))
			.body("type", equalTo("searchset"))
			.body("entry[0].resource.id", equalTo(getTestCodeSystemId()))
			.body("entry[0].resource.url", equalTo(getTestCodeSystemUrl()));
	}
	
	@Test
	public void GET_CodeSystem_Elements() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("_id", getTestCodeSystemId())
			.queryParam("_elements", "name", "url")
			.when().get(CODESYSTEM)
			.then().assertThat() 
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("meta.tag.code", hasItem(FhirCodeSystems.CODING_SUBSETTED.getCode()))
			.body("total", equalTo(1))
			.body("type", equalTo("searchset"))
			// mandatory fields
			.body("entry[0].resource.status", equalTo("draft"))
			.body("entry[0].resource.content", equalTo("not-present"))
			.body("entry[0].resource.id", equalTo(getTestCodeSystemId()))
			// summary and optional fields
			.body("entry[0].resource.text", nullValue())
			.body("entry[0].resource.count", nullValue())
			.body("entry[0].resource.concept", nullValue()) 
			.body("entry[0].resource.copyright", nullValue()) 
			// requested fields
			.body("entry[0].resource.name", equalTo(getTestCodeSystemId()))
			.body("entry[0].resource.url", equalTo(getTestCodeSystemUrl()));
	}
	
	@Test
	public void GET_CodeSystem_ElementsMixedWithSummaryFields() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("_id", getTestCodeSystemId())
			.queryParam("_elements", R5ObjectFields.CodeSystem.ID, R5ObjectFields.CodeSystem.META, R5ObjectFields.CodeSystem.URL, R5ObjectFields.CodeSystem.VERSION, R5ObjectFields.CodeSystem.NAME, R5ObjectFields.CodeSystem.TITLE, R5ObjectFields.CodeSystem.DATE, R5ObjectFields.CodeSystem.PUBLISHER)
			.when().get(CODESYSTEM)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("meta.tag.code", hasItem(FhirCodeSystems.CODING_SUBSETTED.getCode()))
			.body("total", equalTo(1))
			.body("type", equalTo("searchset"))
			// mandatory fields
			.body("entry[0].resource.id", equalTo(getTestCodeSystemId()))
			.body("entry[0].resource.status", equalTo("draft"))
			.body("entry[0].resource.content", equalTo("not-present"))
			// requested fields
			.body("entry[0].resource.url", equalTo(getTestCodeSystemUrl()))
			.body("entry[0].resource.version", nullValue())
			.body("entry[0].resource.name", equalTo(getTestCodeSystemId()))
			.body("entry[0].resource.title", equalTo("Title of " + getTestCodeSystemId()))
			.body("entry[0].resource.date", nullValue())
			.body("entry[0].resource.publisher", equalTo("SNOMED International"));
	}
	
	@Test
	public void GET_CodeSystem_Elements_Unrecognized() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("_elements", "xyz", "abcs")
			.when().get(CODESYSTEM)
			.then().assertThat()
			.statusCode(400)
			.body("resourceType", equalTo("OperationOutcome"))
			.body("issue.severity", hasItem("error"))
			.body("issue.code", hasItem("invalid"));
	}
	
	@Test
	public void GET_CodeSystem_Url_NoMatch() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("url", "http://unknown.com")
			.when().get(CODESYSTEM)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("meta.tag.code", not(hasItem(FhirCodeSystems.CODING_SUBSETTED.getCode())))
			.body("total", equalTo(0))
			.body("type", equalTo("searchset"));
	}
	
	@Test
	public void GET_CodeSystem_Url_Match_Single() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("url", SNOMEDCT_URL)
			.when().get(CODESYSTEM)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("meta.tag.code", not(hasItem(FhirCodeSystems.CODING_SUBSETTED.getCode())))
			.body("total", equalTo(1))
			.body("type", equalTo("searchset"))
			.body("entry[0].resource.id", equalTo("SNOMEDCT"))
			.body("entry[0].resource.url", equalTo(SNOMEDCT_URL));
	}
	
	@Test
	public void GET_CodeSystem_Url_Match_Multiple() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("url", SNOMEDCT_URL, getTestCodeSystemUrl())
			.when().get(CODESYSTEM)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("meta.tag.code", not(hasItem(FhirCodeSystems.CODING_SUBSETTED.getCode())))
			.body("total", equalTo(2))
			.body("type", equalTo("searchset"))
			.body("entry.resource.id", hasItems("SNOMEDCT", getTestCodeSystemId()))
			.body("entry.resource.url", hasItems(SNOMEDCT_URL, getTestCodeSystemUrl()));
	}

	@Test
	public void GET_CodeSystem_System_NoMatch() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("system", "http://unknown.com")
			.when().get(CODESYSTEM)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("meta.tag.code", not(hasItem(FhirCodeSystems.CODING_SUBSETTED.getCode())))
			.body("total", equalTo(0))
			.body("type", equalTo("searchset"));
	}
	
	@Test
	public void GET_CodeSystem_System_Match_Single() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("system", SNOMEDCT_URL)
			.when().get(CODESYSTEM)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("meta.tag.code", not(hasItem(FhirCodeSystems.CODING_SUBSETTED.getCode())))
			.body("total", equalTo(1))
			.body("type", equalTo("searchset"))
			.body("entry[0].resource.id", equalTo("SNOMEDCT"))
			.body("entry[0].resource.url", equalTo(SNOMEDCT_URL));
	}
	
	@Test
	public void GET_CodeSystem_System_Match_Multiple() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("system", SNOMEDCT_URL, getTestCodeSystemUrl())
			.when().get(CODESYSTEM)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("meta.tag.code", not(hasItem(FhirCodeSystems.CODING_SUBSETTED.getCode())))
			.body("total", equalTo(2))
			.body("type", equalTo("searchset"))
			.body("entry.resource.id", hasItems("SNOMEDCT", getTestCodeSystemId()))
			.body("entry.resource.url", hasItems(SNOMEDCT_URL, getTestCodeSystemUrl()));
	}
	
	@Test
	public void GET_CodeSystem_System_And_Url_Intersection_Match() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("url", SNOMEDCT_URL, getTestCodeSystemUrl())
			.queryParam("system", SNOMEDCT_URL)
			.when().get(CODESYSTEM)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("meta.tag.code", not(hasItem(FhirCodeSystems.CODING_SUBSETTED.getCode())))
			.body("total", equalTo(1))
			.body("type", equalTo("searchset"))
			.body("entry[0].resource.id", equalTo("SNOMEDCT"))
			.body("entry[0].resource.url", equalTo(SNOMEDCT_URL));
	}
	
	@Test
	public void GET_CodeSystem_Version_NoMatch() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("version", "unknown-version")
			.when().get(CODESYSTEM)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("meta.tag.code", not(hasItem(FhirCodeSystems.CODING_SUBSETTED.getCode())))
			.body("total", equalTo(0))
			.body("type", equalTo("searchset"));
	}
	
	@Test
	public void GET_CodeSystem_Version_Match_Single() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("version", "2002-01-31")
			.when().get(CODESYSTEM)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("meta.tag.code", not(hasItem(FhirCodeSystems.CODING_SUBSETTED.getCode())))
			.body("total", equalTo(1))
			.body("type", equalTo("searchset"))
			.body("entry[0].resource.id", equalTo("SNOMEDCT/2002-01-31"))
			.body("entry[0].resource.url", equalTo(SNOMEDCT_URL + "/version/20020131"))
			.body("entry[0].resource.version", equalTo("2002-01-31"))
			.body("entry[0].resource.date", equalTo("2002-01-31T00:00:00Z"));
	}
	
	@Test
	public void GET_CodeSystem_Version_Match_Multiple() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("version", "2002-01-31", "2020-01-31")
			.when().get(CODESYSTEM)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("meta.tag.code", not(hasItem(FhirCodeSystems.CODING_SUBSETTED.getCode())))
			.body("total", equalTo(2))
			.body("type", equalTo("searchset"))
			.body("entry[0].resource.id", equalTo("SNOMEDCT/2002-01-31"))
			.body("entry[0].resource.url", equalTo(SNOMEDCT_URL + "/version/20020131"))
			.body("entry[0].resource.version", equalTo("2002-01-31"))
			.body("entry[0].resource.date", equalTo("2002-01-31T00:00:00Z"))
			.body("entry[1].resource.id", equalTo("SNOMEDCT/2020-01-31"))
			.body("entry[1].resource.url", equalTo(SNOMEDCT_URL + "/version/20200131"))
			.body("entry[1].resource.version", equalTo("2020-01-31"))
			.body("entry[1].resource.date", equalTo("2020-01-31T00:00:00Z"));
	}
	
	@Test
	public void GET_CodeSystem_Status_NoMatch() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("status", "unknown")
			.when().get(CODESYSTEM)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("meta.tag.code", not(hasItem(FhirCodeSystems.CODING_SUBSETTED.getCode())))
			.body("total", equalTo(0))
			.body("type", equalTo("searchset"));
	}
	
	@Test
	public void GET_CodeSystem_Status_Match_Single() throws Exception {
		CodeSystemRestRequests.updateCodeSystem(getTestCodeSystemId(), Json.object("status", "mysterious"))
			.statusCode(204);
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("status", "mysterious")
			.queryParam("_sort", "id")
			.when().get(CODESYSTEM)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("meta.tag.code", not(hasItem(FhirCodeSystems.CODING_SUBSETTED.getCode())))
			.body("total", equalTo(1))
			.body("type", equalTo("searchset"))
			.body("entry[0].resource.id", equalTo("GET_CodeSystem_Status_Match_Single"))
			.body("entry[0].resource.url", equalTo(SNOMEDCT_URL + "/GET_CodeSystem_Status_Match_Single"))
			// This is the PublicationStatus code "mysterious" maps to
			.body("entry[0].resource.status", equalTo("unknown"));
	}
	
	@Test
	public void GET_CodeSystem_Status_Match_Multiple() throws Exception {
		CodeSystemRestRequests.updateCodeSystem(getTestCodeSystemId(), Json.object("status", "mysterious"))
			.statusCode(204);

		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("status", "active", "mysterious")
			.queryParam("_sort", "id")
			.when().get(CODESYSTEM)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("meta.tag.code", not(hasItem(FhirCodeSystems.CODING_SUBSETTED.getCode())))
			.body("total", equalTo(39))
			.body("type", equalTo("searchset"))
			.body("entry[0].resource.id", equalTo("GET_CodeSystem_Status_Match_Multiple"))
			.body("entry[0].resource.url", equalTo(SNOMEDCT_URL + "/GET_CodeSystem_Status_Match_Multiple"))
			// This is the PublicationStatus code "mysterious" maps to
			.body("entry[0].resource.status", equalTo("unknown"))
			.body("entry[1].resource.id", equalTo("SNOMEDCT"))
			.body("entry[1].resource.url", equalTo(SNOMEDCT_URL))
			.body("entry[1].resource.status", equalTo("active"));
	}
	
	@Test
	public void GET_CodeSystemId() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.when().get(CODESYSTEM_ID, getTestCodeSystemId())
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("CodeSystem"))
			.body("id", equalTo(getTestCodeSystemId()))
			.body("url", equalTo(getTestCodeSystemUrl()))
			.body("status", equalTo("draft"));
	}
	
	@Test
	public void GET_CodeSystemId_Versioned() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.when().get(CODESYSTEM_ID, "SNOMEDCT/2002-01-31")
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("CodeSystem"))
			.body("id", equalTo("SNOMEDCT/2002-01-31"))
			.body("url", equalTo(SNOMEDCT_URL + "/version/20020131"))
			.body("status", equalTo("active"))
			.body("version", equalTo("2002-01-31"))
			.body("language", equalTo("ENG"))
			.body("publisher", equalTo("SNOMED International"))
			.body("contact[0].telecom[0].system", equalTo("url"))
			.body("contact[0].telecom[0].value", equalTo("https://b2ihealthcare.com"));
	}
	
	//Summary-count should not be allowed for non-search type operations?
	//https://www.hl7.org/fhir/search.html#summary
	@Test
	public void GET_CodeSystemId_Summary_Count_BadRequest() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("_summary", "count")
			.when().get(CODESYSTEM_ID, getTestCodeSystemId())
			.then().assertThat()
			.statusCode(400)
			.body("resourceType", equalTo("OperationOutcome"))
			.body("issue.severity", hasItem("error"))
			.body("issue.code", hasItem("invalid"));
	}
}
