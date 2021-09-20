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
package com.b2international.snowowl.fhir.rest.tests.capabilitystatement;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;

import org.junit.Test;

import com.b2international.snowowl.fhir.core.codesystems.CapabilityStatementKind;
import com.b2international.snowowl.fhir.core.model.operationdefinition.OperationDefinition;
import com.b2international.snowowl.fhir.tests.FhirRestTest;
import static org.hamcrest.CoreMatchers.*;

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
		.prettyPeek()
		.then()
		.assertThat()
		.statusCode(404)
		.body("resourceType", equalTo("OperationOutcome"));
	}

}
