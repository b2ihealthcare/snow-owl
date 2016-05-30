/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.MODULE_SCT_CORE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.ROOT_CONCEPT;
import static com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants.PREFERRED_ACCEPTABILITY_MAP;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.givenBranchWithPath;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentCreated;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.createRefSetMemberRequestBody;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.createRefSetRequestBody;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.getComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.givenConceptRequestBody;
import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.b2international.snowowl.test.commons.rest.RestExtensions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.jayway.restassured.http.ContentType;

/**
 * @since 4.5
 */
public class SnomedRefSetBulkApiTest extends AbstractSnomedApiTest {

	private String refSetId;

	@Test
	public void bulkCreateSimpleMembers() throws Exception {
		// create branch
		givenBranchWithPath(testBranchPath);
		
		int numberOfConcepts = 3;
		final List<String> createdConcepts = newArrayList();
		// create three concepts
		for (int i = 0; i < numberOfConcepts; i++) {
			// create concept
			final Map<?, ?> conceptReq = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
			final String createdConceptId = assertComponentCreated(testBranchPath, SnomedComponentType.CONCEPT, conceptReq);
			createdConcepts.add(createdConceptId);
		}
		
		// create simple type refset
		final Map<String,Object> requestBody = createRefSetRequestBody(SnomedRefSetType.SIMPLE, SnomedTerminologyComponentConstants.CONCEPT, Concepts.REFSET_SIMPLE_TYPE);
		refSetId = assertComponentCreated(testBranchPath, SnomedComponentType.REFSET, requestBody);

		// create bulk request with 3 create requests
		final Collection<Map<String, Object>> bulkRequests = newArrayList();
		for (int i = 0; i < numberOfConcepts; i++) {
			final Map<String, Object> body = createRefSetMemberRequestBody(createdConcepts.get(i), refSetId);
			// wrap main create member body into a action wrapper
			bulkRequests.add(ImmutableMap.<String, Object>builder().put("action", "create").putAll(body).build());
		}
		final Map<String, Object> bulk = ImmutableMap.<String, Object>of("requests", bulkRequests, "commitComment", "Add three members to refset");
		
		RestExtensions
			.givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
			.accept(ContentType.JSON)
			.contentType(ContentType.JSON)
			.body(bulk)
			.put("/{path}/refsets/{id}/members", testBranchPath.getPath(), refSetId)
			.then()
			.log().ifValidationFails()
			.statusCode(204);
		
		// verify that new refset has three members
		getComponent(testBranchPath, SnomedComponentType.REFSET, refSetId, "members()")
			.then()
			.statusCode(200)
			.and()
			.body("members.items.referencedComponent.id", CoreMatchers.hasItems(createdConcepts.toArray()));
	}
	
	@Test
	public void bulkUpdateDeleteMembers() throws Exception {
		// create three members
		bulkCreateSimpleMembers();
		// get current members
		final Collection<String> memberIds = getComponent(testBranchPath, SnomedComponentType.REFSET, refSetId, "members()")
			.body().path("members.items.id");
		
		final String firstMemberId = Iterables.get(memberIds, 0);
		final String secondMemberId = Iterables.get(memberIds, 1);
		
		memberIds.remove(secondMemberId);
		
		// create bulk update with one inactivation and one delete
		final Collection<Map<String, Object>> bulkRequests = newArrayList();
		bulkRequests.add(ImmutableMap.<String, Object>of("action", "update", "memberId", firstMemberId, "active", false));
		bulkRequests.add(ImmutableMap.<String, Object>of("action", "delete", "memberId", secondMemberId));
		final Map<String, Object> bulk = ImmutableMap.<String, Object>of("requests", bulkRequests, "commitComment", "Removed and inactivated members");
		
		RestExtensions
			.givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
			.accept(ContentType.JSON)
			.contentType(ContentType.JSON)
			.body(bulk)
			.put("/{path}/refsets/{id}/members", testBranchPath.getPath(), refSetId)
			.then()
			.log().ifValidationFails()
			.statusCode(204);
		
		// verify that new refset has only two members, and one is inactive
		getComponent(testBranchPath, SnomedComponentType.REFSET, refSetId, "members()")
			.then()
			.body("members.items.id", CoreMatchers.hasItems(memberIds.toArray()))
			.and()
			.body("members.items.active", CoreMatchers.hasItems(true, false));
	}
	
	@Test
	public void bulkForceUpdateAndForceDeleteMembers() throws Exception {
		bulkCreateSimpleMembers();
		
		// get current members
		final Collection<String> memberIds = getComponent(testBranchPath, SnomedComponentType.REFSET, refSetId, "members()")
			.body().path("members.items.id");
		
		final String firstMemberId = Iterables.get(memberIds, 0);
		final String secondMemberId = Iterables.get(memberIds, 1);
		
		// manually release secondMemberId before force deleting it
		SnomedRefSetMemberApiTest.updateMemberEffectiveTime(testBranchPath, secondMemberId, "20160201", true);
		
		// create bulk update with one force deletion and one force update
		final Collection<Map<String, Object>> bulkRequests = newArrayList();
		bulkRequests.add(ImmutableMap.<String, Object>of("action", "update", "memberId", firstMemberId, "effectiveTime", "20160201", "force", true));
		bulkRequests.add(ImmutableMap.<String, Object>of("action", "delete", "memberId", secondMemberId, "force", true));
		final Map<String, Object> bulk = ImmutableMap.<String, Object>of("requests", bulkRequests, "commitComment", "Forcefully deleted/updated members");
		
		// execute bulk update with force deletion and force update
		RestExtensions
			.givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
			.accept(ContentType.JSON)
			.contentType(ContentType.JSON)
			.body(bulk)
			.put("/{path}/refsets/{id}/members", testBranchPath.getPath(), refSetId)
			.then()
			.log().ifValidationFails()
			.statusCode(204);
		
		// we've deleted the secondMemberId, remove it from the memberIds collection before assertions
		memberIds.remove(secondMemberId);
		// verify that new refset has only two members, and one is released
		getComponent(testBranchPath, SnomedComponentType.REFSET, refSetId, "members()")
			.then()
			.body("members.items.id", CoreMatchers.hasItems(memberIds.toArray()))
			.and()
			.body("members.items.active", CoreMatchers.hasItems(true, true))
			.and()
			.body("members.items.effectiveTime", CoreMatchers.hasItems("20160201", null));
	}

}
