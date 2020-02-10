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
package com.b2international.snowowl.test.commons.snomed;
import com.b2international.commons.VerhoeffCheck;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.cis.gen.RandomItemIdGenerationStrategy;
import com.google.common.base.Strings;


/**
 * 
 * @since 6.4
 */
public abstract class RandomSnomedIdentiferGenerator {

	private RandomSnomedIdentiferGenerator() {}
	
	public final static String generateConceptId() {
		return generateConceptId("");
	}
	
	public final static String generateConceptId(String namespace) {
		return generateSnomedId(ComponentCategory.CONCEPT, namespace);
	}
	
	public final static String generateDescriptionId() {
		return generateDescriptionId("");
	}
	
	public final static String generateDescriptionId(String namespace) {
		return generateSnomedId(ComponentCategory.DESCRIPTION, namespace);
	}

	public final static String generateRelationshipId() {
		return generateRelationshipId("");
	}
	
	public final static String generateRelationshipId(String namespace) {
		return generateSnomedId(ComponentCategory.RELATIONSHIP, namespace);
	}
	
	private static String generateSnomedId(ComponentCategory category, String selectedNamespace) {
		final StringBuilder builder = new StringBuilder();
		// generate the SCT Item ID
		builder.append(new RandomItemIdGenerationStrategy().generateItemIds(selectedNamespace, category, 1, 1).stream().findFirst().get());

		// append namespace and the first part of the partition-identifier
		if (Strings.isNullOrEmpty(selectedNamespace)) {
			builder.append('0');
		} else {
			builder.append(selectedNamespace);
			builder.append('1');
		}

		// append the second part of the partition-identifier
		builder.append(category.ordinal());

		// calc check-digit
		builder.append(VerhoeffCheck.calculateChecksum(builder, false));

		return builder.toString();
	}
	
}