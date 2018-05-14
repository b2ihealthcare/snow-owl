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
package com.b2international.snowowl.snomed.api.rest.ext;

import static com.b2international.snowowl.snomed.api.rest.CodeSystemRestRequests.getCodeSystem;
import static com.b2international.snowowl.snomed.api.rest.CodeSystemRestRequests.updateCodeSystem;
import static com.b2international.snowowl.snomed.api.rest.CodeSystemVersionRestRequests.getVersion;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingRestRequests.createBranch;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.createComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewConcept;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.merge;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.Map;

import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;

import com.b2international.snowowl.api.impl.codesystem.domain.CodeSystemVersion;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.merge.Merge;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.BranchBase;
import com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.google.common.collect.ImmutableMap;

/**
 * @since 4.7
 */
@BranchBase(value = SnomedApiTestConstants.EXTENSION_PATH, isolateTests = false)
public class SnomedExtensionDowngradeTest extends AbstractSnomedApiTest {

	@Test
	@Ignore // donation made this obsolete / invalid 
	public void downgradeB2iExtensionWithoutChanges() {
		CodeSystemVersion version = getVersion(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME, "2016-01-31")
				.statusCode(200)
				.extract().as(CodeSystemVersion.class);

		IBranchPath targetPath = BranchPathUtils.createPath(SnomedApiTestConstants.PATH_JOINER.join(version.getParentBranchPath(), 
				version.getVersion(),
				SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME));

		createBranch(targetPath).statusCode(201);
		merge(branchPath, targetPath, "Downgraded B2i extension to 2016-01-31.").body("status", equalTo(Merge.Status.COMPLETED.name()));

		Map<?, ?> updateRequest = ImmutableMap.builder()
				.put("repositoryUuid", SnomedDatastoreActivator.REPOSITORY_UUID)
				.put("branchPath", targetPath.getPath())
				.build();

		updateCodeSystem(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME, updateRequest).statusCode(204);

		getCodeSystem(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME).statusCode(200)
		.body("branchPath", equalTo(targetPath.getPath()));
	}

	@Test
	@Ignore // donation made this obsolete / invalid
	public void downgradeB2iExtensionWithNewConceptOnUpgradedBranch() {
		CodeSystemVersion version = getVersion(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME, "2015-07-31")
				.statusCode(200)
				.extract().as(CodeSystemVersion.class);

		IBranchPath targetPath = BranchPathUtils.createPath(SnomedApiTestConstants.PATH_JOINER.join(version.getParentBranchPath(), 
				version.getVersion(),
				SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME));

		createBranch(targetPath).statusCode(201);

		String conceptId = createNewConcept(targetPath);

		merge(branchPath, targetPath, "Downgraded B2i extension to 2015-07-31.").body("status", equalTo(Merge.Status.COMPLETED.name()));

		Map<?, ?> updateRequest = ImmutableMap.builder()
				.put("repositoryUuid", SnomedDatastoreActivator.REPOSITORY_UUID)
				.put("branchPath", targetPath.getPath())
				.build();

		updateCodeSystem(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME, updateRequest).statusCode(204);

		getCodeSystem(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME).statusCode(200)
		.body("branchPath", equalTo(targetPath.getPath()));

		getComponent(targetPath, SnomedComponentType.CONCEPT, conceptId).statusCode(200);
	}

	@Test
	public void downgradeWithConflictingContent() {
		CodeSystemVersion version = getVersion(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME, "2015-01-31")
				.statusCode(200)
				.extract().as(CodeSystemVersion.class);

		IBranchPath targetPath = BranchPathUtils.createPath(SnomedApiTestConstants.PATH_JOINER.join(version.getParentBranchPath(), 
				version.getVersion(),
				SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME));

		createBranch(targetPath).statusCode(201);

		Map<?, ?> requestBody = ImmutableMap.builder()
				.put("id", "476216051000154119") // Description of Date-time reference set
				.put("conceptId", Concepts.ROOT_CONCEPT)
				.put("moduleId", Concepts.MODULE_SCT_CORE)
				.put("typeId", Concepts.SYNONYM)
				.put("term", "Synonym of root concept")
				.put("languageCode", "en")
				.put("acceptability", SnomedApiTestConstants.UK_ACCEPTABLE_MAP)
				.put("caseSignificance", CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE)
				.put("commitComment", "Created new synonym with duplicate SCTID")
				.build();

		createComponent(targetPath, SnomedComponentType.DESCRIPTION, requestBody).statusCode(201);

		merge(branchPath, targetPath, "Downgraded B2i extension to 2015-01-31.").body("status", equalTo(Merge.Status.CONFLICTS.name()));
	}

	@AfterClass
	public static void restoreB2iCodeSystem() {
		Map<?, ?> updateRequest = ImmutableMap.builder()
				.put("repositoryUuid", SnomedDatastoreActivator.REPOSITORY_UUID)
				.put("branchPath", SnomedApiTestConstants.EXTENSION_PATH)
				.build();

		updateCodeSystem(SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME, updateRequest).statusCode(204);
	}

}
