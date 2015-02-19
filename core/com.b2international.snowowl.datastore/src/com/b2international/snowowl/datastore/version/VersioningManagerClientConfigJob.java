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
package com.b2international.snowowl.datastore.version;

import org.eclipse.net4j.util.container.IPluginContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.Pair;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.DatastoreActivator;
import com.b2international.snowowl.datastore.serviceconfig.ServiceConfigJob;
import com.b2international.snowowl.rpc.RpcProtocol;
import com.b2international.snowowl.rpc.RpcUtil;

/**
 * Configuration job for registering client-side {@link IVersioningManager} instances to the client side.
 *
 */
public class VersioningManagerClientConfigJob extends ServiceConfigJob {

	private static final String JOB_NAME = "Versioning manager client service configuration...";
	private static final Logger LOGGER = LoggerFactory.getLogger(VersioningManagerClientConfigJob.class);
	
	/**Sole constructor.*/
	public VersioningManagerClientConfigJob() {
		super(JOB_NAME, DatastoreActivator.PLUGIN_ID);
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.serviceconfig.ServiceConfigJob#initService()
	 */
	@Override
	protected boolean initService() throws SnowowlServiceException {
		
		if (isRunningInEmbeddedMode()) {
			return false;
		}

		for (final Pair<Class<IVersioningManager>, Class<IVersioningManager>> pair : VersioningManagerBroker.INSTANCE.getVersioningManagerClasses()) {
		
			try {
				
				final Class<IVersioningManager> managerInterface = pair.getA();
				final Class<IVersioningManager> managerClass = pair.getB();
				
				final IVersioningManager manager = managerClass.newInstance();
				
				final RpcProtocol protocol = RpcUtil.getRpcClientProtocol(IPluginContainer.INSTANCE);
				protocol.registerClassLoader(managerInterface, manager.getClass().getClassLoader());
				final IVersioningManager serviceProxy = protocol.getServiceProxy(managerInterface);
				
				ApplicationContext.getInstance().registerService(managerInterface, serviceProxy);

				LOGGER.info("Successfully registered " + managerClass.getSimpleName() + ".");
				
			} catch (final InstantiationException e) {
				LOGGER.error("Error while instantiating versioning manager.", e);
				throw new SnowowlRuntimeException(e);
			} catch (final IllegalAccessException e) {
				LOGGER.error("No args constructor is not available.", e);
				throw new SnowowlRuntimeException(e);
			}
			
		}
		
		return true;
	}

}