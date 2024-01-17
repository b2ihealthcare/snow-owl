/*
 * Copyright 2011-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.domain;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.metric.Metrics;
import com.b2international.index.revision.RevisionSearcher;

/**
 * @since 4.5
 */
public final class RepositoryBranchContext extends DelegatingRepositoryContext implements BranchContext {

	private final String path;

	public RepositoryBranchContext(RepositoryContext context, String path, RevisionSearcher searcher) {
		super(context);
		
		if (searcher.ref().isDeletedBranch()) {
			throw new BadRequestException("Branch '%s' has been deleted and cannot accept search requests nor further modifications.", searcher.ref().path());
		}
		
		this.path = path;
		// configure query performance profiling
		searcher.setMetrics(optionalService(Metrics.class).orElse(Metrics.NOOP));
		bind(RevisionSearcher.class, searcher);
		context.optionalService(ContextConfigurer.class).ifPresent(configurer -> configurer.configure(RepositoryBranchContext.this));
	}
	
	@Override
	public String path() {
		// XXX make sure we return the original path expression requested by the client 
		return path;
	}
	
}
