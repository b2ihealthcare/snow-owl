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
import com.b2international.snowowl.datastore.server.snomed.SnomedTaxonomyServiceImpl;
import com.b2international.snowowl.datastore.serviceconfig.AbstractServerServiceConfigJob;
import com.b2international.snowowl.snomed.datastore.SnomedTaxonomyService;
import com.b2international.snowowl.snomed.datastore.escg.EscgRewriter;

/**
 * Job for registering the {@link SnomedTaxonomyService taxonomy service} for SNOMED&nbsp;CT onto the server-side. 
 * @see SnomedTaxonomyService
 */
public class SnomedTaxonomyServiceConfigJob extends AbstractServerServiceConfigJob<SnomedTaxonomyService> {

	public SnomedTaxonomyServiceConfigJob() {
		super("SNOMED CT taxonomy service configuration...", SnomedDatastoreServerActivator.PLUGIN_ID);
	}

	@Override
	protected Class<SnomedTaxonomyService> getServiceClass() {
		return SnomedTaxonomyService.class;
	}

	@Override
	protected SnomedTaxonomyService createServiceImplementation() throws SnowowlServiceException {
		return new SnomedTaxonomyServiceImpl(getEnvironment().service(EscgRewriter.class));
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
		final SnomedTaxonomyService taxonomyService = getServiceForClass(SnomedTaxonomyService.class);
		if (taxonomyService instanceof IPostStoreUpdateListener) {
			ApplicationContext.getInstance().addServiceListener(IPostStoreUpdateManager.class, new IServiceChangeListener<IPostStoreUpdateManager>() {
				@Override public void serviceChanged(final IPostStoreUpdateManager oldService, final IPostStoreUpdateManager newService) {
					if (null != oldService) {
						oldService.removePostStoreUpdateListener((IPostStoreUpdateListener) taxonomyService);
					}
					
					if (null != newService) {
						newService.addPostStoreUpdateListener((IPostStoreUpdateListener) taxonomyService, false);
					}
				}
			});
		}
	}
}