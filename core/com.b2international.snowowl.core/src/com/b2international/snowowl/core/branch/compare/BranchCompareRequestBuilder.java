/*
 * Copyright 2017-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.branch.compare;

import java.util.Set;

import com.b2international.commons.collections.Collections3;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.RepositoryRequestBuilder;

/**
 * @since 5.9
 */
public final class BranchCompareRequestBuilder extends BaseRequestBuilder<BranchCompareRequestBuilder, RepositoryContext, BranchCompareResult>
		implements RepositoryRequestBuilder<BranchCompareResult> {

	private String base;
	private String compare;
	
	// options
	private Set<String> types;
	private Set<String> ids;
	private int limit = Integer.MAX_VALUE;
	private boolean includeComponentChanges = false;
	private boolean includeDerivedComponentChanges = false;
	private Set<String> statsFor;
	
	public BranchCompareRequestBuilder setBase(String baseBranch) {
		this.base = baseBranch;
		return getSelf();
	}
	
	public BranchCompareRequestBuilder setCompare(String compareBranch) {
		this.compare = compareBranch;
		return getSelf();
	}
	
	public BranchCompareRequestBuilder filterByType(String type) {
		return filterByTypes(type == null ? null : Set.of(type));
	}
	
	public BranchCompareRequestBuilder filterByTypes(Iterable<String> types) {
		this.types = types == null ? null : Collections3.toImmutableSet(types);
		return getSelf();
	}
	
	public BranchCompareRequestBuilder filterById(String id) {
		return filterByIds(id == null ? null : Set.of(id));
	}
	
	public BranchCompareRequestBuilder filterByIds(Iterable<String> ids) {
		this.ids = ids == null ? null : Collections3.toImmutableSet(ids);
		return getSelf();
	}
	
	public BranchCompareRequestBuilder setIncludeComponentChanges(boolean includeComponentChanges) {
		this.includeComponentChanges = includeComponentChanges;
		return getSelf();
	}
	
	public BranchCompareRequestBuilder setIncludeDerivedComponentChanges(boolean includeDerivedComponentChanges) {
		this.includeDerivedComponentChanges = includeDerivedComponentChanges;
		return getSelf();
	}
	
	public BranchCompareRequestBuilder setLimit(int limit) {
		this.limit = limit;
		return getSelf();
	}
	
	public BranchCompareRequestBuilder withChangeStatsFor(String...properties) {
		return withChangeStatsFor(properties == null ? null : Set.of(properties));
	}
	
	public BranchCompareRequestBuilder withChangeStatsFor(Iterable<String> properties) {
		this.statsFor = properties == null ? null : Collections3.toImmutableSet(properties);
		return getSelf();
	}
	
	@Override
	protected Request<RepositoryContext, BranchCompareResult> doBuild() {
		final BranchCompareRequest req = new BranchCompareRequest();
		req.setBaseBranch(base);
		req.setCompareBranch(compare);
		req.setLimit(limit);
		req.setIncludeComponentChanges(includeComponentChanges);
		req.setIncludeDerivedComponentChanges(includeDerivedComponentChanges);
		req.setTypes(types);
		req.setIds(ids);
		req.setStatsFor(statsFor);
		return req;
	}

}
