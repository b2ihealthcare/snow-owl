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
package com.b2international.snowowl.datastore.quicksearch;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.eclipse.emf.ecore.EPackage;

import com.b2international.snowowl.core.TextConstants;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.quicksearch.IQuickSearchProvider;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.index.DelimiterAnalyzer;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public abstract class AbstractQuickSearchContentProvider {

	protected static String[] getComponentIds(final Map<String, Object> configuration) {
		if (configuration.containsKey(IQuickSearchProvider.CONFIGURATION_VALUE_ID_SET)) {
			final Set<String> componentIdSet = (Set<String>) configuration.get(IQuickSearchProvider.CONFIGURATION_VALUE_ID_SET);
			return (String[]) componentIdSet.toArray(new String[componentIdSet.size()]);
		} else {
			return null;
		}
	}

	protected final IBranchPath getBranchPath(final IBranchPathMap branchPathMap) {
		return branchPathMap.getBranchPath(getEPackage());
	}

	protected static int[][] getMatchRegions(final String queryExpression, final String label) {
		final Analyzer analyzer = new DelimiterAnalyzer();
		final List<String> filterTokens = IndexUtils.split(analyzer, queryExpression);
		// FIXME: Remove this when the new Analyzer chain is used
		final String sortKeyLabel = IndexUtils.getSortKey(label.toLowerCase(Locale.ENGLISH));
		final List<String> labelTokens = IndexUtils.split(analyzer, sortKeyLabel);
		final List<int[]> elementMatchRegions = Lists.newArrayList();
		
		int startIndex = 0;
		
		for (final String labelToken : labelTokens) {
			
			startIndex = sortKeyLabel.indexOf(labelToken, startIndex);
			
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
	
	protected static String[] getSuffixes(final String queryExpression, final String label) {
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
	
	protected abstract EPackage getEPackage();
}
