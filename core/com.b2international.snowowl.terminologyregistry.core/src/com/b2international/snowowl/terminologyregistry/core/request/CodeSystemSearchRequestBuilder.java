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

import com.b2international.snowowl.datastore.CodeSystems;
import com.b2international.snowowl.datastore.request.SearchRequest;
import com.b2international.snowowl.datastore.request.SearchRequestBuilder;

/**
 * @since 4.7
 */
public final class CodeSystemSearchRequestBuilder extends SearchRequestBuilder<CodeSystemSearchRequestBuilder, CodeSystems> {

	private String shortName;
	private String oid;

	CodeSystemSearchRequestBuilder(String repositoryId) {
		super(repositoryId);
	}

	public CodeSystemSearchRequestBuilder setShortName(final String shortName) {
		this.shortName = shortName;
		return this.getSelf();
	}

	public CodeSystemSearchRequestBuilder setOid(final String oid) {
		this.oid = oid;
		return this.getSelf();
	}

	@Override
	protected SearchRequest<CodeSystems> createSearch() {
		final CodeSystemSearchRequest req = new CodeSystemSearchRequest();
		req.setShortName(shortName);
		req.setOid(oid);

		return req;
	}

}
