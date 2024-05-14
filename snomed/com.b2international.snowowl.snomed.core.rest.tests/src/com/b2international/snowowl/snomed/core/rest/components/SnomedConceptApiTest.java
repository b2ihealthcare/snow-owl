/*
 * Copyright 2011-2024 B2i Healthcare, https://b2ihealthcare.com
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
import static com.b2international.snowowl.snomed.core.rest.SnomedApiTestConstants.UK_PREFERRED_MAP;
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.*;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.*;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemRestRequests.createCodeSystem;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.createVersion;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.getNextAvailableEffectiveDate;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.junit.Test;

import com.b2international.commons.exceptions.ConflictException;
import com.b2international.commons.json.Json;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.BranchPathUtils;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.bulk.BulkRequest;
import com.b2international.snowowl.core.events.bulk.BulkRequestBuilder;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.cis.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.cis.domain.IdentifierStatus;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.*;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.core.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests;
import com.b2international.snowowl.snomed.core.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.test.commons.SnomedContentRule;
import com.b2international.snowowl.test.commons.rest.RestExtensions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import io.restassured.response.ValidatableResponse;

/**
 * @since 2.0
 */
public class SnomedConceptApiTest extends AbstractSnomedApiTest {

	@Test
	public void updateConceptSemanticTag() throws Exception {
		String conceptId = createConcept(branchPath, SnomedRestFixtures.createConceptRequestBody(Concepts.ROOT_CONCEPT));
		SnomedConcept concept = getConcept(conceptId, "semanticTags()");
		assertThat(concept.getSemanticTags()).isEmpty();
		
		String fsnId = SnomedRestFixtures.createNewDescription(branchPath, Json.object(
			"conceptId", conceptId,
			"moduleId", Concepts.MODULE_SCT_CORE,
			"typeId", Concepts.FULLY_SPECIFIED_NAME,
			"term", "My awesome fsn (tag)",
			"languageCode", "en",
			"acceptability", UK_PREFERRED_MAP,
			"caseSignificanceId", Concepts.ENTIRE_TERM_CASE_INSENSITIVE,
			"commitComment", "New FSN"
		));
		
		concept = getConcept(conceptId, "semanticTags()");
		assertThat(concept.getSemanticTags()).contains("tag");
		
		SnomedComponentRestRequests.updateComponent(branchPath, SnomedComponentType.DESCRIPTION, fsnId, Json.object(
			"term", "My awesome fsn (updated)",
			"commitComment", "Update FSN semantic tag"
		));
		
		concept = getConcept(conceptId, "semanticTags()");
		assertThat(concept.getSemanticTags()).contains("updated");
	}
	
	@Test
	public void deleteConceptSemanticTag() throws Exception {
		String conceptId = createConcept(branchPath, SnomedRestFixtures.createConceptRequestBody(Concepts.ROOT_CONCEPT));
		SnomedConcept concept = getConcept(conceptId, "semanticTags()");
		assertThat(concept.getSemanticTags()).isEmpty();
		
		String fsnId = SnomedRestFixtures.createNewDescription(branchPath, Json.object(
			"conceptId", conceptId,
			"moduleId", Concepts.MODULE_SCT_CORE,
			"typeId", Concepts.FULLY_SPECIFIED_NAME,
			"term", "My awesome fsn (tag)",
			"languageCode", "en",
			"acceptability", UK_PREFERRED_MAP,
			"caseSignificanceId", Concepts.ENTIRE_TERM_CASE_INSENSITIVE,
			"commitComment", "New FSN"
		));
		
		concept = getConcept(conceptId, "semanticTags()");
		assertThat(concept.getSemanticTags()).contains("tag");
		
		SnomedComponentRestRequests.deleteComponent(branchPath, SnomedComponentType.DESCRIPTION, fsnId, false);
		
		concept = getConcept(conceptId, "semanticTags()");
		assertThat(concept.getSemanticTags()).isEmpty();
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
		response.extract()
			.body()
			.as(SnomedConcept.class)
			.getMembers()
			.forEach(m -> assertEquals("Reference set member should be placed in the default module", "449081005", m.getModuleId()));
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
				.with(SnomedRf2Headers.FIELD_TARGET_COMPONENT_ID, Concepts.ROOT_CONCEPT)
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
			.as(SnomedConcept.class)
			.getMembers();
		
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
		LocalDate effectiveDate = getNextAvailableEffectiveDate(shortName);
		createVersion(shortName, "v1", effectiveDate).statusCode(201);

		deleteComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, false).statusCode(409);
	}

	@Test
	public void forceDeleteConcept() {
		String conceptId = createNewConcept(branchPath);

		String shortName = "SNOMEDCT-CON-3";
		createCodeSystem(branchPath, shortName).statusCode(201);
		LocalDate effectiveDate = getNextAvailableEffectiveDate(shortName);
		createVersion(shortName, "v1", effectiveDate).statusCode(201);

		deleteComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, true).statusCode(204);
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId).statusCode(404);
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
		
		// assert that the new description ID got registered as Assigned
		assertSctIdStatus(newTextDefinition.getId(), IdentifierStatus.ASSIGNED);
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
		newRelationship.setRelationshipGroup(0);
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
		
		// assert that the new description ID got registered as Assigned
		assertSctIdStatus(newRelationship.getId(), IdentifierStatus.ASSIGNED);
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
		newMember.setRefsetId(refSetId);
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
			.build(branchPath.getPath())
			.execute(getBus())
			.getSync();
		
	}
	
	@Test
	public void searchConceptWithInboundRelationshipExpand() {
		final String conceptId = createNewConcept(branchPath);
		
		final String inboundRelationshipId = createNewRelationship(branchPath, Concepts.NAMESPACE_ROOT, Concepts.IS_A, conceptId);
		
		final SnomedConcept conceptWithInboundRelationship = SnomedRequests.prepareSearchConcept()
			.all()
			.filterById(conceptId)
			.setExpand("inboundRelationships()")
			.build(branchPath.getPath())
			.execute(getBus())
			.getSync()
			.stream().findFirst().get();
		 
		 assertNotNull(conceptWithInboundRelationship.getInboundRelationships());
		 assertEquals(1, conceptWithInboundRelationship.getInboundRelationships().getItems().size());
		 final SnomedRelationship expandedInboundRelationship = Iterables.getOnlyElement(conceptWithInboundRelationship.getInboundRelationships());
		 assertEquals(inboundRelationshipId, expandedInboundRelationship.getId());
	}
	
	@Test
	public void searchConceptWithInboundRelationshipExpandWithLimit() {
		final String conceptId = createNewConcept(branchPath);
		
		createNewRelationship(branchPath, Concepts.NAMESPACE_ROOT, Concepts.IS_A, conceptId);
		createNewRelationship(branchPath, Concepts.NAMESPACE_ROOT, Concepts.IS_A, conceptId);
		
		final SnomedConcept conceptWithInboundRelationships = SnomedRequests.prepareSearchConcept()
			.all()
			.filterById(conceptId)
			.setExpand("inboundRelationships(limit:1)")
			.build(branchPath.getPath())
			.execute(getBus())
			.getSync()
			.stream().findFirst().get();
		 
		 assertNotNull(conceptWithInboundRelationships.getInboundRelationships());
		 assertEquals(1, conceptWithInboundRelationships.getInboundRelationships().getItems().size());
	}
	
	@Test
	public void searchConceptWithInboundRelationshipExpandWithTypeFilter() {
		final String conceptId = createNewConcept(branchPath);
		
		createNewRelationship(branchPath, Concepts.NAMESPACE_ROOT, Concepts.HAS_DOSE_FORM, conceptId);
		createNewRelationship(branchPath, Concepts.NAMESPACE_ROOT, Concepts.IS_A, conceptId);
		
		final String inboundRelationshipExpandWithTypeFilter = String.format("inboundRelationships(typeId:\"%s\")", Concepts.HAS_DOSE_FORM);
		
		final SnomedConcept conceptWithInboundRelationshipsTypeIdFiltered = SnomedRequests.prepareSearchConcept()
			.all()
			.filterById(conceptId)
			.setExpand(inboundRelationshipExpandWithTypeFilter)
			.build(branchPath.getPath())
			.execute(getBus())
			.getSync()
			.stream().findFirst().get();
		 
		assertNotNull(conceptWithInboundRelationshipsTypeIdFiltered.getInboundRelationships());
		assertEquals(1, conceptWithInboundRelationshipsTypeIdFiltered.getInboundRelationships().getItems().size());
		final SnomedRelationship inboundRelationship = Iterables.getOnlyElement(conceptWithInboundRelationshipsTypeIdFiltered.getInboundRelationships());
		assertEquals(Concepts.HAS_DOSE_FORM, inboundRelationship.getTypeId());
	}
	
	@Test
	public void searchConceptWithInboundRelationshipExpandWithSourceFilter() {
		final String conceptId = createNewConcept(branchPath);
		
		createNewRelationship(branchPath, Concepts.MODULE_SCT_MODEL_COMPONENT, Concepts.HAS_DOSE_FORM, conceptId);
		createNewRelationship(branchPath, Concepts.NAMESPACE_ROOT, Concepts.IS_A, conceptId);
		
		final String inboundRelationshipExpandWithSourceIdFilter = String.format("inboundRelationships(sourceId:\"%s\")", Concepts.MODULE_SCT_MODEL_COMPONENT);
		
		final SnomedConcept conceptWithInboundRelationshipsTypeIdFiltered = SnomedRequests.prepareSearchConcept()
				.all()
				.filterById(conceptId)
				.setExpand(inboundRelationshipExpandWithSourceIdFilter)
				.build(branchPath.getPath())
				.execute(getBus())
				.getSync()
				.stream().findFirst().get();
		
		assertNotNull(conceptWithInboundRelationshipsTypeIdFiltered.getInboundRelationships());
		assertEquals(1, conceptWithInboundRelationshipsTypeIdFiltered.getInboundRelationships().getItems().size());
		final SnomedRelationship inboundRelationship = Iterables.getOnlyElement(conceptWithInboundRelationshipsTypeIdFiltered.getInboundRelationships());
		assertEquals(Concepts.MODULE_SCT_MODEL_COMPONENT, inboundRelationship.getSourceId());
	}
	
	@Test
	public void getConceptWithInboundRelationshipExpand() {
		final String conceptId = createNewConcept(branchPath);
		
		final String inboundRelationshipId = createNewRelationship(branchPath, Concepts.NAMESPACE_ROOT, Concepts.IS_A, conceptId);
		
		final SnomedConcept conceptWithInboundRelationship = SnomedRequests.prepareGetConcept(conceptId)
			.setExpand("inboundRelationships()")
			.build(branchPath.getPath())
			.execute(getBus())
			.getSync();
		 
		 assertNotNull(conceptWithInboundRelationship.getInboundRelationships());
		 assertEquals(1, conceptWithInboundRelationship.getInboundRelationships().getItems().size());
		 final SnomedRelationship expandedInboundRelationship = Iterables.getOnlyElement(conceptWithInboundRelationship.getInboundRelationships());
		 assertEquals(inboundRelationshipId, expandedInboundRelationship.getId());
	}
	
	@Test
	public void testUpdateConceptDefinitionStatusWithoutAxiomMembers() {
		final String conceptId = createNewConcept(branchPath);

		// Update the definition status on concept
		Json updateRequestBody = Json.object(
			"definitionStatusId", Concepts.FULLY_DEFINED,
			"commitComment", "Changed definition status of concept to fully defined"
		);

		updateComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, updateRequestBody).statusCode(204);

		// Verify change of definition status on concept
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, "").statusCode(200)
			.body("definitionStatusId", equalTo(Concepts.FULLY_DEFINED));
	}
	
	@Test
	public void testUpdateConceptDefinitionStatusWithAxiomMembersShouldNotChange() {
		final String conceptId = createNewConcept(branchPath);

		// Update the definition status on concept
		final Map<?, ?> definitionStatusUpdateRequestBody = Json.object(
			"definitionStatusId", Concepts.FULLY_DEFINED,
			"commitComment", "Changed definition status of concept to fully defined"
		);

		updateComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, definitionStatusUpdateRequestBody).statusCode(204);
		
		// Add a reference set member
		final SnomedReferenceSetMember newMember = new SnomedReferenceSetMember();
		newMember.setId(UUID.randomUUID().toString());
		newMember.setActive(true);
		newMember.setRefsetId(Concepts.REFSET_OWL_AXIOM);
		newMember.setProperties(
			Json.object(SnomedRf2Headers.FIELD_OWL_EXPRESSION, String.format("EquivalentClasses(:%s ObjectIntersectionOf(:%s :%s))", conceptId, Concepts.AMBIGUOUS, Concepts.NAMESPACE_ROOT))
		);
		newMember.setType(SnomedRefSetType.OWL_AXIOM);
		newMember.setModuleId(Concepts.MODULE_SCT_CORE);

		final Json updateRequestBody = Json.object(
			"members", SnomedReferenceSetMembers.of(Json.array(newMember)),
			"definitionStatusId", Concepts.PRIMITIVE,
			"commitComment", "Add new reference set member via concept update"
		);

		updateComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, updateRequestBody).statusCode(204);
		
		final SnomedConcept updatedConcept = getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, "members()")
				.statusCode(200)
				.extract().as(SnomedConcept.class);

		assertEquals(1, updatedConcept.getMembers().getTotal());
		
		// Verify that definition status is still fully defined
		assertEquals(Concepts.FULLY_DEFINED, updatedConcept.getDefinitionStatusId());
	}
	
	@Test
	public void testUpdateConceptDefinitionStatusWithEquivalentClassesAxiomMember() {
		final String conceptId = createNewConcept(branchPath);

		// Add a reference set member
		final SnomedReferenceSetMember newMember = new SnomedReferenceSetMember();
		newMember.setId(UUID.randomUUID().toString());
		newMember.setActive(true);
		newMember.setRefsetId(Concepts.REFSET_OWL_AXIOM);
		newMember.setProperties(
			Json.object(SnomedRf2Headers.FIELD_OWL_EXPRESSION, String.format("EquivalentClasses(:%s ObjectIntersectionOf(:%s :%s))", conceptId, Concepts.AMBIGUOUS, Concepts.NAMESPACE_ROOT))
		);
		newMember.setType(SnomedRefSetType.OWL_AXIOM);
		newMember.setModuleId(Concepts.MODULE_SCT_CORE);

		final Map<?, ?> updateRequestBody = Json.object(
			"members", SnomedReferenceSetMembers.of(Json.array(newMember)),
			"commitComment", "Add new reference set member via concept update"
		);

		updateComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, updateRequestBody).statusCode(204);
		
		final SnomedConcept updatedConcept = getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, "members()")
				.statusCode(200)
				.extract().as(SnomedConcept.class);

		assertEquals(1, updatedConcept.getMembers().getTotal());
		
		// Verify that definition status was updated to FULLY DEFINED
		assertEquals(Concepts.FULLY_DEFINED, updatedConcept.getDefinitionStatusId());
	}
	
	@Test
	public void testUpdateConceptDefinitionStatusWithSubClassOfAxiomMemberShouldChangeToPrimitive() {
		final String conceptId = createNewConcept(branchPath);

		// Update the definition status on concept
		final Map<?, ?> definitionStatusUpdateRequestBody = Json.object(
			"definitionStatusId", Concepts.FULLY_DEFINED,
			"commitComment", "Changed definition status of concept to fully defined"
		);

		updateComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, definitionStatusUpdateRequestBody).statusCode(204);
		// Add a reference set member
		final SnomedReferenceSetMember newMember = new SnomedReferenceSetMember();
		newMember.setId(UUID.randomUUID().toString());
		newMember.setActive(true);
		newMember.setRefsetId(Concepts.REFSET_OWL_AXIOM);
		newMember.setProperties(
			Json.object(SnomedRf2Headers.FIELD_OWL_EXPRESSION, String.format("SubClassOf(:%s :%s)", conceptId, Concepts.AMBIGUOUS))
		);
		newMember.setType(SnomedRefSetType.OWL_AXIOM);
		newMember.setModuleId(Concepts.MODULE_SCT_CORE);

		final Json updateRequestBody = Json.object(
			"members", SnomedReferenceSetMembers.of(Json.array(newMember)),
			"commitComment", "Add new reference set member via concept update"
		);

		updateComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, updateRequestBody).statusCode(204);
		
		final SnomedConcept updatedConcept = getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, "members()")
				.statusCode(200)
				.extract().as(SnomedConcept.class);

		assertEquals(1, updatedConcept.getMembers().getTotal());
		
		// Verify that definition status was updated to FULLY DEFINED
		assertEquals(Concepts.PRIMITIVE, updatedConcept.getDefinitionStatusId());
	}
	
	@Test
	public void testPendingMoveUpdateOnConceptWithSetDescriptions() {
		final String conceptId = createNewConcept(branchPath);
		final SnomedConcept conceptBeforeDescriptionPendingMoveChanges = SnomedRequests.prepareGetConcept(conceptId)
			.setExpand("descriptions()")
			.build(branchPath.getPath())
			.execute(getBus())
			.getSync();
		
		final List<SnomedDescription> descriptions = conceptBeforeDescriptionPendingMoveChanges.getDescriptions()
			.stream()
			.map(desc -> {
				desc.setInactivationProperties(new InactivationProperties(Concepts.PENDING_MOVE, Collections.emptyList()));
				desc.setActive(false);
				return desc;
			}).collect(Collectors.toList());
			
		SnomedRequests.prepareUpdateConcept(conceptId)
			.setDescriptions(descriptions)
			.build(branchPath.getPath(), RestExtensions.USER, "Update concept inactivation indicator and its descriptions indicator")
			.execute(getBus())
			.getSync();
		
		final SnomedConcept conceptAfterDescriptionPendingMoveChanges = SnomedRequests.prepareGetConcept(conceptId)
				.setExpand("descriptions(expand(inactivationProperties()))")
				.build(branchPath.getPath())
				.execute(getBus())
				.getSync();
		
		conceptAfterDescriptionPendingMoveChanges.getDescriptions().forEach(desc -> {
			// Check descriptions inactivation indicator
			assertEquals(Concepts.PENDING_MOVE, desc.getInactivationProperties().getInactivationIndicatorId());
		});
	}
	
	@Test
	public void testConceptInactivationModuleChanges() {
		final String conceptId = createNewConcept(branchPath);
		final String moduleConceptId = createNewConcept(branchPath);
		String sourceRelationshipId = createNewRelationship(branchPath, conceptId, Concepts.HAS_DOSE_FORM, Concepts.MODULE_SCT_MODEL_COMPONENT);
		String destinationRelationshipId = createNewRelationship(branchPath, Concepts.MODULE_SCT_MODEL_COMPONENT, Concepts.HAS_DOSE_FORM, conceptId);
		ResourceURI codeSystemURI = SnomedContentRule.SNOMEDCT.withPath(branchPath.getPath().replace(Branch.MAIN_PATH + "/", ""));

		SnomedRelationship sourceRelationship = SnomedRequests.prepareGetRelationship(sourceRelationshipId)
				.build(codeSystemURI)
				.execute(getBus())
				.getSync();

		SnomedRelationship destinationRelationship = SnomedRequests.prepareGetRelationship(destinationRelationshipId)
				.build(codeSystemURI)
				.execute(getBus())
				.getSync();

		Request<TransactionContext,Boolean> request = SnomedRequests.prepareUpdateConcept(conceptId)
				.setActive(false)
				.build();

		SnomedRequests.prepareCommit()
				.setDefaultModuleId(moduleConceptId)
				.setBody(request)
				.setCommitComment("commit")
				.build(codeSystemURI)
				.execute(getBus())
				.getSync();

		SnomedRelationship updatedSourceRelationship = SnomedRequests.prepareGetRelationship(sourceRelationshipId)
				.build(codeSystemURI)
				.execute(getBus())
				.getSync();

		SnomedRelationship updatedDestinationRelationship = SnomedRequests.prepareGetRelationship(destinationRelationshipId)
				.build(codeSystemURI)
				.execute(getBus())
				.getSync();

		//Before update
		assertTrue(sourceRelationship.isActive());
		assertTrue(destinationRelationship.isActive());
		assertFalse(moduleConceptId.equals(sourceRelationship.getModuleId()));
		assertFalse(moduleConceptId.equals(destinationRelationship.getModuleId()));

		//After update
		assertFalse(updatedSourceRelationship.isActive());
		assertEquals(moduleConceptId, updatedSourceRelationship.getModuleId());
		assertFalse(updatedDestinationRelationship.isActive());
		assertEquals(moduleConceptId, updatedDestinationRelationship.getModuleId());
	}
	
	@Test
	public void reactivateConceptWithExistingOWLAxiom() throws Exception {
		final String conceptId = ApplicationContext.getServiceForClass(ISnomedIdentifierService.class).generate(null, ComponentCategory.CONCEPT, 1).iterator().next();
		final String owlSubclassOfExpression = String.format("SubClassOf(:%s :%s)", conceptId, Concepts.ROOT_CONCEPT);
		final String owlAxiomMemberId = UUID.randomUUID().toString();

		// create an inactive concept with an active OWL axiom
		Json conceptRequestBody = createConceptRequestBody(Concepts.FULLY_SPECIFIED_NAME)
			.with(Json.object(
				"id", conceptId,
				"active", false,
				"members", Json.array(Json.object(
					"id", owlAxiomMemberId,
					"active", true,
					"moduleId", Concepts.MODULE_SCT_CORE,
					"refsetId", Concepts.REFSET_OWL_AXIOM,
					SnomedRf2Headers.FIELD_OWL_EXPRESSION, owlSubclassOfExpression
				)),
				"commitComment", "Created concept with owl axiom reference set member"
			));
		conceptRequestBody = conceptRequestBody.without("relationships");
		createConcept(branchPath, conceptRequestBody);
		
		// check the concept before reactivating, parentage info should be empty
		SnomedConcept concept = getConcept(conceptId);
//		assertThat(concept.getStatedAncestorIdsAsString()).isEmpty();
//		assertThat(concept.getStatedParentIdsAsString()).contains(IComponent.ROOT_ID);
		
		// reactivate concept and check stated parentage info
		reactivateConcept(branchPath, conceptId);
		
		// it should receive the necessary info to be part of the tree again
		concept = getConcept(conceptId);
		assertThat(concept.getStatedAncestorIdsAsString()).contains(IComponent.ROOT_ID);
		assertThat(concept.getStatedParentIdsAsString()).contains(Concepts.ROOT_CONCEPT);
	}
	
}
