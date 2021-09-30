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
package com.b2international.snowowl.fhir.tests.domain.conceptmap;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.model.conceptmap.ConceptMapElement;
import com.b2international.snowowl.fhir.core.model.conceptmap.Group;
import com.b2international.snowowl.fhir.core.model.conceptmap.UnMapped;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link Group}
 * @since 8.0.0
 */
public class GroupTest extends FhirTest {
	
	private Group group;

	@Before
	public void setup() throws Exception {
		
		group = Group.builder()
				.source("Source")
				.sourceVersion("SourceVersion")
				.target("Target")
				.targetVersion("TargetVersion")
				.addElement(ConceptMapElement.builder()
						.code("ElementCode")
						.display("ElementDisplay")
						.build())
				.unmapped(UnMapped.builder()
						.mode("Mode")
						.build())
				.build();
		
	}
	
	@Test
	public void build() throws Exception {
		validate(group);
	}
	
	private void validate(Group group) {
		assertEquals("Source", group.getSource().getUriValue());
	}

	@Test
	public void serialize() throws Exception {
		
		printPrettyJson(group);
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(group));
		assertThat(jsonPath.getString("source"), equalTo("Source"));
		assertThat(jsonPath.getString("sourceVersion"), equalTo("SourceVersion"));
		assertThat(jsonPath.get("target"), equalTo("Target"));
		assertThat(jsonPath.get("targetVersion"), equalTo("TargetVersion"));
		assertThat(jsonPath.get("element.code"), hasItem("ElementCode") );
		assertThat(jsonPath.get("element.display"), hasItem("ElementDisplay"));
		assertThat(jsonPath.get("unmapped.mode"), equalTo("Mode") );
	}
	
	@Test
	public void serializeWithMissingOptionalFields() throws Exception {
		
		Group group = Group.builder()
			.addElement(ConceptMapElement.builder()
				.code("ElementCode")
				.display("ElementDisplay")
				.build())
			.build();
		
		JsonPath jsonPath = getJsonPath(group);
		
		assertNull(jsonPath.get("source"));
		assertNull(jsonPath.get("sourceVersion"));
		assertNull(jsonPath.get("target"));
		assertNull(jsonPath.get("targetVersion"));
		assertThat(jsonPath.get("element.code"), hasItem("ElementCode") );
		assertThat(jsonPath.get("element.display"), hasItem("ElementDisplay"));
		assertNull(jsonPath.get("unmapped"));
	}
	
	@Test
	public void deserialize() throws Exception {
		
		Group readGroup = objectMapper.readValue(objectMapper.writeValueAsString(group), Group.class);
		validate(readGroup);
	}

}
