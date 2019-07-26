/*
 * Copyright 2018-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
	
	public ValidationWhiteListSearchRequestBuilder filterByTerm(String term) {
		return addOption(OptionKey.TERM, term);
	}

	public ValidationWhiteListSearchRequestBuilder filterByRuleId(String ruleId) {
		return addOption(OptionKey.RULE_ID, ruleId);
	}

	public ValidationWhiteListSearchRequestBuilder filterByRuleIds(Iterable<String> ruleIds) {
		return addOption(OptionKey.RULE_ID, ruleIds);
	}

	public ValidationWhiteListSearchRequestBuilder filterByComponentIdentifier(String componentIdentifier) {
		return addOption(OptionKey.COMPONENT_ID, componentIdentifier);
	}

	public ValidationWhiteListSearchRequestBuilder filterByComponentIdentifiers(Iterable<String> componentIdentifiers) {
		return addOption(OptionKey.COMPONENT_ID, componentIdentifiers);
	}
	
	public ValidationWhiteListSearchRequestBuilder filterByComponentType(short terminologyComponentId) {
		return addOption(OptionKey.COMPONENT_TYPE, terminologyComponentId);
	}

	public ValidationWhiteListSearchRequestBuilder filterByComponentType(Iterable<Short> terminologyComponentIds) {
		return addOption(OptionKey.COMPONENT_TYPE, terminologyComponentIds);
	}
	
	public ValidationWhiteListSearchRequestBuilder filterByReporter(String reporter) {
		return addOption(OptionKey.REPORTER, reporter);
	}

	public ValidationWhiteListSearchRequestBuilder filterByReporters(Iterable<String> reporters) {
		return addOption(OptionKey.REPORTER, reporters);
	}
	
	public ValidationWhiteListSearchRequestBuilder filterByCreatedAfter(long createdAfter) {
		return addOption(OptionKey.CREATED_AFTER, createdAfter);
	}
	
	public ValidationWhiteListSearchRequestBuilder filterByCreatedBefore(long createdBefore) {
		return addOption(OptionKey.CREATED_BEFORE, createdBefore);
	}
	
	@Override
	protected SearchResourceRequest<ServiceProvider, ValidationWhiteLists> createSearch() {
		return new ValidationWhiteListSearchRequest();
	}

}