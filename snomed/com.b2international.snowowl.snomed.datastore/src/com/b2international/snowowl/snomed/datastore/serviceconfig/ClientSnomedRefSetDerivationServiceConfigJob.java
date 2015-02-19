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

import com.b2international.snowowl.datastore.serviceconfig.ClientServiceConfigJob;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.services.ClientSnomedRefSetDerivationService;
import com.b2international.snowowl.snomed.datastore.services.IClientSnomedRefSetDerivationService;
import com.b2international.snowowl.snomed.datastore.services.ISnomedRefSetDerivationService;

/**
 * Configuration job to initialize and register the SNOMED&nbsp;CT reference set derivation service on the client side.
 * 
 * @since Snow&nbsp;Owl 3.0.1
 */
public class ClientSnomedRefSetDerivationServiceConfigJob extends ClientServiceConfigJob<ISnomedRefSetDerivationService, IClientSnomedRefSetDerivationService>{

	private static final String JOB_NAME = "SNOMED reference set derivator client service configuration...";
	
	public ClientSnomedRefSetDerivationServiceConfigJob() {
		super(JOB_NAME, SnomedDatastoreActivator.PLUGIN_ID);
	}

	@Override
	protected Class<IClientSnomedRefSetDerivationService> getTrackingClass() {
		return IClientSnomedRefSetDerivationService.class;
	}

	@Override
	protected IClientSnomedRefSetDerivationService createTrackingService(final ISnomedRefSetDerivationService branchAwareService) {
		return new ClientSnomedRefSetDerivationService(branchAwareService);
	}

	@Override
	protected Class<ISnomedRefSetDerivationService> getServiceClass() {
		return ISnomedRefSetDerivationService.class;
	}

}