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
import com.b2international.snowowl.datastore.server.request.CommitInfo;
import com.b2international.snowowl.datastore.server.request.RepositoryRequests;

/**
 * @since 4.5
 */
public final class SnomedRefSetMemberUpdateRequestBuilder implements RequestBuilder<TransactionContext, Void> {

	private final String repositoryId;
	
	private String memberId;
	private Map<String, Object> source;
	
	SnomedRefSetMemberUpdateRequestBuilder(String repositoryId) {
		this.repositoryId = repositoryId;
	}
	
	public SnomedRefSetMemberUpdateRequestBuilder setMemberId(String memberId) {
		this.memberId = memberId;
		return this;
	}
	
	public SnomedRefSetMemberUpdateRequestBuilder setSource(Map<String, Object> source) {
		this.source = source;
		return this;
	}
	
	@Override
	public Request<TransactionContext, Void> build() {
		return new SnomedRefSetMemberUpdateRequest(memberId, source);
	}

	public Request<ServiceProvider, CommitInfo> commit(String userId, String branchPath, String commitComment) {
		return RepositoryRequests.prepareCommit(userId, repositoryId, branchPath).setCommitComment(commitComment).setBody(this).build();
	}

}
