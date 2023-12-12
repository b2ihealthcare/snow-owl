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
