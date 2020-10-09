/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.commons.ClassUtils;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.PageableCollectionResource;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 7.11
 */
public final class SearchRawIndexResourceRequestBuilder<C extends ServiceProvider, D> 
		extends SearchResourceRequestBuilder<SearchRawIndexResourceRequestBuilder<C, D>, C, PageableCollectionResource<D>> {

	private final SearchResourceRequestBuilder<?, C, ?> original;
	private final Class<D> select;

	public SearchRawIndexResourceRequestBuilder(SearchResourceRequestBuilder<?, C, ?> original, Class<D> select) {
		this.original = original;
		this.select = select;
	}
	
	@Override
	protected SearchResourceRequest<C, PageableCollectionResource<D>> createSearch() {
		final Request<C, ?> originalSearch = original.build();
		// prepare this builder with all info from original search
		final SearchIndexResourceRequest<C, ?, ?> req = ClassUtils.checkAndCast(originalSearch, SearchIndexResourceRequest.class);
		filterByIds(req.componentIds());
		setLimit(req.limit());
		setSearchAfter(req.searchAfter());
		setExpand(req.expand());
		setLocales(req.locales());
		setFields(req.fields());
		sortBy(req.sortBy());
		return new SearchRawIndexResourceRequest<C, D>(req, select);
	}
	
}
