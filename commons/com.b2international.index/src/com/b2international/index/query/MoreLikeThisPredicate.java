/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.query;

import java.util.List;
import java.util.Set;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.collections.Collections3;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;

/**
 * @since 8.5.0
 */
public final class MoreLikeThisPredicate implements Expression {

	private Set<String> fields;
	private List<String> likeTexts;
	private List<String> unlikeTexts;
	
	// default MLT value in ES API
	private int maxQueryTerms = 25;
	private int minWordLength = 0;
	private int maxWordLength = 0;
	private String minimumShouldMatch = "30%";
	
	// different than the default in ES API to provide more search results without configuration
	private int minTermFreq = 1;
	private int minDocFreq = 1;
	

	MoreLikeThisPredicate(Iterable<String> fields, Iterable<String> likeTexts, Iterable<String> unlikeTexts) {
		this.fields = Collections3.toImmutableSet(fields);
		this.likeTexts = Collections3.toImmutableList(likeTexts);
		this.unlikeTexts = Collections3.toImmutableList(unlikeTexts);
	}

	public Set<String> getFields() {
		return fields;
	}
	
	public List<String> getLikeTexts() {
		return likeTexts;
	}
	
	public List<String> getUnlikeTexts() {
		return unlikeTexts;
	}
	
	public int getMaxQueryTerms() {
		return maxQueryTerms;
	}

	public MoreLikeThisPredicate withMaxQueryTerms(Integer maxQueryTerms) {
		this.maxQueryTerms = maxQueryTerms != null ? maxQueryTerms : 25;
		return this;
	}
	
	public int getMinTermFreq() {
		return minTermFreq;
	}
	
	public MoreLikeThisPredicate withMinTermFreq(Integer minTermFreq) {
		this.minTermFreq = minTermFreq != null ? minTermFreq : 1;
		return this;
	}
	
	public int getMinDocFreq() {
		return minDocFreq;
	}
	
	public MoreLikeThisPredicate withMinDocFreq(Integer minDocFreq) {
		this.minDocFreq = minDocFreq != null ? minDocFreq : 1;
		return this;
	}
	
	public int getMaxWordLength() {
		return maxWordLength;
	}
	
	public MoreLikeThisPredicate withMaxWordLength(Integer maxWordLength) {
		this.maxWordLength = maxWordLength != null ? maxWordLength : 0;
		return this;
	}
	
	public int getMinWordLength() {
		return minWordLength;
	}
	
	public MoreLikeThisPredicate withMinWordLength(Integer minWordLength) {
		this.minWordLength = minWordLength != null ? minWordLength : 0;
		return this;
	}
	
	public String getMinimumShouldMatch() {
		return minimumShouldMatch;
	}
	
	public MoreLikeThisPredicate withMinimumShouldMatch(String minimumShouldMatch) {
		this.minimumShouldMatch = !Strings.isNullOrEmpty(minimumShouldMatch) ? minimumShouldMatch : "30%";
		return this;
	}

	@Override
	public String toString() {
		final String fields = CompareUtils.isEmpty(this.fields) ? "" 
				: String.format(" FIELDS( %s )", Joiner.on(", ").join(this.fields));
		final String likeTexts = CompareUtils.isEmpty(this.likeTexts) ? "" 
				: String.format(" LIKE( %s )", Joiner.on(", ").join(this.likeTexts));
		final String unlikeTexts = CompareUtils.isEmpty(this.unlikeTexts) ? "" 
				: String.format(" UNLIKE( %s )", Joiner.on(", ").join(this.unlikeTexts));
		return String.format("MLT(%s, %s, %s)", fields, likeTexts, unlikeTexts);
	}

}