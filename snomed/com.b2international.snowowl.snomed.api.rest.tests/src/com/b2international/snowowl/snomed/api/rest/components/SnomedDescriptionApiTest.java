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
package com.b2international.snowowl.snomed.api.rest.components;

import static com.b2international.snowowl.datastore.BranchPathUtils.createMainPath;
import static com.b2international.snowowl.datastore.BranchPathUtils.createPath;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.givenBranchWithPath;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentActive;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentCanBeDeleted;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentCanBeUpdated;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentCreated;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentCreatedWithStatus;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentExists;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentHasProperty;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentNotCreated;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertDescriptionExists;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertDescriptionNotExists;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertPreferredTermEquals;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.AssociationType;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.core.domain.DescriptionInactivationIndicator;
import com.b2international.snowowl.snomed.core.domain.InactivationIndicator;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.ValidatableResponse;

/**
 * @since 2.0
 */
public class SnomedDescriptionApiTest extends AbstractSnomedApiTest {

	private static final String DISEASE = "64572001";
	
	private static final String INACTIVE_DISEASE_DESCRIPTION = "187915017";
	private static final String INACTIVE_DISEASE_DESCRIPTION_EFFECTIVE_TIME = "20020131";
	private static final String INACTIVE_DISEASE_DESCRIPTION_MEMBER_ID = "b6da3189-3c2f-5b36-865e-bac13d08aeae";

	private Builder<Object, Object> createRequestBuilder(final String conceptId, 
			final String term, 
			final String moduleId,
			final String typeId, 
			final String comment) {
		return createRequestBuilder(conceptId, term, moduleId, typeId, comment, SnomedApiTestConstants.ACCEPTABLE_ACCEPTABILITY_MAP);
	}

	private Builder<Object, Object> createRequestBuilder(final String conceptId, final String term, final String moduleId, final String typeId,
			final String comment, Map<?, ?> acceptabilityMap) {
		return ImmutableMap.builder()
				.put("conceptId", conceptId)
				.put("moduleId", moduleId)
				.put("typeId", typeId)
				.put("term", term)
				.put("languageCode", "en")
				.put("acceptability", acceptabilityMap)
				.put("commitComment", comment);
	}

	private Map<?, ?> createRequestBody(final String conceptId, 
			final String term, 
			final String moduleId, 
			final String typeId, 
			final String comment) {

		return createRequestBuilder(conceptId, term, moduleId, typeId, comment)
				.build();
	}

	private Map<?, ?> createRequestBody(final String conceptId, 
			final String term, 
			final String moduleId, 
			final String typeId, 
			final CaseSignificance caseSignificance, 
			final String comment) {

		return createRequestBuilder(conceptId, term, moduleId, typeId, comment)
				.put("caseSignificance", caseSignificance.name())
				.build();
	}

	@Test
	public void createDescriptionNonExistentBranch() {
		final Map<?, ?> requestBody = createRequestBody(DISEASE, "Rare disease", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "New description on a non-existent branch");
		assertComponentCreatedWithStatus(createPath("MAIN/1998-01-31"), SnomedComponentType.DESCRIPTION, requestBody, 404)
		.and().body("status", equalTo(404));
	}

	@Test
	public void createDescriptionWithNonExistentConcept() {
		final Map<?, ?> requestBody = createRequestBody("1", "Rare disease", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "New description with a non-existent concept ID");		
		assertComponentNotCreated(createMainPath(), SnomedComponentType.DESCRIPTION, requestBody);
	}

	@Test
	public void createDescriptionWithNonexistentType() {
		final Map<?, ?> requestBody = createRequestBody(DISEASE, "Rare disease", Concepts.MODULE_SCT_CORE, "2", "New description with a non-existent type ID");		
		assertComponentNotCreated(createMainPath(), SnomedComponentType.DESCRIPTION, requestBody);
	}

	@Test
	public void createDescriptionWithNonexistentModule() {
		final Map<?, ?> requestBody = createRequestBody(DISEASE, "Rare disease", "3", Concepts.SYNONYM, "New description with a non-existent module ID");
		assertComponentNotCreated(createMainPath(), SnomedComponentType.DESCRIPTION, requestBody);
	}

	@Test
	public void createDescriptionWithoutCommitComment() {
		final Map<?, ?> requestBody = createRequestBody(DISEASE, "Rare disease", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "");
		assertComponentNotCreated(createMainPath(), SnomedComponentType.DESCRIPTION, requestBody);
	}

	private void assertCaseSignificance(final IBranchPath branchPath, final String descriptionId, final CaseSignificance caseSignificance) {
		assertComponentHasProperty(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, "caseSignificance", caseSignificance.toString());
	}

	private void assertActive(final IBranchPath branchPath, final String descriptionId, final boolean active) {
		assertComponentActive(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, active);
	}

	@Test
	public void createDescription() {
		final Map<?, ?> requestBody = createRequestBody(DISEASE, "Rare disease", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "New description on MAIN");
		final String descriptionId = assertComponentCreated(createMainPath(), SnomedComponentType.DESCRIPTION, requestBody);
		assertCaseSignificance(createMainPath(), descriptionId, CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE);
	}
	
	@Test
	public void createDuplicateDescription() {
		final Map<?, ?> requestBody = createRequestBody(DISEASE, "Rare disease", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "New description on MAIN");
		final String descriptionId = assertComponentCreated(createMainPath(), SnomedComponentType.DESCRIPTION, requestBody);
		
		final Map<Object, Object> dupRequestBody = Maps.<Object, Object>newHashMap(requestBody);
		dupRequestBody.put("id", descriptionId);
		dupRequestBody.put("commitComment", "New duplicate description on MAIN");
		assertComponentCreatedWithStatus(createMainPath(), SnomedComponentType.DESCRIPTION, dupRequestBody, 409);
	}

	@Test
	public void createDescriptionCaseInsensitive() {
		final Map<?, ?> requestBody = createRequestBody(DISEASE, "Rare disease", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, CaseSignificance.CASE_INSENSITIVE, "New description on MAIN");
		final String descriptionId = assertComponentCreated(createMainPath(), SnomedComponentType.DESCRIPTION, requestBody);
		assertCaseSignificance(createMainPath(), descriptionId, CaseSignificance.CASE_INSENSITIVE);
	}

	@Test
	public void deleteDescription() {
		final Map<?, ?> requestBody = createRequestBody(DISEASE, "Rare disease", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "New description on MAIN");
		final String descriptionId = assertComponentCreated(createMainPath(), SnomedComponentType.DESCRIPTION, requestBody);

		assertDescriptionCanBeDeleted(createMainPath(), descriptionId);
		assertDescriptionNotExists(createMainPath(), descriptionId);
	}

	private void assertDescriptionCanBeDeleted(final IBranchPath branchPath, final String descriptionId) {
		assertComponentCanBeDeleted(branchPath, SnomedComponentType.DESCRIPTION, descriptionId);
	}

	private void assertDescriptionCanBeUpdated(final IBranchPath branchPath, final String descriptionId, final Map<?, ?> requestBody) {
		assertComponentCanBeUpdated(branchPath, SnomedComponentType.DESCRIPTION, descriptionId, requestBody);
	}

	@Test
	public void inactivateDescription() {
		final Map<?, ?> createRequestBody = createRequestBody(DISEASE, "Rare disease", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "New description on MAIN");
		final String descriptionId = assertComponentCreated(createMainPath(), SnomedComponentType.DESCRIPTION, createRequestBody);
		assertActive(createMainPath(), descriptionId, true);

		final Map<?, ?> updateRequestBody = ImmutableMap.builder()
				.put("active", false)
				.put("commitComment", "Inactivated description")
				.build();

		assertDescriptionCanBeUpdated(createMainPath(), descriptionId, updateRequestBody);
		assertActive(createMainPath(), descriptionId, false);
	}

	@Test
	public void inactivateDescriptionWithIndicator() {
		final IBranchPath branch = BranchPathUtils.createMainPath();
		final Map<?, ?> createRequestBody = createRequestBody(DISEASE, "Rare disease 2", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "New description Rare disease 2");
		final String descriptionId = assertComponentCreated(branch, SnomedComponentType.DESCRIPTION, createRequestBody);
		final Map<?, ?> updateRequestBody = ImmutableMap.builder()
				.put("active", false)
				.put("inactivationIndicator", DescriptionInactivationIndicator.DUPLICATE)
				.put("commitComment", "Inactivated description")
				.build();

		assertDescriptionCanBeUpdated(branch, descriptionId, updateRequestBody);
		assertComponentExists(branch, SnomedComponentType.DESCRIPTION, descriptionId, "inactivationProperties()")
			.and()
			.body("active", equalTo(false))
			.and()
			.body("inactivationIndicator", equalTo(InactivationIndicator.DUPLICATE.toString()));
	}

	@Test
	public void updateInactivationIndicatorAfterInactivation() {
		final IBranchPath branch = BranchPathUtils.createMainPath();
		final Map<?, ?> createRequestBody = createRequestBody(DISEASE, "Rare disease 3", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "New description Rare disease 3");
		final String descriptionId = assertComponentCreated(branch, SnomedComponentType.DESCRIPTION, createRequestBody);
		Map<?, ?> updateRequestBody = ImmutableMap.builder()
				.put("active", false)
				.put("inactivationIndicator", DescriptionInactivationIndicator.DUPLICATE)
				.put("commitComment", "Inactivated description")
				.build();

		assertDescriptionCanBeUpdated(branch, descriptionId, updateRequestBody);
		assertComponentExists(branch, SnomedComponentType.DESCRIPTION, descriptionId, "inactivationProperties()")
			.and()
			.body("active", equalTo(false))
			.and()
			.body("inactivationIndicator", equalTo(DescriptionInactivationIndicator.DUPLICATE.toString()));

		updateRequestBody = ImmutableMap.builder()
				.put("active", false)
				.put("inactivationIndicator", DescriptionInactivationIndicator.OUTDATED)
				.put("commitComment", "Changed inactivation indicator to " + DescriptionInactivationIndicator.OUTDATED)
				.build();
		assertDescriptionCanBeUpdated(branch, descriptionId, updateRequestBody);
		assertComponentExists(branch, SnomedComponentType.DESCRIPTION, descriptionId, "inactivationProperties()")
			.and()
			.body("active", equalTo(false))
			.and()
			.body("inactivationIndicator", equalTo(DescriptionInactivationIndicator.OUTDATED.toString()));
	}
	
	@Test
	public void inactivateDescriptionWithIndicatorAndAssociationTarget() {
		final IBranchPath branch = BranchPathUtils.createMainPath();
		final Map<?, ?> createRequestBody1 = createRequestBody(DISEASE, "Rare disease 4", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "New description Rare disease 4");
		final Map<?, ?> createRequestBody2 = createRequestBody(DISEASE, "Rare disease 4", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "New description Rare disease 4");
		
		final String descriptionId = assertComponentCreated(branch, SnomedComponentType.DESCRIPTION, createRequestBody1);
		final String sameAsDescriptionId = assertComponentCreated(branch, SnomedComponentType.DESCRIPTION, createRequestBody2);
		
		final Map<?, ?> updateRequestBody = ImmutableMap.builder()
				.put("active", false)
				.put("inactivationIndicator", DescriptionInactivationIndicator.DUPLICATE)
				.put("associationTargets", ImmutableMap.builder()
						.put(AssociationType.POSSIBLY_EQUIVALENT_TO.name(), newArrayList(sameAsDescriptionId))
						.build())
				.put("commitComment", "Inactivated description with DUPLICATE indicator and SAME_AS association target")
				.build();

		assertDescriptionCanBeUpdated(branch, descriptionId, updateRequestBody);
		assertComponentExists(branch, SnomedComponentType.DESCRIPTION, descriptionId, "inactivationProperties()")
			.and()
			.body("active", equalTo(false))
			.and()
			.body("inactivationIndicator", equalTo(InactivationIndicator.DUPLICATE.toString()))
			.and()
			.body("associationTargets." + AssociationType.POSSIBLY_EQUIVALENT_TO.name(), hasItem(sameAsDescriptionId));
	}

	@Test
	public void updateCaseSignificance() {
		final Map<?, ?> createRequestBody = createRequestBody(DISEASE, "Rare disease", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "New description on MAIN");
		final String descriptionId = assertComponentCreated(createMainPath(), SnomedComponentType.DESCRIPTION, createRequestBody);
		assertCaseSignificance(createMainPath(), descriptionId, CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE);

		final Map<?, ?> updateRequestBody = ImmutableMap.builder()
				.put("caseSignificance", CaseSignificance.CASE_INSENSITIVE.name())
				.put("commitComment", "Changed description case significance")
				.build();

		assertDescriptionCanBeUpdated(createMainPath(), descriptionId, updateRequestBody);
		assertCaseSignificance(createMainPath(), descriptionId, CaseSignificance.CASE_INSENSITIVE);
	}

	@Test
	public void updateAcceptability() {
		final Map<?, ?> createRequestBody = createRequestBody(DISEASE, "Rare disease", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "New description on MAIN");
		final String descriptionId = assertComponentCreated(createMainPath(), SnomedComponentType.DESCRIPTION, createRequestBody);
		final Collection<Map<String, Object>> members = assertDescriptionExists(createMainPath(), descriptionId, "members()").extract().body().path("members.items");
		final Map<String, Object> member = Iterables.getOnlyElement(members);
		
		final Map<?, ?> updateRequestBody = ImmutableMap.builder()
				.put("acceptability", SnomedApiTestConstants.PREFERRED_ACCEPTABILITY_MAP)
				.put("commitComment", "Changed description acceptability")
				.build();

		assertDescriptionCanBeUpdated(createMainPath(), descriptionId, updateRequestBody);
		final Collection<Map<String, Object>> updatedMembers = assertDescriptionExists(createMainPath(), descriptionId, "members()").extract().body().path("members.items");
		final Map<String, Object> updatedMember = Iterables.getOnlyElement(updatedMembers);
		// changing acceptability should change the acceptabilityId of the same member and not create a new one
		assertEquals(member.get("id"), updatedMember.get("id"));
		assertEquals(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED, updatedMember.get("acceptabilityId"));
		
		assertPreferredTermEquals(createMainPath(), DISEASE, descriptionId);
	}
	
	@Test
	public void updateAcceptabilityAndInactivate() {
		final String ptId = givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.with().header("Accept-Language", "en-GB")
				.when().get("/{path}/concepts/{conceptId}/pt", createMainPath(), DISEASE)
				.then().extract()
				.body().path("id");

		final Map<?, ?> createRequestBody = createRequestBody(DISEASE, "Rare disease 2", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "New description on MAIN");
		final String descriptionId = assertComponentCreated(createMainPath(), SnomedComponentType.DESCRIPTION, createRequestBody);
		final Map<?, ?> updateRequestBody = ImmutableMap.builder()
				.put("acceptability", SnomedApiTestConstants.PREFERRED_ACCEPTABILITY_MAP)
				.put("active", false)
				.put("commitComment", "Changed description acceptability and inactivated it at the same time")
				.build();
		
		assertDescriptionCanBeUpdated(createMainPath(), descriptionId, updateRequestBody);
		assertPreferredTermEquals(createMainPath(), DISEASE, ptId);
	}

	@Test
	public void createDescriptionOnNestedBranch() {
		SnomedBranchingApiAssert.givenBranchWithPath(testBranchPath);
		final IBranchPath nestedBranchPath = createNestedBranch(testBranchPath, "a", "b");
		final Map<?, ?> createRequestBody = createRequestBody(DISEASE, "Rare disease", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "New description on MAIN");
		final String descriptionId = assertComponentCreated(nestedBranchPath, SnomedComponentType.DESCRIPTION, createRequestBody);		

		assertDescriptionExists(nestedBranchPath, descriptionId);
		assertDescriptionNotExists(nestedBranchPath.getParent(), descriptionId);
		assertDescriptionNotExists(nestedBranchPath.getParent().getParent(), descriptionId);
		assertDescriptionNotExists(nestedBranchPath.getParent().getParent().getParent(), descriptionId);
	}

	@Test
	public void deleteDescriptionOnNestedBranch() {
		SnomedBranchingApiAssert.givenBranchWithPath(testBranchPath);
		final IBranchPath nestedBranchPath = createNestedBranch(testBranchPath, "a", "b");
		final Map<?, ?> createRequestBody = createRequestBody(DISEASE, "Rare disease", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "New description on MAIN");
		final String descriptionId = assertComponentCreated(nestedBranchPath, SnomedComponentType.DESCRIPTION, createRequestBody);		

		assertDescriptionCanBeDeleted(nestedBranchPath, descriptionId);
		assertDescriptionNotExists(nestedBranchPath, descriptionId);
		assertDescriptionNotExists(nestedBranchPath.getParent(), descriptionId);
		assertDescriptionNotExists(nestedBranchPath.getParent().getParent(), descriptionId);
		assertDescriptionNotExists(nestedBranchPath.getParent().getParent().getParent(), descriptionId);
	}
	
	@Test
	public void addNewLanguageReferenceSetMemberToDescription() throws Exception {
		final Map<?, ?> createRequestBody = createRequestBody(DISEASE, "Rare disease", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "New description on MAIN");
		final String descriptionId = assertComponentCreated(createMainPath(), SnomedComponentType.DESCRIPTION, createRequestBody);
		
		final Map<?, ?> updateRequestBody = ImmutableMap.builder()
				.put("acceptability", ImmutableMap.of(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.ACCEPTABLE, Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.PREFERRED))
				.put("commitComment", "Add new preferred acceptability to description")
				.build();

		assertDescriptionCanBeUpdated(createMainPath(), descriptionId, updateRequestBody);
		
		final Collection<String> memberIds = assertDescriptionExists(createMainPath(), descriptionId, "members()").and().extract().body().path("members.items.id");
		assertEquals(2, memberIds.size());
		final Collection<String> memberReferenceSetIds = assertDescriptionExists(createMainPath(), descriptionId, "members()").and().extract().body().path("members.items.referenceSetId");
		assertThat(memberReferenceSetIds, CoreMatchers.hasItems(Concepts.REFSET_LANGUAGE_TYPE_UK, Concepts.REFSET_LANGUAGE_TYPE_US));
		
		assertPreferredTermEquals(createMainPath(), DISEASE, descriptionId, "en-US");
	}
	
	@Test
	public void reactivateDescription() throws Exception {
		givenBranchWithPath(testBranchPath);
		final Map<?, ?> updateRequestBody = ImmutableMap.builder()
				.put("active", true)
				.put("acceptability", ImmutableMap.of(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.ACCEPTABLE))
				.put("commitComment", "Reactivate released description")
				.build();
		assertDescriptionCanBeUpdated(testBranchPath, INACTIVE_DISEASE_DESCRIPTION, updateRequestBody);
		
		final ValidatableResponse getDescriptionResponse = assertDescriptionExists(testBranchPath, INACTIVE_DISEASE_DESCRIPTION, "members()");
		final Collection<String> memberIds = getDescriptionResponse.and().extract().body().path("members.items.id");
		assertEquals(1, memberIds.size());
		assertThat(memberIds, CoreMatchers.hasItem(INACTIVE_DISEASE_DESCRIPTION_MEMBER_ID));
		final Collection<Boolean> statuses = getDescriptionResponse.and().extract().body().path("members.items.active");
		assertThat(statuses, CoreMatchers.hasItem(true));
		final Collection<String> effectiveTimes = getDescriptionResponse.and().extract().body().path("members.items.effectiveTime");
		assertNull(Iterables.getOnlyElement(effectiveTimes));
	}
	
	@Test
	public void inactivateReactivatedDescription() throws Exception {
		reactivateDescription();
		final Map<?, ?> updateRequestBody = ImmutableMap.builder()
				.put("active", false)
				.put("acceptability", ImmutableMap.of())
				.put("commitComment", "Inactivate reactivated released description")
				.build();
		assertDescriptionCanBeUpdated(testBranchPath, INACTIVE_DISEASE_DESCRIPTION, updateRequestBody);
		
		final ValidatableResponse getDescriptionResponse = assertDescriptionExists(testBranchPath, INACTIVE_DISEASE_DESCRIPTION, "members()");
		final Collection<String> memberIds = getDescriptionResponse.and().extract().body().path("members.items.id");
		assertEquals(1, memberIds.size());
		assertThat(memberIds, CoreMatchers.hasItem(INACTIVE_DISEASE_DESCRIPTION_MEMBER_ID));
		final Collection<Boolean> statuses = getDescriptionResponse.and().extract().body().path("members.items.active");
		assertThat(statuses, CoreMatchers.hasItem(false));
		final Collection<String> effectiveTimes = getDescriptionResponse.and().extract().body().path("members.items.effectiveTime");
		assertThat(effectiveTimes, CoreMatchers.hasItem(INACTIVE_DISEASE_DESCRIPTION_EFFECTIVE_TIME));
	}
	
	@Test
	public void deleteLanguageMemberFromDescription() throws Exception {
		final Map<?, ?> createRequestBody = createRequestBuilder(DISEASE,
				"Rare disease", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "New description on MAIN", ImmutableMap
						.of(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.ACCEPTABLE, Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.PREFERRED))
								.build();
		final String descriptionId = assertComponentCreated(createMainPath(), SnomedComponentType.DESCRIPTION, createRequestBody);
		
		final Map<?, ?> updateRequestBody = ImmutableMap.builder()
				.put("acceptability", ImmutableMap.of(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.ACCEPTABLE))
				.put("commitComment", "Remove preferred acceptability from description")
				.build();
		
		assertDescriptionCanBeUpdated(createMainPath(), descriptionId, updateRequestBody);
		
		final Collection<String> memberIds = assertDescriptionExists(createMainPath(), descriptionId, "members()").and().extract().body().path("members.items.id");
		assertEquals(1, memberIds.size());
		final Collection<String> memberReferenceSetIds = assertDescriptionExists(createMainPath(), descriptionId, "members()").and().extract().body().path("members.items.referenceSetId");
		assertThat(memberReferenceSetIds, CoreMatchers.hasItems(Concepts.REFSET_LANGUAGE_TYPE_UK));
		final Collection<String> acceptabilityIds = assertDescriptionExists(createMainPath(), descriptionId, "members()").and().extract().body().path("members.items.acceptabilityId");
		assertThat(acceptabilityIds, CoreMatchers.hasItems(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_ACCEPTABLE));
	}
	
	@Test
	public void issue_fixingDuplicateLanguageMemberInRefSetClearsAcceptabilityMapOnDescription() throws Exception {
		givenBranchWithPath(testBranchPath);
		// create description
		final Map<?, ?> createRequestBody = createRequestBuilder(DISEASE, "Rare disease", Concepts.MODULE_SCT_CORE, Concepts.SYNONYM, "New description on MAIN", SnomedApiTestConstants.PREFERRED_ACCEPTABILITY_MAP).build();
		final String descriptionId = assertComponentCreated(testBranchPath, SnomedComponentType.DESCRIPTION, createRequestBody);
		// inject duplicate inactive language member with different acceptability (API won't allow it) 
		final String memberToUpdate = UUID.randomUUID().toString();
		try (final SnomedEditingContext context = new SnomedEditingContext(testBranchPath)) {
			final SnomedLanguageRefSetMember member = SnomedRefSetFactory.eINSTANCE.createSnomedLanguageRefSetMember();
			member.setUuid(memberToUpdate);
			member.setActive(false);
			member.setModuleId(Concepts.MODULE_SCT_CORE);
			member.setRefSet(context.lookup(Concepts.REFSET_LANGUAGE_TYPE_UK, SnomedRefSet.class));
			member.setReferencedComponentId(descriptionId);
			member.setAcceptabilityId(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_ACCEPTABLE);
			final Description description = context.lookup(descriptionId, Description.class);
			description.getLanguageRefSetMembers().add(member);
			context.commit("Add fake member to " + descriptionId);
		}
		// check the acceptability map after the member injection, it should be preferred in UK lang refset
		final Map<String, Object> acceptabilityMap = assertDescriptionExists(testBranchPath, descriptionId).extract().body().path("acceptabilityMap");
		assertEquals(ImmutableMap.of(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED.name()), acceptabilityMap);
		final Collection<Map<String, Object>> members = getDescriptionMembers(testBranchPath, descriptionId);
		// but there should be two members, one is inactive ACCEPTABLE and one is active PREFERRED
		assertEquals(2, members.size());
		String memberToDelete = null;
		for (Map<String, Object> member : members) {
			if (Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED.equals(member.get(SnomedRf2Headers.FIELD_ACCEPTABILITY_ID))) {
				memberToDelete = (String) member.get(SnomedRf2Headers.FIELD_ID);
				break;
			}
		}
		
		// using bulk update, remove the currently active member and reactive the inactive one with new acceptability
		final Collection<Map<String, Object>> bulkRequests = newArrayList();
		bulkRequests.add(ImmutableMap.<String, Object>of("action", "update", "memberId", memberToUpdate, "active", true, "acceptabilityId", Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED));
		bulkRequests.add(ImmutableMap.<String, Object>of("action", "delete", "memberId", memberToDelete));
		final Map<String, Object> bulk = ImmutableMap.<String, Object>of("requests", bulkRequests, "commitComment", "Delete and update members");
		
		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
			.accept(ContentType.JSON)
			.contentType(ContentType.JSON)
			.body(bulk)
			.put("/{path}/refsets/{id}/members", testBranchPath.getPath(), Concepts.REFSET_LANGUAGE_TYPE_UK)
			.then()
			.log().ifValidationFails()
			.statusCode(204);
		
		// assert that description acceptability is still preferred, but there is only one member available
		final Map<String, Object> newAcceptabilityMap = assertDescriptionExists(testBranchPath, descriptionId).extract().body().path("acceptabilityMap");
		assertEquals(ImmutableMap.of(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED.name()), newAcceptabilityMap);
		final Collection<Map<String, Object>> updatedMembers = getDescriptionMembers(testBranchPath, descriptionId);
		assertEquals(1, updatedMembers.size());
		final Map<String, Object> updatedMember = Iterables.getOnlyElement(updatedMembers);
		assertEquals(true, updatedMember.get(SnomedRf2Headers.FIELD_ACTIVE));
		assertEquals(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED, updatedMember.get(SnomedRf2Headers.FIELD_ACCEPTABILITY_ID));
	}
	
	private Collection<Map<String, Object>> getDescriptionMembers(IBranchPath branchPath, String descriptionId) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
			.when().get("/{path}/{componentType}?referencedComponentId={componentId}", branchPath.getPath(), SnomedComponentType.MEMBER.toLowerCasePlural(), descriptionId)
			.then().log().ifValidationFails().extract().body().path("items");
	}
}
