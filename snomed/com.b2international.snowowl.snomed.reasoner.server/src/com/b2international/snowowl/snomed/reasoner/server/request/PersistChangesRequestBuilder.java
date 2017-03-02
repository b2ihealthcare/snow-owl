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
package com.b2international.snowowl.snomed.reasoner.server.request;

import org.eclipse.core.runtime.IStatus;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.AsyncRequest;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.request.job.JobRequests;
import com.b2international.snowowl.snomed.reasoner.server.classification.ReasonerTaxonomy;

/**
 * @since 5.7
 */
public final class PersistChangesRequestBuilder extends BaseRequestBuilder<PersistChangesRequestBuilder, ServiceProvider, IStatus> {

	private ReasonerTaxonomy taxonomy;
	private String userId;

	public PersistChangesRequestBuilder setTaxonomy(ReasonerTaxonomy taxonomy) {
		this.taxonomy = taxonomy;
		return getSelf();
	}

	public PersistChangesRequestBuilder setUserId(String userId) {
		this.userId = userId;
		return getSelf();
	}

	@Override
	protected Request<ServiceProvider, IStatus> doBuild() {
		return new PersistChangesRequest(taxonomy, userId);
	}

	public AsyncRequest<String> buildAsync() {
		return JobRequests.prepareSchedule()
				.setUser(userId)
				.setRequest(build())
				.setDescription(String.format("Persisting ontology changes on %s", taxonomy.getBranchPath()))
				.buildAsync();
	}

}
