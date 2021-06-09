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
package com.b2international.snowowl.snomed.core.rest.components;

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.*;
import static com.b2international.snowowl.snomed.core.rest.SnomedRefSetRestRequests.bulkUpdateMembers;
import static com.b2international.snowowl.snomed.core.rest.SnomedRefSetRestRequests.updateRefSetComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.*;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemRestRequests.createCodeSystem;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.createCodeSystemAndVersion;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.createVersion;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.getNextAvailableEffectiveDate;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.assertCreated;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.Test;

import com.b2international.commons.exceptions.ConflictException;
import com.b2international.commons.json.Json;
import com.b2international.index.compat.TextConstants;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.TimestampProvider;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.branch.BranchPathUtils;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.bulk.BulkRequest;
import com.b2international.snowowl.core.events.bulk.BulkRequestBuilder;
import com.b2international.snowowl.core.request.RepositoryRequest;
import com.b2international.snowowl.core.request.TermFilter;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.cis.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.cis.domain.IdentifierStatus;
import com.b2international.snowowl.snomed.cis.domain.SctId;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.AssociationTarget;
import com.b2international.snowowl.snomed.core.domain.InactivationProperties;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.core.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.core.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.core.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.ModuleRequest.ModuleIdProvider;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.Iterables;

import io.restassured.http.ContentType;

/**
 * @since 2.0
 */
public class SnomedDescriptionApiTest extends AbstractSnomedApiTest {

	private static final String ROOT_DESCRIPTION_ID = "2913224013";

	@Test
	public void createDescriptionNonExistentBranch() {
		Json requestBody = createDescriptionRequestBody(Concepts.ROOT_CONCEPT)
				.with("commitComment", "Created new description on non-existent branch");

		createComponent(BranchPathUtils.createPath("MAIN/x/y/z"), SnomedComponentType.DESCRIPTION, requestBody).statusCode(404);
	}

	@Test
	public void createDescriptionInvalidConcept() {
		Json requestBody = createDescriptionRequestBody("11110000")
				.with("commitComment", "Created new description with invalid conceptId");

		createComponent(branchPath, SnomedComponentType.DESCRIPTION, requestBody).statusCode(400);
	}

	@Test
	public void createDescriptionInvalidType() {
		Json requestBody = createDescriptionRequestBody(Concepts.ROOT_CONCEPT, "11110000")
				.with("commitComment", "Created new description with invalid typeId");

		createComponent(branchPath, SnomedComponentType.DESCRIPTION, requestBody).statusCode(400);
	}

	@Test
	public void createDescriptionInvalidModule() {
		Json requestBody = createDescriptionRequestBody(Concepts.ROOT_CONCEPT, Concepts.SYNONYM, "11110000")
				.with("commitComment", "Created new description with invalid moduleId");

		createComponent(branchPath, SnomedComponentType.DESCRIPTION, requestBody).statusCode(400);
	}

	@Test
	public void createDescriptionWithoutCommitComment() {
		Json requestBody = createDescriptionRequestBody(Concepts.ROOT_CONCEPT);
		createComponent(branchPath, SnomedComponentType.DESCRIPTION, requestBody).statusCode(400);
	}

	@Test
	public void createDescription() {
		Json requestBody = createDescriptionRequestBody(Concepts.ROOT_CONCEPT)
				.with("commitComment", "Created new description");

		createComponent(branchPath, SnomedComponentType.DESCRIPTION, requestBody).statusCode(201);
	}

	@Test
	public void createDescriptionWithReservedId() {
		ISnomedIdentifierService identifierService = getServiceForClass(ISnomedIdentifierService.class);
		String descriptionId = Iterables.getOnlyElement(identifierService.reserve(null, ComponentCategory.DESCRIPTION, 1));

		Json requestBody = createDescriptionRequestBody(Concepts.ROOT_CONCEPT)
				.with("id", descriptionId)
				.with("commitComment", "Created new description with reserved identifier");

		final String createdDescriptionId = assertCreated(createComponent(branchPath, SnomedComponentType.DESCRIPTION, requestBody));
		assertEquals(descriptionId, createdDescriptionId);
		
		SctId descriptionSctId = SnomedRequests.identifiers().prepareGet()
				.setComponentId(descriptionId)
				.buildAsync()
				.execute(getBus())
				.getSync()
				.first()
				.get();
			
		assertEquals(IdentifierStatus.ASSIGNED.getSerializedName(), descriptionSctId.getStatus());
	}

	@Test
	public void retireDescription() {
		String descriptionId = createNewDescription(branchPath);

		Json requestBody = Json.object(
			"active", false,
			"commitComment", "Inactivated description",
			"acceptability", Json.object()
		);

		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, requestBody)
				.statusCode(204);

		SnomedDescription description = getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, "acceptabilities()")
			.statusCode(200)
			.extract().as(SnomedDescription.class);

		assertTrue(!description.isActive());
		assertTrue(description.getAcceptabilities().isEmpty());
	}
	
	@Test
	public void createDuplicateDescription() {
		String descriptionId = createNewDescription(branchPath);
		Json requestBody = createDescriptionRequestBody(Concepts.ROOT_CONCEPT)
				.with("id", descriptionId)
				.with("commitComment", "Created new description with duplicate identifier");

		createComponent(branchPath, SnomedComponentType.DESCRIPTION, requestBody)
			.statusCode(409);
	}

	@Test
	public void createDescriptionCaseInsensitive() {
		Json requestBody = createDescriptionRequestBody(Concepts.ROOT_CONCEPT, Concepts.SYNONYM, Concepts.MODULE_SCT_CORE, 
				SnomedApiTestConstants.UK_ACCEPTABLE_MAP, 
				Concepts.ENTIRE_TERM_CASE_INSENSITIVE)
				.with("commitComment", "Created new description with case insensitive significance");

		String descriptionId = assertCreated(createComponent(branchPath, SnomedComponentType.DESCRIPTION, requestBody));

		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId)
			.statusCode(200)
			.body("caseSignificanceId", equalTo(Concepts.ENTIRE_TERM_CASE_INSENSITIVE));
	}

	@Test
	public void deleteDescription() {
		String descriptionId = createNewDescription(branchPath);
		deleteComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, false)
			.statusCode(204);
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId)
			.statusCode(404);
	}

	@Test
	public void deleteReleasedDescription() {
		String descriptionId = createNewDescription(branchPath);

		String shortName = "SNOMEDCT-DSC-3";
		createCodeSystem(branchPath, shortName).statusCode(201);
		LocalDate effectiveDate = getNextAvailableEffectiveDate(shortName);
		createVersion(shortName, "v1", effectiveDate).statusCode(201);

		deleteComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, false).statusCode(409);
	}

	@Test
	public void forceDeleteDescription() {
		String descriptionId = createNewDescription(branchPath);

		String shortName = "SNOMEDCT-DSC-4";
		createCodeSystem(branchPath, shortName).statusCode(201);
		LocalDate effectiveDate = getNextAvailableEffectiveDate(shortName);
		createVersion(shortName, "v1", effectiveDate).statusCode(201);

		deleteComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, true).statusCode(204);
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId).statusCode(404);
	}

	@Test
	public void testDescriptionInactivation() {
		String descriptionId = createNewDescription(branchPath);
		Json requestBody = Json.object(
			"active", false,
			"commitComment", "Inactivated description"
		);

		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, requestBody)
			.statusCode(204);
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId)
			.statusCode(200)
			.body("active", equalTo(false));
	}

	@Test
	public void testDescriptionReactivation() throws Exception {
		String descriptionId = createNewDescription(branchPath);
		inactivateDescription(branchPath, descriptionId);

		String shortName = "SNOMEDCT-DSC-1";
		createCodeSystem(branchPath, shortName).statusCode(201);
		LocalDate effectiveDate = getNextAvailableEffectiveDate(shortName);
		createVersion(shortName, "v1", effectiveDate).statusCode(201);

		Json requestBody = Json.object(
			"active", true,
			"acceptability", Json.object(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.ACCEPTABLE),
			"commitComment", "Reactivate released description"
		);

		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, requestBody)
			.statusCode(204);
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, "members()")
			.body("active", equalTo(true))
			.body("released", equalTo(true))
			.body("effectiveTime", nullValue())
			.body("members.items.active", not(hasItem(false)))
			.body("members.items.effectiveTime", not(hasItem(not(nullValue()))));
	}

	@Test
	public void testReactivateThenInactivateDescription() throws Exception {
		String descriptionId = createNewDescription(branchPath);
		inactivateDescription(branchPath, descriptionId);

		String shortName = "SNOMEDCT-DSC-2";
		createCodeSystem(branchPath, shortName).statusCode(201);
		LocalDate effectiveDate = getNextAvailableEffectiveDate(shortName);
		createVersion(shortName, "v1", effectiveDate).statusCode(201);

		Json reactivateRequestBody = Json.object(
			"active", true,
			"acceptability", Json.object(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.ACCEPTABLE),
			"commitComment", "Reactivate released description"
		);

		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, reactivateRequestBody)
			.statusCode(204);

		Json inactivateRequestBody = Json.object(
			"active", false,
			"acceptability", Json.object(),
			"commitComment", "Inactivate reactivated released description again"
		);

		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, inactivateRequestBody)
			.statusCode(204);
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, "members()")
			.body("active", equalTo(false))
			.body("released", equalTo(true))
			.body("effectiveTime", equalTo(effectiveDate.format(DateTimeFormatter.BASIC_ISO_DATE)))
			.body("members.items.active", not(hasItem(true)))
			.body("members.items.effectiveTime", not(hasItem(not(equalTo(effectiveDate)))));
	}

	@Test
	public void inactivateWithIndicator() {
		String descriptionId = createNewDescription(branchPath);
		assertInactivation(
			branchPath, 
			descriptionId, 
			new InactivationProperties(Concepts.DUPLICATE, Collections.emptyList())
		);
	}

	@Test
	public void inactivateWithIndicatorAndAssociationTarget() {
		String descriptionToInactivate = createNewDescription(branchPath);
		String associationTarget = createNewDescription(branchPath);

		assertInactivation(
			branchPath, 
			descriptionToInactivate, 
			new InactivationProperties(Concepts.DUPLICATE, List.of(new AssociationTarget(Concepts.REFSET_POSSIBLY_EQUIVALENT_TO_ASSOCIATION, associationTarget)))
		);
	}

	@Test
	public void updateIndicatorAfterInactivation() {
		String descriptionToInactivate = createNewDescription(branchPath);
		
		assertInactivation(
			branchPath, 
			descriptionToInactivate, 
			new InactivationProperties(Concepts.DUPLICATE, Collections.emptyList())
		);

		assertInactivation(
			branchPath, 
			descriptionToInactivate, 
			new InactivationProperties(Concepts.OUTDATED, Collections.emptyList())
		);
	}
	
	@Test
	public void updateInactivationIndicatorOnActiveReleasedDescription() throws Exception {
		String descriptionToInactivate = createNewDescription(branchPath);
		
		assertInactivation(
			branchPath, 
			descriptionToInactivate, 
			new InactivationProperties(Concepts.PENDING_MOVE, Collections.emptyList())
		);
		
		// release component
		createCodeSystemAndVersion(branchPath, "SNOMEDCT-RELDESC-INACTIVATIONINDICATOR", "v1", LocalDate.parse("2018-07-01"));
		
		SnomedDescription description = assertInactivation(
			branchPath, 
			descriptionToInactivate, 
			new InactivationProperties(Concepts.CONCEPT_NON_CURRENT, Collections.emptyList())
		).extract().as(SnomedDescription.class);
		
		List<SnomedReferenceSetMember> inactivationIndicators = description.getMembers().stream()
			.filter(member -> Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR.equals(member.getReferenceSetId()))
			.collect(Collectors.toList());
		
		final SnomedReferenceSetMember inactivationIndicator = Iterables.getOnlyElement(inactivationIndicators); 
		
		assertEquals(Concepts.CONCEPT_NON_CURRENT, inactivationIndicator.getProperties().get(SnomedRf2Headers.FIELD_VALUE_ID));
		assertNull(inactivationIndicator.getEffectiveTime());
	}

	@Test
	public void updateCaseSignificance() {
		String descriptionId = createNewDescription(branchPath);
		Json inactivationRequestBody = Json.object(
			"active", false,
			"caseSignificanceId", Concepts.ENTIRE_TERM_CASE_INSENSITIVE,
			"commitComment", "Updated description case significance"
		);

		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, inactivationRequestBody)
			.statusCode(204);
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId)
			.statusCode(200)
			.body("caseSignificanceId", equalTo(Concepts.ENTIRE_TERM_CASE_INSENSITIVE));
	}

	@Test
	public void updateAcceptability() {
		String descriptionId = createNewDescription(branchPath);
		SnomedReferenceSetMembers beforeMembers = getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, "members()")
				.statusCode(200)
				.extract().as(SnomedDescription.class)
				.getMembers();

		assertEquals(1, beforeMembers.getTotal());
		SnomedReferenceSetMember beforeMember = Iterables.getOnlyElement(beforeMembers);

		Json requestBody = Json.object(
			"acceptability", SnomedApiTestConstants.UK_PREFERRED_MAP,
			"commitComment", "Updated description acceptability"
		);

		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, requestBody)
			.statusCode(204);
		SnomedReferenceSetMembers afterMembers = getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, "members()")
				.statusCode(200)
				.extract().as(SnomedDescription.class)
				.getMembers();

		assertEquals(1, afterMembers.getTotal());
		SnomedReferenceSetMember afterMember = Iterables.getOnlyElement(afterMembers);

		assertEquals(beforeMember.getId(), afterMember.getId());
		assertEquals(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED, afterMember.getProperties().get(SnomedRf2Headers.FIELD_ACCEPTABILITY_ID));
		assertEquals(Concepts.REFSET_LANGUAGE_TYPE_UK, afterMember.getReferenceSetId());
	}

	@Test
	public void updateAcceptabilityAndInactivate() {
		String conceptId = createNewConcept(branchPath);
		String ptDescriptionId = getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, "pt()")
				.statusCode(200)
				.extract().path("pt.id");

		String descriptionId = createNewDescription(branchPath, conceptId);
		Json requestBody = Json.object(
			"acceptability", SnomedApiTestConstants.US_PREFERRED_MAP,
			"active", false,
			"commitComment", "Updated description acceptability and inactivated it at the same time"
		);

		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, requestBody)
			.statusCode(204);
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, "pt()")
			.statusCode(200)
			.body("pt.id", equalTo(ptDescriptionId));
	}

	@Test
	public void updateAcceptabilityWithAddition() throws Exception {
		changeToAcceptable(branchPath, Concepts.ROOT_CONCEPT, Concepts.REFSET_LANGUAGE_TYPE_UK);
		String descriptionId = createNewDescription(branchPath, Concepts.ROOT_CONCEPT, Concepts.SYNONYM, SnomedApiTestConstants.UK_PREFERRED_MAP);

		changeToAcceptable(branchPath, Concepts.ROOT_CONCEPT, Concepts.REFSET_LANGUAGE_TYPE_US);
		Json requestBody = Json.object(
			"acceptability", Json.object(
				Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.ACCEPTABLE, 
				Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.PREFERRED
			),
			"commitComment", "Changed UK, added US acceptability to description"
		);

		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, requestBody).statusCode(204);
		SnomedReferenceSetMembers members = getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, "members()").statusCode(200)
				.body("members.items.referenceSetId", hasItems(Concepts.REFSET_LANGUAGE_TYPE_UK, Concepts.REFSET_LANGUAGE_TYPE_US))
				.extract().as(SnomedDescription.class)
				.getMembers();

		assertEquals(2, members.getTotal());

		getComponent(branchPath, SnomedComponentType.CONCEPT, Concepts.ROOT_CONCEPT, "pt()")
			.statusCode(200)
			.body("pt.id", equalTo(descriptionId));
	}
	
	@Test
	public void updateAcceptabilityWithDefaultModule() throws Exception {
		// All preferred descriptions in the UK language reference set should be changed to acceptable on SNOMED CT Root
		changeToAcceptable(branchPath, Concepts.ROOT_CONCEPT, Concepts.REFSET_LANGUAGE_TYPE_UK);
		
		// Add new UK preferred term to SNOMED CT Root
		String descriptionId = createNewDescription(branchPath, Concepts.ROOT_CONCEPT, Concepts.SYNONYM, SnomedApiTestConstants.UK_PREFERRED_MAP);
		
		// All preferred descriptions in the US language reference set should be changed to acceptable on SNOMED CT Root
		changeToAcceptable(branchPath, Concepts.ROOT_CONCEPT, Concepts.REFSET_LANGUAGE_TYPE_US);
		
		// Change UK preferred synonym to US preferred, UK acceptable, with default module ID parameter
		Json requestBody = Json.object(
			"acceptability", Json.object(
				Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.ACCEPTABLE, 
				Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.PREFERRED
			),
			"defaultModuleId", "449081005", // SNOMED CT Spanish edition module
			"commitComment", "Changed UK, added US acceptability to description"
		);
		
		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, requestBody)
				.statusCode(204);
		
		// Check language member count and module
		SnomedReferenceSetMembers members = getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, "members()")
				.statusCode(200)
				.body("members.items.referenceSetId", hasItems(Concepts.REFSET_LANGUAGE_TYPE_UK, Concepts.REFSET_LANGUAGE_TYPE_US))
				.extract()
				.as(SnomedDescription.class)
				.getMembers();
		
		assertEquals(2, members.getTotal());
		
		members.forEach(m -> assertEquals(
				"Reference set member should be placed in the default module", "449081005", m.getModuleId()));

		getComponent(branchPath, SnomedComponentType.CONCEPT, Concepts.ROOT_CONCEPT, "pt()")
				.statusCode(200)
				.body("pt.id", equalTo(descriptionId));
	}

	@Test
	public void updateAcceptabilityWithMemberReactivation() throws Exception {
		changeToAcceptable(branchPath, Concepts.ROOT_CONCEPT, Concepts.REFSET_LANGUAGE_TYPE_UK);
		String descriptionId = createNewDescription(branchPath, Concepts.ROOT_CONCEPT, Concepts.SYNONYM, SnomedApiTestConstants.UK_PREFERRED_MAP);

		SnomedReferenceSetMembers members = getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, "members()").statusCode(200)
				.extract()
				.as(SnomedDescription.class)
				.getMembers();

		// Inactivate the reference set member
		members.forEach(m -> {
			Json requestBody = Json.object(
				"active", false,
				"commitComment", "Inactivate language reference set member"
			);
			
			updateRefSetComponent(branchPath, SnomedComponentType.MEMBER, m.getId(), requestBody, false)
				.statusCode(204);
		});
		
		Json requestBody = Json.object(
			"acceptability", Json.object(
				Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED, 
				Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.PREFERRED
			),
			"commitComment", "Reactivated UK, added US acceptability to description"
		);

		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, requestBody)
			.statusCode(204);
		
		members = getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, "members()").statusCode(200)
				.body("members.items.referenceSetId", hasItems(Concepts.REFSET_LANGUAGE_TYPE_UK, Concepts.REFSET_LANGUAGE_TYPE_US))
				.extract().as(SnomedDescription.class)
				.getMembers();

		assertEquals(2, members.getTotal());

		members.forEach(m -> assertEquals(
				"Reference set member should be active", Boolean.TRUE, m.isActive()));
	}
	
	@Test
	public void updateAcceptabilityWithRemove() throws Exception {
		changeToAcceptable(branchPath, Concepts.ROOT_CONCEPT, Concepts.REFSET_LANGUAGE_TYPE_US);
		String descriptionId = createNewDescription(branchPath, Concepts.ROOT_CONCEPT, Concepts.SYNONYM, 
			Map.<String, Acceptability>of(
				Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.ACCEPTABLE, 
				Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.PREFERRED
			)
		);

		Json requestBody = Json.object(
			"acceptability", Json.object(
				Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.ACCEPTABLE
			),
			"commitComment", "Removed US preferred acceptability from description"
		);

		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, requestBody).statusCode(204);
		SnomedReferenceSetMembers members = getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, "members()").statusCode(200)
				.extract().as(SnomedDescription.class)
				.getMembers();

		assertEquals(1, members.getTotal());
		SnomedReferenceSetMember member = Iterables.getOnlyElement(members);

		assertEquals(Concepts.REFSET_LANGUAGE_TYPE_UK, member.getReferenceSetId());
		assertEquals(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_ACCEPTABLE, member.getProperties().get(SnomedRf2Headers.FIELD_ACCEPTABILITY_ID));
	}

	@Test
	public void createDescriptionOnNestedBranch() {
		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		IBranchPath b = BranchPathUtils.createPath(a, "b");
		branching.createBranchRecursively(b);

		String descriptionId = createNewDescription(b);

		getComponent(b, SnomedComponentType.DESCRIPTION, descriptionId).statusCode(200);
		getComponent(a, SnomedComponentType.DESCRIPTION, descriptionId).statusCode(404);
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId).statusCode(404);
	}

	@Test
	public void deleteDescriptionOnNestedBranch() {
		String conceptId = createNewConcept(branchPath);

		List<String> descriptionIds = newArrayList();
		for (int i = 0; i < 5; i++) {
			String descriptionId = createNewDescription(branchPath, conceptId);
			descriptionIds.add(descriptionId);
		}

		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		IBranchPath b = BranchPathUtils.createPath(a, "b");
		branching.createBranchRecursively(b);

		// New description on nested branch resets the concept's version to 1 again
		createNewDescription(b, conceptId);

		// Deleting a description from the middle should work
		String descriptionToDeleteId = descriptionIds.remove(2);
		deleteComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionToDeleteId, false).statusCode(204);
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionToDeleteId).statusCode(404);

		deleteComponent(b, SnomedComponentType.DESCRIPTION, descriptionToDeleteId, false).statusCode(204);
		getComponent(b, SnomedComponentType.DESCRIPTION, descriptionToDeleteId).statusCode(404);

		// All the remaining descriptions should be visible
		for (String descriptionId : descriptionIds) {
			getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId).statusCode(200);
			getComponent(b, SnomedComponentType.DESCRIPTION, descriptionId).statusCode(200);
		}
	}

	@Test
	public void testDuplicateMemberCleanupEmptiesAcceptabilityMap() throws Exception {
		String descriptionId = createNewDescription(branchPath);

		// Inject inactive language member with different acceptability (API won't allow it) 
		String memberIdToUpdate = UUID.randomUUID().toString();
		
		SnomedRefSetMemberIndexEntry member = SnomedRefSetMemberIndexEntry.builder()
				.id(memberIdToUpdate)
				.active(false)
				.released(false)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.referenceSetId(Concepts.REFSET_LANGUAGE_TYPE_UK)
				.referencedComponentId(descriptionId)
				.referenceSetType(SnomedRefSetType.LANGUAGE)
				.field(SnomedRf2Headers.FIELD_ACCEPTABILITY_ID, Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED)
				.build();
		
		new RepositoryRequest<>(SnomedTerminologyComponentConstants.TOOLING_ID, context -> {
			ApplicationContext.getServiceForClass(RepositoryManager.class)
				.get(SnomedTerminologyComponentConstants.TOOLING_ID)
				.service(RevisionIndex.class)
				.prepareCommit(branchPath.getPath())
				.stageNew(member)
				.withContext(context.inject().bind(ModuleIdProvider.class, c -> c.getModuleId()).build())
				.commit(ApplicationContext.getServiceForClass(TimestampProvider.class).getTimestamp(), "test", "Added duplicate language reference set member to " + descriptionId);
			return null;
		}).execute(ApplicationContext.getServiceForClass(Environment.class));
		
		// Check the acceptability map; the description should be acceptable in the UK reference set
		SnomedDescription description = getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, "members()").statusCode(200)
				.extract().as(SnomedDescription.class);

		assertEquals(Acceptability.ACCEPTABLE, description.getAcceptabilityMap().get(Concepts.REFSET_LANGUAGE_TYPE_UK));
		assertEquals(2, description.getMembers().getTotal());

		String memberIdToDelete = null;
		for (SnomedReferenceSetMember descriptionMember : description.getMembers()) {
			if (Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_ACCEPTABLE.equals(descriptionMember.getProperties().get(SnomedRf2Headers.FIELD_ACCEPTABILITY_ID))) {
				memberIdToDelete = descriptionMember.getId();
				break;
			}
		}

		assertNotNull(memberIdToDelete);

		// Using bulk update, remove the currently active member and activate the inactive one, also changing its acceptability
		Json bulkRequest = Json.object(
			"requests", Json.array(
				Json.object(
					"action", "delete",
					"memberId", memberIdToDelete
				),
				Json.object(
					"action", "update",
					"memberId", memberIdToUpdate,
					"active", true,
					"acceptabilityId", Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_ACCEPTABLE
				)
			),
			"commitComment", "Consolidated language reference set members"
		);

		bulkUpdateMembers(branchPath, Concepts.REFSET_LANGUAGE_TYPE_UK, bulkRequest)
			.statusCode(204);

		// Verify that description acceptability is still acceptable, but only one member remains
		SnomedDescription newDescription = getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, "members()").statusCode(200)
				.extract().as(SnomedDescription.class);

		assertEquals(Acceptability.ACCEPTABLE, newDescription.getAcceptabilityMap().get(Concepts.REFSET_LANGUAGE_TYPE_UK));
		assertEquals(1, newDescription.getMembers().getTotal());

		SnomedReferenceSetMember languageMember = Iterables.getOnlyElement(newDescription.getMembers());
		assertEquals(memberIdToUpdate, languageMember.getId());
		assertEquals(true, languageMember.isActive());
	}

	@Test
	public void issue_SO_2158_termFilter_throws_NPE() throws Exception {
		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
			.accept(ContentType.JSON)
			.queryParam("term", "<<")
			.get("/{path:**}/descriptions", branchPath.getPath())
			.then()
			.statusCode(200);
	}
	
	@Test
	public void updateUnreleasedDescriptionTypeId() throws Exception {
		String descriptionId = createNewDescription(branchPath);
		Json update = Json.object(
			SnomedRf2Headers.FIELD_TYPE_ID, Concepts.TEXT_DEFINITION,
			"commitComment", "Update unreleased description type"
		);

		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, update)
			.statusCode(204);
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId)
			.statusCode(200)
			.body(SnomedRf2Headers.FIELD_TYPE_ID, equalTo(Concepts.TEXT_DEFINITION));
	}
	
	@Test
	public void updateUnreleasedDescriptionTerm() throws Exception {
		String descriptionId = createNewDescription(branchPath);
		Json update = Json.object(
			SnomedRf2Headers.FIELD_TERM, "updatedUnreleasedDescriptionTerm",
			"commitComment", "Update unreleased description term"
		);

		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, update)
			.statusCode(204);
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId)
			.statusCode(200)
			.body(SnomedRf2Headers.FIELD_TERM, equalTo("updatedUnreleasedDescriptionTerm"));
	}
	
	@Test
	public void updateUnreleasedDescriptionLanguageCode() throws Exception {
		String descriptionId = createNewDescription(branchPath);
		Json update = Json.object(
			SnomedRf2Headers.FIELD_LANGUAGE_CODE, "hu",
			"commitComment", "Update unreleased description languageCode"
		);

		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, update)
			.statusCode(204);
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId)
			.statusCode(200)
			.body(SnomedRf2Headers.FIELD_LANGUAGE_CODE, equalTo("hu"));
	}
	
	@Test
	public void updateReleasedDescriptionTypeId() throws Exception {
		String descriptionId = createNewDescription(branchPath);
		
		// release component
		createCodeSystemAndVersion(branchPath, "SNOMEDCT-RELDESC-TYPEID", "v1", LocalDate.parse("2017-03-01"));
		
		Json update = Json.object(
			SnomedRf2Headers.FIELD_TYPE_ID, Concepts.TEXT_DEFINITION,
			"commitComment", "Update unreleased description type"
		);

		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, update)
			.statusCode(400);
		
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId)
			.statusCode(200)
			.body(SnomedRf2Headers.FIELD_TYPE_ID, equalTo(Concepts.SYNONYM));
	}
	
	@Test
	public void shouldUpdateReleasedDescriptionTerm() throws Exception {
		final String descriptionId = createNewDescription(branchPath);
		final String newTerm = "updatedUnreleasedDescriptionTerm";
		final Json update = Json.object(
			SnomedRf2Headers.FIELD_TERM, newTerm,
			"commitComment", "Update unreleased description term"
		);
		
		// release component
		createCodeSystemAndVersion(branchPath, "SNOMEDCT-RELDESC-TERM", "v1", LocalDate.parse("2017-03-01"));

		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, update).statusCode(204);
		
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId)
			.statusCode(200)
			.body(SnomedRf2Headers.FIELD_TERM, equalTo(newTerm));
	}
	
	@Test
	public void updateReleasedDescriptionLanguageCode() throws Exception {
		String descriptionId = createNewDescription(branchPath);
		Json update = Json.object(
			SnomedRf2Headers.FIELD_LANGUAGE_CODE, "hu",
			"commitComment", "Update unreleased description languageCode"
		);

		// release component
		createCodeSystemAndVersion(branchPath, "SNOMEDCT-RELDESC-LANGCODE", "v1", LocalDate.parse("2017-03-01"));
		
		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, update).statusCode(400);
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId)
			.statusCode(200)
			.body(SnomedRf2Headers.FIELD_LANGUAGE_CODE, equalTo(SnomedRestFixtures.DEFAULT_LANGUAGE_CODE));	
	}

	@Test(expected = ConflictException.class)
	public void doNotDeleteReleasedDescriptions() {
		
		String descriptionId = createNewDescription(branchPath);
		
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, ROOT_DESCRIPTION_ID)
			.statusCode(200)
			.body("released", equalTo(true));
		
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId)
			.statusCode(200)
			.body("released", equalTo(false));
		
		final BulkRequestBuilder<TransactionContext> bulk = BulkRequest.create();
		
		bulk.add(SnomedRequests.prepareDeleteDescription(descriptionId));
		bulk.add(SnomedRequests.prepareDeleteDescription(ROOT_DESCRIPTION_ID));

		SnomedRequests.prepareCommit()
			.setBody(bulk)
			.setCommitComment("Delete multiple descriptions")
			.setAuthor("test")
			.build(branchPath.getPath())
			.execute(getBus())
			.getSync();
		
	}
	
	@Test
	public void findUtf8Term() {
		String descriptionId = createNewDescription(branchPath);
		// The escaped form is used below; hopefully this catches source file encoding issues as well. 
		String term = "Ménière";
		Json update = Json.object(
			SnomedRf2Headers.FIELD_TERM, term,
			"commitComment", "Updated unreleased description term with special UTF8 char."
		);
		
		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, update).statusCode(204);
		
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId)
									.statusCode(200)
									.body(SnomedRf2Headers.FIELD_TERM, equalTo(term));
		
		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
			.when().get(String.format("/%s/descriptions?term=M\u00E9ni\u00E8re", branchPath.getPath()))
			.then().log().ifValidationFails()
			.and().assertThat().statusCode(200)
			.and().body("total", equalTo(1));
	}
	
	@Test
	public void searchFuzzyWithFilteredCharacters() throws Exception {
		int numberOfResults = SnomedRequests.prepareSearchDescription()
			.setLimit(0)
			.filterByTerm(TermFilter.fuzzyMatch(TextConstants.DELIMITERS))
			.build(branchPath.getPath())
			.execute(getBus())
			.getSync()
			.getTotal();
		assertThat(numberOfResults).isZero();
	}
	
	@Test
	public void restoreEffectiveTimeOnReleasedDescription() throws Exception {
		final String descriptionId = createNewDescription(branchPath);

		final String shortName = "SNOMEDCT-DESC-1";
		createCodeSystem(branchPath, shortName).statusCode(201);
		final LocalDate effectiveDate = getNextAvailableEffectiveDate(shortName);
		createVersion(shortName, "v1", effectiveDate).statusCode(201);

		// After versioning, the description should be released and have an effective time set on it
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId).statusCode(200)
			.body("active", equalTo(true))
			.body("released", equalTo(true))
			.body("effectiveTime", equalTo(effectiveDate.format(DateTimeFormatter.BASIC_ISO_DATE)));

		inactivateDescription(branchPath, descriptionId);

		// An inactivation should unset the effective time field
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId).statusCode(200)
			.body("active", equalTo(false))
			.body("released", equalTo(true))
	 		.body("effectiveTime", nullValue());

		Json reactivationRequestBody = Json.object(
			"active", true,
			"commitComment", "Inactivated description"
		);

		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, reactivationRequestBody).statusCode(204);

		// Getting the description back to its originally released state should restore the effective time
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId).statusCode(200)
			.body("active", equalTo(true))
			.body("released", equalTo(true))
			.body("effectiveTime", equalTo(effectiveDate.format(DateTimeFormatter.BASIC_ISO_DATE)));
	}
	
	@Test
	public void testSearchByExactCaseSensitiveTerm() {
		final String searchTerm = "Body structure";
		
		final List<String> exactDescriptions = SnomedRequests.prepareSearchDescription()
				.filterByExactTerm(searchTerm)
				.build(branchPath.getPath())
				.execute(getBus())
				.getSync(1, TimeUnit.MINUTES)
				.getItems()
				.stream()
				.map(SnomedDescription::getTerm)
				.collect(Collectors.toList());
		
		assertThat(exactDescriptions).hasSameElementsAs(List.of(searchTerm));

		final List<String> upperCaseDescriptions = SnomedRequests.prepareSearchDescription()
				.filterByExactTerm(searchTerm.toUpperCase())
				.build(branchPath.getPath())
				.execute(getBus())
				.getSync(1, TimeUnit.MINUTES)
				.getItems()
				.stream()
				.map(SnomedDescription::getTerm)
				.collect(Collectors.toList());

		assertThat(upperCaseDescriptions).isEmpty();
	}
	
	@Test
	public void testSearchByExactCaseInSensitiveTerm() {
		final String searchTerm = "Clinical finding";
		
		final List<String> descriptions = SnomedRequests.prepareSearchDescription()
				.filterByExactTermIgnoreCase(searchTerm.toUpperCase())
				.build(branchPath.getPath())
				.execute(getBus())
				.getSync(1, TimeUnit.MINUTES)
				.getItems()
				.stream()
				.map(SnomedDescription::getTerm)
				.collect(Collectors.toList());
		
		assertThat(descriptions).hasSameElementsAs(List.of(searchTerm));
	}
}
