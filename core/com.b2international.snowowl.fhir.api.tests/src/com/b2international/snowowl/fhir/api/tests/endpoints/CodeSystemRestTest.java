/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.api.tests.endpoints;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import com.b2international.commons.platform.PlatformUtil;
import com.b2international.snowowl.fhir.api.tests.FhirTest;
import com.b2international.snowowl.test.commons.BundleStartRule;
import com.b2international.snowowl.test.commons.SnowOwlAppRule;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;

/**
 * CodeSystem REST end-point test cases
 * @since 6.3
 */
public class CodeSystemRestTest extends FhirTest {
	
	@ClassRule
	public static final RuleChain appRule = RuleChain
			.outerRule(SnowOwlAppRule.snowOwl().clearResources(false).config(PlatformUtil.toAbsolutePath(CodeSystemRestTest.class, "fhir-configuration.yml")))
			.around(new BundleStartRule("com.b2international.snowowl.fhir.api"));
	
	@Test
	public void pingTest() {
		givenAuthenticatedRequest("/fhir")
			.when().get("/CodeSystem/ping")
			.then().assertThat().statusCode(200);
	}
	
	public void codeSystemsTest() {
		
		
		
		givenAuthenticatedRequest("/fhir")
			.when().post("/CodeSystem")
			.then().assertThat().statusCode(200);
	}
	
	

}
