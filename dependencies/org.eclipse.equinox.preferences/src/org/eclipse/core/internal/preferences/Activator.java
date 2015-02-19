/*******************************************************************************
 * Copyright (c) 2005, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.internal.preferences;

import java.util.Hashtable;
import org.eclipse.core.internal.runtime.RuntimeLog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.osgi.service.environment.EnvironmentInfo;
import org.osgi.framework.*;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * The Preferences bundle activator.
 */
public class Activator implements BundleActivator, ServiceTrackerCustomizer {

	public static final String PI_PREFERENCES = "org.eclipse.equinox.preferences"; //$NON-NLS-1$

	/**
	 * Eclipse property. Set to <code>false</code> to avoid registering JobManager
	 * as an OSGi service.
	 */
	private static final String PROP_REGISTER_PERF_SERVICE = "eclipse.service.pref"; //$NON-NLS-1$
	// the system property
	private static final String PROP_CUSTOMIZATION = "eclipse.pluginCustomization"; //$NON-NLS-1$

	/**
	 * Track the registry service - only register preference service if the registry is 
	 * available
	 */
	private ServiceTracker registryServiceTracker;

	/**
	 * The bundle associated this plug-in
	 */
	private static BundleContext bundleContext;

	/**
	 * This plugin provides a Preferences service.
	 */
	private ServiceRegistration preferencesService = null;

	/**
	 * This plugin provides the OSGi Preferences service.
	 */
	private ServiceRegistration osgiPreferencesService = null;

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		bundleContext = context;
		// Open the services first before processing the command-line args, order is important! (Bug 150288)
		PreferencesOSGiUtils.getDefault().openServices();
		processCommandLine();

		boolean shouldRegister = !"false".equalsIgnoreCase(context.getProperty(PROP_REGISTER_PERF_SERVICE)); //$NON-NLS-1$
		if (shouldRegister) {
			preferencesService = bundleContext.registerService(IPreferencesService.class.getName(), PreferencesService.getDefault(), new Hashtable());
			osgiPreferencesService = bundleContext.registerService(org.osgi.service.prefs.PreferencesService.class.getName(), new OSGiPreferencesServiceManager(bundleContext), null);
		}
		// use the string for the class name here in case the registry isn't around
		registryServiceTracker = new ServiceTracker(bundleContext, "org.eclipse.core.runtime.IExtensionRegistry", this); //$NON-NLS-1$
		registryServiceTracker.open();
	}

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		PreferencesOSGiUtils.getDefault().closeServices();
		if (registryServiceTracker != null) {
			registryServiceTracker.close();
			registryServiceTracker = null;
		}
		if (preferencesService != null) {
			preferencesService.unregister();
			preferencesService = null;
		}
		if (osgiPreferencesService != null) {
			osgiPreferencesService.unregister();
			osgiPreferencesService = null;
		}
		bundleContext = null;
	}

	static BundleContext getContext() {
		return bundleContext;
	}

	/* (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#addingService(org.osgi.framework.ServiceReference)
	 */
	public synchronized Object addingService(ServiceReference reference) {
		Object service = bundleContext.getService(reference);
		// this check is important as it avoids early loading of PreferenceServiceRegistryHelper and allows
		// this bundle to operate with out necessarily resolving against the registry
		if (service != null) {
			try {
				Object helper = new PreferenceServiceRegistryHelper(PreferencesService.getDefault(), service);
				PreferencesService.getDefault().setRegistryHelper(helper);
			} catch (Exception e) {
				RuntimeLog.log(new Status(IStatus.ERROR, PI_PREFERENCES, 0, PrefsMessages.noRegistry, e));
			} catch (NoClassDefFoundError error) {
				// Normally this catch would not be needed since we should never see the
				// IExtensionRegistry service without resolving against registry.
				// However, the check is very lenient with split packages and this can happen when
				// the preferences bundle is already resolved at the time the registry bundle is installed.
				// For this case we ignore the error. When refreshed the bundle will be rewired correctly.
				// null is returned because we don't want to track this particular service reference.
				return null;
			}
		}
		//return the registry service so we track it
		return service;
	}

	/* (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#modifiedService(org.osgi.framework.ServiceReference, java.lang.Object)
	 */
	public void modifiedService(ServiceReference reference, Object service) {
		// nothing to do
	}

	/* (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#removedService(org.osgi.framework.ServiceReference, java.lang.Object)
	 */
	public synchronized void removedService(ServiceReference reference, Object service) {
		PreferencesService.getDefault().setRegistryHelper(null);
		bundleContext.ungetService(reference);
	}

	/**
	 * Look for the plug-in customization file in the system properties and command-line args.
	 */
	private void processCommandLine() {
		// check the value of the system property first because its quicker.
		// if it exists, then set it otherwise look at the command-line arguments.
		String value = bundleContext.getProperty(PROP_CUSTOMIZATION);
		if (value != null) {
			DefaultPreferences.pluginCustomizationFile = value;
			return;
		}

		ServiceTracker environmentTracker = new ServiceTracker(bundleContext, EnvironmentInfo.class.getName(), null);
		environmentTracker.open();
		EnvironmentInfo environmentInfo = (EnvironmentInfo) environmentTracker.getService();
		environmentTracker.close();
		if (environmentInfo == null)
			return;
		String[] args = environmentInfo.getNonFrameworkArgs();
		if (args == null || args.length == 0)
			return;

		for (int i = 0; i < args.length; i++) {
			if (args[i].equalsIgnoreCase(IPreferencesConstants.PLUGIN_CUSTOMIZATION)) {
				if (args.length > i + 1) // make sure the file name is actually there
					DefaultPreferences.pluginCustomizationFile = args[i + 1];
				break; // only interested in this one
			}
		}
	}
}
