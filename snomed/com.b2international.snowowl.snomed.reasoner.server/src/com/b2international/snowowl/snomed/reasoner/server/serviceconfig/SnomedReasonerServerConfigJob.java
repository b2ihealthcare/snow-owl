/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.server.serviceconfig;

import org.eclipse.net4j.util.container.IPluginContainer;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.config.ClientPreferences;
import com.b2international.snowowl.datastore.serviceconfig.ServiceConfigJob;
import com.b2international.snowowl.rpc.RpcSession;
import com.b2international.snowowl.rpc.RpcUtil;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.reasoner.classification.SnomedReasonerService;
import com.b2international.snowowl.snomed.reasoner.preferences.IReasonerPreferencesService;
import com.b2international.snowowl.snomed.reasoner.server.SnomedReasonerServerActivator;
import com.b2international.snowowl.snomed.reasoner.server.classification.SnomedReasonerServerService;
import com.b2international.snowowl.snomed.reasoner.server.ontology.SnomedOntologyService;
import com.b2international.snowowl.snomed.reasoner.server.preferences.ReasonerPreferencesService;

/**
 */
public class SnomedReasonerServerConfigJob extends ServiceConfigJob {

	private static final String JOB_NAME = "SNOMED CT OWL ontology service configuration...";

	public SnomedReasonerServerConfigJob() {
		super(JOB_NAME, SnomedReasonerServerActivator.PLUGIN_ID);
	}

	@Override
	protected boolean initService() throws SnowowlServiceException {

		final ClientPreferences clientConfiguration = ApplicationContext.getInstance().getService(ClientPreferences.class);
		
		if (!clientConfiguration.isClientEmbedded()) {
			return false;
		}

		ApplicationContext.getInstance().registerService(SnomedOntologyService.class, new SnomedOntologyService());
		ApplicationContext.getInstance().registerService(IReasonerPreferencesService.class, new ReasonerPreferencesService());

		final SnomedCoreConfiguration snomedConfiguration = getSnowOwlConfiguration().getModuleConfig(SnomedCoreConfiguration.class);
		final int maximumReasonerCount = snomedConfiguration.getMaxReasonerCount();
		final int maximumTaxonomiesToKeep = snomedConfiguration.getMaxReasonerResults();
		final SnomedReasonerServerService reasonerService = new SnomedReasonerServerService(maximumReasonerCount, maximumTaxonomiesToKeep);
		ApplicationContext.getInstance().registerService(SnomedReasonerService.class, reasonerService);
		reasonerService.registerListeners();

		final RpcSession session = RpcUtil.getInitialServerSession(IPluginContainer.INSTANCE);
		session.registerClassLoader(SnomedReasonerService.class, reasonerService.getClass().getClassLoader());
		session.registerClassLoader(IReasonerPreferencesService.class, new ReasonerPreferencesService().getClass().getClassLoader());
		return true;
	}
}