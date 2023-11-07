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

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.Resource;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ResourceURIWithQuery;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.compare.AnalysisCompareResult;
import com.b2international.snowowl.core.compare.DependencyComparer;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.RepositoryRequest;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 9.0.0
 */
final class DependencyCompareRequest implements Request<RepositoryContext, AnalysisCompareResult> {

	private static final long serialVersionUID = 1L;

	@JsonProperty
	@NotNull
	private final ResourceURIWithQuery fromUri;
	
	@JsonProperty
	@NotNull
	private final ResourceURIWithQuery toUri;
	
	@JsonProperty
	private final boolean includeChanges;

	public DependencyCompareRequest(
		final ResourceURIWithQuery fromUri, 
		final ResourceURIWithQuery toUri, 
		final boolean includeChanges
	) {
		this.fromUri = fromUri;
		this.toUri = toUri;
		this.includeChanges = includeChanges;
	}

	@Override
	public AnalysisCompareResult execute(final RepositoryContext resourceContext) {

		final ResourceURI fromWithoutPath = fromUri.getResourceUri().withoutPath();
		final ResourceURI toWithoutPath = toUri.getResourceUri().withoutPath();

		if (!fromWithoutPath.equals(toWithoutPath)) {
			throw new BadRequestException("Resource URIs should have a common root, got '%s' and '%s'", fromWithoutPath, toWithoutPath);
		}

		// Check whether the resource exists
		final Resource fromResource = ResourceRequests.prepareGet(fromWithoutPath)
			.build()
			.execute(resourceContext);

		if (!(fromResource instanceof final TerminologyResource terminologyResource)) {
			throw new BadRequestException("Only terminology resources are supported, got '%s'", fromResource.getClass().getSimpleName());
		}

		final String toolingId = terminologyResource.getToolingId();
		final RepositoryRequest<AnalysisCompareResult> contentRequest = new RepositoryRequest<>(toolingId, contentContext -> {
			final DependencyComparer dependencyComparer = contentContext.service(DependencyComparer.class);
			return dependencyComparer.compareResource(contentContext, fromUri, toUri, includeChanges);
		});

		return contentRequest.execute(resourceContext);
	}
}
