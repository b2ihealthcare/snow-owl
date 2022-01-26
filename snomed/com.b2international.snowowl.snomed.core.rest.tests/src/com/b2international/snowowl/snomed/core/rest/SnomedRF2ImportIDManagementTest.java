/*
 * Copyright 2021-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.attachments.AttachmentRegistry;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.jobs.JobRequests;
import com.b2international.snowowl.core.util.PlatformUtil;
import com.b2international.snowowl.snomed.cis.domain.SctId;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.core.rest.io.SnomedImportApiTest;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.request.rf2.SnomedRf2Requests;
import com.b2international.snowowl.test.commons.Services;
import com.b2international.snowowl.test.commons.rest.BranchBase;

/**
 * @since 7.18.0
 */
@BranchBase(Branch.MAIN_PATH + "/2020-01-31") 
public class SnomedRF2ImportIDManagementTest extends AbstractSnomedApiTest {

	@Before
	public void setup() {
		try {
			CodeSystemRequests.prepareGetCodeSystem(SnomedRF2ImportIDManagementTest.class.getSimpleName())
				.build(SnomedDatastoreActivator.REPOSITORY_UUID)
				.execute(getBus())
				.getSync(1L, TimeUnit.MINUTES);

		} catch (NotFoundException e) {
			CodeSystemRequests.prepareNewCodeSystem()
				.setBranchPath(Branch.MAIN_PATH + "/2020-01-31/" + SnomedRF2ImportIDManagementTest.class.getSimpleName())
				.setShortName(SnomedRF2ImportIDManagementTest.class.getSimpleName())
				.setName(SnomedRF2ImportIDManagementTest.class.getSimpleName())
				.setTerminologyId(SnomedTerminologyComponentConstants.TERMINOLOGY_ID)
				.setRepositoryId(SnomedDatastoreActivator.REPOSITORY_UUID)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, Branch.MAIN_PATH, "info@b2international.com", "Created new code system " + SnomedRF2ImportIDManagementTest.class.getSimpleName())
				.execute(getBus())
				.getSync(1L, TimeUnit.MINUTES);
		}
	}
	
	@Test
	public void publishReleasedIdOnImport() throws Exception {
		final String branch = branchPath.getPath();
		final String importJobId = SnomedRf2Requests.importJobKey(branch);
		final UUID archiveId = UUID.randomUUID();
		final File importArchive = PlatformUtil.toAbsolutePath(SnomedImportApiTest.class, "SnomedCT_Release_INT_20210502_concept_w_eff_time.zip").toFile();
		ApplicationContext.getServiceForClass(AttachmentRegistry.class).upload(archiveId, new FileInputStream(importArchive));

		String jobId = SnomedRequests.rf2().prepareImport()
				.setRf2ArchiveId(archiveId)
				.setReleaseType(Rf2ReleaseType.DELTA)
				.setCreateVersions(false)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
				.runAsJobWithRestart(importJobId, String.format("Importing SNOMED CT RF2 file '%s'", importArchive.getName()))
				.execute(getBus())
				.getSync(1, TimeUnit.MINUTES);

		JobRequests.waitForJob(getBus(), jobId, 50);
		
		SctId sctId = SnomedRequests.identifiers().prepareGet()
				.setComponentId("100005")
				.buildAsync()
				.execute(Services.bus())
				.getSync()
				.first()
				.get();
			
		assertEquals("Published", sctId.getStatus());
	}
	
	@Test
	public void registerNewIdOnImport() throws Exception {
		final String branch = branchPath.getPath();
		final String importJobId = SnomedRf2Requests.importJobKey(branch);
		final UUID archiveId = UUID.randomUUID();
		final File importArchive = PlatformUtil.toAbsolutePath(SnomedImportApiTest.class, "SnomedCT_Release_INT_20150131_new_concept.zip").toFile();
		ApplicationContext.getServiceForClass(AttachmentRegistry.class).upload(archiveId, new FileInputStream(importArchive));
		
		String jobId = SnomedRequests.rf2().prepareImport()
				.setRf2ArchiveId(archiveId)
				.setReleaseType(Rf2ReleaseType.DELTA)
				.setCreateVersions(false)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
				.runAsJobWithRestart(importJobId, String.format("Importing SNOMED CT RF2 file '%s'", importArchive.getName()))
				.execute(getBus())
				.getSync(1, TimeUnit.MINUTES);
		
		JobRequests.waitForJob(getBus(), jobId, 50);
		
		SctId sctId = SnomedRequests.identifiers().prepareGet()
				.setComponentId("63961392103")
				.buildAsync()
				.execute(Services.bus())
				.getSync()
				.first()
				.get();
		
		assertEquals("Assigned", sctId.getStatus());
		
		final UUID archiveId2 = UUID.randomUUID();
		final File importArchive2 = PlatformUtil.toAbsolutePath(SnomedImportApiTest.class, "SnomedCT_Release_INT_20150131_release_unpublished_concept.zip").toFile();
		ApplicationContext.getServiceForClass(AttachmentRegistry.class).upload(archiveId2, new FileInputStream(importArchive2));
		
		String jobId2 = SnomedRequests.rf2().prepareImport()
				.setRf2ArchiveId(archiveId2)
				.setReleaseType(Rf2ReleaseType.DELTA)
				.setCreateVersions(false)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
				.runAsJobWithRestart(importJobId, String.format("Importing SNOMED CT RF2 file '%s'", importArchive2.getName()))
				.execute(getBus())
				.getSync(1, TimeUnit.MINUTES);
		
		JobRequests.waitForJob(getBus(), jobId2, 50);
		
		SctId updatedSctId = SnomedRequests.identifiers().prepareGet()
				.setComponentId("63961392103")
				.buildAsync()
				.execute(Services.bus())
				.getSync()
				.first()
				.get();
		
		assertEquals("Published", updatedSctId.getStatus());

	}
	
}
