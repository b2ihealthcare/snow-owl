/*******************************************************************************
 * Copyright (c) 2023 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.core.request.resource;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.Resource;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.compare.TerminologyResourceCompareResult;
import com.b2international.snowowl.core.compare.TerminologyResourceComparer;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.RepositoryRequest;
import com.b2international.snowowl.core.request.ResourceRequest;
import com.b2international.snowowl.core.request.ResourceRequests;

/**
 * @since 9.0
 */
final class TerminologyResourceCompareRequest extends ResourceRequest<RepositoryContext, TerminologyResourceCompareResult> {

	private static final long serialVersionUID = 1L;

	private final ResourceURI fromUri;
	private final ResourceURI toUri;
	private final String termType;

	public TerminologyResourceCompareRequest(final ResourceURI fromUri, final ResourceURI toUri, final String termType) {
		this.fromUri = fromUri;
		this.toUri = toUri;
		this.termType = termType;
	}

	@Override
	public TerminologyResourceCompareResult execute(final RepositoryContext metaContext) {

		final ResourceURI fromWithoutPath = fromUri.withoutPath();
		final ResourceURI toWithoutPath = toUri.withoutPath();

		if (!fromWithoutPath.equals(toWithoutPath)) {
			throw new BadRequestException("Resource URIs should have a common root, got '%s' and '%s'", fromWithoutPath, toWithoutPath);
		}

		// Check whether the resource exists
		final Resource fromResource = ResourceRequests.prepareGet(fromWithoutPath)
			.build()
			.execute(metaContext);

		if (!(fromResource instanceof final TerminologyResource terminologyResource)) {
			throw new BadRequestException("Only terminology resources are supported, got '%s'", fromResource.getClass().getSimpleName());
		}

		final String toolingId = terminologyResource.getToolingId();
		final RepositoryRequest<TerminologyResourceCompareResult> repositoryRequest = new RepositoryRequest<>(toolingId, repositoryContext -> {
			final TerminologyResourceComparer resourceComparer = repositoryContext.service(TerminologyResourceComparer.class);
			return resourceComparer.compareResource(repositoryContext, fromUri, toUri, termType, locales());
		});

		return repositoryRequest.execute(metaContext);
	}
}
