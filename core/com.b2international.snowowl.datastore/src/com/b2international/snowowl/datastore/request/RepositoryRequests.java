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

import com.b2international.snowowl.datastore.request.export.ContentExportRequestBuilder;
import com.b2international.snowowl.datastore.request.repository.RepositoryGetRequestBuilder;
import com.b2international.snowowl.datastore.request.repository.RepositorySearchRequestBuilder;
import com.b2international.snowowl.datastore.request.system.ServerInfoGetRequestBuilder;

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
	
	/**
	 * Returns the central class that provides access the server's review features
	 * @return central review class with access to review features
	 */
	public static Reviews reviews() {
		return new Reviews();
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

	public static ContentExportRequestBuilder prepareContentExport() {
		return new ContentExportRequestBuilder();
	}
}
