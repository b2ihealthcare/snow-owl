/*******************************************************************************
 * Copyright (c) 2023 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.core.request.resource;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

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
 * @since 9.0
 */
final class ResourceContentCompareRequest extends ResourceRequest<RepositoryContext, AnalysisCompareResult> {

	private static final long serialVersionUID = 1L;

	private static final String SPECIAL_ID_SEPARATOR = "~";

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

		final ResourceURI fromWithoutPath = removeSpecialIdSuffix(fromUri.getResourceUri().withoutPath());
		final ResourceURI toWithoutPath = removeSpecialIdSuffix(toUri.getResourceUri().withoutPath());

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

	private static ResourceURI removeSpecialIdSuffix(final ResourceURI resourceUri) {
		final String resourceId = resourceUri.getResourceId();
		final int separatorIdx = resourceId.lastIndexOf(SPECIAL_ID_SEPARATOR);
		
		if (separatorIdx > 0) {
			return ResourceURI.of(resourceUri.getResourceType(), resourceId.substring(0, separatorIdx));
		} else {
			return resourceUri;
		}
	}
}