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
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

/**
 * @since 7.0 
 */
public class Rf2DescriptionRowValidator extends AbstractRf2RowValidator {
	
	private final ComponentCategory category = ComponentCategory.DESCRIPTION;
	
	public Rf2DescriptionRowValidator(Rf2ValidationIssueReporter reporter, String[] values) {
		super(reporter, values);
	}

	@Override
	protected void validate(String[] values) {
		final String descriptionId = values[0];
		final String term = values[7];
		
		if (Strings.isNullOrEmpty(term)) {
			reportError(Rf2ValidationDefects.MISSING_DESCRIPTION_TERM.getLabel());
		}
		addValidatableConceptIds(values);
		validateId(descriptionId, category);
	}

	private void addValidatableConceptIds(String[] values) {
		final String conceptId = values[4];
		final String typeId = values[6];
		final String caseSignificanceId = values[8];
		
		addValidatableConcepts(ImmutableList.of(conceptId, typeId, caseSignificanceId));
	}

}
