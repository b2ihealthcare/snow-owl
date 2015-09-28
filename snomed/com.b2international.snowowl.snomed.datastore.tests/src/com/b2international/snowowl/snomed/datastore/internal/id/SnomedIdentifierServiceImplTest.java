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

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.datastore.id.gen.ItemIdGenerationStrategy;
import com.b2international.snowowl.snomed.datastore.id.reservations.ISnomedIdentiferReservationService;
import com.b2international.snowowl.snomed.datastore.id.reservations.Reservations;
import com.b2international.snowowl.snomed.datastore.internal.id.reservations.SnomedIdentifierReservationServiceImpl;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

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
	public void whenGeneratingIDForInvalidComponentCategory_ThrowIllegalArgumentException() throws Exception {
		for (ComponentCategory category : Iterables.filter(newArrayList(ComponentCategory.values()), new Predicate<ComponentCategory>() {
			@Override
			public boolean apply(ComponentCategory input) {
				return input != ComponentCategory.CONCEPT && input != ComponentCategory.DESCRIPTION && input != ComponentCategory.RELATIONSHIP;
			}
		})) {
			try {
				ids.generateId(category);
				fail("IllegalArgumentException should be thrown in case of unrecognized ComponentCategory " + category);
			} catch (IllegalArgumentException e) {
				// ignore, this is expected to be throw
			}
		}
	}
	
	@Test
	public void whenReservingARangeOfIDs_ThenItShouldDisallowAnyIDToBeGeneratedFromThatRange() throws Exception {
		this.reservations.create("range_100_102", Reservations.range(100L, 102L, null, Collections.singleton(ComponentCategory.CONCEPT)));
		when(itemIdGenerationStrategy.generateItemId()).thenReturn("100", "101", "102", "103");
		final String id = ids.generateId(ComponentCategory.CONCEPT);
		assertThat(id).startsWith("103");
	}
	
}
