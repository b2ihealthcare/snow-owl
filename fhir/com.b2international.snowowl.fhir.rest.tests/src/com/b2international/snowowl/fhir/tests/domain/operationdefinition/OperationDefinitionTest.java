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
package com.b2international.snowowl.fhir.tests.domain.operationdefinition;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.codesystems.OperationKind;
import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.operationdefinition.Binding;
import com.b2international.snowowl.fhir.core.model.operationdefinition.OperationDefinition;
import com.b2international.snowowl.fhir.core.model.operationdefinition.Parameter;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link OperationDefinition}
 * @since 8.0.0
 */
public class OperationDefinitionTest extends FhirTest {
	
	private OperationDefinition operationDefinition;

	@Before
	public void setup() throws Exception {
		
		operationDefinition = OperationDefinition.builder()
				.affectState(false)
				.base("base")
				.code("code")
				.comment("comment")
				.date(TEST_DATE_STRING)
				.description("description")
				.kind(OperationKind.OPERATION.getCode())
				.inputProfile("inputProfile")
				.outputProfile("outputProfile")
				.system(true)
				.instance(true)
				.type(true)
				.status(PublicationStatus.ACTIVE)
				.addResource(new Code("resource"))
				.addParameter(Parameter.builder()
						.id("id")
						.name("name")
						.use("use")
						.min(1)
						.max(2)
						.documentation("documentation")
						.type(new Code("type"))
						.addTargetProfile(new Uri("targetProfile"))
						.searchType("searchType")
						.binding(Binding.builder()
								.strength("strength")
								.valueSetUri("valueSetUri")
								.build())
						.addParameter(Parameter.builder()
								.id("id2")
								.name("name2")
								.use("use2")
								.min(0)
								.max(1)
								.build())
						.build())
				.build();
	}
	
	@Test
	public void build() throws Exception {
		validate(operationDefinition);
	}
	
	private void validate(OperationDefinition operationDefinition) {
		assertEquals("base", operationDefinition.getBase().getUriValue());
		assertEquals("comment", operationDefinition.getComment());
		assertEquals(true, operationDefinition.getInstance().booleanValue());
		assertFalse(operationDefinition.getParameters().isEmpty());
	}

	@Test
	public void serialize() throws Exception {
		
		printPrettyJson(operationDefinition);
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(operationDefinition));
		assertThat(jsonPath.getString("base"), equalTo("base"));
		assertThat(jsonPath.getString("parameter[0].id"), equalTo("id"));
		assertThat(jsonPath.getString("parameter[0].name"), equalTo("name"));
		assertThat(jsonPath.getString("parameter[0].use"), equalTo("use"));
	}
	
	@Test
	public void deserialize() throws Exception {
		OperationDefinition readOperationDefinition = objectMapper.readValue(objectMapper.writeValueAsString(operationDefinition), OperationDefinition.class);
		validate(readOperationDefinition);
	}

}
