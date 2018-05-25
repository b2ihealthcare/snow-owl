/*
 * Copyright 2017-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.request.compare;

import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.request.RepositoryIndexRequestBuilder;

/**
 * @since 5.9
 */
public final class BranchCompareRequestBuilder extends BaseRequestBuilder<BranchCompareRequestBuilder, RepositoryContext, CompareResult>
		implements RepositoryIndexRequestBuilder<CompareResult> {

	private String base;
	private String compare;
	private int limit = Integer.MAX_VALUE;
	
	public BranchCompareRequestBuilder setBase(String baseBranch) {
		this.base = baseBranch;
		return getSelf();
	}
	
	public BranchCompareRequestBuilder setCompare(String compareBranch) {
		this.compare = compareBranch;
		return getSelf();
	}
	
	public BranchCompareRequestBuilder setLimit(int limit) {
		this.limit = limit;
		return getSelf();
	}
	
	@Override
	protected Request<RepositoryContext, CompareResult> doBuild() {
		final BranchCompareRequest req = new BranchCompareRequest();
		req.setBaseBranch(base);
		req.setCompareBranch(compare);
		req.setLimit(limit);
		return req;
	}
}
