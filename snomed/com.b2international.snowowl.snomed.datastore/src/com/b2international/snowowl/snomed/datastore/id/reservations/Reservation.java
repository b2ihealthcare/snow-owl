/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.id.reservations;

import java.util.Collections;
import java.util.Set;

import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifier;

/**
 * Represents a SNOMEDT CT Identifier reservation. A reservation is a range of SNOMED CT Identifiers, which are reserved for later use, therefore they
 * are not allowed to be used as IDs for new components (if their IDs have to be generated).
 * 
 * @since 4.0
 */
public interface Reservation {

	/**
	 * @param identifier the component identifier to check
	 * @return <code>true</code> if the given identifier is included in this {@link Reservation}'s range, <code>false</code> otherwise
	 * @deprecated - use {@link #contains(SnomedIdentifier)}
	 */
	default boolean includes(SnomedIdentifier identifier) {
		return intersection(Collections.singleton(identifier)).contains(identifier);
	}
	
	/**
	 * @param identifiers - {@link Set} of {@link SnomedIdentifier} to check
	 * @return a {@link Set} of {@link SnomedIdentifier}s that are in the intersection of the set represented by this {@link Reservation} and the given set of identifiers, never <code>null</code>.  
	 */
	Set<SnomedIdentifier> intersection(Set<SnomedIdentifier> identifiers);

}
