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
package com.b2international.snowowl.datastore.server.snomed.index;

import java.io.File;

import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.server.snomed.SnomedDatastoreServerActivator;
import com.b2international.snowowl.datastore.serviceconfig.IndexServiceConfigJob;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;

/**
 * Job for creating, initializing and registering the component index service for the SNOMED CT store.
 */
public class SnomedIndexServerServiceConfigJob extends IndexServiceConfigJob<SnomedIndexUpdater> {

	public static final String DIRECTORY_PATH = "snomed";
	
	/**
	 * Creates a new job for SNOMED CT index service initialization.
	 */
	public SnomedIndexServerServiceConfigJob() {
		super("SNOMED CT index service configuration...", SnomedDatastoreServerActivator.PLUGIN_ID);
	}

	@Override
	protected Class<SnomedIndexService> getSearcherClass() {
		return SnomedIndexService.class;
	}
	
	@Override
	protected Class<SnomedIndexUpdater> getUpdaterClass() {
		return SnomedIndexUpdater.class;
	}

	@Override
	protected SnomedIndexServerService createServiceImplementation() throws SnowowlServiceException {
		return new SnomedIndexServerService(new File(DIRECTORY_PATH), getIndexTimeout());
	}
}
