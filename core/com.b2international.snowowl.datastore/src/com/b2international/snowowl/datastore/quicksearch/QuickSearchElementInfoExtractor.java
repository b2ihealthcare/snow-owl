///*
// * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
// * 
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.b2international.snowowl.datastore.quicksearch;
//
//import java.util.List;
//
//import com.b2international.snowowl.core.TextConstants;
//import com.b2international.snowowl.core.quicksearch.QuickSearchElement;
//import com.google.common.base.Splitter;
//import com.google.common.collect.ImmutableList;
//import com.google.common.collect.Lists;
//
///**
// * Utility class to extract additional information from a quick search result element.
// */
//public class QuickSearchElementInfoExtractor {
//
//	private final String filter;
//	
//	public QuickSearchElementInfoExtractor(String filter) {
//		this.filter = filter;
//	}
//	
//	/**
//	 * Computes and returns additional information from the specified quick search element.
//	 * 
//	 * @param element
//	 * @return the extracted additional information
//	 */
//	public QuickSearchElementInfo process(QuickSearchElement element) {
//		final Splitter tokenSplitter = Splitter.on(TextConstants.WHITESPACE_OR_DELIMITER_MATCHER).omitEmptyStrings();
//		final List<String> filterTokens = ImmutableList.copyOf(tokenSplitter.split(filter.toLowerCase()));
//		final String lowerCaseLabel = element.getLabel().toLowerCase();
//		final Iterable<String> labelTokens = tokenSplitter.split(lowerCaseLabel);
//		final List<int[]> elementMatchRegions = Lists.newArrayList();
//		final List<String> elementSuffixes = Lists.newArrayList();
//		
//		int startIndex = 0;
//		
//		for (final String labelToken : labelTokens) {
//			
//			// Ignore stop words in the candidate label
//			if (TextConstants.STOPWORDS.contains(labelToken)) {
//				continue;
//			}
//			
//			startIndex = lowerCaseLabel.indexOf(labelToken, startIndex);
//			
//			for (int i = 0; i < filterTokens.size(); i++) {
//				
//				final String filterToken = filterTokens.get(i);
//				
//				if (labelToken.startsWith(filterToken)) {
//					
//					// XXX: the same segment of text may be selected for highlighting multiple times, revisit if this causes problems
//					elementMatchRegions.add(new int[] { startIndex, startIndex + filterToken.length() - 1 });
//					
//					// Last filter token? Also add suffix, unless it is already present in the filter and there's no whitespace at the end of it
//					if (i == (filterTokens.size() - 1) 
//							&& !filterTokens.contains(labelToken) 
//							&& !Character.isWhitespace(filter.charAt(filter.length() - 1))) {
//						
//						elementSuffixes.add(labelToken.substring(filterToken.length()));
//					}
//				}
//			}
//	
//			// If there's whitespace at the end, add complete word suggestions as well
//			if (shouldSuggest(filterTokens, labelToken) 
//					&& !filter.isEmpty() 
//					&& Character.isWhitespace(filter.charAt(filter.length() - 1))) {
//				
//				elementSuffixes.add(labelToken);
//			}
//			
//			// Move past this label token
//			startIndex += labelToken.length();
//		}			
//		
//		if (!elementMatchRegions.isEmpty()) {
//			return new QuickSearchElementInfo(elementMatchRegions.toArray(new int[elementMatchRegions.size()][]), 
//					elementSuffixes.toArray(new String[elementSuffixes.size()]));
//		} else {
//			return new QuickSearchElementInfo(new int[][]{}, new String[]{});
//		}
//	}
//	
//	private boolean shouldSuggest(final List<String> filterTokens, final String labelToken) {
//		for (final String filterToken : filterTokens) {
//			if (labelToken.startsWith(filterToken)) {
//				return false;
//			}
//		}
//		return true;
//	}
//}