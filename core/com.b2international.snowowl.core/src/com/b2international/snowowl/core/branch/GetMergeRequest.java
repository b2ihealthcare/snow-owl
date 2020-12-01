/*
 * Copyright 2019-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.branch;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.authorization.RepositoryAccessControl;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.jobs.RemoteJobEntry;
import com.b2international.snowowl.core.jobs.RemoteJobTracker;
import com.b2international.snowowl.core.merge.Merge;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;

/**
 * @since 7.1
 */
public class GetMergeRequest implements Request<RepositoryContext, Merge>, RepositoryAccessControl {
	
	private static final long serialVersionUID = 1L;
	
	private final String id;
	
	public GetMergeRequest(final String id) {
		this.id = id;
	}

	@Override
	public Merge execute(RepositoryContext context) {
		final RemoteJobEntry job = Iterables.getOnlyElement(context.service(RemoteJobTracker.class).search(RemoteJobEntry.Expressions.id(id), 1), null);
		if (job == null) {
			throw new NotFoundException("Merge", id);
		}
		return SearchMergeRequest.createMergefromJobEntry(job, context.service(ObjectMapper.class));
	}

	@Override
	public String getOperation() {
		return Permission.OPERATION_BROWSE;
	}

}
