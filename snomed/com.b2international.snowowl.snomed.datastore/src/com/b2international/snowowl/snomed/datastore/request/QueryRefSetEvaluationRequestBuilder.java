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
import com.b2international.snowowl.core.request.ResourceRequest;
import com.b2international.snowowl.core.request.ResourceRequestBuilder;
import com.b2international.snowowl.snomed.core.domain.refset.QueryRefSetMemberEvaluations;

/**
 * @since 4.5
 */
public final class QueryRefSetEvaluationRequestBuilder extends ResourceRequestBuilder<QueryRefSetEvaluationRequestBuilder, BranchContext, QueryRefSetMemberEvaluations> {

	private String referenceSetId;

	QueryRefSetEvaluationRequestBuilder() {}
	
	public QueryRefSetEvaluationRequestBuilder setReferenceSetId(String referenceSetId) {
		this.referenceSetId = referenceSetId;
		return getSelf();
	}
	
	@Override
	protected ResourceRequest<BranchContext, QueryRefSetMemberEvaluations> create() {
		return new EvaluateQueryRefSetRequest(referenceSetId);
	}

}
