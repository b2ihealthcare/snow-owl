/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.japi.issue;

import static com.b2international.snowowl.snomed.api.rest.CodeSystemRestRequests.createCodeSystem;
import static com.b2international.snowowl.snomed.api.rest.CodeSystemRestRequests.getCodeSystem;
import static com.b2international.snowowl.snomed.api.rest.CodeSystemVersionRestRequests.getNextAvailableEffectiveDate;

import java.util.Date;

import org.junit.Test;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobEntry;
import com.b2international.snowowl.datastore.request.job.JobRequests;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;
import com.google.common.collect.ImmutableSet;
import com.jayway.restassured.response.ValidatableResponse;

/**
 * @since 5.11.3
 */
public class IssueSO2503RemoteJobDynamicMappingFix extends AbstractSnomedApiTest {

	@Test
	public void verify() throws Exception {
		// create a codesystem to test on
		String codeSystemShortName = "SNOMEDCT-ISSUESO2503";
		createCodeSystem(branchPath, codeSystemShortName).statusCode(201);
		
		ValidatableResponse codeSystem = getCodeSystem(codeSystemShortName);
		final String branchPath = codeSystem.extract().path("branchPath");
		
		// 1. create a version with a datelike versionId
		Request<ServiceProvider, Boolean> v1Req = CodeSystemRequests.prepareNewCodeSystemVersion()
			.setCodeSystemShortName(codeSystemShortName)
			// XXX use default format, ES will likely try to convert this to a date field, unless we disable it in the mapping
			.setVersionId(Dates.formatByGmt(getNextAvailableEffectiveDate(codeSystemShortName), DateFormats.DEFAULT))
			.setEffectiveTime(new Date())
			.setToolingIds(ImmutableSet.of(SnomedTerminologyComponentConstants.TERMINOLOGY_ID))
			.setParentBranchPath(branchPath)
			.setPrimaryToolingId(SnomedTerminologyComponentConstants.TERMINOLOGY_ID)
			.build();

		scheduleJob(v1Req, "Creating version with datelike versionId")
			.then(this::waitDone)
			.thenWith(unused -> {
				// 2. create another version with a non-datelike versionId
				Request<ServiceProvider, Boolean> v2Req = CodeSystemRequests.prepareNewCodeSystemVersion()
					.setCodeSystemShortName(codeSystemShortName)
					.setVersionId("xx-" + Dates.formatByGmt(getNextAvailableEffectiveDate(codeSystemShortName), DateFormats.DEFAULT))
					.setEffectiveTime(new Date())
					.setToolingIds(ImmutableSet.of(SnomedTerminologyComponentConstants.TERMINOLOGY_ID))
					.setParentBranchPath(branchPath)
					.setPrimaryToolingId(SnomedTerminologyComponentConstants.TERMINOLOGY_ID)
					.build();
				return scheduleJob(v2Req, "Creating version with non-datelike versionId");
			})
			.then(this::waitDone)
			.getSync();
		// 3. second step either has failed with index exception or the job is not available via the remote job API thus it throws a NotFoundException on first get call
	}

	private Promise<String> scheduleJob(Request<ServiceProvider, ?> req, String description) {
		return JobRequests.prepareSchedule()
			.setDescription(description)
			.setUser("test")
			.setRequest(req)
			.buildAsync()
			.execute(getBus());
	}

	private Object waitDone(String jobId) {
		RemoteJobEntry job;
		do {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			job = JobRequests.prepareGet(jobId).buildAsync().execute(getBus()).getSync();
		} while (!job.isDone());
		return null;
	}
	
	private IEventBus getBus() {
		return ApplicationContext.getServiceForClass(IEventBus.class);
	}
	
}
