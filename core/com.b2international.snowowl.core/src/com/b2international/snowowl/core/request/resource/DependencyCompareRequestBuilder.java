/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request.resource;

import javax.validation.constraints.NotNull;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ResourceURIWithQuery;
import com.b2international.snowowl.core.compare.AnalysisCompareResult;
import com.b2international.snowowl.core.context.ResourceRepositoryRequestBuilder;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 9.0
 */
public final class DependencyCompareRequestBuilder 
	extends BaseRequestBuilder<DependencyCompareRequestBuilder, RepositoryContext, AnalysisCompareResult> 
	implements ResourceRepositoryRequestBuilder<AnalysisCompareResult> {

	@NotNull
	private ResourceURIWithQuery fromUri;

	@NotNull
	private ResourceURIWithQuery toUri;

	private boolean includeChanges = false;

	public DependencyCompareRequestBuilder setFromUri(final ResourceURI fromUri) {
		return setFromUri(ResourceURIWithQuery.of(fromUri));
	}

	public DependencyCompareRequestBuilder setToUri(final ResourceURI toUri) {
		return setToUri(ResourceURIWithQuery.of(toUri));
	}

	public DependencyCompareRequestBuilder setFromUri(final ResourceURIWithQuery fromUri) {
		this.fromUri = fromUri;
		return getSelf();
	}

	public DependencyCompareRequestBuilder setToUri(final ResourceURIWithQuery toUri) {
		this.toUri = toUri;
		return getSelf();
	}

	public DependencyCompareRequestBuilder setIncludeChanges(final boolean includeChanges) {
		this.includeChanges = includeChanges;
		return getSelf();
	}

	@Override
	protected Request<RepositoryContext, AnalysisCompareResult> doBuild() {
		return new DependencyCompareRequest(fromUri, toUri, includeChanges);
	}
}
