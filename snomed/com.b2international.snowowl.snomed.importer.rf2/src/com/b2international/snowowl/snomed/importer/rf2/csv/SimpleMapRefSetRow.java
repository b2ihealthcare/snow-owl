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
 * Represents a simple type reference set file row where the map target component
 * description might be available.
 *
 */
public class SimpleMapRefSetRow extends AssociatingRefSetRow {
	
	public static final String PROP_MAP_TARGET_DESCRIPTION = "mapTargetDescription";
	
	private String mapTargetDescription;

	public String getMapTargetDescription() {
		return mapTargetDescription;
	}

	public void setMapTargetDescription(final String mapTargetDescription) {
		this.mapTargetDescription = mapTargetDescription;
	}

	@Override
	public String toString() {
		return String.format("AssociatingRefSetRow [uuid=%s, effectiveTime=%s, active=%s, moduleId=%s, refsetId=%s, " +
				"referencedComponentId=%s, associatedComponentId=%s, mapTargetDescription=%s]",
				getUuid(), getEffectiveTime(), isActive(), getModuleId(), getRefSetId(),
				getReferencedComponentId(), getAssociatedComponentId(), getMapTargetDescription());
	}
	
	
}