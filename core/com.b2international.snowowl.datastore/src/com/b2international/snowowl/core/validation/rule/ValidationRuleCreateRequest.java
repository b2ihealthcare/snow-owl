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

import java.io.IOException;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.index.Writer;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.validation.rule.ValidationRule.Severity;
import com.b2international.snowowl.core.validation.rule.ValidationRule.Type;

/**
 * @since 6.0
 */
final class ValidationRuleCreateRequest implements Request<ServiceProvider, String> {

	private String id;
	
	@NotEmpty
	private String toolingId;
	
	@NotEmpty
	private String messageTemplate;
	
	@NotNull 
	private Severity severity;
	
	@NotNull
	private Type type;
	
	@NotEmpty
	private String implementation;

	ValidationRuleCreateRequest() {}

	void setId(String id) {
		this.id = id;
	}
	
	void setToolingId(String toolingId) {
		this.toolingId = toolingId;
	}
	
	void setMessageTemplate(String messageTemplate) {
		this.messageTemplate = messageTemplate;
	}
	
	void setSeverity(Severity severity) {
		this.severity = severity;
	}
	
	void setType(Type type) {
		this.type = type;
	}
	
	void setImplementation(String implementation) {
		this.implementation = implementation;
	}
	
	@Override
	public String execute(ServiceProvider context) {
		try {
			context.service(Writer.class).put(id, new ValidationRule(id, toolingId, messageTemplate, severity, type, implementation));
		} catch (IOException e) {
			throw new SnowowlRuntimeException("Failed to create validation rule: " + e);
		}
		return id;
	}

}
