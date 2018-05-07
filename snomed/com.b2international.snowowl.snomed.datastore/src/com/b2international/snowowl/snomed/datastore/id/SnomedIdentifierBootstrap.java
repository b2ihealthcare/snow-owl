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

import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.index.Index;
import com.b2international.index.Indexes;
import com.b2international.index.mapping.Mappings;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.setup.DefaultBootstrapFragment;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.datastore.config.IndexSettings;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.datastore.config.SnomedIdentifierConfiguration;
import com.b2international.snowowl.snomed.datastore.config.SnomedIdentifierConfiguration.IdGenerationStrategy;
import com.b2international.snowowl.snomed.datastore.id.cis.CisSnomedIdentifierService;
import com.b2international.snowowl.snomed.datastore.id.domain.SctId;
import com.b2international.snowowl.snomed.datastore.id.gen.ItemIdGenerationStrategy;
import com.b2international.snowowl.snomed.datastore.id.gen.SequentialItemIdGenerationStrategy;
import com.b2international.snowowl.snomed.datastore.id.memory.DefaultSnomedIdentifierService;
import com.b2international.snowowl.snomed.datastore.id.reservations.ISnomedIdentifierReservationService;
import com.b2international.snowowl.snomed.datastore.internal.id.reservations.SnomedIdentifierReservationServiceImpl;
import com.b2international.snowowl.snomed.datastore.internal.id.reservations.UniqueInStoreReservation;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 4.5
 */
public class SnomedIdentifierBootstrap extends DefaultBootstrapFragment {

	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedIdentifierBootstrap.class);
	private static final String SNOMED_IDS_INDEX = "snomedids";
	private static final String STORE_RESERVATIONS = "store_reservations";

	@Override
	public void init(final SnowOwlConfiguration configuration, final Environment env) throws Exception {
		checkIdGenerationSource(configuration);

		final ISnomedIdentifierReservationService reservationService = new SnomedIdentifierReservationServiceImpl();
		env.services().registerService(ISnomedIdentifierReservationService.class, reservationService);

		registerTerminologyBrowser(env, reservationService);
	}

	@Override
	public void run(SnowOwlConfiguration configuration, Environment env, IProgressMonitor monitor) throws Exception {
		if (env.isServer() || env.isEmbedded()) {
			final ISnomedIdentifierReservationService reservationService = env.service(ISnomedIdentifierReservationService.class);
			final SnomedIdentifierConfiguration conf = configuration.getModuleConfig(SnomedCoreConfiguration.class).getIds();
			registerSnomedIdentifierService(conf, env, reservationService);
		}
	}

	private void registerTerminologyBrowser(final Environment env, final ISnomedIdentifierReservationService reservationService) {
		final UniqueInStoreReservation storeReservation = new UniqueInStoreReservation(env.provider(IEventBus.class));
		reservationService.create(STORE_RESERVATIONS, storeReservation);
	}

	private void checkIdGenerationSource(final SnowOwlConfiguration configuration) {
		final SnomedIdentifierConfiguration conf = configuration.getModuleConfig(SnomedIdentifierConfiguration.class);
		final IdGenerationStrategy idGenerationSource = conf.getStrategy();

		if (null == idGenerationSource) {
			throw new IllegalStateException("ID generation source is not configured.");
		}
	}

	private void registerSnomedIdentifierService(final SnomedIdentifierConfiguration conf, final Environment env, final ISnomedIdentifierReservationService reservationService) {
		ISnomedIdentifierService identifierService = null;

		switch (conf.getStrategy()) {
		case EMBEDDED:
			final Index index = Indexes.createIndex(SNOMED_IDS_INDEX, env.service(ObjectMapper.class), new Mappings(SctId.class), env.service(IndexSettings.class));
			index.admin().create();
			final ItemIdGenerationStrategy generationStrategy = new SequentialItemIdGenerationStrategy(reservationService); 
			identifierService = new DefaultSnomedIdentifierService(index, generationStrategy, reservationService, conf);
			break;
		case CIS:
			final ObjectMapper mapper = new ObjectMapper();
			identifierService = new CisSnomedIdentifierService(conf, reservationService, mapper);
			break;
		default:
			throw new IllegalStateException(String.format("Unknown ID generation source configured: %s. ", conf.getStrategy()));
		}

		env.services().registerService(ISnomedIdentifierService.class, identifierService);
		LOGGER.info("Snow Owl is configured to use {} based identifier service.", conf.getStrategy());
	}

}
