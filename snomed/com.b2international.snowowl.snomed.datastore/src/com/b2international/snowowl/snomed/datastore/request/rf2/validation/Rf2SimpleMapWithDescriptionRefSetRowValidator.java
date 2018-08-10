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

/**
 * @since 7.0
 */
public class Rf2SimpleMapWithDescriptionRefSetRowValidator extends Rf2RefSetRowValidator {

	public Rf2SimpleMapWithDescriptionRefSetRowValidator(Rf2ValidationResponseEntity validationEntity, String[] values) {
		super(validationEntity, values);
	}
	
	@Override
	protected void validate(String[] values) {
		super.validate(values);
		validateSpecialFields(values);
	}
	
	private void validateSpecialFields(String[] values) {
		final String memberId = values[0];
		final String mapTarget = values[6];
		final String mapTargetDescription = values[7];
		if (Strings.isNullOrEmpty(mapTarget)) {
			reportIssue(Rf2ValidationType.ERROR, String.format("Map target field was empty for '%s'", memberId));
		}
		
		if (Strings.isNullOrEmpty(mapTargetDescription)) {
			reportIssue(Rf2ValidationType.WARNING, String.format("Map target description field was empty for '%s'", memberId));
		}
	}

}
