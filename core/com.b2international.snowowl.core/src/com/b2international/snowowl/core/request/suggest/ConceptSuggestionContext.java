/*
 * Copyright 2022-2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.request.suggest;

import java.util.*;
import java.util.stream.Stream;

import com.b2international.commons.collections.Collections3;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snomed.ecl.Ecl;
import com.b2international.snowowl.core.ResourceTypeConverter;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ResourceURIWithQuery;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.domain.Concept;
import com.b2international.snowowl.core.domain.Concepts;
import com.b2international.snowowl.core.domain.DelegatingContext;
import com.b2international.snowowl.core.domain.Description;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * @since 8.5
 */
public final class ConceptSuggestionContext extends DelegatingContext {

	private final ResourceURIWithQuery from;
	private final SortedSet<String> likes;
	private final SortedSet<String> unlikes;
	private final List<ExtendedLocale> locales;
	
	// dynamically computed exclusion items during like item computation
	private Multimap<ResourceURI, String> exclusionQueriesPerResourceUri = HashMultimap.create();

	public ConceptSuggestionContext(ServiceProvider context, String from, List<String> likes, List<String> unlikes, List<ExtendedLocale> locales) {
		super(context);
		this.locales = locales;
		this.from = resolveUri(context, from);
		this.likes = Collections3.toImmutableSortedSet(likes);
		this.unlikes = Collections3.toImmutableSortedSet(unlikes);
	}

	public SortedSet<String> likes() {
		return likes;
	}
	
	public SortedSet<String> unlikes() {
		return unlikes;
	}

	public Stream<String> streamLikes() {
		Multimap<ResourceURI, String> unlikeQueriesByResource = HashMultimap.create();
		
		unlikes.forEach(unlike -> {
			try {
				final ResourceURIWithQuery uri = resolveUri(this, unlike);
				Collection<String> eclQueries = uri.getQueryValues().get("ecl");
				if (eclQueries.isEmpty()) {
					throw new BadRequestException("Selecting an entire Code System as unlike is not supported yet. Specify an ECL query part like this: %s?ecl=<your_query>", uri.getResourceUri().withoutResourceType());
				} else {
					eclQueries.forEach(q -> {
						unlikeQueriesByResource.put(uri.getResourceUri(), q);
						exclusionQueriesPerResourceUri.put(uri.getResourceUri(), q);
					});
				}
			} catch (Exception e) {
				// not URI, skip for now
				// TODO figure out how to represent unlike keywords in a query, ECL NOT {{ term: <x> }} ?
			}
		});
		
		// TODO optimize multiple likes specifying the same source system
		return likes
			.stream()
			.flatMap(like -> {
				try {
					final ResourceURIWithQuery uri = resolveUri(this, like);
					// raw URIs are not supported yet, because those can select too many concepts
					Collection<String> eclQueries = uri.getQueryValues().get("ecl");
					if (eclQueries.isEmpty()) {
						throw new BadRequestException("Selecting an entire Code System as like is not supported yet. Specify an ECL query part like this: %s?ecl=<your_query>", uri.getResourceUri().withoutResourceType());
					}
					
					Collection<String> exclusionsForThisLike = unlikeQueriesByResource.get(uri.getResourceUri());
					String exclusionQuery = exclusionsForThisLike.isEmpty() ? null : Ecl.or(exclusionsForThisLike);
					
					// register this like query as global exclusion filter for the final suggestion search
					eclQueries.forEach(q -> {
						exclusionQueriesPerResourceUri.put(uri.getResourceUri(), q);
					});
					
					// Get the suggestion base set of concepts in case of URIs with queries
					return CodeSystemRequests.prepareSearchConcepts()
							.filterByCodeSystemUri(uri.getResourceUri())
							.filterByQuery(Ecl.or(eclQueries))
							.filterByExclusion(exclusionQuery)
							.setLimit(getPageSize())
							.setLocales(locales)
							.stream(this)
							.flatMap(Concepts::stream)
							.flatMap(concept -> getAllTerms(concept).stream());
					
				} catch (Exception e) {
					// not URI, use as is
					return List.of(like).stream();
				}
			});
	}

	public ResourceURIWithQuery from() {
		return from;
	}
	
	public List<ExtendedLocale> locales() {
		return locales;
	}
	
	private Set<String> getAllTerms(Concept concept) {
		final Set<String> allTerms = new HashSet<>();
		
		// just in case keep adding the selected display term even though the description list of the generic concept should already contain all descriptions, not just alternatives
		if (concept.getTerm() != null) {
			allTerms.add(concept.getTerm());
		}
		if (concept.getDescriptions() != null) {
			concept.getDescriptions().stream().map(Description::getTerm).forEach(allTerms::add);
		}
		return allTerms;
	}

	public Collection<String> exclusionQuery(ResourceURI resourceUri) {
		return exclusionQueriesPerResourceUri.get(resourceUri);
	}

	public String getInclusionQueries() {
		final Collection<String> inclusionQueries = from().getQueryValues().get("ecl");
		return inclusionQueries.isEmpty() ? null : Ecl.or(inclusionQueries);
	}
	
	private ResourceURIWithQuery resolveUri(ServiceProvider context, String uriToResolve) {
		// find the appropriate resource for this URI by looking at the plugged in resources types
		ResourceURIWithQuery uri = null;
		for (ResourceTypeConverter resourceTypeConverter : context.service(ResourceTypeConverter.Registry.class).getResourceTypeConverters().values()) {
			if (uriToResolve.startsWith(resourceTypeConverter.getResourceType())) {
				uri = resourceTypeConverter.resolveToCodeSystemUriWithQuery(context, uriToResolve);
				break;
			}
		}
		// if the URI is still null, treat it as CodeSystem for now
		return uri == null ? CodeSystem.uriWithQuery(uriToResolve) : uri;
	}
	
}
