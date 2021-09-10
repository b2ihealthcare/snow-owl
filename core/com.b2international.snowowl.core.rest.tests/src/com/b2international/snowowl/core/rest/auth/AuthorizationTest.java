/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.core.rest.BundleApiAssert.createBundle;
import static com.b2international.snowowl.core.rest.BundleApiAssert.prepareBundleCreateRequestBody;
import static com.b2international.snowowl.core.rest.CodeSystemApiAssert.assertCodeSystemCreate;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b2international.commons.json.Json;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.identity.JWTGenerator;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.identity.Role;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.test.commons.ApiTestConstants;
import com.b2international.snowowl.test.commons.Services;
import com.b2international.snowowl.test.commons.rest.RestExtensions;

/**
 * @since 8.0
 */
public class AuthorizationTest {

	private static final String SNOMEDCT_ID = "SNOMEDCT";
	private static final String SNOMEDCT_UK_CL = "SNOMEDCT-UK-CL";
	private static final String SNOMEDCT_UK_CL_MICRO = "SNOMEDCT-UK-CL-MICRO";
	private static final String SNOMEDCT_UK_DR = "SNOMEDCT-UK-DR";
	private static final String UK_ALL_BUNDLE_ID = "uk-all";
	private static final String UK_CLINICAL_BUNDLE_ID = "uk-clinical";
	
	private static final Json SNOMED = Json.object(
		ResourceDocument.Fields.ID, SNOMEDCT_ID,
		ResourceDocument.Fields.TITLE, "SNOMED CT",
		ResourceDocument.Fields.URL, SnomedTerminologyComponentConstants.SNOMED_URI_SCT,
		ResourceDocument.Fields.TOOLING_ID, SnomedTerminologyComponentConstants.TOOLING_ID,
		ResourceDocument.Fields.BUNDLE_ID, IComponent.ROOT_ID
	);
	
	@BeforeClass
	public static void setup() {
		// create test Bundles
		createBundle(prepareBundleCreateRequestBody(UK_ALL_BUNDLE_ID));
		createBundle(prepareBundleCreateRequestBody(UK_CLINICAL_BUNDLE_ID, UK_ALL_BUNDLE_ID));
		
		// create test CodeSystems, one in the root, one inside the bundle
		assertCodeSystemCreate(SNOMED)
			.statusCode(201)
			.header("Location", containsString("/codesystems/SNOMEDCT"));
		
		assertCodeSystemCreate(
			Json.object(
				ResourceDocument.Fields.ID, SNOMEDCT_UK_CL,
				ResourceDocument.Fields.TITLE, "SNOMED CT UK Clinical",
				ResourceDocument.Fields.URL, SnomedTerminologyComponentConstants.SNOMED_URI_SCT + "/" + Concepts.UK_CLINICAL_EXTENSION_MODULE,
				ResourceDocument.Fields.TOOLING_ID, SnomedTerminologyComponentConstants.TOOLING_ID,
				ResourceDocument.Fields.BUNDLE_ID, UK_CLINICAL_BUNDLE_ID
			))
			.statusCode(201)
			.header("Location", containsString("/codesystems/" + SNOMEDCT_UK_CL));
		
		assertCodeSystemCreate(
			Json.object(
				ResourceDocument.Fields.ID, SNOMEDCT_UK_CL_MICRO,
				ResourceDocument.Fields.TITLE, "SNOMED CT UK Clinical (Micro Releases)",
				ResourceDocument.Fields.URL, SnomedTerminologyComponentConstants.SNOMED_URI_DEV + "/" + Concepts.UK_CLINICAL_EXTENSION_MODULE,
				ResourceDocument.Fields.TOOLING_ID, SnomedTerminologyComponentConstants.TOOLING_ID,
				ResourceDocument.Fields.BUNDLE_ID, UK_CLINICAL_BUNDLE_ID
			))
			.statusCode(201)
			.header("Location", containsString("/codesystems/" + SNOMEDCT_UK_CL));
		
		assertCodeSystemCreate(
			Json.object(
				ResourceDocument.Fields.ID, SNOMEDCT_UK_DR,
				ResourceDocument.Fields.TITLE, "SNOMED CT UK Drug",
				ResourceDocument.Fields.URL, SnomedTerminologyComponentConstants.SNOMED_URI_SCT + "/" + Concepts.UK_DRUG_EXTENSION_MODULE,
				ResourceDocument.Fields.TOOLING_ID, SnomedTerminologyComponentConstants.TOOLING_ID,
				ResourceDocument.Fields.BUNDLE_ID, UK_ALL_BUNDLE_ID
			))
			.statusCode(201)
			.header("Location", containsString("/codesystems/" + SNOMEDCT_UK_DR));
	}
	
	@AfterClass
	public static void after() {
		ResourceRequests
			.prepareSearch()
			.all()
			.buildAsync()
			.execute(Services.bus())
			.getSync(1, TimeUnit.MINUTES)
			.forEach(resource -> {
				ResourceRequests
				.prepareDelete(resource.getId())
				.build(RestExtensions.USER, "Delete " + resource.getId())
				.execute(Services.bus())
				.getSync(1, TimeUnit.MINUTES); 
			});
	}
	
	@Test
	public void permissionAllHasAccess() throws Exception {
		String token = generateToken(Permission.ADMIN);
		RestExtensions.givenRequestWithToken(ApiTestConstants.CODESYSTEMS_API, token)
			.get()
			.then()
			.assertThat().statusCode(200)
			.and().body("total", equalTo(4));
	}
	
	@Test
	public void readOnlyAccessOnSingleResource() throws Exception {
		String token = generateToken(Permission.requireAll(Permission.OPERATION_BROWSE, SNOMEDCT_ID));
		RestExtensions.givenRequestWithToken(ApiTestConstants.CODESYSTEMS_API, token)
			.get()
			.then()
			.assertThat().statusCode(200)
			.and().body("total", equalTo(1));
	}
	
	@Test
	public void noAccessToResourceDirectlyWithoutPermission() throws Exception {
		String token = generateToken(Permission.requireAll(Permission.OPERATION_BROWSE, SNOMEDCT_ID));
		RestExtensions.givenRequestWithToken(ApiTestConstants.CODESYSTEMS_API, token)
			.get("/{id}", SNOMEDCT_UK_CL)
			.then()
			.assertThat().statusCode(404);
	}
	
	@Test
	public void shouldReturnResourceWithBundlePermission() throws Exception {
		String token = generateToken(Permission.requireAll(Permission.OPERATION_BROWSE, UK_CLINICAL_BUNDLE_ID));
		RestExtensions.givenRequestWithToken(ApiTestConstants.CODESYSTEMS_API, token)
			.get("/{id}", SNOMEDCT_UK_CL)
			.then()
			.assertThat().statusCode(200);
	}
	
	@Test
	public void readOnlyAccessOnBundleGivesAccessToResourcesWithin() throws Exception {
		String token = generateToken(Permission.requireAll(Permission.OPERATION_BROWSE, UK_CLINICAL_BUNDLE_ID));
		RestExtensions.givenRequestWithToken(ApiTestConstants.CODESYSTEMS_API, token)
			.get()
			.then()
			.assertThat().statusCode(200)
			.and().body("total", equalTo(2));
	}
	
	@Test
	public void readOnlyAccessOnBundleGivesAccessToResourcesWithinTransitively() throws Exception {
		String token = generateToken(Permission.requireAll(Permission.OPERATION_BROWSE, UK_ALL_BUNDLE_ID));
		RestExtensions.givenRequestWithToken(ApiTestConstants.CODESYSTEMS_API, token)
			.get()
			.then()
			.assertThat().statusCode(200)
			.and().body("total", equalTo(3));
	}
	
	@Test
	public void wildcardPermissionGivesAccessToAllMatchingResources() throws Exception {
		String token = generateToken(Permission.requireAll(Permission.OPERATION_BROWSE, SNOMEDCT_UK_CL+"*"));
		RestExtensions.givenRequestWithToken(ApiTestConstants.CODESYSTEMS_API, token)
			.get()
			.then()
			.assertThat().statusCode(200)
			.and().body("total", equalTo(2));
	}
	
	private String generateToken(Permission...permissions) {
		return ApplicationContext.getServiceForClass(JWTGenerator.class).generate(new User(RestExtensions.USER, List.of(new Role("custom", List.of(permissions)))));
	}
	
}
