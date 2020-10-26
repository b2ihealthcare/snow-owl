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

/**
 * Interface to configure term filters in a given request
 * 
 * @since 7.11
 */
public interface TermFilterSupport<T> {

	/**
	 * Filters results by matching terms, as entered (the comparison depends on the configuration selected in the {@link TermFilter term filter
	 * configuration type}).
	 * 
	 * @param termFilter
	 *            - {@link TermFilter} configuration
	 * @return <code>this</code> for method chaining
	 * @see TermFilter#defaultTermMatch(String)
	 * @see TermFilter#exactTermMatch(String)
	 * @see TermFilter#fuzzyMatch(String)
	 * @see TermFilter#minTermMatch(String, Integer)
	 * @see TermFilter#parsedTermMatch(String)
	 */
	T filterByTerm(final TermFilter termFilter);

	/**
	 * Filters results by matching exact terms, as entered (the comparison is case insensitive and folds non-ASCII characters to their closest
	 * equivalent).
	 * <p>
	 * This filter affects the score of each result. If results should be returned in order of relevance, specify {@link SearchResourceRequest#SCORE}
	 * as one of the sort fields.
	 * 
	 * @param exactTermFilter
	 *            - the expression to match
	 * @return <code>this</code> for method chaining
	 * @see TermFilter#exactTermMatch(String)
	 */
	default T filterByExactTerm(final String exactTermFilter) {
		return filterByTerm(exactTermFilter != null ? TermFilter.exactTermMatch(exactTermFilter) : null);
	}

	/**
	 * Filters results by matching description terms, as entered (the comparison is case insensitive and folds non-ASCII characters to their closest
	 * equivalent).
	 * <p>
	 * This filter affects the score of each result. If results should be returned in order of relevance, specify {@link SearchResourceRequest#SCORE}
	 * as one of the sort fields.
	 * 
	 * @param termFilter
	 *            - the expression to match
	 * @return <code>this</code> for method chaining
	 * @see TermFilter#defaultTermMatch(String)
	 */
	default T filterByTerm(final String termFilter) {
		return filterByTerm(termFilter != null ? TermFilter.defaultTermMatch(termFilter) : null);
	}
}
