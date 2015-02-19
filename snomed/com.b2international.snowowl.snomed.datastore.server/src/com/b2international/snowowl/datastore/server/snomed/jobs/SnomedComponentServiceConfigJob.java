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
import com.b2international.snowowl.core.IServiceChangeListener;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.config.ClientPreferences;
import com.b2international.snowowl.datastore.cdo.IPostStoreUpdateManager;
import com.b2international.snowowl.datastore.server.snomed.SnomedComponentService;
import com.b2international.snowowl.datastore.server.snomed.SnomedDatastoreServerActivator;
import com.b2international.snowowl.datastore.serviceconfig.ServiceConfigJob;
import com.b2international.snowowl.rpc.RpcUtil;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;

/**
 * Job for registering the SNOMED&nbsp;CT component service in the application context.
 *  
 * @see ServiceConfigJob
 * @see ISnomedComponentService
 */
public class SnomedComponentServiceConfigJob extends ServiceConfigJob {

	public SnomedComponentServiceConfigJob() {
		super("SNOMED component service configuration...", SnomedDatastoreServerActivator.PLUGIN_ID);
	}

	@Override
	protected boolean initService() throws SnowowlServiceException {
		final ClientPreferences clientConfiguration = ApplicationContext.getInstance().getService(ClientPreferences.class);
		
		if (!clientConfiguration.isClientEmbedded()) {
			return false;
		}
		
		final SnomedComponentService componentService = new SnomedComponentService();
		ApplicationContext.getInstance().registerService(ISnomedComponentService.class, componentService);

		RpcUtil.getInitialServerSession(IPluginContainer.INSTANCE).registerClassLoader(ISnomedComponentService.class, componentService.getClass().getClassLoader());
		
		ApplicationContext.getInstance().addServiceListener(IPostStoreUpdateManager.class, new IServiceChangeListener<IPostStoreUpdateManager>() {
			@Override public void serviceChanged(final IPostStoreUpdateManager oldService, final IPostStoreUpdateManager newService) {
				if (null != oldService) {
					oldService.removePostStoreUpdateListener(componentService);
				}
				
				if (null != newService) {
					newService.addPostStoreUpdateListener(componentService, false);
				}
			}
		});
		
		return true;
	}
}