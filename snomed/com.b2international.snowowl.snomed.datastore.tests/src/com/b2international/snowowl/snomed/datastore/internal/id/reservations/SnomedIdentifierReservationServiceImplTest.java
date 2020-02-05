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
package com.b2international.snowowl.snomed.datastore.internal.id.reservations;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.cis.internal.reservations.SnomedIdentifierReservationServiceImpl;
import com.b2international.snowowl.snomed.cis.reservations.Reservation;
import com.b2international.snowowl.snomed.cis.reservations.Reservations;

/**
 * @since 4.0
 */
public class SnomedIdentifierReservationServiceImplTest {

	private static final String SINGLE_RESERVATION = "single";
	private static final String RANGE_RESERVATION = "range";
	
	private SnomedIdentifierReservationServiceImpl reservationService;

	@Before
	public void givenSnomedIdentifierReservationService() {
		this.reservationService = new SnomedIdentifierReservationServiceImpl();
	}
	
	@Test
	public void whenReservingASingleSnomedIdentifier_ThenServiceMustStoreIt() throws Exception {
		final Reservation reservation = Reservations.single(Concepts.ROOT_CONCEPT);
		this.reservationService.create(SINGLE_RESERVATION, reservation);
		assertThat(this.reservationService.getReservations()).contains(reservation);
		assertThat(this.reservationService.getReservation(SINGLE_RESERVATION)).isEqualTo(reservation);
	}
	
	@Test
	public void whenCreatingMoreThanOneReservation_ThenServiceMustStoreThem() throws Exception {
		final Reservation single = Reservations.single(Concepts.ROOT_CONCEPT);
		final Reservation range = Reservations.range(100, 200, null, Collections.singleton(ComponentCategory.CONCEPT));
		this.reservationService.create(SINGLE_RESERVATION, single);
		this.reservationService.create(RANGE_RESERVATION, range);
		assertThat(this.reservationService.getReservations()).contains(single, range);
	}
	
	@Test
	public void whenCreatingSingleIDReservation_ThenReturnIsReservedTrueForThatID() throws Exception {
		final Reservation single = Reservations.single(Concepts.ROOT_CONCEPT);
		this.reservationService.create(SINGLE_RESERVATION, single);
		assertThat(this.reservationService.isReserved(Concepts.ROOT_CONCEPT)).isTrue();
	}
	
	@Test
	public void whenDeletingReservation_ThenReturnNullAndAllowIDToBeUsed() throws Exception {
		whenCreatingSingleIDReservation_ThenReturnIsReservedTrueForThatID();
		this.reservationService.delete(SINGLE_RESERVATION);
		assertThat(this.reservationService.getReservation(SINGLE_RESERVATION)).isNull();
		assertThat(this.reservationService.isReserved(Concepts.ROOT_CONCEPT)).isFalse();
	}

}
