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
package com.b2international.snowowl.datastore.server.quicksearch;

import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.quicksearch.IQuickSearchService;
import com.b2international.snowowl.datastore.server.DatastoreServerActivator;
import com.b2international.snowowl.datastore.serviceconfig.AbstractServerServiceConfigJob;

/**
 * @since 3.3
 */
public class QuickSearchServerServiceConfigJob extends AbstractServerServiceConfigJob<IQuickSearchService> {

	private static final String JOB_NAME = "Quick search server service configuration...";

	public QuickSearchServerServiceConfigJob() {
		super(JOB_NAME, DatastoreServerActivator.PLUGIN_ID);
	}

	@Override
	protected Class<IQuickSearchService> getServiceClass() {
		return IQuickSearchService.class;
	}

	@Override
	protected IQuickSearchService createServiceImplementation() throws SnowowlServiceException {
		return new QuickSearchServerService();
	}
	
}