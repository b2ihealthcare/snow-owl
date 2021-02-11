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
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.DEFAULT_LANGUAGE_CODE;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.createConceptRequestBody;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.createDescriptionRequestBody;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.createRelationshipRequestBody;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.createVersion;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.getLatestVersion;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.getNextAvailableEffectiveDateAsString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.b2international.commons.json.Json;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.uri.CodeSystemURI;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.core.rest.SnomedComponentType;
import com.b2international.snowowl.test.commons.codesystem.CodeSystemRestRequests;

/**
 * @since 4.7
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SnomedExtensionUpgradeTest extends AbstractSnomedExtensionApiTest {

	private CodeSystemURI latestInternationalVersion;

	@Before
	public void setup() {
		latestInternationalVersion = CodeSystemURI.branch(SNOMEDCT, getLatestVersion(SNOMEDCT));
	}
	
	@Test
	public void upgrade01Nochanges() {
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		
		// create a new INT version without any changes
		String effectiveDate = getNextAvailableEffectiveDateAsString(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveDate, effectiveDate).statusCode(201);
		
		// start upgrade
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extension.getCodeSystemURI(), CodeSystemURI.branch(SNOMEDCT, effectiveDate));
		assertEquals(CodeSystemURI.branch(SNOMEDCT, effectiveDate), upgradeCodeSystem.getExtensionOf());
	}

	@Test
	public void upgrade02NewExtensionConceptOnly() {
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		
		// create a new INT version without any changes
		String effectiveDate = getNextAvailableEffectiveDateAsString(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveDate, effectiveDate).statusCode(201);
		CodeSystemURI upgradeVersion = CodeSystemURI.branch(SNOMEDCT, effectiveDate);
		
		// create new extension version with one new concept, module in this case
		String moduleId = createModule(extension);
		createVersion(extension.getCodeSystemURI().getCodeSystem(), "v1", Dates.now(DateFormats.SHORT)).statusCode(201);
		
		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extension.getCodeSystemURI(), upgradeVersion);
		assertEquals(upgradeVersion, upgradeCodeSystem.getExtensionOf());
		
		getComponent(upgradeCodeSystem.getCodeSystemURI().toString(), SnomedComponentType.CONCEPT, moduleId).statusCode(200);
	}

	@Test
	public void upgrade03NewInternationalConceptOnly() {
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		
		// new SI concept
		String newConceptId = createConcept(new CodeSystemURI(SNOMEDCT), createConceptRequestBody(Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE));
		
		// create a new INT version
		String effectiveDate = getNextAvailableEffectiveDateAsString(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveDate, effectiveDate).statusCode(201);
		CodeSystemURI upgradeVersion = CodeSystemURI.branch(SNOMEDCT, effectiveDate);
		
		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extension.getCodeSystemURI(), upgradeVersion);
		assertEquals(upgradeVersion, upgradeCodeSystem.getExtensionOf());
		
		getComponent(upgradeCodeSystem.getCodeSystemURI().toString(), SnomedComponentType.CONCEPT, newConceptId).statusCode(200);
	}

	@Test
	public void upgrade04InternationalAddedDirectlyToExtensionResultsInConflict() {
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		
		// new SI concept
		String newConceptId = createConcept(new CodeSystemURI(SNOMEDCT), createConceptRequestBody(Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE));
		
		// create a new INT version
		String effectiveDate = getNextAvailableEffectiveDateAsString(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveDate, effectiveDate).statusCode(201);
		CodeSystemURI upgradeVersion = CodeSystemURI.branch(SNOMEDCT, effectiveDate);
		
		// replay SI concept on extension branch, simulating direct import of a preview or release SI RF2
		createConcept(extension.getCodeSystemURI(), createConceptRequestBody(Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE).with("id", newConceptId));
		createVersion(extension.getCodeSystemURI().getCodeSystem(), "v1", Dates.now(DateFormats.SHORT)).statusCode(201);
		
		// start upgrade but it fails due to same content present on both sides
		assertCodeSystemUpgrade(extension.getCodeSystemURI(), upgradeVersion).statusCode(409);
	}
	
	@Test
	public void upgrade05DonatedConcept() {
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		
		String extensionModuleId = createModule(extension);
		String extensionConceptId = createConcept(extension.getCodeSystemURI(), createConceptRequestBody(Concepts.ROOT_CONCEPT, extensionModuleId)
				.with("namespaceId", Concepts.B2I_NAMESPACE));
		// put additional relationships and acceptable synonym on the concept
		String additionalSynonymId = createDescription(extension.getCodeSystemURI(), createDescriptionRequestBody(extensionConceptId)
				.with("moduleId", extensionModuleId)
				.with("namespaceId", Concepts.B2I_NAMESPACE)
				.with("term", "Additional Synonym"));
		String additionalRelationshipId = createRelationship(extension.getCodeSystemURI(), createRelationshipRequestBody(extensionConceptId, Concepts.FINDING_SITE, Concepts.ROOT_CONCEPT)
				.with("moduleId", extensionModuleId)
				.with(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID, Concepts.ADDITIONAL_RELATIONSHIP)
				.with("namespaceId", Concepts.B2I_NAMESPACE));
		
		SnomedConcept extensionConcept = getConcept(extension.getCodeSystemURI(), extensionConceptId, "descriptions()", "relationships()", "fsn()", "pt()");

		// create new extension version
		createVersion(extension.getShortName(), "v1", Dates.now(DateFormats.SHORT)).statusCode(201);
		
		// simulate donation to SI and new version import
		String donatedConceptId = createConcept(new CodeSystemURI(SNOMEDCT), createConceptRequestBody(Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE)
				.with("id", extensionConceptId));
		
		String effectiveDate = getNextAvailableEffectiveDateAsString(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveDate, effectiveDate).statusCode(201);
		
		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extension.getCodeSystemURI(), CodeSystemURI.branch(SNOMEDCT, effectiveDate));

		SnomedConcept donatedConceptAfterUpgrade = getConcept(upgradeCodeSystem.getCodeSystemURI(), donatedConceptId, "descriptions()", "relationships()", "fsn()", "pt()");
		
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

		String extensionDescriptionId = createDescription(extension.getCodeSystemURI(), extensionDescriptionRequest);
		
		// create new extension version
		createVersion(extension.getShortName(), "v1", Dates.now(DateFormats.SHORT)).statusCode(201);

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
		String donatedDescriptionId = createDescription(new CodeSystemURI(SNOMEDCT), donatedDescriptionRequest);
		
		String effectiveDate = getNextAvailableEffectiveDateAsString(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveDate, effectiveDate).statusCode(201);
		
		assertEquals(extensionDescriptionId, donatedDescriptionId);
		
		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extension.getCodeSystemURI(), CodeSystemURI.branch(SNOMEDCT, effectiveDate));

		SnomedDescription donatedDescriptionOnUpgrade = getDescription(upgradeCodeSystem.getCodeSystemURI(), donatedDescriptionId);
		
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
			"group", 0,
			"commitComment", "Created new extension relationship"
		);
		
		String extensionRelationshipId = createRelationship(extension.getCodeSystemURI(), extensionRelationshipRequest);
		
		// create new extension version
		createVersion(extension.getShortName(), "v1", Dates.now(DateFormats.SHORT)).statusCode(201);

		// simulate donation of relationship to INT with slightly different props but same ID
		Json donatedRelationshipRequest = Json.assign(
			extensionRelationshipRequest,
			Json.object(
				"id", extensionRelationshipId,
				"moduleId", Concepts.MODULE_SCT_CORE,
				"characteristicTypeId", Concepts.INFERRED_RELATIONSHIP,
				"group", 1,
				"commitComment", "Created new donated INT relationship"
			)
		);
		
		String donatedRelationshipId = createRelationship(new CodeSystemURI(SNOMEDCT), donatedRelationshipRequest);
		
		String effectiveDate = getNextAvailableEffectiveDateAsString(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveDate, effectiveDate).statusCode(201);
		
		assertEquals(extensionRelationshipId, donatedRelationshipId);
		
		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extension.getCodeSystemURI(), CodeSystemURI.branch(SNOMEDCT, effectiveDate));
		
		SnomedRelationship donatedRelationshipOnUpgrade = getRelationship(upgradeCodeSystem.getCodeSystemURI(), donatedRelationshipId);
		
		assertEquals(Concepts.MODULE_SCT_CORE, donatedRelationshipOnUpgrade.getModuleId());
		assertEquals(Concepts.INFERRED_RELATIONSHIP, donatedRelationshipOnUpgrade.getCharacteristicTypeId());
		assertEquals(1, donatedRelationshipOnUpgrade.getGroup().intValue());
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
		
		String internationalDescriptionId = createDescription(new CodeSystemURI(SNOMEDCT), initialInternationalDescriptionRequest);
		
		String effectiveDate = getNextAvailableEffectiveDateAsString(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveDate, effectiveDate).statusCode(201);
		
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(CodeSystemURI.branch(SNOMEDCT, effectiveDate), branchPath.lastSegment());
		String extensionModuleId = createModule(extension);
		
		// update international description on extension, changing module and case significance
		
		Json extensionDescriptionUpdateRequest = Json.object(
			"moduleId", extensionModuleId,
			"caseSignificanceId", Concepts.ENTIRE_TERM_CASE_SENSITIVE,
			"commitComment", "Changed case significance on description"
		);
		
		updateDescription(extension.getCodeSystemURI(), internationalDescriptionId, extensionDescriptionUpdateRequest);
		
		SnomedDescription updatedInternationalDescription = getDescription(extension.getCodeSystemURI(), internationalDescriptionId);
		assertEquals(true, updatedInternationalDescription.isReleased());
		assertEquals(null, updatedInternationalDescription.getEffectiveTime());
		assertEquals(extensionModuleId, updatedInternationalDescription.getModuleId());
		assertEquals(Concepts.ENTIRE_TERM_CASE_SENSITIVE, updatedInternationalDescription.getCaseSignificanceId());

		// create new extension version
		createVersion(extension.getShortName(), "v1", Dates.now(DateFormats.SHORT)).statusCode(201);
		
		// donate extension changes to international via RF2 update simulation 
		Json descriptionDonateRequest = Json.object(
			"caseSignificanceId", Concepts.ENTIRE_TERM_CASE_SENSITIVE,
			"commitComment", "Changed case significance on description"
		);
		updateDescription(new CodeSystemURI(SNOMEDCT), internationalDescriptionId, descriptionDonateRequest);
		String donationEffectiveDate = getNextAvailableEffectiveDateAsString(SNOMEDCT);
		createVersion(SNOMEDCT, donationEffectiveDate, donationEffectiveDate).statusCode(201);
		
		// upgrade extension to new INT version with donations

		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extension.getCodeSystemURI(), CodeSystemURI.branch(SNOMEDCT, donationEffectiveDate));

		SnomedDescription updatedInternationalDescriptionOnUpgrade = getDescription(upgradeCodeSystem.getCodeSystemURI(), internationalDescriptionId);
		assertEquals(true, updatedInternationalDescriptionOnUpgrade.isReleased());
		assertEquals(EffectiveTimes.parse(donationEffectiveDate, DateFormats.SHORT), updatedInternationalDescriptionOnUpgrade.getEffectiveTime());
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
		
		String extensionConceptId = createConcept(extension.getCodeSystemURI(), extensionConceptRequestBody);
		
		// create new extension version
		createVersion(extension.getShortName(), "v1", Dates.now(DateFormats.SHORT)).statusCode(201);
		
		SnomedConcept extensionConcept = getConcept(extension.getCodeSystemURI(), extensionConceptId, "descriptions()", "relationships()");

		String extensionFsnId = getFirstMatchingDescription(extensionConcept, extensionFsnTerm).getId();
		String extensionPtId = getFirstMatchingDescription(extensionConcept, extensionPtTerm).getId();
		String extensionSynonymId = getFirstMatchingDescription(extensionConcept, extensionSynonymTerm).getId();
		String extensionStatedIsaId = getFirstRelationshipId(extensionConcept, Concepts.STATED_RELATIONSHIP);
		String extensionInferredIsaId = getFirstRelationshipId(extensionConcept, Concepts.INFERRED_RELATIONSHIP);

		// simulate donation via concept create and versioning
		// create INT concept with same ID and with same description and relationship IDs
		String intConceptId = createConcept(new CodeSystemURI(SNOMEDCT), Json.object(
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
		
		String effectiveDate = getNextAvailableEffectiveDateAsString(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveDate, effectiveDate).statusCode(201);
		
		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extension.getCodeSystemURI(), CodeSystemURI.branch(SNOMEDCT, effectiveDate));

		SnomedConcept donatedConceptInExtension = getConcept(upgradeCodeSystem.getCodeSystemURI(), intConceptId, "descriptions()", "relationships()");
		
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
		
		String extensionConceptId = createConcept(extension.getCodeSystemURI(), extensionConceptRequestBody);
		
		// create new extension version
		createVersion(extension.getShortName(), "v1", Dates.now(DateFormats.SHORT)).statusCode(201);
		
		SnomedConcept extensionConcept = getConcept(extension.getCodeSystemURI(), extensionConceptId, "descriptions()", "relationships()");

		String extensionFsnId = getFirstMatchingDescription(extensionConcept, extensionFsnTerm).getId();
		String extensionPtId = getFirstMatchingDescription(extensionConcept, extensionPtTerm).getId();
		String extensionSynonymId = getFirstMatchingDescription(extensionConcept, extensionSynonymTerm).getId();
		String extensionStatedIsaId = getFirstRelationshipId(extensionConcept, Concepts.STATED_RELATIONSHIP);
		String extensionInferredIsaId = getFirstRelationshipId(extensionConcept, Concepts.INFERRED_RELATIONSHIP);

		// simulate donation via concept create and versioning
		// create INT concept with same ID and with same description and relationship IDs
		String intConceptId = createConcept(new CodeSystemURI(SNOMEDCT), Json.object(
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
		
		String effectiveDate = getNextAvailableEffectiveDateAsString(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveDate, effectiveDate).statusCode(201);
		
		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extension.getCodeSystemURI(), CodeSystemURI.branch(SNOMEDCT, effectiveDate));

		SnomedConcept donatedConceptInExtension = getConcept(upgradeCodeSystem.getCodeSystemURI(), intConceptId, "descriptions()", "relationships()");
		
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
		
		String extensionConceptId1 = createConcept(extension.getCodeSystemURI(), extensionConceptRequestBody);

		// create another extension concept which references the previous one
		String extensionFsnTerm2 = "FSN of concept 2";
		String extensionPtTerm2 = "PT of concept 2";
		String extensionSynonymTerm2 = "Synonym of extension concept 2";
		
		Json referenceToExtensionConcept1 = Json.object(
			"typeId", Concepts.PART_OF,
			"destinationId", extensionConceptId1,
			"characteristicTypeId", Concepts.ADDITIONAL_RELATIONSHIP
		);
		
		String extensionConceptId2 = createConcept(extension.getCodeSystemURI(), extensionConceptRequestBody.with(Json.object(
			"descriptions", Json.array(
				fsnRequestBody1.with("term", extensionFsnTerm2), 
				ptRequestBody1.with("term", extensionPtTerm2), 
				synonymRequestBody1.with("term", extensionSynonymTerm2)
			),
			"relationships", Json.array(statedIsa, inferredIsa, referenceToExtensionConcept1)
		)));
		
		// create new extension version
		createVersion(extension.getShortName(), "v1", Dates.now(DateFormats.SHORT)).statusCode(201);

		SnomedConcept extensionConcept1 = getConcept(extension.getCodeSystemURI(), extensionConceptId1, "descriptions()", "relationships()");

		String extensionFsnId1 = getFirstMatchingDescription(extensionConcept1, extensionFsnTerm1).getId();
		String extensionPtId1 = getFirstMatchingDescription(extensionConcept1, extensionPtTerm1).getId();
		String extensionSynonymId1 = getFirstMatchingDescription(extensionConcept1, extensionSynonymTerm1).getId();
		String extensionStatedIsaId1 = getFirstRelationshipId(extensionConcept1, Concepts.STATED_RELATIONSHIP);
		String extensionInferredIsaId1 = getFirstRelationshipId(extensionConcept1, Concepts.INFERRED_RELATIONSHIP);
		
		SnomedConcept extensionConcept2 = getConcept(extension.getCodeSystemURI(), extensionConceptId2, "descriptions()", "relationships()");
		
		String extensionFsnId2 = getFirstMatchingDescription(extensionConcept2, extensionFsnTerm2).getId();
		String extensionPtId2 = getFirstMatchingDescription(extensionConcept2, extensionPtTerm2).getId();
		String extensionSynonymId2 = getFirstMatchingDescription(extensionConcept2, extensionSynonymTerm2).getId();
		String extensionStatedIsaId2 = getFirstRelationshipId(extensionConcept2, Concepts.STATED_RELATIONSHIP);
		String extensionInferredIsaId2 = getFirstRelationshipId(extensionConcept2, Concepts.INFERRED_RELATIONSHIP);
		String referenceToExtensionConcept1RelationshipId = getFirstRelationshipId(extensionConcept2, Concepts.ADDITIONAL_RELATIONSHIP);

		// simulate donation via concept create and versioning
		// donate both concepts without the PART OF relationship reference between them
		String intConceptId1 = createConcept(new CodeSystemURI(SNOMEDCT), Json.object(
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
		String intConceptId2 = createConcept(new CodeSystemURI(SNOMEDCT), Json.object(
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
		
		String effectiveDate = getNextAvailableEffectiveDateAsString(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveDate, effectiveDate).statusCode(201);

		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extension.getCodeSystemURI(), CodeSystemURI.branch(SNOMEDCT, effectiveDate));
		
		SnomedConcept donatedConceptInExtension1 = getConcept(upgradeCodeSystem.getCodeSystemURI(), intConceptId1, "descriptions()", "relationships()");
		SnomedConcept donatedConceptInExtension2 = getConcept(upgradeCodeSystem.getCodeSystemURI(), intConceptId2, "descriptions()", "relationships()");
		
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
		
		String extensionConceptId1 = createConcept(extension.getCodeSystemURI(), extensionConceptRequestBody);

		// create another extension concept which references the previous one
		String extensionFsnTerm2 = "FSN of concept 2";
		String extensionPtTerm2 = "PT of concept 2";
		String extensionSynonymTerm2 = "Synonym of extension concept 2";
		
		Json referenceToExtensionConcept1 = Json.object(
			"typeId", Concepts.PART_OF,
			"destinationId", extensionConceptId1,
			"characteristicTypeId", Concepts.ADDITIONAL_RELATIONSHIP
		);
		
		String extensionConceptId2 = createConcept(extension.getCodeSystemURI(), extensionConceptRequestBody.with(Json.object(
			"descriptions", Json.array(
				fsnRequestBody1.with("term", extensionFsnTerm2), 
				ptRequestBody1.with("term", extensionPtTerm2), 
				synonymRequestBody1.with("term", extensionSynonymTerm2)
			),
			"relationships", Json.array(statedIsa, inferredIsa, referenceToExtensionConcept1)
		)));
		
		// create new extension version
		createVersion(extension.getShortName(), "v1", Dates.now(DateFormats.SHORT)).statusCode(201);

		SnomedConcept extensionConcept1 = getConcept(extension.getCodeSystemURI(), extensionConceptId1, "descriptions()", "relationships()");

		String extensionFsnId1 = getFirstMatchingDescription(extensionConcept1, extensionFsnTerm1).getId();
		String extensionPtId1 = getFirstMatchingDescription(extensionConcept1, extensionPtTerm1).getId();
		String extensionSynonymId1 = getFirstMatchingDescription(extensionConcept1, extensionSynonymTerm1).getId();
		String extensionStatedIsaId1 = getFirstRelationshipId(extensionConcept1, Concepts.STATED_RELATIONSHIP);
		String extensionInferredIsaId1 = getFirstRelationshipId(extensionConcept1, Concepts.INFERRED_RELATIONSHIP);
		
		SnomedConcept extensionConcept2 = getConcept(extension.getCodeSystemURI(), extensionConceptId2, "descriptions()", "relationships()");
		
		String extensionFsnId2 = getFirstMatchingDescription(extensionConcept2, extensionFsnTerm2).getId();
		String extensionPtId2 = getFirstMatchingDescription(extensionConcept2, extensionPtTerm2).getId();
		String extensionSynonymId2 = getFirstMatchingDescription(extensionConcept2, extensionSynonymTerm2).getId();
		String extensionStatedIsaId2 = getFirstRelationshipId(extensionConcept2, Concepts.STATED_RELATIONSHIP);
		String extensionInferredIsaId2 = getFirstRelationshipId(extensionConcept2, Concepts.INFERRED_RELATIONSHIP);
		String referenceToExtensionConcept1RelationshipId = getFirstRelationshipId(extensionConcept2, Concepts.ADDITIONAL_RELATIONSHIP);

		// simulate donation via concept create and versioning
		// donate both concepts without the PART OF relationship reference between them
		String intConceptId = createConcept(new CodeSystemURI(SNOMEDCT), Json.object(
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
		
		String effectiveDate = getNextAvailableEffectiveDateAsString(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveDate, effectiveDate).statusCode(201);

		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extension.getCodeSystemURI(), CodeSystemURI.branch(SNOMEDCT, effectiveDate));
		
		SnomedConcept donatedConceptInExtension1 = getConcept(upgradeCodeSystem.getCodeSystemURI(), intConceptId, "descriptions()", "relationships()");
		SnomedConcept extensionConcept2OnUpgrade = getConcept(upgradeCodeSystem.getCodeSystemURI(), extensionConceptId2, "descriptions()", "relationships()");
		
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
	public void upgrade13CompleteUpgrade() throws Exception {
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		
		// create a new INT version without any changes
		String effectiveDate = getNextAvailableEffectiveDateAsString(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveDate, effectiveDate).statusCode(201);
		CodeSystemURI upgradeVersion = CodeSystemURI.branch(SNOMEDCT, effectiveDate);
		
		// create new extension version with one new concept, module in this case
		String moduleId = createModule(extension);
		createVersion(extension.getCodeSystemURI().getCodeSystem(), "v1", Dates.now(DateFormats.SHORT)).statusCode(201);
		
		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extension.getCodeSystemURI(), upgradeVersion);
		assertEquals(upgradeVersion, upgradeCodeSystem.getExtensionOf());
		
		getComponent(upgradeCodeSystem.getCodeSystemURI().toString(), SnomedComponentType.CONCEPT, moduleId).statusCode(200);
		
		Boolean success = CodeSystemRequests.prepareComplete(upgradeCodeSystem.getShortName())
			.build(upgradeCodeSystem.getRepositoryId())
			.execute(getBus())
			.getSync(1, TimeUnit.MINUTES);
		assertTrue(success);
		
		// after upgrade completion, the upgrade Code System is no longer available
		CodeSystemRestRequests.getCodeSystem(upgradeCodeSystem.getShortName()).assertThat().statusCode(404);
		// the extension should use the upgrade's branch
		CodeSystemRestRequests.getCodeSystem(upgradeCodeSystem.getUpgradeOf().getCodeSystem())
			.assertThat().statusCode(200)
			.and().body("branchPath", is(upgradeCodeSystem.getBranchPath()))
			.and().body("extensionOf", is(upgradeCodeSystem.getExtensionOf().toString()));
	}
	
	private String getFirstRelationshipId(SnomedConcept concept, String characteristicTypeId) {
		return concept.getRelationships().getItems().stream().filter(r -> characteristicTypeId.equals(r.getCharacteristicTypeId())).findFirst().get().getId();
	}
	
	private SnomedDescription getFirstMatchingDescription(SnomedConcept extensionConcept, String extensionFsnTerm) {
		return extensionConcept.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionFsnTerm)).findFirst().get();
	}
	
}
