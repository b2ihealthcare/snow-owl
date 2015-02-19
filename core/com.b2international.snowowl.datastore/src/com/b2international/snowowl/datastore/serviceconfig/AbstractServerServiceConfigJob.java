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
package com.b2international.snowowl.datastore.serviceconfig;

import org.eclipse.net4j.util.container.IPluginContainer;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.config.ClientPreferences;
import com.b2international.snowowl.rpc.RpcSession;
import com.b2international.snowowl.rpc.RpcUtil;

/**
 * 
 *
 * @param <T>
 */
public abstract class AbstractServerServiceConfigJob<T> extends ServiceConfigJob {

	protected AbstractServerServiceConfigJob(String name, Object family) {
		super(name, family);
	}

	protected abstract Class<T> getServiceClass();
	
	protected abstract T createServiceImplementation() throws SnowowlServiceException;

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.serviceconfig.ServiceConfigJob#initService()
	 */
	@Override
	protected boolean initService() throws SnowowlServiceException {

		final ClientPreferences clientConfiguration = ApplicationContext.getInstance().getService(ClientPreferences.class);
		
		if (!clientConfiguration.isClientEmbedded()) {
			return false;
		}
		
		final Class<T> serviceClass = getServiceClass();
		final T implementation = createServiceImplementation();
		ApplicationContext.getInstance().registerService(serviceClass, implementation);
		
		final RpcSession session = RpcUtil.getInitialServerSession(IPluginContainer.INSTANCE);
		session.registerClassLoader(serviceClass, implementation.getClass().getClassLoader());
		
		return true;
	}
}