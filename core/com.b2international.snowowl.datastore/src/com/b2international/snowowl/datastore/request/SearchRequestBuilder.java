/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Collections;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.options.OptionsBuilder;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.google.common.base.Strings;

/**
 * Abstract superclass for building search requests that can return a page-able result set.
 * @since 4.5
 */
public abstract class SearchRequestBuilder<B extends SearchRequestBuilder<B, R>, R> extends BaseResourceRequestBuilder<B, R> {

	public static final int DEFAULT_OFFSET = 0;
	public static final int DEFAULT_LIMIT = 50;
	private static final int MAX_LIMIT = Integer.MAX_VALUE - 1;
	
	private int offset = DEFAULT_OFFSET;
	private int limit = DEFAULT_LIMIT;
	private Collection<String> componentIds = Collections.emptyList();
	private final OptionsBuilder optionsBuilder = OptionsBuilder.newBuilder();
	
	protected SearchRequestBuilder(String repositoryId) {
		super(repositoryId);
	}
	
	/**
	 * Sets the offset for the paging of the result set. 
	 * @param offset for paging the result set returned
	 * @return SearchRequestBuilder
	 */
	public final B setOffset(int offset) {
		this.offset = offset;
		return getSelf();
	}
	
	/**
	 * Sets the limit of the result set returned
	 * @param limit of the result set
	 * @return SearchRequestBuilder
	 */
	public final B setLimit(int limit) {
		this.limit = limit;
		return getSelf();
	}
	
	public final B setComponentIds(Collection<String> componentIds) {
		this.componentIds = componentIds;
		return getSelf();
	}
	
	/**
	 * Sets the request to return the entire results set as a single 'page'.
	 * All results are returned.
	 * @return RevisionSearchRequestBuilder
	 */
	public final B all() {
		return setOffset(DEFAULT_OFFSET).setLimit(MAX_LIMIT);
	}
	
	/**
	 * Returns a single hit from the result set.
	 * @return RevisionSearchRequestBuilder
	 */
	public final B one() {
		return setOffset(DEFAULT_OFFSET).setLimit(1);
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
	protected final BaseResourceRequest<BranchContext, R> create() {
		final SearchRequest<R> req = createSearch();
		req.setOffset(offset);
		req.setLimit(Math.min(limit, MAX_LIMIT - offset));
		// validate componentIds, do NOT allow null or empty strings
		for (String componentId : componentIds) {
			if (Strings.isNullOrEmpty(componentId)) {
				throw new BadRequestException("Component ID filter cannot contain empty values");
			}
		}
		req.setComponentIds(componentIds);
		req.setOptions(optionsBuilder.build());
		return req;
	}
	
	protected abstract SearchRequest<R> createSearch();

}
