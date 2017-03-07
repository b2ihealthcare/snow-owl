/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;

import java.util.*;

import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.ValidatableResponse;

/**
 * @since 4.7
 */
public abstract class CodeSystemVersionRestRequests {

	public static ValidatableResponse getVersion(String shortName, String version) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.ADMIN_API)
				.get("/codesystems/{shortName}/versions/{id}", shortName, version)
				.then();
	}

	public static ValidatableResponse createVersion(String shortName, String version, String effectiveDate) {
		Map<?, ?> requestBody = ImmutableMap.builder()
				.put("version", version)
				.put("description", version)
				.put("effectiveDate", effectiveDate)
				.build();

		return givenAuthenticatedRequest(SnomedApiTestConstants.ADMIN_API)
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

		return givenAuthenticatedRequest(SnomedApiTestConstants.ADMIN_API)
				.contentType(ContentType.JSON)
				.body(requestBody)
				.post("/codesystems/{shortNameOrOid}/versions", shortName)
				.then();
	}

	public static ValidatableResponse getVersions(String shortName) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.ADMIN_API)
				.and().contentType(ContentType.JSON)
				.when().get("/codesystems/{shortName}/versions", shortName)
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

	public static Date getNextAvailableEffectiveDate(String shortName) {
		SortedSet<String> effectiveDates = getEffectiveDates(shortName);
		Calendar calendar = Calendar.getInstance();

		if (!effectiveDates.isEmpty()) {
			Date latestEffectiveDate = Dates.parse(effectiveDates.last(), DateFormats.SHORT);
			calendar.setTime(latestEffectiveDate);
		}

		calendar.add(Calendar.DATE, 1);

		calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		return calendar.getTime();
	}

	public static String getNextAvailableEffectiveDateAsString(String shortName) {
		return Dates.formatByGmt(getNextAvailableEffectiveDate(shortName), DateFormats.SHORT);
	}

	private CodeSystemVersionRestRequests() {
		throw new UnsupportedOperationException("This class is not supposed to be instantiated.");
	}
}
