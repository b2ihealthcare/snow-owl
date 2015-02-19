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
package com.b2international.snowowl.snomed.datastore.validation;

import com.b2international.snowowl.datastore.DatastoreActivator;
import com.b2international.snowowl.datastore.serviceconfig.ClientServiceConfigJob;

/**
 *
 */
public class SnomedComponentValidationClientServiceConfigJob extends ClientServiceConfigJob<ISnomedComponentValidationService, IClientSnomedComponentValidationService> {

	private static final String JOB_NAME = "SNOMED CT validation client service configuration...";
	
	public SnomedComponentValidationClientServiceConfigJob() {
		super(JOB_NAME, DatastoreActivator.PLUGIN_ID);
	}

	@Override
	protected Class<IClientSnomedComponentValidationService> getTrackingClass() {
		return IClientSnomedComponentValidationService.class;
	}

	@Override
	protected IClientSnomedComponentValidationService createTrackingService(ISnomedComponentValidationService branchAwareService) {
		return new SnomedComponentValidationClientService(branchAwareService);
	}

	@Override
	protected Class<ISnomedComponentValidationService> getServiceClass() {
		return ISnomedComponentValidationService.class;
	}
}