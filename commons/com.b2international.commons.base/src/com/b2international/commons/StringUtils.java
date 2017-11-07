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
package com.b2international.commons;

import static com.google.common.base.Preconditions.checkNotNull;

import java.text.Normalizer;
import java.util.Collections;
import java.util.Iterator;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

/**
 * Contains utility methods for manipulating strings. 
 * 
 */
public class StringUtils {
	
	/**Shared empty string.*/
	public static final String EMPTY_STRING = "";
	
	private static final String ELLIPSIS = "...";

	/**
	 * Truncates the input description to maximum 100 character length.
	 * 
	 * @param description
	 * @return
	 */
	public static String truncate(final String description) {
		return truncate(description, 100);
	}

	/**
	 * Truncates the input description to maximize its length defined by the maxLength parameter. Appends three extra
	 * dots at the end of the result
	 * 
	 * @param description
	 * @return
	 */
	public static String truncate(final String description, final int maxLength) {
		if (description == null || description.length() <= maxLength) {
			return description;
		} else {
			return description.substring(0, maxLength - ELLIPSIS.length()) + ELLIPSIS;
		}
	}
	
	/**
	 * Returns the human-readable representation of a camelCase string.
	 * Example: camelCaseExample -> camel Case Example or
	 * complexXMLResolver -> complex XML Resolver
	 * 
	 * @param stringToSplit
	 * @return
	 */
	public static String splitCamelCase(final String s) {
		
		return s.replaceAll(String.format("%s|%s|%s",
				"(?<=[A-Z])(?=[A-Z][a-z])", 
				"(?<=[^A-Z])(?=[A-Z])",
				"(?<=[A-Za-z])(?=[^A-Za-z])"), 
				" ");
	}
	
	/**
	 * Returns the human-readable and capitalized representation of a camelCase string.
	 * Example: camelCaseExample -> Camel Case Example or
	 * complexXMLResolver -> Complex XML Resolver
	 * 
	 * @param stringToSplit
	 * @return
	 */
	public static String splitCamelCaseAndCapitalize(final String stringToSplit) {
		final String splitString = splitCamelCase(stringToSplit);
		return capitalizeFirstLetter(splitString);
	}
	
	/**
	 * Capitalizes the first letter of the passed in string. If the passed word
	 * is an empty word or contains only whitespace characters, then this passed
	 * word is returned. If the first letter is already capitalized returns the
	 * passed word. Otherwise capitalizes the first letter of the this word.
	 * 
	 * @param word
	 * @return
	 */
	public static String capitalizeFirstLetter(final String word) {
		if (isEmpty(word)) return word;
		if (Character.isUpperCase(word.charAt(0)))
			return word;
		if (word.length() == 1)
			return word.toUpperCase();
		return Character.toUpperCase(word.charAt(0)) + word.substring(1);
	}
	
	/**
	 * Transforms the first letter of the argument into a lower case. If the argument
	 * is an empty string or contains only whitespace characters, then returns with the argument.
	 * If the first letter is already lower cased it returns the argument.
	 * @param arg the string to lower case the first letter.
	 * @return the modified string
	 */
	public static String lowerCaseFirstLetter(final String arg) {
		if (isEmpty(arg)) {
			return arg;
		}
		if (Character.isLowerCase(arg.charAt(0))) {
			return arg;
		}
		if (arg.length() == 1) {
			return arg.toUpperCase();
		}
		return Character.toLowerCase(arg.charAt(0)) + arg.substring(1);
	}

	public static boolean isEmpty(final String string) {
		if (string == null || string.length() == 0) {
			return true;
		}
		
		for (int i = 0; i < string.length(); i++) {
			if (!Character.isWhitespace(string.charAt(i))) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Gets the specified line from string. A null separator will leave
	 * the string as-is, with only 1 line available.
	 * 
	 * 
	 * @param string the string to split
	 * @param separator the separator to split the lines by, e.g. "\n"
	 * @param lineNumber index of line to return, starting with 0
	 * @return specified line, or null if string is null, or lineNumber falls our of range.
	 */
	public static String getLine(final String string, final String separator, final int lineNumber) {
		
		if(string == null) {
			return null;
		}
		if(lineNumber < 0) {
			return null;
		}
		if(separator == null) {
			return lineNumber == 0 ? string : null;
		}
		
		int from = 0;
		int to = string.indexOf(separator, from);
		if(to < 0) {
			return string;
		}
		for(int i = 0; i < lineNumber; i++) {
			from = to + separator.length();
			to = string.indexOf(separator, from);
		}
		if(from >= 0 && to >= 0) {
			return string.substring(from, to);
		}
		return null;
	}
	
	/**
	 * Trims a string if it is not null.
	 * 
	 * @param string
	 * @return
	 */
	public static String trimIfNotNull(final String string){
		return null == string ? null : string.trim();
	}
	
	/**
	 * Adds the string representation of the first 10 elements of an
	 * {@link Iterable} to a string builder separated by commas, and appends a
	 * remaining item count, useful for {@link #toString()} output when the
	 * iterable's size is expected to be large. {@link String#valueOf(Object)}
	 * is used for converting individual iterable elements to their string
	 * representation.
	 * 
	 * @param iterable
	 *            the iterable to convert (may not be {@code null})
	 * 
	 * @return a comma separated list of the first 10 elements of the iterable
	 *         and a remaining item count as a string
	 */
	public static String toString(final Iterable<?> iterable) {
	
		checkNotNull(iterable, "iterable");
		
		final int limit = 10;
		final Iterator<?> iterator = iterable.iterator();
		final StringBuilder builder = new StringBuilder();
		int idx = 0;
		
		builder.append('[');
		
		while (idx < limit && iterator.hasNext()) {
			if (idx++ > 0) builder.append(", ");
			builder.append(String.valueOf(iterator.next()));
		}
		
		while (iterator.hasNext()) {
			idx++;
			iterator.next();
		}
		
		if (idx > limit) {
			builder.append(", ");
			builder.append(idx - limit);
			builder.append(" more...");
		}
		
		builder.append(']');
		
		return builder.toString();
	}
	
	/**
	 * Returns {@code true} if the specified string contains at least one single numeric character. Otherwise
	 * returns {@code false}.
	 * @param s the string to check. Can be {@code null}. If {@code null} this method returns {@code true}.
	 * @return {@code true} if specified string contains numeric character.
	 */
	public static boolean containsNumeric(final String s) {
		if (isEmpty(s)) {
			return false;
		}
		
		for (final char c : s.toCharArray()) {
			if (Character.isDigit(c)) {
				return true;
			}
		}
		
		return false;
		
	}
	
	/**
	 * Turns accented characters in the specified string into their non-accented
	 * counterparts.
	 * 
	 * @param s the string to convert (may not be {@code null})
	 * @return the de-accented string
	 */
	public static String removeDiacriticals(final String s) {
		return Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	}
	
	/**
	 * Compares if the two arrays of Strings are equal on an item-by-item basis, without taking case differences
	 * into account. 
	 * 
	 * @param first the first array to compare (may be {@code null}; items may be {@code null})
	 * @param second the second array to compare (may be {@code null}; items may be {@code null})
	 * @return {@code true} if the String arrays are equal, ignoring case differences, {@code false} otherwise
	 */
	public static boolean equalsIgnoreCase(final String[] first, final String[] second) {
		
		if (null == first) {
			// The return value can only be true if second is also null, but we are not interested in further testing if it isn't
			return null == second;
		}
		
		// First may not be null at this point
		if (null == second) {
			return false;
		}
		
		// Both arrays are non-null; sizes have to match
		if (first.length != second.length) {
			return false;
		}
		
		for (int i = 0; i < first.length; i++) {
			
			if (first[i] == null) {
				if (second[i] != null) {
					return false;
				} else {
					// Second[i] is null, so go to the next element pair of the arrays
					continue;
				}
			}
			
			// First[i] is not null at this point, so if second[i] is not null, exit immediately
			if (second[i] == null) {
				return false;
			}
			
			if (!first[i].equalsIgnoreCase(second[i])) {
				return false;
			}
		}
		
		return true;
	}
	
	public static Iterable<String> getWords(final String s) {
		if (!isEmpty(s)) {
			return Splitter.on(CharMatcher.BREAKING_WHITESPACE).split(s);
		}
		return Collections.emptyList();
	}
	
	public static boolean isSingleWord(final String s) {
		if (!isEmpty(s)) {
			return CharMatcher.BREAKING_WHITESPACE.matchesNoneOf(s);
		}
		return false;
	}
	
	public static boolean endsWithDigit(final String s) {
		if (!isEmpty(s)) {
			return Character.isDigit(s.charAt(s.length() - 1));
		}
		return false;
	}
	
	/**
	 * Returns with an empty string is the argument is {@code null} otherwise 
	 * returns with {@link String#valueOf(Object)}.
	 * @param object an object.
	 * @return empty string is the object is {@code null} otherwise with the 
	 * {@link Object#toString()} of the argument.
	 */
	public static String valueOfOrEmptyString(final Object object) {
		return null == object ? EMPTY_STRING : String.valueOf(object);
	}

	/**
	 * Creates a toString value from the given values but limiting the output to limit number of items in the form of [1, 2, 3, , limit... x more
	 * items], where x is the remaining number of items in the given {@link Iterable}.
	 * 
	 * @param values
	 * @param limit
	 * @return
	 */
	public static <T> String limitedToString(Iterable<T> values, int limit) {
		final StringBuilder builder = new StringBuilder("[");

		int remaining = limit;
		Iterator<T> it = values.iterator();
		while (it.hasNext() && remaining > 0) {
			if (builder.length() != 1) {
				builder.append(", ");
			}
			builder.append(it.next());
			remaining--;
		}

		builder.append(String.format("... %d more items", Iterables.size(values) - limit));
		builder.append("]");
		
		return builder.toString();
	}
}
