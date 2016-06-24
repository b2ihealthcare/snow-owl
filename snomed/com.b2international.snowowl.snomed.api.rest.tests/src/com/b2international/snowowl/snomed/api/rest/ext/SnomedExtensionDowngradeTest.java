/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest.ext;

import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.MODULE_SCT_CORE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.ROOT_CONCEPT;
import static com.b2international.snowowl.snomed.api.rest.CodeSystemApiAssert.assertCodeSystemHasProperty;
import static com.b2international.snowowl.snomed.api.rest.CodeSystemApiAssert.assertCodeSystemUpdated;
import static com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants.PREFERRED_ACCEPTABILITY_MAP;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.assertBranchCanBeMerged;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.assertMergeJobFails;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentCreated;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertConceptExists;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertConceptNotExists;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.givenConceptRequestBody;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentType.CONCEPT;

import java.util.Map;

import org.junit.Test;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.google.common.collect.ImmutableMap;

/**
 * @since 4.7
 */
public class SnomedExtensionDowngradeTest extends ExtensionTest {
	
	@Test
	public void downgradeB2iExtensionWithoutChanges() {
		assertB2iExtensionExistsWithDefaults();
		
		final IBranchPath b2iBranchPath = BranchPathUtils.createPath(B2I_EXT_BRANCH);
		final IBranchPath branchPath = createBranchOnNewVersion(INT_SHORT_NAME);
		
		assertBranchCanBeMerged(b2iBranchPath, branchPath, "Upgrade extension branch.");
		
		assertCodeSystemUpdated(B2I_EXT_SHORT_NAME, ImmutableMap.of("branchPath", branchPath.getPath(), "repositoryUuid", "snomedStore"));
		assertCodeSystemHasProperty(B2I_EXT_SHORT_NAME, "branchPath", branchPath.getPath());
		
		assertBranchCanBeMerged(branchPath, b2iBranchPath, "Downgrade extension branch.");
		
		assertCodeSystemUpdated(B2I_EXT_SHORT_NAME, ImmutableMap.of("branchPath", b2iBranchPath.getPath(), "repositoryUuid", "snomedStore"));
		assertCodeSystemHasProperty(B2I_EXT_SHORT_NAME, "branchPath", b2iBranchPath.getPath());
	}
	
	@Test
	public void downgradeB2iExtensionWithNewConceptOnUpgradedBranch() {
		assertB2iExtensionExistsWithDefaults();
		
		final IBranchPath b2iBranchPath = BranchPathUtils.createPath(B2I_EXT_BRANCH);
		final IBranchPath branchPath = createBranchOnNewVersion(INT_SHORT_NAME);
		
		assertBranchCanBeMerged(b2iBranchPath, branchPath, "Upgrade extension branch.");
		
		assertCodeSystemUpdated(B2I_EXT_SHORT_NAME, ImmutableMap.of("branchPath", branchPath.getPath(), "repositoryUuid", "snomedStore"));
		assertCodeSystemHasProperty(B2I_EXT_SHORT_NAME, "branchPath", branchPath.getPath());
		
		final IBranchPath branchPath2 = createBranchOnNewVersion(INT_SHORT_NAME);
		
		assertBranchCanBeMerged(branchPath, branchPath2, "Upgrade extension branch.");
		
		assertCodeSystemUpdated(B2I_EXT_SHORT_NAME, ImmutableMap.of("branchPath", branchPath2.getPath(), "repositoryUuid", "snomedStore"));
		assertCodeSystemHasProperty(B2I_EXT_SHORT_NAME, "branchPath", branchPath2.getPath());

		final Map<?, ?> requestBody = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		final String conceptId = assertComponentCreated(branchPath2, CONCEPT, requestBody);
		
		assertBranchCanBeMerged(branchPath2, branchPath, "Downgrade extension branch with new concept.");
		
		assertCodeSystemUpdated(B2I_EXT_SHORT_NAME, ImmutableMap.of("branchPath", branchPath.getPath(), "repositoryUuid", "snomedStore"));
		assertCodeSystemHasProperty(B2I_EXT_SHORT_NAME, "branchPath", branchPath.getPath());
		
		assertConceptExists(branchPath, conceptId);
	}
	
	@Test
	public void downgradeB2iExtensionWithConflictingContent() {
		assertB2iExtensionExistsWithDefaults();
		
		final IBranchPath b2iBranchPath = BranchPathUtils.createPath(B2I_EXT_BRANCH);
		final IBranchPath branchPath = createBranchOnNewVersion(INT_SHORT_NAME);
		
		assertBranchCanBeMerged(b2iBranchPath, branchPath, "Upgrade extension branch.");
		
		assertCodeSystemUpdated(B2I_EXT_SHORT_NAME, ImmutableMap.of("branchPath", branchPath.getPath(), "repositoryUuid", "snomedStore"));
		assertCodeSystemHasProperty(B2I_EXT_SHORT_NAME, "branchPath", branchPath.getPath());
		
		final Map<?, ?> mainConceptRequestBody = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		final String conceptId = assertComponentCreated(BranchPathUtils.createMainPath(), CONCEPT, mainConceptRequestBody);
		
		final IBranchPath branchPath2 = createBranchOnNewVersion(INT_SHORT_NAME);
		
		assertBranchCanBeMerged(branchPath, branchPath2, "Upgrade extension branch.");
		
		assertCodeSystemUpdated(B2I_EXT_SHORT_NAME, ImmutableMap.of("branchPath", branchPath2.getPath(), "repositoryUuid", "snomedStore"));
		assertCodeSystemHasProperty(B2I_EXT_SHORT_NAME, "branchPath", branchPath2.getPath());

		final Map<?, ?> extensionConceptRequestBody = givenConceptRequestBody(null, conceptId, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		assertComponentCreated(branchPath2, CONCEPT, extensionConceptRequestBody);
		
		assertConceptNotExists(branchPath, conceptId);
		assertMergeJobFails(branchPath2, branchPath, "Downgrade extension branch with conflicting content.");
	}
	
}
