/*
 * Copyright 2020-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.ecl;

import java.util.List;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.request.ResourceRequest;
import com.b2international.snowowl.core.request.ResourceRequestBuilder;
import com.b2international.snowowl.core.request.SystemRequestBuilder;

/**
 * @since 7.6
 */
public final class EclLabelerRequestBuilder extends ResourceRequestBuilder<EclLabelerRequestBuilder, ServiceProvider, LabeledEclExpressions>
		implements SystemRequestBuilder<LabeledEclExpressions> {

	private final String codeSystemUri;
	private final List<String> expressions;
	private String descriptionType = "FSN";

	public EclLabelerRequestBuilder(String codeSystemUri, String expression) {
		this(codeSystemUri, List.of(expression));
	}
	
	public EclLabelerRequestBuilder(String codeSystemUri, List<String> expressions) {
		this.codeSystemUri = codeSystemUri;
		this.expressions = List.copyOf(expressions);
	}

	/**
	 * Select a description type to use when expanding labels for all concept IDs in the given expression. Supported values depend on the codeSystem
	 * that is being used for fetching the labels. Typically values like fsn|pt|id are supported. 
	 * 
	 * @param descriptionType
	 * @return
	 */
	public EclLabelerRequestBuilder setDescriptionType(String descriptionType) {
		this.descriptionType = descriptionType;
		return getSelf();
	}

	@Override
	protected void init(ResourceRequest<ServiceProvider, LabeledEclExpressions> request) {
		super.init(request);
		EclLabelerRequest req = (EclLabelerRequest) request;
		req.setCodeSystemUri(codeSystemUri);
		req.setExpressions(expressions);
		req.setDescriptionType(descriptionType);
	}

	@Override
	protected ResourceRequest<ServiceProvider, LabeledEclExpressions> create() {
		return new EclLabelerRequest();
	}

}
