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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.b2international.snowowl.fhir.core.search.FhirUriParameterDefinition.FhirRequestParameterType;
import com.b2international.snowowl.fhir.core.search.FhirUriParameterDefinition.SearchRequestParameterModifier;

/**
 * Annotation to mark a resource field to support search.
 * @since 7.14
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Searchable {
	
	/**
	 * Name of the field, if ommitted, the name of the field is used
	 * @return
	 */
	String name() default "";
	
	/**
	 * Type of the searchable parameter.
	 * @see {@link FhirRequestParameterType}
	 * @see https://www.hl7.org/fhir/search.html#ptypes
	 * @return
	 */
	String type() default "String";

	/**
	 * Supported modifiers for the given searchable property.
	 * E.g. exact, missing, below, etc.
	 * @see {@link SearchRequestParameterModifier}
	 * @return
	 */
	String[] modifiers() default {};
	
	/**
	 * Returns true if the filter request allows multiple values to be defined
	 * @return
	 */
	boolean supportsMultipleValues() default false;
	
}
