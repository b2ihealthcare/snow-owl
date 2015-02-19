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
package com.b2international.snowowl.snomed.datastore.id;

import java.util.Random;

import com.b2international.commons.VerhoeffCheck;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.google.common.base.Strings;

/**
 * SNOMED CT Identifiers v0.4:
 * <p />
 * <i>An item identifier can have a lowest permissible value of 100 (three digits) and a highest permissible value of 99999999 (8 digits) for long
 * format identifiers or 999999999999999 (15 digits) for short format identifiers. Leading zeros are not permitted in the item identifier.<//>
 */
public class SnomedIdentifiers {

	private SnomedIdentifiers() {
	}

	/**
	 * Generates a valid, random SNOMED CT Concept identifier based on the Core namespace.
	 * 
	 * @return a valid, randomly generated SCT ID.
	 */
	public static String generateConceptId() {
		return generateComponentId(null, SnomedEditingContext.ComponentNature.CONCEPT);
	}
	
	/**
	 * Generates a valid, random SNOMED CT Concept identifier based on the given namespace.
	 * 
	 * @param nameSpaceId
	 *            - the namespace ID to use
	 * @return a valid, randomly generated SCT ID.
	 */
	public static String generateConceptId(String nameSpaceId) {
		return generateComponentId(nameSpaceId, SnomedEditingContext.ComponentNature.CONCEPT);
	}

	/**
	 * Generates a valid random SNOMED CT component identifier.
	 * 
	 * @param nameSpaceId
	 * @param componentNature
	 * @return a valid, randomly generated SCT ID.
	 */
	public static String generateComponentId(String nameSpaceId, SnomedEditingContext.ComponentNature componentNature) {
		StringBuilder buf = new StringBuilder();
		buf.append(generateRandomItemIndentifier());
		if (Strings.isNullOrEmpty(nameSpaceId)) {
			buf.append('0');
		} else {
			buf.append(nameSpaceId);
			buf.append('1');
		}
		buf.append(componentNature.ordinal());
		char checkDigit = VerhoeffCheck.calculateChecksum(buf, false);
		buf.append(checkDigit);

		String componentId = buf.toString();

		return componentId;
	}

	/**
	 * @return a random SCT item identifier
	 */
	private static String generateRandomItemIndentifier() {
		Random random = new Random();
		// nextInt excludes top value, add 1 to make it inclusive
		int randomNum = random.nextInt(99999999 - 100 + 1) + 100;
		return Integer.toString(randomNum);
	}

}
