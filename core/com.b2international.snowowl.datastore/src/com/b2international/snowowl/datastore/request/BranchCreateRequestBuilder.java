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
package com.b2international.snowowl.datastore.request;

import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.snowowl.core.Metadata;
import com.b2international.snowowl.core.MetadataImpl;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.events.CreateBranchRequest;

/**
 * @since 4.5
 */
public final class BranchCreateRequestBuilder {

	private String parent;
	private String name;
	private Metadata metadata = new MetadataImpl();
	
	private final String repositoryId;
	
	BranchCreateRequestBuilder(String repositoryId) {
		this.repositoryId = checkNotNull(repositoryId, "repositoryId");
	}
	
	public BranchCreateRequestBuilder setMetadata(Metadata metadata) {
		this.metadata = metadata;
		return this;
	}
	
	public BranchCreateRequestBuilder setName(String name) {
		this.name = name;
		return this;
	}
	
	public BranchCreateRequestBuilder setParent(String parent) {
		this.parent = parent;
		return this;
	}
	
	public Request<ServiceProvider, Branch> build() {
		return RepositoryRequests.wrap(repositoryId, new CreateBranchRequest(parent, name, metadata));
	}
	
}
