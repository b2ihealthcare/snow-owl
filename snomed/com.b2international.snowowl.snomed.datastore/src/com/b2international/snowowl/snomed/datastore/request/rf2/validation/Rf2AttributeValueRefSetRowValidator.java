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

/**
 * @since 7.0
 */
public class Rf2AttributeValueRefSetRowValidator extends Rf2RefSetRowValidator {

	public Rf2AttributeValueRefSetRowValidator(Rf2ValidationResponseEntity validationEntity, String[] values) {
		super(validationEntity, values);
	}
	
	@Override
	protected void validate(String[] values) {
		super.validate(values);

		final String valueId = values[6];
		validateIds(valueId);
	}

}
