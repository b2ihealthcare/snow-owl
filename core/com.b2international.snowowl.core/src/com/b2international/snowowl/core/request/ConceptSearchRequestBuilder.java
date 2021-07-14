/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.core.CodeType;
import com.b2international.snowowl.core.context.TerminologyResourceContentRequestBuilder;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.Concepts;
import com.b2international.snowowl.core.request.ConceptSearchRequestEvaluator.OptionKey;
import com.google.common.collect.FluentIterable;

/**
 * @since 7.5
 */
public final class ConceptSearchRequestBuilder 
		extends SearchPageableCollectionResourceRequestBuilder<ConceptSearchRequestBuilder, BranchContext, Concepts>
		implements TerminologyResourceContentRequestBuilder<Concepts>, TermFilterSupport<ConceptSearchRequestBuilder> {

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
	 * Filters concepts by their type. Allowed values are: 'code', 'category'.
	 * 
	 * @param type
	 *            - String representation of the {@link CodeType} enum
	 * @return
	 * @see CodeType
	 */
	public ConceptSearchRequestBuilder filterByCodeType(String type) {
		return filterByCodeType(CodeType.valueOfIgnoreCase(type));
	}
	
	/**
	 * Filters concepts by their type. Allowed values are: 'code', 'category'.
	 * 
	 * @param types
	 *            - String representations of the {@link CodeType} enum
	 * @return
	 * @see CodeType
	 */
	public ConceptSearchRequestBuilder filterByCodeType(Iterable<String> types) {
		return filterByCodeTypes(types == null ? null : FluentIterable.from(types).transform(CodeType::valueOfIgnoreCase).toSet());
	}

	/**
	 * Filters concepts by their type. Allowed values are: 'code', 'category'.
	 * 
	 * @param type
	 * @return
	 * @see CodeType
	 */
	public ConceptSearchRequestBuilder filterByCodeType(CodeType type) {
		return addOption(OptionKey.TYPE, type);
	}

	/**
	 * Filters concepts by their type. Allowed values are: 'code', 'category'.
	 * 
	 * @param types
	 * @return
	 * @see CodeType
	 */
	public ConceptSearchRequestBuilder filterByCodeTypes(Iterable<CodeType> types) {
		return addOption(OptionKey.TYPE, types);
	}
	
	/**
	 * Filters concepts to have the given concept their direct parent.
	 * 
	 * @param parentId - a single parent to match
	 * @return
	 */
	public ConceptSearchRequestBuilder filterByParent(String parentId) {
		return addOption(OptionKey.PARENT, parentId);
	}
	
	/**
	 * Filters concepts to have any of the given parents their direct parent.
	 * 
	 * @param parentIds - any parent to match from this collection
	 * @return
	 */
	public ConceptSearchRequestBuilder filterByParents(Iterable<String> parentIds) {
		return addOption(OptionKey.PARENT, parentIds);
	}
	
	/**
	 * Filters concepts to have the given ancestor their direct or indirect parent (ancestor).
	 * 
	 * @param ancestorId - single ancestor to match
	 * @return
	 */
	public ConceptSearchRequestBuilder filterByAncestor(String ancestorId) {
		return addOption(OptionKey.ANCESTOR, ancestorId);
	}
	
	/**
	 * Filters concepts to have any of the given parents their direct or indirect parent (ancestor).
	 * 
	 * @param ancestorIds - any ancestor to match from this collection
	 * @return
	 */
	public ConceptSearchRequestBuilder filterByAncestors(Iterable<String> ancestorIds) {
		return addOption(OptionKey.ANCESTOR, ancestorIds);
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

}
