/*******************************************************************************
 * Copyright (c) 2018 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.validation.snomed.util;
import com.b2international.commons.VerhoeffCheck;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.datastore.id.gen.RandomItemIdGenerationStrategy;
import com.google.common.base.Strings;


/**
 * 
 * @since 6.4
 */
public abstract class RandomSnomedIdentiferGenerator {

	private RandomSnomedIdentiferGenerator() {}
	
	public final static String generateConceptId() {
		return generateSnomedId(ComponentCategory.CONCEPT);
	}
	
	public final static String generateDescriptionId() {
		return generateSnomedId(ComponentCategory.DESCRIPTION);
	}

	public final static String generateRelationshipId() {
		return generateSnomedId(ComponentCategory.RELATIONSHIP);
	}
	
	private static String generateSnomedId(ComponentCategory category) {
		final String selectedNamespace = "";
		final StringBuilder builder = new StringBuilder();
		// generate the SCT Item ID
		builder.append(new RandomItemIdGenerationStrategy().generateItemId(selectedNamespace, category, 1));

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