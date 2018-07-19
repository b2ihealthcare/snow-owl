/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.version;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.request.CommitResult;
import com.b2international.snowowl.datastore.request.TransactionalRequest;

/**
 * @since 7.0
 */
@FunctionalInterface
public interface VersioningRequestBuilder {

	VersioningRequestBuilder DEFAULT = config -> {
		return new TransactionalRequest(
			config.getUser(), 
			"Create version " + config.getVersionId(), 
			new VersioningRequest(config), 
			0L, 
			DatastoreLockContextDescriptions.CREATE_VERSION
		);
	};
	
	Request<BranchContext, CommitResult> build(VersioningConfiguration config);
	
}
