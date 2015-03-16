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
package com.b2international.snowowl.snomed.datastore;

/**
 * @since 4.0
 */
public enum ComponentNature {
	CONCEPT, DESCRIPTION, RELATIONSHIP;

	/**
	 * Checks if the specified component identifier corresponds to this component nature (determined by its last-but-one digit).
	 * 
	 * @param componentId
	 *            the component identifier to check
	 * 
	 * @return {@code true} if the specified identifier is of this nature, {@code false} otherwise
	 */
	public boolean isNatureId(String componentId) {

		if (componentId == null || componentId.length() < 6 || componentId.length() > 18) {
			return false;
		}

		int natureDigit = componentId.charAt(componentId.length() - 2) - '0';
		return (natureDigit == ordinal());
	}

	public static ComponentNature getByOrdinal(int componentIdentifier) {
		for (ComponentNature c : values()) {
			if (c.ordinal() == componentIdentifier) {
				return c;
			}
		}
		throw new IllegalArgumentException("Unknown componentIdentifier: " + componentIdentifier);
	}
}