/*
 * Copyright 2011-2024 B2i Healthcare, https://b2ihealthcare.com
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

import com.b2international.commons.CompareUtils;
import com.b2international.index.Analyzers;

/**
 * @since 4.7
 */
public final class TextPredicate extends Predicate {

	public enum MatchType {
		ALL, ANY, PHRASE, PARSED, BOOLEAN_PREFIX
	}
	
	private final String term;
	private final MatchType type;
	private final int minShouldMatch;
	
	private Analyzers analyzer;
	private Boolean synonymsEnabled;
	private Boolean ignoreStopwords;
	
	private String fuzziness;
	private int maxExpansions = 10;
	private int prefixLength = 1;
	
	TextPredicate(String field, String term, MatchType type) {
		this(field, term, type, 1);
	}
	
	TextPredicate(String field, String term, MatchType type, int minShouldMatch) {
		super(field);
		this.term = term;
		this.type = type;
		this.minShouldMatch = minShouldMatch;
	}
	
	public String term() {
		return term;
	}
	
	public MatchType type() {
		return type;
	}
	
	public int minShouldMatch() {
		return minShouldMatch;
	}
	
	public String analyzer() {
		return analyzer == null ? null : analyzer.getAnalyzer();
	}
	
	public TextPredicate withAnalyzer(Analyzers analyzer) {
		this.analyzer = analyzer;
		return this;
	}
	
	public String fuzziness() {
		return fuzziness;
	}
	
	public int maxExpansions() {
		return maxExpansions;
	}
	
	public int prefixLength() {
		return prefixLength;
	}
	
	public Boolean synonymsEnabled() {
		return synonymsEnabled;
	}
	
	public TextPredicate withSynonymsEnabled(Boolean synonymsEnabled) {
		this.synonymsEnabled = synonymsEnabled;
		return this;
	}
	
	public Boolean ignoreStopwords() {
		return ignoreStopwords;
	}
	
	public TextPredicate withIgnoreStopwords(Boolean ignoreStopwords) {
		this.ignoreStopwords = ignoreStopwords;
		return this;
	}
	
	public TextPredicate withFuzziness(String fuzziness) {
		return withFuzziness(fuzziness, prefixLength, maxExpansions);
	}
	
	public TextPredicate withFuzziness(String fuzziness, Integer prefixLength, Integer maxExpansions) {
		this.fuzziness = fuzziness;
		this.prefixLength = prefixLength != null ? prefixLength : 1;
		this.maxExpansions = maxExpansions != null ? maxExpansions : 10;
		return this;
	}
	
	@Override
	public String toString() {
		return String.format("TEXT(%s %s '%s'[])", getField(), type(), term(), CompareUtils.isEmpty(analyzer));
	}

}
