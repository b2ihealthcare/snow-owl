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
package com.b2international.snowowl.snomed.core.rest;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import io.restassured.http.ContentType;

/**
 * @since 7.17.0
 */
public abstract class SnomedExpressionLabelRequests extends AbstractSnomedApiTest {
	
	public static List<String> getExpressionLabels(String branchPath, List<String> expressions) {
		Map<String, Object> requestBody = ImmutableMap.of(
				"expressions", expressions,
				"descriptionType", "fsn");

		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.contentType(ContentType.JSON)
				.body(requestBody)
				.get("/label-expressions", branchPath)
				.then()
				.extract()
				.jsonPath().getList("items");
	}
	
}
