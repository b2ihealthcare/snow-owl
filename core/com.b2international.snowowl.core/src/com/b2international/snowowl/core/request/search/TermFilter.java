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
import com.google.common.collect.Iterables;

/**
 * Represents a {@link Serializable} encapsulation of term search configuration.
 * 
 * @since 7.11
 */
public abstract class TermFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	public static MatchTermFilter.Builder match() {
		return new MatchTermFilter.Builder();
	}
	
	public static ExactTermFilter.Builder exact() {
		return new ExactTermFilter.Builder();
	}
	
	public static ParsedTermFilter.Builder parsed() {
		return new ParsedTermFilter.Builder();
	}
	
	public static MoreLikeThisTermFilter.Builder mlt() {
		return new MoreLikeThisTermFilter.Builder();
	}

	/**
	 * Get the search term(s) from this term filter. Certain implementations only support a single term to be defined.
	 * 
	 * @return a collection of search terms, never <code>null</code>
	 */
	public abstract Set<String> getTerms();
	
	public final String getSingleTermOrNull() {
		final Set<String> terms = getTerms();
		return terms.size() == 1 ? Iterables.getOnlyElement(terms) : null;
	}
	
	public Expression toExpression(String field) {
		return toExpression(field, "text", "exact", "prefix");
	}
	
	public abstract Expression toExpression(String field, String textFieldSuffix, String exactFieldSuffix, String prefixFieldSuffix);
	
	protected final String fieldAlias(String field, String textFieldSuffix) {
		return String.join(".", field, textFieldSuffix);
	}
	
//	/**
//	 * The <b>default</b> term filter contains the following elastic search expressions:
//	 * <p>
//	 * <li>Exact term match on a case insensitive, ASCII folded keyword type field (usually the <b>term.exact</b> field)
//	 * <li>All terms present match on a case insensitive, ASCII folded, possessive removed, split text type field (usually the <b>term</b> field)
//	 * <li>All term prefixes present match on a case insensitive, ASCII folded, possessive removed and split text type field where the field has 2-12 additional prefix terms generated via the edge_ngram token filter (usually the <b>term.prefix</b> field)
//	 * 
//	 * @param term
//	 * 			- the term to apply the expressions on
//	 * @return {@link TermFilter}
//	 */
//	@JsonIgnore
//	public static final TermFilter defaultTermMatch(final String term) {
//		return builder().term(term).build();
//	}
//	
//	/**
//	 * The <b>minTermMatch</b> term filter contains the following elastic search expressions:
//	 * <p>
//	 * <li>All terms present match on a case insensitive, ASCII folded, possessive removed, split text type field (usually the <b>term</b> field)
//	 * <li>All term prefixes present match on a case insensitive, ASCII folded, possessive removed and split text type field where the field has 2-12 additional prefix terms generated via the edge_ngram token filter (usually the <b>term.prefix</b> field)
//	 * 
//	 * @param term
//	 * 			- the term to apply the expressions on 
//	 * @param minShouldMatch
//	 * 			- the number of tokens to match
//	 * @return {@link TermFilter}
//	 */
//	@JsonIgnore
//	public static final TermFilter minTermMatch(final String term, final Integer minShouldMatch) {
//		if (minShouldMatch >= 1) {
//			return builder().term(term).minShouldMatch(minShouldMatch).build();
//		} else {
//			throw new BadRequestException("minShouldMatch parameter must be greater than or equal to 1. It was '%s'.", minShouldMatch);
//		}
//	}
//	
//	/**
//	 * The <b>fuzzyMatch</b> term filter contains the following elastic search expression:
//	 * 
//	 * <li>All terms present match on a case insensitive, ASCII folded, possessive removed, split text type field (usually the <b>term</b> field) with fuzziness enabled with hardcoded 10 expansions of 1 character difference (Levenshtein distance). 
//	 * 
//	 * @param term
//	 * 			- the term to apply the expressions on
//	 * @return {@link TermFilter}
//	 */
//	@JsonIgnore
//	public static final TermFilter fuzzyMatch(final String term) {
//		return builder().term(term).fuzzy(true).build();
//	}
//	
//	/**
//	 * The <b>exactTermMatch</b> term filter contains the following elastic search expression:
//	 * 
//	 * <li>Documents that contain an <b>exact</b> term in a provided field (usually the <b>term</b> field)
//	 * 
//	 * @param term
//	 * 			- the term to apply the expressions on
//	 * @return {@link TermFilter}
//	 */
//	@JsonIgnore
//	public static final TermFilter exactTermMatch(final String term) {
//		return builder().term(term).exact(true).caseSensitive(true).build();
//	}
//
//	/**
//	 * The <b>exactTermMatch</b> term filter contains the following elastic search expression:
//	 * 
//	 * <li>Exact term match on a case insensitive, ASCII folded keyword type field (usually the <b>term.exact</b> field)
//	 * 
//	 * @param term
//	 * 			- the term to apply the expressions on
//	 * @return {@link TermFilter}
//	 */
//	@JsonIgnore
//	public static final TermFilter exactIgnoreCaseTermMatch(final String term) {
//		return builder().term(term).exact(true).build();
//	}
//	
//	/**
//	 * @param term
//	 * 			- the term to apply the expressions on
//	 * @return {@link TermFilter}
//	 */
//	@JsonIgnore
//	public static final TermFilter parsedTermMatch(final String term) {
//		return builder().term(term).parsed(true).build();
//	}

}
