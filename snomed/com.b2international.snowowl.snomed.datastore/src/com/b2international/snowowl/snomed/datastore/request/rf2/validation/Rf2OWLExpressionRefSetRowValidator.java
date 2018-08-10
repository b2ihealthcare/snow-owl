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
public class Rf2OWLExpressionRefSetRowValidator extends Rf2RefSetRowValidator {

	public Rf2OWLExpressionRefSetRowValidator(Rf2ValidationIssueReporter reporter, String[] values) {
		super(reporter, values);
	}

	@Override
	protected void validate(String[] values) {
		super.validate(values);
		final String memberId = values[0];
		final String owlExpression = values[6];
		
		if (Strings.isNullOrEmpty(owlExpression)) {
			reportError(String.format("Owl expression field was empty for '%s'", memberId));
		}
	}
	
	
}
