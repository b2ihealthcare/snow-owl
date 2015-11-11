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

import java.util.Map;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.BaseRequest;

/**
 * @since 4.5
 */
public abstract class SearchRequest<B> extends BaseRequest<BranchContext, B> {

	@Min(0)
	private int offset;
	
	@Min(1)
	private int limit;
	
	@NotNull
	private Map<String, String> parameters;
	
	protected SearchRequest() {}
	
	void setLimit(int limit) {
		this.limit = limit;
	}
	
	void setOffset(int offset) {
		this.offset = offset;
	}
	
	void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}
	
	protected final int offset() {
		return this.offset;
	}
	
	protected final int limit() {
		return this.limit;
	}
	
	protected final Map<String, String> parameters() {
		return parameters;
	}
	
}
