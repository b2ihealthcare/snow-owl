/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.domain;

import java.io.Serializable;

import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.api.component.IdProvider;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Function;

/**
 * Represents an identifiable component of a code system.
 */
public interface IComponent extends Serializable, IdProvider<String> {

	/**
	 * Unique terminology independent node identifier to make actual root elements searchable.
	 */
	String ROOT_ID = "-1";
	
	/**
	 * Unique terminology independent node identifier to make actual root elements searchable (a {@link Long} value).
	 */
	long ROOT_IDL = Long.parseLong(ROOT_ID);

	/**
	 * Function to extract the ID from an {@link IComponent} instance.
	 * 
	 * @since 4.6
	 */
	Function<IComponent, String> ID_FUNCTION = IComponent::getId;

	/**
	 * Returns the component identifier.
	 * 
	 * @return the component identifier
	 */
	String getId();

	/**
	 * Checks if the component is released.
	 * 
	 * @return {@code true} if the component has already been released as part of a version, {@code false} otherwise
	 */
	Boolean isReleased();

	/**
	 * @deprecated - figure out how to remove storage key from domain representation classes, currently required for CDO object lookups on
	 *             server/client side
	 */
	@JsonIgnore
	long getStorageKey();
	
	/**
	 * Returns a {@link ComponentIdentifier} instance to identify this component using its {@link #getTerminologyComponentId() type} and {@link #getId() id}.
	 * @return
	 */
	@JsonIgnore
	default ComponentIdentifier getComponentIdentifier() {
		return ComponentIdentifier.of(getTerminologyComponentId(), getId());
	}
	
	/**
	 * @return the associated terminology component type identifier of this component.
	 */
	@JsonIgnore
	short getTerminologyComponentId();
}
