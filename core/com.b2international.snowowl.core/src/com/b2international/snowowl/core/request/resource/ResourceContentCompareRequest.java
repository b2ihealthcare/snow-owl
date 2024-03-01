/*
 * Copyright 2023-2024 B2i Healthcare, https://b2ihealthcare.com
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

import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.NotEmpty;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.Resource;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ResourceURIWithQuery;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.compare.AnalysisCompareResult;
import com.b2international.snowowl.core.compare.ResourceContentComparer;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.RepositoryRequest;
import com.b2international.snowowl.core.request.ResourceRequest;
import com.b2international.snowowl.core.request.ResourceRequests;

/**
 * @since 9.0.0
 */
final class ResourceContentCompareRequest extends ResourceRequest<RepositoryContext, AnalysisCompareResult> {

	private static final long serialVersionUID = 1L;

	@NotNull
	private final ResourceURIWithQuery fromUri;
	
	@NotNull
	private final ResourceURIWithQuery toUri;
	
	@NotEmpty
	private final String termType;
	
	private final boolean includeChanges;

	public ResourceContentCompareRequest(
		final ResourceURIWithQuery fromUri, 
		final ResourceURIWithQuery toUri, 
		final boolean includeChanges, 
		final String termType
	) {
		this.fromUri = fromUri;
		this.toUri = toUri;
		this.termType = termType;
		this.includeChanges = includeChanges;
	}

	@Override
	public AnalysisCompareResult execute(final RepositoryContext resourceContext) {

		final ResourceURI fromWithoutPath = fromUri.getResourceUri().withoutPath().withoutSpecialResourceIdPart();
		final ResourceURI toWithoutPath = toUri.getResourceUri().withoutPath().withoutSpecialResourceIdPart();

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
			final ResourceContentComparer contentComparer = contentContext.service(ResourceContentComparer.class);
			return contentComparer.compareResource(contentContext, fromUri, toUri, includeChanges, termType, locales());
		});

		return contentRequest.execute(resourceContext);
	}

}
