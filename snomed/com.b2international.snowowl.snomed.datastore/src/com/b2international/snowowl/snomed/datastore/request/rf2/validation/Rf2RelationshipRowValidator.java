/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request.rf2.validation;

import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.google.common.collect.ImmutableList;

/**
 * @since 7.0
 */
public class Rf2RelationshipRowValidator extends AbstractRf2RowValidator {

	private final ComponentCategory category = ComponentCategory.RELATIONSHIP;
	
	public Rf2RelationshipRowValidator(Rf2ValidationIssueReporter reporter, String[] values) {
		super(reporter, values);
	}

	@Override
	protected void validate(String[] values) {
		final String relationShipId = values[0];
		validateId(relationShipId, category);
		
		final String sourceId = values[4];
		final String destinationId = values[5];
		validateSourceDestinationEquity(sourceId, destinationId);
		
		addValidatableConceptIds(values);
	}
	
	private void validateSourceDestinationEquity(String sourceId, String destinationId) {
		if (sourceId.equals(destinationId)) {
			reportError(Rf2ValidationDefects.RELATIONSHIP_SOURCE_DESTINATION_EQUALS.getLabel());
		}
	}
	
	private void addValidatableConceptIds(String[] values) {
		final String sourceId = values[4];
		final String destinationId = values[5];
		final String typeId = values[7];
		final String characteristicTypeId = values[8];
		final String modifierId = values[9];
		
		addValidatableConcepts(ImmutableList.of(sourceId, destinationId, typeId, characteristicTypeId, modifierId));
	}

}
