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
import com.b2international.snowowl.datastore.server.snomed.index.SnomedStatedServerTerminologyBrowser;
import com.b2international.snowowl.datastore.serviceconfig.IndexServiceTrackingConfigJob;
import com.b2international.snowowl.snomed.datastore.SnomedStatedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;

/**
 * Server-side service config job for Stated SNOMED CT terminology browser
 */
public class SnomedStatedServerTerminologyBrowserConfigJob extends IndexServiceTrackingConfigJob<SnomedStatedTerminologyBrowser, SnomedIndexService> {

	private static final String NAME = "SNOMED CT stated server terminology browser configuration...";

	public SnomedStatedServerTerminologyBrowserConfigJob() {
		super(NAME, SnomedDatastoreServerActivator.PLUGIN_ID);
	}

	@Override
	protected Class<SnomedStatedTerminologyBrowser> getTargetServiceClass() {
		return SnomedStatedTerminologyBrowser.class;
	}

	@Override
	protected Class<SnomedIndexService> getIndexServiceClass() {
		return SnomedIndexService.class;
	}

	@Override
	protected SnomedStatedTerminologyBrowser createServiceImplementation(SnomedIndexService indexService) {
		return new SnomedStatedServerTerminologyBrowser(indexService);
	}

}
