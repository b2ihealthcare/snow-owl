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
package com.b2international.snowowl.fhir.api.tests.serialization.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.b2international.snowowl.fhir.api.tests.FhirTest;
import com.b2international.snowowl.fhir.core.model.dt.Identifier;
import com.b2international.snowowl.fhir.core.model.dt.Reference;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.dt.signature.WhoReferenceSignatureReference;
import com.b2international.snowowl.fhir.core.model.dt.signature.WhoUriSignatureReference;

/**
 * Signature serialization tests.
 * 
 * @since 6.6
 */
public class SignatureSerializationTest extends FhirTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void uriReferenceTest() throws Exception {
		WhoUriSignatureReference signatureReference = WhoUriSignatureReference.builder().value(new Uri("uriValue")).build();

		printPrettyJson(signatureReference);

		String expectedJson = "{\"whoUri\":\"uriValue\"}";
		assertEquals(expectedJson, objectMapper.writeValueAsString(signatureReference));
	}
	
	@Test
	public void referenceReferenceTest() throws Exception {
		
		WhoReferenceSignatureReference signatureReference = WhoReferenceSignatureReference.builder()
				.value(new Reference("reference", Identifier.builder().build(), "display"))
				.build();

		printPrettyJson(signatureReference);

		String expectedJson = "{\"whoReference\":"
				+ "{\"reference\":\"reference\","
				+ "\"identifier\":{},"
				+ "\"display\":\"display\"}}";
		assertEquals(expectedJson, objectMapper.writeValueAsString(signatureReference));
	}

	
}
