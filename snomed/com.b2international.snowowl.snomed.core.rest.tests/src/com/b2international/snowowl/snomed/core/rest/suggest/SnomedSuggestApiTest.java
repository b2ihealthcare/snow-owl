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
package com.b2international.snowowl.snomed.core.rest.suggest;

import static com.b2international.snowowl.test.commons.ApiTestConstants.SUGGEST_API;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.JSON_UTF8;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;

import java.util.List;

import org.junit.Test;

import com.b2international.commons.json.Json;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;

import io.restassured.response.ValidatableResponse;

/**
 * @since 8.0
 */
public class SnomedSuggestApiTest {

	private static final String CODE_SYSTEM_PATH = "SNOMEDCT";
	
	private static final String BODY_STRUCTURE_ID = "123037004";
	private static final String CLINICAL_FINDING_ID = "404684003";
	private static final String SPECIAL_CONCEPT_ID = "370115009";
	private static final String ATTRIBUTE_ID = Concepts.ATTRIBUTE;
	private static final String CONCEPT_HISTORY_ATTRIBUTE_ID = "410663007";
	private static final String CONCEPT_MODEL_ATTRIBUTE_ID = "410662002";
	private static final String CONCEPT_ATTRIBUTE_ID = "734866006";
	
	@Test
	public void getSuggest() {
		getSuggest(prepareJson().with("term", "body structure"))
			.statusCode(200)
			.assertThat()
			//Use default limit
			.body("limit", equalTo(1))
			.body("total", greaterThanOrEqualTo(1))
			.body("items[0].id", equalTo(BODY_STRUCTURE_ID));
	}
	
	@Test
	public void postSuggest() {
		postSuggest(prepareJson().with("term", "finding clinical"))
			.statusCode(200)
			.assertThat()
			//Use default limit
			.body("limit", equalTo(1))
			.body("total", greaterThanOrEqualTo(1))
			.body("items[0].id", equalTo(CLINICAL_FINDING_ID));
	}
	
	@Test
	public void emptyResultSuggest() {
		getSuggest(prepareJson().with("term", "empty result term"))
			.statusCode(200)
			.assertThat()
			.body("limit", equalTo(1))
			.body("total", equalTo(0))
			.body("items.size()", equalTo(0));
	}
	
	@Test
	public void filterByTermMinOccurrence() {
		getSuggest(prepareJson()
				.with("term", "special concept")
				.with("minOccurrenceCount", 1)
				.with("limit", Integer.MAX_VALUE))
		.statusCode(200)
		.assertThat()
		.body("total", greaterThanOrEqualTo(2))
		.body("items.id", allOf(
				hasItem(SPECIAL_CONCEPT_ID),
				hasItem(Concepts.ROOT_CONCEPT)));
	}
	
	@Test
	public void filterByQueryWithMinOccurrence() {
		getSuggest(prepareJson()
				.with("query", String.join(" OR ", SPECIAL_CONCEPT_ID, ATTRIBUTE_ID))
				.with("minOccurrenceCount", 2)
				.with("limit", Integer.MAX_VALUE))
		.statusCode(200)
		.assertThat()
		.body("items.id", hasItems(CONCEPT_HISTORY_ATTRIBUTE_ID, CONCEPT_MODEL_ATTRIBUTE_ID, CONCEPT_ATTRIBUTE_ID))
		.body("items.id", not(hasItems(SPECIAL_CONCEPT_ID, ATTRIBUTE_ID)));
	}
	
	@Test
	public void filterByMustNotQueryWithMinOccurrence() {
		getSuggest(prepareJson()
				.with("query", String.join(" OR ", SPECIAL_CONCEPT_ID, ATTRIBUTE_ID))
				.with("mustNotQuery", String.join("<<", Concepts.FOUNDATION_METADATA_CONCEPTS))
				.with("minOccurrenceCount", 2)
				.with("limit", Integer.MAX_VALUE))
		.statusCode(200)
		.assertThat()
		.body("items.id", hasItems(CONCEPT_HISTORY_ATTRIBUTE_ID, CONCEPT_MODEL_ATTRIBUTE_ID))
		.body("items.id", not(hasItems(SPECIAL_CONCEPT_ID, ATTRIBUTE_ID, CONCEPT_ATTRIBUTE_ID)));
	}
	
	@Test
	public void bulkSuggest() {
		
		final List<Json> body = List.of(
			prepareJson()
				.with("term", "STRUCTURE BODY")
				.with("limit", 12)
				.with("preferredDisplay", "ID_ONLY"),
			prepareJson()
				.with("term", "clinical finding")
				.with("limit", 2)
				.with("preferredDisplay", "FSN")
		);
		
		postBulkSuggest(body)
			.statusCode(200)
			.body("size()", equalTo(2))
			
			//Assert response of STRUCTURE BODY
			.assertThat()
			.body("[0].limit", equalTo(12))
			.body("[0].total", greaterThanOrEqualTo(1))
			.body("[0].items[0].term", equalTo(BODY_STRUCTURE_ID))
			
			//Assert response of clinical finding
			.assertThat()
			.body("[1].limit", equalTo(2))
			.body("[1].total", greaterThanOrEqualTo(1))
			.body("[1].items[0].term", equalTo("Clinical finding (finding)"));
	}
	
	private ValidatableResponse getSuggest(Json params) {
		return givenAuthenticatedRequest(SUGGEST_API)
				.contentType(JSON_UTF8)
				.queryParams(params)
				.get()
				.then();
	}
	
	private ValidatableResponse postSuggest(Json body) {
		return givenAuthenticatedRequest(SUGGEST_API)
				.contentType(JSON_UTF8)
				.body(body)
				.post()
				.then();
	}
	
	private ValidatableResponse postBulkSuggest(List<Json> body) {
		return givenAuthenticatedRequest(SUGGEST_API)
				.contentType(JSON_UTF8)
				.body(body)
				.post("/bulk")
				.then();
	}
	
	private Json prepareJson() {
		return Json.object("codeSystemPath", CODE_SYSTEM_PATH);
	}
}
