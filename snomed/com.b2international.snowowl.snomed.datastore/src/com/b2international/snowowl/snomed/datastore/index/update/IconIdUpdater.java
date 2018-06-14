/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collection;
import java.util.Set;

import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.taxonomy.ISnomedTaxonomyBuilder;
import com.google.common.collect.Sets;
import com.google.common.primitives.Longs;

/**
 * @since 4.3
 */
public class IconIdUpdater {
	
	private final ISnomedTaxonomyBuilder inferredTaxonomy;
	private final ISnomedTaxonomyBuilder statedTaxonomy;
	private final Collection<String> availableImages;
	
	public IconIdUpdater(ISnomedTaxonomyBuilder inferredTaxonomy, ISnomedTaxonomyBuilder statedTaxonomy, Collection<String> availableImages) {
		this.inferredTaxonomy = inferredTaxonomy;
		this.statedTaxonomy = statedTaxonomy;
		this.availableImages = availableImages;
	}
	
	public void update(String id, boolean active, SnomedConceptDocument.Builder doc) {
		doc.iconId(getIconId(id, active));
	}
	
	protected String getIconId(String conceptId, boolean active) {
		String iconId = null;
		if (active) {
			// TODO do not set the iconId to the one in the taxonomyBuilder if the concept is inactive
			// we may have to need a ref to the concept itself or at least know the active flag
			iconId = getParentIcon(conceptId, inferredTaxonomy);
			// try to use the stated form to get the ID
			if (iconId == null) {
				iconId = getParentIcon(conceptId, statedTaxonomy);
			}
		}
		// default icon is the ROOT if neither the inferred nor the stated tree provided an ICON, usually this means that the concept is inactive
		return iconId == null ? Concepts.ROOT_CONCEPT : iconId;
	}
	
	private String getParentIcon(String componentId, ISnomedTaxonomyBuilder taxonomyBuilder) {
		if (componentId == null || !taxonomyBuilder.containsNode(componentId)) {
			return null;
		}
		final Set<String> visitedNodes = Sets.newHashSet();
		return getParentFrom(componentId, taxonomyBuilder, visitedNodes);
	}
	
	private String getParentFrom(final String conceptId, ISnomedTaxonomyBuilder taxonomyBuilder, final Set<String> visitedNodes) {
		if (visitedNodes.add(conceptId)) {
			if (this.availableImages.contains(conceptId)) {
				return conceptId;
			}
			final LongSet ancestorNodeIds = taxonomyBuilder.getAncestorNodeIds(conceptId);
			if (ancestorNodeIds.size() == 0) {
				return null;
			}
			final long minConceptId = Longs.min(ancestorNodeIds.toArray());
			return getParentFrom(Long.toString(minConceptId), taxonomyBuilder, visitedNodes);
		} else {
			// if we reached an already visited node, then skip and return null
			return null;
		}
	}
}
