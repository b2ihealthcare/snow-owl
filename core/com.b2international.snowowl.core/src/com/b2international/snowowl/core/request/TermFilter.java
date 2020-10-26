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

import java.io.Serializable;

import javax.validation.constraints.Min;

import com.b2international.commons.exceptions.BadRequestException;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Represents a {@link Serializable} encapsulation of term search configuration.
 * 
 * @since 7.11
 */
public final class TermFilter implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final String term;
	
	@Min(0)
	private final Integer minShouldMatch;
	
	private final boolean fuzzy;
	private final boolean exact;

	public TermFilter(final String term, final Integer minShouldMatch, final boolean fuzzy, final boolean exact) {
		this.term = term;
		this.minShouldMatch = minShouldMatch;
		this.fuzzy = fuzzy;
		this.exact = exact;
	}
	
	public String getTerm() {
		return term;
	}
	
	public Integer getMinShouldMatch() {
		return minShouldMatch;
	}
	
	public boolean isFuzzy() {
		return fuzzy;
	}
	
	public boolean isExact() {
		return exact;
	}
	
	public static final class Builder {
		private String term;
		private Integer minShouldMatch;
		private boolean fuzzy = false;
		private boolean exact = false;
		
		private Builder() { }
		
		public static final Builder builder() {
			return new Builder();
		}
		
		public final Builder term(final String term) {
			this.term = term;
			return this;
		}
		
		public final Builder minShouldMatch(final Integer minShouldMatch) {
			this.minShouldMatch = minShouldMatch;
			return this;
		}
		
		public final Builder fuzzy(final boolean fuzzy) {
			this.fuzzy = fuzzy;
			return this;
		}
		
		public final Builder exact(final boolean exact) {
			this.exact = exact;
			return this;
		}
		
		public final TermFilter build() {
			return new TermFilter(term, minShouldMatch, fuzzy, exact);
		}
	}
	
	/**
	 * The <b>default</b> term filter contains the following elastic search expressions:
	 * <p>
	 * <li> Exact term match on a case insensitive, ASCII folded keyword type field (usually the <b>term.exact</b> field)
	 * <li>All terms present match on a case insensitive, ASCII folded, possessive removed, split text type field (usually the <b>term</b> field)
	 * <li>All term prefixes present match on a case insensitive, ASCII folded, possessive removed and split text type field where the field has 2-12 additional prefix terms generated via the edge_ngram token filter (usually the <b>term.prefix</b> field)
	 * 
	 * @param term
	 * 			- the term to apply the expressions on
	 * @return {@link TermFilter}
	 */
	@JsonIgnore
	public static final TermFilter defaultTermMatch(final String term) {
		return Builder.builder().term(term).build();
	}
	
	/**
	 * The <b>minTermMatch</b> term filter contains the following elastic search expressions:
	 * <p>
	 * <li>All terms present match on a case insensitive, ASCII folded, possessive removed, split text type field (usually the <b>term</b> field)
	 * <li>All term prefixes present match on a case insensitive, ASCII folded, possessive removed and split text type field where the field has 2-12 additional prefix terms generated via the edge_ngram token filter (usually the <b>term.prefix</b> field)
	 * 
	 * @param term
	 * 			- the term to apply the expressions on 
	 * @param minShouldMatch
	 * 			- the number of tokens to match
	 * @return {@link TermFilter}
	 */
	@JsonIgnore
	public static final TermFilter minTermMatch(final String term, final Integer minShouldMatch) {
		if (minShouldMatch >= 1) {
			return Builder.builder().term(term).minShouldMatch(minShouldMatch).build();
		} else {
			throw new BadRequestException(String.format("minShouldMatch parameter must be greater than or equal to 1. It was %s ", minShouldMatch.toString()));
		}
	}
	
	/**
	 * The <b>fuzzyMatch</b> term filter contains the following elastic search expression:
	 * 
	 * <li>All terms present match on a case insensitive, ASCII folded, possessive removed, split text type field (usually the <b>term</b> field) with fuzziness enabled with hardcoded 10 expansions of 1 character difference (Levenshtein distance). 
	 * 
	 * @param term
	 * 			- the term to apply the expressions on
	 * @return {@link TermFilter}
	 */
	@JsonIgnore
	public static final TermFilter fuzzyMatch(final String term) {
		return Builder.builder().term(term).fuzzy(true).build();
	}
	
	/**
	 * The <b>exactTermMatch</b> term filter contains the following elastic search expression:
	 * 
	 * <li>Exact term match on a case insensitive, ASCII folded keyword type field (usually the <b>term.exact</b> field)
	 * 
	 * @param term
	 * 			- the term to apply the expressions on
	 * @return {@link TermFilter}
	 */
	@JsonIgnore
	public static final TermFilter exactTermMatch(final String term) {
		return Builder.builder().term(term).exact(true).build();
	}
}
