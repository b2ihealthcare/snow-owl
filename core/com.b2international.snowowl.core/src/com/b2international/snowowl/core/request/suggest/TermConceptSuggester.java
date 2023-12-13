/*
 * Copyright 2022 B2i Healthcare, https://b2ihealthcare.com
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
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.tartarus.snowball.ext.EnglishStemmer;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.index.compat.TextConstants;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.plugin.Component;
import com.b2international.snowowl.core.request.SearchIndexResourceRequest;
import com.b2international.snowowl.core.request.search.TermFilter;
import com.b2international.snowowl.eventbus.IEventBus;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.base.Splitter;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

/**
 * @since 8.5
 */
@Component
@JsonTypeName("term")
public final class TermConceptSuggester implements ConceptSuggester {

	// Split terms at delimiter or whitespace separators
	private static final Splitter TOKEN_SPLITTER = Splitter.on(TextConstants.WHITESPACE_OR_DELIMITER_MATCHER)
			.trimResults()
			.omitEmptyStrings();
	
	/**
	 * The number of most frequent tokens (words) to consider from the selected like text corpus. Default value is 9.
	 */
	@JsonProperty
	private Integer topTokenCount = 9;
	
	/**
	 * The number of most frequent tokens (topTokenCount) to match in order to be accepted as a suggestion. Default value is 3. 
	 */
	@JsonProperty
	private Integer minOccurenceCount = 3;
	
	/**
	 * The minimum length for a token to be considered as potential top token. Default value is 0, accepting all token lengths.
	 */
	@JsonProperty
	private int minTokenLength = 0;
	
	/**
	 * Whether to ignore any stopwords in the selected text corpus or not. Default value is <code>true</code>.
	 */
	@JsonProperty
	private boolean ignoreStopwords = true;
	
	/**
	 * Whether to perform english word stemming on the tokens or not. Default value is false.
	 */
	@JsonProperty
	private boolean stemming = false;
	
	/**
	 * Whether to allow top tokens to fuzzy match among candidates. Default value is false. 
	 */
	@JsonProperty
	private boolean fuzzy = false;
	
	@Override
	public Promise<Suggestions> suggest(ConceptSuggestionContext context, int limit, String display, List<ExtendedLocale> locales) {
		
		// Gather tokens
		final Multiset<String> tokenOccurrences = HashMultiset.create(); 
		final EnglishStemmer stemmer = new EnglishStemmer();
		
		context.streamLikes()
			.map(term -> term.toLowerCase(Locale.US))
			.flatMap(lowerCaseTerm -> TOKEN_SPLITTER.splitToList(lowerCaseTerm).stream())
			.filter(token -> token.length() >= minTokenLength) // skip short tokens
			.map(token -> stemToken(stemmer, token))
			.forEach(tokenOccurrences::add);
			
		final List<String> topTokens = Multisets.copyHighestCountFirst(tokenOccurrences)
			.elementSet()
			.stream()
			.limit(topTokenCount)
			.collect(Collectors.toList());
		
		// if there are no tokens to search for then shortcut here
		if (topTokens.isEmpty()) {
			return Promise.immediate(new Suggestions(topTokens, limit, 0));
		}
		
		final TermFilter termFilter = TermFilter.match()
				.term(String.join(" ", topTokens))
				.minShouldMatch(Math.min(minOccurenceCount, topTokens.size()))
				.ignoreStopwords(ignoreStopwords)
				.fuzziness(fuzzy ? "AUTO" : null)
				// TODO make fuzziness options configurable in term suggester settings
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
					return new Suggestions(topTokens, concepts.getItems(), concepts.getSearchAfter(), limit, concepts.getTotal());
				});
	}
	
	private String stemToken(EnglishStemmer stemmer, String token) {
		if (stemming) {
			stemmer.setCurrent(token);
			stemmer.stem();
			return stemmer.getCurrent();
		} else {
			return token;
		}
	}
	
}
