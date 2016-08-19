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
package com.b2international.snowowl.snomed.core.domain;

/**
 * Enumerates available RF2 release formats for the SNOMED CT ontology.
 */
public enum Rf2ReleaseType {

	/**
	 * Delta RF2 publication format.
	 * Contains the most recent changes.
	 */
	DELTA("Delta"),

	/**
	 * Snapshot RF2 publication format.
	 * Contains all component with their latest state.
	 */
	SNAPSHOT("Snapshot"),

	/**
	 * Full RF2 publication format.
	 * Contains everything.
	 */
	FULL("Full");

	private String name;

	private Rf2ReleaseType(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
