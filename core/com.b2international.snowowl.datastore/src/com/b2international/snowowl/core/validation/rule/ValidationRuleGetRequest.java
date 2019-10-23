/*
 * Copyright 2017-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.core.authorization.AccessControl;
import com.b2international.snowowl.core.request.GetResourceRequest;
import com.b2international.snowowl.identity.domain.Permission;

/**
 * @since 6.0
 */
final class ValidationRuleGetRequest
		extends GetResourceRequest<ValidationRuleSearchRequestBuilder, ServiceProvider, ValidationRule>
		implements AccessControl {

	ValidationRuleGetRequest(String ruleId) {
		super(ruleId);
	}

	@Override
	protected ValidationRuleSearchRequestBuilder createSearchRequestBuilder() {
		return new ValidationRuleSearchRequestBuilder();
	}
	
	@Override
	public String getOperation() {
		return Permission.BROWSE;
	}
	
}
