/*
 * Copyright 2018-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.tests;

import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.jobs.JobRequests;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.test.commons.Services;
import com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests;

/**
 * Common superclass for test artefact creators
 * 
 * @since 7.0
 */
public class TestArtifactCreator {
	
	protected synchronized static void createVersion(String version, String codeSystemName) {
		String nextAvailableEffectiveTime = CodeSystemVersionRestRequests.getNextAvailableEffectiveDateAsString(codeSystemName);
		String jobId = CodeSystemRequests.prepareNewVersion()
			.setCodeSystemShortName(codeSystemName)
			.setDescription("FHIR Test version")
			.setVersion(version)
			.setEffectiveTime(nextAvailableEffectiveTime)
			.buildAsync()
			.runAsJob(String.format("Creating version '%s/%s'", codeSystemName, version))
			.execute(getEventBus())
			.getSync();
		
		JobRequests.waitForJob(getEventBus(), jobId, 1000);
	}
	
	protected static IEventBus getEventBus() {
		return Services.bus();
	}

}
