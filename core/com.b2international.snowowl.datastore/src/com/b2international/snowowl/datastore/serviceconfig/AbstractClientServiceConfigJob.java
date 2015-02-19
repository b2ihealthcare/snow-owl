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

import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.eclipse.net4j.util.container.IPluginContainer;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.config.ClientPreferences;
import com.b2international.snowowl.rpc.RpcProtocol;
import com.b2international.snowowl.rpc.RpcUtil;

/**
 * Job for creating, initializing and registering a service for the application on the client side.
 * 
 * 
 * @param <S> the RPC-capable interface type
 */
public abstract class AbstractClientServiceConfigJob<S> extends ServiceConfigJob {

	/**
	 * Creates a new job for initializing and configuring the index service.
	 * @param name the name of the job.
	 * @param family family object where this job belongs to. 
	 */
	protected AbstractClientServiceConfigJob(final String name, final Object family) {
		super(name, family);
	}

	protected abstract Class<S> getServiceClass();
	
	protected ClassLoader getClassLoader() {
		return getServiceClass().getClassLoader();
	}

	@Override
	@OverridingMethodsMustInvokeSuper
	protected boolean initService() throws SnowowlServiceException {

		final ClientPreferences clientConfiguration = ApplicationContext.getInstance().getService(ClientPreferences.class);
		
		// Register proxy only in remote mode
		if (clientConfiguration.isClientEmbedded()) {
			return false;
		}
		
		final Class<S> serviceClass = getServiceClass();
		final RpcProtocol protocol = RpcUtil.getRpcClientProtocol(IPluginContainer.INSTANCE);
		protocol.registerClassLoader(serviceClass, getClassLoader());
		final S indexServiceProxy = protocol.getServiceProxy(serviceClass);
		
		ApplicationContext.getInstance().registerService(serviceClass, indexServiceProxy);
		return true;
	}
}