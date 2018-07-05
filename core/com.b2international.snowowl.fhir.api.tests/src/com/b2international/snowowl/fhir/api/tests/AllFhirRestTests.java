/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.api.tests;

import org.junit.ClassRule;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.b2international.commons.platform.PlatformUtil;
import com.b2international.snowowl.fhir.api.tests.endpoints.CodeSystemRestTest;
import com.b2international.snowowl.test.commons.BundleStartRule;
import com.b2international.snowowl.test.commons.SnowOwlAppRule;

/**
 * FHIR test suite for RESTful operations.
 * @since 6.6
 */
@RunWith(Suite.class)
@SuiteClasses({ 
	CodeSystemRestTest.class
})
public class AllFhirRestTests {
	
	@ClassRule
	public static final RuleChain appRule = RuleChain
			.outerRule(SnowOwlAppRule.snowOwl().clearResources(false).config(PlatformUtil.toAbsolutePath(AllFhirRestTests.class, "fhir-configuration.yml")))
			.around(new BundleStartRule("org.eclipse.jetty.osgi.boot"))
			.around(new BundleStartRule("com.b2international.snowowl.fhir.api"));
}
