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
package com.b2international.snowowl.snomed.datastore.id;

import java.io.File;
import java.nio.file.Paths;

import org.eclipse.core.runtime.IProgressMonitor;
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
import com.b2international.snowowl.snomed.datastore.config.SnomedIdentifierConfiguration.IdGenerationService;
import com.b2international.snowowl.snomed.datastore.config.SnomedIdentifierConfiguration.IdGenerationSource;
import com.b2international.snowowl.snomed.datastore.config.SnomedIdentifierConfiguration.IdGenerationStrategy;
import com.b2international.snowowl.snomed.datastore.id.cis.CisSnomedIdentifierService;
import com.b2international.snowowl.snomed.datastore.id.cis.SctId;
import com.b2international.snowowl.snomed.datastore.id.gen.ItemIdGenerationStrategy;
import com.b2international.snowowl.snomed.datastore.id.gen.SequentialItemIdGenerationStrategy;
import com.b2international.snowowl.snomed.datastore.id.memory.DefaultSnomedIdentifierService;
import com.b2international.snowowl.snomed.datastore.id.reservations.ISnomedIdentiferReservationService;
import com.b2international.snowowl.snomed.datastore.internal.id.reservations.SnomedIdentifierReservationServiceImpl;
import com.b2international.snowowl.snomed.datastore.internal.id.reservations.UniqueInStoreReservation;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Provider;

/**
 * @since 4.5
 */
public class SnomedIdentifierBootstrap extends DefaultBootstrapFragment {

	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedIdentifierBootstrap.class);

	private static final String STORE_RESERVATIONS = "store_reservations";

	@Override
	public void init(final SnowOwlConfiguration configuration, final Environment env) throws Exception {
		checkIdGenerationSource(configuration);

		final ISnomedIdentiferReservationService reservationService = new SnomedIdentifierReservationServiceImpl();
		env.services().registerService(ISnomedIdentiferReservationService.class, reservationService);

		registerTerminologyBrowser(env, reservationService);
	}

	@Override
	public void run(SnowOwlConfiguration configuration, Environment env, IProgressMonitor monitor) throws Exception {
		if (env.isServer() || env.isEmbedded()) {
			final ISnomedIdentiferReservationService reservationService = env.service(ISnomedIdentiferReservationService.class);
			final SnomedIdentifierConfiguration conf = configuration.getModuleConfig(SnomedCoreConfiguration.class).getIds();
			registerSnomedIdentifierService(conf, env, reservationService);
		}
	}

	private void registerTerminologyBrowser(final Environment env, final ISnomedIdentiferReservationService reservationService) {
		final Provider<SnomedTerminologyBrowser> provider = env.provider(SnomedTerminologyBrowser.class);
		final UniqueInStoreReservation storeReservation = new UniqueInStoreReservation(provider);
		reservationService.create(STORE_RESERVATIONS, storeReservation);
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
		
		if (conf.getIdGenerationService() == IdGenerationService.EMBEDDED) {
	
			if (conf.getIdGenerationSource() == IdGenerationSource.INDEX) {
				final IndexStore<SctId> indexStore = getIndexStore(env, mapper);
				
				switch (conf.getIdGenerationStrategy()) {
					case RANDOM:
						identifierService = new DefaultSnomedIdentifierService(indexStore, ItemIdGenerationStrategy.RANDOM, reservationService, conf);
						LOGGER.info("Snow Owl is configured to use index based identifier service with RANDOM id allocation.");
						break;
	
					case SEQUENTIAL:
						final ItemIdGenerationStrategy generationStrategy = new SequentialItemIdGenerationStrategy(reservationService);
						identifierService = new DefaultSnomedIdentifierService(indexStore, generationStrategy, reservationService, conf);
						LOGGER.info("Snow Owl is configured to use index-based identifier service with SEQUENTIAL id allocation.");
						break;
				}
			
			} else if (conf.getIdGenerationSource() == IdGenerationSource.MEMORY) {
				final MemStore<SctId> memStore = new MemStore<SctId>();

				switch (conf.getIdGenerationStrategy()) {
					case RANDOM:
						identifierService = new DefaultSnomedIdentifierService(memStore, ItemIdGenerationStrategy.RANDOM, reservationService, conf);
						LOGGER.info("Snow Owl is configured to use memory-based identifier service with RANDOM id allocation.");
						break;
					case SEQUENTIAL:
						final ItemIdGenerationStrategy generationStrategy = new SequentialItemIdGenerationStrategy(reservationService);
						identifierService = new DefaultSnomedIdentifierService(memStore, generationStrategy, reservationService, conf);
						LOGGER.info("Snow Owl is configured to use memory-based identifier service with SEQUENTIAL id allocation.");
						break;
					}
			}

		} else if (conf.getIdGenerationService() == IdGenerationService.CIS) {
			identifierService = new CisSnomedIdentifierService(conf, reservationService, mapper);
			LOGGER.info("Snow Owl is configured to use CIS based identifier service.");
		} else {
			throw new IllegalStateException(String.format("Unknown ID generation source configured: %s. ", conf.getIdGenerationStrategy()));
		}
		
		env.services().registerService(ISnomedIdentifierService.class, identifierService);

	}

	private IndexStore<SctId> getIndexStore(final Environment env, ObjectMapper mapper) {
		final File dir = env.getDataDirectory().toPath().resolve(Paths.get("indexes", "snomed", "identifiers")).toFile();
		return new IndexStore<>(dir, mapper, SctId.class);
	}

}
