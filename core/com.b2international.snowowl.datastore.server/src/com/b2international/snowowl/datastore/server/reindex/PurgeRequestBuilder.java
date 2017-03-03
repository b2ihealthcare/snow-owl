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
package com.b2international.snowowl.datastore.server.reindex;

import com.b2international.index.revision.Purge;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.request.RepositoryRequestBuilder;

/**
 * @since 5.0
 */
public final class PurgeRequestBuilder extends BaseRequestBuilder<PurgeRequestBuilder, RepositoryContext, Boolean> implements RepositoryRequestBuilder<Boolean> {

	private String branchPath;
	private Purge purge = Purge.LATEST;

	PurgeRequestBuilder() {}
	
	public PurgeRequestBuilder setBranchPath(String branchPath) {
		this.branchPath = branchPath;
		return getSelf();
	}
	
	public PurgeRequestBuilder setPurge(Purge purge) {
		this.purge = purge;
		return getSelf();
	}
	
	@Override
	protected Request<RepositoryContext, Boolean> doBuild() {
		PurgeRequest req = new PurgeRequest();
		req.setBranchPath(branchPath);
		req.setPurge(purge );
		return req;
	}
	
}
