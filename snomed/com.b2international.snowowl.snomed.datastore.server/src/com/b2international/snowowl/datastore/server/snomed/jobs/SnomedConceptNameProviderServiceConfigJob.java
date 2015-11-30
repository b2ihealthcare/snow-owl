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

import java.util.List;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.datastore.server.snomed.SnomedDatastoreServerActivator;
import com.b2international.snowowl.datastore.serviceconfig.IndexServiceTrackingConfigJob;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.server.SnomedConceptNameProvider;
import com.b2international.snowowl.snomed.datastore.services.ISnomedConceptNameProvider;
import com.google.common.collect.ImmutableList;

/**
 * Job for initializing and configuring the SNOMED CT concept name provider.
 */
public class SnomedConceptNameProviderServiceConfigJob extends IndexServiceTrackingConfigJob<ISnomedConceptNameProvider, SnomedIndexService> {

	/**
	 * Creates a new job for SNOMED CT concept name provider service initialization.
	 */
	public SnomedConceptNameProviderServiceConfigJob() {
		super("SNOMED CT concept name provider configuration...", SnomedDatastoreServerActivator.PLUGIN_ID);
	}

	@Override
	protected Class<SnomedIndexService> getIndexServiceClass() {
		return SnomedIndexService.class;
	}

	@Override
	protected Class<ISnomedConceptNameProvider> getTargetServiceClass() {
		return ISnomedConceptNameProvider.class;
	}

	@Override
	protected ISnomedConceptNameProvider createServiceImplementation(final SnomedIndexService indexService) {
		return new SnomedConceptNameProvider(indexService, getEnvironment().provider(IEventBus.class), getExtendedLocales());
	}

	private List<ExtendedLocale> getExtendedLocales() {
		SnomedCoreConfiguration coreConfiguration = getSnowOwlConfiguration().getModuleConfig(SnomedCoreConfiguration.class);
		return ImmutableList.of(ExtendedLocale.valueOf(coreConfiguration.getLanguage()));
	}
}
