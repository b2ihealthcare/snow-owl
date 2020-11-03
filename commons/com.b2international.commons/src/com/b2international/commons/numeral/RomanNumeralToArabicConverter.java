/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.commons.numeral;

import java.util.regex.Pattern;

/**
 * Class for converting Roman numeral to Arabic number.
 */
public abstract class RomanNumeralToArabicConverter {
	
	public static final Pattern CONTAINS_ROMAN_NUMBER_REGEX = Pattern.compile("^.*\\b(^(?=[MDCLXVI])M*(C[MD]|D?C*)(X[CL]|L?X*)(I[XV]|V?I*))\\b.*$");

	private static int decodeSingle(final char letter) {
		switch (letter) {
			case 'M':
				return 1000;
			case 'D':
				return 500;
			case 'C':
				return 100;
			case 'L':
				return 50;
			case 'X':
				return 10;
			case 'V':
				return 5;
			case 'I':
				return 1;
			default:
				return 0;
		}
	}

	/**
	 * Converts a Roman number to Arabic integer.
	 * @param roman the Roman number as a string literal.
	 * @return the Arabic representation of the Roman number as an {@code int}. 
	 */
	public static int decode(final String roman) {
		int result = 0;
		final String uRoman = roman.toUpperCase(); // case-insensitive
		for (int i = 0; i < uRoman.length() - 1; i++) {
			// loop over all but the last character
			// if this character has a lower value than the next character
			if (decodeSingle(uRoman.charAt(i)) < decodeSingle(uRoman.charAt(i + 1))) {
				// subtract it
				result -= decodeSingle(uRoman.charAt(i));
			} else {
				// add it
				result += decodeSingle(uRoman.charAt(i));
			}
		}
		// decode the last character, which is always added
		result += decodeSingle(uRoman.charAt(uRoman.length() - 1));
		return result;
	}

}