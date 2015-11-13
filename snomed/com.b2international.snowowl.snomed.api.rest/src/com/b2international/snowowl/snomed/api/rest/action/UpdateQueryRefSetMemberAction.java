/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest.action;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.server.request.CommitInfo;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedRequests;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 4.5
 */
public class UpdateQueryRefSetMemberAction implements SnomedRefSetMemberAction<CommitInfo> {

	@NotEmpty
	private String commitComment;
	
	@Override
	public String getType() {
		return "update";
	}

	@JsonProperty
	void setCommitComment(String commitComment) {
		this.commitComment = commitComment;
	}

	@Override
	public Request<ServiceProvider, CommitInfo> toRequest(String branch, String userId, String memberId) {
		return SnomedRequests.prepareUpdateQueryRefSetMember(memberId).build(userId, branch, commitComment);
	}
	
}
