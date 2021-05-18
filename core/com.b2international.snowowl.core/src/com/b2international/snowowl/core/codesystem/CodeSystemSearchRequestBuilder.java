/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.codesystem.CodeSystemSearchRequest.OptionKey;
import com.b2international.snowowl.core.context.ResourceRepositoryRequestBuilder;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.SearchResourceRequestBuilder;

/**
 * @since 4.7
 */
public final class CodeSystemSearchRequestBuilder 
		extends SearchResourceRequestBuilder<CodeSystemSearchRequestBuilder, RepositoryContext, CodeSystems>
		implements ResourceRepositoryRequestBuilder<CodeSystems> {

	CodeSystemSearchRequestBuilder() {
		super();
	}
	
	public CodeSystemSearchRequestBuilder filterByToolingId(String toolingId) {
		return addOption(OptionKey.TOOLING_ID, toolingId);
	}

	public CodeSystemSearchRequestBuilder filterByToolingIds(Iterable<String> toolingIds) {
		return addOption(OptionKey.TOOLING_ID, toolingIds);
	}
	
	public CodeSystemSearchRequestBuilder filterByTitle(String term) {
		return addOption(OptionKey.TITLE, term);
	}
	
	public CodeSystemSearchRequestBuilder filterByTitleExact(String term) {
		return addOption(OptionKey.TITLE_EXACT, term);
	}
	
	public CodeSystemSearchRequestBuilder filterByTitleExact(Iterable<String> titles) {
		return addOption(OptionKey.TITLE_EXACT, titles);
	}
	
	public CodeSystemSearchRequestBuilder filterByOid(String oid) {
		return addOption(OptionKey.OID, oid);
	}

	public CodeSystemSearchRequestBuilder filterByOids(Iterable<String> oids) {
		return addOption(OptionKey.OID, oids);
	}
	
	public CodeSystemSearchRequestBuilder filterByUpgradeOf(ResourceURI upgradeOf) {
		return addOption(OptionKey.UPGRADE_OF, upgradeOf);
	}
	
	public CodeSystemSearchRequestBuilder filterByUpgradeOf(Iterable<ResourceURI> upgradeOfs) {
		return addOption(OptionKey.UPGRADE_OF, upgradeOfs);
	}

	@Override
	protected SearchResourceRequest<RepositoryContext, CodeSystems> createSearch() {
		return new CodeSystemSearchRequest();
	}

}
