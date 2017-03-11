/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import com.b2international.index.analyzer.ComponentTermAnalyzer;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * @since 4.7
 */
public class Highlighting {

	/**
	 * Splits a string to a list of tokens using the specified Lucene analyzer.
	 * 
	 * @param analyzer the analyzer determining token boundaries (may not be {@code null})
	 * @param s the string to split
	 * @return a list of tokens, or an empty list if {@code s} is {@code null} or empty
	 */
	public static List<String> split(Analyzer analyzer, final String s) {
		
		checkNotNull(analyzer, "analyzer");
		
		if (Strings.isNullOrEmpty(s)) {
			return ImmutableList.of();
		}
		
		final List<String> tokens = Lists.newArrayList();
		TokenStream stream = null;
		
		try {
			
			stream = analyzer.tokenStream(null, new StringReader(s));
			stream.reset();
			
			while (stream.incrementToken()) {
				tokens.add(stream.getAttribute(CharTermAttribute.class).toString());
			}
			
		} catch (final IOException ignored) {
			// Should not be thrown when using a string reader
		} finally {
			endAndCloseQuietly(stream);
		}
		
		return tokens;
	}
	
	private static void endAndCloseQuietly(final TokenStream stream) {
		if (stream != null) {
			try (final TokenStream tokenStream = stream; ) {
				tokenStream.end();
			} catch (final IOException e) {
				// Should not be thrown when using a string reader
			}
		}
		
		
	}
	
	public static int[][] getMatchRegions(final String queryExpression, final String sortKeyLabel) {
		final String lcQuery = queryExpression.toLowerCase();
		final String lcLabel = sortKeyLabel.toLowerCase();
		
		final Analyzer analyzer = new ComponentTermAnalyzer(false, false);
		
		final List<String> filterTokens = split(analyzer, lcQuery);
		final List<String> labelTokens = split(analyzer, lcLabel);
		final List<int[]> elementMatchRegions = Lists.newArrayList();
		
		int startIndex = 0;
		
		for (final String labelToken : labelTokens) {
			
			startIndex = lcLabel.indexOf(labelToken, startIndex);
			
			final Iterator<String> itr = filterTokens.iterator();
			while (itr.hasNext()) {
				final String filterToken = itr.next();
				if (labelToken.startsWith(filterToken)) {
					// XXX: the same segment of text may be selected for highlighting multiple times, revisit if this causes problems
					elementMatchRegions.add(new int[] { startIndex, startIndex + filterToken.length() - 1 });
				}
			}
			
			// Move past this label token
			startIndex += labelToken.length();
		}
		return Iterables.toArray(elementMatchRegions, int[].class);
	}
	
	public static String[] getSuffixes(final String queryExpression, final String label) {
		final Splitter tokenSplitter = Splitter.on(TextConstants.WHITESPACE_OR_DELIMITER_MATCHER).omitEmptyStrings();
		final List<String> filterTokens = tokenSplitter.splitToList(queryExpression.toLowerCase());
		final boolean spaceAtTheEnd = !queryExpression.isEmpty() && Character.isWhitespace(queryExpression.charAt(queryExpression.length() - 1));
		final String lowerCaseLabel = label.toLowerCase();
		final Iterable<String> labelTokens = tokenSplitter.split(lowerCaseLabel);
		final List<String> elementSuffixes = Lists.newArrayList();
		
		for (final String labelToken : labelTokens) {
			
			final Iterator<String> itr = filterTokens.iterator();
			while (itr.hasNext()) {
				final String filterToken = itr.next();
				if (labelToken.startsWith(filterToken)) {
					// Last filter token? Also add suffix, unless it is already present in the filter and there's no whitespace at the end of it
					if (!itr.hasNext() && !filterTokens.contains(labelToken) && !spaceAtTheEnd) {
						elementSuffixes.add(labelToken.substring(filterToken.length()));
					}
				}
			}
			
			// If there's whitespace at the end, add complete word suggestions as well
			if (shouldSuggest(filterTokens, labelToken) && spaceAtTheEnd) {
				elementSuffixes.add(labelToken);
			}
		}

		return Iterables.toArray(elementSuffixes, String.class);
	}
	
	private static boolean shouldSuggest(final List<String> filterTokens, final String labelToken) {
		for (final String filterToken : filterTokens) {
			if (labelToken.startsWith(filterToken)) {
				return false;
			}
		}
		
		return true;
	}
	
}
