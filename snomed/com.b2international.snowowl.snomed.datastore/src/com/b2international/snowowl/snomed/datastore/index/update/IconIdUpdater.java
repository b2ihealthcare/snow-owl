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
package com.b2international.snowowl.snomed.datastore.index.update;

import java.util.Collection;

import com.b2international.snowowl.core.exceptions.CycleDetectedException;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedDocumentBuilder;
import com.b2international.snowowl.snomed.datastore.taxonomy.ISnomedTaxonomyBuilder;

/**
 * @since 4.3
 */
public class IconIdUpdater extends SnomedDocumentUpdaterBase {
	
	private final ISnomedTaxonomyBuilder statedTaxonomy;
	private final Collection<String> availableImages;
	private final boolean active;
	
	public IconIdUpdater(ISnomedTaxonomyBuilder inferredTaxonomy, ISnomedTaxonomyBuilder statedTaxonomy, Component component, Collection<String> availableImages) {
		this(inferredTaxonomy, statedTaxonomy, component.getId(), component.isActive(), availableImages);
	}
	
	public IconIdUpdater(ISnomedTaxonomyBuilder inferredTaxonomy, ISnomedTaxonomyBuilder statedTaxonomy, String conceptId, boolean active, Collection<String> availableImages) {
		super(inferredTaxonomy, conceptId);
		this.statedTaxonomy = statedTaxonomy;
		this.active = active;
		this.availableImages = availableImages;
	}
	
	@Override
	public final void doUpdate(SnomedDocumentBuilder doc) {
		final Long iconIdLong = Long.valueOf(getIconId(getComponentId(), active));
		doc.iconId(iconIdLong);
	}

	protected String getIconId(String conceptId, boolean active) {
		String iconId = null;
		if (active) {
			// TODO do not set the iconId to the one in the taxonomyBuilder if the concept is inactive
			// we may have to need a ref to the concept itself or at least know the active flag
			final ISnomedTaxonomyBuilder inferredTaxonomy = getTaxonomyBuilder();
			if (inferredTaxonomy.containsNode(conceptId)) {
				iconId = getParentIcon(conceptId, inferredTaxonomy);
			}
			// try to use the stated form to get the ID
			if (iconId == null) {
				iconId = getParentIcon(conceptId, statedTaxonomy);
			}
		}
		// default icon is the ROOT if neither the inferred nor the stated tree provided an ICON, usually this means that the concept is inactive
		return iconId == null ? Concepts.ROOT_CONCEPT : iconId;
	}
	
	private String getParentIcon(String componentId, ISnomedTaxonomyBuilder taxonomyBuilder) {
		if (componentId == null) {
			return null;
		}
		if (taxonomyBuilder.getAllAncestorNodeIds(componentId).contains(Long.valueOf(componentId))) {
			throw new CycleDetectedException("Concept " + componentId + " would introduce a cycle in the ISA graph (loop).");
		}
		return getParentFrom(componentId, taxonomyBuilder);
	}
	
	private String getParentFrom(final String conceptId, ISnomedTaxonomyBuilder taxonomyBuilder) {
		if (taxonomyBuilder.getAncestorNodeIds(conceptId).size() == 0) {
			return null;
		}
		if (this.availableImages.contains(conceptId)) {
			return conceptId;
		}
		return getParentFrom(Long.toString(taxonomyBuilder.getAncestorNodeIds(conceptId).iterator().next()), taxonomyBuilder);
	}
}
