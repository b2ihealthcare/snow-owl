/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request.rf2.importer;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.core.request.io.ImportDefectAcceptor.ImportDefectBuilder;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.datastore.request.rf2.validation.Rf2ValidationDefects;

/**
 * Represents a SNOMED CT Relationship with a value literal.
 * 
 * @since 7.17
 */
final class Rf2ConcreteValueContentType implements Rf2ContentType<SnomedRelationship> {

	@Override
	public SnomedRelationship create() {
		return new SnomedRelationship();
	}

	@Override
	public String getContainerId(String[] values) {
		final String sourceId = values[4];
		return sourceId;
	}

	@Override
	public String[] getHeaderColumns() {
		return SnomedRf2Headers.CONCRETE_VALUE_HEADER;
	}

	@Override
	public void resolve(SnomedRelationship component, String[] values) {
		component.setSourceId(values[4]);
		// XXX: all imported numbers will be of decimal type
		component.setValue(values[5]);
		component.setRelationshipGroup(Integer.parseInt(values[6]));
		component.setTypeId(values[7]);
		component.setCharacteristicTypeId(values[8]);
		component.setModifierId(values[9]);
		component.setUnionGroup(0);
	}

	@Override
	public String getType() {
		return "concrete-value";
	}
	
	@Override
	public LongSet getDependencies(String[] values) {
		return PrimitiveSets.newLongOpenHashSet(
			Long.parseLong(values[3]), // moduleId
			Long.parseLong(values[4]), // sourceId
			// "value" is not a dependency
			// "relationshipGroup" is not a dependency
			Long.parseLong(values[7]), // typeId
			Long.parseLong(values[8]), // characteristicTypeId
			Long.parseLong(values[9])  // modifierId
		);
	}
	
	@Override
	public void validateByContentType(ImportDefectBuilder defectBuilder, String[] values) {
		final String relationshipId = values[0];
		final String sourceId = values[4];
		final String value = values[5];
		final String typeId = values[7];
		final String characteristicTypeId = values[8];
		final String modifierId = values[9];
		
		// The identifier of a concrete value component should be in the "relationship" category
		validateByComponentCategory(defectBuilder, relationshipId, ComponentCategory.RELATIONSHIP);
		validateConceptIds(defectBuilder, sourceId, typeId, characteristicTypeId, modifierId);
		
		/* 
		 * Validate if the literal is valid:
		 * 
		 * - starts with # and contains a . -> should be a valid decimal (double) number 
		 * - starts with #                  -> should be a valid integer
		 * - starts and ends with "         -> should be a quoted string, with other double quotes escaped
		 */
		if (value.startsWith("#")) {
			// Remove prefix
			final String numericValue = value.substring(1);
			if (numericValue.contains(".")) {
				defectBuilder
					.whenThrows(() -> Double.parseDouble(numericValue))
					.error("%s %s", relationshipId, Rf2ValidationDefects.RELATIONSHIP_VALUE_INVALID_DECIMAL);
			} else {
				defectBuilder
					.whenThrows(() -> Integer.parseInt(numericValue))
					.error("%s %s", relationshipId, Rf2ValidationDefects.RELATIONSHIP_VALUE_INVALID_INTEGER);
			}
			
			return;
		}
		
		if (value.startsWith("\"") && value.endsWith("\"")) {
			// Remove prefix and suffix
			final String quotedValue = value.substring(1, value.length() - 1); 
			
			// If the string ends with an escape character, we have an unclosed string
			defectBuilder
				.when(quotedValue.endsWith("\\"))
				.error("%s %s", relationshipId, Rf2ValidationDefects.RELATIONSHIP_VALUE_INVALID_STRING);
			
			// All remaining occurrences of a quote character should be escaped
			int quoteIndex = -1;
			while ((quoteIndex = quotedValue.indexOf('"', quoteIndex + 1)) != -1) {
				final char beforeQuote = (quoteIndex > 0) ? quotedValue.charAt(quoteIndex - 1) : 0;
				
				defectBuilder
					.when(beforeQuote != '\\')
					.error("%s %s", relationshipId, Rf2ValidationDefects.RELATIONSHIP_VALUE_INVALID_STRING);
			}
			
			return;
		}
	}
}
