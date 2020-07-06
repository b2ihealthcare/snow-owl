/*
 * Copyright 2017-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request;

import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.AsyncRequest;
import com.b2international.snowowl.core.events.Request;

/**
 * Provides a default method for wrapping {@link BranchContext}-based requests
 * into {@link AsyncRequest}s. The provided {@code BranchContext} allows
 * searching for document revisions via a {@link RevisionSearcher} service
 * reference.
 * 
 * @since 5.7
 * @param <R> - the return type
 */
public interface RevisionIndexRequestBuilder<R> extends BranchRequestBuilder<R> {

	@Override
	default Request<BranchContext, R> wrap(Request<BranchContext, R> req) {
		return new RevisionIndexReadRequest<>(req, snapshot());
	}
	
	default boolean snapshot() {
		return true;
	}
	
}
