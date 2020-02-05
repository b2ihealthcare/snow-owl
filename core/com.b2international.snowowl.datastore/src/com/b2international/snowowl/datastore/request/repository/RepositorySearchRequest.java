/*
 * Copyright 2017-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

import com.b2international.snowowl.core.Repositories;
import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.RepositoryInfo;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.google.common.collect.ImmutableList;

/**
 * @since 5.8
 */
class RepositorySearchRequest extends SearchResourceRequest<ServiceProvider, Repositories> {

	private static final long serialVersionUID = 1L;

	@Override
	protected Repositories createEmptyResult(int limit) {
		return new Repositories();
	}

	@Override
	protected Repositories doExecute(ServiceProvider context) throws IOException {
		final Collection<String> ids = componentIds();
		final Collection<RepositoryInfo> repositories = context.service(RepositoryManager.class)
				.repositories()
				.stream()
				.filter(repository -> ids == null ? true : ids.contains(repository.id()))
				.map(Repository::status)
				.collect(Collectors.toList());
		return new Repositories(ImmutableList.copyOf(repositories));
	}
	
}
