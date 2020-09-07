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
package com.b2international.snowowl.core.request;

import static com.b2international.snowowl.core.compare.ConceptMapCompareResult.DEFAULT_LIMIT_PER_COMPARE_CATEGORY;

import javax.validation.constraints.Min;

import com.b2international.snowowl.core.compare.ConceptMapCompareResult;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.uri.ComponentURI;

/**
* @since 7.8
*/
public final class ConceptMapCompareRequestBuilder 
		extends ResourceRequestBuilder<ConceptMapCompareRequestBuilder, BranchContext, ConceptMapCompareResult>
		implements RevisionIndexRequestBuilder<ConceptMapCompareResult> {
	
	@Min(0)
	private int limit;

	private final ComponentURI baseConceptMapURI;
	private final ComponentURI compareConceptMapURI;
	
	public ConceptMapCompareRequestBuilder(ComponentURI baseConceptMapURI, ComponentURI compareConceptMapURI) {
		this.baseConceptMapURI = baseConceptMapURI;
		this.compareConceptMapURI = compareConceptMapURI;
		this.limit = DEFAULT_LIMIT_PER_COMPARE_CATEGORY;
	}
	
	public ConceptMapCompareRequestBuilder setLimit(int limit) {
		this.limit = limit;
		return this;
	}
	
	@Override
	protected ResourceRequest<BranchContext, ConceptMapCompareResult> create() {
		return new ConceptMapCompareRequest(baseConceptMapURI, compareConceptMapURI, limit);
	}
	
}
