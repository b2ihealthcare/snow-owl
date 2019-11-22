/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.validation.issue;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.internal.validation.ValidationRepository;
import com.b2international.snowowl.core.validation.ValidationDeleteNotification;
import com.b2international.snowowl.core.validation.ValidationRequests;
import com.b2international.snowowl.eventbus.IEventBus;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 6.20.0
 */
final class ValidationIssueDeleteRequest implements Request<ServiceProvider, Boolean> {

	private static final long serialVersionUID = 1L;
	
	@JsonProperty
	@NotEmpty
	private final String branch;

	@JsonProperty
	@NotEmpty
	private String toolingId;
	
	ValidationIssueDeleteRequest(String branch, String toolingId) {
		this.branch = branch;
		this.toolingId = toolingId;
	}
	
	@Override
	public Boolean execute(ServiceProvider context) {
		return context.service(ValidationRepository.class).write(writer -> {
			
			ValidationIssues issues = ValidationRequests.issues().prepareSearch()
				.all()
				.filterByBranchPath(branch)
				.filterByTooling(toolingId)
				.build()
				.execute(context);
			
			for (ValidationIssue issue : issues) {
				writer.remove(ValidationIssue.class, issue.getId());
			}
			
			writer.commit();
			
			new ValidationDeleteNotification(branch, toolingId).publish(context.service(IEventBus.class));
			
			return Boolean.TRUE;
		});
	}

}
