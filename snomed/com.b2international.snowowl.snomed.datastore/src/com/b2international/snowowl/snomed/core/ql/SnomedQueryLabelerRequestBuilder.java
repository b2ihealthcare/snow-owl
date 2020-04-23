/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.ql;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.request.ResourceRequest;
import com.b2international.snowowl.core.request.ResourceRequestBuilder;
import com.b2international.snowowl.core.request.RevisionIndexRequestBuilder;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;

/**
 * @since 7.6
 */
public final class SnomedQueryLabelerRequestBuilder 
		extends ResourceRequestBuilder<SnomedQueryLabelerRequestBuilder, BranchContext, String>
		implements RevisionIndexRequestBuilder<String> {

	private final String expression;
	private String descriptionType = SnomedConcept.Expand.FULLY_SPECIFIED_NAME; 

	public SnomedQueryLabelerRequestBuilder(String expression) {
		this.expression = expression;
	}
	
	public SnomedQueryLabelerRequestBuilder setDescriptionType(String descriptionType) {
		this.descriptionType = descriptionType;
		return getSelf();
	}

	@Override
	protected void init(ResourceRequest<BranchContext, String> request) {
		super.init(request);
		SnomedQueryLabelerRequest req = (SnomedQueryLabelerRequest) request;
		req.setExpression(expression);
		req.setDescriptionType(descriptionType);
	}
	
	@Override
	protected ResourceRequest<BranchContext, String> create() {
		return new SnomedQueryLabelerRequest();
	}
	
}
