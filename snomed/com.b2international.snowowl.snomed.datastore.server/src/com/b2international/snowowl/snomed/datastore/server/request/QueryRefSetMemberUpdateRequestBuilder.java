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
package com.b2international.snowowl.snomed.datastore.server.request;

import java.util.Map;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.RequestBuilder;
import com.b2international.snowowl.datastore.request.CommitInfo;

/**
 * @since 4.5
 */
public final class QueryRefSetMemberUpdateRequestBuilder implements RequestBuilder<TransactionContext, Void> {

	private final String repositoryId;
	
	private String memberId;
	private String moduleId;

	QueryRefSetMemberUpdateRequestBuilder(String repositoryId) {
		this.repositoryId = repositoryId;
	}
	
	public QueryRefSetMemberUpdateRequestBuilder setMemberId(String memberId) {
		this.memberId = memberId;
		return this;
	}
	
	public QueryRefSetMemberUpdateRequestBuilder setModuleId(String moduleId) {
		this.moduleId = moduleId;
		return this;
	}
	
	@Override
	public Request<TransactionContext, Void> build() {
		return new QueryRefSetMemberUpdateRequest(memberId, moduleId);
	}

	public Request<ServiceProvider, CommitInfo> build(String userId, String branch, String commitComment) {
		return SnomedRequests.prepareCommit(userId, branch).setBody(build()).setCommitComment(commitComment).build();
	}

	public QueryRefSetMemberUpdateRequestBuilder setSource(Map<String, Object> source) {
		return setModuleId((String) source.get("moduleId")).setMemberId((String) source.get("memberId"));
	}

}
