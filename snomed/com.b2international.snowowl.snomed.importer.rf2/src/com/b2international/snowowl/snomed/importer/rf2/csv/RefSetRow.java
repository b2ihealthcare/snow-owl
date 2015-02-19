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
 * Represents a reference set release file row with a single referenced
 * component. Provides storage for the following CSV field:
 * <ul>
 * <li>{@code referencedComponentId}
 * </ul>
 * 
 */
public class RefSetRow extends AbstractRefSetRow {

	public static final String PROP_REFERENCED_COMPONENT_ID = "referencedComponentId";
	
	private String referencedComponentId;
	
	public String getReferencedComponentId() {
		return referencedComponentId;
	}
	
	public void setReferencedComponentId(final String referencedComponentId) {
		this.referencedComponentId = referencedComponentId;
	}

	@Override
	public String toString() {
		return String.format("RefSetRow [uuid=%s, effectiveTime=%s, active=%s, moduleId=%s, refsetId=%s, " +
				"referencedComponentId=%s]",
				getUuid(), getEffectiveTime(), isActive(), getModuleId(), getRefSetId(),
				getReferencedComponentId());
	}
}