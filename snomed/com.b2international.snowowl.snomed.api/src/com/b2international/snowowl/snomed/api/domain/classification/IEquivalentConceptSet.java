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
package com.b2international.snowowl.snomed.api.domain.classification;

import java.util.List;

/**
 * Represents an equivalent concept set, in which all concepts have the same description logic representation, without
 * any extra defining relationships which would differentiate them from each other.
 * <p>
 * A special equivalent concept set is the set of unsatisfiable concepts, which are also equivalent to {@code Nothing}
 * (the "empty set").
 */
public interface IEquivalentConceptSet {

	/**
	 * Checks if this set represents the unsatisfiable concept set.
	 *  
	 * @return {@code true} if concepts in this set are unsatisfiable, {@code false} if it is a regular equivalent set
	 */
	boolean isUnsatisfiable();

	/**
	 * Returns the list of contained equivalent concepts.
	 * 
	 * @return the equivalent concepts
	 */
	List<IEquivalentConcept> getEquivalentConcepts();
}
