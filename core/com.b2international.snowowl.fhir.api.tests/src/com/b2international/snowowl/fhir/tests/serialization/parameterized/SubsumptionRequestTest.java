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
package com.b2international.snowowl.fhir.tests.serialization.parameterized;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.b2international.snowowl.fhir.core.model.codesystem.SubsumptionRequest;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.tests.FhirTest;

/**
 * SubsumptionRequest Test
 * @since 6.6
 */
public class SubsumptionRequestTest extends FhirTest {
	
	@Test
	public void lookupRequestCodeTest() throws Exception {
		
		String jsonParam = "{\"resourceType\":\"Parameters\","
				+ "\"parameter\":["
					+ "{\"name\":\"codeA\",\"valueCode\":\"1234\"},"
					+ "{\"name\":\"codeB\",\"valueCode\":\"5678\"},"
					+ "{\"name\":\"system\",\"valueUri\":\"http://snomed.info/sct\"},"
					+ "{\"name\":\"version\",\"valueString\":\"20180131\"}"
					+ "]}";
		
		//Magic to turn the FHIR params -> Parameters -> LookupRequest
		Parameters.Fhir fhirParameters = objectMapper.readValue(jsonParam, Parameters.Fhir.class);
		SubsumptionRequest request = objectMapper.convertValue(fhirParameters.toJson(), SubsumptionRequest.class);
		
		assertEquals("1234", request.getCodeA());
		assertEquals("5678", request.getCodeB());
		assertEquals("http://snomed.info/sct", request.getSystem());
		assertEquals("20180131", request.getVersion());
	}
	
	@Test
	public void lookupRequestCodingTest() throws Exception {
		
		String jsonParam = "{\"resourceType\":\"Parameters\","
				+ "\"parameter\":["
					+ "{\"name\":\"system\",\"valueUri\":\"http://snomed.info/sct\"},"
					+ "{\"name\":\"codingA\", \"valueCoding\":"
							+ "{\"code\":\"1234\","
							+ "\"system\":\"http://snomed.info/sct\","
							+ "\"version\":\"20180131\",\"userSelected\":false}"
						+ "},"
					+ "{\"name\":\"codingB\", \"valueCoding\":"
						+ "{\"code\":\"5678\","
						+ "\"system\":\"http://snomed.info/sct\","
						+ "\"version\":\"20180131\",\"userSelected\":false}"
					+ "}"
				+ "]}";
		
		//Magic to turn the FHIR params -> Parameters -> LookupRequest
		Parameters.Fhir fhirParameters = objectMapper.readValue(jsonParam, Parameters.Fhir.class);
		SubsumptionRequest request = objectMapper.convertValue(fhirParameters.toJson(), SubsumptionRequest.class);
		
		printPrettyJson(request);
		assertEquals("http://snomed.info/sct", request.getSystem());
		assertEquals("1234", request.getCodingA().getCodeValue());
		assertEquals("5678", request.getCodingB().getCodeValue());
		assertEquals("20180131", request.getCodingA().getVersion());
	}
}
