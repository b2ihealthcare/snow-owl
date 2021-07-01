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
package com.b2international.snowowl.fhir.rest.tests.batch;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import com.b2international.snowowl.fhir.core.codesystems.BundleType;
import com.b2international.snowowl.fhir.core.model.BatchRequest;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.ParametersRequestEntry;
import com.b2international.snowowl.fhir.core.model.RequestEntry;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupRequest;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Json;
import com.b2international.snowowl.fhir.rest.tests.AllFhirRestTests;
import com.b2international.snowowl.fhir.tests.FhirRestTest;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.test.commons.BundleStartRule;
import com.b2international.snowowl.test.commons.Resources;
import com.b2international.snowowl.test.commons.SnomedContentRule;
import com.b2international.snowowl.test.commons.SnowOwlAppRule;

/**
 * Tests for batch REST operations
 * @since 8.0.0
 */
public class BatchApiRestTest extends FhirRestTest {
	
	@Test
	public void singleLookupGET() {
		
		RequestEntry entry = RequestEntry.builder()
				.request(BatchRequest.createGetRequest("CodeSystem/$lookup?"
						+ "system=http://snomed.info/sct"
						+ "&code=" + Concepts.IS_A))
				.build();
			
		Bundle bundle = Bundle.builder()
			.language("en")
			.total(1)
			.type(BundleType.BATCH)
			.addEntry(entry)
			.build();
		
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.contentType(APPLICATION_FHIR_JSON)
			.body(bundle)
			.when().post("/")
			.prettyPeek()
			.then()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("type", is("batch-response"))
			.root("entry[0]")
			.body("response.status", equalTo("200"))
			.root("entry[0].resource")
			.body("resourceType", equalTo("Parameters"));
		
	}
	
	@Test
	public void singleLookupPost() {
		
		LookupRequest request = LookupRequest.builder()
				.coding(Coding.builder()
						.system("http://snomed.info/sct")
						.code(Concepts.IS_A)
						.build())
				.build();
		
		Json json = new Parameters.Json(request);
		
		ParametersRequestEntry entry = ParametersRequestEntry.builder()
				.request(BatchRequest.createPostRequest("CodeSystem/$lookup"))
				.resource(new Parameters.Fhir(json.parameters()))
				.build();
		
		Bundle bundle = Bundle.builder()
				.language("en")
				.total(1)
				.type(BundleType.BATCH)
				.addEntry(entry)
				.build();
			
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
				.contentType(APPLICATION_FHIR_JSON)
				.body(bundle)
				.when().post("/")
				.prettyPeek()
				.then()
				.statusCode(200)
				.body("resourceType", equalTo("Bundle"))
				.body("type", is("batch-response"))
				.root("entry[0]")
				.body("response.status", equalTo("200"))
				.root("entry[0].resource")
				.body("resourceType", equalTo("Parameters"));
		
	}
	
	@Test
	public void multiRequestWithError() {
		
		LookupRequest request = LookupRequest.builder()
				.coding(Coding.builder()
						.system("http://snomed.info/sct")
						.code(Concepts.IS_A)
						.build())
				.build();
		
		Json json = new Parameters.Json(request);
		
		ParametersRequestEntry entry = ParametersRequestEntry.builder()
				.request(BatchRequest.createPostRequest("CodeSystem/$lookup"))
				.resource(new Parameters.Fhir(json.parameters()))
				.build();
		
		RequestEntry entry2 = RequestEntry.builder()
				.request(BatchRequest.createGetRequest("CodeSystem/$lookup?system=whatever?code=1234"))
				.build();
		
		Bundle bundle = Bundle.builder()
				.language("en")
				.total(2)
				.type(BundleType.BATCH)
				.addEntry(entry)
				.addEntry(entry2)
				.build();
			
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
				.contentType(APPLICATION_FHIR_JSON)
				.body(bundle)
				.when().post("/")
				.prettyPeek()
				.then()
				.statusCode(200)
				.body("resourceType", equalTo("Bundle"))
				.body("type", is("batch-response"))
				.body("entry[0].response.status", equalTo("200"))
				.body("entry[0].resource.resourceType", equalTo("Parameters"))
				.body("entry[1].response.status", equalTo("500"))
				.body("entry[1].resource.resourceType", equalTo("OperationOutcome"));
		
	}
	
	@Test
	public void multiRequestWithError2() {
		
		LookupRequest request = LookupRequest.builder()
				.coding(Coding.builder()
						.system("http://snomed.info/sct")
						.code(Concepts.IS_A)
						.build())
				.build();
		
		Json json = new Parameters.Json(request);
		
		ParametersRequestEntry entry = ParametersRequestEntry.builder()
				.request(BatchRequest.createPostRequest("CodeSystem/$lookup"))
				.resource(new Parameters.Fhir(json.parameters()))
				.build();
		
		RequestEntry entry2 = RequestEntry.builder()
				.request(BatchRequest.createGetRequest("CodeSystem/$lookup?system=whatever&code=1234"))
				.build();
		
		Bundle bundle = Bundle.builder()
				.language("en")
				.total(2)
				.type(BundleType.BATCH)
				.addEntry(entry)
				.addEntry(entry2)
				.build();
			
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
				.contentType(APPLICATION_FHIR_JSON)
				.body(bundle)
				.when().post("/")
				.prettyPeek()
				.then()
				.statusCode(200)
				.body("resourceType", equalTo("Bundle"))
				.body("type", is("batch-response"))
				.body("entry[0].response.status", equalTo("200"))
				.root("entry[0].resource")
				.body("entry[0].resource.resourceType", equalTo("Parameters"))
				.body("entry[1].response.status", equalTo("404"))
				.body("entry[1].resource.resourceType", equalTo("OperationOutcome"));
		
	}
	
	@ClassRule
	public static final RuleChain appRule = RuleChain
		.outerRule(SnowOwlAppRule.snowOwl(AllFhirRestTests.class).clearResources(false))
		.around(new BundleStartRule("org.eclipse.jetty.osgi.boot"))
		.around(new BundleStartRule("com.b2international.snowowl.core.rest"));
	
	//@ClassRule
	public static final RuleChain APPRULE = RuleChain
		.outerRule(SnowOwlAppRule.snowOwl(AllFhirRestTests.class).clearResources(false))
		.around(new BundleStartRule("org.eclipse.jetty.osgi.boot"))
		.around(new BundleStartRule("com.b2international.snowowl.core.rest"))
		.around(new SnomedContentRule(SnomedContentRule.SNOMEDCT, Resources.Snomed.MINI_RF2_INT, Rf2ReleaseType.FULL));
	

}
