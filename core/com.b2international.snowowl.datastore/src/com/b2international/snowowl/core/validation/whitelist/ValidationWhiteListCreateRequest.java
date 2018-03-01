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

import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.internal.validation.ValidationRepositoryContext;

/**
 * @since 6.1
 */
final class ValidationWhiteListCreateRequest implements Request<ValidationRepositoryContext, String> {

	@NotEmpty String ruleId;
	@NotNull ComponentIdentifier componentIdentifier;
	@NotEmpty String reporter;
	private long createdAt;
	
	@Override
	public String execute(ValidationRepositoryContext context) {
		final String id = UUID.randomUUID().toString();
		context.save(new ValidationWhiteList(id, ruleId, reporter, createdAt, componentIdentifier));
		return id;
	}

	void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}
	
	void setComponentIdentifier(ComponentIdentifier componentIdentifier) {
		this.componentIdentifier = componentIdentifier;
	}
	
	void setReporter(String reporter) {
		this.reporter = reporter;
	}
	
	void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

}
