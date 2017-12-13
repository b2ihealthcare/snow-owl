/*******************************************************************************
 * Copyright (c) 2017 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.core.validation.whitelist;

/**
 * @since 6.1
 */
public enum ValidationWhiteListRequests {

	INSTANCE;
	
	public ValidationWhiteListCreateRequestBuilder prepareCreate() {
		return new ValidationWhiteListCreateRequestBuilder();
	}
	
	public ValidationWhiteListDeleteRequestBuilder prepareDelete(String id) {
		return new ValidationWhiteListDeleteRequestBuilder(id);
	}

	public ValidationWhiteListGetRequestBuilder prepareGet(String id) {
		return new ValidationWhiteListGetRequestBuilder(id);
	}
	
	public ValidationWhiteListSearchRequestBuilder prepareSearch() {
		return new ValidationWhiteListSearchRequestBuilder();
	}
	
}
