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

import static com.b2international.snowowl.snomed.core.rest.CodeSystemVersionRestRequests.createVersion;
import static com.b2international.snowowl.snomed.core.rest.CodeSystemVersionRestRequests.getLatestVersion;
import static com.b2international.snowowl.snomed.core.rest.CodeSystemVersionRestRequests.getNextAvailableEffectiveDateAsString;
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.DEFAULT_LANGUAGE_CODE;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.createConceptRequestBody;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.createDescriptionRequestBody;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.createRelationshipRequestBody;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.b2international.commons.json.Json;
import com.b2international.snowowl.core.codesystem.CodeSystem;
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
//		// create extension concept on extension's current branch
//		
//		String extensionFsnTerm = "FSN of concept";
//		String extensionPtTerm = "PT of concept";
//		String extensionSynonymTerm = "Synonym of extension concept";
//		
//		Map<String, Object> fsnRequestBody = createDescriptionRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
//				Concepts.FULLY_SPECIFIED_NAME, extensionFsnTerm, SnomedApiTestConstants.UK_PREFERRED_MAP);
//		Map<String, Object> ptRequestBody = createDescriptionRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
//				Concepts.SYNONYM, extensionPtTerm, SnomedApiTestConstants.UK_PREFERRED_MAP);
//		Map<String, Object> synonymRequestBody = createDescriptionRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION, 
//				Concepts.SYNONYM, extensionSynonymTerm, SnomedApiTestConstants.UK_ACCEPTABLE_MAP);
//	
//		Map<String, Object> statedIsaRequestBody = createRelationshipRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION, 
//				Concepts.IS_A, Concepts.ROOT_CONCEPT, Concepts.STATED_RELATIONSHIP);
//		Map<String, Object> inferredIsaRequestBody = createRelationshipRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
//				Concepts.IS_A, Concepts.ROOT_CONCEPT, Concepts.INFERRED_RELATIONSHIP);
//	
//		Map<String, Object> extensionConceptRequestBody = createConceptRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
//				ImmutableList.of(statedIsaRequestBody, inferredIsaRequestBody), 
//				ImmutableList.of(fsnRequestBody, ptRequestBody, synonymRequestBody));
//		
//		String extensionConceptId = lastPathSegment(createComponent(branchPath, SnomedComponentType.CONCEPT, extensionConceptRequestBody)
//				.statusCode(201)
//				.extract().header("Location"));
//		
//		SnomedConcept extensionConcept = getComponent(branchPath, SnomedComponentType.CONCEPT, extensionConceptId, "descriptions(), relationships()")
//				.statusCode(200)
//				.extract()
//				.as(SnomedConcept.class);
//		
//		String extensionFsnId = extensionConcept.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionFsnTerm)).findFirst()
//				.get().getId();
//		String extensionPtId = extensionConcept.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionPtTerm)).findFirst()
//				.get().getId();
//		String extensionSynonymId = extensionConcept.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionSynonymTerm))
//				.findFirst().get().getId();
//	
//		String extensionStatedIsaId = getFirstRelationshipId(extensionConcept, Concepts.STATED_RELATIONSHIP);
//		String extensionInferredIsaId = getFirstRelationshipId(extensionConcept, Concepts.INFERRED_RELATIONSHIP);
//	
//		// create new version on MAIN
//		
//		String effectiveDate = getNextAvailableEffectiveDateAsString(SNOMEDCT);
//		String versionId = "v8";
//		createVersion(SNOMEDCT, versionId, effectiveDate).statusCode(201);
//		
//		IBranchPath targetPath = BranchPathUtils.createPath(PATH_JOINER.join(
//				Branch.MAIN_PATH, 
//				versionId, 
//				SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME));
//	
//		branching.createBranch(targetPath).statusCode(201);
//		
//		// create INT concept with same ID but different description and relationship IDs on version branch
//		
//		Map<String, Object> intFsnRequestBody = createDescriptionRequestBody(extensionFsnId, "", Concepts.MODULE_SCT_CORE,
//				Concepts.FULLY_SPECIFIED_NAME, extensionFsnTerm, SnomedApiTestConstants.UK_PREFERRED_MAP);
//		Map<String, Object> intPtRequestBody = createDescriptionRequestBody(extensionPtId, "", Concepts.MODULE_SCT_CORE,
//				Concepts.SYNONYM, extensionPtTerm, SnomedApiTestConstants.UK_PREFERRED_MAP);
//	
//		Map<String, Object> intStatedIsaRequestBody = createRelationshipRequestBody(extensionStatedIsaId, "", Concepts.MODULE_SCT_CORE, 
//				Concepts.IS_A, Concepts.ROOT_CONCEPT, Concepts.STATED_RELATIONSHIP);
//		Map<String, Object> intInferredIsaRequestBody = createRelationshipRequestBody(extensionInferredIsaId, "", Concepts.MODULE_SCT_CORE,
//				Concepts.IS_A, Concepts.ROOT_CONCEPT, Concepts.INFERRED_RELATIONSHIP);
//		
//		Map<String, Object> intConceptRequestBody = createConceptRequestBody(extensionConceptId, "", Concepts.MODULE_SCT_CORE,
//				ImmutableList.of(intStatedIsaRequestBody, intInferredIsaRequestBody), 
//				ImmutableList.of(intFsnRequestBody, intPtRequestBody));
//		
//		String intConceptId = lastPathSegment(createComponent(targetPath, SnomedComponentType.CONCEPT, intConceptRequestBody)
//				.statusCode(201)
//				.body(equalTo(""))
//				.extract().header("Location"));
//		
//		SnomedConcept intConcept = getComponent(targetPath, SnomedComponentType.CONCEPT, intConceptId,
//				"descriptions(), relationships()")
//				.statusCode(200)
//				.extract().as(SnomedConcept.class);
//		
//		String intFsnId = intConcept.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionFsnTerm)).findFirst().get()
//				.getId();
//		String intPtId = intConcept.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionPtTerm)).findFirst().get().getId();
//	
//		String intStatedIsaId = getFirstRelationshipId(intConcept, Concepts.STATED_RELATIONSHIP);
//		String intInferredIsaId = getFirstRelationshipId(intConcept, Concepts.INFERRED_RELATIONSHIP);
//	
//		assertEquals(intConceptId, extensionConceptId);
//		assertNotEquals(intConcept.getModuleId(), extensionConcept.getModuleId());
//		
//		assertEquals(intFsnId, extensionFsnId);
//		assertEquals(intPtId, extensionPtId);
//		assertEquals(intStatedIsaId, extensionStatedIsaId);
//		assertEquals(intInferredIsaId, extensionInferredIsaId);
//				
//		// upgrade extension to new INT version
//		
//		merge(branchPath, targetPath, "Upgraded B2i extension to v8").body("status", equalTo(Merge.Status.COMPLETED.name()));
//	
//		Map<?, ?> updateRequest = ImmutableMap.builder()
//				.put("repositoryId", SnomedDatastoreActivator.REPOSITORY_UUID)
//				.put("branchPath", targetPath.getPath())
//				.build();
//	
//		updateCodeSystem(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME, updateRequest).statusCode(204);
//		getCodeSystem(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME).statusCode(200).body("branchPath", equalTo(targetPath.getPath()));
//	
//		SnomedConcept donatedConceptInExtension = getComponent(targetPath, SnomedComponentType.CONCEPT, extensionConceptId,
//				"descriptions(), relationships()")
//				.statusCode(200)
//				.extract().as(SnomedConcept.class);
//		
//		// validate components of donated concept on extension branch
//		
//		List<SnomedDescription> donatedFsns = donatedConceptInExtension.getDescriptions().getItems().stream()
//				.filter(d -> d.getTerm().equals(extensionFsnTerm)).collect(toList());
//
//		assertEquals(1, donatedFsns.size());
//		SnomedDescription donatedFsn = Iterables.getOnlyElement(donatedFsns);
//
//		assertEquals(Concepts.MODULE_SCT_CORE, donatedFsn.getModuleId());
//
//		List<SnomedDescription> donatedPts = donatedConceptInExtension.getDescriptions().getItems().stream()
//				.filter(d -> d.getTerm().equals(extensionPtTerm)).collect(toList());
//
//		assertEquals(1, donatedPts.size());
//		SnomedDescription donatedPt = Iterables.getOnlyElement(donatedPts);
//		
//		assertEquals(Concepts.MODULE_SCT_CORE, donatedPt.getModuleId());
//		
//		Set<String> descriptionIds = donatedConceptInExtension.getDescriptions().getItems().stream().map(SnomedDescription::getId).collect(toSet());
//		assertTrue(descriptionIds.contains(extensionSynonymId));
//		
//		List<SnomedRelationship> donatedStatedIsas = donatedConceptInExtension.getRelationships().getItems().stream()
//				.filter(r -> r.getTypeId().equals(Concepts.IS_A) && Concepts.STATED_RELATIONSHIP.equals(r.getCharacteristicTypeId()))
//				.collect(toList());
//		
//		assertEquals(1, donatedStatedIsas.size());
//		SnomedRelationship donatedStatedIsa = Iterables.getOnlyElement(donatedStatedIsas);
//		assertEquals(extensionStatedIsaId, donatedStatedIsa.getId());
//		assertEquals(Concepts.MODULE_SCT_CORE, donatedStatedIsa.getModuleId());
//		
//		List<SnomedRelationship> donatedInferredIsas = donatedConceptInExtension.getRelationships().getItems().stream()
//				.filter(r -> r.getTypeId().equals(Concepts.IS_A) && Concepts.INFERRED_RELATIONSHIP.equals(r.getCharacteristicTypeId()))
//				.collect(toList());
//		
//		assertEquals(1, donatedInferredIsas.size());
//		SnomedRelationship donatedInferredIsa = Iterables.getOnlyElement(donatedInferredIsas);
//		assertEquals(extensionInferredIsaId, donatedInferredIsa.getId());
//		assertEquals(Concepts.MODULE_SCT_CORE, donatedInferredIsa.getModuleId());
	}
	
	@Test
	public void upgrade11DonatedConceptAndDescriptionsAndRelationshipsWithCrossReference() {
//		// create extension concept on extension's current branch
//		
//		String extensionFsnTerm1 = "FSN of concept";
//		String extensionPtTerm1 = "PT of concept";
//		String extensionSynonymTerm1 = "Synonym of extension concept";
//		
//		Map<String, Object> fsnRequestBody1 = createDescriptionRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
//				Concepts.FULLY_SPECIFIED_NAME, extensionFsnTerm1, SnomedApiTestConstants.UK_PREFERRED_MAP);
//		Map<String, Object> ptRequestBody1 = createDescriptionRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
//				Concepts.SYNONYM, extensionPtTerm1, SnomedApiTestConstants.UK_PREFERRED_MAP);
//		Map<String, Object> synonymRequestBody1 = createDescriptionRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION, 
//				Concepts.SYNONYM, extensionSynonymTerm1, SnomedApiTestConstants.UK_ACCEPTABLE_MAP);
//	
//		Map<String, Object> statedIsaRequestBody1 = createRelationshipRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION, 
//				Concepts.IS_A, Concepts.ROOT_CONCEPT, Concepts.STATED_RELATIONSHIP);
//		Map<String, Object> inferredIsaRequestBody1 = createRelationshipRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
//				Concepts.IS_A, Concepts.ROOT_CONCEPT, Concepts.INFERRED_RELATIONSHIP);
//	
//		Map<String, Object> extensionConceptRequestBody1 = createConceptRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
//				ImmutableList.of(statedIsaRequestBody1, inferredIsaRequestBody1), 
//				ImmutableList.of(fsnRequestBody1, ptRequestBody1, synonymRequestBody1));
//		
//		String extensionConceptId1 = lastPathSegment(createComponent(branchPath, SnomedComponentType.CONCEPT, extensionConceptRequestBody1)
//				.statusCode(201)
//				.extract().header("Location"));
//		
//		SnomedConcept extensionConcept1 = getComponent(branchPath, SnomedComponentType.CONCEPT, extensionConceptId1, "descriptions(), relationships()")
//				.statusCode(200)
//				.extract()
//				.as(SnomedConcept.class);
//		
//		String extensionFsnId1 = extensionConcept1.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionFsnTerm1)).findFirst()
//				.get().getId();
//		String extensionPtId1 = extensionConcept1.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionPtTerm1)).findFirst()
//				.get().getId();
//		String extensionSynonymId1 = extensionConcept1.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionSynonymTerm1))
//				.findFirst().get().getId();
//	
//		String extensionStatedIsaId1 = getFirstRelationshipId(extensionConcept1, Concepts.STATED_RELATIONSHIP);
//		String extensionInferredIsaId1 = getFirstRelationshipId(extensionConcept1, Concepts.INFERRED_RELATIONSHIP);
//	
//		// create another extension concept which references the previous one
//		
//		String extensionFsnTerm2 = "FSN of concept 2";
//		String extensionPtTerm2 = "PT of concept 2";
//		String extensionSynonymTerm2 = "Synonym of extension concept 2";
//		
//		Map<String, Object> fsnRequestBody2 = createDescriptionRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
//				Concepts.FULLY_SPECIFIED_NAME, extensionFsnTerm2, SnomedApiTestConstants.UK_PREFERRED_MAP);
//		Map<String, Object> ptRequestBody2 = createDescriptionRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
//				Concepts.SYNONYM, extensionPtTerm2, SnomedApiTestConstants.UK_PREFERRED_MAP);
//		Map<String, Object> synonymRequestBody2 = createDescriptionRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION, 
//				Concepts.SYNONYM, extensionSynonymTerm2, SnomedApiTestConstants.UK_ACCEPTABLE_MAP);
//	
//		Map<String, Object> statedIsaRequestBody2 = createRelationshipRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION, 
//				Concepts.IS_A, Concepts.ROOT_CONCEPT, Concepts.STATED_RELATIONSHIP);
//		Map<String, Object> inferredIsaRequestBody2 = createRelationshipRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
//				Concepts.IS_A, Concepts.ROOT_CONCEPT, Concepts.INFERRED_RELATIONSHIP);
//		
//		Map<String, Object> partOfRequestBody = createRelationshipRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
//				Concepts.PART_OF, extensionConceptId1, Concepts.ADDITIONAL_RELATIONSHIP);
//	
//		Map<String, Object> extensionConceptRequestBody2 = createConceptRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
//				ImmutableList.of(statedIsaRequestBody2, inferredIsaRequestBody2, partOfRequestBody), 
//				ImmutableList.of(fsnRequestBody2, ptRequestBody2, synonymRequestBody2));
//		
//		String extensionConceptId2 = lastPathSegment(createComponent(branchPath, SnomedComponentType.CONCEPT, extensionConceptRequestBody2)
//				.statusCode(201)
//				.extract().header("Location"));
//		
//		SnomedConcept extensionConcept2 = getComponent(branchPath, SnomedComponentType.CONCEPT, extensionConceptId2, "descriptions(), relationships()")
//				.statusCode(200)
//				.extract()
//				.as(SnomedConcept.class);
//		
//		String extensionFsnId2 = extensionConcept2.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionFsnTerm2)).findFirst()
//				.get().getId();
//		String extensionPtId2 = extensionConcept2.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionPtTerm2)).findFirst()
//				.get().getId();
//		String extensionSynonymId2 = extensionConcept2.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionSynonymTerm2))
//				.findFirst().get().getId();
//	
//		String extensionStatedIsaId2 = getFirstRelationshipId(extensionConcept2, Concepts.STATED_RELATIONSHIP);
//		String extensionInferredIsaId2 = getFirstRelationshipId(extensionConcept2, Concepts.INFERRED_RELATIONSHIP);
//		String extensionPartOfId = getFirstRelationshipId(extensionConcept2, Concepts.ADDITIONAL_RELATIONSHIP);
//		
//		// create new version on MAIN
//		
//		String effectiveDate = getNextAvailableEffectiveDateAsString(SNOMEDCT);
//		String versionId = "v13";
//		createVersion(SNOMEDCT, versionId, effectiveDate).statusCode(201);
//		
//		IBranchPath targetPath = BranchPathUtils.createPath(PATH_JOINER.join(
//				Branch.MAIN_PATH, 
//				versionId, 
//				SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME));
//	
//		branching.createBranch(targetPath).statusCode(201);
//		
//		// create INT concept with same ID on version branch
//		
//		Map<String, Object> intFsnRequestBody1 = createDescriptionRequestBody(extensionFsnId1, "", Concepts.MODULE_SCT_CORE,
//				Concepts.FULLY_SPECIFIED_NAME, extensionFsnTerm1, SnomedApiTestConstants.UK_PREFERRED_MAP);
//		Map<String, Object> intPtRequestBody1 = createDescriptionRequestBody(extensionPtId1, "", Concepts.MODULE_SCT_CORE,
//				Concepts.SYNONYM, extensionPtTerm1, SnomedApiTestConstants.UK_PREFERRED_MAP);
//	
//		Map<String, Object> intStatedIsaRequestBody1 = createRelationshipRequestBody(extensionStatedIsaId1, "", Concepts.MODULE_SCT_CORE, 
//				Concepts.IS_A, Concepts.ROOT_CONCEPT, Concepts.STATED_RELATIONSHIP);
//		Map<String, Object> intInferredIsaRequestBody1 = createRelationshipRequestBody(extensionInferredIsaId1, "", Concepts.MODULE_SCT_CORE,
//				Concepts.IS_A, Concepts.ROOT_CONCEPT, Concepts.INFERRED_RELATIONSHIP);
//		
//		Map<String, Object> intConceptRequestBody1 = createConceptRequestBody(extensionConceptId1, "", Concepts.MODULE_SCT_CORE,
//				ImmutableList.of(intStatedIsaRequestBody1, intInferredIsaRequestBody1), 
//				ImmutableList.of(intFsnRequestBody1, intPtRequestBody1));
//		
//		String intConceptId1 = lastPathSegment(createComponent(targetPath, SnomedComponentType.CONCEPT, intConceptRequestBody1)
//				.statusCode(201)
//				.body(equalTo(""))
//				.extract().header("Location"));
//		
//		SnomedConcept intConcept1 = getComponent(targetPath, SnomedComponentType.CONCEPT, intConceptId1,
//				"descriptions(), relationships()")
//				.statusCode(200)
//				.extract().as(SnomedConcept.class);
//		
//		String intFsnId1 = intConcept1.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionFsnTerm1)).findFirst().get()
//				.getId();
//		String intPtId1 = intConcept1.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionPtTerm1)).findFirst().get().getId();
//	
//		String intStatedIsaId1 = getFirstRelationshipId(intConcept1, Concepts.STATED_RELATIONSHIP);
//		String intInferredIsaId1 = getFirstRelationshipId(intConcept1, Concepts.INFERRED_RELATIONSHIP);
//	
//		assertEquals(intConceptId1, extensionConceptId1);
//		assertNotEquals(intConcept1.getModuleId(), extensionConcept1.getModuleId());
//		
//		assertEquals(intFsnId1, extensionFsnId1);
//		assertEquals(intPtId1, extensionPtId1);
//		assertEquals(intStatedIsaId1, extensionStatedIsaId1);
//		assertEquals(intInferredIsaId1, extensionInferredIsaId1);
//		
//		// create another INT concept for extension concept 2 with same ID but WITHOUT the part of relationship on version branch
//		
//		Map<String, Object> intFsnRequestBody2 = createDescriptionRequestBody(extensionFsnId2, "", Concepts.MODULE_SCT_CORE,
//				Concepts.FULLY_SPECIFIED_NAME, extensionFsnTerm2, SnomedApiTestConstants.UK_PREFERRED_MAP);
//		Map<String, Object> intPtRequestBody2 = createDescriptionRequestBody(extensionPtId2, "", Concepts.MODULE_SCT_CORE,
//				Concepts.SYNONYM, extensionPtTerm2, SnomedApiTestConstants.UK_PREFERRED_MAP);
//	
//		Map<String, Object> intStatedIsaRequestBody2 = createRelationshipRequestBody(extensionStatedIsaId2, "", Concepts.MODULE_SCT_CORE, 
//				Concepts.IS_A, Concepts.ROOT_CONCEPT, Concepts.STATED_RELATIONSHIP);
//		Map<String, Object> intInferredIsaRequestBody2 = createRelationshipRequestBody(extensionInferredIsaId2, "", Concepts.MODULE_SCT_CORE,
//				Concepts.IS_A, Concepts.ROOT_CONCEPT, Concepts.INFERRED_RELATIONSHIP);
//		
//		Map<String, Object> intConceptRequestBody2 = createConceptRequestBody(extensionConceptId2, "", Concepts.MODULE_SCT_CORE,
//				ImmutableList.of(intStatedIsaRequestBody2, intInferredIsaRequestBody2), 
//				ImmutableList.of(intFsnRequestBody2, intPtRequestBody2));
//		
//		String intConceptId2 = lastPathSegment(createComponent(targetPath, SnomedComponentType.CONCEPT, intConceptRequestBody2)
//				.statusCode(201)
//				.body(equalTo(""))
//				.extract().header("Location"));
//		
//		SnomedConcept intConcept2 = getComponent(targetPath, SnomedComponentType.CONCEPT, intConceptId2,
//				"descriptions(), relationships()")
//				.statusCode(200)
//				.extract().as(SnomedConcept.class);
//		
//		String intFsnId2 = intConcept2.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionFsnTerm2)).findFirst().get()
//				.getId();
//		String intPtId2 = intConcept2.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionPtTerm2)).findFirst().get().getId();
//	
//		String intStatedIsaId2 = getFirstRelationshipId(intConcept2, Concepts.STATED_RELATIONSHIP);
//		String intInferredIsaId2 = getFirstRelationshipId(intConcept2, Concepts.INFERRED_RELATIONSHIP);
//	
//		assertEquals(intConceptId2, extensionConceptId2);
//		assertNotEquals(intConcept2.getModuleId(), extensionConcept2.getModuleId());
//		
//		assertEquals(intFsnId2, extensionFsnId2);
//		assertEquals(intPtId2, extensionPtId2);
//		assertEquals(intStatedIsaId2, extensionStatedIsaId2);
//		assertEquals(intInferredIsaId2, extensionInferredIsaId2);
//		
//		// upgrade extension to new INT version
//		
//		merge(branchPath, targetPath, "Upgraded B2i extension to v13").body("status", equalTo(Merge.Status.COMPLETED.name()));
//	
//		Map<?, ?> updateRequest = ImmutableMap.builder()
//				.put("repositoryId", SnomedDatastoreActivator.REPOSITORY_UUID)
//				.put("branchPath", targetPath.getPath())
//				.build();
//	
//		updateCodeSystem(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME, updateRequest).statusCode(204);
//		getCodeSystem(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME).statusCode(200).body("branchPath", equalTo(targetPath.getPath()));
//	
//		SnomedConcept donatedConceptInExtension1 = getComponent(targetPath, SnomedComponentType.CONCEPT, extensionConceptId1,
//				"descriptions(), relationships()")
//				.statusCode(200)
//				.extract().as(SnomedConcept.class);
//		
//		// validate components of donated concept on extension branch
//		
//		List<SnomedDescription> donatedFsns = donatedConceptInExtension1.getDescriptions().getItems().stream()
//				.filter(d -> d.getTerm().equals(extensionFsnTerm1)).collect(toList());
//
//		assertEquals(1, donatedFsns.size());
//		SnomedDescription donatedFsn = Iterables.getOnlyElement(donatedFsns);
//
//		assertEquals(extensionFsnId1, donatedFsn.getId());
//		assertEquals(Concepts.MODULE_SCT_CORE, donatedFsn.getModuleId());
//
//		List<SnomedDescription> donatedPts = donatedConceptInExtension1.getDescriptions().getItems().stream()
//				.filter(d -> d.getTerm().equals(extensionPtTerm1)).collect(toList());
//
//		assertEquals(1, donatedPts.size());
//		SnomedDescription donatedPt = Iterables.getOnlyElement(donatedPts);
//		
//		assertEquals(extensionPtId1, donatedPt.getId());
//		assertEquals(Concepts.MODULE_SCT_CORE, donatedPt.getModuleId());
//		
//		List<SnomedDescription> additionalSynonyms = donatedConceptInExtension1.getDescriptions().getItems().stream()
//				.filter(d -> d.getTerm().equals(extensionSynonymTerm1)).collect(toList());
//
//		assertEquals(1, additionalSynonyms.size());
//		SnomedDescription additionalSyonym = Iterables.getOnlyElement(additionalSynonyms);
//		
//		assertEquals(extensionSynonymId1, additionalSyonym.getId());
//		assertEquals(Concepts.MODULE_B2I_EXTENSION, additionalSyonym.getModuleId());
//		
//		List<SnomedRelationship> donatedStatedIsas = donatedConceptInExtension1.getRelationships().getItems().stream()
//				.filter(r -> r.getTypeId().equals(Concepts.IS_A) && Concepts.STATED_RELATIONSHIP.equals(r.getCharacteristicTypeId()))
//				.collect(toList());
//		
//		assertEquals(1, donatedStatedIsas.size());
//		SnomedRelationship donatedStatedIsa = Iterables.getOnlyElement(donatedStatedIsas);
//		assertEquals(extensionStatedIsaId1, donatedStatedIsa.getId());
//		assertEquals(Concepts.MODULE_SCT_CORE, donatedStatedIsa.getModuleId());
//		
//		List<SnomedRelationship> donatedInferredIsas = donatedConceptInExtension1.getRelationships().getItems().stream()
//				.filter(r -> r.getTypeId().equals(Concepts.IS_A) && Concepts.INFERRED_RELATIONSHIP.equals(r.getCharacteristicTypeId()))
//				.collect(toList());
//		
//		assertEquals(1, donatedInferredIsas.size());
//		SnomedRelationship donatedInferredIsa = Iterables.getOnlyElement(donatedInferredIsas);
//		assertEquals(extensionInferredIsaId1, donatedInferredIsa.getId());
//		assertEquals(Concepts.MODULE_SCT_CORE, donatedInferredIsa.getModuleId());
//		
//		// validate components of donated concept 2 on extension branch
//
//		SnomedConcept donatedConceptInExtension2 = getComponent(targetPath, SnomedComponentType.CONCEPT, extensionConceptId2,
//				"descriptions(), relationships()")
//				.statusCode(200)
//				.extract().as(SnomedConcept.class);
//		
//		List<SnomedDescription> donatedFsns2 = donatedConceptInExtension2.getDescriptions().getItems().stream()
//				.filter(d -> d.getTerm().equals(extensionFsnTerm2)).collect(toList());
//
//		assertEquals(1, donatedFsns2.size());
//		SnomedDescription donatedFsn2 = Iterables.getOnlyElement(donatedFsns2);
//
//		assertEquals(extensionFsnId2, donatedFsn2.getId());
//		assertEquals(Concepts.MODULE_SCT_CORE, donatedFsn2.getModuleId());
//
//		List<SnomedDescription> donatedPts2 = donatedConceptInExtension2.getDescriptions().getItems().stream()
//				.filter(d -> d.getTerm().equals(extensionPtTerm2)).collect(toList());
//
//		assertEquals(1, donatedPts2.size());
//		SnomedDescription donatedPt2 = Iterables.getOnlyElement(donatedPts2);
//		
//		assertEquals(extensionPtId2, donatedPt2.getId());
//		assertEquals(Concepts.MODULE_SCT_CORE, donatedPt2.getModuleId());
//		
//		List<SnomedDescription> additionalSynonyms2 = donatedConceptInExtension2.getDescriptions().getItems().stream()
//				.filter(d -> d.getTerm().equals(extensionSynonymTerm2)).collect(toList());
//
//		assertEquals(1, additionalSynonyms2.size());
//		SnomedDescription additionalSyonym2 = Iterables.getOnlyElement(additionalSynonyms2);
//		
//		assertEquals(extensionSynonymId2, additionalSyonym2.getId());
//		assertEquals(Concepts.MODULE_B2I_EXTENSION, additionalSyonym2.getModuleId());
//		
//		List<SnomedRelationship> donatedStatedIsas2 = donatedConceptInExtension2.getRelationships().getItems().stream()
//				.filter(r -> r.getTypeId().equals(Concepts.IS_A) && Concepts.STATED_RELATIONSHIP.equals(r.getCharacteristicTypeId()))
//				.collect(toList());
//		
//		assertEquals(1, donatedStatedIsas2.size());
//		SnomedRelationship donatedStatedIsa2 = Iterables.getOnlyElement(donatedStatedIsas2);
//		assertEquals(extensionStatedIsaId2, donatedStatedIsa2.getId());
//		assertEquals(Concepts.MODULE_SCT_CORE, donatedStatedIsa2.getModuleId());
//		
//		List<SnomedRelationship> donatedInferredIsas2 = donatedConceptInExtension2.getRelationships().getItems().stream()
//				.filter(r -> r.getTypeId().equals(Concepts.IS_A) && Concepts.INFERRED_RELATIONSHIP.equals(r.getCharacteristicTypeId()))
//				.collect(toList());
//		
//		assertEquals(1, donatedInferredIsas2.size());
//		SnomedRelationship donatedInferredIsa2 = Iterables.getOnlyElement(donatedInferredIsas2);
//		assertEquals(extensionInferredIsaId2, donatedInferredIsa2.getId());
//		assertEquals(Concepts.MODULE_SCT_CORE, donatedInferredIsa2.getModuleId());
//		
//		List<SnomedRelationship> additionalRelationships = donatedConceptInExtension2.getRelationships().getItems().stream()
//				.filter(r -> Concepts.ADDITIONAL_RELATIONSHIP.equals(r.getCharacteristicTypeId()))
//				.collect(toList());
//		
//		assertEquals(1, additionalRelationships.size());
//		SnomedRelationship additionalRelationship = Iterables.getOnlyElement(additionalRelationships);
//		assertEquals(extensionPartOfId, additionalRelationship.getId());
//		assertEquals(Concepts.MODULE_B2I_EXTENSION, additionalRelationship.getModuleId());
//		assertEquals(extensionConceptId1, additionalRelationship.getDestinationId());
	}

	@Test
	public void upgrade12WithDonatedConceptAndDescriptionsAndRelationshipsWithExternalReference() {
//		// create extension concept on extension's current branch
//		
//		String extensionFsnTerm1 = "FSN of concept";
//		String extensionPtTerm1 = "PT of concept";
//		String extensionSynonymTerm1 = "Synonym of extension concept";
//		
//		Map<String, Object> fsnRequestBody1 = createDescriptionRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
//				Concepts.FULLY_SPECIFIED_NAME, extensionFsnTerm1, SnomedApiTestConstants.UK_PREFERRED_MAP);
//		Map<String, Object> ptRequestBody1 = createDescriptionRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
//				Concepts.SYNONYM, extensionPtTerm1, SnomedApiTestConstants.UK_PREFERRED_MAP);
//		Map<String, Object> synonymRequestBody1 = createDescriptionRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION, 
//				Concepts.SYNONYM, extensionSynonymTerm1, SnomedApiTestConstants.UK_ACCEPTABLE_MAP);
//	
//		Map<String, Object> statedIsaRequestBody1 = createRelationshipRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION, 
//				Concepts.IS_A, Concepts.ROOT_CONCEPT, Concepts.STATED_RELATIONSHIP);
//		Map<String, Object> inferredIsaRequestBody1 = createRelationshipRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
//				Concepts.IS_A, Concepts.ROOT_CONCEPT, Concepts.INFERRED_RELATIONSHIP);
//	
//		Map<String, Object> extensionConceptRequestBody1 = createConceptRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
//				ImmutableList.of(statedIsaRequestBody1, inferredIsaRequestBody1), 
//				ImmutableList.of(fsnRequestBody1, ptRequestBody1, synonymRequestBody1));
//		
//		String extensionConceptId1 = lastPathSegment(createComponent(branchPath, SnomedComponentType.CONCEPT, extensionConceptRequestBody1)
//				.statusCode(201)
//				.extract().header("Location"));
//		
//		SnomedConcept extensionConcept1 = getComponent(branchPath, SnomedComponentType.CONCEPT, extensionConceptId1, "descriptions(), relationships()")
//				.statusCode(200)
//				.extract()
//				.as(SnomedConcept.class);
//		
//		String extensionFsnId1 = extensionConcept1.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionFsnTerm1)).findFirst()
//				.get().getId();
//		String extensionPtId1 = extensionConcept1.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionPtTerm1)).findFirst()
//				.get().getId();
//		String extensionSynonymId1 = extensionConcept1.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionSynonymTerm1))
//				.findFirst().get().getId();
//	
//		String extensionStatedIsaId1 = getFirstRelationshipId(extensionConcept1, Concepts.STATED_RELATIONSHIP);
//		String extensionInferredIsaId1 = getFirstRelationshipId(extensionConcept1, Concepts.INFERRED_RELATIONSHIP);
//	
//		// create another extension concept which references the previous one
//		
//		String extensionFsnTerm2 = "FSN of concept 2";
//		String extensionPtTerm2 = "PT of concept 2";
//		String extensionSynonymTerm2 = "Synonym of extension concept 2";
//		
//		Map<String, Object> fsnRequestBody2 = createDescriptionRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
//				Concepts.FULLY_SPECIFIED_NAME, extensionFsnTerm2, SnomedApiTestConstants.UK_PREFERRED_MAP);
//		Map<String, Object> ptRequestBody2 = createDescriptionRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
//				Concepts.SYNONYM, extensionPtTerm2, SnomedApiTestConstants.UK_PREFERRED_MAP);
//		Map<String, Object> synonymRequestBody2 = createDescriptionRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION, 
//				Concepts.SYNONYM, extensionSynonymTerm2, SnomedApiTestConstants.UK_ACCEPTABLE_MAP);
//	
//		Map<String, Object> statedIsaRequestBody2 = createRelationshipRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION, 
//				Concepts.IS_A, Concepts.ROOT_CONCEPT, Concepts.STATED_RELATIONSHIP);
//		Map<String, Object> inferredIsaRequestBody2 = createRelationshipRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
//				Concepts.IS_A, Concepts.ROOT_CONCEPT, Concepts.INFERRED_RELATIONSHIP);
//		
//		Map<String, Object> partOfRequestBody = createRelationshipRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
//				Concepts.PART_OF, extensionConceptId1, Concepts.ADDITIONAL_RELATIONSHIP);
//	
//		Map<String, Object> extensionConceptRequestBody2 = createConceptRequestBody(Concepts.B2I_NAMESPACE, Concepts.MODULE_B2I_EXTENSION,
//				ImmutableList.of(statedIsaRequestBody2, inferredIsaRequestBody2, partOfRequestBody), 
//				ImmutableList.of(fsnRequestBody2, ptRequestBody2, synonymRequestBody2));
//		
//		String extensionConceptId2 = lastPathSegment(createComponent(branchPath, SnomedComponentType.CONCEPT, extensionConceptRequestBody2)
//				.statusCode(201)
//				.extract().header("Location"));
//		
//		SnomedConcept extensionConcept2 = getComponent(branchPath, SnomedComponentType.CONCEPT, extensionConceptId2, "descriptions(), relationships()")
//				.statusCode(200)
//				.extract()
//				.as(SnomedConcept.class);
//		
//		String extensionFsnId2 = extensionConcept2.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionFsnTerm2)).findFirst()
//				.get().getId();
//		String extensionPtId2 = extensionConcept2.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionPtTerm2)).findFirst()
//				.get().getId();
//		String extensionSynonymId2 = extensionConcept2.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionSynonymTerm2))
//				.findFirst().get().getId();
//	
//		String extensionStatedIsaId2 = getFirstRelationshipId(extensionConcept2, Concepts.STATED_RELATIONSHIP);
//		String extensionInferredIsaId2 = getFirstRelationshipId(extensionConcept2, Concepts.INFERRED_RELATIONSHIP);
//		String extensionPartOfId = getFirstRelationshipId(extensionConcept2, Concepts.ADDITIONAL_RELATIONSHIP);
//		
//		// create new version on MAIN
//		
//		String effectiveDate = getNextAvailableEffectiveDateAsString(SNOMEDCT);
//		String versionId = "v12";
//		createVersion(SNOMEDCT, versionId, effectiveDate).statusCode(201);
//		
//		IBranchPath targetPath = BranchPathUtils.createPath(PATH_JOINER.join(
//				Branch.MAIN_PATH, 
//				versionId, 
//				SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME));
//	
//		branching.createBranch(targetPath).statusCode(201);
//		
//		// create INT concept with same ID on version branch
//		
//		Map<String, Object> intFsnRequestBody1 = createDescriptionRequestBody(extensionFsnId1, "", Concepts.MODULE_SCT_CORE,
//				Concepts.FULLY_SPECIFIED_NAME, extensionFsnTerm1, SnomedApiTestConstants.UK_PREFERRED_MAP);
//		Map<String, Object> intPtRequestBody1 = createDescriptionRequestBody(extensionPtId1, "", Concepts.MODULE_SCT_CORE,
//				Concepts.SYNONYM, extensionPtTerm1, SnomedApiTestConstants.UK_PREFERRED_MAP);
//	
//		Map<String, Object> intStatedIsaRequestBody1 = createRelationshipRequestBody(extensionStatedIsaId1, "", Concepts.MODULE_SCT_CORE, 
//				Concepts.IS_A, Concepts.ROOT_CONCEPT, Concepts.STATED_RELATIONSHIP);
//		Map<String, Object> intInferredIsaRequestBody1 = createRelationshipRequestBody(extensionInferredIsaId1, "", Concepts.MODULE_SCT_CORE,
//				Concepts.IS_A, Concepts.ROOT_CONCEPT, Concepts.INFERRED_RELATIONSHIP);
//		
//		Map<String, Object> intConceptRequestBody1 = createConceptRequestBody(extensionConceptId1, "", Concepts.MODULE_SCT_CORE,
//				ImmutableList.of(intStatedIsaRequestBody1, intInferredIsaRequestBody1), 
//				ImmutableList.of(intFsnRequestBody1, intPtRequestBody1));
//		
//		String intConceptId1 = lastPathSegment(createComponent(targetPath, SnomedComponentType.CONCEPT, intConceptRequestBody1)
//				.statusCode(201)
//				.body(equalTo(""))
//				.extract().header("Location"));
//		
//		SnomedConcept intConcept1 = getComponent(targetPath, SnomedComponentType.CONCEPT, intConceptId1,
//				"descriptions(), relationships()")
//				.statusCode(200)
//				.extract().as(SnomedConcept.class);
//		
//		String intFsnId1 = intConcept1.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionFsnTerm1)).findFirst().get()
//				.getId();
//		String intPtId1 = intConcept1.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionPtTerm1)).findFirst().get().getId();
//	
//		String intStatedIsaId1 = getFirstRelationshipId(intConcept1, Concepts.STATED_RELATIONSHIP);
//		String intInferredIsaId1 = getFirstRelationshipId(intConcept1, Concepts.INFERRED_RELATIONSHIP);
//	
//		assertEquals(intConceptId1, extensionConceptId1);
//		assertNotEquals(intConcept1.getModuleId(), extensionConcept1.getModuleId());
//		
//		assertEquals(intFsnId1, extensionFsnId1);
//		assertEquals(intPtId1, extensionPtId1);
//		assertEquals(intStatedIsaId1, extensionStatedIsaId1);
//		assertEquals(intInferredIsaId1, extensionInferredIsaId1);
//		
//		// do not create an international pair for extension concept 2 (so we can handle the "external" reference)
//		
//		// upgrade extension to new INT version
//		
//		merge(branchPath, targetPath, "Upgraded B2i extension to v12").body("status", equalTo(Merge.Status.COMPLETED.name()));
//	
//		Map<?, ?> updateRequest = ImmutableMap.builder()
//				.put("repositoryId", SnomedDatastoreActivator.REPOSITORY_UUID)
//				.put("branchPath", targetPath.getPath())
//				.build();
//	
//		updateCodeSystem(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME, updateRequest).statusCode(204);
//		getCodeSystem(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME).statusCode(200).body("branchPath", equalTo(targetPath.getPath()));
//	
//		SnomedConcept donatedConceptInExtension1 = getComponent(targetPath, SnomedComponentType.CONCEPT, extensionConceptId1,
//				"descriptions(), relationships()")
//				.statusCode(200)
//				.extract().as(SnomedConcept.class);
//		
//		// validate components of donated concept on extension branch
//		
//		List<SnomedDescription> donatedFsns = donatedConceptInExtension1.getDescriptions().getItems().stream()
//				.filter(d -> d.getTerm().equals(extensionFsnTerm1)).collect(toList());
//
//		assertEquals(1, donatedFsns.size());
//		SnomedDescription donatedFsn = Iterables.getOnlyElement(donatedFsns);
//
//		assertEquals(extensionFsnId1, donatedFsn.getId());
//		assertEquals(Concepts.MODULE_SCT_CORE, donatedFsn.getModuleId());
//
//		List<SnomedDescription> donatedPts = donatedConceptInExtension1.getDescriptions().getItems().stream()
//				.filter(d -> d.getTerm().equals(extensionPtTerm1)).collect(toList());
//
//		assertEquals(1, donatedPts.size());
//		SnomedDescription donatedPt = Iterables.getOnlyElement(donatedPts);
//		
//		assertEquals(extensionPtId1, donatedPt.getId());
//		assertEquals(Concepts.MODULE_SCT_CORE, donatedPt.getModuleId());
//		
//		List<SnomedDescription> additionalSynonyms = donatedConceptInExtension1.getDescriptions().getItems().stream()
//				.filter(d -> d.getTerm().equals(extensionSynonymTerm1)).collect(toList());
//
//		assertEquals(1, additionalSynonyms.size());
//		SnomedDescription additionalSyonym = Iterables.getOnlyElement(additionalSynonyms);
//		
//		assertEquals(extensionSynonymId1, additionalSyonym.getId());
//		assertEquals(Concepts.MODULE_B2I_EXTENSION, additionalSyonym.getModuleId());
//		
//		List<SnomedRelationship> donatedStatedIsas = donatedConceptInExtension1.getRelationships().getItems().stream()
//				.filter(r -> r.getTypeId().equals(Concepts.IS_A) && Concepts.STATED_RELATIONSHIP.equals(r.getCharacteristicTypeId()))
//				.collect(toList());
//		
//		assertEquals(1, donatedStatedIsas.size());
//		SnomedRelationship donatedStatedIsa = Iterables.getOnlyElement(donatedStatedIsas);
//		assertEquals(extensionStatedIsaId1, donatedStatedIsa.getId());
//		assertEquals(Concepts.MODULE_SCT_CORE, donatedStatedIsa.getModuleId());
//		
//		List<SnomedRelationship> donatedInferredIsas = donatedConceptInExtension1.getRelationships().getItems().stream()
//				.filter(r -> r.getTypeId().equals(Concepts.IS_A) && Concepts.INFERRED_RELATIONSHIP.equals(r.getCharacteristicTypeId()))
//				.collect(toList());
//		
//		assertEquals(1, donatedInferredIsas.size());
//		SnomedRelationship donatedInferredIsa = Iterables.getOnlyElement(donatedInferredIsas);
//		assertEquals(extensionInferredIsaId1, donatedInferredIsa.getId());
//		assertEquals(Concepts.MODULE_SCT_CORE, donatedInferredIsa.getModuleId());
//		
//		// validate components of extension concept 2 on extension branch
//
//		SnomedConcept extensionConceptInExtension2 = getComponent(targetPath, SnomedComponentType.CONCEPT, extensionConceptId2,
//				"descriptions(), relationships()")
//				.statusCode(200)
//				.extract().as(SnomedConcept.class);
//		
//		List<SnomedDescription> extensionFsns2 = extensionConceptInExtension2.getDescriptions().getItems().stream()
//				.filter(d -> d.getTerm().equals(extensionFsnTerm2)).collect(toList());
//
//		assertEquals(1, extensionFsns2.size());
//		SnomedDescription extensionFsn2 = Iterables.getOnlyElement(extensionFsns2);
//
//		assertEquals(extensionFsnId2, extensionFsn2.getId());
//		assertEquals(Concepts.MODULE_B2I_EXTENSION, extensionFsn2.getModuleId());
//
//		List<SnomedDescription> extensionPts2 = extensionConceptInExtension2.getDescriptions().getItems().stream()
//				.filter(d -> d.getTerm().equals(extensionPtTerm2)).collect(toList());
//
//		assertEquals(1, extensionPts2.size());
//		SnomedDescription extensionPt2 = Iterables.getOnlyElement(extensionPts2);
//		
//		assertEquals(extensionPtId2, extensionPt2.getId());
//		assertEquals(Concepts.MODULE_B2I_EXTENSION, extensionPt2.getModuleId());
//		
//		List<SnomedDescription> additionalSynonyms2 = extensionConceptInExtension2.getDescriptions().getItems().stream()
//				.filter(d -> d.getTerm().equals(extensionSynonymTerm2)).collect(toList());
//
//		assertEquals(1, additionalSynonyms2.size());
//		SnomedDescription additionalSyonym2 = Iterables.getOnlyElement(additionalSynonyms2);
//		
//		assertEquals(extensionSynonymId2, additionalSyonym2.getId());
//		assertEquals(Concepts.MODULE_B2I_EXTENSION, additionalSyonym2.getModuleId());
//		
//		List<SnomedRelationship> extensionStatedIsas2 = extensionConceptInExtension2.getRelationships().getItems().stream()
//				.filter(r -> r.getTypeId().equals(Concepts.IS_A) && Concepts.STATED_RELATIONSHIP.equals(r.getCharacteristicTypeId()))
//				.collect(toList());
//		
//		assertEquals(1, extensionStatedIsas2.size());
//		SnomedRelationship extensionStatedIsa2 = Iterables.getOnlyElement(extensionStatedIsas2);
//		assertEquals(extensionStatedIsaId2, extensionStatedIsa2.getId());
//		assertEquals(Concepts.MODULE_B2I_EXTENSION, extensionStatedIsa2.getModuleId());
//		
//		List<SnomedRelationship> extensionInferredIsas2 = extensionConceptInExtension2.getRelationships().getItems().stream()
//				.filter(r -> r.getTypeId().equals(Concepts.IS_A) && Concepts.INFERRED_RELATIONSHIP.equals(r.getCharacteristicTypeId()))
//				.collect(toList());
//		
//		assertEquals(1, extensionInferredIsas2.size());
//		SnomedRelationship extensionInferredIsa2 = Iterables.getOnlyElement(extensionInferredIsas2);
//		assertEquals(extensionInferredIsaId2, extensionInferredIsa2.getId());
//		assertEquals(Concepts.MODULE_B2I_EXTENSION, extensionInferredIsa2.getModuleId());
//		
//		List<SnomedRelationship> additionalRelationships = extensionConceptInExtension2.getRelationships().getItems().stream()
//				.filter(r -> Concepts.ADDITIONAL_RELATIONSHIP.equals(r.getCharacteristicTypeId()))
//				.collect(toList());
//		
//		assertEquals(1, additionalRelationships.size());
//		SnomedRelationship additionalRelationship = Iterables.getOnlyElement(additionalRelationships);
//		assertEquals(extensionPartOfId, additionalRelationship.getId());
//		assertEquals(Concepts.MODULE_B2I_EXTENSION, additionalRelationship.getModuleId());
//		assertEquals(extensionConceptId1, additionalRelationship.getDestinationId());
	}
	
//	private Map<String, Object> createConceptRequestBody(String namespace, String moduleId, List<?> relationshipRequestBodies, List<?> descriptionRequestBodies) {
//		return createConceptRequestBody("", namespace, moduleId, relationshipRequestBodies, descriptionRequestBodies);
//	}
//	
//	private Map<String, Object> createConceptRequestBody(String id, String namespace, String moduleId, List<?> relationshipRequestBodies, List<?> descriptionRequestBodies) {
//		
//		Builder<String, Object> builder = ImmutableMap.<String, Object>builder()
//				.put("moduleId", moduleId)
//				.put("namespaceId", Strings.isNullOrEmpty(namespace) ? "" : namespace)
//				.put("descriptions", descriptionRequestBodies)
//				.put("relationships", relationshipRequestBodies)
//				.put("commitComment", "Added concept");
//		
//		if (!Strings.isNullOrEmpty(id)) {
//			builder.put("id", id);
//		}
//		
//		return builder.build();
//		
//	}
//	
//	private Map<String, Object> createDescriptionRequestBody(String namespace, String moduleId, String typeId, String term, Map<String, Acceptability> acceptabilityMap) {
//		return createDescriptionRequestBody("", namespace, moduleId, typeId, term, acceptabilityMap);
//	}
//	
//	private Map<String, Object> createDescriptionRequestBody(String id, String namespace, String moduleId, String typeId, String term, Map<String, Acceptability> acceptabilityMap) {
//		
//		Builder<String, Object> builder = ImmutableMap.<String, Object>builder()
//			.put("moduleId", moduleId)
//			.put("namespaceId", Strings.isNullOrEmpty(namespace) ? "" : namespace)
//			.put("typeId", typeId)
//			.put("term", term)
//			.put("languageCode", DEFAULT_LANGUAGE_CODE)
//			.put("acceptability", acceptabilityMap);
//			
//		if (!Strings.isNullOrEmpty(id)) {
//			builder.put("id", id);
//		}
//		
//		return builder.build();
//	}
//
//	private Map<String, Object> createRelationshipRequestBody(String namespace, String moduleId, String typeId, String destinationId, String characteristicTypeId) {
//		return createRelationshipRequestBody("", namespace, moduleId, typeId, destinationId, characteristicTypeId);
//	}
//	
//	private Map<String, Object> createRelationshipRequestBody(String id, String namespace, String moduleId, String typeId, String destinationId, String characteristicTypeId) {
//		
//		Builder<String, Object> builder = ImmutableMap.<String, Object>builder()
//			.put("moduleId", moduleId)
//			.put("namespaceId", Strings.isNullOrEmpty(namespace) ? "" : namespace)
//			.put("typeId", typeId)
//			.put("destinationId", destinationId)
//			.put("characteristicTypeId", characteristicTypeId);
//		
//		if (!Strings.isNullOrEmpty(id)) {
//			builder.put("id", id);
//		}
//		
//		return builder.build();
//	}
//	
	private String getFirstRelationshipId(SnomedConcept concept, String characteristicTypeId) {
		return concept.getRelationships().getItems().stream().filter(r -> characteristicTypeId.equals(r.getCharacteristicTypeId())).findFirst().get().getId();
	}
	
	private SnomedDescription getFirstMatchingDescription(SnomedConcept extensionConcept, String extensionFsnTerm) {
		return extensionConcept.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionFsnTerm)).findFirst().get();
	}
	
}
