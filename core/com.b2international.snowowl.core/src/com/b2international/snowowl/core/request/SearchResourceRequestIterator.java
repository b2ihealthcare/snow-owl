/*
 * Copyright 2018-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.function.Function;

import com.b2international.snowowl.core.domain.PageableCollectionResource;
import com.google.common.collect.AbstractIterator;

/**
 * @since 6.4
 * 
 * @param <B> the search resource request builder type; an instance of this will be configured for paging
 * @param <R> the collection resource response type; the iterator will return results as instances of this type
 */
public final class SearchResourceRequestIterator<
		B extends SearchResourceRequestBuilder<B, ?, R>, 
		R extends PageableCollectionResource<?>> extends AbstractIterator<R> {
	
	private final B searchRequestBuilder;
	private final Function<B, R> executeHandler;

	private boolean firstRun = true;
	private String searchAfter;
	private int visited;
	private int total;
	
	/**
	 * @param searchRequestBuilder
	 *            the pre-configured request builder (should have a batch limit set)
	 * @param executeHandler
	 *            a function that builds an appropriate request, executes it either
	 *            through a request context or an event bus, and returns the results
	 */
	public SearchResourceRequestIterator(B searchRequestBuilder, Function<B, R> executeHandler) {
		this.searchRequestBuilder = searchRequestBuilder;
		this.executeHandler = executeHandler;
	}
	
	@Override
	protected R computeNext() {
		// If it is not the first time we run the request, and all items have already been collected, exit
		if (!firstRun && visited >= total) {
			return endOfData();
		}
		
		// Execute the request with the last recorded searchAfter value (can be null on first run) 
		searchRequestBuilder.setSearchAfter(searchAfter);
		R hits = executeHandler.apply(searchRequestBuilder);

		// Initialize total counter on first run
		if (firstRun) {
			firstRun = false;
			total = hits.getTotal();
			
			if (total < 1) {
				return endOfData();
			}
		}

		// Update searchAfter and visited counter
		searchAfter = hits.getSearchAfter();
		visited += hits.getItems().size();

		return hits;
	}
}
