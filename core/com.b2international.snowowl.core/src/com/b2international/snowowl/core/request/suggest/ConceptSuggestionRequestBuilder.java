/*
 * Copyright 2020-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ResourceURIWithQuery;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.request.ConceptSearchRequestEvaluator.OptionKey;
import com.b2international.snowowl.core.request.SearchPageableCollectionResourceRequestBuilder;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.SystemRequestBuilder;

/**
 * @since 7.7
 */
public final class ConceptSuggestionRequestBuilder
		extends SearchPageableCollectionResourceRequestBuilder<ConceptSuggestionRequestBuilder, ServiceProvider, Suggestions>
		implements SystemRequestBuilder<Suggestions> {

	private Suggester suggester;
	
	@Deprecated
	private Integer topTokenCount;
	
	@Deprecated
	private Integer minOccurrenceCount;
	
	/**
	 * Selects the substrate where the suggestions can come from. 
	 * @param from - supports any code system {@link ResourceURI} optionally with {@link ResourceURIWithQuery query part (ECL)} 
	 * @return
	 * @since 8.5
	 */
	public ConceptSuggestionRequestBuilder setFrom(String from) {
		return addOption(ConceptSuggestionRequest.OptionKey.FROM, from);
	}
	
	/**
	 * Configures the suggester to run when suggesting concepts.
	 * 
	 * @param suggester - configure the suggester type and its optional settings
	 * @return 
	 * @since 8.5
	 */
	public ConceptSuggestionRequestBuilder setSuggester(Suggester suggester) {
		this.suggester = suggester;
		return getSelf();
	}
	
	/**
	 * Suggest concepts that are similar to the text or concepts (selected via ECL query) specified here. 
	 * 
	 * @param likeTextOrQuery - supports any text as input, usually free text, terms or {@link ResourceURIWithQuery URIs with optional queries} 
	 * @return
	 * @since 8.5
	 */
	public ConceptSuggestionRequestBuilder setLike(String likeTextOrQuery) {
		return addOption(ConceptSuggestionRequest.OptionKey.LIKE, likeTextOrQuery);
	}
	
	/**
	 * Suggest concepts that are similar to the array of text or concepts (selected via an array of ECL queries) specified here.
	 * 
	 * @param likeTextsOrQueries - supports any text as input, usually free text, terms or {@link ResourceURIWithQuery URIs with optional queries}
	 * @return
	 * @since 8.5
	 */
	public ConceptSuggestionRequestBuilder setLike(Iterable<String> likeTextsOrQueries) {
		return addOption(ConceptSuggestionRequest.OptionKey.LIKE, likeTextsOrQueries);
	}
	
	/**
	 * Suggest concepts that are dissimilar to the text or concepts (selected via ECL query) specified here.
	 * 
	 * @param unlikeTextOrQuery - supports any text as input, usually free text, terms or {@link ResourceURIWithQuery URIs with optional queries}
	 * @return
	 * @since 8.5
	 */
	public ConceptSuggestionRequestBuilder setUnlike(String unlikeTextOrQuery) {
		return addOption(ConceptSuggestionRequest.OptionKey.UNLIKE, unlikeTextOrQuery);
	}
	
	/**
	 * Suggest concepts that are dissimilar to the array of texts or concepts (selected via an array of ECL queries) specified here.
	 * 
	 * @param unlikeTextsOrQueries - supports any text as input, usually free text, terms or {@link ResourceURIWithQuery URIs with optional queries}
	 * @return
	 * @since 8.5
	 */
	public ConceptSuggestionRequestBuilder setUnlike(Iterable<String> unlikeTextsOrQueries) {
		return addOption(ConceptSuggestionRequest.OptionKey.UNLIKE, unlikeTextsOrQueries);
	}
	
	/**
	 * Filters matches by a query expression defined in the target code system's query language.
	 * 
	 * @param query
	 *            - the query expression
	 * @return
	 * @deprecated - replaced with {@link #setLike(String)} configuration parameter, will be removed in Snow Owl 9
	 */
	public ConceptSuggestionRequestBuilder filterByQuery(String query) {
		return setLike(query);
	}

	/**
	 * Filter by multiple query expressions defined in the target code system's query language.
	 * 
	 * @param inclusions
	 *            - query expressions that include matches
	 * @return
	 * @deprecated - replaced with {@link #setLike(Iterable)} configuration parameter, will be removed in Snow Owl 9
	 */
	public ConceptSuggestionRequestBuilder filterByInclusions(Iterable<String> inclusions) {
		return setLike(inclusions);
	}

	/**
	 * Exclude matches by specifying one exclusion query defined in the target code system's query language.
	 * 
	 * @param exclusion
	 *            - query expression that exclude matches
	 * @return
	 * @deprecated - replaced with {@link #setUnlike(String)} configuration parameter, will be removed in Snow Owl 9
	 */
	public ConceptSuggestionRequestBuilder filterByExclusion(String exclusion) {
		return setUnlike(exclusion);
	}
	
	/**
	 * Exclude matches by specifying one or more exclusion queries defined in the target code system's query language.
	 * 
	 * @param exclusions
	 *            - query expression that exclude matches
	 * @return
	 * @deprecated - replaced with {@link #setUnlike(Iterable)} configuration parameter, will be removed in Snow Owl 9
	 */
	public ConceptSuggestionRequestBuilder filterByExclusions(Iterable<String> exclusions) {
		return setUnlike(exclusions);
	}
	
	/**
	 * Filter matches by a specified term. Please note that since Snow Owl 8.5 calling this method automatically configures the default term-based suggester, overriding the current suggester value.
	 * 
	 * @param term
	 *            - term to filter matches by
	 * @return
	 * @deprecated - replaced with {@link #setLike(String)}, will be removed in Snow Owl 9
	 */
	public ConceptSuggestionRequestBuilder filterByTerm(final String term) {
		return setLike(term);
	}
	
	/**
	 * Set the {@link SnomedDisplayTermType} of the returning term in case of SNOMED.
	 * 
	 * @param display
	 *            - the display type
	 * @return
	 */
	public ConceptSuggestionRequestBuilder setPreferredDisplay(final String display) {
		return addOption(OptionKey.DISPLAY, display);
	}
	
	/**
	 * Suggested concepts are based on term queries that use the top "n" tokens most frequently occurring in 
	 * the suggestion base set, defined by the method above. The cut-off value of "n" is set by this method.
	 * 
	 * @param topTokenCount the number of tokens to consider for suggestions (default is 9)
	 * @return
	 * @deprecated - replaced with specific suggester algorithm configuration, will be removed in Snow Owl 9
	 */
	public ConceptSuggestionRequestBuilder setTopTokenCount(Integer topTokenCount) {
		this.topTokenCount = topTokenCount;
		return getSelf();
	}
	
	/**
	 * Sets the minimum number of occurrences of the top "n" token list that a concept must have in order
	 * to consider it for inclusion in the suggestions list.  
	 * 
	 * @param minOccurrenceCount the minimum number of occurrences to use (default is 3)
	 * @return
	 * @deprecated - replaced with specific suggester algorithm configuration, will be removed in Snow Owl 9
	 */
	public ConceptSuggestionRequestBuilder setMinOccurrenceCount(Integer minOccurrenceCount) {
		this.minOccurrenceCount = minOccurrenceCount;
		return getSelf();
	}
	
	@Override
	protected SearchResourceRequest<ServiceProvider, Suggestions> createSearch() {
		// XXX API backward compatibility, channel topTokenCount and minOccurenceCount to suggester settings if suggester is defined, remove in Snow Owl 9
		// if configured, specify the values here, otherwise let the suggester implementation use its defaults
		if (suggester != null && topTokenCount != null) {
			suggester.setSettings("topTokenCount", topTokenCount);
		}
		if (suggester != null && minOccurrenceCount != null) {
			suggester.setSettings("minOccurenceCount", minOccurrenceCount);
		}
		
		ConceptSuggestionRequest req = new ConceptSuggestionRequest();
		req.setSuggester(suggester);
		return req;
	}

}
