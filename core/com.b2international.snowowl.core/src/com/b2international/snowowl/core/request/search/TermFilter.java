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
	 * The <b>default</b> term matching contains the following elastic search expressions:
	 * <p>
	 * <li>Exact term match on a case insensitive, ASCII folded keyword type field (usually the <b>term.exact</b> field)
	 * <li>All terms present match on a case insensitive, ASCII folded, possessive removed, split text type field (usually the <b>term</b> field)
	 * <li>All term prefixes present match on a case insensitive, ASCII folded, possessive removed and split text type field where the field has 2-12 additional prefix terms generated via the edge_ngram token filter (usually the <b>term.prefix</b> field)
	 * 
	 * The <b>minShouldMatch</b> term filter contains the following elastic search expressions:
	 * <p>
	 * <li>All terms present match on a case insensitive, ASCII folded, possessive removed, split text type field (usually the <b>term</b> field)
	 * <li>All term prefixes present match on a case insensitive, ASCII folded, possessive removed and split text type field where the field has 2-12 additional prefix terms generated via the edge_ngram token filter (usually the <b>term.prefix</b> field)
	 * 
	 * The <b>fuzziness</b> configuration alters the query to allow certain amount of fuzziness in the given terms:
	 * 
	 * <li>All terms present match on a case insensitive, ASCII folded, possessive removed, split text type field (usually the <b>term</b> field) with fuzziness enabled with hardcoded 10 expansions of 1 character difference (Levenshtein distance).
	 * 
	 * Additionally stopwords can be ignored, case sensitivity can be enabled/disabled and synonyms can be included if needed.
	 * 
	 * @return {@link MatchTermFilter.Builder}
	 */
	@JsonIgnore
	public static MatchTermFilter.Builder match() {
		return new MatchTermFilter.Builder();
	}
	
	/**
	 * The <b>exactTermMatch</b> term filter contains the following elastic search expression:
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
	
	@JsonIgnore
	public Expression toExpression(String field) {
		return toExpression(field, "text", "exact", "prefix");
	}
	
	@JsonIgnore
	public abstract Expression toExpression(String field, String textFieldSuffix, String exactFieldSuffix, String prefixFieldSuffix);
	
	@JsonIgnore
	protected final String fieldAlias(String field, String fieldSuffix) {
		return String.join(".", field, fieldSuffix);
	}
	
}
