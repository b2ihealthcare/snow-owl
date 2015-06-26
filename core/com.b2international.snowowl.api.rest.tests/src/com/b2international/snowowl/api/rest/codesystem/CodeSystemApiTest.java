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
package com.b2international.snowowl.api.rest.codesystem;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;

import org.junit.Test;

/**
 * @since 1.0
 */
public class CodeSystemApiTest {

	@Test
	public void getAllCodeSystems() {
		givenAuthenticatedRequest("/admin")
		.when().get("/codesystems")
		.then().assertThat().statusCode(200)
		.and().body("items.oid", hasItem("2.16.840.1.113883.6.96"));
	}

	@Test
	public void getCodeSystemByOid() {
		givenAuthenticatedRequest("/admin")
		.when().get("/codesystems/{id}", "2.16.840.1.113883.6.96")
		.then().assertThat().statusCode(200)
		.and().body("oid", equalTo("2.16.840.1.113883.6.96"));		
	}

	@Test
	public void getCodeSystemByShortName() {
		givenAuthenticatedRequest("/admin")
		.when().get("/codesystems/{id}", "SNOMEDCT")
		.then().assertThat().statusCode(200)
		.and().body("shortName", equalTo("SNOMEDCT"));		
	}

	@Test
	public void getCodeSystemByNonExistentOid() {
		givenAuthenticatedRequest("/admin")
		.when().get("/codesystems/{id}", "1.2.3.4.10000")
		.then().assertThat().statusCode(404);		
	}
}
