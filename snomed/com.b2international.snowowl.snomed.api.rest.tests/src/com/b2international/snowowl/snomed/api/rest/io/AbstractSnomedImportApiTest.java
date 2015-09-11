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
package com.b2international.snowowl.snomed.api.rest.io;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;

import java.util.Map;

import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants;
import com.b2international.snowowl.test.commons.rest.RestExtensions;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

/**
 * @since 2.0
 */
public abstract class AbstractSnomedImportApiTest extends AbstractSnomedApiTest {

	protected Response whenCreatingImportConfiguration(final Map<?, ?> importConfiguration) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.with().contentType(ContentType.JSON)
				.and().body(importConfiguration)
				.when().post("/imports");
	}

	protected String assertImportConfigurationCanBeCreated(final Map<?, ?> importConfiguration) {
		final Response response = whenCreatingImportConfiguration(importConfiguration);

		final String location = RestExtensions.expectStatus(response, 201)
				.and().extract().response().header("Location");
		
		return lastPathSegment(location);
	}
}
