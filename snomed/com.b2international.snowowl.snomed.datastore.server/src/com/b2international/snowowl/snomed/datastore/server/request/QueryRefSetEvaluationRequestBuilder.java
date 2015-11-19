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

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.RequestBuilder;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.snomed.core.domain.refset.QueryRefSetMemberEvaluations;

/**
 * @since 4.5
 */
public final class QueryRefSetEvaluationRequestBuilder implements RequestBuilder<BranchContext, QueryRefSetMemberEvaluations> {

	private final String repositoryId;
	
	private String referenceSetId;

	QueryRefSetEvaluationRequestBuilder(String repositoryId) {
		this.repositoryId = repositoryId;
	}
	
	public Request<ServiceProvider, QueryRefSetMemberEvaluations> build(String branch) {
		return RepositoryRequests.wrap(repositoryId, branch, build());
	}
	
	@Override
	public Request<BranchContext, QueryRefSetMemberEvaluations> build() {
		return new EvaluateQueryRefSetRequest(referenceSetId);
	}

	public QueryRefSetEvaluationRequestBuilder setReferenceSetId(String referenceSetId) {
		this.referenceSetId = referenceSetId;
		return this;
	}

}
