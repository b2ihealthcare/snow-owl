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
package com.b2international.snowowl.snomed.common;

import static com.google.common.base.Strings.nullToEmpty;

import com.b2international.commons.StringUtils;

/**
 * Enumerates available content subtypes. The subtype appears as a part of most
 * release file names.
 * 
 * @since 1.3
 */
public enum ContentSubType {

	/**
	 * Delta RF1/RF2 publication format. Contains the changes.  (Value: <b>0x00</b>)
	 * @see ReleaseType
	 */
	DELTA("Delta", 0x00),
	/**
	 * Snapshot RF1/RF2 publication format. Contains all component with their latest state. (Value: <b>0x01</b>)
	 * @see ReleaseType
	 */
	SNAPSHOT("Snapshot", 0x01),
	/**
	 * Full RF1/RF2 publication format. Contains everything. (Value: <b>0x02</b>)
	 * @see ReleaseType
	 */
	FULL("Full", 0x02);

	private String name;
	private int value;
	
	private ContentSubType(final String name, final int value) {
		this.name = name;
		this.value = value;
	}
	
	/**
	 * @return the display name of this subtype, which is the Capitalized
	 *         version of the item name
	 */
	public String getDisplayName() {
		return StringUtils.capitalizeFirstLetter(name().toLowerCase());
	}
	
	/**Returns with the lower case name of the current enumeration.*/
	public String getLowerCaseName() {
		return name().toLowerCase();
	}
	
	/**
	 * Returns with the value of the publication type.
	 * @return the value associated with the publication type.
	 */
	public int getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	/**
	 * Returns with the content subtype identified by the specified value.
	 * @param value the value of the content subtype.
	 * @return the content subtype.
	 */
	public static ContentSubType getByValue(final int value) {
		for (final ContentSubType type : values()) {
			if (value == type.getValue())
				return type;
		}
		throw new IllegalArgumentException("Unknown value: " + value);
	}
	
	/**
	 * Returns with the content subtype identified by the specified name.
	 * @param name the display name of the content subtype.
	 * @return the content subtype.
	 */
	public static ContentSubType getByNameIgnoreCase(final String name) {
		for (final ContentSubType type : values()) {
			if (nullToEmpty(name).equalsIgnoreCase(nullToEmpty(type.name)))
				return type;
		}
		throw new IllegalArgumentException("Unknown type for name: " + name);
	}

}