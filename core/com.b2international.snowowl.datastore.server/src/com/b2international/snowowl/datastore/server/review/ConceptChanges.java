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
package com.b2international.snowowl.datastore.server.review;

import java.util.Set;

/**
 * @since 4.2
 */
public interface ConceptChanges {

	/**
	 * Returns the associated review's unique identifier.
	 */
	String id();

	/**
	 * Returns a set of SNOMED CT concept identifiers which were marked as new in the comparison.
	 */
	Set<String> newConcepts();

	/**
	 * Returns a set of SNOMED CT concept identifiers which were marked as changed in the comparison.
	 * <p>
	 * Changes on inbound relationships are not taken into account.
	 */
	Set<String> changedConcepts();

	/**
	 * Returns a set of SNOMED CT concept identifiers which were marked as deleted in the comparison.
	 */
	Set<String> deletedConcepts();
}
