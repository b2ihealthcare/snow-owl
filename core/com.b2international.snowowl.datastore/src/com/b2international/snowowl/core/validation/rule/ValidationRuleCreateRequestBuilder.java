/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.validation.rule;

import java.util.UUID;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.internal.validation.ValidationRepositoryWriteRequestBuilder;
import com.b2international.snowowl.core.validation.rule.ValidationRule.Severity;
import com.b2international.snowowl.core.validation.rule.ValidationRule.Type;
import com.google.common.base.Strings;

/**
 * @since 6.0
 */
public final class ValidationRuleCreateRequestBuilder 
		extends BaseRequestBuilder<ValidationRuleCreateRequestBuilder, ServiceProvider, String>
		implements ValidationRepositoryWriteRequestBuilder<String> {

	private String id;
	private String toolingId;
	private String messageTemplate;
	private Severity severity;
	private Type type;
	private String implementation;
	
	ValidationRuleCreateRequestBuilder() {}
	
	public ValidationRuleCreateRequestBuilder setId(String id) {
		this.id = id;
		return getSelf();
	}
	
	public ValidationRuleCreateRequestBuilder setToolingId(String toolingId) {
		this.toolingId = toolingId;
		return getSelf();
	}
	
	public ValidationRuleCreateRequestBuilder setMessageTemplate(String messageTemplate) {
		this.messageTemplate = messageTemplate;
		return getSelf();
	}
	
	public ValidationRuleCreateRequestBuilder setSeverity(Severity severity) {
		this.severity = severity;
		return getSelf();
	}
	
	public ValidationRuleCreateRequestBuilder setType(Type type) {
		this.type = type;
		return getSelf();
	}
	
	public ValidationRuleCreateRequestBuilder setImplementation(String implementation) {
		this.implementation = implementation;
		return getSelf();
	}
	
	@Override
	protected Request<ServiceProvider, String> doBuild() {
		ValidationRuleCreateRequest req = new ValidationRuleCreateRequest();
		req.setId(Strings.isNullOrEmpty(id) ? UUID.randomUUID().toString() : id);
		req.setToolingId(toolingId);
		req.setMessageTemplate(messageTemplate);
		req.setSeverity(severity);
		req.setType(type);
		req.setImplementation(implementation);
		return req;
	}

}
