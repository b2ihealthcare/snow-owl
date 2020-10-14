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

import java.util.Map;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.Concepts;
import com.b2international.snowowl.core.events.AsyncRequest;
import com.b2international.snowowl.core.request.ConceptSearchRequestEvaluator.OptionKey;

/**
 * @since 7.5
 */
public final class ConceptSearchRequestBuilder extends SearchResourceRequestBuilder<ConceptSearchRequestBuilder, BranchContext, Concepts>
		implements RevisionIndexRequestBuilder<Concepts> {

	/**
	 * Filters matches by their active/inactive status. 
	 * 
	 * @param active
	 * @return
	 */
	public ConceptSearchRequestBuilder filterByActive(Boolean active) {
		return addOption(OptionKey.ACTIVE, active);
	}
	
	/**
	 * Filters matches by their lexical terms. The exact semantics of how a term match works depends on the given code system, but usually it supports
	 * exact, partial word and prefix matches.
	 * 
	 * @param term
	 * @return
	 */
	public ConceptSearchRequestBuilder filterByTerm(String term) {
		return addOption(OptionKey.TERM, term);
	}

	/**
	 * Filters matches by their lexical terms. Returns matches with exact case insensitive term matches.
	 * 
	 * @param exactTerm
	 * @return
	 */
	public ConceptSearchRequestBuilder filterByExactTerm(String exactTerm) {
		return addOption(OptionKey.TERM_EXACT, exactTerm);
	}

	/**
	 * Sets the minimum number of terms that should be matched in a {@link #filterByTerm(String)} clause.
	 * The default is "all terms", when not given.
	 * 
	 * @param minTermMatch
	 * @return
	 */
	public ConceptSearchRequestBuilder setMinTermMatch(int minTermMatch) {
		return addOption(OptionKey.MIN_TERM_MATCH, minTermMatch);
	}
	
	/**
	 * Filters matches by a query expression defined in the target code system's query language.
	 * 
	 * @param query
	 *            - the query expression
	 * @return
	 */
	public ConceptSearchRequestBuilder filterByQuery(String query) {
		return addOption(OptionKey.QUERY, query);
	}

	/**
	 * Filter by multiple query expressions defined in the target code system's query language.
	 * 
	 * @param inclusions
	 *            - query expressions that include matches
	 * @return
	 */
	public ConceptSearchRequestBuilder filterByInclusions(Iterable<String> inclusions) {
		return addOption(OptionKey.QUERY, inclusions);
	}

	/**
	 * Exclude matches by specifying one exclusion query defined in the target code system's query language.
	 * 
	 * @param exclusion
	 *            - query expression that exclude matches
	 * @return
	 */
	public ConceptSearchRequestBuilder filterByExclusion(String exclusion) {
		return addOption(OptionKey.MUST_NOT_QUERY, exclusion);
	}
	
	/**
	 * Exclude matches by specifying one or more exclusion queries defined in the target code system's query language.
	 * 
	 * @param exclusions
	 *            - query expression that exclude matches
	 * @return
	 */
	public ConceptSearchRequestBuilder filterByExclusions(Iterable<String> exclusions) {
		return addOption(OptionKey.MUST_NOT_QUERY, exclusions);
	}
	
	/**
	 * Sets the preferred display term to return for every code system
	 * 
	 * @param prefferedDisplayMap: String representation of the preferred display
	 * @return
	 */
	public ConceptSearchRequestBuilder setPreferredDisplay(String prefferedDisplay) {
		return addOption(OptionKey.DISPLAY, prefferedDisplay);
	}

	@Override
	protected SearchResourceRequest<BranchContext, Concepts> createSearch() {
		return new ConceptSearchRequest();
	}

	/**
	 * @deprecated - use the {@link #build(String)} method instead
	 */
	@Override
	public AsyncRequest<Concepts> build(String repositoryId, String branch) {
		throw new UnsupportedOperationException("This build() method is unsupported for generic requests");
	}

}
