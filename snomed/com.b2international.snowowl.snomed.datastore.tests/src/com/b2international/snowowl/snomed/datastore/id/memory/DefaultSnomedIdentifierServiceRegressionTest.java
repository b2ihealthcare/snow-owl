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
package com.b2international.snowowl.snomed.datastore.id.memory;

import static com.google.common.collect.Sets.newLinkedHashSetWithExpectedSize;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.index.Index;
import com.b2international.index.Indexes;
import com.b2international.index.mapping.Mappings;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.datastore.config.SnomedIdentifierConfiguration;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.datastore.id.domain.SctId;
import com.b2international.snowowl.snomed.datastore.id.gen.ItemIdGenerationStrategy;
import com.b2international.snowowl.snomed.datastore.id.gen.SequentialItemIdGenerationStrategy;
import com.b2international.snowowl.snomed.datastore.id.reservations.ISnomedIdentifierReservationService;
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

	@Rule
	public TestWatcher w = new TestWatcher( ) {
		@Override
		protected void starting(Description description) {
			System.out.println("=============="+description+"================");
		}
	};
	
	/** Test-only item ID generator that return the same IDs over and over. */
	private static final class CyclingItemIdGenerationStrategy implements ItemIdGenerationStrategy {
		private final Iterator<String> itr;

		public CyclingItemIdGenerationStrategy(final String... itemIds) {
			this.itr = Iterables.cycle(itemIds).iterator();
		}

		@Override
		public Set<String> generateItemIds(final String namespace, final ComponentCategory category, int quantity, int attempt) {
			final Set<String> generatedItemIds = newLinkedHashSetWithExpectedSize(quantity);
			while (quantity > 0) {
				if (generatedItemIds.add(itr.next())) {
					quantity--;
				}
			}
			return generatedItemIds;
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
			assertThat(e.getMessage()).isEqualTo(String.format("Couldn't generate 1 identifiers [CONCEPT, INT] in maximum (%s) number of attempts",
					SnomedIdentifierConfiguration.DEFAULT_ID_GENERATION_ATTEMPTS));
		}
	}

	@Test
	public void issue_SO_2138_testItemIdsReturnedInSequence() throws Exception {
		final ISnomedIdentifierReservationService reservationService = new SnomedIdentifierReservationServiceImpl();
		final ItemIdGenerationStrategy idGenerationStrategy = new SequentialItemIdGenerationStrategy(reservationService);
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
		final ISnomedIdentifierReservationService reservationService = new SnomedIdentifierReservationServiceImpl();
		final SequentialItemIdGenerationStrategy idGenerationStrategy = new SequentialItemIdGenerationStrategy(reservationService);
		idGenerationStrategy.getOrCreateCounter(INT_NAMESPACE, ComponentCategory.CONCEPT).setCounter(999999999999999L);
		
		final ISnomedIdentifierService identifiers = new DefaultSnomedIdentifierService(store, idGenerationStrategy, reservationService, new SnomedIdentifierConfiguration());
		
		// Register a few existing SCTIDs to see if sorting works
		identifiers.register(ImmutableSet.of("999999999999997008", "999999999999998003", "100005", "101009"));
		
		List<String> actualIds = ImmutableList.copyOf(identifiers.generate(INT_NAMESPACE, ComponentCategory.CONCEPT, 2));
		List<String> expectedIds = ImmutableList.of("102002", "103007");
		assertEquals(expectedIds, actualIds);
	}
	
	@Test
	public void issue_SO_2138_testSkipReservedRangeWithWraparound() throws Exception {
		final ISnomedIdentifierReservationService reservationService = new SnomedIdentifierReservationServiceImpl();
		reservationService.create("nothingAboveTwoHundred", Reservations.range(200L, 8999_9999_9999_999L, null, ImmutableSet.of(ComponentCategory.CONCEPT)));
		
		final ItemIdGenerationStrategy idGenerationStrategy = new SequentialItemIdGenerationStrategy(reservationService);
		final ISnomedIdentifierService identifiers = new DefaultSnomedIdentifierService(store, idGenerationStrategy, reservationService, new SnomedIdentifierConfiguration());
		
		// The next item ID would be 200, if it weren't for the reserved range, which goes to the maximum allowed value
		identifiers.register(ImmutableSet.of("199004"));
		
		assertEquals("100005", Iterables.getOnlyElement(identifiers.generate(INT_NAMESPACE, ComponentCategory.CONCEPT, 1)));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void issue_SO_2138_testCoveringReservedRanges() throws Exception {
		
		final ISnomedIdentifierReservationService reservationService = new SnomedIdentifierReservationServiceImpl();
		reservationService.create("nothingAboveOneHundredNinetyNine", Reservations.range(200L, 8999_9999_9999_999L, null, ImmutableSet.of(ComponentCategory.CONCEPT)));
		reservationService.create("nothingBelowOneHundredNinetyNine", Reservations.range(100L, 198L, null, ImmutableSet.of(ComponentCategory.CONCEPT)));
		
		final ItemIdGenerationStrategy idGenerationStrategy = new SequentialItemIdGenerationStrategy(reservationService);
		final ISnomedIdentifierService identifiers = new DefaultSnomedIdentifierService(store, idGenerationStrategy, reservationService, new SnomedIdentifierConfiguration());
		
		identifiers.register(ImmutableSet.of("198007"));
		
		assertEquals("199004", Iterables.getOnlyElement(identifiers.generate(INT_NAMESPACE, ComponentCategory.CONCEPT, 1)));
		
		// This attempt should not be able to generate any other value
		System.err.println(identifiers.generate(INT_NAMESPACE, ComponentCategory.CONCEPT, 1));
	}
	
	@Test
	public void testQuadraticProbing() throws Exception {
		final ISnomedIdentifierReservationService reservationService = new SnomedIdentifierReservationServiceImpl();
		final ItemIdGenerationStrategy idGenerationStrategy = new SequentialItemIdGenerationStrategy(reservationService);
		final ISnomedIdentifierService identifiers = new DefaultSnomedIdentifierService(store, idGenerationStrategy, reservationService, new SnomedIdentifierConfiguration());

		identifiers.generate(INT_NAMESPACE, ComponentCategory.CONCEPT, 1);
		
		// Register a few existing SCTIDs that are "in the way"
		identifiers.register(ImmutableSet.of(
			"101009", // 1
			"102002"  // 2
			) 
		);
		
		assertEquals("103007", Iterables.getOnlyElement(identifiers.generate(INT_NAMESPACE, ComponentCategory.CONCEPT, 1)));
	}
	
	@Test
	public void testQuadraticProbing_wraparound() throws Exception {
		final ISnomedIdentifierReservationService reservationService = new SnomedIdentifierReservationServiceImpl();
		final SequentialItemIdGenerationStrategy idGenerationStrategy = new SequentialItemIdGenerationStrategy(reservationService);
		// next free itemId is 99999997
		idGenerationStrategy.getOrCreateCounter("1000154", ComponentCategory.CONCEPT).setCounter(99999997L);
		final ISnomedIdentifierService identifiers = new DefaultSnomedIdentifierService(store, idGenerationStrategy, reservationService, new SnomedIdentifierConfiguration());

		assertEquals("999999971000154106", Iterables.getOnlyElement(identifiers.generate("1000154", ComponentCategory.CONCEPT, 1)));
		
		// 99999998 and 2 becomes registered
		identifiers.register(ImmutableSet.of("999999981000154108", "21000154106"));

		// Attempt 1, quantity 2, 99999998 - 0
		// Attempt 2, quantity 1, 2 - 2
		// Attempt 3, quantity 1, 6 - 6
		List<String> actualIds = ImmutableList.copyOf(identifiers.generate("1000154", ComponentCategory.CONCEPT, 2));
		List<String> expectedIds = ImmutableList.of("999999991000154105", "11000154102");
		assertEquals(expectedIds, actualIds);
	}

	@Test
	public void testQuadraticProbing_skipReservedRange() throws Exception {
		final ISnomedIdentifierReservationService reservationService = new SnomedIdentifierReservationServiceImpl();
		reservationService.create("noTwoHundreds", Reservations.range(200L, 299L, "1000004", ImmutableSet.of(ComponentCategory.CONCEPT)));
		
		final ItemIdGenerationStrategy idGenerationStrategy = new SequentialItemIdGenerationStrategy(reservationService);
		final ISnomedIdentifierService identifiers = new DefaultSnomedIdentifierService(store, idGenerationStrategy, reservationService, new SnomedIdentifierConfiguration());
		
		// generate all IDs until 200
		identifiers.generate("1000004", ComponentCategory.CONCEPT, 199);
		
		// 300 becomes registered
		identifiers.register(ImmutableSet.of("3001000004102"));
		
		List<String> actualIds = ImmutableList.copyOf(identifiers.generate("1000004", ComponentCategory.CONCEPT, 2));
		List<String> expectedIds = ImmutableList.of("3011000004104", "3021000004108");
		assertEquals(expectedIds, actualIds);
	}
	
	@Test
	public void testQuadraticProbing_skipReservedRangeWithWraparound() throws Exception {
		final ISnomedIdentifierReservationService reservationService = new SnomedIdentifierReservationServiceImpl();
		reservationService.create("nothingAboveTwoHundred", Reservations.range(200L, 9999_9999L, "1000133", ImmutableSet.of(ComponentCategory.CONCEPT)));

		final SequentialItemIdGenerationStrategy idGenerationStrategy = new SequentialItemIdGenerationStrategy(reservationService);
		idGenerationStrategy.getOrCreateCounter("1000133", ComponentCategory.CONCEPT).setCounter(199L);
		
		final ISnomedIdentifierService identifiers = new DefaultSnomedIdentifierService(store, idGenerationStrategy, reservationService, new SnomedIdentifierConfiguration());

		// itemId counter is initialized with 199, next free itemId is 199
		assertEquals("1991000133107", Iterables.getOnlyElement(identifiers.generate("1000133", ComponentCategory.CONCEPT, 1)));

		// 1 becomes registered
		identifiers.register(ImmutableSet.of("11000133105"));

		List<String> actualIds = ImmutableList.copyOf(identifiers.generate("1000133", ComponentCategory.CONCEPT, 2));
		List<String> expectedIds = ImmutableList.of("21000133103", "31000133100");
		assertEquals(expectedIds, actualIds);
	}
}
