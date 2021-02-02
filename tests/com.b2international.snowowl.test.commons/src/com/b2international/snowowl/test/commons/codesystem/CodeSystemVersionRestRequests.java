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
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.test.commons.ApiTestConstants;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

/**
 * @since 4.7
 */
public abstract class CodeSystemVersionRestRequests {

	public static String getLatestVersion(String codeSystemId) {
		// TODO add proper version API to control version searches
		Map<?, ?> versions = getVersions(codeSystemId).extract().as(Map.class);
		List<Map<?, ?>> items = (List<Map<?, ?>>) versions.get("items");
		return (String) Iterables.getLast(items).get("version");
	}
	
	public static ValidatableResponse getVersion(String shortName, String version) {
		return givenAuthenticatedRequest(ApiTestConstants.ADMIN_API)
				.get("/codesystems/{shortName}/versions/{id}", shortName, version)
				.then();
	}

	public static ValidatableResponse createVersion(String shortName, String version, String effectiveDate) {
		Map<?, ?> requestBody = ImmutableMap.builder()
				.put("version", version)
				.put("description", version)
				.put("effectiveDate", effectiveDate)
				.build();

		return givenAuthenticatedRequest(ApiTestConstants.ADMIN_API)
				.contentType(ContentType.JSON)
				.body(requestBody)
				.post("/codesystems/{shortNameOrOid}/versions", shortName)
				.then();
	}

	public static ValidatableResponse createVersion(String shortName, String version, String description, String effectiveDate) {
		Map<?, ?> requestBody = ImmutableMap.builder()
				.put("version", version)
				.put("description", description)
				.put("effectiveDate", effectiveDate)
				.build();

		return givenAuthenticatedRequest(ApiTestConstants.ADMIN_API)
				.contentType(ContentType.JSON)
				.body(requestBody)
				.post("/codesystems/{shortNameOrOid}/versions", shortName)
				.then();
	}

	public static ValidatableResponse getVersions(String shortName) {
		return givenAuthenticatedRequest(ApiTestConstants.ADMIN_API)
				.and().contentType(ContentType.JSON)
				.when()
				.get("/codesystems/{shortName}/versions", shortName)
				.then();
	}

	public static SortedSet<String> getEffectiveDates(String shortName) {
		Map<?, ?> response = getVersions(shortName).extract().as(Map.class);

		if (!response.containsKey("items")) {
			return ImmutableSortedSet.of();
		} else {
			ImmutableSortedSet.Builder<String> effectiveDatesBuilder = ImmutableSortedSet.naturalOrder();
			@SuppressWarnings("unchecked")
			List<Map<?, ?>> items = (List<Map<?, ?>>) response.get("items");
			for (Map<?, ?> version : items) {
				String effectiveDate = (String) version.get("effectiveDate");
				effectiveDatesBuilder.add(effectiveDate);
			}

			return effectiveDatesBuilder.build();
		}
	}

	public static LocalDate getNextAvailableEffectiveDate(String shortName) {
		LocalDate effectiveDate = LocalDate.now();

		SortedSet<String> effectiveDates = getEffectiveDates(shortName);
		if (!effectiveDates.isEmpty()) {
			LocalDate latestEffectiveDate = EffectiveTimes.parse(effectiveDates.last(), DateFormats.SHORT);
			// XXX make sure we always use today or later dates, so all versions created in chronological order, even if some of the pre-imported content we are relying on are still in the past
			// and adding one day to that historical version would mean a historical effective time version, which is unfortunate and can lead to inconsistencies in tests
			if (latestEffectiveDate.isAfter(effectiveDate)) {
				effectiveDate = latestEffectiveDate;
			}
		}

		return effectiveDate.plus(1, ChronoUnit.DAYS);
	}

	public static String getNextAvailableEffectiveDateAsString(String shortName) {
		return EffectiveTimes.format(getNextAvailableEffectiveDate(shortName), DateFormats.SHORT);
	}
	
	public static void createCodeSystemAndVersion(final IBranchPath branchPath, String codeSystemShortName, String versionId, String effectiveTime) {
		createCodeSystem(branchPath, codeSystemShortName).statusCode(201);
		createVersion(codeSystemShortName, versionId, effectiveTime).statusCode(201);
	}

	private CodeSystemVersionRestRequests() {
		throw new UnsupportedOperationException("This class is not supposed to be instantiated.");
	}
}
