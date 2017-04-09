/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * This class uniquely identifies a terminology component.
 * @since 5.9 
 */
public final class ComponentIdentifier implements Serializable {

	private static final long serialVersionUID = -7770154600003784759L;

	private static final ComponentIdentifier UNKOWN = of(CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT, "unknown");

	private final short terminologyComponentId;
	private final String componentId;
	
	/**
	 * Returns with a component identifier pair where the terminology component identifier is 
	 * {@link CoreTerminologyBroker#UNSPECIFIED_NUMBER_SHORT} and the component identifier is {@code empty}.
	 * @return a special component identifier with {@code empty} component identifier.
	 * @see CoreTerminologyBroker#UNSPECIFIED_NUMBER_SHORT
	 */
	public static final ComponentIdentifier unknown() {
		return UNKOWN;
	}  

	public static ComponentIdentifier of(short terminologyComponentId, String componentId) {
		return new ComponentIdentifier(terminologyComponentId, componentId);
	}
	
	private ComponentIdentifier(final short terminologyComponentId, final String componentId) {
		this.terminologyComponentId = terminologyComponentId;
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
	public short getTerminologyComponentId() {
		return terminologyComponentId;
	}
	
	@JsonValue
	@Override
	public String toString() {
		return String.format("%s/%s", terminologyComponentId, componentId);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(terminologyComponentId, componentId);
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final ComponentIdentifier other = (ComponentIdentifier) obj;
		return Objects.equals(componentId, other.componentId)
				&& Objects.equals(terminologyComponentId, other.terminologyComponentId);
	}
	
}