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
package com.b2international.snowowl.snomed.datastore.internal.boot;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.setup.DefaultBootstrapFragment;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.setup.ModuleConfig;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.datastore.id.reservations.ISnomedIdentiferReservationService;
import com.b2international.snowowl.snomed.datastore.id.reservations.Reservations;
import com.b2international.snowowl.snomed.datastore.internal.id.SnomedIdentifierServiceImpl;
import com.b2international.snowowl.snomed.datastore.internal.id.reservations.SnomedIdentifierReservationServiceImpl;
import com.google.inject.Provider;

/**
 * @since 3.4
 */
@ModuleConfig(fieldName = "snomed", type = SnomedCoreConfiguration.class)
public class SnomedCoreBootstrap extends DefaultBootstrapFragment {

	private static final String STORE_RESERVATIONS = "internal_store_reservations";

	@Override
	public void init(SnowOwlConfiguration configuration, Environment env) throws Exception {
		final Provider<SnomedTerminologyBrowser> browser = env.provider(SnomedTerminologyBrowser.class);
		final ISnomedIdentiferReservationService reservationService = new SnomedIdentifierReservationServiceImpl();
		reservationService.create(STORE_RESERVATIONS, Reservations.uniqueInStore(browser));
		final ISnomedIdentifierService idService = new SnomedIdentifierServiceImpl(reservationService);
		env.services().registerService(ISnomedIdentiferReservationService.class, reservationService);
		env.services().registerService(ISnomedIdentifierService.class, idService);
	}

	@Override
	public void run(SnowOwlConfiguration configuration, Environment env, IProgressMonitor monitor) throws Exception {
		// TODO figure out how to properly register Handler to specific endpoints in core services,
		// It would be nice to use a framework like reactor
		// Also if we stick with the current IEventBus impl, we should definitely implement
		env.service(IEventBus.class).registerHandler("/snomed-ct/ids", new SnomedIdentifierServiceEventHandler(env.provider(ISnomedIdentifierService.class)));
	}

}