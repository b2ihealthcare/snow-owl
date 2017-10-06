/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.request.job;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.SearchResourceRequestBuilder;
import com.b2international.snowowl.core.request.SystemRequestBuilder;
import com.b2international.snowowl.datastore.remotejobs.RemoteJob;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobEntry;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobs;

/**
 * A request builder that builds a request to search/list {@link RemoteJob job entries}.
 * 
 * @since 5.7
 */
public final class SearchJobRequestBuilder extends SearchResourceRequestBuilder<SearchJobRequestBuilder, ServiceProvider, RemoteJobs> implements SystemRequestBuilder<RemoteJobs> {

	SearchJobRequestBuilder() {
	}

	/**
	 * Filter {@link RemoteJob job entries} by the assigned user.
	 * @param user
	 * @return
	 */
	public SearchJobRequestBuilder filterByUser(String user) {
		return addOption(RemoteJobEntry.Fields.USER, user);
	}
	
	@Override
	protected SearchResourceRequest<ServiceProvider, RemoteJobs> createSearch() {
		return new SearchJobRequest();
	}
	
}
