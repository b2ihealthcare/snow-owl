/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createConceptRequestBody;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewConcept;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewRefSet;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewRelationship;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createRefSetMemberRequestBody;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createRelationshipRequestBody;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.inactivateConcept;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.reactivateConcept;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.reserveComponentId;
import static com.b2international.snowowl.snomed.common.SnomedConstants.Concepts.ROOT_CONCEPT;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.Test;

import com.b2international.commons.exceptions.ConflictException;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.bulk.BulkRequest;
import com.b2international.snowowl.core.events.bulk.BulkRequestBuilder;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.AssociationType;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.DescriptionInactivationIndicator;
import com.b2international.snowowl.snomed.core.domain.InactivationIndicator;
import com.b2international.snowowl.snomed.core.domain.RelationshipModifier;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
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
			.body("active", equalTo(false))
			.body("parentIds", equalTo(ImmutableList.of(IComponent.ROOT_ID)))
			.body("ancestorIds", equalTo(ImmutableList.of()))
			.body("statedParentIds", equalTo(ImmutableList.of(IComponent.ROOT_ID)))
			.body("statedAncestorIds", equalTo(ImmutableList.of()));
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
		.body("active", equalTo(true))
		.body("released", equalTo(true))
		.body("effectiveTime", equalTo(effectiveDate));

		inactivateConcept(branchPath, conceptId);

		// An inactivation should unset the effective time field
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId).statusCode(200)
		.body("active", equalTo(false))
		.body("released", equalTo(true))
 		.body("effectiveTime", nullValue());

		reactivateConcept(branchPath, conceptId);

		// Getting the concept back to its originally released state should restore the effective time
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId).statusCode(200)
		.body("active", equalTo(true))
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
		updateComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, updateReq)
			.statusCode(204);
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, "inactivationProperties()")
			.statusCode(200)
			.body("inactivationIndicator", equalTo(InactivationIndicator.PENDING_MOVE.toString()));
	}
	
	@Test
	public void updateInactivationIndicatorOnActiveConceptWithAssociationTargets() throws Exception {
		String conceptId = createNewConcept(branchPath);
		Map<?, ?> requestBody = createRefSetMemberRequestBody(Concepts.REFSET_POSSIBLY_EQUIVALENT_TO_ASSOCIATION, conceptId)
				.put(SnomedRf2Headers.FIELD_TARGET_COMPONENT, Concepts.ROOT_CONCEPT)
				.put("commitComment", "Created new reference set member")
				.build();

		final String associationMemberId = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody)
				.statusCode(201)
				.extract().header("Location"));
		
		// ensure that the concept does not have any indicator set before the update
		SnomedReferenceSetMembers currentMembers = getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, "inactivationProperties(),members()")
			.statusCode(200)
			.body("inactivationIndicator", nullValue())
			.body("associationTargets", equalTo(ImmutableMap.of(AssociationType.POSSIBLY_EQUIVALENT_TO.name(), ImmutableList.of(Concepts.ROOT_CONCEPT))))
			.extract()
			.jsonPath().getObject("members", SnomedReferenceSetMembers.class);
		
		Map<?, ?> updateReq = ImmutableMap.<String, Object>builder()
				.put("inactivationIndicator", InactivationIndicator.PENDING_MOVE)
				// XXX also pass the current members to the update, without the fix this would cause duplicate association members
				.put("members", currentMembers)
				.put("commitComment", "Add a pending move indicator to an active concept")
				.build();
		updateComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, updateReq)
			.statusCode(204);
		List<String> memberIds = getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, "inactivationProperties(),members()")
			.statusCode(200)
			.body("inactivationIndicator", equalTo(InactivationIndicator.PENDING_MOVE.toString()))
			.body("associationTargets", equalTo(ImmutableMap.of(AssociationType.POSSIBLY_EQUIVALENT_TO.name(), ImmutableList.of(Concepts.ROOT_CONCEPT))))
			.extract().path("members.items.id");
		
		assertThat(memberIds.remove(associationMemberId)).isTrue();
		assertThat(memberIds.remove(associationMemberId)).isFalse();
		// the remaining member is the inactivation indicator
		assertThat(memberIds).hasSize(1);
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
	
	@Test
	public void createConceptWithOwlAxiomMemberWithSubClassOfExpression() throws Exception {
		final String owlSubclassOfExpression = String.format("SubClassOf(:%s :%s)", Concepts.FULLY_SPECIFIED_NAME, Concepts.AMBIGUOUS);
		
		final Map<?, ?> memberRequestBody = ImmutableMap.builder()
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("referenceSetId", Concepts.REFSET_OWL_AXIOM)
				.put(SnomedRf2Headers.FIELD_OWL_EXPRESSION, owlSubclassOfExpression)
				.build();

		final Map<?, ?> conceptRequestBody = createConceptRequestBody(Concepts.ROOT_CONCEPT)
				.put("members", ImmutableList.of(memberRequestBody))
				.put("commitComment", "Created concept with owl axiom reference set member")
				.build();

		final String conceptId = lastPathSegment(createComponent(branchPath, SnomedComponentType.CONCEPT, conceptRequestBody)
				.statusCode(201)
				.extract().header("Location"));
		
		final SnomedConcept conceptWithAxiomMember = SnomedRequests.prepareGetConcept(conceptId)
			.setExpand("members()")
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
			.execute(getBus())
			.getSync();
		
		assertNotNull(conceptWithAxiomMember);
		assertEquals(1, conceptWithAxiomMember.getMembers().getTotal());
		assertEquals(DefinitionStatus.PRIMITIVE, conceptWithAxiomMember.getDefinitionStatus()); 
	}
	
	@Test
	public void createConceptWithOwlAxiomMemberWithEquivalentClassesExpression() throws Exception {
		final String owlEquivalentClassesExpression = String.format("EquivalentClasses(:%s ObjectIntersectionOf(:%s :%s))", Concepts.FULLY_SPECIFIED_NAME, Concepts.AMBIGUOUS, Concepts.NAMESPACE_ROOT);
		
		final Map<?, ?> memberRequestBody = ImmutableMap.builder()
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("referenceSetId", Concepts.REFSET_OWL_AXIOM)
				.put(SnomedRf2Headers.FIELD_OWL_EXPRESSION, owlEquivalentClassesExpression)
				.build();

		final Map<?, ?> conceptRequestBody = createConceptRequestBody(Concepts.ROOT_CONCEPT)
				.put("members", ImmutableList.of(memberRequestBody))
				.put("commitComment", "Created concept with owl axiom reference set member")
				.build();

		final String conceptId = lastPathSegment(createComponent(branchPath, SnomedComponentType.CONCEPT, conceptRequestBody)
				.statusCode(201)
				.extract().header("Location"));
		
		final SnomedConcept conceptWithAxiomMember = SnomedRequests.prepareGetConcept(conceptId)
			.setExpand("members()")
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
			.execute(getBus())
			.getSync();
		
		assertNotNull(conceptWithAxiomMember);
		assertEquals(1, conceptWithAxiomMember.getMembers().getTotal());
		assertEquals(DefinitionStatus.FULLY_DEFINED, conceptWithAxiomMember.getDefinitionStatus()); 
	}

	@Test
	public void createConceptWithOwlAxiomMemberWithComplexSubClassOfExpressionShouldDefaultToPrimitive() throws Exception {
		final String owlSubClassOfExpression = "SubClassOf(ObjectIntersectionOf(:73211009 ObjectSomeValuesFrom(:609096000 ObjectSomeValuesFrom(:100106001 :100102001))) :8801005)";
		final Map<?, ?> memberRequestBody = ImmutableMap.builder()
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("referenceSetId", Concepts.REFSET_OWL_AXIOM)
				.put(SnomedRf2Headers.FIELD_OWL_EXPRESSION, owlSubClassOfExpression)
				.build();
		
		final Map<?, ?> conceptRequestBody = createConceptRequestBody(Concepts.ROOT_CONCEPT)
				.put("members", ImmutableList.of(memberRequestBody))
				.put("commitComment", "Created concept with owl axiom reference set member")
				.build();
		
		final String conceptId = lastPathSegment(createComponent(branchPath, SnomedComponentType.CONCEPT, conceptRequestBody)
				.statusCode(201)
				.extract().header("Location"));
		
		final SnomedConcept conceptWithAxiomMember = SnomedRequests.prepareGetConcept(conceptId)
				.setExpand("members()")
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
				.execute(getBus())
				.getSync();
		
		assertNotNull(conceptWithAxiomMember);
		assertEquals(1, conceptWithAxiomMember.getMembers().getTotal());
		assertEquals(DefinitionStatus.PRIMITIVE, conceptWithAxiomMember.getDefinitionStatus()); 
	}
	
	@Test
	public void createConceptWithoutOwlAxiomMembersConceptDefinitionStatusShouldDefaultToPrimitive() throws Exception {
		final Map<?, ?> conceptRequestBody = createConceptRequestBody(Concepts.ROOT_CONCEPT)
				.put("commitComment", "Created concept")
				.build();
		
		final String conceptId = lastPathSegment(createComponent(branchPath, SnomedComponentType.CONCEPT, conceptRequestBody)
				.statusCode(201)
				.extract().header("Location"));
		
		final SnomedConcept concept = SnomedRequests.prepareGetConcept(conceptId)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
				.execute(getBus())
				.getSync();
		
		assertNotNull(concept);
		assertEquals(DefinitionStatus.PRIMITIVE, concept.getDefinitionStatus()); 
	}
	
	@Test
	public void testUpdateConceptDefinitionStatusWithoutAxiomMembers() {
		final String conceptId = createNewConcept(branchPath);

		// Update the definition status on concept
		Map<?, ?> updateRequestBody = ImmutableMap.<String, Object>builder()
				.put("definitionStatus", DefinitionStatus.FULLY_DEFINED)
				.put("commitComment", "Changed definition status of concept to fully defined")
				.build();

		updateComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, updateRequestBody).statusCode(204);

		// Verify change of definition status on concept
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, "").statusCode(200)
			.body("definitionStatus", equalTo(DefinitionStatus.FULLY_DEFINED.toString()));
	}
	
	@Test
	public void testUpdateConceptDefinitionStatusWithAxiomMembersShouldNotChange() {
		final String conceptId = createNewConcept(branchPath);

		// Update the definition status on concept
		final Map<?, ?> definitionStatusUpdateRequestBody = ImmutableMap.<String, Object>builder()
				.put("definitionStatus", DefinitionStatus.FULLY_DEFINED)
				.put("commitComment", "Changed definition status of concept to fully defined")
				.build();

		updateComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, definitionStatusUpdateRequestBody).statusCode(204);
		
		final Map<String, Object> properties = ImmutableMap.of(SnomedRf2Headers.FIELD_OWL_EXPRESSION, String.format("EquivalentClasses(:%s ObjectIntersectionOf(:%s :%s))", Concepts.FULLY_SPECIFIED_NAME, Concepts.AMBIGUOUS, Concepts.NAMESPACE_ROOT));
		// Add a reference set member
		final SnomedReferenceSetMember newMember = new SnomedReferenceSetMember();
		newMember.setId(UUID.randomUUID().toString());
		newMember.setActive(true);
		newMember.setReferenceSetId(Concepts.REFSET_OWL_AXIOM);
		newMember.setProperties(properties);
		newMember.setType(SnomedRefSetType.OWL_AXIOM);
		newMember.setModuleId(Concepts.MODULE_SCT_CORE);

		final List<SnomedReferenceSetMember> changedMembers = ImmutableList.<SnomedReferenceSetMember>builder()
				.add(newMember)
				.build();

		final Map<?, ?> updateRequestBody = ImmutableMap.builder()
				.put("members", SnomedReferenceSetMembers.of(changedMembers))
				.put("definitionStatus", DefinitionStatus.PRIMITIVE)
				.put("commitComment", "Add new reference set member via concept update")
				.build();

		updateComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, updateRequestBody).statusCode(204);
		
		final SnomedConcept updatedConcept = getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, "members()")
				.statusCode(200)
				.extract().as(SnomedConcept.class);

		assertEquals(1, updatedConcept.getMembers().getTotal());
		
		// Verify that definition status is still fully defined
		assertEquals(DefinitionStatus.FULLY_DEFINED, updatedConcept.getDefinitionStatus());
	}
	
	@Test
	public void testUpdateConceptDefinitionStatusWithEquivalentClassesAxiomMember() {
		final String conceptId = createNewConcept(branchPath);

		final Map<String, Object> properties = ImmutableMap.of(SnomedRf2Headers.FIELD_OWL_EXPRESSION, String.format("EquivalentClasses(:%s ObjectIntersectionOf(:%s :%s))", Concepts.FULLY_SPECIFIED_NAME, Concepts.AMBIGUOUS, Concepts.NAMESPACE_ROOT));
		// Add a reference set member
		final SnomedReferenceSetMember newMember = new SnomedReferenceSetMember();
		newMember.setId(UUID.randomUUID().toString());
		newMember.setActive(true);
		newMember.setReferenceSetId(Concepts.REFSET_OWL_AXIOM);
		newMember.setProperties(properties);
		newMember.setType(SnomedRefSetType.OWL_AXIOM);
		newMember.setModuleId(Concepts.MODULE_SCT_CORE);

		final List<SnomedReferenceSetMember> changedMembers = ImmutableList.<SnomedReferenceSetMember>builder()
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
		
		// Verify that definition status was updated to FULLY DEFINED
		assertEquals(DefinitionStatus.FULLY_DEFINED, updatedConcept.getDefinitionStatus());
	}
	
	@Test
	public void testUpdateConceptDefinitionStatusWithSubClassOfAxiomMemberShouldChangeToPrimitive() {
		final String conceptId = createNewConcept(branchPath);

		// Update the definition status on concept
		final Map<?, ?> definitionStatusUpdateRequestBody = ImmutableMap.<String, Object>builder()
				.put("definitionStatus", DefinitionStatus.FULLY_DEFINED)
				.put("commitComment", "Changed definition status of concept to fully defined")
				.build();

		updateComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, definitionStatusUpdateRequestBody).statusCode(204);
		final Map<String, Object> properties = ImmutableMap.of(SnomedRf2Headers.FIELD_OWL_EXPRESSION, String.format("SubClassOf(:%s :%s)", Concepts.FULLY_SPECIFIED_NAME, Concepts.AMBIGUOUS));
		// Add a reference set member
		final SnomedReferenceSetMember newMember = new SnomedReferenceSetMember();
		newMember.setId(UUID.randomUUID().toString());
		newMember.setActive(true);
		newMember.setReferenceSetId(Concepts.REFSET_OWL_AXIOM);
		newMember.setProperties(properties);
		newMember.setType(SnomedRefSetType.OWL_AXIOM);
		newMember.setModuleId(Concepts.MODULE_SCT_CORE);

		final List<SnomedReferenceSetMember> changedMembers = ImmutableList.<SnomedReferenceSetMember>builder()
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
		
		// Verify that definition status was updated to FULLY DEFINED
		assertEquals(DefinitionStatus.PRIMITIVE, updatedConcept.getDefinitionStatus());
	}
	
	@Test
	public void testPendingMoveUpdateOnConceptWithSetDescriptions() {
		final String conceptId = createNewConcept(branchPath);
		final SnomedConcept conceptBeforeDescriptionPendingMoveChanges = SnomedRequests.prepareGetConcept(conceptId)
			.setExpand("descriptions()")
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
			.execute(getBus())
			.getSync();
		
		final List<SnomedDescription> descriptions = conceptBeforeDescriptionPendingMoveChanges.getDescriptions()
			.stream()
			.map(desc -> {
				desc.setInactivationIndicator(DescriptionInactivationIndicator.PENDING_MOVE);
				desc.setActive(false);
				return desc;
			}).collect(Collectors.toList());
			
		SnomedRequests.prepareUpdateConcept(conceptId)
			.setActive(false)
			.setDescriptions(descriptions)
			.setInactivationIndicator(InactivationIndicator.PENDING_MOVE)
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath(), "info@b2international.com", "Update concept inactivation indicator and its descriptions indicator")
			.execute(getBus())
			.getSync();
		
		final SnomedConcept conceptAfterDescriptionPendingMoveChanges = SnomedRequests.prepareGetConcept(conceptId)
				.setExpand("inactivationProperties(), descriptions(expand(inactivationProperties()))")
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
				.execute(getBus())
				.getSync();
		
		// Check if concepts indicator was updated or not
		assertEquals(InactivationIndicator.PENDING_MOVE, conceptAfterDescriptionPendingMoveChanges.getInactivationIndicator());

		conceptAfterDescriptionPendingMoveChanges.getDescriptions().forEach(desc -> {
			// Check descriptions inactivation indicator
			assertEquals(DescriptionInactivationIndicator.PENDING_MOVE, desc.getInactivationIndicator());
		});
	}
	
}
