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

import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.MODULE_SCT_CORE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.ROOT_CONCEPT;
import static com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants.PREFERRED_ACCEPTABILITY_MAP;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.assertBranchCanBeMerged;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.givenBranchWithPath;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.whenMergingOrRebasingBranches;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentCanBeDeleted;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentCreated;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.givenConceptRequestBody;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.givenRelationshipRequestBody;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.server.branch.Branch;
import com.b2international.snowowl.datastore.server.review.ReviewStatus;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

/**
 * @since 2.0
 */
public class SnomedReviewApiTest extends AbstractSnomedApiTest {

	private static final Set<String> FINISH_STATES = ImmutableSet.of(
			ReviewStatus.CURRENT.toString(), 
			ReviewStatus.FAILED.toString(), 
			ReviewStatus.STALE.toString());

	private static final long POLL_INTERVAL = TimeUnit.SECONDS.toMillis(1L);
	private static final long POLL_TIMEOUT = TimeUnit.SECONDS.toMillis(30L);
	
	private static final String DISEASE = "64572001";
	private static final String TEMPORAL_CONTEXT = "410510008";
	private static final String FINDING_CONTEXT = "408729009";
	
	private Response whenCreatingReview(String source, String target) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
		.with()
			.contentType(ContentType.JSON)
			.body(ImmutableMap.builder().put("source", source).put("target", target).build())
		.when()
			.post("/reviews");
	}

	private String andCreatedReview(String source, String target) {
		final String location = whenCreatingReview(source, target)
		.then()
			.statusCode(201)
			.header("Location", notNullValue())
			.extract().header("Location");

		return lastPathSegment(location);
	}
	
	private Response whenRetrievingReviewWithId(String reviewId) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
		.when()
			.get("/reviews/{id}", reviewId);
	}
	
	@Test
	public void createReviewEmptyFields() {
		whenCreatingReview("", "")
		.then()
			.statusCode(400)
			.body("message", equalTo("2 validation errors"))
			.body("violations", hasItem("'source' may not be empty (was '')"))
			.body("violations", hasItem("'target' may not be empty (was '')"));
	}
	
	@Test
	public void createReviewNonExistentBranch() {
		whenCreatingReview(testBranchPath.getPath(), Branch.MAIN_PATH)
		.then()
			.statusCode(400);
	}
	
	@Test
	public void createReviewRebaseMain() {
		whenCreatingReview("MAIN", "MAIN")
		.then()
		.statusCode(400);
	}
	
	@Test
	public void createReview() {
		givenBranchWithPath(testBranchPath);
		final String reviewId = andCreatedReview("MAIN", testBranchPath.getPath());
		whenRetrievingReviewWithId(reviewId)
		.then()
			.statusCode(200)
			.body("status", equalTo(ReviewStatus.PENDING.toString()));
		
		assertReviewCurrent(reviewId);
	}

	private void assertReviewCurrent(final String reviewId) {
		final long endTime = System.currentTimeMillis() + POLL_TIMEOUT;
		long currentTime;
		String currentStatus;

		do {

			try {
				Thread.sleep(POLL_INTERVAL);
			} catch (final InterruptedException e) {
				fail(e.toString());
			}

			currentStatus = whenRetrievingReviewWithId(reviewId)
			.then()
				.statusCode(200)
				.extract().body().path("status");

			currentTime = System.currentTimeMillis();

		} while (!FINISH_STATES.contains(currentStatus) && currentTime < endTime);

		assertEquals("End state should be CURRENT.", currentStatus, ReviewStatus.CURRENT.toString());
	}
	
	@Test
	public void reviewBeforeMerge() {
		final IBranchPath setupBranch = createNestedBranch("a", "b");
		final Map<?, ?> conceptRequestBody = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);

		// Create a new concept on the setup branch so that it can be deleted on the nested branch
		final String c1 = assertComponentCreated(setupBranch, SnomedComponentType.CONCEPT, conceptRequestBody);
		assertBranchCanBeMerged(setupBranch, "Creating concept which can be deleted");
		
		final IBranchPath setupSiblingPath = BranchPathUtils.createPath(setupBranch.getParent(), "c");
		givenBranchWithPath(setupSiblingPath);
		
		// Create new concept on sibling branch
		final String c2 = assertComponentCreated(setupSiblingPath, SnomedComponentType.CONCEPT, conceptRequestBody);
		
		// Create new relationship, which will mark "Disease" as changed, but not "Finding context"
		final Map<?, ?> relationshipRequestBody = givenRelationshipRequestBody(DISEASE, TEMPORAL_CONTEXT, FINDING_CONTEXT, MODULE_SCT_CORE, "New relationship");
		assertComponentCreated(setupSiblingPath, SnomedComponentType.RELATIONSHIP, relationshipRequestBody);
		
		// Delete the concept we have created earlier
		assertComponentCanBeDeleted(setupSiblingPath, SnomedComponentType.CONCEPT, c1);
		
		// See what happened on the sibling branch before merging changes to its parent
		final String reviewId = andCreatedReview(setupSiblingPath.getPath(), setupSiblingPath.getParentPath());
		assertReviewCurrent(reviewId);
		
		whenRetrievingChangesWithId(reviewId)
		.then()
			.statusCode(200)
			.body("id", equalTo(reviewId))
			.body("newConcepts", hasItem(c2))
			.body("changedConcepts", hasItem(DISEASE))
			.body("changedConcepts", not(hasItem(FINDING_CONTEXT)))
			.body("deletedConcepts", equalTo(ImmutableList.of(c1)));
	}
	
	@Test
	public void reviewBeforeRebase() {
		// Open the setup branch
		IBranchPath setupBranchPath = createNestedBranch("A");
		
		// Create a new concept on the setup branch so that it can be deleted on the test branch
		final Map<?, ?> conceptRequestBody = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		final String c1 = assertComponentCreated(setupBranchPath, SnomedComponentType.CONCEPT, conceptRequestBody);
		assertBranchCanBeMerged(setupBranchPath, "Creating concept which can be deleted");
		
		// Create a nested branch after C1 appears
		IBranchPath nestedBranchPath = BranchPathUtils.createPath(testBranchPath, "B");
		givenBranchWithPath(nestedBranchPath);
		
		// Create new concept on test branch
		final String c2 = assertComponentCreated(testBranchPath, SnomedComponentType.CONCEPT, conceptRequestBody);
		
		// Create new relationship, which will mark "Disease" as changed, but not "Finding context"
		final Map<?, ?> relationshipRequestBody = givenRelationshipRequestBody(DISEASE, TEMPORAL_CONTEXT, FINDING_CONTEXT, MODULE_SCT_CORE, "New relationship");
		assertComponentCreated(testBranchPath, SnomedComponentType.RELATIONSHIP, relationshipRequestBody);
		
		// Delete the concept we have created earlier
		assertComponentCanBeDeleted(testBranchPath, SnomedComponentType.CONCEPT, c1);
		
		// See what happened on testBranchPath before we actually decide to rebase (reopen) nestedBranchPath on top of it
		final String reviewId = andCreatedReview(testBranchPath.getPath(), nestedBranchPath.getPath());
		assertReviewCurrent(reviewId);
		
		whenRetrievingChangesWithId(reviewId)
		.then()
			.statusCode(200)
			.body("id", equalTo(reviewId))
			.body("newConcepts", hasItem(c2))
			.body("changedConcepts", hasItem(DISEASE))
			.body("changedConcepts", not(hasItem(FINDING_CONTEXT)))
			.body("deletedConcepts", equalTo(ImmutableList.of(c1)));
	}

	private Response whenRetrievingChangesWithId(final String reviewId) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
		.when()
			.get("/reviews/{id}/concept-changes", reviewId);
	}
	
	@Test
	public void deleteReviewAndChanges() {
		givenBranchWithPath(testBranchPath);
		final String reviewId = andCreatedReview("MAIN", testBranchPath.getPath());
		assertReviewCurrent(reviewId);
		
		whenRetrievingChangesWithId(reviewId).then().statusCode(200);
		
		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
		.when().delete("/reviews/{id}", reviewId)
		.then().statusCode(204);
			
		whenRetrievingReviewWithId(reviewId).then().statusCode(404);
		whenRetrievingChangesWithId(reviewId).then().statusCode(404);
	}
	
	@Test
	public void setReviewStale() {
		givenBranchWithPath(testBranchPath);
		final String reviewId = andCreatedReview("MAIN", testBranchPath.getPath());

		final Map<?, ?> conceptRequestBody = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		assertComponentCreated(testBranchPath, SnomedComponentType.CONCEPT, conceptRequestBody);
		
		whenRetrievingReviewWithId(reviewId)
		.then()
			.statusCode(200)
			.body("status", equalTo(ReviewStatus.STALE.toString()));		
	}
	
	@Test
	public void mergeWithNonExistentReview() {
		givenBranchWithPath(testBranchPath);
		whenMergingOrRebasingBranches(testBranchPath, testBranchPath.getParent(), "Merge commit", "abc")
		.then()
			.statusCode(400);
	}
}
