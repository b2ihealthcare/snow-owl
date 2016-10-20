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
package com.b2international.snowowl.core.quicksearch;

import java.util.List;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

/**
 * Collection of utility methods for working with {@link IQuickSearchProvider quick search providers}.
 * 
 */
public final class QuickSearchProviderUtils {

	/**
	 * Returns an {@link Iterable} of all the suffixes from the specified entries, whose iteration order is highest count first.
	 * 
	 * @param entries the {@link QuickSearchEntryBase quick search entries}
	 * @return an ordered {@link Iterable}, whose iteration order is highest count first
	 */
	public static Iterable<String> getSuffixesByDecreasingPopularity(List<List<QuickSearchEntryBase>> entries) {
		final Multiset<String> suffixPopularity = createSuffixPopularityMultiset(entries);
		ImmutableMultiset<String> highestCountFirst = Multisets.copyHighestCountFirst(suffixPopularity);
		return highestCountFirst;
	}

	public static Multiset<String> createSuffixPopularityMultiset(List<List<QuickSearchEntryBase>> entries) {
		final Multiset<String> suffixPopularity = HashMultiset.create(entries.size());
		
		for (final List<QuickSearchEntryBase> entryList : entries) {
			if (null != entryList) { // FIXME: why do we have nulls here?
				for (final QuickSearchEntryBase entry : entryList) {
					// We're adding suffixes multiple times for favoured providers
					int suffixMultiplier = entry.getElement().getParentProvider().getSuffixMultiplier();
					for (String suffix : entry.getElementSuffixes()) {
						suffixPopularity.add(suffix, suffixMultiplier);
					}
				}
			}
		}
		
		return suffixPopularity;
	}
	
	private QuickSearchProviderUtils() {
		// Prevent instantiation
	}
}