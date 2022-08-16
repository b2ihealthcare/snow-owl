/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.plugin.Component;
import com.b2international.snowowl.core.request.SearchIndexResourceRequest;
import com.b2international.snowowl.core.request.search.MoreLikeThisTermFilter;
import com.b2international.snowowl.core.request.search.TermFilter;
import com.b2international.snowowl.eventbus.IEventBus;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Using Elasticsearch More Like This query, we can generate a form of similarity query based on a large text corpus and letting Elasticsearch select the best words based on their current term_vector score.
 *
 * @since 8.5
 */
@Component
@JsonTypeName("mlt")
public final class MoreLikeThisConceptSuggester implements ConceptSuggester {

	@JsonProperty
	private Integer maxQueryTerms;
	
	@JsonProperty
	private Integer minTermFreq;
	
	@JsonProperty
	private Integer minDocFreq;
	
	@JsonProperty
	private Integer minWordLength;
	
	@JsonProperty
	private Integer maxWordLength;
	
	@JsonProperty
	private String minimumShouldMatch;
	
	@Override
	public Promise<Suggestions> suggest(ConceptSuggestionContext context, int limit, String display, List<ExtendedLocale> locales) {
		
		final MoreLikeThisTermFilter termFilter = TermFilter.mlt()
				// TODO is it okay to stream all available like text into the query? this can get really big in certain contexts
				.likeTexts(context.streamLikes().collect(Collectors.toList()))
				// not using unlike text array, as it is now really filtering out the unlikes
				.unlikeTexts(null)
				// TODO instead generate a negated query clause from all unlike text and filter out any document that matches them
				// alternative configuration would be to just decrease their score with some negative boost (ES boosting query?)
				.maxQueryTerms(maxQueryTerms)
				.minTermFreq(minTermFreq)
				.minDocFreq(minDocFreq)
				.minWordLength(minWordLength)
				.maxWordLength(maxWordLength)
				.minimumShouldMatch(minimumShouldMatch)
				.build();
		
		// get the ECL query of the from code system
		final String inclusionQuery = context.getInclusionQueries();
		// get the dynamically computed exclusion query set
		final Collection<String> exclusionQueries = context.exclusionQuery(context.from().getResourceUri());
		
		return CodeSystemRequests.prepareSearchConcepts()
				// always return active concepts only
				// TODO support in suggest API settings?
				.filterByActive(true)
				// configure from, resource and optional ECL query
				.filterByCodeSystemUri(context.from().getResourceUri())
				.filterByQuery(inclusionQuery)
				// make sure we won't suggest the same concepts as defined in like and unlike arrays (for the same code system)
				.filterByExclusions(exclusionQueries.isEmpty() ? null : exclusionQueries)
				// configure lexical match as basis of suggestion
				.filterByTerm(termFilter)
				// configure display, limit and locales
				.setPreferredDisplay(display)
				.setLimit(limit)
				.setLocales(locales)
				// always order by score
				.sortBy(SearchIndexResourceRequest.SCORE)
				.build()
				.async(Map.of(User.class, context.service(User.class)))
				.execute(context.service(IEventBus.class))
				.then(concepts -> {
					return new Suggestions(null, concepts.getItems(), concepts.getSearchAfter(), limit, concepts.getTotal());
				});
	}

}
