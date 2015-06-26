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

import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.assertBranchCreated;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.assertBranchDeleted;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.Map;

import org.junit.Test;

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.domain.Acceptability;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * @since 2.0
 */
public class SnomedConceptApiTest extends AbstractSnomedApiTest {

	private static final Map<?, ?> INVALID_ACCEPTABILITY_MAP = ImmutableMap.of(
		"1", Acceptability.PREFERRED
	);

	private Map<?, ?> createRequestBody(String conceptId, String parentId, String moduleId, Map<?, ?> acceptabilityMap, boolean skipComment) {
		Date creationDate = new Date();
		Builder<String, Object> builder = ImmutableMap.builder();

		builder.put("parentId", parentId);
		builder.put("moduleId", moduleId);
		builder.put("descriptions", ImmutableList.of(
			ImmutableMap.of(
				"typeId", Concepts.FULLY_SPECIFIED_NAME,
				"term", "New FSN at " + creationDate,
				"languageCode", "en",
				"acceptability", acceptabilityMap 
			),
			
			ImmutableMap.of(
				"typeId", Concepts.SYNONYM,
				"term", "New PT at " + creationDate,
				"languageCode", "en",
				"acceptability", SnomedApiTestConstants.PREFERRED_ACCEPTABILITY_MAP 
			)
		));
			
		if (conceptId != null) {
			builder.put("id", conceptId);
		}
		
		if (!skipComment) {
			builder.put("commitComment", "New concept");
		}
		
		return builder.build();
	}
	
	@Test
	public void createConceptNonExistentBranch() {
		final Map<?, ?> requestBody = createRequestBody(null, Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE, SnomedApiTestConstants.PREFERRED_ACCEPTABILITY_MAP, false);
		assertComponentCreationStatus("concepts", requestBody, 404, "MAIN", "1998-01-31") // !
		.and()
			.body("status", equalTo(404));
	}
	
	@Test
	public void createConceptWithoutParent() {
		final Map<?, ?> requestBody = createRequestBody(null, "", Concepts.MODULE_SCT_CORE, SnomedApiTestConstants.PREFERRED_ACCEPTABILITY_MAP, false);		
		assertComponentCanNotBeCreated("concepts", requestBody, "MAIN")
		.and()
			.body("message", equalTo("1 validation error"))
		.and()
			.body("violations", hasItem("'parentId' may not be empty (was '')"));
	}
	
	@Test
	public void createConceptWithNonexistentParent() {
		final Map<?, ?> requestBody = createRequestBody(null, "1000", Concepts.MODULE_SCT_CORE, SnomedApiTestConstants.PREFERRED_ACCEPTABILITY_MAP, false);
		assertComponentCanNotBeCreated("concepts", requestBody, "MAIN");
	}
	
	@Test
	public void createConceptWithNonexistentLanguageRefSet() {
		final Map<?, ?> requestBody = createRequestBody(null, Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE, INVALID_ACCEPTABILITY_MAP, false);
		assertComponentCanNotBeCreated("concepts", requestBody, "MAIN");
	}
	
	@Test
	public void createConceptWithNonexistentModule() {
		final Map<?, ?> requestBody = createRequestBody(null, Concepts.ROOT_CONCEPT, "1", SnomedApiTestConstants.PREFERRED_ACCEPTABILITY_MAP, false);
		assertComponentCanNotBeCreated("concepts", requestBody, "MAIN");
	}
	
	@Test
	public void createConceptWithoutCommitComment() {
		final Map<?, ?> requestBody = createRequestBody(null, Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE, SnomedApiTestConstants.PREFERRED_ACCEPTABILITY_MAP, true);
		assertComponentCanNotBeCreated("concepts", requestBody, "MAIN");
	}
	
	@Test
	public void createConcept() {
		final Map<?, ?> requestBody = createRequestBody(null, Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE, SnomedApiTestConstants.PREFERRED_ACCEPTABILITY_MAP, false);
		assertComponentCanBeCreated("concepts", requestBody, "MAIN");
	}
	
	@Test
	public void createConceptWithGeneratedId() {
		String conceptId = SnomedIdentifiers.generateConceptId();
		final Map<?, ?> requestBody = createRequestBody(conceptId, Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE, SnomedApiTestConstants.PREFERRED_ACCEPTABILITY_MAP, false);		
		String createdId = assertComponentCanBeCreated("concepts", requestBody, "MAIN");
		assertEquals("Pre-generated and returned concept ID should match.", conceptId, createdId);
	}
	
	@Test
	public void createConceptOnBranch() {
		assertBranchCreated(branchPath);
		final Map<?, ?> requestBody = createRequestBody(null, Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE, SnomedApiTestConstants.PREFERRED_ACCEPTABILITY_MAP, false);
		assertComponentCanBeCreated("concepts", requestBody, "MAIN", branchName);
	}
	
	@Test
	public void createConceptWithGeneratedIdOnBranch() {
		assertBranchCreated(branchPath);
		String conceptId = SnomedIdentifiers.generateConceptId();
		final Map<?, ?> requestBody = createRequestBody(conceptId, Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE, SnomedApiTestConstants.PREFERRED_ACCEPTABILITY_MAP, false);
		String createdId = assertComponentCanBeCreated("concepts", requestBody, "MAIN", branchName);
		assertEquals("Pre-generated and returned concept ID should match.", conceptId, createdId);
	}
	
	@Test
	public void createConceptOnDeletedBranch() {
		assertBranchCreated(branchPath);
		assertBranchDeleted(branchPath);
		final Map<?, ?> requestBody = createRequestBody(null, Concepts.ROOT_CONCEPT, Concepts.MODULE_SCT_CORE, SnomedApiTestConstants.PREFERRED_ACCEPTABILITY_MAP, false);		
		assertComponentCanNotBeCreated("concepts", requestBody, "MAIN", branchName);
	}
}
