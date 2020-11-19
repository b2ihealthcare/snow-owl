/*
 * Copyright 2019-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.SystemRequestBuilder;
import com.b2international.snowowl.core.uri.CodeSystemURI;

/**
 * @since 6.20.0
 */
public final class ValidationIssueDeleteRequestBuilder 
	extends BaseRequestBuilder<ValidationIssueDeleteRequestBuilder, ServiceProvider, Boolean>
	implements SystemRequestBuilder<Boolean> {
	
	private String codeSystemURI;
	private String toolingId;
	
	ValidationIssueDeleteRequestBuilder() {}
	
	public ValidationIssueDeleteRequestBuilder setCodeSystemURI(CodeSystemURI codeSystemURI) {
		this.codeSystemURI = codeSystemURI.toString();
		return getSelf();
	}
	
	public ValidationIssueDeleteRequestBuilder setToolingId(String toolingId) {
		this.toolingId = toolingId;
		return getSelf();
	}
	
	@Override
	protected Request<ServiceProvider, Boolean> doBuild() {
		return new ValidationIssueDeleteRequest(codeSystemURI, toolingId);
	}
	
}
