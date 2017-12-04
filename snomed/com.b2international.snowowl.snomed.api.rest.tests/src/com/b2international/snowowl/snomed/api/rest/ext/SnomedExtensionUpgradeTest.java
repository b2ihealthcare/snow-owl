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
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.updateComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.DEFAULT_LANGUAGE_CODE;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewConcept;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.merge;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Iterables;

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
	public void upgradeWithBackAndForthDonatedConcept() {
	
		// create new version on MAIN with core module and initial char case insensitive case significance
		
		String descriptionTerm = "Description term";
		
		Map<?, ?> requestBody = ImmutableMap.builder()
				.put("conceptId", Concepts.ROOT_CONCEPT)
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("typeId", Concepts.SYNONYM)
				.put("term", descriptionTerm)
				.put("languageCode", DEFAULT_LANGUAGE_CODE)
				.put("acceptability", SnomedApiTestConstants.UK_ACCEPTABLE_MAP)
				.put("caseSignificance", CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE)
				.put("commitComment", "Created new synonym")
				.build();

		String descriptionId = lastPathSegment(createComponent(BranchPathUtils.createMainPath(), SnomedComponentType.DESCRIPTION, requestBody)
				.statusCode(201)
				.extract().header("Location"));
		
		// version new description
		
		String effectiveDate = getNextAvailableEffectiveDateAsString(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME);
		String versionId = "v10";
		createVersion(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME, versionId, effectiveDate).statusCode(201);
		
		getComponent(BranchPathUtils.createMainPath(), SnomedComponentType.DESCRIPTION, descriptionId)
			.statusCode(200)
			.body("released", equalTo(true))
			.body("effectiveTime", equalTo(effectiveDate))
			.body("moduleId", equalTo(Concepts.MODULE_SCT_CORE))
			.body("caseSignificance", equalTo(CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE.toString()));
		
		// upgrade extension to latest INT version
		
		IBranchPath targetPath = BranchPathUtils.createPath(SnomedApiTestConstants.PATH_JOINER.join(
				Branch.MAIN_PATH, 
				versionId, 
				SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME));

		createBranch(targetPath).statusCode(201);
		
		merge(branchPath, targetPath, "Upgraded B2i extension to v10").body("status", equalTo(Merge.Status.COMPLETED.name()));

		Map<?, ?> updateRequest = ImmutableMap.builder()
				.put("repositoryUuid", SnomedDatastoreActivator.REPOSITORY_UUID)
				.put("branchPath", targetPath.getPath())
				.build();

		updateCodeSystem(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME, updateRequest).statusCode(204);
		getCodeSystem(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME).statusCode(200).body("branchPath", equalTo(targetPath.getPath()));
		
		// update description on extension, change module and case significance
		
		Map<?, ?> descriptionUpdateRequest = ImmutableMap.builder()
				.put("caseSignificance", CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE)
				.put("moduleId", Concepts.MODULE_B2I_EXTENSION)
				.put("commitComment", "Changed case significance on description")
				.build();

		updateComponent(targetPath, SnomedComponentType.DESCRIPTION, descriptionId, descriptionUpdateRequest).statusCode(204);
		
		getComponent(targetPath, SnomedComponentType.DESCRIPTION, descriptionId)
				.statusCode(200)
				.body("released", equalTo(true))
				.body("effectiveTime", nullValue())
				.body("moduleId", equalTo(Concepts.MODULE_B2I_EXTENSION))
				.body("caseSignificance", equalTo(CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE.toString()));
		
		// version extension
		
		String extensionEffectiveDate = getNextAvailableEffectiveDateAsString(SNOMED_B2I_SHORT_NAME);
		String extensionVersionId = "ev2";

		createVersion(SNOMED_B2I_SHORT_NAME, extensionVersionId, extensionEffectiveDate).statusCode(201);
		
		getComponent(targetPath, SnomedComponentType.DESCRIPTION, descriptionId)
			.statusCode(200)
			.body("released", equalTo(true))
			.body("effectiveTime", equalTo(extensionEffectiveDate));
		
		// update description on MAIN ("take" extension changes and apply it in INT) 
		
		Map<?, ?> intDescriptionUpdateRequest = ImmutableMap.builder()
				.put("caseSignificance", CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE)
				.put("commitComment", "Changed case significance on description on MAIN")
				.build();

		updateComponent(BranchPathUtils.createMainPath(), SnomedComponentType.DESCRIPTION, descriptionId, intDescriptionUpdateRequest).statusCode(204);
		
		// version INT on MAIN
		
		String newIntEffectiveDate = getNextAvailableEffectiveDateAsString(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME);
		String newIntversionId = "v11";
		createVersion(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME, newIntversionId, newIntEffectiveDate).statusCode(201);
		
		getComponent(BranchPathUtils.createMainPath(), SnomedComponentType.DESCRIPTION, descriptionId)
				.statusCode(200)
				.body("released", equalTo(true))
				.body("effectiveTime", equalTo(newIntEffectiveDate))
				.body("moduleId", equalTo(Concepts.MODULE_SCT_CORE))
				.body("caseSignificance", equalTo(CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE.toString()));
		
		// upgrade extension to new INT version
		
		IBranchPath newTargetPath = BranchPathUtils.createPath(SnomedApiTestConstants.PATH_JOINER.join(
				Branch.MAIN_PATH, 
				newIntversionId, 
				SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME));

		createBranch(newTargetPath).statusCode(201);
		
		merge(targetPath, newTargetPath, "Upgraded B2i extension to v11").body("status", equalTo(Merge.Status.COMPLETED.name()));

		Map<?, ?> newUpdateRequest = ImmutableMap.builder()
				.put("repositoryUuid", SnomedDatastoreActivator.REPOSITORY_UUID)
				.put("branchPath", newTargetPath.getPath())
				.build();

		updateCodeSystem(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME, newUpdateRequest).statusCode(204);
		getCodeSystem(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME).statusCode(200).body("branchPath", equalTo(newTargetPath.getPath()));
		
		getComponent(newTargetPath, SnomedComponentType.DESCRIPTION, descriptionId)
			.statusCode(200)
			.body("released", equalTo(true))
			.body("effectiveTime", equalTo(newIntEffectiveDate))
			.body("moduleId", equalTo(Concepts.MODULE_SCT_CORE))
			.body("caseSignificance", equalTo(CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE.toString()));
		
	}
	
	@Test
	public void upgradeWithDonatedConcept() {
		
		// create extension concept on extension's current branch
		
		String extensionFsnTerm = "FSN of concept";
		String extensionPtTerm = "PT of concept";
		String extensionSynonymTerm = "Synonym of extension concept";
		
		Map<String, Object> fsnRequestBody = createDescriptionRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
				Concepts.FULLY_SPECIFIED_NAME, extensionFsnTerm, SnomedApiTestConstants.UK_PREFERRED_MAP);
		Map<String, Object> ptRequestBody = createDescriptionRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
				Concepts.SYNONYM, extensionPtTerm, SnomedApiTestConstants.UK_PREFERRED_MAP);
		Map<String, Object> synonymRequestBody = createDescriptionRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION, 
				Concepts.SYNONYM, extensionSynonymTerm, SnomedApiTestConstants.UK_ACCEPTABLE_MAP);

		Map<String, Object> statedIsaRequestBody = createRelationshipRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION, 
				Concepts.IS_A, Concepts.ROOT_CONCEPT, CharacteristicType.STATED_RELATIONSHIP);
		Map<String, Object> inferredIsaRequestBody = createRelationshipRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
				Concepts.IS_A, Concepts.ROOT_CONCEPT, CharacteristicType.INFERRED_RELATIONSHIP);
		
		Map<String, Object> additionalRelationshipRequestBody = createRelationshipRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
				Concepts.PART_OF, Concepts.NAMESPACE_ROOT, CharacteristicType.ADDITIONAL_RELATIONSHIP);

		Map<String, Object> extensionConceptRequestBody = createConceptRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
				ImmutableList.of(statedIsaRequestBody, inferredIsaRequestBody, additionalRelationshipRequestBody), 
				ImmutableList.of(fsnRequestBody, ptRequestBody, synonymRequestBody));
		
		String extensionConceptId = lastPathSegment(createComponent(branchPath, SnomedComponentType.CONCEPT, extensionConceptRequestBody)
				.statusCode(201)
				.extract().header("Location"));
		
		SnomedConcept extensionConcept = getComponent(branchPath, SnomedComponentType.CONCEPT, extensionConceptId, "descriptions(), relationships()")
				.statusCode(200)
				.extract()
				.as(SnomedConcept.class);
		
		String extensionFsnId = extensionConcept.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionFsnTerm)).findFirst()
				.get().getId();
		String extensionPtId = extensionConcept.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionPtTerm)).findFirst()
				.get().getId();
		String extensionSynonymId = extensionConcept.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionSynonymTerm))
				.findFirst().get().getId();

		String extensionStatedIsaId = extensionConcept.getRelationships().getItems().stream()
				.filter(r -> r.getCharacteristicType() == CharacteristicType.STATED_RELATIONSHIP).findFirst().get().getId();
		String extensionInferredIsaId = extensionConcept.getRelationships().getItems().stream()
				.filter(r -> r.getCharacteristicType() == CharacteristicType.INFERRED_RELATIONSHIP).findFirst().get().getId();
		String extensionAdditionalRelationshipId = extensionConcept.getRelationships().getItems().stream()
				.filter(r -> r.getCharacteristicType() == CharacteristicType.ADDITIONAL_RELATIONSHIP).findFirst().get().getId();

		// create new version on MAIN
		
		String effectiveDate = getNextAvailableEffectiveDateAsString(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME);
		String versionId = "v5";
		createVersion(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME, versionId, effectiveDate).statusCode(201);
		
		IBranchPath targetPath = BranchPathUtils.createPath(SnomedApiTestConstants.PATH_JOINER.join(
				Branch.MAIN_PATH, 
				versionId, 
				SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME));

		createBranch(targetPath).statusCode(201);
		
		// create INT concept with same ID but different description and relationship IDs on version branch
		
		Map<String, Object> intFsnRequestBody = createDescriptionRequestBody("", Concepts.MODULE_SCT_CORE,
				Concepts.FULLY_SPECIFIED_NAME, extensionFsnTerm, SnomedApiTestConstants.UK_PREFERRED_MAP);
		Map<String, Object> intPtRequestBody = createDescriptionRequestBody("", Concepts.MODULE_SCT_CORE,
				Concepts.SYNONYM, extensionPtTerm, SnomedApiTestConstants.UK_PREFERRED_MAP);

		Map<String, Object> intStatedIsaRequestBody = createRelationshipRequestBody("", Concepts.MODULE_SCT_CORE, 
				Concepts.IS_A, Concepts.ROOT_CONCEPT, CharacteristicType.STATED_RELATIONSHIP);
		Map<String, Object> intInferredIsaRequestBody = createRelationshipRequestBody("", Concepts.MODULE_SCT_CORE,
				Concepts.IS_A, Concepts.ROOT_CONCEPT, CharacteristicType.INFERRED_RELATIONSHIP);
		
		Map<String, Object> intConceptRequestBody = createConceptRequestBody(extensionConceptId, "", Concepts.MODULE_SCT_CORE,
				ImmutableList.of(intStatedIsaRequestBody, intInferredIsaRequestBody), 
				ImmutableList.of(intFsnRequestBody, intPtRequestBody));
		
		String intConceptId = lastPathSegment(createComponent(targetPath, SnomedComponentType.CONCEPT, intConceptRequestBody)
				.statusCode(201)
				.body(equalTo(""))
				.extract().header("Location"));
		
		SnomedConcept intConcept = getComponent(targetPath, SnomedComponentType.CONCEPT, intConceptId,
				"descriptions(), relationships()")
				.statusCode(200)
				.extract().as(SnomedConcept.class);
		
		String intFsnId = intConcept.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionFsnTerm)).findFirst().get()
				.getId();
		String intPtId = intConcept.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionPtTerm)).findFirst().get().getId();

		String intStatedIsaId = intConcept.getRelationships().getItems().stream()
				.filter(r -> r.getCharacteristicType() == CharacteristicType.STATED_RELATIONSHIP).findFirst().get().getId();
		String intInferredIsaId = intConcept.getRelationships().getItems().stream()
				.filter(r -> r.getCharacteristicType() == CharacteristicType.INFERRED_RELATIONSHIP).findFirst().get().getId();

		assertEquals(intConceptId, extensionConceptId);
		assertNotEquals(intConcept.getModuleId(), extensionConcept.getModuleId());
		
		assertNotEquals(intFsnId, extensionFsnId);
		assertNotEquals(intPtId, extensionPtId);
		assertNotEquals(intStatedIsaId, extensionStatedIsaId);
		assertNotEquals(intInferredIsaId, extensionInferredIsaId);
				
		// upgrade extension to new INT version
		
		merge(branchPath, targetPath, "Upgraded B2i extension to v5").body("status", equalTo(Merge.Status.COMPLETED.name()));

		Map<?, ?> updateRequest = ImmutableMap.builder()
				.put("repositoryUuid", SnomedDatastoreActivator.REPOSITORY_UUID)
				.put("branchPath", targetPath.getPath())
				.build();

		updateCodeSystem(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME, updateRequest).statusCode(204);
		getCodeSystem(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME).statusCode(200).body("branchPath", equalTo(targetPath.getPath()));

		SnomedConcept donatedConceptInExtension = getComponent(targetPath, SnomedComponentType.CONCEPT, extensionConceptId,
				"descriptions(), relationships()")
				.statusCode(200)
				.extract().as(SnomedConcept.class);
		
		// validate components of donated concept on extension branch
		
		Set<String> descriptionIds = donatedConceptInExtension.getDescriptions().getItems().stream().map(SnomedDescription::getId).collect(toSet());
		
		assertTrue(descriptionIds.contains(intFsnId));
		assertTrue(descriptionIds.contains(intPtId));
		assertTrue(descriptionIds.contains(extensionSynonymId));
		
		assertTrue(descriptionIds.contains(extensionFsnId));
		assertTrue(descriptionIds.contains(extensionPtId));
		
		Set<String> relationshipIds = donatedConceptInExtension.getRelationships().getItems().stream().map(SnomedRelationship::getId).collect(toSet());
		
		assertTrue(relationshipIds.contains(intStatedIsaId));
		assertTrue(relationshipIds.contains(intInferredIsaId));
		assertTrue(relationshipIds.contains(extensionStatedIsaId));
		assertTrue(relationshipIds.contains(extensionInferredIsaId));
		assertTrue(relationshipIds.contains(extensionAdditionalRelationshipId));
	}
	
	@Test
	public void upgradeWithDonatedDescription() {
		
		String descriptionTerm = "Donated synonym of root concept";
		
		Map<?, ?> requestBody = ImmutableMap.builder()
				.put("conceptId", Concepts.ROOT_CONCEPT)
				.put("namespace", Concepts.B2I_NAMESPACE)
				.put("moduleId", Concepts.MODULE_B2I_EXTENSION)
				.put("typeId", Concepts.SYNONYM)
				.put("term", descriptionTerm)
				.put("languageCode", DEFAULT_LANGUAGE_CODE)
				.put("acceptability", SnomedApiTestConstants.US_ACCEPTABLE_MAP)
				.put("caseSignificance", CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE)
				.put("commitComment", "Created new extension synonym")
				.build();

		String extensionDescriptionId = lastPathSegment(createComponent(branchPath, SnomedComponentType.DESCRIPTION, requestBody)
				.statusCode(201)
				.extract().header("Location"));

		SnomedDescription extensionDescription = getComponent(branchPath, SnomedComponentType.DESCRIPTION, extensionDescriptionId)
				.statusCode(200)
				.extract().as(SnomedDescription.class);
		
		assertEquals(CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE, extensionDescription.getCaseSignificance());
		assertThat(extensionDescription.getAcceptabilityMap().containsKey(Concepts.REFSET_LANGUAGE_TYPE_US));
		assertEquals(Acceptability.ACCEPTABLE, extensionDescription.getAcceptabilityMap().get(Concepts.REFSET_LANGUAGE_TYPE_US));
		assertFalse(extensionDescription.getAcceptabilityMap().containsKey(Concepts.REFSET_LANGUAGE_TYPE_UK));
		
		// create new version on MAIN
		
		String effectiveDate = getNextAvailableEffectiveDateAsString(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME);
		String versionId = "v6";
		createVersion(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME, versionId, effectiveDate).statusCode(201);
		
		IBranchPath targetPath = BranchPathUtils.createPath(SnomedApiTestConstants.PATH_JOINER.join(
				Branch.MAIN_PATH, 
				versionId, 
				SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME));

		createBranch(targetPath).statusCode(201);
		
		// create INT description with same ID but with slightly different properties
		
		Map<?, ?> intRequestBody = ImmutableMap.builder()
				.put("id", extensionDescriptionId)
				.put("conceptId", Concepts.ROOT_CONCEPT)
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("typeId", Concepts.SYNONYM)
				.put("term", descriptionTerm)
				.put("languageCode", "en")
				.put("acceptability", SnomedApiTestConstants.UK_ACCEPTABLE_MAP)
				.put("caseSignificance", CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE)
				.put("commitComment", "Created new donated synonym")
				.build();

		String donatedDescriptionId = lastPathSegment(createComponent(targetPath, SnomedComponentType.DESCRIPTION, intRequestBody)
				.statusCode(201)
				.extract().header("Location"));
		
		assertEquals(extensionDescriptionId, donatedDescriptionId);
		
		SnomedDescription donatedDescription = getComponent(targetPath, SnomedComponentType.DESCRIPTION, donatedDescriptionId)
				.statusCode(200)
				.extract().as(SnomedDescription.class);
		
		assertEquals(CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE, donatedDescription.getCaseSignificance());
		assertThat(donatedDescription.getAcceptabilityMap().containsKey(Concepts.REFSET_LANGUAGE_TYPE_UK));
		assertEquals(Acceptability.ACCEPTABLE, donatedDescription.getAcceptabilityMap().get(Concepts.REFSET_LANGUAGE_TYPE_UK));
		assertFalse(donatedDescription.getAcceptabilityMap().containsKey(Concepts.REFSET_LANGUAGE_TYPE_US));
		
		// upgrade extension to new INT version
		
		merge(branchPath, targetPath, "Upgraded B2i extension to v6").body("status", equalTo(Merge.Status.COMPLETED.name()));

		Map<?, ?> updateRequest = ImmutableMap.builder()
				.put("repositoryUuid", SnomedDatastoreActivator.REPOSITORY_UUID)
				.put("branchPath", targetPath.getPath())
				.build();

		updateCodeSystem(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME, updateRequest).statusCode(204);
		getCodeSystem(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME).statusCode(200).body("branchPath", equalTo(targetPath.getPath()));

		SnomedDescription donatedDescriptionInExtension = getComponent(targetPath, SnomedComponentType.DESCRIPTION, extensionDescriptionId)
				.statusCode(200)
				.extract().as(SnomedDescription.class);
		
		assertEquals(Concepts.MODULE_SCT_CORE, donatedDescriptionInExtension.getModuleId());
		assertEquals(CaseSignificance.ENTIRE_TERM_CASE_SENSITIVE, donatedDescriptionInExtension.getCaseSignificance());
		assertThat(donatedDescriptionInExtension.getAcceptabilityMap().containsKey(Concepts.REFSET_LANGUAGE_TYPE_US));
		assertEquals(Acceptability.ACCEPTABLE, donatedDescriptionInExtension.getAcceptabilityMap().get(Concepts.REFSET_LANGUAGE_TYPE_US));
		assertThat(donatedDescriptionInExtension.getAcceptabilityMap().containsKey(Concepts.REFSET_LANGUAGE_TYPE_UK));
		assertEquals(Acceptability.ACCEPTABLE, donatedDescriptionInExtension.getAcceptabilityMap().get(Concepts.REFSET_LANGUAGE_TYPE_UK));
		
	}
	
	@Test
	public void upgradeWithDonatedRelationship() {
		
		Map<?, ?> requestBody = ImmutableMap.builder()
			.put("namespace", Concepts.B2I_NAMESPACE)
			.put("moduleId", Concepts.MODULE_B2I_EXTENSION)
			.put("sourceId", Concepts.ROOT_CONCEPT)
			.put("typeId", Concepts.PART_OF)
			.put("destinationId", Concepts.NAMESPACE_ROOT)
			.put("characteristicType", CharacteristicType.ADDITIONAL_RELATIONSHIP)
			.put("group", 0)
			.put("commitComment", "Created new extension relationship")
			.build();

		String extensionRelationshipId = lastPathSegment(createComponent(branchPath, SnomedComponentType.RELATIONSHIP, requestBody)
				.statusCode(201)
				.extract().header("Location"));

		SnomedRelationship extensionRelationship = getComponent(branchPath, SnomedComponentType.RELATIONSHIP, extensionRelationshipId)
				.statusCode(200)
				.extract().as(SnomedRelationship.class);
		
		assertEquals(CharacteristicType.ADDITIONAL_RELATIONSHIP, extensionRelationship.getCharacteristicType());
		assertEquals(0, extensionRelationship.getGroup().intValue());
		
		// create new version on MAIN
		
		String effectiveDate = getNextAvailableEffectiveDateAsString(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME);
		String versionId = "v9";
		createVersion(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME, versionId, effectiveDate).statusCode(201);
		
		IBranchPath targetPath = BranchPathUtils.createPath(SnomedApiTestConstants.PATH_JOINER.join(
				Branch.MAIN_PATH, 
				versionId, 
				SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME));

		createBranch(targetPath).statusCode(201);
		
		// create INT relationship with same ID but with slightly different properties
		
		Map<?, ?> intRequestBody = ImmutableMap.builder()
				.put("id", extensionRelationshipId)
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("sourceId", Concepts.ROOT_CONCEPT)
				.put("typeId", Concepts.PART_OF)
				.put("destinationId", Concepts.NAMESPACE_ROOT)
				.put("characteristicType", CharacteristicType.INFERRED_RELATIONSHIP)
				.put("group", 1)
				.put("commitComment", "Created new donated INT relationship")
				.build();

		String donatedRelationshipId = lastPathSegment(createComponent(targetPath, SnomedComponentType.RELATIONSHIP, intRequestBody)
				.statusCode(201)
				.extract().header("Location"));
		
		assertEquals(extensionRelationshipId, donatedRelationshipId);
		
		SnomedRelationship donatedRelationship = getComponent(targetPath, SnomedComponentType.RELATIONSHIP, donatedRelationshipId)
				.statusCode(200)
				.extract().as(SnomedRelationship.class);
		
		assertEquals(CharacteristicType.INFERRED_RELATIONSHIP, donatedRelationship.getCharacteristicType());
		assertEquals(1, donatedRelationship.getGroup().intValue());
		
		// upgrade extension to new INT version
		
		merge(branchPath, targetPath, "Upgraded B2i extension to v9").body("status", equalTo(Merge.Status.COMPLETED.name()));

		Map<?, ?> updateRequest = ImmutableMap.builder()
				.put("repositoryUuid", SnomedDatastoreActivator.REPOSITORY_UUID)
				.put("branchPath", targetPath.getPath())
				.build();

		updateCodeSystem(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME, updateRequest).statusCode(204);
		getCodeSystem(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME).statusCode(200).body("branchPath", equalTo(targetPath.getPath()));

		SnomedRelationship donatedRelationshipInExtension = getComponent(targetPath, SnomedComponentType.RELATIONSHIP, extensionRelationshipId)
				.statusCode(200)
				.extract().as(SnomedRelationship.class);
		
		assertEquals(Concepts.MODULE_SCT_CORE, donatedRelationshipInExtension.getModuleId());
		assertEquals(CharacteristicType.INFERRED_RELATIONSHIP, donatedRelationshipInExtension.getCharacteristicType());
		assertEquals(1, donatedRelationshipInExtension.getGroup().intValue());
	}
	
	@Test
	public void upgradeWithDonatedConceptAndDescriptions() {
		
		// create extension concept on extension's current branch
		
		String extensionFsnTerm = "FSN of concept";
		String extensionPtTerm = "PT of concept";
		String extensionSynonymTerm = "Synonym of extension concept";
		
		Map<String, Object> fsnRequestBody = createDescriptionRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
				Concepts.FULLY_SPECIFIED_NAME, extensionFsnTerm, SnomedApiTestConstants.UK_PREFERRED_MAP);
		Map<String, Object> ptRequestBody = createDescriptionRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
				Concepts.SYNONYM, extensionPtTerm, SnomedApiTestConstants.UK_PREFERRED_MAP);
		Map<String, Object> synonymRequestBody = createDescriptionRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION, 
				Concepts.SYNONYM, extensionSynonymTerm, SnomedApiTestConstants.UK_ACCEPTABLE_MAP);
	
		Map<String, Object> statedIsaRequestBody = createRelationshipRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION, 
				Concepts.IS_A, Concepts.ROOT_CONCEPT, CharacteristicType.STATED_RELATIONSHIP);
		Map<String, Object> inferredIsaRequestBody = createRelationshipRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
				Concepts.IS_A, Concepts.ROOT_CONCEPT, CharacteristicType.INFERRED_RELATIONSHIP);
	
		Map<String, Object> extensionConceptRequestBody = createConceptRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
				ImmutableList.of(statedIsaRequestBody, inferredIsaRequestBody), 
				ImmutableList.of(fsnRequestBody, ptRequestBody, synonymRequestBody));
		
		String extensionConceptId = lastPathSegment(createComponent(branchPath, SnomedComponentType.CONCEPT, extensionConceptRequestBody)
				.statusCode(201)
				.extract().header("Location"));
		
		SnomedConcept extensionConcept = getComponent(branchPath, SnomedComponentType.CONCEPT, extensionConceptId, "descriptions(), relationships()")
				.statusCode(200)
				.extract()
				.as(SnomedConcept.class);
		
		String extensionFsnId = extensionConcept.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionFsnTerm)).findFirst()
				.get().getId();
		String extensionPtId = extensionConcept.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionPtTerm)).findFirst()
				.get().getId();
		String extensionSynonymId = extensionConcept.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionSynonymTerm))
				.findFirst().get().getId();
	
		String extensionStatedIsaId = extensionConcept.getRelationships().getItems().stream()
				.filter(r -> r.getCharacteristicType() == CharacteristicType.STATED_RELATIONSHIP).findFirst().get().getId();
		String extensionInferredIsaId = extensionConcept.getRelationships().getItems().stream()
				.filter(r -> r.getCharacteristicType() == CharacteristicType.INFERRED_RELATIONSHIP).findFirst().get().getId();
	
		// create new version on MAIN
		
		String effectiveDate = getNextAvailableEffectiveDateAsString(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME);
		String versionId = "v7";
		createVersion(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME, versionId, effectiveDate).statusCode(201);
		
		IBranchPath targetPath = BranchPathUtils.createPath(SnomedApiTestConstants.PATH_JOINER.join(
				Branch.MAIN_PATH, 
				versionId, 
				SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME));
	
		createBranch(targetPath).statusCode(201);
		
		// create INT concept with same ID but different description and relationship IDs on version branch
		
		Map<String, Object> intFsnRequestBody = createDescriptionRequestBody(extensionFsnId, "", Concepts.MODULE_SCT_CORE,
				Concepts.FULLY_SPECIFIED_NAME, extensionFsnTerm, SnomedApiTestConstants.UK_PREFERRED_MAP);
		Map<String, Object> intPtRequestBody = createDescriptionRequestBody(extensionPtId, "", Concepts.MODULE_SCT_CORE,
				Concepts.SYNONYM, extensionPtTerm, SnomedApiTestConstants.UK_PREFERRED_MAP);
	
		Map<String, Object> intStatedIsaRequestBody = createRelationshipRequestBody("", Concepts.MODULE_SCT_CORE, 
				Concepts.IS_A, Concepts.ROOT_CONCEPT, CharacteristicType.STATED_RELATIONSHIP);
		Map<String, Object> intInferredIsaRequestBody = createRelationshipRequestBody("", Concepts.MODULE_SCT_CORE,
				Concepts.IS_A, Concepts.ROOT_CONCEPT, CharacteristicType.INFERRED_RELATIONSHIP);
		
		Map<String, Object> intConceptRequestBody = createConceptRequestBody(extensionConceptId, "", Concepts.MODULE_SCT_CORE,
				ImmutableList.of(intStatedIsaRequestBody, intInferredIsaRequestBody), 
				ImmutableList.of(intFsnRequestBody, intPtRequestBody));
		
		String intConceptId = lastPathSegment(createComponent(targetPath, SnomedComponentType.CONCEPT, intConceptRequestBody)
				.statusCode(201)
				.body(equalTo(""))
				.extract().header("Location"));
		
		SnomedConcept intConcept = getComponent(targetPath, SnomedComponentType.CONCEPT, intConceptId,
				"descriptions(), relationships()")
				.statusCode(200)
				.extract().as(SnomedConcept.class);
		
		String intFsnId = intConcept.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionFsnTerm)).findFirst().get()
				.getId();
		String intPtId = intConcept.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionPtTerm)).findFirst().get().getId();
	
		String intStatedIsaId = intConcept.getRelationships().getItems().stream()
				.filter(r -> r.getCharacteristicType() == CharacteristicType.STATED_RELATIONSHIP).findFirst().get().getId();
		String intInferredIsaId = intConcept.getRelationships().getItems().stream()
				.filter(r -> r.getCharacteristicType() == CharacteristicType.INFERRED_RELATIONSHIP).findFirst().get().getId();
	
		assertEquals(intConceptId, extensionConceptId);
		assertNotEquals(intConcept.getModuleId(), extensionConcept.getModuleId());
		
		assertEquals(intFsnId, extensionFsnId);
		assertEquals(intPtId, extensionPtId);
		assertNotEquals(intStatedIsaId, extensionStatedIsaId);
		assertNotEquals(intInferredIsaId, extensionInferredIsaId);
				
		// upgrade extension to new INT version
		
		merge(branchPath, targetPath, "Upgraded B2i extension to v7").body("status", equalTo(Merge.Status.COMPLETED.name()));
	
		Map<?, ?> updateRequest = ImmutableMap.builder()
				.put("repositoryUuid", SnomedDatastoreActivator.REPOSITORY_UUID)
				.put("branchPath", targetPath.getPath())
				.build();
	
		updateCodeSystem(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME, updateRequest).statusCode(204);
		getCodeSystem(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME).statusCode(200).body("branchPath", equalTo(targetPath.getPath()));
	
		SnomedConcept donatedConceptInExtension = getComponent(targetPath, SnomedComponentType.CONCEPT, extensionConceptId,
				"descriptions(), relationships()")
				.statusCode(200)
				.extract().as(SnomedConcept.class);
		
		// validate components of donated concept on extension branch
		
		List<SnomedDescription> donatedFsns = donatedConceptInExtension.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionFsnTerm)).collect(toList());
		
		assertEquals(1, donatedFsns.size());
		SnomedDescription donatedFsn = Iterables.getOnlyElement(donatedFsns);
		
		assertEquals(Concepts.MODULE_SCT_CORE, donatedFsn.getModuleId());
		
		List<SnomedDescription> donatedPts = donatedConceptInExtension.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionPtTerm)).collect(toList());
	
		assertEquals(1, donatedPts.size());
		SnomedDescription donatedPt = Iterables.getOnlyElement(donatedPts);
		
		assertEquals(Concepts.MODULE_SCT_CORE, donatedPt.getModuleId());
		
		Set<String> descriptionIds = donatedConceptInExtension.getDescriptions().getItems().stream().map(SnomedDescription::getId).collect(toSet());
		assertTrue(descriptionIds.contains(extensionSynonymId));
		
		Set<String> relationshipIds = donatedConceptInExtension.getRelationships().getItems().stream().map(SnomedRelationship::getId).collect(toSet());
		
		assertTrue(relationshipIds.contains(intStatedIsaId));
		assertTrue(relationshipIds.contains(intInferredIsaId));
		assertTrue(relationshipIds.contains(extensionStatedIsaId));
		assertTrue(relationshipIds.contains(extensionInferredIsaId));
				
	}
	
	@Test
	public void upgradeWithDonatedConceptAndDescriptionsAndRelationships() {
		
		// create extension concept on extension's current branch
		
		String extensionFsnTerm = "FSN of concept";
		String extensionPtTerm = "PT of concept";
		String extensionSynonymTerm = "Synonym of extension concept";
		
		Map<String, Object> fsnRequestBody = createDescriptionRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
				Concepts.FULLY_SPECIFIED_NAME, extensionFsnTerm, SnomedApiTestConstants.UK_PREFERRED_MAP);
		Map<String, Object> ptRequestBody = createDescriptionRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
				Concepts.SYNONYM, extensionPtTerm, SnomedApiTestConstants.UK_PREFERRED_MAP);
		Map<String, Object> synonymRequestBody = createDescriptionRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION, 
				Concepts.SYNONYM, extensionSynonymTerm, SnomedApiTestConstants.UK_ACCEPTABLE_MAP);
	
		Map<String, Object> statedIsaRequestBody = createRelationshipRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION, 
				Concepts.IS_A, Concepts.ROOT_CONCEPT, CharacteristicType.STATED_RELATIONSHIP);
		Map<String, Object> inferredIsaRequestBody = createRelationshipRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
				Concepts.IS_A, Concepts.ROOT_CONCEPT, CharacteristicType.INFERRED_RELATIONSHIP);
	
		Map<String, Object> extensionConceptRequestBody = createConceptRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
				ImmutableList.of(statedIsaRequestBody, inferredIsaRequestBody), 
				ImmutableList.of(fsnRequestBody, ptRequestBody, synonymRequestBody));
		
		String extensionConceptId = lastPathSegment(createComponent(branchPath, SnomedComponentType.CONCEPT, extensionConceptRequestBody)
				.statusCode(201)
				.extract().header("Location"));
		
		SnomedConcept extensionConcept = getComponent(branchPath, SnomedComponentType.CONCEPT, extensionConceptId, "descriptions(), relationships()")
				.statusCode(200)
				.extract()
				.as(SnomedConcept.class);
		
		String extensionFsnId = extensionConcept.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionFsnTerm)).findFirst()
				.get().getId();
		String extensionPtId = extensionConcept.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionPtTerm)).findFirst()
				.get().getId();
		String extensionSynonymId = extensionConcept.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionSynonymTerm))
				.findFirst().get().getId();
	
		String extensionStatedIsaId = extensionConcept.getRelationships().getItems().stream()
				.filter(r -> r.getCharacteristicType() == CharacteristicType.STATED_RELATIONSHIP).findFirst().get().getId();
		String extensionInferredIsaId = extensionConcept.getRelationships().getItems().stream()
				.filter(r -> r.getCharacteristicType() == CharacteristicType.INFERRED_RELATIONSHIP).findFirst().get().getId();
	
		// create new version on MAIN
		
		String effectiveDate = getNextAvailableEffectiveDateAsString(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME);
		String versionId = "v8";
		createVersion(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME, versionId, effectiveDate).statusCode(201);
		
		IBranchPath targetPath = BranchPathUtils.createPath(SnomedApiTestConstants.PATH_JOINER.join(
				Branch.MAIN_PATH, 
				versionId, 
				SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME));
	
		createBranch(targetPath).statusCode(201);
		
		// create INT concept with same ID but different description and relationship IDs on version branch
		
		Map<String, Object> intFsnRequestBody = createDescriptionRequestBody(extensionFsnId, "", Concepts.MODULE_SCT_CORE,
				Concepts.FULLY_SPECIFIED_NAME, extensionFsnTerm, SnomedApiTestConstants.UK_PREFERRED_MAP);
		Map<String, Object> intPtRequestBody = createDescriptionRequestBody(extensionPtId, "", Concepts.MODULE_SCT_CORE,
				Concepts.SYNONYM, extensionPtTerm, SnomedApiTestConstants.UK_PREFERRED_MAP);
	
		Map<String, Object> intStatedIsaRequestBody = createRelationshipRequestBody(extensionStatedIsaId, "", Concepts.MODULE_SCT_CORE, 
				Concepts.IS_A, Concepts.ROOT_CONCEPT, CharacteristicType.STATED_RELATIONSHIP);
		Map<String, Object> intInferredIsaRequestBody = createRelationshipRequestBody(extensionInferredIsaId, "", Concepts.MODULE_SCT_CORE,
				Concepts.IS_A, Concepts.ROOT_CONCEPT, CharacteristicType.INFERRED_RELATIONSHIP);
		
		Map<String, Object> intConceptRequestBody = createConceptRequestBody(extensionConceptId, "", Concepts.MODULE_SCT_CORE,
				ImmutableList.of(intStatedIsaRequestBody, intInferredIsaRequestBody), 
				ImmutableList.of(intFsnRequestBody, intPtRequestBody));
		
		String intConceptId = lastPathSegment(createComponent(targetPath, SnomedComponentType.CONCEPT, intConceptRequestBody)
				.statusCode(201)
				.body(equalTo(""))
				.extract().header("Location"));
		
		SnomedConcept intConcept = getComponent(targetPath, SnomedComponentType.CONCEPT, intConceptId,
				"descriptions(), relationships()")
				.statusCode(200)
				.extract().as(SnomedConcept.class);
		
		String intFsnId = intConcept.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionFsnTerm)).findFirst().get()
				.getId();
		String intPtId = intConcept.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionPtTerm)).findFirst().get().getId();
	
		String intStatedIsaId = intConcept.getRelationships().getItems().stream()
				.filter(r -> r.getCharacteristicType() == CharacteristicType.STATED_RELATIONSHIP).findFirst().get().getId();
		String intInferredIsaId = intConcept.getRelationships().getItems().stream()
				.filter(r -> r.getCharacteristicType() == CharacteristicType.INFERRED_RELATIONSHIP).findFirst().get().getId();
	
		assertEquals(intConceptId, extensionConceptId);
		assertNotEquals(intConcept.getModuleId(), extensionConcept.getModuleId());
		
		assertEquals(intFsnId, extensionFsnId);
		assertEquals(intPtId, extensionPtId);
		assertEquals(intStatedIsaId, extensionStatedIsaId);
		assertEquals(intInferredIsaId, extensionInferredIsaId);
				
		// upgrade extension to new INT version
		
		merge(branchPath, targetPath, "Upgraded B2i extension to v8").body("status", equalTo(Merge.Status.COMPLETED.name()));
	
		Map<?, ?> updateRequest = ImmutableMap.builder()
				.put("repositoryUuid", SnomedDatastoreActivator.REPOSITORY_UUID)
				.put("branchPath", targetPath.getPath())
				.build();
	
		updateCodeSystem(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME, updateRequest).statusCode(204);
		getCodeSystem(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME).statusCode(200).body("branchPath", equalTo(targetPath.getPath()));
	
		SnomedConcept donatedConceptInExtension = getComponent(targetPath, SnomedComponentType.CONCEPT, extensionConceptId,
				"descriptions(), relationships()")
				.statusCode(200)
				.extract().as(SnomedConcept.class);
		
		// validate components of donated concept on extension branch
		
		List<SnomedDescription> donatedFsns = donatedConceptInExtension.getDescriptions().getItems().stream()
				.filter(d -> d.getTerm().equals(extensionFsnTerm)).collect(toList());

		assertEquals(1, donatedFsns.size());
		SnomedDescription donatedFsn = Iterables.getOnlyElement(donatedFsns);

		assertEquals(Concepts.MODULE_SCT_CORE, donatedFsn.getModuleId());

		List<SnomedDescription> donatedPts = donatedConceptInExtension.getDescriptions().getItems().stream()
				.filter(d -> d.getTerm().equals(extensionPtTerm)).collect(toList());

		assertEquals(1, donatedPts.size());
		SnomedDescription donatedPt = Iterables.getOnlyElement(donatedPts);
		
		assertEquals(Concepts.MODULE_SCT_CORE, donatedPt.getModuleId());
		
		Set<String> descriptionIds = donatedConceptInExtension.getDescriptions().getItems().stream().map(SnomedDescription::getId).collect(toSet());
		assertTrue(descriptionIds.contains(extensionSynonymId));
		
		List<SnomedRelationship> donatedStatedIsas = donatedConceptInExtension.getRelationships().getItems().stream()
				.filter(r -> r.getTypeId().equals(Concepts.IS_A) && r.getCharacteristicType() == CharacteristicType.STATED_RELATIONSHIP)
				.collect(toList());
		
		assertEquals(1, donatedStatedIsas.size());
		SnomedRelationship donatedStatedIsa = Iterables.getOnlyElement(donatedStatedIsas);
		assertEquals(extensionStatedIsaId, donatedStatedIsa.getId());
		assertEquals(Concepts.MODULE_SCT_CORE, donatedStatedIsa.getModuleId());
		
		List<SnomedRelationship> donatedInferredIsas = donatedConceptInExtension.getRelationships().getItems().stream()
				.filter(r -> r.getTypeId().equals(Concepts.IS_A) && r.getCharacteristicType() == CharacteristicType.INFERRED_RELATIONSHIP)
				.collect(toList());
		
		assertEquals(1, donatedInferredIsas.size());
		SnomedRelationship donatedInferredIsa = Iterables.getOnlyElement(donatedInferredIsas);
		assertEquals(extensionInferredIsaId, donatedInferredIsa.getId());
		assertEquals(Concepts.MODULE_SCT_CORE, donatedInferredIsa.getModuleId());

	}
	
	@Test
	public void upgradeWithDonatedConceptAndDescriptionsAndRelationshipsWithCrossReference() {
		
		// create extension concept on extension's current branch
		
		String extensionFsnTerm1 = "FSN of concept";
		String extensionPtTerm1 = "PT of concept";
		String extensionSynonymTerm1 = "Synonym of extension concept";
		
		Map<String, Object> fsnRequestBody1 = createDescriptionRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
				Concepts.FULLY_SPECIFIED_NAME, extensionFsnTerm1, SnomedApiTestConstants.UK_PREFERRED_MAP);
		Map<String, Object> ptRequestBody1 = createDescriptionRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
				Concepts.SYNONYM, extensionPtTerm1, SnomedApiTestConstants.UK_PREFERRED_MAP);
		Map<String, Object> synonymRequestBody1 = createDescriptionRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION, 
				Concepts.SYNONYM, extensionSynonymTerm1, SnomedApiTestConstants.UK_ACCEPTABLE_MAP);
	
		Map<String, Object> statedIsaRequestBody1 = createRelationshipRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION, 
				Concepts.IS_A, Concepts.ROOT_CONCEPT, CharacteristicType.STATED_RELATIONSHIP);
		Map<String, Object> inferredIsaRequestBody1 = createRelationshipRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
				Concepts.IS_A, Concepts.ROOT_CONCEPT, CharacteristicType.INFERRED_RELATIONSHIP);
	
		Map<String, Object> extensionConceptRequestBody1 = createConceptRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
				ImmutableList.of(statedIsaRequestBody1, inferredIsaRequestBody1), 
				ImmutableList.of(fsnRequestBody1, ptRequestBody1, synonymRequestBody1));
		
		String extensionConceptId1 = lastPathSegment(createComponent(branchPath, SnomedComponentType.CONCEPT, extensionConceptRequestBody1)
				.statusCode(201)
				.extract().header("Location"));
		
		SnomedConcept extensionConcept1 = getComponent(branchPath, SnomedComponentType.CONCEPT, extensionConceptId1, "descriptions(), relationships()")
				.statusCode(200)
				.extract()
				.as(SnomedConcept.class);
		
		String extensionFsnId1 = extensionConcept1.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionFsnTerm1)).findFirst()
				.get().getId();
		String extensionPtId1 = extensionConcept1.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionPtTerm1)).findFirst()
				.get().getId();
		String extensionSynonymId1 = extensionConcept1.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionSynonymTerm1))
				.findFirst().get().getId();
	
		String extensionStatedIsaId1 = extensionConcept1.getRelationships().getItems().stream()
				.filter(r -> r.getCharacteristicType() == CharacteristicType.STATED_RELATIONSHIP).findFirst().get().getId();
		String extensionInferredIsaId1 = extensionConcept1.getRelationships().getItems().stream()
				.filter(r -> r.getCharacteristicType() == CharacteristicType.INFERRED_RELATIONSHIP).findFirst().get().getId();
	
		// create another extension concept which references the previous one
		
		String extensionFsnTerm2 = "FSN of concept 2";
		String extensionPtTerm2 = "PT of concept 2";
		String extensionSynonymTerm2 = "Synonym of extension concept 2";
		
		Map<String, Object> fsnRequestBody2 = createDescriptionRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
				Concepts.FULLY_SPECIFIED_NAME, extensionFsnTerm2, SnomedApiTestConstants.UK_PREFERRED_MAP);
		Map<String, Object> ptRequestBody2 = createDescriptionRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
				Concepts.SYNONYM, extensionPtTerm2, SnomedApiTestConstants.UK_PREFERRED_MAP);
		Map<String, Object> synonymRequestBody2 = createDescriptionRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION, 
				Concepts.SYNONYM, extensionSynonymTerm2, SnomedApiTestConstants.UK_ACCEPTABLE_MAP);
	
		Map<String, Object> statedIsaRequestBody2 = createRelationshipRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION, 
				Concepts.IS_A, Concepts.ROOT_CONCEPT, CharacteristicType.STATED_RELATIONSHIP);
		Map<String, Object> inferredIsaRequestBody2 = createRelationshipRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
				Concepts.IS_A, Concepts.ROOT_CONCEPT, CharacteristicType.INFERRED_RELATIONSHIP);
		
		Map<String, Object> partOfRequestBody = createRelationshipRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
				Concepts.PART_OF, extensionConceptId1, CharacteristicType.ADDITIONAL_RELATIONSHIP);
	
		Map<String, Object> extensionConceptRequestBody2 = createConceptRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
				ImmutableList.of(statedIsaRequestBody2, inferredIsaRequestBody2, partOfRequestBody), 
				ImmutableList.of(fsnRequestBody2, ptRequestBody2, synonymRequestBody2));
		
		String extensionConceptId2 = lastPathSegment(createComponent(branchPath, SnomedComponentType.CONCEPT, extensionConceptRequestBody2)
				.statusCode(201)
				.extract().header("Location"));
		
		SnomedConcept extensionConcept2 = getComponent(branchPath, SnomedComponentType.CONCEPT, extensionConceptId2, "descriptions(), relationships()")
				.statusCode(200)
				.extract()
				.as(SnomedConcept.class);
		
		String extensionFsnId2 = extensionConcept2.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionFsnTerm2)).findFirst()
				.get().getId();
		String extensionPtId2 = extensionConcept2.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionPtTerm2)).findFirst()
				.get().getId();
		String extensionSynonymId2 = extensionConcept2.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionSynonymTerm2))
				.findFirst().get().getId();
	
		String extensionStatedIsaId2 = extensionConcept2.getRelationships().getItems().stream()
				.filter(r -> r.getCharacteristicType() == CharacteristicType.STATED_RELATIONSHIP).findFirst().get().getId();
		String extensionInferredIsaId2 = extensionConcept2.getRelationships().getItems().stream()
				.filter(r -> r.getCharacteristicType() == CharacteristicType.INFERRED_RELATIONSHIP).findFirst().get().getId();
		String extensionPartOfId = extensionConcept2.getRelationships().getItems().stream()
				.filter(r -> r.getCharacteristicType() == CharacteristicType.ADDITIONAL_RELATIONSHIP).findFirst().get().getId();
		
		// create new version on MAIN
		
		String effectiveDate = getNextAvailableEffectiveDateAsString(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME);
		String versionId = "v13";
		createVersion(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME, versionId, effectiveDate).statusCode(201);
		
		IBranchPath targetPath = BranchPathUtils.createPath(SnomedApiTestConstants.PATH_JOINER.join(
				Branch.MAIN_PATH, 
				versionId, 
				SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME));
	
		createBranch(targetPath).statusCode(201);
		
		// create INT concept with same ID on version branch
		
		Map<String, Object> intFsnRequestBody1 = createDescriptionRequestBody(extensionFsnId1, "", Concepts.MODULE_SCT_CORE,
				Concepts.FULLY_SPECIFIED_NAME, extensionFsnTerm1, SnomedApiTestConstants.UK_PREFERRED_MAP);
		Map<String, Object> intPtRequestBody1 = createDescriptionRequestBody(extensionPtId1, "", Concepts.MODULE_SCT_CORE,
				Concepts.SYNONYM, extensionPtTerm1, SnomedApiTestConstants.UK_PREFERRED_MAP);
	
		Map<String, Object> intStatedIsaRequestBody1 = createRelationshipRequestBody(extensionStatedIsaId1, "", Concepts.MODULE_SCT_CORE, 
				Concepts.IS_A, Concepts.ROOT_CONCEPT, CharacteristicType.STATED_RELATIONSHIP);
		Map<String, Object> intInferredIsaRequestBody1 = createRelationshipRequestBody(extensionInferredIsaId1, "", Concepts.MODULE_SCT_CORE,
				Concepts.IS_A, Concepts.ROOT_CONCEPT, CharacteristicType.INFERRED_RELATIONSHIP);
		
		Map<String, Object> intConceptRequestBody1 = createConceptRequestBody(extensionConceptId1, "", Concepts.MODULE_SCT_CORE,
				ImmutableList.of(intStatedIsaRequestBody1, intInferredIsaRequestBody1), 
				ImmutableList.of(intFsnRequestBody1, intPtRequestBody1));
		
		String intConceptId1 = lastPathSegment(createComponent(targetPath, SnomedComponentType.CONCEPT, intConceptRequestBody1)
				.statusCode(201)
				.body(equalTo(""))
				.extract().header("Location"));
		
		SnomedConcept intConcept1 = getComponent(targetPath, SnomedComponentType.CONCEPT, intConceptId1,
				"descriptions(), relationships()")
				.statusCode(200)
				.extract().as(SnomedConcept.class);
		
		String intFsnId1 = intConcept1.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionFsnTerm1)).findFirst().get()
				.getId();
		String intPtId1 = intConcept1.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionPtTerm1)).findFirst().get().getId();
	
		String intStatedIsaId1 = intConcept1.getRelationships().getItems().stream()
				.filter(r -> r.getCharacteristicType() == CharacteristicType.STATED_RELATIONSHIP).findFirst().get().getId();
		String intInferredIsaId1 = intConcept1.getRelationships().getItems().stream()
				.filter(r -> r.getCharacteristicType() == CharacteristicType.INFERRED_RELATIONSHIP).findFirst().get().getId();
	
		assertEquals(intConceptId1, extensionConceptId1);
		assertNotEquals(intConcept1.getModuleId(), extensionConcept1.getModuleId());
		
		assertEquals(intFsnId1, extensionFsnId1);
		assertEquals(intPtId1, extensionPtId1);
		assertEquals(intStatedIsaId1, extensionStatedIsaId1);
		assertEquals(intInferredIsaId1, extensionInferredIsaId1);
		
		// create another INT concept for extension concept 2 with same ID but WITHOUT the part of relationship on version branch
		
		Map<String, Object> intFsnRequestBody2 = createDescriptionRequestBody(extensionFsnId2, "", Concepts.MODULE_SCT_CORE,
				Concepts.FULLY_SPECIFIED_NAME, extensionFsnTerm2, SnomedApiTestConstants.UK_PREFERRED_MAP);
		Map<String, Object> intPtRequestBody2 = createDescriptionRequestBody(extensionPtId2, "", Concepts.MODULE_SCT_CORE,
				Concepts.SYNONYM, extensionPtTerm2, SnomedApiTestConstants.UK_PREFERRED_MAP);
	
		Map<String, Object> intStatedIsaRequestBody2 = createRelationshipRequestBody(extensionStatedIsaId2, "", Concepts.MODULE_SCT_CORE, 
				Concepts.IS_A, Concepts.ROOT_CONCEPT, CharacteristicType.STATED_RELATIONSHIP);
		Map<String, Object> intInferredIsaRequestBody2 = createRelationshipRequestBody(extensionInferredIsaId2, "", Concepts.MODULE_SCT_CORE,
				Concepts.IS_A, Concepts.ROOT_CONCEPT, CharacteristicType.INFERRED_RELATIONSHIP);
		
		Map<String, Object> intConceptRequestBody2 = createConceptRequestBody(extensionConceptId2, "", Concepts.MODULE_SCT_CORE,
				ImmutableList.of(intStatedIsaRequestBody2, intInferredIsaRequestBody2), 
				ImmutableList.of(intFsnRequestBody2, intPtRequestBody2));
		
		String intConceptId2 = lastPathSegment(createComponent(targetPath, SnomedComponentType.CONCEPT, intConceptRequestBody2)
				.statusCode(201)
				.body(equalTo(""))
				.extract().header("Location"));
		
		SnomedConcept intConcept2 = getComponent(targetPath, SnomedComponentType.CONCEPT, intConceptId2,
				"descriptions(), relationships()")
				.statusCode(200)
				.extract().as(SnomedConcept.class);
		
		String intFsnId2 = intConcept2.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionFsnTerm2)).findFirst().get()
				.getId();
		String intPtId2 = intConcept2.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionPtTerm2)).findFirst().get().getId();
	
		String intStatedIsaId2 = intConcept2.getRelationships().getItems().stream()
				.filter(r -> r.getCharacteristicType() == CharacteristicType.STATED_RELATIONSHIP).findFirst().get().getId();
		String intInferredIsaId2 = intConcept2.getRelationships().getItems().stream()
				.filter(r -> r.getCharacteristicType() == CharacteristicType.INFERRED_RELATIONSHIP).findFirst().get().getId();
	
		assertEquals(intConceptId2, extensionConceptId2);
		assertNotEquals(intConcept2.getModuleId(), extensionConcept2.getModuleId());
		
		assertEquals(intFsnId2, extensionFsnId2);
		assertEquals(intPtId2, extensionPtId2);
		assertEquals(intStatedIsaId2, extensionStatedIsaId2);
		assertEquals(intInferredIsaId2, extensionInferredIsaId2);
		
		// upgrade extension to new INT version
		
		merge(branchPath, targetPath, "Upgraded B2i extension to v13").body("status", equalTo(Merge.Status.COMPLETED.name()));
	
		Map<?, ?> updateRequest = ImmutableMap.builder()
				.put("repositoryUuid", SnomedDatastoreActivator.REPOSITORY_UUID)
				.put("branchPath", targetPath.getPath())
				.build();
	
		updateCodeSystem(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME, updateRequest).statusCode(204);
		getCodeSystem(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME).statusCode(200).body("branchPath", equalTo(targetPath.getPath()));
	
		SnomedConcept donatedConceptInExtension1 = getComponent(targetPath, SnomedComponentType.CONCEPT, extensionConceptId1,
				"descriptions(), relationships()")
				.statusCode(200)
				.extract().as(SnomedConcept.class);
		
		// validate components of donated concept on extension branch
		
		List<SnomedDescription> donatedFsns = donatedConceptInExtension1.getDescriptions().getItems().stream()
				.filter(d -> d.getTerm().equals(extensionFsnTerm1)).collect(toList());

		assertEquals(1, donatedFsns.size());
		SnomedDescription donatedFsn = Iterables.getOnlyElement(donatedFsns);

		assertEquals(extensionFsnId1, donatedFsn.getId());
		assertEquals(Concepts.MODULE_SCT_CORE, donatedFsn.getModuleId());

		List<SnomedDescription> donatedPts = donatedConceptInExtension1.getDescriptions().getItems().stream()
				.filter(d -> d.getTerm().equals(extensionPtTerm1)).collect(toList());

		assertEquals(1, donatedPts.size());
		SnomedDescription donatedPt = Iterables.getOnlyElement(donatedPts);
		
		assertEquals(extensionPtId1, donatedPt.getId());
		assertEquals(Concepts.MODULE_SCT_CORE, donatedPt.getModuleId());
		
		List<SnomedDescription> additionalSynonyms = donatedConceptInExtension1.getDescriptions().getItems().stream()
				.filter(d -> d.getTerm().equals(extensionSynonymTerm1)).collect(toList());

		assertEquals(1, additionalSynonyms.size());
		SnomedDescription additionalSyonym = Iterables.getOnlyElement(additionalSynonyms);
		
		assertEquals(extensionSynonymId1, additionalSyonym.getId());
		assertEquals(Concepts.MODULE_B2I_EXTENSION, additionalSyonym.getModuleId());
		
		List<SnomedRelationship> donatedStatedIsas = donatedConceptInExtension1.getRelationships().getItems().stream()
				.filter(r -> r.getTypeId().equals(Concepts.IS_A) && r.getCharacteristicType() == CharacteristicType.STATED_RELATIONSHIP)
				.collect(toList());
		
		assertEquals(1, donatedStatedIsas.size());
		SnomedRelationship donatedStatedIsa = Iterables.getOnlyElement(donatedStatedIsas);
		assertEquals(extensionStatedIsaId1, donatedStatedIsa.getId());
		assertEquals(Concepts.MODULE_SCT_CORE, donatedStatedIsa.getModuleId());
		
		List<SnomedRelationship> donatedInferredIsas = donatedConceptInExtension1.getRelationships().getItems().stream()
				.filter(r -> r.getTypeId().equals(Concepts.IS_A) && r.getCharacteristicType() == CharacteristicType.INFERRED_RELATIONSHIP)
				.collect(toList());
		
		assertEquals(1, donatedInferredIsas.size());
		SnomedRelationship donatedInferredIsa = Iterables.getOnlyElement(donatedInferredIsas);
		assertEquals(extensionInferredIsaId1, donatedInferredIsa.getId());
		assertEquals(Concepts.MODULE_SCT_CORE, donatedInferredIsa.getModuleId());
		
		// validate components of donated concept 2 on extension branch

		SnomedConcept donatedConceptInExtension2 = getComponent(targetPath, SnomedComponentType.CONCEPT, extensionConceptId2,
				"descriptions(), relationships()")
				.statusCode(200)
				.extract().as(SnomedConcept.class);
		
		List<SnomedDescription> donatedFsns2 = donatedConceptInExtension2.getDescriptions().getItems().stream()
				.filter(d -> d.getTerm().equals(extensionFsnTerm2)).collect(toList());

		assertEquals(1, donatedFsns2.size());
		SnomedDescription donatedFsn2 = Iterables.getOnlyElement(donatedFsns2);

		assertEquals(extensionFsnId2, donatedFsn2.getId());
		assertEquals(Concepts.MODULE_SCT_CORE, donatedFsn2.getModuleId());

		List<SnomedDescription> donatedPts2 = donatedConceptInExtension2.getDescriptions().getItems().stream()
				.filter(d -> d.getTerm().equals(extensionPtTerm2)).collect(toList());

		assertEquals(1, donatedPts2.size());
		SnomedDescription donatedPt2 = Iterables.getOnlyElement(donatedPts2);
		
		assertEquals(extensionPtId2, donatedPt2.getId());
		assertEquals(Concepts.MODULE_SCT_CORE, donatedPt2.getModuleId());
		
		List<SnomedDescription> additionalSynonyms2 = donatedConceptInExtension2.getDescriptions().getItems().stream()
				.filter(d -> d.getTerm().equals(extensionSynonymTerm2)).collect(toList());

		assertEquals(1, additionalSynonyms2.size());
		SnomedDescription additionalSyonym2 = Iterables.getOnlyElement(additionalSynonyms2);
		
		assertEquals(extensionSynonymId2, additionalSyonym2.getId());
		assertEquals(Concepts.MODULE_B2I_EXTENSION, additionalSyonym2.getModuleId());
		
		List<SnomedRelationship> donatedStatedIsas2 = donatedConceptInExtension2.getRelationships().getItems().stream()
				.filter(r -> r.getTypeId().equals(Concepts.IS_A) && r.getCharacteristicType() == CharacteristicType.STATED_RELATIONSHIP)
				.collect(toList());
		
		assertEquals(1, donatedStatedIsas2.size());
		SnomedRelationship donatedStatedIsa2 = Iterables.getOnlyElement(donatedStatedIsas2);
		assertEquals(extensionStatedIsaId2, donatedStatedIsa2.getId());
		assertEquals(Concepts.MODULE_SCT_CORE, donatedStatedIsa2.getModuleId());
		
		List<SnomedRelationship> donatedInferredIsas2 = donatedConceptInExtension2.getRelationships().getItems().stream()
				.filter(r -> r.getTypeId().equals(Concepts.IS_A) && r.getCharacteristicType() == CharacteristicType.INFERRED_RELATIONSHIP)
				.collect(toList());
		
		assertEquals(1, donatedInferredIsas2.size());
		SnomedRelationship donatedInferredIsa2 = Iterables.getOnlyElement(donatedInferredIsas2);
		assertEquals(extensionInferredIsaId2, donatedInferredIsa2.getId());
		assertEquals(Concepts.MODULE_SCT_CORE, donatedInferredIsa2.getModuleId());
		
		List<SnomedRelationship> additionalRelationships = donatedConceptInExtension2.getRelationships().getItems().stream()
				.filter(r -> r.getCharacteristicType() == CharacteristicType.ADDITIONAL_RELATIONSHIP)
				.collect(toList());
		
		assertEquals(1, additionalRelationships.size());
		SnomedRelationship additionalRelationship = Iterables.getOnlyElement(additionalRelationships);
		assertEquals(extensionPartOfId, additionalRelationship.getId());
		assertEquals(Concepts.MODULE_B2I_EXTENSION, additionalRelationship.getModuleId());
		assertEquals(extensionConceptId1, additionalRelationship.getDestinationId());

	}

	@Test
	public void upgradeWithDonatedConceptAndDescriptionsAndRelationshipsWithExternalReference() {
		
		// create extension concept on extension's current branch
		
		String extensionFsnTerm1 = "FSN of concept";
		String extensionPtTerm1 = "PT of concept";
		String extensionSynonymTerm1 = "Synonym of extension concept";
		
		Map<String, Object> fsnRequestBody1 = createDescriptionRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
				Concepts.FULLY_SPECIFIED_NAME, extensionFsnTerm1, SnomedApiTestConstants.UK_PREFERRED_MAP);
		Map<String, Object> ptRequestBody1 = createDescriptionRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
				Concepts.SYNONYM, extensionPtTerm1, SnomedApiTestConstants.UK_PREFERRED_MAP);
		Map<String, Object> synonymRequestBody1 = createDescriptionRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION, 
				Concepts.SYNONYM, extensionSynonymTerm1, SnomedApiTestConstants.UK_ACCEPTABLE_MAP);
	
		Map<String, Object> statedIsaRequestBody1 = createRelationshipRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION, 
				Concepts.IS_A, Concepts.ROOT_CONCEPT, CharacteristicType.STATED_RELATIONSHIP);
		Map<String, Object> inferredIsaRequestBody1 = createRelationshipRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
				Concepts.IS_A, Concepts.ROOT_CONCEPT, CharacteristicType.INFERRED_RELATIONSHIP);
	
		Map<String, Object> extensionConceptRequestBody1 = createConceptRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
				ImmutableList.of(statedIsaRequestBody1, inferredIsaRequestBody1), 
				ImmutableList.of(fsnRequestBody1, ptRequestBody1, synonymRequestBody1));
		
		String extensionConceptId1 = lastPathSegment(createComponent(branchPath, SnomedComponentType.CONCEPT, extensionConceptRequestBody1)
				.statusCode(201)
				.extract().header("Location"));
		
		SnomedConcept extensionConcept1 = getComponent(branchPath, SnomedComponentType.CONCEPT, extensionConceptId1, "descriptions(), relationships()")
				.statusCode(200)
				.extract()
				.as(SnomedConcept.class);
		
		String extensionFsnId1 = extensionConcept1.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionFsnTerm1)).findFirst()
				.get().getId();
		String extensionPtId1 = extensionConcept1.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionPtTerm1)).findFirst()
				.get().getId();
		String extensionSynonymId1 = extensionConcept1.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionSynonymTerm1))
				.findFirst().get().getId();
	
		String extensionStatedIsaId1 = extensionConcept1.getRelationships().getItems().stream()
				.filter(r -> r.getCharacteristicType() == CharacteristicType.STATED_RELATIONSHIP).findFirst().get().getId();
		String extensionInferredIsaId1 = extensionConcept1.getRelationships().getItems().stream()
				.filter(r -> r.getCharacteristicType() == CharacteristicType.INFERRED_RELATIONSHIP).findFirst().get().getId();
	
		// create another extension concept which references the previous one
		
		String extensionFsnTerm2 = "FSN of concept 2";
		String extensionPtTerm2 = "PT of concept 2";
		String extensionSynonymTerm2 = "Synonym of extension concept 2";
		
		Map<String, Object> fsnRequestBody2 = createDescriptionRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
				Concepts.FULLY_SPECIFIED_NAME, extensionFsnTerm2, SnomedApiTestConstants.UK_PREFERRED_MAP);
		Map<String, Object> ptRequestBody2 = createDescriptionRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
				Concepts.SYNONYM, extensionPtTerm2, SnomedApiTestConstants.UK_PREFERRED_MAP);
		Map<String, Object> synonymRequestBody2 = createDescriptionRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION, 
				Concepts.SYNONYM, extensionSynonymTerm2, SnomedApiTestConstants.UK_ACCEPTABLE_MAP);
	
		Map<String, Object> statedIsaRequestBody2 = createRelationshipRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION, 
				Concepts.IS_A, Concepts.ROOT_CONCEPT, CharacteristicType.STATED_RELATIONSHIP);
		Map<String, Object> inferredIsaRequestBody2 = createRelationshipRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
				Concepts.IS_A, Concepts.ROOT_CONCEPT, CharacteristicType.INFERRED_RELATIONSHIP);
		
		Map<String, Object> partOfRequestBody = createRelationshipRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
				Concepts.PART_OF, extensionConceptId1, CharacteristicType.ADDITIONAL_RELATIONSHIP);
	
		Map<String, Object> extensionConceptRequestBody2 = createConceptRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
				ImmutableList.of(statedIsaRequestBody2, inferredIsaRequestBody2, partOfRequestBody), 
				ImmutableList.of(fsnRequestBody2, ptRequestBody2, synonymRequestBody2));
		
		String extensionConceptId2 = lastPathSegment(createComponent(branchPath, SnomedComponentType.CONCEPT, extensionConceptRequestBody2)
				.statusCode(201)
				.extract().header("Location"));
		
		SnomedConcept extensionConcept2 = getComponent(branchPath, SnomedComponentType.CONCEPT, extensionConceptId2, "descriptions(), relationships()")
				.statusCode(200)
				.extract()
				.as(SnomedConcept.class);
		
		String extensionFsnId2 = extensionConcept2.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionFsnTerm2)).findFirst()
				.get().getId();
		String extensionPtId2 = extensionConcept2.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionPtTerm2)).findFirst()
				.get().getId();
		String extensionSynonymId2 = extensionConcept2.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionSynonymTerm2))
				.findFirst().get().getId();
	
		String extensionStatedIsaId2 = extensionConcept2.getRelationships().getItems().stream()
				.filter(r -> r.getCharacteristicType() == CharacteristicType.STATED_RELATIONSHIP).findFirst().get().getId();
		String extensionInferredIsaId2 = extensionConcept2.getRelationships().getItems().stream()
				.filter(r -> r.getCharacteristicType() == CharacteristicType.INFERRED_RELATIONSHIP).findFirst().get().getId();
		String extensionPartOfId = extensionConcept2.getRelationships().getItems().stream()
				.filter(r -> r.getCharacteristicType() == CharacteristicType.ADDITIONAL_RELATIONSHIP).findFirst().get().getId();
		
		// create new version on MAIN
		
		String effectiveDate = getNextAvailableEffectiveDateAsString(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME);
		String versionId = "v12";
		createVersion(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME, versionId, effectiveDate).statusCode(201);
		
		IBranchPath targetPath = BranchPathUtils.createPath(SnomedApiTestConstants.PATH_JOINER.join(
				Branch.MAIN_PATH, 
				versionId, 
				SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME));
	
		createBranch(targetPath).statusCode(201);
		
		// create INT concept with same ID on version branch
		
		Map<String, Object> intFsnRequestBody1 = createDescriptionRequestBody(extensionFsnId1, "", Concepts.MODULE_SCT_CORE,
				Concepts.FULLY_SPECIFIED_NAME, extensionFsnTerm1, SnomedApiTestConstants.UK_PREFERRED_MAP);
		Map<String, Object> intPtRequestBody1 = createDescriptionRequestBody(extensionPtId1, "", Concepts.MODULE_SCT_CORE,
				Concepts.SYNONYM, extensionPtTerm1, SnomedApiTestConstants.UK_PREFERRED_MAP);
	
		Map<String, Object> intStatedIsaRequestBody1 = createRelationshipRequestBody(extensionStatedIsaId1, "", Concepts.MODULE_SCT_CORE, 
				Concepts.IS_A, Concepts.ROOT_CONCEPT, CharacteristicType.STATED_RELATIONSHIP);
		Map<String, Object> intInferredIsaRequestBody1 = createRelationshipRequestBody(extensionInferredIsaId1, "", Concepts.MODULE_SCT_CORE,
				Concepts.IS_A, Concepts.ROOT_CONCEPT, CharacteristicType.INFERRED_RELATIONSHIP);
		
		Map<String, Object> intConceptRequestBody1 = createConceptRequestBody(extensionConceptId1, "", Concepts.MODULE_SCT_CORE,
				ImmutableList.of(intStatedIsaRequestBody1, intInferredIsaRequestBody1), 
				ImmutableList.of(intFsnRequestBody1, intPtRequestBody1));
		
		String intConceptId1 = lastPathSegment(createComponent(targetPath, SnomedComponentType.CONCEPT, intConceptRequestBody1)
				.statusCode(201)
				.body(equalTo(""))
				.extract().header("Location"));
		
		SnomedConcept intConcept1 = getComponent(targetPath, SnomedComponentType.CONCEPT, intConceptId1,
				"descriptions(), relationships()")
				.statusCode(200)
				.extract().as(SnomedConcept.class);
		
		String intFsnId1 = intConcept1.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionFsnTerm1)).findFirst().get()
				.getId();
		String intPtId1 = intConcept1.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionPtTerm1)).findFirst().get().getId();
	
		String intStatedIsaId1 = intConcept1.getRelationships().getItems().stream()
				.filter(r -> r.getCharacteristicType() == CharacteristicType.STATED_RELATIONSHIP).findFirst().get().getId();
		String intInferredIsaId1 = intConcept1.getRelationships().getItems().stream()
				.filter(r -> r.getCharacteristicType() == CharacteristicType.INFERRED_RELATIONSHIP).findFirst().get().getId();
	
		assertEquals(intConceptId1, extensionConceptId1);
		assertNotEquals(intConcept1.getModuleId(), extensionConcept1.getModuleId());
		
		assertEquals(intFsnId1, extensionFsnId1);
		assertEquals(intPtId1, extensionPtId1);
		assertEquals(intStatedIsaId1, extensionStatedIsaId1);
		assertEquals(intInferredIsaId1, extensionInferredIsaId1);
		
		// do not create an international pair for extension concept 2 (so we can handle the "external" reference)
		
		// upgrade extension to new INT version
		
		merge(branchPath, targetPath, "Upgraded B2i extension to v12").body("status", equalTo(Merge.Status.COMPLETED.name()));
	
		Map<?, ?> updateRequest = ImmutableMap.builder()
				.put("repositoryUuid", SnomedDatastoreActivator.REPOSITORY_UUID)
				.put("branchPath", targetPath.getPath())
				.build();
	
		updateCodeSystem(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME, updateRequest).statusCode(204);
		getCodeSystem(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME).statusCode(200).body("branchPath", equalTo(targetPath.getPath()));
	
		SnomedConcept donatedConceptInExtension1 = getComponent(targetPath, SnomedComponentType.CONCEPT, extensionConceptId1,
				"descriptions(), relationships()")
				.statusCode(200)
				.extract().as(SnomedConcept.class);
		
		// validate components of donated concept on extension branch
		
		List<SnomedDescription> donatedFsns = donatedConceptInExtension1.getDescriptions().getItems().stream()
				.filter(d -> d.getTerm().equals(extensionFsnTerm1)).collect(toList());

		assertEquals(1, donatedFsns.size());
		SnomedDescription donatedFsn = Iterables.getOnlyElement(donatedFsns);

		assertEquals(extensionFsnId1, donatedFsn.getId());
		assertEquals(Concepts.MODULE_SCT_CORE, donatedFsn.getModuleId());

		List<SnomedDescription> donatedPts = donatedConceptInExtension1.getDescriptions().getItems().stream()
				.filter(d -> d.getTerm().equals(extensionPtTerm1)).collect(toList());

		assertEquals(1, donatedPts.size());
		SnomedDescription donatedPt = Iterables.getOnlyElement(donatedPts);
		
		assertEquals(extensionPtId1, donatedPt.getId());
		assertEquals(Concepts.MODULE_SCT_CORE, donatedPt.getModuleId());
		
		List<SnomedDescription> additionalSynonyms = donatedConceptInExtension1.getDescriptions().getItems().stream()
				.filter(d -> d.getTerm().equals(extensionSynonymTerm1)).collect(toList());

		assertEquals(1, additionalSynonyms.size());
		SnomedDescription additionalSyonym = Iterables.getOnlyElement(additionalSynonyms);
		
		assertEquals(extensionSynonymId1, additionalSyonym.getId());
		assertEquals(Concepts.MODULE_B2I_EXTENSION, additionalSyonym.getModuleId());
		
		List<SnomedRelationship> donatedStatedIsas = donatedConceptInExtension1.getRelationships().getItems().stream()
				.filter(r -> r.getTypeId().equals(Concepts.IS_A) && r.getCharacteristicType() == CharacteristicType.STATED_RELATIONSHIP)
				.collect(toList());
		
		assertEquals(1, donatedStatedIsas.size());
		SnomedRelationship donatedStatedIsa = Iterables.getOnlyElement(donatedStatedIsas);
		assertEquals(extensionStatedIsaId1, donatedStatedIsa.getId());
		assertEquals(Concepts.MODULE_SCT_CORE, donatedStatedIsa.getModuleId());
		
		List<SnomedRelationship> donatedInferredIsas = donatedConceptInExtension1.getRelationships().getItems().stream()
				.filter(r -> r.getTypeId().equals(Concepts.IS_A) && r.getCharacteristicType() == CharacteristicType.INFERRED_RELATIONSHIP)
				.collect(toList());
		
		assertEquals(1, donatedInferredIsas.size());
		SnomedRelationship donatedInferredIsa = Iterables.getOnlyElement(donatedInferredIsas);
		assertEquals(extensionInferredIsaId1, donatedInferredIsa.getId());
		assertEquals(Concepts.MODULE_SCT_CORE, donatedInferredIsa.getModuleId());
		
		// validate components of extension concept 2 on extension branch

		SnomedConcept extensionConceptInExtension2 = getComponent(targetPath, SnomedComponentType.CONCEPT, extensionConceptId2,
				"descriptions(), relationships()")
				.statusCode(200)
				.extract().as(SnomedConcept.class);
		
		List<SnomedDescription> extensionFsns2 = extensionConceptInExtension2.getDescriptions().getItems().stream()
				.filter(d -> d.getTerm().equals(extensionFsnTerm2)).collect(toList());

		assertEquals(1, extensionFsns2.size());
		SnomedDescription extensionFsn2 = Iterables.getOnlyElement(extensionFsns2);

		assertEquals(extensionFsnId2, extensionFsn2.getId());
		assertEquals(Concepts.MODULE_B2I_EXTENSION, extensionFsn2.getModuleId());

		List<SnomedDescription> extensionPts2 = extensionConceptInExtension2.getDescriptions().getItems().stream()
				.filter(d -> d.getTerm().equals(extensionPtTerm2)).collect(toList());

		assertEquals(1, extensionPts2.size());
		SnomedDescription extensionPt2 = Iterables.getOnlyElement(extensionPts2);
		
		assertEquals(extensionPtId2, extensionPt2.getId());
		assertEquals(Concepts.MODULE_B2I_EXTENSION, extensionPt2.getModuleId());
		
		List<SnomedDescription> additionalSynonyms2 = extensionConceptInExtension2.getDescriptions().getItems().stream()
				.filter(d -> d.getTerm().equals(extensionSynonymTerm2)).collect(toList());

		assertEquals(1, additionalSynonyms2.size());
		SnomedDescription additionalSyonym2 = Iterables.getOnlyElement(additionalSynonyms2);
		
		assertEquals(extensionSynonymId2, additionalSyonym2.getId());
		assertEquals(Concepts.MODULE_B2I_EXTENSION, additionalSyonym2.getModuleId());
		
		List<SnomedRelationship> extensionStatedIsas2 = extensionConceptInExtension2.getRelationships().getItems().stream()
				.filter(r -> r.getTypeId().equals(Concepts.IS_A) && r.getCharacteristicType() == CharacteristicType.STATED_RELATIONSHIP)
				.collect(toList());
		
		assertEquals(1, extensionStatedIsas2.size());
		SnomedRelationship extensionStatedIsa2 = Iterables.getOnlyElement(extensionStatedIsas2);
		assertEquals(extensionStatedIsaId2, extensionStatedIsa2.getId());
		assertEquals(Concepts.MODULE_B2I_EXTENSION, extensionStatedIsa2.getModuleId());
		
		List<SnomedRelationship> extensionInferredIsas2 = extensionConceptInExtension2.getRelationships().getItems().stream()
				.filter(r -> r.getTypeId().equals(Concepts.IS_A) && r.getCharacteristicType() == CharacteristicType.INFERRED_RELATIONSHIP)
				.collect(toList());
		
		assertEquals(1, extensionInferredIsas2.size());
		SnomedRelationship extensionInferredIsa2 = Iterables.getOnlyElement(extensionInferredIsas2);
		assertEquals(extensionInferredIsaId2, extensionInferredIsa2.getId());
		assertEquals(Concepts.MODULE_B2I_EXTENSION, extensionInferredIsa2.getModuleId());
		
		List<SnomedRelationship> additionalRelationships = extensionConceptInExtension2.getRelationships().getItems().stream()
				.filter(r -> r.getCharacteristicType() == CharacteristicType.ADDITIONAL_RELATIONSHIP)
				.collect(toList());
		
		assertEquals(1, additionalRelationships.size());
		SnomedRelationship additionalRelationship = Iterables.getOnlyElement(additionalRelationships);
		assertEquals(extensionPartOfId, additionalRelationship.getId());
		assertEquals(Concepts.MODULE_B2I_EXTENSION, additionalRelationship.getModuleId());
		assertEquals(extensionConceptId1, additionalRelationship.getDestinationId());

	}
	
	@AfterClass
	public static void restoreB2iCodeSystem() {
		Map<?, ?> updateRequest = ImmutableMap.builder()
				.put("repositoryUuid", SnomedDatastoreActivator.REPOSITORY_UUID)
				.put("branchPath", SnomedApiTestConstants.EXTENSION_PATH)
				.build();

		updateCodeSystem(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME, updateRequest).statusCode(204);
	}
	
	private Map<String, Object> createConceptRequestBody(String namespace, String moduleId, List<?> relationshipRequestBodies, List<?> descriptionRequestBodies) {
		return createConceptRequestBody("", namespace, moduleId, relationshipRequestBodies, descriptionRequestBodies);
	}
	
	private Map<String, Object> createConceptRequestBody(String id, String namespace, String moduleId, List<?> relationshipRequestBodies, List<?> descriptionRequestBodies) {
		
		Builder<String, Object> builder = ImmutableMap.<String, Object>builder()
				.put("moduleId", moduleId)
				.put("namespaceId", Strings.isNullOrEmpty(namespace) ? "" : namespace)
				.put("descriptions", descriptionRequestBodies)
				.put("relationships", relationshipRequestBodies)
				.put("commitComment", "Added concept");
		
		if (!Strings.isNullOrEmpty(id)) {
			builder.put("id", id);
		}
		
		return builder.build();
		
	}
	
	private Map<String, Object> createDescriptionRequestBody(String namespace, String moduleId, String typeId, String term, Map<String, Acceptability> acceptabilityMap) {
		return createDescriptionRequestBody("", namespace, moduleId, typeId, term, acceptabilityMap);
	}
	
	private Map<String, Object> createDescriptionRequestBody(String id, String namespace, String moduleId, String typeId, String term, Map<String, Acceptability> acceptabilityMap) {
		
		Builder<String, Object> builder = ImmutableMap.<String, Object>builder()
			.put("moduleId", moduleId)
			.put("namespaceId", Strings.isNullOrEmpty(namespace) ? "" : namespace)
			.put("typeId", typeId)
			.put("term", term)
			.put("languageCode", DEFAULT_LANGUAGE_CODE)
			.put("acceptability", acceptabilityMap);
			
		if (!Strings.isNullOrEmpty(id)) {
			builder.put("id", id);
		}
		
		return builder.build();
	}

	private Map<String, Object> createRelationshipRequestBody(String namespace, String moduleId, String typeId, String destinationId, CharacteristicType characteristicType) {
		return createRelationshipRequestBody("", namespace, moduleId, typeId, destinationId, characteristicType);
	}
	
	private Map<String, Object> createRelationshipRequestBody(String id, String namespace, String moduleId, String typeId, String destinationId, CharacteristicType characteristicType) {
		
		Builder<String, Object> builder = ImmutableMap.<String, Object>builder()
			.put("moduleId", moduleId)
			.put("namespaceId", Strings.isNullOrEmpty(namespace) ? "" : namespace)
			.put("typeId", typeId)
			.put("destinationId", destinationId)
			.put("characteristicType", characteristicType);
		
		if (!Strings.isNullOrEmpty(id)) {
			builder.put("id", id);
		}
		
		return builder.build();
	}
	
}
