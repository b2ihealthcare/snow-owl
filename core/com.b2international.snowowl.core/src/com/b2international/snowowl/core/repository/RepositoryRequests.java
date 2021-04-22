/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.repository;

import com.b2international.snowowl.core.branch.Branching;
import com.b2international.snowowl.core.branch.Merging;
import com.b2international.snowowl.core.commit.CommitInfoRequests;
import com.b2international.snowowl.core.request.RepositoryBulkReadRequestBuilder;
import com.b2international.snowowl.core.request.RepositoryCommitRequestBuilder;
import com.b2international.snowowl.core.system.ServerInfoGetRequestBuilder;

/**
 * The central class of Snow Owl's terminology independent Java APIs.
 * @since 4.5
 */
public final class RepositoryRequests {

	private RepositoryRequests() {}
	
	/**
	 * Returns the central class that provides access the server's branching features.
	 * @return central branching class with access to branching features
	 */
	public static Branching branching() {
		return new Branching();
	}
	
	/**
	 * Returns the central class that provides access the server's revision control
	 * merging features.
	 * @return central merging class with access to merging features
	 */
	public static Merging merging() {
		return new Merging();
	}
	
	public static CommitInfoRequests commitInfos() {
		return new CommitInfoRequests();
	}
	
	public static RepositoryBulkReadRequestBuilder prepareBulkRead() {
		return new RepositoryBulkReadRequestBuilder();
	}
	
	public static RepositorySearchRequestBuilder prepareSearch() {
		return new RepositorySearchRequestBuilder();
	}
	
	public static RepositoryGetRequestBuilder prepareGet(String repositoryId) {
		return new RepositoryGetRequestBuilder(repositoryId);
	}
	
	public static ServerInfoGetRequestBuilder prepareGetServerInfo() {
		return new ServerInfoGetRequestBuilder();
	}
	
	public static RepositoryClearRequestBuilder prepareClear() {
		return new RepositoryClearRequestBuilder();
	}
	
	public static RepositoryCommitRequestBuilder prepareCommit() {
		return new RepositoryCommitRequestBuilder();
	}
	
}
