/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest.components;

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.snomed.api.rest.CodeSystemRestRequests.createCodeSystem;
import static com.b2international.snowowl.snomed.api.rest.CodeSystemVersionRestRequests.createCodeSystemAndVersion;
import static com.b2international.snowowl.snomed.api.rest.CodeSystemVersionRestRequests.createVersion;
import static com.b2international.snowowl.snomed.api.rest.CodeSystemVersionRestRequests.getNextAvailableEffectiveDateAsString;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingRestRequests.createBranchRecursively;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.createComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.deleteComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.updateComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedRefSetRestRequests.bulkUpdateMembers;
import static com.b2international.snowowl.snomed.api.rest.SnomedRefSetRestRequests.updateRefSetComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.changeToAcceptable;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createDescriptionRequestBody;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewConcept;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewDescription;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.inactivateDescription;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;

import com.b2international.index.compat.TextConstants;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.bulk.BulkRequest;
import com.b2international.snowowl.core.events.bulk.BulkRequestBuilder;
import com.b2international.snowowl.core.exceptions.ConflictException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.AssociationType;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.core.domain.DescriptionInactivationIndicator;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.datastore.id.domain.IdentifierStatus;
import com.b2international.snowowl.snomed.datastore.id.domain.SctId;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

import io.restassured.http.ContentType;

/**
 * @since 2.0
 */
public class SnomedDescriptionApiTest extends AbstractSnomedApiTest {

	private static final String ROOT_DESCRIPTION_ID = "2913224013";

	@Test
	public void createDescriptionNonExistentBranch() {
		Map<?, ?> requestBody = createDescriptionRequestBody(Concepts.ROOT_CONCEPT)
				.put("commitComment", "Created new description on non-existent branch")
				.build();

		createComponent(BranchPathUtils.createPath("MAIN/x/y/z"), SnomedComponentType.DESCRIPTION, requestBody).statusCode(404);
	}

	@Test
	public void createDescriptionInvalidConcept() {
		Map<?, ?> requestBody = createDescriptionRequestBody("11110000")
				.put("commitComment", "Created new description with invalid conceptId")
				.build();

		createComponent(branchPath, SnomedComponentType.DESCRIPTION, requestBody).statusCode(400);
	}

	@Test
	public void createDescriptionInvalidType() {
		Map<?, ?> requestBody = createDescriptionRequestBody(Concepts.ROOT_CONCEPT, "11110000")
				.put("commitComment", "Created new description with invalid typeId")
				.build();

		createComponent(branchPath, SnomedComponentType.DESCRIPTION, requestBody).statusCode(400);
	}

	@Test
	public void createDescriptionInvalidModule() {
		Map<?, ?> requestBody = createDescriptionRequestBody(Concepts.ROOT_CONCEPT, Concepts.SYNONYM, "11110000")
				.put("commitComment", "Created new description with invalid moduleId")
				.build();

		createComponent(branchPath, SnomedComponentType.DESCRIPTION, requestBody).statusCode(400);
	}

	@Test
	public void createDescriptionWithoutCommitComment() {
		Map<?, ?> requestBody = createDescriptionRequestBody(Concepts.ROOT_CONCEPT).build();
		createComponent(branchPath, SnomedComponentType.DESCRIPTION, requestBody).statusCode(400);
	}

	@Test
	public void createDescription() {
		Map<?, ?> requestBody = createDescriptionRequestBody(Concepts.ROOT_CONCEPT)
				.put("commitComment", "Created new description")
				.build();

		createComponent(branchPath, SnomedComponentType.DESCRIPTION, requestBody).statusCode(201);
	}

	@Test
	public void createDescriptionWithReservedId() {
		ISnomedIdentifierService identifierService = getServiceForClass(ISnomedIdentifierService.class);
		String descriptionId = Iterables.getOnlyElement(identifierService.reserve(null, ComponentCategory.DESCRIPTION, 1));

		Map<?, ?> requestBody = createDescriptionRequestBody(Concepts.ROOT_CONCEPT)
				.put("id", descriptionId)
				.put("commitComment", "Created new description with reserved identifier")
				.build();

		createComponent(branchPath, SnomedComponentType.DESCRIPTION, requestBody).statusCode(201)
		.header("Location", endsWith("/" + descriptionId));
		
		SctId descriptionSctId = SnomedRequests.identifiers().prepareGet()
				.setComponentId(descriptionId)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID)
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.getSync()
				.first()
				.get();
			
		assertEquals(IdentifierStatus.ASSIGNED.getSerializedName(), descriptionSctId.getStatus());
	}

	@Test
	public void createDuplicateDescription() {
		String descriptionId = createNewDescription(branchPath);
		Map<?, ?> requestBody = createDescriptionRequestBody(Concepts.ROOT_CONCEPT)
				.put("id", descriptionId)
				.put("commitComment", "Created new description with duplicate identifier")
				.build();

		createComponent(branchPath, SnomedComponentType.DESCRIPTION, requestBody).statusCode(409);
	}

	@Test
	public void createDescriptionCaseInsensitive() {
		Map<?, ?> requestBody = createDescriptionRequestBody(Concepts.ROOT_CONCEPT, Concepts.SYNONYM, Concepts.MODULE_SCT_CORE, 
				SnomedApiTestConstants.UK_ACCEPTABLE_MAP, 
				CaseSignificance.CASE_INSENSITIVE)
				.put("commitComment", "Created new description with case insensitive significance")
				.build();

		String descriptionId = lastPathSegment(createComponent(branchPath, SnomedComponentType.DESCRIPTION, requestBody)
				.statusCode(201)
				.extract().header("Location"));

		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId).statusCode(200)
		.body("caseSignificance", equalTo(CaseSignificance.CASE_INSENSITIVE.name()));
	}

	@Test
	public void deleteDescription() {
		String descriptionId = createNewDescription(branchPath);
		deleteComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, false).statusCode(204);
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId).statusCode(404);
	}

	@Test
	public void deleteReleasedDescription() {
		String descriptionId = createNewDescription(branchPath);

		String shortName = "SNOMEDCT-DSC-3";
		createCodeSystem(branchPath, shortName).statusCode(201);
		String effectiveDate = getNextAvailableEffectiveDateAsString(shortName);
		createVersion(shortName, "v1", effectiveDate).statusCode(201);

		deleteComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, false).statusCode(409);
	}

	@Test
	public void forceDeleteDescription() {
		String descriptionId = createNewDescription(branchPath);

		String shortName = "SNOMEDCT-DSC-4";
		createCodeSystem(branchPath, shortName).statusCode(201);
		String effectiveDate = getNextAvailableEffectiveDateAsString(shortName);
		createVersion(shortName, "v1", effectiveDate).statusCode(201);

		deleteComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, true).statusCode(204);
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId).statusCode(404);
	}

	@Test
	public void testDescriptionInactivation() {
		String descriptionId = createNewDescription(branchPath);
		Map<?, ?> requestBody = ImmutableMap.builder()
				.put("active", false)
				.put("commitComment", "Inactivated description")
				.build();

		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, requestBody).statusCode(204);
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId).statusCode(200)
		.body("active", equalTo(false));
	}

	@Test
	public void testDescriptionReactivation() throws Exception {
		String descriptionId = createNewDescription(branchPath);
		inactivateDescription(branchPath, descriptionId);

		String shortName = "SNOMEDCT-DSC-1";
		createCodeSystem(branchPath, shortName).statusCode(201);
		String effectiveDate = getNextAvailableEffectiveDateAsString(shortName);
		createVersion(shortName, "v1", effectiveDate).statusCode(201);

		Map<?, ?> requestBody = ImmutableMap.builder()
				.put("active", true)
				.put("acceptability", ImmutableMap.of(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.ACCEPTABLE))
				.put("commitComment", "Reactivate released description")
				.build();

		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, requestBody).statusCode(204);
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
		String effectiveDate = getNextAvailableEffectiveDateAsString(shortName);
		createVersion(shortName, "v1", effectiveDate).statusCode(201);

		Map<?, ?> reactivateRequestBody = ImmutableMap.builder()
				.put("active", true)
				.put("acceptability", ImmutableMap.of(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.ACCEPTABLE))
				.put("commitComment", "Reactivate released description")
				.build();

		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, reactivateRequestBody).statusCode(204);

		Map<?, ?> inactivateRequestBody = ImmutableMap.builder()
				.put("active", false)
				.put("acceptability", ImmutableMap.of())
				.put("commitComment", "Inactivate reactivated released description again")
				.build();

		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, inactivateRequestBody).statusCode(204);
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, "members()")
		.body("active", equalTo(false))
		.body("released", equalTo(true))
		.body("effectiveTime", equalTo(effectiveDate))
		.body("members.items.active", not(hasItem(true)))
		.body("members.items.effectiveTime", not(hasItem(not(equalTo(effectiveDate)))));
	}

	@Test
	public void inactivateWithIndicator() {
		String descriptionId = createNewDescription(branchPath);
		Map<?, ?> requestBody = ImmutableMap.builder()
				.put("active", false)
				.put("inactivationIndicator", DescriptionInactivationIndicator.DUPLICATE)
				.put("commitComment", "Inactivated description with indicator")
				.build();

		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, requestBody).statusCode(204);
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, "inactivationProperties()").statusCode(200)
		.body("active", equalTo(false))
		.body("inactivationIndicator", equalTo(DescriptionInactivationIndicator.DUPLICATE.name()));
	}

	@Test
	public void inactivateWithIndicatorAndAssociationTarget() {
		String description1Id = createNewDescription(branchPath);
		String description2Id = createNewDescription(branchPath);

		Map<?, ?> requestBody = ImmutableMap.builder()
				.put("active", false)
				.put("inactivationIndicator", DescriptionInactivationIndicator.DUPLICATE)
				.put("associationTargets", ImmutableMap.of(AssociationType.POSSIBLY_EQUIVALENT_TO.name(), ImmutableList.of(description1Id)))
				.put("commitComment", "Inactivated description with indicator and association target")
				.build();

		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, description2Id, requestBody).statusCode(204);
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, description2Id, "inactivationProperties()").statusCode(200)
			.body("active", equalTo(false))
			.body("inactivationIndicator", equalTo(DescriptionInactivationIndicator.DUPLICATE.name()))
			.body("associationTargets." + AssociationType.POSSIBLY_EQUIVALENT_TO.name(), hasItem(description1Id));
	}

	@Test
	public void updateIndicatorAfterInactivation() {
		String descriptionId = createNewDescription(branchPath);
		Map<?, ?> inactivationRequestBody = ImmutableMap.builder()
				.put("active", false)
				.put("inactivationIndicator", DescriptionInactivationIndicator.DUPLICATE)
				.put("commitComment", "Inactivated description with indicator")
				.build();

		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, inactivationRequestBody).statusCode(204);
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, "inactivationProperties()").statusCode(200)
			.body("active", equalTo(false))
			.body("inactivationIndicator", equalTo(DescriptionInactivationIndicator.DUPLICATE.name()));

		Map<?, ?> updateRequestBody = ImmutableMap.builder()
				.put("active", false)
				.put("inactivationIndicator", DescriptionInactivationIndicator.OUTDATED)
				.put("commitComment", "Updated inactivation indicator on description")
				.build();

		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, updateRequestBody).statusCode(204);
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, "inactivationProperties()").statusCode(200)
			.body("active", equalTo(false))
			.body("inactivationIndicator", equalTo(DescriptionInactivationIndicator.OUTDATED.name()));
	}
	
	@Test
	public void updateInactivationIndicatorOnActiveReleasedDescription() throws Exception {
		String descriptionId = createNewDescription(branchPath);
		Map<?, ?> inactivationRequestBody = ImmutableMap.builder()
				.put("inactivationIndicator", DescriptionInactivationIndicator.PENDING_MOVE)
				.put("commitComment", "Add pending move to description")
				.build();
		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, inactivationRequestBody).statusCode(204);
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, "inactivationProperties()").statusCode(200)
			.body("inactivationIndicator", equalTo(DescriptionInactivationIndicator.PENDING_MOVE.name()));
		
		// release component
		createCodeSystemAndVersion(branchPath, "SNOMEDCT-RELDESC-INACTIVATIONINDICATOR", "v1", "20180701");
		
		Map<?, ?> updateRequestBody = ImmutableMap.builder()
				.put("inactivationIndicator", DescriptionInactivationIndicator.CONCEPT_NON_CURRENT)
				.put("commitComment", "Updated inactivation indicator on description")
				.build();
		
		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, updateRequestBody).statusCode(204);
		SnomedDescription description = getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, "members()").statusCode(200).extract().as(SnomedDescription.class);
		
		SnomedReferenceSetMember inactivationIndicator = description.getMembers().stream()
			.filter(member -> Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR.equals(member.getReferenceSetId()))
			.findFirst()
			.get();
		
		assertEquals(Concepts.CONCEPT_NON_CURRENT, inactivationIndicator.getProperties().get(SnomedRf2Headers.FIELD_VALUE_ID));
		assertNull(inactivationIndicator.getEffectiveTime());
	}

	@Test
	public void updateCaseSignificance() {
		String descriptionId = createNewDescription(branchPath);
		Map<?, ?> inactivationRequestBody = ImmutableMap.builder()
				.put("active", false)
				.put("caseSignificance", CaseSignificance.CASE_INSENSITIVE)
				.put("commitComment", "Updated description case significance")
				.build();

		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, inactivationRequestBody).statusCode(204);
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId).statusCode(200)
			.body("caseSignificance", equalTo(CaseSignificance.CASE_INSENSITIVE.name()));
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

		Map<?, ?> requestBody = ImmutableMap.builder()
				.put("acceptability", SnomedApiTestConstants.UK_PREFERRED_MAP)
				.put("commitComment", "Updated description acceptability")
				.build();

		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, requestBody).statusCode(204);
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
		Map<?, ?> requestBody = ImmutableMap.builder()
				.put("acceptability", SnomedApiTestConstants.US_PREFERRED_MAP)
				.put("active", false)
				.put("commitComment", "Updated description acceptability and inactivated it at the same time")
				.build();

		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, requestBody).statusCode(204);
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, "pt()")
		.statusCode(200)
		.body("pt.id", equalTo(ptDescriptionId));
	}

	@Test
	public void updateAcceptabilityWithAddition() throws Exception {
		changeToAcceptable(branchPath, Concepts.ROOT_CONCEPT, Concepts.REFSET_LANGUAGE_TYPE_UK);
		String descriptionId = createNewDescription(branchPath, Concepts.ROOT_CONCEPT, Concepts.SYNONYM, SnomedApiTestConstants.UK_PREFERRED_MAP);

		changeToAcceptable(branchPath, Concepts.ROOT_CONCEPT, Concepts.REFSET_LANGUAGE_TYPE_US);
		Map<?, ?> requestBody = ImmutableMap.builder()
				.put("acceptability", ImmutableMap.of(
						Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.ACCEPTABLE, 
						Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.PREFERRED))
				.put("commitComment", "Changed UK, added US acceptability to description")
				.build();

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
		Map<?, ?> requestBody = ImmutableMap.builder()
				.put("acceptability", ImmutableMap.of(
						Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.ACCEPTABLE, 
						Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.PREFERRED))
				.put("commitComment", "Changed UK, added US acceptability to description")
				.put("defaultModuleId", "449081005") // SNOMED CT Spanish edition module
				.build();
		
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
			Map<?, ?> requestBody = ImmutableMap.builder()
					.put("active", false)
					.put("commitComment", "Inactivate language reference set member")
					.build();
			
			updateRefSetComponent(branchPath, SnomedComponentType.MEMBER, m.getId(), requestBody, false).statusCode(204);
		});
		
		Map<?, ?> requestBody = ImmutableMap.builder()
				.put("acceptability", ImmutableMap.of(
						Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED, 
						Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.PREFERRED))
				.put("commitComment", "Reactivated UK, added US acceptability to description")
				.build();

		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, requestBody).statusCode(204);
		
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
		String descriptionId = createNewDescription(branchPath, Concepts.ROOT_CONCEPT, Concepts.SYNONYM, ImmutableMap.<String, Acceptability>of(
				Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.ACCEPTABLE, 
				Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.PREFERRED));

		Map<?, ?> requestBody = ImmutableMap.builder()
				.put("acceptability", ImmutableMap.of(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.ACCEPTABLE))
				.put("commitComment", "Removed US preferred acceptability from description")
				.build();

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
		createBranchRecursively(b);

		String descriptionId = createNewDescription(b);

		getComponent(b, SnomedComponentType.DESCRIPTION, descriptionId).statusCode(200);
		getComponent(a, SnomedComponentType.DESCRIPTION, descriptionId).statusCode(404);
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId).statusCode(404);
	}

	@Test
	public void deleteDescriptionOnNestedBranch() {
		String conceptId = createNewConcept(branchPath);

		List<String> descriptionIds = newArrayList();
		for (int i = 0; i < 10; i++) {
			String descriptionId = createNewDescription(branchPath, conceptId);
			descriptionIds.add(descriptionId);
		}

		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		IBranchPath b = BranchPathUtils.createPath(a, "b");
		createBranchRecursively(b);

		// New description on nested branch resets the concept's version to 1 again
		createNewDescription(b, conceptId);

		// Deleting a description from the middle should work
		String descriptionToDeleteId = descriptionIds.remove(4);
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
		try (SnomedEditingContext context = new SnomedEditingContext(branchPath)) {
			SnomedLanguageRefSetMember member = SnomedRefSetFactory.eINSTANCE.createSnomedLanguageRefSetMember();
			member.setUuid(memberIdToUpdate);
			member.setActive(false);
			member.setModuleId(Concepts.MODULE_SCT_CORE);
			member.setRefSet(context.lookup(Concepts.REFSET_LANGUAGE_TYPE_UK, SnomedRefSet.class));
			member.setReferencedComponentId(descriptionId);
			member.setAcceptabilityId(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED);

			Description description = context.lookup(descriptionId, Description.class);
			description.getLanguageRefSetMembers().add(member);
			context.commit("Added duplicate language reference set member to " + descriptionId);
		}

		// Check the acceptability map; the description should be acceptable in the UK reference set
		SnomedDescription description = getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, "members()").statusCode(200)
				.extract().as(SnomedDescription.class);

		assertEquals(Acceptability.ACCEPTABLE, description.getAcceptabilityMap().get(Concepts.REFSET_LANGUAGE_TYPE_UK));
		assertEquals(2, description.getMembers().getTotal());

		String memberIdToDelete = null;
		for (SnomedReferenceSetMember member : description.getMembers()) {
			if (Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_ACCEPTABLE.equals(member.getProperties().get(SnomedRf2Headers.FIELD_ACCEPTABILITY_ID))) {
				memberIdToDelete = member.getId();
				break;
			}
		}

		assertNotNull(memberIdToDelete);

		// Using bulk update, remove the currently active member and activate the inactive one, also changing its acceptability
		Map<?, ?> deleteMember = ImmutableMap.<String, Object>builder()
				.put("action", "delete")
				.put("memberId", memberIdToDelete)
				.build();

		Map<?, ?> activateMember = ImmutableMap.<String, Object>builder()
				.put("action", "update")
				.put("memberId", memberIdToUpdate)
				.put("active", true)
				.put("acceptabilityId", Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_ACCEPTABLE)
				.build();

		Map<?, ?> bulkRequest = ImmutableMap.<String, Object>builder()
				.put("requests", ImmutableList.of(deleteMember, activateMember))
				.put("commitComment", "Consolidated language reference set members")
				.build();

		bulkUpdateMembers(branchPath, Concepts.REFSET_LANGUAGE_TYPE_UK, bulkRequest).statusCode(204);

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
		.get("/{path}/descriptions", branchPath.getPath())
		.then()
		.statusCode(200);
	}
	
	@Test
	public void updateUnreleasedDescriptionTypeId() throws Exception {
		String descriptionId = createNewDescription(branchPath);
		Map<?, ?> update = ImmutableMap.builder()
				.put(SnomedRf2Headers.FIELD_TYPE_ID, Concepts.TEXT_DEFINITION)
				.put("commitComment", "Update unreleased description type")
				.build();

		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, update).statusCode(204);
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId)
			.statusCode(200)
			.body(SnomedRf2Headers.FIELD_TYPE_ID, equalTo(Concepts.TEXT_DEFINITION));
	}
	
	@Test
	public void updateUnreleasedDescriptionTerm() throws Exception {
		String descriptionId = createNewDescription(branchPath);
		Map<?, ?> update = ImmutableMap.builder()
				.put(SnomedRf2Headers.FIELD_TERM, "updatedUnreleasedDescriptionTerm")
				.put("commitComment", "Update unreleased description term")
				.build();

		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, update).statusCode(204);
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId)
			.statusCode(200)
			.body(SnomedRf2Headers.FIELD_TERM, equalTo("updatedUnreleasedDescriptionTerm"));
	}
	
	@Test
	public void updateUnreleasedDescriptionLanguageCode() throws Exception {
		String descriptionId = createNewDescription(branchPath);
		Map<?, ?> update = ImmutableMap.builder()
				.put(SnomedRf2Headers.FIELD_LANGUAGE_CODE, "hu")
				.put("commitComment", "Update unreleased description languageCode")
				.build();

		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, update).statusCode(204);
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId)
			.statusCode(200)
			.body(SnomedRf2Headers.FIELD_LANGUAGE_CODE, equalTo("hu"));
	}
	
	@Test
	public void updateReleasedDescriptionTypeId() throws Exception {
		String descriptionId = createNewDescription(branchPath);
		
		// release component
		createCodeSystemAndVersion(branchPath, "SNOMEDCT-RELDESC-TYPEID", "v1", "20170301");
		
		Map<?, ?> update = ImmutableMap.builder()
				.put(SnomedRf2Headers.FIELD_TYPE_ID, Concepts.TEXT_DEFINITION)
				.put("commitComment", "Update unreleased description type")
				.build();

		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, update).statusCode(400);
		
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId)
			.statusCode(200)
			.body(SnomedRf2Headers.FIELD_TYPE_ID, equalTo(Concepts.SYNONYM));
	}
	
	@Test
	public void shouldUpdateReleasedDescriptionTerm() throws Exception {
		final String descriptionId = createNewDescription(branchPath);
		final String newTerm = "updatedUnreleasedDescriptionTerm";
		final Map<?, ?> update = ImmutableMap.builder()
				.put(SnomedRf2Headers.FIELD_TERM, newTerm)
				.put("commitComment", "Update unreleased description term")
				.build();
		
		// release component
		createCodeSystemAndVersion(branchPath, "SNOMEDCT-RELDESC-TERM", "v1", "20170301");

		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, update).statusCode(204);
		
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId)
			.statusCode(200)
			.body(SnomedRf2Headers.FIELD_TERM, equalTo(newTerm));
	}
	
	@Test
	public void updateReleasedDescriptionLanguageCode() throws Exception {
		String descriptionId = createNewDescription(branchPath);
		Map<?, ?> update = ImmutableMap.builder()
				.put(SnomedRf2Headers.FIELD_LANGUAGE_CODE, "hu")
				.put("commitComment", "Update unreleased description languageCode")
				.build();

		// release component
		createCodeSystemAndVersion(branchPath, "SNOMEDCT-RELDESC-LANGCODE", "v1", "20170301");
		
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
			.setUserId("test")
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
			.execute(ApplicationContext.getServiceForClass(IEventBus.class))
			.getSync();
		
	}
	
	@Test
	public void searchFuzzyWithFilteredCharacters() throws Exception {
		int numberOfResults = SnomedRequests.prepareSearchDescription()
			.setLimit(0)
			.filterByTerm(TextConstants.DELIMITERS)
			.withFuzzySearch()
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
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
		final String effectiveDate = getNextAvailableEffectiveDateAsString(shortName);
		createVersion(shortName, "v1", effectiveDate).statusCode(201);

		// After versioning, the description should be released and have an effective time set on it
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId).statusCode(200)
		.body("active", equalTo(true))
		.body("released", equalTo(true))
		.body("effectiveTime", equalTo(effectiveDate));

		inactivateDescription(branchPath, descriptionId);

		// An inactivation should unset the effective time field
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId).statusCode(200)
		.body("active", equalTo(false))
		.body("released", equalTo(true))
 		.body("effectiveTime", nullValue());

		Map<?, ?> reactivationRequestBody = ImmutableMap.builder()
				.put("active", true)
				.put("commitComment", "Inactivated description")
				.build();

		updateComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, reactivationRequestBody).statusCode(204);

		// Getting the description back to its originally released state should restore the effective time
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionId).statusCode(200)
		.body("active", equalTo(true))
		.body("released", equalTo(true))
		.body("effectiveTime", equalTo(effectiveDate));
	}
	
}
