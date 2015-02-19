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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.concurrent.equinox.ForkJoinUtils;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Sets;

/**
 * Class for managing and registering all service configuration jobs available via extension points.
 * 
 * 
 * @see ServiceConfigJob
 */
public enum ServiceConfigJobManager {

	/**
	 * The manager instance.
	 */
	INSTANCE;
	
	/**
	 * Unique identifier of the service configuration job extension point. <br><br>ID: {@value}
	 */
	public static final String  SERVICE_CONFIG_JOB_EXTENSION_POINT_ID = "com.b2international.snowowl.datastore.serviceConfigJob";
	
	private static final String CLASS_ATTRIBUTE = "class";
	private static final String ID_ATTRIBUTE = "id";
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceConfigJobManager.class);
	
	private Supplier<Collection<ServiceConfigJob>> configJobSupplier = Suppliers.memoize(new ServiceConfigJobSupplier());
	private Collection<IServiceConfigJobChangeListener> listeners = Sets.newHashSet();
	
	/**
	 * Synchronously starts and registers all available service configuration jobs to the application.
	 */
	public void registerServices() {
		registerServices(null);
	}
	
	/**
	 * Synchronously starts and registers all available service configuration jobs to the application.
	 * @param monitor the monitor for the registration process. 
	 */
	public void registerServices(final IProgressMonitor monitor) {
		ForkJoinUtils.runJobsInParallel(configJobSupplier.get(), monitor);
		final IProgressMonitor convert = SubMonitor.convert(monitor, listeners.size());
		for (final IServiceConfigJobChangeListener listener : listeners) {
			listener.done(convert);
		}
	}

	/**
	 * Adds a listener. Duplicate entries are not allowed.
	 * @param listener the listener to add.
	 */
	public void addListener(final IServiceConfigJobChangeListener listener) {
		listeners.add(Preconditions.checkNotNull(listener, "Listener argument cannot be null."));
	}
	
	/**
	 * Removes a listener. Does nothing if listener is not registered.
	 * @param listener the listener to remove.
	 */
	public void removeListener(final IServiceConfigJobChangeListener listener) {
		listeners.remove(listener);
	}
	
	private class ServiceConfigJobSupplier implements Supplier<Collection<ServiceConfigJob>> {
		
		@Override 
		public Collection<ServiceConfigJob> get() {
			final Set<ServiceConfigJob> jobs = Sets.newHashSet();
			for (final IConfigurationElement element : getChangeProcessorFactoryExtensions()) {
				final ServiceConfigJob job = createConfigJobSafe(element);
				if (null != job) jobs.add(job);
			}
			return jobs;
		}

		/*returns with the configuration elements of the service configuration job extension point*/
		private IConfigurationElement[] getChangeProcessorFactoryExtensions() {
			return Platform.getExtensionRegistry().getConfigurationElementsFor(SERVICE_CONFIG_JOB_EXTENSION_POINT_ID);
		}

		/*creates the executable service configuration job from the specified configuration element. returns with null if error occurred.*/
		private ServiceConfigJob createConfigJobSafe(final IConfigurationElement element) {
			checkNotNull(element, "Configuration element argument should not be null.");
			try {
				final Object executableExtension = element.createExecutableExtension(CLASS_ATTRIBUTE);
				if (executableExtension instanceof ServiceConfigJob) {
					return (ServiceConfigJob) executableExtension;
				} else {
					throw new Exception("Executable extension should be a service configuration job but was: " + executableExtension.getClass());
				}
			} catch (final Exception e) {
				final String id = element.getAttribute(ID_ATTRIBUTE);
				LOGGER.error("Error while creating executable service configuration job with ID: '" + String.valueOf(id) + "'.", e);
				return null;
			}
		}
	}
	
	/**
	 * Clients may be get notification if service registration finished 
	 * after performing {@link ServiceConfigJobManager#registerServices()}.
	 */
	public static interface IServiceConfigJobChangeListener {
		
		/**Notify clients about successful service registration.
		 * @param monitor */
		void done(final IProgressMonitor monitor);
		
	}
}