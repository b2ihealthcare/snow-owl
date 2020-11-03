/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.cis;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.index.Index;
import com.b2international.index.Indexes;
import com.b2international.index.mapping.Mappings;
import com.b2international.snowowl.core.config.IndexSettings;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.plugin.Component;
import com.b2international.snowowl.core.setup.ConfigurationRegistry;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.setup.Plugin;
import com.b2international.snowowl.snomed.cis.SnomedIdentifierConfiguration.IdGenerationStrategy;
import com.b2international.snowowl.snomed.cis.client.CisSnomedIdentifierService;
import com.b2international.snowowl.snomed.cis.domain.SctId;
import com.b2international.snowowl.snomed.cis.gen.ItemIdGenerationStrategy;
import com.b2international.snowowl.snomed.cis.gen.SequentialItemIdGenerationStrategy;
import com.b2international.snowowl.snomed.cis.internal.reservations.IdSetReservation;
import com.b2international.snowowl.snomed.cis.internal.reservations.SnomedIdentifierReservationServiceImpl;
import com.b2international.snowowl.snomed.cis.memory.DefaultSnomedIdentifierService;
import com.b2international.snowowl.snomed.cis.reservations.ISnomedIdentifierReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;

/**
 * @since 4.5
 */
@Component
public final class SnomedIdentifierPlugin extends Plugin {

	private static final Logger LOGGER = LoggerFactory.getLogger("snomedids");
	private static final String SNOMED_IDS_INDEX = "snomedids";

	@Override
	public void addConfigurations(ConfigurationRegistry registry) {
		registry.add("cis", SnomedIdentifierConfiguration.class);
	}
	
	@Override
	public void init(final SnowOwlConfiguration configuration, final Environment env) throws Exception {
		checkIdGenerationSource(configuration);

		final ISnomedIdentifierReservationService reservationService = new SnomedIdentifierReservationServiceImpl();
		env.services().registerService(ISnomedIdentifierReservationService.class, reservationService);

		registerDefaultReservations(env, reservationService);
	}

	@Override
	public void run(SnowOwlConfiguration configuration, Environment env) throws Exception {
		if (env.isServer()) {
			final ISnomedIdentifierReservationService reservationService = env.service(ISnomedIdentifierReservationService.class);
			final SnomedIdentifierConfiguration conf = configuration.getModuleConfig(SnomedIdentifierConfiguration.class);
			registerSnomedIdentifierService(conf, env, reservationService);
		}
	}

	private void registerDefaultReservations(final Environment env, final ISnomedIdentifierReservationService reservationService) {
		final File reservationsDirectory = env.getConfigPath().resolve("reservations").toFile();
		if (reservationsDirectory.exists() && reservationsDirectory.isDirectory()) {
			for (File reservationFile : reservationsDirectory.listFiles()) {
				final String reservationFileName = reservationFile.getName();
				if (reservationFileName.endsWith(".txt")) {
					try {
						final Set<String> idsToExclude = Files.lines(reservationFile.toPath(), Charsets.UTF_8)
								.filter(SnomedIdentifiers::isValid)
								.collect(Collectors.toSet());
						
						if (idsToExclude.isEmpty()) {
							LOGGER.warn("Could not find any valid Snomed Identifier in the source file: '{}'", reservationFileName);
						} else {
							reservationService.create(com.google.common.io.Files.getNameWithoutExtension(reservationFileName), new IdSetReservation(idsToExclude));
						}
					} catch (IOException e) {
						LOGGER.error("Could not read file: '{}'", reservationFileName);
					}
				}
			}
		}
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

