/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.snomed.core.rest.SnomedApiTestConstants.UK_PREFERRED_MAP;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.JSON_UTF8;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.Test;

import com.b2international.commons.json.Json;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures;

/**
 * @since 8.0.0
 */
public class SnomedConceptSearchApiTest extends AbstractSnomedApiTest {
	
	@Test
	public void searchBySemanticTag() throws Exception {
		String conceptId = createConcept(branchPath, SnomedRestFixtures.createConceptRequestBody(Concepts.ROOT_CONCEPT));
		SnomedRestFixtures.createNewDescription(branchPath, Json.object(
			"conceptId", conceptId,
			"moduleId", Concepts.MODULE_SCT_CORE,
			"typeId", Concepts.FULLY_SPECIFIED_NAME,
			"term", "My awesome fsn (tag)",
			"languageCode", "en",
			"acceptability", UK_PREFERRED_MAP,
			"caseSignificanceId", Concepts.ENTIRE_TERM_CASE_INSENSITIVE,
			"commitComment", "New FSN"
		));
		
		SnomedConcepts hits = givenAuthenticatedRequest(getApiBaseUrl())
			.accept(JSON_UTF8)
			.queryParams(Map.of("semanticTag", "tag"))
			.get("/{path}/concepts/", branchPath.getPath())
			.then().assertThat()
			.statusCode(200)
			.extract().as(SnomedConcepts.class);
		assertThat(hits.getTotal()).isEqualTo(1);
	}
	
	/**
	 * Related to https://github.com/b2ihealthcare/snow-owl/issues/738
	 */
	@Test
	public void searchByBothSemanticTagAndTerm() throws Exception {
		String conceptId = createConcept(branchPath, SnomedRestFixtures.createConceptRequestBody(Concepts.ROOT_CONCEPT));
		SnomedRestFixtures.createNewDescription(branchPath, Json.object(
			"conceptId", conceptId,
			"moduleId", Concepts.MODULE_SCT_CORE,
			"typeId", Concepts.FULLY_SPECIFIED_NAME,
			"term", "My awesome fsn (tag)",
			"languageCode", "en",
			"acceptability", UK_PREFERRED_MAP,
			"caseSignificanceId", Concepts.ENTIRE_TERM_CASE_INSENSITIVE,
			"commitComment", "New FSN"
		));
		SnomedRestFixtures.createNewDescription(branchPath, Json.object(
			"conceptId", conceptId,
			"moduleId", Concepts.MODULE_SCT_CORE,
			"typeId", Concepts.FULLY_SPECIFIED_NAME,
			"term", "My another fsn (other)",
			"languageCode", "en",
			"acceptability", UK_PREFERRED_MAP,
			"caseSignificanceId", Concepts.ENTIRE_TERM_CASE_INSENSITIVE,
			"commitComment", "New FSN"
		));
		
		SnomedConcepts hits = givenAuthenticatedRequest(getApiBaseUrl())
			.accept(JSON_UTF8)
			.queryParams(Map.of(
				"term", "another",
				"semanticTag", "tag"
			))
			.get("/{path}/concepts/", branchPath.getPath())
			.then().assertThat()
			.statusCode(200)
			.extract().as(SnomedConcepts.class);
		assertThat(hits.getTotal()).isEqualTo(1);
	}
	
}
