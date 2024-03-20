/*
 * Copyright 2020-2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.request.search;

import java.io.Serializable;
import java.util.Set;

import com.b2international.index.query.Expression;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Iterables;

/**
 * Represents a {@link Serializable} encapsulation of term search configuration.
 * 
 * @since 7.11
 */
public abstract class TermFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The <b>default</b> term matching contains the following index queries:
	 * <p>
	 * <li>Exact term match on a case insensitive, ASCII folded keyword type field (usually the <b>term.exact</b> field)</li>
	 * <li>All terms present match on a case insensitive, ASCII folded, possessive removed, split text type field (usually the <b>term</b> field)</li>
	 * <li>All terms but the last present exactly and the last term present as a prefix of a word. (the usual ASCII folding, possessive removals, text splitting and case sensitivity configuration apply)</li>
	 * <li>All term prefixes present match on a case insensitive, ASCII folded, possessive removed and split text type field where the field has 2-12 additional prefix terms generated via the edge_ngram token filter (usually the <b>term.prefix</b> field)</li>
	 * </p>
	 * 
	 * The <b>minShouldMatch</b> term filter contains the following index queries:
	 * <p>
	 * <li>All terms present match on a case insensitive, ASCII folded, possessive removed, split text type field (usually the <b>term</b> field)</li>
	 * <li>All term prefixes present match on a case insensitive, ASCII folded, possessive removed and split text type field where the field has 2-12 additional prefix terms generated via the edge_ngram token filter (usually the <b>term.prefix</b> field)</li>
	 * </p>
	 * 
	 * The <b>fuzziness</b> configuration alters the query to allow certain amount of fuzziness in the given terms:
	 * 
	 * <li>All terms present match on a case insensitive, ASCII folded, possessive removed, split text type field (usually the <b>term</b> field) with fuzziness enabled with configurable max expansions (defaults to 10), prefixLength (defaults to 1) and configurable distance (usually set to AUTO, but can be set to any value according to Levenshtein distance).</li>
	 * 
	 * Additionally stopwords can be ignored, case sensitivity can be enabled/disabled and synonyms can be included if needed, unless any of these are taken into account on via the selected field's default search_analyzer configuration.
	 * 
	 * @return {@link MatchTermFilter.Builder}
	 */
	@JsonIgnore
	public static MatchTermFilter.Builder match() {
		return new MatchTermFilter.Builder();
	}
	
	/**
	 * The <b>exact</b> term filter contains the following index queries:
	 * 
	 * <li>Documents that contain an <b>exact</b> term in a provided field (usually the <b>term</b> field)
	 * 
	 * Additionally case sensitivity can be enabled/disabled.
	 * 
	 * @return {@link ExactTermFilter.Builder}
	 */
	@JsonIgnore
	public static ExactTermFilter.Builder exact() {
		return new ExactTermFilter.Builder();
	}
	
	@JsonIgnore
	public static ParsedTermFilter.Builder parsed() {
		return new ParsedTermFilter.Builder();
	}
	
	@JsonIgnore
	public static MoreLikeThisTermFilter.Builder mlt() {
		return new MoreLikeThisTermFilter.Builder();
	}

	/**
	 * Get the search term(s) from this term filter. Certain implementations only support a single term to be defined.
	 * 
	 * @return a collection of search terms, never <code>null</code>
	 */
	@JsonIgnore
	public abstract Set<String> getTerms();
	
	@JsonIgnore
	public final String getSingleTermOrNull() {
		final Set<String> terms = getTerms();
		return terms.size() == 1 ? Iterables.getOnlyElement(terms) : null;
	}
	
	/**
	 * Converts this {@link TermFilter} instance into an executable low-level index query {@link Expression} instance using the given field and the default 'text', 'exact', 'prefix' field alias values.
	 * 
	 * @param field - the field to perform the term filter search on
	 * @return an executable {@link Expression} instance, never <code>null</code>
	 * @see #toExpression(String, String, String, String)
	 */
	@JsonIgnore
	public Expression toExpression(String field) {
		return toExpression(field, "text", "exact", "prefix");
	}
	
	/**
	 * Converts this {@link TermFilter} instance into an executable low-level index query {@link Expression} instance using the given field and field alias suffixes.
	 * 
	 * @param field - the field to perform the term filter search on
	 * @param textFieldSuffix - the tokenized text field alias to perform tokenized match on
	 * @param exactFieldSuffix - the exact text field alias to perform exact match on
	 * @param prefixFieldSuffix - the prefix text field suffix to perform prefix match on
	 * @return an executable {@link Expression} instance, never <code>null</code>
	 * @see #toExpression(String)
	 */
	@JsonIgnore
	public abstract Expression toExpression(String field, String textFieldSuffix, String exactFieldSuffix, String prefixFieldSuffix);
	
	@JsonIgnore
	protected final String fieldAlias(String field, String fieldSuffix) {
		return String.join(".", field, fieldSuffix);
	}
	
}
