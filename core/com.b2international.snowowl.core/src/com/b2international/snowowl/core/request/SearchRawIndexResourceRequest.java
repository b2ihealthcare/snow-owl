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

import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
import com.b2international.snowowl.core.ServiceProvider;

/**
 * @since 7.11
 */
public final class SearchRawIndexResourceRequest<C extends ServiceProvider, D> extends SearchIndexResourceRequest<C, Hits<D>, D> {

	private final Class<D> select;
	private final SearchIndexResourceRequest<C, ?, ?> original;

	public SearchRawIndexResourceRequest(SearchIndexResourceRequest<C, ?, ?> original, Class<D> select) {
		this.original = original;
		this.select = select;
	}
	
	@Override
	protected Expression prepareQuery(C context) {
		return original.prepareQuery(context);
	}

	@Override
	protected Class<D> getSelect() {
		return select;
	}
	
	@Override
	protected Class<?> getFrom() {
		return original.getFrom();
	}

	@Override
	protected Hits<D> toCollectionResource(C context, Hits<D> hits) {
		return hits;
	}

	@Override
	protected Hits<D> createEmptyResult(int limit) {
		return Hits.empty(limit);
	}

}
