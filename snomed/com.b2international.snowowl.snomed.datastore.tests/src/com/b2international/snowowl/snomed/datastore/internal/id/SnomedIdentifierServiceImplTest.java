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
package com.b2international.snowowl.snomed.datastore.internal.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.b2international.snowowl.snomed.datastore.ComponentNature;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.datastore.id.gen.ItemIdGenerationStrategy;
import com.b2international.snowowl.snomed.datastore.id.reservations.ISnomedIdentiferReservationService;
import com.b2international.snowowl.snomed.datastore.id.reservations.Reservations;
import com.b2international.snowowl.snomed.datastore.internal.id.reservations.SnomedIdentifierReservationServiceImpl;

/**
 * @since 4.0
 */
@RunWith(MockitoJUnitRunner.class)
public class SnomedIdentifierServiceImplTest {

	private ISnomedIdentifierService ids;
	
	private ISnomedIdentiferReservationService reservations = new SnomedIdentifierReservationServiceImpl();
	
	@Mock
	private ItemIdGenerationStrategy itemIdGenerationStrategy;

	@Before
	public void givenIdentifierService() {
		this.ids = new SnomedIdentifierServiceImpl(reservations, itemIdGenerationStrategy);
	}
	
	@Test
	public void whenReservingARangeOfIDs_ThenItShouldDisallowAnyIDToBeGeneratedFromThatRange() throws Exception {
		this.reservations.create("range_100_102", Reservations.range(100L, 102L, null, Collections.singleton(ComponentNature.CONCEPT)));
		when(itemIdGenerationStrategy.generateItemId()).thenReturn("100", "101", "102", "103");
		final String id = ids.generateId(ComponentNature.CONCEPT);
		assertThat(id).startsWith("103");
	}
	
}
