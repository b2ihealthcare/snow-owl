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
 * Implementations allow clients to generate component identifiers conforming to the specified constraints.
 */
public interface IdGenerationStrategy {

	/**
	 * Generates a component identifier.
	 * <p>
	 * This method may return different results each time it is invoked. 
	 * 
	 * @return the generated identifier
	 */
	String getId();
}
