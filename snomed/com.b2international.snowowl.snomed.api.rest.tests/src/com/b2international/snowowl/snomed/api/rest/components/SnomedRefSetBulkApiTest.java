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

import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedRefSetRestRequests.bulkUpdateMembers;
import static com.b2international.snowowl.snomed.api.rest.SnomedRefSetRestRequests.updateRefSetMemberEffectiveTime;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewConcept;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewRefSet;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createRefSetMemberRequestBody;
import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * @since 4.5
 */
public class SnomedRefSetBulkApiTest extends AbstractSnomedApiTest {

	@Test
	public void bulkCreateSimpleMembers() throws Exception {
		List<String> conceptIds = newArrayList();
		String refSetId = createMembers(3, conceptIds);

		getComponent(branchPath, SnomedComponentType.REFSET, refSetId, "members()")
		.statusCode(200)
		.body("members.items.referencedComponent.id", hasItems(conceptIds.toArray()));
	}

	@Test
	public void bulkUpdateDeleteMembers() throws Exception {
		List<String> conceptIds = newArrayList();
		String refSetId = createMembers(3, conceptIds);

		Collection<String> memberIds = getComponent(branchPath, SnomedComponentType.REFSET, refSetId, "members()")
				.statusCode(200)
				.extract().path("members.items.id");

		String firstMemberId = Iterables.get(memberIds, 0);
		String secondMemberId = Iterables.get(memberIds, 1);
		memberIds.remove(secondMemberId);

		Map<?, ?> updateRequest = ImmutableMap.<String, Object>builder()
				.put("action", "update")
				.put("memberId", firstMemberId)
				.put("active", false)
				.build();

		Map<?, ?> deleteRequest = ImmutableMap.<String, Object>builder()
				.put("action", "delete")
				.put("memberId", secondMemberId)
				.build();

		List<Map<?, ?>> requests = newArrayList(updateRequest, deleteRequest);
		Map<?, ?> bulkRequest = ImmutableMap.<String, Object>builder()
				.put("requests", requests)
				.put("commitComment", "Removed and inactivated members")
				.build();

		bulkUpdateMembers(branchPath, refSetId, bulkRequest).statusCode(204);

		Collection<SnomedRefSetMember> members = getComponent(branchPath, SnomedComponentType.REFSET, refSetId, "members()")
				.statusCode(200)
				.body("members.items.id", hasItems(memberIds.toArray()))
				.body("members.items.active", hasItems(true, false))
				.extract().path("members.items");

		assertEquals(2, members.size());
	}

	@Test
	public void bulkForceUpdateAndForceDeleteMembers() throws Exception {
		List<String> conceptIds = newArrayList();
		String refSetId = createMembers(3, conceptIds);

		Collection<String> memberIds = getComponent(branchPath, SnomedComponentType.REFSET, refSetId, "members()")
				.statusCode(200)
				.extract().path("members.items.id");

		String firstMemberId = Iterables.get(memberIds, 0);
		String secondMemberId = Iterables.get(memberIds, 1);
		memberIds.remove(secondMemberId);

		updateRefSetMemberEffectiveTime(branchPath, secondMemberId, EffectiveTimes.parse("20160201", DateFormats.SHORT));

		Map<?, ?> forceUpdateRequest = ImmutableMap.<String, Object>builder()
				.put("action", "update")
				.put("memberId", firstMemberId)
				.put("effectiveTime", "20160201")
				.put("force", true)
				.build();

		Map<?, ?> forceDeleteRequest = ImmutableMap.<String, Object>builder()
				.put("action", "delete")
				.put("memberId", secondMemberId)
				.put("force", true)
				.build();

		List<Map<?, ?>> requests = newArrayList(forceUpdateRequest, forceDeleteRequest);
		Map<?, ?> bulkRequest = ImmutableMap.<String, Object>builder()
				.put("requests", requests)
				.put("commitComment", "Forcefully deleted/updated members")
				.build();

		bulkUpdateMembers(branchPath, refSetId, bulkRequest).statusCode(204);

		Collection<SnomedRefSetMember> members = getComponent(branchPath, SnomedComponentType.REFSET, refSetId, "members()")
				.statusCode(200)
				.body("members.items.id", hasItems(memberIds.toArray()))
				.body("members.items.active", hasItems(true, true))
				.body("members.items.effectiveTime", CoreMatchers.hasItems("20160201", null))
				.extract().path("members.items");

		assertEquals(2, members.size());
	}

	private String createMembers(int numberOfConcepts, List<String> conceptIds) {
		String refSetId = createNewRefSet(branchPath);

		for (int i = 0; i < numberOfConcepts; i++) {
			conceptIds.add(createNewConcept(branchPath));
		}

		List<Map<?, ?>> requests = newArrayList();
		for (int i = 0; i < numberOfConcepts; i++) {
			Map<?, ?> createRequest = ImmutableMap.<String, Object>builder()
					.put("action", "create")
					.putAll(createRefSetMemberRequestBody(refSetId, conceptIds.get(i)).build())
					.build();

			requests.add(createRequest);
		}

		Map<?, ?> bulkRequest = ImmutableMap.<String, Object>builder()
				.put("requests", requests)
				.put("commitComment", "Add three members to refset")
				.build();

		bulkUpdateMembers(branchPath, refSetId, bulkRequest).statusCode(204);
		return refSetId;
	}

}
