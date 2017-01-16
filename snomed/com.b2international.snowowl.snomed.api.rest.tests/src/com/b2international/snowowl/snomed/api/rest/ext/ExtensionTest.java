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

import static com.b2international.snowowl.snomed.api.rest.CodeSystemApiAssert.assertCodeSystemUpdated;
import static com.b2international.snowowl.snomed.api.rest.CodeSystemApiAssert.getCodeSystem;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.assertBranchExists;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.givenBranchWithPath;
import static com.b2international.snowowl.snomed.api.rest.SnomedVersioningApiAssert.assertVersionCreated;
import static com.b2international.snowowl.snomed.api.rest.SnomedVersioningApiAssert.getLatestAvailableVersionDateAsString;

import java.text.SimpleDateFormat;
import java.util.UUID;

import com.b2international.snowowl.api.impl.codesystem.domain.CodeSystem;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.api.rest.SnomedExtensionApiTests;
import com.google.common.collect.ImmutableMap;

/**
 * @since 4.7
 */
public abstract class ExtensionTest {
	
	protected static final String INT_SHORT_NAME = "SNOMEDCT";
	protected static final String B2I_EXT_SHORT_NAME = "SNOMEDCT-B2I";
	protected static final String B2I_EXT_BRANCH = SnomedExtensionApiTests.EXT_BRANCH_PATH;
	
	protected static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	
	protected void assertB2iExtensionExistsWithDefaults() {
		final IBranchPath b2iBranchPath = BranchPathUtils.createPath(B2I_EXT_BRANCH);
		assertBranchExists(b2iBranchPath);
		
		final CodeSystem codeSystem = getCodeSystem(B2I_EXT_SHORT_NAME);
		if (!codeSystem.getBranchPath().equals(B2I_EXT_BRANCH)) {
			assertCodeSystemUpdated(B2I_EXT_SHORT_NAME, ImmutableMap.of("branchPath", b2iBranchPath.getPath(), "repositoryUuid", "snomedStore"));
		}
	}
	
	protected IBranchPath createBranchOnNewVersion(final String uniqueId) {
		final String versionDate = getLatestAvailableVersionDateAsString(uniqueId);
		final String versionId = UUID.randomUUID().toString();
		
		assertVersionCreated(versionId, versionDate, uniqueId, 201);
		
		final String lastPath = UUID.randomUUID().toString();
		final IBranchPath branchPath = BranchPathUtils.createPath(BranchPathUtils.createMainPath() + "/" + versionId + "/" + lastPath);
		givenBranchWithPath(branchPath);
		
		return branchPath;
	}
	
}
