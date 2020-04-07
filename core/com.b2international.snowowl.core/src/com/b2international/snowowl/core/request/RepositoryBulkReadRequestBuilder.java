/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.bulk.BulkRequestBuilder;
import com.b2international.snowowl.core.events.bulk.BulkResponse;

/**
 * @since 4.5
 */
public final class RepositoryBulkReadRequestBuilder extends BaseRequestBuilder<RepositoryBulkReadRequestBuilder, BranchContext, BulkResponse> implements RevisionIndexRequestBuilder<BulkResponse> {
	
	private Request<BranchContext, BulkResponse> body;

	public RepositoryBulkReadRequestBuilder() {}
	
	public final RepositoryBulkReadRequestBuilder setBody(BulkRequestBuilder<BranchContext> req) {
		body = req.build();
		return getSelf();
	}

	@Override
	protected Request<BranchContext, BulkResponse> doBuild() {
		return body;
	}
	
}
