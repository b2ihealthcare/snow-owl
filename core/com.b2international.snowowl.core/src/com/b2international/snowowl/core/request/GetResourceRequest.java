/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Iterables;

/**
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
	
	/**
	 * Creates a new {@link SearchResourceRequestBuilder} to search for the resource by its identifier.
	 * @return
	 */
	protected abstract SB createSearchRequestBuilder();
	
	@Override
	public R execute(final C context) {
		SR items = createSearchRequestBuilder()
			.setLimit(2)
			.setFields(fields())
			.setLocales(locales())
			.setExpand(expand())
			.filterById(id)
			.build()
			.execute(context);
		return extractFirst(items) 
			.orElseThrow(() -> new NotFoundException(StringUtils.splitCamelCaseAndCapitalize(getReturnType().getSimpleName()), id));
	}

	/**
	 * Extract the first item from the search results and returns it.
	 * 
	 * @param items
	 * @return an {@link Optional} representing the first item of the search results or absent {@link Optional} value if no matches can be found.
	 * @throws IllegalStateException - if more than 
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
