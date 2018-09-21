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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.b2international.snowowl.fhir.api.tests.FhirTest;
import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
import com.b2international.snowowl.fhir.core.exceptions.ValidationException;
import com.b2international.snowowl.fhir.core.model.ContactDetail;
import com.b2international.snowowl.fhir.core.model.conceptmap.ConceptMap;
import com.b2international.snowowl.fhir.core.model.conceptmap.ConceptMapElement;
import com.b2international.snowowl.fhir.core.model.conceptmap.DependsOn;
import com.b2international.snowowl.fhir.core.model.conceptmap.Group;
import com.b2international.snowowl.fhir.core.model.conceptmap.Target;
import com.b2international.snowowl.fhir.core.model.conceptmap.UnMapped;
import com.b2international.snowowl.fhir.core.model.dt.CodeableConcept;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.ContactPoint;
import com.b2international.snowowl.fhir.core.model.dt.Identifier;
import com.b2international.snowowl.fhir.core.model.usagecontext.CodeableConceptUsageContext;
import com.jayway.restassured.path.json.JsonPath;

/**
 * Test for checking the ConceptMap serialization
 * @since 6.10
 */
public class ConceptMapSerializationTest extends FhirTest {
	
	@Test
	public void unMappedTest() throws Exception {
		
		UnMapped unMapped = UnMapped.builder()
					.mode("Mode")
					.code("Code")
					.display("Display")
					.url("Url")
					.build();

		printPrettyJson(unMapped);
	
		JsonPath jsonPath = getJsonPath(unMapped);
		
		assertThat(jsonPath.get("mode"), equalTo("Mode"));
		assertThat(jsonPath.get("code"), equalTo("Code"));
		assertThat(jsonPath.getString("display"), equalTo("Display"));
		assertThat(jsonPath.get("url"),  equalTo("Url"));
	}
	
	@Test
	public void unMappedMissingOptionalFieldsTest() throws Exception {
		
		UnMapped unMapped = UnMapped.builder()
					.mode("Mode")
					.build();

		printPrettyJson(unMapped);
		
		JsonPath jsonPath = getJsonPath(unMapped);
		assertNull(jsonPath.get("code"));
	}
	
	@Test
	public void unMappedMissingModeTest() throws Exception {
		
		exception.expect(ValidationException.class);
		UnMapped.builder()
					.code("Code")
					.display("Display")
					.url("Url")
					.build();
	}
	
	
	@Test
	public void dependsOnTest() throws Exception {
		
		DependsOn dependsOn = DependsOn.builder()
		.property("Property")
		.system("System")
		.code("Code")
		.display("Display")
		.build();
		
		printPrettyJson(dependsOn);

		JsonPath jsonPath = getJsonPath(dependsOn);
		
		assertThat(jsonPath.get("property"), equalTo("Property"));
		assertThat(jsonPath.get("system"), equalTo("System"));
		assertThat(jsonPath.get("code"), equalTo("Code"));
		assertEquals(jsonPath.getString("display"), "Display");
	
	}
	
	@Test
	public void dependsOnMissingOptionalFieldTest() throws Exception {
		
		DependsOn dependsOn = DependsOn.builder()
		.property("Property")
		//.system("System")
		.code("Code")
		//.display("Display")
		.build();
		
		printPrettyJson(dependsOn);

		JsonPath jsonPath = getJsonPath(dependsOn);
		
		assertThat(jsonPath.get("property"), equalTo("Property"));
		assertNull(jsonPath.get("system"));
		assertThat(jsonPath.get("code"), equalTo("Code"));
		assertNull(jsonPath.get("display"));
	
	}
	
	@Test
	public void dependsOnMissingMandatoryFieldTest() {

		exception.expect(ValidationException.class);
		
		DependsOn.builder()
			.code("Code")
			.system("System")
			.display("Display")
			.build();
	
	}
	
	@Test
	public void targetTest() throws Exception{
		
		Target target = Target.builder()
				.code("Code")
				.display("Display")
				.equivalence("Equivalence")
				.comment("Comment")
				.addDependsOn(DependsOn.builder()
						.property("Property")
						.code("Code")
						.build())
				.addDependsOn(DependsOn.builder()
						.property("Property.2")
						.code("Code 2")
						.build())
				.addProduct(DependsOn.builder()
						.property("ProductProperty")
						.code("ProductCode")
						.build())
				.addProduct(DependsOn.builder()
						.property("ProductProperty.2")
						.code("ProductCode 2")
						.build())
				
				.build();
		
		printPrettyJson(target);
		
		JsonPath jsonPath = getJsonPath(target);
		
		assertThat(jsonPath.get("code"), equalTo("Code"));
		assertThat(jsonPath.get("display"), equalTo("Display"));
		assertThat(jsonPath.get("equivalence"), equalTo("Equivalence") );
		assertThat(jsonPath.get("comment"),equalTo("Comment"));
		assertThat(jsonPath.get("dependsOn.property"), hasItem("Property.2"));
		assertThat(jsonPath.get("product.property"), hasItem("ProductProperty.2"));
		
	}
	
	@Test
	public void targetMissingOptionalFieldsTest() throws Exception{
		
		Target target = Target.builder()
						.build();
		
		printPrettyJson(target);
		
		JsonPath jsonPath = getJsonPath(target);
		
		assertNull(jsonPath.get("code"));
		assertNull(jsonPath.get("display"));
		assertNull(jsonPath.get("equivalence"));
		assertNull(jsonPath.get("comment"));
		assertNull(jsonPath.get("dependsOn"));
		assertNull(jsonPath.get("product"));
		
	}
	
	@Test
	public void elementTest() throws Exception{
		
		ConceptMapElement element = ConceptMapElement.builder()
				.code("Code")
				.display("Display")
				.addTarget(Target.builder()
						.display("Display")
						.equivalence("Equivalence")
						.build())
				
				.build();
		
		printPrettyJson(element);
		
		JsonPath jsonPath = getJsonPath(element);
		
		assertThat(jsonPath.get("code"), equalTo("Code"));
		assertThat(jsonPath.get("display"), equalTo("Display"));
		assertThat(jsonPath.get("target.equivalence"), hasItem("Equivalence"));
		assertThat(jsonPath.get("target.display"), hasItem("Display"));
		
	}
	
	@Test
	public void elementMissingOptioalFieldTest() throws Exception{
		
		ConceptMapElement element = ConceptMapElement.builder()
									.build();
		
		printPrettyJson(element);
		
		JsonPath jsonPath = getJsonPath(element);
		
		assertNull(jsonPath.get("code"));
		assertNull(jsonPath.get("display"));
		assertNull(jsonPath.get("target"));
		assertNull(jsonPath.get("target"));
		
	}
	
	@Test
	public void groupTest() throws Exception {
		
		Group group = Group.builder()
				
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
		
		printPrettyJson(group);
		
		JsonPath jsonPath = getJsonPath(group);
		
		assertThat(jsonPath.get("source"), equalTo("Source"));
		assertThat(jsonPath.get("sourceVersion"), equalTo("SourceVersion"));
		assertThat(jsonPath.get("target"), equalTo("Target"));
		assertThat(jsonPath.get("targetVersion"), equalTo("TargetVersion"));
		assertThat(jsonPath.get("element.code"), hasItem("ElementCode") );
		assertThat(jsonPath.get("element.display"), hasItem("ElementDisplay"));
		assertThat(jsonPath.get("unmapped.mode"), equalTo("Mode") );
		
		
	}
	
	@Test
	public void groupMissingOptionalFieldTest() throws Exception {
		
		Group group = Group.builder()
				
				.addElement(ConceptMapElement.builder()
						.code("ElementCode")
						.display("ElementDisplay")
						
						.build())
				.build();
		
		printPrettyJson(group);
		
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
	public void groupMissingMandatoryFiledTest() {

		exception.expect(ValidationException.class);

			Group.builder()
				.source("Source")
				.sourceVersion("SourceVersion")
				.target("Target")
				.targetVersion("TargetVersion")
				.unmapped(UnMapped.builder()
						.mode("Mode")
						.build())
				.build();

	}
	
	@Test
	public void conceptMapTest() throws Exception {
	
		ConceptMap conceptMap = ConceptMap.builder("-1")
				
				.url("http://who.org")
				.identifier(Identifier.builder()
						.build())
				.version("20130131")
				.name("conceptMapName")
				.title("conceptMapTitle")
				.status(PublicationStatus.ACTIVE)
				.date(TEST_DATE_STRING)
				.publisher("b2i")
				.contact(ContactDetail.builder()
						.addContactPoint(ContactPoint.builder()
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
				.jurisdiction(CodeableConcept.builder()
								.text("CodeableConceptText")
								.build())
				.purpose("Purpose")
				.copyright("Copyright")
				.sourceUri("SourceUri")
				.targetUri("TargetUri")
				.build();
		
		printPrettyJson(conceptMap);
		
		JsonPath jsonPath = getJsonPath(conceptMap);
		
		assertThat(jsonPath.get("url"), equalTo("http://who.org"));
		assertThat(jsonPath.get("name"), equalTo("conceptMapName"));
		assertThat(jsonPath.get("title"), equalTo("conceptMapTitle"));
		assertThat(jsonPath.get("purpose"), equalTo("Purpose"));
		assertThat(jsonPath.get("sourceUri"), equalTo("SourceUri"));
		assertThat(jsonPath.get("useContext.valueCodeableConcept.text"), hasItem("CodeableConceptText"));
		
		
	}

}
