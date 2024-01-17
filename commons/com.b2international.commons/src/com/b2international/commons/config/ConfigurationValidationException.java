/*
 * Copyright 2011-2015 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.commons.config;

import java.util.Collection;
import java.util.Set;

import javax.validation.ConstraintViolation;

import com.b2international.commons.validation.ConstraintViolations;
import com.google.common.collect.ImmutableSet;

/**
 * @since 3.4
 */
public class ConfigurationValidationException extends RuntimeException {

	private static final long serialVersionUID = -6108120930513005260L;

	private final Set<ConstraintViolation<?>> violations;
	
	/**
	 * Creates a new {@link ConfigurationValidationException} instance using the
	 * given violations.
	 * 
	 * @param violations
	 */
	public <T> ConfigurationValidationException(String path, Collection<ConstraintViolation<T>> violations) {
		super(formatMessage(path, ConstraintViolations.format(violations)));
		this.violations = ImmutableSet.<ConstraintViolation<?>>copyOf(violations);
	}
	
	/**
	 * @return the violations
	 */
	public Set<ConstraintViolation<?>> getViolations() {
		return violations;
	}
	
	/**
	 * Formats the 
	 * @param file
	 * @param errors
	 * @return
	 */
	protected static String formatMessage(String file, Collection<String> errors) {
        final StringBuilder msg = new StringBuilder(file);
        msg.append(errors.size() == 1 ? " has an error:" : " has the following errors:").append("\r\n");
        for (String error : errors) {
            msg.append("  * ").append(error).append("\r\n");
        }
        return msg.toString();
    }

}