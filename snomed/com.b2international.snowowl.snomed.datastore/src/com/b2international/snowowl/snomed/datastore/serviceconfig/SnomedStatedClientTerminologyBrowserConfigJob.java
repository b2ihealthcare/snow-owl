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
import com.b2international.snowowl.snomed.datastore.SnomedStatedClientTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedStatedTerminologyBrowser;

/**
 * Client-side service config job for Stated SNOMED CT terminology browser
 */
public class SnomedStatedClientTerminologyBrowserConfigJob extends ClientServiceConfigJob<SnomedStatedTerminologyBrowser, SnomedStatedClientTerminologyBrowser> {

	private static final String NAME = "SNOMED CT stated client terminology browser configuration...";

	public SnomedStatedClientTerminologyBrowserConfigJob() {
		super(NAME, SnomedDatastoreActivator.PLUGIN_ID);
	}

	@Override
	protected Class<SnomedStatedTerminologyBrowser> getServiceClass() {
		return SnomedStatedTerminologyBrowser.class;
	}

	@Override
	protected Class<SnomedStatedClientTerminologyBrowser> getTrackingClass() {
		return SnomedStatedClientTerminologyBrowser.class;
	}

	@Override
	protected SnomedStatedClientTerminologyBrowser createTrackingService(SnomedStatedTerminologyBrowser branchAwareService) {
		return new SnomedStatedClientTerminologyBrowser(branchAwareService);
	}

}
