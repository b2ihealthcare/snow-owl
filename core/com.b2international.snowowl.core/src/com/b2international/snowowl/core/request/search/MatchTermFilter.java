/*
 * Copyright 2022-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.index.query.Expressions.dismaxWithScoreCategories;
import static com.b2international.index.query.Expressions.matchBooleanPrefix;
import static com.b2international.index.query.Expressions.matchTextAll;
import static com.b2international.index.query.Expressions.matchTextAny;

import java.util.Set;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @since 8.5
 */
public final class MatchTermFilter extends TermFilter {

	private static final long serialVersionUID = 1L;
	
	private final String term;
	private final Integer minShouldMatch;
	
	private final boolean ignoreStopwords;
	private final boolean caseSensitive;
	private final Boolean synonyms;
	
	private final String fuzziness;
	private final Integer prefixLength;
	private final Integer maxExpansions;
	
	MatchTermFilter(final String term, final Integer minShouldMatch, final boolean ignoreStopwords, final boolean caseSensitive, final Boolean synonyms, final String fuzziness, final Integer prefixLength, final Integer maxExpansions) {
		if (term == null) {
			throw new BadRequestException("'term' filter parameter was null.");
		}
		this.term = term.trim();
		this.minShouldMatch = minShouldMatch;
		this.ignoreStopwords = ignoreStopwords;
		this.caseSensitive = caseSensitive;
		this.synonyms = synonyms;
		this.fuzziness = fuzziness;
		this.prefixLength = prefixLength;
		this.maxExpansions = maxExpansions;
	}
	
	public String getTerm() {
		return term;
	}
	
	public Integer getMinShouldMatch() {
		return minShouldMatch;
	}
	
	public boolean isIgnoreStopwords() {
		return ignoreStopwords;
	}
	
	public boolean isCaseSensitive() {
		return caseSensitive;
	}
	
	public Boolean isSynonyms() {
		return synonyms;
	}
	
	public String getFuzziness() {
		return fuzziness;
	}
	
	public Integer getPrefixLength() {
		return prefixLength;
	}
	
	public Integer getMaxExpansions() {
		return maxExpansions;
	}
	
	@JsonIgnore
	public boolean isAnyMatch() {
		return getMinShouldMatch() != null;
	}
	
	public MatchTermFilter withIgnoreStopwords() {
		return new Builder(this).ignoreStopwords(true).build();
	}
	
	public MatchTermFilter withTerm(String newTerm) {
		return new Builder(this).term(newTerm).build();
	}
	
	@Override
	public Set<String> getTerms() {
		return Set.of(term);
	}
	
	@Override
	public Expression toExpression(String field, String textFieldSuffix, String exactFieldSuffix, String prefixFieldSuffix) {
		if (getFuzziness() != null) {
			return Expressions.matchTextAll(fieldAlias(field, textFieldSuffix), getTerm()).withFuzziness(fuzziness, prefixLength, maxExpansions);
		} else if (isAnyMatch()) {
			return minShouldMatchTermDisjunctionQuery(field, textFieldSuffix, exactFieldSuffix, prefixFieldSuffix);
		} else {
			return termDisjunctionQuery(field, textFieldSuffix, exactFieldSuffix, prefixFieldSuffix);
		}
	}
	
	public static final class Builder {
		
		private String term;
		private Integer minShouldMatch;
		
		private boolean ignoreStopwords;
		private boolean caseSensitive;
		private Boolean synonyms;
		
		private String fuzziness;
		private Integer prefixLength;
		private Integer maxExpansions;
		
		Builder() {
		}
		
		Builder(MatchTermFilter from) {
			this.term = from.getTerm();
			this.minShouldMatch = from.getMinShouldMatch();
			this.ignoreStopwords = from.isIgnoreStopwords();
			this.caseSensitive = from.isCaseSensitive();
			this.synonyms = from.isSynonyms();
			this.fuzziness = from.getFuzziness();
			this.prefixLength = from.getPrefixLength();
			this.maxExpansions = from.getMaxExpansions();
		}
		
		public Builder term(String term) {
			this.term = term;
			return this;
		}
		
		public Builder minShouldMatch(Integer minShouldMatch) {
			this.minShouldMatch = minShouldMatch;
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
		
		public Builder synonyms(Boolean synonyms) {
			this.synonyms = synonyms;
			return this;
		}
		
		public Builder fuzzy() {
			return fuzziness("AUTO");
		}
		
		public Builder fuzziness(String fuzziness) {
			this.fuzziness = fuzziness;
			return this;
		}
		
		public Builder prefixLength(Integer prefixLength) {
			this.prefixLength = prefixLength;
			return this;
		}
		
		public Builder maxExpansions(Integer maxExpansions) {
			this.maxExpansions = maxExpansions;
			return this;
		}
		
		public MatchTermFilter build() {
			return new MatchTermFilter(term, minShouldMatch, ignoreStopwords, caseSensitive, synonyms, fuzziness, prefixLength, maxExpansions);
		}

	}
	
	public Expression termDisjunctionQuery(String field, String textFieldSuffix, String exactFieldSuffix, String prefixFieldSuffix) {
		return dismaxWithScoreCategories(
			TermFilter.exact().term(getTerm()).caseSensitive(isCaseSensitive()).build().toExpression(field, textFieldSuffix, exactFieldSuffix, prefixFieldSuffix),
			matchTextAll(fieldAlias(field, textFieldSuffix), getTerm())
				.withIgnoreStopwords(isIgnoreStopwords())
				.withSynonyms(isSynonyms()),
			matchBooleanPrefix(fieldAlias(field, textFieldSuffix), getTerm())
				.withIgnoreStopwords(isIgnoreStopwords())
				.withSynonyms(isSynonyms()),
			matchTextAll(fieldAlias(field, prefixFieldSuffix), getTerm())
				.withIgnoreStopwords(isIgnoreStopwords())
		);
	}

	public Expression minShouldMatchTermDisjunctionQuery(String field, String textFieldSuffix, String exactFieldSuffix, String prefixFieldSuffix) {
		return dismaxWithScoreCategories(
			matchTextAny(fieldAlias(field, textFieldSuffix), getTerm(), getMinShouldMatch())
				.withIgnoreStopwords(isIgnoreStopwords())
				.withSynonyms(isSynonyms()),
			matchTextAny(fieldAlias(field, prefixFieldSuffix), getTerm(), getMinShouldMatch())
				.withIgnoreStopwords(isIgnoreStopwords())
				.withSynonyms(isSynonyms())
		);
	}

}
