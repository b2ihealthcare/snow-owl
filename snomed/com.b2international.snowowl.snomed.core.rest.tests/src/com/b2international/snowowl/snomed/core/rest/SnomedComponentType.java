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
package com.b2international.snowowl.snomed.core.rest;

import java.util.Locale;

/**
 * Enumerates SNOMED CT component types used in the REST API tests.
 * 
 * @since 2.0
 */
public enum SnomedComponentType {

	CONCEPT,
	DESCRIPTION,
	RELATIONSHIP, 
	REFSET, 
	MEMBER;

	/**
	 * @return the all-lower case plural form of this type
	 */
	public String toLowerCasePlural() {
		return toString().toLowerCase(Locale.ENGLISH) + "s";
	}
}
