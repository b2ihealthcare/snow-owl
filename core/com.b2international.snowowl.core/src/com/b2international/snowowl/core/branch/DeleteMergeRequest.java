/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collections;

import com.b2international.snowowl.core.authorization.RepositoryAccessControl;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.jobs.RemoteJobTracker;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 4.6
 */
final class DeleteMergeRequest implements Request<RepositoryContext, Boolean>, RepositoryAccessControl {

	@JsonProperty
	private final String id;

	DeleteMergeRequest(String id) {
		this.id = id;
	}

	@Override
	public Boolean execute(RepositoryContext context) {
		context.service(RemoteJobTracker.class).requestDeletes(Collections.singleton(id));
		return Boolean.TRUE;
	}
	
	@Override
	public String getOperation() {
		return Permission.OPERATION_EDIT;
	}
	
}
