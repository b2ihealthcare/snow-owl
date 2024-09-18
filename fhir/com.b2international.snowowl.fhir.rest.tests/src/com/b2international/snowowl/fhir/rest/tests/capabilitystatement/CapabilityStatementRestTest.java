/*
 * Copyright 2021-2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.fhir.rest.tests.capabilitystatement;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

import org.hl7.fhir.r5.model.Enumerations.CapabilityStatementKind;
import org.junit.Test;

import com.b2international.snowowl.fhir.rest.tests.FhirRestTest;

/**
 * REST test cases for {@link CapabilityStatement} and the referenced {@link OperationDefinition}s.
 * 
 * @since 8.0.0
 */
public class CapabilityStatementRestTest extends FhirRestTest {
	
	@Test
	public void capabilityStatementTest() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.when().get("metadata")
			.then()
			.assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("CapabilityStatement"))
			.body("url", notNullValue())
			.body("version", notNullValue())
			.body("name", notNullValue())
			.body("title", notNullValue())
			.body("status", notNullValue())
			.body("date", notNullValue())
			.body("description", notNullValue())
			.body("kind", equalTo(CapabilityStatementKind.INSTANCE.getDefinition()))
			.body("fhirVersion", equalTo("5.0.0"))
			.body("rest[0]", notNullValue())
			.body("rest[0].resource[0]", notNullValue());
	}
	
	@Test
	public void operationDefinitionTest() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.when()
			.get("OperationDefinition/CodeSystem$lookup")
			.then()
			.assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("OperationDefinition"));
	}

	@Test
	public void nonExistentOperationDefinitionTest() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.when()
			.get("OperationDefinition/CodeSystem$invalid")
			.then()
			.assertThat()
			.statusCode(404)
			.body("resourceType", equalTo("OperationOutcome"))
			.body("issue[0].code", equalTo("not_found"));
	}

}
