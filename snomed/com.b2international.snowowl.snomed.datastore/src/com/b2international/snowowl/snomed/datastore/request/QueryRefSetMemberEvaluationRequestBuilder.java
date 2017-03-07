/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.datastore.request.ResourceRequest;
import com.b2international.snowowl.datastore.request.ResourceRequestBuilder;
import com.b2international.snowowl.snomed.core.domain.refset.QueryRefSetMemberEvaluation;

/**
 * @since 4.5
 */
public final class QueryRefSetMemberEvaluationRequestBuilder extends ResourceRequestBuilder<QueryRefSetMemberEvaluationRequestBuilder, BranchContext, QueryRefSetMemberEvaluation> {

	private String memberId;

	QueryRefSetMemberEvaluationRequestBuilder() {}

	public QueryRefSetMemberEvaluationRequestBuilder setMemberId(String memberId) {
		this.memberId = memberId;
		return this;
	}
	
	@Override
	public ResourceRequest<BranchContext, QueryRefSetMemberEvaluation> create() {
		return new EvaluateQueryRefSetMemberRequest(memberId);
	}

}
