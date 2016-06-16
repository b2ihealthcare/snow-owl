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

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.serviceconfig.ServiceConfigJob;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedStatedClientTerminologyBrowser;

/**
 * Client-side service config job for Stated SNOMED CT terminology browser
 */
public class SnomedStatedClientTerminologyBrowserConfigJob extends ServiceConfigJob {

	private static final String NAME = "SNOMED CT stated client terminology browser configuration...";

	public SnomedStatedClientTerminologyBrowserConfigJob() {
		super(NAME, SnomedDatastoreActivator.PLUGIN_ID);
	}

	@Override
	protected boolean initService() throws SnowowlServiceException {
		final SnomedStatedClientTerminologyBrowser browser = new SnomedStatedClientTerminologyBrowser(
				getEnvironment().service(IEventBus.class),
				getEnvironment().provider(LanguageSetting.class));
		ApplicationContext.getInstance().registerService(SnomedStatedClientTerminologyBrowser.class, browser);
		return true;
	}
	
}
