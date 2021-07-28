/*
 * Copyright 2018-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.rest;

import static com.b2international.snowowl.snomed.core.rest.SnomedApiTestConstants.*;
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.createComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.searchComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.updateComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedMergingRestRequests.createMerge;
import static com.b2international.snowowl.snomed.core.rest.SnomedMergingRestRequests.waitForMergeJob;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.assertCreated;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;
import static com.google.common.collect.Maps.newHashMap;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.List;
import java.util.Map;

import com.b2international.commons.Pair;
import com.b2international.commons.Pair.IdenticalPair;
import com.b2international.commons.json.Json;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.core.terminology.TerminologyRegistry;
import com.b2international.snowowl.snomed.cis.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.*;
import com.b2international.snowowl.snomed.core.domain.refset.DataType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.datastore.request.RefSetSupport;
import com.google.common.collect.Iterables;

import io.restassured.response.ValidatableResponse;

/**
 * @since 5.0
 */
public abstract class SnomedRestFixtures {

	public static final String DEFAULT_TERM = "Description term";
	public static final String DEFAULT_LANGUAGE_CODE = "en";

	private static final Map<IdenticalPair<String, String>, String> REFERENCED_COMPONENT_CACHE = newHashMap();
	
	public static String createNewConcept(IBranchPath conceptPath) {
		return createNewConcept(conceptPath, Concepts.ROOT_CONCEPT);
	}

	public static String createNewConcept(IBranchPath conceptPath, String parentConceptId) {
		return createNewConcept(conceptPath, Json.assign(
			createConceptRequestBody(parentConceptId),
			Json.object("commitComment", "Created new concept")
		)); 
	}
	
	public static String createNewConcept(IBranchPath conceptPath, Map<String, ?> requestBody) {
		return assertCreated(createComponent(conceptPath, SnomedComponentType.CONCEPT, requestBody));
	}

	public static Json childUnderRootWithDefaults() {
		return createConceptRequestBody(Concepts.ROOT_CONCEPT);
	}
	
	public static Json createConceptRequestBody(String parentConceptId) {
		return createConceptRequestBody(parentConceptId, Concepts.MODULE_SCT_CORE, SnomedApiTestConstants.UK_PREFERRED_MAP);
	}
	
	public static Json createConceptRequestBody(String parentConceptId, String moduleId) {
		return createConceptRequestBody(parentConceptId, moduleId, SnomedApiTestConstants.UK_PREFERRED_MAP);
	}

	public static Json createConceptRequestBody(String parentConceptId, String moduleId, Map<String, Acceptability> acceptabilityMap) {
		return createConceptRequestBody(parentConceptId, moduleId, acceptabilityMap, true);
	}
	
	public static Json createConceptRequestBody(String parentConceptId, String moduleId, Map<String, Acceptability> acceptabilityMap, boolean active) {
		return Json.object(
			"active", active,
			"moduleId", moduleId, // module is applied to all subcomponents implicitly via the API
			"descriptions", Json.array(
				Json.object(
					"typeId", Concepts.FULLY_SPECIFIED_NAME,
					"term", "FSN of concept",
					"languageCode", DEFAULT_LANGUAGE_CODE,
					"acceptability", acceptabilityMap
				),
				Json.object(
					"typeId", Concepts.SYNONYM,
					"term", "PT of concept",
					"languageCode", DEFAULT_LANGUAGE_CODE,
					"acceptability", acceptabilityMap
				)
			),
			"relationships", Json.array(
				Json.object(
					"active", active,
					"typeId", Concepts.IS_A,
					"destinationId", parentConceptId
				)
			)
		);
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
		Map<?, ?> requestBody = Json.assign(createDescriptionRequestBody(conceptId, typeId, Concepts.MODULE_SCT_CORE, acceptabilityMap, Concepts.ONLY_INITIAL_CHARACTER_CASE_INSENSITIVE, languageCode))
				.with("commitComment", "Created new description");
		return createNewDescription(descriptionPath, requestBody);
	}

	public static String createNewDescription(IBranchPath descriptionPath, String conceptId, String typeId, Map<String, Acceptability> acceptabilityMap) {
		Map<?, ?> requestBody = Json.assign(createDescriptionRequestBody(conceptId, typeId, Concepts.MODULE_SCT_CORE, acceptabilityMap))
				.with("commitComment", "Created new description");

		return createNewDescription(descriptionPath, requestBody);
	}

	public static String createNewTextDefinition(IBranchPath descriptionPath, Map<String, Acceptability> acceptabilityMap) {
		Map<?, ?> requestBody = Json.assign(createDescriptionRequestBody(Concepts.ROOT_CONCEPT, Concepts.TEXT_DEFINITION, Concepts.MODULE_SCT_CORE, acceptabilityMap))
				.with("commitComment", "Created new text definition");
		return createNewDescription(descriptionPath, requestBody);
	}
	
	public static String createNewDescription(IBranchPath descriptionPath, Map<?, ?> requestBody) {
		return assertCreated(createComponent(descriptionPath, SnomedComponentType.DESCRIPTION, requestBody));
	}

	public static Json createDescriptionRequestBody(String conceptId) {
		return createDescriptionRequestBody(conceptId, Concepts.SYNONYM, Concepts.MODULE_SCT_CORE, SnomedApiTestConstants.UK_ACCEPTABLE_MAP);
	}

	public static Json createDescriptionRequestBody(String conceptId, String typeId) {
		return createDescriptionRequestBody(conceptId, typeId, Concepts.MODULE_SCT_CORE, SnomedApiTestConstants.UK_ACCEPTABLE_MAP);
	}

	public static Json createDescriptionRequestBody(String conceptId, String typeId, String moduleId) {
		return createDescriptionRequestBody(conceptId, typeId, moduleId, SnomedApiTestConstants.UK_ACCEPTABLE_MAP);
	}

	public static Json createDescriptionRequestBody(String conceptId, String typeId, String moduleId, Map<String, Acceptability> acceptabilityMap) {
		return createDescriptionRequestBody(conceptId, typeId, moduleId, acceptabilityMap, Concepts.ONLY_INITIAL_CHARACTER_CASE_INSENSITIVE);
	}

	public static Json createDescriptionRequestBody(String conceptId, String typeId, String moduleId, 
			Map<String, Acceptability> acceptabilityMap,
			String caseSignificanceId) {
		return createDescriptionRequestBody(conceptId, typeId, moduleId, acceptabilityMap, caseSignificanceId, DEFAULT_LANGUAGE_CODE);
	}

	private static Json createDescriptionRequestBody(
			String conceptId, 
			String typeId, 
			String moduleId, 
			Map<String, Acceptability> acceptabilityMap,
			String caseSignificanceId, 
			final String languageCode) {
		return Json.object(
			"conceptId", conceptId,
			"moduleId", moduleId,
			"typeId", typeId,
			"term", DEFAULT_TERM,
			"languageCode", languageCode,
			"acceptability", acceptabilityMap,
			"caseSignificanceId", caseSignificanceId
		);
	}

	public static String createNewRelationship(IBranchPath relationshipPath) {
		return createNewRelationship(relationshipPath, Concepts.ROOT_CONCEPT, Concepts.PART_OF, Concepts.NAMESPACE_ROOT, Concepts.STATED_RELATIONSHIP, 0);
	}

	public static String createNewRelationship(IBranchPath relationshipPath, String sourceId, String typeId, String destinationId) {
		return createNewRelationship(relationshipPath, sourceId, typeId, destinationId, Concepts.STATED_RELATIONSHIP, 0);
	}

	public static String createNewRelationship(IBranchPath relationshipPath, String sourceId, String typeId, String destinationId, String characteristicTypeId) {
		return createNewRelationship(relationshipPath, sourceId, typeId, destinationId, characteristicTypeId, 0);
	}

	public static String createNewRelationship(IBranchPath relationshipPath, String sourceId, String typeId, String destinationId, String characteristicTypeId, int group) {
		Map<?, ?> relationshipRequestBody = Json.assign(createRelationshipRequestBody(sourceId, typeId, destinationId, Concepts.MODULE_SCT_CORE, characteristicTypeId, group))
				.with("commitComment", "Created new relationship");

		return assertCreated(createComponent(relationshipPath, SnomedComponentType.RELATIONSHIP, relationshipRequestBody));
	}

	public static Json createRelationshipRequestBody(String sourceId, String typeId, String destinationId) {
		return createRelationshipRequestBody(sourceId, typeId, destinationId, Concepts.MODULE_SCT_CORE, Concepts.STATED_RELATIONSHIP, 0);
	}

	public static Json createRelationshipRequestBody(String sourceId, String typeId, String destinationId, String characteristicTypeId) {
		return createRelationshipRequestBody(sourceId, typeId, destinationId, Concepts.MODULE_SCT_CORE, characteristicTypeId, 0);
	}

	public static Json createRelationshipRequestBody(String sourceId, String typeId, String destinationId, String moduleId, String characteristicTypeId) {
		return createRelationshipRequestBody(sourceId, typeId, destinationId, moduleId, characteristicTypeId, 0);
	}

	public static Json createRelationshipRequestBody(String sourceId, String typeId, String destinationId, String moduleId, String characteristicTypeId, int relationshipGroup) {
		return Json.object(
			"moduleId", moduleId,
			"sourceId", sourceId,
			"typeId", typeId,
			"destinationId", destinationId,
			"characteristicTypeId", characteristicTypeId,
			"relationshipGroup", relationshipGroup
		);
	}

	public static String createNewConcreteValue(IBranchPath concreteValuePath) {
		return createNewConcreteValue(concreteValuePath, Concepts.ROOT_CONCEPT, Concepts.PART_OF, new RelationshipValue("Hello World!"));
	}

	public static String createNewConcreteValue(IBranchPath concreteValuePath, String sourceId, String typeId, RelationshipValue value) {
		return createNewConcreteValue(concreteValuePath, sourceId, typeId, value, Concepts.INFERRED_RELATIONSHIP);
	}

	public static String createNewConcreteValue(IBranchPath concreteValuePath, String sourceId, String typeId, RelationshipValue value, String characteristicTypeId) {
		return createNewConcreteValue(concreteValuePath, sourceId, typeId, value, characteristicTypeId, 0);
	}

	public static String createNewConcreteValue(IBranchPath concreteValuePath, String sourceId, String typeId, RelationshipValue value, String characteristicTypeId, int relationshipGroup) {
		Map<?, ?> concreteValueRequestBody = Json.assign(createConcreteValueRequestBody(
			sourceId, typeId, value, characteristicTypeId, relationshipGroup))
			.with("commitComment", "Created new relationship with value");

		return assertCreated(createComponent(concreteValuePath, SnomedComponentType.RELATIONSHIP, concreteValueRequestBody));
	}

	public static Json createConcreteValueRequestBody(String sourceId, String typeId, RelationshipValue value) {
		return createConcreteValueRequestBody(sourceId, typeId, value, Concepts.INFERRED_RELATIONSHIP);
	}

	public static Json createConcreteValueRequestBody(String sourceId, String typeId, RelationshipValue value, String characteristicTypeId) {
		return createConcreteValueRequestBody(sourceId, typeId, value, characteristicTypeId, 0);
	}

	public static Json createConcreteValueRequestBody(String sourceId, String typeId, RelationshipValue value, String characteristicTypeId, int relationshipGroup) {
		return createConcreteValueRequestBody(sourceId, typeId, value, characteristicTypeId, relationshipGroup, Concepts.MODULE_SCT_CORE);
	}

	public static Json createConcreteValueRequestBody(String sourceId, String typeId, RelationshipValue value, String characteristicTypeId, int relationshipGroup, String moduleId) {
		return Json.object(
			"moduleId", moduleId,
			"sourceId", sourceId,
			"typeId", typeId,
			"value", value.map(
				i -> Json.object("type", value.type(), "numericValue", i.toString()),
				d -> Json.object("type", value.type(), "numericValue", d.toPlainString()),
				s -> Json.object("type", value.type(), "stringValue", s)
			),
			"characteristicTypeId", characteristicTypeId,
			"relationshipGroup", relationshipGroup
		);
	}
	
	public static String createNewRefSet(IBranchPath refSetPath) {
		return createNewRefSet(refSetPath, SnomedRefSetType.SIMPLE);
	}

	public static String createNewRefSet(IBranchPath refSetPath, SnomedRefSetType type, String identifierConceptId) {
		return assertCreated(
			createComponent(refSetPath, SnomedComponentType.REFSET, 
			Json.object(
				"id", identifierConceptId,
				"type", type,
				"referencedComponentType", getFirstAllowedReferencedComponentType(type),
				"commitComment", "Created new reference set"
			))
		);
	}
	
	public static String createNewRefSet(IBranchPath refSetPath, SnomedRefSetType type) {
		String parentConceptId = SnomedRefSetUtil.getParentConceptId(type);
		String referencedComponentType = getFirstAllowedReferencedComponentType(type);
		Map<?, ?> refSetRequestBody = Json.assign(
			createConceptRequestBody(parentConceptId),
			Json.object(
				"type", type,
				"referencedComponentType", referencedComponentType,
				"commitComment", "Created new reference set"
			)
		);

		return assertCreated(createComponent(refSetPath, SnomedComponentType.REFSET, refSetRequestBody));
	}

	public static String createNewRefSetMember(IBranchPath memberPath) {
		return createNewRefSetMember(memberPath, Concepts.ROOT_CONCEPT);
	}

	public static String createNewRefSetMember(IBranchPath memberPath, String referencedConceptId) {
		String refSetId = createNewRefSet(memberPath);

		return createNewRefSetMember(memberPath, referencedConceptId, refSetId);
	}

	public static String createNewRefSetMember(IBranchPath memberPath, String referencedConceptId, String refSetId) {
		Map<?, ?> requestBody = Json.assign(createRefSetMemberRequestBody(refSetId, referencedConceptId))
				.with("commitComment", "Created new reference set member");

		return assertCreated(createComponent(memberPath, SnomedComponentType.MEMBER, requestBody));
	}
	
	public static String createNewRefSetMember(IBranchPath memberPath, String referencedConceptId, String refSetId, final Map<String, Object> properties) {
		Map<?, ?> requestBody = Json.assign(createRefSetMemberRequestBody(refSetId, referencedConceptId))
				.with("commitComment", "Created new reference set member")
				.with(properties);

		return assertCreated(createComponent(memberPath, SnomedComponentType.MEMBER, requestBody));
	}

	public static String createNewLanguageRefSetMember(IBranchPath memberPath, String referencedDescriptionId, String refSetId, String acceptabilityId) {
		Map<?, ?> requestBody = Json.assign(createRefSetMemberRequestBody(refSetId, referencedDescriptionId))
				.with("acceptabilityId", acceptabilityId)
				.with("commitComment", "Created new language reference set member");

		return assertCreated(createComponent(memberPath, SnomedComponentType.MEMBER, requestBody));
	}

	public static Json createRefSetMemberRequestBody(String refSetId, String referencedComponentId) {
		return Json.object(
			"moduleId", Concepts.MODULE_SCT_CORE,
			"refsetId", refSetId,
			"referencedComponentId", referencedComponentId
		);
	}
	
	public static ValidatableResponse merge(IBranchPath sourcePath, IBranchPath targetPath, String commitComment) {
		final String mergeLocation = createMerge(sourcePath, targetPath, commitComment)
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
		updateComponent(
			conceptPath, 
			SnomedComponentType.CONCEPT, 
			conceptId, 
			Json.object(
				"active", false,
				"commitComment", "Inactivated concept"
			)
		).statusCode(204);
	}

	public static void inactivateDescription(IBranchPath descriptionPath, String descriptionId) {
		updateComponent(
			descriptionPath, 
			SnomedComponentType.DESCRIPTION, 
			descriptionId,
			Json.object(
				"active", false,
				"acceptability", Json.object(),
				"commitComment", "Inactivated description"
			)
		).statusCode(204);
	}

	public static void inactivateRelationship(IBranchPath relationshipPath, String relationshipId) {
		updateComponent(
			relationshipPath, 
			SnomedComponentType.RELATIONSHIP, 
			relationshipId, 
			Json.object(
				"active", false,
				"commitComment", "Inactivated relationship"
			)
		).statusCode(204);
	}

	@SuppressWarnings("unchecked")
	public static void reactivateConcept(IBranchPath branch, String conceptId) {
		final Map<String, Object> concept = getComponent(branch, SnomedComponentType.CONCEPT, conceptId, "descriptions()", "relationships()")
				.statusCode(200)
				.extract().as(Map.class);

		final Json reactivationRequest = Json.assign(concept)
			.without("inactivationIndicator")
			.without("associationTargets")
			.with("active", true)
			.with("commitComment", "Reactivated concept");

		// reactivate all relationships as well
		final Map<String, Object> relationships = (Map<String, Object>) reactivationRequest.get("relationships");
		final List<Map<String, Object>> relationshipItems = (List<Map<String, Object>>) relationships.get("items");
		relationshipItems.forEach(relationship -> {
			relationship.put("active", true);
		});

		updateComponent(branch, SnomedComponentType.CONCEPT, conceptId, reactivationRequest).statusCode(204);
	}
	
	public static void changeCaseSignificance(IBranchPath descriptionPath, String descriptionId) {
		changeCaseSignificance(descriptionPath, descriptionId, Concepts.ENTIRE_TERM_CASE_SENSITIVE);
	}

	public static void changeCaseSignificance(IBranchPath descriptionPath, String descriptionId, String caseSignificanceId) {
		updateComponent(
			descriptionPath, 
			SnomedComponentType.DESCRIPTION, 
			descriptionId,
			Json.object(
				"caseSignificanceId", caseSignificanceId,
				"commitComment", "Changed case significance on description"
			)
		).statusCode(204);
	}

	public static void changeToDefining(IBranchPath conceptPath, String conceptId) {
		updateComponent(
			conceptPath, 
			SnomedComponentType.CONCEPT, 
			conceptId,
			Json.object(
				"definitionStatusId", Concepts.FULLY_DEFINED,
				"commitComment", "Changed definition status on concept"
			)
		).statusCode(204);
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
				
				updateComponent(
					conceptPath, 
					SnomedComponentType.DESCRIPTION, 
					description.getId(),
					Json.object(
						"acceptability", newAcceptabilityMap,
						"commitComment", "Updated description acceptability on previous PT ".concat(description.getId())
					)
				).statusCode(204);
			}
		}
	}

	public static void changeRelationshipGroup(IBranchPath relationshipPath, String relationshipId) {
		updateComponent(
			relationshipPath, 
			SnomedComponentType.RELATIONSHIP, 
			relationshipId, 
			Json.object(
				"relationshipGroup", 99,
				"commitComment", "Changed group on relationship"
			)
		).statusCode(204);
	}

	public static String reserveComponentId(String namespace, ComponentCategory category) {
		ISnomedIdentifierService identifierService = ApplicationContext.getInstance().getService(ISnomedIdentifierService.class);
		return Iterables.getOnlyElement(identifierService.reserve(namespace, category, 1));
	}

	public static String createReferencedComponent(IBranchPath branchPath, SnomedRefSetType refSetType) {
		return createNewComponent(branchPath, getFirstAllowedReferencedComponentType(refSetType));
	}

	public static String createNewComponent(IBranchPath branchPath, String referencedComponentType) {
		switch (referencedComponentType) {
		case SnomedConcept.TYPE:
			return createNewConcept(branchPath);
		case SnomedDescription.TYPE:
			return createNewDescription(branchPath);
		case SnomedRelationship.TYPE:
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
		case SnomedConcept.TYPE:
			return ComponentCategory.CONCEPT;
		case SnomedDescription.TYPE:
			return ComponentCategory.DESCRIPTION;
		case SnomedRelationship.TYPE:
			return ComponentCategory.RELATIONSHIP;
		default:
			throw new IllegalStateException("Can't convert referenced component type '" + referencedComponentType + "' to a category.");
		}
	}
	
	public static SnomedComponentType getSnomedComponentType(String referencedComponentType) {
		switch (referencedComponentType) {
		case SnomedConcept.TYPE:
			return SnomedComponentType.CONCEPT;
		case SnomedDescription.TYPE:
			return SnomedComponentType.DESCRIPTION;
		case SnomedRelationship.TYPE:
			return SnomedComponentType.RELATIONSHIP;
		default:
			throw new IllegalStateException("Can't convert referenced component type '" + referencedComponentType + "' to a component type.");
		}
	}
	
	public static String getFirstMatchingComponent(IBranchPath branchPath, String referencedComponentType) {
		IdenticalPair<String, String> key = Pair.identicalPairOf(branchPath.getPath(), referencedComponentType);
		if (!REFERENCED_COMPONENT_CACHE.containsKey(key)) {
			final String referencedComponentId = Iterables.getFirst(searchComponent(branchPath, getSnomedComponentType(referencedComponentType), Json.object("limit", 1))
					.extract()
					.<List<String>>path("items.id"), null);
			REFERENCED_COMPONENT_CACHE.put(key, referencedComponentId);
		}
		return REFERENCED_COMPONENT_CACHE.get(key);
	}

	public static void createConcreteDomainParentConcept(IBranchPath conceptPath) {
		SnomedCoreConfiguration snomedConfiguration = getSnomedCoreConfiguration();

		Map<?, ?> parentConceptRequestBody = Json.assign(createConceptRequestBody(Concepts.REFSET_ROOT_CONCEPT))
				.with("id", snomedConfiguration.getConcreteDomainTypeRefsetIdentifier())
				.with("commitComment", "Created concrete domain reference set parent concept");

		createComponent(conceptPath, SnomedComponentType.CONCEPT, parentConceptRequestBody).statusCode(201);
		getComponent(conceptPath, SnomedComponentType.CONCEPT, Concepts.REFSET_CONCRETE_DOMAIN_TYPE).statusCode(200);
	}

	public static String createConcreteDomainRefSet(IBranchPath refSetPath, DataType dataType) {
		String refSetId = SnomedRefSetUtil.getRefSetId(dataType);

		Map<?, ?> refSetRequestBody = Json.assign(
			createConceptRequestBody(Concepts.REFSET_CONCRETE_DOMAIN_TYPE),
			Json.object(
				"id", refSetId,
				"type", SnomedRefSetType.CONCRETE_DATA_TYPE,
				"referencedComponentType", TerminologyRegistry.UNSPECIFIED,
				"commitComment", "Created new concrete domain reference set"
			)
		);

		createComponent(refSetPath, SnomedComponentType.REFSET, refSetRequestBody).statusCode(201);
		getComponent(refSetPath, SnomedComponentType.CONCEPT, refSetId).statusCode(200);
		getComponent(refSetPath, SnomedComponentType.REFSET, refSetId).statusCode(200);

		return refSetId;
	}
	
	public static Json getValidProperties(SnomedRefSetType refSetType, String referencedComponentId) {
		switch (refSetType) {
		case ASSOCIATION:
			return Json.object(
				SnomedRf2Headers.FIELD_TARGET_COMPONENT_ID, Concepts.ROOT_CONCEPT
			);
		case ATTRIBUTE_VALUE:
			return Json.object(
				SnomedRf2Headers.FIELD_VALUE_ID, Concepts.ROOT_CONCEPT
			);
		case COMPLEX_MAP:
			return Json.object(
				SnomedRf2Headers.FIELD_MAP_TARGET, "complexMapTarget",
				SnomedRf2Headers.FIELD_MAP_GROUP, 0,
				SnomedRf2Headers.FIELD_MAP_PRIORITY, 0,
				SnomedRf2Headers.FIELD_MAP_RULE, "complexMapRule",
				SnomedRf2Headers.FIELD_MAP_ADVICE, "complexMapAdvice",
				SnomedRf2Headers.FIELD_CORRELATION_ID, Concepts.REFSET_CORRELATION_NOT_SPECIFIED
			);
		case COMPLEX_BLOCK_MAP:
			return Json.object(
				SnomedRf2Headers.FIELD_MAP_TARGET, "complexBlockMapTarget",
				SnomedRf2Headers.FIELD_MAP_GROUP, 0,
				SnomedRf2Headers.FIELD_MAP_PRIORITY, 0,
				SnomedRf2Headers.FIELD_MAP_RULE, "complexBlockMapRule",
				SnomedRf2Headers.FIELD_MAP_ADVICE, "complexBlockMapAdvice",
				SnomedRf2Headers.FIELD_CORRELATION_ID, Concepts.REFSET_CORRELATION_NOT_SPECIFIED,
				SnomedRf2Headers.FIELD_MAP_BLOCK, 1
			);
		case DESCRIPTION_TYPE:
			return Json.object(
				SnomedRf2Headers.FIELD_DESCRIPTION_FORMAT, Concepts.ROOT_CONCEPT,
				SnomedRf2Headers.FIELD_DESCRIPTION_LENGTH, 100
			);
		case EXTENDED_MAP:
			return Json.object(
				SnomedRf2Headers.FIELD_MAP_TARGET, "extendedMapTarget",
				SnomedRf2Headers.FIELD_MAP_GROUP, 10,
				SnomedRf2Headers.FIELD_MAP_PRIORITY, 10,
				SnomedRf2Headers.FIELD_MAP_RULE, "extendedMapRule",
				SnomedRf2Headers.FIELD_MAP_ADVICE, "extendedMapAdvice",
				SnomedRf2Headers.FIELD_CORRELATION_ID, Concepts.REFSET_CORRELATION_NOT_SPECIFIED,
				SnomedRf2Headers.FIELD_MAP_CATEGORY_ID, Concepts.MAP_CATEGORY_NOT_CLASSIFIED
			);
		case LANGUAGE:
			return Json.object(
				SnomedRf2Headers.FIELD_ACCEPTABILITY_ID, Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_ACCEPTABLE
			);
		case MODULE_DEPENDENCY:
			return Json.object(
				SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME, "20170222",
				SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME, "20170223"
			);
		case SIMPLE:
			return Json.object();
		case SIMPLE_MAP:
			return Json.object(
				SnomedRf2Headers.FIELD_MAP_TARGET, "simpleMapTarget"
			);
		case SIMPLE_MAP_WITH_DESCRIPTION:
			return Json.object(
				SnomedRf2Headers.FIELD_MAP_TARGET, "mapTarget",
				SnomedRf2Headers.FIELD_MAP_TARGET_DESCRIPTION, "mapTargetDescription"
			);
		case OWL_AXIOM:
			return Json.object(
				SnomedRf2Headers.FIELD_OWL_EXPRESSION, SnomedApiTestConstants.owlAxiom1(referencedComponentId)
			);
		case OWL_ONTOLOGY:
			return Json.object(
				SnomedRf2Headers.FIELD_OWL_EXPRESSION, OWL_ONTOLOGY_1
			);
		case MRCM_DOMAIN:
			return Json.object(
				SnomedRf2Headers.FIELD_MRCM_DOMAIN_CONSTRAINT, DOMAIN_CONSTRAINT,
				SnomedRf2Headers.FIELD_MRCM_PARENT_DOMAIN, PARENT_DOMAIN,
				SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_CONSTRAINT, PROXIMAL_PRIMITIVE_CONSTRAINT,
				SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_REFINEMENT, PROXIMAL_PRIMITIVE_REFINEMENT,
				SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_PRECOORDINATION, DOMAIN_TEMPLATE_FOR_PRECOORDINATION,
				SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_POSTCOORDINATION, DOMAIN_TEMPLATE_FOR_POSTCOORDINATION,
				SnomedRf2Headers.FIELD_MRCM_EDITORIAL_GUIDE_REFERENCE, EDITORIAL_GUIDE_REFERENCE
			);
		case MRCM_ATTRIBUTE_DOMAIN:
			return Json.object(
				SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID, DOMAIN_ID,
				SnomedRf2Headers.FIELD_MRCM_GROUPED, Boolean.TRUE,
				SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY, ATTRIBUTE_CARDINALITY,
				SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY, ATTRIBUTE_IN_GROUP_CARDINALITY,
				SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, RULE_STRENGTH_ID,
				SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, CONTENT_TYPE_ID
			);
		case MRCM_ATTRIBUTE_RANGE:
			return Json.object(
				SnomedRf2Headers.FIELD_MRCM_RANGE_CONSTRAINT, RANGE_CONSTRAINT,
				SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_RULE, ATTRIBUTE_RULE,
				SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, RULE_STRENGTH_ID,
				SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, CONTENT_TYPE_ID
			);
		case MRCM_MODULE_SCOPE:
			return Json.object(
				SnomedRf2Headers.FIELD_MRCM_RULE_REFSET_ID, RULE_REFSET_ID
			);
		default:
			throw new IllegalStateException("Unexpected reference set type '" + refSetType + "'.");
		}
	}

	private static SnomedCoreConfiguration getSnomedCoreConfiguration() {
		return ApplicationContext.getServiceForClass(SnowOwlConfiguration.class).getModuleConfig(SnomedCoreConfiguration.class);
	}

	private SnomedRestFixtures() {
		throw new UnsupportedOperationException("This class is not supposed to be instantiated.");
	}
}
