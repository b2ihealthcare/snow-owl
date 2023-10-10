/*
 * Copyright 2011-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Preconditions.checkState;

import java.util.Optional;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.StringUtils;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.PageableCollectionResource;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Iterables;

/**
 * Base class for requests that use an accompanying search request's "filter by ID"
 * functionality to retrieve a single object.
 * 
 * @param <SB> - the search request builder type
 * @param <C> - the request context type
 * @param <SR> - the search response type (a {@link PageableCollectionResource} that should have at most one item)
 * @param <R> - the response type
 *
 * @since 5.2
 */
public abstract class GetResourceRequest<SB extends SearchResourceRequestBuilder<SB, C, SR>, C extends ServiceProvider, SR, R> 
	extends IndexResourceRequest<C, R> {
	
	private static final long serialVersionUID = 1L;
	
	@NotEmpty
	@JsonProperty
	private final String id;
	
	protected GetResourceRequest(final String id) {
		this.id = id;
	}
	
	protected final String id() {
		return id;
	}
	
	/**
	 * Creates a new {@link SearchResourceRequestBuilder} to search for the resource by its identifier.
	 * @return
	 */
	protected abstract SB createSearchRequestBuilder();
	
	@Override
	public R execute(final C context) {
		C alteredContext = alterContextBeforeFetch(context);
		SR items = fetch(alteredContext, this.id);
		return extractFirst(items)
				.or(() -> fetchAlternative(alteredContext))
				.orElseThrow(() -> new NotFoundException(StringUtils.splitCamelCaseAndCapitalize(getReturnType().getSimpleName()), id));
	}

	/**
	 * Subclasses may optionally alter the context and attach values to it so that certain services can rely on those attached values later.
	 * 
	 * @param context
	 * @return
	 */
	protected C alterContextBeforeFetch(C context) {
		return context;
	}

	/**
	 * For reusability subclasses may perform the same search with a different ID if they wish to perform additional fetches when the primary resource couldn't be found.
	 * 
	 * @param context
	 * @param identifier
	 * @return
	 */
	protected final SR fetch(final C context, final String identifier) {
		return createSearchRequestBuilder()
			.setLimit(2)
			.setFields(fields())
			.setLocales(locales())
			.setExpand(expand())
			.filterById(identifier)
			.build()
			.execute(context);
	}

	/**
	 * Subclasses may opt-in for an additional fall back logic when the original request couldn't find any actual documents in the store. Usually
	 * plug-ins that require alternate identifiers or special ID parts to be handled along with the original identifiers can leverage this method.
	 * 
	 * @param context
	 * @return
	 */
	protected Optional<R> fetchAlternative(C context) {
		return Optional.empty();
	}

	/**
	 * Extracts the first item from the search results and returns it. 
	 * 
	 * @param context
	 * @param items
	 * @return an {@link Optional} representing the first item of the search results or absent {@link Optional} value if no matches can be found.
	 * @throws IllegalStateException - if more than one document is found by the system, which should never happen and should be immediately reported as critical error
	 */
	protected Optional<R> extractFirst(SR items) {
		if (items instanceof Iterable<?>) {
			Iterable<R> iterable = (Iterable<R>) items;
			checkState(Iterables.size(iterable) <= 1, "Multiple documents found for '%s'.", id);
			return Optional.ofNullable(Iterables.getFirst(iterable, null));
		} else {
			throw new UnsupportedOperationException(String.format("Unrecognized collection-like type '%s', specific handling is required.", items.getClass()));
		}
	}
	
}
