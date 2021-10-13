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
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.b2international.snowowl.core.attachments.Attachment;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.jobs.JobRequests;
import com.b2international.snowowl.core.jobs.RemoteJobEntry;
import com.b2international.snowowl.core.util.PlatformUtil;
import com.b2international.snowowl.core.version.Version;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.request.rf2.SnomedRf2Requests;
import com.b2international.snowowl.test.commons.Resources;
import com.b2international.snowowl.test.commons.Services;
import com.b2international.snowowl.test.commons.SnomedContentRule;
import com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests;

/**
 * @since 8.0
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SnomedRf2NextReleaseImportTest extends AbstractSnomedApiTest {

	@Test
	public void import01_CurrentReleaseAgain_NoChanges() throws Exception {
		doImport(Resources.Snomed.MINI_RF2_INT_20210731, "20210131");
	}

	@Test
	public void import02_NextFullRelease_20210731() throws Exception {
		doImport(Resources.Snomed.MINI_RF2_INT_20210731, "20210731");
	}
	
	private void doImport(final String importFile, final String importUntil) {
		Attachment attachment = Attachment.upload(Services.context(), PlatformUtil.toAbsolutePathBundleEntry(SnomedContentRule.class, importFile));
		String jobId = SnomedRequests.rf2().prepareImport()
			.setRf2Archive(attachment)
			.setReleaseType(Rf2ReleaseType.FULL)
			.setCreateVersions(true)
			.setImportUntil(importUntil)
			.build(SnomedContentRule.SNOMEDCT)
			.runAsJobWithRestart(SnomedRf2Requests.importJobKey(SnomedContentRule.SNOMEDCT), String.format("Import %s release", importFile))
			.execute(Services.bus())
			.getSync(1, TimeUnit.MINUTES);
		RemoteJobEntry job = JobRequests.waitForJob(Services.bus(), jobId, 2000 /* 2 seconds */);
		assertTrue("Failed to import RF2 archive", job.isSuccessful());
		// assert that the version for importUntil is present in the system
		Version latestVersion = CodeSystemVersionRestRequests.getLatestVersion(SnomedContentRule.SNOMEDCT_ID).get();
		assertEquals(importUntil, EffectiveTimes.format(latestVersion.getEffectiveTime(), DateFormats.SHORT));
	}
	
}
