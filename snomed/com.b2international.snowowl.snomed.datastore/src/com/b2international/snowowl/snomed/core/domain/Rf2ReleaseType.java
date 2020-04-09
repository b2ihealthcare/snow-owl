/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.domain;

import static com.google.common.base.Strings.nullToEmpty;

import com.b2international.commons.exceptions.BadRequestException;
import com.google.common.base.CaseFormat;

/**
 * Enumerates available RF2 release formats for the SNOMED CT ontology.
 */
public enum Rf2ReleaseType {

	/**
	 * Delta RF2 publication format. Contains the most recent changes.
	 */
	DELTA,

	/**
	 * Snapshot RF2 publication format. Contains all component with their latest state.
	 */
	SNAPSHOT,

	/**
	 * Full RF2 publication format. Contains everything.
	 */
	FULL;

	@Override
	public String toString() {
		return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
	}

	/**
	 * Returns with the RF2 release type identified by the specified value.
	 * 
	 * @param name
	 *            the value of the release type
	 * @return the release type, never <code>null</code>
	 * @throws BadRequestException - if the specified name cannot be recognized as a valid RF2 release type option 
	 */
	public static Rf2ReleaseType getByNameIgnoreCase(String name) {
		for (final Rf2ReleaseType type : values()) {
			if (nullToEmpty(name).equalsIgnoreCase(type.toString())) {
				return type;
			}
		}
		throw new BadRequestException("Unknown RF2 release type: '%s'", name);
	}
	
}
