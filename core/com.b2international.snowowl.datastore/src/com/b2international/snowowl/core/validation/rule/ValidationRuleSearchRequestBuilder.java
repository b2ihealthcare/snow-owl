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
package com.b2international.snowowl.core.validation.rule;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.SearchResourceRequestBuilder;
import com.b2international.snowowl.core.request.SystemRequestBuilder;
import com.b2international.snowowl.core.validation.rule.ValidationRule.Severity;
import com.b2international.snowowl.core.validation.rule.ValidationRuleSearchRequest.OptionKey;

/**
 * @since 6.0
 */
public final class ValidationRuleSearchRequestBuilder
		extends SearchResourceRequestBuilder<ValidationRuleSearchRequestBuilder, ServiceProvider, ValidationRules>
		implements SystemRequestBuilder<ValidationRules> {

	ValidationRuleSearchRequestBuilder() {}
	
	public ValidationRuleSearchRequestBuilder filterBySeverity(final Severity severity) {
		return addOption(OptionKey.SEVERITY, severity);
	}
	
	public ValidationRuleSearchRequestBuilder filterBySeverity(final Iterable<Severity> severities) {
		return addOption(OptionKey.SEVERITY, severities);
	}
 	
	@Override
	protected SearchResourceRequest<ServiceProvider, ValidationRules> createSearch() {
		return new ValidationRuleSearchRequest();
	}

}
