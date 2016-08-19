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
import com.b2international.snowowl.datastore.server.snomed.index.SnomedServerTerminologyBrowser;
import com.b2international.snowowl.datastore.serviceconfig.IndexServiceTrackingConfigJob;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;

/**
 * Class for initializing and registering the SNOMED&nbsp;CT terminology browser service to the server side. 
 */
public class SnomedServerTerminologyBrowserConfigJob extends IndexServiceTrackingConfigJob<SnomedTerminologyBrowser, SnomedIndexService> {

	/**
	 * Creates a new job instance to register SNOMED&nbsp;CT terminology browser.
	 */
	public SnomedServerTerminologyBrowserConfigJob() {
		super("SNOMED CT server terminology browser configuration...", SnomedDatastoreServerActivator.PLUGIN_ID);
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.serviceconfig.TerminologyBrowserConfigJob#getTerminologyBrowserClass()
	 */
	@Override
	protected Class<SnomedTerminologyBrowser> getTargetServiceClass() {
		return SnomedTerminologyBrowser.class;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.serviceconfig.TerminologyBrowserConfigJob#getIndexServiceClass()
	 */
	@Override
	protected Class<SnomedIndexService> getIndexServiceClass() {
		return SnomedIndexService.class;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.serviceconfig.TerminologyBrowserConfigJob#createTerminologyBrowser(com.b2international.snowowl.core.api.index.IIndexService)
	 */
	@Override
	protected SnomedServerTerminologyBrowser createServiceImplementation(final SnomedIndexService indexService) {
		return new SnomedServerTerminologyBrowser(indexService);
	}

}