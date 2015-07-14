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

import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.FULLY_SPECIFIED_NAME;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.SYNONYM;
import static com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants.ACCEPTABLE_ACCEPTABILITY_MAP;
import static com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants.PREFERRED_ACCEPTABILITY_MAP;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.assertBranchCanBeMerged;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.assertBranchCanBeRebased;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.assertBranchConflicts;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.givenBranchWithPath;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentHasProperty;
import static com.google.common.collect.Maps.newHashMap;

import java.util.Date;
import java.util.Map;

import org.junit.Test;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.domain.CaseSignificance;
import com.b2international.snowowl.snomed.api.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * @since 2.0
 */
public class SnomedMergeApiTest extends AbstractSnomedApiTest {

	private final Map<String, String> symbolicNameMap = newHashMap();

	// --------------------------------------------------------
	// Symbolic component existence checks
	// --------------------------------------------------------

	private void assertConceptExists(final IBranchPath branchPath, final String symbolicName) {
		SnomedComponentApiAssert.assertConceptExists(branchPath, symbolicNameMap.get(symbolicName));
	}

	private void assertDescriptionExists(final IBranchPath branchPath, final String symbolicName) {
		SnomedComponentApiAssert.assertDescriptionExists(branchPath, symbolicNameMap.get(symbolicName));
	}

	private void assertRelationshipExists(final IBranchPath branchPath, final String symbolicName) {
		SnomedComponentApiAssert.assertRelationshipExists(branchPath, symbolicNameMap.get(symbolicName));
	}

	private void assertConceptNotExists(final IBranchPath branchPath, final String symbolicName) {
		SnomedComponentApiAssert.assertConceptNotExists(branchPath, symbolicNameMap.get(symbolicName));
	}

	private void assertDescriptionNotExists(final IBranchPath branchPath, final String symbolicName) {
		SnomedComponentApiAssert.assertDescriptionNotExists(branchPath, symbolicNameMap.get(symbolicName));
	}

	private void assertRelationshipNotExists(final IBranchPath branchPath, final String symbolicName) {
		SnomedComponentApiAssert.assertRelationshipNotExists(branchPath, symbolicNameMap.get(symbolicName));
	}

	// --------------------------------------------------------
	// Symbolic component creation
	// --------------------------------------------------------

	private void assertComponentCreated(final IBranchPath branchPath, 
			final String symbolicName, 
			final SnomedComponentType componentType, 
			final Map<?, ?> requestBody) {

		symbolicNameMap.put(symbolicName, SnomedComponentApiAssert.assertComponentCreated(branchPath, componentType, requestBody));
	}

	private void assertConceptCreated(final IBranchPath branchPath, final String symbolicName) {
		final Date creationDate = new Date();

		final Map<?, ?> fsnDescription = ImmutableMap.<String, Object>builder()
				.put("typeId", FULLY_SPECIFIED_NAME)
				.put("term", "New FSN at " + creationDate)
				.put("languageCode", "en")
				.put("acceptability", PREFERRED_ACCEPTABILITY_MAP)
				.build();

		final Map<?, ?> ptDescription = ImmutableMap.<String, Object>builder()
				.put("typeId", SYNONYM)
				.put("term", "New PT at " + creationDate)
				.put("languageCode", "en")
				.put("acceptability", PREFERRED_ACCEPTABILITY_MAP)
				.build();

		final ImmutableMap.Builder<String, Object> conceptBuilder = ImmutableMap.<String, Object>builder()
				.put("commitComment", "New concept")
				.put("parentId", Concepts.ROOT_CONCEPT)
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("descriptions", ImmutableList.of(fsnDescription, ptDescription));

		assertComponentCreated(branchPath, symbolicName, SnomedComponentType.CONCEPT, conceptBuilder.build());
	}

	private void assertDescriptionCreated(final IBranchPath branchPath, final String symbolicName, final Map<?, ?> acceptabilityMap) {
		final Date creationDate = new Date();

		final Map<?, ?> requestBody = ImmutableMap.builder()
				.put("conceptId", Concepts.ROOT_CONCEPT)
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("typeId", Concepts.SYNONYM)
				.put("term", "New description at " + creationDate)
				.put("languageCode", "en")
				.put("acceptability", acceptabilityMap)
				.put("commitComment", "New description")
				.build();

		assertComponentCreated(branchPath, symbolicName, SnomedComponentType.DESCRIPTION, requestBody);
	}

	private void assertRelationshipCreated(final IBranchPath branchPath, final String symbolicName) {
		final Map<?, ?> requestBody = ImmutableMap.builder()
				.put("sourceId", Concepts.ROOT_CONCEPT)
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("typeId", "116676008") // Associated morphology
				.put("destinationId", "49755003") // Morphologic abnormality
				.put("commitComment", "New relationship")
				.build();

		assertComponentCreated(branchPath, symbolicName, SnomedComponentType.RELATIONSHIP, requestBody);
	}

	// --------------------------------------------------------
	// Symbolic component updates
	// --------------------------------------------------------

	private void assertComponentCanBeUpdated(final IBranchPath branchPath, 
			final String symbolicName, 
			final SnomedComponentType componentType, 
			final Map<?, ?> requestBody) {

		SnomedComponentApiAssert.assertComponentCanBeUpdated(branchPath, componentType, symbolicNameMap.get(symbolicName), requestBody);
	}

	private void assertConceptCanBeUpdated(final IBranchPath branchPath, final String symbolicName, final Map<?, ?> requestBody) {
		assertComponentCanBeUpdated(branchPath, symbolicName, SnomedComponentType.CONCEPT, requestBody);
	}

	private void assertDescriptionCanBeUpdated(final IBranchPath branchPath, final String symbolicName, final Map<?, ?> requestBody) {
		assertComponentCanBeUpdated(branchPath, symbolicName, SnomedComponentType.DESCRIPTION, requestBody);
	}

	// --------------------------------------------------------
	// Symbolic component deletion
	// --------------------------------------------------------

	private void assertComponentCanBeDeleted(final IBranchPath branchPath, final String symbolicName, final SnomedComponentType componentType) {
		SnomedComponentApiAssert.assertComponentCanBeDeleted(branchPath, componentType, symbolicNameMap.get(symbolicName));
	}

	private void assertConceptCanBeDeleted(final IBranchPath branchPath, final String symbolicName) {
		assertComponentCanBeDeleted(branchPath, symbolicName, SnomedComponentType.CONCEPT);
	}

	private void assertDescriptionCanBeDeleted(final IBranchPath branchPath, final String symbolicName) {
		assertComponentCanBeDeleted(branchPath, symbolicName, SnomedComponentType.DESCRIPTION);
	}

	@Test
	public void mergeNewConceptForward() {
		givenBranchWithPath(testBranchPath);

		assertConceptCreated(testBranchPath, "C1");
		assertConceptExists(testBranchPath, "C1");

		assertBranchCanBeMerged(testBranchPath, "Merge new concept");

		assertConceptExists(testBranchPath, "C1");
		assertConceptExists(testBranchPath.getParent(), "C1");
	}

	@Test
	public void mergeNewDescriptionForward() {
		givenBranchWithPath(testBranchPath);

		assertDescriptionCreated(testBranchPath, "D1", ACCEPTABLE_ACCEPTABILITY_MAP);
		assertDescriptionExists(testBranchPath, "D1");

		assertBranchCanBeMerged(testBranchPath, "Merge new description");

		assertDescriptionExists(testBranchPath, "D1");
		assertDescriptionExists(testBranchPath.getParent(), "D1");
	}

	@Test
	public void mergeNewRelationshipForward() {
		givenBranchWithPath(testBranchPath);

		assertRelationshipCreated(testBranchPath, "R1");
		assertRelationshipExists(testBranchPath, "R1");

		assertBranchCanBeMerged(testBranchPath, "Merge new relationship");

		assertRelationshipExists(testBranchPath, "R1");
		assertRelationshipExists(testBranchPath.getParent(), "R1");
	}

	@Test
	public void noMergeNewConceptDiverged() {
		givenBranchWithPath(testBranchPath);

		assertConceptCreated(testBranchPath, "C1");
		assertConceptExists(testBranchPath, "C1");
		assertConceptNotExists(testBranchPath.getParent(), "C1");

		assertConceptCreated(testBranchPath.getParent(), "C2");
		assertConceptExists(testBranchPath.getParent(), "C2");
		assertConceptNotExists(testBranchPath, "C2");

		assertBranchConflicts(testBranchPath, testBranchPath.getParent(), "Merge new concept");

		assertConceptExists(testBranchPath, "C1");
		assertConceptNotExists(testBranchPath.getParent(), "C1");
		assertConceptExists(testBranchPath.getParent(), "C2");
		assertConceptNotExists(testBranchPath, "C2");
	}

	@Test
	public void noMergeNewDescriptionDiverged() {
		givenBranchWithPath(testBranchPath);

		assertDescriptionCreated(testBranchPath, "D1", SnomedApiTestConstants.ACCEPTABLE_ACCEPTABILITY_MAP);
		assertDescriptionExists(testBranchPath, "D1");
		assertDescriptionNotExists(testBranchPath.getParent(), "D1");

		assertDescriptionCreated(testBranchPath.getParent(), "D2", SnomedApiTestConstants.ACCEPTABLE_ACCEPTABILITY_MAP);
		assertDescriptionExists(testBranchPath.getParent(), "D2");
		assertDescriptionNotExists(testBranchPath, "D2");

		assertBranchConflicts(testBranchPath, testBranchPath.getParent(), "Merge new description");

		assertDescriptionExists(testBranchPath, "D1");
		assertDescriptionNotExists(testBranchPath.getParent(), "D1");
		assertDescriptionExists(testBranchPath.getParent(), "D2");
		assertDescriptionNotExists(testBranchPath, "D2");
	}

	@Test
	public void noMergeNewRelationshipDiverged() {
		givenBranchWithPath(testBranchPath);

		assertRelationshipCreated(testBranchPath, "R1");
		assertRelationshipExists(testBranchPath, "R1");
		assertRelationshipNotExists(testBranchPath.getParent(), "R1");

		assertRelationshipCreated(testBranchPath.getParent(), "R2");
		assertRelationshipExists(testBranchPath.getParent(), "R2");
		assertRelationshipNotExists(testBranchPath, "R2");

		assertBranchConflicts(testBranchPath, testBranchPath.getParent(), "Merge new relationship");

		assertRelationshipExists(testBranchPath, "R1");
		assertRelationshipNotExists(testBranchPath.getParent(), "R1");
		assertRelationshipExists(testBranchPath.getParent(), "R2");
		assertRelationshipNotExists(testBranchPath, "R2");
	}

	@Test
	public void rebaseNewConceptDiverged() {
		givenBranchWithPath(testBranchPath);

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
		givenBranchWithPath(testBranchPath);

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
		givenBranchWithPath(testBranchPath);

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
	public void noRebaseNewPreferredTerm() {
		givenBranchWithPath(testBranchPath);

		assertDescriptionCreated(testBranchPath, "D1", PREFERRED_ACCEPTABILITY_MAP);
		assertDescriptionExists(testBranchPath, "D1");
		assertDescriptionNotExists(testBranchPath.getParent(), "D1");

		assertDescriptionCreated(testBranchPath.getParent(), "D2", PREFERRED_ACCEPTABILITY_MAP);
		assertDescriptionExists(testBranchPath.getParent(), "D2");
		assertDescriptionNotExists(testBranchPath,"D2");

		assertBranchConflicts(testBranchPath.getParent(), testBranchPath, "Rebase new preferred term");

		assertDescriptionExists(testBranchPath, "D1");
		assertDescriptionNotExists(testBranchPath.getParent(), "D1");
		assertDescriptionExists(testBranchPath.getParent(), "D2");
		assertDescriptionNotExists(testBranchPath,"D2"); // D2 did not become visible because the rebase was rejected
	}

	private void assertDescriptionChangesConflict(final Map<?, ?> changesOnParent, final Map<?, ?> changesOnBranch) {
		mergeNewDescriptionForward();

		assertDescriptionCanBeUpdated(testBranchPath.getParent(), "D1", changesOnParent);
		assertDescriptionCanBeUpdated(testBranchPath, "D1", changesOnBranch);

		assertBranchConflicts(testBranchPath.getParent(), testBranchPath, "Rebase conflicting description change");
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

		assertBranchConflicts(testBranchPath.getParent(), testBranchPath, "Rebase conflicting concept deletion");
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

		assertBranchCanBeRebased(testBranchPath, "Rebase description dual deletion");
		assertBranchCanBeMerged(testBranchPath, "Merge description dual deletion");

		assertDescriptionNotExists(testBranchPath, "D1");
		assertDescriptionNotExists(testBranchPath.getParent(), "D1");
		assertDescriptionExists(testBranchPath, "D2");
		assertDescriptionExists(testBranchPath.getParent(), "D2");
	}
}
