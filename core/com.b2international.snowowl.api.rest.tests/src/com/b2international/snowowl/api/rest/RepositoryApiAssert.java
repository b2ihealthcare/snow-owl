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
package com.b2international.snowowl.api.rest;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.junit.Assert.assertNotNull;

import org.hamcrest.CoreMatchers;

import com.jayway.restassured.response.ValidatableResponse;

/**
 * @since 5.8
 */
public abstract class RepositoryApiAssert {

	private static final String ADMIN = "/admin";
	private static final String REPOSITORIES = "/repositories";

	public static ValidatableResponse assertRepositoryInfoForExistingRepository(final String repositoryId, final String status) {
		return assertRepositoryInfoReadWithStatus(repositoryId, status, 200);
	}

	public static ValidatableResponse assertRepositoryInfoForInvalidRepository(final String repositoryId) {
		return assertRepositoryInfoReadWithStatus(repositoryId, null, 404);
	}

	
	public static ValidatableResponse assertRepositoryInfoReadWithStatus(final String repositoryId, final String health, final int statusCode) {
		assertNotNull(repositoryId);
		return givenAuthenticatedRequest(ADMIN)
				.when().get(REPOSITORIES + "/{repositoryId}", repositoryId)
				.then().assertThat()
					.statusCode(statusCode)
				.and()
					.body("health", CoreMatchers.equalTo(health));
	}

	public static ValidatableResponse assertAllRepositoryInfo() {
		return givenAuthenticatedRequest(ADMIN)
				.when().get(REPOSITORIES)
				.then().assertThat().statusCode(200);
	}
	
}
