/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.internal.preferences;

import org.eclipse.core.internal.preferences.exchange.ILegacyPreferences;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.osgi.service.debug.DebugOptions;
import org.osgi.framework.*;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * This class contains a set of helper OSGI methods for the Preferences plugin.
 * The closeServices() method should be called before the plugin is stopped. 
 * 
 * @since org.eclipse.equinox.preferences 3.2
 */
public class PreferencesOSGiUtils {
	private ServiceTracker initTracker = null;
	private ServiceTracker debugTracker = null;
	private ServiceTracker bundleTracker = null;
	private ServiceTracker configurationLocationTracker = null;
	private ServiceTracker instanceLocationTracker = null;

	private static final PreferencesOSGiUtils singleton = new PreferencesOSGiUtils();

	public static PreferencesOSGiUtils getDefault() {
		return singleton;
	}

	/**
	 * Private constructor to block instance creation.
	 */
	private PreferencesOSGiUtils() {
		super();
	}

	void openServices() {
		BundleContext context = Activator.getContext();
		if (context == null) {
			if (EclipsePreferences.DEBUG_PREFERENCE_GENERAL)
				PrefsMessages.message("PreferencesOSGiUtils called before plugin started"); //$NON-NLS-1$
			return;
		}

		initTracker = new ServiceTracker(context, ILegacyPreferences.class.getName(), null);
		initTracker.open(true);

		debugTracker = new ServiceTracker(context, DebugOptions.class.getName(), null);
		debugTracker.open();

		bundleTracker = new ServiceTracker(context, PackageAdmin.class.getName(), null);
		bundleTracker.open();

		// locations

		Filter filter = null;
		try {
			filter = context.createFilter(Location.CONFIGURATION_FILTER);
		} catch (InvalidSyntaxException e) {
			// ignore this.  It should never happen as we have tested the above format.
		}
		configurationLocationTracker = new ServiceTracker(context, filter, null);
		configurationLocationTracker.open();

		try {
			filter = context.createFilter(Location.INSTANCE_FILTER);
		} catch (InvalidSyntaxException e) {
			// ignore this.  It should never happen as we have tested the above format.
		}
		instanceLocationTracker = new ServiceTracker(context, filter, null);
		instanceLocationTracker.open();
	}

	void closeServices() {
		if (initTracker != null) {
			initTracker.close();
			initTracker = null;
		}
		if (debugTracker != null) {
			debugTracker.close();
			debugTracker = null;
		}
		if (bundleTracker != null) {
			bundleTracker.close();
			bundleTracker = null;
		}
		if (configurationLocationTracker != null) {
			configurationLocationTracker.close();
			configurationLocationTracker = null;
		}
		if (instanceLocationTracker != null) {
			instanceLocationTracker.close();
			instanceLocationTracker = null;
		}
	}

	public ILegacyPreferences getLegacyPreferences() {
		if (initTracker != null)
			return (ILegacyPreferences) initTracker.getService();
		if (EclipsePreferences.DEBUG_PREFERENCE_GENERAL)
			PrefsMessages.message("Legacy preference tracker is not set"); //$NON-NLS-1$
		return null;
	}

	public boolean getBooleanDebugOption(String option, boolean defaultValue) {
		if (debugTracker == null) {
			if (EclipsePreferences.DEBUG_PREFERENCE_GENERAL)
				PrefsMessages.message("Debug tracker is not set"); //$NON-NLS-1$
			return defaultValue;
		}
		DebugOptions options = (DebugOptions) debugTracker.getService();
		if (options != null) {
			String value = options.getOption(option);
			if (value != null)
				return value.equalsIgnoreCase("true"); //$NON-NLS-1$
		}
		return defaultValue;
	}

	public Bundle getBundle(String bundleName) {
		if (bundleTracker == null) {
			if (EclipsePreferences.DEBUG_PREFERENCE_GENERAL)
				PrefsMessages.message("Bundle tracker is not set"); //$NON-NLS-1$
			return null;
		}
		PackageAdmin packageAdmin = (PackageAdmin) bundleTracker.getService();
		if (packageAdmin == null)
			return null;
		Bundle[] bundles = packageAdmin.getBundles(bundleName, null);
		if (bundles == null)
			return null;
		//Return the first bundle that is not installed or uninstalled
		for (int i = 0; i < bundles.length; i++) {
			if ((bundles[i].getState() & (Bundle.INSTALLED | Bundle.UNINSTALLED)) == 0) {
				return bundles[i];
			}
		}
		return null;
	}

	public Location getConfigurationLocation() {
		if (configurationLocationTracker != null)
			return (Location) configurationLocationTracker.getService();
		return null;
	}

	public Location getInstanceLocation() {
		if (instanceLocationTracker != null)
			return (Location) instanceLocationTracker.getService();
		return null;
	}
}
