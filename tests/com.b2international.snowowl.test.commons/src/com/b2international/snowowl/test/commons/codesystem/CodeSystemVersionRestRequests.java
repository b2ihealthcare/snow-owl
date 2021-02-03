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

import static com.b2international.snowowl.test.commons.codesystem.CodeSystemRestRequests.createCodeSystem;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.codesystem.CodeSystemVersion;
import com.b2international.snowowl.core.codesystem.CodeSystemVersions;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.test.commons.ApiTestConstants;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

/**
 * @since 4.7
 */
public abstract class CodeSystemVersionRestRequests {

	public static String getLatestVersion(String codeSystemId) {
		// TODO add proper version API to control version searches
		return Iterables.getLast(getVersions(codeSystemId)).getVersion();
	}
	
	public static ValidatableResponse getVersion(String codeSystemId, String version) {
		return givenAuthenticatedRequest(ApiTestConstants.ADMIN_API)
				.get("/codesystems/{codeSystemId}/versions/{id}", codeSystemId, version)
				.then();
	}

	public static ValidatableResponse createVersion(String codeSystemId, String version, String effectiveDate) {
		Map<?, ?> requestBody = ImmutableMap.builder()
				.put("version", version)
				.put("description", version)
				.put("effectiveDate", effectiveDate)
				.build();

		return givenAuthenticatedRequest(ApiTestConstants.ADMIN_API)
				.contentType(ContentType.JSON)
				.body(requestBody)
				.post("/codesystems/{codeSystemId}/versions", codeSystemId)
				.then();
	}

	public static ValidatableResponse createVersion(String codeSystemId, String version, String description, String effectiveDate) {
		Map<?, ?> requestBody = ImmutableMap.builder()
				.put("version", version)
				.put("description", description)
				.put("effectiveDate", effectiveDate)
				.build();

		return givenAuthenticatedRequest(ApiTestConstants.ADMIN_API)
				.contentType(ContentType.JSON)
				.body(requestBody)
				.post("/codesystems/{codeSystemId}/versions", codeSystemId)
				.then();
	}

	public static CodeSystemVersions getVersions(String codeSystemId) {
		return givenAuthenticatedRequest(ApiTestConstants.ADMIN_API)
				.and().contentType(ContentType.JSON)
				.when()
				.get("/codesystems/{codeSystemId}/versions", codeSystemId)
				.then()
				.extract()
				.as(CodeSystemVersions.class);
	}

	public static LocalDate getNextAvailableEffectiveDate(String codeSystemId) {
		// XXX make sure we always use today or later dates for versions
		// This ensures that all versions created by tests will be in chronological order, even if some of the pre-imported content we are relying on is in the past
		// and adding one day to that historical version would mean a historical effective time version, which is unfortunate and can lead to inconsistencies in tests
		LocalDate today = LocalDate.now();
		return Optional.ofNullable(Iterables.getLast(getVersions(codeSystemId), null))
				.map(CodeSystemVersion::getEffectiveTime)
				.map(latestVersion -> latestVersion.isBefore(today) ? today : latestVersion.plus(1, ChronoUnit.DAYS))
				.orElse(today);
	}

	public static String getNextAvailableEffectiveDateAsString(String codeSystemId) {
		return EffectiveTimes.format(getNextAvailableEffectiveDate(codeSystemId), DateFormats.SHORT);
	}
	
	public static void createCodeSystemAndVersion(final IBranchPath branchPath, String codeSystemId, String versionId, String effectiveTime) {
		createCodeSystem(branchPath, codeSystemId).statusCode(201);
		createVersion(codeSystemId, versionId, effectiveTime).statusCode(201);
	}

	private CodeSystemVersionRestRequests() {
		throw new UnsupportedOperationException("This class is not supposed to be instantiated.");
	}
}
