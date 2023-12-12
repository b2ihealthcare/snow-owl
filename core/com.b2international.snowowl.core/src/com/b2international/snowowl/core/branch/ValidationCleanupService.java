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

package com.b2international.snowowl.core.branch;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.AsyncRequest;
import com.b2international.snowowl.core.jobs.JobRequests;
import com.b2international.snowowl.core.jobs.SerializableSchedulingRule;
import com.b2international.snowowl.core.validation.ValidationRequests;
import com.google.common.base.Joiner;

/**
 * @since 9.0
 */
public class ValidationCleanupService {
	
	public static final SerializableSchedulingRule VALIDATION_ISSUE_CLEANUP_RULE = new SerializableSchedulingRule() {
		
		private static final long serialVersionUID = 1L;

		@Override
		public boolean isConflicting(ISchedulingRule rule) {
			return rule == this;
		}
		
		@Override
		public boolean contains(ISchedulingRule rule) {
			return rule == this;
		}
	};

	public ValidationCleanupService() {
	}
	
	public void scheduleStaleIssueRemoval(ServiceProvider context, String resourceURI) {
		scheduleStaleIssueRemoval(context, List.of(resourceURI), Collections.emptyList());
	}
	
	public void scheduleStaleIssueRemoval(ServiceProvider context, List<String> resourceURIs) {
		scheduleStaleIssueRemoval(context, resourceURIs, Collections.emptyList());
	}
	
	public void scheduleStaleIssueRemoval(ServiceProvider context, String resourceURI, List<String> resultIds) {
		scheduleStaleIssueRemoval(context, List.of(resourceURI), resultIds);
	}
	
	public void scheduleStaleIssueRemoval(ServiceProvider context, List<String> resourceURIs, List<String> resultIds) {
		AsyncRequest<Boolean> request;
		
		if (!CompareUtils.isEmpty(resultIds)) {
			request = ValidationRequests.issues()
					.prepareDelete()
					.setResultIds(resultIds)
					.buildAsync();
		} else {
			if (CompareUtils.isEmpty(resourceURIs)) {
				return;
			}
			
			request = ValidationRequests.issues()
				.prepareDelete()
				.setCodeSystemURIs(resourceURIs)
				.buildAsync();
		}
		
		final String description = String.format("Remove validation issues on stale/removed branch(es) of %s", Joiner.on(", ").join(resourceURIs));
		
		JobRequests.prepareSchedule()
			.setRequest(request)
			.setDescription(description)
			.setAutoClean(true)
			.setSchedulingRule(VALIDATION_ISSUE_CLEANUP_RULE)
			.build()
			.execute(context);
	}
	
}
