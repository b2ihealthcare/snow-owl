/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.request.repository;

import com.b2international.index.Index;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 4.7
 */
public final class OptimizeRequest implements Request<RepositoryContext, Boolean> {

	private int maxSegments;
	
	OptimizeRequest() {}
	
	void setMaxSegments(int maxSegments) {
		this.maxSegments = maxSegments;
	}
	
	@Override
	public Boolean execute(RepositoryContext context) {
		context.service(Index.class).admin().optimize(maxSegments);
		return Boolean.TRUE;
	}

	public static OptimizeRequestBuilder builder() {
		return new OptimizeRequestBuilder();
	}

}
