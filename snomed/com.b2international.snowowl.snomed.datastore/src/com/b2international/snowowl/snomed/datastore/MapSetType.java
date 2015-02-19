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
 * Enumeration for representing all available map set types for SNOMED&nbsp;CT RF1 publication.
 * <p>The following types are available:
 * <ul>
 * <li>{@link #UNSPECIFIED <em>Unspecified</em>}</li>
 * <li>{@link #SINGLE <em>Single</em>}</li>
 * <li>{@link #MULTIPLE <em>Multiple</em>}</li>
 * <li>{@link #CHOICE <em>Choice</em>}</li>
 * <li>{@link #FLEXIBLE <em>Flexible</em>}</li>
 * </ul>
 * </p>
 */
public enum MapSetType {
	
	/**
	 * Unspecified. Value: {@code 0x00}.
	 * @see MapSetType
	 */
	UNSPECIFIED("Unspecified", 0x00),
	/**
	 * Single. All maps are unique one-to-one maps. Value: {@code 0x01}.
	 * @see MapSetType
	 */
	SINGLE("Single", 0x01),
	/**
	 * Multiple. Some maps are one-to-many maps but there are no choices. Value: {@code 0x02}.
	 * @see MapSetType
	 */
	MULTIPLE("Multiple", 0x02),
	/**
	 * Choice. Some maps include choices of one-to-one maps but there are no one-to-many maps. Value: {@code 0x03}.
	 * @see MapSetType
	 */
	CHOICE("Choice", 0x03),
	/**
	 * Flexible. Some maps include choices and there are some one-to-many maps. Value: {@code 0x04}.
	 * @see MapSetType
	 */
	FLEXIBLE("Flexible", 0x04);
	
	private final String name;
	private final int value;
	
	private MapSetType(final String name, final int value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * Humane readable name of the map set type.
	 * @return the name of the map set type.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns with the value of the map set type.
	 * @return the value of the map set type.
	 */
	public int getValue() {
		return value;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}
	
	/**
	 * Returns with the map set type identified by its specified unique value.
	 * @param value the value of the map set type.
	 * @return the map set type looked up by its unique value.
	 */
	public static MapSetType getByValue(final int value) {
		for (final MapSetType type : values())
			if (value == type.value)
				return type;
		throw new IllegalArgumentException("Unknown map set type for value: " + value + ".");
	}
	
}