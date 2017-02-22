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

import static com.b2international.snowowl.datastore.remotejobs.RemoteJobEntry.Expressions.ids;
import static com.b2international.snowowl.datastore.remotejobs.RemoteJobEntry.Expressions.user;
import static com.b2international.snowowl.datastore.remotejobs.RemoteJobEntry.Fields.ID;
import static com.b2international.snowowl.datastore.remotejobs.RemoteJobEntry.Fields.USER;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.b2international.commons.options.Options;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.BaseRequest;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobTracker;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobs;

/**
 * @since 5.7
 */
final class SearchJobRequest extends BaseRequest<ServiceProvider, RemoteJobs> {

	@Min(0)
	private int offset;

	@Min(0)
	private int limit;

	@NotNull
	private Options options;
	
	SearchJobRequest() {
	}
	
	@Override
	public RemoteJobs execute(ServiceProvider context) {
		final ExpressionBuilder queryBuilder = Expressions.builder();
		
		if (options.containsKey(ID)) {
			queryBuilder.must(ids(options.getCollection(ID, String.class)));
		}
		
		if (options.containsKey(USER)) {
			queryBuilder.must(user(options.getString(USER)));
		}
		
		return context.service(RemoteJobTracker.class).search(queryBuilder.build(), offset, limit);
	}

	@Override
	protected Class<RemoteJobs> getReturnType() {
		return RemoteJobs.class;
	}

	void setOffset(int offset) {
		this.offset = offset;
	}
	
	void setLimit(int limit) {
		this.limit = limit;
	}
	
	void setOptions(Options options) {
		this.options = options;
	}

}
