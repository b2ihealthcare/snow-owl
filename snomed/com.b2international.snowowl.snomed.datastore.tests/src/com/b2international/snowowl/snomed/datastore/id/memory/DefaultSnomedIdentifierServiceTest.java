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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.b2international.commons.FileUtils;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.store.IndexStore;
import com.b2international.snowowl.snomed.datastore.config.SnomedIdentifierConfiguration;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.datastore.id.cis.SctId;
import com.b2international.snowowl.snomed.datastore.id.gen.ItemIdGenerationStrategy;
import com.b2international.snowowl.snomed.datastore.id.gen.SequentialItemIdGenerationStrategy;
import com.b2international.snowowl.snomed.datastore.id.reservations.ISnomedIdentiferReservationService;
import com.b2international.snowowl.snomed.datastore.id.reservations.Reservations;
import com.b2international.snowowl.snomed.datastore.internal.id.reservations.SnomedIdentifierReservationServiceImpl;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.io.Files;

/**
 * @since 4.7
 */
public class DefaultSnomedIdentifierServiceTest {

	private static final class CyclingItemIdGenerationStrategy implements ItemIdGenerationStrategy {
		private final Iterator<String> itr;

		public CyclingItemIdGenerationStrategy(final String... itemIds) {
			this.itr = Iterables.cycle(itemIds).iterator();
		}

		@Override
		public String generateItemId(final String namespace, final ComponentCategory category, int attempt) {
			return itr.next();
		}
	}

	private static final String INT_NAMESPACE = null;
	private static final String B2I_NAMESPACE = "1000129";
	
	private IndexStore<SctId> store;
	
	@Before
	public void setup() {
		store = new IndexStore<>(Files.createTempDir(), SctId.class);
		
		store.configureSearchable(SctId.Fields.NAMESPACE);
		store.configureSearchable(SctId.Fields.PARTITION_ID);
		store.configureSearchable(SctId.Fields.SEQUENCE);
		store.configureSortable(SctId.Fields.SEQUENCE);
	}
	
	@After
	public void tearDown() {
		if (store != null) {
			store.dispose();
			FileUtils.deleteDirectory(store.getDirectory());
		}
	}

	@Test
	public void issue_SO_1945_testItemIdPoolExhausted() throws Exception {
		final ItemIdGenerationStrategy idGenerationStrategy = new CyclingItemIdGenerationStrategy("1000", "1001");
		final ISnomedIdentifierService identifiers = new DefaultSnomedIdentifierService(idGenerationStrategy);

		final String first = identifiers.generate(INT_NAMESPACE, ComponentCategory.CONCEPT);
		assertThat(first).startsWith("1000");
		final String second = identifiers.generate(INT_NAMESPACE, ComponentCategory.CONCEPT);
		assertThat(second).startsWith("1001");

		/*
		 * The third attempt should generate itemId 1000 again,
		 * but that is already generated and no more itemIds are available
		 * therefore it will try to generate 1001, and that fails too, 
		 * rinse and repeat until maxIdGenerationAttempts are made
		 */
		try {
			identifiers.generate(INT_NAMESPACE, ComponentCategory.CONCEPT);
		} catch (final BadRequestException e) {
			assertThat(e.getMessage()).isEqualTo(String.format("Couldn't generate identifier in %s number of attempts",
					SnomedIdentifierConfiguration.DEFAULT_ID_GENERATION_ATTEMPTS));
		}
	}

	@Test
	public void issue_SO_2138_testItemIdsReturnedInSequence() throws Exception {
		final ISnomedIdentiferReservationService reservationService = new SnomedIdentifierReservationServiceImpl();
		final ItemIdGenerationStrategy idGenerationStrategy = new SequentialItemIdGenerationStrategy(reservationService);
		final ISnomedIdentifierService identifiers = new DefaultSnomedIdentifierService(store, idGenerationStrategy, reservationService, new SnomedIdentifierConfiguration());

		List<String> actualIds = ImmutableList.copyOf(identifiers.generate(INT_NAMESPACE, ComponentCategory.CONCEPT, 3));
		List<String> expectedIds = ImmutableList.of("100005", "101009", "102002");
		assertEquals(expectedIds, actualIds);

		actualIds = ImmutableList.copyOf(identifiers.generate(B2I_NAMESPACE, ComponentCategory.CONCEPT, 3));
		expectedIds = ImmutableList.of("11000129102", "21000129106", "31000129108");
		assertEquals(expectedIds, actualIds);

		// Make a surprise return to the INT namespace here 
		assertEquals("103007", identifiers.generate(INT_NAMESPACE, ComponentCategory.CONCEPT));
	}

	@Test
	public void issue_SO_2138_testSkipReservedRange() throws Exception {
		final ISnomedIdentiferReservationService reservationService = new SnomedIdentifierReservationServiceImpl();
		reservationService.create("noTwoHundreds", Reservations.range(200L, 299L, null, ImmutableSet.of(ComponentCategory.CONCEPT)));
		
		final ItemIdGenerationStrategy idGenerationStrategy = new SequentialItemIdGenerationStrategy(reservationService);
		final ISnomedIdentifierService identifiers = new DefaultSnomedIdentifierService(store, idGenerationStrategy, reservationService, new SnomedIdentifierConfiguration());
		
		// The next item ID would be 200, if it weren't for the reserved range 200-299
		identifiers.generate(INT_NAMESPACE, ComponentCategory.CONCEPT, 100); // item IDs 100-199 assigned
		
		assertEquals("300004", identifiers.generate(INT_NAMESPACE, ComponentCategory.CONCEPT));
	}
	
	@Test
	public void issue_SO_2138_testSkipReservedRangeWithWraparound() throws Exception {
		final ISnomedIdentiferReservationService reservationService = new SnomedIdentifierReservationServiceImpl();
		reservationService.create("nothingAboveTwoHundred", Reservations.range(200L, 8999_9999_9999_999L, null, ImmutableSet.of(ComponentCategory.CONCEPT)));
		
		final ItemIdGenerationStrategy idGenerationStrategy = new SequentialItemIdGenerationStrategy(reservationService);
		final ISnomedIdentifierService identifiers = new DefaultSnomedIdentifierService(store, idGenerationStrategy, reservationService, new SnomedIdentifierConfiguration());
		
		identifiers.generate(INT_NAMESPACE, ComponentCategory.CONCEPT, 100); // item IDs 101-199 assigned
		identifiers.release("100005"); // item ID 100 is available
		
		// The next item ID would be 200, if it weren't for the reserved range, which goes to the maximum allowed value
		assertEquals("100005", identifiers.generate(INT_NAMESPACE, ComponentCategory.CONCEPT));
	}
	
	@Test(expected=IllegalStateException.class)
	public void issue_SO_2138_testCoveringReservedRanges() throws Exception {
		final ISnomedIdentiferReservationService reservationService = new SnomedIdentifierReservationServiceImpl();
		reservationService.create("nothingAboveOneHundredNinetyNine", Reservations.range(200L, 8999_9999_9999_999L, null, ImmutableSet.of(ComponentCategory.CONCEPT)));
		reservationService.create("nothingBelowOneHundredNinetyNine", Reservations.range(100L, 198L, null, ImmutableSet.of(ComponentCategory.CONCEPT)));
		
		final ItemIdGenerationStrategy idGenerationStrategy = new SequentialItemIdGenerationStrategy(reservationService);
		final ISnomedIdentifierService identifiers = new DefaultSnomedIdentifierService(store, idGenerationStrategy, reservationService, new SnomedIdentifierConfiguration());
		
		identifiers.register("198007");
		
		assertEquals("199004", identifiers.generate(INT_NAMESPACE, ComponentCategory.CONCEPT));
		
		// This attempt should not be able to generate any other value
		identifiers.generate(INT_NAMESPACE, ComponentCategory.CONCEPT);
	}
	
	@Test
	public void testQuadraticProbing() throws Exception {
		final ISnomedIdentiferReservationService reservationService = new SnomedIdentifierReservationServiceImpl();
		final ItemIdGenerationStrategy idGenerationStrategy = new SequentialItemIdGenerationStrategy(reservationService);
		final ISnomedIdentifierService identifiers = new DefaultSnomedIdentifierService(store, idGenerationStrategy, reservationService, new SnomedIdentifierConfiguration());

		identifiers.generate(INT_NAMESPACE, ComponentCategory.CONCEPT);
		
		// Register a few existing SCTIDs that are "in the way"
		identifiers.register("101009"); // 1
		identifiers.register("104001"); // 4
		identifiers.register("109006"); // 9
		
		assertEquals("116007", identifiers.generate(INT_NAMESPACE, ComponentCategory.CONCEPT));
	}

	@Test
	public void testQuadraticProbing_skipReservedRange() throws Exception {
		final ISnomedIdentiferReservationService reservationService = new SnomedIdentifierReservationServiceImpl();
		reservationService.create("noTwoHundreds", Reservations.range(200L, 299L, "1000004", ImmutableSet.of(ComponentCategory.CONCEPT)));
		
		final ItemIdGenerationStrategy idGenerationStrategy = new SequentialItemIdGenerationStrategy(reservationService);
		final ISnomedIdentifierService identifiers = new DefaultSnomedIdentifierService(store, idGenerationStrategy, reservationService, new SnomedIdentifierConfiguration());
		
		// itemId counter is initialized with 198, next free itemId is 199
		identifiers.generate("1000004", ComponentCategory.CONCEPT, 198);
		assertEquals("1991000004108", identifiers.generate("1000004", ComponentCategory.CONCEPT));
		
		// 300 becomes registered
		identifiers.register("3001000004102");
		
		// Attempt 1, ID 1: 199 + 1 = 200 -> in reserved range, adjusted to 300, in use
		// Attempt 2, ID 1: 300 + 3 = 303 -> good
		// Attempt 1, ID 2: 303 + 1 = 304 -> good
		List<String> actualIds = ImmutableList.copyOf(identifiers.generate("1000004", ComponentCategory.CONCEPT, 2));
		List<String> expectedIds = ImmutableList.of("3031000004106", "3041000004100");
		assertEquals(expectedIds, actualIds);
	}
	
	@Test
	public void testQuadraticProbing_skipReservedRangeWithWraparound() throws Exception {
		final ISnomedIdentiferReservationService reservationService = new SnomedIdentifierReservationServiceImpl();
		reservationService.create("nothingAboveTwoHundred", Reservations.range(200L, 9999_9999L, "1000133", ImmutableSet.of(ComponentCategory.CONCEPT)));
		
		final ItemIdGenerationStrategy idGenerationStrategy = new SequentialItemIdGenerationStrategy(reservationService);
		final ISnomedIdentifierService identifiers = new DefaultSnomedIdentifierService(store, idGenerationStrategy, reservationService, new SnomedIdentifierConfiguration());

		// itemId counter is initialized with 198, next free itemId is 199
		identifiers.generate("1000133", ComponentCategory.CONCEPT, 198); // item IDs 1-198 are assigned
		assertEquals("1991000133107", identifiers.generate("1000133", ComponentCategory.CONCEPT));

		// 4 and 5 released
		identifiers.release("41000133109");
		identifiers.release("51000133106");

		// Attempt 1, ID 1: 199 + 1 = 200 -> in reserved range, adjusted to 100000000, wraps to 1, in use
		// Attempt 2, ID 1: 1   + 3   = 4 -> good
		// Attempt 1, ID 2: 4   + 1   = 5 -> good
		List<String> actualIds = ImmutableList.copyOf(identifiers.generate("1000133", ComponentCategory.CONCEPT, 2));
		List<String> expectedIds = ImmutableList.of("41000133109", "51000133106");
		assertEquals(expectedIds, actualIds);
	}
}
