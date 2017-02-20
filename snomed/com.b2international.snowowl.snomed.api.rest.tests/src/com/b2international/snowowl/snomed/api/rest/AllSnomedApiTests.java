/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.junit.ClassRule;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.b2international.commons.platform.PlatformUtil;
import com.b2international.snowowl.snomed.api.impl.SnomedReleases;
import com.b2international.snowowl.snomed.api.japi.branches.SnomedBranchRequestTest;
import com.b2international.snowowl.snomed.api.rest.branches.SnomedBranchingApiTest;
import com.b2international.snowowl.snomed.api.rest.branches.SnomedMergeApiTest;
import com.b2international.snowowl.snomed.api.rest.branches.SnomedMergeConflictTest;
import com.b2international.snowowl.snomed.api.rest.branches.SnomedReviewApiTest;
import com.b2international.snowowl.snomed.api.rest.browser.SnomedBrowserApiTest;
import com.b2international.snowowl.snomed.api.rest.classification.SnomedClassificationApiTest;
import com.b2international.snowowl.snomed.api.rest.components.SnomedConceptApiTest;
import com.b2international.snowowl.snomed.api.rest.components.SnomedDescriptionApiTest;
import com.b2international.snowowl.snomed.api.rest.components.SnomedRefSetApiTest;
import com.b2international.snowowl.snomed.api.rest.components.SnomedRefSetBulkApiTest;
import com.b2international.snowowl.snomed.api.rest.components.SnomedRefSetMemberApiTest;
import com.b2international.snowowl.snomed.api.rest.components.SnomedRelationshipApiTest;
import com.b2international.snowowl.snomed.api.rest.components.SnomedReleasedConceptApiTest;
import com.b2international.snowowl.snomed.api.rest.ext.SnomedExtensionDowngradeTest;
import com.b2international.snowowl.snomed.api.rest.ext.SnomedExtensionUpgradeTest;
import com.b2international.snowowl.snomed.api.rest.ext.SnomedExtensionVersioningTest;
import com.b2international.snowowl.snomed.api.rest.id.SnomedIdentifierApiTest;
import com.b2international.snowowl.snomed.api.rest.io.SnomedExportApiTest;
import com.b2international.snowowl.snomed.api.rest.io.SnomedImportApiTest;
import com.b2international.snowowl.snomed.api.rest.perf.SnomedConceptCreatePerformanceTest;
import com.b2international.snowowl.snomed.api.rest.versioning.SnomedVersioningApiTest;
import com.b2international.snowowl.snomed.common.ContentSubType;
import com.b2international.snowowl.test.commons.BundleStartRule;
import com.b2international.snowowl.test.commons.Resources;
import com.b2international.snowowl.test.commons.SnomedContentRule;
import com.b2international.snowowl.test.commons.SnowOwlAppRule;

/**
 * @since 1.0
 */
@RunWith(Suite.class)
@SuiteClasses({ 
	// RESTful API test cases
//	SnomedBranchingApiTest.class,
//	SnomedMergeApiTest.class,
//	SnomedMergeConflictTest.class,
//	SnomedReviewApiTest.class,
//	SnomedVersioningApiTest.class,
//	SnomedImportApiTest.class,
//	SnomedIdentifierApiTest.class,
//	SnomedConceptApiTest.class,
//	SnomedConceptCreatePerformanceTest.class,
//	SnomedReleasedConceptApiTest.class,
//	SnomedDescriptionApiTest.class,
//	SnomedRelationshipApiTest.class,
//	SnomedRefSetApiTest.class,
//	SnomedRefSetMemberApiTest.class,
//	SnomedRefSetBulkApiTest.class,
//	SnomedBrowserApiTest.class,
//	SnomedClassificationApiTest.class,
	SnomedExportApiTest.class,
	// Extension test cases
//	SnomedExtensionUpgradeTest.class,
//	SnomedExtensionDowngradeTest.class,
//	SnomedExtensionVersioningTest.class,
	// Java API test cases
//	SnomedBranchRequestTest.class
})
public class AllSnomedApiTests {

	@ClassRule
	public static final RuleChain appRule = RuleChain
			.outerRule(SnowOwlAppRule.snowOwl().clearResources(true).config(PlatformUtil.toAbsolutePath(AllSnomedApiTests.class, "snomed-api-test-config.yml")))
			.around(new BundleStartRule("com.b2international.snowowl.api.rest"))
			.around(new BundleStartRule("com.b2international.snowowl.snomed.api.rest"))
			.around(new SnomedContentRule(SnomedReleases.internationalRelease(), Resources.Snomed.MINI_RF2_INT, ContentSubType.FULL))
			.around(new SnomedContentRule(SnomedApiTestConstants.EXTENSION_PATH, SnomedReleases.b2iExtension(SnomedApiTestConstants.EXTENSION_PATH), Resources.Snomed.MINI_RF2_EXT, ContentSubType.DELTA));

}
