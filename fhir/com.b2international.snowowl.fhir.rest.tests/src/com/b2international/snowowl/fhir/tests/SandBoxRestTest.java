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

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;

import com.b2international.snowowl.fhir.core.model.codesystem.LookupRequest;
import com.b2international.snowowl.fhir.core.model.codesystem.SubsumptionResult;
import com.b2international.snowowl.fhir.core.model.codesystem.SubsumptionResult.SubsumptionType;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Parameter;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Fhir;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Json;

/**
 * CodeSystem REST end-point test cases
 * 
 * parameter: [ {
 *      "name" : "version",
 *      "valueString" : "v1"
 *    },
 *    {
 *      "name": "property",
 *      "valueCode" : "whateverCode"
 *    },
 *    {
 *       "name" : "property",
 *       "part" : [
 *           "name" : "code"
 *           "valueCode" : "whateverCode"
 *       ]
 *     },
 *     ..
 * ]
 * 
 * 
 * @since 6.6
 */
public class SandBoxRestTest extends FhirRestTest {
	
	private static final String FHIR_ISSUE_TYPE_CODESYSTEM_URI = "http://hl7.org/fhir/issue-type";
	
	private static final String FHIR_ISSUE_TYPE_CODESYSTEM_ID = "fhir:issue-type";
	
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
	
	/*
    "resourceType": "CodeSystem",
    "id": "snomedStore://MAIN/2018-01-31",
    "meta": {
        "lastUpdated": "1969-12-31T23:59:59.999Z"
    },
    "language": "en",
    "text": {
        "status": "additional",
        "div": "<div>SNOMED CT contributes to the improvement of patient care by underpinning the development of Electronic Health Records that record clinical information in ways that enable meaning-based retrieval. This provides effective access to information required for decision support and consistent reporting and analysis. Patients benefit from the use of SNOMED CT because it improves the recording of EHR information and facilitates better communication, leading to improvements in the quality of care.</div>"
    },
    "url": "http://snomed.info/sct/version/20180131",
    "identifier": {
        "use": "official",
        "system": "http://www.snomed.org",
        "value": "2.16.840.1.113883.6.96"
    },
    "version": "2018-01-31",
    "name": "SNOMEDCT",
    "title": "SNOMED CT",
    "status": "active",
    "publisher": "http://www.snomed.org",
    "description": "SNOMED CT contributes to the improvement of patient care by underpinning the development of Electronic Health Records that record clinical information in ways that enable meaning-based retrieval. This provides effective access to information required for decision support and consistent reporting and analysis. Patients benefit from the use of SNOMED CT because it improves the recording of EHR information and facilitates better communication, leading to improvements in the quality of care.",
    "hierarchyMeaning": "is-a",
    "content": "not-present",
    "count": 448216,
	*/
	
	//Fully detailed SNOMED CT code system
	//@Test
	public void getSnomedCodeSystemTest() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
		 	.pathParam("id", "snomedStore/SNOMEDCT") 
		 	//.param("_elements", "filter")
			.when().get("/CodeSystem/{id}")
			.prettyPrint();
	}
	
	//Fully detailed SNOMED CT code system
	//@Test
	public void getSnomedCodeSystemVersionTest() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
		 	.pathParam("id", "snomedStore:MAIN/2018-01-31") 
		 	//.param("_elements", "filter")
			.when().get("/CodeSystem/{id}")
			.prettyPrint();
	}
	
	//@Test
	public void subsumedByWithVersionTest() throws Exception {
		
		String responseString = givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("codeA", "413029008") //Monospecific reactions
			.param("codeB", "59524001") //59524001 - Blood bank procedure (parent)
			.param("system", "http://snomed.info/sct/900000000000207008/version/20180131")
			.when().get("/CodeSystem/$subsumes")
			.asString();
		
		SubsumptionResult result = convertToSubsumptionResult(responseString);
		Assert.assertEquals(SubsumptionType.SUBSUMED_BY, result.getOutcome());
	}
	
	private SubsumptionResult convertToSubsumptionResult(String responseString) throws Exception {
		Fhir parameters = objectMapper.readValue(responseString, Parameters.Fhir.class);
		Json json = new Parameters.Json(parameters);
		return objectMapper.convertValue(json, SubsumptionResult.class);
	}
	
}
