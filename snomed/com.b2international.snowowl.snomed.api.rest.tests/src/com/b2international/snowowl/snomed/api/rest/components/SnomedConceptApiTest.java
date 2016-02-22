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
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.IS_A;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.MODULE_SCT_CORE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.ROOT_CONCEPT;
import static com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants.INVALID_ACCEPTABILITY_MAP;
import static com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants.PREFERRED_ACCEPTABILITY_MAP;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.givenBranchWithPath;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.whenDeletingBranchWithPath;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.*;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.asPath;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.postJson;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.AssociationType;
import com.b2international.snowowl.snomed.core.domain.InactivationIndicator;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.response.Response;

/**
 * @since 2.0
 */
public class SnomedConceptApiTest extends AbstractSnomedApiTest {

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
		.and().body("violations", hasItem("'parentId' may not be empty (was '')"));
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
		final SnomedIdentifiers snomedIdentifiers = new SnomedIdentifiers(identifierService);
		final String conceptId = snomedIdentifiers.generate(null, ComponentCategory.CONCEPT);
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
		final SnomedIdentifiers snomedIdentifiers = new SnomedIdentifiers(identifierService);
		final String conceptId = snomedIdentifiers.generate(null, ComponentCategory.CONCEPT);
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
		final Response response = postJson(SnomedApiTestConstants.SCT_API, inactivationBody, asPath(newArrayList(testBranchPath.getPath(), "concepts", componentId, "updates")));
		response.then().statusCode(204);
		assertComponentHasProperty(testBranchPath, SnomedComponentType.CONCEPT, componentId, "active", false);
	}
	
	@Test
	public void updateAssociationTargetOnConcepts() throws Exception {
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
	public void createDuplicateConcept() throws Exception {
		final Map<?, ?> requestBody = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		final String conceptId = assertComponentCreated(createMainPath(), SnomedComponentType.CONCEPT, requestBody);
		// component should not be created with duplicate ID
		assertComponentCreatedWithStatus(createMainPath(), SnomedComponentType.CONCEPT, ImmutableMap.<Object, Object>builder().put("id", conceptId).putAll(requestBody).build(), 409);
	}
	
}
