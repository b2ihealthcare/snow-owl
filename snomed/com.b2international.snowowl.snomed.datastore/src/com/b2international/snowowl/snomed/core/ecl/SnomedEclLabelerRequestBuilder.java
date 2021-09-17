/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.ecl;

import java.util.List;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.request.ResourceRequest;
import com.b2international.snowowl.core.request.ResourceRequestBuilder;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.datastore.request.SnomedContentRequestBuilder;

/**
 * @since 7.6
 */
public final class SnomedEclLabelerRequestBuilder extends ResourceRequestBuilder<SnomedEclLabelerRequestBuilder, BranchContext, LabeledEclExpressions>
		implements SnomedContentRequestBuilder<LabeledEclExpressions> {

	private final List<String> expressions;
	private String descriptionType = SnomedConcept.Expand.FULLY_SPECIFIED_NAME;

	public SnomedEclLabelerRequestBuilder(String expression) {
		this(List.of(expression));
	}
	
	public SnomedEclLabelerRequestBuilder(List<String> expressions) {
		this.expressions = List.copyOf(expressions);
	}

	/**
	 * Select a description type to use when expanding labels for all concept IDs in the given expression. Supported values are: <code>fsn</code> and
	 * <code>pt</code>. Other values are simply omitted and no label expansion will happen.
	 * 
	 * @param descriptionType
	 * @return
	 * @see SnomedConcept.Expand#FULLY_SPECIFIED_NAME
	 * @see SnomedConcept.Expand#PREFERRED_TERM
	 */
	public SnomedEclLabelerRequestBuilder setDescriptionType(String descriptionType) {
		this.descriptionType = descriptionType;
		return getSelf();
	}

	@Override
	protected void init(ResourceRequest<BranchContext, LabeledEclExpressions> request) {
		super.init(request);
		SnomedEclLabelerRequest req = (SnomedEclLabelerRequest) request;
		req.setExpressions(expressions);
		req.setDescriptionType(descriptionType);
	}

	@Override
	protected ResourceRequest<BranchContext, LabeledEclExpressions> create() {
		return new SnomedEclLabelerRequest();
	}

}
