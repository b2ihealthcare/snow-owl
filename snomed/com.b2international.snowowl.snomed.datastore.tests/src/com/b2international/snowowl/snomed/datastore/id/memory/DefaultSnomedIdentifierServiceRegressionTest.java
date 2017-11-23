/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.id.memory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.b2international.index.Index;
import com.b2international.index.Indexes;
import com.b2international.index.mapping.Mappings;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.datastore.config.SnomedIdentifierConfiguration;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.datastore.id.domain.SctId;
import com.b2international.snowowl.snomed.datastore.id.gen.ItemIdGenerationStrategy;
import com.b2international.snowowl.snomed.datastore.id.gen.SequentialItemIdGenerationStrategy;
import com.b2international.snowowl.snomed.datastore.id.reservations.ISnomedIdentiferReservationService;
import com.b2international.snowowl.snomed.datastore.id.reservations.Reservations;
import com.b2international.snowowl.snomed.datastore.internal.id.reservations.SnomedIdentifierReservationServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Provider;
import com.google.inject.util.Providers;

/**
 * @since 4.7
 */
public class DefaultSnomedIdentifierServiceRegressionTest {

	private static final class CyclingItemIdGenerationStrategy implements ItemIdGenerationStrategy {
		private final Iterator<String> itr;

		public CyclingItemIdGenerationStrategy(final String... itemIds) {
			this.itr = Iterables.cycle(itemIds).iterator();
		}

		@Override
		public String generateItemId(final String namespace, final ComponentCategory category) {
			return itr.next();
		}
	}

	private static final String INT_NAMESPACE = "";
	private static final String B2I_NAMESPACE = "1000129";

	private Index store;

	@Before
	public void init() {
		store = Indexes.createIndex(UUID.randomUUID().toString(), new ObjectMapper(), new Mappings(SctId.class));
		store.admin().create();
	}

	@After
	public void after() {
		store.admin().delete();
	}

	@Test
	public void issue_SO_1945_testItemIdPoolExhausted() throws Exception {
		final Provider<Index> storeProvider = Providers.of(store);
		final ItemIdGenerationStrategy idGenerationStrategy = new CyclingItemIdGenerationStrategy("1000", "1001");
		final ISnomedIdentifierService identifiers = new DefaultSnomedIdentifierService(storeProvider, idGenerationStrategy);

		final String first = Iterables.getOnlyElement(identifiers.generate(INT_NAMESPACE, ComponentCategory.CONCEPT, 1));
		assertThat(first).startsWith("1000");
		final String second = Iterables.getOnlyElement(identifiers.generate(INT_NAMESPACE, ComponentCategory.CONCEPT, 1));
		assertThat(second).startsWith("1001");

		/*
		 * The third attempt should generate itemId 1000 again,
		 * but that is already generated and no more itemIds are available
		 * therefore it will try to generate 1001, and that fails too, 
		 * rinse and repeat until maxIdGenerationAttempts are made
		 */
		try {
			identifiers.generate(INT_NAMESPACE, ComponentCategory.CONCEPT, 1);
		} catch (final BadRequestException e) {
			assertThat(e.getMessage()).isEqualTo(String.format("Couldn't generate identifier in maximum (%s) number of attempts",
					SnomedIdentifierConfiguration.DEFAULT_ID_GENERATION_ATTEMPTS));
		}
	}

	@Test
	public void issue_SO_2138_testItemIdsReturnedInSequence() throws Exception {
		final ISnomedIdentiferReservationService reservationService = new SnomedIdentifierReservationServiceImpl();
		final ItemIdGenerationStrategy idGenerationStrategy = new SequentialItemIdGenerationStrategy(store, reservationService);
		final ISnomedIdentifierService identifiers = new DefaultSnomedIdentifierService(store, idGenerationStrategy, reservationService, new SnomedIdentifierConfiguration());

		List<String> actualIds = ImmutableList.copyOf(identifiers.generate(INT_NAMESPACE, ComponentCategory.CONCEPT, 3));
		List<String> expectedIds = ImmutableList.of("100005", "101009", "102002");
		assertEquals(expectedIds, actualIds);

		actualIds = ImmutableList.copyOf(identifiers.generate(B2I_NAMESPACE, ComponentCategory.CONCEPT, 3));
		expectedIds = ImmutableList.of("11000129102", "21000129106", "31000129108");
		assertEquals(expectedIds, actualIds);

		// Make a surprise return to the INT namespace here 
		assertEquals("103007", Iterables.getFirst(identifiers.generate(INT_NAMESPACE, ComponentCategory.CONCEPT, 1), null));
	}
	
	@Test
	public void issue_SO_2138_testItemIdWraparound() throws Exception {
		final ISnomedIdentiferReservationService reservationService = new SnomedIdentifierReservationServiceImpl();
		final ItemIdGenerationStrategy idGenerationStrategy = new SequentialItemIdGenerationStrategy(store, reservationService);
		final ISnomedIdentifierService identifiers = new DefaultSnomedIdentifierService(store, idGenerationStrategy, reservationService, new SnomedIdentifierConfiguration());
		
		identifiers.register(ImmutableSet.of("999999999999998003"));
		
		List<String> actualIds = ImmutableList.copyOf(identifiers.generate(INT_NAMESPACE, ComponentCategory.CONCEPT, 2));
		List<String> expectedIds = ImmutableList.of("999999999999999006", "100005");
		assertEquals(expectedIds, actualIds);
	}
	
	@Test
	public void issue_SO_2138_testSkipReservedRange() throws Exception {
		
		final ISnomedIdentiferReservationService reservationService = new SnomedIdentifierReservationServiceImpl();
		reservationService.create("noTwoHundreds", Reservations.range(200L, 299L, null, ImmutableSet.of(ComponentCategory.CONCEPT)));
		
		final ItemIdGenerationStrategy idGenerationStrategy = new SequentialItemIdGenerationStrategy(store, reservationService);
		final ISnomedIdentifierService identifiers = new DefaultSnomedIdentifierService(store, idGenerationStrategy, reservationService, new SnomedIdentifierConfiguration());
		
		// The next item ID would be 200, if it weren't for the reserved range 200-299
		identifiers.register(ImmutableSet.of("199004"));
		
		assertEquals("300004", Iterables.getOnlyElement(identifiers.generate(INT_NAMESPACE, ComponentCategory.CONCEPT, 1)));
	}
	
	@Test
	public void issue_SO_2138_testSkipReservedRangeWithWraparound() throws Exception {
		final ISnomedIdentiferReservationService reservationService = new SnomedIdentifierReservationServiceImpl();
		reservationService.create("nothingAboveTwoHundred", Reservations.range(200L, 9999_9999_9999_999L, null, ImmutableSet.of(ComponentCategory.CONCEPT)));
		
		final ItemIdGenerationStrategy idGenerationStrategy = new SequentialItemIdGenerationStrategy(store, reservationService);
		final ISnomedIdentifierService identifiers = new DefaultSnomedIdentifierService(store, idGenerationStrategy, reservationService, new SnomedIdentifierConfiguration());
		
		// The next item ID would be 200, if it weren't for the reserved range, which goes to the maximum allowed value
		identifiers.register(ImmutableSet.of("199004"));
		
		assertEquals("100005", Iterables.getOnlyElement(identifiers.generate(INT_NAMESPACE, ComponentCategory.CONCEPT, 1)));
	}
	
	@Test(expected=IllegalStateException.class)
	public void issue_SO_2138_testCoveringReservedRanges() throws Exception {
		
		final ISnomedIdentiferReservationService reservationService = new SnomedIdentifierReservationServiceImpl();
		reservationService.create("nothingAboveOneHundredNinetyNine", Reservations.range(200L, 9999_9999_9999_999L, null, ImmutableSet.of(ComponentCategory.CONCEPT)));
		reservationService.create("nothingBelowOneHundredNinetyNine", Reservations.range(100L, 198L, null, ImmutableSet.of(ComponentCategory.CONCEPT)));
		
		final ItemIdGenerationStrategy idGenerationStrategy = new SequentialItemIdGenerationStrategy(store, reservationService);
		final ISnomedIdentifierService identifiers = new DefaultSnomedIdentifierService(store, idGenerationStrategy, reservationService, new SnomedIdentifierConfiguration());
		
		identifiers.register(ImmutableSet.of("198007"));
		
		assertEquals("199004", Iterables.getOnlyElement(identifiers.generate(INT_NAMESPACE, ComponentCategory.CONCEPT, 1)));
		
		// This attempt should not be able to generate any other value
		identifiers.generate(INT_NAMESPACE, ComponentCategory.CONCEPT, 1);
	}
}
