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

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.Branches;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.events.DeleteBranchRequest;
import com.b2international.snowowl.datastore.events.ReadBranchChildrenRequest;
import com.b2international.snowowl.datastore.events.ReadBranchRequest;
import com.b2international.snowowl.datastore.events.ReopenBranchRequest;

/**
 * @since 4.5
 */
public class Branching {

	private String repositoryId;

	Branching(String repositoryId) {
		this.repositoryId = repositoryId;
	}
	
	public BranchCreateRequestBuilder prepareCreate() {
		return new BranchCreateRequestBuilder(repositoryId);
	}
	
	public BranchSearchRequestBuilder prepareSearch() {
		return new BranchSearchRequestBuilder(repositoryId);
	}
	
	public Request<ServiceProvider, Branch> prepareGet(String path) {
		return RepositoryRequests.wrap(repositoryId, new ReadBranchRequest(path));
	}
	
	public Request<ServiceProvider, Branches> prepareGetChildren(String path) {
		return RepositoryRequests.wrap(repositoryId, new ReadBranchChildrenRequest(path));
	}
	
	public BranchMergeRequestBuilder prepareMerge() {
		return new BranchMergeRequestBuilder(repositoryId);
	}

	public Request<ServiceProvider, Branch> prepareDelete(String branchPath) {
		return RepositoryRequests.wrap(repositoryId, new DeleteBranchRequest(branchPath));
	}

	public Request<ServiceProvider, Branch> prepareReopen(String branchPath) {
		return RepositoryRequests.wrap(repositoryId, new ReopenBranchRequest(branchPath));
	}
	
}
