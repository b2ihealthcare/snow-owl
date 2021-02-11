/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.codesystem;

import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.RepositoryRequestBuilder;

/**
 * @since 7.15.0
 */
public final class CodeSystemCompleteUpgradeRequestBuilder 
			extends BaseRequestBuilder<CodeSystemCompleteUpgradeRequestBuilder, RepositoryContext, Boolean>
			implements RepositoryRequestBuilder<Boolean> {

	private final String codeSystemId;

	public CodeSystemCompleteUpgradeRequestBuilder(String codeSystemId) {
		this.codeSystemId = codeSystemId;
	}
	
	@Override
	protected Request<RepositoryContext, Boolean> doBuild() {
		CodeSystemCompleteUpgradeRequest req = new CodeSystemCompleteUpgradeRequest();
		req.setCodeSystemId(codeSystemId);
		return req;
	}

}
