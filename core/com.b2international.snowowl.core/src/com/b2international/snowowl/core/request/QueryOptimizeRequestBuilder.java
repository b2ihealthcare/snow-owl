/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.List;

import com.b2international.snowowl.core.context.TerminologyResourceContentRequestBuilder;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.QueryExpression;
import com.b2international.snowowl.core.domain.QueryExpressionDiffs;

/**
 * @since 7.7
 */
public class QueryOptimizeRequestBuilder 
	extends ResourceRequestBuilder<QueryOptimizeRequestBuilder, BranchContext, QueryExpressionDiffs> 
	implements TerminologyResourceContentRequestBuilder<QueryExpressionDiffs> {

	private final List<QueryExpression> inclusions = newArrayList();
	private final List<QueryExpression> exclusions = newArrayList();
	
	public QueryOptimizeRequestBuilder filterByInclusions(Collection<QueryExpression> inclusions) {
		if (inclusions != null) {
			this.inclusions.addAll(inclusions);
		}
		return getSelf();
	}

	public QueryOptimizeRequestBuilder filterByExclusions(Collection<QueryExpression> exclusions) {
		if (exclusions != null) {
			this.exclusions.addAll(exclusions);
		}
		return getSelf();
	}
	
	@Override
	protected QueryOptimizeRequest create() {
		return new QueryOptimizeRequest();
	}
	
	@Override
	protected void init(ResourceRequest<BranchContext, QueryExpressionDiffs> request) {
		super.init(request);
		
		final QueryOptimizeRequest req = (QueryOptimizeRequest) request;
		req.setInclusions(inclusions);
		req.setExclusions(exclusions);
	}
}
