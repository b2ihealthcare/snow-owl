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
import com.b2international.snowowl.core.compare.TerminologyResourceCompareResult;
import com.b2international.snowowl.core.context.ResourceRepositoryRequestBuilder;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 9.0
 */
public final class TerminologyResourceCompareRequestBuilder 
	extends BaseRequestBuilder<TerminologyResourceCompareRequestBuilder, RepositoryContext, TerminologyResourceCompareResult> 
	implements ResourceRepositoryRequestBuilder<TerminologyResourceCompareResult> {

	@NotNull
	private ResourceURI fromUri;
	
	@NotNull
	private ResourceURI toUri;
	
	public TerminologyResourceCompareRequestBuilder setFromUri(final ResourceURI fromUri) {
		this.fromUri = fromUri;
		return getSelf();
	}
	
	public TerminologyResourceCompareRequestBuilder setToUri(final ResourceURI toUri) {
		this.toUri = toUri;
		return getSelf();
	}
	
	@Override
	protected Request<RepositoryContext, TerminologyResourceCompareResult> doBuild() {
		return new TerminologyResourceCompareRequest(fromUri, toUri);
	}
}
