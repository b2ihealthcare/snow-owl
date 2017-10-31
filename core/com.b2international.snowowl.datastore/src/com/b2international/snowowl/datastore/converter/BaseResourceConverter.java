/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.converter;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.commons.options.OptionsBuilder;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.CollectionResource;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.google.common.collect.Iterables;

/**
 * @since 4.0
 * @param <T>
 * @param <R>
 * @param <CR>
 */
public abstract class BaseResourceConverter<T, R, CR extends CollectionResource<R>> implements ResourceConverter<T, R, CR> {

	private final RepositoryContext context;
	private final Options expand;
	private final List<ExtendedLocale> locales;

	protected BaseResourceConverter(RepositoryContext context, Options expand, List<ExtendedLocale> locales) {
		this.context = checkNotNull(context, "context");
		this.expand = expand == null ? OptionsBuilder.newBuilder().build() : expand;
		this.locales = locales == null ? Collections.<ExtendedLocale>emptyList() : locales;
	}

	protected final Options expand() {
		return expand;
	}

	protected RepositoryContext context() {
		return context;
	}
	
	protected final List<ExtendedLocale> locales() {
		return locales;
	}

	@Override
	public final R convert(T component) {
		return Iterables.getOnlyElement(convert(Collections.singleton(component), null, 1, 1));
	}

	@Override
	public final CR convert(Collection<T> components, String scrollId, int limit, int total) {
		final List<R> results = components
				.stream()
				.map(this::toResource)
				.collect(Collectors.toList());
		
		expand(results);
		
		return createCollectionResource(results, scrollId, limit, total);
	}

	protected abstract CR createCollectionResource(List<R> results, String scrollId, int limit, int total);

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
	
	protected final int getLimit(final Options expandOptions) {
		return expandOptions.containsKey("limit") ? expandOptions.get("limit", Integer.class) : 50;
	}

	protected final int getOffset(final Options expandOptions) {
		return expandOptions.containsKey("offset") ? expandOptions.get("offset", Integer.class) : 0;
	}
}
