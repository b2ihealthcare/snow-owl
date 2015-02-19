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
package com.b2international.snowowl.terminologyregistry.core.serviceconfig;

import com.b2international.snowowl.datastore.TerminologyRegistryService;
import com.b2international.snowowl.datastore.serviceconfig.ClientServiceConfigJob;
import com.b2international.snowowl.terminologyregistry.core.TerminologyRegistryCoreActivator;
import com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryClientService;

/**
 * Configuration job to initialize and register the service for the terminology registry on the client side.
 */
public class TerminologyRegistryIndexServiceConfigJob extends ClientServiceConfigJob<TerminologyRegistryService, TerminologyRegistryClientService> {

	private static final String JOB_NAME = "Terminology registry service configuration...";
	
	public TerminologyRegistryIndexServiceConfigJob() {
		super(JOB_NAME, TerminologyRegistryCoreActivator.PLUGIN_ID);
	}

	@Override
	protected Class<TerminologyRegistryClientService> getTrackingClass() {
		return TerminologyRegistryClientService.class;
	}

	@Override
	protected TerminologyRegistryClientService createTrackingService(final TerminologyRegistryService terminologyRegistryService) {
		return new TerminologyRegistryClientService(terminologyRegistryService);
	}

	@Override
	protected Class<TerminologyRegistryService> getServiceClass() {
		return TerminologyRegistryService.class;
	}


}