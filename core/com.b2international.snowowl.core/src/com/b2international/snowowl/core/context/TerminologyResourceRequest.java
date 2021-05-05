/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.Resource;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.uri.ResourceURIPathResolver;

/**
 * @since 8.0
 * @param <R>
 */
public final class TerminologyResourceRequest<R> extends DelegatingRequest<ServiceProvider, TerminologyResourceContext, R> {

	private static final long serialVersionUID = 1L;
	
	@NotEmpty
	private final ResourceURI resourceURI;

	private transient TerminologyResource resource;
	private transient String branchPath;
	
	public TerminologyResourceRequest(final ResourceURI resourceURI, final Request<TerminologyResourceContext, R> next) {
		super(next);
		this.resourceURI = resourceURI;
	}
	
	public ResourceURI getResourceURI() {
		return resourceURI;
	}
	
	@Override
	public R execute(ServiceProvider context) {
		final DefaultTerminologyResourceContext resourceContext = new DefaultTerminologyResourceContext(context);
		resourceContext.bind(ResourceURI.class, resource.getResourceURI());
		resourceContext.bind(TerminologyResource.class, resource);
		return next(resourceContext);
	}
	
	public TerminologyResource getResource(ServiceProvider context) {
		if (resource == null) {
			Resource resource = ResourceRequests.prepareGet(resourceURI.toString()).buildAsync().getRequest().execute(context);
			if (!(resource instanceof TerminologyResource)) {
				throw new NotFoundException("Terminology Resource", resourceURI.toString());
			}
			this.resource = (TerminologyResource) resource;
		}
		return resource;
	}

	public String getBranchPath(ServiceProvider context) {
		if (branchPath == null) {
			branchPath = context.service(ResourceURIPathResolver.class).resolve(context, resourceURI, resource);
		}
		return branchPath;
	}

}
