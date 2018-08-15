/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Set;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.options.OptionsBuilder;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.PageableCollectionResource;
import com.b2international.snowowl.core.request.SearchResourceRequest.OptionKey;
import com.b2international.snowowl.core.request.SearchResourceRequest.Sort;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

/**
 * @since 5.2
 */
public abstract class SearchResourceRequestBuilder<B extends SearchResourceRequestBuilder<B, C, R>, C extends ServiceProvider, R> extends ResourceRequestBuilder<B, C, R> {
	
	private static final int MAX_LIMIT = Integer.MAX_VALUE - 1;
	
	private Set<String> componentIds;
	private String scrollKeepAlive;
	private String scrollId;
	private String searchAfter;
	
	private int limit = 50;
	
	private final OptionsBuilder optionsBuilder = OptionsBuilder.newBuilder();
	
	protected SearchResourceRequestBuilder() {
		super();
	}
	
	/**
	 * Sets the "search after" parameter to the specified array of scroll values.
	 * @param searchAfter
	 * @return
	 * @see PageableCollectionResource#getSearchAfter()
	 */
	public B setSearchAfter(String searchAfter) {
		this.searchAfter = searchAfter;
		return getSelf();
	}
	
	/**
	 * Sets the scroll keep alive value to the specified value to start a scroll based on the query of this request. 
	 * @param scrollKeepAlive
	 * @return this builder instance
	 */
	public final B setScroll(String scrollKeepAlive) {
		this.scrollKeepAlive = scrollKeepAlive;
		return getSelf();
	}

	/*
	 * Used by SearchResourceRequestIterator
	 */
	final String getScrollKeepAlive() {
		return scrollKeepAlive;
	}
	
	/**
	 * Sets the scroll Id to continue a previously started scroll.
	 * @param scrollId
	 * @return
	 * @see PageableCollectionResource#getScrollId()
	 */
	public final B setScrollId(String scrollId) {
		this.scrollId = scrollId;
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
		if (ids != null) {
			this.componentIds = ImmutableSet.copyOf(ids);
		}
		return getSelf();
	}
	
	/**
	 * Sorts the result set by the given sort descriptor.
	 * <p>
	 * The format is <code>field1,field2:asc,field3:desc</code>.
	 * 
	 * @param sortBy - the sort descriptor
	 * @return this builder instance
	 */
	public final B sortBy(String sortBy) {
		return sortBy(SortParser.parse(sortBy));
	}
	
	/**
	 * Sorts the result set by the given sort fields.
	 * 
	 * @param first - the first sort field
	 * @param rest - any remaining sort fields (optional)
	 * @return this builder instance
	 */
	public final B sortBy(Sort first, Sort... rest) {
		return sortBy(Lists.asList(first, rest));
	}

	/**
	 * Sorts the result set by the given sort fields.
	 * 
	 * @param sortFields - the list of fields to sort by, in order
	 * @return this builder instance
	 */
	public final B sortBy(List<Sort> sorts) {
		addOption(OptionKey.SORT_BY, ImmutableList.copyOf(sorts));
		return getSelf();
	}
	
	/**
	 * Sets the request to return the entire results set as a single 'page'.
	 * @return this builder instance
	 */
	public final B all() {
		return setScroll(null).setLimit(MAX_LIMIT);
	}
	
	/**
	 * Returns a single hit from the result set.
	 * @return this builder instance
	 */
	public final B one() {
		return setScroll(null).setLimit(1);
	}
	
	// XXX: Does not allow null values or collections with null values
	protected final B addOption(String key, Object value) {
		if (value instanceof Iterable<?>) {
			for (final Object val : (Iterable<?>)value) {
				if (val instanceof SearchResourceRequest.Sort) {
					// ignore sort fields
				} else if (val == null) {
					throw new BadRequestException("%s filter cannot contain null values", key);
				}
			}
			optionsBuilder.put(key, value);
		} else if (value != null) {
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
		req.setComponentIds(componentIds);
		req.setScrollId(scrollId);
		req.setScrollKeepAlive(scrollKeepAlive);
		req.setSearchAfter(searchAfter);
		req.setLimit(Math.min(limit, MAX_LIMIT));
		req.setOptions(optionsBuilder.build());
		return req;
	}
	
	protected abstract SearchResourceRequest<C, R> createSearch();
}
