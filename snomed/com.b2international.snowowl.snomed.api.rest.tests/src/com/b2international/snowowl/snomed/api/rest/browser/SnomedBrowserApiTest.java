/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest.browser;

import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.MODULE_SCT_CORE;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingRestRequests.deleteBranch;
import static com.b2international.snowowl.snomed.api.rest.SnomedBrowserRestRequests.createBrowserConcept;
import static com.b2international.snowowl.snomed.api.rest.SnomedBrowserRestRequests.updateBrowserConcept;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.deleteComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewConcept;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.reserveComponentId;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.domain.browser.SnomedBrowserDescriptionType;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.RelationshipModifier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * @since 4.5
 */
public class SnomedBrowserApiTest extends AbstractSnomedApiTest {

	private static Map<String, Object> createBrowserConceptRequest() {
		ImmutableMap.Builder<String, Object> conceptBuilder = ImmutableMap.<String, Object>builder()
				.put("fsn", "FSN of new concept")
				.put("preferredSynonym", "PT of new concept")
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("definitionStatus", DefinitionStatus.PRIMITIVE)
				.put("descriptions", createDefaultDescriptions())
				.put("relationships", createIsaRelationship());

		return conceptBuilder.build();
	}

	private static List<?> createDefaultDescriptions() {
		Map<?, ?> fsnDescription = ImmutableMap.<String, Object>builder()
				.put("active", true)
				.put("term", "FSN of new concept")
				.put("type", SnomedBrowserDescriptionType.FSN)
				.put("lang", "en")
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("caseSignificance", CaseSignificance.CASE_INSENSITIVE)
				.put("acceptabilityMap", SnomedApiTestConstants.UK_PREFERRED_MAP)
				.build();

		Map<?, ?> ptDescription = ImmutableMap.<String, Object>builder()
				.put("active", true)
				.put("term", "PT of new concept")
				.put("type", SnomedBrowserDescriptionType.SYNONYM)
				.put("lang", "en")
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("caseSignificance", CaseSignificance.CASE_INSENSITIVE)
				.put("acceptabilityMap", SnomedApiTestConstants.UK_PREFERRED_MAP)
				.build();

		return ImmutableList.of(fsnDescription, ptDescription);
	}

	private static List<?> createIsaRelationship() {
		return createIsaRelationship(Concepts.ROOT_CONCEPT);
	}

	private static List<?> createIsaRelationship(String parentId) {
		Map<?, ?> type = ImmutableMap.<String, Object>builder()
				.put("conceptId", Concepts.IS_A)
				.put("fsn", "Is a (attribute)")
				.build();

		Map<?, ?> target = ImmutableMap.<String, Object>builder()
				.put("active", true)
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("conceptId", parentId)
				.put("fsn", "Parent of new concept")
				.put("definitionStatus", DefinitionStatus.PRIMITIVE)
				.build();

		Map<?, ?> isaRelationship = ImmutableMap.<String, Object>builder()
				.put("modifier", RelationshipModifier.EXISTENTIAL)
				.put("groupId", "0")
				.put("characteristicType", CharacteristicType.STATED_RELATIONSHIP)
				.put("active", true)
				.put("type", type)
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("target", target)
				.build();

		return ImmutableList.of(isaRelationship);
	}

	@SuppressWarnings("unchecked")
	private static List<Object> getListElement(Map<String, Object> concept, String elementName) {
		Object listElement = concept.get(elementName);

		if (listElement instanceof List<?>) {
			return (List<Object>) listElement;
		} else {
			return null;
		}
	}

	private static List<?> createNewDescriptions(int quantity) {
		List<Map<String, Object>> results = newArrayList();

		for (int i = 0; i < quantity; i++) {

			Map<String, Object> description = ImmutableMap.<String, Object>builder()
					.put("active", true)
					.put("term", String.format("New extra Synonym %s", i))
					.put("type", SnomedBrowserDescriptionType.SYNONYM)
					.put("lang", "en")
					.put("moduleId", MODULE_SCT_CORE)
					.put("caseSignificance", CaseSignificance.CASE_INSENSITIVE)
					.put("acceptabilityMap", SnomedApiTestConstants.UK_ACCEPTABLE_MAP)
					.build();

			results.add(description);
		}

		return results;
	}

	private static List<?> createNewRelationships(int quantity, String sourceId) {
		List<Map<String, Object>> results = newArrayList();

		for (int i = 0; i < quantity; i++) {

			Map<?, ?> type = ImmutableMap.<String, Object>builder()
					.put("conceptId", Concepts.PART_OF)
					.put("fsn", "Part of (attribute)")
					.build();

			Map<?, ?> target = ImmutableMap.<String, Object>builder()
					.put("active", true)
					.put("moduleId", MODULE_SCT_CORE)
					.put("conceptId", Concepts.NAMESPACE_ROOT)
					.put("fsn", "Destination of new relationship")
					.put("definitionStatus", DefinitionStatus.PRIMITIVE)
					.build();

			Map<String, Object> relationship = ImmutableMap.<String, Object>builder()
					.put("sourceId", sourceId)
					.put("modifier", RelationshipModifier.EXISTENTIAL)
					.put("groupId", "0")
					.put("characteristicType", CharacteristicType.ADDITIONAL_RELATIONSHIP)
					.put("active", true)
					.put("type", type)
					.put("moduleId", MODULE_SCT_CORE)
					.put("target", target)
					.build();

			results.add(relationship);
		}

		return results;
	}

	@Test
	public void createConceptNonExistentBranch() {
		createBrowserConcept(BranchPathUtils.createPath("MAIN/x/y/z"), createBrowserConceptRequest()).statusCode(404);
	}

	@Test
	public void createConceptWithoutParent() {
		Map<?, ?> conceptRequest = newHashMap(createBrowserConceptRequest());
		conceptRequest.remove("relationships");

		createBrowserConcept(branchPath, conceptRequest).statusCode(400)
		.body("message", equalTo("1 validation error"));
	}

	@Test
	public void createConceptWithNonexistentParent() {
		String conceptId = createNewConcept(branchPath);

		deleteComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, false).statusCode(204);

		Map<String, Object> conceptRequest = newHashMap(createBrowserConceptRequest());
		conceptRequest.put("relationships", createIsaRelationship(conceptId));

		createBrowserConcept(branchPath, conceptRequest).statusCode(400);
	}

	@Test
	public void createRegularConcept() {
		createBrowserConcept(branchPath, createBrowserConceptRequest()).statusCode(200);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void createConceptWithReservedId() {
		String expectedConceptId = reserveComponentId(null, ComponentCategory.CONCEPT);

		Map<String, Object> conceptRequest = newHashMap(createBrowserConceptRequest());
		conceptRequest.put("conceptId", expectedConceptId);

		Map<String, Object> conceptResponse = createBrowserConcept(branchPath, conceptRequest).statusCode(200)
				.extract().as(Map.class);

		String actualConceptId = (String) conceptResponse.get("conceptId");
		assertEquals(expectedConceptId, actualConceptId);
	}

	@Test
	public void createConceptOnDeletedBranch() {
		deleteBranch(branchPath).statusCode(204);
		createBrowserConcept(branchPath, createBrowserConceptRequest()).statusCode(400);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void inactivateConcept() throws Exception {
		Map<String, Object> conceptRequest = createBrowserConcept(branchPath, createBrowserConceptRequest()).statusCode(200)
				.extract().as(Map.class);

		String conceptId = (String) conceptRequest.get("conceptId");
		conceptRequest.put("active", false);
		updateBrowserConcept(branchPath, conceptId, conceptRequest).statusCode(200);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void removeAllRelationshipsFromConcept() throws Exception {
		Map<String, Object> conceptRequest = createBrowserConcept(branchPath, createBrowserConceptRequest()).statusCode(200)
				.extract().as(Map.class);

		String conceptId = (String) conceptRequest.get("conceptId");
		conceptRequest.remove("relationships");
		updateBrowserConcept(branchPath, conceptId, conceptRequest).statusCode(200);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void updateConceptWithNewComponents() {
		Map<String, Object> conceptRequest = createBrowserConcept(branchPath, createBrowserConceptRequest()).statusCode(200)
				.extract().as(Map.class);

		String conceptId = (String) conceptRequest.get("conceptId");

		List<Object> descriptionsList = getListElement(conceptRequest, "descriptions");
		descriptionsList.addAll(createNewDescriptions(5));

		List<Object> relationshipsList = getListElement(conceptRequest, "relationships");
		relationshipsList.addAll(createNewRelationships(5, conceptId));

		Map<String, Object> updatedConcept = updateBrowserConcept(branchPath, conceptId, conceptRequest).statusCode(200)
				.extract().as(Map.class);

		List<Object> updatedDescriptions = getListElement(updatedConcept, "descriptions");
		assertEquals(7, updatedDescriptions.size());

		List<Object> updatedRelationships = getListElement(updatedConcept, "relationships");
		assertEquals(6, updatedRelationships.size());
	}
}
