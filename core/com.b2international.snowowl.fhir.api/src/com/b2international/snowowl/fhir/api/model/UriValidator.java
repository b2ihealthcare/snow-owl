/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.api.model;

import java.net.URI;
import java.net.URISyntaxException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Uri validator for the {@link Uri} annotation
 * 
 * @since 6.3
 */
public class UriValidator implements ConstraintValidator<Uri, String> {

	// private Uri uri;

	@Override
	public void initialize(Uri constraintAnnotation) {
	}

	@Override
	public boolean isValid(String object, ConstraintValidatorContext constraintContext) {

		//use @NotNull for null validation
		if (object == null) {
			return true;
		}
		try {
			new URI(object);
		} catch (URISyntaxException e) {

			return false;
		}
		return true;
	}
}