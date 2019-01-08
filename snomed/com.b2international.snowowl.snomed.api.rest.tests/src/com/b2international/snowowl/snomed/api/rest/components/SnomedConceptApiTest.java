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
package com.b2international.snowowl.snomed.api.rest.components;

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.ROOT_CONCEPT;
import static com.b2international.snowowl.snomed.api.rest.CodeSystemRestRequests.createCodeSystem;
import static com.b2international.snowowl.snomed.api.rest.CodeSystemVersionRestRequests.createVersion;
import static com.b2international.snowowl.snomed.api.rest.CodeSystemVersionRestRequests.getNextAvailableEffectiveDateAsString;
import static com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants.UK_ACCEPTABLE_MAP;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingRestRequests.createBranchRecursively;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingRestRequests.deleteBranch;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.createComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.deleteComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.updateComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.*;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.bulk.BulkRequest;
import com.b2international.snowowl.core.events.bulk.BulkRequestBuilder;
import com.b2international.snowowl.core.exceptions.ConflictException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.core.domain.*;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.datastore.id.domain.IdentifierStatus;
import com.b2international.snowowl.snomed.datastore.id.domain.SctId;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * @since 2.0
 */
public class SnomedConceptApiTest extends AbstractSnomedApiTest {

	@Test
	public void createConceptNonExistentBranch() {
		Map<?, ?> requestBody = createConceptRequestBody(Concepts.ROOT_CONCEPT)
				.put("commitComment", "Created new concept in non-existent branch")
				.build();

		createComponent(BranchPathUtils.createPath("MAIN/x/y/z"), SnomedComponentType.CONCEPT, requestBody).statusCode(404);
	}

	@Test
	public void createConceptEmptyParent() {
		Map<?, ?> requestBody = createConceptRequestBody("")
				.put("commitComment", "Created new concept with empty parentConceptId")
				.build();

		createComponent(branchPath, SnomedComponentType.CONCEPT, requestBody).statusCode(400)
		.body("message", equalTo("1 validation error"))
		.body("violations", hasItem("'destinationId' may not be empty (was '')"));
	}

	@Test
	public void createConceptInvalidParent() {
		Map<?, ?> requestBody = createConceptRequestBody("11110000")
				.put("commitComment", "Created new concept with invalid parentConceptId")
				.build();

		createComponent(branchPath, SnomedComponentType.CONCEPT, requestBody).statusCode(400);
	}

	@Test
	public void createConceptInvalidLanguageRefSet() {
		Map<?, ?> requestBody = createConceptRequestBody(Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE, SnomedApiTestConstants.INVALID_PREFERRED_MAP)
				.put("commitComment", "Created new concept with invalid acceptability maps")
				.build();

		createComponent(branchPath, SnomedComponentType.CONCEPT, requestBody).statusCode(400);
	}

	@Test
	public void createConceptInvalidModule() {
		Map<?, ?> requestBody = createConceptRequestBody(Concepts.ROOT_CONCEPT, "11110000", SnomedApiTestConstants.INVALID_PREFERRED_MAP)
				.put("commitComment", "Created new concept with invalid moduleId")
				.build();

		createComponent(branchPath, SnomedComponentType.CONCEPT, requestBody).statusCode(400);
	}

	@Test
	public void createConceptWithoutCommitComment() {
		Map<?, ?> requestBody = createConceptRequestBody(Concepts.ROOT_CONCEPT).build();
		createComponent(branchPath, SnomedComponentType.CONCEPT, requestBody).statusCode(400);
	}

	@Test
	public void createConcept() {
		Map<?, ?> requestBody = createConceptRequestBody(Concepts.ROOT_CONCEPT)
				.put("commitComment", "Created new concept")
				.build();

		final String locationHeader = createComponent(branchPath, SnomedComponentType.CONCEPT, requestBody).statusCode(201).extract().header("Location");
		final String conceptId = lastPathSegment(locationHeader);
		final SnomedConcept concept = getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, "statedAncestors(direct:true),ancestors(direct:true)").extract().as(SnomedConcept.class);
		assertEquals(1, concept.getStatedAncestors().getTotal());
		assertEquals(0, concept.getAncestors().getTotal());
	}

	@Test
	public void createConceptWithReservedId() {
		ISnomedIdentifierService identifierService = getServiceForClass(ISnomedIdentifierService.class);
		String conceptId = Iterables.getOnlyElement(identifierService.reserve(null, ComponentCategory.CONCEPT, 1));

		Map<?, ?> requestBody = createConceptRequestBody(Concepts.ROOT_CONCEPT)
				.put("id", conceptId)
				.put("commitComment", "Created new concept with reserved identifier")
				.build();

		createComponent(branchPath, SnomedComponentType.CONCEPT, requestBody).statusCode(201)
		.header("Location", endsWith("/" + conceptId));
		
		SctId conceptSctId = SnomedRequests.identifiers().prepareGet()
			.setComponentId(conceptId)
			.build(SnomedDatastoreActivator.REPOSITORY_UUID)
			.execute(ApplicationContext.getServiceForClass(IEventBus.class))
			.getSync()
			.first()
			.get();
		
		assertEquals(IdentifierStatus.ASSIGNED.getSerializedName(), conceptSctId.getStatus());
	}

	@Test
	public void createConceptOnDeletedBranch() {
		deleteBranch(branchPath);

		Map<?, ?> requestBody = createConceptRequestBody(Concepts.ROOT_CONCEPT)
				.put("commitComment", "Created new concept on deleted branch")
				.build();

		createComponent(branchPath, SnomedComponentType.CONCEPT, requestBody).statusCode(400);
	}

	@Test
	public void createShortIsACycle() throws Exception {
		String concept1Id = createNewConcept(branchPath);
		String concept2Id = createNewConcept(branchPath, concept1Id);

		// Try creating a cycle between the two concepts
		Map<?, ?> requestBody = createRelationshipRequestBody(concept1Id, Concepts.IS_A, concept2Id)
				.put("commitComment", "Created an IS A cycle with two relationships")
				.build();

		createComponent(branchPath, SnomedComponentType.RELATIONSHIP, requestBody).statusCode(400);
	}

	@Test
	public void createLongIsACycle() throws Exception {
		String concept1Id = createNewConcept(branchPath);
		String concept2Id = createNewConcept(branchPath, concept1Id);
		String concept3Id = createNewConcept(branchPath, concept2Id);

		// Try creating a cycle between the starting and the ending concept
		Map<?, ?> requestBody = createRelationshipRequestBody(concept1Id, Concepts.IS_A, concept3Id)
				.put("commitComment", "Created an IS A cycle with three relationships")
				.build();

		createComponent(branchPath, SnomedComponentType.RELATIONSHIP, requestBody).statusCode(400);
	}

	@Test
	public void testConceptInactivation() throws Exception {
		String conceptId = createNewConcept(branchPath);

		inactivateConcept(branchPath, conceptId);
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId).statusCode(200)
		.body("active", equalTo(false));
	}

	@Test
	public void testConceptReactivation() throws Exception {
		// Create two concepts, add an additional relationship pointing from one to the other
		String conceptId1 = createNewConcept(branchPath);
		String conceptId2 = createNewConcept(branchPath);
		String relationshipId = createNewRelationship(branchPath, conceptId1, Concepts.PART_OF, conceptId2);

		// Inactivate the concept with the relationship is pointing to
		Map<?, ?> inactivationBody = ImmutableMap.<String, Object>builder()
				.put("active", false)
				.put("inactivationIndicator", InactivationIndicator.DUPLICATE)
				.put("associationTargets", ImmutableMap.of(AssociationType.POSSIBLY_EQUIVALENT_TO, ImmutableList.of(conceptId1)))
				.put("commitComment", "Inactivated concept")
				.build();

		updateComponent(branchPath, SnomedComponentType.CONCEPT, conceptId2, inactivationBody).statusCode(204);
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId2, "inactivationProperties()").statusCode(200)
		.body("active", equalTo(false))
		.body("inactivationIndicator", equalTo(InactivationIndicator.DUPLICATE.toString()))
		.body("associationTargets." + AssociationType.POSSIBLY_EQUIVALENT_TO.name(), hasItem(conceptId1));

		// Verify that the inbound relationship is inactive
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId).statusCode(200)
		.body("active", equalTo(false));

		// Reactivate the concept
		reactivateConcept(branchPath, conceptId2);

		// Verify that the concept is active again, it has two active descriptions, no association targets, no indicator
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId2, "inactivationProperties()").statusCode(200)
		.body("active", equalTo(true))
		.body("inactivationIndicator", nullValue())
		.body("associationTargets", nullValue());

		// Verify that the inbound relationship is still inactive, meaning that manual reactivation is required
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId).statusCode(200)
		.body("active", equalTo(false));
	}

	@Test
	public void restoreEffectiveTimeOnReleasedConcept() throws Exception {
		String conceptId = createNewConcept(branchPath);

		String shortName = "SNOMEDCT-CON-1";
		createCodeSystem(branchPath, shortName).statusCode(201);
		String effectiveDate = getNextAvailableEffectiveDateAsString(shortName);
		createVersion(shortName, "v1", effectiveDate).statusCode(201);

		// After versioning, the concept should be released and have an effective time set on it
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId).statusCode(200)
		.body("released", equalTo(true))
		.body("effectiveTime", equalTo(effectiveDate));

		inactivateConcept(branchPath, conceptId);

		// An inactivation should unset the effective time field
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId).statusCode(200)
		.body("released", equalTo(true))
		.body("effectiveTime", nullValue());

		reactivateConcept(branchPath, conceptId);

		// Getting the concept back to its originally released state should restore the effective time
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId).statusCode(200)
		.body("released", equalTo(true))
		.body("effectiveTime", equalTo(effectiveDate));
	}

	@Test
	public void updateAssociationTarget() throws Exception {
		String conceptId1 = createNewConcept(branchPath);
		String conceptId2 = createNewConcept(branchPath);
		String conceptId3 = createNewConcept(branchPath);

		// Inactivate the duplicate concept and point to the other one
		Map<?, ?> inactivationRequestBody = ImmutableMap.<String, Object>builder()
				.put("active", false)
				.put("inactivationIndicator", InactivationIndicator.DUPLICATE)
				.put("associationTargets", ImmutableMap.of(AssociationType.POSSIBLY_EQUIVALENT_TO, ImmutableList.of(conceptId1)))
				.put("commitComment", "Inactivated concept")
				.build();

		updateComponent(branchPath, SnomedComponentType.CONCEPT, conceptId2, inactivationRequestBody).statusCode(204);
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId2, "inactivationProperties()").statusCode(200)
		.body("active", equalTo(false))
		.body("inactivationIndicator", equalTo(InactivationIndicator.DUPLICATE.toString()))
		.body("associationTargets." + AssociationType.POSSIBLY_EQUIVALENT_TO.name(), hasItem(conceptId1));

		// Update the inactivation reason and association target properties
		Map<?, ?> updateRequestBody = ImmutableMap.<String, Object>builder()
				.put("active", false)
				.put("inactivationIndicator", InactivationIndicator.AMBIGUOUS)
				.put("associationTargets", ImmutableMap.of(AssociationType.REPLACED_BY, ImmutableList.of(conceptId3)))
				.put("commitComment", "Changed inactivation reason and association target")
				.build();

		updateComponent(branchPath, SnomedComponentType.CONCEPT, conceptId2, updateRequestBody).statusCode(204);

		// Verify association target and inactivation indicator update
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId2, "inactivationProperties()").statusCode(200)
		.body("active", equalTo(false))
		.body("inactivationIndicator", equalTo(InactivationIndicator.AMBIGUOUS.toString()))
		.body("associationTargets." + AssociationType.POSSIBLY_EQUIVALENT_TO.name(), nullValue())
		.body("associationTargets." + AssociationType.REPLACED_BY.name(), allOf(hasItem(conceptId3), not(hasItem(conceptId1))));
	}

	@Test
	public void updateAssociationTargetWithReuse() throws Exception {
		String conceptId1 = createNewConcept(branchPath);
		String conceptId2 = createNewConcept(branchPath);
		String conceptId3 = createNewConcept(branchPath);
		String conceptId4 = createNewConcept(branchPath);

		// Inactivate the duplicate concept and point to the other one
		Map<?, ?> inactivationRequestBody = ImmutableMap.<String, Object>builder()
				.put("active", false)
				.put("inactivationIndicator", InactivationIndicator.DUPLICATE)
				.put("associationTargets", ImmutableMap.of(AssociationType.POSSIBLY_EQUIVALENT_TO, ImmutableList.of(conceptId1)))
				.put("commitComment", "Inactivated concept")
				.build();

		updateComponent(branchPath, SnomedComponentType.CONCEPT, conceptId2, inactivationRequestBody).statusCode(204);
		Collection<String> memberIds = getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId2, 
				"members()", "inactivationProperties()").statusCode(200)
				.body("active", equalTo(false))
				.body("inactivationIndicator", equalTo(InactivationIndicator.DUPLICATE.toString()))
				.body("associationTargets." + AssociationType.POSSIBLY_EQUIVALENT_TO.name(), hasItem(conceptId1))
				.extract().path("members.items.id");

		assertEquals(2, memberIds.size());

		// Update the inactivation reason and association target
		Map<?, ?> updateRequestBody = ImmutableMap.<String, Object>builder()
				.put("active", false)
				.put("inactivationIndicator", InactivationIndicator.AMBIGUOUS)
				.put("associationTargets", ImmutableMap.of(
						AssociationType.POSSIBLY_EQUIVALENT_TO, ImmutableList.of(conceptId3, conceptId1),
						AssociationType.REPLACED_BY, ImmutableList.of(conceptId4)))
				.put("commitComment", "Changed inactivation reason and association targets")
				.build();

		updateComponent(branchPath, SnomedComponentType.CONCEPT, conceptId2, updateRequestBody).statusCode(204);
		Collection<String> updatedMemberIds = getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId2, 
				"members()", "inactivationProperties()").statusCode(200)
				.body("active", equalTo(false))
				.body("inactivationIndicator", equalTo(InactivationIndicator.AMBIGUOUS.toString()))
				.body("associationTargets." + AssociationType.POSSIBLY_EQUIVALENT_TO.name(), allOf(hasItem(conceptId3), hasItem(conceptId1)))
				.body("associationTargets." + AssociationType.REPLACED_BY.name(), hasItem(conceptId4))
				.extract().path("members.items.id");

		// Verify that the member UUIDs have not been cycled
		assertEquals(4, updatedMemberIds.size());
		assertTrue(updatedMemberIds.containsAll(memberIds));
	}

	@Test
	public void updateInactivationIndicatorOnActiveConcept() throws Exception {
		String conceptId = createNewConcept(branchPath);
		
		// ensure that the concept does not have any indicator set before the update
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, "inactivationProperties()")
			.statusCode(200)
			.body("inactivationIndicator", nullValue());
		
		// Inactivate the duplicate concept and point to the other one
		Map<?, ?> updateReq = ImmutableMap.<String, Object>builder()
				.put("inactivationIndicator", InactivationIndicator.PENDING_MOVE)
				.put("commitComment", "Add a pending move indicator to an active concept")
				.build();
		updateComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, updateReq);
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, "inactivationProperties()")
			.statusCode(200)
			.body("inactivationIndicator", equalTo(InactivationIndicator.PENDING_MOVE.toString()));
	}
	
	@Test
	public void createDuplicateConcept() throws Exception {
		String conceptId = createNewConcept(branchPath);
		Map<?, ?> requestBody = createConceptRequestBody(Concepts.ROOT_CONCEPT)
				.put("id", conceptId)
				.put("commitComment", "Created new concept with duplicate identifier")
				.build();

		createComponent(branchPath, SnomedComponentType.CONCEPT, requestBody).statusCode(409);
	}

	@Test
	public void deleteConcept() {
		String conceptId = createNewConcept(branchPath);
		deleteComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, false).statusCode(204);
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId).statusCode(404);
	}

	@Test
	public void deleteConceptOnNestedBranch() {
		String parentId = ROOT_CONCEPT;

		for (int i = 0; i < 10; i++) {
			parentId = createNewConcept(branchPath, parentId);
		}

		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		IBranchPath b = BranchPathUtils.createPath(a, "b");
		createBranchRecursively(b);

		// New component on nested branch resets the container's version to 1 again
		createNewConcept(b, parentId);

		// Deleting the last concept in the chain
		deleteComponent(branchPath, SnomedComponentType.CONCEPT, parentId, false).statusCode(204);
		getComponent(branchPath, SnomedComponentType.CONCEPT, parentId).statusCode(404);

		// Should still exist on the nested branch, and be possible to remove
		deleteComponent(b, SnomedComponentType.CONCEPT, parentId, false).statusCode(204);
		getComponent(b, SnomedComponentType.CONCEPT, parentId).statusCode(404);
	}

	@Test
	public void deleteReleasedConcept() {
		String conceptId = createNewConcept(branchPath);

		String shortName = "SNOMEDCT-CON-2";
		createCodeSystem(branchPath, shortName).statusCode(201);
		String effectiveDate = getNextAvailableEffectiveDateAsString(shortName);
		createVersion(shortName, "v1", effectiveDate).statusCode(201);

		deleteComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, false).statusCode(409);
	}

	@Test
	public void forceDeleteConcept() {
		String conceptId = createNewConcept(branchPath);

		String shortName = "SNOMEDCT-CON-3";
		createCodeSystem(branchPath, shortName).statusCode(201);
		String effectiveDate = getNextAvailableEffectiveDateAsString(shortName);
		createVersion(shortName, "v1", effectiveDate).statusCode(201);

		deleteComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, true).statusCode(204);
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId).statusCode(404);
	}

	@Test
	public void createConceptWithMember() throws Exception {
		String refSetId = createNewRefSet(branchPath);

		Map<?, ?> memberRequestBody = ImmutableMap.builder()
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("referenceSetId", refSetId)
				.build();

		Map<?, ?> conceptRequestBody = createConceptRequestBody(Concepts.ROOT_CONCEPT)
				.put("members", ImmutableList.of(memberRequestBody))
				.put("commitComment", "Created concept with reference set member")
				.build();

		String conceptId = lastPathSegment(createComponent(branchPath, SnomedComponentType.CONCEPT, conceptRequestBody)
				.statusCode(201)
				.extract().header("Location"));

		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, "members()").statusCode(200)
		.body("members.items[0].referenceSetId", equalTo(refSetId));
	}

	@Test
	public void addDescriptionViaConceptUpdate() throws Exception {
		String conceptId = createNewConcept(branchPath);
		SnomedConcept concept = getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, "descriptions()")
				.statusCode(200)
				.extract().as(SnomedConcept.class);

		assertEquals(2, concept.getDescriptions().getTotal());

		// Add a text definition	
		SnomedDescription newTextDefinition = new SnomedDescription();
		newTextDefinition.setId(reserveComponentId(null, ComponentCategory.DESCRIPTION));
		newTextDefinition.setActive(true);
		newTextDefinition.setAcceptabilityMap(UK_ACCEPTABLE_MAP);
		newTextDefinition.setTypeId(Concepts.TEXT_DEFINITION);
		newTextDefinition.setTerm("Text Definiton " + new Date());
		newTextDefinition.setLanguageCode("en");
		newTextDefinition.setCaseSignificance(CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE);
		newTextDefinition.setModuleId(Concepts.MODULE_SCT_CORE);

		List<SnomedDescription> changedDescriptions = ImmutableList.<SnomedDescription>builder()
				.addAll(concept.getDescriptions())
				.add(newTextDefinition)
				.build();

		Map<?, ?> updateRequestBody = ImmutableMap.builder()
				.put("descriptions", SnomedDescriptions.of(changedDescriptions))
				.put("commitComment", "Add new description via concept update")
				.build();

		updateComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, updateRequestBody).statusCode(204);
		SnomedConcept updatedConcept = getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, "descriptions()")
				.statusCode(200)
				.extract().as(SnomedConcept.class);

		assertEquals(3, updatedConcept.getDescriptions().getTotal());
	}

	@Test
	public void addRelationshipViaConceptUpdate() throws Exception {
		final String conceptId = createNewConcept(branchPath);
		final SnomedConcept concept = getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, "relationships()")
				.statusCode(200)
				.extract().as(SnomedConcept.class);

		assertEquals(1, concept.getRelationships().getTotal());

		// Add a relationship
		SnomedRelationship newRelationship = new SnomedRelationship();
		newRelationship.setId(reserveComponentId(null, ComponentCategory.RELATIONSHIP));
		newRelationship.setActive(true);
		newRelationship.setCharacteristicType(CharacteristicType.STATED_RELATIONSHIP);
		newRelationship.setTypeId(Concepts.PART_OF);
		newRelationship.setDestinationId(Concepts.NAMESPACE_ROOT);
		newRelationship.setModuleId(Concepts.MODULE_SCT_CORE);
		newRelationship.setGroup(0);
		newRelationship.setUnionGroup(0);
		newRelationship.setModifier(RelationshipModifier.EXISTENTIAL);

		final List<SnomedRelationship> changedRelationships = ImmutableList.<SnomedRelationship>builder()
				.addAll(concept.getRelationships())
				.add(newRelationship)
				.build();

		final Map<?, ?> updateRequestBody = ImmutableMap.builder()
				.put("relationships", SnomedRelationships.of(changedRelationships))
				.put("commitComment", "Add new relationship via concept update")
				.build();

		updateComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, updateRequestBody).statusCode(204);
		final SnomedConcept updatedConcept = getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, "relationships()")
				.statusCode(200)
				.extract().as(SnomedConcept.class);

		assertEquals(2, updatedConcept.getRelationships().getTotal());
	}

	@Test
	public void addMemberViaConceptUpdate() throws Exception {
		final String refSetId = createNewRefSet(branchPath);
		final String conceptId = createNewConcept(branchPath);
		SnomedConcept concept = getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, "members()")
				.statusCode(200)
				.extract().as(SnomedConcept.class);

		assertEquals(0, concept.getMembers().getTotal());

		// Add a reference set member
		SnomedReferenceSetMember newMember = new SnomedReferenceSetMember();
		newMember.setId(UUID.randomUUID().toString());
		newMember.setActive(true);
		newMember.setReferenceSetId(refSetId);
		newMember.setModuleId(Concepts.MODULE_SCT_CORE);

		final List<SnomedReferenceSetMember> changedMembers = ImmutableList.<SnomedReferenceSetMember>builder()
				.addAll(concept.getMembers())
				.add(newMember)
				.build();

		final Map<?, ?> updateRequestBody = ImmutableMap.builder()
				.put("members", SnomedReferenceSetMembers.of(changedMembers))
				.put("commitComment", "Add new reference set member via concept update")
				.build();

		updateComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, updateRequestBody).statusCode(204);
		final SnomedConcept updatedConcept = getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, "members()")
				.statusCode(200)
				.extract().as(SnomedConcept.class);

		assertEquals(1, updatedConcept.getMembers().getTotal());
	}
	
	@Test(expected = ConflictException.class)
	public void doNotDeleteReleasedConceptsInTheSameTransaction() {
		
		final String conceptId = createNewConcept(branchPath);
		
		getComponent(branchPath, SnomedComponentType.CONCEPT, Concepts.PART_OF)
			.statusCode(200)
			.body("released", equalTo(true));
		
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId)
			.statusCode(200)
			.body("released", equalTo(false));
		
		final BulkRequestBuilder<TransactionContext> bulk = BulkRequest.create();
		
		bulk.add(SnomedRequests.prepareDeleteConcept(conceptId));
		bulk.add(SnomedRequests.prepareDeleteConcept(Concepts.PART_OF));

		SnomedRequests.prepareCommit()
			.setBody(bulk)
			.setCommitComment("Delete multiple concepts")
			.setUserId("test")
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
			.execute(ApplicationContext.getServiceForClass(IEventBus.class))
			.getSync();
		
	}
	
	@Test
	public void testConceptSearchRequestWithInboundRelationshipExpand() {
		final String conceptId = createNewConcept(branchPath);
		
		final String inboundRelationshipId = createNewRelationship(branchPath, Concepts.NAMESPACE_ROOT, Concepts.IS_A, conceptId);
		
		final SnomedConcept conceptWithInboundRelationship = SnomedRequests.prepareSearchConcept()
			.all()
			.filterById(conceptId)
			.setExpand("inboundRelationships()")
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
			.execute(getBus())
			.getSync()
			.stream().findFirst().get();
		 
		 assertNotNull(conceptWithInboundRelationship.getInboundRelationships());
		 assertEquals(1, conceptWithInboundRelationship.getInboundRelationships().getItems().size());
		 final SnomedRelationship expandedInboundRelationship = Iterables.getOnlyElement(conceptWithInboundRelationship.getInboundRelationships());
		 assertEquals(inboundRelationshipId, expandedInboundRelationship.getId());
	}
	
	@Test
	public void testConceptSearchRequestWithInboundRelationshipExpandWithLimit() {
		final String conceptId = createNewConcept(branchPath);
		
		createNewRelationship(branchPath, Concepts.NAMESPACE_ROOT, Concepts.IS_A, conceptId);
		createNewRelationship(branchPath, Concepts.NAMESPACE_ROOT, Concepts.IS_A, conceptId);
		
		final SnomedConcept conceptWithInboundRelationships = SnomedRequests.prepareSearchConcept()
			.all()
			.filterById(conceptId)
			.setExpand("inboundRelationships(limit:1)")
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
			.execute(getBus())
			.getSync()
			.stream().findFirst().get();
		 
		 assertNotNull(conceptWithInboundRelationships.getInboundRelationships());
		 assertEquals(1, conceptWithInboundRelationships.getInboundRelationships().getItems().size());
	}
	
	@Test
	public void testConceptSearchRequestWithInboundRelationshipExpandWithTypeFilter() {
		final String conceptId = createNewConcept(branchPath);
		
		createNewRelationship(branchPath, Concepts.NAMESPACE_ROOT, Concepts.HAS_DOSE_FORM, conceptId);
		createNewRelationship(branchPath, Concepts.NAMESPACE_ROOT, Concepts.IS_A, conceptId);
		
		final String inboundRelationshipExpandWithTypeFilter = String.format("inboundRelationships(typeId:\"%s\")", Concepts.HAS_DOSE_FORM);
		
		final SnomedConcept conceptWithInboundRelationshipsTypeIdFiltered = SnomedRequests.prepareSearchConcept()
			.all()
			.filterById(conceptId)
			.setExpand(inboundRelationshipExpandWithTypeFilter)
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
			.execute(getBus())
			.getSync()
			.stream().findFirst().get();
		 
		assertNotNull(conceptWithInboundRelationshipsTypeIdFiltered.getInboundRelationships());
		assertEquals(1, conceptWithInboundRelationshipsTypeIdFiltered.getInboundRelationships().getItems().size());
		final SnomedRelationship inboundRelationship = Iterables.getOnlyElement(conceptWithInboundRelationshipsTypeIdFiltered.getInboundRelationships());
		assertEquals(Concepts.HAS_DOSE_FORM, inboundRelationship.getTypeId());
	}
	
	@Test
	public void testConceptSearchRequestWithInboundRelationshipExpandWithSourceFilter() {
		final String conceptId = createNewConcept(branchPath);
		
		createNewRelationship(branchPath, Concepts.MODULE_SCT_MODEL_COMPONENT, Concepts.HAS_DOSE_FORM, conceptId);
		createNewRelationship(branchPath, Concepts.NAMESPACE_ROOT, Concepts.IS_A, conceptId);
		
		final String inboundRelationshipExpandWithSourceIdFilter = String.format("inboundRelationships(sourceId:\"%s\")", Concepts.MODULE_SCT_MODEL_COMPONENT);
		
		final SnomedConcept conceptWithInboundRelationshipsTypeIdFiltered = SnomedRequests.prepareSearchConcept()
				.all()
				.filterById(conceptId)
				.setExpand(inboundRelationshipExpandWithSourceIdFilter)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
				.execute(getBus())
				.getSync()
				.stream().findFirst().get();
		
		assertNotNull(conceptWithInboundRelationshipsTypeIdFiltered.getInboundRelationships());
		assertEquals(1, conceptWithInboundRelationshipsTypeIdFiltered.getInboundRelationships().getItems().size());
		final SnomedRelationship inboundRelationship = Iterables.getOnlyElement(conceptWithInboundRelationshipsTypeIdFiltered.getInboundRelationships());
		assertEquals(Concepts.MODULE_SCT_MODEL_COMPONENT, inboundRelationship.getSourceId());
	}
	
	@Test
	public void testConceptGetRequestWithInboundRelationshipExpand() {
		final String conceptId = createNewConcept(branchPath);
		
		final String inboundRelationshipId = createNewRelationship(branchPath, Concepts.NAMESPACE_ROOT, Concepts.IS_A, conceptId);
		
		final SnomedConcept conceptWithInboundRelationship = SnomedRequests.prepareGetConcept(conceptId)
			.setExpand("inboundRelationships()")
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
			.execute(getBus())
			.getSync();
		 
		 assertNotNull(conceptWithInboundRelationship.getInboundRelationships());
		 assertEquals(1, conceptWithInboundRelationship.getInboundRelationships().getItems().size());
		 final SnomedRelationship expandedInboundRelationship = Iterables.getOnlyElement(conceptWithInboundRelationship.getInboundRelationships());
		 assertEquals(inboundRelationshipId, expandedInboundRelationship.getId());
	}
	
}
