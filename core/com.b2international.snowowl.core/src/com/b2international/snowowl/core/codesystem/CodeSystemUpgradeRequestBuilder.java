/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.core.uri.CodeSystemURI;

/**
 * @since 7.13.0
 */
public final class CodeSystemUpgradeRequestBuilder extends BaseRequestBuilder<CodeSystemUpgradeRequestBuilder, RepositoryContext, String> implements RepositoryRequestBuilder<String> {

	private final CodeSystemURI codeSystem;
	private final CodeSystemURI extensionOf;
	
	private String codeSystemId;
	private boolean force;

	CodeSystemUpgradeRequestBuilder(CodeSystemURI codeSystem, CodeSystemURI extensionOf) {
		this.codeSystem = codeSystem;
		this.extensionOf = extensionOf;
	}
	
	/**
	 * Optionally set the upgrade CodeSystem's unique ID instead of letting the system generate it automatically from the codeSystem and the new extensionOf values.
	 * 
	 * @param codeSystemId
	 * @return
	 */
	public CodeSystemUpgradeRequestBuilder setCodeSystemId(String codeSystemId) {
		this.codeSystemId = codeSystemId;
		return getSelf();
	}
	
	/**
	 * Optionally set the force flag to force recreation of version if it already exists
	 * 
	 * @param force
	 * @return
	 */
	public CodeSystemUpgradeRequestBuilder setForce(boolean force) {
		this.force = force;
		return getSelf();
	}
	
	@Override
	protected Request<RepositoryContext, String> doBuild() {
		final CodeSystemUpgradeRequest req = new CodeSystemUpgradeRequest(codeSystem, extensionOf);
		req.setCodeSystemId(codeSystemId);
		req.setForce(force);
		return req;
	}

}
