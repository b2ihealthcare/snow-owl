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
package com.b2international.snowowl.core.terminology;

import com.b2international.commons.StringUtils;

/**
 * Enumerates high-level component categories which can exist in any code system.
 */
public enum ComponentCategory {

	/**
	 * Use if a terminology component cannot be categorized into any of the other literals.
	 */
	UNKNOWN,
	
	/**
	 * A category for ideas, physical objects or events.
	 */
	CONCEPT,

	/**
	 * A label or other textual representation for a concept.
	 */
	DESCRIPTION,

	/**
	 * A typed connection between two concepts.
	 */
	RELATIONSHIP,

	/**
	 * A scalar value or measurement associated with another component.
	 */
	CONCRETE_DOMAIN,

	/**
	 * A set of unique set members.
	 */
	SET,

	/**
	 * Points to another component, indicating that it is part of the member's parent set.
	 */
	SET_MEMBER,

	/**
	 * A set of unique map members.
	 */
	MAP,

	/**
	 * Points to a source and a target component, indicating that a mapping exists between the two in the context of the member's parent map.
	 */
	MAP_MEMBER;

	/**
	 * Returns the human-readable name of this category, obtained by converting the original name of the enum value to lower case, changing underscore
	 * characters to whitespace separators, and changing the first letter to upper case.
	 * 
	 * @return the display name of this category
	 */
	public String getDisplayName() {
		return StringUtils.capitalizeFirstLetter(name().replace('_', ' ').toLowerCase());
	}

	/**
	 * Returns the {@link ComponentCategory} for the given ordinal, or throws an {@link IllegalArgumentException} if not found.
	 * 
	 * @param ordinal
	 * @return
	 * @throws IllegalArgumentException
	 *             - if {@link ComponentCategory} not found for the given ordinal
	 */
	public static ComponentCategory getByOrdinal(int ordinal) {
		for (ComponentCategory c : values()) {
			if (c.ordinal() == ordinal) {
				return c;
			}
		}
		throw new IllegalArgumentException("Unknown componentCategory ordinal: " + ordinal);
	}
}
