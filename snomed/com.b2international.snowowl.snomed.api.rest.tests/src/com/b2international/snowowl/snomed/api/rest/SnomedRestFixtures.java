/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static com.google.common.collect.Maps.newHashMap;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.List;
import java.util.Map;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.SnowOwlApplication;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.datastore.request.RefSetSupport;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.jayway.restassured.response.ValidatableResponse;

/**
 * @since 5.0
 */
public abstract class SnomedRestFixtures {

	public static final String DEFAULT_TERM = "Description term";
	public static final String DEFAULT_LANGUAGE_CODE = "en";

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

	public static Builder<String, Object> createConceptRequestBody(String parentConceptId) {
		return createConceptRequestBody(parentConceptId, Concepts.MODULE_SCT_CORE, SnomedApiTestConstants.UK_PREFERRED_MAP);
	}

	public static Builder<String, Object> createConceptRequestBody(String parentConceptId, String moduleId, Map<String, Acceptability> acceptabilityMap) {
		Map<?, ?> relationshipRequestBody = ImmutableMap.builder()
				.put("moduleId", moduleId)
				.put("typeId", Concepts.IS_A)
				.put("destinationId", parentConceptId)
				.build();

		Map<?, ?> ptRequestBody = ImmutableMap.builder()
				.put("moduleId", moduleId)
				.put("typeId", Concepts.SYNONYM)
				.put("term", "PT of concept")
				.put("languageCode", DEFAULT_LANGUAGE_CODE)
				.put("acceptability", acceptabilityMap)
				.build();

		Map<?, ?> fsnRequestBody = ImmutableMap.builder()
				.put("moduleId", moduleId)
				.put("typeId", Concepts.FULLY_SPECIFIED_NAME)
				.put("term", "FSN of concept")
				.put("languageCode", DEFAULT_LANGUAGE_CODE)
				.put("acceptability", acceptabilityMap)
				.build();

		Builder<String, Object> conceptRequestBody = ImmutableMap.<String, Object>builder()
				.put("moduleId", moduleId)
				.put("descriptions", ImmutableList.of(fsnRequestBody, ptRequestBody))
				.put("relationships", ImmutableList.of(relationshipRequestBody));

		return conceptRequestBody;
	}

	public static String createNewDescription(IBranchPath descriptionPath) {
		return createNewDescription(descriptionPath, Concepts.ROOT_CONCEPT, Concepts.SYNONYM, SnomedApiTestConstants.UK_ACCEPTABLE_MAP);
	}

	public static String createNewDescription(IBranchPath descriptionPath, String conceptId) {
		return createNewDescription(descriptionPath, conceptId, Concepts.SYNONYM, SnomedApiTestConstants.UK_ACCEPTABLE_MAP);
	}

	public static String createNewDescription(IBranchPath descriptionPath, String conceptId, String typeId) {
		return createNewDescription(descriptionPath, conceptId, typeId, SnomedApiTestConstants.UK_ACCEPTABLE_MAP);
	}

	public static String createNewDescription(IBranchPath descriptionPath, String conceptId, String typeId, Map<String, Acceptability> acceptabilityMap, final String languageCode) {
		Map<?, ?> requestBody = createDescriptionRequestBody(conceptId, typeId, Concepts.MODULE_SCT_CORE, acceptabilityMap, CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE, languageCode)
				.put("commitComment", "Created new description")
				.build();

		return lastPathSegment(createComponent(descriptionPath, SnomedComponentType.DESCRIPTION, requestBody)
				.statusCode(201)
				.body(equalTo(""))
				.extract().header("Location"));
	}
	
	public static String createNewDescription(IBranchPath descriptionPath, String conceptId, String typeId, Map<String, Acceptability> acceptabilityMap) {
		Map<?, ?> requestBody = createDescriptionRequestBody(conceptId, typeId, Concepts.MODULE_SCT_CORE, acceptabilityMap)
				.put("commitComment", "Created new description")
				.build();

		return lastPathSegment(createComponent(descriptionPath, SnomedComponentType.DESCRIPTION, requestBody)
				.statusCode(201)
				.body(equalTo(""))
				.extract().header("Location"));
	}

	public static String createNewTextDefinition(IBranchPath descriptionPath, Map<String, Acceptability> acceptabilityMap) {
		Map<?, ?> requestBody = createDescriptionRequestBody(Concepts.ROOT_CONCEPT, Concepts.TEXT_DEFINITION, Concepts.MODULE_SCT_CORE, acceptabilityMap)
				.put("commitComment", "Created new text definition")
				.build();

		return lastPathSegment(createComponent(descriptionPath, SnomedComponentType.DESCRIPTION, requestBody)
				.statusCode(201)
				.body(equalTo(""))
				.extract().header("Location"));
	}

	public static Builder<String, Object> createDescriptionRequestBody(String conceptId) {
		return createDescriptionRequestBody(conceptId, Concepts.SYNONYM, Concepts.MODULE_SCT_CORE, SnomedApiTestConstants.UK_ACCEPTABLE_MAP);
	}

	public static Builder<String, Object> createDescriptionRequestBody(String conceptId, String typeId) {
		return createDescriptionRequestBody(conceptId, typeId, Concepts.MODULE_SCT_CORE, SnomedApiTestConstants.UK_ACCEPTABLE_MAP);
	}

	public static Builder<String, Object> createDescriptionRequestBody(String conceptId, String typeId, String moduleId) {
		return createDescriptionRequestBody(conceptId, typeId, moduleId, SnomedApiTestConstants.UK_ACCEPTABLE_MAP);
	}

	public static Builder<String, Object> createDescriptionRequestBody(String conceptId, String typeId, String moduleId, Map<String, Acceptability> acceptabilityMap) {
		return createDescriptionRequestBody(conceptId, typeId, moduleId, acceptabilityMap, CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE);
	}

	public static Builder<String, Object> createDescriptionRequestBody(String conceptId, String typeId, String moduleId, 
			Map<String, Acceptability> acceptabilityMap,
			CaseSignificance caseSignificance) {
		return createDescriptionRequestBody(conceptId, typeId, moduleId, acceptabilityMap, caseSignificance, DEFAULT_LANGUAGE_CODE);
	}

	private static Builder<String, Object> createDescriptionRequestBody(String conceptId, String typeId, String moduleId, Map<String, Acceptability> acceptabilityMap,
			CaseSignificance caseSignificance, final String languageCode) {
		return ImmutableMap.<String, Object>builder()
				.put("conceptId", conceptId)
				.put("moduleId", moduleId)
				.put("typeId", typeId)
				.put("term", DEFAULT_TERM)
				.put("languageCode", languageCode)
				.put("acceptability", acceptabilityMap)
				.put("caseSignificance", caseSignificance);
	}

	public static String createNewRelationship(IBranchPath relationshipPath) {
		return createNewRelationship(relationshipPath, Concepts.ROOT_CONCEPT, Concepts.PART_OF, Concepts.NAMESPACE_ROOT, CharacteristicType.STATED_RELATIONSHIP, 0);
	}

	public static String createNewRelationship(IBranchPath relationshipPath, String sourceId, String typeId, String destinationId) {
		return createNewRelationship(relationshipPath, sourceId, typeId, destinationId, CharacteristicType.STATED_RELATIONSHIP, 0);
	}

	public static String createNewRelationship(IBranchPath relationshipPath, String sourceId, String typeId, String destinationId, CharacteristicType characteristicType) {
		return createNewRelationship(relationshipPath, sourceId, typeId, destinationId, characteristicType, 0);
	}

	public static String createNewRelationship(IBranchPath relationshipPath, String sourceId, String typeId, String destinationId, CharacteristicType characteristicType, int group) {
		Map<?, ?> relationshipRequestBody = createRelationshipRequestBody(sourceId, typeId, destinationId, Concepts.MODULE_SCT_CORE, characteristicType, group)
				.put("commitComment", "Created new relationship")
				.build();

		return lastPathSegment(createComponent(relationshipPath, SnomedComponentType.RELATIONSHIP, relationshipRequestBody)
				.statusCode(201)
				.body(equalTo(""))
				.extract().header("Location"));
	}

	public static Builder<String, Object> createRelationshipRequestBody(String sourceId, String typeId, String destinationId) {
		return createRelationshipRequestBody(sourceId, typeId, destinationId, Concepts.MODULE_SCT_CORE, CharacteristicType.STATED_RELATIONSHIP, 0);
	}

	public static Builder<String, Object> createRelationshipRequestBody(String sourceId, String typeId, String destinationId, CharacteristicType characteristicType) {
		return createRelationshipRequestBody(sourceId, typeId, destinationId, Concepts.MODULE_SCT_CORE, characteristicType, 0);
	}

	public static Builder<String, Object> createRelationshipRequestBody(String sourceId, String typeId, String destinationId, String moduleId) {
		return createRelationshipRequestBody(sourceId, typeId, destinationId, moduleId, CharacteristicType.STATED_RELATIONSHIP, 0);
	}

	public static Builder<String, Object> createRelationshipRequestBody(String sourceId, String typeId, String destinationId, String moduleId, CharacteristicType characteristicType, int group) {
		return ImmutableMap.<String, Object>builder()
				.put("moduleId", moduleId)
				.put("sourceId", sourceId)
				.put("typeId", typeId)
				.put("destinationId", destinationId)
				.put("characteristicType", characteristicType)
				.put("group", group);
	}

	public static String createNewRefSet(IBranchPath refSetPath) {
		return createNewRefSet(refSetPath, SnomedRefSetType.SIMPLE);
	}

	public static String createNewRefSet(IBranchPath refSetPath, SnomedRefSetType type, String identifierConceptId) {
		
		Map<?, ?> refSetRequestBody = ImmutableMap.<String, Object>builder()
				.put("id", identifierConceptId)
				.put("type", type)
				.put("referencedComponentType", getFirstAllowedReferencedComponentType(type))
				.put("commitComment", "Created new reference set")
				.build();
		
		return lastPathSegment(createComponent(refSetPath, SnomedComponentType.REFSET, refSetRequestBody)
				.statusCode(201)
				.body(equalTo(""))
				.extract().header("Location"));
	}
	
	public static String createNewRefSet(IBranchPath refSetPath, SnomedRefSetType type) {
		String parentConceptId = SnomedRefSetUtil.getParentConceptId(type);
		String referencedComponentType = getFirstAllowedReferencedComponentType(type);
		Map<?, ?> refSetRequestBody = createConceptRequestBody(parentConceptId)
				.put("type", type)
				.put("referencedComponentType", referencedComponentType)
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

		return createNewRefSetMember(memberPath, referencedConceptId, refSetId);
	}

	public static String createNewRefSetMember(IBranchPath memberPath, String referencedConceptId, String refSetId) {
		Map<?, ?> requestBody = createRefSetMemberRequestBody(refSetId, referencedConceptId)
				.put("commitComment", "Created new reference set member")
				.build();

		return lastPathSegment(createComponent(memberPath, SnomedComponentType.MEMBER, requestBody)
				.statusCode(201)
				.body(equalTo(""))
				.extract().header("Location"));
	}

	public static String createNewLanguageRefSetMember(IBranchPath memberPath, String referencedDescriptionId, String refSetId, String acceptabilityId) {
		Map<?, ?> requestBody = createRefSetMemberRequestBody(refSetId, referencedDescriptionId)
				.put("acceptabilityId", acceptabilityId)
				.put("commitComment", "Created new language reference set member")
				.build();

		return lastPathSegment(createComponent(memberPath, SnomedComponentType.MEMBER, requestBody)
				.statusCode(201)
				.body(equalTo(""))
				.extract().header("Location"));
	}

	public static Builder<String, Object> createRefSetMemberRequestBody(String refSetId, String referencedComponentId) {
		return ImmutableMap.<String, Object>builder()
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("referenceSetId", refSetId)
				.put("referencedComponentId", referencedComponentId);
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
				.put("acceptability", ImmutableMap.of())
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
		reactivationRequest.remove("inactivationIndicator");
		reactivationRequest.remove("associationTargets");
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

	public static void changeToDefining(IBranchPath conceptPath, String conceptId) {
		Map<?, ?> conceptUpdateRequest = ImmutableMap.builder()
				.put("definitionStatus", DefinitionStatus.FULLY_DEFINED)
				.put("commitComment", "Changed definition status on concept")
				.build();

		updateComponent(conceptPath, SnomedComponentType.CONCEPT, conceptId, conceptUpdateRequest).statusCode(204);
	}

	public static void changeToAcceptable(IBranchPath conceptPath, String conceptId, String languageRefSetId) {
		SnomedConcept concept = getComponent(conceptPath, SnomedComponentType.CONCEPT, conceptId, "descriptions()")
				.statusCode(200)
				.extract()
				.as(SnomedConcept.class);
		
		for (SnomedDescription description : concept.getDescriptions()) {
			if (description.isActive() 
					&& description.getTypeId().equals(Concepts.SYNONYM)
					&& Acceptability.PREFERRED.equals(description.getAcceptabilityMap().get(languageRefSetId))) {
				
				Map<String, Acceptability> newAcceptabilityMap = newHashMap(description.getAcceptabilityMap());
				newAcceptabilityMap.put(languageRefSetId, Acceptability.ACCEPTABLE);
				
				Map<?, ?> requestBody = ImmutableMap.builder()
						.put("acceptability", newAcceptabilityMap)
						.put("commitComment", String.format("Updated description acceptability on previous PT %s", description.getId()))
						.build();
				
				updateComponent(conceptPath, SnomedComponentType.DESCRIPTION, description.getId(), requestBody).statusCode(204);
			}
		}
	}

	public static void changeRelationshipGroup(IBranchPath relationshipPath, String relationshipId) {
		Map<?, ?> relationshipUpdateRequest = ImmutableMap.builder()
				.put("group", 99)
				.put("commitComment", "Changed group on relationship")
				.build();

		updateComponent(relationshipPath, SnomedComponentType.RELATIONSHIP, relationshipId, relationshipUpdateRequest).statusCode(204);
	}

	public static String reserveComponentId(String namespace, ComponentCategory category) {
		ISnomedIdentifierService identifierService = ApplicationContext.getInstance().getService(ISnomedIdentifierService.class);
		return Iterables.getOnlyElement(identifierService.reserve(namespace, category, 1));
	}

	public static String createReferencedComponent(IBranchPath branchPath, SnomedRefSetType refSetType) {
		String referencedComponentType = getFirstAllowedReferencedComponentType(refSetType);
		return createNewComponent(branchPath, referencedComponentType);
	}

	public static String createNewComponent(IBranchPath branchPath, String referencedComponentType) {
		switch (referencedComponentType) {
		case SnomedTerminologyComponentConstants.CONCEPT:
			return createNewConcept(branchPath);
		case SnomedTerminologyComponentConstants.DESCRIPTION:
			return createNewDescription(branchPath);
		case SnomedTerminologyComponentConstants.RELATIONSHIP:
			return createNewRelationship(branchPath);
		default:
			throw new IllegalStateException("Can't create a referenced component of type '" + referencedComponentType + "'.");
		}
	}

	public static String getFirstAllowedReferencedComponentType(SnomedRefSetType refSetType) {
		return Iterables.getFirst(RefSetSupport.getSupportedReferencedComponentTypes(refSetType), null);
	}

	public static ComponentCategory getFirstAllowedReferencedComponentCategory(SnomedRefSetType refSetType) {
		String referencedComponentType = getFirstAllowedReferencedComponentType(refSetType);

		switch (referencedComponentType) {
		case SnomedTerminologyComponentConstants.CONCEPT:
			return ComponentCategory.CONCEPT;
		case SnomedTerminologyComponentConstants.DESCRIPTION:
			return ComponentCategory.DESCRIPTION;
		case SnomedTerminologyComponentConstants.RELATIONSHIP:
			return ComponentCategory.RELATIONSHIP;
		default:
			throw new IllegalStateException("Can't convert referenced component type '" + referencedComponentType + "' to a category.");
		}
	}

	public static void createConcreteDomainParentConcept(IBranchPath conceptPath) {
		SnomedCoreConfiguration snomedConfiguration = getSnomedCoreConfiguration();

		Map<?, ?> parentConceptRequestBody = createConceptRequestBody(Concepts.REFSET_ROOT_CONCEPT)
				.put("id", snomedConfiguration.getConcreteDomainTypeRefsetIdentifier())
				.put("commitComment", "Created concrete domain reference set parent concept")
				.build();

		createComponent(conceptPath, SnomedComponentType.CONCEPT, parentConceptRequestBody).statusCode(201);
		getComponent(conceptPath, SnomedComponentType.CONCEPT, Concepts.REFSET_CONCRETE_DOMAIN_TYPE).statusCode(200);
	}

	public static String createConcreteDomainRefSet(IBranchPath refSetPath, DataType dataType) {
		String refSetId = SnomedRefSetUtil.getRefSetId(dataType);

		Map<?, ?> refSetRequestBody = createConceptRequestBody(Concepts.REFSET_CONCRETE_DOMAIN_TYPE)
				.put("id", refSetId)
				.put("type", SnomedRefSetType.CONCRETE_DATA_TYPE)
				.put("referencedComponentType", CoreTerminologyBroker.UNSPECIFIED)
				.put("commitComment", "Created new concrete domain reference set")
				.build();

		createComponent(refSetPath, SnomedComponentType.REFSET, refSetRequestBody).statusCode(201);
		getComponent(refSetPath, SnomedComponentType.CONCEPT, refSetId).statusCode(200);
		getComponent(refSetPath, SnomedComponentType.REFSET, refSetId).statusCode(200);

		return refSetId;
	}

	private static SnomedCoreConfiguration getSnomedCoreConfiguration() {
		return SnowOwlApplication.INSTANCE.getConfiguration().getModuleConfig(SnomedCoreConfiguration.class);
	}

	private SnomedRestFixtures() {
		throw new UnsupportedOperationException("This class is not supposed to be instantiated.");
	}
}
