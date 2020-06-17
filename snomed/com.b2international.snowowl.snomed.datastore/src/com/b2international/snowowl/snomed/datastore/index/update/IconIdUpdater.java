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
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import com.b2international.collections.longs.LongSet;
import com.b2international.commons.collect.LongSets;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.taxonomy.TaxonomyGraph;
import com.google.common.base.CharMatcher;
import com.google.common.collect.Sets;

/**
 * @since 4.3
 */
public class IconIdUpdater {
	
	private static final char WORD_SEPARATOR = '_';

	private static final CharMatcher WORD_SEPARATOR_MATCHER = CharMatcher.is(WORD_SEPARATOR);
	
	private static final CharMatcher DISALLOWED_CHARACTERS = CharMatcher.inRange('0', '9')
			.or(CharMatcher.inRange('a', 'z'))
			.negate();
	
	private static final int MAX_SUFFIX = 64;
	
	private final TaxonomyGraph inferredTaxonomy;
	private final TaxonomyGraph statedTaxonomy;
	private final Collection<String> availableImages;
	
	public IconIdUpdater(TaxonomyGraph inferredTaxonomy, TaxonomyGraph statedTaxonomy, Collection<String> availableImages) {
		this.inferredTaxonomy = inferredTaxonomy;
		this.statedTaxonomy = statedTaxonomy;
		this.availableImages = availableImages;
	}
	
	public void update(String id, String semanticTag, boolean active, SnomedConceptDocument.Builder doc) {
		doc.iconId(getIconId(id, semanticTag, active));
	}
	
	protected String getIconId(String id, String semanticTag, boolean active) {
		if (!active) {
			return Concepts.ROOT_CONCEPT;
		}
		
		final String imageSuffix = getImageSuffix(semanticTag);
		if (availableImages.contains(imageSuffix)) {
			return imageSuffix;
		}
		
		return getParentIcon(id, inferredTaxonomy)
				.or(() -> getParentIcon(id, statedTaxonomy))
				.orElse(Concepts.ROOT_CONCEPT);
	}

	/**
	 * @param semanticTag the semantic tag included on the concept's FSN
	 * @return an underscore_separated version of the semantic tag, 
	 *         restricted to at most 64 characters, 
	 *         including only letters and digits
	 */
	private String getImageSuffix(String semanticTag) {
		final String lowerCase = semanticTag.toLowerCase(Locale.ENGLISH);
		final String restrictChars = DISALLOWED_CHARACTERS.replaceFrom(lowerCase, WORD_SEPARATOR);
		final String collapseSeparators = WORD_SEPARATOR_MATCHER.collapseFrom(restrictChars, WORD_SEPARATOR);
		final String imageSuffix = collapseSeparators.substring(0, Math.min(MAX_SUFFIX, collapseSeparators.length()));
		return imageSuffix;
	}
	
	private Optional<String> getParentIcon(String id, TaxonomyGraph taxonomyGraph) {
		if (id == null || !taxonomyGraph.containsNode(Long.parseLong(id))) {
			return Optional.empty();
		}
		final Set<String> visitedNodes = Sets.newHashSet();
		final Set<String> candidates = Sets.newTreeSet();
		final Deque<String> toVisit = new ArrayDeque<>();
		toVisit.add(id);
		
		while (!toVisit.isEmpty()) {
			final String currentId = toVisit.removeFirst();
			if (!visitedNodes.add(currentId)) {
				continue;
			}
			
			if (availableImages.contains(currentId)) {
				candidates.add(currentId);
			} else {
				// XXX: we stop looking further "up" this hierarchy if there is any image present for the visited concept 
				final LongSet ancestorNodeIds = taxonomyGraph.getAncestorNodeIds(Long.parseLong(currentId));
				toVisit.addAll(LongSets.toStringList(ancestorNodeIds));
			}
		}
		
		return candidates.stream().findFirst();
	}
}
