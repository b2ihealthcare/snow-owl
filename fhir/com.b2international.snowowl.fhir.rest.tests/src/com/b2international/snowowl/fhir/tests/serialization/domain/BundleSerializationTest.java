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

import java.util.Iterator;

import org.junit.Test;

import com.b2international.snowowl.fhir.core.codesystems.BundleType;
import com.b2international.snowowl.fhir.core.codesystems.CodeSystemContentMode;
import com.b2international.snowowl.fhir.core.codesystems.HttpVerb;
import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
import com.b2international.snowowl.fhir.core.model.BatchRequest;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.Entry;
import com.b2international.snowowl.fhir.core.model.Link;
import com.b2international.snowowl.fhir.core.model.RequestEntry;
import com.b2international.snowowl.fhir.core.model.ResourceEntry;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupRequest;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Fhir;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Json;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Test for checking the serialization from model->JSON.
 * @since 6.3
 */
public class BundleSerializationTest extends FhirTest {
	
	@Test
	public void resourceBundleBuildTest() throws Exception {
		
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
	public void bulkRequestBuildTest() throws Exception {
		
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
			
		Bundle bundle = Bundle.builder("bundle_id")
			.language("en")
			.total(1)
			.type(BundleType.SEARCHSET)
			.addLink("self", "http://localhost:8080/snowowl/CodeSystem")
			.addEntry(entry)
			.build();
		
		assertEquals("bundle_id", bundle.getId().getIdValue());
		assertEquals("en", bundle.getLanguage().getCodeValue());
		assertEquals(1, bundle.getTotal());
		assertEquals(BundleType.SEARCHSET.getCode(), bundle.getType());
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
	public void bundleRequestDeserializationTest() throws Exception {
		
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

}
