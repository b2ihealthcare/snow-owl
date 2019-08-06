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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

import com.b2international.index.Index;
import com.b2international.index.Indexes;
import com.b2international.index.mapping.Mappings;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.cis.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.cis.SnomedIdentifier;
import com.b2international.snowowl.snomed.cis.SnomedIdentifierConfiguration;
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers;
import com.b2international.snowowl.snomed.cis.domain.SctId;
import com.b2international.snowowl.snomed.cis.gen.ItemIdGenerationStrategy;
import com.b2international.snowowl.snomed.cis.internal.reservations.SnomedIdentifierReservationServiceImpl;
import com.b2international.snowowl.snomed.cis.memory.DefaultSnomedIdentifierService;
import com.b2international.snowowl.snomed.cis.reservations.Reservation;
import com.b2international.snowowl.snomed.cis.reservations.Reservations;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 4.0
 */
public class ReservationImplTest {

	@Test
	public void whenReservingSingleID_ThenItShouldConflictWithThatIDOnly() throws Exception {
		final Reservation single = Reservations.single(Concepts.ROOT_CONCEPT);
		assertTrue(single.includes(SnomedIdentifiers.create(Concepts.ROOT_CONCEPT)));
		assertFalse(single.includes(SnomedIdentifiers.create(Concepts.FULLY_DEFINED)));
		assertFalse(single.includes(SnomedIdentifiers.create(Concepts.ADDITIONAL_RELATIONSHIP)));
	}
	
	@Test
	public void whenReservingRangeOfIDs_ThenItShouldConflictWithAllIDsInThatRangeIncludingBoundaries() throws Exception {
		final Index store = Indexes.createIndex(UUID.randomUUID().toString(), new ObjectMapper(), new Mappings(SctId.class));
		store.admin().create();
		final ISnomedIdentifierService identifierService = new DefaultSnomedIdentifierService(store, new ItemIdGenerationStrategy() {
			int counter = 200;
			@Override
			public Set<String> generateItemIds(String namespace, ComponentCategory category, int quantity, int attempt) {
				return IntStream.range(counter, counter + quantity).mapToObj(String::valueOf).collect(Collectors.toSet());
			}
		}, new SnomedIdentifierReservationServiceImpl(), new SnomedIdentifierConfiguration());
		final Set<ComponentCategory> components = Collections.singleton(ComponentCategory.CONCEPT);
		final Reservation range = Reservations.range(200, 300, "", components);
		final Set<String> componentIds = identifierService.generate(null, ComponentCategory.CONCEPT, 300 - 200 + 1);
		
		for (String id : componentIds) { 
			final SnomedIdentifier identifier = SnomedIdentifiers.create(id);
			assertTrue(range.includes(identifier));
		}
		
		store.admin().delete();
	}
	
}
