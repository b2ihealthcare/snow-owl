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

import static com.b2international.snowowl.datastore.BranchPathUtils.createMainPath;

import org.eclipse.net4j.util.container.IPluginContainer;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.api.index.IIndexUpdater;
import com.b2international.snowowl.core.config.ClientPreferences;
import com.b2international.snowowl.rpc.RpcSession;
import com.b2international.snowowl.rpc.RpcUtil;

/**
 * Job for creating, initializing and registering an index service to the application on the server side.
 * 
 * @param <U> the index updater interface type
 */
public abstract class IndexServiceConfigJob<U extends IIndexUpdater<?>> extends ServiceConfigJob {

	/**
	 * Creates a new job for initializing and configuring the index service.
	 * @param name the name of the job.
	 * @param family family object where this job belongs to. 
	 */
	protected IndexServiceConfigJob(final String name, final Object family) {
		super(name, family);
	}

	protected abstract U createServiceImplementation() throws SnowowlServiceException;
	
	protected abstract Class<? super U> getSearcherClass();

	protected abstract Class<U> getUpdaterClass();
	
	@Override
	protected final boolean initService() throws SnowowlServiceException {
		
		final ClientPreferences clientConfiguration = ApplicationContext.getInstance().getService(ClientPreferences.class);
		
		if (!clientConfiguration.isClientEmbedded()) {
			return false;
		}
		
		// Register implementation for both reading and writing
		final U implementation = createServiceImplementation();
		implementation.prepare(createMainPath());
		final Class<? super U> searcherClass = getSearcherClass();
		final Class<U> updaterClass = getUpdaterClass();
		
		ApplicationContext.getInstance().registerService(searcherClass, implementation);
		ApplicationContext.getInstance().registerService(updaterClass, implementation);
		
		// Only register the searcher, as this is what can be called remotely
		final RpcSession session = RpcUtil.getInitialServerSession(IPluginContainer.INSTANCE);
		session.registerClassLoader(searcherClass, implementation.getClass().getClassLoader());
		
		return true;
	}
}