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
package com.b2international.snowowl.core.codesystem;

import java.util.Collection;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.SystemRequestBuilder;
import com.google.common.collect.ImmutableSet;

/**
 * @since 7.8
 */
public final class AllCodeSystemSearchRequestBuilder implements SystemRequestBuilder<CodeSystems>  {

	private Collection<String> ids;
	private Iterable<String> fields;
	private String expand;
	private Iterable<String> toolingIds;
	
	public AllCodeSystemSearchRequestBuilder filterById(final String id) {
		this.ids = ImmutableSet.of(id);
		return this;
	}
	
	public AllCodeSystemSearchRequestBuilder filterByIds(final Collection<String> ids) {
		this.ids = ImmutableSet.copyOf(ids);
		return this;
	}
	
	public AllCodeSystemSearchRequestBuilder filterByToolingIds(final Iterable<String> toolingIds) {
		this.toolingIds = toolingIds;
		return this;
	}
	
	public AllCodeSystemSearchRequestBuilder setFields(final Iterable<String> fields) {
		this.fields = fields;
		return this;
	}
	
	public AllCodeSystemSearchRequestBuilder setExpand(final String expand) {
		this.expand = expand;
		return this;
	}
	
	@Override
	public Request<ServiceProvider, CodeSystems> build() {
		return new AllCodeSystemSearchRequest(ids, fields, expand, toolingIds);
	}

}
