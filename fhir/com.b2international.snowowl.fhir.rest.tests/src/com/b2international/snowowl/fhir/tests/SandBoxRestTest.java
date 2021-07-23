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
package com.b2international.snowowl.fhir.tests;

import static com.b2international.snowowl.fhir.tests.FhirRestTest.Endpoints.CODESYSTEM;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.b2international.snowowl.core.util.PlatformUtil;
import com.b2international.snowowl.fhir.core.codesystems.BundleType;
import com.b2international.snowowl.fhir.core.model.BatchRequest;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.ParametersRequestEntry;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupRequest;
import com.b2international.snowowl.fhir.core.model.codesystem.SubsumptionResult;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Parameter;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Fhir;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Json;
import com.b2international.snowowl.fhir.rest.tests.AllFhirRestTests;
import com.b2international.snowowl.fhir.tests.domain.CodeSystemTest;
import com.b2international.snowowl.test.commons.BundleStartRule;
import com.b2international.snowowl.test.commons.SnowOwlAppRule;
import com.b2international.snowowl.test.commons.rest.RestExtensions;

import io.restassured.http.ContentType;

/**
 * @since 6.6
 */
public class SandBoxRestTest extends FhirRestTest {
	
	private static final String FHIR_ISSUE_TYPE_CODESYSTEM_URI = "http://hl7.org/fhir/issue-type";
	
	private static final String FHIR_ISSUE_TYPE_CODESYSTEM_ID = "fhir/issue-type";
	
	/**
	 * Execute the tests with this rule if the no dataset needs to be imported
	 */
	@ClassRule
	public static final RuleChain appRule = RuleChain
		.outerRule(SnowOwlAppRule.snowOwl(AllFhirRestTests.class).clearResources(false))
		.around(new BundleStartRule("org.eclipse.jetty.osgi.boot"))
		.around(new BundleStartRule("com.b2international.snowowl.core.rest"));
	
	@Test
	public void createCodeSystem() throws Exception {
		
		File jsonFilePath = PlatformUtil.toAbsolutePathBundleEntry(this.getClass(), "/src/com/b2international/snowowl/fhir/tests/dd_codesystem.json").toFile();
		
		System.out.println(jsonFilePath);
		CodeSystem codeSystem = objectMapper.readValue(jsonFilePath, CodeSystem.class);
		printPrettyJson(codeSystem);
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.contentType(ContentType.JSON)
			.body(codeSystem)
			.when().post("/CodeSystem")
			.then()
			.statusCode(200);
			
	}
	
	@Test
	public void listCodeSystems() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.when().get(CODESYSTEM)
			.prettyPrint();
	}
	
	
	//@Test
	public void restTemplateCallTest() {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(RestExtensions.USER, RestExtensions.PASS);
		MediaType mediaType = MediaType.parseMediaType("application/fhir+json;charset=utf-8");
		headers.setContentType(mediaType);
		HttpEntity<Bundle> request = new HttpEntity<>(headers);
		
		RestTemplate restTemplate = new RestTemplate();

		List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
		MappingJackson2HttpMessageConverter jsonMessageConverter = new MappingJackson2HttpMessageConverter() {

			public boolean canRead(java.lang.Class<?> clazz, org.springframework.http.MediaType mediaType) {
				return true;
			}

			public boolean canRead(java.lang.reflect.Type type, java.lang.Class<?> contextClass,
					org.springframework.http.MediaType mediaType) {
				return true;
			}

			protected boolean canRead(org.springframework.http.MediaType mediaType) {
				return true;
			}
		};

		jsonMessageConverter.setObjectMapper(objectMapper);
		messageConverters.add(jsonMessageConverter);

		restTemplate.setMessageConverters(messageConverters);
		
		ResponseEntity<Bundle> response = restTemplate.exchange("http://localhost:8080/snowowl/fhir/CodeSystem", HttpMethod.GET, request, Bundle.class);

		//ResponseEntity<Bundle> bundle = restTemplate.getForEntity("http://localhost:8080/snowowl/fhir/CodeSystem", Bundle.class, request);
		System.out.println(response.getStatusCodeValue());
		System.out.println(response.getBody().getId());
	}
	
	
	//@Test
	public void bulkRequestTest() {
		
		LookupRequest lookupRequest = LookupRequest.builder()
				.code("23245-4")
				.system("http://loinc.org")
				.build();
		
		Json json1 = new Parameters.Json(lookupRequest);
		System.out.println("JSON params:" + json1);
		
		ParametersRequestEntry entry = ParametersRequestEntry.builder()
				.request(BatchRequest.createPostRequest("CodeSystem/$lookup"))
				.resource(new Parameters.Fhir(json1.parameters()))
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
			.body("type", is("batch-response"));
	}
	
	//@Test
	public void invalidCodeGetTest2() throws Exception {
		
		String responseString = givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("code", "unknownCode")
			.param("url",FHIR_ISSUE_TYPE_CODESYSTEM_URI)
			.param("_format", "json")
			.when().get("/CodeSystem/$validate-code")
			.prettyPeek()
			.then().assertThat()
			.statusCode(200)
			.extract()
			.body()
			.asString();
			
	}
	
	//@Test
	public void printAllCodesystems() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
		.when().get("/CodeSystem").prettyPrint();
	}
	
	//@Test
	public void getCodeSystemLastUpdatedParamTest() {
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("_lastUpdated", "2002-01-31")
			.when().get("/CodeSystem")
			.then()
			.body("resourceType", equalTo("Bundle"))
			.body("total", equalTo(41))
			.body("type", equalTo("searchset"))
			//.body("entry[0].resource.concept", notNullValue())
			.statusCode(200);
	}
	
	//@Test
	public void getCodeSystemLastUpdatedParamTest2() {
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("_lastUpdated", "2002-01-30")
			.when().get("/CodeSystem")
			.then()
			.body("resourceType", equalTo("Bundle"))
			.body("total", equalTo(0))
			.body("type", equalTo("searchset"))
			.statusCode(200);
	}
	
	/*
	 * the name, _id, description and publisher parameters (these 3 additionally support the :exact, :contains and :missing modifiers)
	 */
	//@Test
	public void searchCodeSystemByName() {
			
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("_name", "IssueType")
			.when().get("/CodeSystem").prettyPrint();
		
		/*
			.then()
			.body("resourceType", equalTo("Bundle"))
			.body("total", equalTo(1))
			.body("type", equalTo("searchset"))
			.body("entry[0].resource.concept", notNullValue())
			.statusCode(200);
			*/
	}
	
	//@Test
	public void buildCode() throws IOException {
		Coding coding = Coding.builder()
				.system("http://hl7.org/fhir/issue-severity")
				.code("fatal")
				.build();

			LookupRequest request = LookupRequest.builder()
				.coding(coding)
				.addProperty("name")
				.build();
		
			Json json1 = new Parameters.Json(request);
			Fhir fhir = new Parameters.Fhir(json1.parameters());
			
			String fhirJson = objectMapper.writeValueAsString(fhir);
			System.out.println("This is the JSON request from the client: " + fhirJson);
			
			System.out.println("This is happening in the server-side...");
			Fhir parameters = objectMapper.readValue(fhirJson, Parameters.Fhir.class);
			System.out.println("Deserialized into FHIR parameters..." + parameters.getParameters());
			
			System.out.println("Back to Domain JSON...");
			Json json = new Parameters.Json(parameters);
			Parameters parameters2 = json.parameters();
			List<Parameter> parameters3 = parameters2.getParameters();
			for (Parameter parameter : parameters3) {
				System.out.println("P: " + parameter);
			}
			
			LookupRequest lookupRequest = objectMapper.convertValue(json, LookupRequest.class);
			System.out.println("... and back to the object representation we started from:" + lookupRequest);
			
			
			Json finalJson = new Parameters.Json(lookupRequest);
			Fhir finalFhir = new Parameters.Fhir(finalJson.parameters());
			
			String stringJson = objectMapper.writeValueAsString(finalFhir);
			System.out.println("Final final: " + stringJson);
			/*
			*/
			//String jsonBody = objectMapper.writeValueAsString(fhirParameters);
			//System.out.println("Json: " + jsonBody);
	}
		
	private SubsumptionResult convertToSubsumptionResult(String responseString) throws Exception {
		Fhir parameters = objectMapper.readValue(responseString, Parameters.Fhir.class);
		Json json = new Parameters.Json(parameters);
		return objectMapper.convertValue(json, SubsumptionResult.class);
	}
	
}
