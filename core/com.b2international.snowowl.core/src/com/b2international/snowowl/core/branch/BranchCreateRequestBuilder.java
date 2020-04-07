/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.branch;

import com.b2international.commons.options.Metadata;
import com.b2international.commons.options.MetadataImpl;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.RepositoryRequestBuilder;

/**
 * @since 4.5
 */
public final class BranchCreateRequestBuilder extends BaseRequestBuilder<BranchCreateRequestBuilder, RepositoryContext, String> implements RepositoryRequestBuilder<String> {

	private String parent;
	private String name;
	private Metadata metadata = new MetadataImpl();
	
	BranchCreateRequestBuilder() {}
	
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
	
	@Override
	protected Request<RepositoryContext, String> doBuild() {
		return new BranchCreateRequest(parent, name, metadata);
	}
	
}
