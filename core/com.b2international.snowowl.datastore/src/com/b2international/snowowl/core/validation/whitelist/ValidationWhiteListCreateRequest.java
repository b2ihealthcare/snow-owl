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
package com.b2international.snowowl.core.validation.whitelist;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.internal.validation.ValidationRepository;

/**
 * @since 6.1
 */
final class ValidationWhiteListCreateRequest implements Request<ServiceProvider, String> {

	private String id;
	@NotEmpty private String ruleId;
	@NotNull private ComponentIdentifier componentIdentifier;
	
	@Override
	public String execute(ServiceProvider context) {
		context.service(ValidationRepository.class).save(id, new ValidationWhiteList(id, ruleId, componentIdentifier));
		return id;
	}

	void setId(String id) {
		this.id = id;
	}
	
	void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}
	
	void setComponentIdentifier(ComponentIdentifier componentIdentifier) {
		this.componentIdentifier = componentIdentifier;
	}

}
