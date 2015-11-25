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

import java.util.Collections;
import java.util.Map;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.RequestBuilder;
import com.b2international.snowowl.core.events.Requests;

/**
 * @since 4.5
 */
public class RefSetMemberCreateRequestBuilder implements RequestBuilder<TransactionContext, String> {

	private String moduleId;
	private String referenceSetId;
	private String referencedComponentId;
	private Map<String, Object> properties = Collections.emptyMap();
	
	RefSetMemberCreateRequestBuilder() {}
	
	public RefSetMemberCreateRequestBuilder setReferencedComponentId(String referencedComponentId) {
		this.referencedComponentId = referencedComponentId;
		return this;
	}
	
	public RefSetMemberCreateRequestBuilder setModuleId(String moduleId) {
		this.moduleId = moduleId;
		return this;
	}
	
	public RefSetMemberCreateRequestBuilder setReferenceSetId(String referenceSetId) {
		this.referenceSetId = referenceSetId;
		return this;
	}
	
	public RefSetMemberCreateRequestBuilder setProperties(Map<String, Object> properties) {
		this.properties = properties;
		return this;
	}
	
	public RefSetMemberCreateRequestBuilder setSource(Map<String, Object> source) {
		setModuleId((String) source.get("moduleId"));
		setReferencedComponentId((String) source.get("referencedComponentId"));
		setReferenceSetId((String) source.get("referenceSetId"));
		setProperties(source);
		return this;
	}
	
	@Override
	public Request<TransactionContext, String> build() {
		final SnomedRefSetMemberCreateRequest req = new SnomedRefSetMemberCreateRequest();
		req.setModuleId(moduleId);
		req.setReferencedComponentId(referencedComponentId);
		req.setReferenceSetId(referenceSetId);
		req.setProperties(properties);
		return req;
	}

	public Request<TransactionContext, Void> buildNoContent() {
		return Requests.noContent(build());
	}
	
}
