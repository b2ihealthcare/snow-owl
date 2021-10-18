/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.test.commons.codesystem;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.b2international.commons.json.Json;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.codesystem.CodeSystems;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.test.commons.ApiTestConstants;
import com.b2international.snowowl.test.commons.Services;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

/**
 * Common requests for working with Code Systems (only SNOMED CT is supported).
 * 
 * @since 4.7
 */
public abstract class CodeSystemRestRequests {
	
	public static ValidatableResponse createCodeSystem(String codeSystemId) {
		String branchPath = RepositoryRequests.branching().prepareCreate()
			.setParent(Branch.MAIN_PATH)
			.setName(codeSystemId)
			.build(SnomedTerminologyComponentConstants.TOOLING_ID)
			.execute(Services.bus())
			.getSync(1, TimeUnit.MINUTES);
		return createCodeSystem(null, branchPath, codeSystemId);
	}
	
	public static ValidatableResponse createCodeSystem(IBranchPath branchPath, String codeSystemId) {
		return createCodeSystem(branchPath.getPath(), codeSystemId);
	}
	
	public static ValidatableResponse createCodeSystem(String branchPath, String codeSystemId) {
		return createCodeSystem(null, branchPath, codeSystemId);
	}

	public static ValidatableResponse createCodeSystem(ResourceURI extensionOf, String codeSystemId) {
		return createCodeSystem(extensionOf, null, codeSystemId);
	}
	
	public static ValidatableResponse createCodeSystem(ResourceURI extensionOf, String branchPath, String codeSystemId) {
		return createCodeSystem(extensionOf, branchPath, codeSystemId, Map.of());
	}
	
	public static ValidatableResponse createCodeSystem(ResourceURI extensionOf, String codeSystemId,  Map<String, Object> settings) {
		return createCodeSystem(extensionOf, null, codeSystemId, settings);
	}
	
	public static ValidatableResponse createCodeSystem(ResourceURI extensionOf, String branchPath, String codeSystemId,  Map<String, Object> settings) {
		Json requestBody = Json.object(
			"id", codeSystemId,
			"title", codeSystemId,
			"url", getCodeSystemUrl(codeSystemId),
			"description", "<div>Markdown supported</div>",
			"toolingId", SnomedTerminologyComponentConstants.TOOLING_ID,
			"oid", "oid_" + codeSystemId,
			"language", "ENG",
			"extensionOf", extensionOf,
			"branchPath", branchPath,
			"owner", "https://b2i.sg",
			"settings", configureLanguageConfig(settings)
		);
				
		return givenAuthenticatedRequest(ApiTestConstants.CODESYSTEMS_API)
				.contentType(ContentType.JSON)
				.body(requestBody)
				.post()
				.then();
	}

	private static Map<String, Object> configureLanguageConfig(Map<String, Object> settings) {
		settings = settings == null ? Maps.newHashMap() : Maps.newHashMap(settings);
		settings.putIfAbsent(SnomedTerminologyComponentConstants.CODESYSTEM_LANGUAGE_CONFIG_KEY, List.of(
			Map.of(
				"languageTag", "en",
				"languageRefSetIds", Lists.newArrayList(Concepts.REFSET_LANGUAGE_TYPE_UK, Concepts.REFSET_LANGUAGE_TYPE_US)
			),
			Map.of(
				"languageTag", "en-us",
				"languageRefSetIds", Lists.newArrayList(Concepts.REFSET_LANGUAGE_TYPE_US)
			),
			Map.of(
				"languageTag", "en-gb",
				"languageRefSetIds", Lists.newArrayList(Concepts.REFSET_LANGUAGE_TYPE_UK)
			)
		));
		return settings;
	}

	public static String getCodeSystemUrl(String codeSystemId) {
		return SnomedTerminologyComponentConstants.SNOMED_URI_SCT + "/900000000000207008/" + codeSystemId;
	}

	public static ValidatableResponse assertGetCodeSystem(String codeSystemId) {
		return givenAuthenticatedRequest(ApiTestConstants.CODESYSTEMS_API)
				.get("/{id}", codeSystemId)
				.then().assertThat();
	}
	
	public static CodeSystem getCodeSystem(String codeSystemId) {
		return assertGetCodeSystem(codeSystemId).statusCode(200).extract().as(CodeSystem.class);
	}

	public static ValidatableResponse updateCodeSystem(String id, Map<?, ?> requestBody) {
		return givenAuthenticatedRequest(ApiTestConstants.CODESYSTEMS_API)
				.contentType(ContentType.JSON)
				.body(requestBody)
				.put("/{id}", id)
				.then().assertThat().statusCode(204);
	}
	
	public static ValidatableResponse deleteCodeSystem(String codeSystemId) {
		return givenAuthenticatedRequest(ApiTestConstants.CODESYSTEMS_API)
				.delete("/{id}", codeSystemId)
				.then().assertThat().statusCode(204);

	}
	
	public static ValidatableResponse assertCodeSystemUpgrade(ResourceURI upgradeOf, ResourceURI extensionOf) {
		return givenAuthenticatedRequest("/upgrade")
				.contentType(ContentType.JSON)
				.body(Map.of(
					"extensionOf", extensionOf.toString(),
					"upgradeOf", upgradeOf.toString()
				))
				.post()
				.then().assertThat();
	}
	
	public static CodeSystems search(String id, String...expand) {
		return givenAuthenticatedRequest(ApiTestConstants.CODESYSTEMS_API)
				.contentType(ContentType.JSON)
				.body(Map.of(
					"id", Set.of(id),
					"expand", List.of(expand)
				))
				.post("/search")
				.then().assertThat().statusCode(200)
				.extract().as(CodeSystems.class);
	}

	private CodeSystemRestRequests() {
		throw new UnsupportedOperationException("This class is not supposed to be instantiated.");
	}

}
