/*******************************************************************************
 * Copyright (c) 2004, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Danail Nachev (ProSyst) - bug 188070
 *******************************************************************************/
package org.eclipse.core.internal.preferences;

import java.util.Set;
import java.util.TreeSet;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.osgi.framework.*;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * <p>
 * Class used to manage OSGi Preferences Service.  Creates a new OSGiPreferencesServiceImpl
 * object for every bundle that gets the Preferences Service.  When a bundle ungets the 
 * Preference Service, it's preferences are flushed to disk.
 * </p>
 * <p>
 * Also deletes saved preferences for bundles which are uninstalled.
 * </p>
 */
public class OSGiPreferencesServiceManager implements ServiceFactory, BundleListener {

	private static final String ORG_ECLIPSE_CORE_INTERNAL_PREFERENCES_OSGI = "org.eclipse.core.internal.preferences.osgi"; //$NON-NLS-1$

	//keys are bundles that use OSGi prefs
	private Preferences prefBundles;

	public OSGiPreferencesServiceManager(BundleContext context) {

		context.addBundleListener(this);

		//prefBundles = new InstanceScope().getNode(ORG_ECLIPSE_CORE_INTERNAL_PREFERENCES_OSGI);
		prefBundles = ConfigurationScope.INSTANCE.getNode(ORG_ECLIPSE_CORE_INTERNAL_PREFERENCES_OSGI);

		//clean up prefs for bundles that have been uninstalled
		try {

			//get list of currently installed bundles
			Bundle[] allBundles = context.getBundles();
			Set bundleQualifiers = new TreeSet();
			for (int i = 0; i < allBundles.length; i++) {
				bundleQualifiers.add(getQualifier(allBundles[i]));
			}

			//get list of bundles we created prefs for
			String[] prefsBundles = prefBundles.keys();

			//remove prefs nodes for bundles that are no longer installed
			for (int i = 0; i < prefsBundles.length; i++) {
				if (!bundleQualifiers.contains(prefsBundles[i])) {
					removePrefs(prefsBundles[i]);
				}
			}

		} catch (BackingStoreException e) {
			//best effort
		}
	}

	/**
	 * Creates a new OSGiPreferencesServiceImpl for each bundle.
	 */
	public Object getService(Bundle bundle, ServiceRegistration registration) {
		String qualifier = getQualifier(bundle);
		//remember we created prefs for this bundle
		Preferences bundlesNode = getBundlesNode();
		bundlesNode.put(qualifier, ""); //$NON-NLS-1$
		try {
			bundlesNode.flush();
		} catch (BackingStoreException e) {
			//best effort
		}
		//return new OSGiPreferencesServiceImpl(new InstanceScope().getNode(getQualifier(bundle)));
		return new OSGiPreferencesServiceImpl(ConfigurationScope.INSTANCE.getNode(getQualifier(bundle)));
	}

	/**
	 * Store preferences per bundle id
	 */
	private String getQualifier(Bundle bundle) {
		String qualifier = "org.eclipse.core.runtime.preferences.OSGiPreferences." + bundle.getBundleId(); //$NON-NLS-1$		
		return qualifier;
	}

	/**
	 * Flush the bundle's preferences.
	 */
	public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {
		try {
			//new InstanceScope().getNode(getQualifier(bundle)).flush();
			ConfigurationScope.INSTANCE.getNode(getQualifier(bundle)).flush();
		} catch (BackingStoreException e) {
			//best effort
		}
	}

	/**
	 * If a bundle is uninstalled, delete all of it's preferences from the disk.
	 */
	public void bundleChanged(BundleEvent event) {
		if (event.getType() == BundleEvent.UNINSTALLED) {
			try {
				removePrefs(getQualifier(event.getBundle()));
			} catch (BackingStoreException e) {
				//best effort
			}
		}

	}

	protected void removePrefs(String qualifier) throws BackingStoreException {
		//remove bundle's prefs
		//new InstanceScope().getNode(qualifier).removeNode();
		ConfigurationScope.INSTANCE.getNode(qualifier).removeNode();

		//remove from our list of bundles with prefs
		Preferences bundlesNode = getBundlesNode();
		bundlesNode.remove(qualifier);
		bundlesNode.flush();
	}

	private Preferences getBundlesNode() {
		try {
			if (prefBundles == null || !prefBundles.nodeExists("")) { //$NON-NLS-1$
				prefBundles = ConfigurationScope.INSTANCE.getNode(ORG_ECLIPSE_CORE_INTERNAL_PREFERENCES_OSGI);
			}
			return prefBundles;
		} catch (BackingStoreException e) {
			// ignore
		}
		return null;
	}
}
