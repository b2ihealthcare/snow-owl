package com.b2international.snowowl.core.branch;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.AsyncRequest;
import com.b2international.snowowl.core.identity.User;
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
	
	public void removeStaleIssues(ServiceProvider context, String resourceURI) {
		removeStaleIssues(context, List.of(resourceURI), Collections.emptyList());
	}
	
	public void removeStaleIssues(ServiceProvider context, List<String> resourceURIs) {
		removeStaleIssues(context, resourceURIs, Collections.emptyList());
	}
	
	public void removeStaleIssues(ServiceProvider context, String resourceURI, List<String> resultIds) {
		removeStaleIssues(context, List.of(resourceURI), resultIds);
	}
	
	public void removeStaleIssues(ServiceProvider context, List<String> resourceURIs, List<String> resultIds) {
		AsyncRequest<Boolean> request;
		
		if (!resultIds.isEmpty()) {
			request = ValidationRequests.issues()
					.prepareDelete()
					.setResultIds(resultIds)
					.buildAsync();
		} else {
			request = ValidationRequests.issues()
				.prepareDelete()
				.setCodeSystemURIs(resourceURIs)
				.buildAsync();
		}
		
		final String description = String.format("Remove validation issues on stale/removed branch(es) %s", Joiner.on(", ").join(resourceURIs));
		
		JobRequests.prepareSchedule()
			.setRequest(request)
			.setDescription(description)
			.setUser(context.service(User.class).getUserId())
			.setAutoClean(true)
			.setSchedulingRule(VALIDATION_ISSUE_CLEANUP_RULE)
			.build()
			.execute(context);
	}
	
}
