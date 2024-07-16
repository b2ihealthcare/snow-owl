/*
 * Copyright 2011-2024 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.snomed.core.rest.SnomedApiTestConstants.INT_CODESYSTEM;
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.createComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedMergingRestRequests.createMerge;
import static com.b2international.snowowl.snomed.core.rest.SnomedMergingRestRequests.waitForMergeJob;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.*;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.createVersion;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.getLatestVersionId;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.getNextAvailableEffectiveDateAsString;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.assertCreated;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.b2international.commons.json.Json;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.*;
import com.b2international.index.revision.RevisionBranch.BranchState;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.branch.BranchPathUtils;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.codesystem.CodeSystemVersions;
import com.b2international.snowowl.core.codesystem.CodeSystems;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.merge.Merge;
import com.b2international.snowowl.core.request.RepositoryRequest;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.uri.CodeSystemURI;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.*;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests;
import com.b2international.snowowl.snomed.core.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.ModuleRequest.ModuleIdProvider;
import com.b2international.snowowl.test.commons.codesystem.CodeSystemRestRequests;
import com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * @since 4.7
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SnomedExtensionUpgradeTest extends AbstractSnomedExtensionApiTest {

	private CodeSystemURI latestInternationalVersion;

	@Before
	public void setup() {
		latestInternationalVersion = CodeSystemURI.branch(SNOMEDCT, getLatestVersionId(SNOMEDCT));
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
	public void upgrade13SemanticallySameChangesAppliedTwiceBeforeUpgradeAndAfterUpgrade() throws Exception {
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		
		// create new extension version with one new concept and one member on INT concept
		String moduleId = createModule(extension);
		
		SnomedConcept concept = searchConcept(baseInternationalCodeSystem, Map.of("module", Concepts.MODULE_SCT_CORE, "expand", "descriptions()"), 1).stream().findFirst().get();
		String descriptionId = concept.getDescriptions().stream().findFirst().get().getId();
		// create new inactivation indicator for one of the description, pending move
		createMember(extension.getCodeSystemURI(), Map.of(
			"moduleId", moduleId,
			"referenceSetId", Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR,
			"referencedComponentId", descriptionId,
			"valueId", Concepts.PENDING_MOVE
		));
		
		// version extension
		createVersion(extension.getCodeSystemURI().getCodeSystem(), "v1", Dates.now(DateFormats.SHORT)).statusCode(201);		
		
		// add another member on INT representing a change in INT and allow extension to upgrade
		createMember(new CodeSystemURI(SNOMEDCT), Map.of(
			"moduleId", Concepts.MODULE_SCT_CORE,
			"referenceSetId", Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR,
			"referencedComponentId", descriptionId,
			"valueId", Concepts.PENDING_MOVE
		));
		
		String effectiveDate = getNextAvailableEffectiveDateAsString(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveDate, effectiveDate).statusCode(201);
		CodeSystemURI upgradeVersion = CodeSystemURI.branch(SNOMEDCT, effectiveDate);
		
		// start the upgrade
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extension.getCodeSystemURI(), upgradeVersion);
		
		// add another extension member to the same int concept with same meaning to the original extension branch
		createMember(extension.getCodeSystemURI(), Map.of(
			"moduleId", moduleId,
			"referenceSetId", Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR,
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
		getConcept(upgradeCodeSystem.getCodeSystemURI(), concept.getId());
		
	}
	
	@Test
	public void upgrade14CompleteUpgrade() throws Exception {
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
	
	@Test
	public void upgrade15Version() {
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		
		String effectiveDate = getNextAvailableEffectiveDateAsString(INT_CODESYSTEM);
		createVersion(INT_CODESYSTEM, effectiveDate, effectiveDate).statusCode(201);
		
		String newEffectiveDate = getNextAvailableEffectiveDateAsString(INT_CODESYSTEM);
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extension.getCodeSystemURI(), CodeSystemURI.branch(INT_CODESYSTEM, effectiveDate));
		createVersion(upgradeCodeSystem.getShortName(), newEffectiveDate, newEffectiveDate).statusCode(400);
	}
	
	@Test
	public void upgrade16UpgradeFromCodeSystemVersion() {
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		
		// new SI concept
		String newConceptId = createConcept(new CodeSystemURI(SNOMEDCT), createConceptRequestBody(Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE));
		
		// create a new INT version
		String effectiveDate = getNextAvailableEffectiveDateAsString(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveDate, effectiveDate).statusCode(201);
		CodeSystemURI upgradeVersion = CodeSystemURI.branch(SNOMEDCT, effectiveDate);
		
		// version extension
		String effectiveDate2 = Dates.now(DateFormats.SHORT);
		createVersion(extension.getShortName(), effectiveDate2, effectiveDate2).statusCode(201);
		CodeSystemURI extensionVersion = CodeSystemURI.branch(extension.getShortName(), effectiveDate2);
		
		
		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extensionVersion, upgradeVersion);
		assertEquals(upgradeVersion, upgradeCodeSystem.getExtensionOf());
		
		getComponent(upgradeCodeSystem.getCodeSystemURI().toString(), SnomedComponentType.CONCEPT, newConceptId).statusCode(200);
	}
	
	@Test
	public void upgrade17UpgradeFromCodeSystemVersionWithPublishedChanges() {
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		
		// new SI concept
		String newConceptId = createConcept(new CodeSystemURI(SNOMEDCT), createConceptRequestBody(Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE));
		
		// create a new INT version
		String effectiveDate = getNextAvailableEffectiveDateAsString(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveDate, effectiveDate).statusCode(201);
		CodeSystemURI upgradeVersion = CodeSystemURI.branch(SNOMEDCT, effectiveDate);
		
		// new SE concept
		String extensionModuleId = createModule(extension);
		String newConceptId2 = createConcept(extension.getCodeSystemURI(), createConceptRequestBody(Concepts.ROOT_CONCEPT, extensionModuleId));
		
		// version extension
		String effectiveDate2 = getNextAvailableEffectiveDateAsString(extension.getShortName());
		createVersion(extension.getShortName(), effectiveDate2, effectiveDate2).statusCode(201);
		CodeSystemURI extensionVersion = CodeSystemURI.branch(extension.getShortName(), effectiveDate2);
		
		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extensionVersion, upgradeVersion);
		assertEquals(upgradeVersion, upgradeCodeSystem.getExtensionOf());
		
		getComponent(upgradeCodeSystem.getCodeSystemURI().toString(), SnomedComponentType.CONCEPT, newConceptId).statusCode(200);
		getComponent(upgradeCodeSystem.getCodeSystemURI().toString(), SnomedComponentType.CONCEPT, newConceptId2).statusCode(200);
	}
	
	@Test
	public void upgrade18UpgradeFromCodeSystemVersionWithUnpublishedChanges() {
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		
		// new SI concept
		String newConceptId = createConcept(new CodeSystemURI(SNOMEDCT), createConceptRequestBody(Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE));
		
		// create a new INT version
		String effectiveDate = getNextAvailableEffectiveDateAsString(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveDate, effectiveDate).statusCode(201);
		CodeSystemURI upgradeVersion = CodeSystemURI.branch(SNOMEDCT, effectiveDate);
		
		
		// version extension
		String effectiveDate2 = Dates.now(DateFormats.SHORT);
		createVersion(extension.getShortName(), effectiveDate2, effectiveDate2).statusCode(201);
		CodeSystemURI extensionVersion = CodeSystemURI.branch(extension.getShortName(), effectiveDate2);
		
		// new SE concept
		String extensionModuleId = createModule(extension);
		String newConceptId2 = createConcept(extension.getCodeSystemURI(), createConceptRequestBody(Concepts.ROOT_CONCEPT, extensionModuleId));
		
		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extensionVersion, upgradeVersion);
		assertEquals(upgradeVersion, upgradeCodeSystem.getExtensionOf());
		
		getComponent(upgradeCodeSystem.getCodeSystemURI().toString(), SnomedComponentType.CONCEPT, newConceptId).statusCode(200);
		getComponent(upgradeCodeSystem.getCodeSystemURI().toString(), SnomedComponentType.CONCEPT, newConceptId2).statusCode(404);
	}
	
	@Test
	public void upgrade19UpgradeFromCodeSystemVersionWithUnpublishedAndPublishedChanges() {
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		
		// new SI concept
		String newConceptId = createConcept(new CodeSystemURI(SNOMEDCT), createConceptRequestBody(Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE));
		
		// create a new INT version
		String effectiveDate = getNextAvailableEffectiveDateAsString(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveDate, effectiveDate).statusCode(201);
		CodeSystemURI upgradeVersion = CodeSystemURI.branch(SNOMEDCT, effectiveDate);
		
		
		// new SE concept
		String extensionModuleId = createModule(extension);
		String newConceptId2 = createConcept(extension.getCodeSystemURI(), createConceptRequestBody(Concepts.ROOT_CONCEPT, extensionModuleId));
		
		// version extension
		String effectiveDate2 = Dates.now(DateFormats.SHORT);
		createVersion(extension.getShortName(), effectiveDate2, effectiveDate2).statusCode(201);
		CodeSystemURI extensionVersion = CodeSystemURI.branch(extension.getShortName(), effectiveDate2);
		
		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extensionVersion, upgradeVersion);
		assertEquals(upgradeVersion, upgradeCodeSystem.getExtensionOf());
		
		// new SE concept
		String newConceptId3 = createConcept(extension.getCodeSystemURI(), createConceptRequestBody(Concepts.ROOT_CONCEPT, extensionModuleId));
		
		getComponent(upgradeCodeSystem.getCodeSystemURI().toString(), SnomedComponentType.CONCEPT, newConceptId).statusCode(200);
		getComponent(upgradeCodeSystem.getCodeSystemURI().toString(), SnomedComponentType.CONCEPT, newConceptId2).statusCode(200);
		getComponent(upgradeCodeSystem.getCodeSystemURI().toString(), SnomedComponentType.CONCEPT, newConceptId3).statusCode(404);
	}
	
	@Test
	public void upgrade20ExpandUpgradeInfo() throws Exception {
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		
		// new SI concept
		createConcept(new CodeSystemURI(SNOMEDCT), createConceptRequestBody(Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE));
		
		// create a new INT version
		String effectiveDate = getNextAvailableEffectiveDateAsString(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveDate, effectiveDate).statusCode(201);
		CodeSystemURI upgradeVersion = CodeSystemURI.branch(SNOMEDCT, effectiveDate);
		
		
		// new SE concept
		String extensionModuleId = createModule(extension);
		createConcept(extension.getCodeSystemURI(), createConceptRequestBody(Concepts.ROOT_CONCEPT, extensionModuleId));
		
		// version extension
		String effectiveDate2 = Dates.now(DateFormats.SHORT);
		createVersion(extension.getShortName(), effectiveDate2, effectiveDate2).statusCode(201);
		CodeSystemURI extensionVersion = CodeSystemURI.branch(extension.getShortName(), effectiveDate2);
		
		// new SE concept
		createConcept(extension.getCodeSystemURI(), createConceptRequestBody(Concepts.ROOT_CONCEPT, extensionModuleId));
		
		// version extension
		String effectiveDate3 =  getNextAvailableEffectiveDateAsString(extension.getShortName());
		createVersion(extension.getShortName(), effectiveDate3, effectiveDate3).statusCode(201);
		CodeSystemURI extensionVersion2 = CodeSystemURI.branch(extension.getShortName(), effectiveDate3);
		
		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extensionVersion, upgradeVersion);
		
		CodeSystems expandedCodeSystems = CodeSystemRestRequests.search(upgradeCodeSystem.getShortName(), CodeSystem.Expand.UPGRADE_INFO + "()").extract().as(CodeSystems.class);
		
		assertEquals(upgradeVersion, upgradeCodeSystem.getExtensionOf());
		assertThat(expandedCodeSystems.first().get().getUpgradeInfo().getAvailableVersions()).doesNotContainSequence(extensionVersion);
		assertThat(expandedCodeSystems.first().get().getUpgradeInfo().getAvailableVersions()).contains(extensionVersion2);
	}
	
	@Test
	public void upgrade21ExpandUpgradeInfo() throws Exception {
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		
		// new SI concept
		createConcept(new CodeSystemURI(SNOMEDCT), createConceptRequestBody(Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE));
		
		// create a new INT version
		String effectiveDate = getNextAvailableEffectiveDateAsString(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveDate, effectiveDate).statusCode(201);
		CodeSystemURI upgradeVersion = CodeSystemURI.branch(SNOMEDCT, effectiveDate);
		
		
		// new SE concept
		String extensionModuleId = createModule(extension);
		createConcept(extension.getCodeSystemURI(), createConceptRequestBody(Concepts.ROOT_CONCEPT, extensionModuleId));
		
		// version extension
		String effectiveDate2 = Dates.now(DateFormats.SHORT);
		createVersion(extension.getShortName(), effectiveDate2, effectiveDate2).statusCode(201);
		CodeSystemURI extensionVersion = CodeSystemURI.branch(extension.getShortName(), effectiveDate2);
		
		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extensionVersion, upgradeVersion);
		
		// new SE concept
		createConcept(extension.getCodeSystemURI(), createConceptRequestBody(Concepts.ROOT_CONCEPT, extensionModuleId));
		
		// version extension
		String effectiveDate3 =  getNextAvailableEffectiveDateAsString(extension.getShortName());
		createVersion(extension.getShortName(), effectiveDate3, effectiveDate3).statusCode(201);
		CodeSystemURI extensionVersion2 = CodeSystemURI.branch(extension.getShortName(), effectiveDate3);
		
		CodeSystems expandedCodeSystems = CodeSystemRestRequests.search(upgradeCodeSystem.getShortName(), CodeSystem.Expand.UPGRADE_INFO + "()").extract().as(CodeSystems.class);
		
		assertEquals(upgradeVersion, upgradeCodeSystem.getExtensionOf());
		assertThat(expandedCodeSystems.first().get().getUpgradeInfo().getAvailableVersions()).doesNotContainSequence(extensionVersion);
		assertThat(expandedCodeSystems.first().get().getUpgradeInfo().getAvailableVersions()).contains(extensionVersion2);
	}
	
	@Test
	public void upgrade22UpgradeSychronizeHead() throws Exception {
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		
		// new SI concept
		createConcept(new CodeSystemURI(SNOMEDCT), createConceptRequestBody(Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE));
		
		// create a new INT version
		String effectiveDate = getNextAvailableEffectiveDateAsString(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveDate, effectiveDate).statusCode(201);
		CodeSystemURI upgradeVersion = CodeSystemURI.branch(SNOMEDCT, effectiveDate);
		
		
		// new SE concept
		String extensionModuleId = createModule(extension);
		createConcept(extension.getCodeSystemURI(), createConceptRequestBody(Concepts.ROOT_CONCEPT, extensionModuleId));
		
		// version extension
		String effectiveDate2 = Dates.now(DateFormats.SHORT);
		createVersion(extension.getShortName(), effectiveDate2, effectiveDate2).statusCode(201);
		CodeSystemURI extensionVersion = CodeSystemURI.branch(extension.getShortName(), effectiveDate2);
		
		// new SE concept
		createConcept(extension.getCodeSystemURI(), createConceptRequestBody(Concepts.ROOT_CONCEPT, extensionModuleId));
		
		// version extension
		String effectiveDate3 =  getNextAvailableEffectiveDateAsString(extension.getShortName());
		createVersion(extension.getShortName(), effectiveDate3, effectiveDate3).statusCode(201);
		CodeSystemURI extensionVersion2 = CodeSystemURI.branch(extension.getShortName(), effectiveDate3);
		
		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extensionVersion, upgradeVersion);
		
		CodeSystems expandedCodeSystems = CodeSystemRestRequests.search(upgradeCodeSystem.getShortName(), CodeSystem.Expand.UPGRADE_INFO + "()").extract().as(CodeSystems.class);
		
		assertEquals(upgradeVersion, upgradeCodeSystem.getExtensionOf());
		assertThat(expandedCodeSystems.first().get().getUpgradeInfo().getAvailableVersions()).doesNotContainSequence(extensionVersion);
		assertThat(expandedCodeSystems.first().get().getUpgradeInfo().getAvailableVersions()).contains(extensionVersion2);
		
		IBranchPath extensionBranch = BranchPathUtils.createPath(extension.getBranchPath());
		IBranchPath upgradeBranch = BranchPathUtils.createPath(upgradeCodeSystem.getBranchPath());
		merge(extensionBranch, upgradeBranch, "Merged new concept from child branch").body("status", equalTo(Merge.Status.COMPLETED.name()));	
		
		Boolean result = CodeSystemRequests.prepareUpgradeSynchronization(upgradeCodeSystem.getCodeSystemURI(), extension.getCodeSystemURI())
			.build(upgradeCodeSystem.getRepositoryId())
			.execute(getBus())
			.getSync(1, TimeUnit.MINUTES);
		assertTrue(result);
		
		CodeSystems expandedCodeSystemsAfterMerge = CodeSystemRestRequests.search(upgradeCodeSystem.getShortName(), CodeSystem.Expand.UPGRADE_INFO + "()").extract().as(CodeSystems.class);
		assertThat(expandedCodeSystemsAfterMerge.first().get().getUpgradeInfo().getAvailableVersions()).isEmpty();
	}
	
	@Test
	public void upgrade23UpgradeSychronizeVersion() throws Exception {
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());
		
		// new SI concept
		createConcept(new CodeSystemURI(SNOMEDCT), createConceptRequestBody(Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE));
		
		// create a new INT version
		String effectiveDate = getNextAvailableEffectiveDateAsString(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveDate, effectiveDate).statusCode(201);
		CodeSystemURI upgradeVersion = CodeSystemURI.branch(SNOMEDCT, effectiveDate);
		
		
		// new SE concept
		String extensionModuleId = createModule(extension);
		createConcept(extension.getCodeSystemURI(), createConceptRequestBody(Concepts.ROOT_CONCEPT, extensionModuleId));
		
		// version extension
		String effectiveDate2 = Dates.now(DateFormats.SHORT);
		createVersion(extension.getShortName(), effectiveDate2, effectiveDate2).statusCode(201);
		CodeSystemURI extensionVersion = CodeSystemURI.branch(extension.getShortName(), effectiveDate2);
		
		// new SE concept
		createConcept(extension.getCodeSystemURI(), createConceptRequestBody(Concepts.ROOT_CONCEPT, extensionModuleId));
		
		// version extension
		String effectiveDate3 =  getNextAvailableEffectiveDateAsString(extension.getShortName());
		createVersion(extension.getShortName(), effectiveDate3, effectiveDate3).statusCode(201);
		CodeSystemURI extensionVersion2 = CodeSystemURI.branch(extension.getShortName(), effectiveDate3);
		
		// new SE concept
		createConcept(extension.getCodeSystemURI(), createConceptRequestBody(Concepts.ROOT_CONCEPT, extensionModuleId));
		
		// version extension
		String effectiveDate4 =  getNextAvailableEffectiveDateAsString(extension.getShortName());
		createVersion(extension.getShortName(), effectiveDate4, effectiveDate4).statusCode(201);
		CodeSystemURI extensionVersion3 = CodeSystemURI.branch(extension.getShortName(), effectiveDate4);
		
		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extensionVersion, upgradeVersion);
		
		CodeSystems expandedCodeSystems = CodeSystemRestRequests.search(upgradeCodeSystem.getShortName(), CodeSystem.Expand.UPGRADE_INFO + "()").extract().as(CodeSystems.class);
		
		assertEquals(upgradeVersion, upgradeCodeSystem.getExtensionOf());
		assertThat(expandedCodeSystems.first().get().getUpgradeInfo().getAvailableVersions()).doesNotContainSequence(extensionVersion);
		assertThat(expandedCodeSystems.first().get().getUpgradeInfo().getAvailableVersions()).contains(extensionVersion2);
		
		Boolean result = CodeSystemRequests.prepareUpgradeSynchronization(upgradeCodeSystem.getCodeSystemURI(), extensionVersion2)
				.build(upgradeCodeSystem.getRepositoryId())
				.execute(getBus())
				.getSync(1, TimeUnit.MINUTES);
		assertTrue(result);
		
		CodeSystems expandedCodeSystemsAfterMerge = CodeSystemRestRequests.search(upgradeCodeSystem.getShortName(), CodeSystem.Expand.UPGRADE_INFO + "()").extract().as(CodeSystems.class);
		assertThat(expandedCodeSystemsAfterMerge.first().get().getUpgradeInfo().getAvailableVersions()).containsOnly(extensionVersion3);
	}
	
	@Test
	public void upgrade24UpgradeWithEmptyExtension() throws Exception {
		
		// new SI concept
		createConcept(new CodeSystemURI(SNOMEDCT), createConceptRequestBody(Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE));
		
		// create a new INT version
		String effectiveDate1 = getNextAvailableEffectiveDateAsString(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveDate1, effectiveDate1).statusCode(201);
		CodeSystemURI upgradeVersion1 = CodeSystemURI.branch(SNOMEDCT, effectiveDate1);
		
		// new SI concept
		String conceptId = createConcept(new CodeSystemURI(SNOMEDCT), createConceptRequestBody(Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE));
		
		// create a new INT version
		String effectiveDate2 = getNextAvailableEffectiveDateAsString(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveDate2, effectiveDate2).statusCode(201);
		CodeSystemURI upgradeVersion2 = CodeSystemURI.branch(SNOMEDCT, effectiveDate2);
		
		// new SI concept
		createConcept(new CodeSystemURI(SNOMEDCT), createConceptRequestBody(Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE));
		
		// create a new INT version
		String effectiveDate3 = getNextAvailableEffectiveDateAsString(SNOMEDCT);
		createVersion(SNOMEDCT, effectiveDate3, effectiveDate3).statusCode(201);
		CodeSystemURI upgradeVersion3 = CodeSystemURI.branch(SNOMEDCT, effectiveDate3);
		
		// create extension on the latest SI VERSION
		CodeSystem extension = createExtension(upgradeVersion1, branchPath.lastSegment());
		
		// start upgrade to the new available upgrade version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extension.getCodeSystemURI(), upgradeVersion2);
		
		assertState(upgradeCodeSystem.getBranchPath(), extension.getBranchPath(), BranchState.FORWARD);
		
		Boolean result = CodeSystemRequests.prepareUpgradeSynchronization(upgradeCodeSystem.getCodeSystemURI(), extension.getCodeSystemURI())
				.build(upgradeCodeSystem.getRepositoryId())
				.execute(getBus())
				.getSync(1, TimeUnit.MINUTES);
		assertTrue(result);
		
		assertState(upgradeCodeSystem.getBranchPath(), extension.getBranchPath(), BranchState.FORWARD);
		
		Boolean successComplete = CodeSystemRequests.prepareComplete(upgradeCodeSystem.getShortName())
			.build(upgradeCodeSystem.getRepositoryId())
			.execute(getBus())
			.getSync(1, TimeUnit.MINUTES);
		assertTrue(successComplete);
		
		getComponent(extension.getCodeSystemURI().toString(), SnomedComponentType.CONCEPT, conceptId).statusCode(200);
	}
	
	/**
	 * This test is to detect and verify a bug in the
	 * {@link RevisionBranch#difference(RevisionBranch)} (more precisely in the
	 * RevisionBranchRef#difference method). The problem was that the system used
	 * SortedSet for {@link RevisionSegment} and built on the fact that branches can
	 * be always sortable by both time and space. This is true most of the time, but
	 * when synchronizing old content from earlier branches this might result in
	 * incorrect conflict reporting behavior due to incorrect branch segment
	 * diffing. The resulting branch diff contains incorrect branch segments, thus
	 * the branch compare algorithm fetches irrelevant, already merged commits,
	 * which are being reported as conflicts in the system (both sides added the
	 * same component, which is also incorrect).
	 */
	@Test
	public void upgrade25UpgradeExtensionUsingExistingInternationalVersions() {
		CodeSystemVersions allVersions = CodeSystemVersionRestRequests.getVersions(SNOMEDCT);
		String secondLatestSIVersion = allVersions.getItems().get(allVersions.getItems().size() - 2).getVersion();
		String latestSIVersion = Iterables.getLast(allVersions).getVersion();
		
		// Create extension with one before latest SI version
		CodeSystem extension = createExtension(CodeSystemURI.branch(SNOMEDCT, secondLatestSIVersion), branchPath.lastSegment());
		String extensionModuleId = createModule(extension);
		
		// Version extension
		String firstExtensionVersion = getNextAvailableEffectiveDateAsString(extension.getShortName());
		createVersion(extension.getShortName(), firstExtensionVersion, firstExtensionVersion).statusCode(201);
		
		// Start upgrade to the newer SI version
		CodeSystemURI upgradeVersion = CodeSystemURI.branch(SNOMEDCT, latestSIVersion);
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extension.getCodeSystemURI(), upgradeVersion);
		assertEquals(upgradeVersion, upgradeCodeSystem.getExtensionOf());
		
		//Create new concept on the extension code system
		String newEXTConceptId2 = createConcept(extension.getCodeSystemURI(), createConceptRequestBody(Concepts.ROOT_CONCEPT, extensionModuleId));

		//Create concept on UPL
		String newUPLConceptId1 = createConcept(upgradeCodeSystem.getCodeSystemURI(), createConceptRequestBody(Concepts.ROOT_CONCEPT, extensionModuleId));

		//Sync
		Boolean result = CodeSystemRequests.prepareUpgradeSynchronization(upgradeCodeSystem.getCodeSystemURI(), extension.getCodeSystemURI())
				.build(upgradeCodeSystem.getRepositoryId())
				.execute(getBus())
				.getSync(100, TimeUnit.MINUTES);
		assertTrue(result);
	}
	
	@Test
	public void upgrade29AcceptableSynonymChangeVSRevertedInferredOnlyAncestorChange() throws Exception {
		// prepare extension on the second latest int version
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());

		// alphaproteobacteria
		String intConceptId = "413858005";
		// using an existing ancestors as direct parent
		String inferredExtensionRelationship = createRelationship(extension.getCodeSystemURI(), createRelationshipRequestBody(intConceptId, Concepts.IS_A, "410607006", Concepts.INFERRED_RELATIONSHIP));

		// then immediately delete it on the extension
		SnomedComponentRestRequests.deleteComponent(
			extension.getBranchPath(), 
			SnomedComponentType.RELATIONSHIP, 
			inferredExtensionRelationship,
			false
		).assertThat().statusCode(204);
		
		// verify that inferred relationship does not exist anymore on the extension branch
		getComponent(extension.getBranchPath(), SnomedComponentType.RELATIONSHIP, inferredExtensionRelationship).statusCode(404);

		// create an acceptable synonym on the international edition and version it
		String acceptableSynonymId = createDescription(new CodeSystemURI(SNOMEDCT), createDescriptionRequestBody(intConceptId, Concepts.SYNONYM, Concepts.MODULE_SCT_CORE, ImmutableMap.of(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.ACCEPTABLE, Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.ACCEPTABLE)));

		// version INT
		String newSIVersion = getNextAvailableEffectiveDateAsString(SNOMEDCT);
		createVersion(SNOMEDCT, newSIVersion, newSIVersion).statusCode(201);

		// create upgrade to the latest SI version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extension.getCodeSystemURI(), CodeSystemURI.branch(SNOMEDCT, newSIVersion)); 

		// on the upgrade branch, the INT concept should be visible
		SnomedConcept conceptOnUpgradeBranch = getConcept(upgradeCodeSystem.getCodeSystemURI(), intConceptId, "descriptions()");
		assertThat(conceptOnUpgradeBranch.getParentIdsAsString())
			.doesNotContain(Concepts.CONCEPT_MODEL_OBJECT_ATTRIBUTE);
		assertThat(conceptOnUpgradeBranch.getDescriptions())
			.extracting(SnomedDescription::getId)
			.contains(acceptableSynonymId);
	}
	
	@Test
	public void upgrade30DuplicateDescription() {
		CodeSystemVersions allVersions = CodeSystemVersionRestRequests.getVersions(SNOMEDCT);
		String secondLatestSIVersion = allVersions.getItems().get(allVersions.getItems().size() - 2).getVersion();
		String latestSIVersion = Iterables.getLast(allVersions).getVersion();

		// Create extension with one before latest SI version
		CodeSystem extensionCodeSystem = createExtension(CodeSystemURI.branch(SNOMEDCT, secondLatestSIVersion), branchPath.lastSegment());
		IBranchPath extensionPath = BranchPathUtils.createPath(extensionCodeSystem.getBranchPath());
		String moduleId = createModule(extensionCodeSystem);
		
		// Create simple type reference set
		String parentConceptId = SnomedRefSetUtil.getParentConceptId(SnomedRefSetType.SIMPLE);
		Map<?, ?> refSetRequestBody = Json.assign(
			createConceptRequestBody(parentConceptId, moduleId),
			Json.object(
				"namespaceId", Concepts.B2I_NAMESPACE,
				"type", SnomedRefSetType.SIMPLE,
				"referencedComponentType", SnomedTerminologyComponentConstants.DESCRIPTION,
				"commitComment", "Created new reference set"
			)
		);

		String refSetId = assertCreated(createComponent(extensionPath, SnomedComponentType.REFSET, refSetRequestBody));

		// Add a member to an existing INT description
		String descriptionId = "517382016"; // FSN of "SNOMED CT Concept"
		Map<?, ?> memberRequestBody = Json.assign(createRefSetMemberRequestBody(refSetId, descriptionId, moduleId, true))
			.with("commitComment", "Created new reference set member");

		String memberId = assertCreated(createComponent(extensionPath, SnomedComponentType.MEMBER, memberRequestBody));
		
		// Version extension
		String firstExtensionVersion = getNextAvailableEffectiveDateAsString(extensionCodeSystem.getShortName());
		createVersion(extensionCodeSystem.getShortName(), firstExtensionVersion, firstExtensionVersion).statusCode(201);

		// Inactivate member on the extension branch after versioning
		inactivateMember(extensionPath, memberId);

		// Start upgrade of extension version to the newer SI version -- the inactivation should not be part of this
		CodeSystemURI upgradeVersion = CodeSystemURI.branch(SNOMEDCT, latestSIVersion);
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(CodeSystemURI.branch(extensionCodeSystem.getShortName(), firstExtensionVersion), upgradeVersion);
		assertEquals(upgradeVersion, upgradeCodeSystem.getExtensionOf());
		
		// Check that at this point we only see a single revision of the description
		SnomedDescriptions descriptions = SnomedComponentRestRequests.searchComponent(
			upgradeCodeSystem.getBranchPath(), 
			SnomedComponentType.DESCRIPTION, 
			Json.object(
				"id", descriptionId, 
				"limit", 2
			)
		).assertThat()
		.statusCode(200)
		.extract()
		.as(SnomedDescriptions.class);
		
		assertThat(descriptions.getTotal()).isEqualTo(1);
		
		// Reactivate member on the extension branch
		reactivateMember(extensionPath, memberId);
		
		// Sync unversioned content
		Boolean result = CodeSystemRequests.prepareUpgradeSynchronization(upgradeCodeSystem.getCodeSystemURI(), extensionCodeSystem.getCodeSystemURI())
			.build(upgradeCodeSystem.getRepositoryId())
			.execute(getBus())
			.getSync(1, TimeUnit.MINUTES);

		assertTrue(result);
		
		// Check description count again
		descriptions = SnomedComponentRestRequests.searchComponent(
			upgradeCodeSystem.getBranchPath(), 
			SnomedComponentType.DESCRIPTION, 
			Json.object(
				"id", descriptionId, 
				"limit", 2
			)
		).assertThat()
		.statusCode(200)
		.extract()
		.as(SnomedDescriptions.class);
			
		assertThat(descriptions.getTotal()).isEqualTo(1);
	}
	
	@Test
	public void upgrade31DuplicateDescriptionAfterDerivedPropertyToggle() {
		CodeSystemVersions allVersions = CodeSystemVersionRestRequests.getVersions(SNOMEDCT);
		String secondLatestSIVersion = allVersions.getItems().get(allVersions.getItems().size() - 2).getVersion();
		String latestSIVersion = Iterables.getLast(allVersions).getVersion();

		// Create extension with one before latest SI version
		CodeSystem extensionCodeSystem = createExtension(CodeSystemURI.branch(SNOMEDCT, secondLatestSIVersion), branchPath.lastSegment());
		IBranchPath extensionPath = BranchPathUtils.createPath(extensionCodeSystem.getBranchPath());
		String extensionModuleId = createModule(extensionCodeSystem);
		
		//Create a concept on the extension code system
		String extensionConceptId = createConcept(extensionCodeSystem.getCodeSystemURI(), createConceptRequestBody(Concepts.ROOT_CONCEPT, extensionModuleId)
				.with("namespaceId", Concepts.B2I_NAMESPACE));
		// Add a synonym to the concept
		String descriptionId = createDescription(extensionCodeSystem.getCodeSystemURI(), createDescriptionRequestBody(extensionConceptId)
				.with("moduleId", extensionModuleId)
				.with("namespaceId", Concepts.B2I_NAMESPACE)
				.with("term", "Example Synonym"));
				
		// Version the extension
		String firstExtensionVersion = getNextAvailableEffectiveDateAsString(extensionCodeSystem.getShortName());
		createVersion(extensionCodeSystem.getShortName(), firstExtensionVersion, firstExtensionVersion).statusCode(201);
				
		//Create "Task" branch
		IBranchPath taskBranchPath = BranchPathUtils.createPath(extensionPath, "4170");
		branching.createBranch(taskBranchPath).statusCode(201);
		
		//Change the description on the task branch by modifying derived properties like memberOf/activeMemberOf
		@SuppressWarnings("deprecation")
		RevisionIndex index = ApplicationContext.getServiceForClass(RepositoryManager.class)
			.get(SnomedDatastoreActivator.REPOSITORY_UUID)
			.service(RevisionIndex.class);
		
		SnomedDescriptionIndexEntry releasedRevision = index.read(taskBranchPath.getPath(), reader -> {
			Query<SnomedDescriptionIndexEntry> query = Query.select(SnomedDescriptionIndexEntry.class)
					.from(SnomedDescriptionIndexEntry.class)
					.where(Expressions.builder()
							.filter(SnomedDescriptionIndexEntry.Expressions.released(true))
							.filter(SnomedDescriptionIndexEntry.Expressions.id(descriptionId))
							.build())
					.build();
			return Iterables.getOnlyElement(reader.search(query), null);
		});
		
		SnomedDescriptionIndexEntry revisionModifiedOnTask = SnomedDescriptionIndexEntry.builder(releasedRevision)
				.memberOf(Collections.emptySet())
				.activeMemberOf(Collections.emptySet())
				.build();
		
		new RepositoryRequest<>(SnomedDatastoreActivator.REPOSITORY_UUID, context -> {
			index.prepareCommit(taskBranchPath.getPath())
				.stageNew(revisionModifiedOnTask)
				.withContext(context.inject().bind(ModuleIdProvider.class, c -> c.getModuleId()).build())
				.commit(ApplicationContext.getServiceForClass(TimestampProvider.class).getTimestamp(), "test", "Apply non-structural change to description " + descriptionId);
			return null;
		}).execute(ApplicationContext.getServiceForClass(Environment.class));
		
		//Create the second extension version
		Json properties = createConceptRequestBody(Concepts.ROOT_CONCEPT, extensionModuleId).with("namespaceId", Concepts.B2I_NAMESPACE);
		createConcept(extensionCodeSystem.getCodeSystemURI(), properties); //Unrelated content, to have something to version
		
		String secondExtensionVersion = getNextAvailableEffectiveDateAsString(extensionCodeSystem.getShortName());
		createVersion(extensionCodeSystem.getShortName(), secondExtensionVersion, secondExtensionVersion).statusCode(201);
		String secondVersionPath = extensionCodeSystem.getRelativeBranchPath(secondExtensionVersion);
		
		//Revision visible here is the versioned one from the extension code system, which has been revised by the changes on the task branch
		index.read(secondVersionPath, reader -> {
			Query<JsonNode> query = Query.select(JsonNode.class)
				.from(SnomedDescriptionIndexEntry.class)
				.where(Expressions.builder()
						.filter(SnomedDescriptionIndexEntry.Expressions.id(descriptionId))
						.build())
				.build();
		
			List<JsonNode> hits = reader.search(query).getHits();
			assertThat(hits).hasSize(1);
			return null;
		});
		
		//Promote the task branch
		final String promoteLocation = createMerge(taskBranchPath.getPath(), extensionPath.getPath(), "Merge task branch into extension code system", true)
				.statusCode(202)
				.extract()
				.header("Location");
		waitForMergeJob(lastPathSegment(promoteLocation));
		
		//Create upgrade from second version of extension and latest INT version
		CodeSystemURI upgradeVersion = CodeSystemURI.branch(SNOMEDCT, latestSIVersion);
		String upgradeCodeSystemId = CodeSystemRequests.prepareUpgrade(CodeSystemURI.branch(extensionCodeSystem.getShortName(), secondExtensionVersion), upgradeVersion)
			.build(extensionCodeSystem.getRepositoryId())
			.execute(getBus())
			.getSync();
		CodeSystem upgradeCodeSystem = CodeSystemRestRequests.getCodeSystem(upgradeCodeSystemId).extract().as(CodeSystem.class);
		assertEquals(upgradeVersion, upgradeCodeSystem.getExtensionOf());
		index.read(upgradeCodeSystem.getBranchPath(), reader -> {
			Query<JsonNode> query = Query.select(JsonNode.class)
				.from(SnomedDescriptionIndexEntry.class)
				.where(Expressions.builder()
						.filter(SnomedDescriptionIndexEntry.Expressions.id(descriptionId))
						.build())
				.build();
			
			List<JsonNode> hits = reader.search(query).getHits();
			assertThat(hits).hasSize(1);
			return null;
		});
		
		//Synchronize the latest extension code system state with the upgrade code system
		Boolean result = CodeSystemRequests.prepareUpgradeSynchronization(upgradeCodeSystem.getCodeSystemURI(), extensionCodeSystem.getCodeSystemURI())
				.build(upgradeCodeSystem.getRepositoryId())
				.execute(getBus())
				.getSync();
		assertTrue(result);
		assertState(upgradeCodeSystem.getBranchPath(), extensionCodeSystem.getBranchPath(), BranchState.FORWARD);
		
		index.read(upgradeCodeSystem.getBranchPath(), reader -> {
			Query<JsonNode> query = Query.select(JsonNode.class)
				.from(SnomedDescriptionIndexEntry.class)
				.where(Expressions.builder()
						.filter(SnomedDescriptionIndexEntry.Expressions.id(descriptionId))
						.build())
				.build();
			
			List<JsonNode> hits = reader.search(query).getHits();
			assertThat(hits).hasSize(1);
			return null;
		});
		
		//Check that there are no duplicates on the upgrade branch
		getDescription(upgradeCodeSystem.getCodeSystemURI(), descriptionId);	
	}
	
	@Test
	public void upgrade32InactivateRelationshipOnIntAndExt() throws Exception {
		// prepare extension on the latest int version
		CodeSystem extension = createExtension(latestInternationalVersion, branchPath.lastSegment());

		//Create international concept with a relationship
		String intConceptId = createConcept(new CodeSystemURI(SNOMEDCT), createConceptRequestBody(Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE)
				.with("namespaceId", ""));
		
		
		IBranchPath mainPath = BranchPathUtils.createMainPath();
		String relationshipId = createNewRelationship(mainPath, intConceptId, Concepts.IS_A, Concepts.PROCEDURE_SITE_DIRECT);

		//Create INT version
		String intVersion1 = getNextAvailableEffectiveDateAsString(SNOMEDCT);
		createVersion(SNOMEDCT, intVersion1, intVersion1).statusCode(201);
		
		// version INT
		String firstSIVersion = getNextAvailableEffectiveDateAsString(SNOMEDCT);
		createVersion(SNOMEDCT, firstSIVersion, firstSIVersion).statusCode(201);

		// create upgrade to the latest SI version
		CodeSystem upgradeCodeSystem = createExtensionUpgrade(extension.getCodeSystemURI(), CodeSystemURI.branch(SNOMEDCT, firstSIVersion));
		
		//Inactivate relationship on EXT
		inactivateRelationship(BranchPathUtils.createPath(upgradeCodeSystem.getBranchPath()), relationshipId);
		
		Boolean success = CodeSystemRequests.prepareComplete(upgradeCodeSystem.getShortName())
				.build(upgradeCodeSystem.getRepositoryId())
				.execute(getBus())
				.getSync(1, TimeUnit.MINUTES);
		assertTrue(success);
		
		//Create new version after upgrade
		String extVersion = getNextAvailableEffectiveDateAsString(extension.getShortName());
		createVersion(extension.getShortName(), extVersion, extVersion).statusCode(201);
		
		//Inactivate relationship on INT
		inactivateRelationship(mainPath, relationshipId);
		
		// version INT & upgrade EXT again
		String secondSIVersion = getNextAvailableEffectiveDateAsString(SNOMEDCT);
		createVersion(SNOMEDCT, secondSIVersion, secondSIVersion).statusCode(201);
		
		CodeSystem secondUpgradeCodeSystem = createExtensionUpgrade(extension.getCodeSystemURI(), CodeSystemURI.branch(SNOMEDCT, secondSIVersion));
		Boolean result = CodeSystemRequests.prepareComplete(secondUpgradeCodeSystem.getShortName())
				.build(upgradeCodeSystem.getRepositoryId())
				.execute(getBus())
				.getSync(1, TimeUnit.MINUTES);
		assertTrue(result);
		
		// Activate relationship on INT
		activateRelationship(mainPath, relationshipId);
		
		//Version INT and upgrade EXT again
		String thirdSIVersion = getNextAvailableEffectiveDateAsString(SNOMEDCT);
		createVersion(SNOMEDCT, thirdSIVersion, thirdSIVersion).statusCode(201);
		
		CodeSystem thirdUpgradeCodeSystem = createExtensionUpgrade(extension.getCodeSystemURI(), CodeSystemURI.branch(SNOMEDCT, thirdSIVersion));
	}

	
	private void assertState(String branchPath, String compareWith, BranchState expectedState) {
		BaseRevisionBranching branching = ApplicationContext.getServiceForClass(RepositoryManager.class).get(SnomedDatastoreActivator.REPOSITORY_UUID).service(BaseRevisionBranching.class);
		assertEquals(expectedState, branching.getBranchState(branchPath, compareWith));
	}
	
	private String getFirstRelationshipId(SnomedConcept concept, String characteristicTypeId) {
		return concept.getRelationships().getItems().stream().filter(r -> characteristicTypeId.equals(r.getCharacteristicTypeId())).findFirst().get().getId();
	}
	
	private SnomedDescription getFirstMatchingDescription(SnomedConcept extensionConcept, String extensionFsnTerm) {
		return extensionConcept.getDescriptions().getItems().stream().filter(d -> d.getTerm().equals(extensionFsnTerm)).findFirst().get();
	}
	
}
