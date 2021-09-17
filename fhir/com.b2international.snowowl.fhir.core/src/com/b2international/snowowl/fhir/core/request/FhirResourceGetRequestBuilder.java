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
package com.b2international.snowowl.fhir.core.request;

import java.util.List;

import com.b2international.snowowl.core.context.ResourceRepositoryRequestBuilder;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.GetResourceRequestBuilder;
import com.b2international.snowowl.core.request.ResourceRequest;
import com.b2international.snowowl.fhir.core.model.Bundle;

/**
 * @since 8.0
 */
public abstract class FhirResourceGetRequestBuilder<B extends FhirResourceGetRequestBuilder<B, SB, R>, SB extends FhirResourceSearchRequestBuilder<SB>, R> 
	extends GetResourceRequestBuilder<B, SB, RepositoryContext, Bundle, R>
	implements ResourceRepositoryRequestBuilder<R> {

	private String summary;
	private List<String> elements;
	
	public FhirResourceGetRequestBuilder(FhirResourceGetRequest<SB, R> request) {
		super(request);
	}

	@Override
	protected void init(ResourceRequest<RepositoryContext, R> req) {
		super.init(req);
		FhirResourceGetRequest<SB, R> request = (FhirResourceGetRequest<SB, R>) req;
		request.setSummary(summary);
		request.setElements(elements);
	}

	public final B setSummary(String summary) {
		this.summary = summary;
		return getSelf();
	}

	public final B setElements(List<String> elements) {
		this.elements = elements;
		return getSelf();
	}
	
}
