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
package com.b2international.snowowl.snomed.core.refset.automap;

/**
 * Enumeration to represent the state of an {@link AutoMapEntry}.
 * 
 */
public enum MappingState {
	/**
	 * The mapping has not been accepted (yet).
	 */
	NOT_ACCEPTED("Not accepted"),
	
	/**
	 * The mapping has been manually accepted.
	 */
	ACCEPTED("Accepted");
	
	private final String text;
	
	private MappingState(String text) {
		this.text = text;
	}
	
	@Override
	public String toString() {
		return text;
	}
}