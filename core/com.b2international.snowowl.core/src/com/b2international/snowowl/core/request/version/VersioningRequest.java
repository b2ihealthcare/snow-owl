/*
 * Copyright 2011-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.request.version;

import org.slf4j.Logger;

import com.b2international.commons.exceptions.ApiException;
import com.b2international.index.revision.Commit;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.authorization.AccessControl;
import com.b2international.snowowl.core.domain.CappedTransactionContext;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.repository.TerminologyRepositoryPlugin;

/**
 * {@link VersioningRequest} that will create a {@link CodeSystemVersionEntry} without modifying any of the available terminology components. 
 * {@link TerminologyRepositoryPlugin}s may extend the versioning functionality via the {@link TerminologyRepositoryPlugin#getVersioningRequestBuilder} method.
 * 
 * @since 7.0
 */
public class VersioningRequest implements Request<TransactionContext, Boolean>, AccessControl {

	private static final long serialVersionUID = 1L;
	private final VersioningConfiguration config;

	public VersioningRequest(VersioningConfiguration config) {
		this.config = config;
	}
	
	protected final VersioningConfiguration config() {
		return config;
	}
	
	@Override
	public final Boolean execute(TransactionContext context) {
		final Logger log = context.log();
		
		log.info("Versioning components of '{}' resource...", config.getResource());
		try {
			// capped context to commit versioned components in the configured low watermark bulks
			try (CappedTransactionContext versioningContext = CappedTransactionContext.create(context, context.getCommitLimit()).onCommit(this::onCommit)) {
				doVersionComponents(versioningContext);
			}
		} catch (Exception e) {
			if (e instanceof ApiException) {
				throw (ApiException) e;
			}
			throw new SnowowlRuntimeException(e);
		}
		return Boolean.TRUE;
	}

	/**
	 * Subclasses may override this method to update versioning properties on terminology components before creating the version. 
	 * @param context
	 * @throws Exception 
	 */
	protected void doVersionComponents(TransactionContext context) throws Exception {
	}
	
	/**
	 * Run additional logic when a successful versioning commit was made by this request.
	 * @param context
	 * @param commit
	 */
	protected void onCommit(TransactionContext context, Commit commit) {
	}
	
	protected final VersioningConfiguration getConfig() {
		return config;
	}

	@Override
	public String getOperation() {
		return Permission.OPERATION_EDIT;
	}
	
}