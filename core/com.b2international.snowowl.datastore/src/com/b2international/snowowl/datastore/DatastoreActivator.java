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
package com.b2international.snowowl.datastore;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.config.ClientPreferences;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.serviceconfig.ServiceConfigJobManager;
import com.b2international.snowowl.datastore.serviceconfig.ServiceConfigJobManager.IServiceConfigJobChangeListener;
import com.b2international.snowowl.datastore.tasks.ITaskStateManager;
import com.b2international.snowowl.datastore.tasks.Task;
import com.b2international.snowowl.datastore.tasks.TaskManager;

/**
 * The activator class controls the plug-in life cycle
 */
public class DatastoreActivator implements BundleActivator, IServiceConfigJobChangeListener {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.b2international.terminology.datastore"; //$NON-NLS-1$

	private static BundleContext context;

	public static BundleContext getContext() {
		return context;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(final BundleContext context) throws Exception {
		DatastoreActivator.context = context;
		ServiceConfigJobManager.INSTANCE.addListener(this);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(final BundleContext context) throws Exception {
		DatastoreActivator.context = null;
		ServiceConfigJobManager.INSTANCE.removeListener(this);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.serviceconfig.ServiceConfigJobManager.IServiceConfigJobChangeListener#done(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void done(final IProgressMonitor monitor) {
		
		final IBranchPathMap userBranchPathMap = getTaskStateManager().getBranchPathMapConfiguration(getUserId(), false);
		final TaskManager taskManager = new TaskManager(new UserBranchPathMap(userBranchPathMap.asMap(getRepositoryUuids())));
		ApplicationContext.getInstance().registerService(TaskManager.class, taskManager);
		final ClientPreferences clientConfiguration = ApplicationContext.getInstance().getService(ClientPreferences.class);
		
		if (clientConfiguration.hasLastActiveTaskId()) {
			final String lastActiveTaskId = clientConfiguration.getLastActiveTaskId();
			final Task lastActiveTask = ApplicationContext.getInstance().getService(TaskManager.class).getTask(lastActiveTaskId);
			
			if (null != lastActiveTask) {
				taskManager.activateTask(lastActiveTask, monitor);
			}
		}
	}
	
	/*returns with the task state manager service*/
	private ITaskStateManager getTaskStateManager() {
		return ApplicationContext.getInstance().getService(ITaskStateManager.class);
	}
	
	/*returns with the user ID*/
	private String getUserId() {
		return getConnectionManager().getUserId();
	}

	/*returns with the connection manager*/
	private ICDOConnectionManager getConnectionManager() {
		return ApplicationContext.getInstance().getService(ICDOConnectionManager.class);
	}
	
	/*returns with the repository UUIDs*/
	private Set<String> getRepositoryUuids() {
		return getConnectionManager().uuidKeySet();
	}
	
	
}