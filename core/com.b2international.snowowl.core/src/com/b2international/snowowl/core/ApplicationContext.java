/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core;

import static com.google.common.collect.Sets.newHashSet;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.status.Statuses;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class ApplicationContext {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContext.class);

	public static class ServiceRegistryEntry<S> implements Comparable<ServiceRegistryEntry<S>> {
		private Class<S> serviceInterface;
		private S implementation;
		private final Set<IServiceChangeListener<S>> listeners = new CopyOnWriteArraySet<IServiceChangeListener<S>>();
		
		/**
		 * Returns the service interface of this registry entry.
		 * @return the service interface
		 */
		public Class<S> getServiceInterface() {
			return serviceInterface;
		}
		
		/**
		 * Returns the actual implementation of the service interface that belongs to this registry entry
		 * @return
		 */
		public S getImplementation() {
			return implementation;
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(final ServiceRegistryEntry<S> o) {
			return serviceInterface.getName().compareTo(o.serviceInterface.getName());
		}

		@Override
		public String toString() {
			return "ServiceRegistryEntry [serviceInterface=" + serviceInterface + ", implementation=" + implementation + ", listeners=" + listeners
					+ "]";
		}
	}
	
	private static ApplicationContext instance;
	
	private final LoadingCache<Class<?>, ServiceRegistryEntry<?>> serviceMap;
	
	/**Returns with the service registered for the class argument key from the shared application context.*/
	public static <T> T getServiceForClass(final Class<T> clazz) {
		return getInstance().getService(clazz);
	}
	
	/**
	 * Factory method.
	 * @return the singleton application context instance.
	 */
	public static ApplicationContext getInstance() {

		if (instance == null) {
			synchronized (ApplicationContext.class) {
				if (instance == null) {
					instance = new ApplicationContext();
				}
			}
		}

		return instance;
	}
	
	private ApplicationContext() {
		serviceMap = CacheBuilder.newBuilder().build(new CacheLoader<Class<?>, ServiceRegistryEntry<?>>() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override public ServiceRegistryEntry<?> load(final Class<?> clazz) throws Exception {
				final ServiceRegistryEntry entry = new ServiceRegistryEntry();
				entry.serviceInterface = clazz;
				return entry;
			}});
	}
	
	/**
	 * Returns with a unmodifiable collection of services that are registered to the application context and
	 * is assignable from the specified service interface.
	 * @param serviceInterface the service interface.
	 * @return a collection of registered services.
	 */
	@SuppressWarnings("unchecked")
	public <T> Collection<T> getServices(final Class<T> serviceInterface) {
		
		Preconditions.checkNotNull(serviceInterface, "Service interface argument cannot be null.");
		final List<T> services = Lists.newArrayList();
		
		for (final ServiceRegistryEntry<?> entry : serviceMap.asMap().values()) {
			if (serviceInterface.isAssignableFrom(entry.serviceInterface)) {
				services.add((T) entry.implementation);
			}
			
		}
		
		return ImmutableList.<T>copyOf(services);
	}
	
	public <T> T getService(final Class<T> serviceInterface) {
		return getServiceEntry(serviceInterface).implementation;
	}

	@SuppressWarnings("unchecked")
	private <T> ServiceRegistryEntry<T> getServiceEntry(final Class<T> serviceInterface) {
		return (ServiceRegistryEntry<T>) serviceMap.getUnchecked(serviceInterface);
	}
	
	/**
	 * Returns with the service instance associated with the passed in service interface.
	 * May throw runtime exception if the service associated with the passed in interface is not registered or {@code null}.  
	 * @param serviceInterface the interface of the required service. Should not be {@code null}.
	 * @return the available registered service instance.
	 * @param <T> type of the service.
	 */
	public <T> T getServiceChecked(final Class<T> serviceInterface) {
		Preconditions.checkNotNull(serviceInterface, "Service interface argument cannot be null.");
		final T service = getService(serviceInterface);
		Preconditions.checkNotNull(service, "Service for " + serviceInterface.getSimpleName() + " service cannot be null.");
		return service;
	}
	
	
	public <T> void registerService(final Class<T> serviceInterface, final T implementation) {
		
		Preconditions.checkNotNull(implementation, "Registering service for " + serviceInterface.getSimpleName() + " is prohibited with unspecified implementation.");
		
		final ServiceRegistryEntry<T> entry = getServiceEntry(serviceInterface);
		final T oldImplementation = entry.implementation;
		entry.implementation = implementation;
		// notify listeners
		for (final Iterator<IServiceChangeListener<T>> ir = new CopyOnWriteArrayList<IServiceChangeListener<T>>(entry.listeners).iterator(); ir.hasNext(); /**/) {
			final IServiceChangeListener<T> listener = ir.next();
			listener.serviceChanged(oldImplementation, implementation);
		}
		
		// dispose old registered implementation
		if (oldImplementation instanceof IDisposableService && !((IDisposableService) oldImplementation).isDisposed()) {
			((IDisposableService)oldImplementation).dispose();
		}
		
		LOGGER.debug(MessageFormat.format("Registered service {0} for interface {1}.", implementation.getClass().getName(), serviceInterface.getName()));
	}
	
	/**
	 * Unregisters a service give its service interface from the application context. The associated {@link IServiceChangeListener service change listener}s
	 * will not be removed. 
	 * @param serviceInterface the service interface to unregister.
	 */
	public <T> void unregisterService(final Class<T> serviceInterface) {
		
		Preconditions.checkNotNull(serviceInterface, "Service interface class argument cannot be null.");
		
		final AtomicBoolean exists = new AtomicBoolean(null != serviceMap.getIfPresent(serviceInterface));
		serviceMap.asMap().remove(serviceInterface);
		exists.compareAndSet(true, null == serviceMap.getIfPresent(serviceInterface));
		if (exists.get()) {
			LOGGER.debug(MessageFormat.format("Unregistered service for interface {0}", serviceInterface.getName()));
		} else {
			LOGGER.warn("Failed to unregister service for " + serviceInterface.getSimpleName() + ".");
		}
		
	}
	
	/**
	 * Returns {@code true} if a the service given with it interface is registered to the application context with an implementation
	 * that can be referenced by clients. Otherwise {@code false}.  
	 * @param serviceInterface
	 */
	public <T> boolean exists(final Class<T> serviceInterface) {
		final ServiceRegistryEntry<?> entry = serviceMap.getIfPresent(Preconditions.checkNotNull(serviceInterface, "Service interface argument cannot be null."));
		return null != entry && null != entry.implementation;
	}
	
	public <T> void addServiceListener(final Class<T> serviceInterface, final IServiceChangeListener<T> listener) {
		final ServiceRegistryEntry<T> entry = getServiceEntry(serviceInterface);
		if (null != entry.implementation) {
			listener.serviceChanged(null, entry.implementation);
		}
		entry.listeners.add(listener);
	}
	
	/**
	 * Checks the availability of the registered services strictly, which means
	 * that <code>null</code> implementations are not acceptable.
	 * 
	 * @return the set of {@link ServiceRegistryEntry}s which are currently does
	 *         not have implementation classes registered.
	 */
	public Collection<ServiceRegistryEntry<?>> checkStrictServices() {
		final Collection<ServiceRegistryEntry<?>> missingImplementations = newHashSet();
		for (final ServiceRegistryEntry<?> entry : serviceMap.asMap().values()) {
			if (null == entry.implementation) {
				missingImplementations.add(entry);
			}
		}
		return missingImplementations;
	}
	
	public <T> void removeServiceListener(final Class<T> serviceInterface, final IServiceChangeListener<T> listener) {
		final ServiceRegistryEntry<T> entry = getServiceEntry(serviceInterface);
		entry.listeners.remove(listener);
	}

	public void dispose() {
		for (final Object service : serviceMap.asMap().values()) {
			if (service instanceof ServiceRegistryEntry<?> && ((ServiceRegistryEntry<?>) service).implementation instanceof IDisposableService) {
				try {
					((IDisposableService) ((ServiceRegistryEntry<?>) service).implementation).dispose();

				} catch (final Exception e) {
					LOGGER.error("Error while disposing service " + service.getClass().getCanonicalName(), e);
				}
			}
		}
	}
	
	private static final String IS_SERVER_MODE_KEY = "serverMode";
	private static final String VIRGO_KERNEL_HOME_KEY = "org.eclipse.virgo.kernel.home";
	
	private final Supplier<Boolean> serverFlagSupplier = Suppliers.memoize(new Supplier<Boolean>() {
		@Override public Boolean get() {
			if (null != Platform.getProduct()) {
				// equinox based server
				return Boolean.parseBoolean(Platform.getProduct().getProperty(IS_SERVER_MODE_KEY));
			}
			// when running junit plugin tests, the product is null, and no virgo kernel
			return System.getProperty(VIRGO_KERNEL_HOME_KEY) != null; 
		};
	}); 

	/**
	 * Returns {@code true} if the application for the current application context is running in server mode. Otherwise, returns {@code false}.
	 * @return {@code true} if the application is running in server mode, otherwise {@code false}.
	 */
	public boolean isServerMode() {
		return serverFlagSupplier.get();
	}
	
	/**
	 * Convenience method for handling generic exceptions. 
	 * 
	 * @param exception
	 * @param errorMessage
	 */
	public static void handleException(final Plugin plugin, final Throwable exception, final String errorMessage) {
		final MultiStatus errorStatus = getServiceInfo(plugin.getBundle(), exception);
		final IStatus status = new Status(IStatus.ERROR, plugin.getBundle().getSymbolicName(), IStatus.ERROR, errorMessage, exception);
		errorStatus.add(status);
		plugin.getLog().log(status);
	}
	
	/**
	 * Convenience method for handling generic exceptions. 
	 * 
	 * @param exception
	 * @param errorMessage
	 */
	public static void handleException(final Bundle bundle, final Exception exception, final String errorMessage) {
		final MultiStatus errorStatus = getServiceInfo(bundle, exception);
		final IStatus status = new Status(IStatus.ERROR, bundle.getSymbolicName(), IStatus.ERROR, errorMessage, exception);
		errorStatus.add(status);
		Platform.getLog(bundle).log(status);
	}
	
	/**
	 * Convenience method for handling generic exceptions. 
	 * @param bundle
	 * @param status
	 */
	public static void handleErrorStatus(final Bundle bundle, final IStatus status) {
		final MultiStatus multiStatus = getServiceInfo(bundle, status.getException());
		multiStatus.add(status);
		Platform.getLog(bundle).log(status);
	}
	
	/**
	 * Method for log warning. 
	 * @param bundle required for the bundle ID.
	 * @param warningMessage the message to log.
	 */
	public static void logWarning(final Bundle bundle, final String warningMessage) {
		Platform.getLog(bundle).log(new Status(IStatus.WARNING, bundle.getSymbolicName(), warningMessage));
	}
	
	/**
	 * Initializes the project specific MultiStatus object for generic serviceablility support for the plugin.
	 * 
	 * @param exception
	 * @return configured and initialized MultiStatus object
	 */
	private static MultiStatus getServiceInfo(final Bundle bundle, final Throwable exception) {
		final String symbolicName = bundle.getSymbolicName();
		final MultiStatus multiStatus = new MultiStatus(symbolicName, IStatus.ERROR, exception == null ? null : exception.getMessage(), null);

		final String bundleName = bundle.getHeaders().get("Bundle-Name");
		final String bundleVendor = bundle.getHeaders().get("Bundle-Vendor");
		final String bundleVersion = bundle.getHeaders().get("Bundle-Version");
		
		multiStatus.add(Statuses.error(symbolicName, "Plug-in Vendor: " + bundleVendor));
		multiStatus.add(Statuses.error(symbolicName, "Plug-in Name: " + bundleName));
		multiStatus.add(Statuses.error(symbolicName, "Plug-in ID: " + symbolicName));
		multiStatus.add(Statuses.error(symbolicName, "Plug-in Version: " + bundleVersion));

		return multiStatus;
	}

}