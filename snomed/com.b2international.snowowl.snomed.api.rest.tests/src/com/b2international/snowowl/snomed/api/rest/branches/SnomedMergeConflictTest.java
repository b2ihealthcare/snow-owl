/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest.branches;

import static com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants.ACCEPTABLE_ACCEPTABILITY_MAP;
import static com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants.SCT_API;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.assertBranchCanBeMerged;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.assertMergeJobFailsWithConflict;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.givenBranchWithPath;
import static com.b2international.snowowl.snomed.api.rest.SnomedMergeApiAssert.*;
import static com.b2international.snowowl.snomed.api.rest.SnomedRefSetApiAssert.updateMemberEffectiveTime;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.server.cdo.ConflictMapper;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.response.Response;

/**
 * @since 4.7
 */
public class SnomedMergeConflictTest extends AbstractSnomedApiTest {

	/*
	 * The test branch path is MAIN/<random UUID>/merge-conflict-test. Tests should affect <random UUID> and merge-conflict-test branches
	 */
	@Override
	public void setup() {
		super.setup();
		givenBranchWithPath(testBranchPath);
	}

	@Override
	protected IBranchPath createRandomBranchPath() {
		final IBranchPath parentBranch = super.createRandomBranchPath();
		givenBranchWithPath(parentBranch);
		return BranchPathUtils.createPath(parentBranch, "merge-conflict-test");
	}

	@Test
	public void changedInSourceAndTargetMergeConflict() {
		assertDescriptionCreated(testBranchPath, "D100", ACCEPTABLE_ACCEPTABILITY_MAP);
		assertDescriptionExists(testBranchPath, "D100");

		assertBranchCanBeMerged(testBranchPath, "Merge new description");

		assertDescriptionExists(testBranchPath, "D100");
		assertDescriptionExists(testBranchPath.getParent(), "D100");

		final Map<?, ?> changesOnParent = ImmutableMap.builder().put("caseSignificance", CaseSignificance.CASE_INSENSITIVE)
				.put("commitComment", "Changed case significance on parent").build();

		assertDescriptionCanBeUpdated(testBranchPath.getParent(), "D100", changesOnParent);

		final Map<?, ?> changesOnBranch = ImmutableMap.builder().put("caseSignificance", CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE)
				.put("commitComment", "Changed case significance on branch").build();

		assertDescriptionCanBeUpdated(testBranchPath, "D100", changesOnBranch);

		Response mergeResponse = assertMergeJobFailsWithConflict(testBranchPath.getParent(), testBranchPath, "commit");
		
		List<Map<String, Object>> conflicts = mergeResponse.jsonPath().getList("conflicts");
		
		assertEquals(1, conflicts.size());

		ImmutableMap<String, Object> conflict = ImmutableMap.<String, Object>builder()
				.put("sourceType", "Description")
				.put("sourceId", symbolicNameMap.get("D100"))
				.put("targetType", "Description")
				.put("targetId", symbolicNameMap.get("D100"))
				.put("message", String.format(ConflictMapper.CHANGED_IN_SOURCE_AND_TARGET_MESSAGE, symbolicNameMap.get("D100")))
				.put("changedSourceFeatures", singletonList("caseSignificance"))
				.put("changedTargetFeatures", singletonList("caseSignificance"))
				.build();
		
		assertThat(conflicts, hasItem(conflict));
	}

	@Test
	public void changedInSourceDetachedInTargetMergeConflict() {

		setup();

		assertRefsetMemberCreated(testBranchPath, "M1");

		assertRefSetMemberExists(testBranchPath, "M1");
		assertRefSetMemberNotExists(testBranchPath.getParent(), "M1");

		assertBranchCanBeMerged(testBranchPath, "merge branch");

		assertRefSetMemberExists(testBranchPath, "M1");
		assertRefSetMemberExists(testBranchPath.getParent(), "M1");

		givenAuthenticatedRequest(SCT_API).when().get("{path}/members/{memberId}", testBranchPath.getParent().getPath(), symbolicNameMap.get("M1"))
				.then().assertThat().body("effectiveTime", nullValue()).body("released", equalTo(false));

		String effectiveTime = EffectiveTimes.format(new Date(), DateFormats.SHORT);

		updateMemberEffectiveTime(testBranchPath.getParent(), symbolicNameMap.get("M1"), effectiveTime, true);

		givenAuthenticatedRequest(SCT_API).when().get("{path}/members/{memberId}", testBranchPath.getParent().getPath(), symbolicNameMap.get("M1"))
				.then().assertThat().body("effectiveTime", equalTo(effectiveTime)).body("released", equalTo(true));

		assertRefSetMemberCanBeDeleted(testBranchPath, "M1");

		assertRefSetMemberExists(testBranchPath.getParent(), "M1");
		assertRefSetMemberNotExists(testBranchPath, "M1");

		Response mergeResponse = assertMergeJobFailsWithConflict(testBranchPath.getParent(), testBranchPath, "commit");

		List<Map<String, Object>> conflicts = mergeResponse.jsonPath().getList("conflicts");
		
		assertEquals(1, conflicts.size());

		ImmutableMap<String, Object> conflict = ImmutableMap.<String, Object>builder()
				.put("sourceType", "SnomedRefSetMember")
				.put("sourceId", symbolicNameMap.get("M1"))
				.put("message", String.format(ConflictMapper.CHANGED_IN_SOURCE_DETACHED_IN_TARGET_MESSAGE, symbolicNameMap.get("M1")))
				.put("changedSourceFeatures", newArrayList("effectiveTime", "released"))
				.build();
		
		assertThat(conflicts, hasItem(conflict));
	}

	@Test
	public void changedInSourceDetachedInTargetNoMergeConflict() {

		setup();

		assertDescriptionCreated(testBranchPath, "D100", ACCEPTABLE_ACCEPTABILITY_MAP);
		assertDescriptionExists(testBranchPath, "D100");

		assertBranchCanBeMerged(testBranchPath, "Merge new description into parent branch");

		assertDescriptionExists(testBranchPath, "D100");
		assertDescriptionExists(testBranchPath.getParent(), "D100");

		assertDescriptionProperty(testBranchPath.getParent(), symbolicNameMap.get("D100"), "caseSignificance",
				CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE.name());

		final Map<?, ?> changesOnParent = ImmutableMap.builder().put("caseSignificance", CaseSignificance.CASE_INSENSITIVE)
				.put("commitComment", "Changed case significance on parent").build();

		assertDescriptionCanBeUpdated(testBranchPath.getParent(), "D100", changesOnParent);

		assertDescriptionProperty(testBranchPath.getParent(), symbolicNameMap.get("D100"), "caseSignificance",
				CaseSignificance.CASE_INSENSITIVE.name());

		assertDescriptionCanBeDeleted(testBranchPath, "D100");

		assertDescriptionNotExists(testBranchPath, "D100");
		assertDescriptionExists(testBranchPath.getParent(), "D100");

		assertBranchCanBeMerged(testBranchPath.getParent(), testBranchPath, "commit");

		assertDescriptionNotExists(testBranchPath, "D100");
		assertDescriptionExists(testBranchPath.getParent(), "D100");
	}
	
	@Test
	public void changedInTargetDetachedInSourceMergeConflict() {
		
		setup();
		
		assertDescriptionCreated(testBranchPath, "D100", ACCEPTABLE_ACCEPTABILITY_MAP);
		assertDescriptionExists(testBranchPath, "D100");

		assertBranchCanBeMerged(testBranchPath, "Merge new description into parent branch");

		assertDescriptionExists(testBranchPath, "D100");
		assertDescriptionExists(testBranchPath.getParent(), "D100");
		
		assertDescriptionProperty(testBranchPath, symbolicNameMap.get("D100"), "caseSignificance",
				CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE.name());
		
		final Map<?, ?> changesOnBranch = ImmutableMap.builder().put("caseSignificance", CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE)
				.put("commitComment", "Changed case significance on branch").build();

		assertDescriptionCanBeUpdated(testBranchPath, "D100", changesOnBranch);
		
		assertDescriptionProperty(testBranchPath, symbolicNameMap.get("D100"), "caseSignificance",
				CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE.name());
	
		assertDescriptionCanBeDeleted(testBranchPath.getParent(), "D100");
		
		assertDescriptionNotExists(testBranchPath.getParent(), "D100");
		assertDescriptionExists(testBranchPath, "D100");
		
		Response mergeResponse = assertMergeJobFailsWithConflict(testBranchPath.getParent(), testBranchPath, "merge");
		
		List<Map<String, Object>> conflicts = mergeResponse.jsonPath().getList("conflicts");
		
		assertEquals(1, conflicts.size());

		ImmutableMap<String, Object> conflict = ImmutableMap.<String, Object>builder()
				.put("targetId", symbolicNameMap.get("D100"))
				.put("targetType", "Description")
				.put("message", String.format(ConflictMapper.CHANGED_IN_TARGET_DETACHED_IN_SOURCE_MESSAGE, symbolicNameMap.get("D100")))
				.put("changedTargetFeatures", singletonList("caseSignificance"))
				.build();
		
		assertThat(conflicts, hasItem(conflict));
	}
	
	@Test
	public void addedInSourceAndTargetMergeConflict() {
		
		setup();
		
		assertDescriptionCreated(testBranchPath, "D200", ACCEPTABLE_ACCEPTABILITY_MAP);
		
		String descriptionId = symbolicNameMap.get("D200");
		
		assertDescriptionExists(testBranchPath, "D200");
		assertDescriptionNotExists(testBranchPath.getParent(), "D200");
		
		assertDescriptionCreatedWithId(testBranchPath.getParent(), "D300", descriptionId, ACCEPTABLE_ACCEPTABILITY_MAP);
		assertDescriptionExists(testBranchPath.getParent(), "D300");

		assertEquals(descriptionId, symbolicNameMap.get("D300"));
		
		Response mergeResponse = assertMergeJobFailsWithConflict(testBranchPath.getParent(), testBranchPath, "commit");
		
		List<Map<String, Object>> conflicts = mergeResponse.jsonPath().getList("conflicts");
		
		assertEquals(2, conflicts.size());

		ImmutableMap<String, Object> conflict1 = ImmutableMap.<String, Object>builder()
				.put("sourceType", "Description")
				.put("sourceId", descriptionId)
				.put("message", String.format(ConflictMapper.ADDED_IN_SOURCE_AND_TARGET_SOURCE_MESSAGE, "Description", descriptionId))
				.build();
		
		assertThat(conflicts, hasItem(conflict1));
		
		ImmutableMap<String, Object> conflict2 = ImmutableMap.<String, Object>builder()
				.put("targetType", "Description")
				.put("targetId", descriptionId)
				.put("message", String.format(ConflictMapper.ADDED_IN_SOURCE_AND_TARGET_TARGET_MESSAGE, "Description", descriptionId))
				.build();
		
		assertThat(conflicts, hasItem(conflict2));
	}
	
	@Test
	public void addedInTargetDetachedInSourceMergeConflict() {
		
		setup();
		
		assertConceptCreated(testBranchPath, "C1");

		assertBranchCanBeMerged(testBranchPath, "commit");
		
		assertConceptExists(testBranchPath, "C1");
		assertConceptExists(testBranchPath.getParent(), "C1");
		
		assertRelationshipCreated(testBranchPath, "R1", Concepts.ROOT_CONCEPT, MORPHOLOGIC_ABNORMALITY, symbolicNameMap.get("C1"));
		
		assertRelationshipExists(testBranchPath, "R1");
		assertRelationshipNotExists(testBranchPath.getParent(), "R1");
		
		assertConceptCanBeDeleted(testBranchPath.getParent(), "C1");
		assertConceptNotExists(testBranchPath.getParent(), "C1");
		
		Response mergeResponse = assertMergeJobFailsWithConflict(testBranchPath.getParent(), testBranchPath, "merge");
		
		List<Map<String, Object>> conflicts = mergeResponse.jsonPath().getList("conflicts");
		
		assertEquals(1, conflicts.size());

		ImmutableMap<String, Object> conflict = ImmutableMap.<String, Object>builder()
				.put("sourceType", "Concept")
				.put("sourceId", symbolicNameMap.get("C1"))
				.put("targetType", "Relationship")
				.put("targetId", symbolicNameMap.get("R1"))
				.put("message", String.format(ConflictMapper.ADDED_IN_TARGET_DETACHED_IN_SOURCE_MESSAGE, "Relationship", symbolicNameMap.get("R1"), "Concept", symbolicNameMap.get("C1")))
				.build();
		
		assertThat(conflicts, hasItem(conflict));
	}
	
	@Test
	public void addedInSourceDetachedInTargetMergeConflict() {
		
		setup();
		
		assertConceptCreated(testBranchPath, "C1");

		assertBranchCanBeMerged(testBranchPath, "commit");
		
		assertConceptExists(testBranchPath, "C1");
		assertConceptExists(testBranchPath.getParent(), "C1");
		
		assertRelationshipCreated(testBranchPath.getParent(), "R1", Concepts.ROOT_CONCEPT, MORPHOLOGIC_ABNORMALITY, symbolicNameMap.get("C1"));
		
		assertRelationshipExists(testBranchPath.getParent(), "R1");
		assertRelationshipNotExists(testBranchPath, "R1");
		
		assertConceptCanBeDeleted(testBranchPath, "C1");
		assertConceptNotExists(testBranchPath, "C1");
		
		Response mergeResponse = assertMergeJobFailsWithConflict(testBranchPath.getParent(), testBranchPath, "merge");
		
		List<Map<String, Object>> conflicts = mergeResponse.jsonPath().getList("conflicts");
		
		assertEquals(1, conflicts.size());

		ImmutableMap<String, Object> conflict = ImmutableMap.<String, Object>builder()
				.put("sourceType", "Relationship")
				.put("sourceId", symbolicNameMap.get("R1"))
				.put("targetType", "Concept")
				.put("targetId", symbolicNameMap.get("C1"))
				.put("message", String.format(ConflictMapper.ADDED_IN_SOURCE_DETACHED_IN_TARGET_MESSAGE, "Relationship", symbolicNameMap.get("R1"), "Concept", symbolicNameMap.get("C1")))
				.build();
		
		assertThat(conflicts, hasItem(conflict));
	}
	
}
