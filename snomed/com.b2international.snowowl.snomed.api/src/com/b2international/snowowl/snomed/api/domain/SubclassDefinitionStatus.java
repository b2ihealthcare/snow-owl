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
package com.b2international.snowowl.snomed.api.domain;

/**
 * Enumerates assertions about direct subclasses.
 */
public enum SubclassDefinitionStatus {

	/**
	 * Direct subclasses of the concept form a disjoint union.
	 */
	DISJOINT_SUBCLASSES(true),

	/**
	 * Direct subclasses of the concept can be incomplete, and their definition can overlap each other.
	 */
	NON_DISJOINT_SUBCLASSES(false);

	private final boolean exhaustive;

	private SubclassDefinitionStatus(final boolean exhaustive) {
		this.exhaustive = exhaustive;
	}

	/**
	 * Converts the definition status to a boolean.
	 * 
	 * @return {@code true} if direct subclasses of this concept form a disjoint union (in OWL terms), {@code false}
	 * otherwise
	 */
	public boolean isExhaustive() {
		return exhaustive;
	}
}
