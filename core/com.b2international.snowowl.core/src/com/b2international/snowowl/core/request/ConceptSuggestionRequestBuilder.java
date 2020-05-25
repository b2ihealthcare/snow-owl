/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.Concepts;
import com.b2international.snowowl.core.events.AsyncRequest;
import com.b2international.snowowl.core.request.ConceptSearchRequestEvaluator.OptionKey;

/**
 * @since 7.7
 */
public final class ConceptSuggestionRequestBuilder 
		extends SearchResourceRequestBuilder<ConceptSuggestionRequestBuilder, BranchContext, Concepts>
		implements RevisionIndexRequestBuilder<Concepts> {

	private int topTokenCount = 9;
	
	private int minOccurrenceCount = 3;
	
	/**
	 * Filters matches by their lexical terms. The exact semantics of how a term match works depends on the given code system, but usually it supports
	 * exact, partial word and prefix matches.
	 * 
	 * @param term
	 * @return
	 */
	public ConceptSuggestionRequestBuilder filterByTerm(String term) {
		return addOption(OptionKey.TERM, term);
	}

	/**
	 * Filters matches by their lexical terms. Returns matches with exact case insensitive term matches.
	 * 
	 * @param exactTerm
	 * @return
	 */
	public ConceptSuggestionRequestBuilder filterByExactTerm(String exactTerm) {
		return addOption(OptionKey.TERM_EXACT, exactTerm);
	}
	
	/**
	 * Filters matches by a query expression defined in the target code system's query language.
	 * 
	 * @param query
	 *            - the query expression
	 * @return
	 */
	public ConceptSuggestionRequestBuilder filterByQuery(String query) {
		return addOption(OptionKey.QUERY, query);
	}

	/**
	 * Filter by multiple query expressions defined in the target code system's query language.
	 * 
	 * @param inclusions
	 *            - query expressions that include matches
	 * @return
	 */
	public ConceptSuggestionRequestBuilder filterByInclusions(Iterable<String> inclusions) {
		return addOption(OptionKey.QUERY, inclusions);
	}

	/**
	 * Exclude matches by specifying one exclusion query defined in the target code system's query language.
	 * 
	 * @param exclusion
	 *            - query expression that exclude matches
	 * @return
	 */
	public ConceptSuggestionRequestBuilder filterByExclusion(String exclusion) {
		return addOption(OptionKey.MUST_NOT_QUERY, exclusion);
	}
	
	/**
	 * Exclude matches by specifying one or more exclusion queries defined in the target code system's query language.
	 * 
	 * @param exclusions
	 *            - query expression that exclude matches
	 * @return
	 */
	public ConceptSuggestionRequestBuilder filterByExclusions(Iterable<String> exclusions) {
		return addOption(OptionKey.MUST_NOT_QUERY, exclusions);
	}
	
	/**
	 * Suggested concepts are based on term queries that use the top "n" tokens most frequently occurring in 
	 * the suggestion base set, defined by the method above. The cut-off value of "n" is set by this method.
	 * 
	 * @param termCount the number of tokens to consider for suggestions (default is 9)
	 * @return
	 */
	public ConceptSuggestionRequestBuilder setTopTokenCount(int topTokenCount) {
		this.topTokenCount = topTokenCount;
		return getSelf();
	}
	
	/**
	 * Sets the minimum number of occurrences of the top "n" token list that a concept must have in order
	 * to consider it for inclusion in the suggestions list.  
	 * 
	 * @param minOccurrenceCount the minimum number of occurrences to use (default is 3)
	 * @return
	 */
	public ConceptSuggestionRequestBuilder setMinOccurrenceCount(int minOccurrenceCount) {
		this.minOccurrenceCount = minOccurrenceCount;
		return getSelf();
	}
	
	@Override
	protected SearchResourceRequest<BranchContext, Concepts> createSearch() {
		final ConceptSuggestionRequest request = new ConceptSuggestionRequest();
		request.setTopTokenCount(topTokenCount);
		request.setMinOccurrenceCount(minOccurrenceCount);
		return request;
	}

	/**
	 * @deprecated - use the {@link #build(String)} method instead
	 */
	@Override
	public AsyncRequest<Concepts> build(String repositoryId, String branch) {
		throw new UnsupportedOperationException("This build() method is unsupported for generic requests");
	}
}
