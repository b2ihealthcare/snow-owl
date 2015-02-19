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

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.IServiceChangeListener;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.IPostStoreUpdateListener;
import com.b2international.snowowl.datastore.cdo.IPostStoreUpdateManager;
import com.b2international.snowowl.datastore.server.snomed.SnomedDatastoreServerActivator;
import com.b2international.snowowl.datastore.serviceconfig.AbstractServerServiceConfigJob;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLabelProviderService;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLabelProviderServiceImpl;

/**
 * Job for registering the {@link SnomedConceptLabelProviderService concept label provider service} for SNOMED&nbsp;CT onto the server-side. 
 */
public class SnomedConceptLabelProviderServiceConfigJob extends AbstractServerServiceConfigJob<SnomedConceptLabelProviderService> {

	public SnomedConceptLabelProviderServiceConfigJob() {
		super("SNOMED CT concept label provider service configuration...", SnomedDatastoreServerActivator.PLUGIN_ID);
	}

	@Override
	protected Class<SnomedConceptLabelProviderService> getServiceClass() {
		return SnomedConceptLabelProviderService.class;
	}

	@Override
	protected SnomedConceptLabelProviderService createServiceImplementation() throws SnowowlServiceException {
		return new SnomedConceptLabelProviderServiceImpl(); 
	}
	
	@Override
	protected boolean initService() throws SnowowlServiceException {
		final boolean success = super.initService(); //does the service registration
		
		if (success) {
			registerAsPostStoreListener();
		}
		
		return success;
	}

	private void registerAsPostStoreListener() {
		final SnomedConceptLabelProviderService labelProviderService = getServiceForClass(SnomedConceptLabelProviderService.class);
		if (labelProviderService instanceof IPostStoreUpdateListener) {
			ApplicationContext.getInstance().addServiceListener(IPostStoreUpdateManager.class, new IServiceChangeListener<IPostStoreUpdateManager>() {
				@Override public void serviceChanged(final IPostStoreUpdateManager oldService, final IPostStoreUpdateManager newService) {
					if (null != oldService) {
						oldService.removePostStoreUpdateListener((IPostStoreUpdateListener) labelProviderService);
					}
					
					if (null != newService) {
						newService.addPostStoreUpdateListener((IPostStoreUpdateListener) labelProviderService, false);
					}
				}
			});
		}
	}
}