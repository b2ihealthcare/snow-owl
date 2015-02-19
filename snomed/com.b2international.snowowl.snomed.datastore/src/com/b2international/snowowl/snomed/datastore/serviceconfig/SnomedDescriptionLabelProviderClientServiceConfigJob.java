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
package com.b2international.snowowl.snomed.datastore.serviceconfig;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.datastore.serviceconfig.ClientServiceConfigJob;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedDescriptionLabelProviderClientService;
import com.b2international.snowowl.snomed.datastore.SnomedDescriptionLabelProviderClientServiceImpl;
import com.b2international.snowowl.snomed.datastore.SnomedDescriptionLabelProviderService;

/**
 * Job for registering the {@link SnomedDescriptionLabelProviderClientService} into the {@link ApplicationContext application context}.
 * 
 */
public class SnomedDescriptionLabelProviderClientServiceConfigJob extends ClientServiceConfigJob<SnomedDescriptionLabelProviderService, SnomedDescriptionLabelProviderClientService> {

	private static final String JOB_NAME = "SNOMED CT description label provider client service configuration...";
	
	public SnomedDescriptionLabelProviderClientServiceConfigJob() {
		super(JOB_NAME, SnomedDatastoreActivator.PLUGIN_ID);
	}
	
	@Override
	protected Class<SnomedDescriptionLabelProviderService> getServiceClass() {
		return SnomedDescriptionLabelProviderService.class;
	}

	protected Class<SnomedDescriptionLabelProviderClientService> getTrackingClass() {
		return SnomedDescriptionLabelProviderClientService.class;
	}

	@Override
	protected SnomedDescriptionLabelProviderClientService createTrackingService(final SnomedDescriptionLabelProviderService branchAwareService) {
		return new SnomedDescriptionLabelProviderClientServiceImpl(branchAwareService);
	}
}