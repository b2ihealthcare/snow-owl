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

import static com.b2international.snowowl.datastore.BranchPathUtils.createMainPath;
import static com.b2international.snowowl.datastore.BranchPathUtils.createPath;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.*;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.Map;

import org.junit.Test;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.domain.CaseSignificance;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * @since 2.0
 */
public class SnomedDescriptionApiTest extends AbstractSnomedApiTest {

	private static final String DISEASE = "64572001";

	private Builder<Object, Object> createRequestBuilder(final String conceptId, 
			final String term, 
			final String moduleId,
			final String typeId, 
			final String comment) {

		return ImmutableMap.builder()
				.put("conceptId", conceptId)
				.put("moduleId", moduleId)
				.put("typeId", typeId)
				.put("term", term)
				.put("languageCode", "en")
				.put("acceptability", SnomedApiTestConstants.ACCEPTABLE_ACCEPTABILITY_MAP)
				.put("commitComment", comment);
	}

	private Map<?, ?> createRequestBody(final String conceptId, 
			final String term, 
			final String moduleId, 
			final String typeId, 
			final String comment) {

		return createRequestBuilder(conceptId, term, moduleId, typeId, comment)
				.build();
	}

	private Map<?, ?> createRequestBody(final String conceptId, 
			final String term, 
			final String moduleId, 
			final String typeId, 
			final CaseSignificance caseSignificance, 
			final String comment) {

		return createRequestBuilder(conceptId, term, moduleId, typeId, comment)
				.put("caseSignificance", caseSignificance.name())
				.build();
	}

	@Test
	public void createDescriptionNonExistentBranch() {
		final Map<?, ?> requestBody = createRequestBody(DISEASE, "Rare disease", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "New description on a non-existent branch");
		assertComponentCreatedWithStatus(createPath("MAIN/1998-01-31"), SnomedComponentType.DESCRIPTION, requestBody, 404)
		.and().body("status", equalTo(404));
	}

	@Test
	public void createDescriptionWithNonExistentConcept() {
		final Map<?, ?> requestBody = createRequestBody("1", "Rare disease", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "New description with a non-existent concept ID");		
		assertComponentNotCreated(createMainPath(), SnomedComponentType.DESCRIPTION, requestBody);
	}

	@Test
	public void createDescriptionWithNonexistentType() {
		final Map<?, ?> requestBody = createRequestBody(DISEASE, "Rare disease", Concepts.MODULE_SCT_CORE, "2", "New description with a non-existent type ID");		
		assertComponentNotCreated(createMainPath(), SnomedComponentType.DESCRIPTION, requestBody);
	}

	@Test
	public void createDescriptionWithNonexistentModule() {
		final Map<?, ?> requestBody = createRequestBody(DISEASE, "Rare disease", "3", Concepts.SYNONYM, "New description with a non-existent module ID");
		assertComponentNotCreated(createMainPath(), SnomedComponentType.DESCRIPTION, requestBody);
	}

	@Test
	public void createDescriptionWithoutCommitComment() {
		final Map<?, ?> requestBody = createRequestBody(DISEASE, "Rare disease", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "");
		assertComponentNotCreated(createMainPath(), SnomedComponentType.DESCRIPTION, requestBody);
	}

	private void assertCaseSignificance(final IBranchPath branchPath, final String descriptionId, final CaseSignificance caseSignificance) {
		assertComponentHasProperty(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, "caseSignificance", caseSignificance.toString());
	}

	private void assertActive(final IBranchPath branchPath, final String descriptionId, final boolean active) {
		assertComponentActive(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, active);
	}

	@Test
	public void createDescription() {
		final Map<?, ?> requestBody = createRequestBody(DISEASE, "Rare disease", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "New description on MAIN");
		final String descriptionId = assertComponentCreated(createMainPath(), SnomedComponentType.DESCRIPTION, requestBody);
		assertCaseSignificance(createMainPath(), descriptionId, CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE);
	}

	@Test
	public void createDescriptionCaseInsensitive() {
		final Map<?, ?> requestBody = createRequestBody(DISEASE, "Rare disease", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, CaseSignificance.CASE_INSENSITIVE, "New description on MAIN");
		final String descriptionId = assertComponentCreated(createMainPath(), SnomedComponentType.DESCRIPTION, requestBody);
		assertCaseSignificance(createMainPath(), descriptionId, CaseSignificance.CASE_INSENSITIVE);
	}

	@Test
	public void deleteDescription() {
		final Map<?, ?> requestBody = createRequestBody(DISEASE, "Rare disease", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "New description on MAIN");
		final String descriptionId = assertComponentCreated(createMainPath(), SnomedComponentType.DESCRIPTION, requestBody);

		assertDescriptionCanBeDeleted(createMainPath(), descriptionId);
		assertDescriptionNotExists(createMainPath(), descriptionId);
	}

	private void assertDescriptionCanBeDeleted(final IBranchPath branchPath, final String descriptionId) {
		assertComponentCanBeDeleted(branchPath, SnomedComponentType.DESCRIPTION, descriptionId);
	}

	private void assertDescriptionCanBeUpdated(final IBranchPath branchPath, final String descriptionId, final Map<?, ?> requestBody) {
		assertComponentCanBeUpdated(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, requestBody);
	}

	@Test
	public void inactivateDescription() {
		final Map<?, ?> createRequestBody = createRequestBody(DISEASE, "Rare disease", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "New description on MAIN");
		final String descriptionId = assertComponentCreated(createMainPath(), SnomedComponentType.DESCRIPTION, createRequestBody);
		assertActive(createMainPath(), descriptionId, true);

		final Map<?, ?> updateRequestBody = ImmutableMap.builder()
				.put("active", false)
				.put("commitComment", "Inactivated description")
				.build();

		assertDescriptionCanBeUpdated(createMainPath(), descriptionId, updateRequestBody);
		assertActive(createMainPath(), descriptionId, false);
	}

	@Test
	public void updateCaseSignificance() {
		final Map<?, ?> createRequestBody = createRequestBody(DISEASE, "Rare disease", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "New description on MAIN");
		final String descriptionId = assertComponentCreated(createMainPath(), SnomedComponentType.DESCRIPTION, createRequestBody);
		assertCaseSignificance(createMainPath(), descriptionId, CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE);

		final Map<?, ?> updateRequestBody = ImmutableMap.builder()
				.put("caseSignificance", CaseSignificance.CASE_INSENSITIVE.name())
				.put("commitComment", "Changed description case significance")
				.build();

		assertDescriptionCanBeUpdated(createMainPath(), descriptionId, updateRequestBody);
		assertCaseSignificance(createMainPath(), descriptionId, CaseSignificance.CASE_INSENSITIVE);
	}

	@Test
	public void updateAcceptability() {
		final Map<?, ?> createRequestBody = createRequestBody(DISEASE, "Rare disease", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "New description on MAIN");
		final String descriptionId = assertComponentCreated(createMainPath(), SnomedComponentType.DESCRIPTION, createRequestBody);

		final Map<?, ?> updateRequestBody = ImmutableMap.builder()
				.put("acceptability", SnomedApiTestConstants.PREFERRED_ACCEPTABILITY_MAP)
				.put("commitComment", "Changed description acceptability")
				.build();

		assertDescriptionCanBeUpdated(createMainPath(), descriptionId, updateRequestBody);
		assertPreferredTermEquals(createMainPath(), DISEASE, descriptionId);
	}

	@Test
	public void createDescriptionOnNestedBranch() {
		final IBranchPath nestedBranchPath = createNestedBranch("a", "b");
		final Map<?, ?> createRequestBody = createRequestBody(DISEASE, "Rare disease", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "New description on MAIN");
		final String descriptionId = assertComponentCreated(nestedBranchPath, SnomedComponentType.DESCRIPTION, createRequestBody);		

		assertDescriptionExists(nestedBranchPath, descriptionId);
		assertDescriptionNotExists(nestedBranchPath.getParent(), descriptionId);
		assertDescriptionNotExists(nestedBranchPath.getParent().getParent(), descriptionId);
		assertDescriptionNotExists(nestedBranchPath.getParent().getParent().getParent(), descriptionId);
	}

	@Test
	public void deleteDescriptionOnNestedBranch() {
		final IBranchPath nestedBranchPath = createNestedBranch("a", "b");
		final Map<?, ?> createRequestBody = createRequestBody(DISEASE, "Rare disease", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "New description on MAIN");
		final String descriptionId = assertComponentCreated(nestedBranchPath, SnomedComponentType.DESCRIPTION, createRequestBody);		

		assertDescriptionCanBeDeleted(nestedBranchPath, descriptionId);
		assertDescriptionNotExists(nestedBranchPath, descriptionId);
		assertDescriptionNotExists(nestedBranchPath.getParent(), descriptionId);
		assertDescriptionNotExists(nestedBranchPath.getParent().getParent(), descriptionId);
		assertDescriptionNotExists(nestedBranchPath.getParent().getParent().getParent(), descriptionId);
	}
}
