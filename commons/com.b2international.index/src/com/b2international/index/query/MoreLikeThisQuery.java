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

/**
 * @since 8.4.0
 */
public final class MoreLikeThisQuery implements Expression {

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
	

	MoreLikeThisQuery(Iterable<String> fields, Iterable<String> likeTexts, Iterable<String> unlikeTexts) {
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

	public void setMaxQueryTerms(int maxQueryTerms) {
		this.maxQueryTerms = maxQueryTerms;
	}
	
	public int getMinTermFreq() {
		return minTermFreq;
	}
	
	public void setMinTermFreq(int minTermFreq) {
		this.minTermFreq = minTermFreq;
	}
	
	public int getMinDocFreq() {
		return minDocFreq;
	}
	
	public void setMinDocFreq(int minDocFreq) {
		this.minDocFreq = minDocFreq;
	}
	
	public int getMaxWordLength() {
		return maxWordLength;
	}
	
	public void setMaxWordLength(int maxWordLength) {
		this.maxWordLength = maxWordLength;
	}
	
	public int getMinWordLength() {
		return minWordLength;
	}
	
	public void setMinWordLength(int minWordLength) {
		this.minWordLength = minWordLength;
	}
	
	public String getMinimumShouldMatch() {
		return minimumShouldMatch;
	}
	
	public void setMinimumShouldMatch(String minimumShouldMatch) {
		this.minimumShouldMatch = minimumShouldMatch;
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