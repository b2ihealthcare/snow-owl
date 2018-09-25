/*
 * Copyright 2017-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.validation;

import java.util.Collection;
import java.util.Map;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.request.RevisionIndexRequestBuilder;

/**
 * @since 6.0
 */
public final class ValidateRequestBuilder 
		extends BaseRequestBuilder<ValidateRequestBuilder, BranchContext, ValidationResult>
		implements RevisionIndexRequestBuilder<ValidationResult> {
	
	ValidateRequestBuilder() {}
	
	private Collection<String> ruleIds;
	
	private Map<String, Object> ruleParameters;
	
	public ValidateRequestBuilder setRuleIds(Collection<String> ruleIds) {
		this.ruleIds = ruleIds;
		return getSelf();
	}
	
	public ValidateRequestBuilder setRuleParameters(Map<String, Object> ruleParameters) {
		this.ruleParameters = ruleParameters;
		return getSelf();
	}
	
	@Override
	protected Request<BranchContext, ValidationResult> doBuild() {
		ValidateRequest validateRequest = new ValidateRequest();
		validateRequest.setRuleIds(ruleIds);
		validateRequest.setRuleParameters(ruleParameters);
		return validateRequest;
	}

}
