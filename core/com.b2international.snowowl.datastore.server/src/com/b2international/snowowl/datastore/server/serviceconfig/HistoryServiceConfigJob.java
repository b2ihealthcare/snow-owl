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
package com.b2international.snowowl.datastore.server.serviceconfig;

import static com.b2international.snowowl.datastore.server.DatastoreServerActivator.PLUGIN_ID;
import static com.b2international.snowowl.rpc.RpcUtil.getInitialServerSession;
import static org.eclipse.net4j.util.container.IPluginContainer.INSTANCE;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.history.HistoryService;
import com.b2international.snowowl.datastore.server.history.HistoryServiceImpl;
import com.b2international.snowowl.datastore.serviceconfig.ServiceConfigJob;

/**
 * Job for registering the {@link HistoryService} into the server-side's 
 * {@link ApplicationContext application context}.
 *
 */
public class HistoryServiceConfigJob extends ServiceConfigJob {

	private static final String JOB_NAME = "History service configuration...";

	public HistoryServiceConfigJob() {
		super(JOB_NAME, PLUGIN_ID);
	}

	@Override
	protected boolean initService() throws SnowowlServiceException {

		if (!isRunningInEmbeddedMode()) {
			return false;
		}

		final HistoryServiceImpl service = new HistoryServiceImpl();
		ApplicationContext.getInstance().registerService(HistoryService.class, service);
		final ClassLoader classLoader = service.getClass().getClassLoader();
		getInitialServerSession(INSTANCE).registerClassLoader(HistoryService.class, classLoader);

		return true;
	}

}