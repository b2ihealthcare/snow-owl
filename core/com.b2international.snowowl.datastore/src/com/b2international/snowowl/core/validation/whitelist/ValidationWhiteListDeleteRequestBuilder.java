/*******************************************************************************
 * Copyright (c) 2017 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.core.validation.whitelist;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.SystemRequestBuilder;

/**
 * @since 6.1
 */
public final class ValidationWhiteListDeleteRequestBuilder 
	extends BaseRequestBuilder<ValidationWhiteListDeleteRequestBuilder, ServiceProvider, Boolean>
	implements SystemRequestBuilder<Boolean> {

	private final String id;

	ValidationWhiteListDeleteRequestBuilder(final String id) {
		this.id = id;
	}
	
	@Override
	protected Request<ServiceProvider, Boolean> doBuild() {
		return new ValidationWhiteListDeleteRequest(id);
	}
}
