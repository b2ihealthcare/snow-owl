/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.analyzer;

import org.apache.lucene.analysis.util.CharTokenizer;

import com.b2international.index.compat.TextConstants;
import com.google.common.base.CharMatcher;

/**
 * A character-oriented tokenizer which splits tokens on whitespace and delimiters enumerated in
 * {@link IndexUtils#DELIMITERS}, and also converts characters to lower case in the normalization phase.
 * 
 */
public class DelimiterTokenizer extends CharTokenizer {

	// Excludes 2000-2000a, which is handled as a range
	private static final String BREAKING_WHITESPACE_CHARS = "\t\n\013\f\r \u0085\u1680\u2028\u2029\u205f\u3000";

	// Excludes 2007, which is handled as a gap in a pair of ranges
	private static final String NON_BREAKING_WHITESPACE_CHARS = "\u00a0\u180e\u202f";
	
	/**
	 * @see TextConstants#WHITESPACE_OR_DELIMITER_MATCHER
	 */
	private static final CharMatcher TOKEN_CHARS;
	
	static {
		TOKEN_CHARS = CharMatcher.ANY
			.and(CharMatcher.anyOf(BREAKING_WHITESPACE_CHARS))
			.and(CharMatcher.anyOf(NON_BREAKING_WHITESPACE_CHARS))
			.and(CharMatcher.anyOf(TextConstants.DELIMITERS))
			.and(CharMatcher.inRange('\u2000', '\u200a'))
			.precomputed();
	}
	
	public DelimiterTokenizer() {
		super();
	}
	
	@Override
	protected int normalize(int c) {
		return Character.toLowerCase(c);
	}
	
	@Override
	protected boolean isTokenChar(int c) {
		// We don't have whitespace characters to match in the supplementary code point range
		return c >= Character.MIN_SUPPLEMENTARY_CODE_POINT || !TOKEN_CHARS.matches((char) c);
	}
}
