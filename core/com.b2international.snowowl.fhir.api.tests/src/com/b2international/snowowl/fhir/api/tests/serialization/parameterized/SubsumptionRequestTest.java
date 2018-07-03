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
package com.b2international.snowowl.fhir.api.tests.serialization.parameterized;

import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;

import org.junit.Test;

import com.b2international.snowowl.fhir.api.tests.FhirTest;
import com.b2international.snowowl.fhir.core.FhirConstants;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.lookup.LookupRequest;
import com.b2international.snowowl.fhir.core.model.subsumption.SubsumptionRequest;

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
