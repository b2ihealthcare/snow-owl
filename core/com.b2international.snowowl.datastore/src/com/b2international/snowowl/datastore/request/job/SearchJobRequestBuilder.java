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

import java.util.Collection;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.options.OptionsBuilder;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobEntry;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobs;
import com.b2international.snowowl.datastore.request.BaseSystemRequestBuilder;

/**
 * @since 5.7
 */
public final class SearchJobRequestBuilder extends BaseSystemRequestBuilder<SearchJobRequestBuilder, RemoteJobs> {

	private int offset = 0;
	private int limit = 50;
	private final OptionsBuilder optionsBuilder = OptionsBuilder.newBuilder();
	
	SearchJobRequestBuilder() {
	}
	
	public SearchJobRequestBuilder setLimit(int limit) {
		this.limit = limit;
		return getSelf();
	}
	
	public SearchJobRequestBuilder setOffset(int offset) {
		this.offset = offset;
		return getSelf();
	}
	
	public SearchJobRequestBuilder filterByIds(Collection<String> ids) {
		return addOption(RemoteJobEntry.Fields.ID, ids);
	}
	
	public SearchJobRequestBuilder filterByUser(String user) {
		return addOption(RemoteJobEntry.Fields.USER, user);
	}
	
	private SearchJobRequestBuilder addOption(String key, Object value) {
		if (!CompareUtils.isEmpty(value)) {
			this.optionsBuilder.put(key, value);
		}
		return getSelf();
	}

	@Override
	protected Request<ServiceProvider, RemoteJobs> doBuild() {
		final SearchJobRequest req = new SearchJobRequest();
		req.setOffset(offset);
		req.setLimit(limit);
		req.setOptions(optionsBuilder.build());
		return req;
	}

}
