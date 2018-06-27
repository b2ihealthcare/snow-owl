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

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.internal.validation.ValidationRepository;
import com.b2international.snowowl.core.validation.rule.ValidationRule.CheckType;
import com.b2international.snowowl.core.validation.rule.ValidationRule.Severity;

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
	
	private CheckType checkType;
	
	@NotNull
	private String type;
	
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
	
	void setCheckType(CheckType checkType) {
		this.checkType = checkType;
	}
	
	void setType(String type) {
		this.type = type;
	}
	
	void setImplementation(String implementation) {
		this.implementation = implementation;
	}
	
	@Override
	public String execute(ServiceProvider context) {
		context.service(ValidationRepository.class).save(id, new ValidationRule(id, toolingId, messageTemplate, severity, checkType,  type, implementation));
		return id;
	}

}
