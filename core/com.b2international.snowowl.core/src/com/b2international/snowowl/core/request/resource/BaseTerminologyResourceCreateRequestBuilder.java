/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request.resource;

import java.util.List;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.b2international.snowowl.core.Dependency;
import com.b2international.snowowl.core.request.BaseResourceCreateRequestBuilder;
import com.google.common.collect.ImmutableList;

/**
 * @since 8.12.0
 */
public abstract class BaseTerminologyResourceCreateRequestBuilder<RB extends BaseTerminologyResourceCreateRequestBuilder<RB, R>, R extends BaseTerminologyResourceCreateRequest> 
		extends BaseResourceCreateRequestBuilder<RB, R> {

	private String oid;
	private String branchPath;
	private List<Dependency> dependencies;
	
	public final RB setDependencies(Dependency... dependencies) {
		this.dependencies = ImmutableList.copyOf(dependencies);
		return getSelf();
	}
	
	public final RB setDependencies(List<Dependency> dependencies) {
		this.dependencies = dependencies;
		return getSelf();
	}
	
	public final RB setOid(String oid) {
		this.oid = oid;
		return getSelf();
	}
	
	public final RB setBranchPath(String branchPath) {
		this.branchPath = branchPath;
		return getSelf();
	}
	
	@OverridingMethodsMustInvokeSuper
	@Override
	protected void init(R req) {
		super.init(req);
		req.setDependencies(dependencies);
		req.setOid(oid);
		req.setBranchPath(branchPath);
	}
	
}
