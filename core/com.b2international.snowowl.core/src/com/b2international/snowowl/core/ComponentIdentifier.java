/*
 * Copyright 2017-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * This class uniquely identifies a terminology component.
 * @since 5.9 
 */
public final class ComponentIdentifier implements Serializable {

	private static final long serialVersionUID = -7770154600003784759L;

	private final String componentType;
	private final String componentId;
	
	@JsonCreator
	public static ComponentIdentifier valueOf(String componentIdentifier) {
		final String[] parts = componentIdentifier.split("/");
		checkArgument(parts.length == 2, "ComponentIdentifier should consist of two parts, a short type id and String component id (was: %s)", componentIdentifier);
		return of(parts[0], parts[1]);
	}
	
	public static ComponentIdentifier of(String componentType, String componentId) {
		return new ComponentIdentifier(componentType, componentId);
	}
	
	private ComponentIdentifier(final String componentType, final String componentId) {
		this.componentType = componentType;
		this.componentId = checkNotNull(componentId, "componentId");
	}
	
	/**
	 * Returns with the unique identifier of the component.
	 * @return unique identifier of the component.
	 */
	public String getComponentId() {
		return componentId;
	}
	
	/**
	 * Returns with the unique terminology component identifier.
	 * @return unique terminology component identifier.
	 */
	public String getComponentType() {
		return componentType;
	}
	
	@JsonValue
	@Override
	public String toString() {
		return String.join("/", componentType, componentId);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(componentType, componentId);
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final ComponentIdentifier other = (ComponentIdentifier) obj;
		return Objects.equals(componentId, other.componentId)
				&& Objects.equals(componentType, other.componentType);
	}
	
}