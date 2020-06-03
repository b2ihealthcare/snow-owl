/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.commons;

import static com.google.common.base.Strings.nullToEmpty;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;

/**
 * Alpha-numeric comparator that treats digits and number in String as numbers and digits when comparing them.
 */
public class AlphaNumericComparator implements Comparator<String> {

	/*returns true if the char argument is a number*/
	private static final boolean isDigit(final char c) {
		return CharMatcher.digit().matches(c);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(final String o1, final String o2) {
		final char[] s1Array = nullToEmpty(o1).toCharArray();
		final char[] s2Array = nullToEmpty(o2).toCharArray();
		
		int s1Marker = 0;
		int s2Marker = 0;
		
		while (s1Marker < s1Array.length && s2Marker < s2Array.length) {
			final StringBuilder s1Chunk = readAndGetChunk(s1Array, s1Marker);
			s1Marker += s1Chunk.length();
			
			final StringBuilder s2Chunk = readAndGetChunk(s2Array, s2Marker);
			s2Marker += s2Chunk.length();
			
			int result = 0;
			if (isDigit(s1Chunk.charAt(0)) && isDigit(s2Chunk.charAt(0))) {
				result = s1Chunk.length() - s2Chunk.length();
				if (0 == result) {
					for (int i = 0; i < s1Chunk.length(); i++) {
						result = s1Chunk.charAt(i) - s2Chunk.charAt(i);
						if (0 != result)
							return result;
					}
				}
			} else {
				result = s1Chunk.toString().compareTo(s2Chunk.toString());
			}
			
			if (0 != result)
				return result;
		}

		return s1Array.length - s2Array.length;
	}

	private final StringBuilder readAndGetChunk(final char[] charArray, int marker) {
		final StringBuilder chunk = new StringBuilder(charArray.length);
		char c = charArray[marker++];
		chunk.append(c);
		if (isDigit(c)) {
			while (marker < charArray.length) {
				if (!isDigit(charArray[marker]))
					break;
				chunk.append(charArray[marker++]);
			}
		} else {
			while (marker < charArray.length) {
				if (isDigit(charArray[marker])) 
					break;
				chunk.append(charArray[marker++]);
			}
		}
		return chunk;
	}
	
	/**
	 * For testing.
	 * @param args
	 */
	public static void main(final String[] args) {
		final List<String> testList = Lists.newArrayList(
				"1000X Radonius Maximus",
				"10X Radonius",
				"200X Radonius",
				"20X Radonius",
				"20X Radonius Prime",
				"30X Radonius",
				"40X Radonius",
				"Allegia 50 Clasteron",
				"Allegia 500 Clasteron",
				"Allegia 50B Clasteron",
				"Allegia 51 Clasteron",
				"Allegia 6R Clasteron",
				"Alpha 100",
				"Alpha 2",
				"Alpha 200",
				"Alpha 2A",
				"Alpha 2A-8000",
				"Alpha 2A-900",
				"Callisto Morphamax",
				"Callisto Morphamax 500",
				"Callisto Morphamax 5000",
				"Callisto Morphamax 600",
				"Callisto Morphamax 6000 SE",
				"Callisto Morphamax 6000 SE2",
				"Callisto Morphamax 700",
				"Callisto Morphamax 7000",
				"Xiph Xlater 10000",
				"Xiph Xlater 2000",
				"Xiph Xlater 300",
				"Xiph Xlater 40",
				"Xiph Xlater 5",
				"Xiph Xlater 50",
				"Xiph Xlater 500",
				"Xiph Xlater 5000",
				"Xiph Xlater 58"
				);
		
		System.out.println("String compare to...");
		Collections.sort(testList);
		for (final String s : testList) {
			System.out.println(s);
		}

		System.out.println("\n\nComparison with com.b2international.commons.AlphaNumericComparator...");
		Collections.sort(testList, new AlphaNumericComparator());
		for (final String s  : testList) {
			System.out.println(s);
		}
					
				
	}
	
}