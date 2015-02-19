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
import com.b2international.snowowl.core.IServiceChangeListener;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.api.index.IIndexService;
import com.b2international.snowowl.core.config.ClientPreferences;
import com.b2international.snowowl.rpc.RpcSession;
import com.b2international.snowowl.rpc.RpcUtil;

/**
 */
public abstract class BrowserConfigJob<T, I extends IIndexService<?>> extends ServiceConfigJob {

	protected BrowserConfigJob(final String name, final Object family) {
		super(name, family);
	}
	
	protected abstract Class<T> getBrowserClass();
	
	protected abstract Class<I> getIndexServiceClass();
	
	protected abstract T createBrowser(I indexService);
	
	@Override
	protected boolean initService() throws SnowowlServiceException {
		
		final ClientPreferences clientConfiguration = ApplicationContext.getInstance().getService(ClientPreferences.class);
		
		if (!clientConfiguration.isClientEmbedded()) {
			return false;
		}

		// Watch terminology index service changes, register browser whenever that happens
		ApplicationContext.getInstance().addServiceListener(getIndexServiceClass(), new IServiceChangeListener<I>() {
			@Override public void serviceChanged(final I oldService, final I newService) {
				//null can be the newService while un-registering an existing service.
				final T impl = null == newService ? null : createBrowser(newService);
				ApplicationContext.getInstance().registerService(getBrowserClass(), impl);
				
				if (null != impl) {
				
					final RpcSession session = RpcUtil.getInitialServerSession(IPluginContainer.INSTANCE);
					session.registerClassLoader(getBrowserClass(), impl.getClass().getClassLoader());
					
				}
				
			}
		});
		
		return true;
	}
}