/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.version;

import java.util.Date;

import javax.annotation.Nullable;

import org.slf4j.Logger;

import com.b2international.commons.exceptions.AlreadyExistsException;
import com.b2international.commons.exceptions.ApiException;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.CodeSystemVersionEntry;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;

/**
 * {@link VersioningRequest} that will create a 
 * 
 * @since 7.0
 */
public class VersioningRequest implements Request<TransactionContext, Boolean> {

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
		
		CodeSystemVersionEntry version = getVersion(context);
		if (version != null) {
			throw new AlreadyExistsException("Version", config.getVersionId());
		}

		log.info("Versioning components of '{}' codesystem...", config.getCodeSystemShortName());
		try {
			doVersionComponents(context);
			context.add(createVersion(context, config));
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

	@Nullable
	private CodeSystemVersionEntry getVersion(TransactionContext context) {
		return CodeSystemRequests
				.prepareSearchCodeSystemVersion()
				.setLimit(2)
				.filterByCodeSystemShortName(config.getCodeSystemShortName())
				.filterByVersionId(config.getVersionId())
				.build()
				.execute(context)
				.first()
				.orElse(null);
	}
	
	private final CodeSystemVersionEntry createVersion(final TransactionContext context, final VersioningConfiguration config) {
		return CodeSystemVersionEntry.builder()
			.description(config.getDescription())
			.effectiveDate(config.getEffectiveTime().getTime())
			.importDate(new Date().getTime())
			.parentBranchPath(context.branchPath())
			.versionId(config.getVersionId())
			.codeSystemShortName(config.getCodeSystemShortName())
			.repositoryUuid(context.id())
			.build();
	}

}