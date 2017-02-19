/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest;

import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.createComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.updateComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedMergingRestRequests.createMerge;
import static com.b2international.snowowl.snomed.api.rest.SnomedMergingRestRequests.waitForMergeJob;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.List;
import java.util.Map;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.jayway.restassured.response.ValidatableResponse;

/**
 * @since 5.0
 */
public abstract class SnomedRestFixtures {

	public static String createNewConcept(IBranchPath conceptPath) {
		return createNewConcept(conceptPath, Concepts.ROOT_CONCEPT);
	}

	public static String createNewConcept(IBranchPath conceptPath, String parentConceptId) {
		Map<?, ?> conceptRequestBody = createConceptRequestBody(parentConceptId)
				.put("commitComment", "Created new concept")
				.build();

		return lastPathSegment(createComponent(conceptPath, SnomedComponentType.CONCEPT, conceptRequestBody)
				.statusCode(201)
				.body(equalTo(""))
				.extract().header("Location"));
	}

	private static ImmutableMap.Builder<String, Object> createConceptRequestBody(String parentConceptId) {
		Map<?, ?> relationshipRequestBody = ImmutableMap.builder()
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("typeId", Concepts.IS_A)
				.put("destinationId", parentConceptId)
				.build();

		Map<?, ?> ptRequestBody = ImmutableMap.builder()
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("typeId", Concepts.SYNONYM)
				.put("term", "PT of concept")
				.put("languageCode", "en")
				.put("acceptability", SnomedApiTestConstants.UK_PREFERRED_MAP)
				.build();

		Map<?, ?> fsnRequestBody = ImmutableMap.builder()
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("typeId", Concepts.FULLY_SPECIFIED_NAME)
				.put("term", "FSN of concept")
				.put("languageCode", "en")
				.put("acceptability", SnomedApiTestConstants.UK_PREFERRED_MAP)
				.build();

		ImmutableMap.Builder<String, Object> conceptRequestBody = ImmutableMap.<String, Object>builder()
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("descriptions", ImmutableList.of(fsnRequestBody, ptRequestBody))
				.put("relationships", ImmutableList.of(relationshipRequestBody))			
				.put("namespaceId", SnomedIdentifiers.INT_NAMESPACE);

		return conceptRequestBody;
	}

	public static String createNewDescription(IBranchPath descriptionPath) {
		return createNewDescription(descriptionPath, Concepts.ROOT_CONCEPT);
	}
	
	public static String createNewDescription(IBranchPath descriptionPath, String conceptId) {
		return createNewDescription(descriptionPath, conceptId, Concepts.SYNONYM);
	}
	
	public static String createNewDescription(IBranchPath descriptionPath, String conceptId, String typeId) {
		return createNewDescription(descriptionPath, conceptId, typeId, SnomedApiTestConstants.UK_ACCEPTABLE_MAP);
	}
	
	public static String createNewDescription(IBranchPath descriptionPath, String conceptId, String typeId, Map<String, Acceptability> acceptabilityMap) {
		Map<?, ?> requestBody = ImmutableMap.builder()
				.put("conceptId", conceptId)
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("typeId", typeId)
				.put("term", "Description term")
				.put("languageCode", "en")
				.put("acceptability", acceptabilityMap)
				.put("caseSignificance", CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE)
				.put("commitComment", "Created new description")
				.build();

		return lastPathSegment(createComponent(descriptionPath, SnomedComponentType.DESCRIPTION, requestBody)
				.statusCode(201)
				.body(equalTo(""))
				.extract().header("Location"));
	}

	public static String createNewTextDefinition(IBranchPath descriptionPath, Map<String, Acceptability> acceptabilityMap) {
		Map<?, ?> requestBody = ImmutableMap.builder()
				.put("conceptId", Concepts.ROOT_CONCEPT)
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("typeId", Concepts.TEXT_DEFINITION)
				.put("term", "Text definition of root concept")
				.put("languageCode", "en")
				.put("acceptability", acceptabilityMap)
				.put("caseSignificance", CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE)
				.put("commitComment", "Created new text definition")
				.build();

		return lastPathSegment(createComponent(descriptionPath, SnomedComponentType.DESCRIPTION, requestBody)
				.statusCode(201)
				.body(equalTo(""))
				.extract().header("Location"));
	}

	public static String createNewRelationship(IBranchPath relationshipPath) {
		return createNewRelationship(relationshipPath, Concepts.ROOT_CONCEPT, Concepts.PART_OF, Concepts.NAMESPACE_ROOT);
	}

	public static String createNewRelationship(IBranchPath relationshipPath, String sourceId, String typeId, String destinationId) {
		return createNewRelationship(relationshipPath, sourceId, typeId, destinationId, CharacteristicType.STATED_RELATIONSHIP);
	}

	public static String createNewRelationship(IBranchPath relationshipPath, String sourceId, String typeId, String destinationId, CharacteristicType characteristicType) {
		return createNewRelationship(relationshipPath, sourceId, typeId, destinationId, characteristicType, 0);
	}

	public static String createNewRelationship(IBranchPath relationshipPath, String sourceId, String typeId, String destinationId, CharacteristicType characteristicType, int group) {
		Map<?, ?> requestBody = ImmutableMap.builder()
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("sourceId", sourceId)
				.put("typeId", typeId)
				.put("destinationId", destinationId)
				.put("characteristicType", characteristicType)
				.put("group", group)
				.put("commitComment", "Created new relationship")
				.build();

		return lastPathSegment(createComponent(relationshipPath, SnomedComponentType.RELATIONSHIP, requestBody)
				.statusCode(201)
				.body(equalTo(""))
				.extract().header("Location"));
	}

	public static String createNewRefSet(IBranchPath refSetPath) {
		Map<?, ?> refSetRequestBody = createConceptRequestBody(Concepts.REFSET_SIMPLE_TYPE)
				.put("type", SnomedRefSetType.SIMPLE)
				.put("referencedComponentType", SnomedTerminologyComponentConstants.CONCEPT)
				.put("commitComment", "Created new reference set")
				.build();

		return lastPathSegment(createComponent(refSetPath, SnomedComponentType.REFSET, refSetRequestBody)
				.statusCode(201)
				.body(equalTo(""))
				.extract().header("Location"));
	}

	public static String createNewRefSetMember(IBranchPath memberPath) {
		return createNewRefSetMember(memberPath, Concepts.ROOT_CONCEPT);
	}

	public static String createNewRefSetMember(IBranchPath memberPath, String referencedConceptId) {
		String refSetId = createNewRefSet(memberPath);

		Map<?, ?> requestBody = ImmutableMap.builder()
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("referenceSetId", refSetId)
				.put("referencedComponentId", referencedConceptId)
				.put("commitComment", "Created new reference set member")
				.build();

		return lastPathSegment(createComponent(memberPath, SnomedComponentType.MEMBER, requestBody)
				.statusCode(201)
				.body(equalTo(""))
				.extract().header("Location"));
	}

	public static String createNewLanguageRefSetMember(IBranchPath memberPath, String referencedConceptId, String refSetId, String acceptabilityId) {
		Map<?, ?> requestBody = ImmutableMap.builder()
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("referenceSetId", refSetId)
				.put("referencedComponentId", referencedConceptId)
				.put("acceptabilityId", acceptabilityId)
				.put("commitComment", "Created new language reference set member")
				.build();

		return lastPathSegment(createComponent(memberPath, SnomedComponentType.MEMBER, requestBody)
				.statusCode(201)
				.body(equalTo(""))
				.extract().header("Location"));
	}

	public static ValidatableResponse merge(IBranchPath sourcePath, IBranchPath targetPath, String commitComment) {
		String mergeLocation = createMerge(sourcePath, targetPath, commitComment)
				.statusCode(202)
				.body(equalTo(""))
				.extract().header("Location");

		return waitForMergeJob(lastPathSegment(mergeLocation));
	}

	public static String createInactiveConcept(IBranchPath conceptPath) {
		String conceptId = createNewConcept(conceptPath);
		inactivateConcept(conceptPath, conceptId);
		return conceptId;
	}

	public static void inactivateConcept(IBranchPath conceptPath, String conceptId) {
		Map<?, ?> inactivationRequest = ImmutableMap.builder()
				.put("active", false)
				.put("commitComment", "Inactivated concept")
				.build();

		updateComponent(conceptPath, SnomedComponentType.CONCEPT, conceptId, inactivationRequest).statusCode(204);
	}

	public static void inactivateDescription(IBranchPath descriptionPath, String descriptionId) {
		Map<?, ?> inactivationRequest = ImmutableMap.builder()
				.put("active", false)
				.put("commitComment", "Inactivated description")
				.build();

		updateComponent(descriptionPath, SnomedComponentType.DESCRIPTION, descriptionId, inactivationRequest).statusCode(204);
	}

	public static void inactivateRelationship(IBranchPath relationshipPath, String relationshipId) {
		Map<?, ?> inactivationRequest = ImmutableMap.builder()
				.put("active", false)
				.put("commitComment", "Inactivated relationship")
				.build();

		updateComponent(relationshipPath, SnomedComponentType.RELATIONSHIP, relationshipId, inactivationRequest).statusCode(204);
	}

	@SuppressWarnings("unchecked")
	public static void reactivateConcept(IBranchPath conceptPath, String id) {
		Map<String, Object> concept = getComponent(conceptPath, SnomedComponentType.CONCEPT, id, "descriptions()", "relationships()")
				.statusCode(200)
				.extract().as(Map.class);

		Map<String, Object> reactivationRequest = Maps.newHashMap(concept);
		reactivationRequest.put("active", true);
		reactivationRequest.put("commitComment", "Reactivated concept");

		Map<String, Object> relationships = (Map<String, Object>) reactivationRequest.get("relationships");
		List<Map<String, Object>> relationshipItems = (List<Map<String, Object>>) relationships.get("items");
		relationshipItems.get(0).put("active", true);

		updateComponent(conceptPath, SnomedComponentType.CONCEPT, id, reactivationRequest).statusCode(204);
	}

	public static void changeCaseSignificance(IBranchPath descriptionPath, String descriptionId) {
		changeCaseSignificance(descriptionPath, descriptionId, CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE);
	}

	public static void changeCaseSignificance(IBranchPath descriptionPath, String descriptionId, CaseSignificance caseSignificance) {
		Map<?, ?> descriptionUpdateRequest = ImmutableMap.builder()
				.put("caseSignificance", caseSignificance)
				.put("commitComment", "Changed case significance on description")
				.build();

		updateComponent(descriptionPath, SnomedComponentType.DESCRIPTION, descriptionId, descriptionUpdateRequest).statusCode(204);
	}

	public static void changeDefinitionStatus(IBranchPath conceptPath, String conceptId) {
		Map<?, ?> conceptUpdateRequest = ImmutableMap.builder()
				.put("definitionStatus", DefinitionStatus.FULLY_DEFINED)
				.put("commitComment", "Changed definition status on concept")
				.build();

		updateComponent(conceptPath, SnomedComponentType.CONCEPT, conceptId, conceptUpdateRequest).statusCode(204);
	}

	public static void changeRelationshipGroup(IBranchPath relationshipPath, String relationshipId) {
		Map<?, ?> relationshipUpdateRequest = ImmutableMap.builder()
				.put("group", 99)
				.put("commitComment", "Changed group on relationship")
				.build();

		updateComponent(relationshipPath, SnomedComponentType.RELATIONSHIP, relationshipId, relationshipUpdateRequest).statusCode(204);
	}

	private SnomedRestFixtures() {
		throw new UnsupportedOperationException("This class is not supposed to be instantiated.");
	}
}
