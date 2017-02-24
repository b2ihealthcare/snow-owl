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
import java.util.Collections;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.options.OptionsBuilder;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.google.common.base.Strings;

/**
 * @since 4.5
 */
public abstract class RevisionSearchRequestBuilder<B extends RevisionSearchRequestBuilder<B, R>, R> extends BaseRevisionResourceRequestBuilder<B, R> {

	private static final int MAX_LIMIT = Integer.MAX_VALUE - 1;
	
	private int offset = 0;
	private int limit = 50;
	private Collection<String> componentIds = Collections.emptyList();
	private final OptionsBuilder optionsBuilder = OptionsBuilder.newBuilder();
	
	protected RevisionSearchRequestBuilder() {
		super();
	}
	
	public final B setOffset(int offset) {
		this.offset = offset;
		return getSelf();
	}
	
	public final B setLimit(int limit) {
		this.limit = limit;
		return getSelf();
	}
	
	/**
	 * Sets the component IDs for the artefacts to be returned.
	 * @param componentIds
	 * @return RevisionSearchRequestBuilder
	 */
	public final B setComponentIds(Collection<String> componentIds) {
		this.componentIds = componentIds;
		return getSelf();
	}
	
	/**
	 * Sets the request to return the entire results set as a single 'page'.
	 * @return RevisionSearchRequestBuilder
	 */
	public final B all() {
		return setOffset(0).setLimit(MAX_LIMIT);
	}
	
	/**
	 * Returns a single hit from the result set.
	 * @return RevisionSearchRequestBuilder
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
	protected final BaseResourceRequest<BranchContext, R> create() {
		final RevisionSearchRequest<R> req = createSearch();
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
	
	protected abstract RevisionSearchRequest<R> createSearch();

}
