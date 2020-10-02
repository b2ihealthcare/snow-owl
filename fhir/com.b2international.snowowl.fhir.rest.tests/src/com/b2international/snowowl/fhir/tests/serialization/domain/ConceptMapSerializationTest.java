/*
 * Copyright 2018-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.tests.serialization.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;

import org.junit.Test;

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
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Test for checking the ConceptMap serialization
 * @since 7.0
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

		JsonPath jsonPath = getJsonPath(unMapped);
		
		assertThat(jsonPath.getString("mode")).isEqualTo("Mode");
		assertThat(jsonPath.getString("code")).isEqualTo("Code");
		assertThat(jsonPath.getString("display")).isEqualTo("Display");
		assertThat(jsonPath.getString("url")).isEqualTo("Url");
	}
	
	@Test
	public void unMappedMissingOptionalFieldsTest() throws Exception {
		UnMapped unMapped = UnMapped.builder()
			.mode("Mode")
			.build();

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
		.value("Value")
		.display("Display")
		.build();
		
		JsonPath jsonPath = getJsonPath(dependsOn);
		
		assertThat(jsonPath.getString("property")).isEqualTo("Property");
		assertThat(jsonPath.getString("system")).isEqualTo("System");
		assertThat(jsonPath.getString("value")).isEqualTo("Value");
		assertThat(jsonPath.getString("display")).isEqualTo("Display");
	}
	
	@Test
	public void dependsOnMissingOptionalFieldTest() throws Exception {
		
		DependsOn dependsOn = DependsOn.builder()
		.property("Property")
		//.system("System")
		.value("Value")
		//.display("Display")
		.build();
		
		JsonPath jsonPath = getJsonPath(dependsOn);
		
		assertThat(jsonPath.getString("property")).isEqualTo("Property");
		assertNull(jsonPath.getString("system"));
		assertThat(jsonPath.getString("value")).isEqualTo("Value");
		assertNull(jsonPath.getString("display"));
	}
	
	@Test
	public void dependsOnMissingMandatoryFieldTest() {

		exception.expect(ValidationException.class);
		
		DependsOn.builder()
			.value("Value")
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
				.value("Code")
				.build())
			.addDependsOn(DependsOn.builder()
				.property("Property.2")
				.value("Code 2")
				.build())
			.addProduct(DependsOn.builder()
				.property("ProductProperty")
				.value("ProductCode")
				.build())
			.addProduct(DependsOn.builder()
				.property("ProductProperty.2")
				.value("ProductCode 2")
				.build())
			.build();
		
		JsonPath jsonPath = getJsonPath(target);
		
		assertThat(jsonPath.getString("code")).isEqualTo("Code");
		assertThat(jsonPath.getString("display")).isEqualTo("Display");
		assertThat(jsonPath.getString("equivalence")).isEqualTo("Equivalence");
		assertThat(jsonPath.getString("comment")).isEqualTo("Comment");
		assertThat(jsonPath.getList("dependsOn.property")).contains("Property.2");
		assertThat(jsonPath.getList("product.property")).contains("ProductProperty.2");
	}
	
	@Test
	public void targetMissingOptionalFieldsTest() throws Exception{
		
		Target target = Target.builder()
						.build();
		
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
		
		JsonPath jsonPath = getJsonPath(element);
		
		assertThat(jsonPath.getString("code")).isEqualTo("Code");
		assertThat(jsonPath.getString("display")).isEqualTo("Display");
		assertThat(jsonPath.getList("target.equivalence")).contains("Equivalence");
		assertThat(jsonPath.getList("target.display")).contains("Display");
	}
	
	@Test
	public void elementMissingOptioalFieldTest() throws Exception{
		
		ConceptMapElement element = ConceptMapElement.builder()
									.build();
		
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
		
		JsonPath jsonPath = getJsonPath(group);
		
		assertThat(jsonPath.getString("source")).isEqualTo("Source");
		assertThat(jsonPath.getString("sourceVersion")).isEqualTo("SourceVersion");
		assertThat(jsonPath.getString("target")).isEqualTo("Target");
		assertThat(jsonPath.getString("targetVersion")).isEqualTo("TargetVersion");
		assertThat(jsonPath.getList("element.code")).contains("ElementCode");
		assertThat(jsonPath.getList("element.display")).contains("ElementDisplay");
		assertThat(jsonPath.getString("unmapped.mode")).isEqualTo("Mode");
	}
	
	@Test
	public void groupMissingOptionalFieldTest() throws Exception {
		
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
		assertThat(jsonPath.getList("element.code")).contains("ElementCode");
		assertThat(jsonPath.getList("element.display")).contains("ElementDisplay");
		assertNull(jsonPath.get("unmapped"));
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
				.addContact(ContactDetail.builder()
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
				.addJurisdiction(CodeableConcept.builder()
					.text("CodeableConceptText")
					.build())
				.purpose("Purpose")
				.copyright("Copyright")
				.sourceUri("SourceUri")
				.targetUri("TargetUri")
				.build();
		
		applyFilter(conceptMap);
		
		JsonPath jsonPath = getJsonPath(conceptMap);
		
		assertThat(jsonPath.getString("url")).isEqualTo("http://who.org");
		assertThat(jsonPath.getString("name")).isEqualTo("conceptMapName");
		assertThat(jsonPath.getString("title")).isEqualTo("conceptMapTitle");
		assertThat(jsonPath.getString("purpose")).isEqualTo("Purpose");
		assertThat(jsonPath.getString("sourceUri")).isEqualTo("SourceUri");
		assertThat(jsonPath.getList("useContext.valueCodeableConcept.text")).contains("CodeableConceptText");
	}

}
