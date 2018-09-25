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

import com.b2international.snowowl.core.validation.issue.ValidationIssueRequests;
import com.b2international.snowowl.core.validation.rule.ValidationRuleRequests;
import com.b2international.snowowl.core.validation.whitelist.ValidationWhiteListRequests;

/**
 * @since 6.0
 */
public final class ValidationRequests {

	private ValidationRequests() {}
	
	public static ValidationIssueRequests issues() {
		return ValidationIssueRequests.INSTANCE;
	}
	
	public static ValidationRuleRequests rules() {
		return ValidationRuleRequests.INSTANCE;
	}

	public static ValidationWhiteListRequests whiteList() {
		return ValidationWhiteListRequests.INSTANCE;
	}

	public static ValidateRequestBuilder prepareValidate() {
		
		return new ValidateRequestBuilder();
	}

}