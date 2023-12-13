/*
 * Copyright 2011-2023 B2i Healthcare, https://b2ihealthcare.com
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

import java.util.SortedSet;

import com.b2international.commons.options.Metadata;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.RepositoryRequestBuilder;
import com.google.common.collect.ImmutableSortedSet;

/**
 * @since 5.0
 */
public final class BranchUpdateRequestBuilder extends BaseRequestBuilder<BranchUpdateRequestBuilder, RepositoryContext, Boolean> implements RepositoryRequestBuilder<Boolean> {

	private final String branchPath;
	private Metadata metadata;
	private SortedSet<String> nameAliases;

	BranchUpdateRequestBuilder(String branchPath) {
		this.branchPath = branchPath;
	}
	
	/**
	 * Update (override) the current {@link Metadata} with the specified {@link Metadata}. To clear metadata, specify an empty {@link Metadata} object.
	 * @param metadata
	 * @return
	 */
	public BranchUpdateRequestBuilder setMetadata(Metadata metadata) {
		this.metadata = metadata;
		return getSelf();
	}
	
	/**
	 * Update (override) the current set of name aliases assigned to the selected branch. To clear name aliases, specify an empty collection.
	 * @param nameAliases
	 * @return
	 */
	public BranchUpdateRequestBuilder setNameAliases(Iterable<String> nameAliases) {
		this.nameAliases = nameAliases == null ? null : ImmutableSortedSet.copyOf(nameAliases);
		return getSelf();
	}
	
	@Override
	protected Request<RepositoryContext, Boolean> doBuild() {
		final BranchUpdateRequest req = new BranchUpdateRequest(branchPath);
		req.setMetadata(metadata);
		req.setNameAliases(nameAliases);
		return req;
	}

}
