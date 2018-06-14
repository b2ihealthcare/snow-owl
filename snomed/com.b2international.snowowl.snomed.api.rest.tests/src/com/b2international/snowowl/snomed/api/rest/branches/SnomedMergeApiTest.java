/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.snomed.api.rest.CodeSystemVersionRestRequests.getNextAvailableEffectiveDate;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingRestRequests.createBranch;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingRestRequests.getBranchChildren;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.createComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.deleteComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.updateComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedMergingRestRequests.createMerge;
import static com.b2international.snowowl.snomed.api.rest.SnomedRefSetRestRequests.updateRefSetComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedRefSetRestRequests.updateRefSetMemberEffectiveTime;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.changeCaseSignificance;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.changeRelationshipGroup;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createInactiveConcept;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewConcept;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewDescription;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewRefSetMember;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewRelationship;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewTextDefinition;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.merge;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.reactivateConcept;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.startsWith;

import java.util.Calendar;
import java.util.Map;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import com.b2international.index.revision.RevisionBranch;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.merge.Merge;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.google.common.collect.ImmutableMap;

/**
 * Contains test cases for the branch rebase and merge functionality.
 * <p>
 * All scenarios that result in a content conflict should be placed in {@link SnomedMergeConflictTest} instead.
 * 
 * @since 2.0
 */
public class SnomedMergeApiTest extends AbstractSnomedApiTest {

	@After
	public void noTempBranchVisibleAfter() {
		getBranchChildren(branchPath).statusCode(200).body("items.name", not(hasItem(startsWith(RevisionBranch.TEMP_PREFIX))));
	}

	private static void rebaseConceptDeletionOverChange(IBranchPath parentPath, IBranchPath childPath, String conceptId) {
		Map<?, ?> changeOnParent = ImmutableMap.builder()
				.put("definitionStatus", DefinitionStatus.FULLY_DEFINED)
				.put("commitComment", "Changed definition status on parent")
				.build();

		updateComponent(parentPath, SnomedComponentType.CONCEPT, conceptId, changeOnParent).statusCode(204);
		deleteComponent(childPath, SnomedComponentType.CONCEPT, conceptId, false).statusCode(204);

		merge(parentPath, childPath, "Rebased concept deletion over concept change").body("status", equalTo(Merge.Status.COMPLETED.name()));
	}

	@Test
	public void mergeNewConceptForward() {
		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		String conceptId = createNewConcept(a);
		merge(a, branchPath, "Merged new concept from child branch").body("status", equalTo(Merge.Status.COMPLETED.name()));

		getComponent(a, SnomedComponentType.CONCEPT, conceptId).statusCode(200);
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId).statusCode(200);
	}

	@Test
	public void mergeNewDescriptionForward() {
		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		String descriptionId = createNewDescription(a);
		merge(a, branchPath, "Merged new description from child branch").body("status", equalTo(Merge.Status.COMPLETED.name()));

		getComponent(a, SnomedComponentType.DESCRIPTION, descriptionId).statusCode(200);
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId).statusCode(200);
	}

	@Test
	public void mergeNewRelationshipForward() {
		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		String relationshipId = createNewRelationship(a);
		merge(a, branchPath, "Merged new relationship from child branch").body("status", equalTo(Merge.Status.COMPLETED.name()));

		getComponent(a, SnomedComponentType.RELATIONSHIP, relationshipId).statusCode(200);
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId).statusCode(200);
	}

	@Test
	public void noMergeWithNonExistentReview() {
		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		createNewConcept(a);
		createMerge(a, branchPath, "Merged new concept from child branch with non-existent review ID", "non-existent-id").statusCode(400);
	}

	@Test
	public void noMergeNewConceptDiverged() {
		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		String concept1Id = createNewConcept(a);
		String concept2Id = createNewConcept(branchPath);

		getComponent(branchPath, SnomedComponentType.CONCEPT, concept1Id).statusCode(404);
		getComponent(a, SnomedComponentType.CONCEPT, concept1Id).statusCode(200);

		getComponent(branchPath, SnomedComponentType.CONCEPT, concept2Id).statusCode(200);
		getComponent(a, SnomedComponentType.CONCEPT, concept2Id).statusCode(404);

		merge(a, branchPath, "Merged new concept from diverged branch").body("status", equalTo(Merge.Status.FAILED.name()));

		getComponent(branchPath, SnomedComponentType.CONCEPT, concept1Id).statusCode(404);
		getComponent(a, SnomedComponentType.CONCEPT, concept1Id).statusCode(200);

		getComponent(branchPath, SnomedComponentType.CONCEPT, concept2Id).statusCode(200);
		getComponent(a, SnomedComponentType.CONCEPT, concept2Id).statusCode(404);
	}

	@Test
	public void noMergeNewDescriptionDiverged() {
		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		String description1Id = createNewDescription(a);
		String description2Id = createNewDescription(branchPath);

		getComponent(branchPath, SnomedComponentType.DESCRIPTION, description1Id).statusCode(404);
		getComponent(a, SnomedComponentType.DESCRIPTION, description1Id).statusCode(200);

		getComponent(branchPath, SnomedComponentType.DESCRIPTION, description2Id).statusCode(200);
		getComponent(a, SnomedComponentType.DESCRIPTION, description2Id).statusCode(404);

		merge(a, branchPath, "Merged new description from diverged branch").body("status", equalTo(Merge.Status.FAILED.name()));

		getComponent(branchPath, SnomedComponentType.DESCRIPTION, description1Id).statusCode(404);
		getComponent(a, SnomedComponentType.DESCRIPTION, description1Id).statusCode(200);

		getComponent(branchPath, SnomedComponentType.DESCRIPTION, description2Id).statusCode(200);
		getComponent(a, SnomedComponentType.DESCRIPTION, description2Id).statusCode(404);
	}

	@Test
	public void noMergeNewRelationshipDiverged() {
		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		String relationship1Id = createNewRelationship(a);
		String relationship2Id = createNewRelationship(branchPath);

		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationship1Id).statusCode(404);
		getComponent(a, SnomedComponentType.RELATIONSHIP, relationship1Id).statusCode(200);

		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationship2Id).statusCode(200);
		getComponent(a, SnomedComponentType.RELATIONSHIP, relationship2Id).statusCode(404);

		merge(a, branchPath, "Merged new relationship from diverged branch").body("status", equalTo(Merge.Status.FAILED.name()));

		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationship1Id).statusCode(404);
		getComponent(a, SnomedComponentType.RELATIONSHIP, relationship1Id).statusCode(200);

		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationship2Id).statusCode(200);
		getComponent(a, SnomedComponentType.RELATIONSHIP, relationship2Id).statusCode(404);
	}

	@Test
	public void mergeNewConceptToUnrelatedBranch() {
		IBranchPath v1 = BranchPathUtils.createPath(branchPath, "v1");
		createBranch(v1).statusCode(201);

		// Concept 1 is created on the two branches' common ancestor
		String concept1Id = createNewConcept(branchPath);

		IBranchPath v2 = BranchPathUtils.createPath(branchPath, "v2");
		createBranch(v2).statusCode(201);

		IBranchPath a = BranchPathUtils.createPath(v1, "extension-old");
		createBranch(a).statusCode(201);
		IBranchPath b = BranchPathUtils.createPath(v2, "extension-new");
		createBranch(b).statusCode(201);

		// Concept 2 is initially only visible on branch "extension-old"
		String concept2Id = createNewConcept(a);
		getComponent(a, SnomedComponentType.CONCEPT, concept2Id).statusCode(200);

		getComponent(branchPath, SnomedComponentType.CONCEPT, concept1Id).statusCode(200);
		getComponent(a, SnomedComponentType.CONCEPT, concept1Id).statusCode(404);
		getComponent(b, SnomedComponentType.CONCEPT, concept1Id).statusCode(200);

		merge(a, b, "Merged new concept from unrelated branch").body("status", equalTo(Merge.Status.COMPLETED.name()));

		getComponent(b, SnomedComponentType.CONCEPT, concept1Id).statusCode(200);
		getComponent(b, SnomedComponentType.CONCEPT, concept2Id).statusCode(200);
	}

	@Test
	public void mergeNewDescriptionToUnrelatedBranch() {
		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);
		IBranchPath b = BranchPathUtils.createPath(branchPath, "b");
		createBranch(b).statusCode(201);

		String descriptionId = createNewDescription(a);
		getComponent(a, SnomedComponentType.DESCRIPTION, descriptionId).statusCode(200);
		getComponent(b, SnomedComponentType.DESCRIPTION, descriptionId).statusCode(404);

		merge(a, b, "Merged new description from unrelated branch").body("status", equalTo(Merge.Status.COMPLETED.name()));

		getComponent(a, SnomedComponentType.DESCRIPTION, descriptionId).statusCode(200);
		getComponent(b, SnomedComponentType.DESCRIPTION, descriptionId).statusCode(200);
	}

	@Test
	public void mergeNewRelationshipToUnrelatedBranch() {
		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);
		IBranchPath b = BranchPathUtils.createPath(branchPath, "b");
		createBranch(b).statusCode(201);

		String relationshipId = createNewRelationship(a);
		getComponent(a, SnomedComponentType.RELATIONSHIP, relationshipId).statusCode(200);
		getComponent(b, SnomedComponentType.RELATIONSHIP, relationshipId).statusCode(404);

		merge(a, b, "Merged new relationship from unrelated branch").body("status", equalTo(Merge.Status.COMPLETED.name()));

		getComponent(a, SnomedComponentType.RELATIONSHIP, relationshipId).statusCode(200);
		getComponent(b, SnomedComponentType.RELATIONSHIP, relationshipId).statusCode(200);
	}

	@Test
	public void mergeReactivatedConcept() {
		String conceptId = createInactiveConcept(branchPath);

		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		reactivateConcept(a, conceptId);

		merge(a, branchPath, "Merged reactivation from child branch").body("status", equalTo(Merge.Status.COMPLETED.name()));

		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, "descriptions()", "relationships()").statusCode(200)
		.body("active", equalTo(true))
		.body("descriptions.items[0].active", equalTo(true))
		.body("descriptions.items[1].active", equalTo(true))
		.body("relationships.items[0].active", equalTo(true));
	}

	@Test
	public void rebaseReactivatedConcept() {
		String concept1Id = createInactiveConcept(branchPath);

		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		reactivateConcept(a, concept1Id);

		// Create concept 2 on "branchPath" so that "a" can be rebased
		String concept2Id = createNewConcept(branchPath);

		merge(branchPath, a, "Rebased reactivation on child branch").body("status", equalTo(Merge.Status.COMPLETED.name()));

		// Concept 1 should be active on "a", but still inactive on "branchPath"
		getComponent(branchPath, SnomedComponentType.CONCEPT, concept1Id, "descriptions()", "relationships()").statusCode(200)
		.body("active", equalTo(false))
		.body("descriptions.items[0].active", equalTo(true))
		.body("descriptions.items[1].active", equalTo(true))
		.body("relationships.items[0].active", equalTo(false));

		getComponent(a, SnomedComponentType.CONCEPT, concept1Id, "descriptions()", "relationships()").statusCode(200)
		.body("active", equalTo(true))
		.body("descriptions.items[0].active", equalTo(true))
		.body("descriptions.items[1].active", equalTo(true))
		.body("relationships.items[0].active", equalTo(true));

		// Concept 2 should be visible everywhere
		getComponent(branchPath, SnomedComponentType.CONCEPT, concept2Id).statusCode(200);
		getComponent(a, SnomedComponentType.CONCEPT, concept2Id).statusCode(200);
	}

	@Test
	public void rebaseNewConceptDiverged() {
		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		String concept1Id = createNewConcept(branchPath);
		String concept2Id = createNewConcept(a);

		getComponent(branchPath, SnomedComponentType.CONCEPT, concept1Id).statusCode(200);
		getComponent(a, SnomedComponentType.CONCEPT, concept1Id).statusCode(404);

		getComponent(branchPath, SnomedComponentType.CONCEPT, concept2Id).statusCode(404);
		getComponent(a, SnomedComponentType.CONCEPT, concept2Id).statusCode(200);

		merge(branchPath, a, "Rebased new concept").body("status", equalTo(Merge.Status.COMPLETED.name()));

		// Concept 1 from the parent becomes visible on the child after rebasing
		getComponent(branchPath, SnomedComponentType.CONCEPT, concept1Id).statusCode(200);
		getComponent(a, SnomedComponentType.CONCEPT, concept1Id).statusCode(200);

		// Concept 2 should still not be present on the parent, however
		getComponent(branchPath, SnomedComponentType.CONCEPT, concept2Id).statusCode(404);
		getComponent(a, SnomedComponentType.CONCEPT, concept2Id).statusCode(200);
	}

	@Test
	public void rebaseNewDescriptionDiverged() {
		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		String description1Id = createNewDescription(branchPath);
		String description2Id = createNewDescription(a);

		getComponent(branchPath, SnomedComponentType.DESCRIPTION, description1Id).statusCode(200);
		getComponent(a, SnomedComponentType.DESCRIPTION, description1Id).statusCode(404);

		getComponent(branchPath, SnomedComponentType.DESCRIPTION, description2Id).statusCode(404);
		getComponent(a, SnomedComponentType.DESCRIPTION, description2Id).statusCode(200);

		merge(branchPath, a, "Rebased new description").body("status", equalTo(Merge.Status.COMPLETED.name()));

		// Description 1 from the parent becomes visible on the child after rebasing
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, description1Id).statusCode(200);
		getComponent(a, SnomedComponentType.DESCRIPTION, description1Id).statusCode(200);

		// Description 2 should still not be present on the parent, however
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, description2Id).statusCode(404);
		getComponent(a, SnomedComponentType.DESCRIPTION, description2Id).statusCode(200);
	}

	@Test
	public void rebaseNewRelationshipDiverged() {
		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		String relationship1Id = createNewRelationship(branchPath);
		String relationship2Id = createNewRelationship(a);

		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationship1Id).statusCode(200);
		getComponent(a, SnomedComponentType.RELATIONSHIP, relationship1Id).statusCode(404);

		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationship2Id).statusCode(404);
		getComponent(a, SnomedComponentType.RELATIONSHIP, relationship2Id).statusCode(200);

		merge(branchPath, a, "Rebased new relationship").body("status", equalTo(Merge.Status.COMPLETED.name()));

		// Relationship 1 from the parent becomes visible on the child after rebasing
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationship1Id).statusCode(200);
		getComponent(a, SnomedComponentType.RELATIONSHIP, relationship1Id).statusCode(200);

		// Relationship 2 should still not be present on the parent, however
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationship2Id).statusCode(404);
		getComponent(a, SnomedComponentType.RELATIONSHIP, relationship2Id).statusCode(200);
	}

	@Test
	public void rebaseNewConceptStale() {
		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		IBranchPath b = BranchPathUtils.createPath(a, "b");
		createBranch(b).statusCode(201);

		String concept1Id = createNewConcept(b);
		String concept2Id = createNewConcept(a);
		String concept3Id = createNewConcept(branchPath);

		merge(branchPath, a, "Rebased new concept on child over new concept on parent").body("status", equalTo(Merge.Status.COMPLETED.name()));

		// "a" now knows about concept 3
		getComponent(branchPath, SnomedComponentType.CONCEPT, concept3Id).statusCode(200);
		getComponent(a, SnomedComponentType.CONCEPT, concept3Id).statusCode(200);

		// "b" is now in STALE state, and doesn't know about either concept 2 or 3
		getComponent(b, SnomedComponentType.CONCEPT, concept3Id).statusCode(404);
		getComponent(b, SnomedComponentType.CONCEPT, concept2Id).statusCode(404);

		merge(a, b, "Rebased new concept on nested child over new concepts on child").body("status", equalTo(Merge.Status.COMPLETED.name()));

		// Now "b" should see all three concepts
		getComponent(b, SnomedComponentType.CONCEPT, concept3Id).statusCode(200);
		getComponent(b, SnomedComponentType.CONCEPT, concept2Id).statusCode(200);
		getComponent(b, SnomedComponentType.CONCEPT, concept1Id).statusCode(200);
	}

	@Test
	public void rebaseChangedConceptOnParentDeletedOnBranch() {
		String conceptId = createNewConcept(branchPath);

		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		rebaseConceptDeletionOverChange(branchPath, a, conceptId);

		// Concept should still be present on parent, and deleted on child
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId).statusCode(200).body("definitionStatus", equalTo(DefinitionStatus.FULLY_DEFINED.name()));
		getComponent(a, SnomedComponentType.CONCEPT, conceptId).statusCode(404);
	}

	@Test
	public void rebaseChangedDescriptionOnParentDeletedOnBranch() {
		String descriptionId = createNewDescription(branchPath);

		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		changeCaseSignificance(branchPath, descriptionId); // Parent branch changes to CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE
		deleteComponent(a, SnomedComponentType.DESCRIPTION, descriptionId, false).statusCode(204);

		merge(branchPath, a, "Rebased deletion over case significance change").body("status", equalTo(Merge.Status.COMPLETED.name()));

		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId).statusCode(200).body("caseSignificance", equalTo(CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE.name()));
		getComponent(a, SnomedComponentType.DESCRIPTION, descriptionId).statusCode(404);
	}

	@Test
	public void rebaseAndMergeChangedOnParentDeletedOnBranch() {
		String conceptId = createNewConcept(branchPath);

		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		rebaseConceptDeletionOverChange(branchPath, a, conceptId);

		merge(a, branchPath, "Merged concept deletion").body("status", equalTo(Merge.Status.COMPLETED.name()));

		// Concept should now be deleted everywhere
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId).statusCode(404);
		getComponent(a, SnomedComponentType.CONCEPT, conceptId).statusCode(404);
	}

	@Test
	public void rebaseAndMergeChangedDescription() {
		String description1Id = createNewDescription(branchPath);

		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		String description2Id = createNewDescription(branchPath);

		Map<?, ?> requestBody = ImmutableMap.builder()
				.put("caseSignificance", CaseSignificance.CASE_INSENSITIVE)
				.put("moduleId", Concepts.MODULE_ROOT)
				.put("commitComment", "Changed case significance and module on child")
				.build();

		updateComponent(a, SnomedComponentType.DESCRIPTION, description1Id, requestBody);

		merge(branchPath, a, "Rebased description change over new description creation").body("status", equalTo(Merge.Status.COMPLETED.name()));

		// Description 2 is now visible on both parent and child
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, description2Id).statusCode(200);
		getComponent(a, SnomedComponentType.DESCRIPTION, description2Id).statusCode(200);

		// Description 1 retains the changes on child, keeps the original values on parent
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, description1Id).statusCode(200)
		.body("caseSignificance", equalTo(CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE.name()))
		.body("moduleId", equalTo(Concepts.MODULE_SCT_CORE));

		getComponent(a, SnomedComponentType.DESCRIPTION, description1Id).statusCode(200)
		.body("caseSignificance", equalTo(CaseSignificance.CASE_INSENSITIVE.name()))
		.body("moduleId", equalTo(Concepts.MODULE_ROOT));

		merge(a, branchPath, "Merged description change to parent").body("status", equalTo(Merge.Status.COMPLETED.name()));

		getComponent(branchPath, SnomedComponentType.DESCRIPTION, description2Id).statusCode(200);
		getComponent(a, SnomedComponentType.DESCRIPTION, description2Id).statusCode(200);

		// Description 1 changes are visible everywhere
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, description1Id).statusCode(200)
		.body("caseSignificance", equalTo(CaseSignificance.CASE_INSENSITIVE.name()))
		.body("moduleId", equalTo(Concepts.MODULE_ROOT));

		getComponent(a, SnomedComponentType.DESCRIPTION, description1Id).statusCode(200)
		.body("caseSignificance", equalTo(CaseSignificance.CASE_INSENSITIVE.name()))
		.body("moduleId", equalTo(Concepts.MODULE_ROOT));
	}

	@Test
	public void rebaseAndMergeNewDescriptionBothDeleted() {
		String description1Id = createNewDescription(branchPath);

		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		String description2Id = createNewDescription(branchPath);

		deleteComponent(branchPath, SnomedComponentType.DESCRIPTION, description1Id, false).statusCode(204);
		deleteComponent(a, SnomedComponentType.DESCRIPTION, description1Id, false).statusCode(204);

		/* 
		 * The rebase sees that the same thing has already happened on the parent branch, and does not 
		 * add an empty commit to the new instance of the child; it will be in UP_TO_DATE state and can 
		 * not be promoted.
		 */
		merge(branchPath, a, "Rebased description dual deletion over description creation").body("status", equalTo(Merge.Status.COMPLETED.name()));
		merge(a, branchPath, "Merged description dual deletion").body("status", equalTo(Merge.Status.FAILED.name()));

		// Description 1 is now deleted on both branches
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, description1Id).statusCode(404);
		getComponent(a, SnomedComponentType.DESCRIPTION, description1Id).statusCode(404);

		// Description 2 should be present, however
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, description2Id).statusCode(200);
		getComponent(a, SnomedComponentType.DESCRIPTION, description2Id).statusCode(200);
	}

	@Test
	public void rebaseOverReusedRelationshipId() {
		String relationshipId = createNewRelationship(branchPath);

		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		deleteComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId, false).statusCode(204);

		Map<?, ?> requestBody = ImmutableMap.builder()
				.put("sourceId", Concepts.ROOT_CONCEPT)
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("typeId", Concepts.FINDING_SITE)
				.put("destinationId", Concepts.ROOT_CONCEPT)
				.put("id", relationshipId)
				.put("commitComment", "Created new relationship on parent with same SCTID")
				.build();

		createComponent(branchPath, SnomedComponentType.RELATIONSHIP, requestBody).statusCode(201);

		// Different relationships before rebase
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId).statusCode(200).body("typeId", equalTo(Concepts.FINDING_SITE));
		getComponent(a, SnomedComponentType.RELATIONSHIP, relationshipId).statusCode(200).body("typeId", equalTo(Concepts.PART_OF));

		merge(branchPath, a, "Rebase after new relationship creation on parent").body("status", equalTo(Merge.Status.COMPLETED.name()));

		// Same relationships after rebase
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId).statusCode(200).body("typeId", equalTo(Concepts.FINDING_SITE));
		getComponent(a, SnomedComponentType.RELATIONSHIP, relationshipId).statusCode(200).body("typeId", equalTo(Concepts.FINDING_SITE));
	}

	@Test
	public void rebaseTextDefinitions() throws Exception {
		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		// Create two new text definitions with "cross-shaped" acceptability on child
		String textDefinition1Id = createNewTextDefinition(a, ImmutableMap.of(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED, Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.ACCEPTABLE));
		String textDefinition2Id = createNewTextDefinition(a, ImmutableMap.of(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.ACCEPTABLE, Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.PREFERRED));

		// Create change on parent
		String relationshipId = createNewRelationship(branchPath);

		merge(branchPath, a, "Rebased new text definitions over new relationship").body("status", equalTo(Merge.Status.COMPLETED.name()));

		// Relationship should be visible on both branches
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, relationshipId).statusCode(200);
		getComponent(a, SnomedComponentType.RELATIONSHIP, relationshipId).statusCode(200);

		// Text definitions are only on the child, however
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, textDefinition1Id).statusCode(404);
		getComponent(a, SnomedComponentType.DESCRIPTION, textDefinition1Id).statusCode(200);
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, textDefinition2Id).statusCode(404);
		getComponent(a, SnomedComponentType.DESCRIPTION, textDefinition2Id).statusCode(200);
	}

	@Test
	public void rebaseStaleBranchWithChangesOnDeletedContent() throws Exception {
		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		String relationshipId = createNewRelationship(a);
		String descriptionId = createNewDescription(a);

		IBranchPath b = BranchPathUtils.createPath(a, "b");
		createBranch(b).statusCode(201);

		deleteComponent(a, SnomedComponentType.RELATIONSHIP, relationshipId, false).statusCode(204);
		deleteComponent(a, SnomedComponentType.DESCRIPTION, descriptionId, false).statusCode(204);

		changeCaseSignificance(b, descriptionId);
		changeRelationshipGroup(b, relationshipId);

		// Make change on "branchPath" so "a" can be rebased
		createNewRelationship(branchPath);
		merge(branchPath, a, "Rebased component deletion over new relationship").body("status", equalTo(Merge.Status.COMPLETED.name()));

		// "b" should be STALE at this point, try to rebase it, it should pass and the components should be deleted
		merge(a, b, "Rebased component updates over deletion").body("status", equalTo(Merge.Status.COMPLETED.name()));

		// Verify that the two deleted components are really deleted
		getComponent(b, SnomedComponentType.DESCRIPTION, descriptionId).statusCode(404);
		getComponent(b, SnomedComponentType.RELATIONSHIP, relationshipId).statusCode(404);
	}

	@Test
	public void rebaseStaleBranchWithChangesOnNewContent() throws Exception {
		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		String relationshipId = createNewRelationship(a);
		String descriptionId = createNewDescription(a);

		IBranchPath b = BranchPathUtils.createPath(a, "b");
		createBranch(b).statusCode(201);

		changeCaseSignificance(b, descriptionId);
		changeRelationshipGroup(b, relationshipId);

		// Make change on "branchPath" so "a" can be rebased
		createNewRelationship(branchPath);
		merge(branchPath, a, "Rebased new components over new relationship").body("status", equalTo(Merge.Status.COMPLETED.name()));

		getComponent(a, SnomedComponentType.DESCRIPTION, descriptionId).statusCode(200).body("caseSignificance", equalTo(CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE.name()));
		getComponent(a, SnomedComponentType.RELATIONSHIP, relationshipId).statusCode(200).body("group", equalTo(0));

		// "b" should be STALE at this point, try to rebase it, it should pass and the components should still exist with changed content
		merge(a, b, "Rebased changed components over new components").body("status", equalTo(Merge.Status.COMPLETED.name()));

		// Verify that the two components have the modified values
		getComponent(b, SnomedComponentType.DESCRIPTION, descriptionId).statusCode(200).body("caseSignificance", equalTo(CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE.name()));
		getComponent(b, SnomedComponentType.RELATIONSHIP, relationshipId).statusCode(200).body("group", equalTo(99));
	}

	@Test
	public void rebaseStaleBranchWithDeleteOnChangedContent() throws Exception {
		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		String relationshipId = createNewRelationship(a);
		String descriptionId = createNewDescription(a);

		IBranchPath b = BranchPathUtils.createPath(a, "b");
		createBranch(b).statusCode(201);

		// Make changes on branch "a"
		changeCaseSignificance(a, descriptionId);
		changeRelationshipGroup(a, relationshipId);

		// Delete description on branch "b"
		deleteComponent(b, SnomedComponentType.DESCRIPTION, descriptionId, false).statusCode(204);

		// Make change on "branchPath" so "a" can be rebased
		createNewRelationship(branchPath);
		merge(branchPath, a, "Rebased changed components over new relationship").body("status", equalTo(Merge.Status.COMPLETED.name()));

		// "b" should be STALE at this point, try to rebase it, it should pass and the description should be deleted
		merge(a, b, "Rebased description deletion over changed components").body("status", equalTo(Merge.Status.COMPLETED.name()));

		// Verify that the relationship has the modified values, and the description stayed deleted
		getComponent(b, SnomedComponentType.DESCRIPTION, descriptionId).statusCode(404);
		getComponent(b, SnomedComponentType.RELATIONSHIP, relationshipId).statusCode(200).body("group", equalTo(99));
	}

	@Test
	@Ignore("Currently always fails due to merge policy")
	public void rebaseChangedConceptOnBranchDeletedOnParent() {
		String conceptId = createNewConcept(branchPath);

		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		Map<?, ?> requestBody = ImmutableMap.builder()
				.put("definitionStatus", DefinitionStatus.FULLY_DEFINED)
				.put("commitComment", "Changed definition status on child")
				.build();

		updateComponent(a, SnomedComponentType.CONCEPT, conceptId, requestBody).statusCode(204);
		deleteComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, false).statusCode(204);

		merge(branchPath, a, "Rebased concept change over deletion").body("status", equalTo(Merge.Status.COMPLETED.name()));
	}

	@Test
	public void rebaseUnsetEffectiveTimeOnSource() {
		String memberId = createNewRefSetMember(branchPath);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(getNextAvailableEffectiveDate(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME));
		updateRefSetMemberEffectiveTime(branchPath, memberId, calendar.getTime());

		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		calendar.add(Calendar.DATE, 1);
		updateRefSetMemberEffectiveTime(branchPath, memberId, calendar.getTime()); // Parent increases the effective time by one day

		Map<?, ?> childRequest = ImmutableMap.builder()
				.put("active", false)
				.put("commitComment", "Inactivated reference set member")
				.build();

		updateRefSetComponent(a, SnomedComponentType.MEMBER, memberId, childRequest, false).statusCode(204); // Child unsets it and inactivates the member

		merge(branchPath, a, "Rebased update over effective time change").body("status", equalTo(Merge.Status.COMPLETED.name()));

		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200)
		.body("released", equalTo(true))
		.body("effectiveTime", equalTo(EffectiveTimes.format(calendar.getTime(), DateFormats.SHORT)))
		.body("active", equalTo(true));

		getComponent(a, SnomedComponentType.MEMBER, memberId).statusCode(200)
		.body("released", equalTo(true))
		.body("effectiveTime", nullValue())
		.body("active", equalTo(false));
	}

	@Test
	public void rebaseUnsetEffectiveTimeOnTarget() {
		String memberId = createNewRefSetMember(branchPath);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(getNextAvailableEffectiveDate(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME));
		updateRefSetMemberEffectiveTime(branchPath, memberId, calendar.getTime());

		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		Map<?, ?> parentRequest = ImmutableMap.builder()
				.put("active", false)
				.put("commitComment", "Inactivated reference set member")
				.build();

		updateRefSetComponent(branchPath, SnomedComponentType.MEMBER, memberId, parentRequest, false).statusCode(204); // Parent unsets the effective time and inactivates the member

		calendar.add(Calendar.DATE, 1);
		updateRefSetMemberEffectiveTime(a, memberId, calendar.getTime()); // Child increases the effective time by one day

		merge(branchPath, a, "Rebased effective time change over update").body("status", equalTo(Merge.Status.COMPLETED.name()));

		getComponent(branchPath, SnomedComponentType.MEMBER, memberId).statusCode(200)
		.body("released", equalTo(true))
		.body("effectiveTime", nullValue())
		.body("active", equalTo(false));

		getComponent(a, SnomedComponentType.MEMBER, memberId).statusCode(200)
		.body("released", equalTo(true))
		.body("effectiveTime", nullValue()) // Parent wins because of the effective time unset
		.body("active", equalTo(false)); // Child didn't update the status, so inactivation on the parent is in effect
	}

}
