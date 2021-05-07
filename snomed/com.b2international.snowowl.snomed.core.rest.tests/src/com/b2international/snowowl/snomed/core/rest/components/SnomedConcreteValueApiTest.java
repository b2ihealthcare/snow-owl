/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.createConcreteValueRequestBody;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.createNewConcept;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.createNewConcreteValue;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.createCodeSystemAndVersion;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.assertCreated;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.b2international.commons.exceptions.ConflictException;
import com.b2international.commons.json.Json;
import com.b2international.snowowl.core.branch.BranchPathUtils;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.cis.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.cis.domain.IdentifierStatus;
import com.b2international.snowowl.snomed.cis.domain.SctId;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.RelationshipValue;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.core.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.test.commons.Services;
import com.google.common.collect.Iterables;

/**
 * @since 7.17
 */
public class SnomedConcreteValueApiTest extends AbstractSnomedApiTest {

	@Test
	public void createConcreteValueNonExistentBranch() {
		Json requestBody = createConcreteValueRequestBody(
			Concepts.ROOT_CONCEPT, Concepts.PART_OF, new RelationshipValue("Hello world!"))
			.with("commitComment", "Created new concrete value on non-existent branch");

		createComponent(BranchPathUtils.createPath("MAIN/x/y/z"), SnomedComponentType.RELATIONSHIP, requestBody).statusCode(404);
	}

	@Test
	public void createConcreteValueInvalidSource() {
		Json requestBody = createConcreteValueRequestBody(
			"11110000", Concepts.PART_OF, new RelationshipValue(3.334d))
			.with("commitComment", "Created new concrete value with invalid sourceId");

		createComponent(branchPath, SnomedComponentType.RELATIONSHIP, requestBody).statusCode(400);
	}

	@Test
	public void createConcreteValueInvalidType() {
		Json requestBody = createConcreteValueRequestBody(
			Concepts.ROOT_CONCEPT, "11110000", new RelationshipValue(false))
			.with("commitComment", "Created new concrete value with invalid typeId");

		createComponent(branchPath, SnomedComponentType.RELATIONSHIP, requestBody).statusCode(400);
	}

	@Test
	public void createConcreteValueInvalidValue() {
		Json requestBody = createConcreteValueRequestBody(
			Concepts.ROOT_CONCEPT, Concepts.PART_OF, new RelationshipValue(false))
			/*
			 * XXX: need to set literal again here, as the request body creation method above 
			 * does not allow invalid values.
			 */
			.with("value", "2021-06-05")
			.with("commitComment", "Created new concrete value with invalid value");

		createComponent(branchPath, SnomedComponentType.RELATIONSHIP, requestBody).statusCode(400);
	}

	@Test
	public void createConcreteValueInvalidModule() {
		Json requestBody = createConcreteValueRequestBody(
			Concepts.ROOT_CONCEPT, Concepts.PART_OF, new RelationshipValue(5), Concepts.INFERRED_RELATIONSHIP, 0, "11110000")
			.with("commitComment", "Created new concrete value with invalid moduleId");

		createComponent(branchPath, SnomedComponentType.RELATIONSHIP, requestBody).statusCode(400);
	}

	@Test
	public void createConcreteValue() {
		Json requestBody = createConcreteValueRequestBody(
			Concepts.ROOT_CONCEPT, Concepts.PART_OF, new RelationshipValue("string value"))
			.with("commitComment", "Created new concrete value");

		createComponent(branchPath, SnomedComponentType.RELATIONSHIP, requestBody).statusCode(201);
	}

	@Test
	public void createConcreteValueWithReservedId() {
		ISnomedIdentifierService identifierService = getServiceForClass(ISnomedIdentifierService.class);
		String relationshipId = Iterables.getOnlyElement(identifierService.reserve(null, ComponentCategory.RELATIONSHIP, 1));

		Json requestBody = createConcreteValueRequestBody(
			Concepts.ROOT_CONCEPT, Concepts.PART_OF, new RelationshipValue("string value"))
			.with("id", relationshipId)
			.with("commitComment", "Created new concrete value with reserved identifier");

		createComponent(branchPath, SnomedComponentType.RELATIONSHIP, requestBody)
			.statusCode(201)
			.header("Location", endsWith("/" + relationshipId));
		
		SctId concreteValueSctId = SnomedRequests.identifiers().prepareGet()
			.setComponentId(relationshipId)
			.buildAsync()
			.execute(getBus())
			.getSync()
			.first()
			.get();
			
		assertEquals(IdentifierStatus.ASSIGNED.getSerializedName(), concreteValueSctId.getStatus());
	}

	@Test
	public void createDuplicateConcreteValue() {
		String relationshipId = createNewConcreteValue(branchPath);
		Json requestBody = createConcreteValueRequestBody(
			Concepts.ROOT_CONCEPT, Concepts.PART_OF, new RelationshipValue("string value"))
			.with("id", relationshipId)
			.with("commitComment", "Created new concrete value with duplicate identifier");

		createComponent(branchPath, SnomedComponentType.RELATIONSHIP, requestBody).statusCode(409);
	}

	@Test
	public void createConcreteValueInferred() {
		Json requestBody = createConcreteValueRequestBody(
			Concepts.ROOT_CONCEPT, Concepts.PART_OF, new RelationshipValue(7), Concepts.INFERRED_RELATIONSHIP)
			.with("commitComment", "Created new concrete value with inferred characteristic type");

		String relationshipId = assertCreated(createComponent(branchPath, SnomedComponentType.RELATIONSHIP, requestBody));

		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId)
			.statusCode(200)
			.body("characteristicTypeId", equalTo(Concepts.INFERRED_RELATIONSHIP));
	}

	@Test
	public void deleteConcreteValue() {
		String relationshipId = createNewConcreteValue(branchPath);
		deleteComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId, false).statusCode(204);
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId).statusCode(404);
	}

	@Test
	public void inactivateConcreteValue() {
		String relationshipId = createNewConcreteValue(branchPath);
		Json requestBody = Json.object(
			"active", false,
			"commitComment", "Inactivated concrete value");

		updateComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId, requestBody)
			.statusCode(204);
		
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId)
			.statusCode(200)
			.body("active", equalTo(false));
	}

	@Test
	public void deleteInactiveConcreteValue() {
		String sourceId = createNewConcept(branchPath);
		String relationshipId = createNewConcreteValue(branchPath, sourceId, Concepts.PART_OF, new RelationshipValue("hello world!"));

		Json requestBody = Json.object(
			"active", false,
			"commitComment", "Inactivated concrete value");

		updateComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId, requestBody).statusCode(204);
		deleteComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId, false).statusCode(204);
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId).statusCode(404);

		/* 
		 * Source concept should still exist at this point (the deletion plan should not 
		 * consider removing it with the concrete value).
		 */
		getComponent(branchPath, SnomedComponentType.CONCEPT, sourceId).statusCode(200);
	}

	@Test
	public void createInactiveConcreteValue() {
		Json requestBody = createConcreteValueRequestBody(
			Concepts.ROOT_CONCEPT, Concepts.PART_OF, new RelationshipValue(5))
			.with("active", false)
			.with("commitComment", "Created inactive concrete value");

		String relationshipId = assertCreated(createComponent(branchPath, SnomedComponentType.RELATIONSHIP, requestBody));

		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId)
			.statusCode(200)
			.body("active", equalTo(false));
	}

	@Test
	public void updateGroup() {
		String relationshipId = createNewConcreteValue(branchPath);
		Json requestBody = Json.object(
			"group", 99,
			"commitComment", "Updated concrete value group");

		updateComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId, requestBody)
			.statusCode(204);
		
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId)
			.statusCode(200)
			.body("group", equalTo(99));
	}

	@Test
	public void updateGroupToInvalidValue() {
		String relationshipId = createNewConcreteValue(branchPath);
		Json requestBody = Json.object(
			"group", -5,
			"commitComment", "Updated concrete value group to invalid value");

		updateComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId, requestBody)
			.statusCode(400);
		
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId)
			.statusCode(200)
			.body("group", equalTo(0));
	}

	@Test
	public void changeConcreteValueCharacteristicType() {
		String relationshipId = createNewConcreteValue(branchPath);
		Json requestBody = Json.object(
			"characteristicTypeId", Concepts.ADDITIONAL_RELATIONSHIP,
			"commitComment", "Updated concrete value characteristic type");

		updateComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId, requestBody)
			.statusCode(204);
		
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId)
			.statusCode(200)
			.body("characteristicTypeId", equalTo(Concepts.ADDITIONAL_RELATIONSHIP));
	}

	@Test
	public void changeConcreteValueModifier() {
		String relationshipId = createNewConcreteValue(branchPath);
		Json requestBody = Json.object(
			"modifierId", Concepts.UNIVERSAL_RESTRICTION_MODIFIER,
			"commitComment", "Updated concrete value modifier");

		updateComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId, requestBody).statusCode(204);
		
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId)
			.statusCode(200)
			.body("modifierId", equalTo(Concepts.UNIVERSAL_RESTRICTION_MODIFIER));
	}
	
	@Test
	public void updateUnreleasedConcreteValueTypeId() throws Exception {
		String relationshipId = createNewConcreteValue(branchPath);
		Json update = Json.object(
			SnomedRf2Headers.FIELD_TYPE_ID, Concepts.DEFINING_RELATIONSHIP, // "part of" is the initial type ID
			"commitComment", "Updated unreleased concrete value typeId");

		updateComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId, update)
			.statusCode(204);
		
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId)
			.statusCode(200)
			.body(SnomedRf2Headers.FIELD_TYPE_ID, equalTo(Concepts.DEFINING_RELATIONSHIP));
	}
	
	@Test
	public void updateUnreleasedConcreteValue() throws Exception {
		String relationshipId = createNewConcreteValue(branchPath);
		Json update = Json.object(
			"value", "#15",
			"commitComment", "Updated value of unreleased concrete value"
		);

		updateComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId, update)
			.statusCode(204);
		
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId)
			.statusCode(200)
			.body("value", equalTo("#15"));
	}
	
	@Test
	public void updateReleasedConcreteValueTypeId() throws Exception {
		String relationshipId = createNewConcreteValue(branchPath);
		Json update = Json.object(
			SnomedRf2Headers.FIELD_TYPE_ID, Concepts.DEFINING_RELATIONSHIP, // "part of" is the initial type ID
			"commitComment", "Updated released concrete value typeId");

		// release component first
		createCodeSystemAndVersion(branchPath, "SNOMEDCT-CONCVAL-TYPEID", "v1", "20170301");
		
		// then try to update - it should fail as typeId is immutable
		updateComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId, update)
			.statusCode(400);
		
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId)
			.statusCode(200)
			.body(SnomedRf2Headers.FIELD_TYPE_ID, equalTo(Concepts.PART_OF));
	}
	
	@Test
	public void updateReleasedConcreteValue() throws Exception {
		String relationshipId = createNewConcreteValue(branchPath);
		Json update = Json.object(
			"value", "true",
			"commitComment", "Updated released concrete value"
		);

		// release component
		createCodeSystemAndVersion(branchPath, "SNOMEDCT-CONCVAL-VALUE", "v1", "20170301");
		
		updateComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId, update)
			.statusCode(400);
		
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId)
			.statusCode(200)
			.body("value", equalTo("\"Hello World!\""));
	}
	
	@Test(expected = ConflictException.class)
	public void doNotDeleteReleasedConcreteValues() {
		String relationshipId = createNewConcreteValue(branchPath);
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId)
			.statusCode(200)
			.body("released", equalTo(false));

		createCodeSystemAndVersion(branchPath, "SNOMEDCT-CONCVAL-RELEASE", "v1", "20210505");
		
		SnomedRequests.prepareCommit()
			.setBody(SnomedRequests.prepareDeleteRelationship(relationshipId))
			.setCommitComment("Delete released concrete value")
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
			.execute(Services.bus())
			.getSync();
	}
	
	@Test
	public void restoreEffectiveTimeOnReleasedConcreteValue() throws Exception {
		final String relationshipId = createNewConcreteValue(branchPath);
	
		// Create code system and version
		final String effectiveTime = "20170301";
		createCodeSystemAndVersion(branchPath, "SNOMEDCT-CONCVAL-EFFTIME", "v1", effectiveTime);

		// After versioning, the concrete value should be released and have an effective time set on it
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId)
			.statusCode(200)
			.body("active", equalTo(true))
			.body("released", equalTo(true))
			.body("effectiveTime", equalTo(effectiveTime));
		
		Json inactivationRequestBody = Json.object(
			"active", false,
			"commitComment", "Inactivate concrete value");

		updateComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId, inactivationRequestBody).statusCode(204);

		// An inactivation should unset the effective time field
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId)
			.statusCode(200)
			.body("active", equalTo(false))
			.body("released", equalTo(true))
			.body("effectiveTime", nullValue());

		Json reactivationRequestBody = Json.object(
			"active", true,
			"commitComment", "Reactivate concrete value");

		updateComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId, reactivationRequestBody).statusCode(204);

		// Getting the concrete value back to its originally released state should restore its effective time
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId)
			.statusCode(200)
			.body("active", equalTo(true))
			.body("released", equalTo(true))
			.body("effectiveTime", equalTo(effectiveTime));
	}
	
	@Test
	public void updateConcreteValueWithSamePropertiesShouldNotCauseAnyChange() throws Exception {
		final String relationshipId = createNewConcreteValue(branchPath);
		
		final SnomedRelationship concreteValue = getRelationship(relationshipId);
		concreteValue.setActive(new Boolean(true)); // explicitly create a new Boolean object with same value to simulate deserialization "bug"
		concreteValue.setValueAsObject(new RelationshipValue("Hello World!")); // same value as set in createNewConcreteValue(String)
		
		Boolean updated = SnomedRequests.prepareCommit()
			.setCommitComment("Update concrete value")
			.setBody(concreteValue.toUpdateRequest())
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
			.execute(getBus())
			.getSync(1, TimeUnit.MINUTES)
			.getResultAs(Boolean.class);
		
		assertFalse(updated);
	}
}
