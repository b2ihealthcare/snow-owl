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
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentCreated;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentCreatedWithStatus;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentHasProperty;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentNotCreated;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.givenConceptRequestBody;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.givenRelationshipRequestBody;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.asPath;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.postJson;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
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
		final String conceptId = SnomedIdentifiers.generateConceptId();
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
		final String conceptId = SnomedIdentifiers.generateConceptId();
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
		final String newConceptId = "91559698001"; // randomly generated concept ID
		final Map<?, ?> body = givenConceptRequestBody(newConceptId, DISEASE, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		assertComponentCreated(createMainPath(), SnomedComponentType.CONCEPT, body);
		// try creating a relationship between the ROOT_CONCEPT and the newConceptId
		final Map<?, ?> newRelationshipBody = givenRelationshipRequestBody(DISEASE, IS_A, newConceptId, MODULE_SCT_CORE, "Trying to create a 1 long ISA cycle");
		assertComponentNotCreated(createMainPath(), SnomedComponentType.RELATIONSHIP, newRelationshipBody);
	}
	
	@Test
	public void createConceptISACycle_Long() throws Exception {
		final String newConceptId = "80844557002"; // randomly generated concept ID
		final Map<?, ?> body = givenConceptRequestBody(newConceptId, DISEASE, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		assertComponentCreated(createMainPath(), SnomedComponentType.CONCEPT, body);
		
		final String newConceptId2 = "45933887007"; // randomly generated concept ID
		final Map<?, ?> body2 = givenConceptRequestBody(newConceptId2, newConceptId, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		assertComponentCreated(createMainPath(), SnomedComponentType.CONCEPT, body2);
		
		final Map<?, ?> newRelationshipBody = givenRelationshipRequestBody(DISEASE, IS_A, newConceptId2, MODULE_SCT_CORE, "Trying to create a 2 long ISA cycle");
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
	public void createDuplicateConcept() throws Exception {
		final Map<?, ?> requestBody = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		final String conceptId = assertComponentCreated(createMainPath(), SnomedComponentType.CONCEPT, requestBody);
		// component should not be created with duplicate ID
		assertComponentCreatedWithStatus(createMainPath(), SnomedComponentType.CONCEPT, ImmutableMap.<Object, Object>builder().put("id", conceptId).putAll(requestBody).build(), 409);
	}
	
}
