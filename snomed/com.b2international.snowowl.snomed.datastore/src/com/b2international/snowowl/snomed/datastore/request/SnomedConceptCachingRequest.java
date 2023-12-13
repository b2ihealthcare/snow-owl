/*
 * Copyright 2021 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.snomed.datastore.request;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 8.1
 * @param <R>
 */
public final class SnomedConceptCachingRequest<R> extends DelegatingRequest<BranchContext, BranchContext, R> {

	private static final long serialVersionUID = 1L;
	
	public SnomedConceptCachingRequest(Request<BranchContext, R> next) {
		super(next);
	}

	@Override
	public R execute(BranchContext context) {
		SnomedConceptRequestCache cache = new SnomedConceptRequestCache();
		final BranchContext cachingContext = context.inject().bind(SnomedConceptRequestCache.class, cache).build();
		R response = next(cachingContext);
		// compute the cache if the next callback returns successfully
		cache.compute(cachingContext);
		return response; 
	}

}
