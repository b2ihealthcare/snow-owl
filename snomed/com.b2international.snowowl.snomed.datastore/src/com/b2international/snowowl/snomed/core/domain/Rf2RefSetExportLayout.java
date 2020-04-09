/*
 * Copyright 2018-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

/**
 * Enumerates available export file layouts for reference sets.
 */
public enum Rf2RefSetExportLayout {

	/**
	 * Reference set content for the same type are concatenated into a single file.
	 */
	COMBINED("One file per reference set type"),

	/**
	 * A separate file is created for each reference set.
	 */
	INDIVIDUAL("One file per reference set");

	private String displayLabel;

	private Rf2RefSetExportLayout(final String displayLabel) {
		this.displayLabel = displayLabel;
	}

	@Override
	public String toString() {
		return displayLabel;
	}

	/**
	 * Returns with the RF2 refset export layout identified by the specified value.
	 * 
	 * @param name
	 *            the value of the refset export layout
	 * @return the refset export layout, never <code>null</code>
	 * @throws BadRequestException
	 *             - if the specified name cannot be recognized as a valid refset export layout option
	 */
	public static Rf2RefSetExportLayout getByNameIgnoreCase(String name) {
		for (final Rf2RefSetExportLayout type : values()) {
			if (nullToEmpty(name).equalsIgnoreCase(type.name())) {
				return type;
			}
		}
		throw new BadRequestException("Unknown RF2 refset export layout '%s'.", name);
	}

}
