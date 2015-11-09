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

import com.b2international.snowowl.datastore.server.snomed.SnomedDatastoreServerActivator;
import com.b2international.snowowl.datastore.serviceconfig.IndexServiceTrackingConfigJob;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.server.SnomedDescriptionNameProvider;
import com.b2international.snowowl.snomed.datastore.services.ISnomedDescriptionNameProvider;

/**
 * Job for initializing and configuring the SNOMED CT description name provider.
 */
public class SnomedDescriptionNameProviderServiceConfigJob extends IndexServiceTrackingConfigJob<ISnomedDescriptionNameProvider, SnomedIndexService> {

	/**
	 * Creates a new job for SNOMED CT description name provider service initialization.
	 */
	public SnomedDescriptionNameProviderServiceConfigJob() {
		super("SNOMED CT description name provider configuration...", SnomedDatastoreServerActivator.PLUGIN_ID);
	}

	@Override
	protected Class<SnomedIndexService> getIndexServiceClass() {
		return SnomedIndexService.class;
	}

	@Override
	protected Class<ISnomedDescriptionNameProvider> getTargetServiceClass() {
		return ISnomedDescriptionNameProvider.class;
	}

	@Override
	protected ISnomedDescriptionNameProvider createServiceImplementation(final SnomedIndexService indexService) {
		return new SnomedDescriptionNameProvider(indexService);
	}
}
