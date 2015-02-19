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
package com.b2international.snowowl.terminologyregistry.core.server;

import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.TerminologyRegistryService;
import com.b2international.snowowl.datastore.serviceconfig.AbstractServerServiceConfigJob;

/**
 * Server side configuration job for registering index support for terminology metadata.
 *
 */
public class TerminologyRegistryServerServiceConfigJob extends AbstractServerServiceConfigJob<TerminologyRegistryService> {

	public static final String DIRECTORY_PATH = "terminologyregistry";
	
	public TerminologyRegistryServerServiceConfigJob() {
		super("Terminology registry service configuration...", TerminologyRegistryServerActivator.ID);
	}

	@Override
	protected Class<TerminologyRegistryService> getServiceClass() {
		return TerminologyRegistryService.class;
	}

	@Override
	protected TerminologyRegistryService createServiceImplementation() throws SnowowlServiceException {
		return TerminologyRegistryServiceImpl.INSTANCE;
	}
}