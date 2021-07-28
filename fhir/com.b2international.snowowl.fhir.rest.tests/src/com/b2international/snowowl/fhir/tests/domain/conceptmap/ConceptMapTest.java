/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.codesystems.ConceptMapGroupUnmappedMode;
import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
import com.b2international.snowowl.fhir.core.model.ContactDetail;
import com.b2international.snowowl.fhir.core.model.conceptmap.ConceptMap;
import com.b2international.snowowl.fhir.core.model.conceptmap.ConceptMapElement;
import com.b2international.snowowl.fhir.core.model.conceptmap.Group;
import com.b2international.snowowl.fhir.core.model.conceptmap.UnMapped;
import com.b2international.snowowl.fhir.core.model.dt.CodeableConcept;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.ContactPoint;
import com.b2international.snowowl.fhir.core.model.dt.Identifier;
import com.b2international.snowowl.fhir.core.model.usagecontext.CodeableConceptUsageContext;
import com.b2international.snowowl.fhir.tests.FhirTest;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link ConceptMap}
 * @since 7.0
 */
public class ConceptMapTest extends FhirTest {
	
	private ConceptMap conceptMap;

	@Before
	public void setup() throws Exception {
	
		conceptMap = ConceptMap.builder("-1")
				.url("http://who.org")
				.addIdentifier(Identifier.builder()
					.build())
				.version("20130131")
				.name("conceptMapName")
				.title("conceptMapTitle")
				.status(PublicationStatus.ACTIVE)
				.date(TEST_DATE_STRING)
				.publisher("b2i")
				.addContact(ContactDetail.builder()
					.addTelecom(ContactPoint.builder()
						.id("contactPointId")
						.build())
					.build())
				.description("Description")
				.addUseContext(CodeableConceptUsageContext.builder()
					.code(Coding.builder()
						.display("CodingDisplay")
						.build())
					.value(CodeableConcept.builder()
						.text("CodeableConceptText")
						.build())
					.build())
				.addJurisdiction(CodeableConcept.builder()
					.text("CodeableConceptText")
					.build())
				.purpose("Purpose")
				.copyright("Copyright")
				.sourceUri("SourceUri")
				.targetUri("TargetUri")
				.addGroup(Group.builder()
						.source("source")
						.sourceVersion("sourceVersion")
						.target("target")
						.targetVersion("targetVersion")
						.addElement(ConceptMapElement.builder()
								.code("ElementCode")
								.display("ElementDisplay")
								.build())
						.unmapped(UnMapped.builder()
								.code("code")
								.display("display")
								.mode(ConceptMapGroupUnmappedMode.FIXED)
								.url("url")
								.build())
						.build())
				.build();
		
		applyFilter(conceptMap);
		
		JsonPath jsonPath = getJsonPath(conceptMap);
		
		assertThat(jsonPath.get("url"), equalTo("http://who.org"));
		assertThat(jsonPath.get("name"), equalTo("conceptMapName"));
		assertThat(jsonPath.get("title"), equalTo("conceptMapTitle"));
		assertThat(jsonPath.get("purpose"), equalTo("Purpose"));
		assertThat(jsonPath.get("sourceUri"), equalTo("SourceUri"));
		assertThat(jsonPath.get("useContext.valueCodeableConcept.text"), hasItem("CodeableConceptText"));
	}
	
	@Test
	public void build() throws Exception {
		validate(conceptMap);
	}
	
	private void validate(ConceptMap conceptMap) {
		assertEquals("-1", conceptMap.getId().getIdValue());
	}
	
	@Test
	public void serialize() throws Exception {
		
		printPrettyJson(conceptMap);
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(conceptMap));
		assertThat(jsonPath.getString("id"), equalTo("-1"));
	}
	
	@Test
	public void deserialize() throws Exception, JsonProcessingException {
		ConceptMap readConceptMap = objectMapper.readValue(objectMapper.writeValueAsString(conceptMap), ConceptMap.class);
		validate(readConceptMap);
	}

}
