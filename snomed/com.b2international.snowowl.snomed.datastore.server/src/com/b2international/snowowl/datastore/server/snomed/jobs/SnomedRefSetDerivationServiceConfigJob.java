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
package com.b2international.snowowl.datastore.server.snomed.jobs;

import org.eclipse.net4j.util.container.IPluginContainer;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.config.ClientPreferences;
import com.b2international.snowowl.datastore.server.snomed.SnomedDatastoreServerActivator;
import com.b2international.snowowl.datastore.server.snomed.SnomedRefSetDerivationService;
import com.b2international.snowowl.datastore.serviceconfig.ServiceConfigJob;
import com.b2international.snowowl.rpc.RpcUtil;
import com.b2international.snowowl.snomed.datastore.services.ISnomedRefSetDerivationService;

/**
 * Job for registering the SNOMED&nbsp;CT reference set derivation service in the application context.
 * 
 * @since Snow&nbsp;Owl 3.0.1
 */
public class SnomedRefSetDerivationServiceConfigJob extends ServiceConfigJob {
	
	private static final String JOB_NAME = "SNOMED reference set derivation service configuration...";

	public SnomedRefSetDerivationServiceConfigJob() {
		super(JOB_NAME, SnomedDatastoreServerActivator.PLUGIN_ID);
	}

	@Override
	protected boolean initService() throws SnowowlServiceException {
		
		final ClientPreferences configuration = ApplicationContext.getInstance().getService(ClientPreferences.class);
		
		if (!configuration.isClientEmbedded()) {
			return false;
		}
		
		final SnomedRefSetDerivationService derivationService = new SnomedRefSetDerivationService();
		ApplicationContext.getInstance().registerService(ISnomedRefSetDerivationService.class, derivationService);
		
		RpcUtil.getInitialServerSession(IPluginContainer.INSTANCE).registerClassLoader(ISnomedRefSetDerivationService.class, derivationService.getClass().getClassLoader());
		
		return true;
	}

}