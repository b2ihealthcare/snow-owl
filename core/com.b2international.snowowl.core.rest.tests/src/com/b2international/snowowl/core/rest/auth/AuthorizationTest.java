/*
 * Copyright 2021-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.rest.auth;

import static com.b2international.snowowl.test.commons.ApiTestConstants.CODESYSTEMS_API;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.Map;

import org.junit.Test;

import com.b2international.commons.json.Json;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.test.commons.ApiTestConstants;
import com.b2international.snowowl.test.commons.auth.BaseAuthorizationTest;
import com.b2international.snowowl.test.commons.rest.RestExtensions;

import io.restassured.http.ContentType;

/**
 * @since 8.0
 */
public class AuthorizationTest extends BaseAuthorizationTest {

	@Test
	public void emptyPermissionsNoAccess() throws Exception {
		String token = RestExtensions.generateToken();
		RestExtensions.givenRequestWithToken(ApiTestConstants.CODESYSTEMS_API, token)
			.get()
			.then()
			.assertThat().statusCode(200)
			.and().body("total", equalTo(0));
	}
	
	@Test
	public void permissionAllHasAccess() throws Exception {
		String token = RestExtensions.generateToken(Permission.ADMIN);
		RestExtensions.givenRequestWithToken(ApiTestConstants.CODESYSTEMS_API, token)
			.get()
			.then()
			.assertThat().statusCode(200)
			.and().body("total", equalTo(4));
	}
	
	@Test
	public void readOnlyAccessOnSingleResource() throws Exception {
		String token = RestExtensions.generateToken(Permission.requireAll(Permission.OPERATION_BROWSE, SNOMEDCT_ID));
		RestExtensions.givenRequestWithToken(ApiTestConstants.CODESYSTEMS_API, token)
			.get()
			.then()
			.assertThat().statusCode(200)
			.and().body("total", equalTo(1));
	}
	
	@Test
	public void noAccessToResourceDirectlyWithoutPermission() throws Exception {
		String token = RestExtensions.generateToken(Permission.requireAll(Permission.OPERATION_BROWSE, SNOMEDCT_ID));
		RestExtensions.givenRequestWithToken(ApiTestConstants.CODESYSTEMS_API, token)
			.get("/{id}", SNOMEDCT_UK_CL)
			.then()
			.assertThat().statusCode(404);
	}
	
	@Test
	public void shouldReturnResourceWithBundlePermission() throws Exception {
		String token = RestExtensions.generateToken(Permission.requireAll(Permission.OPERATION_BROWSE, UK_CLINICAL_BUNDLE_ID));
		RestExtensions.givenRequestWithToken(ApiTestConstants.CODESYSTEMS_API, token)
			.get("/{id}", SNOMEDCT_UK_CL)
			.then()
			.assertThat().statusCode(200);
	}
	
	@Test
	public void readOnlyAccessOnBundleGivesAccessToResourcesWithin() throws Exception {
		String token = RestExtensions.generateToken(Permission.requireAll(Permission.OPERATION_BROWSE, UK_CLINICAL_BUNDLE_ID));
		RestExtensions.givenRequestWithToken(ApiTestConstants.CODESYSTEMS_API, token)
			.get()
			.then()
			.assertThat().statusCode(200)
			.and().body("total", equalTo(2));
	}
	
	@Test
	public void readOnlyWildcardAccessOnBundleGivesAccessToResourcesWithin() throws Exception {
		String token = RestExtensions.generateToken(Permission.requireAll(Permission.OPERATION_BROWSE, UK_CLINICAL_BUNDLE_ID + "*"));
		RestExtensions.givenRequestWithToken(ApiTestConstants.CODESYSTEMS_API, token)
			.get()
			.then()
			.assertThat().statusCode(200)
			.and().body("total", equalTo(2));
	}
	
	@Test
	public void readOnlyAccessOnBundleGivesAccessToResourcesWithinTransitively() throws Exception {
		String token = RestExtensions.generateToken(Permission.requireAll(Permission.OPERATION_BROWSE, UK_ALL_BUNDLE_ID));
		RestExtensions.givenRequestWithToken(ApiTestConstants.CODESYSTEMS_API, token)
			.get()
			.then()
			.assertThat().statusCode(200)
			.and().body("total", equalTo(3));
	}
	
	@Test
	public void readOnlyWildcardAccessOnBundleGivesAccessToResourcesWithinTransitively() throws Exception {
		String token = RestExtensions.generateToken(Permission.requireAll(Permission.OPERATION_BROWSE, UK_ALL_BUNDLE_ID + "*"));
		RestExtensions.givenRequestWithToken(ApiTestConstants.CODESYSTEMS_API, token)
			.get()
			.then()
			.assertThat().statusCode(200)
			.and().body("total", equalTo(3));
	}
	
	@Test
	public void wildcardPermissionGivesAccessToAllMatchingResources() throws Exception {
		String token = RestExtensions.generateToken(Permission.requireAll(Permission.OPERATION_BROWSE, SNOMEDCT_UK_CL+"*"));
		RestExtensions.givenRequestWithToken(ApiTestConstants.CODESYSTEMS_API, token)
			.get()
			.then()
			.assertThat().statusCode(200)
			.and().body("total", equalTo(2));
	}
	
	@Test
	public void adminPermissionOnSingleResourceAllowsVersioning() throws Exception {
		String token = RestExtensions.generateToken(Permission.requireAll(Permission.ALL, SNOMEDCT_UK_CL));
		RestExtensions.givenRequestWithToken(ApiTestConstants.VERSIONS_API, token)
			.contentType(ContentType.JSON)
			.body(Json.object(
				"resource", CodeSystem.uri(SNOMEDCT_UK_CL).toString(),
				"version", "v1",
				"description", "v1",
				"effectiveTime", "2021-09-13",
				"force", false
			))
			.post()
			.then().assertThat()
			.statusCode(201);
	}
	
	@Test
	public void adminPermissionOnContainerBundleAllowsVersioning() throws Exception {
		String token = RestExtensions.generateToken(Permission.requireAll(Permission.ALL, UK_CLINICAL_BUNDLE_ID));
		RestExtensions.givenRequestWithToken(ApiTestConstants.VERSIONS_API, token)
			.contentType(ContentType.JSON)
			.body(Json.object(
				"resource", CodeSystem.uri(SNOMEDCT_UK_CL).toString(),
				"version", "v2",
				"description", "v2",
				"effectiveTime", "2021-09-14",
				"force", false
			))
			.post()
			.then().assertThat()
			.statusCode(201);
	}
	
	@Test
	public void adminPermissionOnContainerBundleAllowsEditingOfResourceMetadata() throws Exception {
		String token = RestExtensions.generateToken(Permission.requireAll(Permission.ALL, UK_CLINICAL_BUNDLE_ID));
		RestExtensions.givenRequestWithToken(CODESYSTEMS_API, token)
			.with().contentType(ContentType.JSON)
			.and().body(Map.of(ResourceDocument.Fields.CONTACT, "contactme"))
			.when().put("/{id}", SNOMEDCT_UK_CL)
			.then().assertThat()
			.statusCode(204);
	}
	
}
