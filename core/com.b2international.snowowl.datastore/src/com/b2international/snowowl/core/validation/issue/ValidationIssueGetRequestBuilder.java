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
package com.b2international.snowowl.core.validation.issue;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.internal.validation.ValidationRepositoryReadRequestBuilder;
import com.b2international.snowowl.core.request.GetResourceRequestBuilder;

/**
 * @since 6.0
 */
public final class ValidationIssueGetRequestBuilder 
		extends GetResourceRequestBuilder<ValidationIssueGetRequestBuilder, ValidationIssueSearchRequestBuilder, ServiceProvider, ValidationIssue> 
		implements ValidationRepositoryReadRequestBuilder<ValidationIssue> {

	ValidationIssueGetRequestBuilder(final String issueId) {
		super(new ValidationIssueGetRequest(issueId));
	}

}
