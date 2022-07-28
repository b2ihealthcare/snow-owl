/*
 * Copyright 2021-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snomed.ecl.Ecl;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.core.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests;
import com.b2international.snowowl.snomed.core.rest.SnomedComponentType;
import com.b2international.snowowl.test.commons.snomed.RandomSnomedIdentiferGenerator;

import io.restassured.response.ValidatableResponse;

/**
 * @since 8.0
 */
public class SnomedSuggestApiTest extends AbstractSnomedApiTest {

	private static final String CODE_SYSTEM_PATH = "SNOMEDCT";
	
	private static final String BODY_STRUCTURE_ID = "123037004";
	private static final String CLINICAL_FINDING_ID = "404684003";
	private static final String SPECIAL_CONCEPT_ID = "370115009";
	private static final String ATTRIBUTE_ID = Concepts.ATTRIBUTE;
	private static final String CONCEPT_HISTORY_ATTRIBUTE_ID = "410663007";
	private static final String CONCEPT_MODEL_ATTRIBUTE_ID = "410662002";
	private static final String CONCEPT_ATTRIBUTE_ID = "734866006";
	
	@Test
	public void suggestTerm() {
		suggest(
			Json.object(
				"from", CODE_SYSTEM_PATH,
				"like", Json.array("body structure"),
				"suggester", Json.object(
					"type", "term"
				)
			)
		)
			.statusCode(200)
			.assertThat()
			//Use default limit
			.body("limit", equalTo(1))
			.body("total", greaterThanOrEqualTo(1))
			.body("items[0].id", equalTo(BODY_STRUCTURE_ID));
	}
	
	@Test
	public void suggestConceptWithoutAlternativeTerms() {
		String descriptionToDelete = RandomSnomedIdentiferGenerator.generateDescriptionId();
		String baseConceptId = createConcept(branchPath, Json.object(
			"active", true,
			"moduleId", Concepts.MODULE_SCT_CORE,
			"descriptions", Json.array(
				Json.object(
					"id", descriptionToDelete,
					"typeId", Concepts.FULLY_SPECIFIED_NAME,
					"term", "Suggest me, please (will be deleted)",
					"languageCode", "en",
					"acceptability", SnomedApiTestConstants.UK_PREFERRED_MAP
				),
				Json.object(
					"typeId", Concepts.SYNONYM,
					"term", "Suggest me, please",
					"languageCode", "en",
					"acceptability", SnomedApiTestConstants.UK_PREFERRED_MAP
				)
			),
			"relationships", Json.array(
				Json.object(
					"active", true,
					"typeId", Concepts.IS_A,
					"destinationId", Concepts.ROOT_CONCEPT
				)
			)
		));
		
		String expectedSuggestedConceptId = createConcept(branchPath, Json.object(
			"active", true,
			"moduleId", Concepts.MODULE_SCT_CORE,
			"descriptions", Json.array(
				Json.object(
					"typeId", Concepts.FULLY_SPECIFIED_NAME,
					"term", "Suggest me, please (hello)",
					"languageCode", "en",
					"acceptability", SnomedApiTestConstants.UK_PREFERRED_MAP
				),
				Json.object(
					"typeId", Concepts.SYNONYM,
					"term", "Suggest me, pretty please",
					"languageCode", "en",
					"acceptability", SnomedApiTestConstants.UK_PREFERRED_MAP
				)
			),
			"relationships", Json.array(
				Json.object(
					"active", true,
					"typeId", Concepts.IS_A,
					"destinationId", Concepts.ROOT_CONCEPT
				)
			)
		));
		
		// delete the synonym to leave only a single active description on the concept
		SnomedComponentRestRequests.deleteComponent(branchPath, SnomedComponentType.DESCRIPTION, descriptionToDelete, false).statusCode(204);
		
		// suggest should still work without any errors
		suggest(
			Json.object(
				"from", getDefaultSnomedResourceUri().withoutResourceType(),
				"like", Json.array(String.format("%s?ecl=%s", getDefaultSnomedResourceUri().withoutResourceType(), baseConceptId)),
				"suggester", Json.object(
					"type", "term"
				)
			)
		)
			.statusCode(200)
			.assertThat()
			// Use default limit
			.body("limit", equalTo(1))
			.body("total", equalTo(1))
			.body("items[0].id", equalTo(expectedSuggestedConceptId));
	}
	
	@Test
	public void suggestTerm_Post() {
		suggest(
			Json.object(
				"from", CODE_SYSTEM_PATH,
				"like", Json.array("finding clinical"),
				"suggester", Json.object(
					"type", "term"
				)
			)
		)
			.statusCode(200)
			.assertThat()
			//Use default limit
			.body("limit", equalTo(1))
			.body("total", greaterThanOrEqualTo(1))
			.body("items[0].id", equalTo(CLINICAL_FINDING_ID));
	}
	
	@Test
	public void suggestNoMatch() {
		suggest(
			Json.object(
				"from", CODE_SYSTEM_PATH,
				"like", Json.array("empty result term"),
				"suggester", Json.object(
					"type", "term"
				)
			)
		)
			.statusCode(200)
			.assertThat()
			.body("limit", equalTo(1))
			.body("total", equalTo(0))
			.body("items.size()", equalTo(0));
	}
	
	@Test
	public void suggestTermMinOccurrence() {
		suggest(
			Json.object(
				"from", CODE_SYSTEM_PATH,
				"like", Json.array("special concept"),
				"suggester", Json.object(
					"type", "term",
					"minOccurenceCount", 1
				),
				"limit", Integer.MAX_VALUE
			)
		)
		.statusCode(200)
		.assertThat()
		.body("total", greaterThanOrEqualTo(2))
		.body("items.id", allOf(
				hasItem(SPECIAL_CONCEPT_ID),
				hasItem(Concepts.ROOT_CONCEPT)));
	}
	
	@Test
	public void suggestQueryMinOccurrence() {
		suggest(
			Json.object(
				"from", CODE_SYSTEM_PATH,
				"like", Json.array(String.format("%s?ecl=%s", CODE_SYSTEM_PATH, Ecl.or(SPECIAL_CONCEPT_ID, ATTRIBUTE_ID))),
				"suggester", Json.object(
					"type", "term",
					"minOccurenceCount", 2
				),
				"limit", Integer.MAX_VALUE
			)
		)
		.statusCode(200)
		.assertThat()
		.body("items.id", hasItems(CONCEPT_HISTORY_ATTRIBUTE_ID, CONCEPT_MODEL_ATTRIBUTE_ID, CONCEPT_ATTRIBUTE_ID))
		.body("items.id", not(hasItems(SPECIAL_CONCEPT_ID, ATTRIBUTE_ID)));
	}
	
	@Test
	public void suggestMustNotQueryMinOccurrence() {
		suggest(
			Json.object(
				"from", CODE_SYSTEM_PATH,
				"like", Json.array(String.format("%s?ecl=%s", CODE_SYSTEM_PATH, Ecl.or(SPECIAL_CONCEPT_ID, ATTRIBUTE_ID))),
				"unlike", Json.array(String.format("%s?ecl=%s", CODE_SYSTEM_PATH, "<<" + Concepts.FOUNDATION_METADATA_CONCEPTS)),
				"suggester", Json.object(
					"type", "term",
					"minOccurenceCount", 2
				),
				"limit", Integer.MAX_VALUE
			)
		)
		.statusCode(200)
		.assertThat()
		.body("items.id", hasItems(CONCEPT_HISTORY_ATTRIBUTE_ID, CONCEPT_MODEL_ATTRIBUTE_ID))
		.body("items.id", not(hasItems(SPECIAL_CONCEPT_ID, ATTRIBUTE_ID, CONCEPT_ATTRIBUTE_ID)));
	}
	
	@Test
	public void suggestBulk() {
		final List<Json> body = List.of(
			Json.object(
				"from", CODE_SYSTEM_PATH,
				"like", Json.array("STRUCTURE BODY"),
				"suggester", Json.object(
					"type", "term"
				),
				"limit", 12,
				"preferredDisplay", "ID_ONLY"
			),
			Json.object(
				"from", CODE_SYSTEM_PATH,
				"like", Json.array("clinical finding"),
				"suggester", Json.object(
					"type", "term"
				),
				"limit", 2,
				"preferredDisplay", "FSN"
			)
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
	
	private ValidatableResponse suggest(Json body) {
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
	
}
