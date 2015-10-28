/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.server.request;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.request.BranchRequest;
import com.b2international.snowowl.datastore.request.RepositoryRequest;
import com.b2international.snowowl.snomed.core.domain.SearchKind;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

/**
 * @since 4.5
 */
public final class SnomedConceptSearchRequestBuilder {

	private String branch;
	private int offset = 0;
	private int limit = 50;
	private ImmutableMap.Builder<SearchKind, String> filters = ImmutableMap.builder();
	
	public SnomedConceptSearchRequestBuilder(String branch) {
		this.branch = branch;
	}
	
	public final SnomedConceptSearchRequestBuilder setOffset(int offset) {
		this.offset = offset;
		return this;
	}
	
	public final SnomedConceptSearchRequestBuilder setLimit(int limit) {
		this.limit = limit;
		return this;
	}
	
	public SnomedConceptSearchRequestBuilder setLabel(String label) {
		if (!Strings.isNullOrEmpty(label)) {
			filters.put(SearchKind.LABEL, label);
		}
		return this;
	}
	
	public SnomedConceptSearchRequestBuilder setEscg(String escg) {
		if (!Strings.isNullOrEmpty(escg)) {
			filters.put(SearchKind.ESCG, escg);
		}
		return this;
	}
	
	public Request<ServiceProvider, SnomedConcepts> build() {
		return new RepositoryRequest<>("SNOMEDCT", new BranchRequest<>(branch, new SnomedConceptReadAllRequest(offset, limit, filters.build())));
	}

}
