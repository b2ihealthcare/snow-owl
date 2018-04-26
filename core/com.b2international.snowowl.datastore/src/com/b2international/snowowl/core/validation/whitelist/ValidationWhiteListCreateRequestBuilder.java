/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.validation.whitelist;

import java.util.Date;

import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.internal.validation.ValidationRepositoryContext;
import com.b2international.snowowl.core.internal.validation.ValidationRepositoryRequestBuilder;

/**
 * @since 6.1
 */
public final class ValidationWhiteListCreateRequestBuilder 
	extends BaseRequestBuilder<ValidationWhiteListCreateRequestBuilder, ValidationRepositoryContext, String> 
	implements ValidationRepositoryRequestBuilder<String> {

	private String ruleId;
	private ComponentIdentifier componentIdentifier;
	private String reporter;
	private long createdAt = new Date().getTime();
	
	ValidationWhiteListCreateRequestBuilder() {}
	
	public ValidationWhiteListCreateRequestBuilder setRuleId(String ruleId) {
		this.ruleId = ruleId;
		return getSelf();
	}
	
	public ValidationWhiteListCreateRequestBuilder setComponentIdentifier(ComponentIdentifier componentIdentifier) {
		this.componentIdentifier = componentIdentifier;
		return getSelf();
	}
	
	public ValidationWhiteListCreateRequestBuilder setReporter(String reporter) {
		this.reporter = reporter;
		return getSelf();
	}
	
	public ValidationWhiteListCreateRequestBuilder setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
		return getSelf();
	}
	
	@Override
	protected Request<ValidationRepositoryContext, String> doBuild() {
		ValidationWhiteListCreateRequest req = new ValidationWhiteListCreateRequest();
		req.setRuleId(ruleId);
		req.setComponentIdentifier(componentIdentifier);
		req.setReporter(reporter);
		req.setCreatedAt(createdAt);
		return req;
	}
	
}