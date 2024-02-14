/*
 * Copyright 2011-2024 B2i Healthcare, https://b2ihealthcare.com
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

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import jakarta.validation.ConstraintViolation;

/**
 * @since 3.4
 */
public class ConstraintViolations {

	private ConstraintViolations() {
	}

	/**
	 * Formats the given {@link ConstraintViolation} to a readable format.
	 * 
	 * @param violation
	 * @return
	 */
	public static <T> String format(ConstraintViolation<T> violation) {
		return String.format("'%s' %s (was '%s')", violation.getPropertyPath(), violation.getMessage(),
				violation.getInvalidValue());
	}

	/**
	 * Formats the given set of {@link ConstraintViolation} to a list of
	 * readable format.
	 * 
	 * @param violations
	 * @return
	 */
	public static List<String> format(Collection<? extends ConstraintViolation<?>> violations) {
		final Set<String> errors = Sets.newHashSet();
		for (ConstraintViolation<?> v : violations) {
			errors.add(format(v));
		}
		return Ordering.natural().immutableSortedCopy(errors);
	}

}