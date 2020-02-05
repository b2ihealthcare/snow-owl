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
package com.b2international.commons.exceptions;

/**
 * Thrown when a component has been already inactivated in a previous request.
 * 
 * @since 1.0
 */
public class ComponentStatusConflictException extends ConflictException {

	private static final long serialVersionUID = -8674961206384074905L;

	/**
	 * Creates a new exception instance with the specified component identifier and active status.
	 * 
	 * @param componentId     the identifier of the component to report
	 * @param componentStatus {@code true} if the component is currently active, {@code false} if it is inactive
	 */
	public ComponentStatusConflictException(String componentId, boolean componentStatus) {
		super(String.format("Component %s is already %s.", componentId, componentStatus ? "active" : "inactive"));
	}
}
