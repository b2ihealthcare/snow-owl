/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.repository;

import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.RepositoryRequestBuilder;

/**
 * @since 4.7
 */
public final class OptimizeRequestBuilder extends BaseRequestBuilder<OptimizeRequestBuilder, RepositoryContext, Boolean> implements RepositoryRequestBuilder<Boolean> {

	private int maxSegments = 1;
	
	OptimizeRequestBuilder() {}
	
	public OptimizeRequestBuilder setMaxSegments(int maxSegments) {
		this.maxSegments = maxSegments;
		return getSelf();
	}
	
	@Override
	protected Request<RepositoryContext, Boolean> doBuild() {
		OptimizeRequest req = new OptimizeRequest();
		req.setMaxSegments(maxSegments);
		return req;
	}

}
