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

import static com.b2international.snowowl.datastore.remotejobs.RemoteJobEntry.Expressions.user;
import static com.b2international.snowowl.datastore.remotejobs.RemoteJobEntry.Fields.USER;

import java.io.IOException;
import java.util.Collections;

import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobEntry;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobTracker;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobs;
import com.b2international.snowowl.datastore.request.SearchResourceRequest;

/**
 * @since 5.7
 */
final class SearchJobRequest extends SearchResourceRequest<ServiceProvider, RemoteJobs> {

	SearchJobRequest() {
	}
	
	@Override
	protected RemoteJobs doExecute(ServiceProvider context) throws IOException {
		final ExpressionBuilder queryBuilder = Expressions.builder();
		
		addIdFilter(queryBuilder, RemoteJobEntry.Expressions::ids);
		
		if (options().containsKey(USER)) {
			queryBuilder.must(user(options().getString(USER)));
		}
		
		return context.service(RemoteJobTracker.class).search(queryBuilder.build(), offset(), limit());
	}

	@Override
	protected RemoteJobs createEmptyResult(int offset, int limit) {
		return new RemoteJobs(Collections.emptyList(), offset, limit, 0);
	}
	
}
