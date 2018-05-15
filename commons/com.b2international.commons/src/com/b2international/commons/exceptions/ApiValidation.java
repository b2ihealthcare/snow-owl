/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.commons.exceptions;

import java.util.Set;

import javax.validation.ConstraintViolation;

import com.b2international.snowowl.hibernate.validator.ValidationUtil;

/**
 * @since 4.1.1
 */
public class ApiValidation {

	private ApiValidation() {}

	/**
	 * Validates the given object using the Bean Validation 1.1 spec
	 * @param object - the object to validate
	 * @return - the object if valid
	 * @throws ValidationException - if there are validation constraint violations
	 */
	public static <T> T checkInput(T object) {
		final Set<ConstraintViolation<T>> violations = ValidationUtil.getValidator().validate(object);
		if (!violations.isEmpty()) {
			throw new ValidationException(violations);
		} else {
			return object;
		}
	}
	
}
