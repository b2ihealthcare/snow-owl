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
package com.b2international.snowowl.snomed.api.impl.traceability;

import org.eclipse.emf.ecore.EClass;

import com.google.common.base.Objects;

/**
 * Captures a summary of a single changed component related to a concept.
 */
class TraceabilityChange {
	
	private final String componentType;
	private final String componentId;
	private final ChangeType type;
	
	public TraceabilityChange(final EClass eClass, final String componentId, final ChangeType type) {
		this.componentType = eClass.getName();
		this.componentId = componentId;
		this.type = type;
	}

	public String getComponentType() {
		return componentType;
	}

	public String getComponentId() {
		return componentId;
	}

	public ChangeType getType() {
		return type;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(componentType, componentId, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		
		final TraceabilityChange other = (TraceabilityChange) obj;
		
		if (!componentId.equals(other.componentId)) return false;
		if (!componentType.equals(other.componentType)) return false;
		if (type != other.type) return false;
		
		return true;
	}
}
