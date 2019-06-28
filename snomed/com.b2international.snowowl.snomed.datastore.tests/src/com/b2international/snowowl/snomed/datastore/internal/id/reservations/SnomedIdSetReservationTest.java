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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.Test;

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifier;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.id.reservations.Reservation;
import com.b2international.snowowl.snomed.datastore.id.reservations.Reservations;
import com.google.common.collect.Sets;

/**
 * @since 6.17
 */
public class SnomedIdSetReservationTest {
	
	@Test
	public void testIdSetReservationReservedIds() {
		final Reservation idSetReservation = Reservations.idSetReservation(Sets.newHashSet(Concepts.MODULE_ROOT, Concepts.MODULE_SCT_CORE));
		final SnomedIdentifier rootIdentifier = SnomedIdentifiers.create(Concepts.ROOT_CONCEPT);
		final SnomedIdentifier sctCoreIdentifier = SnomedIdentifiers.create(Concepts.MODULE_SCT_CORE);
		
		final Set<SnomedIdentifier> reservedIds = idSetReservation.intersection(Sets.newHashSet(rootIdentifier, sctCoreIdentifier));
		
		assertThat(reservedIds).containsOnly(sctCoreIdentifier);
	}
	
}

