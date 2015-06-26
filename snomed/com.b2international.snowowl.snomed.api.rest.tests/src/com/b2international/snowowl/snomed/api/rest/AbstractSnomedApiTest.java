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

import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.assertBranchCreated;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.joinPath;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.Map;
import java.util.UUID;

import org.junit.Before;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.response.ValidatableResponse;

/**
 * @since 2.0
 */
public abstract class AbstractSnomedApiTest {

	protected IBranchPath branchPath;
	protected String branchName; // TODO remove me
			
	@Before
	public void setup() {
		branchName = UUID.randomUUID().toString();
		branchPath = BranchPathUtils.createPath(BranchPathUtils.createMainPath(), branchName);
	}

	private void assertComponentStatus(String componentType, int statusCode, String componentId, String... segments) {
		String path = joinPath(segments);
		
		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
		.when()
			.get("/{path}/{componentType}/{id}", path, componentType, componentId)
		.then()
		.assertThat()
			.statusCode(statusCode);
	}
	
	private void assertComponentExists(String componentType, String symbolicName, String... segments) {
		assertComponentStatus(componentType, 200, symbolicName, segments);
	}

	protected void assertConceptExists(String componentId, String... segments) {
		assertComponentExists("concepts", componentId, segments);
	}
	
	protected void assertDescriptionExists(String componentId, String... segments) {
		assertComponentExists("descriptions", componentId, segments);
	}
	
	protected void assertRelationshipExists(String componentId, String... segments) {
		assertComponentExists("relationships", componentId, segments);
	}
	
	protected void assertComponentNotExists(String componentType, String componentId, String... segments) {
		assertComponentStatus(componentType, 404, componentId, segments);
	}
	
	protected void assertConceptNotExists(String componentId, String... segments) {
		assertComponentNotExists("concepts", componentId, segments);
	}
	
	protected void assertDescriptionNotExists(String componentId, String... segments) {
		assertComponentNotExists("descriptions", componentId, segments);
	}
	
	protected void assertRelationshipNotExists(String componentId, String... segments) {
		assertComponentNotExists("relationships", componentId, segments);
	}

	private Response whenCreatingComponent(String componentType, Map<?, ?> requestBody, String... segments) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
		.with()
			.contentType(ContentType.JSON)
		.and()
			.body(requestBody)
		.when()
			.post("/{path}/{componentType}", joinPath(segments), componentType);
	}

	protected ValidatableResponse assertComponentCreationStatus(String componentType, Map<?, ?> requestBody, int statusCode, String... segments) {
		return whenCreatingComponent(componentType, requestBody, segments)
		.then()
		.assertThat()
			.statusCode(statusCode);
	}

	protected ValidatableResponse assertComponentCanNotBeCreated(String componentType, Map<?, ?> requestBody, String... segments) {
		return assertComponentCreationStatus(componentType, requestBody, 400, joinPath(segments))
		.and()
			.body("status", equalTo(400));
	}
	
	protected String assertComponentCanBeCreated(String componentType, Map<?, ?> requestBody, String... segments) {
		String path = joinPath(segments);

		String location = assertComponentCreationStatus(componentType, requestBody, 201, path)
		.and()
			.header("Location", containsString(String.format("/%s/%s", path, componentType)))
		.and()
			.body(equalTo(""))
		.and()
			.extract().response().getHeader("Location");
		
		return lastPathSegment(location);
	}
	
	protected void assertPreferredTermEquals(final String conceptId, final String descriptionId, final String... segments) {
		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
		.with()
			.header("Accept-Language", "en-GB")
		.when()
			.get("/{path}/concepts/{conceptId}/pt", joinPath(segments), conceptId)
		.then()
		.assertThat()
			.statusCode(200)
		.and()
			.body("id", equalTo(descriptionId));
	}
	
	protected void assertComponentHasProperty(String componentType, String componentId, String propertyName, Object value, String... segments) {
		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
		.when()
			.get("/{path}/{type}/{id}", joinPath(segments), componentType, componentId)
		.then()
		.assertThat()
			.statusCode(200)
		.and()
			.body(propertyName, equalTo(value));
	}
	
	protected void assertComponentCanBeDeleted(String componentType, String componentId, String... segments) {
		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
		.when()
			.delete("/{path}/{type}/{id}", joinPath(segments), componentType, componentId)
		.then()
		.assertThat()
			.statusCode(204);
	}
	
	protected void assertComponentCanBeUpdated(String componentType, String componentId, Map<?, ?> updateRequestBody, String... segments) {
		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
		.with()
			.contentType(ContentType.JSON)
		.and()
			.body(updateRequestBody)
		.when()
			.post("/{path}/{type}/{id}/updates", joinPath(segments), componentType, componentId)
		.then()
		.assertThat()
			.statusCode(204);
	}
	
	protected void assertComponentActive(String componentType, String componentId, boolean active, String... segments) {
		assertComponentHasProperty(componentType, componentId, "active", active, segments);
	}
	
	protected void createNestedBranch(String... segments) {
		IBranchPath currentBranchPath = branchPath;
		assertBranchCreated(currentBranchPath);

		for (String segment : segments) {
			currentBranchPath = BranchPathUtils.createPath(currentBranchPath, segment);
			assertBranchCreated(currentBranchPath);
		}
	}
}
