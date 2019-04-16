/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.api.rest.auth;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenInvalidPasswordRequest;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenUnauthenticatedRequest;

import org.junit.Test;

import io.restassured.specification.RequestSpecification;

/**
 * @since 1.0
 */
public class BasicAuthenticationTest {

	private void assertResponseStatus(final RequestSpecification request, final int statusCode) {
		request.when().get("/repositories").then().assertThat().statusCode(statusCode);
	}

	@Test
	public void denyUnauthenticated() {
		assertResponseStatus(givenUnauthenticatedRequest("/admin"), 401);
	}

	@Test
	public void denyIncorrectCredentials() {
		assertResponseStatus(givenInvalidPasswordRequest("/admin"), 401);
	}

	@Test
	public void allowAuthenticated() {
		assertResponseStatus(givenAuthenticatedRequest("/admin"), 200);
	}
}
