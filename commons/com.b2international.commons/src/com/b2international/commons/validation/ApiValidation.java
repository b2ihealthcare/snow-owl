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
package com.b2international.commons.validation;

import static java.util.Collections.singletonList;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationProviderResolver;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;
import javax.validation.metadata.BeanDescriptor;

import org.hibernate.validator.HibernateValidator;

import com.b2international.commons.exceptions.ValidationException;

/**
 * Custom ValidationUtil class to access powerful bean validation infrastructure
 * specified in JSR-303 at http://beanvalidation.org/1.1/spec/. The static
 * factory field is thread safe, so this class caches it and if you need a
 * validator instance just use the {@link #getValidator()} method to validate
 * Java Beans.
 * 
 * See for details: https://access.redhat.com/site/solutions/734273
 * 
 * @since 4.1.1
 * @see http://beanvalidation.org/1.1/spec/
 * @see http://hibernate.org/validator/
 */
public class ApiValidation {

	private static final ValidatorFactory FACTORY = Validation.byDefaultProvider()
			.providerResolver(new HibernateValidationProviderResolver()).configure().buildValidatorFactory();;

	private static class HibernateValidationProviderResolver implements ValidationProviderResolver {

		@Override
		public List getValidationProviders() {
			return singletonList(new HibernateValidator());
		}

	}
	
	private ApiValidation() {}

	/**
	 * Validates the given object using the Bean Validation 1.1 spec
	 * @param object - the object to validate
	 * @return - the object if valid
	 * @throws ValidationException - if there are validation constraint violations
	 */
	public static <T> T checkInput(T object) {
		final Set<ConstraintViolation<T>> violations = getValidator().validate(object);
		if (!violations.isEmpty()) {
			throw new ValidationException(violations);
		} else {
			return object;
		}
	}
	
	/**
	 * Returns a configured {@link Validator} instance for Java bean validation.
	 * 
	 * @return
	 */
	public static final Validator getValidator() {
		return new Validator() {
			
			@Override
			public <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, String propertyName, Object value, Class<?>... groups) {
				return null;
			}
			
			@Override
			public <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName, Class<?>... groups) {
				return null;
			}
			
			@Override
			public <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
				final Thread thread = Thread.currentThread();
				final ClassLoader oldClassLoader = thread.getContextClassLoader();
				try {
					thread.setContextClassLoader(ApiValidation.class.getClassLoader());
					return FACTORY.getValidator().validate(object, groups);
				} finally {
					thread.setContextClassLoader(oldClassLoader);
				}
			}
			
			@Override
			public <T> T unwrap(Class<T> type) {
				return null;
			}
			
			@Override
			public BeanDescriptor getConstraintsForClass(Class<?> clazz) {
				return null;
			}
			
			@Override
			public ExecutableValidator forExecutables() {
				return null;
			}
		};
	}
	
}
