/*******************************************************************************
 * Copyright (c) 2017 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.core.validation.whitelist;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.request.GetResourceRequest;

/**
 * @since 6.1
 */
final class ValidationWhiteListGetRequest 
	extends GetResourceRequest<ValidationWhiteListSearchRequestBuilder, ServiceProvider, ValidationWhiteList>{

	protected ValidationWhiteListGetRequest(String id) {
		super(id);
	}

	@Override
	protected ValidationWhiteListSearchRequestBuilder createSearchRequestBuilder() {
		return new ValidationWhiteListSearchRequestBuilder();
	}
	
}
