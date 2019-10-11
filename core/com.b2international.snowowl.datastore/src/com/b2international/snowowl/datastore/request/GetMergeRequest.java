/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Map;

import com.b2international.commons.exceptions.ApiError;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.snowowl.core.authorization.AccessControl;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.merge.Merge;
import com.b2international.snowowl.core.merge.MergeImpl;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobEntry;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobTracker;
import com.b2international.snowowl.identity.domain.Permission;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;

/**
 * @since 7.1
 */
public class GetMergeRequest implements Request<RepositoryContext, Merge>, AccessControl {
	
	private static final long serialVersionUID = 1L;
	
	private final String id;
	
	public GetMergeRequest(final String id) {
		this.id = id;
	}

	@Override
	public Merge execute(RepositoryContext context) {
		final RemoteJobEntry job = Iterables.getOnlyElement(context.service(RemoteJobTracker.class).search(DocumentMapping.matchId(id), 1), null);
		if (job == null) {
			throw new NotFoundException("Merge ", id);
		}
		
		final ObjectMapper mapper = context.service(ObjectMapper.class);
		
		if (job.isSuccessful()) {
			return job.getResultAs(mapper, Merge.class);
		}
		
		final Map<String, Object> params = job.getParameters(mapper);
		final String source = (String) params.get("source");
		final String target = (String) params.get("target");
		
		if (job.getResult() == null) {
			return MergeImpl.builder(source, target).build();
		}
		
		// failed job result is ApiError
		return MergeImpl.builder(source, target).build().failed(job.getResultAs(mapper, ApiError.class));
	}
	
	@Override
	public Permission getPermission() {
		return new Permission(Permission.BROWSE, Permission.ALL);
	}

}
