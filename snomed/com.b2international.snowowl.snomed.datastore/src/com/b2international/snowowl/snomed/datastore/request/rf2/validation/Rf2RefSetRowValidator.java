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

import java.util.UUID;

import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * @since 7.0
 */
public abstract class Rf2RefSetRowValidator extends AbstractRf2RowValidator {

	public Rf2RefSetRowValidator(Rf2ValidationIssueReporter reporter, String[] values) {
		super(reporter, values);
	}
	
	@Override
	@OverridingMethodsMustInvokeSuper
	protected void validate(String[] values) {
		addValidatableConceptIds(values);
		final String memberId = values[0];
		final String referencedComponentId = values[5];
		validateIds(referencedComponentId);
		try {
			UUID.fromString(memberId);
		} catch (IllegalArgumentException e) {
			reportError(String.format("Invalid UUID '%s' in release file", memberId));
		}
		
	}
	
	private void addValidatableConceptIds(String[] values) {
		final String refsetId = values[4];
		
		addValidatableConcept(refsetId);
	}
	
}
