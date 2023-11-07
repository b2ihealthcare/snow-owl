/*
 * Copyright 2011-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collection;

import com.b2international.snowowl.core.branch.BranchSearchRequest.OptionKey;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.RepositoryRequestBuilder;
import com.b2international.snowowl.core.request.SearchPageableCollectionResourceRequestBuilder;
import com.b2international.snowowl.core.request.SearchResourceRequest;

/**
 * @since 4.5
 */
public final class BranchSearchRequestBuilder extends SearchPageableCollectionResourceRequestBuilder<BranchSearchRequestBuilder, RepositoryContext, Branches> implements RepositoryRequestBuilder<Branches> {

	BranchSearchRequestBuilder() {
		super();
	}
	
	public BranchSearchRequestBuilder filterByParent(String parentPath) {
		return addOption(OptionKey.PARENT, parentPath);
	}
	
	public BranchSearchRequestBuilder filterByParent(Collection<String> parentPaths) {
		return addOption(OptionKey.PARENT, parentPaths);
	}
	
	public BranchSearchRequestBuilder filterByName(String name) {
		return addOption(OptionKey.NAME, name);
	}
	
	public BranchSearchRequestBuilder filterByName(Collection<String> names) {
		return addOption(OptionKey.NAME, names);
	}
	
	public BranchSearchRequestBuilder filterByBranchId(Long branchId) {
		return addOption(OptionKey.BRANCH_ID, branchId);
	}
	
	public BranchSearchRequestBuilder filterByBranchId(Collection<Long> branchIds) {
		return addOption(OptionKey.BRANCH_ID, branchIds);
	}
	
	public BranchSearchRequestBuilder filterByMetadata(Iterable<String> metadataFilters) {
		return addOption(OptionKey.METADATA, metadataFilters);
	}
	
	public BranchSearchRequestBuilder filterByMetadata(String metadataFilter) {
		return addOption(OptionKey.METADATA, metadataFilter);
	}

	@Override
	protected SearchResourceRequest<RepositoryContext, Branches> createSearch() {
		return new BranchSearchRequest();
	}
	
}
