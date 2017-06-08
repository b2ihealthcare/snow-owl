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
package com.b2international.snowowl.snomed.datastore.converter;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.commons.options.OptionsBuilder;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.CollectionResource;
import com.b2international.snowowl.datastore.request.SearchRequestBuilder;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedIndexEntry;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

/**
 * @since 4.0
 * @param <T>
 * @param <R>
 * @param <CR>
 */
abstract class BaseSnomedComponentConverter<T extends SnomedIndexEntry, R extends SnomedComponent, CR extends CollectionResource<R>>
		implements ResourceConverter<T, R, CR> {

	private final BranchContext context;
	private final Options expand;
	private final List<ExtendedLocale> locales;

	protected BaseSnomedComponentConverter(BranchContext context, Options expand, List<ExtendedLocale> locales) {
		this.context = context;
		this.expand = expand == null ? OptionsBuilder.newBuilder().build() : expand;
		this.locales = locales == null ? Collections.<ExtendedLocale>emptyList() : locales;
	}

	protected final Options expand() {
		return expand;
	}

	protected final BranchContext context() {
		return context;
	}
	
	protected final List<ExtendedLocale> locales() {
		return locales;
	}

	@Override
	public final R convert(T component) {
		return convert(Collections.singleton(component), 0, 1, 1).getItems().iterator().next();
	}

	@Override
	public final CR convert(Collection<T> components, int offset, int limit, int total) {
		final List<R> results = FluentIterable.from(components).transform(new Function<T, R>() {
			@Override
			public R apply(T input) {
				return toResource(input);
			}
		}).toList();
		expand(results);
		return createCollectionResource(results, offset, limit, total);
	}

	protected abstract CR createCollectionResource(List<R> results, int offset, int limit, int total);

	/**
	 * Subclasses may override to expand resources based on the {@link #expand()} list.
	 * 
	 * @param results
	 */
	protected void expand(List<R> results) {
	}

	protected abstract R toResource(T entry);

	protected final Date toEffectiveTime(final long effectiveTimeAsLong) {
		return EffectiveTimes.toDate(effectiveTimeAsLong);
	}
	
	protected static final int getLimit(final Options expandOptions) {
		return expandOptions.containsKey("limit") ? expandOptions.get("limit", Integer.class) : SearchRequestBuilder.DEFAULT_LIMIT;
	}

	protected static final int getOffset(final Options expandOptions) {
		return expandOptions.containsKey("offset") ? expandOptions.get("offset", Integer.class) : SearchRequestBuilder.DEFAULT_OFFSET;
	}
}
