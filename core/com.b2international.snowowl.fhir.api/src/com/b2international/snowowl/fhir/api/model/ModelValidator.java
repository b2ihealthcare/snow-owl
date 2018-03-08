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

import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.b2international.snowowl.core.exceptions.ValidationException;

/**
 * Annotation based validator superclass for FHIR model classes and data types. 
 * @since 6.3
 */
public abstract class ModelValidator<T> {
	
	public void validateModel(T model) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<T>> violations = validator.validate(model);
		if (!violations.isEmpty()) {
			//bit of a hack
			ValidationException validationException = new ValidationException(violations);
			Map<String, Object> additionalInfo = validationException.toApiError().getAdditionalInfo();
			throw new javax.validation.ValidationException(additionalInfo.toString());
		}
	}
	
	public T build() {
		T model = doBuild();
		validateModel(model);
		return model;
	}

	/**
	 * Performs the actual building of the model instance.
	 * @return T model instance built
	 */
	protected abstract T doBuild();

}
