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
package com.b2international.snowowl.snomed.api.rest.branches;

import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingRestRequests.createBranch;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.deleteComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.*;
import static com.b2international.snowowl.snomed.api.rest.SnomedReviewRestRequests.*;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.*;

import java.util.Map;

import org.junit.Test;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.merge.Merge;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.review.ReviewStatus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.http.ContentType;

/**
 * @since 2.0
 */
public class SnomedReviewApiTest extends AbstractSnomedApiTest {

	@Test
	public void createReviewEmptyFields() {
		Map<?, ?> requestBody = ImmutableMap.builder()
				.put("source", "")
				.put("target", "")
				.build();

		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
		.contentType(ContentType.JSON)
		.body(requestBody)
		.post("/reviews")
		.then()
		.statusCode(400)
		.body("message", equalTo("2 validation errors"))
		.body("violations", hasItem("'source' may not be empty (was '')"))
		.body("violations", hasItem("'target' may not be empty (was '')"));
	}

	@Test
	public void createReviewNonExistentBranch() {
		createReview(BranchPathUtils.createPath("MAIN/x/y/z"), BranchPathUtils.createMainPath()).statusCode(400);
	}

	@Test
	public void createReviewRebaseMain() {
		createReview(BranchPathUtils.createMainPath(), BranchPathUtils.createMainPath()).statusCode(400);
	}

	@Test
	public void createRegularReview() {
		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		String reviewId = getReviewJobId(createReview(a, branchPath));
		
		getReview(reviewId).statusCode(200)
				.body("status", anyOf(equalTo(ReviewStatus.PENDING.name()), equalTo(ReviewStatus.CURRENT.name())));

		waitForReviewJob(reviewId).body("status", equalTo(ReviewStatus.CURRENT.name()));
	}

	@Test
	public void reviewBeforeMerge() {
		String deletedConceptId = createNewConcept(branchPath);

		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		String newConceptId = createNewConcept(a);
		createNewRelationship(a);
		deleteComponent(a, SnomedComponentType.CONCEPT, deletedConceptId, false).statusCode(204);

		String reviewId = getReviewJobId(createReview(a, branchPath));
		waitForReviewJob(reviewId).body("status", equalTo(ReviewStatus.CURRENT.name()));

		getConceptChanges(reviewId).statusCode(200)
		.body("id", equalTo(reviewId))
		.body("newConcepts", hasItem(newConceptId))
		.body("changedConcepts", hasItem(Concepts.ROOT_CONCEPT)) // source of new relationship
		.body("changedConcepts", not(hasItem(Concepts.NAMESPACE_ROOT))) // destination of new relationship
		.body("deletedConcepts", hasItem(deletedConceptId));
	}

	@Test
	public void reviewBeforeRebase() {
		String deletedConceptId = createNewConcept(branchPath);

		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		String newConceptId = createNewConcept(branchPath);
		createNewRelationship(branchPath);
		deleteComponent(branchPath, SnomedComponentType.CONCEPT, deletedConceptId, false).statusCode(204);

		String reviewId = getReviewJobId(createReview(branchPath, a));
		waitForReviewJob(reviewId).body("status", equalTo(ReviewStatus.CURRENT.name()));

		getConceptChanges(reviewId).statusCode(200)
		.body("id", equalTo(reviewId))
		.body("newConcepts", hasItem(newConceptId))
		.body("changedConcepts", hasItem(Concepts.ROOT_CONCEPT)) // source of new relationship
		.body("changedConcepts", not(hasItem(Concepts.NAMESPACE_ROOT))) // destination of new relationship
		.body("deletedConcepts", hasItem(deletedConceptId));
	}

	@Test
	public void reviewDescriptionInactivationRebase() {
		String descriptionId = createNewDescription(branchPath);

		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		inactivateDescription(branchPath, descriptionId);

		String reviewId = getReviewJobId(createReview(branchPath, a));
		waitForReviewJob(reviewId).body("status", equalTo(ReviewStatus.CURRENT.name()));

		getConceptChanges(reviewId).statusCode(200)
		.body("id", equalTo(reviewId))
		.body("changedConcepts", hasItem(Concepts.ROOT_CONCEPT)); // concept of inactivated description
	}

	@Test
	public void reviewRelationshipInactivationRebase() {
		String relationshipId = createNewRelationship(branchPath);

		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		inactivateRelationship(branchPath, relationshipId);

		String reviewId = getReviewJobId(createReview(branchPath, a));
		waitForReviewJob(reviewId).body("status", equalTo(ReviewStatus.CURRENT.name()));

		getConceptChanges(reviewId).statusCode(200)
		.body("id", equalTo(reviewId))
		.body("changedConcepts", hasItem(Concepts.ROOT_CONCEPT))
		.body("changedConcepts", not(hasItem(Concepts.NAMESPACE_ROOT)));
	}

	@Test
	public void reviewAfterParentRebase() {
		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);
		
		String deletedConceptId = createNewConcept(a);
		
		IBranchPath b = BranchPathUtils.createPath(a, "b");
		createBranch(b).statusCode(201);

		deleteComponent(a, SnomedComponentType.CONCEPT, deletedConceptId, false).statusCode(204);
		String newConcept1Id = createNewConcept(a); 
		createNewRelationship(a);

		// Create new concept on "branchPath" so "a" can be rebased (and "b" becomes stale)
		String newConcept2Id = createNewConcept(branchPath);

		merge(branchPath, a, "Rebased changes over appearing and then deleted concept").body("status", equalTo(Merge.Status.COMPLETED.name()));

		// Generate review for a rebase of "b" on top of the now-rebased "a" 
		String reviewId = getReviewJobId(createReview(a, b));
		waitForReviewJob(reviewId).body("status", equalTo(ReviewStatus.CURRENT.name()));

		getConceptChanges(reviewId).statusCode(200)
		.body("id", equalTo(reviewId))
		.body("newConcepts", hasItems(newConcept1Id, newConcept2Id))
		.body("changedConcepts", hasItem(Concepts.ROOT_CONCEPT))
		.body("changedConcepts", not(hasItem(Concepts.NAMESPACE_ROOT)))
		.body("deletedConcepts", nullValue()); // In this test case we never see c1
	}

	@Test
	public void reviewAfterParentMerge() {
		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);
		
		String deletedConceptId = createNewConcept(a);
		
		IBranchPath b = BranchPathUtils.createPath(a, "b");
		createBranch(b).statusCode(201);

		deleteComponent(a, SnomedComponentType.CONCEPT, deletedConceptId, false).statusCode(204);
		String newConcept1Id = createNewConcept(a); 
		createNewRelationship(a);

		// Merge "a" back to "branchPath"
		merge(a, branchPath, "Merged changes on child to parent").body("status", equalTo(Merge.Status.COMPLETED.name()));

		// Create another new concept on "branchPath"
		String newConcept2Id = createNewConcept(branchPath);

		// Generate review for a rebase of "b" on top of the now-merged "a"
		String reviewId = getReviewJobId(createReview(a, b));
		waitForReviewJob(reviewId).body("status", equalTo(ReviewStatus.CURRENT.name()));

		getConceptChanges(reviewId).statusCode(200)
		.body("id", equalTo(reviewId))
		.body("newConcepts", hasItem(newConcept1Id))
		.body("newConcepts", not(hasItem(newConcept2Id))) // newConcept2Id has been added after the merge, it should not be visible here
		.body("changedConcepts", hasItem(Concepts.ROOT_CONCEPT))
		.body("changedConcepts", not(hasItem(Concepts.NAMESPACE_ROOT)))
		.body("deletedConcepts", nullValue());
	}

	@Test
	public void deleteReviewAndChanges() {
		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		String reviewId = getReviewJobId(createReview(a, branchPath));
		waitForReviewJob(reviewId).body("status", equalTo(ReviewStatus.CURRENT.name()));

		getConceptChanges(reviewId).statusCode(200);
		deleteReview(reviewId).statusCode(204);

		getReview(reviewId).statusCode(404);
		getConceptChanges(reviewId).statusCode(404);
	}

	@Test
	public void setReviewStale() throws Exception {
		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		String reviewId = getReviewJobId(createReview(a, branchPath));
		waitForReviewJob(reviewId).body("status", equalTo(ReviewStatus.CURRENT.name()));

		createNewConcept(branchPath);

		// wait 1s before checking review state 
		Thread.sleep(1000);

		getReview(reviewId).statusCode(200).body("status", equalTo(ReviewStatus.STALE.toString()));		
	}

	@Test
	public void setReviewStaleAfterParentRebase() {
		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);
		IBranchPath b = BranchPathUtils.createPath(a, "b");
		createBranch(b).statusCode(201);

		createNewConcept(branchPath);

		String reviewId = getReviewJobId(createReview(a, b));
		waitForReviewJob(reviewId).body("status", equalTo(ReviewStatus.CURRENT.name()));

		merge(branchPath, a, "Rebased child branch over new concept").body("status", equalTo(Merge.Status.COMPLETED.name()));

		getReview(reviewId).statusCode(200).body("status", equalTo(ReviewStatus.STALE.toString()));
	}

}
