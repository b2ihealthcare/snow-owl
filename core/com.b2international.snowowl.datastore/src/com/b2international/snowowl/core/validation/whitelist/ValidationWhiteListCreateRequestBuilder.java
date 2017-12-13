/*******************************************************************************
 * Copyright (c) 2017 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.core.validation.whitelist;

import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.SystemRequestBuilder;

/**
 * @since 6.1
 */
public final class ValidationWhiteListCreateRequestBuilder 
	extends BaseRequestBuilder<ValidationWhiteListCreateRequestBuilder, ServiceProvider, String> 
	implements SystemRequestBuilder<String> {

	private String id;
	private String ruleId;
	private ComponentIdentifier componentIdentifier;
	
	ValidationWhiteListCreateRequestBuilder() {}
	
	public ValidationWhiteListCreateRequestBuilder setId(String id) {
		this.id = id;
		return getSelf();
	}
	
	public ValidationWhiteListCreateRequestBuilder setRuleId(String ruleId) {
		this.ruleId = ruleId;
		return getSelf();
	}
	
	public ValidationWhiteListCreateRequestBuilder setComponentIdentifier(ComponentIdentifier componentIdentifier) {
		this.componentIdentifier = componentIdentifier;
		return getSelf();
	}

	@Override
	protected Request<ServiceProvider, String> doBuild() {
		ValidationWhiteListCreateRequest req = new ValidationWhiteListCreateRequest();
		req.setId(id);
		req.setRuleId(ruleId);
		req.setComponentIdentifier(componentIdentifier);
		return req;
	}
	
}