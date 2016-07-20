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

import org.eclipse.net4j.util.container.IPluginContainer;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.server.snomed.SnomedDatastoreServerActivator;
import com.b2international.snowowl.datastore.serviceconfig.ServiceConfigJob;
import com.b2international.snowowl.rpc.RpcUtil;
import com.b2international.snowowl.snomed.datastore.server.SnomedRelationshipNameProvider;
import com.b2international.snowowl.snomed.datastore.services.ISnomedRelationshipNameProvider;

/**
 * Job for initializing and configuring the SNOMED CT relationship name provider.
 */
public class SnomedRelationshipNameProviderServiceConfigJob extends ServiceConfigJob {

	/**
	 * Creates a new job for SNOMED CT relationship name provider service initialization.
	 */
	public SnomedRelationshipNameProviderServiceConfigJob() {
		super("SNOMED CT relationship name provider configuration...", SnomedDatastoreServerActivator.PLUGIN_ID);
	}

	@Override
	protected boolean initService() throws SnowowlServiceException {
		if (!isRunningInEmbeddedMode()) {
			return false;
		}

		final SnomedRelationshipNameProvider service = new SnomedRelationshipNameProvider();
		ApplicationContext.getInstance().registerService(ISnomedRelationshipNameProvider.class, service);
		RpcUtil.getInitialServerSession(IPluginContainer.INSTANCE).registerClassLoader(SnomedRelationshipNameProvider.class, service.getClass().getClassLoader());
		
		return true;
	}
	
}
