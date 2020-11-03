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
package com.b2international.commons.numeral;

/**
 * Simple implementation of arabic to roman number converter.
 * 
 */
public abstract class ArabicToRomanNumeralConverter {

	private static int[] NUMBERS = { 1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1 };

	private static String[] LETTERS = { "M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I" };

	/**
	 * Returns the Roman numeral representation of the input integer value.
	 * @param arabic
	 * @return
	 * @throws NumberFormatException, if the numeral is not valid.
	 */
	public static String convert(final int arabic) {
		if (arabic < 1 || arabic > 3999) {
            throw new NumberFormatException("Value of RomanNumeral must be within the range 1..3999.");
		}
		String roman = "";
		int N = arabic;
		for (int i = 0; i < NUMBERS.length; i++) {
			while (N >= NUMBERS[i]) {
				roman += LETTERS[i];
				N -= NUMBERS[i];
			}
		}
		return roman;
	}

}