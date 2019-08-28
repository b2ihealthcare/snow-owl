/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Date;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobEntry;
import com.b2international.snowowl.datastore.request.job.JobRequests;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.identity.domain.User;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;

/**
 * Common superclass for test artefact creators
 * 
 * @since 7.0
 */
public class TestArtifactCreator {
	
	protected synchronized static void createVersion(String version, String codeSystemName) {
		
		Request<ServiceProvider, Boolean> request = CodeSystemRequests.prepareNewCodeSystemVersion()
			.setCodeSystemShortName(codeSystemName)
			.setDescription("FHIR Test version")
			.setVersionId(version)
			.setEffectiveTime(new Date())
			.build();
			
		String jobId = JobRequests.prepareSchedule()
			.setDescription(String.format("Creating version '%s/%s'", 
					codeSystemName, version))
			.setUser(User.SYSTEM.getUsername())
			.setRequest(request)
			.buildAsync()
			.execute(getEventBus())
			.getSync();
		
		RemoteJobEntry job = null;
		do {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				throw new SnowowlRuntimeException(e);
			}
			
			job = JobRequests.prepareGet(jobId)
					.buildAsync()
					.execute(getEventBus())
					.getSync();
		} while (job == null || !job.isDone());
	}
	
	protected static IEventBus getEventBus() {
		return ApplicationContext.getServiceForClass(IEventBus.class);
	}

}
