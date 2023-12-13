/*
 * Copyright 2021-2022 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.context;

import java.util.List;

import com.b2international.index.Searcher;
import com.b2international.index.revision.BaseRevisionBranching;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.RevisionIndexRead;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.RepositoryInfo;
import com.b2international.snowowl.core.RepositoryInfo.Health;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.ContextConfigurer;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.internal.ResourceRepository;
import com.b2international.snowowl.core.repository.DefaultRepositoryContext;
import com.b2international.snowowl.core.request.RepositoryAwareRequest;
import com.b2international.snowowl.core.request.RevisionIndexReadRequestTimestampProvider;

/**
 * @since 8.0
 * @param <R>
 */
public final class ResourceRepositoryRequest<R> extends DelegatingRequest<ServiceProvider, RepositoryContext, R> implements RepositoryAwareRequest {

	public static final String RESOURCE_REPOSITORY_ID = "resources";
	
	private static final long serialVersionUID = 1L;

	private final Long timestamp;
	
	public ResourceRepositoryRequest(Request<RepositoryContext, R> next) {
		this(null, next);
	}
	
	public ResourceRepositoryRequest(Long timestamp, Request<RepositoryContext, R> next) {
		super(next);
		this.timestamp = timestamp;
	}
	
	@Override
	public String getRepositoryId() {
		return RESOURCE_REPOSITORY_ID;
	}
	
	@Override
	public String getContextId() {
		return RESOURCE_REPOSITORY_ID;
	}

	@Override
	public R execute(ServiceProvider context) {
		ResourceRepository resourceRepository = context.service(ResourceRepository.class);
		RevisionIndexRead<R> read = searcher -> next(prepareRepositoryContext(context, resourceRepository, searcher));

		// read from the latest snapshot of the repository to get the actual timestamp we have to read to satisfy the request
		Long readTimestamp = resourceRepository.read((searcher) -> getReadTimestamp(prepareRepositoryContext(context, resourceRepository, searcher)));
		
		return resourceRepository.read(readTimestamp, read);
	}

	private DefaultRepositoryContext prepareRepositoryContext(ServiceProvider context, ResourceRepository resourceRepository, RevisionSearcher searcher) {
		// TODO check health
		DefaultRepositoryContext repository = new DefaultRepositoryContext(context, RepositoryInfo.of(RESOURCE_REPOSITORY_ID, Health.GREEN, null, List.of()));
		repository.bind(RevisionIndex.class, resourceRepository);
		repository.bind(Searcher.class, searcher.searcher());
		repository.bind(RevisionSearcher.class, searcher);
		repository.bind(ContextConfigurer.class, ContextConfigurer.NOOP);
		repository.bind(BaseRevisionBranching.class, resourceRepository.branching());
		return repository;
	}

	private Long getReadTimestamp(ServiceProvider context) {
		if (timestamp != null) {
			return timestamp;
		} else {
			RevisionIndexReadRequestTimestampProvider readTimestampProvider = getNestedRequest(RevisionIndexReadRequestTimestampProvider.class);
			if (readTimestampProvider != null) {
				return readTimestampProvider.getReadTimestamp(context);
			}
		}
		return null;
	}
}
