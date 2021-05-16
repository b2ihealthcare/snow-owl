/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request;

import com.b2international.snowowl.core.Resources;
import com.b2international.snowowl.core.context.ResourceRepositoryRequestBuilder;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.ResourceSearchRequest.OptionKey;

/**
 * @since 8.0
 */
public final class ResourceSearchRequestBuilder 
		extends SearchResourceRequestBuilder<ResourceSearchRequestBuilder, RepositoryContext, Resources>
		implements ResourceRepositoryRequestBuilder<Resources> {

	/**
	 * Filters matches by the given resource type.
	 * 
	 * @param resourceType
	 * @return this builder
	 */
	public ResourceSearchRequestBuilder filterByResourceType(String resourceType) {
		return addOption(OptionKey.RESOURCE_TYPE, resourceType);
	}
	
	/**
	 * Filters matches to have at least one of the specified resource types.
	 * 
	 * @param resourceTypes - the resource types to match
	 * @return this builder
	 */
	public ResourceSearchRequestBuilder filterByResourceType(Iterable<String> resourceTypes) {
		return addOption(OptionKey.RESOURCE_TYPE, resourceTypes);
	}
	
	/**
	 * Filters matches by their title (exact match).
	 * 
	 * @param title - the single to match
	 * @return this builder
	 */
	public ResourceSearchRequestBuilder filterByTitleExact(String title) {
		return addOption(OptionKey.TITLE_EXACT, title);
	}
	
	/**
	 * Filters matches by their title (exact match).
	 * 
	 * @param titles - at least one of these titles match
	 * @return this builder
	 */
	public ResourceSearchRequestBuilder filterByTitleExact(Iterable<String> titles) {
		return addOption(OptionKey.TITLE_EXACT, titles);
	}
	
	public ResourceSearchRequestBuilder filterByToolingId(String toolingId) {
		return addOption(OptionKey.TOOLING_ID, toolingId);
	}
	
	public ResourceSearchRequestBuilder filterByToolingIds(Iterable<String> toolingIds) {
		return addOption(OptionKey.TOOLING_ID, toolingIds);
	}
	
	public ResourceSearchRequestBuilder filterByBranch(String branchPath) {
		return addOption(OptionKey.BRANCH, branchPath);
	}
	
	public ResourceSearchRequestBuilder filterByBranches(Iterable<String> branchPaths) {
		return addOption(OptionKey.BRANCH, branchPaths);
	}
	
	@Override
	protected SearchResourceRequest<RepositoryContext, Resources> createSearch() {
		return new ResourceSearchRequest();
	}

}
