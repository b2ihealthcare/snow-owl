/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.rest.components;

import static com.b2international.snowowl.snomed.common.SnomedConstants.Concepts.ROOT_CONCEPT;
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.createComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.updateComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.*;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.assertCreated;
import static com.google.common.collect.Maps.newHashMap;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.b2international.commons.json.Json;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.AssociationTarget;
import com.b2international.snowowl.snomed.core.domain.InactivationProperties;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.core.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.core.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests;
import com.b2international.snowowl.snomed.core.rest.SnomedComponentType;
import com.google.common.collect.Maps;

/**
 * @since 7.7
 */
public class SnomedConceptInactivationApiTest extends AbstractSnomedApiTest {

	private void assertConceptInactive(String conceptId) {
		assertGetConcept(conceptId, "descriptions(expand(inactivationProperties()))")
			.log().ifValidationFails()
			.statusCode(200)
			.body("active", equalTo(false))
			.body("definitionStatusId", equalTo(Concepts.PRIMITIVE))
			.body("parentIds", equalTo(List.of(IComponent.ROOT_ID)))
			.body("ancestorIds", equalTo(List.of()))
			.body("statedParentIds", equalTo(List.of(IComponent.ROOT_ID)))
			.body("statedAncestorIds", equalTo(List.of()))
			.body("descriptions.items.active", everyItem(equalTo(true)))
			.body("descriptions.items.inactivationProperties.inactivationIndicator.id", everyItem(equalTo(Concepts.CONCEPT_NON_CURRENT)));
	}
	
	@Test
	public void inactivatePrimitiveConcept() throws Exception {
		String conceptId = createNewConcept(branchPath);

		inactivateConcept(branchPath, conceptId);
		assertConceptInactive(conceptId);
	}

	@Test
	public void inactivateFullyDefinedConcept() throws Exception {
		Map<String, ?> conceptRequestBody = createConceptRequestBody(Concepts.ROOT_CONCEPT)
				.with("definitionStatusId", Concepts.FULLY_DEFINED)
				.with("commitComment", "Created new concept");
		String conceptId = createNewConcept(branchPath, conceptRequestBody);

		inactivateConcept(branchPath, conceptId);
		assertConceptInactive(conceptId);
	}
	
	@Test
	public void toggleDefinitionStatusAtStatusChange() {
		String conceptId = createNewConcept(branchPath);
		Json requestBody = createRefSetMemberRequestBody(Concepts.REFSET_OWL_AXIOM, conceptId)
				.with(SnomedRf2Headers.FIELD_OWL_EXPRESSION, "EquivalentClasses(:" + conceptId + " :" + Concepts.NAMESPACE_ROOT + ")")
				.with("commitComment", "Created new equivalent class OWL Axiom reference set member");
		
		assertCreated(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody));
		SnomedReferenceSetMembers members = SnomedComponentRestRequests.searchComponent(branchPath, 
				SnomedComponentType.MEMBER, 
				Map.of("referencedComponentId", conceptId))
			.extract().as(SnomedReferenceSetMembers.class);
		
		updateComponent(
				branchPath, 
				SnomedComponentType.CONCEPT, 
				conceptId, 
				Json.object(
					"members", members,
					"commitComment", "Update concept members"
				)
			).statusCode(204);

		SnomedConcept concept = assertGetConcept(conceptId)
			.statusCode(200)
			.extract().as(SnomedConcept.class);
		assertTrue(concept.isActive());
		assertTrue(!concept.isPrimitive()); //Active concept is defined after adding equivalent class axioms to it
		
		inactivateConcept(branchPath, conceptId);
		concept = assertGetConcept(conceptId)
				.statusCode(200)
				.extract().as(SnomedConcept.class);
		assertTrue(!concept.isActive());
		assertTrue(concept.isPrimitive()); //Inactive concept becomes primitive
		
		updateComponent(
				branchPath, 
				SnomedComponentType.CONCEPT, 
				conceptId, 
				Json.object(
					"active", true,
					"commitComment", "Reactivate concept"
				)
			).statusCode(204);
		
		concept = assertGetConcept(conceptId)
				.statusCode(200)
				.extract().as(SnomedConcept.class);
		assertTrue(concept.isActive());
		assertTrue(concept.isPrimitive()); //Reactivated concept with no active members stays primitive
	}

	@Test
	public void reactivateConceptWithActiveParentAndInboundRelationship() throws Exception {
		// Create two concepts, one that will be inactivated
		String conceptWithReferenceToInactivatedConcept = createNewConcept(branchPath);
		String conceptToInactivate = createNewConcept(branchPath);
		// and an inbound relationship to the inactivated concept
		String inboundStatedRelationshipId = createNewRelationship(branchPath, conceptWithReferenceToInactivatedConcept, Concepts.PART_OF, conceptToInactivate, Concepts.STATED_RELATIONSHIP);
		// and an outbound inferred relationships, which will be reactivated along with the concept
		String outboundInferredRelationshipId = createNewRelationship(branchPath, conceptToInactivate, Concepts.IS_A, Concepts.ROOT_CONCEPT, Concepts.INFERRED_RELATIONSHIP);

		// Inactivate the concept with the relationship is pointing to
		final InactivationProperties inactivationProperties = new InactivationProperties(
			Concepts.DUPLICATE,
			List.of(
				new AssociationTarget(Concepts.REFSET_POSSIBLY_EQUIVALENT_TO_ASSOCIATION, conceptWithReferenceToInactivatedConcept)
			)
		);
		Map<String, Object> inactivationBody = Json.object(
			"active", false,
			"inactivationProperties", inactivationProperties,
			"commitComment", "Inactivated concept"
		);

		updateConcept(conceptToInactivate, inactivationBody);
		
		assertGetConcept(conceptToInactivate, "inactivationProperties()")
			.statusCode(200)
			.body("active", equalTo(false))
			.body("inactivationProperties.inactivationIndicatorId", equalTo(Concepts.DUPLICATE))
			.body("inactivationProperties.associationTargets.referenceSetId", hasItem(Concepts.REFSET_POSSIBLY_EQUIVALENT_TO_ASSOCIATION))
			.body("inactivationProperties.associationTargets.targetComponentId", hasItem(conceptWithReferenceToInactivatedConcept));

		// Verify that the inbound relationship is inactive
		assertGetRelationship(inboundStatedRelationshipId)
			.statusCode(200)
			.body("active", equalTo(false));

		// Reactivate the concept
		reactivateConcept(branchPath, conceptToInactivate);

		// verify that the inferred outbound relationship is active again
		assertGetRelationship(outboundInferredRelationshipId)
			.statusCode(200)
			.body("active", equalTo(true));
		
		// Verify that the concept is active again, it has two active descriptions, no association targets, no indicator
		assertGetConcept(conceptToInactivate, "inactivationProperties()")
			.statusCode(200)
			.body("active", equalTo(true))
			.body("parentIds", equalTo(List.of(Concepts.ROOT_CONCEPT))) // verify the the inferred and stated hierarchy is back and valid
			.body("statedParentIds", equalTo(List.of(Concepts.ROOT_CONCEPT)))
			.body("ancestorIds", equalTo(List.of(IComponent.ROOT_ID)))
			.body("statedAncestorIds", equalTo(List.of(IComponent.ROOT_ID)))
			.body("inactivationProperties.inactivationIndicator", nullValue())
			.body("inactivationProperties.associationTargets", equalTo(List.of()));
		
		// Verify that the inbound relationship is still inactive, meaning that manual reactivation is required
		assertGetRelationship(inboundStatedRelationshipId)
			.statusCode(200)
			.body("active", equalTo(false));
	}
	
	@Test
	public void reactivateConceptWithInactiveParent() throws Exception {
		// Create two concepts, one that will be inactivated
		String inactiveParentConcept = createNewConcept(branchPath, createConceptRequestBody(ROOT_CONCEPT, Concepts.MODULE_SCT_CORE, SnomedApiTestConstants.UK_PREFERRED_MAP, false)
				.with("commitComment", "Created new concept"));
		String inactiveChildConcept = createNewConcept(branchPath, createConceptRequestBody(inactiveParentConcept, Concepts.MODULE_SCT_CORE, SnomedApiTestConstants.UK_PREFERRED_MAP, false)
				.with("commitComment", "Created new concept"));

		// Reactivate the child concept
		reactivateConcept(branchPath, inactiveChildConcept);

		// Verify that the concept is active again, no association targets, no indicator
		assertGetConcept(inactiveChildConcept, "inactivationProperties()")
			.statusCode(200)
			.body("active", equalTo(true))
			.body("parentIds", equalTo(List.of(IComponent.ROOT_ID)))
			.body("statedParentIds", equalTo(List.of(inactiveParentConcept)))
			.body("ancestorIds", equalTo(List.of()))
			.body("statedAncestorIds", equalTo(List.of(IComponent.ROOT_ID)))
			.body("inactivationProperties.inactivationIndicator", nullValue())
			.body("inactivationProperties.associationTargets", equalTo(List.of()));
		
		// after reactivating the parent the child should have the proper parentage set
		reactivateConcept(branchPath, inactiveParentConcept);
		
		assertGetConcept(inactiveParentConcept, "inactivationProperties()")
			.statusCode(200)
			.body("active", equalTo(true))
			.body("parentIds", equalTo(List.of(IComponent.ROOT_ID)))
			.body("statedParentIds", equalTo(List.of(Concepts.ROOT_CONCEPT)))
			.body("ancestorIds", equalTo(List.of()))
			.body("statedAncestorIds", equalTo(List.of(IComponent.ROOT_ID)))
			.body("inactivationProperties.inactivationIndicator", nullValue())
			.body("inactivationProperties.associationTargets", equalTo(List.of()));
		
		assertGetConcept(inactiveChildConcept, "inactivationProperties()")
			.statusCode(200)
			.body("active", equalTo(true))
			.body("parentIds", equalTo(List.of(IComponent.ROOT_ID)))
			.body("statedParentIds", equalTo(List.of(inactiveParentConcept)))
			.body("ancestorIds", equalTo(List.of()))
			.body("statedAncestorIds", equalTo(List.of(IComponent.ROOT_ID, Concepts.ROOT_CONCEPT)))
			.body("inactivationProperties.inactivationIndicator", nullValue())
			.body("inactivationProperties.associationTargets", equalTo(List.of()));
	}

	@Test
	public void reactivateConceptWithInactiveParentRelationshipsFirst() throws Exception {
		// Create two concepts, one that will be inactivated
		String inactiveParentConcept = createNewConcept(branchPath, createConceptRequestBody(ROOT_CONCEPT, Concepts.MODULE_SCT_CORE, SnomedApiTestConstants.UK_PREFERRED_MAP, false)
				.with("commitComment", "Created new concept"));
		String inactiveChildConcept = createNewConcept(branchPath, createConceptRequestBody(inactiveParentConcept, Concepts.MODULE_SCT_CORE, SnomedApiTestConstants.UK_PREFERRED_MAP, false)
				.with("commitComment", "Created new concept"));

		final Map<String, Object> concept = assertGetConcept(inactiveChildConcept, "relationships()")
				.statusCode(200)
				.extract().as(Map.class);
		// Reactivate relationships first
		final List<Map<String, Object>> relationshipItems = (List<Map<String, Object>>) ((Map<String, Object>) concept.get("relationships")).get("items");
		relationshipItems.forEach(relationship -> {
			final Map<String, Object> updatedRelationship = newHashMap(relationship);
			updatedRelationship.put("active", true);
			updatedRelationship.put("commitComment", "Reactivate Relationship");
			updateRelationship((String) relationship.get("id"), updatedRelationship);
		});
		
		// Reactivate the child concept
		final Map<String, Object> reactivationRequest = Maps.newHashMap(concept);
		reactivationRequest.put("active", true);
		reactivationRequest.remove("relationships"); //remove relationships from concept update call
		reactivationRequest.put("commitComment", "Reactivated concept");

		updateConcept(inactiveChildConcept, reactivationRequest);

		// Verify that the concept is active again, no association targets, no indicator
		assertGetConcept(inactiveChildConcept, "inactivationProperties()")
			.statusCode(200)
			.body("active", equalTo(true))
			.body("parentIds", equalTo(List.of(IComponent.ROOT_ID)))
			.body("statedParentIds", equalTo(List.of(inactiveParentConcept)))
			.body("ancestorIds", equalTo(List.of()))
			.body("statedAncestorIds", equalTo(List.of(IComponent.ROOT_ID)))
			.body("inactivationProperties.inactivationIndicator", nullValue())
			.body("inactivationProperties.associationTargets", equalTo(List.of()));
		
		// after reactivating the parent the child should have the proper parentage set
		reactivateConcept(branchPath, inactiveParentConcept);
		
		assertGetConcept(inactiveParentConcept, "inactivationProperties()")
			.statusCode(200)
			.body("active", equalTo(true))
			.body("parentIds", equalTo(List.of(IComponent.ROOT_ID)))
			.body("statedParentIds", equalTo(List.of(Concepts.ROOT_CONCEPT)))
			.body("ancestorIds", equalTo(List.of()))
			.body("statedAncestorIds", equalTo(List.of(IComponent.ROOT_ID)))
			.body("inactivationProperties.inactivationIndicator", nullValue())
			.body("inactivationProperties.associationTargets", equalTo(List.of()));
		
		assertGetConcept(inactiveChildConcept, "inactivationProperties()")
			.statusCode(200)
			.body("active", equalTo(true))
			.body("parentIds", equalTo(List.of(IComponent.ROOT_ID)))
			.body("statedParentIds", equalTo(List.of(inactiveParentConcept)))
			.body("ancestorIds", equalTo(List.of()))
			.body("statedAncestorIds", equalTo(List.of(IComponent.ROOT_ID, Concepts.ROOT_CONCEPT)))
			.body("inactivationProperties.inactivationIndicator", nullValue())
			.body("inactivationProperties.associationTargets", equalTo(List.of()));
	}
	
	@Test
	public void reactivateConceptShouldRemoveConceptNonCurrentIndicatorsFromDescriptions() throws Exception {
		final String inactiveConceptId = createNewConcept(branchPath, createConceptRequestBody(ROOT_CONCEPT, Concepts.MODULE_SCT_CORE, SnomedApiTestConstants.UK_PREFERRED_MAP, false)
				.with("commitComment", "Created inactive concept"));
		
		// create non-current indicators for the pre-inactivated concept's descriptions 
		getConcept(inactiveConceptId, "descriptions()").getDescriptions().forEach(description -> {
			Map<?, ?> requestBody = createRefSetMemberRequestBody(Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR, description.getId())
					.with(SnomedRf2Headers.FIELD_VALUE_ID, Concepts.CONCEPT_NON_CURRENT)
					.with("commitComment", "Created new indicator member for description: " + description.getId());

			createComponent(branchPath, SnomedComponentType.MEMBER, requestBody).statusCode(201);
		});
		
		reactivateConcept(branchPath, inactiveConceptId);
		
		assertGetConcept(inactiveConceptId, "inactivationProperties(),descriptions(expand(inactivationProperties()))")
			.statusCode(200)
			.body("active", equalTo(true))
			.body("parentIds", equalTo(List.of(IComponent.ROOT_ID)))
			.body("statedParentIds", equalTo(List.of(Concepts.ROOT_CONCEPT)))
			.body("ancestorIds", equalTo(List.of()))
			.body("statedAncestorIds", equalTo(List.of(IComponent.ROOT_ID)))
			.body("inactivationProperties.inactivationIndicator", nullValue())
			.body("inactivationProperties.associationTargets", equalTo(List.of()))
			.body("descriptions.items.active", everyItem(equalTo(true)))
			.body("descriptions.items.inactivationProperties.inactivationIndicator[0].id", nullValue())
			.body("descriptions.items.inactivationProperties.inactivationIndicator[1].id", nullValue());
	}
	
}
