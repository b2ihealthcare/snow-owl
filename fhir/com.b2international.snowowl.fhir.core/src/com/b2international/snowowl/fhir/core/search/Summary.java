/*
 * Copyright 2018-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Set;

import com.google.common.collect.ImmutableSortedSet;

/**
 * Annotation to mark properties that are returned in a summary
 * @since 6.4
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface Summary {

	String TRUE = "true";
	String FALSE = "false";
	String TEXT = "text";
	String DATA = "data";
	String COUNT = "count";
	
	Set<String> VALUES = ImmutableSortedSet.of(TRUE, FALSE, TEXT, DATA, COUNT);
}
