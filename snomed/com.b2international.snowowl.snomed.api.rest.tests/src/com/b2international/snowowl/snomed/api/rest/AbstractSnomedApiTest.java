/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static org.hamcrest.CoreMatchers.endsWith;

import java.util.Map;
import java.util.UUID;

import org.junit.Before;

import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

/**
 * @since 2.0
 */
public abstract class AbstractSnomedApiTest {

	protected static String API = "/snomed-ct/v2";
	
	protected String branchName;
			
	@Before
	public void setup() {
		branchName = UUID.randomUUID().toString();
	}

	protected Response whenCreatingBranch(final RequestSpecification givenRequest, final String parent, final String name) {
		return whenCreatingBranch(givenRequest, parent, name, ImmutableMap.of());
	}

	protected Response whenCreatingBranch(final RequestSpecification givenRequest, final String parent, final String name, final Map<?, ?> metadata) {
		final Map<?, ?> requestBody = ImmutableMap.of(
			"parent", parent,
			"name", name,
			"metadata", metadata		
		);
		
		final Response response = givenRequest
		.and()
			.contentType(ContentType.JSON)
		.and()
			.body(requestBody)
		.when()
			.post("/branches");
		
		return response;
	}

	protected void assertBranchCanBeCreated(final String parent, final String name) {
		assertBranchCanBeCreated(parent, name, ImmutableMap.of());
	}

	protected void assertBranchCanBeCreated(final String parent, final String name, final Map<?, ?> metadata) {
		whenCreatingBranch(givenAuthenticatedRequest(API), parent, name, metadata)
		.then()
		.assertThat()
			.statusCode(201)
		.and()
			.header("Location", endsWith(String.format("/branches/%s/%s", parent, name)));
	}
}
