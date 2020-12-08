/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core;

import com.b2international.commons.exceptions.BadRequestException;

/**
 * Represents the type of the Code in a given CodeSystem. Generally speaking this is usually takes two forms:
 * <ul>
 *  <li>code - the actual code that represents a clinical meaning, concept or term</li>
 *  <li>category - a non-complete code, that represents some kind of categorization in the given code system (eg. blocks, categories, sections, chapters, etc.)</li>
 * </ul>
 * 
 * @since 7.13
 */
public enum CodeType {
	
	CATEGORY,
	
	CODE;

	public static CodeType valueOfIgnoreCase(String value) {
		for (CodeType codeType : values()) {
			if (codeType.name().equalsIgnoreCase(value)) {
				return codeType;
			}
		}
		throw new BadRequestException("Unrecognized CodeType value: '%s'. Allowed values are: 'code', 'category'.", value);
	}
	
}
