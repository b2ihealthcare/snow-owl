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
package com.b2international.snowowl.datastore.server.version;

import org.eclipse.net4j.util.container.IPluginContainer;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.server.DatastoreServerActivator;
import com.b2international.snowowl.datastore.serviceconfig.ServiceConfigJob;
import com.b2international.snowowl.datastore.version.NodeTransformerService;
import com.b2international.snowowl.datastore.version.NodeTransformerServiceImpl;
import com.b2international.snowowl.rpc.RpcUtil;

/**
 * Job for registering the {@link NodeTransformerService} onto the server-side.
 *
 */
public class NodeTransformerServiceConfigJob extends ServiceConfigJob {

	private static final String JOB_NAME = "Node transformer service configuration...";

	public NodeTransformerServiceConfigJob() {
		super(JOB_NAME, DatastoreServerActivator.PLUGIN_ID);
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.serviceconfig.ServiceConfigJob#initService()
	 */
	@Override
	protected boolean initService() throws SnowowlServiceException {
		
		if (!isRunningInEmbeddedMode()) {
			return false;
		}
		
		final NodeTransformerServiceImpl service = new NodeTransformerServiceImpl();
		ApplicationContext.getInstance().registerService(NodeTransformerService.class, service);
		
		RpcUtil.getInitialServerSession(IPluginContainer.INSTANCE).registerClassLoader(NodeTransformerService.class, service.getClass().getClassLoader());
		
		return true;
	}

}