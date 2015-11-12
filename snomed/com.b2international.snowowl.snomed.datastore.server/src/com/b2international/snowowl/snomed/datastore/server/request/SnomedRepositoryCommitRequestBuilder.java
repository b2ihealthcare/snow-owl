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

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.server.request.CommitInfo;
import com.b2international.snowowl.datastore.server.request.RepositoryCommitRequestBuilder;

/**
 * @since 4.5
 */
class SnomedRepositoryCommitRequestBuilder extends RepositoryCommitRequestBuilder {

	SnomedRepositoryCommitRequestBuilder(String userId, String repositoryId, String branch) {
		super(userId, repositoryId, branch);
	}
	
	@Override
	protected Request<BranchContext, CommitInfo> createRequest() {
		return new IdRequest(super.createRequest()); 
	}

}
