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
package com.b2international.snowowl.fhir.api.tests.validation;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Rule;
import org.junit.rules.ExpectedException;

import com.b2international.snowowl.fhir.api.tests.FhirTest;
import com.b2international.snowowl.fhir.core.exceptions.ValidationException;

/**
 * @since 6.3
 *
 */
public class ValidatorTest<T> extends FhirTest {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	/**
	 * Validates this operation outcome.
	 * @throws ValidationException
	 */
	public void validate(T object) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<T>> violations = validator.validate(object);
			if (!violations.isEmpty()) {
				
//				violations.forEach(cv -> {
//					
//					System.out.println(cv.toString());
//					System.out.println("Message: " + cv.getMessage());
//					System.out.println(" xMessage template: " + cv.getMessageTemplate());
//					System.out.println("Invalid value: " + cv.getInvalidValue());
//					System.out.println("Leaf bean: " + cv.getLeafBean());
//					System.out.println("Property path: " + cv.getPropertyPath());
//					System.out.println("Root bean (Coding): " + cv.getRootBean());
//					System.out.println("Root bean class: " + cv.getRootBeanClass());
//					System.out.println(" xConstraint descriptor: " + cv.getConstraintDescriptor());
//					System.out.println(" xExec parameters: " + cv.getExecutableParameters());
//					System.out.println(" xExec return value: " + cv.getExecutableReturnValue());
//					
//				});
				ValidationException validationException = new ValidationException(violations);
				throw validationException;
			}
	}

}
