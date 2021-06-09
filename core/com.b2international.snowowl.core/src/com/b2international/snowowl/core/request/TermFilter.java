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

import java.io.Serializable;

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
	private final Integer minShouldMatch;
	
	private final boolean fuzzy;
	private final boolean exact;
	private final boolean parsed;
	private final boolean ignoreStopwords;
	private final boolean caseSensitive;

	public TermFilter(final String term, final Integer minShouldMatch, final boolean fuzzy, final boolean exact, final boolean parsed, final boolean ignoreStopwords, final boolean caseSensitive) {
		if (term == null) {
			throw new BadRequestException("'term' filter parameter was null.");
		}
		this.term = term.trim();
		this.minShouldMatch = minShouldMatch;
		this.fuzzy = fuzzy;
		this.exact = exact;
		this.parsed = parsed;
		this.ignoreStopwords = ignoreStopwords;
		this.caseSensitive = caseSensitive;
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
	
	public boolean isParsed() {
		return parsed;
	}
	
	public boolean isIgnoreStopwords() {
		return ignoreStopwords;
	}
	
	public boolean isCaseSensitive() {
		return caseSensitive;
	}
	
	public boolean isAnyMatch() {
		return getMinShouldMatch() != null;
	}
	
	public TermFilter withIgnoreStopwords() {
		return builder(this).ignoreStopwords(true).build();
	}
	
	public static final Builder builder(final TermFilter termFilter) {
		return new Builder()
				.term(termFilter.getTerm())
				.minShouldMatch(termFilter.getMinShouldMatch())
				.fuzzy(termFilter.isFuzzy())
				.exact(termFilter.isExact())
				.parsed(termFilter.isParsed())
				.ignoreStopwords(termFilter.isIgnoreStopwords());
	}
	
	/**
	 * @since 7.11
	 */
	public static final class Builder {
		private String term;
		private Integer minShouldMatch;
		private boolean fuzzy;
		private boolean exact;
		private boolean parsed;
		private boolean ignoreStopwords;
		private boolean caseSensitive;
		
		private Builder() { }
		
		public static final Builder builder() {
			return new Builder();
		}
		
		public Builder term(final String term) {
			this.term = term;
			return this;
		}
		
		public Builder minShouldMatch(final Integer minShouldMatch) {
			this.minShouldMatch = minShouldMatch;
			return this;
		}
		
		public Builder fuzzy(final boolean fuzzy) {
			this.fuzzy = fuzzy;
			return this;
		}
		
		public Builder exact(final boolean exact) {
			this.exact = exact;
			return this;
		}
		
		public Builder parsed(final boolean parsed) {
			this.parsed = parsed;
			return this;
		}
		
		public Builder ignoreStopwords(boolean ignoreStopwords) {
			this.ignoreStopwords = ignoreStopwords;
			return this;
		}

		public Builder caseSensitive(boolean caseSensitive) {
			this.caseSensitive = caseSensitive;
			return this;
		}
		
		public TermFilter build() {
			return new TermFilter(term, minShouldMatch, fuzzy, exact, parsed, ignoreStopwords, caseSensitive);
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
			throw new BadRequestException("minShouldMatch parameter must be greater than or equal to 1. It was '%s'.", minShouldMatch);
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
	 * <li>Documents that contain an <b>exact</b> term in a provided field (usually the <b>term</b> field)
	 * 
	 * @param term
	 * 			- the term to apply the expressions on
	 * @return {@link TermFilter}
	 */
	@JsonIgnore
	public static final TermFilter exactTermMatch(final String term) {
		return Builder.builder().term(term).exact(true).caseSensitive(true).build();
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
	public static final TermFilter exactIgnoreCaseTermMatch(final String term) {
		return Builder.builder().term(term).exact(true).build();
	}
	
	/**
	 * @param term
	 * 			- the term to apply the expressions on
	 * @return {@link TermFilter}
	 */
	@JsonIgnore
	public static final TermFilter parsedTermMatch(final String term) {
		return Builder.builder().term(term).parsed(true).build();
	}

}
