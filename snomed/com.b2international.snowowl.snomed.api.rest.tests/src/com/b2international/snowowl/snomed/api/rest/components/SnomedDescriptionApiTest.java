/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest.components;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.joinPath;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.Map;

import org.junit.Test;

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.domain.CaseSignificance;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.jayway.restassured.http.ContentType;

/**
 * @since 2.0
 */
public class SnomedDescriptionApiTest extends AbstractSnomedApiTest {

	private static final String DISEASE = "64572001";
	
	private Builder<Object, Object> createRequestBuilder(String conceptId, String term, String moduleId, String typeId, String comment) {
		return ImmutableMap.builder()
				.put("conceptId", conceptId)
				.put("moduleId", moduleId)
				.put("typeId", typeId)
				.put("term", term)
				.put("languageCode", "en")
				.put("acceptability", SnomedApiTestConstants.ACCEPTABLE_ACCEPTABILITY_MAP)
				.put("commitComment", comment);
	}
	
	private Map<?, ?> createRequestBody(String conceptId, String term, String moduleId, String typeId, String comment) {
		return createRequestBuilder(conceptId, term, moduleId, typeId, comment).build();
	}
	
	private Map<?, ?> createRequestBody(String conceptId, String term, String moduleId, String typeId, CaseSignificance caseSignificance, String comment) {
		return createRequestBuilder(conceptId, term, moduleId, typeId, comment)
			.put("caseSignificance", caseSignificance.name())
			.build();
	}
	
	@Test
	public void createDescriptionNonExistentBranch() {
		final Map<?, ?> requestBody = createRequestBody(DISEASE, "Rare disease", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "New description on a non-existent branch");
		assertComponentCreationStatus("descriptions", requestBody, 404, "MAIN", "1998-01-31") // !
		.and()
			.body("status", equalTo(404));
	}
	
	@Test
	public void createDescriptionWithNonExistentConcept() {
		final Map<?, ?> requestBody = createRequestBody("1", "Rare disease", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "New description with a non-existent concept ID");		
		assertComponentCanNotBeCreated("descriptions", requestBody, "MAIN");
	}
	
	@Test
	public void createDescriptionWithNonexistentType() {
		final Map<?, ?> requestBody = createRequestBody(DISEASE, "Rare disease", Concepts.MODULE_SCT_CORE, "2", "New description with a non-existent type ID");		
		assertComponentCanNotBeCreated("descriptions", requestBody, "MAIN");
	}
	
	@Test
	public void createDescriptionWithNonexistentModule() {
		final Map<?, ?> requestBody = createRequestBody(DISEASE, "Rare disease", "3", Concepts.SYNONYM, "New description with a non-existent module ID");
		assertComponentCanNotBeCreated("descriptions", requestBody, "MAIN");
	}
	
	@Test
	public void createDescriptionWithoutCommitComment() {
		final Map<?, ?> requestBody = createRequestBody(DISEASE, "Rare disease", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "");
		assertComponentCanNotBeCreated("descriptions", requestBody, "MAIN");
	}

	private void assertDescriptionHasProperty(String descriptionId, String propertyName, Object value) {
		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
		.when()
			.get("/MAIN/descriptions/{id}", descriptionId)
		.then()
		.assertThat()
			.statusCode(200)
		.and()
			.body(propertyName, equalTo(value));
	}
	
	private void assertCaseSignificance(String descriptionId, CaseSignificance caseSignificance) {
		assertDescriptionHasProperty(descriptionId, "caseSignificance", caseSignificance.toString());
	}
	
	private void assertActive(String descriptionId, boolean active) {
		assertDescriptionHasProperty(descriptionId, "active", active);
	}

	@Test
	public void createDescription() {
		final Map<?, ?> requestBody = createRequestBody(DISEASE, "Rare disease", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "New description on MAIN");
		String descriptionId = assertComponentCanBeCreated("descriptions", requestBody, "MAIN");
		assertCaseSignificance(descriptionId, CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE);
	}
	
	@Test
	public void createDescriptionCaseSensitive() {
		final Map<?, ?> requestBody = createRequestBody(DISEASE, "Rare disease", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, CaseSignificance.CASE_INSENSITIVE, "New description on MAIN");
		String descriptionId = assertComponentCanBeCreated("descriptions", requestBody, "MAIN");
		assertCaseSignificance(descriptionId, CaseSignificance.CASE_INSENSITIVE);
	}
	
	@Test
	public void deleteDescription() {
		final Map<?, ?> requestBody = createRequestBody(DISEASE, "Rare disease", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "New description on MAIN");
		String descriptionId = assertComponentCanBeCreated("descriptions", requestBody, "MAIN");

		assertDescriptionCanBeDeleted(descriptionId, "MAIN");
		
		assertDescriptionNotExists(descriptionId, "MAIN");
	}

	private void assertDescriptionCanBeDeleted(String descriptionId, String... segments) {
		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
		.when()
			.delete("/{path}/descriptions/{id}", joinPath(segments), descriptionId)
		.then()
		.assertThat()
			.statusCode(204);
	}

	private void assertDescriptionCanBeUpdated(String descriptionId, final Map<?, ?> updateRequestBody) {
		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
		.with()
			.contentType(ContentType.JSON)
		.and()
			.body(updateRequestBody)
		.when()
			.post("/MAIN/descriptions/{id}/updates", descriptionId)
		.then()
		.assertThat()
			.statusCode(204);
	}

	@Test
	public void inactivateDescription() {
		final Map<?, ?> createRequestBody = createRequestBody(DISEASE, "Rare disease", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "New description on MAIN");
		String descriptionId = assertComponentCanBeCreated("descriptions", createRequestBody, "MAIN");
		
		final Map<?, ?> updateRequestBody = ImmutableMap.of(
			"active", false,
			"commitComment", "Inactivated description"
		);
		
		assertDescriptionCanBeUpdated(descriptionId, updateRequestBody);
		assertActive(descriptionId, false);
	}
	
	@Test
	public void updateCaseSignificance() {
		final Map<?, ?> createRequestBody = createRequestBody(DISEASE, "Rare disease", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "New description on MAIN");
		String descriptionId = assertComponentCanBeCreated("descriptions", createRequestBody, "MAIN");
		
		final Map<?, ?> updateRequestBody = ImmutableMap.of(
			"caseSignificance", CaseSignificance.CASE_INSENSITIVE.name(),
			"commitComment", "Changed description case significance"
		);
		
		assertDescriptionCanBeUpdated(descriptionId, updateRequestBody);
		assertCaseSignificance(descriptionId, CaseSignificance.CASE_INSENSITIVE);
	}
	
	@Test
	public void updateAcceptability() {
		final Map<?, ?> createRequestBody = createRequestBody(DISEASE, "Rare disease", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "New description on MAIN");
		String descriptionId = assertComponentCanBeCreated("descriptions", createRequestBody, "MAIN");
		
		final Map<?, ?> updateRequestBody = ImmutableMap.of(
			"acceptability", SnomedApiTestConstants.PREFERRED_ACCEPTABILITY_MAP,
			"commitComment", "Changed description acceptability"
		);
		
		assertDescriptionCanBeUpdated(descriptionId, updateRequestBody);
		assertPreferredTermEquals(DISEASE, descriptionId, "MAIN");
	}
	
	@Test
	public void createDescriptionOnNestedBranch() {
		assertBranchCanBeCreated("MAIN", branchName);
		assertBranchCanBeCreated("MAIN/" + branchName, "a");
		assertBranchCanBeCreated("MAIN/" + branchName + "/a", "b");
		
		final Map<?, ?> createRequestBody = createRequestBody(DISEASE, "Rare disease", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "New description on MAIN");
		String descriptionId = assertComponentCanBeCreated("descriptions", createRequestBody, "MAIN", branchName, "a", "b");		
		
		assertDescriptionExists(descriptionId, "MAIN", branchName, "a", "b");
		assertDescriptionNotExists(descriptionId, "MAIN", branchName, "a");
		assertDescriptionNotExists(descriptionId, "MAIN", branchName);
		assertDescriptionNotExists(descriptionId, "MAIN");
	}
	
	@Test
	public void deleteDescriptionOnNestedBranch() {
		assertBranchCanBeCreated("MAIN", branchName);
		assertBranchCanBeCreated("MAIN/" + branchName, "a");
		assertBranchCanBeCreated("MAIN/" + branchName + "/a", "b");
		
		final Map<?, ?> createRequestBody = createRequestBody(DISEASE, "Rare disease", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "New description on MAIN");
		String descriptionId = assertComponentCanBeCreated("descriptions", createRequestBody, "MAIN", branchName, "a", "b");		

		assertDescriptionCanBeDeleted(descriptionId, "MAIN", branchName, "a", "b");
	}
}
