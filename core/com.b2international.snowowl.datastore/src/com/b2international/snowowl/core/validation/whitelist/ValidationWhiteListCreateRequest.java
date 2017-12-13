/*******************************************************************************
 * Copyright (c) 2017 B2i Healthcare. All rights reserved.
 *******************************************************************************/
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
