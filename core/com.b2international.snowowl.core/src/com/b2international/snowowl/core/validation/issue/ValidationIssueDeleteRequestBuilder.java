/*
 * Copyright 2019-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.validation.issue;

import java.util.Set;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.SystemRequestBuilder;
import com.google.common.collect.ImmutableSet;

/**
 * @since 6.20.0
 */
public final class ValidationIssueDeleteRequestBuilder 
	extends BaseRequestBuilder<ValidationIssueDeleteRequestBuilder, ServiceProvider, Boolean>
	implements SystemRequestBuilder<Boolean> {
	
	private Set<String> codeSystemURIs;
	private Set<String> toolingIds;
	
	ValidationIssueDeleteRequestBuilder() {}
	
	public ValidationIssueDeleteRequestBuilder setCodeSystemURIs(Iterable<String> codeSystemURIs) {
		this.codeSystemURIs = codeSystemURIs == null ? null : ImmutableSet.copyOf(codeSystemURIs);
		return getSelf();
	}
	
	public ValidationIssueDeleteRequestBuilder setToolingIds(Iterable<String> toolingIds) {
		this.toolingIds = toolingIds == null ? null : ImmutableSet.copyOf(toolingIds);
		return getSelf();
	}
	
	@Override
	protected Request<ServiceProvider, Boolean> doBuild() {
		return new ValidationIssueDeleteRequest(codeSystemURIs, toolingIds);
	}
	
}
