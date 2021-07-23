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
package com.b2international.snowowl.core.conceptmap;

import java.util.Set;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.compare.ConceptMapCompareConfigurationProperties;
import com.b2international.snowowl.core.compare.ConceptMapCompareResult;
import com.b2international.snowowl.core.context.ResourceRepositoryRequestBuilder;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.ResourceRequest;
import com.b2international.snowowl.core.request.ResourceRequestBuilder;

/**
* @since 7.8
*/
public final class ConceptMapCompareRequestBuilder 
		extends ResourceRequestBuilder<ConceptMapCompareRequestBuilder, RepositoryContext, ConceptMapCompareResult>
		implements ResourceRepositoryRequestBuilder<ConceptMapCompareResult> {
	
	private static final int MAX_LIMIT = Integer.MAX_VALUE - 1;
	
	private final ResourceURI baseConceptMapURI;
	private final ResourceURI compareConceptMapURI;
	
	private int limit = 5000;
	private Set<ConceptMapCompareConfigurationProperties> compareConfig = ConceptMapCompareConfigurationProperties.DEFAULT_SELECTED_PROPERTIES;
	private String preferredDisplay = "FSN";

	public ConceptMapCompareRequestBuilder(ResourceURI baseConceptMapURI, ResourceURI compareConceptMapURI) {
		this.baseConceptMapURI = baseConceptMapURI;
		this.compareConceptMapURI = compareConceptMapURI;
	}

	public ConceptMapCompareRequestBuilder all() {
		this.limit = MAX_LIMIT;
		return getSelf();
	}
	
	public ConceptMapCompareRequestBuilder setLimit(int limit) {
		this.limit = limit;
		return getSelf();
	}
	
	public ConceptMapCompareRequestBuilder setPreferredDisplay(String preferredDisplay) {
		this.preferredDisplay = preferredDisplay;
		return getSelf();
	}
	
	public ConceptMapCompareRequestBuilder setCompareConfig(Set<ConceptMapCompareConfigurationProperties> compareConfig) {
		this.compareConfig = compareConfig;
		return getSelf();
	}
	
	@Override
	protected ResourceRequest<RepositoryContext, ConceptMapCompareResult> create() {
		return new ConceptMapCompareRequest(baseConceptMapURI, compareConceptMapURI, limit, compareConfig, preferredDisplay);
	}
	
}
