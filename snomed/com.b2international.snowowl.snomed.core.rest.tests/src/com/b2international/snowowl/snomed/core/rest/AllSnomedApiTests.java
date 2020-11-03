/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.rest;

import org.junit.ClassRule;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.branch.BranchCompareRequestTest;
import com.b2international.snowowl.snomed.core.branch.SnomedBranchRequestTest;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.core.io.MrcmImportExportTest;
import com.b2international.snowowl.snomed.core.io.SnomedRefSetDSVExportTest;
import com.b2international.snowowl.snomed.core.issue.EclSerializerTest;
import com.b2international.snowowl.snomed.core.issue.IssueSO2503RemoteJobDynamicMappingFix;
import com.b2international.snowowl.snomed.core.request.ConceptMapSearchMappingRequestSnomedMapTypeReferenceSetTest;
import com.b2international.snowowl.snomed.core.request.ConceptSearchRequestSnomedTest;
import com.b2international.snowowl.snomed.core.request.ValueSetMemberSearchSnomedReferenceSetTest;
import com.b2international.snowowl.snomed.core.rest.branches.SnomedBranchingApiTest;
import com.b2international.snowowl.snomed.core.rest.branches.SnomedMergeApiTest;
import com.b2international.snowowl.snomed.core.rest.branches.SnomedMergeConflictTest;
import com.b2international.snowowl.snomed.core.rest.branches.SnomedReviewApiTest;
import com.b2international.snowowl.snomed.core.rest.classification.SnomedClassificationApiTest;
import com.b2international.snowowl.snomed.core.rest.compare.ConceptMapCompareSnomedMapTypeReferenceSetTest;
import com.b2international.snowowl.snomed.core.rest.components.*;
import com.b2international.snowowl.snomed.core.rest.io.SnomedExportApiTest;
import com.b2international.snowowl.snomed.core.rest.io.SnomedImportApiTest;
import com.b2international.snowowl.snomed.core.rest.io.SnomedImportRowValidatorTest;
import com.b2international.snowowl.snomed.core.rest.perf.SnomedConceptCreatePerformanceTest;
import com.b2international.snowowl.snomed.core.rest.perf.SnomedMergePerformanceTest;
import com.b2international.snowowl.snomed.core.rest.versioning.SnomedVersioningApiTest;
import com.b2international.snowowl.test.commons.BundleStartRule;
import com.b2international.snowowl.test.commons.Resources;
import com.b2international.snowowl.test.commons.SnomedContentRule;
import com.b2international.snowowl.test.commons.SnowOwlAppRule;

/**
 * @since 1.0
 */
@RunWith(Suite.class)
@SuiteClasses({ 
	SnomedRf2ContentImportTest.class,
	// High-level issue test cases, Java API test cases
	IssueSO2503RemoteJobDynamicMappingFix.class,
	Issue3019FixDeletionOfReferringMembersTest.class,
	EclSerializerTest.class,
	// RESTful API test cases
	// Branch API tests
	SnomedBranchRequestTest.class,
	BranchCompareRequestTest.class,
	SnomedCompareRestRequestTest.class,
	SnomedBranchingApiTest.class,
	// Component API test cases
	SnomedConceptApiTest.class,
	SnomedConceptInactivationApiTest.class,
	SnomedDescriptionApiTest.class,
	SnomedRelationshipApiTest.class,
	SnomedPartialLoadingApiTest.class,
	SnomedComponentInactivationApiTest.class,
	SnomedRefSetApiTest.class,
	SnomedReferenceSetDeletionPerformanceTest.class,
	SnomedRefSetParameterizedTest.class,
	SnomedRefSetMemberParameterizedTest.class,
	SnomedRefSetMemberApiTest.class,
	SnomedRefSetBulkApiTest.class,
	// Merge, Review test cases
	SnomedMergeApiTest.class,
	SnomedMergeConflictTest.class,
	SnomedReviewApiTest.class,
	// Import-Export-Versioning-Classification
	SnomedClassificationApiTest.class,
	SnomedImportApiTest.class,
	SnomedImportRowValidatorTest.class,
	SnomedExportApiTest.class,
	SnomedRefSetDSVExportTest.class,
	// Module dependecy test cases - they modify the MAIN branch so should be executed after tests that rely on MAIN branch stuff
	SnomedModuleDependencyRefsetTest.class,
	SnomedVersioningApiTest.class,
	// Extension test cases - 7.0.preview version currently does not support extension upgrade
//	SnomedExtensionUpgradeTest.class, 
//	SnomedExtensionDowngradeTest.class,
//	SnomedExtensionVersioningTest.class,
	// MRCM export/importF
	MrcmImportExportTest.class,
	// Performance test cases
	SnomedConceptCreatePerformanceTest.class,
	SnomedMergePerformanceTest.class,
	// Generic API
	ConceptSearchRequestSnomedTest.class,
	ValueSetMemberSearchSnomedReferenceSetTest.class,
	ConceptMapCompareSnomedMapTypeReferenceSetTest.class,
	ConceptMapSearchMappingRequestSnomedMapTypeReferenceSetTest.class
})
public class AllSnomedApiTests {

	@ClassRule
	public static final RuleChain appRule = RuleChain
			.outerRule(SnowOwlAppRule.snowOwl(AllSnomedApiTests.class))
			.around(new BundleStartRule("org.eclipse.jetty.osgi.boot"))
			.around(new BundleStartRule("com.b2international.snowowl.core.rest"))
			.around(new SnomedContentRule(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME, Branch.MAIN_PATH, Resources.Snomed.MINI_RF2_INT, Rf2ReleaseType.FULL))
			.around(new SnomedContentRule(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME, SnomedApiTestConstants.EXTENSION_PATH, Resources.Snomed.MINI_RF2_EXT, Rf2ReleaseType.DELTA))
			.around(new SnomedContentRule(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME, Branch.MAIN_PATH, Resources.Snomed.MINI_RF2_COMPLEX_BLOCK_MAP, Rf2ReleaseType.DELTA));

}
