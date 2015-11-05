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

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.SimpleFamilyJob;
import com.b2international.snowowl.core.SnowOwlApplication;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.config.ClientPreferences;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.setup.Environment;
import com.google.common.base.Stopwatch;

/**
 * Job for creating, initializing and registering an application service.
 * 
 */
public abstract class ServiceConfigJob extends SimpleFamilyJob {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceConfigJob.class);

	/**
	 * Creates a new job for initializing and configuring some service.
	 * @param name the name of the job.
	 * @param family family object where this job belongs to. 
	 */
	protected ServiceConfigJob(final String name, final Object family) {
		super(name, family);
		setUser(false);
		setPriority(Job.LONG);
	}
	
	/**
	 * Initialize the service and returns with the configured service instance.
	 * @return {@code true} if service initialization succeeded, {@code false} otherwise
	 * @throws SnowowlServiceException error occurred during the service initialization.
	 */
	protected abstract boolean initService() throws SnowowlServiceException;
	
	@Override
	protected final IStatus run(final IProgressMonitor monitor) {
		final Stopwatch serviceStopwatch = Stopwatch.createStarted();
		LOGGER.debug(">>> " + getName());
		monitor.beginTask(getName(), IProgressMonitor.UNKNOWN);
		
		try {
			return initService() ? Status.OK_STATUS : Status.CANCEL_STATUS;
		} catch (final SnowowlServiceException e) {
			// TODO (apeteri): consider returning error status here
			throw new SnowowlRuntimeException("Error when initializing service.", e);
		} finally {
			monitor.done();
			LOGGER.debug(MessageFormat.format("<<< {0} [{1}]", getName(), serviceStopwatch));
		}
	}
	
	/**Returns {@code true} if the application is running in embedded mode, otherwise returns with {@code false}.*/
	protected boolean isRunningInEmbeddedMode() {
		return ApplicationContext.getInstance().getService(ClientPreferences.class).isClientEmbedded();
	}
	
	/**
	 * Returns the currently loaded {@link SnowOwlConfiguration} instance from the {@link SnowOwlApplication}.
	 * @return the global application configuration instance
	 */
	protected final SnowOwlConfiguration getSnowOwlConfiguration() {
		return SnowOwlApplication.INSTANCE.getConfiguration();
	}
	
	protected final Environment getEnvironment() {
		return SnowOwlApplication.INSTANCE.getEnviroment();
	}
	
}