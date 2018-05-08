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
package com.b2international.snowowl.snomed.datastore.id.cis;

import org.junit.Before;

import com.b2international.snowowl.snomed.datastore.config.SnomedIdentifierConfiguration;
import com.b2international.snowowl.snomed.datastore.id.AbstractIdentifierServiceTest;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.datastore.id.reservations.ISnomedIdentifierReservationService;
import com.b2international.snowowl.snomed.datastore.internal.id.reservations.SnomedIdentifierReservationServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 4.5
 */
public class CisSnomedIdentifierServiceTest extends AbstractIdentifierServiceTest {

	private ISnomedIdentifierService service;
	private final ObjectMapper mapper = new ObjectMapper();

	@Override
	protected ISnomedIdentifierService getIdentifierService() {
		return service;
	}

	@Before
	public void init() {
		final SnomedIdentifierConfiguration conf = new SnomedIdentifierConfiguration();
		conf.setCisBaseUrl("http://107.170.101.181:3000");
		conf.setCisContextRoot("api");
		conf.setCisClientSoftwareKey("Snow Owl dev. tests");
		conf.setCisUserName("snowowl-dev-b2i");
		conf.setCisPassword("hAAYLYMX5gc98SDEz9cr");
		conf.setCisTimeBetweenPollTries(1000);
		conf.setCisNumberOfPollTries(5);

		final ISnomedIdentifierReservationService reservationService = new SnomedIdentifierReservationServiceImpl();
		service = new CisSnomedIdentifierService(conf, reservationService, mapper);
	}

}
