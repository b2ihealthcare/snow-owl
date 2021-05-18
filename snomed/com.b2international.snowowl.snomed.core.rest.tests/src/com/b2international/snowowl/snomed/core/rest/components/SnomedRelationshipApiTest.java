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

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.createComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.deleteComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.updateComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.createNewConcept;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.createNewRelationship;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.createRelationshipRequestBody;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemRestRequests.createCodeSystem;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.createCodeSystemAndVersion;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.createVersion;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.getNextAvailableEffectiveDate;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.assertCreated;
import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.b2international.commons.exceptions.ConflictException;
import com.b2international.commons.json.Json;
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
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.core.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.test.commons.Services;
import com.google.common.collect.Iterables;

/**
 * @since 4.0
 */
public class SnomedRelationshipApiTest extends AbstractSnomedApiTest {

	private static final String RELEASED_ISA_RELATIONSHIP_ID = "1019504021";

	@Test
	public void createRelationshipNonExistentBranch() {
		Json requestBody = createRelationshipRequestBody(Concepts.ROOT_CONCEPT, Concepts.PART_OF, Concepts.NAMESPACE_ROOT)
				.with("commitComment", "Created new relationship on non-existent branch");

		createComponent(BranchPathUtils.createPath("MAIN/x/y/z"), SnomedComponentType.RELATIONSHIP, requestBody).statusCode(404);
	}

	@Test
	public void createRelationshipInvalidSource() {
		Json requestBody = createRelationshipRequestBody("11110000", Concepts.PART_OF, Concepts.NAMESPACE_ROOT)
				.with("commitComment", "Created new relationship with invalid sourceId");

		createComponent(branchPath, SnomedComponentType.RELATIONSHIP, requestBody).statusCode(400);
	}

	@Test
	public void createRelationshipInvalidType() {
		Json requestBody = createRelationshipRequestBody(Concepts.ROOT_CONCEPT, "11110000", Concepts.NAMESPACE_ROOT)
				.with("commitComment", "Created new relationship with invalid typeId");

		createComponent(branchPath, SnomedComponentType.RELATIONSHIP, requestBody).statusCode(400);
	}

	@Test
	public void createRelationshipInvalidDestination() {
		Json requestBody = createRelationshipRequestBody(Concepts.ROOT_CONCEPT, Concepts.PART_OF, "11110000")
				.with("commitComment", "Created new relationship with invalid destinationId");

		createComponent(branchPath, SnomedComponentType.RELATIONSHIP, requestBody).statusCode(400);
	}

	@Test
	public void createRelationshipInvalidModule() {
		Json requestBody = createRelationshipRequestBody(Concepts.ROOT_CONCEPT, Concepts.PART_OF, Concepts.NAMESPACE_ROOT, "11110000")
				.with("commitComment", "Created new relationship with invalid moduleId");

		createComponent(branchPath, SnomedComponentType.RELATIONSHIP, requestBody).statusCode(400);
	}

	@Test
	public void createRelationship() {
		Json requestBody = createRelationshipRequestBody(Concepts.ROOT_CONCEPT, Concepts.PART_OF, Concepts.NAMESPACE_ROOT)
				.with("commitComment", "Created new relationship");

		createComponent(branchPath, SnomedComponentType.RELATIONSHIP, requestBody).statusCode(201);
	}

	@Test
	public void createRelationshipWithReservedId() {
		ISnomedIdentifierService identifierService = getServiceForClass(ISnomedIdentifierService.class);
		String relationshipId = Iterables.getOnlyElement(identifierService.reserve(null, ComponentCategory.RELATIONSHIP, 1));

		Json requestBody = createRelationshipRequestBody(Concepts.ROOT_CONCEPT, Concepts.PART_OF, Concepts.NAMESPACE_ROOT)
				.with("id", relationshipId)
				.with("commitComment", "Created new relationship with reserved identifier");

		createComponent(branchPath, SnomedComponentType.RELATIONSHIP, requestBody).statusCode(201)
		.header("Location", endsWith("/" + relationshipId));
		
		SctId relationshipSctId = SnomedRequests.identifiers().prepareGet()
				.setComponentId(relationshipId)
				.buildAsync()
				.execute(getBus())
				.getSync()
				.first()
				.get();
			
		assertEquals(IdentifierStatus.ASSIGNED.getSerializedName(), relationshipSctId.getStatus());
	}

	@Test
	public void createDuplicateRelationship() {
		String relationshipId = createNewRelationship(branchPath);
		Json requestBody = createRelationshipRequestBody(Concepts.ROOT_CONCEPT, Concepts.PART_OF, Concepts.NAMESPACE_ROOT)
				.with("id", relationshipId)
				.with("commitComment", "Created new relationship with duplicate identifier");

		createComponent(branchPath, SnomedComponentType.RELATIONSHIP, requestBody).statusCode(409);
	}

	@Test
	public void createRelationshipInferred() {
		Json requestBody = createRelationshipRequestBody(Concepts.ROOT_CONCEPT, Concepts.PART_OF, Concepts.NAMESPACE_ROOT, Concepts.INFERRED_RELATIONSHIP)
				.with("commitComment", "Created new relationship with inferred characteristic type");

		String relationshipId = assertCreated(createComponent(branchPath, SnomedComponentType.RELATIONSHIP, requestBody));

		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId).statusCode(200)
		.body("characteristicTypeId", equalTo(Concepts.INFERRED_RELATIONSHIP));
	}

	@Test
	public void deleteRelationship() {
		String relationshipId = createNewRelationship(branchPath);
		deleteComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId, false).statusCode(204);
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId).statusCode(404);
	}

	@Test
	public void inactivateRelationship() {
		String relationshipId = createNewRelationship(branchPath);
		Json requestBody = Json.object(
			"active", false,
			"commitComment", "Inactivated relationship"
		);

		updateComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId, requestBody)
			.statusCode(204);
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId).statusCode(200)
			.body("active", equalTo(false));
	}

	@Test
	public void deleteInactiveNonIsaRelationship() throws Exception {
		deleteInactiveRelationship(Concepts.PART_OF);
	}

	@Test
	public void deleteInactiveIsaRelationship() throws Exception {
		deleteInactiveRelationship(Concepts.IS_A);
	}

	private void deleteInactiveRelationship(String typeId) {
		String sourceId = createNewConcept(branchPath);
		String relationshipId = createNewRelationship(branchPath, sourceId, typeId, Concepts.NAMESPACE_ROOT);

		Json requestBody = Json.object(
			"active", false,
			"commitComment", "Inactivated relationship"
		);

		updateComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId, requestBody).statusCode(204);
		deleteComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId, false).statusCode(204);
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId).statusCode(404);

		/* 
		 * Source concept should still exist at this point (the deletion plan should not 
		 * consider removing it with the relationship).
		 */
		getComponent(branchPath, SnomedComponentType.CONCEPT, sourceId).statusCode(200);
	}

	@Test
	public void createInactiveNonIsaRelationship() throws Exception {
		createInactiveRelationship(Concepts.PART_OF);
	}

	@Test
	public void createInactiveIsaRelationship() throws Exception {
		createInactiveRelationship(Concepts.IS_A);
	}

	private void createInactiveRelationship(String typeId) {
		Json requestBody = createRelationshipRequestBody(Concepts.ROOT_CONCEPT, typeId, Concepts.NAMESPACE_ROOT)
				.with("active", false)
				.with("commitComment", "Created inactive relationship");

		String relationshipId = assertCreated(createComponent(branchPath, SnomedComponentType.RELATIONSHIP, requestBody));

		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId).statusCode(200)
			.body("active", equalTo(false));
	}

	@Test
	public void createIsARelationshipToSelf() throws Exception {
		Json requestBody = createRelationshipRequestBody(Concepts.NAMESPACE_ROOT, Concepts.IS_A, Concepts.NAMESPACE_ROOT)
				.with("commitComment", "Created new relationship pointing to itself");

		createComponent(branchPath, SnomedComponentType.RELATIONSHIP, requestBody)
			.statusCode(400);
	}

	@Test
	public void updateGroup() {
		String relationshipId = createNewRelationship(branchPath);
		Json requestBody = Json.object(
			"group", 99,
			"commitComment", "Updated relationship group"
		);

		updateComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId, requestBody)
			.statusCode(204);
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId)
			.statusCode(200)
			.body("group", equalTo(99));
	}

	@Test
	public void updateGroupToInvalidValue() {
		String relationshipId = createNewRelationship(branchPath);
		Json requestBody = Json.object(
			"group", -5,
			"commitComment", "Updated relationship group to invalid value"
		);

		updateComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId, requestBody)
			.statusCode(400);
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId)
			.statusCode(200)
			.body("group", equalTo(0));
	}

	@Test
	public void changeRelationshipUnionGroup() {
		String relationshipId = createNewRelationship(branchPath);
		Json requestBody = Json.object(
			"unionGroup", 101,
			"commitComment", "Updated relationship union group"
		);

		updateComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId, requestBody)
			.statusCode(204);
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId)
			.statusCode(200)
			.body("unionGroup", equalTo(101));
	}

	@Test
	public void changeRelationshipCharacteristicType() {
		String relationshipId = createNewRelationship(branchPath);
		Json requestBody = Json.object(
			"characteristicTypeId", Concepts.ADDITIONAL_RELATIONSHIP,
			"commitComment", "Updated relationship characteristic type"
		);

		updateComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId, requestBody)
			.statusCode(204);
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId)
			.statusCode(200)
			.body("characteristicTypeId", equalTo(Concepts.ADDITIONAL_RELATIONSHIP));
	}

	@Test
	public void changeRelationshipModifier() {
		String relationshipId = createNewRelationship(branchPath);
		Json requestBody = Json.object(
			"modifierId", Concepts.UNIVERSAL_RESTRICTION_MODIFIER,
			"commitComment", "Updated relationship modifier"
		);

		updateComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId, requestBody).statusCode(204);
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId).statusCode(200)
		.body("modifierId", equalTo(Concepts.UNIVERSAL_RESTRICTION_MODIFIER));
	}

	@Test
	public void createRelationshipOnNestedBranch() {
		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		IBranchPath b = BranchPathUtils.createPath(a, "b");
		branching.createBranchRecursively(b);

		String relationshipId = createNewRelationship(b);

		getComponent(b, SnomedComponentType.RELATIONSHIP, relationshipId).statusCode(200);
		getComponent(a, SnomedComponentType.RELATIONSHIP, relationshipId).statusCode(404);
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId).statusCode(404);
	}

	@Test
	public void deleteRelationshipOnNestedBranch() {
		String conceptId = createNewConcept(branchPath);

		List<String> typeIds = newArrayList();
		for (int i = 0; i < 10; i++) {
			String typeId = createNewConcept(branchPath);
			typeIds.add(typeId);
		}

		List<String> relationshipIds = newArrayList();
		for (int i = 0; i < 10; i++) {
			String relationshipId = createNewRelationship(branchPath, conceptId, typeIds.get(i), Concepts.NAMESPACE_ROOT);
			relationshipIds.add(relationshipId);
		}

		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		IBranchPath b = BranchPathUtils.createPath(a, "b");
		branching.createBranchRecursively(b);

		// New relationship on nested branch resets the concept's version to 1 again
		createNewRelationship(b, conceptId, Concepts.PART_OF, Concepts.NAMESPACE_ROOT);

		// Deleting a relationship from the middle should work
		String relationshipToDeleteId = relationshipIds.remove(7);
		deleteComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipToDeleteId, false).statusCode(204);
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipToDeleteId).statusCode(404);

		deleteComponent(b, SnomedComponentType.RELATIONSHIP, relationshipToDeleteId, false).statusCode(204);
		getComponent(b, SnomedComponentType.RELATIONSHIP, relationshipToDeleteId).statusCode(404);

		// All the remaining relationships should be visible
		for (String relationshipId : relationshipIds) {
			getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId).statusCode(200);
			getComponent(b, SnomedComponentType.RELATIONSHIP, relationshipId).statusCode(200);
		}
	}
	
	@Test
	public void updateUnreleasedRelationshipTypeId() throws Exception {
		String relationshipId = createNewRelationship(branchPath);
		Json update = Json.object(
			SnomedRf2Headers.FIELD_TYPE_ID, Concepts.DEFINING_RELATIONSHIP, // part of is the initial type
			"commitComment", "Updated unreleased relationship typeId"
		);

		updateComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId, update)
			.statusCode(204);
		
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId)
			.statusCode(200)
			.body(SnomedRf2Headers.FIELD_TYPE_ID, equalTo(Concepts.DEFINING_RELATIONSHIP));
	}
	
	@Test
	public void updateUnreleasedRelationshipDestinationId() throws Exception {
		String relationshipId = createNewRelationship(branchPath);
		Json update = Json.object(
			SnomedRf2Headers.FIELD_DESTINATION_ID, Concepts.MODULE_ROOT,
			"commitComment", "Updated unreleased relationship destinationId"
		);

		updateComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId, update)
			.statusCode(204);
		
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId)
			.statusCode(200)
			.body(SnomedRf2Headers.FIELD_DESTINATION_ID, equalTo(Concepts.MODULE_ROOT));
	}
	
	@Test
	public void updateReleasedRelationshipTypeId() throws Exception {
		String relationshipId = createNewRelationship(branchPath);
		Json update = Json.object(
			SnomedRf2Headers.FIELD_TYPE_ID, Concepts.DEFINING_RELATIONSHIP, // part of is the initial type
			"commitComment", "Updated unreleased relationship typeId"
		);

		// release component
		createCodeSystemAndVersion(branchPath, "SNOMEDCT-RELREL-TYPEID", "v1", LocalDate.parse("2017-03-01"));
		
		updateComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId, update)
			.statusCode(400);
		
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId)
			.statusCode(200)
			.body(SnomedRf2Headers.FIELD_TYPE_ID, equalTo(Concepts.PART_OF));
	}
	
	@Test
	public void updateReleasedRelationshipDestinationId() throws Exception {
		String relationshipId = createNewRelationship(branchPath);
		Json update = Json.object(
			SnomedRf2Headers.FIELD_DESTINATION_ID, Concepts.MODULE_ROOT,
			"commitComment", "Updated unreleased relationship destinationId"
		);

		// release component
		createCodeSystemAndVersion(branchPath, "SNOMEDCT-RELREL-DESTID", "v1", LocalDate.parse("2017-03-01"));
		
		updateComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId, update)
			.statusCode(400);
		
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId)
			.statusCode(200)
			.body(SnomedRf2Headers.FIELD_DESTINATION_ID, equalTo(Concepts.NAMESPACE_ROOT));
	}
	
	@Test(expected = ConflictException.class)
	public void doNotDeleteReleasedRelationships() {
		
		String relationshipId = createNewRelationship(branchPath);
		
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, RELEASED_ISA_RELATIONSHIP_ID)
			.statusCode(200)
			.body("released", equalTo(true));
		
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId)
			.statusCode(200)
			.body("released", equalTo(false));
		
		final BulkRequestBuilder<TransactionContext> bulk = BulkRequest.create();
		
		bulk.add(SnomedRequests.prepareDeleteRelationship(relationshipId));
		bulk.add(SnomedRequests.prepareDeleteRelationship(RELEASED_ISA_RELATIONSHIP_ID));

		SnomedRequests.prepareCommit()
			.setBody(bulk)
			.setCommitComment("Delete multiple relationships")
			.build(branchPath.getPath())
			.execute(Services.bus())
			.getSync();
		
	}
	
	@Test
	public void restoreEffectiveTimeOnReleasedRelationship() throws Exception {
		final String relationshipId = createNewRelationship(branchPath);

		final String shortName = "SNOMEDCT-REL-1";
		createCodeSystem(branchPath, shortName).statusCode(201);
		final LocalDate effectiveDate = getNextAvailableEffectiveDate(shortName);
		createVersion(shortName, "v1", effectiveDate).statusCode(201);

		// After versioning, the relationship should be released and have an effective time set on it
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId).statusCode(200)
		.body("active", equalTo(true))
		.body("released", equalTo(true))
		.body("effectiveTime", equalTo(effectiveDate.format(DateTimeFormatter.BASIC_ISO_DATE)));
		
		Json inactivationRequestBody = Json.object(
			"active", false,
			"commitComment", "Inactivated relationship"
		);

		updateComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId, inactivationRequestBody).statusCode(204);

		// An inactivation should unset the effective time field
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId).statusCode(200)
		.body("active", equalTo(false))
		.body("released", equalTo(true))
 		.body("effectiveTime", nullValue());

		Json reactivationRequestBody = Json.object(
			"active", true,
			"commitComment", "Inactivated relationships"
		);

		updateComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId, reactivationRequestBody).statusCode(204);

		// Getting the relationship back to its originally released state should restore the effective time
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId).statusCode(200)
			.body("active", equalTo(true))
			.body("released", equalTo(true))
			.body("effectiveTime", equalTo(effectiveDate.format(DateTimeFormatter.BASIC_ISO_DATE)));
	}
	
	@Test
	public void updateRelationshipWithSamePropertiesShouldNotCauseAnyChange() throws Exception {
		final String relationshipId = createNewRelationship(branchPath);
		final SnomedRelationship relationship = getRelationship(relationshipId);
		relationship.setActive(new Boolean(true)); // explicitly create a new Boolean object with same value to simulate deserialization "bug"
		Boolean updated = SnomedRequests.prepareCommit()
			.setCommitComment("Update relationship")
			.setBody(relationship.toUpdateRequest())
			.build(branchPath.getPath())
			.execute(getBus())
			.getSync(1, TimeUnit.MINUTES)
			.getResultAs(Boolean.class);
		assertFalse(updated);
	}
	
}
