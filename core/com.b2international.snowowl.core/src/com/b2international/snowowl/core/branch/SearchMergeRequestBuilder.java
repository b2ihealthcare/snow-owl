/*
 * Copyright 2019-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.branch;

import com.b2international.snowowl.core.branch.SearchMergeRequest.OptionKey;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.merge.Merges;
import com.b2international.snowowl.core.request.RepositoryRequestBuilder;
import com.b2international.snowowl.core.request.SearchPageableCollectionResourceRequestBuilder;
import com.b2international.snowowl.core.request.SearchResourceRequest;

/**
 * @since 7.1 
 */
public final class SearchMergeRequestBuilder 
		extends SearchPageableCollectionResourceRequestBuilder<SearchMergeRequestBuilder, RepositoryContext, Merges> 
		implements RepositoryRequestBuilder<Merges>  {

	SearchMergeRequestBuilder() {}
	
	public SearchMergeRequestBuilder filterBySource(String source) {
		return addOption(OptionKey.SOURCE, source);
	}
	
	public SearchMergeRequestBuilder filterByTarget(String target) {
		return addOption(OptionKey.TARGET, target);
	}
	
	public SearchMergeRequestBuilder filterByStatus(String status) {
		return addOption(OptionKey.STATUS, status);
	}

	@Override
	protected SearchResourceRequest<RepositoryContext, Merges> createSearch() {
		return new SearchMergeRequest();
	}

}
