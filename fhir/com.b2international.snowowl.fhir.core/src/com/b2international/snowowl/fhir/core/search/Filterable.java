/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.search;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;

/**
 * Annotation to mark resources with the supported filter parameters
 * 
 * @since 7.17.0
 */
@Retention(RUNTIME)
@Repeatable(value = Filterables.class)
public @interface Filterable {

	/**
	 * Returns the name of the filter
	 * @return
	 */
	String filter();
	
	/**
	 * Returns true if the filter request allows multiple values to be defined
	 * @return
	 */
	boolean supportsMultipleValues() default false;
	
	/**
	 * Returns the supported string filter parameter values
	 * @return
	 */
	String[] values()  default {};
}
