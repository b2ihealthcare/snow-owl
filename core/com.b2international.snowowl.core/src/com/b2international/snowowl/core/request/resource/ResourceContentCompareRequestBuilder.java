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

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ResourceURIWithQuery;
import com.b2international.snowowl.core.compare.AnalysisCompareResult;
import com.b2international.snowowl.core.context.ResourceRepositoryRequestBuilder;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.ResourceRequest;
import com.b2international.snowowl.core.request.ResourceRequestBuilder;

/**
 * @since 9.0
 */
public final class ResourceContentCompareRequestBuilder 
	extends ResourceRequestBuilder<ResourceContentCompareRequestBuilder, RepositoryContext, AnalysisCompareResult> 
	implements ResourceRepositoryRequestBuilder<AnalysisCompareResult> {

	private ResourceURIWithQuery fromUri;
	private ResourceURIWithQuery toUri;
	private String termType = "FSN";
	private boolean includeChanges = true;

	public ResourceContentCompareRequestBuilder setFromUri(final ResourceURI fromUri) {
		return setFromUri(ResourceURIWithQuery.of(fromUri));
	}

	public ResourceContentCompareRequestBuilder setToUri(final ResourceURI toUri) {
		return setToUri(ResourceURIWithQuery.of(toUri));
	}

	public ResourceContentCompareRequestBuilder setFromUri(final ResourceURIWithQuery fromUri) {
		this.fromUri = fromUri;
		return getSelf();
	}

	public ResourceContentCompareRequestBuilder setToUri(final ResourceURIWithQuery toUri) {
		this.toUri = toUri;
		return getSelf();
	}

	public ResourceContentCompareRequestBuilder setIncludeChanges(final boolean includeChanges) {
		this.includeChanges = includeChanges;
		return getSelf();
	}

	public ResourceContentCompareRequestBuilder setTermType(final String termType) {
		this.termType = StringUtils.isEmpty(termType) ? "FSN" : termType;
		return getSelf();
	}

	@Override
	protected ResourceRequest<RepositoryContext, AnalysisCompareResult> create() {
		return new ResourceContentCompareRequest(fromUri, toUri, includeChanges, termType);
	}
}
