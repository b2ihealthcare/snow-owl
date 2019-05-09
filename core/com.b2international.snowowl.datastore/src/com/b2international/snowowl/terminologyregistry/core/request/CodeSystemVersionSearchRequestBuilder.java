/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.terminologyregistry.core.request;

import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.SearchResourceRequestBuilder;
import com.b2international.snowowl.datastore.CodeSystemVersions;
import com.b2international.snowowl.datastore.request.RepositoryIndexRequestBuilder;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemVersionSearchRequest.OptionKey;

/**
 * @since 4.7
 */
public final class CodeSystemVersionSearchRequestBuilder 
		extends SearchResourceRequestBuilder<CodeSystemVersionSearchRequestBuilder, RepositoryContext, CodeSystemVersions>
 		implements RepositoryIndexRequestBuilder<CodeSystemVersions> {

	private String codeSystemShortName;
	private String versionId;

	CodeSystemVersionSearchRequestBuilder() {
		super();
	}

	public CodeSystemVersionSearchRequestBuilder filterByCodeSystemShortName(String codeSystemShortName) {
		this.codeSystemShortName = codeSystemShortName;
		return getSelf();
	}
	
	/**
	 * Filter versions by their version ID.
	 * @param versionId - the versionId to look for.
	 * @return
	 */
	public CodeSystemVersionSearchRequestBuilder filterByVersionId(String versionId) {
		this.versionId = versionId;
		return getSelf();
	}

	/**
	 * Filter versions by created at (formerly import date) using the specified range.
	 * @param fromCreatedAt - the lower bound of the created at date range
	 * @param toCreatedAt - the upper bound of the created at date range
	 * @return
	 */
	public CodeSystemVersionSearchRequestBuilder filterByCreatedAt(final long fromCreatedAt, final long toCreatedAt) {
		return addOption(OptionKey.CREATED_AT_START, fromCreatedAt).addOption(OptionKey.CREATED_AT_END, toCreatedAt);
	}
	
	/**
	 * Filter versions by created at date (formerly import date) using the specified value.
	 * @param createdAt the exact created at date to match for
	 * @return
	 */
	public CodeSystemVersionSearchRequestBuilder filterByCreatedAt(final long createdAt) {
		return filterByCreatedAt(createdAt, createdAt);
	}

	@Override
	protected SearchResourceRequest<RepositoryContext, CodeSystemVersions> createSearch() {
		final CodeSystemVersionSearchRequest req = new CodeSystemVersionSearchRequest();
		req.setCodeSystemShortName(codeSystemShortName);
		req.setVersionId(versionId);
		return req;
	}

}
