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
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.MODULE_SCT_CORE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.ROOT_CONCEPT;
import static com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants.INVALID_ACCEPTABILITY_MAP;
import static com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants.PREFERRED_ACCEPTABILITY_MAP;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.whenDeletingBranchWithPath;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.givenBranchWithPath;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentCreated;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentCreatedWithStatus;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentNotCreated;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.givenConceptRequestBody;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;

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
}
