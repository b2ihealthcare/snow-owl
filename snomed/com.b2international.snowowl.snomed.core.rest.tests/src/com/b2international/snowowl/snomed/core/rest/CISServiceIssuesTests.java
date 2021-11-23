/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.junit.Test;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.attachments.AttachmentRegistry;
import com.b2international.snowowl.core.jobs.JobRequests;
import com.b2international.snowowl.core.util.PlatformUtil;
import com.b2international.snowowl.snomed.cis.domain.SctId;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.core.rest.io.SnomedImportApiTest;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.request.rf2.SnomedRf2Requests;
import com.b2international.snowowl.test.commons.Services;

/**
 * @since 7.18.0
 */
public class CISServiceIssuesTests extends AbstractSnomedApiTest {

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
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
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
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
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
	}
	
}
