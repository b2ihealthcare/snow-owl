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
package com.b2international.snowowl.snomed.api.rest.branches;

import static com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants.ACCEPTABLE_ACCEPTABILITY_MAP;
import static com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants.PREFERRED_ACCEPTABILITY_MAP;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.assertBranchCanBeMerged;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.assertBranchCanBeRebased;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.assertMergeJobFails;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.assertMergeJobFailsWithConflict;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.givenBranchWithPath;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentHasProperty;
import static com.b2international.snowowl.snomed.api.rest.SnomedMergeApiAssert.*;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.merge.ConflictingAttribute;
import com.b2international.snowowl.core.merge.ConflictingAttributeImpl;
import com.b2international.snowowl.core.merge.MergeConflict.ConflictType;
import com.b2international.snowowl.core.merge.MergeConflictImpl;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.response.ValidatableResponse;

/**
 * @since 2.0
 */
public class SnomedMergeApiTest extends AbstractSnomedApiTest {

	@Override
	public void setup() {
		super.setup();
		givenBranchWithPath(testBranchPath);
	}
	
	@Override
	protected IBranchPath createRandomBranchPath() {
		// XXX make sure we nest the merge test cases under MAIN, so MAIN is not affected at all
		final IBranchPath parentBranch = super.createRandomBranchPath();
		givenBranchWithPath(parentBranch);
		return BranchPathUtils.createPath(parentBranch, "merge-test");
	}

	@Test
	public void mergeNewConceptForward() {
		assertConceptCreated(testBranchPath, "C1");
		assertConceptExists(testBranchPath, "C1");

		assertBranchCanBeMerged(testBranchPath, "Merge new concept");

		assertConceptExists(testBranchPath, "C1");
		assertConceptExists(testBranchPath.getParent(), "C1");
	}

	@Test
	public void mergeNewDescriptionForward() {
		assertDescriptionCreated(testBranchPath, "D1", ACCEPTABLE_ACCEPTABILITY_MAP);
		assertDescriptionExists(testBranchPath, "D1");

		assertBranchCanBeMerged(testBranchPath, "Merge new description");

		assertDescriptionExists(testBranchPath, "D1");
		assertDescriptionExists(testBranchPath.getParent(), "D1");
	}

	@Test
	public void mergeNewRelationshipForward() {
		assertRelationshipCreated(testBranchPath, "R1");
		assertRelationshipExists(testBranchPath, "R1");

		assertBranchCanBeMerged(testBranchPath, "Merge new relationship");

		assertRelationshipExists(testBranchPath, "R1");
		assertRelationshipExists(testBranchPath.getParent(), "R1");
	}

	@Test
	public void noMergeNewConceptDiverged() {
		assertConceptCreated(testBranchPath, "C1");
		assertConceptExists(testBranchPath, "C1");
		assertConceptNotExists(testBranchPath.getParent(), "C1");

		assertConceptCreated(testBranchPath.getParent(), "C2");
		assertConceptExists(testBranchPath.getParent(), "C2");
		assertConceptNotExists(testBranchPath, "C2");

		assertMergeJobFails(testBranchPath, testBranchPath.getParent(), "Merge new concept");

		assertConceptExists(testBranchPath, "C1");
		assertConceptNotExists(testBranchPath.getParent(), "C1");
		assertConceptExists(testBranchPath.getParent(), "C2");
		assertConceptNotExists(testBranchPath, "C2");
	}

	@Test
	public void noMergeNewDescriptionDiverged() {
		assertDescriptionCreated(testBranchPath, "D1", SnomedApiTestConstants.ACCEPTABLE_ACCEPTABILITY_MAP);
		assertDescriptionExists(testBranchPath, "D1");
		assertDescriptionNotExists(testBranchPath.getParent(), "D1");

		assertDescriptionCreated(testBranchPath.getParent(), "D2", SnomedApiTestConstants.ACCEPTABLE_ACCEPTABILITY_MAP);
		assertDescriptionExists(testBranchPath.getParent(), "D2");
		assertDescriptionNotExists(testBranchPath, "D2");

		assertMergeJobFails(testBranchPath, testBranchPath.getParent(), "Merge new description");

		assertDescriptionExists(testBranchPath, "D1");
		assertDescriptionNotExists(testBranchPath.getParent(), "D1");
		assertDescriptionExists(testBranchPath.getParent(), "D2");
		assertDescriptionNotExists(testBranchPath, "D2");
	}

	@Test
	public void noMergeNewRelationshipDiverged() {
		assertRelationshipCreated(testBranchPath, "R1");
		assertRelationshipExists(testBranchPath, "R1");
		assertRelationshipNotExists(testBranchPath.getParent(), "R1");

		assertRelationshipCreated(testBranchPath.getParent(), "R2");
		assertRelationshipExists(testBranchPath.getParent(), "R2");
		assertRelationshipNotExists(testBranchPath, "R2");

		assertMergeJobFails(testBranchPath, testBranchPath.getParent(), "Merge new relationship");

		assertRelationshipExists(testBranchPath, "R1");
		assertRelationshipNotExists(testBranchPath.getParent(), "R1");
		assertRelationshipExists(testBranchPath.getParent(), "R2");
		assertRelationshipNotExists(testBranchPath, "R2");
	}
	
	@Test
	public void mergeNewConceptToEmptyStaleBranch() {
		final IBranchPath b1 = createRandomBranchPath();
		final IBranchPath b2 = createRandomBranchPath();
		
		givenBranchWithPath(b1);
		givenBranchWithPath(b2);
		
		assertConceptCreated(b1, "C1");
		assertConceptExists(b1, "C1");
		
		assertConceptNotExists(b2, "C1");
		
		assertBranchCanBeMerged(b1, b2, "Merge b1 to b2");
		
		assertConceptExists(b1, "C1");
		assertConceptExists(b2, "C1");
	}
	
	@Test
	public void mergeNewConceptToStaleBranchWithChangesInBoth() {
		final IBranchPath b1 = createRandomBranchPath();
		givenBranchWithPath(b1);
		
		assertConceptCreated(b1, "C1");
		assertConceptExists(b1, "C1");
		
		assertConceptCreated(BranchPathUtils.createMainPath(), "C");
		assertConceptExists(BranchPathUtils.createMainPath(), "C");
		
		final IBranchPath b2 = createRandomBranchPath();
		givenBranchWithPath(b2);
		
		assertConceptExists(b2, "C");
		assertConceptNotExists(b1, "C");
		assertConceptNotExists(b2, "C1");
		
		assertBranchCanBeMerged(b1, b2, "Merge b1 to b2");
		
		assertConceptExists(b2, "C");
		assertConceptExists(b2, "C1");
	}
	
	@Test
	public void mergeNewDescriptionToEmptyStaleBranch() {
		final IBranchPath b1 = createRandomBranchPath();
		final IBranchPath b2 = createRandomBranchPath();
		
		givenBranchWithPath(b1);
		givenBranchWithPath(b2);
		
		assertDescriptionCreated(b1, "D", ACCEPTABLE_ACCEPTABILITY_MAP);
		assertDescriptionExists(b1, "D");

		assertBranchCanBeMerged(b1, b2, "Merge b1 to b2");

		assertDescriptionExists(b1, "D");
		assertDescriptionExists(b2, "D");
	}
	
	@Test
	public void mergeNewRelationshipToEmptyStaleBranch() {
		final IBranchPath b1 = createRandomBranchPath();
		final IBranchPath b2 = createRandomBranchPath();
		
		givenBranchWithPath(b1);
		givenBranchWithPath(b2);
		
		assertRelationshipCreated(b1, "R");
		assertRelationshipExists(b1, "R");

		assertBranchCanBeMerged(b1, b2, "Merge b1 to b2");

		assertRelationshipExists(b1, "R");
		assertRelationshipExists(b2, "R");
	}

	@Test
	public void noMergeNewDescriptionToConflictingStaleBranch() {
		final IBranchPath mainPath = BranchPathUtils.createMainPath();
		assertConceptCreated(mainPath, "C");
		assertConceptExists(mainPath, "C");
		
		final IBranchPath b1 = createRandomBranchPath();
		givenBranchWithPath(b1);
		
		final String conceptId = symbolicNameMap.get("C");
		assertDescriptionCreated(b1, "D1", conceptId, SnomedApiTestConstants.ACCEPTABLE_ACCEPTABILITY_MAP);
		assertDescriptionExists(b1, "D1");
		
		final IBranchPath b2 = createRandomBranchPath();
		givenBranchWithPath(b2);
		
		assertConceptCanBeDeleted(b2, "C");
		assertConceptNotExists(b2, "C");

		Response mergeResponse = assertMergeJobFailsWithConflict(b1, b2, "Merge b1 to b2");
		
		List<Map<String, Object>> conflicts = mergeResponse.jsonPath().getList("conflicts");
		
		assertEquals(1, conflicts.size());
		
		ImmutableMap<String, Object> conflict = ImmutableMap.<String, Object>builder()
				.put("componentId", conceptId)
				.put("componentType", "Concept")
				.put("type", ConflictType.CAUSES_MISSING_REFERENCE.name())
				.put("message", MergeConflictImpl.buildDefaultMessage(
						conceptId, 
						"Concept", 
						Collections.<ConflictingAttribute>emptyList(), 
						ConflictType.CAUSES_MISSING_REFERENCE))
				.build();
		
		assertThat(conflicts, hasItem(conflict));
		
		assertDescriptionNotExists(b2, "D1");
	}
	
	@Test
	public void noMergeNewRelationshipToConflictingStaleBranch() {
		final IBranchPath mainPath = BranchPathUtils.createMainPath();
		assertConceptCreated(mainPath, "C");
		assertConceptExists(mainPath, "C");
		
		final IBranchPath b1 = createRandomBranchPath();
		givenBranchWithPath(b1);
		assertConceptExists(b1, "C");
		
		final String conceptId = symbolicNameMap.get("C");
		assertRelationshipCreated(b1, "R", Concepts.ROOT_CONCEPT, conceptId);
		assertRelationshipExists(b1, "R");
		
		final IBranchPath b2 = createRandomBranchPath();
		givenBranchWithPath(b2);
		
		assertConceptCanBeDeleted(b2, "C");
		assertConceptNotExists(b2, "C");
		
		Response mergeResponse = assertMergeJobFailsWithConflict(b1, b2, "Merge b1 to b2");
		
		List<Map<String, Object>> conflicts = mergeResponse.jsonPath().getList("conflicts");
		
		assertEquals(1, conflicts.size());
		
		ImmutableMap<String, Object> conflict = ImmutableMap.<String, Object>builder()
				.put("componentId", conceptId)
				.put("componentType", "Concept")
				.put("type", ConflictType.CAUSES_MISSING_REFERENCE.name())
				.put("message", MergeConflictImpl.buildDefaultMessage(
						conceptId, 
						"Concept", 
						Collections.<ConflictingAttribute>emptyList(), 
						ConflictType.CAUSES_MISSING_REFERENCE))
				.build();

		assertThat(conflicts, hasItem(conflict));
		
		assertRelationshipNotExists(b2, "R");
	}

	@Test
	public void rebaseNewConceptDiverged() {
		assertConceptCreated(testBranchPath, "C1");
		assertConceptExists(testBranchPath, "C1");
		assertConceptNotExists(testBranchPath.getParent(), "C1");

		assertConceptCreated(testBranchPath.getParent(), "C2");
		assertConceptExists(testBranchPath.getParent(), "C2");
		assertConceptNotExists(testBranchPath,"C2");

		assertBranchCanBeRebased(testBranchPath, "Rebase new concept");

		assertConceptExists(testBranchPath,"C1");
		assertConceptNotExists(testBranchPath.getParent(), "C1");
		assertConceptExists(testBranchPath.getParent(), "C2");
		assertConceptExists(testBranchPath, "C2"); // C2 from the parent becomes visible on the test branch after rebasing
	}

	@Test
	public void rebaseNewDescriptionDiverged() {
		assertDescriptionCreated(testBranchPath, "D1", ACCEPTABLE_ACCEPTABILITY_MAP);
		assertDescriptionExists(testBranchPath, "D1");
		assertDescriptionNotExists(testBranchPath.getParent(), "D1");

		assertDescriptionCreated(testBranchPath.getParent(), "D2", ACCEPTABLE_ACCEPTABILITY_MAP);
		assertDescriptionExists(testBranchPath.getParent(), "D2");
		assertDescriptionNotExists(testBranchPath,"D2");

		assertBranchCanBeRebased(testBranchPath, "Rebase new description");

		assertDescriptionExists(testBranchPath,"D1");
		assertDescriptionNotExists(testBranchPath.getParent(), "D1");
		assertDescriptionExists(testBranchPath.getParent(), "D2");
		assertDescriptionExists(testBranchPath, "D2");
	}

	@Test
	public void rebaseNewRelationshipDiverged() {
		assertRelationshipCreated(testBranchPath, "R1");
		assertRelationshipExists(testBranchPath, "R1");
		assertRelationshipNotExists(testBranchPath.getParent(), "R1");

		assertRelationshipCreated(testBranchPath.getParent(), "R2");
		assertRelationshipExists(testBranchPath.getParent(), "R2");
		assertRelationshipNotExists(testBranchPath,"R2");

		assertBranchCanBeRebased(testBranchPath, "Rebase new concept");

		assertRelationshipExists(testBranchPath,"R1");
		assertRelationshipNotExists(testBranchPath.getParent(), "R1");
		assertRelationshipExists(testBranchPath.getParent(), "R2");
		assertRelationshipExists(testBranchPath, "R2");
	}

	@Test
	public void rebaseNewConceptStale() {
		IBranchPath branchPath = createNestedBranch(testBranchPath, "A", "B");

		assertConceptCreated(branchPath, "CB1");
		assertConceptExists(branchPath, "CB1");
		assertConceptNotExists(branchPath.getParent(), "CB1");
		assertConceptNotExists(branchPath.getParent().getParent(), "CB1");

		assertConceptCreated(branchPath.getParent(), "CA");
		assertConceptExists(branchPath.getParent(), "CA");
		assertConceptNotExists(branchPath, "CA");
		assertConceptNotExists(branchPath.getParent().getParent(), "CA");

		assertConceptCreated(branchPath.getParent().getParent(), "Cm");
		assertConceptExists(branchPath.getParent().getParent(), "Cm");
		assertConceptNotExists(branchPath, "Cm");
		assertConceptNotExists(branchPath.getParent(), "Cm");

		assertBranchCanBeRebased(branchPath.getParent(), "Rebase test/A");

		assertConceptExists(branchPath, "CB1");
		assertConceptExists(branchPath.getParent(), "CA");
		assertConceptExists(branchPath.getParent().getParent(), "Cm");

		assertConceptExists(branchPath.getParent(), "Cm"); // The rebase made the concept on test visible to test/A
		assertConceptNotExists(branchPath, "CA"); // test/A/B is left behind, and still doesn't know about the other concepts
		assertConceptNotExists(branchPath, "Cm");

		assertConceptCreated(branchPath, "CB2");
		assertConceptExists(branchPath, "CB2");
		assertConceptNotExists(branchPath.getParent(), "CB2");
		assertConceptNotExists(branchPath.getParent().getParent(), "CB2");

		assertBranchCanBeRebased(branchPath, "Rebase test/A/B");

		assertConceptExists(branchPath, "CB1");
		assertConceptExists(branchPath, "CB2");
		assertConceptExists(branchPath.getParent(), "CA");
		assertConceptExists(branchPath.getParent().getParent(), "Cm");

		assertConceptExists(branchPath.getParent(), "Cm"); // The rebase made the concept on test and test/A visible to test/A/B
		assertConceptExists(branchPath, "CA");
	}

	@Test
	public void noRebaseNewPreferredTerm() {
		assertDescriptionCreated(testBranchPath, "D1", PREFERRED_ACCEPTABILITY_MAP);
		assertDescriptionExists(testBranchPath, "D1");
		assertDescriptionNotExists(testBranchPath.getParent(), "D1");

		ValidatableResponse response = SnomedComponentApiAssert.assertDescriptionExists(testBranchPath, symbolicNameMap.get("D1"), "members()");
		List<String> memberIds = response.and().extract().body().path("members.items.id");
		assertEquals(memberIds.size(), 1);
		String memberId = memberIds.get(0);

		List<String> acceptabilityIds = response.and().extract().body().path("members.items.acceptabilityId");
		assertEquals(acceptabilityIds.size(), 1);
		String acceptabilityId = acceptabilityIds.get(0);
		
		assertDescriptionCreated(testBranchPath.getParent(), "D2", PREFERRED_ACCEPTABILITY_MAP);
		assertDescriptionExists(testBranchPath.getParent(), "D2");
		assertDescriptionNotExists(testBranchPath,"D2");

		Response mergeResponse = assertMergeJobFailsWithConflict(testBranchPath.getParent(), testBranchPath, "Rebase new preferred term");

		List<Map<String, Object>> conflicts = mergeResponse.jsonPath().getList("conflicts");
		
		assertEquals(1, conflicts.size());
		
		ConflictingAttribute attribute = ConflictingAttributeImpl.builder()
				.property("acceptabilityId")
				.value(acceptabilityId)
				.build();

		ImmutableMap<String, Object> conflict = ImmutableMap.<String, Object>builder()
				.put("componentId", memberId)
				.put("componentType", "SnomedLanguageRefSetMember")
				.put("conflictingAttributes", createAttributesMap(attribute))
				.put("type", ConflictType.CONFLICTING_CHANGE.name())
				.put("message", MergeConflictImpl.buildDefaultMessage(
						memberId, 
						"SnomedLanguageRefSetMember", 
						Collections.<ConflictingAttribute>singletonList(attribute), 
						ConflictType.CONFLICTING_CHANGE))
				.build();
		
		assertThat(conflicts, hasItem(conflict));
		
		assertDescriptionExists(testBranchPath, "D1");
		assertDescriptionNotExists(testBranchPath.getParent(), "D1");
		assertDescriptionExists(testBranchPath.getParent(), "D2");
		assertDescriptionNotExists(testBranchPath,"D2"); // D2 did not become visible because the rebase was rejected
	}

	private void assertDescriptionChangesConflict(final Map<?, ?> changesOnParent, final Map<?, ?> changesOnBranch) {
		mergeNewDescriptionForward();

		assertDescriptionCanBeUpdated(testBranchPath.getParent(), "D1", changesOnParent);
		assertDescriptionCanBeUpdated(testBranchPath, "D1", changesOnBranch);

		assertMergeJobFails(testBranchPath.getParent(), testBranchPath, "Rebase conflicting description change");
	}

	@Test
	public void noRebaseConflictingDescription() {
		final Map<?, ?> changesOnParent = ImmutableMap.builder()
				.put("caseSignificance", CaseSignificance.CASE_INSENSITIVE)
				.put("commitComment", "Changed case significance on parent")
				.build();

		final Map<?, ?> changesOnBranch = ImmutableMap.builder()
				.put("caseSignificance", CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE)
				.put("commitComment", "Changed case significance on branch")
				.build();

		assertDescriptionChangesConflict(changesOnParent, changesOnBranch);
	}

	@Test
	public void noRebaseConflictingDescriptionMultipleChanges() {
		final Map<?, ?> changesOnParent = ImmutableMap.builder()
				.put("caseSignificance", CaseSignificance.CASE_INSENSITIVE)
				.put("moduleId", "900000000000013009")
				.put("commitComment", "Changed case significance and module on parent")
				.build();

		final Map<?, ?> changesOnBranch = ImmutableMap.builder()
				.put("caseSignificance", CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE)
				.put("moduleId", "900000000000443000")
				.put("commitComment", "Changed case significance and module on branch")
				.build();

		assertDescriptionChangesConflict(changesOnParent, changesOnBranch);
	}

	@Test
	public void noRebaseChangedConceptOnBranchDeletedOnParent() {
		mergeNewConceptForward();

		final Map<?, ?> changeOnBranch = ImmutableMap.builder()
				.put("definitionStatus", DefinitionStatus.FULLY_DEFINED)
				.put("commitComment", "Changed definition status on branch")
				.build();

		assertConceptCanBeDeleted(testBranchPath.getParent(), "C1");
		assertConceptCanBeUpdated(testBranchPath, "C1", changeOnBranch);

		assertMergeJobFails(testBranchPath.getParent(), testBranchPath, "Rebase conflicting concept deletion");
	}
	
	@Test
	public void noRebaseInactivatedConceptOnBranchNewRelationshipOnParent() {
		mergeNewConceptForward();
		
		final Map<?, ?> changeOnBranch = ImmutableMap.builder()
				.put("active", false)
				.put("commitComment", "Inactivated concept on branch")
				.build();
		
		assertConceptCanBeUpdated(testBranchPath, "C1", changeOnBranch);

		final Map<?, ?> changeOnParent = ImmutableMap.builder()
				.put("sourceId", symbolicNameMap.get("C1"))
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("typeId", Concepts.IS_A)
				.put("destinationId", "49755003") // Morphologic abnormality
				.put("commitComment", "New relationship")
				.build();

		assertComponentCreated(testBranchPath.getParent(), "R1", SnomedComponentType.RELATIONSHIP, changeOnParent);
		
		Response mergeResponse = assertMergeJobFailsWithConflict(testBranchPath.getParent(), testBranchPath, "Rebase conflicting concept inactivation");
		
		List<Map<String, Object>> conflicts = mergeResponse.jsonPath().getList("conflicts");
		
		assertEquals(1, conflicts.size());
		
		ConflictingAttribute attribute = ConflictingAttributeImpl.builder()
				.property("sourceId")
				.value(symbolicNameMap.get("C1"))
				.build();
		
		ImmutableMap<String, Object> conflict = ImmutableMap.<String, Object>builder()
				.put("componentId", symbolicNameMap.get("R1"))
				.put("componentType", "Relationship")
				.put("conflictingAttributes", createAttributesMap(attribute))
				.put("type", ConflictType.HAS_INACTIVE_REFERENCE.name())
				.put("message", MergeConflictImpl.buildDefaultMessage(
						symbolicNameMap.get("R1"), 
						"Relationship",
						Collections.<ConflictingAttribute>singletonList(attribute),
						ConflictType.HAS_INACTIVE_REFERENCE))
				.build();
		
		assertThat(conflicts, hasItem(conflict));
		
		// If changes could not be taken over, C1 will be active on the test branch
		SnomedComponentApiAssert.assertComponentActive(testBranchPath, SnomedComponentType.CONCEPT, symbolicNameMap.get("C1"), false);
	}

	@Test
	public void rebaseChangedConceptOnParentDeletedOnBranch() {
		mergeNewConceptForward();

		final Map<?, ?> changeOnParent = ImmutableMap.builder()
				.put("definitionStatus", DefinitionStatus.FULLY_DEFINED)
				.put("commitComment", "Changed definition status on parent")
				.build();

		assertConceptCanBeUpdated(testBranchPath.getParent(), "C1", changeOnParent);
		assertConceptCanBeDeleted(testBranchPath, "C1");

		assertBranchCanBeRebased(testBranchPath, "Rebase concept deletion");

		assertConceptExists(testBranchPath.getParent(), "C1");
		assertConceptNotExists(testBranchPath, "C1");
	}

	@Test
	public void rebaseAndMergeChangedConceptOnParentDeletedOnBranch() {
		rebaseChangedConceptOnParentDeletedOnBranch();

		assertBranchCanBeMerged(testBranchPath, "Merge concept deletion back to MAIN");

		assertConceptNotExists(testBranchPath.getParent(), "C1");
		assertConceptNotExists(testBranchPath, "C1");
	}

	@Test
	public void rebaseAndMergeChangedDescriptionMultipleChanges() {
		mergeNewDescriptionForward();

		assertDescriptionCreated(testBranchPath.getParent(), "D2", SnomedApiTestConstants.ACCEPTABLE_ACCEPTABILITY_MAP);

		final Map<?, ?> changesOnBranch = ImmutableMap.builder()
				.put("caseSignificance", CaseSignificance.CASE_INSENSITIVE)
				.put("moduleId", "900000000000013009")
				.put("commitComment", "Changed case significance and module on branch")
				.build();

		assertDescriptionCanBeUpdated(testBranchPath, "D1", changesOnBranch);

		assertBranchCanBeRebased(testBranchPath, "Rebase description update");
		assertBranchCanBeMerged(testBranchPath, "Merge description update");

		assertDescriptionExists(testBranchPath.getParent(), "D1");
		assertDescriptionExists(testBranchPath.getParent(), "D2");

		assertComponentHasProperty(testBranchPath.getParent(), SnomedComponentType.DESCRIPTION, symbolicNameMap.get("D1"), "caseSignificance", CaseSignificance.CASE_INSENSITIVE.name());
		assertComponentHasProperty(testBranchPath.getParent(), SnomedComponentType.DESCRIPTION, symbolicNameMap.get("D1"), "moduleId", "900000000000013009");
	}

	@Test
	public void rebaseAndMergeNewDescriptionBothDeleted() {
		mergeNewDescriptionForward();

		assertDescriptionCreated(testBranchPath.getParent(), "D2", SnomedApiTestConstants.ACCEPTABLE_ACCEPTABILITY_MAP);
		assertDescriptionCanBeDeleted(testBranchPath, "D1");
		assertDescriptionCanBeDeleted(testBranchPath.getParent(), "D1");

		/* 
		 * The rebase sees that the same thing has already happened on the parent branch, and does not 
		 * add an empty commit to the new instance of the child; it will be in UP_TO_DATE state and can 
		 * not be promoted.
		 */
		assertBranchCanBeRebased(testBranchPath, "Rebase description dual deletion");
		assertMergeJobFails(testBranchPath, testBranchPath.getParent(), "Merge description dual deletion");

		assertDescriptionNotExists(testBranchPath, "D1");
		assertDescriptionNotExists(testBranchPath.getParent(), "D1");
		assertDescriptionExists(testBranchPath, "D2");
		assertDescriptionExists(testBranchPath.getParent(), "D2");
	}
	
	@Test
	public void rebaseOverReusedRelationshipId() {
		assertRelationshipCreated(testBranchPath.getParent(), "R1");
		assertRelationshipExists(testBranchPath.getParent(), "R1");
		final String relationshipId = symbolicNameMap.get("R1");
		
		assertBranchCanBeRebased(testBranchPath, "Rebase after relationship creation");
		assertComponentCanBeDeleted(testBranchPath.getParent(), "R1", SnomedComponentType.RELATIONSHIP);
		
		final Map<?, ?> requestBody = ImmutableMap.builder()
				.put("sourceId", Concepts.ROOT_CONCEPT)
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("typeId", "116676008") // Associated morphology
				.put("destinationId", "404684003") // ??? (different from morphologic abnormality)
				.put("id", relationshipId)
				.put("commitComment", "New relationship with same ID")
				.build();

		assertComponentCreated(testBranchPath.getParent(), "new-R1", SnomedComponentType.RELATIONSHIP, requestBody);
		
		// Different relationships before rebase
		assertComponentHasProperty(testBranchPath.getParent(), SnomedComponentType.RELATIONSHIP, relationshipId, "destinationId", "404684003");
		assertComponentHasProperty(testBranchPath, SnomedComponentType.RELATIONSHIP, relationshipId, "destinationId", "49755003");
		
		assertBranchCanBeRebased(testBranchPath, "Rebase after new relationship creation");
		
		// Same relationships after rebase
		assertComponentHasProperty(testBranchPath.getParent(), SnomedComponentType.RELATIONSHIP, relationshipId, "destinationId", "404684003");
		assertComponentHasProperty(testBranchPath, SnomedComponentType.RELATIONSHIP, relationshipId, "destinationId", "404684003");
	}
	
	@Test
	public void rebaseTwoNewTextDefinitionsWithDifferentAcceptabilityMapShouldNotConflict() throws Exception {
		// create two new text definitions with different lang. acceptability on testBranchPath
		assertDescriptionCreated(testBranchPath, "D1", Concepts.TEXT_DEFINITION, ImmutableMap.of(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED, Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.ACCEPTABLE));
		assertDescriptionCreated(testBranchPath, "D2", Concepts.TEXT_DEFINITION, ImmutableMap.of(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.ACCEPTABLE, Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.PREFERRED));
		
		// create change on parent
		assertRelationshipCreated(testBranchPath.getParent(), "R1");
		
		assertBranchCanBeRebased(testBranchPath, "Rebase two new text definitions");
		assertDescriptionExists(testBranchPath, "D1");
		assertDescriptionExists(testBranchPath, "D2");
	}
	
}
