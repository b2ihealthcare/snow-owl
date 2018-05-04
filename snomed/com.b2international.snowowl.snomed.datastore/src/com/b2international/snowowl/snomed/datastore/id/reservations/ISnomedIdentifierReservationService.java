/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg * 
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

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * SNOMED CT Identifier reservation service interface. Capable of creating reservation of SNOMED CT Identifiers. A {@link Reservation} is denoted by a
 * simple unique name.
 * 
 * @since 4.0
 */
public interface ISnomedIdentifierReservationService {

	/**
	 * Create a SNOMED CT Identifier {@link Reservation} in the system to restrict certain identifiers from being generated for new components.
	 * 
	 * @param reservationName
	 * @param reservation
	 */
	void create(String reservationName, Reservation reservation);

	/**
	 * Returns all current {@link Reservation} in the system.
	 * 
	 * @return
	 */
	Collection<Reservation> getReservations();

	/**
	 * Returns a {@link Reservation} item found by the defined reservationName.
	 * 
	 * @param reservationName
	 * @return
	 */
	Reservation getReservation(String reservationName);

	/**
	 * Delete a previously created reservation.
	 * 
	 * @param reservationName
	 */
	void delete(String reservationName);

	/**
	 * Returns whether the given componentId is reserved for later use or not.
	 * 
	 * @param componentId - the ID to check
	 * @return <code>true</code> if reserved, <code>false</code> otherwise
	 * @deprecated - use {@link #isReserved(Set)} instead
	 */
	default boolean isReserved(String componentId) {
		return isReserved(Collections.singleton(componentId)).contains(componentId);
	}
	
	/**
	 * Returns with a {@link Set} of component IDs that are already taken and in use in the system among the given {@link Set} of component IDs.
	 * 
	 * @param componentIdsToCheck - the IDs to check
	 * @return a never <code>null</code> {@link Set} of component IDs that are already in use in the system
	 * @since 6.5
	 */
	Set<String> isReserved(Set<String> componentIdsToCheck);

}
