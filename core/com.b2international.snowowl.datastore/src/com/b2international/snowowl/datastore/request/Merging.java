/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.UUID;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.merge.Merge;

/**
 * @since 4.6
 */
public class Merging {

	private String repositoryId;

	Merging(String repositoryId) {
		this.repositoryId = repositoryId;
	}
	
	public CreateMergeRequestBuilder prepareCreate() {
		return new CreateMergeRequestBuilder(repositoryId);
	}

	public Request<ServiceProvider, Merge> prepareGet(UUID id) {
		return RepositoryRequests.wrap(repositoryId, new GetMergeRequest(id));
	}
	
	public SearchMergeRequestBuilder prepareSearch() {
		return new SearchMergeRequestBuilder(repositoryId);
	}

	public Request<ServiceProvider, Void> prepareDelete(UUID id) {
		return RepositoryRequests.wrap(repositoryId, new DeleteMergeRequest(id));
	}
}
