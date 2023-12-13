/*
 * Copyright 2022 B2i Healthcare, https://b2ihealthcare.com
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

import java.util.List;
import java.util.Set;

import com.b2international.commons.collections.Collections3;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;

/**
 * @since 8.5
 */
public final class MoreLikeThisTermFilter extends TermFilter {

	private static final long serialVersionUID = 1L;
	
	private final List<String> likeTexts;
	private final List<String> unlikeTexts;
	
	private final Integer maxQueryTerms;
	private final Integer minTermFreq;
	private final Integer minDocFreq;
	private final Integer minWordLength;
	private final Integer maxWordLength;
	private final String minimumShouldMatch;
	
	MoreLikeThisTermFilter(Iterable<String> likeText, Iterable<String> unlikeText, Integer maxQueryTerms, Integer minTermFreq, Integer minDocFreq, Integer minWordLength, Integer maxWordLength, String minimumShouldMatch) {
		this.likeTexts = Collections3.toImmutableList(likeText);
		this.unlikeTexts = Collections3.toImmutableList(unlikeText);
		this.maxQueryTerms = maxQueryTerms;
		this.minTermFreq = minTermFreq;
		this.minDocFreq = minDocFreq;
		this.minWordLength = minWordLength;
		this.maxWordLength = maxWordLength;
		this.minimumShouldMatch = minimumShouldMatch;
	}
	
	public List<String> getLikeTexts() {
		return likeTexts;
	}
	
	public List<String> getUnlikeTexts() {
		return unlikeTexts;
	}
	
	@Override
	public Set<String> getTerms() {
		return Set.copyOf(likeTexts);
	}
	
	@Override
	public Expression toExpression(String field, String textFieldSuffix, String exactFieldSuffix, String prefixFieldSuffix) {
		return Expressions.moreLikeThis(List.of(fieldAlias(field, textFieldSuffix)), getLikeTexts(), getUnlikeTexts())
				.withMaxQueryTerms(maxQueryTerms)
				.withMinTermFreq(minTermFreq)
				.withMinDocFreq(minDocFreq)
				.withMinWordLength(minWordLength)
				.withMaxWordLength(maxWordLength)
				.withMinimumShouldMatch(minimumShouldMatch);
	}
	
	public static final class Builder {
		
		private Iterable<String> likeTexts;
		private Iterable<String> unlikeTexts;
		
		private Integer maxQueryTerms;
		private Integer minTermFreq;
		private Integer minDocFreq;
		private Integer minWordLength;
		private Integer maxWordLength;
		private String minimumShouldMatch;
		
		Builder() {
		}
		
		Builder(MoreLikeThisTermFilter from) {
			this.likeTexts = from.getLikeTexts();
			this.unlikeTexts = from.getUnlikeTexts();
		}
		
		public Builder likeTexts(Iterable<String> likeTexts) {
			this.likeTexts = likeTexts;
			return this;
		}
		
		public Builder unlikeTexts(Iterable<String> unlikeTexts) {
			this.unlikeTexts = unlikeTexts;
			return this;
		}
		
		public Builder maxQueryTerms(Integer maxQueryTerms) {
			this.maxQueryTerms = maxQueryTerms;
			return this;
		}
		
		public Builder minTermFreq(Integer minTermFreq) {
			this.minTermFreq = minTermFreq;
			return this;
		}
		
		public Builder minDocFreq(Integer minDocFreq) {
			this.minDocFreq = minDocFreq;
			return this;
		}
		
		public Builder minWordLength(Integer minWordLength) {
			this.minWordLength = minWordLength;
			return this;
		}
		
		public Builder maxWordLength(Integer maxWordLength) {
			this.maxWordLength = maxWordLength;
			return this;
		}
		
		public Builder minimumShouldMatch(String minimumShouldMatch) {
			this.minimumShouldMatch = minimumShouldMatch;
			return this;
		}
		
		public MoreLikeThisTermFilter build() {
			return new MoreLikeThisTermFilter(likeTexts, unlikeTexts, maxQueryTerms, minTermFreq, minDocFreq, minWordLength, maxWordLength, minimumShouldMatch);
		}
		
	}
	
	

}
