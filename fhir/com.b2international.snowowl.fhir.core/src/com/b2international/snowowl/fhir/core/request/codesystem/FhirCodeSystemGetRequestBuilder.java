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
package com.b2international.snowowl.fhir.core.request.codesystem;

import java.util.List;

import com.b2international.snowowl.core.context.ResourceRepositoryRequestBuilder;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.GetResourceRequestBuilder;
import com.b2international.snowowl.core.request.ResourceRequest;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;

/**
 * @since 8.0
 */
public final class FhirCodeSystemGetRequestBuilder 
		extends GetResourceRequestBuilder<FhirCodeSystemGetRequestBuilder, FhirCodeSystemSearchRequestBuilder, RepositoryContext, Bundle, CodeSystem>
		implements ResourceRepositoryRequestBuilder<CodeSystem> {

	private String summary;
	private List<String> elements;

	public FhirCodeSystemGetRequestBuilder(String idOrUrl) {
		super(new FhirCodeSystemGetRequest(idOrUrl));
	}
	
	@Override
	protected void init(ResourceRequest<RepositoryContext, CodeSystem> req) {
		super.init(req);
		FhirCodeSystemGetRequest request = (FhirCodeSystemGetRequest) req;
		request.setSummary(summary);
		request.setElements(elements);
	}

	public FhirCodeSystemGetRequestBuilder setSummary(String summary) {
		this.summary = summary;
		return getSelf();
	}
	
	public FhirCodeSystemGetRequestBuilder setElements(List<String> elements) {
		this.elements = elements;
		return getSelf();
	}

}
