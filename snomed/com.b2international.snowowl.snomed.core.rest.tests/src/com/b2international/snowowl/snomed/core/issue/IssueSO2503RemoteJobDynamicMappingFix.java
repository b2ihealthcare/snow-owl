/*
 * Copyright 2017-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.issue;

import static com.b2international.snowowl.test.commons.codesystem.CodeSystemRestRequests.createCodeSystem;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.getNextAvailableEffectiveDate;

import java.time.LocalDate;

import org.junit.Test;

import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.jobs.JobRequests;
import com.b2international.snowowl.snomed.core.rest.AbstractSnomedApiTest;

/**
 * @since 5.11.3
 */
public class IssueSO2503RemoteJobDynamicMappingFix extends AbstractSnomedApiTest {

	@Test
	public void verify() throws Exception {
		// create a codesystem to test on
		String codeSystemShortName = "SNOMEDCT-ISSUESO2503";
		createCodeSystem(branchPath, codeSystemShortName).statusCode(201);
		
		// 1. create a version with a datelike versionId
		LocalDate nextAvailableEffectiveDate1 = getNextAvailableEffectiveDate(codeSystemShortName);
		CodeSystemRequests.prepareNewVersion()
			.setCodeSystemShortName(codeSystemShortName)
			// XXX use default format, ES will likely try to convert this to a date field, unless we disable it in the mapping
			.setVersionId(nextAvailableEffectiveDate1.toString())
			.setEffectiveTime(EffectiveTimes.format(nextAvailableEffectiveDate1, DateFormats.SHORT))
			.buildAsync()
			.runAsJob("Creating version with datelike versionId")
			.execute(getBus())
			.then(this::waitDone)
			.thenWith(unused -> {
				// 2. create another version with a non-datelike versionId
				LocalDate nextAvailableEffectiveDate2 = getNextAvailableEffectiveDate(codeSystemShortName);
				return CodeSystemRequests.prepareNewVersion()
					.setCodeSystemShortName(codeSystemShortName)
					.setVersionId("xx-" + nextAvailableEffectiveDate2.toString())
					.setEffectiveTime(EffectiveTimes.format(nextAvailableEffectiveDate2, DateFormats.SHORT))
					.buildAsync()
					.runAsJob("Creating version with non-datelike versionId")
					.execute(getBus());
			})
			.then(this::waitDone)
			.getSync();
		// 3. second step either has failed with index exception or the job is not available via the remote job API thus it throws a NotFoundException on first get call
	}

	private Object waitDone(String jobId) {
		return JobRequests.waitForJob(getBus(), jobId, 50);
	}
	
}
