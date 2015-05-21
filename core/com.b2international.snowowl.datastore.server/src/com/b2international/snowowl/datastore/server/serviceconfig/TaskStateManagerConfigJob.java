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

import java.io.File;

import org.eclipse.net4j.util.container.IPluginContainer;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.config.ClientPreferences;
import com.b2international.snowowl.datastore.server.DatastoreServerActivator;
import com.b2international.snowowl.datastore.server.tasks.TaskStateManager;
import com.b2international.snowowl.datastore.serviceconfig.ServiceConfigJob;
import com.b2international.snowowl.datastore.tasks.ITaskStateManager;
import com.b2international.snowowl.rpc.RpcUtil;

/**
 *
 */
public class TaskStateManagerConfigJob extends ServiceConfigJob {

	private static final String JOB_NAME = "Task state management service configuration...";

	public TaskStateManagerConfigJob() {
		super(JOB_NAME, DatastoreServerActivator.PLUGIN_ID);
	}

	@Override
	protected boolean initService() throws SnowowlServiceException {

		final ClientPreferences clientConfiguration = ApplicationContext.getInstance().getService(ClientPreferences.class);
		
		if (!clientConfiguration.isClientEmbedded()) {
			return false;
		}

		final File dir = new File(new File(getEnvironment().getDataDirectory(), "indexes"), "tasks");
		TaskStateManager service = new TaskStateManager(dir);
		ApplicationContext.getInstance().registerService(ITaskStateManager.class, service);

		RpcUtil.getInitialServerSession(IPluginContainer.INSTANCE).registerClassLoader(ITaskStateManager.class, service.getClass().getClassLoader());

		return true;
	}
}