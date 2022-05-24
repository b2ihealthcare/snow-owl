/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.test.commons.auth;

import static com.b2international.snowowl.test.commons.rest.BundleApiAssert.createBundle;
import static com.b2international.snowowl.test.commons.rest.BundleApiAssert.prepareBundleCreateRequestBody;
import static com.b2international.snowowl.test.commons.rest.CodeSystemApiAssert.assertCodeSystemCreate;
import static org.hamcrest.CoreMatchers.containsString;

import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.b2international.commons.json.Json;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.test.commons.Services;
import com.b2international.snowowl.test.commons.rest.RestExtensions;

/**
 * @since 8.4
 */
public abstract class BaseAuthorizationTest {

	public static final String SNOMEDCT_ID = "SNOMEDCT";
	public static final String SNOMEDCT_UK_CL = "SNOMEDCT-UK-CL";
	public static final String SNOMEDCT_UK_CL_MICRO = "SNOMEDCT-UK-CL-MICRO";
	public static final String SNOMEDCT_UK_DR = "SNOMEDCT-UK-DR";
	public static final String UK_ALL_BUNDLE_ID = "uk-all";
	public static final String UK_CLINICAL_BUNDLE_ID = "uk-clinical";
	
	protected static final Json SNOMED = Json.object(
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
	
}
