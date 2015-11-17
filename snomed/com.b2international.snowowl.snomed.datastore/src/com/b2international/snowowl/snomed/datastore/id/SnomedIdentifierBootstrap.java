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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.setup.DefaultBootstrapFragment;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.setup.ModuleConfig;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration.IdGenerationStrategy;
import com.b2international.snowowl.snomed.datastore.id.cis.CisSnomedIdentifierServiceImpl;
import com.b2international.snowowl.snomed.datastore.id.gen.ItemIdGenerationStrategy;
import com.b2international.snowowl.snomed.datastore.id.memory.InMemorySnomedIdentifierServiceImpl;
import com.b2international.snowowl.snomed.datastore.id.reservations.ISnomedIdentiferReservationService;
import com.b2international.snowowl.snomed.datastore.internal.id.reservations.SnomedIdentifierReservationServiceImpl;
import com.google.inject.Provider;

/**
 * @since 4.5
 */
@ModuleConfig(fieldName = "snomed", type = SnomedCoreConfiguration.class)
public class SnomedIdentifierBootstrap extends DefaultBootstrapFragment {

	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedIdentifierBootstrap.class);
	private static final String IDENTIFIER_SERVICE_RESERVATIONS = "identifier_service_reservations";

	@Override
	public void init(final SnowOwlConfiguration configuration, final Environment env) throws Exception {
		checkIdGenerationSource(configuration);

		final SnomedCoreConfiguration coreConfiguration = configuration.getModuleConfig(SnomedCoreConfiguration.class);
		final ISnomedIdentiferReservationService reservationService = new SnomedIdentifierReservationServiceImpl();
		env.services().registerService(ISnomedIdentiferReservationService.class, reservationService);

		registerSnomedIdentifierService(coreConfiguration, env, reservationService);
	}

	private void checkIdGenerationSource(SnowOwlConfiguration configuration) {
		final SnomedCoreConfiguration coreConfiguration = configuration.getModuleConfig(SnomedCoreConfiguration.class);
		final IdGenerationStrategy idGenerationSource = coreConfiguration.getIdGenerationStrategy();

		if (null == idGenerationSource) {
			throw new IllegalStateException("ID generation source is not configured.");
		}
	}

	private void registerSnomedIdentifierService(final SnomedCoreConfiguration conf, final Environment env,
			final ISnomedIdentiferReservationService reservationService) {
		ISnomedIdentifierService identifierService = null;

		final Provider<SnomedTerminologyBrowser> provider = getTerminologyBrowserProvider();

		switch (conf.getIdGenerationStrategy()) {
		case MEMORY:
			LOGGER.info("Snow Owl is configured to use memory based identifier serivce.");
			identifierService = new InMemorySnomedIdentifierServiceImpl(ItemIdGenerationStrategy.RANDOM, provider, reservationService);
			break;
		case CIS:
			LOGGER.info("Snow Owl is configured to use CIS based identifier serivce.");
			identifierService = new CisSnomedIdentifierServiceImpl(conf, provider, reservationService);
			break;
		default:
			throw new IllegalStateException(String.format("Unknown ID generation source configured: %s. ", conf.getIdGenerationStrategy()));
		}

		reservationService.create(IDENTIFIER_SERVICE_RESERVATIONS, identifierService);
		env.services().registerService(ISnomedIdentifierService.class, identifierService);
	}

	private Provider<SnomedTerminologyBrowser> getTerminologyBrowserProvider() {
		return new Provider<SnomedTerminologyBrowser>() {
			@Override
			public SnomedTerminologyBrowser get() {
				return ApplicationContext.getInstance().getService(SnomedTerminologyBrowser.class);
			}
		};
	}

}
