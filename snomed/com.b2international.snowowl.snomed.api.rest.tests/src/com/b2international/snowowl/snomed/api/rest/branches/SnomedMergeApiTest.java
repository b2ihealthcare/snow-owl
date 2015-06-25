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

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.joinPath;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;
import static com.google.common.collect.Maps.newHashMap;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.Date;
import java.util.Map;

import org.junit.Test;

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.domain.Acceptability;
import com.b2international.snowowl.snomed.api.domain.CaseSignificance;
import com.b2international.snowowl.snomed.api.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

/**
 * @since 2.0
 */
public class SnomedMergeApiTest extends AbstractSnomedApiTest {

	private static final Map<?, ?> ACCEPTABLE_ACCEPTABILITY_MAP = ImmutableMap.of(
		Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.ACCEPTABLE
	);

	private static final Map<?, ?> PREFERRED_ACCEPTABILITY_MAP = ImmutableMap.of(
		Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED
	);

	private final Map<String, String> symbolicNameToIds = newHashMap();
	
	private void assertComponentCanBeCreated(String componentType, String symbolicName, Map<?, ?> requestBody, String... segments) {
		String path = joinPath(segments);

		Response response = givenAuthenticatedRequest(SCT_API)
		.with()
			.contentType(ContentType.JSON)
		.and()
			.body(requestBody)
		.when()
			.post("/{path}/{componentType}", path, componentType);
			
		response
		.then()
		.assertThat()
			.statusCode(201)
		.and()
			.header("Location", containsString(String.format("/%s/%s", path, componentType)));
		
		symbolicNameToIds.put(symbolicName, lastPathSegment(response.getHeader("Location")));
	}
	
	private void assertConceptCanBeCreated(String symbolicName, String... segments) {
		
		Date creationDate = new Date();
		Map<?, ?> requestBody = ImmutableMap.of(
			"commitComment", "New concept",
			"parentId", Concepts.ROOT_CONCEPT,
			"moduleId", Concepts.MODULE_SCT_CORE,
			"descriptions", ImmutableList.of(
				ImmutableMap.of(
					"typeId", Concepts.FULLY_SPECIFIED_NAME,
					"term", "New FSN at " + creationDate,
					"languageCode", "en",
					"acceptability", PREFERRED_ACCEPTABILITY_MAP 
				),
				
				ImmutableMap.of(
					"typeId", Concepts.SYNONYM,
					"term", "New PT at " + creationDate,
					"languageCode", "en",
					"acceptability", PREFERRED_ACCEPTABILITY_MAP 
				)
			)
		);
				
		assertComponentCanBeCreated("concepts", symbolicName, requestBody, segments);
	}
	
	private void assertDescriptionCanBeCreated(String symbolicName, Map<?, ?> acceptabilityMap, String... segments) {
		
		Date creationDate = new Date();
		Map<?, ?> requestBody = ImmutableMap.builder()
			.put("conceptId", Concepts.ROOT_CONCEPT)
			.put("moduleId", Concepts.MODULE_SCT_CORE)
			.put("typeId", Concepts.SYNONYM)
			.put("term", "New PT at " + creationDate)
			.put("languageCode", "en")
			.put("acceptability", acceptabilityMap)
			.put("commitComment", "New description")
			.build();
		
		assertComponentCanBeCreated("descriptions", symbolicName, requestBody, segments);
	}
	
	private void assertRelationshipCanBeCreated(String symbolicName, String... segments) {
		
		Map<?, ?> requestBody = ImmutableMap.of(
			"sourceId", Concepts.ROOT_CONCEPT,
			"moduleId", Concepts.MODULE_SCT_CORE,
			"typeId", "116676008", // Associated morphology
			"destinationId", "49755003", // Morphologic abnormality
			"commitComment", "New relationship"
		);
		
		assertComponentCanBeCreated("relationships", symbolicName, requestBody, segments);
	}

	private void assertComponentCanBeUpdated(String componentType, String symbolicName, Map<?, ?> requestBody, String... segments) {
		String path = joinPath(segments);

		Response response = givenAuthenticatedRequest(SCT_API)
		.with()
			.contentType(ContentType.JSON)
		.and()
			.body(requestBody)
		.when()
			.post("/{path}/{componentType}/{id}/updates", path, componentType, symbolicNameToIds.get(symbolicName));
			
		response
		.then()
		.assertThat()
			.statusCode(204);
	}

	private void assertConceptCanBeUpdated(String symbolicName, Map<?, ?> requestBody, String... segments) {
		assertComponentCanBeUpdated("concepts", symbolicName, requestBody, segments);
	}
	
	private void assertDescriptionCanBeUpdated(String symbolicName, Map<?, ?> requestBody, String... segments) {
		assertComponentCanBeUpdated("descriptions", symbolicName, requestBody, segments);
	}
	
	private void assertComponentCanBeDeleted(String componentType, String symbolicName, String... segments) {
		String path = joinPath(segments);

		Response response = givenAuthenticatedRequest(SCT_API)
		.when()
			.delete("/{path}/{componentType}/{id}", path, componentType, symbolicNameToIds.get(symbolicName));
			
		response
		.then()
		.assertThat()
			.statusCode(204);
	}

	private void assertConceptCanBeDeleted(String symbolicName, String... segments) {
		assertComponentCanBeDeleted("concepts", symbolicName, segments);
	}
	
	private void assertDescriptionCanBeDeleted(String symbolicName, String... segments) {
		assertComponentCanBeDeleted("descriptions", symbolicName, segments);
	}

	private void assertComponentStatus(String componentType, int statusCode, String symbolicName, String... segments) {
		String path = joinPath(segments);
		
		givenAuthenticatedRequest(SCT_API)
		.when()
			.get("/{path}/{componentType}/{id}", path, componentType, symbolicNameToIds.get(symbolicName))
		.then()
		.assertThat()
			.statusCode(statusCode);
	}
	
	private void assertComponentExists(String componentType, String symbolicName, String... segments) {
		assertComponentStatus(componentType, 200, symbolicName, segments);
	}

	private void assertConceptExists(String symbolicName, String... segments) {
		assertComponentExists("concepts", symbolicName, segments);
	}
	
	private void assertDescriptionExists(String symbolicName, String... segments) {
		assertComponentExists("descriptions", symbolicName, segments);
	}
	
	private void assertRelationshipExists(String symbolicName, String... segments) {
		assertComponentExists("relationships", symbolicName, segments);
	}
	
	private void assertComponentNotExists(String componentType, String symbolicName, String... segments) {
		assertComponentStatus(componentType, 404, symbolicName, segments);
	}
	
	private void assertConceptNotExists(String symbolicName, String... segments) {
		assertComponentNotExists("concepts", symbolicName, segments);
	}
	
	private void assertDescriptionNotExists(String symbolicName, String... segments) {
		assertComponentNotExists("descriptions", symbolicName, segments);
	}
	
	private void assertRelationshipNotExists(String symbolicName, String... segments) {
		assertComponentNotExists("relationships", symbolicName, segments);
	}

	private Response whenMergingOrRebasingBranches(RequestSpecification request, String source, String target, String commitComment) {
		Map<?, ?> requestBody = ImmutableMap.of(
			"source", source,
			"target", target,
			"commitComment", commitComment
		);

		return request
		.with()
			.contentType(ContentType.JSON)
		.and()
			.body(requestBody)
		.when()
			.post("/merges");
	}

	private void assertBranchCanBeMerged(String source, String target, String commitComment) {
		whenMergingOrRebasingBranches(givenAuthenticatedRequest(SCT_API), source, target, commitComment)
		.then()
		.assertThat()
			.statusCode(204);
	}
	
	private void assertBranchCanBeRebased(String source, String target, String commitComment) {
		// Convenience method; the merge service figures out what to do by inspecting the relationship between source and target
		assertBranchCanBeMerged(source, target, commitComment);
	}
	
	private void assertBranchConflicts(String source, String target, String commitComment) {
		whenMergingOrRebasingBranches(givenAuthenticatedRequest(SCT_API), source, target, commitComment)
		.then()
		.assertThat()
			.statusCode(409);
	}

	@Test
	public void mergeNewConceptForward() {
		assertBranchCanBeCreated("MAIN", branchName);
		
		assertConceptCanBeCreated("C1", "MAIN", branchName);
		assertConceptExists("C1", "MAIN", branchName);

		assertBranchCanBeMerged(joinPath("MAIN", branchName), "MAIN", "Merge new concept");
		
		assertConceptExists("C1", "MAIN", branchName);
		assertConceptExists("C1", "MAIN");
	}
	
	@Test
	public void mergeNewDescriptionForward() {
		assertBranchCanBeCreated("MAIN", branchName);
		
		assertDescriptionCanBeCreated("D1", ACCEPTABLE_ACCEPTABILITY_MAP, "MAIN", branchName);
		assertDescriptionExists("D1", "MAIN", branchName);
		
		assertBranchCanBeMerged(joinPath("MAIN", branchName), "MAIN", "Merge new description");
		
		assertDescriptionExists("D1", "MAIN", branchName);
		assertDescriptionExists("D1", "MAIN");
	}
	
	@Test
	public void mergeNewRelationshipForward() {
		assertBranchCanBeCreated("MAIN", branchName);
		
		assertRelationshipCanBeCreated("R1", "MAIN", branchName);
		assertRelationshipExists("R1", "MAIN", branchName);
		
		assertBranchCanBeMerged(joinPath("MAIN", branchName), "MAIN", "Merge new relationship");
		
		assertRelationshipExists("R1", "MAIN", branchName);
		assertRelationshipExists("R1", "MAIN");
	}
	
	@Test
	public void noMergeNewConceptDiverged() {
		assertBranchCanBeCreated("MAIN", branchName);
		
		assertConceptCanBeCreated("C1", "MAIN", branchName);
		assertConceptCanBeCreated("C2", "MAIN");
		
		assertConceptExists("C1", "MAIN", branchName);
		assertConceptNotExists("C1", "MAIN");
		assertConceptExists("C2", "MAIN");
		assertConceptNotExists("C2", "MAIN", branchName);
		
		assertBranchConflicts(joinPath("MAIN", branchName), "MAIN", "Merge new concept");
		
		assertConceptExists("C1", "MAIN", branchName);
		assertConceptNotExists("C1", "MAIN");
		assertConceptExists("C2", "MAIN");
		assertConceptNotExists("C2", "MAIN", branchName);
	}

	@Test
	public void noMergeNewDescriptionDiverged() {
		assertBranchCanBeCreated("MAIN", branchName);
		
		assertDescriptionCanBeCreated("D1", ACCEPTABLE_ACCEPTABILITY_MAP, "MAIN", branchName);
		assertDescriptionCanBeCreated("D2", ACCEPTABLE_ACCEPTABILITY_MAP, "MAIN");
		
		assertDescriptionExists("D1", "MAIN", branchName);
		assertDescriptionNotExists("D1", "MAIN");
		assertDescriptionExists("D2", "MAIN");
		assertDescriptionNotExists("D2", "MAIN", branchName);
		
		assertBranchConflicts(joinPath("MAIN", branchName), "MAIN", "Merge new description");
		
		assertDescriptionExists("D1", "MAIN", branchName);
		assertDescriptionNotExists("D1", "MAIN");
		assertDescriptionExists("D2", "MAIN");
		assertDescriptionNotExists("D2", "MAIN", branchName);
	}
	
	@Test
	public void noMergeNewRelationshipDiverged() {
		assertBranchCanBeCreated("MAIN", branchName);
		
		assertRelationshipCanBeCreated("R1", "MAIN", branchName);
		assertRelationshipCanBeCreated("R2", "MAIN");
		
		assertRelationshipExists("R1", "MAIN", branchName);
		assertRelationshipNotExists("R1", "MAIN");
		assertRelationshipExists("R2", "MAIN");
		assertRelationshipNotExists("R2", "MAIN", branchName);
		
		assertBranchConflicts(joinPath("MAIN", branchName), "MAIN", "Merge new relationship");
		
		assertRelationshipExists("R1", "MAIN", branchName);
		assertRelationshipNotExists("R1", "MAIN");
		assertRelationshipExists("R2", "MAIN");
		assertRelationshipNotExists("R2", "MAIN", branchName);
	}
	
	@Test
	public void rebaseNewConceptDiverged() {
		assertBranchCanBeCreated("MAIN", branchName);
		
		assertConceptCanBeCreated("C1", "MAIN", branchName);
		assertConceptCanBeCreated("C2", "MAIN");
		
		assertConceptExists("C1", "MAIN", branchName);
		assertConceptNotExists("C1", "MAIN");
		assertConceptExists("C2", "MAIN");
		assertConceptNotExists("C2", "MAIN", branchName);
		
		assertBranchCanBeRebased("MAIN", joinPath("MAIN", branchName), "Rebase new concept");
		
		assertConceptExists("C1", "MAIN", branchName);
		assertConceptNotExists("C1", "MAIN");
		assertConceptExists("C2", "MAIN");
		assertConceptExists("C2", "MAIN", branchName); // C2 becomes visible after rebasing
	}

	@Test
	public void rebaseNewDescriptionDiverged() {
		assertBranchCanBeCreated("MAIN", branchName);
		
		assertDescriptionCanBeCreated("D1", ACCEPTABLE_ACCEPTABILITY_MAP, "MAIN", branchName);
		assertDescriptionCanBeCreated("D2", ACCEPTABLE_ACCEPTABILITY_MAP, "MAIN");
		
		assertDescriptionExists("D1", "MAIN", branchName);
		assertDescriptionNotExists("D1", "MAIN");
		assertDescriptionExists("D2", "MAIN");
		assertDescriptionNotExists("D2", "MAIN", branchName);
		
		assertBranchCanBeRebased("MAIN", joinPath("MAIN", branchName), "Rebase new description");
		
		assertDescriptionExists("D1", "MAIN", branchName);
		assertDescriptionNotExists("D1", "MAIN");
		assertDescriptionExists("D2", "MAIN");
		assertDescriptionExists("D2", "MAIN", branchName); // D2 becomes visible after rebasing
	}
	
	@Test
	public void rebaseNewRelationshipDiverged() {
		assertBranchCanBeCreated("MAIN", branchName);
		
		assertRelationshipCanBeCreated("R1", "MAIN", branchName);
		assertRelationshipCanBeCreated("R2", "MAIN");
		
		assertRelationshipExists("R1", "MAIN", branchName);
		assertRelationshipNotExists("R1", "MAIN");
		assertRelationshipExists("R2", "MAIN");
		assertRelationshipNotExists("R2", "MAIN", branchName);
		
		assertBranchCanBeRebased("MAIN", joinPath("MAIN", branchName), "Rebase new relationship");
		
		assertRelationshipExists("R1", "MAIN", branchName);
		assertRelationshipNotExists("R1", "MAIN");
		assertRelationshipExists("R2", "MAIN");
		assertRelationshipExists("R2", "MAIN", branchName); // R2 becomes visible after rebasing
	}
	
	@Test
	public void noRebaseNewPT() {
		assertBranchCanBeCreated("MAIN", branchName);
		
		assertDescriptionCanBeCreated("D1", PREFERRED_ACCEPTABILITY_MAP, "MAIN", branchName);
		assertDescriptionCanBeCreated("D2", PREFERRED_ACCEPTABILITY_MAP, "MAIN");
		
		assertDescriptionExists("D1", "MAIN", branchName);
		assertDescriptionNotExists("D1", "MAIN");
		assertDescriptionExists("D2", "MAIN");
		assertDescriptionNotExists("D2", "MAIN", branchName);
		
		assertBranchConflicts("MAIN", joinPath("MAIN", branchName), "Rebase new PT");
		
		assertDescriptionExists("D1", "MAIN", branchName);
		assertDescriptionNotExists("D1", "MAIN");
		assertDescriptionExists("D2", "MAIN");
		assertDescriptionNotExists("D2", "MAIN", branchName); // D2 did not become visible because the rebase was rejected
	}
	
	private void assertDescriptionChangesConflict(Map<?, ?> changesOnMain, Map<?, ?> changesOnBranch) {
		mergeNewDescriptionForward();

		assertDescriptionCanBeUpdated("D1", changesOnMain, "MAIN");
		assertDescriptionCanBeUpdated("D1", changesOnBranch, "MAIN", branchName);
		
		assertBranchConflicts("MAIN", joinPath("MAIN", branchName), "Rebase conflicting description change");
	}
	
	@Test
	public void noRebaseConflictingDescription() {
		Map<?, ?> changesOnMain = ImmutableMap.of(
			"caseSignificance", CaseSignificance.CASE_INSENSITIVE,
			"commitComment", "Changed case significance on MAIN"
		);
		
		Map<?, ?> changesOnBranch = ImmutableMap.of(
			"caseSignificance", CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE,
			"commitComment", "Changed case significance on branch"
		);
		
		assertDescriptionChangesConflict(changesOnMain, changesOnBranch);
	}
	
	@Test
	public void noRebaseConflictingDescriptionMultipleChanges() {
		Map<?, ?> changesOnMain = ImmutableMap.of(
			"caseSignificance", CaseSignificance.CASE_INSENSITIVE,
			"moduleId", "900000000000013009",
			"commitComment", "Changed case significance and module on MAIN"
		);
		
		Map<?, ?> changesOnBranch = ImmutableMap.of(
			"caseSignificance", CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE,
			"moduleId", "900000000000443000",
			"commitComment", "Changed case significance and module on branch"
		);
		
		assertDescriptionChangesConflict(changesOnMain, changesOnBranch);
	}
	
	@Test
	public void noRebaseChangedConceptOnBranchDeletedOnParent() {
		mergeNewConceptForward();
		
		Map<?, ?> changeOnBranch = ImmutableMap.of(
			"definitionStatus", DefinitionStatus.FULLY_DEFINED,
			"commitComment", "Changed definition status on branch"
		);
		
		assertConceptCanBeDeleted("C1", "MAIN");
		assertConceptCanBeUpdated("C1", changeOnBranch, "MAIN", branchName);
		
		assertBranchConflicts("MAIN", joinPath("MAIN", branchName), "Rebase conflicting concept deletion");
	}
	
	@Test
	public void rebaseChangedConceptOnParentDeletedOnBranch() {
		mergeNewConceptForward();
		
		Map<?, ?> changeOnMain = ImmutableMap.of(
			"definitionStatus", DefinitionStatus.FULLY_DEFINED,
			"commitComment", "Changed definition status on MAIN"
		);
		
		assertConceptCanBeUpdated("C1", changeOnMain, "MAIN");
		assertConceptCanBeDeleted("C1", "MAIN", branchName);
		
		assertBranchCanBeRebased("MAIN", joinPath("MAIN", branchName), "Rebase concept deletion");
		
		assertConceptExists("C1", "MAIN");
		assertConceptNotExists("C1", "MAIN", branchName);
	}
	
	@Test
	public void rebaseAndMergeChangedConceptOnParentDeletedOnBranch() {
		rebaseChangedConceptOnParentDeletedOnBranch();
		
		assertBranchCanBeMerged(joinPath("MAIN", branchName), "MAIN", "Merge concept deletion back to MAIN");
		assertConceptNotExists("C1", "MAIN", branchName);
		assertConceptNotExists("C1", "MAIN");
	}
	
	@Test
	public void rebaseAndMergeChangedDescriptionMultipleChanges() {
		mergeNewDescriptionForward();
		
		assertDescriptionCanBeCreated("D2", ACCEPTABLE_ACCEPTABILITY_MAP, "MAIN");
		
		Map<?, ?> changesOnBranch = ImmutableMap.of(
			"caseSignificance", CaseSignificance.CASE_INSENSITIVE,
			"moduleId", "900000000000013009",
			"commitComment", "Changed case significance and module on branch"
		);

		assertDescriptionCanBeUpdated("D1", changesOnBranch, "MAIN", branchName);
		assertBranchCanBeRebased("MAIN", joinPath("MAIN", branchName), "Rebase description update");
		assertBranchCanBeMerged(joinPath("MAIN", branchName), "MAIN", "Merge description update");
		
		assertDescriptionExists("D1", "MAIN");
		assertDescriptionExists("D2", "MAIN");
		
		givenAuthenticatedRequest(SCT_API)
		.when()
			.get("/MAIN/descriptions/{id}", symbolicNameToIds.get("D1"))
		.then()
		.assertThat()
			.statusCode(200)
		.and()
			.body("caseSignificance", equalTo(CaseSignificance.CASE_INSENSITIVE.name()))
		.and()
			.body("moduleId", equalTo("900000000000013009"));
	}
	
	@Test
	public void rebaseAndMergeNewDescriptionBothDeleted() {
		mergeNewDescriptionForward();
		
		assertDescriptionCanBeCreated("D2", ACCEPTABLE_ACCEPTABILITY_MAP, "MAIN");
		assertDescriptionCanBeDeleted("D1", "MAIN");
		assertDescriptionCanBeDeleted("D1", "MAIN", branchName);
		
		assertBranchCanBeRebased("MAIN", joinPath("MAIN", branchName), "Rebase description dual deletion");
		assertBranchCanBeMerged(joinPath("MAIN", branchName), "MAIN", "Merge description dual deletion");
		
		assertDescriptionNotExists("D1", "MAIN");
		assertDescriptionNotExists("D1", "MAIN", branchName);
		assertDescriptionExists("D2", "MAIN");
		assertDescriptionExists("D2", "MAIN", branchName);
	}
}
