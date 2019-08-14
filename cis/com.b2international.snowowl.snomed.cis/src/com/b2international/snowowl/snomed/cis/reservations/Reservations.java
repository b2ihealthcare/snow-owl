/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.cis.reservations;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.cis.SnomedIdentifier;
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers;
import com.b2international.snowowl.snomed.cis.internal.reservations.IdSetReservation;
import com.b2international.snowowl.snomed.cis.internal.reservations.ReservationRangeImpl;

/**
 * @since 4.0
 */
public abstract class Reservations {

	private Reservations() { }
	
	/**
	 * Creates a {@link Reservation} for the given single component identifier.
	 * 
	 * @param componentId the single identifier to reserve
	 * @return a {@link Reservation} instance for the given identifier
	 */
	public static Reservation single(final String componentId) {
		final SnomedIdentifier id = SnomedIdentifiers.create(componentId);
		return new ReservationRangeImpl(id.getItemId(), id.getItemId(), id.getNamespace(), Collections.singleton(id.getComponentCategory()));
	}

	/**
	 * @param itemIdMin the range's minimum value (inclusive)
	 * @param itemIdMax the range's maximum value (inclusive)
	 * @param namespace the namespace ID to use, or <code>null</code> to indicate the International namespace
	 * @param components the set of component types affected, cannot be empty.
	 * @return a {@link Reservation} for the given range specifications
	 */
	public static Reservation range(final long itemIdMin, final long itemIdMax, final String namespace, final Collection<ComponentCategory> components) {
		return new ReservationRangeImpl(itemIdMin, itemIdMax, namespace, components);
	}

	/**
	 * Creates a {@link Reservation} for the given component identifiers.
	 * 
	 * @param componentIds to reserve
	 * @return a {@link Reservation} instance for the given identifiers
	 */
	public static Reservation idSetReservation(final Set<String> componentIds) {
		return new IdSetReservation(componentIds);
	}

}
