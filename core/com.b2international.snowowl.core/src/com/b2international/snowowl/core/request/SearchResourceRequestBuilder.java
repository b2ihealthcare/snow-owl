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
package com.b2international.snowowl.core.request;

import java.util.Collection;
import java.util.List;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.options.OptionsBuilder;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.request.SearchResourceRequest.OptionKey;
import com.b2international.snowowl.core.request.SearchResourceRequest.SortField;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

/**
 * @since 5.2
 */
public abstract class SearchResourceRequestBuilder<B extends SearchResourceRequestBuilder<B, C, R>, C extends ServiceProvider, R> extends ResourceRequestBuilder<B, C, R> {
	
	private static final int MAX_LIMIT = Integer.MAX_VALUE - 1;
	
	private int offset = 0;
	private int limit = 50;
	
	private final OptionsBuilder optionsBuilder = OptionsBuilder.newBuilder();
	
	protected SearchResourceRequestBuilder() {
		super();
	}
	
	/**
	 * Sets the offset for the paging of the result set. 
	 * @param offset for paging the result set returned
	 * @return this builder instance
	 */
	public final B setOffset(int offset) {
		this.offset = offset;
		return getSelf();
	}
	
	/**
	 * Sets the limit of the result set returned
	 * @param limit of the result set
	 * @return this builder instance
	 */
	public final B setLimit(int limit) {
		this.limit = limit;
		return getSelf();
	}
	
	/**
	 * Filter by resource identifiers.
	 * @param id - a single identifier to match
	 * @return this builder instance
	 */
	public final B filterById(String id) {
		return filterByIds(ImmutableSet.of(id));
	}
	
	/**
	 * Filter by resource identifiers.
	 * @param ids - a {@link Collection} of identifiers to match
	 * @return this builder instance
	 */
	public final B filterByIds(Collection<String> ids) {
		for (final String id : ids) {
			if (Strings.isNullOrEmpty(id)) {
				throw new BadRequestException("ID filter cannot contain empty values");
			}
		}

		addOption(OptionKey.COMPONENT_IDS, ImmutableSet.copyOf(ids));
		return getSelf();
	}
	
	/**
	 * Sorts the result set by the given sort fields.
	 * 
	 * @param first - the first sort field
	 * @param rest - any remaining sort fields (optional)
	 * @return this builder instance
	 */
	public final B sortBy(SortField first, SortField... rest) {
		return sortBy(Lists.asList(first, rest));
	}

	/**
	 * Sorts the result set by the given sort fields.
	 * 
	 * @param sortFields - the list of fields to sort by, in order
	 * @return this builder instance
	 */
	public final B sortBy(List<SortField> sortFields) {
		addOption(OptionKey.SORT_BY, ImmutableList.copyOf(sortFields));
		return getSelf();
	}
	
	/**
	 * Sets the request to return the entire results set as a single 'page'.
	 * @return this builder instance
	 */
	public final B all() {
		return setOffset(0).setLimit(MAX_LIMIT);
	}
	
	/**
	 * Returns a single hit from the result set.
	 * @return this builder instance
	 */
	public final B one() {
		return setOffset(0).setLimit(1);
	}
	
	// XXX: Does not allow empty-ish values
	protected final B addOption(String key, Object value) {
		if (!CompareUtils.isEmpty(value)) {
			optionsBuilder.put(key, value);
		}
		return getSelf();
	}
	
	protected final B addOption(Enum<?> key, Object value) {
		return addOption(key.name(), value);
	}
	
	@Override
	protected ResourceRequest<C, R> create() {
		final SearchResourceRequest<C, R> req = createSearch();
		req.setOffset(offset);
		req.setLimit(Math.min(limit, MAX_LIMIT - offset));
		req.setOptions(optionsBuilder.build());
		return req;
	}
	
	protected abstract SearchResourceRequest<C, R> createSearch();
}
