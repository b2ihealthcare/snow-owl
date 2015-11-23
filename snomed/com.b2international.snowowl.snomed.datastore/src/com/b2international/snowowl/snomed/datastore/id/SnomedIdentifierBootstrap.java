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
package com.b2international.snowowl.snomed.datastore.id;

import java.io.File;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.setup.DefaultBootstrapFragment;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.datastore.store.IndexStore;
import com.b2international.snowowl.datastore.store.MemStore;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.datastore.config.SnomedIdentifierConfiguration;
import com.b2international.snowowl.snomed.datastore.config.SnomedIdentifierConfiguration.IdGenerationStrategy;
import com.b2international.snowowl.snomed.datastore.id.cis.CisSnomedIdentifierServiceImpl;
import com.b2international.snowowl.snomed.datastore.id.cis.SctId;
import com.b2international.snowowl.snomed.datastore.id.gen.ItemIdGenerationStrategy;
import com.b2international.snowowl.snomed.datastore.id.memory.DefaultSnomedIdentifierServiceImpl;
import com.b2international.snowowl.snomed.datastore.id.reservations.ISnomedIdentiferReservationService;
import com.b2international.snowowl.snomed.datastore.internal.id.reservations.SnomedIdentifierReservationServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Provider;

/**
 * @since 4.5
 */
public class SnomedIdentifierBootstrap extends DefaultBootstrapFragment {

	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedIdentifierBootstrap.class);
	private static final String IDENTIFIER_SERVICE_RESERVATIONS = "identifier_service_reservations";

	@Override
	public void init(final SnowOwlConfiguration configuration, final Environment env) throws Exception {
		checkIdGenerationSource(configuration);

		final SnomedIdentifierConfiguration conf = configuration.getModuleConfig(SnomedCoreConfiguration.class).getIds();
		final ISnomedIdentiferReservationService reservationService = new SnomedIdentifierReservationServiceImpl();
		env.services().registerService(ISnomedIdentiferReservationService.class, reservationService);

		registerSnomedIdentifierService(conf, env, reservationService);
	}

	private void checkIdGenerationSource(final SnowOwlConfiguration configuration) {
		final SnomedIdentifierConfiguration conf = configuration.getModuleConfig(SnomedIdentifierConfiguration.class);
		final IdGenerationStrategy idGenerationSource = conf.getIdGenerationStrategy();

		if (null == idGenerationSource) {
			throw new IllegalStateException("ID generation source is not configured.");
		}
	}

	private void registerSnomedIdentifierService(final SnomedIdentifierConfiguration conf, final Environment env,
			final ISnomedIdentiferReservationService reservationService) {
		ISnomedIdentifierService identifierService = null;

		final ObjectMapper mapper = new ObjectMapper();
		final Provider<SnomedTerminologyBrowser> provider = env.provider(SnomedTerminologyBrowser.class);

		switch (conf.getIdGenerationStrategy()) {
		case MEMORY:
			LOGGER.info("Snow Owl is configured to use memory based identifier serivce.");
			final MemStore<SctId> memStore = new MemStore<SctId>();
			identifierService = new DefaultSnomedIdentifierServiceImpl(memStore, ItemIdGenerationStrategy.RANDOM, provider,
					reservationService);
			break;
		case INDEX:
			LOGGER.info("Snow Owl is configured to use index based identifier serivce.");
			final IndexStore<SctId> indexStore = getIndexStore(env);
			identifierService = new DefaultSnomedIdentifierServiceImpl(indexStore, ItemIdGenerationStrategy.RANDOM, provider,
					reservationService);
			break;
		case CIS:
			LOGGER.info("Snow Owl is configured to use CIS based identifier serivce.");
			identifierService = new CisSnomedIdentifierServiceImpl(conf, provider, reservationService, mapper);
			break;
		default:
			throw new IllegalStateException(String.format("Unknown ID generation source configured: %s. ", conf.getIdGenerationStrategy()));
		}

		reservationService.create(IDENTIFIER_SERVICE_RESERVATIONS, identifierService);
		env.services().registerService(ISnomedIdentifierService.class, identifierService);
	}

	private IndexStore<SctId> getIndexStore(final Environment env) {
		final File dir = env.getDataDirectory()
				.toPath()
				.resolve(Paths.get("indexes", "snomed", "identifiers"))
				.toFile();
		return new IndexStore<>(dir, new ObjectMapper(), SctId.class);
	}

}
