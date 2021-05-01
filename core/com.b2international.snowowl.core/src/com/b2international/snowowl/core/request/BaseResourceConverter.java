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

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.index.Hits;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.CollectionResource;
import com.google.common.collect.Iterables;

/**
 * @since 4.0
 * @param <T> - document type
 * @param <R> - domain type
 * @param <CR> - collection resource type
 */
public abstract class BaseResourceConverter<T, R, CR extends CollectionResource<R>> extends ResourceExpander {

	protected BaseResourceConverter(ServiceProvider context, Options expand, List<ExtendedLocale> locales) {
		super(context, expand, locales);
	}

	public final CR convert(Hits<T> hits) {
		return convert(hits.getHits(), hits.getSearchAfter(), hits.getLimit(), hits.getTotal());
	}
	
	/**
	 * Convert a single internal index based entity to a resource based representation.
	 * 
	 * @param component
	 * @return
	 */
	public final R convert(T component) {
		return Iterables.getOnlyElement(convert(Collections.singleton(component), null, 1, 1));
	}

	/**
	 * Convert multiple internal index based entities to resource based representations.
	 * 
	 * @param components
	 * @param searchAfter
	 * @param limit
	 * @param total
	 * @return
	 */
	public final CR convert(Collection<T> components, String searchAfter, int limit, int total) {
		final List<R> results = components
				.stream()
				.map(this::toResource)
				.collect(Collectors.toList());
		
		if (!results.isEmpty()) {
			expand(results);
		}
		
		return createCollectionResource(results, searchAfter, limit, total);
	}

	protected abstract CR createCollectionResource(List<R> results, String searchAfter, int limit, int total);

	/**
	 * Subclasses may override to expand resources based on the {@link #expand()} list.
	 * 
	 * @param results
	 */
	protected void expand(List<R> results) {
	}

	protected abstract R toResource(T entry);

	protected final LocalDate toEffectiveTime(final Long effectiveTimeAsLong) {
		return effectiveTimeAsLong == null ? null : EffectiveTimes.toDate(effectiveTimeAsLong);
	}
	
}
