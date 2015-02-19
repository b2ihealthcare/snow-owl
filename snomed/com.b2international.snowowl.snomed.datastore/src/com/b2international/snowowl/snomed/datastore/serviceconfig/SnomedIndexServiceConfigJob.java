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
import com.b2international.snowowl.snomed.datastore.index.SnomedClientIndexService;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;

/**
 * Job for creating, initializing and registering some Lucene specific service for the SNOMED CT core.
 * <p>
 * <b>Note: </b>this class belongs to the {@link SnomedDatastoreActivator#PLUGIN_ID SNOMED CT data store plug-in identifier} job family.
 * 
 */
public class SnomedIndexServiceConfigJob extends ClientServiceConfigJob<SnomedIndexService, SnomedClientIndexService> {

	private static final String JOB_NAME = "SNOMED CT Lucene service configuration...";
	
	/**
	 * Creates a new job for initializing and configuring the Lucene specific service 
	 * for <b>SNOMED CT</b> concepts and descriptions.
	 */
	public SnomedIndexServiceConfigJob() {
		super(JOB_NAME, SnomedDatastoreActivator.PLUGIN_ID);
	}

	@Override
	protected Class<SnomedIndexService> getServiceClass() {
		return SnomedIndexService.class;
	}
	
	@Override
	protected Class<SnomedClientIndexService> getTrackingClass() {
		return SnomedClientIndexService.class;
	}
	
	@Override
	protected SnomedClientIndexService createTrackingService(final SnomedIndexService wrappedService) {
		return new SnomedClientIndexService(wrappedService);
	}
}