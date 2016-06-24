/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

/**
 * @since 4.7
 */
public class SnomedVersioningApiAssert {
	
	/**
	 * The context-relative base URL for the administrative controller. 
	 */
	private static String ADMIN_API = "/admin";
	
	public static void assertVersionGetStatus(final String version, final int status) {
		assertVersionGetStatus(version, status, "SNOMEDCT");
	}
	
	public static void assertVersionGetStatus(final String version, final int status, final String shortName) {
		givenAuthenticatedRequest(ADMIN_API)
		.when().get("/codesystems/{shortNameOrOid}/versions/{id}", shortName, version)
		.then().assertThat().statusCode(status);
	}

	public static void assertVersionPostStatus(final String version, final String effectiveDate, final int status) {
		assertVersionPostStatus(version, effectiveDate, "SNOMEDCT", status);
	}
	
	public static void assertVersionPostStatus(final String version, final String effectiveDate, final String shortName, final int status) {
		whenCreatingVersion(version, effectiveDate, shortName)
		.then().assertThat().statusCode(status);
	}

	public static Response whenCreatingVersion(final String version, final String effectiveDate) {
		return whenCreatingVersion(version, effectiveDate, "SNOMEDCT");
	}
	
	public static Response whenCreatingVersion(final String version, final String effectiveDate, final String shortName) {
		final Map<?, ?> requestBody = ImmutableMap.builder()
				.put("version", version)
				.put("description", version)
				.put("effectiveDate", effectiveDate)
				.build();

		return givenAuthenticatedRequest(ADMIN_API)
				.and().contentType(ContentType.JSON)
				.and().body(requestBody)
				.when().post("/codesystems/{shortNameOrOid}/versions", shortName);
	}
	
	public static Collection<String> getEffectiveDates(final String uniqueId) {
		final Map<?, ?> response = givenAuthenticatedRequest(ADMIN_API)
			.and().contentType(ContentType.JSON)
			.when().get("/codesystems/{shortName}/versions", uniqueId)
			.then().extract().body().as(Map.class);
		
		if (!response.containsKey("items")) {
			return Collections.emptyList();
		} else {
			final List<String> effectiveDates = Lists.newArrayList();
			final List<Map<?, ?>> items = (List<Map<?, ?>>) response.get("items");
			for (final Map<?, ?> version : items) {
				final String effectiveDate = (String) version.get("effectiveDate");
				effectiveDates.add(effectiveDate);
			}
			
			return effectiveDates;
		}
	}
	
}
