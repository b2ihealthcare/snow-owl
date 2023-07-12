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

import com.b2international.snowowl.core.request.BaseResourceUpdateRequestBuilder;

/**
 * @since 8.12
 */
public abstract class BaseTerminologyResourceUpdateRequestBuilder<RB extends BaseTerminologyResourceUpdateRequestBuilder<RB, R>, R extends BaseTerminologyResourceUpdateRequest> 
		extends BaseResourceUpdateRequestBuilder<RB, R> {

	private String oid;
	private String branchPath;
	
	protected BaseTerminologyResourceUpdateRequestBuilder(String resourceId) {
		super(resourceId);
	}
	
	public RB setOid(String oid) {
		this.oid = oid;
		return getSelf();
	}

	public RB setBranchPath(String branchPath) {
		this.branchPath = branchPath;
		return getSelf();
	}
	
	@Override
	protected void init(R req) {
		super.init(req);
		req.setOid(oid);
		req.setBranchPath(branchPath);
	}

}
