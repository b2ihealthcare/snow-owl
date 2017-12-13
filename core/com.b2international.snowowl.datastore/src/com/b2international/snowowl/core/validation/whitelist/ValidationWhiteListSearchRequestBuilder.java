/*******************************************************************************
 * Copyright (c) 2017 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.core.validation.whitelist;

import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.SearchResourceRequestBuilder;
import com.b2international.snowowl.core.request.SystemRequestBuilder;
import com.b2international.snowowl.core.validation.whitelist.ValidationWhiteListSearchRequest.OptionKey;

/**
 * @since 6.1
 */
public final class ValidationWhiteListSearchRequestBuilder 
	extends SearchResourceRequestBuilder<ValidationWhiteListSearchRequestBuilder, ServiceProvider, ValidationWhiteLists> 
	implements SystemRequestBuilder<ValidationWhiteLists> {

	ValidationWhiteListSearchRequestBuilder() {}
	
	public ValidationWhiteListSearchRequestBuilder filterByRuleId(String ruleId) {
		return addOption(OptionKey.RULE_ID, ruleId);
	}

	public ValidationWhiteListSearchRequestBuilder filterByComponentIdentifier(ComponentIdentifier componentIdentifier) {
		return addOption(OptionKey.COMPONENTENT_IDENTIFIER, componentIdentifier);
	}
	
	@Override
	protected SearchResourceRequest<ServiceProvider, ValidationWhiteLists> createSearch() {
		return new ValidationWhiteListSearchRequest();
	}

}