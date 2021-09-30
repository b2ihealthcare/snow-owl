/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.rest.ext;

import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedMergingRestRequests.createMerge;
import static com.b2international.snowowl.snomed.core.rest.SnomedMergingRestRequests.waitForMergeJob;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.*;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.createVersion;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.getLatestVersion;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.getNextAvailableEffectiveDate;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.b2international.commons.json.Json;
import com.b2international.index.revision.BaseRevisionBranching;
import com.b2international.index.revision.RevisionBranch.BranchState;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.branch.BranchPathUtils;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.codesystem.CodeSystems;
import com.b2international.snowowl.core.merge.Merge;
import com.b2international.snowowl.core.version.Version;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.core.rest.SnomedComponentType;
import com.b2international.snowowl.test.commons.SnomedContentRule;
import com.b2international.snowowl.test.commons.codesystem.CodeSystemRestRequests;
import com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests;

/**
 * @since 4.7
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SnomedExtensionUpgradeTest extends AbstractSnomedExtensionApiTest {

	private ResourceURI latestInternationalVersion;

	@Before
	public void setup() {
		latestInternationalVersion = SnomedContentRule.SNOMEDCT.withPath(getLatestVersion(SnomedContentRule.SNOMEDCT_ID).get().getVersion());
	}
	
	@Test
	public void upgrade01Nochanges() {
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		
		// create a new INT version without any changes
		LocalDate effectiveTime = getNextAvailableEffectiveDate(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveTime).statusCode(201);
		
		// start upgrade
		final ResourceURI newExtensionOf = CodeSystem.uri(SNOMEDCT, effectiveTime.toString());
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extension.getResourceURI(), newExtensionOf);
		assertEquals(newExtensionOf, upgradeCodeSystem.getExtensionOf());
	}
	
	@Test
	public void upgrade02NewExtensionConceptOnly() {
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		
		// create a new INT version without any changes
		LocalDate effectiveTime = getNextAvailableEffectiveDate(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveTime).statusCode(201);
		ResourceURI upgradeVersion = CodeSystem.uri(SNOMEDCT, effectiveTime.toString());
		
		// create new extension version with one new concept, module in this case
		String moduleId = createModule(extension);
		createVersion(extension.getId(), "v1", LocalDate.now()).statusCode(201);
		
		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extension.getResourceURI(), upgradeVersion);
		assertEquals(upgradeVersion, upgradeCodeSystem.getExtensionOf());
		
		getComponent(upgradeCodeSystem.getResourceURI(), SnomedComponentType.CONCEPT, moduleId).statusCode(200);
	}

	@Test
	public void upgrade03NewInternationalConceptOnly() {
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		
		// new SI concept
		String newConceptId = createConcept(SnomedContentRule.SNOMEDCT, createConceptRequestBody(Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE));
		
		// create a new INT version
		LocalDate effectiveTime = getNextAvailableEffectiveDate(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveTime).statusCode(201);
		ResourceURI upgradeVersion = CodeSystem.uri(SNOMEDCT, effectiveTime.toString());
		
		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extension.getResourceURI(), upgradeVersion);
		assertEquals(upgradeVersion, upgradeCodeSystem.getExtensionOf());
		
		getComponent(upgradeCodeSystem.getResourceURI(), SnomedComponentType.CONCEPT, newConceptId).statusCode(200);
	}

	@Test
	public void upgrade04InternationalAddedDirectlyToExtensionResultsInConflict() {
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		
		// new SI concept
		String newConceptId = createConcept(SnomedContentRule.SNOMEDCT, createConceptRequestBody(Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE));
		
		// create a new INT version
		LocalDate effectiveTime = getNextAvailableEffectiveDate(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveTime).statusCode(201);
		ResourceURI upgradeVersion = CodeSystem.uri(SNOMEDCT, effectiveTime.toString());
		
		// replay SI concept on extension branch, simulating direct import of a preview or release SI RF2
		createConcept(extension.getResourceURI(), createConceptRequestBody(Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE).with("id", newConceptId));
		createVersion(extension.getId(), "v1", LocalDate.now()).statusCode(201);
		
		// start upgrade but it fails due to same content present on both sides
		assertCodeSystemUpgrade(extension.getResourceURI(), upgradeVersion).statusCode(409);
	}
	
	@Test
	public void upgrade05DonatedConcept() {
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		
		String extensionModuleId = createModule(extension);
		String extensionConceptId = createConcept(extension.getResourceURI(), createConceptRequestBody(Concepts.ROOT_CONCEPT, extensionModuleId)
				.with("namespaceId", Concepts.B2I_NAMESPACE));
		// put additional relationships and acceptable synonym on the concept
		String additionalSynonymId = createDescription(extension.getResourceURI(), createDescriptionRequestBody(extensionConceptId)
				.with("moduleId", extensionModuleId)
				.with("namespaceId", Concepts.B2I_NAMESPACE)
				.with("term", "Additional Synonym"));
		String additionalRelationshipId = createRelationship(extension.getResourceURI(), createRelationshipRequestBody(extensionConceptId, Concepts.FINDING_SITE, Concepts.ROOT_CONCEPT)
				.with("moduleId", extensionModuleId)
				.with(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID, Concepts.ADDITIONAL_RELATIONSHIP)
				.with("namespaceId", Concepts.B2I_NAMESPACE));
		
		SnomedConcept extensionConcept = getConcept(extension.getResourceURI(), extensionConceptId, "descriptions()", "relationships()", "fsn()", "pt()");

		// create new extension version
		createVersion(extension.getId(), "v1", LocalDate.now()).statusCode(201);
		
		// simulate donation to SI and new version import
		String donatedConceptId = createConcept(SnomedContentRule.SNOMEDCT, createConceptRequestBody(Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE)
				.with("id", extensionConceptId));
		
		LocalDate effectiveTime = getNextAvailableEffectiveDate(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveTime).statusCode(201);
		
		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extension.getResourceURI(), CodeSystem.uri(SNOMEDCT, effectiveTime.toString()));

		SnomedConcept donatedConceptAfterUpgrade = getConcept(upgradeCodeSystem.getResourceURI(), donatedConceptId, "descriptions()", "relationships()", "fsn()", "pt()");
		
		// validate components of donated concept on extension branch
		
		assertEquals(extensionConcept.getId(), donatedConceptAfterUpgrade.getId());
		assertNotEquals(extensionConcept.getModuleId(), donatedConceptAfterUpgrade.getModuleId());
		
		// additional not donated extension content should remain on the concept
		assertTrue(donatedConceptAfterUpgrade.getDescriptions().stream().filter(desc -> additionalSynonymId.equals(desc.getId())).findFirst().isPresent());
		assertTrue(donatedConceptAfterUpgrade.getRelationships().stream().filter(rel -> additionalRelationshipId.equals(rel.getId())).findFirst().isPresent());
	}
	
	@Test
	public void upgrade06DonatedDescription() throws Exception {
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		
		String extensionModuleId = createModule(extension);
		String descriptionTerm = "Donated synonym of root concept";
		
		Json extensionDescriptionRequest = Json.object(
			"conceptId", Concepts.ROOT_CONCEPT,
			"namespaceId", Concepts.B2I_NAMESPACE,
			"moduleId", extensionModuleId,
			"typeId", Concepts.SYNONYM,
			"term", descriptionTerm,
			"languageCode", DEFAULT_LANGUAGE_CODE,
			"acceptability", SnomedApiTestConstants.US_ACCEPTABLE_MAP,
			"caseSignificanceId", Concepts.ONLY_INITIAL_CHARACTER_CASE_INSENSITIVE,
			"commitComment", "Created new extension synonym"
		);

		String extensionDescriptionId = createDescription(extension.getResourceURI(), extensionDescriptionRequest);
		
		// create new extension version
		createVersion(extension.getId(), "v1", LocalDate.now()).statusCode(201);

		// simulate donation to SI with same ID but with slightly different properties
		Json donatedDescriptionRequest = Json.assign(
			extensionDescriptionRequest,
			Json.object(
				"id", extensionDescriptionId,
				"moduleId", Concepts.MODULE_SCT_CORE,
				"languageCode", "en",
				"acceptability", SnomedApiTestConstants.UK_ACCEPTABLE_MAP,
				"caseSignificanceId", Concepts.ENTIRE_TERM_CASE_SENSITIVE,
				"commitComment", "Created new donated synonym"
			)
		);
		String donatedDescriptionId = createDescription(SnomedContentRule.SNOMEDCT, donatedDescriptionRequest);
		
		LocalDate effectiveTime = getNextAvailableEffectiveDate(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveTime).statusCode(201);
		
		assertEquals(extensionDescriptionId, donatedDescriptionId);
		
		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extension.getResourceURI(), CodeSystem.uri(SNOMEDCT, effectiveTime.toString()));

		SnomedDescription donatedDescriptionOnUpgrade = getDescription(upgradeCodeSystem.getResourceURI(), donatedDescriptionId);
		
		assertEquals(Concepts.MODULE_SCT_CORE, donatedDescriptionOnUpgrade.getModuleId());
		assertEquals(Concepts.ENTIRE_TERM_CASE_SENSITIVE, donatedDescriptionOnUpgrade.getCaseSignificanceId());
		// acceptability should come from both INT
		assertThat(donatedDescriptionOnUpgrade.getAcceptabilityMap().containsKey(Concepts.REFSET_LANGUAGE_TYPE_UK));
		assertEquals(Acceptability.ACCEPTABLE, donatedDescriptionOnUpgrade.getAcceptabilityMap().get(Concepts.REFSET_LANGUAGE_TYPE_UK));
		// and EXT should keep its acceptability values
		assertThat(donatedDescriptionOnUpgrade.getAcceptabilityMap().containsKey(Concepts.REFSET_LANGUAGE_TYPE_US));
		assertEquals(Acceptability.ACCEPTABLE, donatedDescriptionOnUpgrade.getAcceptabilityMap().get(Concepts.REFSET_LANGUAGE_TYPE_US));
	}
	
	@Test
	public void upgrade07DonatedRelationship() throws Exception {
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		
		String extensionModuleId = createModule(extension);
		Json extensionRelationshipRequest = Json.object(
			"namespaceId", Concepts.B2I_NAMESPACE,
			"moduleId", extensionModuleId,
			"sourceId", Concepts.ROOT_CONCEPT,
			"typeId", Concepts.PART_OF,
			"destinationId", Concepts.NAMESPACE_ROOT,
			"characteristicTypeId", Concepts.ADDITIONAL_RELATIONSHIP,
			"relationshipGroup", 0,
			"commitComment", "Created new extension relationship"
		);
		
		String extensionRelationshipId = createRelationship(extension.getResourceURI(), extensionRelationshipRequest);
		
		// create new extension version
		createVersion(extension.getId(), "v1", LocalDate.now()).statusCode(201);

		// simulate donation of relationship to INT with slightly different props but same ID
		Json donatedRelationshipRequest = Json.assign(
			extensionRelationshipRequest,
			Json.object(
				"id", extensionRelationshipId,
				"moduleId", Concepts.MODULE_SCT_CORE,
				"characteristicTypeId", Concepts.INFERRED_RELATIONSHIP,
				"relationshipGroup", 1,
				"commitComment", "Created new donated INT relationship"
			)
		);
		
		String donatedRelationshipId = createRelationship(SnomedContentRule.SNOMEDCT, donatedRelationshipRequest);
		
		LocalDate effectiveTime = getNextAvailableEffectiveDate(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveTime).statusCode(201);
		
		assertEquals(extensionRelationshipId, donatedRelationshipId);
		
		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extension.getResourceURI(), CodeSystem.uri(SNOMEDCT, effectiveTime.toString()));
		
		SnomedRelationship donatedRelationshipOnUpgrade = getRelationship(upgradeCodeSystem.getResourceURI(), donatedRelationshipId);
		
		assertEquals(Concepts.MODULE_SCT_CORE, donatedRelationshipOnUpgrade.getModuleId());
		assertEquals(Concepts.INFERRED_RELATIONSHIP, donatedRelationshipOnUpgrade.getCharacteristicTypeId());
		assertEquals(1, donatedRelationshipOnUpgrade.getRelationshipGroup().intValue());
	}

	@Test
	public void upgrade08WithBackAndForthDonatedConcept() {
		// create new version on INT with core module and initial char case insensitive case significance
		String descriptionTerm = "Description term";
		
		Json initialInternationalDescriptionRequest = Json.object(
			"conceptId", Concepts.ROOT_CONCEPT,
			"moduleId", Concepts.MODULE_SCT_CORE,
			"typeId", Concepts.SYNONYM,
			"term", descriptionTerm,
			"languageCode", DEFAULT_LANGUAGE_CODE,
			"acceptability", SnomedApiTestConstants.UK_ACCEPTABLE_MAP,
			"caseSignificanceId", Concepts.ONLY_INITIAL_CHARACTER_CASE_INSENSITIVE,
			"commitComment", "Created new synonym"
		);
		
		String internationalDescriptionId = createDescription(SnomedContentRule.SNOMEDCT, initialInternationalDescriptionRequest);
		
		LocalDate effectiveTime = getNextAvailableEffectiveDate(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveTime).statusCode(201);
		
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(CodeSystem.uri(SNOMEDCT, effectiveTime.toString()), branchPath.lastSegment());
		String extensionModuleId = createModule(extension);
		
		// update international description on extension, changing module and case significance
		
		Json extensionDescriptionUpdateRequest = Json.object(
			"moduleId", extensionModuleId,
			"caseSignificanceId", Concepts.ENTIRE_TERM_CASE_SENSITIVE,
			"commitComment", "Changed case significance on description"
		);
		
		updateDescription(extension.getResourceURI(), internationalDescriptionId, extensionDescriptionUpdateRequest);
		
		SnomedDescription updatedInternationalDescription = getDescription(extension.getResourceURI(), internationalDescriptionId);
		assertEquals(true, updatedInternationalDescription.isReleased());
		assertEquals(null, updatedInternationalDescription.getEffectiveTime());
		assertEquals(extensionModuleId, updatedInternationalDescription.getModuleId());
		assertEquals(Concepts.ENTIRE_TERM_CASE_SENSITIVE, updatedInternationalDescription.getCaseSignificanceId());

		// create new extension version
		createVersion(extension.getId(), "v1", LocalDate.now()).statusCode(201);
		
		// donate extension changes to international via RF2 update simulation 
		Json descriptionDonateRequest = Json.object(
			"caseSignificanceId", Concepts.ENTIRE_TERM_CASE_SENSITIVE,
			"commitComment", "Changed case significance on description"
		);
		updateDescription(SnomedContentRule.SNOMEDCT, internationalDescriptionId, descriptionDonateRequest);
		LocalDate donationEffectiveDate = getNextAvailableEffectiveDate(SNOMEDCT);
		createVersion(SNOMEDCT, donationEffectiveDate).statusCode(201);
		
		// upgrade extension to new INT version with donations

		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extension.getResourceURI(), CodeSystem.uri(SNOMEDCT, donationEffectiveDate.toString()));

		SnomedDescription updatedInternationalDescriptionOnUpgrade = getDescription(upgradeCodeSystem.getResourceURI(), internationalDescriptionId);
		assertEquals(true, updatedInternationalDescriptionOnUpgrade.isReleased());
		assertEquals(donationEffectiveDate, updatedInternationalDescriptionOnUpgrade.getEffectiveTime());
		assertEquals(Concepts.MODULE_SCT_CORE, updatedInternationalDescriptionOnUpgrade.getModuleId());
		assertEquals(Concepts.ENTIRE_TERM_CASE_SENSITIVE, updatedInternationalDescriptionOnUpgrade.getCaseSignificanceId());
	}
	
	@Test
	public void upgrade09DonatedConceptAndDescriptions() {
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		String extensionModuleId = createModule(extension);
		
		String extensionFsnTerm = "FSN of concept";
		String extensionPtTerm = "PT of concept";
		String extensionSynonymTerm = "Synonym of extension concept";
		
		Json fsnRequestBody = Json.object(
			"typeId", Concepts.FULLY_SPECIFIED_NAME,
			"term", extensionFsnTerm,
			"languageCode", DEFAULT_LANGUAGE_CODE,
			"acceptability", SnomedApiTestConstants.UK_PREFERRED_MAP
		);
		Json ptRequestBody = Json.object(
			"typeId", Concepts.SYNONYM,
			"term", extensionPtTerm,
			"languageCode", DEFAULT_LANGUAGE_CODE,
			"acceptability", SnomedApiTestConstants.UK_PREFERRED_MAP
		);
		Json synonymRequestBody = Json.object(
			"typeId", Concepts.SYNONYM,
			"term", extensionSynonymTerm,
			"languageCode", DEFAULT_LANGUAGE_CODE,
			"acceptability", SnomedApiTestConstants.UK_ACCEPTABLE_MAP
		); 
		Json statedIsa = Json.object(
			"typeId", Concepts.IS_A,
			"destinationId", Concepts.ROOT_CONCEPT,
			"characteristicTypeId", Concepts.STATED_RELATIONSHIP
		);
		Json inferredIsa = Json.object(
			"typeId", Concepts.IS_A,
			"destinationId", Concepts.ROOT_CONCEPT,
			"characteristicTypeId", Concepts.INFERRED_RELATIONSHIP
		);
		Json extensionConceptRequestBody = Json.object(
			"namespaceId", Concepts.B2I_NAMESPACE,
			"moduleId", extensionModuleId,
			"descriptions", Json.array(fsnRequestBody, ptRequestBody, synonymRequestBody),
			"relationships", Json.array(statedIsa, inferredIsa)
		);
		
		String extensionConceptId = createConcept(extension.getResourceURI(), extensionConceptRequestBody);
		
		// create new extension version
		createVersion(extension.getId(), "v1", LocalDate.now()).statusCode(201);
		
		SnomedConcept extensionConcept = getConcept(extension.getResourceURI(), extensionConceptId, "descriptions()", "relationships()");

		String extensionFsnId = getFirstMatchingDescription(extensionConcept, extensionFsnTerm).getId();
		String extensionPtId = getFirstMatchingDescription(extensionConcept, extensionPtTerm).getId();
		String extensionSynonymId = getFirstMatchingDescription(extensionConcept, extensionSynonymTerm).getId();
		String extensionStatedIsaId = getFirstRelationshipId(extensionConcept, Concepts.STATED_RELATIONSHIP);
		String extensionInferredIsaId = getFirstRelationshipId(extensionConcept, Concepts.INFERRED_RELATIONSHIP);

		// simulate donation via concept create and versioning
		// create INT concept with same ID and with same description and relationship IDs
		String intConceptId = createConcept(SnomedContentRule.SNOMEDCT, Json.object(
			"id", extensionConceptId,
			"moduleId", Concepts.MODULE_SCT_CORE,
			"descriptions", Json.array(
				fsnRequestBody.with("id", extensionFsnId), 
				ptRequestBody.with("id", extensionPtId)
			),
			"relationships", Json.array(
				statedIsa, 
				inferredIsa
			)
		));
		
		LocalDate effectiveTime = getNextAvailableEffectiveDate(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveTime).statusCode(201);
		
		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extension.getResourceURI(), CodeSystem.uri(SNOMEDCT, effectiveTime.toString()));

		SnomedConcept donatedConceptInExtension = getConcept(upgradeCodeSystem.getResourceURI(), intConceptId, "descriptions()", "relationships()");
		
		// validate components of donated concept on extension branch
		// same ID, different module
		assertNotEquals(donatedConceptInExtension.getModuleId(), extensionConcept.getModuleId());
		
		SnomedDescription donatedFsn = getFirstMatchingDescription(donatedConceptInExtension, extensionFsnTerm);
		assertEquals(extensionFsnId, donatedFsn.getId());
		assertEquals(Concepts.MODULE_SCT_CORE, donatedFsn.getModuleId());
		
		SnomedDescription donatedPt = getFirstMatchingDescription(donatedConceptInExtension, extensionPtTerm);
		assertEquals(extensionPtId, donatedPt.getId());
		assertEquals(Concepts.MODULE_SCT_CORE, donatedPt.getModuleId());
		
		Set<String> descriptionIds = donatedConceptInExtension.getDescriptions().getItems().stream().map(SnomedDescription::getId).collect(Collectors.toSet());
		assertThat(descriptionIds)
			.hasSize(3)
			.contains(extensionSynonymId);

		Set<String> relationshipIds = donatedConceptInExtension.getRelationships().getItems().stream().map(SnomedRelationship::getId).collect(Collectors.toSet());
		assertThat(relationshipIds)
			.hasSize(4)
			.contains(extensionStatedIsaId, extensionInferredIsaId);
	}
	
	@Test
	public void upgrade10DonatedConceptAndDescriptionsAndRelationships() {
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		String extensionModuleId = createModule(extension);
		
		String extensionFsnTerm = "FSN of concept";
		String extensionPtTerm = "PT of concept";
		String extensionSynonymTerm = "Synonym of extension concept";
		
		Json fsnRequestBody = Json.object(
			"typeId", Concepts.FULLY_SPECIFIED_NAME,
			"term", extensionFsnTerm,
			"languageCode", DEFAULT_LANGUAGE_CODE,
			"acceptability", SnomedApiTestConstants.UK_PREFERRED_MAP
		);
		Json ptRequestBody = Json.object(
			"typeId", Concepts.SYNONYM,
			"term", extensionPtTerm,
			"languageCode", DEFAULT_LANGUAGE_CODE,
			"acceptability", SnomedApiTestConstants.UK_PREFERRED_MAP
		);
		Json synonymRequestBody = Json.object(
			"typeId", Concepts.SYNONYM,
			"term", extensionSynonymTerm,
			"languageCode", DEFAULT_LANGUAGE_CODE,
			"acceptability", SnomedApiTestConstants.UK_ACCEPTABLE_MAP
		); 
		Json statedIsa = Json.object(
			"typeId", Concepts.IS_A,
			"destinationId", Concepts.ROOT_CONCEPT,
			"characteristicTypeId", Concepts.STATED_RELATIONSHIP
		);
		Json inferredIsa = Json.object(
			"typeId", Concepts.IS_A,
			"destinationId", Concepts.ROOT_CONCEPT,
			"characteristicTypeId", Concepts.INFERRED_RELATIONSHIP
		);
		Json extensionConceptRequestBody = Json.object(
			"namespaceId", Concepts.B2I_NAMESPACE,
			"moduleId", extensionModuleId,
			"descriptions", Json.array(fsnRequestBody, ptRequestBody, synonymRequestBody),
			"relationships", Json.array(statedIsa, inferredIsa)
		);
		
		String extensionConceptId = createConcept(extension.getResourceURI(), extensionConceptRequestBody);
		
		// create new extension version
		createVersion(extension.getId(), "v1", LocalDate.now()).statusCode(201);
		
		SnomedConcept extensionConcept = getConcept(extension.getResourceURI(), extensionConceptId, "descriptions()", "relationships()");

		String extensionFsnId = getFirstMatchingDescription(extensionConcept, extensionFsnTerm).getId();
		String extensionPtId = getFirstMatchingDescription(extensionConcept, extensionPtTerm).getId();
		String extensionSynonymId = getFirstMatchingDescription(extensionConcept, extensionSynonymTerm).getId();
		String extensionStatedIsaId = getFirstRelationshipId(extensionConcept, Concepts.STATED_RELATIONSHIP);
		String extensionInferredIsaId = getFirstRelationshipId(extensionConcept, Concepts.INFERRED_RELATIONSHIP);

		// simulate donation via concept create and versioning
		// create INT concept with same ID and with same description and relationship IDs
		String intConceptId = createConcept(SnomedContentRule.SNOMEDCT, Json.object(
			"id", extensionConceptId,
			"moduleId", Concepts.MODULE_SCT_CORE,
			"descriptions", Json.array(
				fsnRequestBody.with("id", extensionFsnId), 
				ptRequestBody.with("id", extensionPtId)
			),
			"relationships", Json.array(
				statedIsa.with("id", extensionStatedIsaId), 
				inferredIsa.with("id", extensionInferredIsaId)
			)
		));
		
		LocalDate effectiveTime = getNextAvailableEffectiveDate(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveTime).statusCode(201);
		
		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extension.getResourceURI(), CodeSystem.uri(SNOMEDCT, effectiveTime.toString()));

		SnomedConcept donatedConceptInExtension = getConcept(upgradeCodeSystem.getResourceURI(), intConceptId, "descriptions()", "relationships()");
		
		// validate components of donated concept on extension branch
		// same ID, different module
		assertEquals(donatedConceptInExtension.getId(), extensionConcept.getId());
		assertNotEquals(donatedConceptInExtension.getModuleId(), extensionConcept.getModuleId());
		
		SnomedDescription donatedFsn = getFirstMatchingDescription(donatedConceptInExtension, extensionFsnTerm);
		assertEquals(extensionFsnId, donatedFsn.getId());
		assertEquals(Concepts.MODULE_SCT_CORE, donatedFsn.getModuleId());
		
		SnomedDescription donatedPt = getFirstMatchingDescription(donatedConceptInExtension, extensionPtTerm);
		assertEquals(extensionPtId, donatedPt.getId());
		assertEquals(Concepts.MODULE_SCT_CORE, donatedPt.getModuleId());
		
		Set<String> descriptionIds = donatedConceptInExtension.getDescriptions().getItems().stream().map(SnomedDescription::getId).collect(Collectors.toSet());
		assertThat(descriptionIds)
			.hasSize(3)
			.contains(extensionSynonymId);

		Set<String> relationshipIds = donatedConceptInExtension.getRelationships().getItems().stream().map(SnomedRelationship::getId).collect(Collectors.toSet());
		assertThat(relationshipIds)
			.hasSize(2)
			.contains(extensionStatedIsaId, extensionInferredIsaId);
	}
	
	@Test
	public void upgrade11DonatedConceptAndDescriptionsAndRelationshipsWithCrossReference() {
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		String extensionModuleId = createModule(extension);
		
		String extensionFsnTerm1 = "FSN of concept";
		String extensionPtTerm1 = "PT of concept";
		String extensionSynonymTerm1 = "Synonym of extension concept";
		
		Json fsnRequestBody1 = Json.object(
			"typeId", Concepts.FULLY_SPECIFIED_NAME,
			"term", extensionFsnTerm1,
			"languageCode", DEFAULT_LANGUAGE_CODE,
			"acceptability", SnomedApiTestConstants.UK_PREFERRED_MAP
		);
		Json ptRequestBody1 = Json.object(
			"typeId", Concepts.SYNONYM,
			"term", extensionPtTerm1,
			"languageCode", DEFAULT_LANGUAGE_CODE,
			"acceptability", SnomedApiTestConstants.UK_PREFERRED_MAP
		);
		Json synonymRequestBody1 = Json.object(
			"typeId", Concepts.SYNONYM,
			"term", extensionSynonymTerm1,
			"languageCode", DEFAULT_LANGUAGE_CODE,
			"acceptability", SnomedApiTestConstants.UK_ACCEPTABLE_MAP
		); 
		Json statedIsa = Json.object(
			"typeId", Concepts.IS_A,
			"destinationId", Concepts.ROOT_CONCEPT,
			"characteristicTypeId", Concepts.STATED_RELATIONSHIP
		);
		Json inferredIsa = Json.object(
			"typeId", Concepts.IS_A,
			"destinationId", Concepts.ROOT_CONCEPT,
			"characteristicTypeId", Concepts.INFERRED_RELATIONSHIP
		);
		Json extensionConceptRequestBody = Json.object(
			"namespaceId", Concepts.B2I_NAMESPACE,
			"moduleId", extensionModuleId,
			"descriptions", Json.array(fsnRequestBody1, ptRequestBody1, synonymRequestBody1),
			"relationships", Json.array(statedIsa, inferredIsa)
		);
		
		String extensionConceptId1 = createConcept(extension.getResourceURI(), extensionConceptRequestBody);

		// create another extension concept which references the previous one
		String extensionFsnTerm2 = "FSN of concept 2";
		String extensionPtTerm2 = "PT of concept 2";
		String extensionSynonymTerm2 = "Synonym of extension concept 2";
		
		Json referenceToExtensionConcept1 = Json.object(
			"typeId", Concepts.PART_OF,
			"destinationId", extensionConceptId1,
			"characteristicTypeId", Concepts.ADDITIONAL_RELATIONSHIP
		);
		
		String extensionConceptId2 = createConcept(extension.getResourceURI(), extensionConceptRequestBody.with(Json.object(
			"descriptions", Json.array(
				fsnRequestBody1.with("term", extensionFsnTerm2), 
				ptRequestBody1.with("term", extensionPtTerm2), 
				synonymRequestBody1.with("term", extensionSynonymTerm2)
			),
			"relationships", Json.array(statedIsa, inferredIsa, referenceToExtensionConcept1)
		)));
		
		// create new extension version
		createVersion(extension.getId(), "v1", LocalDate.now()).statusCode(201);

		SnomedConcept extensionConcept1 = getConcept(extension.getResourceURI(), extensionConceptId1, "descriptions()", "relationships()");

		String extensionFsnId1 = getFirstMatchingDescription(extensionConcept1, extensionFsnTerm1).getId();
		String extensionPtId1 = getFirstMatchingDescription(extensionConcept1, extensionPtTerm1).getId();
		String extensionSynonymId1 = getFirstMatchingDescription(extensionConcept1, extensionSynonymTerm1).getId();
		String extensionStatedIsaId1 = getFirstRelationshipId(extensionConcept1, Concepts.STATED_RELATIONSHIP);
		String extensionInferredIsaId1 = getFirstRelationshipId(extensionConcept1, Concepts.INFERRED_RELATIONSHIP);
		
		SnomedConcept extensionConcept2 = getConcept(extension.getResourceURI(), extensionConceptId2, "descriptions()", "relationships()");
		
		String extensionFsnId2 = getFirstMatchingDescription(extensionConcept2, extensionFsnTerm2).getId();
		String extensionPtId2 = getFirstMatchingDescription(extensionConcept2, extensionPtTerm2).getId();
		String extensionSynonymId2 = getFirstMatchingDescription(extensionConcept2, extensionSynonymTerm2).getId();
		String extensionStatedIsaId2 = getFirstRelationshipId(extensionConcept2, Concepts.STATED_RELATIONSHIP);
		String extensionInferredIsaId2 = getFirstRelationshipId(extensionConcept2, Concepts.INFERRED_RELATIONSHIP);
		String referenceToExtensionConcept1RelationshipId = getFirstRelationshipId(extensionConcept2, Concepts.ADDITIONAL_RELATIONSHIP);

		// simulate donation via concept create and versioning
		// donate both concepts without the PART OF relationship reference between them
		String intConceptId1 = createConcept(SnomedContentRule.SNOMEDCT, Json.object(
			"id", extensionConceptId1,
			"moduleId", Concepts.MODULE_SCT_CORE,
			"descriptions", Json.array(
				fsnRequestBody1.with("id", extensionFsnId1), 
				ptRequestBody1.with("id", extensionPtId1)
			),
			"relationships", Json.array(
				statedIsa.with("id", extensionStatedIsaId1), 
				inferredIsa.with("id", extensionInferredIsaId1)
			)
		));
		String intConceptId2 = createConcept(SnomedContentRule.SNOMEDCT, Json.object(
			"id", extensionConceptId2,
			"moduleId", Concepts.MODULE_SCT_CORE,
			"descriptions", Json.array(
				fsnRequestBody1.with("id", extensionFsnId2), 
				ptRequestBody1.with("id", extensionPtId2)
			),
			"relationships", Json.array(
				statedIsa.with("id", extensionStatedIsaId2), 
				inferredIsa.with("id", extensionInferredIsaId2)
			)
		));
		
		LocalDate effectiveTime = getNextAvailableEffectiveDate(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveTime).statusCode(201);

		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extension.getResourceURI(), CodeSystem.uri(SNOMEDCT, effectiveTime.toString()));
		
		SnomedConcept donatedConceptInExtension1 = getConcept(upgradeCodeSystem.getResourceURI(), intConceptId1, "descriptions()", "relationships()");
		SnomedConcept donatedConceptInExtension2 = getConcept(upgradeCodeSystem.getResourceURI(), intConceptId2, "descriptions()", "relationships()");
		
		// validate components of donated concepts on extension branch
		// same IDs, different modules
		assertEquals(donatedConceptInExtension1.getId(), extensionConcept1.getId());
		assertNotEquals(donatedConceptInExtension1.getModuleId(), extensionConcept1.getModuleId());
		
		assertThat(donatedConceptInExtension1.getDescriptions())
			.extracting(SnomedDescription::getId)
			.containsOnly(extensionFsnId1, extensionPtId1, extensionSynonymId1);

		assertThat(donatedConceptInExtension1.getRelationships())
			.extracting(SnomedRelationship::getId)
			.containsOnly(extensionStatedIsaId1, extensionInferredIsaId1);
		
		// check if the other donated extension concept still has the reference relationship
		assertEquals(donatedConceptInExtension2.getId(), extensionConcept2.getId());
		assertNotEquals(donatedConceptInExtension2.getModuleId(), extensionConcept2.getModuleId());
		
		assertThat(donatedConceptInExtension2.getDescriptions())
			.extracting(SnomedDescription::getId)
			.containsOnly(extensionFsnId2, extensionPtId2, extensionSynonymId2);

		assertThat(donatedConceptInExtension2.getRelationships())
			.extracting(SnomedRelationship::getId)
			.containsOnly(extensionStatedIsaId2, extensionInferredIsaId2, referenceToExtensionConcept1RelationshipId);
	}

	@Test
	public void upgrade12WithDonatedConceptAndDescriptionsAndRelationshipsWithExternalReference() {
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		String extensionModuleId = createModule(extension);
		
		String extensionFsnTerm1 = "FSN of concept";
		String extensionPtTerm1 = "PT of concept";
		String extensionSynonymTerm1 = "Synonym of extension concept";
		
		Json fsnRequestBody1 = Json.object(
			"typeId", Concepts.FULLY_SPECIFIED_NAME,
			"term", extensionFsnTerm1,
			"languageCode", DEFAULT_LANGUAGE_CODE,
			"acceptability", SnomedApiTestConstants.UK_PREFERRED_MAP
		);
		Json ptRequestBody1 = Json.object(
			"typeId", Concepts.SYNONYM,
			"term", extensionPtTerm1,
			"languageCode", DEFAULT_LANGUAGE_CODE,
			"acceptability", SnomedApiTestConstants.UK_PREFERRED_MAP
		);
		Json synonymRequestBody1 = Json.object(
			"typeId", Concepts.SYNONYM,
			"term", extensionSynonymTerm1,
			"languageCode", DEFAULT_LANGUAGE_CODE,
			"acceptability", SnomedApiTestConstants.UK_ACCEPTABLE_MAP
		); 
		Json statedIsa = Json.object(
			"typeId", Concepts.IS_A,
			"destinationId", Concepts.ROOT_CONCEPT,
			"characteristicTypeId", Concepts.STATED_RELATIONSHIP
		);
		Json inferredIsa = Json.object(
			"typeId", Concepts.IS_A,
			"destinationId", Concepts.ROOT_CONCEPT,
			"characteristicTypeId", Concepts.INFERRED_RELATIONSHIP
		);
		Json extensionConceptRequestBody = Json.object(
			"namespaceId", Concepts.B2I_NAMESPACE,
			"moduleId", extensionModuleId,
			"descriptions", Json.array(fsnRequestBody1, ptRequestBody1, synonymRequestBody1),
			"relationships", Json.array(statedIsa, inferredIsa)
		);
		
		String extensionConceptId1 = createConcept(extension.getResourceURI(), extensionConceptRequestBody);

		// create another extension concept which references the previous one
		String extensionFsnTerm2 = "FSN of concept 2";
		String extensionPtTerm2 = "PT of concept 2";
		String extensionSynonymTerm2 = "Synonym of extension concept 2";
		
		Json referenceToExtensionConcept1 = Json.object(
			"typeId", Concepts.PART_OF,
			"destinationId", extensionConceptId1,
			"characteristicTypeId", Concepts.ADDITIONAL_RELATIONSHIP
		);
		
		String extensionConceptId2 = createConcept(extension.getResourceURI(), extensionConceptRequestBody.with(Json.object(
			"descriptions", Json.array(
				fsnRequestBody1.with("term", extensionFsnTerm2), 
				ptRequestBody1.with("term", extensionPtTerm2), 
				synonymRequestBody1.with("term", extensionSynonymTerm2)
			),
			"relationships", Json.array(statedIsa, inferredIsa, referenceToExtensionConcept1)
		)));
		
		// create new extension version
		createVersion(extension.getId(), "v1", LocalDate.now()).statusCode(201);

		SnomedConcept extensionConcept1 = getConcept(extension.getResourceURI(), extensionConceptId1, "descriptions()", "relationships()");

		String extensionFsnId1 = getFirstMatchingDescription(extensionConcept1, extensionFsnTerm1).getId();
		String extensionPtId1 = getFirstMatchingDescription(extensionConcept1, extensionPtTerm1).getId();
		String extensionSynonymId1 = getFirstMatchingDescription(extensionConcept1, extensionSynonymTerm1).getId();
		String extensionStatedIsaId1 = getFirstRelationshipId(extensionConcept1, Concepts.STATED_RELATIONSHIP);
		String extensionInferredIsaId1 = getFirstRelationshipId(extensionConcept1, Concepts.INFERRED_RELATIONSHIP);
		
		SnomedConcept extensionConcept2 = getConcept(extension.getResourceURI(), extensionConceptId2, "descriptions()", "relationships()");
		
		String extensionFsnId2 = getFirstMatchingDescription(extensionConcept2, extensionFsnTerm2).getId();
		String extensionPtId2 = getFirstMatchingDescription(extensionConcept2, extensionPtTerm2).getId();
		String extensionSynonymId2 = getFirstMatchingDescription(extensionConcept2, extensionSynonymTerm2).getId();
		String extensionStatedIsaId2 = getFirstRelationshipId(extensionConcept2, Concepts.STATED_RELATIONSHIP);
		String extensionInferredIsaId2 = getFirstRelationshipId(extensionConcept2, Concepts.INFERRED_RELATIONSHIP);
		String referenceToExtensionConcept1RelationshipId = getFirstRelationshipId(extensionConcept2, Concepts.ADDITIONAL_RELATIONSHIP);

		// simulate donation via concept create and versioning
		// donate both concepts without the PART OF relationship reference between them
		String intConceptId = createConcept(SnomedContentRule.SNOMEDCT, Json.object(
			"id", extensionConceptId1,
			"moduleId", Concepts.MODULE_SCT_CORE,
			"descriptions", Json.array(
				fsnRequestBody1.with("id", extensionFsnId1), 
				ptRequestBody1.with("id", extensionPtId1)
			),
			"relationships", Json.array(
				statedIsa.with("id", extensionStatedIsaId1), 
				inferredIsa.with("id", extensionInferredIsaId1)
			)
		));
		
		LocalDate effectiveTime = getNextAvailableEffectiveDate(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveTime).statusCode(201);

		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extension.getResourceURI(), CodeSystem.uri(SNOMEDCT, effectiveTime.toString()));
		
		SnomedConcept donatedConceptInExtension1 = getConcept(upgradeCodeSystem.getResourceURI(), intConceptId, "descriptions()", "relationships()");
		SnomedConcept extensionConcept2OnUpgrade = getConcept(upgradeCodeSystem.getResourceURI(), extensionConceptId2, "descriptions()", "relationships()");
		
		// validate components of donated concepts on extension branch
		// same IDs, different modules
		assertEquals(donatedConceptInExtension1.getId(), extensionConcept1.getId());
		assertNotEquals(donatedConceptInExtension1.getModuleId(), extensionConcept1.getModuleId());
		
		assertThat(donatedConceptInExtension1.getDescriptions())
			.extracting(SnomedDescription::getId)
			.containsOnly(extensionFsnId1, extensionPtId1, extensionSynonymId1);

		assertThat(donatedConceptInExtension1.getRelationships())
			.extracting(SnomedRelationship::getId)
			.containsOnly(extensionStatedIsaId1, extensionInferredIsaId1);
		
		// check if the external reference extension concept still has the reference relationship and all its content
		assertThat(extensionConcept2OnUpgrade.getDescriptions())
			.extracting(SnomedDescription::getId)
			.containsOnly(extensionFsnId2, extensionPtId2, extensionSynonymId2);

		assertThat(extensionConcept2OnUpgrade.getRelationships())
			.extracting(SnomedRelationship::getId)
			.containsOnly(extensionStatedIsaId2, extensionInferredIsaId2, referenceToExtensionConcept1RelationshipId);
	}
	
	@Test
	public void upgrade13SemanticallySameChangesAppliedTwiceBeforeUpgradeAndAfterUpgrade() throws Exception {
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		
		// create new extension version with one new concept and one member on INT concept
		String moduleId = createModule(extension);
		
		SnomedConcept concept = searchConcepts(baseInternationalCodeSystem, Map.of("module", Concepts.MODULE_SCT_CORE, "expand", "descriptions()"), 1).stream().findFirst().get();
		String descriptionId = concept.getDescriptions().stream().findFirst().get().getId();
		// create new inactivation indicator for one of the description, pending move
		createMember(extension.getResourceURI(), Map.of(
			"moduleId", moduleId,
			"refsetId", Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR,
			"referencedComponentId", descriptionId,
			"valueId", Concepts.PENDING_MOVE
		));
		
		// version extension
		createVersion(extension.getId(), "v1", LocalDate.now()).statusCode(201);		
		
		// add another member on INT representing a change in INT and allow extension to upgrade
		createMember(SnomedContentRule.SNOMEDCT, Map.of(
			"moduleId", Concepts.MODULE_SCT_CORE,
			"refsetId", Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR,
			"referencedComponentId", descriptionId,
			"valueId", Concepts.PENDING_MOVE
		));
		
		LocalDate effectiveTime = getNextAvailableEffectiveDate(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveTime).statusCode(201);
		ResourceURI upgradeVersion = CodeSystem.uri(SNOMEDCT, effectiveTime.toString());
		
		// start the upgrade
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extension.getResourceURI(), upgradeVersion);
		
		// add another extension member to the same int concept with same meaning to the original extension branch
		createMember(extension.getResourceURI(), Map.of(
			"moduleId", moduleId,
			"refsetId", Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR,
			"referencedComponentId", descriptionId,
			"valueId", Concepts.AMBIGUOUS
		));
		
		// synchronize upgrade
		final String mergeLocation = createMerge(extension.getBranchPath(), upgradeCodeSystem.getBranchPath(), "Sync Upgrade CodeSystem", false)
			.statusCode(202)
			.extract()
			.header("Location");

		waitForMergeJob(lastPathSegment(mergeLocation));
		
		// each component should have a single revision after successful sync
		getConcept(upgradeCodeSystem.getResourceURI(), concept.getId());
		
	}
	
	@Test
	public void upgrade14CompleteUpgrade() throws Exception {
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		
		// create a new INT version without any changes
		LocalDate effectiveTime = getNextAvailableEffectiveDate(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveTime).statusCode(201);
		ResourceURI upgradeVersion = CodeSystem.uri(SNOMEDCT, effectiveTime.toString());
		
		// create new extension version with one new concept, module in this case
		String moduleId = createModule(extension);
		createVersion(extension.getId(), "v1", LocalDate.now()).statusCode(201);
		
		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extension.getResourceURI(), upgradeVersion);
		assertEquals(upgradeVersion, upgradeCodeSystem.getExtensionOf());
		
		getComponent(upgradeCodeSystem.getResourceURI(), SnomedComponentType.CONCEPT, moduleId).statusCode(200);
		
		Boolean success = CodeSystemRequests.prepareComplete(upgradeCodeSystem.getId())
			.buildAsync()
			.execute(getBus())
			.getSync(1, TimeUnit.MINUTES);
		assertTrue(success);
		
		// after upgrade completion, the upgrade Code System is no longer available
		CodeSystemRestRequests.assertGetCodeSystem(upgradeCodeSystem.getId())
			.statusCode(404);
		
		// the extension should use the upgrade's branch
		CodeSystem afterUpgrade = CodeSystemRestRequests.getCodeSystem(upgradeCodeSystem.getUpgradeOf().getResourceId());
		assertThat(afterUpgrade.getBranchPath()).isEqualTo(upgradeCodeSystem.getBranchPath());
		assertThat(afterUpgrade.getExtensionOf()).isEqualByComparingTo(upgradeCodeSystem.getExtensionOf());
	}
	
	@Test
	public void upgrade15Version() {
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		
		LocalDate effectiveTime = getNextAvailableEffectiveDate(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveTime).statusCode(201);
		
		LocalDate newEffectiveDate = getNextAvailableEffectiveDate(SNOMEDCT);
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extension.getResourceURI(), CodeSystem.uri(SNOMEDCT, effectiveTime.toString()));
		createVersion(upgradeCodeSystem.getId(), newEffectiveDate).statusCode(400);
	}
	
	@Test
	public void upgrade16UpgradeFromCodeSystemVersion() {
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		
		// new SI concept
		String newConceptId = createConcept(SnomedContentRule.SNOMEDCT, createConceptRequestBody(Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE));
		
		// create a new INT version
		LocalDate effectiveTime = getNextAvailableEffectiveDate(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveTime).statusCode(201);
		ResourceURI upgradeVersion = CodeSystem.uri(SNOMEDCT, effectiveTime.toString());
		
		// version extension
		LocalDate effectiveDate2 = LocalDate.now();
		createVersion(extension.getId(), effectiveDate2).statusCode(201);
		ResourceURI extensionVersion = CodeSystem.uri(extension.getId(), effectiveDate2.toString());
		
		
		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extensionVersion, upgradeVersion);
		assertEquals(upgradeVersion, upgradeCodeSystem.getExtensionOf());
		
		getComponent(upgradeCodeSystem.getResourceURI(), SnomedComponentType.CONCEPT, newConceptId).statusCode(200);
	}
	
	@Test
	public void upgrade17UpgradeFromCodeSystemVersionWithPublishedChanges() {
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		
		// new SI concept
		String newConceptId = createConcept(SnomedContentRule.SNOMEDCT, createConceptRequestBody(Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE));
		
		// create a new INT version
		LocalDate effectiveDate = getNextAvailableEffectiveDate(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveDate).statusCode(201);
		ResourceURI upgradeVersion = CodeSystem.uri(SNOMEDCT, effectiveDate.toString());
		
		// new SE concept
		String extensionModuleId = createModule(extension);
		String newConceptId2 = createConcept(extension.getResourceURI(), createConceptRequestBody(Concepts.ROOT_CONCEPT, extensionModuleId));
		
		// version extension
		LocalDate effectiveDate2 = getNextAvailableEffectiveDate(extension.getId());
		createVersion(extension.getId(), effectiveDate2).statusCode(201);
		ResourceURI extensionVersion = CodeSystem.uri(extension.getId(), effectiveDate2.toString());
		
		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extensionVersion, upgradeVersion);
		assertEquals(upgradeVersion, upgradeCodeSystem.getExtensionOf());
		
		getComponent(upgradeCodeSystem.getResourceURI(), SnomedComponentType.CONCEPT, newConceptId).statusCode(200);
		getComponent(upgradeCodeSystem.getResourceURI(), SnomedComponentType.CONCEPT, newConceptId2).statusCode(200);
	}
	
	@Test
	public void upgrade18UpgradeFromCodeSystemVersionWithUnpublishedChanges() {
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		
		// new SI concept
		String newConceptId = createConcept(SnomedContentRule.SNOMEDCT, createConceptRequestBody(Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE));
		
		// create a new INT version
		LocalDate effectiveDate = getNextAvailableEffectiveDate(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveDate).statusCode(201);
		ResourceURI upgradeVersion = CodeSystem.uri(SNOMEDCT, effectiveDate.toString());
		
		
		// version extension
		LocalDate effectiveDate2 = LocalDate.now();
		createVersion(extension.getId(), effectiveDate2).statusCode(201);
		ResourceURI extensionVersion = CodeSystem.uri(extension.getId(), effectiveDate2.toString());
		
		// new SE concept
		String extensionModuleId = createModule(extension);
		String newConceptId2 = createConcept(extension.getResourceURI(), createConceptRequestBody(Concepts.ROOT_CONCEPT, extensionModuleId));
		
		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extensionVersion, upgradeVersion);
		assertEquals(upgradeVersion, upgradeCodeSystem.getExtensionOf());
		
		getComponent(upgradeCodeSystem.getResourceURI(), SnomedComponentType.CONCEPT, newConceptId).statusCode(200);
		getComponent(upgradeCodeSystem.getResourceURI(), SnomedComponentType.CONCEPT, newConceptId2).statusCode(404);
	}
	
	@Test
	public void upgrade19UpgradeFromCodeSystemVersionWithUnpublishedAndPublishedChanges() {
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		
		// new SI concept
		String newConceptId = createConcept(SnomedContentRule.SNOMEDCT, createConceptRequestBody(Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE));
		
		// create a new INT version
		LocalDate effectiveDate = getNextAvailableEffectiveDate(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveDate).statusCode(201);
		ResourceURI upgradeVersion = CodeSystem.uri(SNOMEDCT, effectiveDate.toString());
		
		
		// new SE concept
		String extensionModuleId = createModule(extension);
		String newConceptId2 = createConcept(extension.getResourceURI(), createConceptRequestBody(Concepts.ROOT_CONCEPT, extensionModuleId));
		
		// version extension
		LocalDate effectiveDate2 = LocalDate.now();
		createVersion(extension.getId(), effectiveDate2).statusCode(201);
		ResourceURI extensionVersion = CodeSystem.uri(extension.getId(), effectiveDate2.toString());
		
		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extensionVersion, upgradeVersion);
		assertEquals(upgradeVersion, upgradeCodeSystem.getExtensionOf());
		
		// new SE concept
		String newConceptId3 = createConcept(extension.getResourceURI(), createConceptRequestBody(Concepts.ROOT_CONCEPT, extensionModuleId));
		
		getComponent(upgradeCodeSystem.getResourceURI(), SnomedComponentType.CONCEPT, newConceptId).statusCode(200);
		getComponent(upgradeCodeSystem.getResourceURI(), SnomedComponentType.CONCEPT, newConceptId2).statusCode(200);
		getComponent(upgradeCodeSystem.getResourceURI(), SnomedComponentType.CONCEPT, newConceptId3).statusCode(404);
	}
	
	@Test
	public void upgrade20ExpandUpgradeInfo() throws Exception {
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		
		// new SI concept
		createConcept(SnomedContentRule.SNOMEDCT, createConceptRequestBody(Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE));
		
		// create a new INT version
		LocalDate effectiveDate = getNextAvailableEffectiveDate(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveDate).statusCode(201);
		ResourceURI upgradeVersion = CodeSystem.uri(SNOMEDCT, effectiveDate.toString());
		
		// new SE concept
		String extensionModuleId = createModule(extension);
		createConcept(extension.getResourceURI(), createConceptRequestBody(Concepts.ROOT_CONCEPT, extensionModuleId));
		
		// version extension
		LocalDate effectiveDate2 = LocalDate.now();
		createVersion(extension.getId(), effectiveDate2).statusCode(201);
		ResourceURI extensionVersion = CodeSystem.uri(extension.getId(), effectiveDate2.toString());
		
		// new SE concept
		createConcept(extension.getResourceURI(), createConceptRequestBody(Concepts.ROOT_CONCEPT, extensionModuleId));
		
		// version extension
		LocalDate effectiveDate3 =  getNextAvailableEffectiveDate(extension.getId());
		createVersion(extension.getId(), effectiveDate3).statusCode(201);
		ResourceURI extensionVersion2 = CodeSystem.uri(extension.getId(), effectiveDate3.toString());
		
		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extensionVersion, upgradeVersion);
		
		CodeSystems expandedCodeSystems = CodeSystemRestRequests.search(upgradeCodeSystem.getId(), CodeSystem.Expand.UPGRADE_INFO + "()");
		
		assertEquals(upgradeVersion, upgradeCodeSystem.getExtensionOf());
		assertThat(expandedCodeSystems.first().get().getUpgradeInfo().getAvailableVersions()).doesNotContainSequence(extensionVersion);
		assertThat(expandedCodeSystems.first().get().getUpgradeInfo().getAvailableVersions()).contains(extensionVersion2);
	}
	
	@Test
	public void upgrade21ExpandUpgradeInfo() throws Exception {
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		
		// new SI concept
		createConcept(SnomedContentRule.SNOMEDCT, createConceptRequestBody(Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE));
		
		// create a new INT version
		LocalDate effectiveDate = getNextAvailableEffectiveDate(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveDate).statusCode(201);
		ResourceURI upgradeVersion = CodeSystem.uri(SNOMEDCT, effectiveDate.toString());
		
		
		// new SE concept
		String extensionModuleId = createModule(extension);
		createConcept(extension.getResourceURI(), createConceptRequestBody(Concepts.ROOT_CONCEPT, extensionModuleId));
		
		// version extension
		LocalDate effectiveDate2 = LocalDate.now();
		createVersion(extension.getId(), effectiveDate2).statusCode(201);
		ResourceURI extensionVersion = CodeSystem.uri(extension.getId(), effectiveDate2.toString());
		
		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extensionVersion, upgradeVersion);
		
		// new SE concept
		createConcept(extension.getResourceURI(), createConceptRequestBody(Concepts.ROOT_CONCEPT, extensionModuleId));
		
		// version extension
		LocalDate effectiveDate3 =  getNextAvailableEffectiveDate(extension.getId());
		createVersion(extension.getId(), effectiveDate3).statusCode(201);
		ResourceURI extensionVersion2 = CodeSystem.uri(extension.getId(), effectiveDate3.toString());
		
		CodeSystems expandedCodeSystems = CodeSystemRestRequests.search(upgradeCodeSystem.getId(), CodeSystem.Expand.UPGRADE_INFO + "()");
		
		assertEquals(upgradeVersion, upgradeCodeSystem.getExtensionOf());
		assertThat(expandedCodeSystems.first().get().getUpgradeInfo().getAvailableVersions()).doesNotContainSequence(extensionVersion);
		assertThat(expandedCodeSystems.first().get().getUpgradeInfo().getAvailableVersions()).contains(extensionVersion2);
	}
	
	@Test
	public void upgrade22UpgradeSychronizeHead() throws Exception {
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		
		// new SI concept
		createConcept(SnomedContentRule.SNOMEDCT, createConceptRequestBody(Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE));
		
		// create a new INT version
		LocalDate effectiveDate = getNextAvailableEffectiveDate(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveDate).statusCode(201);
		ResourceURI upgradeVersion = CodeSystem.uri(SNOMEDCT, effectiveDate.toString());
		
		
		// new SE concept
		String extensionModuleId = createModule(extension);
		createConcept(extension.getResourceURI(), createConceptRequestBody(Concepts.ROOT_CONCEPT, extensionModuleId));
		
		// version extension
		LocalDate effectiveDate2 = LocalDate.now();
		createVersion(extension.getId(), effectiveDate2).statusCode(201);
		ResourceURI extensionVersion = CodeSystem.uri(extension.getId(), effectiveDate2.toString());
		
		// new SE concept
		createConcept(extension.getResourceURI(), createConceptRequestBody(Concepts.ROOT_CONCEPT, extensionModuleId));
		
		// version extension
		LocalDate effectiveDate3 =  getNextAvailableEffectiveDate(extension.getId());
		createVersion(extension.getId(), effectiveDate3).statusCode(201);
		ResourceURI extensionVersion2 = CodeSystem.uri(extension.getId(), effectiveDate3.toString());
		
		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extensionVersion, upgradeVersion);
		
		CodeSystems expandedCodeSystems = CodeSystemRestRequests.search(upgradeCodeSystem.getId(), CodeSystem.Expand.UPGRADE_INFO + "()");
		
		assertEquals(upgradeVersion, upgradeCodeSystem.getExtensionOf());
		assertThat(expandedCodeSystems.first().get().getUpgradeInfo().getAvailableVersions()).doesNotContainSequence(extensionVersion);
		assertThat(expandedCodeSystems.first().get().getUpgradeInfo().getAvailableVersions()).contains(extensionVersion2);
		
		IBranchPath extensionBranch = BranchPathUtils.createPath(extension.getBranchPath());
		IBranchPath upgradeBranch = BranchPathUtils.createPath(upgradeCodeSystem.getBranchPath());
		merge(extensionBranch, upgradeBranch, "Merged new concept from child branch").body("status", equalTo(Merge.Status.COMPLETED.name()));	
		
		Boolean result = CodeSystemRequests.prepareUpgradeSynchronization(upgradeCodeSystem.getResourceURI(), extension.getResourceURI())
			.buildAsync()
			.execute(getBus())
			.getSync(1, TimeUnit.MINUTES);
		assertTrue(result);
		
		CodeSystems expandedCodeSystemsAfterMerge = CodeSystemRestRequests.search(upgradeCodeSystem.getId(), CodeSystem.Expand.UPGRADE_INFO + "()");
		assertThat(expandedCodeSystemsAfterMerge.first().get().getUpgradeInfo().getAvailableVersions()).isEmpty();
	}
	
	@Test
	public void upgrade23UpgradeSychronizeVersion() throws Exception {
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		
		// new SI concept
		createConcept(SnomedContentRule.SNOMEDCT, createConceptRequestBody(Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE));
		
		// create a new INT version
		LocalDate effectiveDate = getNextAvailableEffectiveDate(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveDate).statusCode(201);
		ResourceURI upgradeVersion = CodeSystem.uri(SNOMEDCT, effectiveDate.toString());
		
		
		// new SE concept
		String extensionModuleId = createModule(extension);
		createConcept(extension.getResourceURI(), createConceptRequestBody(Concepts.ROOT_CONCEPT, extensionModuleId));
		
		// version extension
		LocalDate effectiveDate2 = LocalDate.now();
		createVersion(extension.getId(), effectiveDate2).statusCode(201);
		ResourceURI extensionVersion = CodeSystem.uri(extension.getId(), effectiveDate2.toString());
		
		// new SE concept
		createConcept(extension.getResourceURI(), createConceptRequestBody(Concepts.ROOT_CONCEPT, extensionModuleId));
		
		// version extension
		LocalDate effectiveDate3 =  getNextAvailableEffectiveDate(extension.getId());
		createVersion(extension.getId(), effectiveDate3).statusCode(201);
		Version codeSystemVersion2 = CodeSystemVersionRestRequests.getVersion(extension.getId(), effectiveDate3.toString());
		ResourceURI extensionVersion2 = CodeSystem.uri(extension.getId(), effectiveDate3.toString());
		
		// new SE concept
		createConcept(extension.getResourceURI(), createConceptRequestBody(Concepts.ROOT_CONCEPT, extensionModuleId));
		
		// version extension
		LocalDate effectiveDate4 =  getNextAvailableEffectiveDate(extension.getId());
		createVersion(extension.getId(), effectiveDate4).statusCode(201);
		ResourceURI extensionVersion3 = CodeSystem.uri(extension.getId(), effectiveDate4.toString());
		
		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extensionVersion, upgradeVersion);
		
		CodeSystems expandedCodeSystems = CodeSystemRestRequests.search(upgradeCodeSystem.getId(), CodeSystem.Expand.UPGRADE_INFO + "()");
		
		assertEquals(upgradeVersion, upgradeCodeSystem.getExtensionOf());
		assertThat(expandedCodeSystems.first().get().getUpgradeInfo().getAvailableVersions()).doesNotContainSequence(extensionVersion);
		assertThat(expandedCodeSystems.first().get().getUpgradeInfo().getAvailableVersions()).contains(extensionVersion2);
		
		IBranchPath extensionBranch = BranchPathUtils.createPath(extension.getBranchPath());
		IBranchPath upgradeBranch = BranchPathUtils.createPath(upgradeCodeSystem.getBranchPath());
		merge(extensionBranch, upgradeBranch, "Merged new concept from child branch").body("status", equalTo(Merge.Status.COMPLETED.name()));	
		
		Boolean result = CodeSystemRequests.prepareUpgradeSynchronization(upgradeCodeSystem.getResourceURI(), extensionVersion2)
				.buildAsync()
				.execute(getBus())
				.getSync(1, TimeUnit.MINUTES);
		assertTrue(result);
		
		CodeSystems expandedCodeSystemsAfterMerge = CodeSystemRestRequests.search(upgradeCodeSystem.getId(), CodeSystem.Expand.UPGRADE_INFO + "()");
		assertThat(expandedCodeSystemsAfterMerge.first().get().getUpgradeInfo().getAvailableVersions()).containsOnly(extensionVersion3);
	}
	
	@Test
	public void upgrade24UpgradeWithEmptyExtension() throws Exception {
		
		// new SI concept
		ResourceURI base = CodeSystem.uri(SNOMEDCT);
		createConcept(base, createConceptRequestBody(Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE));
		
		// create a new INT version
		LocalDate effectiveDate1 = getNextAvailableEffectiveDate(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveDate1).statusCode(201);
		ResourceURI upgradeVersion1 = base.withPath(effectiveDate1.toString());
		
		// new SI concept
		String conceptId = createConcept(base, createConceptRequestBody(Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE));
		
		// create a new INT version
		LocalDate effectiveDate2 = getNextAvailableEffectiveDate(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveDate2).statusCode(201);
		ResourceURI upgradeVersion2 = base.withPath(effectiveDate2.toString());
		
		// new SI concept
		createConcept(base, createConceptRequestBody(Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE));
		
		// create a new INT version
		LocalDate effectiveDate3 = getNextAvailableEffectiveDate(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveDate3).statusCode(201);
		ResourceURI upgradeVersion3 = base.withPath(effectiveDate3.toString());
		
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(upgradeVersion1, branchPath.lastSegment());
		
		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extension.getResourceURI(), upgradeVersion2);
		
		assertState(upgradeCodeSystem.getBranchPath(), extension.getBranchPath(), BranchState.FORWARD);
		
		Boolean result = CodeSystemRequests.prepareUpgradeSynchronization(upgradeCodeSystem.getResourceURI(), extension.getResourceURI())
			.buildAsync()
			.execute(getBus())
			.getSync(1, TimeUnit.MINUTES);
		
		assertTrue(result);
		
		assertState(upgradeCodeSystem.getBranchPath(), extension.getBranchPath(), BranchState.FORWARD);
		
		Boolean successComplete = CodeSystemRequests.prepareComplete(upgradeCodeSystem.getId())
			.buildAsync()
			.execute(getBus())
			.getSync(1, TimeUnit.MINUTES);
		
		assertTrue(successComplete);
		
		getComponent(extension.getResourceURI(), SnomedComponentType.CONCEPT, conceptId).statusCode(200);
	}
	
	private void assertState(String branchPath, String compareWith, BranchState expectedState) {
		BaseRevisionBranching branching = ApplicationContext.getServiceForClass(RepositoryManager.class)
			.get(SnomedTerminologyComponentConstants.TOOLING_ID)
			.service(BaseRevisionBranching.class);
		
		assertEquals(expectedState, branching.getBranchState(branchPath, compareWith));
	}
	
	private String getFirstRelationshipId(SnomedConcept concept, String characteristicTypeId) {
		return concept.getRelationships().getItems().stream().filter(r -> characteristicTypeId.equals(r.getCharacteristicTypeId())).findFirst().get().getId();
	}
	
	private SnomedDescription getFirstMatchingDescription(SnomedConcept extensionConcept, String extensionFsnTerm) {
		return extensionConcept.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionFsnTerm)).findFirst().get();
	}
	
}
