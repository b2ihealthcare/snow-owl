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
package com.b2international.snowowl.snomed.importer.rf2.csv;

/**
 * Represents reference set release file rows where the referenced component is
 * associated with another component. Instances of this row can be found in
 * <b>attribute-value</b>, <b>association</b>, <b>language</b> and <b>simple
 * map</b> type reference set files.
 * <p>
 * The class provides storage for the following CSV fields:
 * <ul>
 * <li>{@code valueId, targetComponent, mapTarget, acceptabilityId}<br>
 * (actual name depends on reference set type)
 * </ul>
 * 
 */
public class AssociatingRefSetRow extends RefSetRow {

	public static final String PROP_ASSOCIATED_COMPONENT_ID = "associatedComponentId";
	
	private String associatedComponentId;

	public String getAssociatedComponentId() {
		return associatedComponentId;
	}

	public void setAssociatedComponentId(final String associatedComponentId) {
		this.associatedComponentId = associatedComponentId;
	}

	@Override
	public String toString() {
		return String.format("AssociatingRefSetRow [uuid=%s, effectiveTime=%s, active=%s, moduleId=%s, refsetId=%s, " +
				"referencedComponentId=%s, associatedComponentId=%s]",
				getUuid(), getEffectiveTime(), isActive(), getModuleId(), getRefSetId(),
				getReferencedComponentId(), getAssociatedComponentId());
	}
}