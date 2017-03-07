/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.datastore.CodeSystemVersions;
import com.b2international.snowowl.datastore.request.IndexRequestBuilder;
import com.b2international.snowowl.datastore.request.SearchResourceRequest;
import com.b2international.snowowl.datastore.request.SearchResourceRequestBuilder;

/**
 * @since 4.7
 */
public final class CodeSystemVersionSearchRequestBuilder 
		extends SearchResourceRequestBuilder<CodeSystemVersionSearchRequestBuilder, RepositoryContext, CodeSystemVersions>
 		implements IndexRequestBuilder<CodeSystemVersions> {

	private String codeSystemShortName;
	private String versionId;

	CodeSystemVersionSearchRequestBuilder() {
		super();
	}

	public CodeSystemVersionSearchRequestBuilder filterByCodeSystemShortName(String codeSystemShortName) {
		this.codeSystemShortName = codeSystemShortName;
		return getSelf();
	}
	
	public CodeSystemVersionSearchRequestBuilder filterByVersionId(String versionId) {
		this.versionId = versionId;
		return getSelf();
	}

	@Override
	protected SearchResourceRequest<RepositoryContext, CodeSystemVersions> createSearch() {
		final CodeSystemVersionSearchRequest req = new CodeSystemVersionSearchRequest();
		req.setCodeSystemShortName(codeSystemShortName);
		req.setVersionId(versionId);
		return req;
	}

}
