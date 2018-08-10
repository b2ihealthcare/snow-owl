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
public class Rf2MRCMAttributeDomainRefSetRowValidator extends Rf2RefSetRowValidator {

	public Rf2MRCMAttributeDomainRefSetRowValidator(Rf2ValidationResponseEntity validationEntity, String[] values) {
		super(validationEntity, values);
	}
	
	@Override
	protected void validate(String[] values) {
		super.validate(values);
		addValidatableConcepts(values);
		validateSpecialFields(values);
	}
	
	private void validateSpecialFields(String[] values) {
		final String memberId = values[0];
		final String grouped = values[7];
		final String attributeCardinality = values[8];
		final String attributeInGroupCardinality = values[9];
		if (Strings.isNullOrEmpty(grouped)) {
			reportIssue(Rf2ValidationType.ERROR, String.format("Grouped field was empty for member '%s'", memberId));
		}
		
		if (Strings.isNullOrEmpty(attributeCardinality)) {
			reportIssue(Rf2ValidationType.ERROR, String.format("AttributeCardinality field was empty for member '%s'", memberId));
		}
		
		if (Strings.isNullOrEmpty(attributeInGroupCardinality)) {
			reportIssue(Rf2ValidationType.ERROR, String.format("AttributeInGroupCardinality field was empty for member '%s'", memberId));
		}
		
	}
	
	private void addValidatableConcepts(String[] values) {
		final String domainId = values[6];
		final String ruleStrenghtId = values[10];
		final String contentTypeId = values[11];
		addValidatableConcepts(ImmutableList.of(domainId, ruleStrenghtId, contentTypeId));
	}
	
}
