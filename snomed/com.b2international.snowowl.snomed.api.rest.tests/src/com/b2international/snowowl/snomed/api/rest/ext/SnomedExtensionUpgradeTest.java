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
import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
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
		
		// create extension concept on extension's current branch
		
		String extensionFsnTerm = "FSN of concept";
		String extensionPtTerm = "PT of concept";
		String extensionSynonymTerm = "Synonym of extension concept";
		
		Map<String, Object> extensionConceptRequestBody = createConceptRequestBody("", Concepts.MODULE_B2I_EXTENSION, Concepts.B2I_NAMESPACE,
				extensionFsnTerm, extensionPtTerm, extensionSynonymTerm);

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
		String versionId = "v5";
		createVersion(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME, versionId, effectiveDate).statusCode(201);
		
		IBranchPath targetPath = BranchPathUtils.createPath(SnomedApiTestConstants.PATH_JOINER.join(
				Branch.MAIN_PATH, 
				versionId, 
				SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME));

		createBranch(targetPath).statusCode(201);
		
		// create INT concept with same ID but different description and relationship IDs on version branch
		
		Map<String, Object> intConceptRequestBody = createConceptRequestBody(extensionConceptId, Concepts.MODULE_SCT_CORE, null, extensionFsnTerm,
				extensionPtTerm);
		
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
	}
	
	@AfterClass
	public static void restoreB2iCodeSystem() {
		Map<?, ?> updateRequest = ImmutableMap.builder()
				.put("repositoryUuid", SnomedDatastoreActivator.REPOSITORY_UUID)
				.put("branchPath", SnomedApiTestConstants.EXTENSION_PATH)
				.build();

		updateCodeSystem(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME, updateRequest).statusCode(204);
	}
	
	private Map<String, Object> createConceptRequestBody(String id, String moduleId, String namespace, String fsn, String pt, String...synonyms) {
		
		Map<?, ?> statedRelationshipRequestBody = ImmutableMap.builder()
				.put("moduleId", moduleId)
				.put("namespaceId", Strings.isNullOrEmpty(namespace) ? "" : namespace)
				.put("typeId", Concepts.IS_A)
				.put("destinationId", Concepts.ROOT_CONCEPT)
				.put("characteristicType", CharacteristicType.STATED_RELATIONSHIP)
				.build();
		
		Map<?, ?> inferredRelationshipRequestBody = ImmutableMap.builder()
				.put("moduleId", moduleId)
				.put("namespaceId", Strings.isNullOrEmpty(namespace) ? "" : namespace)
				.put("typeId", Concepts.IS_A)
				.put("destinationId", Concepts.ROOT_CONCEPT)
				.put("characteristicType", CharacteristicType.INFERRED_RELATIONSHIP)
				.build();

		Map<?, ?> fsnRequestBody = ImmutableMap.builder()
				.put("moduleId", moduleId)
				.put("namespaceId", Strings.isNullOrEmpty(namespace) ? "" : namespace)
				.put("typeId", Concepts.FULLY_SPECIFIED_NAME)
				.put("term", fsn)
				.put("languageCode", DEFAULT_LANGUAGE_CODE)
				.put("acceptability", SnomedApiTestConstants.UK_PREFERRED_MAP)
				.build();
		
		Map<?, ?> ptRequestBody = ImmutableMap.builder()
				.put("moduleId", moduleId)
				.put("namespaceId", Strings.isNullOrEmpty(namespace) ? "" : namespace)
				.put("typeId", Concepts.SYNONYM)
				.put("term", pt)
				.put("languageCode", DEFAULT_LANGUAGE_CODE)
				.put("acceptability", SnomedApiTestConstants.UK_PREFERRED_MAP)
				.build();
		
		List<Map<?, ?>> synonymRequests = newArrayList();
		
		for (String synonym : synonyms) {
			
			synonymRequests.add(ImmutableMap.builder()
					.put("moduleId", moduleId)
					.put("namespaceId", Strings.isNullOrEmpty(namespace) ? "" : namespace)
					.put("typeId", Concepts.SYNONYM)
					.put("term", synonym)
					.put("languageCode", DEFAULT_LANGUAGE_CODE)
					.put("acceptability", SnomedApiTestConstants.UK_ACCEPTABLE_MAP)
					.build());
			
		}

		Builder<String, Object> builder = ImmutableMap.<String, Object>builder()
				.put("moduleId", moduleId)
				.put("namespaceId", Strings.isNullOrEmpty(namespace) ? "" : namespace)
				.put("descriptions", ImmutableList.builder().add(fsnRequestBody).add(ptRequestBody).addAll(synonymRequests).build())
				.put("relationships", ImmutableList.of(statedRelationshipRequestBody, inferredRelationshipRequestBody))
				.put("commitComment", "Added concept");
		
		if (!Strings.isNullOrEmpty(id)) {
			builder.put("id", id);
		}
		
		return builder.build();
	}
	
}
