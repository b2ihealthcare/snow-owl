/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.attachments.Attachment;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.jobs.JobRequests;
import com.b2international.snowowl.core.util.PlatformUtil;
import com.b2international.snowowl.snomed.cis.domain.SctId;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.core.rest.io.SnomedImportApiTest;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.request.rf2.SnomedRf2Requests;
import com.b2international.snowowl.test.commons.Services;
import com.b2international.snowowl.test.commons.rest.BranchBase;

/**
 * @since 8.10.1
 */
@BranchBase(Branch.MAIN_PATH + "/2020-01-31") 
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SnomedRF2ImportIDManagementTest extends AbstractSnomedApiTest {

	private String codeSystemId;

	@Before
	public void setup() {
		codeSystemId = SnomedRF2ImportIDManagementTest.class.getSimpleName();
		
		try {
			CodeSystemRequests.prepareGetCodeSystem(codeSystemId)
				.buildAsync()
				.execute(getBus())
				.getSync(1L, TimeUnit.MINUTES);

		} catch (NotFoundException e) {
			CodeSystemRequests.prepareNewCodeSystem()
				.setBranchPath(Branch.MAIN_PATH + "/2020-01-31/" + codeSystemId)
				.setId(codeSystemId)
				.setToolingId(SnomedTerminologyComponentConstants.TOOLING_ID)
				.setUrl(SnomedTerminologyComponentConstants.SNOMED_URI_SCT + "/" + codeSystemId)
				.setTitle(codeSystemId)
				.build("info@b2international.com", "Created new code system " + codeSystemId)
				.execute(getBus())
				.getSync(1L, TimeUnit.MINUTES);
		}
	}
	
	@Test
	public void test01PublishReleasedIdOnImport() throws Exception {
		final String branch = branchPath.getPath();
		final String importJobId = SnomedRf2Requests.importJobKey(branch);
		final String archiveName = "SnomedCT_Release_INT_20210502_concept_w_eff_time.zip";
		final Path path = PlatformUtil.toAbsolutePath(SnomedImportApiTest.class, archiveName);
		Attachment attachment = Attachment.upload(Services.context(), path);
		
		String jobId = SnomedRequests.rf2().prepareImport()
				.setRf2Archive(attachment)
				.setReleaseType(Rf2ReleaseType.DELTA)
				.setCreateVersions(false)
				.build(codeSystemId)
				.runAsJobWithRestart(importJobId, String.format("Importing SNOMED CT RF2 file '%s'", archiveName))
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
	public void test02RegisterNewIdOnImport() throws Exception {
		final String branch = branchPath.getPath();
		final String importJobId = SnomedRf2Requests.importJobKey(branch);
		final String archiveName = "SnomedCT_Release_INT_20150131_new_concept.zip";
		final Path path = PlatformUtil.toAbsolutePath(SnomedImportApiTest.class, archiveName);
		Attachment attachment = Attachment.upload(Services.context(), path);
		
		String jobId = SnomedRequests.rf2().prepareImport()
				.setRf2Archive(attachment)
				.setReleaseType(Rf2ReleaseType.DELTA)
				.setCreateVersions(false)
				.build(codeSystemId)
				.runAsJobWithRestart(importJobId, String.format("Importing SNOMED CT RF2 file '%s'", archiveName))
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
		
		final String importJobId2 = SnomedRf2Requests.importJobKey(branch);
		final String archiveName2 = "SnomedCT_Release_INT_20150131_release_unpublished_concept.zip";
		final Path path2 = PlatformUtil.toAbsolutePath(SnomedImportApiTest.class, archiveName2);
		Attachment attachment2 = Attachment.upload(Services.context(), path2);		
		
		String jobId2 = SnomedRequests.rf2().prepareImport()
				.setRf2Archive(attachment2)
				.setReleaseType(Rf2ReleaseType.DELTA)
				.setCreateVersions(false)
				.build(codeSystemId)
				.runAsJobWithRestart(importJobId2, String.format("Importing SNOMED CT RF2 file '%s'", archiveName2))
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
