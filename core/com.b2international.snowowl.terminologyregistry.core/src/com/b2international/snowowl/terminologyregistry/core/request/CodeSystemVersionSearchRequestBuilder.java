/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.datastore.CodeSystemVersions;
import com.b2international.snowowl.datastore.request.SearchRequest;
import com.b2international.snowowl.datastore.request.SearchRequestBuilder;

/**
 * @since 4.7
 */
public final class CodeSystemVersionSearchRequestBuilder
		extends SearchRequestBuilder<CodeSystemVersionSearchRequestBuilder, CodeSystemVersions> {

	private String codeSystemShortName;
	private String versionId;

	CodeSystemVersionSearchRequestBuilder(final String repositoryId) {
		super(repositoryId);
	}

	public CodeSystemVersionSearchRequestBuilder setCodeSystemShortName(String codeSystemShortName) {
		this.codeSystemShortName = codeSystemShortName;
		return getSelf();
	}
	
	public CodeSystemVersionSearchRequestBuilder setVersionId(String versionId) {
		this.versionId = versionId;
		return getSelf();
	}

	@Override
	protected SearchRequest<CodeSystemVersions> createSearch() {
		final CodeSystemVersionSearchRequest req = new CodeSystemVersionSearchRequest();
		req.setCodeSystemShortName(codeSystemShortName);
		req.setVersionId(versionId);

		return req;
	}

}
