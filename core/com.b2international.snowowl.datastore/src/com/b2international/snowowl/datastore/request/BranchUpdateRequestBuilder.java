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
package com.b2international.snowowl.datastore.request;

import com.b2international.commons.options.Metadata;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 5.0
 */
public final class BranchUpdateRequestBuilder extends BaseRequestBuilder<BranchUpdateRequestBuilder, RepositoryContext, Boolean> implements RepositoryRequestBuilder<Boolean> {

	private final String branchPath;
	private Metadata metadata;

	BranchUpdateRequestBuilder(String branchPath) {
		this.branchPath = branchPath;
	}
	
	/**
	 * Update (override) the current {@link Metadata} with the specified {@link Metadata}.  
	 * @param metadata
	 * @return
	 */
	public BranchUpdateRequestBuilder setMetadata(Metadata metadata) {
		this.metadata = metadata;
		return getSelf();
	}
	
	@Override
	protected Request<RepositoryContext, Boolean> doBuild() {
		final BranchUpdateRequest req = new BranchUpdateRequest(branchPath);
		req.setMetadata(metadata);
		return req;
	}

}
