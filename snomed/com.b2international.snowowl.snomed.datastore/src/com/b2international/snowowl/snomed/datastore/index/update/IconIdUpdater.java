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
	
	private Collection<String> availableImages;
	private boolean active;
	
	public IconIdUpdater(ISnomedTaxonomyBuilder taxonomyBuilder, Component component, Collection<String> availableImages) {
		this(taxonomyBuilder, component.getId(), component.isActive(), availableImages);
	}
	
	public IconIdUpdater(ISnomedTaxonomyBuilder taxonomyBuilder, String conceptId, boolean active, Collection<String> availableImages) {
		super(taxonomyBuilder, conceptId);
		this.active = active;
		this.availableImages = availableImages;
	}
	
	@Override
	public final void doUpdate(SnomedDocumentBuilder doc) {
		final long iconIdLong = Long.parseLong(getIconId(getComponentId(), active));
		doc.iconId(iconIdLong);
	}

	protected String getIconId(String conceptId, boolean active) {
		if (active) {
			// TODO do not set the iconId to the one in the taxonomyBuilder if the concept is inactive
			// we may have to need a ref to the concept itself or at least know the active flag
			if (getTaxonomyBuilder().containsNode(conceptId)) {
				return getIconComponentId(conceptId, getTaxonomyBuilder());
			}
		}
		// default icon is the ROOT
		return Concepts.ROOT_CONCEPT;
	}
	
	private String getIconComponentId(String componentId, ISnomedTaxonomyBuilder taxonomyBuilder) {
		if (componentId == null) {
			return null;
		}
		if (taxonomyBuilder.getAllAncestorNodeIds(componentId).contains(Long.valueOf(componentId))) {
			throw new CycleDetectedException("Concept " + componentId + " would introduce a cycle in the ISA graph (loop).");
		}
		String iconId = getParentFrom(componentId);
		return iconId != null ? iconId : Concepts.ROOT_CONCEPT;
	}
	
	private String getParentFrom(final String conceptId) {
		if (getTaxonomyBuilder().getAncestorNodeIds(conceptId).size() == 0) {
			return conceptId;
		}
		if (this.availableImages.contains(conceptId)) {
			return conceptId;
		}
		return getParentFrom(Long.toString(getTaxonomyBuilder().getAncestorNodeIds(conceptId).iterator().next()));
	}
		
}
