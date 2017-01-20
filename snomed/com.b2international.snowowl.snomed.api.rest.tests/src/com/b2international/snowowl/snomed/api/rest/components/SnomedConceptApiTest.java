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
package com.b2international.snowowl.snomed.api.rest.components;

import static com.b2international.snowowl.datastore.BranchPathUtils.createMainPath;
import static com.b2international.snowowl.datastore.BranchPathUtils.createPath;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.IS_A;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.MODULE_SCT_CORE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.ROOT_CONCEPT;
import static com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants.ACCEPTABLE_ACCEPTABILITY_MAP;
import static com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants.INVALID_ACCEPTABILITY_MAP;
import static com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants.PREFERRED_ACCEPTABILITY_MAP;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.givenBranchWithPath;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.whenDeletingBranchWithPath;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentCanBeDeleted;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentCanBeUpdated;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentCreated;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentCreatedWithStatus;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentExists;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentHasProperty;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentNotCreated;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentNotExists;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.createRefSetMemberRequestBody;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.createRefSetRequestBody;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.givenConceptRequestBody;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.givenRelationshipRequestBody;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.hamcrest.CoreMatchers.either;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.AssociationType;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.InactivationIndicator;
import com.b2international.snowowl.snomed.core.domain.RelationshipModifier;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.jayway.restassured.response.ValidatableResponse;

/**
 * @since 2.0
 */
public class SnomedConceptApiTest extends AbstractSnomedApiTest {

	// Values below were picked from the minified dataset, representing an inactive concept
	private static final String INACTIVE_CONCEPT_ID = "118225008";
	private static final InactivationIndicator INACTIVE_CONCEPT_REASON = InactivationIndicator.AMBIGUOUS;
	private static final List<String> INACTIVE_CONCEPT_EQUIVALENTS = ImmutableList.of("118222006", "250171008", "413350009");
	
	@Test
	public void createConceptNonExistentBranch() {
		final Map<?, ?> requestBody = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		assertComponentCreatedWithStatus(createPath("MAIN/1998-01-31"), SnomedComponentType.CONCEPT, requestBody, 404)
		.and().body("status", equalTo(404));
	}

	@Test
	public void createConceptWithoutParent() {
		final Map<?, ?> requestBody = givenConceptRequestBody(null, "", MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);		
		assertComponentCreatedWithStatus(createMainPath(), SnomedComponentType.CONCEPT, requestBody, 400)
		.and().body("message", equalTo("1 validation error"))
		.and().body("violations", hasItem("'destinationId' may not be empty (was '')"));
	}

	@Test
	public void createConceptWithNonexistentParent() {
		final Map<?, ?> requestBody = givenConceptRequestBody(null, "1000", MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		assertComponentNotCreated(createMainPath(), SnomedComponentType.CONCEPT, requestBody);
	}

	@Test
	public void createConceptWithNonexistentLanguageRefSet() {
		final Map<?, ?> requestBody = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, INVALID_ACCEPTABILITY_MAP, false);
		assertComponentNotCreated(createMainPath(), SnomedComponentType.CONCEPT, requestBody);
	}

	@Test
	public void createConceptWithNonexistentModule() {
		final Map<?, ?> requestBody = givenConceptRequestBody(null, ROOT_CONCEPT, "1", PREFERRED_ACCEPTABILITY_MAP, false);
		assertComponentNotCreated(createMainPath(), SnomedComponentType.CONCEPT, requestBody);
	}

	@Test
	public void createConceptWithoutCommitComment() {
		final Map<?, ?> requestBody = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, true);
		assertComponentNotCreated(createMainPath(), SnomedComponentType.CONCEPT, requestBody);
	}

	@Test
	public void createConcept() {
		final Map<?, ?> requestBody = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		assertComponentCreated(createMainPath(), SnomedComponentType.CONCEPT, requestBody);
	}

	@Test
	public void createConceptWithGeneratedId() {
		final ISnomedIdentifierService identifierService = ApplicationContext.getInstance().getServiceChecked(ISnomedIdentifierService.class);
		final String conceptId = Iterables.getOnlyElement(identifierService.reserve(null, ComponentCategory.CONCEPT, 1));
		final Map<?, ?> requestBody = givenConceptRequestBody(conceptId, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);		
		final String createdId = assertComponentCreated(createMainPath(), SnomedComponentType.CONCEPT, requestBody);
		assertEquals("Pre-generated and returned concept ID should match.", conceptId, createdId);
	}

	@Test
	public void createConceptOnBranch() {
		givenBranchWithPath(testBranchPath);
		final Map<?, ?> requestBody = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		assertComponentCreated(testBranchPath, SnomedComponentType.CONCEPT, requestBody);
	}

	@Test
	public void createConceptWithGeneratedIdOnBranch() {
		givenBranchWithPath(testBranchPath);
		final ISnomedIdentifierService identifierService = ApplicationContext.getInstance().getServiceChecked(ISnomedIdentifierService.class);
		final String conceptId = Iterables.getOnlyElement(identifierService.reserve(null, ComponentCategory.CONCEPT, 1));
		final Map<?, ?> requestBody = givenConceptRequestBody(conceptId, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		final String createdId = assertComponentCreated(testBranchPath, SnomedComponentType.CONCEPT, requestBody);
		assertEquals("Pre-generated and returned concept ID should match.", conceptId, createdId);
	}

	@Test
	public void createConceptOnDeletedBranch() {
		givenBranchWithPath(testBranchPath);
		whenDeletingBranchWithPath(testBranchPath);
		final Map<?, ?> requestBody = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);		
		assertComponentNotCreated(testBranchPath, SnomedComponentType.CONCEPT, requestBody);
	}
	
	@Test
	public void createConceptISACycle_Simple() throws Exception {
		final Map<?, ?> body = givenConceptRequestBody(null, DISEASE, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		String conceptId = assertComponentCreated(createMainPath(), SnomedComponentType.CONCEPT, body);
		// try creating a relationship between the ROOT_CONCEPT and the newConceptId
		final Map<?, ?> newRelationshipBody = givenRelationshipRequestBody(DISEASE, IS_A, conceptId, MODULE_SCT_CORE, "Trying to create a 1 long ISA cycle");
		assertComponentNotCreated(createMainPath(), SnomedComponentType.RELATIONSHIP, newRelationshipBody);
	}
	
	@Test
	public void createConceptISACycle_Long() throws Exception {
		final Map<?, ?> body = givenConceptRequestBody(null, DISEASE, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		final String conceptId = assertComponentCreated(createMainPath(), SnomedComponentType.CONCEPT, body);
		
		final Map<?, ?> body2 = givenConceptRequestBody(null, conceptId, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		assertComponentCreated(createMainPath(), SnomedComponentType.CONCEPT, body2);
		
		final Map<?, ?> newRelationshipBody = givenRelationshipRequestBody(DISEASE, IS_A, conceptId, MODULE_SCT_CORE, "Trying to create a 2 long ISA cycle");
		assertComponentNotCreated(createMainPath(), SnomedComponentType.RELATIONSHIP, newRelationshipBody);
	}
	
	@Test
	public void inactivateConcept() throws Exception {
		givenBranchWithPath(testBranchPath);
		final Map<?, ?> body = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		final String componentId = assertComponentCreated(testBranchPath, SnomedComponentType.CONCEPT, body);
		final Map<String, Object> inactivationBody = newHashMap();
		inactivationBody.put("active", false);
		inactivationBody.put("commitComment", "Inactivated " + componentId);
		assertComponentCanBeUpdated(testBranchPath, SnomedComponentType.CONCEPT, componentId, inactivationBody);
		assertComponentHasProperty(testBranchPath, SnomedComponentType.CONCEPT, componentId, "active", false);
	}
	
	@Test
	public void reactivateConcept() throws Exception {
		// create two concepts, add an additional relationship pointing from one to the other
		givenBranchWithPath(testBranchPath);
		final Map<?, ?> body = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		final String inactivatableConceptId = assertComponentCreated(testBranchPath, SnomedComponentType.CONCEPT, body);
		final String sourceConceptId = assertComponentCreated(testBranchPath, SnomedComponentType.CONCEPT, body);
		final Map<?, ?> relationshipReq = givenRelationshipRequestBody(sourceConceptId, Concepts.MORPHOLOGY, inactivatableConceptId, Concepts.MODULE_SCT_CORE, "New relationship");
		final String relationshipId = assertComponentCreated(testBranchPath, SnomedComponentType.RELATIONSHIP, relationshipReq);
		
		// inactivate the concept with the relationship is pointing to
		final Map<String, Object> inactivationBody = newHashMap();
		inactivationBody.put("active", false);
		inactivationBody.put("inactivationIndicator", InactivationIndicator.DUPLICATE);
		inactivationBody.put("associationTargets", ImmutableMap.builder().put(AssociationType.POSSIBLY_EQUIVALENT_TO, newArrayList(sourceConceptId)).build());
		inactivationBody.put("commitComment", "Inactivated " + inactivatableConceptId);
		assertComponentCanBeUpdated(testBranchPath, SnomedComponentType.CONCEPT, inactivatableConceptId, inactivationBody);
		assertComponentExists(testBranchPath, SnomedComponentType.CONCEPT, inactivatableConceptId)
			.and()
			.body("active", equalTo(false))
			.and()
			.body("inactivationIndicator", equalTo(InactivationIndicator.DUPLICATE.toString()))
			.and()
			.body("associationTargets." + AssociationType.POSSIBLY_EQUIVALENT_TO.name(), hasItem(sourceConceptId));
		
		// verify that the inbound relationship is inactive
		assertComponentExists(testBranchPath, SnomedComponentType.RELATIONSHIP, relationshipId).and().body("active", equalTo(false));
		
		// reactivate it
		final Map<String, Object> reactivationBody = newHashMap();
		reactivationBody.put("active", true);
		reactivationBody.put("commitComment", "Reactivated " + inactivatableConceptId);
		assertComponentCanBeUpdated(testBranchPath, SnomedComponentType.CONCEPT, inactivatableConceptId, reactivationBody);
		
		// assert that the concept is active again, it has two active descriptions, no association targets, no indicator, and 1 outbound relationship, and one inbound relationship
		assertComponentExists(testBranchPath, SnomedComponentType.CONCEPT, inactivatableConceptId)
			.and()
			.body("active", equalTo(true))
			.and()
			.body("inactivationIndicator", nullValue())
			.and()
			.body("associationTargets", nullValue());
		
		// verify that the inbound relationship is still inactive, manual reactivation is required
		assertComponentExists(testBranchPath, SnomedComponentType.RELATIONSHIP, relationshipId).and().body("active", equalTo(false));
	}
	
	@Test
	public void restoreEffectiveTimeOnReleasedConcept() throws Exception {
		givenBranchWithPath(testBranchPath);
		
		final Map<?, ?> reactivationBody = ImmutableMap.builder()
				.put("active", true)
				.put("commitComment", "Reactivated " + INACTIVE_CONCEPT_ID)
				.build();
		
		assertComponentCanBeUpdated(testBranchPath, SnomedComponentType.CONCEPT, INACTIVE_CONCEPT_ID, reactivationBody);
		
		final Map<?, ?> inactivationBody = ImmutableMap.builder()
				.put("active", false)
				.put("associationTargets", ImmutableMap.of(AssociationType.POSSIBLY_EQUIVALENT_TO, INACTIVE_CONCEPT_EQUIVALENTS))
				.put("inactivationIndicator", INACTIVE_CONCEPT_REASON.toString())
				.put("commitComment", "Reactivated " + INACTIVE_CONCEPT_ID)
				.build();
		
		assertComponentCanBeUpdated(testBranchPath, SnomedComponentType.CONCEPT, INACTIVE_CONCEPT_ID, inactivationBody);
		
		final ValidatableResponse conceptResponse = assertComponentExists(testBranchPath, SnomedComponentType.CONCEPT, INACTIVE_CONCEPT_ID, "members()");
		final Collection<String> memberIds = conceptResponse.and().extract().body().path("members.items.id");
		assertEquals(4, memberIds.size());

		final Collection<Boolean> statuses = conceptResponse.and().extract().body().path("members.items.active");
		assertThat(statuses, everyItem(is(true)));
		final Collection<String> effectiveTimes = conceptResponse.and().extract().body().path("members.items.effectiveTime");
		assertThat(effectiveTimes, everyItem(either(is("20050131")).or(is("20050731"))));
	}
	
	@Test
	public void updateAssociationTarget() throws Exception {
		// create concept and a duplicate
		givenBranchWithPath(testBranchPath);
		final Map<?, ?> body = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		final String componentId = assertComponentCreated(testBranchPath, SnomedComponentType.CONCEPT, body);
		final String duplicateComponentId = assertComponentCreated(testBranchPath, SnomedComponentType.CONCEPT, body);
		
		// inactivate the duplicate concept and point to the original one
		final Map<String, Object> inactivationBody = newHashMap();
		inactivationBody.put("active", false);
		inactivationBody.put("inactivationIndicator", InactivationIndicator.DUPLICATE);
		inactivationBody.put("associationTargets", ImmutableMap.builder().put(AssociationType.POSSIBLY_EQUIVALENT_TO, newArrayList(componentId)).build());
		inactivationBody.put("commitComment", "Inactivated " + duplicateComponentId);
		
		assertComponentCanBeUpdated(testBranchPath, SnomedComponentType.CONCEPT, duplicateComponentId, inactivationBody);
		// check if inactivation went through properly
		assertComponentExists(testBranchPath, SnomedComponentType.CONCEPT, duplicateComponentId)
			.and()
			.body("active", equalTo(false))
			.and()
			.body("inactivationIndicator", equalTo(InactivationIndicator.DUPLICATE.toString()))
			.and()
			.body("associationTargets." + AssociationType.POSSIBLY_EQUIVALENT_TO.name(), hasItem(componentId));
		
		// try to update the association target
		final Map<String, Object> associationTargetUpdateBody = newHashMap();
		associationTargetUpdateBody.put("active", false);
		associationTargetUpdateBody.put("inactivationIndicator", InactivationIndicator.AMBIGUOUS);
		associationTargetUpdateBody.put("associationTargets", ImmutableMap.builder().put(AssociationType.REPLACED_BY, newArrayList(componentId)).build());
		associationTargetUpdateBody.put("commitComment", "Changed association target to be replaced by instead in " + duplicateComponentId);
		assertComponentCanBeUpdated(testBranchPath, SnomedComponentType.CONCEPT, duplicateComponentId, associationTargetUpdateBody);

		// verify association target and inactivation indicator update
		assertComponentExists(testBranchPath, SnomedComponentType.CONCEPT, duplicateComponentId)
			.and()
			.body("active", equalTo(false))
			.and()
			.body("inactivationIndicator", equalTo(InactivationIndicator.AMBIGUOUS.toString()))
			.and()
			.body("associationTargets." + AssociationType.REPLACED_BY.name(), hasItem(componentId));
	}
	
	@Test
	public void updateAssociationTargetsWithReuse() throws Exception {
		// create concept and a duplicate
		givenBranchWithPath(testBranchPath);
		final Map<?, ?> body = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		final String componentId = assertComponentCreated(testBranchPath, SnomedComponentType.CONCEPT, body);
		final String duplicateComponentId = assertComponentCreated(testBranchPath, SnomedComponentType.CONCEPT, body);
		
		// inactivate the duplicate concept and point to the original one
		final Map<String, Object> inactivationBody = newHashMap();
		inactivationBody.put("active", false);
		inactivationBody.put("inactivationIndicator", InactivationIndicator.DUPLICATE);
		inactivationBody.put("associationTargets", ImmutableMap.builder().put(AssociationType.POSSIBLY_EQUIVALENT_TO, newArrayList(componentId)).build());
		inactivationBody.put("commitComment", "Inactivated " + duplicateComponentId);
		
		assertComponentCanBeUpdated(testBranchPath, SnomedComponentType.CONCEPT, duplicateComponentId, inactivationBody);
		// check if inactivation went through properly
		final Collection<String> memberIds = assertComponentExists(testBranchPath, SnomedComponentType.CONCEPT, duplicateComponentId, "members()")
				.body("active", equalTo(false))
				.body("inactivationIndicator", equalTo(InactivationIndicator.DUPLICATE.toString()))
				.body("associationTargets." + AssociationType.POSSIBLY_EQUIVALENT_TO.name(), hasItem(componentId))
				.extract()
				.body().path("members.items.id");

		// retrieve association member and store its UUID
		assertEquals(2, memberIds.size());
		
		// try to update the association target, switching the order of targets around
		final Map<String, Object> associationTargetUpdateBody = newHashMap();
		associationTargetUpdateBody.put("active", false);
		associationTargetUpdateBody.put("inactivationIndicator", InactivationIndicator.AMBIGUOUS);
		associationTargetUpdateBody.put("associationTargets", ImmutableMap.builder()
				.put(AssociationType.POSSIBLY_EQUIVALENT_TO, newArrayList(DISEASE, componentId))
				.put(AssociationType.REPLACED_BY, newArrayList(componentId))
				.build());
		associationTargetUpdateBody.put("commitComment", "Changed association targets on " + duplicateComponentId);
		assertComponentCanBeUpdated(testBranchPath, SnomedComponentType.CONCEPT, duplicateComponentId, associationTargetUpdateBody);
		
		// verify association target and inactivation indicator update
		final Collection<String> updatedMemberIds = assertComponentExists(testBranchPath, SnomedComponentType.CONCEPT, duplicateComponentId, "members()")
				.body("active", equalTo(false))
				.body("inactivationIndicator", equalTo(InactivationIndicator.AMBIGUOUS.toString()))
				.body("associationTargets." + AssociationType.POSSIBLY_EQUIVALENT_TO.name(), hasItem(componentId))
				.body("associationTargets." + AssociationType.POSSIBLY_EQUIVALENT_TO.name(), hasItem(DISEASE))
				.body("associationTargets." + AssociationType.REPLACED_BY.name(), hasItem(componentId))
				.extract()
				.body().path("members.items.id");
		
		// check that the member UUIDs have not been cycled
		assertEquals(4, updatedMemberIds.size());
		assertTrue(updatedMemberIds.containsAll(memberIds));
	}
	
	@Test
	public void createDuplicateConcept() throws Exception {
		final Map<?, ?> requestBody = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		final String conceptId = assertComponentCreated(createMainPath(), SnomedComponentType.CONCEPT, requestBody);

		final Map<Object, Object> dupRequestBody = Maps.<Object, Object>newHashMap(requestBody);
		dupRequestBody.put("id", conceptId);
		dupRequestBody.put("commitComment", "New duplicate concept on MAIN");
		assertComponentCreatedWithStatus(createMainPath(), SnomedComponentType.CONCEPT, dupRequestBody, 409);
	}
	
	@Test
	public void deleteConcept() {
		givenBranchWithPath(testBranchPath);
		final Map<?, ?> requestBody = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		final String conceptId = assertComponentCreated(testBranchPath, SnomedComponentType.CONCEPT, requestBody);
		assertComponentCanBeDeleted(testBranchPath, SnomedComponentType.CONCEPT, conceptId);
		assertComponentNotExists(testBranchPath, SnomedComponentType.CONCEPT, conceptId);
	}
	
	@Test
	public void deleteConceptOnNestedBranch() {
		givenBranchWithPath(testBranchPath);

		Map<?, ?> requestBody;
		String parentId = ROOT_CONCEPT;
		
		for (int i = 0; i < 10; i++) {
			requestBody = givenConceptRequestBody(null, parentId, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
			parentId = assertComponentCreated(testBranchPath, SnomedComponentType.CONCEPT, requestBody);
		}

		// New component on nested branch resets the container's version to 1 again
		final IBranchPath nestedBranchPath = createNestedBranch(testBranchPath, "A", "B");
		requestBody = givenConceptRequestBody(null, parentId, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		assertComponentCreated(nestedBranchPath, SnomedComponentType.CONCEPT, requestBody);

		// Deleting the last concept in the chain
		assertComponentCanBeDeleted(testBranchPath, SnomedComponentType.CONCEPT, parentId);
		assertComponentNotExists(testBranchPath, SnomedComponentType.CONCEPT, parentId);

		// Should still exist on the nested branch, and be possible to remove
		assertComponentCanBeDeleted(nestedBranchPath, SnomedComponentType.CONCEPT, parentId);
		assertComponentNotExists(nestedBranchPath, SnomedComponentType.CONCEPT, parentId);
	}
	
	@Test
	public void createConceptWithMember() throws Exception {
		givenBranchWithPath(testBranchPath);
		
		// create a test refset
		final Map<String,Object> refSetReq = createRefSetRequestBody(SnomedRefSetType.SIMPLE, SnomedTerminologyComponentConstants.CONCEPT, Concepts.REFSET_SIMPLE_TYPE);
		final String createdRefSetId = assertComponentCreated(testBranchPath, SnomedComponentType.REFSET, refSetReq);
		assertComponentExists(testBranchPath, SnomedComponentType.REFSET, createdRefSetId);
		
		// create concept with member
		final ImmutableMap.Builder<String, Object> req = ImmutableMap.builder();
		req.putAll(givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false));
		
		final ImmutableList.Builder<Map<String, Object>> members = ImmutableList.builder();
		members.add(createRefSetMemberRequestBody(null, createdRefSetId));
		req.put("members", members.build());
		
		// verify that member got created
		final String conceptId = assertComponentCreated(testBranchPath, SnomedComponentType.CONCEPT, req.build());
		final List<Object> actualMembers = assertComponentExists(testBranchPath, SnomedComponentType.CONCEPT, conceptId, "members()")
			.extract().path("members.items");
		assertEquals(1, actualMembers.size());
	}
	
	@Test
	public void addDescriptionViaConceptUpdate() throws Exception {
		givenBranchWithPath(testBranchPath);
		
		// create a concept
		final Map<String, Object> conceptCreateReq = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		final String newConceptId = assertComponentCreated(testBranchPath, SnomedComponentType.CONCEPT, conceptCreateReq);
		// get the current state of description list
		final SnomedConcept newConcept = assertComponentExists(testBranchPath, SnomedComponentType.CONCEPT, newConceptId, "descriptions()").extract().as(SnomedConcept.class);
		// two descriptions should exist at this point, one FSN, one PT
		assertEquals(2, newConcept.getDescriptions().getTotal());
		
		// update concept with a new Text Definition	
		final List<SnomedDescription> changedDescriptions = newArrayList(newConcept.getDescriptions()); 
		final ImmutableMap.Builder<String, Object> updateReq = ImmutableMap.builder();
		final SnomedDescription newDescription = new SnomedDescription();
		newDescription.setId(getIdentifierService().generate(null, ComponentCategory.DESCRIPTION));
		newDescription.setActive(true);
		newDescription.setAcceptabilityMap(ACCEPTABLE_ACCEPTABILITY_MAP);
		newDescription.setTypeId(Concepts.TEXT_DEFINITION);
		newDescription.setTerm("Text Definiton " + new Date());
		newDescription.setLanguageCode("en");
		newDescription.setCaseSignificance(CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE);
		newDescription.setModuleId(newConcept.getModuleId());
		changedDescriptions.add(newDescription);
		
		updateReq.put("commitComment", "Add new description via concept update");
		updateReq.put("descriptions", changedDescriptions);
		assertComponentCanBeUpdated(testBranchPath, SnomedComponentType.CONCEPT, newConceptId, updateReq.build());
		
		final SnomedConcept updatedConcept = assertComponentExists(testBranchPath, SnomedComponentType.CONCEPT, newConceptId, "descriptions()").extract().as(SnomedConcept.class);
		assertEquals(3, updatedConcept.getDescriptions().getTotal());
	}
	
	@Test
	public void addRelationshipViaConceptUpdate() throws Exception {
		givenBranchWithPath(testBranchPath);
		
		// create a concept
		final Map<String, Object> conceptCreateReq = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		final String newConceptId = assertComponentCreated(testBranchPath, SnomedComponentType.CONCEPT, conceptCreateReq);
		// get the current state of relationship list
		final SnomedConcept newConcept = assertComponentExists(testBranchPath, SnomedComponentType.CONCEPT, newConceptId, "relationships()").extract().as(SnomedConcept.class);
		// one relationship should exist at this point, one stated ISA to the parent
		assertEquals(1, newConcept.getRelationships().getTotal());
		
		// update concept with a new stated relationship	
		final List<SnomedRelationship> changedRelationships = newArrayList(newConcept.getRelationships()); 
		final ImmutableMap.Builder<String, Object> updateReq = ImmutableMap.builder();
		final SnomedRelationship newRelationships = new SnomedRelationship();
		newRelationships.setId(getIdentifierService().generate(null, ComponentCategory.RELATIONSHIP));
		newRelationships.setActive(true);
		newRelationships.setCharacteristicType(CharacteristicType.STATED_RELATIONSHIP);
		newRelationships.setTypeId(FINDING_CONTEXT);
		newRelationships.setDestinationId(DISEASE);
		newRelationships.setModuleId(newConcept.getModuleId());
		newRelationships.setGroup(0);
		newRelationships.setUnionGroup(0);
		newRelationships.setModifier(RelationshipModifier.EXISTENTIAL);
		changedRelationships.add(newRelationships);
		
		updateReq.put("commitComment", "Add new relationship via concept update");
		updateReq.put("relationships", changedRelationships);
		assertComponentCanBeUpdated(testBranchPath, SnomedComponentType.CONCEPT, newConceptId, updateReq.build());
		
		final SnomedConcept updatedConcept = assertComponentExists(testBranchPath, SnomedComponentType.CONCEPT, newConceptId, "relationships()").extract().as(SnomedConcept.class);
		assertEquals(2, updatedConcept.getRelationships().getTotal());
	}
	
	@Test
	public void addMemberViaConceptUpdate() throws Exception {
		givenBranchWithPath(testBranchPath);
		
		// create a test refset
		final Map<String,Object> refSetReq = createRefSetRequestBody(SnomedRefSetType.SIMPLE, SnomedTerminologyComponentConstants.CONCEPT, Concepts.REFSET_SIMPLE_TYPE);
		final String createdRefSetId = assertComponentCreated(testBranchPath, SnomedComponentType.REFSET, refSetReq);
		assertComponentExists(testBranchPath, SnomedComponentType.REFSET, createdRefSetId);
		
		// create a concept
		final Map<String, Object> conceptCreateReq = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		final String newConceptId = assertComponentCreated(testBranchPath, SnomedComponentType.CONCEPT, conceptCreateReq);
		// get the current state of relationship list
		final SnomedConcept newConcept = assertComponentExists(testBranchPath, SnomedComponentType.CONCEPT, newConceptId, "members()").extract().as(SnomedConcept.class);
		// members should be empty at this point
		assertEquals(0, newConcept.getMembers().getTotal());
		
		// add a new concrete domain member via concept update endpoint
		final List<SnomedReferenceSetMember> changedMembers = newArrayList(); 
		final ImmutableMap.Builder<String, Object> updateReq = ImmutableMap.builder();
		final SnomedReferenceSetMember newMember = new SnomedReferenceSetMember();
		newMember.setId(UUID.randomUUID().toString());
		newMember.setActive(true);
		newMember.setReferenceSetId(SnomedRefSetUtil.getConcreteDomainRefSetMap().get(DataType.STRING));
		newMember.setModuleId(newConcept.getModuleId());
		newMember.setProperties(ImmutableMap.<String, Object>builder()
				.put(SnomedRf2Headers.FIELD_ATTRIBUTE_NAME, FINDING_CONTEXT)
				.put(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID, Concepts.STATED_RELATIONSHIP)
				.put(SnomedRf2Headers.FIELD_VALUE, "Value")
				.build());
		changedMembers.add(newMember);
		
		updateReq.put("commitComment", "Add new concrete domain member via concept update");
		updateReq.put("members", changedMembers);
		assertComponentCanBeUpdated(testBranchPath, SnomedComponentType.CONCEPT, newConceptId, updateReq.build());
		
		final SnomedConcept updatedConcept = assertComponentExists(testBranchPath, SnomedComponentType.CONCEPT, newConceptId, "members()").extract().as(SnomedConcept.class);
		assertEquals(1, updatedConcept.getMembers().getTotal());
	}
	
	private ISnomedIdentifierService getIdentifierService() {
		return ApplicationContext.getInstance().getServiceChecked(ISnomedIdentifierService.class);
	}
	
}
