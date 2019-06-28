/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.internal.id.reservations;

import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifier;
import com.b2international.snowowl.snomed.datastore.id.reservations.Reservation;

/**
 * @since 6.17
 */
public class IdSetReservation implements Reservation {

	private final Set<String> idsToExclude;

	public IdSetReservation(Set<String> idsToExclude) {
		this.idsToExclude = idsToExclude;
	}
	
	@Override
	public Set<SnomedIdentifier> intersection(Set<SnomedIdentifier> identifiers) {
		return identifiers.stream()
				.filter(id -> idsToExclude.contains(id.toString()))
				.collect(Collectors.toSet());
	}

}
