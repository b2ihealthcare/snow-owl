/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request;

import java.util.Map;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 4.5
 */
public final class QueryRefSetUpdateRequestBuilder extends BaseRequestBuilder<QueryRefSetUpdateRequestBuilder, TransactionContext, Boolean> {

	private String referenceSetId;
	private String moduleId;
	
	public QueryRefSetUpdateRequestBuilder setModuleId(String moduleId) {
		this.moduleId = moduleId;
		return this;
	}
	
	public QueryRefSetUpdateRequestBuilder setReferenceSetId(String refSetId) {
		this.referenceSetId = refSetId;
		return this;
	}
	
	public QueryRefSetUpdateRequestBuilder setSource(Map<String, Object> source) {
		return setModuleId((String) source.get("moduleId")).setReferenceSetId((String) source.get("referenceSetId"));
	}

	@Override
	protected Request<TransactionContext, Boolean> doBuild() {
		return new QueryRefSetUpdateRequest(referenceSetId, moduleId);
	}
	
}
