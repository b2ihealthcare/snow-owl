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

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.CodeType;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.Concepts;
import com.b2international.snowowl.core.events.AsyncRequest;
import com.b2international.snowowl.core.request.ConceptSearchRequestEvaluator.OptionKey;
import com.google.common.collect.ImmutableSet;

/**
 * @since 7.5
 */
public final class ConceptSearchRequestBuilder extends SearchResourceRequestBuilder<ConceptSearchRequestBuilder, BranchContext, Concepts>
		implements RevisionIndexRequestBuilder<Concepts>, TermFilterSupport<ConceptSearchRequestBuilder> {

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
	 * {@inheritDoc}
	 */
	@Override
	public ConceptSearchRequestBuilder filterByTerm(TermFilter termFilter) {
		return addOption(OptionKey.TERM, termFilter);
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
	 * Filters terms by their type.
	 * 
	 * @param termType
	 *            - String representation of the term type filtering
	 * @return
	 */
	public ConceptSearchRequestBuilder filterByTermType(String termType) {
		return addOption(OptionKey.TERM_TYPE, termType);
	}

	/**
	 * Filters concepts by their type.
	 * 
	 * @param type
	 *            - String representation of the concept type filtering
	 * @return
	 */
	public ConceptSearchRequestBuilder filterByType(String type) {
		try {
			return filterByTypes(ImmutableSet.of(CodeType.valueOf(type)));
		} catch (Exception e) {
			throw new BadRequestException("%s is not a valid CodeType enum.", type);
		}
	}

	public ConceptSearchRequestBuilder filterByType(CodeType type) {
		return filterByTypes(ImmutableSet.of(type));
	}

	public ConceptSearchRequestBuilder filterByTypes(Iterable<CodeType> types) {
		return addOption(OptionKey.TYPE, types);
	}
	
	
	/**
	 * Sets the preferred display term to return for every code system
	 * 
	 * @param preferredDisplay
	 *            - String representation of the preferred display
	 * @return
	 */
	public ConceptSearchRequestBuilder setPreferredDisplay(String preferredDisplay) {
		return addOption(OptionKey.DISPLAY, preferredDisplay);
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
