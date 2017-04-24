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

import java.util.UUID;

import org.junit.After;
import org.junit.Before;

import com.b2international.index.Index;
import com.b2international.index.Indexes;
import com.b2international.index.mapping.Mappings;
import com.b2international.snowowl.snomed.datastore.config.SnomedIdentifierConfiguration;
import com.b2international.snowowl.snomed.datastore.id.AbstractIdentifierServiceTest;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.datastore.id.domain.SctId;
import com.b2international.snowowl.snomed.datastore.id.gen.ItemIdGenerationStrategy;
import com.b2international.snowowl.snomed.datastore.id.gen.SequentialItemIdGenerationStrategy;
import com.b2international.snowowl.snomed.datastore.id.reservations.ISnomedIdentiferReservationService;
import com.b2international.snowowl.snomed.datastore.internal.id.reservations.SnomedIdentifierReservationServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 4.5
 */
public class DefaultSnomedIdentifierServiceTest extends AbstractIdentifierServiceTest {

	private ISnomedIdentifierService service;
	private Index store;

	@Override
	protected ISnomedIdentifierService getIdentifierService() {
		return service;
	}

	@Before
	public void init() {
		store = Indexes.createIndex(UUID.randomUUID().toString(), new ObjectMapper(), new Mappings(SctId.class));
		store.admin().create();
		
		final ISnomedIdentiferReservationService reservationService = new SnomedIdentifierReservationServiceImpl();
		final ItemIdGenerationStrategy idGenerationStrategy = new SequentialItemIdGenerationStrategy(store, reservationService);
		
		service = new DefaultSnomedIdentifierService(store, idGenerationStrategy, reservationService, new SnomedIdentifierConfiguration());
	}
	
	@After
	public void after() {
		store.admin().delete();
	}

}
