/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.createComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedRefSetRestRequests.executeMemberAction;
import static com.b2international.snowowl.snomed.api.rest.SnomedRefSetRestRequests.updateRefSetComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.*;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;
import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.bulk.BulkRequest;
import com.b2international.snowowl.core.events.bulk.BulkRequestBuilder;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.ImmutableMap;

/**
 * @since 4.5
 */
public class SnomedRefSetMemberApiTest extends AbstractSnomedApiTest {

	@Test
	public void getMemberNonExistingBranch() throws Exception {
		// UUID is the language reference set member for the SNOMED CT root concept's FSN
		getComponent(BranchPathUtils.createPath("MAIN/x/y/z"), SnomedComponentType.MEMBER, "e606c375-501d-5db6-821f-f03d8a12ad1c").statusCode(404);
	}

	@Test
	public void getMemberNonExistingIdentifier() throws Exception {
		getComponent(branchPath, SnomedComponentType.MEMBER, "00001111-0000-0000-0000-000000000000").statusCode(404);
	}

	@Test
	public void createConcreteDomainMemberInvalidValue() {
		createConcreteDomainParentConcept(branchPath);

		String refSetId = createConcreteDomainRefSet(branchPath, DataType.INTEGER);
		Map<?, ?> requestBody = createRefSetMemberRequestBody(refSetId, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_ATTRIBUTE_NAME, "numberOfWidgets")
				.put(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID, Concepts.STATED_RELATIONSHIP)
				.put(SnomedRf2Headers.FIELD_VALUE, "five") // bad
				.put(SnomedRf2Headers.FIELD_OPERATOR_ID, Concepts.REFSET_ATTRIBUTE)
				.put("commitComment", "Created new reference set member")
				.build();

		createComponent(branchPath, SnomedComponentType.MEMBER, requestBody).statusCode(400);
	}

	@Test
	public void createConcreteDomainMember() {
		createConcreteDomainParentConcept(branchPath);

		String refSetId = createConcreteDomainRefSet(branchPath, DataType.DECIMAL);
		Map<?, ?> requestBody = createRefSetMemberRequestBody(refSetId, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_ATTRIBUTE_NAME, "pi")
				.put(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID, Concepts.STATED_RELATIONSHIP)
				.put(SnomedRf2Headers.FIELD_VALUE, "3.1415927")
				.put(SnomedRf2Headers.FIELD_OPERATOR_ID, Concepts.REFSET_ATTRIBUTE) // Using "Reference set attribute" root as operator
				.put("commitComment", "Created new reference set member")
				.build();

		String memberId = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, requestBody)
				.statusCode(201)
				.extract().header("Location"));

		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200)
		.body(SnomedRf2Headers.FIELD_ATTRIBUTE_NAME, equalTo("pi"))
		.body(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID, equalTo(Concepts.STATED_RELATIONSHIP))
		.body(SnomedRf2Headers.FIELD_VALUE, equalTo("3.1415927"))
		.body(SnomedRf2Headers.FIELD_OPERATOR_ID, equalTo(Concepts.REFSET_ATTRIBUTE));
	}

	@Test
	public void updateConcreteDomainMember() {
		createConcreteDomainParentConcept(branchPath);

		String refSetId = createConcreteDomainRefSet(branchPath, DataType.DECIMAL);
		Map<?, ?> createRequest = createRefSetMemberRequestBody(refSetId, Concepts.ROOT_CONCEPT)
				.put(SnomedRf2Headers.FIELD_ATTRIBUTE_NAME, "pi")
				.put(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID, Concepts.STATED_RELATIONSHIP)
				.put(SnomedRf2Headers.FIELD_VALUE, "3.1415927")
				.put(SnomedRf2Headers.FIELD_OPERATOR_ID, Concepts.REFSET_ATTRIBUTE) // Using "Reference set attribute" root as operator
				.put("commitComment", "Created new concrete domain reference set member")
				.build();

		String memberId = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, createRequest)
				.statusCode(201)
				.extract().header("Location"));

		@SuppressWarnings("unchecked")
		Map<String, Object> member = getComponent(branchPath, SnomedComponentType.MEMBER, memberId)
		.statusCode(200)
		.body(SnomedRf2Headers.FIELD_ATTRIBUTE_NAME, equalTo("pi"))
		.body(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID, equalTo(Concepts.STATED_RELATIONSHIP))
		.body(SnomedRf2Headers.FIELD_VALUE, equalTo("3.1415927"))
		.body(SnomedRf2Headers.FIELD_OPERATOR_ID, equalTo(Concepts.REFSET_ATTRIBUTE))
		.extract().as(Map.class);

		member.put(SnomedRf2Headers.FIELD_ATTRIBUTE_NAME, "e");
		member.put(SnomedRf2Headers.FIELD_VALUE, "2.7182818");
		member.put("commitComment", "Updated existing concrete domain reference set member");

		updateRefSetComponent(branchPath, SnomedComponentType.MEMBER, memberId, member, false).statusCode(204);
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId)
		.statusCode(200)
		.body(SnomedRf2Headers.FIELD_ATTRIBUTE_NAME, equalTo("e"))
		.body(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID, equalTo(Concepts.STATED_RELATIONSHIP))
		.body(SnomedRf2Headers.FIELD_VALUE, equalTo("2.7182818"))
		.body(SnomedRf2Headers.FIELD_OPERATOR_ID, equalTo(Concepts.REFSET_ATTRIBUTE));
	}

	@Test
	public void executeInvalidAction() throws Exception {
		String queryRefSetId = createNewRefSet(branchPath, SnomedRefSetType.QUERY);
		String simpleRefSetId = createNewRefSet(branchPath);

		final Map<?, ?> memberRequest = ImmutableMap.<String, Object>builder()
				.put(SnomedRf2Headers.FIELD_MODULE_ID, Concepts.MODULE_SCT_CORE)
				.put("referenceSetId", queryRefSetId)
				.put(SnomedRf2Headers.FIELD_REFERENCED_COMPONENT_ID, simpleRefSetId)
				.put(SnomedRf2Headers.FIELD_QUERY, "<" + Concepts.REFSET_ROOT_CONCEPT)
				.put("commitComment", "Created new query reference set member")
				.build();

		final String memberId = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, memberRequest)
				.statusCode(201)
				.extract().header("Location"));

		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200);

		final Map<?, ?> invalidActionRequest = ImmutableMap.<String, Object>builder()
				.put("action", "invalid")
				.put("commitComment", "Executed invalid action on reference set member")
				.build();

		executeMemberAction(branchPath, memberId, invalidActionRequest).statusCode(400)
		.body("message", CoreMatchers.equalTo("Invalid action type 'invalid'."));
	}

	@Test
	public void executeSyncAction() throws Exception {
		String queryRefSetId = createNewRefSet(branchPath, SnomedRefSetType.QUERY);
		String simpleRefSetId = createNewRefSet(branchPath);

		String parentId = createNewConcept(branchPath);
		List<String> conceptIds = newArrayList();
		for (int i = 0; i < 3; i++) {
			String conceptId = createNewConcept(branchPath, parentId);
			conceptIds.add(conceptId);
			// Need to add an inferred IS A counterpart, as query evaluation uses inferred relationships
			createNewRelationship(branchPath, conceptId, Concepts.IS_A, parentId, CharacteristicType.INFERRED_RELATIONSHIP);
		}

		final Map<?, ?> memberRequest = ImmutableMap.<String, Object>builder()
				.put(SnomedRf2Headers.FIELD_MODULE_ID, Concepts.MODULE_SCT_CORE)
				.put("referenceSetId", queryRefSetId)
				.put(SnomedRf2Headers.FIELD_REFERENCED_COMPONENT_ID, simpleRefSetId)
				.put(SnomedRf2Headers.FIELD_QUERY, "<" + parentId)
				.put("commitComment", "Created new query reference set member")
				.build();

		final String memberId = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, memberRequest)
				.statusCode(201)
				.extract().header("Location"));

		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200);

		// Since we have used an existing simple type refset, it will have no members initially, so sync first 
		executeSyncAction(memberId);
		checkReferencedComponentIds(conceptIds, simpleRefSetId);

		// Add a new concept that matches the query, then sync again
		String extraConceptId = createNewConcept(branchPath, parentId);
		conceptIds.add(extraConceptId);
		createNewRelationship(branchPath, extraConceptId, Concepts.IS_A, parentId, CharacteristicType.INFERRED_RELATIONSHIP);

		executeSyncAction(memberId);
		checkReferencedComponentIds(conceptIds, simpleRefSetId);
	}
	
	/**
	 * Removals are sent in a BulkRequest which includes individual DeleteRequests for each member to be deleted. The version of SnomedEditingContext prior to the fix, however, used
	 * a server-side query to determine the list index for each member, and the list index reported by the database become misaligned with the actual
	 * contents if the members were not removed in decreasing index order. This test verifies that order of delete requests inside a bulk request does not matter.
	 */
	@Test
	public void issue_SO_2501_ioobe_during_member_deletion() throws Exception {
		String simpleRefSetId = createNewRefSet(branchPath);
		
		final Map<?, ?> memberRequest = ImmutableMap.<String, Object>builder()
				.put(SnomedRf2Headers.FIELD_MODULE_ID, Concepts.MODULE_SCT_CORE)
				.put("referenceSetId", simpleRefSetId)
				.put(SnomedRf2Headers.FIELD_REFERENCED_COMPONENT_ID, simpleRefSetId)
				.put("commitComment", "Created new simple reference set member")
				.build();
		
		final String member1Id = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, memberRequest)
				.statusCode(201)
				.extract().header("Location"));
		
		final String member2Id = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, memberRequest)
				.statusCode(201)
				.extract().header("Location"));
		
		final String member3Id = lastPathSegment(createComponent(branchPath, SnomedComponentType.MEMBER, memberRequest)
				.statusCode(201)
				.extract().header("Location"));
		
		final BulkRequestBuilder<TransactionContext> bulk = BulkRequest.create();
		
		bulk.add(SnomedRequests.prepareDeleteMember(member1Id));
		bulk.add(SnomedRequests.prepareDeleteMember(member3Id));

		SnomedRequests.prepareCommit()
			.setBody(bulk)
			.setCommitComment("Delete reference set members in ascending index order")
			.setUserId("test")
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
			.execute(ApplicationContext.getServiceForClass(IEventBus.class))
			.getSync();
	}

	private void executeSyncAction(final String memberId) {
		final Map<?, ?> syncActionRequest = ImmutableMap.<String, Object>builder()
				.put("action", "sync")
				.put(SnomedRf2Headers.FIELD_MODULE_ID, Concepts.MODULE_SCT_CORE)
				.put("commitComment", "Executed sync action on reference set member")
				.build();

		executeMemberAction(branchPath, memberId, syncActionRequest).statusCode(200);
	}

	private void checkReferencedComponentIds(List<String> conceptIds, String simpleRefSetId) {
		List<String> referencedComponentIds = getComponent(branchPath, SnomedComponentType.REFSET, simpleRefSetId, "members()")
				.statusCode(200)
				.extract().path("members.items.referencedComponent.id");

		assertEquals(conceptIds.size(), referencedComponentIds.size());
		assertTrue(referencedComponentIds.containsAll(conceptIds));
	}
}
