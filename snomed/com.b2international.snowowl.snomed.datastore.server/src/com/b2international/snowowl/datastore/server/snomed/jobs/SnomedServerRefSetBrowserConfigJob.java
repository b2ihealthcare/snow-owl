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
import com.b2international.snowowl.datastore.server.snomed.SnomedDatastoreServerActivator;
import com.b2international.snowowl.datastore.server.snomed.index.SnomedServerRefSetBrowser;
import com.b2international.snowowl.datastore.serviceconfig.IndexServiceTrackingConfigJob;
import com.b2international.snowowl.rpc.RpcSession;
import com.b2international.snowowl.rpc.RpcUtil;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;

/**
 * Class for initializing and registering the SNOMED&nbsp;CT reference set browser service to the server side. 
 */
public class SnomedServerRefSetBrowserConfigJob extends IndexServiceTrackingConfigJob<SnomedRefSetBrowser, SnomedIndexService> {

	/**
	 * Creates a new job instance to to register SNOMED CT terminology browser.
	 */
	public SnomedServerRefSetBrowserConfigJob() {
		super("SNOMED CT reference set server terminology browser configuration...", SnomedDatastoreServerActivator.PLUGIN_ID);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.serviceconfig.TerminologyBrowserConfigJob#getTerminologyBrowserClass()
	 */
	@Override
	protected Class<SnomedRefSetBrowser> getTargetServiceClass() {
		return SnomedRefSetBrowser.class;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.serviceconfig.TerminologyBrowserConfigJob#getIndexServiceClass()
	 */
	@Override
	protected Class<SnomedIndexService> getIndexServiceClass() {
		return SnomedIndexService.class;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.serviceconfig.TerminologyBrowserConfigJob#createTerminologyBrowser(com.b2international.snowowl.core.api.index.IIndexService)
	 */
	@Override
	protected SnomedRefSetBrowser createServiceImplementation(SnomedIndexService indexService) {
		return new SnomedServerRefSetBrowser(indexService);
	}
	
	@Override
	protected boolean initService() throws SnowowlServiceException {
		
		final ClientPreferences clientConfiguration = ApplicationContext.getInstance().getService(ClientPreferences.class);
		
		if (!clientConfiguration.isClientEmbedded()) {
			return false;
		}

		// Watch SNOMED terminology browser changes, instead of index service changes, as in the superclass. Since SNOMED terminology browser also
		// requires the index service, it is implied that it will be registered after the index service becomes available. 
		ApplicationContext.getInstance().addServiceListener(SnomedTerminologyBrowser.class, new IServiceChangeListener<SnomedTerminologyBrowser>() {
			@Override public void serviceChanged(final SnomedTerminologyBrowser oldService, final SnomedTerminologyBrowser newService) {
				
				if (newService == null) {
					return;
				}
				
				SnomedIndexService indexService = ApplicationContext.getInstance().getService(SnomedIndexService.class);
				final SnomedRefSetBrowser impl = createServiceImplementation(indexService);
				ApplicationContext.getInstance().registerService(getTargetServiceClass(), impl);
				
				if (null != impl) {
				
					final RpcSession session = RpcUtil.getInitialServerSession(IPluginContainer.INSTANCE);
					session.registerClassLoader(getTargetServiceClass(), impl.getClass().getClassLoader());
					
				}
				
			}
		});
		
		return true;
	}
}