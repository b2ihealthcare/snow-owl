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
package com.b2international.snowowl.fhir.tests.serialization.domain;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;

import com.b2international.snowowl.fhir.core.codesystems.*;
import com.b2international.snowowl.fhir.core.model.*;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupRequest;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupResult;
import com.b2international.snowowl.fhir.core.model.codesystem.Property;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Fhir;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Json;
import com.b2international.snowowl.fhir.core.model.dt.SubProperty;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Test for checking the serialization of Bundles from model->JSON and back.
 * @since 6.3
 */
public class BundleSerializationTest extends FhirTest {
	
	@Test
	public void resourceBundleSerializationTest() throws Exception {
		
		CodeSystem codeSystem = CodeSystem.builder("repo/shortName")
			.status(PublicationStatus.ACTIVE)
			.name("Local code system")
			.content(CodeSystemContentMode.COMPLETE)
			.url(new Uri("code system uri"))
			.build();
		
		ResourceEntry entry = ResourceEntry.builder().fullUrl("full_Url").resource(codeSystem).build();
		
		Bundle bundle = Bundle.builder("bundle_Id?")
			.language("en")
			.total(1)
			.type(BundleType.SEARCHSET)
			.addLink("self", "http://localhost:8080/snowowl/CodeSystem")
			.addEntry(entry)
			.build();
		
		applyFilter(bundle);
		
		printPrettyJson(bundle);
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(bundle));
		
		assertThat(jsonPath.getString("resourceType"), equalTo("Bundle"));
		assertThat(jsonPath.getString("id"), equalTo("bundle_Id?"));
		assertThat(jsonPath.getString("language"), equalTo("en"));
		assertThat(jsonPath.getString("type"), equalTo("searchset"));
		assertThat(jsonPath.getInt("total"), equalTo(1));
		
		jsonPath.setRoot("link[0]");
		
		assertThat(jsonPath.getString("relation"), equalTo("self"));
		assertThat(jsonPath.getString("url"), equalTo("http://localhost:8080/snowowl/CodeSystem"));
		
		jsonPath.setRoot("entry[0]");
		
		assertThat(jsonPath.getString("fullUrl"), equalTo("full_Url"));
		jsonPath.setRoot("entry[0].resource");
		
		assertThat(jsonPath.getString("resourceType"), equalTo("CodeSystem"));
		assertThat(jsonPath.getString("id"), equalTo("repo/shortName"));
		assertThat(jsonPath.getString("url"), equalTo("code system uri"));
		assertThat(jsonPath.getString("name"), equalTo("Local code system"));
		assertThat(jsonPath.getString("status"), equalTo("active"));
		assertThat(jsonPath.getString("content"), equalTo("complete"));
	}
	
	@Test
	public void requestBundleBuildTest() throws Exception {
		
		LookupRequest lookupRequest = LookupRequest.builder()
				.code("23245-4")
				.system("http://loinc.org")
				.build();
		
		Json json1 = new Parameters.Json(lookupRequest);
		System.out.println("JSON params:" + json1);
		
		RequestEntry entry = RequestEntry.builder()
				.request(BatchRequest.createPostRequest("CodeSystem/$lookup"))
				.resource(new Parameters.Fhir(json1.parameters()))
				.build();
			
		Bundle bundle = Bundle.builder()
			.language("en")
			.total(1)
			.type(BundleType.BATCH)
			.addLink("self", "http://localhost:8080/snowowl/CodeSystem")
			.addEntry(entry)
			.build();
		
		assertEquals("en", bundle.getLanguage().getCodeValue());
		assertEquals(1, bundle.getTotal());
		assertEquals(BundleType.BATCH.getCode(), bundle.getType());
		Link link = bundle.getLink().iterator().next();
		assertEquals("self", link.getRelation());
		assertEquals("http://localhost:8080/snowowl/CodeSystem", link.getUrl().getUriValue());
		
		Entry bundleEntry = bundle.getItems().iterator().next();
		assertTrue(bundleEntry instanceof RequestEntry);
		RequestEntry requestEntry = (RequestEntry) bundleEntry;
		BatchRequest batchRequest = requestEntry.getRequest();
		
		assertEquals(HttpVerb.POST.getCode(), batchRequest.getMethod());
		assertEquals("CodeSystem/$lookup", batchRequest.getUrl().getUriValue());
		
		Fhir requestResource = requestEntry.getRequestResource();
		
		//Back to Domain JSON...
		Json json = new Parameters.Json(requestResource);
		LookupRequest returnedLookupRequest = objectMapper.convertValue(json, LookupRequest.class);
		assertEquals("23245-4", returnedLookupRequest.getCode());
		assertEquals("http://loinc.org", returnedLookupRequest.getSystem());
		
		printPrettyJson(bundle);
			
	}
	
	@Test
	public void requestBundleDeserializationTest() throws Exception {
		
		String jsonCoding =   "{ \"type\" : \"batch\", "
				+ "\"resourceType\" : \"Bundle\", "
				+ "\"entry\" : "
					+ "[{\"request\" : "
						+ "{\"method\" : \"POST\","
						+ "\"url\" : \"CodeSystem/$lookup\"},"
					+ "\"resource\" : "
						+ "{\"resourceType\" : \"Parameters\","
						+ "\"parameter\" : ["
							+ "{\"valueUri\" : \"http://loinc.org\","
							+ "\"name\" : \"system\"},"
							+ "{\"valueCode\" : \"23245-4\","
							+ "\"name\" : \"code\"}"
							+ "]"
						+ "}"
					+ "},"
					+ "{\"request\" : "
						+ "{\"method\" : \"POST\","
						+ "\"url\" : \"CodeSystem/$lookup\"},"
					+ "\"resource\" : "
						+ "{\"resourceType\" : \"Parameters\","
						+ "\"parameter\" : ["
							+ "{\"name\" : \"system\","
							+ "\"valueUri\" : \"http://snomed.info/sct\"}"
							+ ",{\"valueCode\" : \"263495000\","
							+ "\"name\" : \"code\"}"
						+ "]"
					+ "}}"
				+ "]}";
		
		Bundle bundle = objectMapper.readValue(jsonCoding, Bundle.class);
		
		assertEquals(BundleType.BATCH.getCode(), bundle.getType());
		
		Iterator<Entry> iterator = bundle.getItems().iterator();
		Entry bundleEntry = iterator.next();
		assertTrue(bundleEntry instanceof RequestEntry);
		RequestEntry requestEntry = (RequestEntry) bundleEntry;
		BatchRequest batchRequest = requestEntry.getRequest();
		
		assertEquals(HttpVerb.POST.getCode(), batchRequest.getMethod());
		assertEquals("CodeSystem/$lookup", batchRequest.getUrl().getUriValue());
		
		Fhir requestResource = requestEntry.getRequestResource();
		
		//Back to Domain JSON...
		Json json = new Parameters.Json(requestResource);
		LookupRequest returnedLookupRequest = objectMapper.convertValue(json, LookupRequest.class);
		assertEquals("23245-4", returnedLookupRequest.getCode());
		assertEquals("http://loinc.org", returnedLookupRequest.getSystem());
		
		bundleEntry = iterator.next();
		assertTrue(bundleEntry instanceof RequestEntry);
		requestEntry = (RequestEntry) bundleEntry;
		batchRequest = requestEntry.getRequest();
		
		assertEquals(HttpVerb.POST.getCode(), batchRequest.getMethod());
		assertEquals("CodeSystem/$lookup", batchRequest.getUrl().getUriValue());
		
		requestResource = requestEntry.getRequestResource();
		
		//Back to Domain JSON...
		json = new Parameters.Json(requestResource);
		returnedLookupRequest = objectMapper.convertValue(json, LookupRequest.class);
		assertEquals("263495000", returnedLookupRequest.getCode());
		assertEquals("http://snomed.info/sct", returnedLookupRequest.getSystem());
	}
	
	@Test
	public void responseBundleBuildTest() throws Exception {
		
		LookupResult lookupResult = LookupResult.builder()
				.name("test")
				.display("display")
				.addDesignation(Designation.builder()
						.value("dValue")
						.languageCode("uk").build())
				.addProperty(Property.builder()
						.code("1234")
						.description("propDescription")
						.valueString("stringValue")
						.addSubProperty(SubProperty.builder()
							.code("subCode")
							.description("subCodeDescription")
							.valueInteger(1)
							.build())
						.build())
				.build();
		
		
		Json json1 = new Parameters.Json(lookupResult);
		System.out.println("JSON params:" + json1);
		
		
		ResponseEntry entry = ResponseEntry.builder()
				.resource(new Parameters.Fhir(json1.parameters()))
				.response(BatchResponse.createOkResponse())
				.build();
		
		Bundle bundle = Bundle.builder()
			.language("en")
			.type(BundleType.BATCH_RESPONSE)
			.addLink("self", "http://localhost:8080/snowowl/CodeSystem")
			.addEntry(entry)
			.build();
		
		assertEquals("en", bundle.getLanguage().getCodeValue());
		assertEquals(BundleType.BATCH_RESPONSE.getCode(), bundle.getType());
		Link link = bundle.getLink().iterator().next();
		assertEquals("self", link.getRelation());
		assertEquals("http://localhost:8080/snowowl/CodeSystem", link.getUrl().getUriValue());
		
		Entry bundleEntry = bundle.getItems().iterator().next();
		assertTrue(bundleEntry instanceof ResponseEntry);
		ResponseEntry responseEntry = (ResponseEntry) bundleEntry;
		assertEquals("200", responseEntry.getResponse().getStatus());
		
		Fhir requestResource = responseEntry.getResponseResource();
		
		//Back to Domain JSON...
		Json json = new Parameters.Json(requestResource);
		LookupResult returnedResponse = objectMapper.convertValue(json, LookupResult.class);
		assertEquals("test", returnedResponse.getName());
		assertEquals("display", returnedResponse.getDisplay());
		
		printPrettyJson(bundle);
			
	}
	
	@Test
	public void responseBundleDeserializationTest() throws Exception {
		String jsonResponse = "{\"resourceType\":\"Bundle\","
			+ "\"id\":\"ID\","
			+ "\"type\":\"batch-response\","
			+ "\"link\":["
				+ "{"
					+ "\"relation\":\"self\","
					+ "\"url\":\"http://b2i.sg\""
				+ "}"
			+ "],"
			+ "\"entry\":["
				+ "{"
					+ "\"resource\":{"
						+ "\"resourceType\":\"Parameters\","
						+ "\"parameter\":["
							+ "{"
								+ "\"name\":\"name\","
								+ "\"valueString\":\"LOINC\""
							+ "},"
							+ "{"
								+ "\"name\":\"version\","
								+ "\"valueString\":\"2.61\""
							+ "},"
							+ "{"
								+ "\"name\":\"display\","
								+ "\"valueString\":\"LOINC code label\""
							+ "},"
								+ "{"
									+ "\"name\":\"property\","
									+ "\"part\":["
										+ "{"
											+ "\"name\":\"code\","
											+ "\"valueCode\":\"parent\""
										+ "},"
										+ "{"
											+ "\"name\":\"value\","
											+ "\"valueCode\":\"Parent code\""
										+ "}"
									+ "]"
								+ "},"
								+ "{"
									+ "\"name\":\"designation\","
									+ "\"part\":["
										+ "{"
											+ "\"name\":\"language\","
											+ "\"valueCode\":\"en\""
										+ "},"
										+ "{"
											+ "\"name\":\"use\","
											+ "\"valueCoding\":{"
												+ "\"system\":\"http://snomed.info/sct\","
												+ "\"code\":\"900000000000013009\","
												+ "\"display\":\"Synonym\""
											+ "}"
										+ "},"
										+ "{"
											+ "\"name\":\"value\","
											+ "\"valueString\":\"SNOMED CT synonym\""
										+ "}"
									+ "]"
								+ "}"
							+ "]"
						+ "},"
						+ "\"response\":{"
							+ "\"status\":\"200\""
							+ "}"
						+ "}"
					+ "]"
				+ "}";
	
		Bundle bundle = objectMapper.readValue(jsonResponse, Bundle.class);
		
		assertEquals(BundleType.BATCH_RESPONSE.getCode(), bundle.getType());
		
		Iterator<Entry> iterator = bundle.getItems().iterator();
		Entry bundleEntry = iterator.next();
		assertTrue(bundleEntry instanceof ResponseEntry);
		ResponseEntry responseEntry = (ResponseEntry) bundleEntry;
		
		Fhir responseResource = responseEntry.getResponseResource();
		
		//Back to Domain JSON...
		Json json = new Parameters.Json(responseResource);
		LookupResult lookupResult = objectMapper.convertValue(json, LookupResult.class);
		assertEquals("LOINC", lookupResult.getName());
		printPrettyJson(lookupResult);
		
	}
	
	@Test
	public void responseMixedBundleBuildTest() throws Exception {
		
		LookupResult lookupResult = LookupResult.builder()
				.name("test")
				.display("display")
				.addDesignation(Designation.builder()
						.value("dValue")
						.languageCode("uk").build())
				.addProperty(Property.builder()
						.code("1234")
						.description("propDescription")
						.valueString("stringValue")
						.build())
				.build();
		
		OperationOutcome operationOutcome = OperationOutcome.builder()
			.addIssue(Issue.builder().code(IssueType.CODE_INVALID).diagnostics("Invalid code").severity(IssueSeverity.ERROR).build())
			.build();
		
		
		Json json1 = new Parameters.Json(lookupResult);
		System.out.println("JSON params:" + json1);
		
		ResponseEntry lookupResultEntry = ResponseEntry.builder()
				.resource(new Parameters.Fhir(json1.parameters()))
				.response(BatchResponse.createOkResponse())
				.build();
		
		OperationOutcomeEntry operationOutcomeEntry = OperationOutcomeEntry.builder().operationOutcome(operationOutcome).build();
		
		Bundle bundle = Bundle.builder()
			.language("en")
			.type(BundleType.BATCH_RESPONSE)
			.addLink("self", "http://localhost:8080/snowowl/CodeSystem")
			.addEntry(lookupResultEntry)
			.addEntry(operationOutcomeEntry)
			.build();
		
		assertEquals("en", bundle.getLanguage().getCodeValue());
		assertEquals(BundleType.BATCH_RESPONSE.getCode(), bundle.getType());
		Link link = bundle.getLink().iterator().next();
		assertEquals("self", link.getRelation());
		assertEquals("http://localhost:8080/snowowl/CodeSystem", link.getUrl().getUriValue());
		
		Iterator<Entry> iterator = bundle.getItems().iterator();
		Entry bundleEntry = iterator.next();
		assertTrue(bundleEntry instanceof ResponseEntry);
		ResponseEntry responseEntry = (ResponseEntry) bundleEntry;
		assertEquals("200", responseEntry.getResponse().getStatus());
		
		Fhir requestResource = responseEntry.getResponseResource();
		
		//Back to Domain JSON...
		Json json = new Parameters.Json(requestResource);
		LookupResult returnedResponse = objectMapper.convertValue(json, LookupResult.class);
		assertEquals("test", returnedResponse.getName());
		assertEquals("display", returnedResponse.getDisplay());

		bundleEntry = iterator.next();
		assertTrue(bundleEntry instanceof OperationOutcomeEntry);
		OperationOutcomeEntry ooce = (OperationOutcomeEntry) bundleEntry;
		Collection<Issue> issues = ooce.getOperationOutcome().getIssues();
		assertEquals(1, issues.size());
		assertEquals(IssueSeverity.ERROR.getCode(), issues.iterator().next().getSeverity());
		
		printPrettyJson(bundle);
			
	}
	
}
