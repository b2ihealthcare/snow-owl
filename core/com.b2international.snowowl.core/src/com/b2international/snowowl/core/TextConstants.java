/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core;

import java.util.Set;

import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableSet;

public abstract class TextConstants {

	/**
	 * Defines a set of words that are not added to the index during the analyzing process; they must also be removed
	 * from query strings as well when matching for exact terms.
	 * <p>
	 * The original set of stop words are defined in <i>zres_ExcludedWords_en-US_INT_20070731.txt</i> in the International
	 * RF1 SNOMED CT distribution.
	 */
	public static final Set<String> STOPWORDS = ImmutableSet.of("about", "alongside", "an", "and", "anything",
			"around", "as", "at", "because", "before", "being", "both", "by", "cannot", "chronically", "consists",
			"covered", "does", "during", "every", "finds", "for", "from", "in", "instead", "into", "more", "must",
			"no", "not", "of", "on", "only", "or", "properly", "side", "sided", "some", "something", "specific",
			"than", "that", "the", "things", "this", "throughout", "to", "up", "using", "usually", "when", "while",
			"without");
	
	/**
	 * Holds a set of characters that should be considered as a token separator (non-token character) when splitting
	 * strings into tokens for analyzing and querying. Care should be taken to also check for
	 * {@link Character#isWhitespace(int) whitespace characters} when using this set.
	 * <p>
	 * The original set can be found in <i>Draft guidance for clinician-facing SNOMED browsers</i> by Jeremy Rogers,
	 * UKTC; note that the set has been extended with additional characters.
	 */
	public static final String DELIMITERS = "()[]/,.:;%#&+-*~'^><=\"`";

	/**
	 * A {@link CharMatcher} that matches all characters in {@link #DELIMITERS}.
	 */
	public static final CharMatcher DELIMITER_MATCHER = CharMatcher.anyOf(DELIMITERS);

	/**
	 * A {@link CharMatcher} that matches all characters in {@link #DELIMITERS} as well as all whitespace characters.
	 */
	public static final CharMatcher WHITESPACE_OR_DELIMITER_MATCHER = CharMatcher.WHITESPACE.or(DELIMITER_MATCHER);

	private TextConstants() {
		// Prevent instantiation
	}
}