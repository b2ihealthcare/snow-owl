/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;

/**
 * @since 8.1.0
 */
public class SnomedConceptRequestCache {

	private final Map<FetchConfig, Map<String, SnomedConcept>> cache = new HashMap<>(); 
	private final Deque<FetchConfig> requestedFetches = new ArrayDeque<>();
	
	public void request(Iterable<String> ids, Options expand, List<ExtendedLocale> locales, Consumer<Map<String, SnomedConcept>> onConceptsReady) {
		requestedFetches.add(new FetchConfig(ids, expand, locales, onConceptsReady));
	}
	
	public Map<String, SnomedConcept> get(BranchContext context, Iterable<String> ids, Options expand, List<ExtendedLocale> locales) {
		FetchConfig config = new FetchConfig(ids, expand, locales, concepts -> {});
		if (cache.containsKey(config)) {
			return cache.get(config);
		} else {
			requestedFetches.add(config);
			compute(context);
			return cache.get(config);
		}
	}
	
	public void compute(BranchContext context) {
		// run until all requested fetches are resolved
		while (!requestedFetches.isEmpty()) {
			// using the currently first requestedFetch config
			// check if there are similar fetchConfigs requested earlier, and if yes, try to fetch them now, so they can be referenced later from the cache, and immediately get populated via the callback
			FetchConfig config = requestedFetches.getFirst();
			final Set<FetchConfig> configsToFetch = requestedFetches.stream()
					.filter(cfg -> Objects.equals(config.expand, cfg.expand) && Objects.equals(config.locales, cfg.locales))
					.collect(Collectors.toSet());
			// remove them from the requested fetches
			requestedFetches.removeAll(configsToFetch);

			// search for configs using the same expand and locales, merge the IDs and fetch all of them together
			final Set<String> ids = configsToFetch.stream().flatMap(cfg -> cfg.ids.stream()).collect(Collectors.toSet());
			
			context.log().info("Fetching concepts: ids={}, expand={}, locales={}", ids, config.expand, config.locales);
			Map<String, SnomedConcept> fetchedConcepts = SnomedRequests.prepareSearchConcept()
					.setLimit(ids.size())
					.filterByIds(ids)
					.setExpand(config.expand)
					.setLocales(config.locales)
					.build()
					.execute(context)
					.stream()
					.collect(Collectors.toMap(IComponent::getId, c -> c));
			
			// populate the cache for each fetch config and call the callback
			configsToFetch.forEach(cfg -> {
				ImmutableMap.Builder<String, SnomedConcept> configConcepts = ImmutableMap.builder();
				cfg.ids.forEach(idToCache -> {
					configConcepts.put(idToCache, fetchedConcepts.get(idToCache));
				});
				ImmutableMap<String, SnomedConcept> concepts = configConcepts.build();
				cache.put(cfg, concepts);
				cfg.onConceptsReady.accept(concepts);
			});
		}
	}
	
	private static final class FetchConfig {
		
		private final SortedSet<String> ids;
		private final Options expand;
		private final List<ExtendedLocale> locales;
		private final Consumer<Map<String, SnomedConcept>> onConceptsReady;
		
		public FetchConfig(Iterable<String> ids, Options expand, List<ExtendedLocale> locales, Consumer<Map<String, SnomedConcept>> onConceptsReady) {
			this.ids = ImmutableSortedSet.copyOf(ids);
			this.expand = expand;
			this.locales = locales;
			this.onConceptsReady = onConceptsReady;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(ids, expand, locales);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			FetchConfig other = (FetchConfig) obj;
			return Objects.equals(ids, other.ids)
					&& Objects.equals(expand, other.expand)
					&& Objects.equals(locales, other.locales);
		}
		
	}
	
}
