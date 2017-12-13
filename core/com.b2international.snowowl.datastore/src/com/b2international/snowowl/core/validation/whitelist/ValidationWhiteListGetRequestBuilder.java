/*******************************************************************************
 * Copyright (c) 2017 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.core.validation.whitelist;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.request.GetResourceRequestBuilder;
import com.b2international.snowowl.core.request.SystemRequestBuilder;

/**
 * @since 6.1
 */
public final class ValidationWhiteListGetRequestBuilder 
	extends GetResourceRequestBuilder<ValidationWhiteListGetRequestBuilder, ValidationWhiteListSearchRequestBuilder, ServiceProvider, ValidationWhiteList>
	implements SystemRequestBuilder<ValidationWhiteList> {
	
	ValidationWhiteListGetRequestBuilder(final String id) {
		super(new ValidationWhiteListGetRequest(id));
	}


}
