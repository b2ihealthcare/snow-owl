/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static com.b2international.snowowl.snomed.core.rest.SnomedApiTestConstants.UK_ACCEPTABLE_MAP;
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.*;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.*;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemRestRequests.createCodeSystem;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.createVersion;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.getNextAvailableEffectiveDateAsString;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.assertCreated;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.*;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.b2international.commons.exceptions.ConflictException;
import com.b2international.commons.json.Json;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.branch.BranchPathUtils;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.bulk.BulkRequest;
import com.b2international.snowowl.core.events.bulk.BulkRequestBuilder;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.cis.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.cis.domain.IdentifierStatus;
import com.b2international.snowowl.snomed.cis.domain.SctId;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.*;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.core.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.core.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.core.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.test.commons.rest.RestExtensions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import io.restassured.response.ValidatableResponse;

/**
 * @since 2.0
 */
public class SnomedConceptApiTest extends AbstractSnomedApiTest {

	@Test
	public void createConceptNonExistentBranch() {
		assertCreateConcept(BranchPathUtils.createPath("MAIN/x/y/z"), createConceptRequestBody(Concepts.ROOT_CONCEPT))
			.statusCode(404);
	}

	@Test
	public void createConceptEmptyParent() {
		assertCreateConcept(branchPath, createConceptRequestBody(""))
			.statusCode(400)
			.body("message", equalTo("1 validation error"))
			.body("violations", hasItem("'destinationId' may not be empty (was '')"));
	}

	@Test
	public void createConceptInvalidParent() {
		assertCreateConcept(branchPath, createConceptRequestBody("11110000"))
			.statusCode(400);
	}

	@Test
	public void createConceptInvalidLanguageRefSet() {
		assertCreateConcept(branchPath, createConceptRequestBody(Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE, SnomedApiTestConstants.INVALID_PREFERRED_MAP))
			.statusCode(400);
	}

	@Test
	public void createConceptInvalidModule() {
		assertCreateConcept(branchPath, createConceptRequestBody(Concepts.ROOT_CONCEPT, "11110000", SnomedApiTestConstants.INVALID_PREFERRED_MAP))
			.statusCode(400);
	}

	@Test
	public void createConceptWithoutCommitComment() {
		assertCreateConcept(branchPath, createConceptRequestBody(Concepts.ROOT_CONCEPT).with("commitComment", ""))
			.statusCode(400);
	}
	
	@Test
	public void createConceptOnDeletedBranch() {
		branching.deleteBranch(branchPath);

		assertCreateConcept(branchPath, createConceptRequestBody(Concepts.ROOT_CONCEPT))
			.statusCode(400);
	}

	@Test
	public void createShortIsACycle() throws Exception {
		String concept1Id = createNewConcept(branchPath);
		String concept2Id = createNewConcept(branchPath, concept1Id);

		// Try creating a cycle between the two concepts
		Map<?, ?> requestBody = createRelationshipRequestBody(concept1Id, Concepts.IS_A, concept2Id)
				.with("commitComment", "Created an IS A cycle with two relationships");

		createComponent(branchPath, SnomedComponentType.RELATIONSHIP, requestBody).statusCode(400);
	}
	
	@Test
	public void createConcept() {
		final String conceptId = createConcept(branchPath, createConceptRequestBody(Concepts.ROOT_CONCEPT));
		final SnomedConcept concept = getConcept(conceptId, "statedAncestors(direct:true),ancestors(direct:true)");
		assertEquals(1, concept.getStatedAncestors().getTotal());
		assertEquals(0, concept.getAncestors().getTotal());
	}
	
	@Test
	public void createConceptWithReservedId() {
		ISnomedIdentifierService identifierService = ApplicationContext.getServiceForClass(ISnomedIdentifierService.class);
		String conceptId = Iterables.getOnlyElement(identifierService.reserve(null, ComponentCategory.CONCEPT, 1));

		String createConceptId = createConcept(branchPath, createConceptRequestBody(Concepts.ROOT_CONCEPT).with("id", conceptId));
		
		assertEquals(conceptId, createConceptId);
		
		SctId conceptSctId = SnomedRequests.identifiers().prepareGet()
			.setComponentId(conceptId)
			.buildAsync()
			.execute(getBus())
			.getSync(1, TimeUnit.MINUTES)
			.first()
			.get();
		
		assertEquals(IdentifierStatus.ASSIGNED.getSerializedName(), conceptSctId.getStatus());
	}

	@Test
	public void createLongIsACycle() throws Exception {
		String concept1Id = createNewConcept(branchPath);
		String concept2Id = createNewConcept(branchPath, concept1Id);
		String concept3Id = createNewConcept(branchPath, concept2Id);

		// Try creating a cycle between the starting and the ending concept
		Map<?, ?> requestBody = createRelationshipRequestBody(concept1Id, Concepts.IS_A, concept3Id)
				.with("commitComment", "Created an IS A cycle with three relationships");

		createComponent(branchPath, SnomedComponentType.RELATIONSHIP, requestBody).statusCode(400);
	}

	@Test
	public void updateAssociationTarget() throws Exception {
		String conceptToInactivate = createNewConcept(branchPath);
		String firstAssociationTarget = createNewConcept(branchPath);
		String secondAssociationTarget = createNewConcept(branchPath);

		// Inactivate the concept first pointing to the first association target
		assertInactivation(
			branchPath,
			conceptToInactivate, 
			new InactivationProperties(Concepts.DUPLICATE, List.of(
				new AssociationTarget(Concepts.REFSET_POSSIBLY_EQUIVALENT_TO_ASSOCIATION, firstAssociationTarget)
			))
		);
		
		// Inactivate again, this time it will update the inactivation reason and association target properties
		assertInactivation(
			branchPath,
			conceptToInactivate, 
			new InactivationProperties(Concepts.AMBIGUOUS, List.of(
				new AssociationTarget(Concepts.REFSET_REPLACED_BY_ASSOCIATION, secondAssociationTarget)
			))
		);
	}
	
	@Test
	public void updateAssociationTargetWithReuse() throws Exception {
		String conceptToInactivate = createNewConcept(branchPath);
		String firstAssociationTarget = createNewConcept(branchPath);
		String secondAssociationTarget = createNewConcept(branchPath);
		String thirdAssociationTarget = createNewConcept(branchPath);

		// Inactivate the duplicate concept and point to the other one
		Collection<String> memberIds = assertInactivation(
			branchPath, 
			conceptToInactivate, 
			new InactivationProperties(Concepts.DUPLICATE, List.of(
				new AssociationTarget(Concepts.REFSET_POSSIBLY_EQUIVALENT_TO_ASSOCIATION, firstAssociationTarget)
			))
		).extract().path("members.items.id");
		assertEquals(2, memberIds.size());

		// Inactivate again, this time it will update the inactivation reason and association target properties
		Collection<String> updatedMemberIds = assertInactivation(
			branchPath,
			conceptToInactivate, 
			new InactivationProperties(Concepts.AMBIGUOUS, List.of(
				new AssociationTarget(Concepts.REFSET_POSSIBLY_EQUIVALENT_TO_ASSOCIATION, firstAssociationTarget),
				new AssociationTarget(Concepts.REFSET_REPLACED_BY_ASSOCIATION, secondAssociationTarget),
				new AssociationTarget(Concepts.REFSET_REPLACED_BY_ASSOCIATION, thirdAssociationTarget)
			))
		).extract().path("members.items.id");

		// Verify that the member UUIDs have not been cycled
		assertEquals(4, updatedMemberIds.size());
		assertTrue(updatedMemberIds.containsAll(memberIds));
	}

	@Test
	public void updateAssociationTargetWithDefaultModule() throws Exception {
		String conceptToInactivate = createNewConcept(branchPath);
		String firstAssociationTarget = createNewConcept(branchPath);
		String secondAssociationTarget = createNewConcept(branchPath);

		// Inactivate the duplicate concept and point to the other one
		assertInactivation(
			branchPath, 
			conceptToInactivate, 
			new InactivationProperties(Concepts.DUPLICATE, List.of(
				new AssociationTarget(Concepts.REFSET_POSSIBLY_EQUIVALENT_TO_ASSOCIATION, firstAssociationTarget)
			))
		);

		// Update the inactivation reason and association target properties, specifying the module
		final ValidatableResponse response = assertInactivation(
			branchPath, 
			conceptToInactivate, 
			new InactivationProperties(Concepts.AMBIGUOUS, List.of(
				new AssociationTarget(Concepts.REFSET_REPLACED_BY_ASSOCIATION, secondAssociationTarget)
			)),
			"449081005" // defaultModuleId
		);
		
		// Check that the default module is honored
		final SnomedReferenceSetMembers refSetMembers = response.extract()
			.body()
			.jsonPath()
			.getObject("members", SnomedReferenceSetMembers.class);
		
		refSetMembers.forEach(m -> assertEquals(
				"Reference set member should be placed in the default module", "449081005", m.getModuleId()));
	}
	
	@Test
	public void updateInactivationIndicatorOnActiveConcept() throws Exception {
		String conceptId = createNewConcept(branchPath);
		
		// ensure that the concept does not have any indicator set before the update
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, "inactivationProperties()")
			.statusCode(200)
			.body("inactivationProperties.inactivationIndicator", nullValue());
		
		// Inactivate the duplicate concept and point to the other one
		assertInactivation(
			branchPath, 
			conceptId, 
			new InactivationProperties(Concepts.PENDING_MOVE, List.of())
		);
	}
	
	@Test
	public void updateInactivationIndicatorOnActiveConceptWithAssociationTargets() throws Exception {
		String conceptId = createNewConcept(branchPath);
		Map<?, ?> requestBody = createRefSetMemberRequestBody(Concepts.REFSET_POSSIBLY_EQUIVALENT_TO_ASSOCIATION, conceptId)
				.with(SnomedRf2Headers.FIELD_TARGET_COMPONENT, Concepts.ROOT_CONCEPT)
				.with("commitComment", "Created new reference set member");

		final String associationMemberId = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody)
				.statusCode(201)
				.extract().header("Location"));
		
		// ensure that the concept does not have any indicator set before the update
		SnomedReferenceSetMembers currentMembers = getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, "inactivationProperties(),members()")
			.statusCode(200)
			.body("inactivationProperties.inactivationIndicatorId", nullValue())
			.body("inactivationProperties.associationTargets.referenceSetId", equalTo(List.of(Concepts.REFSET_POSSIBLY_EQUIVALENT_TO_ASSOCIATION)))
			.body("inactivationProperties.associationTargets.targetComponentId", equalTo(List.of(Concepts.ROOT_CONCEPT)))
			.extract()
			.jsonPath().getObject("members", SnomedReferenceSetMembers.class);
		
		Map<?, ?> updateReq = Json.object(
			"inactivationProperties", new InactivationProperties(Concepts.PENDING_MOVE, List.of(new AssociationTarget(Concepts.REFSET_POSSIBLY_EQUIVALENT_TO_ASSOCIATION, Concepts.ROOT_CONCEPT))),
			// XXX also pass the current members to the update, without the fix this would cause duplicate association members
			"members", currentMembers,
			"commitComment", "Add a pending move indicator to an active concept"
		);
		updateComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, updateReq)
			.statusCode(204);
		List<String> memberIds = getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, "inactivationProperties(),members()")
			.statusCode(200)
			.body("inactivationProperties.inactivationIndicatorId", equalTo(Concepts.PENDING_MOVE))
			.body("inactivationProperties.associationTargets.referenceSetId", equalTo(List.of(Concepts.REFSET_POSSIBLY_EQUIVALENT_TO_ASSOCIATION)))
			.body("inactivationProperties.associationTargets.targetComponentId", equalTo(List.of(Concepts.ROOT_CONCEPT)))
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
				.with("id", conceptId)
				.with("commitComment", "Created new concept with duplicate identifier");

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
		branching.createBranchRecursively(b);

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

		Map<?, ?> conceptRequestBody = createConceptRequestBody(Concepts.ROOT_CONCEPT)
				.with("members", Json.array(Json.object(
					"moduleId", Concepts.MODULE_SCT_CORE,
					"referenceSetId", refSetId
				)))
				.with("commitComment", "Created concept with reference set member");

		String conceptId = assertCreated(createComponent(branchPath, SnomedComponentType.CONCEPT, conceptRequestBody));

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
		newTextDefinition.setCaseSignificanceId(Concepts.ONLY_INITIAL_CHARACTER_CASE_INSENSITIVE);
		newTextDefinition.setModuleId(Concepts.MODULE_SCT_CORE);

		List<SnomedDescription> changedDescriptions = ImmutableList.<SnomedDescription>builder()
				.addAll(concept.getDescriptions())
				.add(newTextDefinition)
				.build();

		Map<?, ?> updateRequestBody = Json.object(
			"descriptions", SnomedDescriptions.of(changedDescriptions),
			"commitComment", "Add new description via concept update"
		);

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
		newRelationship.setCharacteristicTypeId(Concepts.STATED_RELATIONSHIP);
		newRelationship.setTypeId(Concepts.PART_OF);
		newRelationship.setDestinationId(Concepts.NAMESPACE_ROOT);
		newRelationship.setModuleId(Concepts.MODULE_SCT_CORE);
		newRelationship.setGroup(0);
		newRelationship.setUnionGroup(0);
		newRelationship.setModifierId(Concepts.EXISTENTIAL_RESTRICTION_MODIFIER);

		final List<SnomedRelationship> changedRelationships = ImmutableList.<SnomedRelationship>builder()
				.addAll(concept.getRelationships())
				.add(newRelationship)
				.build();

		final Map<?, ?> updateRequestBody = Json.object(
			"relationships", SnomedRelationships.of(changedRelationships),
			"commitComment", "Add new relationship via concept update"
		);

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

		final Map<?, ?> updateRequestBody = Json.object(
			"members", SnomedReferenceSetMembers.of(changedMembers),
			"commitComment", "Add new reference set member via concept update"
		);

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
			.setAuthor("test")
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
			.execute(getBus())
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
	public void testConceptInactivationModuleChanges() {
		final String conceptId = createNewConcept(branchPath);
		String sourceRelationshipId = createNewRelationship(branchPath, conceptId, Concepts.HAS_DOSE_FORM, Concepts.MODULE_SCT_MODEL_COMPONENT);
		String destinationRelationshipId = createNewRelationship(branchPath, Concepts.MODULE_SCT_MODEL_COMPONENT, Concepts.HAS_DOSE_FORM, conceptId);
		
		
		SnomedRelationship sourceRelationship = SnomedRequests.prepareGetRelationship(sourceRelationshipId)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
				.execute(getBus())
				.getSync();
		
		SnomedRelationship destinationRelationship = SnomedRequests.prepareGetRelationship(destinationRelationshipId)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
				.execute(getBus())
				.getSync();

		SnomedRequests.prepareUpdateConcept(conceptId)
				.setActive(false)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath(), RestExtensions.USER, "commit",  Concepts.MODULE_B2I_EXTENSION)
				.execute(getBus())
				.getSync();

		SnomedRelationship updatedSourceRelationship = SnomedRequests.prepareGetRelationship(sourceRelationshipId)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
				.execute(getBus())
				.getSync();

		SnomedRelationship updatedDestinationRelationship = SnomedRequests.prepareGetRelationship(destinationRelationshipId)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
				.execute(getBus())
				.getSync();

		//Before update
		assertTrue(sourceRelationship.isActive());
		assertTrue(destinationRelationship.isActive());
		assertFalse(Concepts.MODULE_B2I_EXTENSION.equals(sourceRelationship.getModuleId()));
		assertFalse(Concepts.MODULE_B2I_EXTENSION.equals(destinationRelationship.getModuleId()));
		
		//After update
		assertFalse(updatedSourceRelationship.isActive());
		assertEquals(Concepts.MODULE_B2I_EXTENSION, updatedSourceRelationship.getModuleId());
		assertFalse(updatedDestinationRelationship.isActive());
		assertEquals(Concepts.MODULE_B2I_EXTENSION, updatedDestinationRelationship.getModuleId());

	}

}
