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

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

/**
 * @since 7.0
 */
public class Rf2ExtendedMapRefSetRowValidator extends Rf2RefSetRowValidator {

	public Rf2ExtendedMapRefSetRowValidator(Rf2ValidationIssueReporter reporter, String[] values) {
		super(reporter, values);
	}
	
	@Override
	protected void validate(String[] values) {
		super.validate(values);
		addValidatableConcepts(values);
		validateRowsIds(values);
	}
	
	private void validateRowsIds(String[] values) {
		final String memberId = values[0];
		final String mapGroup = values[6];
		final String mapPriority = values[7];
		final String mapRule = values[8];
		final String mapAdvice = values[9];
		final String mapTarget = values[10];
		
		if (Strings.isNullOrEmpty(mapGroup)) {
			reportError(String.format("Map group field was empty for '%s' in a release file", memberId));
		}
		
		if (Strings.isNullOrEmpty(mapPriority)) {
			reportError(String.format("Map priority field was empty for '%s' in a release file", memberId));
		}
		
		if (Strings.isNullOrEmpty(mapRule)) {
			reportError(String.format("Map rule field was empty for '%s' in a release file", memberId));
		}
		
		if (Strings.isNullOrEmpty(mapAdvice)) {
			reportError(String.format("Map advice field was empty for '%s' in a release file", memberId));
		}
		
		if (Strings.isNullOrEmpty(mapTarget)) {
			reportError(String.format("Map target field was empty for '%s' in a release file", memberId));
		}
	}
	
	private void addValidatableConcepts(String[] values) {
		final String correlationId = values[11];
		final String mapCategoryId = values[12];
		
		addValidatableConcepts(ImmutableList.of(correlationId, mapCategoryId));
	}
	
}
