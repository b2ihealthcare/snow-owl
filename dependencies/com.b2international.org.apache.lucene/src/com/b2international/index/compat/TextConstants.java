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
package com.b2international.index.compat;

import com.google.common.base.CharMatcher;

/**
 * Holds constants related to text manipulation in terminology indexes.
 */
public abstract class TextConstants {
	
	/**
	 * Holds a set of characters that should be considered as a token separator (non-token character) when splitting
	 * strings into tokens for analyzing and querying. Care should be taken to also check for
	 * {@link Character#isWhitespace(int) whitespace characters} when using this set.
	 * <p>
	 * The original set can be found in <i>Draft guidance for clinician-facing SNOMED browsers</i> by Jeremy Rogers,
	 * UKTC; note that the set has been extended with additional characters.
	 */
	public static final String DELIMITERS = "()[]/,.:;%#&+-*~'^><=\"`";

	private static final CharMatcher INTERNAL_DELIMITER_MATCHER = CharMatcher.anyOf(DELIMITERS);

	/**
	 * A {@link CharMatcher} that matches all characters in {@link #DELIMITERS}.
	 */
	public static final CharMatcher DELIMITER_MATCHER = INTERNAL_DELIMITER_MATCHER.precomputed();

	/**
	 * A {@link CharMatcher} that matches all characters in {@link #DELIMITERS} as well as all whitespace characters.
	 */
	public static final CharMatcher WHITESPACE_OR_DELIMITER_MATCHER = INTERNAL_DELIMITER_MATCHER.or(CharMatcher.WHITESPACE).precomputed();

	private TextConstants() {
		throw new UnsupportedOperationException("This class is not supposed to be instantiated.");
	}

	/**
	 * Escapes invalid characters in the given searchTerm and returns the escaped form.
	 * 
	 * @param string
	 * @return
	 */
	public static String escape(final String string) {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < string.length(); i++) {
			final char c = string.charAt(i);
			outerLoop: {
				switch (c) {
				case '\\':
					if (i != string.length() - 1) {
						final char charAt = string.charAt(i + 1);
						switch (charAt) {
						case '^':
						case '?':
						case '~':
						case '*':
							break outerLoop;
						}
					}
				case '+':
				case '-':
				case '!':
				case '(':
				case ')':
				case ':':
				case '[':
				case ']':
				case '\"':
				case '{':
				case '}':
				case '|':
				case '&':
				case '/':
					sb.append('\\');
					break;
				}
			}

			sb.append(c);

			switch (c) {
			case '~':
				sb.append(' ');
				break;
			}
		}

		return sb.toString();
	}

}
