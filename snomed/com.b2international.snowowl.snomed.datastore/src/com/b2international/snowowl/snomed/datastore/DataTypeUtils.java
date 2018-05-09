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
package com.b2international.snowowl.snomed.datastore;

import java.util.Locale;

import com.b2international.commons.StringUtils;

/**
 * Collection of utility methods related to data types.
 */
public abstract class DataTypeUtils {

	/**
	 * Formatted text editing pattern for BigDecimals (25 digits with thousands separators, 9 decimal places)
	 */
	public static final String BIG_DECIMAL_EDIT_PATTERN = "#,###,###,###,###,###.#########";

	/**
	 * Formatted text editing pattern for integers (12 digits with thousands separators)
	 */
	public static final String INTEGER_EDIT_PATTERN = "###,###,###.";

	/**
	 * Returns the human-readable (default) label of a concrete domain attribute, suitable for UI display.
	 * 
	 * @param dataTypeName the attribute name, in camel-case form (eg. "isaVitamin")
	 * @return the computed human-readable attribute label (eg. "Vitamin")
	 */
	public static String getDefaultDataTypeLabel(final String dataTypeName) {
		if (StringUtils.isEmpty(dataTypeName)) {
			return StringUtils.EMPTY_STRING;
		}

		final String suffixesRemoved = dataTypeName.replaceFirst("canBeTaggedWith|isa|is|does|has", "");
		final String camelCaseSplit = StringUtils.splitCamelCase(suffixesRemoved);
		final String lowerCased = camelCaseSplit.toLowerCase(Locale.ENGLISH);

		return StringUtils.capitalizeFirstLetter(lowerCased);
	}

	private DataTypeUtils() {
		throw new UnsupportedOperationException("This class is not supposed to be instantiated.");
	}
}
