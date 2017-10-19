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
package com.b2international.snowowl.snomed.api.rest.ext;

import static com.b2international.snowowl.snomed.api.rest.CodeSystemRestRequests.getCodeSystem;
import static com.b2international.snowowl.snomed.api.rest.CodeSystemRestRequests.updateCodeSystem;
import static com.b2international.snowowl.snomed.api.rest.CodeSystemVersionRestRequests.createVersion;
import static com.b2international.snowowl.snomed.api.rest.CodeSystemVersionRestRequests.getNextAvailableEffectiveDateAsString;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingRestRequests.createBranch;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.createComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.DEFAULT_LANGUAGE_CODE;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewConcept;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.merge;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.AfterClass;
import org.junit.Test;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.merge.Merge;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.BranchBase;
import com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.response.ValidatableResponse;

/**
 * @since 4.7
 */
@BranchBase(value = SnomedApiTestConstants.EXTENSION_PATH, isolateTests = false)
public class SnomedExtensionUpgradeTest extends AbstractSnomedApiTest {

	@Test
	public void upgradeWithoutChanges() {
		String effectiveDate = getNextAvailableEffectiveDateAsString(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME);
		String versionId = "v1";
		createVersion(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME, versionId, effectiveDate).statusCode(201);

		IBranchPath targetPath = BranchPathUtils.createPath(SnomedApiTestConstants.PATH_JOINER.join(
				Branch.MAIN_PATH, 
				versionId, 
				SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME));

		createBranch(targetPath).statusCode(201);		

		merge(branchPath, targetPath, "Upgraded B2i extension to v1.").body("status", equalTo(Merge.Status.COMPLETED.name()));

		Map<?, ?> updateRequest = ImmutableMap.builder()
				.put("repositoryUuid", SnomedDatastoreActivator.REPOSITORY_UUID)
				.put("branchPath", targetPath.getPath())
				.build();

		updateCodeSystem(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME, updateRequest).statusCode(204);

		getCodeSystem(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME).statusCode(200)
		.body("branchPath", equalTo(targetPath.getPath()));
	}

	@Test
	public void upgradeWithNewConceptOnSource() {
		String effectiveDate = getNextAvailableEffectiveDateAsString(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME);
		String versionId = "v2";
		createVersion(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME, versionId, effectiveDate).statusCode(201);

		IBranchPath targetPath = BranchPathUtils.createPath(SnomedApiTestConstants.PATH_JOINER.join(
				Branch.MAIN_PATH, 
				versionId, 
				SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME));

		createBranch(targetPath).statusCode(201);		

		String conceptId = createNewConcept(branchPath);

		merge(branchPath, targetPath, "Upgraded B2i extension to v2.").body("status", equalTo(Merge.Status.COMPLETED.name()));

		Map<?, ?> updateRequest = ImmutableMap.builder()
				.put("repositoryUuid", SnomedDatastoreActivator.REPOSITORY_UUID)
				.put("branchPath", targetPath.getPath())
				.build();

		updateCodeSystem(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME, updateRequest).statusCode(204);

		getCodeSystem(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME).statusCode(200)
		.body("branchPath", equalTo(targetPath.getPath()));

		getComponent(targetPath, SnomedComponentType.CONCEPT, conceptId).statusCode(200);
	}

	@Test
	public void upgradeWithNewConceptOnTarget() {
		String effectiveDate = getNextAvailableEffectiveDateAsString(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME);
		String versionId = "v3";
		createVersion(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME, versionId, effectiveDate).statusCode(201);

		IBranchPath targetPath = BranchPathUtils.createPath(SnomedApiTestConstants.PATH_JOINER.join(
				Branch.MAIN_PATH, 
				versionId, 
				SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME));

		createBranch(targetPath).statusCode(201);		

		String conceptId = createNewConcept(targetPath);

		merge(branchPath, targetPath, "Upgraded B2i extension to v3.").body("status", equalTo(Merge.Status.COMPLETED.name()));

		Map<?, ?> updateRequest = ImmutableMap.builder()
				.put("repositoryUuid", SnomedDatastoreActivator.REPOSITORY_UUID)
				.put("branchPath", targetPath.getPath())
				.build();

		updateCodeSystem(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME, updateRequest).statusCode(204);

		getCodeSystem(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME).statusCode(200)
		.body("branchPath", equalTo(targetPath.getPath()));

		getComponent(targetPath, SnomedComponentType.CONCEPT, conceptId).statusCode(200);
	}

	@Test
	public void upgradeWithConflictingContent() {
		String effectiveDate = getNextAvailableEffectiveDateAsString(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME);
		String versionId = "v4";
		createVersion(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME, versionId, effectiveDate).statusCode(201);

		IBranchPath targetPath = BranchPathUtils.createPath(SnomedApiTestConstants.PATH_JOINER.join(
				Branch.MAIN_PATH, 
				versionId, 
				SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME));

		createBranch(targetPath).statusCode(201);		

		Map<?, ?> requestBody = ImmutableMap.builder()
				.put("id", "476216051000154119") // Description of Date-time reference set
				.put("conceptId", Concepts.ROOT_CONCEPT)
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("typeId", Concepts.SYNONYM)
				.put("term", "Synonym of root concept")
				.put("languageCode", "en")
				.put("acceptability", SnomedApiTestConstants.UK_ACCEPTABLE_MAP)
				.put("caseSignificance", CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE)
				.put("commitComment", "Created new synonym with duplicate SCTID")
				.build();

		createComponent(targetPath, SnomedComponentType.DESCRIPTION, requestBody).statusCode(201);

		merge(branchPath, targetPath, "Upgraded B2i extension to v4.").body("status", equalTo(Merge.Status.CONFLICTS.name()));
	}

	@Test
	public void upgradeWithDonatedContent() {
		
		Map<?, ?> statedRelationshipRequestBody = ImmutableMap.builder()
				.put("moduleId", Concepts.MODULE_B2I_EXTENSION)
				.put("namespaceId", Concepts.B2I_NAMESPACE)
				.put("typeId", Concepts.IS_A)
				.put("destinationId", Concepts.ROOT_CONCEPT)
				.put("characteristicType", CharacteristicType.STATED_RELATIONSHIP)
				.build();
		
		Map<?, ?> inferredRelationshipRequestBody = ImmutableMap.builder()
				.put("moduleId", Concepts.MODULE_B2I_EXTENSION)
				.put("namespaceId", Concepts.B2I_NAMESPACE)
				.put("typeId", Concepts.IS_A)
				.put("destinationId", Concepts.ROOT_CONCEPT)
				.put("characteristicType", CharacteristicType.INFERRED_RELATIONSHIP)
				.build();

		String fsnTerm = "FSN of concept";
		
		Map<?, ?> fsnRequestBody = ImmutableMap.builder()
				.put("moduleId", Concepts.MODULE_B2I_EXTENSION)
				.put("namespaceId", Concepts.B2I_NAMESPACE)
				.put("typeId", Concepts.FULLY_SPECIFIED_NAME)
				.put("term", fsnTerm)
				.put("languageCode", DEFAULT_LANGUAGE_CODE)
				.put("acceptability", SnomedApiTestConstants.UK_PREFERRED_MAP)
				.build();
		
		String ptTerm = "PT of donated concept";
		
		Map<?, ?> ptRequestBody = ImmutableMap.builder()
				.put("moduleId", Concepts.MODULE_B2I_EXTENSION)
				.put("namespaceId", Concepts.B2I_NAMESPACE)
				.put("typeId", Concepts.SYNONYM)
				.put("term", ptTerm)
				.put("languageCode", DEFAULT_LANGUAGE_CODE)
				.put("acceptability", SnomedApiTestConstants.UK_PREFERRED_MAP)
				.build();
		
		String synonymTerm = "Synonym of donated concept";
		
		Map<?, ?> synonymRequestBody = ImmutableMap.builder()
				.put("moduleId", Concepts.MODULE_B2I_EXTENSION)
				.put("namespaceId", Concepts.B2I_NAMESPACE)
				.put("typeId", Concepts.SYNONYM)
				.put("term", synonymTerm)
				.put("languageCode", DEFAULT_LANGUAGE_CODE)
				.put("acceptability", SnomedApiTestConstants.UK_ACCEPTABLE_MAP)
				.build();

		Map<String, Object> conceptRequestBody = ImmutableMap.<String, Object>builder()
				.put("moduleId", Concepts.MODULE_B2I_EXTENSION)
				.put("namespaceId", Concepts.B2I_NAMESPACE)
				.put("descriptions", ImmutableList.of(fsnRequestBody, ptRequestBody, synonymRequestBody))
				.put("relationships", ImmutableList.of(statedRelationshipRequestBody, inferredRelationshipRequestBody))
				.put("commitComment", "Added donated concept")
				.build();
		
		String donatedConceptId = lastPathSegment(createComponent(branchPath, SnomedComponentType.CONCEPT, conceptRequestBody)
				.statusCode(201)
				.extract().header("Location"));
		
		Map<?, ?> intStatedRelationshipRequestBody = ImmutableMap.builder()
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("typeId", Concepts.IS_A)
				.put("destinationId", Concepts.ROOT_CONCEPT)
				.put("characteristicType", CharacteristicType.STATED_RELATIONSHIP)
				.build();
		
		Map<?, ?> intInferredRelationshipRequestBody = ImmutableMap.builder()
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("typeId", Concepts.IS_A)
				.put("destinationId", Concepts.ROOT_CONCEPT)
				.put("characteristicType", CharacteristicType.INFERRED_RELATIONSHIP)
				.build();

		Map<?, ?> intFsnRequestBody = ImmutableMap.builder()
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("typeId", Concepts.FULLY_SPECIFIED_NAME)
				.put("term", fsnTerm)
				.put("languageCode", DEFAULT_LANGUAGE_CODE)
				.put("acceptability", SnomedApiTestConstants.UK_PREFERRED_MAP)
				.build();
		
		Map<?, ?> intPtRequestBody = ImmutableMap.builder()
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("typeId", Concepts.SYNONYM)
				.put("term", ptTerm)
				.put("languageCode", DEFAULT_LANGUAGE_CODE)
				.put("acceptability", SnomedApiTestConstants.UK_PREFERRED_MAP)
				.build();

		Map<String, Object> intConceptRequestBody = ImmutableMap.<String, Object>builder()
				.put("id", donatedConceptId)
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("descriptions", ImmutableList.of(intFsnRequestBody, intPtRequestBody))
				.put("relationships", ImmutableList.of(intStatedRelationshipRequestBody, intInferredRelationshipRequestBody))
				.put("commitComment", "Added donated concept to INT")
				.build();
		
		String intConceptId = lastPathSegment(createComponent(BranchPathUtils.createMainPath(), SnomedComponentType.CONCEPT, intConceptRequestBody)
				.statusCode(201)
				.body(equalTo(""))
				.extract().header("Location"));
		
		assertEquals(donatedConceptId, intConceptId);
		
		String effectiveDate = getNextAvailableEffectiveDateAsString(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME);
		String versionId = "v5";
		createVersion(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME, versionId, effectiveDate).statusCode(201);
		
		IBranchPath targetPath = BranchPathUtils.createPath(SnomedApiTestConstants.PATH_JOINER.join(
				Branch.MAIN_PATH, 
				versionId, 
				SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME));

		createBranch(targetPath).statusCode(201);
		
		ValidatableResponse response = merge(branchPath, targetPath, "Upgraded B2i extension to v5");
		response.body("status", equalTo(Merge.Status.CONFLICTS.name()));
		
	}
	
	
	@AfterClass
	public static void restoreB2iCodeSystem() {
		Map<?, ?> updateRequest = ImmutableMap.builder()
				.put("repositoryUuid", SnomedDatastoreActivator.REPOSITORY_UUID)
				.put("branchPath", SnomedApiTestConstants.EXTENSION_PATH)
				.build();

		updateCodeSystem(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME, updateRequest).statusCode(204);
	}
}
