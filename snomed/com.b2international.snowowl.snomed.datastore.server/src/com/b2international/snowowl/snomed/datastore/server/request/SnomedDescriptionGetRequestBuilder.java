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
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;

/**
 * @since 4.5
 */
public final class SnomedDescriptionGetRequestBuilder implements RequestBuilder<BranchContext, ISnomedDescription> {

	private final String repositoryId;
	
	private String id;
	
	SnomedDescriptionGetRequestBuilder(String repositoryId) {
		this.repositoryId = repositoryId;
	}
	
	public SnomedDescriptionGetRequestBuilder setId(String id) {
		this.id = id;
		return this;
	}
	
	@Override
	public Request<BranchContext, ISnomedDescription> build() {
		return new SnomedDescriptionReadRequest(id);
	}
	
	public Request<ServiceProvider, ISnomedDescription> build(String branch) {
		return RepositoryRequests.wrap(repositoryId, branch, build());
	}
	
}
