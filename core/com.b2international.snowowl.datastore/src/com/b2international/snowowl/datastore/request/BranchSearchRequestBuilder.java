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
package com.b2international.snowowl.datastore.request;

import java.util.Collection;

import com.b2international.snowowl.core.branch.Branches;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.google.common.collect.ImmutableSet;

/**
 * @since 4.5
 */
public final class BranchSearchRequestBuilder extends SearchResourceRequestBuilder<BranchSearchRequestBuilder, RepositoryContext, Branches> implements IndexRequestBuilder<Branches> {

	BranchSearchRequestBuilder() {
		super();
	}
	
	public BranchSearchRequestBuilder filterByParent(String parentPath) {
		return filterByParent(ImmutableSet.of(parentPath));
	}
	
	public BranchSearchRequestBuilder filterByParent(Collection<String> parentPaths) {
		return addOption(BranchSearchRequest.OptionKey.PARENT, parentPaths);
	}
	
	public BranchSearchRequestBuilder filterByName(String name) {
		return filterByName(ImmutableSet.of(name));
	}
	
	public BranchSearchRequestBuilder filterByName(Collection<String> names) {
		return addOption(BranchSearchRequest.OptionKey.NAME, names);
	}
	
	@Override
	protected SearchResourceRequest<RepositoryContext, Branches> createSearch() {
		return new BranchSearchRequest();
	}
	
}
