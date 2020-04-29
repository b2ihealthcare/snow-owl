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
package com.b2international.snowowl.snomed.datastore.index.update;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.collections.longs.LongSet;
import com.b2international.commons.StringUtils;
import com.b2international.commons.collect.LongSets;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.taxonomy.TaxonomyGraph;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @since 4.3
 */
public class IconIdUpdater {
	
	private static final char WORD_SEPARATOR = '_';
	private static final Splitter WORD_SPLITTER = Splitter.on(WORD_SEPARATOR)
			.omitEmptyStrings()
			.trimResults();
	
	private static final int MAX_SUFFIX = 128;
	private static final CharSequence SUFFIX_SEPARATOR = "_";
	private static final CharMatcher DIGIT_OR_LETTER = CharMatcher.inRange('0', '9')
			.or(CharMatcher.inRange('a', 'z'))
			.or(CharMatcher.is(WORD_SEPARATOR));
	
	private final TaxonomyGraph inferredTaxonomy;
	private final TaxonomyGraph statedTaxonomy;
	private final Collection<String> availableImages;
	
	public IconIdUpdater(TaxonomyGraph inferredTaxonomy, TaxonomyGraph statedTaxonomy, Collection<String> availableImages) {
		this.inferredTaxonomy = inferredTaxonomy;
		this.statedTaxonomy = statedTaxonomy;
		this.availableImages = availableImages;
	}
	
	/**
	 * @param id the ID of the concept to update
	 * @param semanticTag the semantic tag included in the concept's FSN
	 * @param active {@code true} if the concept to update is active, {@code false} otherwise
	 * @param doc the document builder on which an iconId update can be applied
	 */
	public void update(String id, String semanticTag, boolean active, SnomedConceptDocument.Builder doc) {
		doc.iconId(getIconId(id, semanticTag, active));
	}
	
	protected String getIconId(String id, String semanticTag, boolean active) {
		String iconId = null;
		
		if (active) {
			final String imageSuffix = getImageSuffix(semanticTag);
			iconId = getParentIcon(id, imageSuffix, inferredTaxonomy);
			
			// Use the stated form to get the icon ID as a fallback
			if (iconId == null) {
				iconId = getParentIcon(id, imageSuffix, statedTaxonomy);
			}
		}
		
		// default icon is the ROOT if neither the inferred nor the stated tree provided an ICON, usually this means that the concept is inactive
		return iconId == null ? Concepts.ROOT_CONCEPT : iconId;
	}

	/**
	 * @param semanticTag the semantic tag included on the concept's FSN
	 * @return a camel-cased version of the semantic tag, 
	 *         restricted to at most 16 characters, 
	 *         including only letters and digits
	 */
	private String getImageSuffix(String semanticTag) {
		final String lowerCase = semanticTag.toLowerCase(Locale.ENGLISH);
		final String convertWhitespace = CharMatcher.whitespace().replaceFrom(lowerCase, WORD_SEPARATOR);
		final String restrictChars = DIGIT_OR_LETTER.retainFrom(convertWhitespace);
		final List<String> words = WORD_SPLITTER.splitToList(restrictChars);
		final String camelCase = words.stream().map(StringUtils::capitalizeFirstLetter).collect(Collectors.joining());
		final String imageSuffix = camelCase.substring(0, Math.min(MAX_SUFFIX, camelCase.length()));
		return imageSuffix;
	}
	
	private String getParentIcon(String id, String imageSuffix, TaxonomyGraph taxonomyGraph) {
		if (id == null || !taxonomyGraph.containsNode(Long.parseLong(id))) {
			return null;
		}
		
		final Set<String> visitedNodes = Sets.newHashSet();
		final Map<String, String> candidates = Maps.newTreeMap();
		final Deque<String> toVisit = new ArrayDeque<>();
		toVisit.add(id);
		
		while (!toVisit.isEmpty()) {
			final String currentId = toVisit.removeFirst();
			if (!visitedNodes.add(currentId)) {
				continue;
			}
			
			final String idWithSuffix = currentId + SUFFIX_SEPARATOR + imageSuffix;
			if (availableImages.contains(idWithSuffix)) {
				candidates.put(currentId, idWithSuffix);
			} else if (availableImages.contains(currentId)) {
				candidates.put(currentId, currentId);
			} else {
				// XXX: we stop looking further "up" this hierarchy if there is any image present for this concept 
				final LongSet ancestorNodeIds = taxonomyGraph.getAncestorNodeIds(Long.parseLong(currentId));
				toVisit.addAll(LongSets.toStringList(ancestorNodeIds));
			}
		}
		
		/* 
		 * From the closest icon-bearing ancestors:
		 * 
		 * 1) return the one which has the same suffix as the concept;
		 * 2) if none if them match, return a generic icon with the smallest SCTID;
		 * 3) return null otherwise.
		 */
		return firstSuffixMatch(candidates, SUFFIX_SEPARATOR + imageSuffix) 
				.or(() -> firstGenericMatch(candidates))
				.orElse(null);
	}

	private Optional<String> firstSuffixMatch(Map<String, String> candidates, String suffixWithSeparator) {
		return candidates.values()
			.stream()
			.filter(iconId -> iconId.endsWith(suffixWithSeparator))
			.findFirst();
	}
	
	private Optional<String> firstGenericMatch(Map<String, String> candidates) {
		return candidates.values()
			.stream()
			.filter(iconId -> !iconId.contains(SUFFIX_SEPARATOR))
			.findFirst();
	}
}
