/*******************************************************************************
 * Copyright (c) 2004, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Gunnar Wagenknecht - Bug 179695 - [prefs] NPE when using Preferences API without a product
 *******************************************************************************/
package org.eclipse.core.internal.preferences;

import java.io.*;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.*;
import org.eclipse.core.internal.preferences.exchange.IProductPreferencesService;
import org.eclipse.core.internal.runtime.RuntimeLog;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.preferences.BundleDefaultsScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.Preferences;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @since 3.0
 */
public class DefaultPreferences extends EclipsePreferences {
	// cache which nodes have been loaded from disk
	private static Set loadedNodes = Collections.synchronizedSet(new HashSet());
	private static final String KEY_PREFIX = "%"; //$NON-NLS-1$
	private static final String KEY_DOUBLE_PREFIX = "%%"; //$NON-NLS-1$
	private static final IPath NL_DIR = new Path("$nl$"); //$NON-NLS-1$

	private static final String PROPERTIES_FILE_EXTENSION = "properties"; //$NON-NLS-1$
	private static Properties productCustomization;
	private static Properties productTranslation;
	private static Properties commandLineCustomization;
	private EclipsePreferences loadLevel;
	private Thread initializingThread;

	// cached values
	private String qualifier;
	private int segmentCount;
	private WeakReference pluginReference;

	public static String pluginCustomizationFile = null;

	/**
	 * Default constructor for this class.
	 */
	public DefaultPreferences() {
		this(null, null);
	}

	private DefaultPreferences(EclipsePreferences parent, String name, Object context) {
		this(parent, name);
		this.pluginReference = new WeakReference(context);
	}

	private DefaultPreferences(EclipsePreferences parent, String name) {
		super(parent, name);

		if (parent instanceof DefaultPreferences)
			this.pluginReference = ((DefaultPreferences) parent).pluginReference;

		// cache the segment count
		String path = absolutePath();
		segmentCount = getSegmentCount(path);
		if (segmentCount < 2)
			return;

		// cache the qualifier
		qualifier = getSegment(path, 1);
	}

	/*
	 * Apply the values set in the bundle's install directory.
	 * 
	 * In Eclipse 2.1 this is equivalent to:
	 *		/eclipse/plugins/<pluginID>/prefs.ini
	 */
	private void applyBundleDefaults() {
		Bundle bundle = PreferencesOSGiUtils.getDefault().getBundle(name());
		if (bundle == null)
			return;
		URL url = FileLocator.find(bundle, new Path(IPreferencesConstants.PREFERENCES_DEFAULT_OVERRIDE_FILE_NAME), null);
		if (url == null) {
			if (EclipsePreferences.DEBUG_PREFERENCE_GENERAL)
				PrefsMessages.message("Preference default override file not found for bundle: " + bundle.getSymbolicName()); //$NON-NLS-1$
			return;
		}
		URL transURL = FileLocator.find(bundle, NL_DIR.append(IPreferencesConstants.PREFERENCES_DEFAULT_OVERRIDE_BASE_NAME).addFileExtension(PROPERTIES_FILE_EXTENSION), null);
		if (transURL == null && EclipsePreferences.DEBUG_PREFERENCE_GENERAL)
			PrefsMessages.message("Preference translation file not found for bundle: " + bundle.getSymbolicName()); //$NON-NLS-1$
		applyDefaults(name(), loadProperties(url), loadProperties(transURL));
	}

	/*
	 * Apply the default values as specified in the file
	 * as an argument on the command-line.
	 */
	private void applyCommandLineDefaults() {
		// prime the cache the first time
		if (commandLineCustomization == null) {
			String filename = pluginCustomizationFile;
			if (filename == null) {
				if (EclipsePreferences.DEBUG_PREFERENCE_GENERAL)
					PrefsMessages.message("Command-line preferences customization file not specified."); //$NON-NLS-1$
				return;
			}
			if (EclipsePreferences.DEBUG_PREFERENCE_GENERAL)
				PrefsMessages.message("Using command-line preference customization file: " + filename); //$NON-NLS-1$
			commandLineCustomization = loadProperties(filename);
		}
		applyDefaults(null, commandLineCustomization, null);
	}

	/*
	 * If the qualifier is null then the file is of the format:
	 * 	pluginID/key=value
	 * otherwise the file is of the format:
	 * 	key=value
	 */
	private void applyDefaults(String id, Properties defaultValues, Properties translations) {
		for (Enumeration e = defaultValues.keys(); e.hasMoreElements();) {
			String fullKey = (String) e.nextElement();
			String value = defaultValues.getProperty(fullKey);
			if (value == null)
				continue;
			IPath childPath = new Path(fullKey);
			String key = childPath.lastSegment();
			childPath = childPath.removeLastSegments(1);
			String localQualifier = id;
			if (id == null) {
				localQualifier = childPath.segment(0);
				childPath = childPath.removeFirstSegments(1);
			}
			if (name().equals(localQualifier)) {
				value = translatePreference(value, translations);
				if (EclipsePreferences.DEBUG_PREFERENCE_SET)
					PrefsMessages.message("Setting default preference: " + (new Path(absolutePath()).append(childPath).append(key)) + '=' + value); //$NON-NLS-1$
				((EclipsePreferences) internalNode(childPath.toString(), false, null)).internalPut(key, value);
			}
		}
	}

	public IEclipsePreferences node(String childName, Object context) {
		return internalNode(childName, true, context);
	}

	/*
	 * Runtime defaults are the ones which are specified in code at runtime. 
	 * 
	 * In the Eclipse 2.1 world they were the ones which were specified in the
	 * over-ridden Plugin#initializeDefaultPluginPreferences() method.
	 * 
	 * In Eclipse 3.0 they are set in the code which is indicated by the
	 * extension to the plug-in default customizer extension point.
	 */
	private void applyRuntimeDefaults() {
		WeakReference ref = PreferencesService.getDefault().applyRuntimeDefaults(name(), pluginReference);
		if (ref != null)
			pluginReference = ref;
	}

	/*
	 * Apply the default values as specified by the file
	 * in the product extension.
	 * 
	 * In Eclipse 2.1 this is equivalent to the plugin_customization.ini
	 * file in the primary feature's plug-in directory.
	 */
	private void applyProductDefaults() {
		// prime the cache the first time
		if (productCustomization == null) {
			BundleContext context = Activator.getContext();
			if (context != null) {
				ServiceTracker productTracker = new ServiceTracker(context, IProductPreferencesService.class.getName(), null);
				productTracker.open();
				IProductPreferencesService productSpecials = (IProductPreferencesService) productTracker.getService();
				if (productSpecials != null) {
					productCustomization = productSpecials.getProductCustomization();
					productTranslation = productSpecials.getProductTranslation();
				}
				productTracker.close();
			} else {
				PrefsMessages.message("Product-specified preferences called before plugin is started"); //$NON-NLS-1$
			}
			if (productCustomization == null)
				productCustomization = new Properties();
		}
		if (!productCustomization.isEmpty())
			applyDefaults(null, productCustomization, productTranslation);
	}

	/* (non-Javadoc)
	 * @see org.osgi.service.prefs.Preferences#flush()
	 */
	public void flush() {
		// default values are not persisted
	}

	protected IEclipsePreferences getLoadLevel() {
		if (loadLevel == null) {
			if (qualifier == null)
				return null;
			// Make it relative to this node rather than navigating to it from the root.
			// Walk backwards up the tree starting at this node.
			// This is important to avoid a chicken/egg thing on startup.
			EclipsePreferences node = this;
			for (int i = 2; i < segmentCount; i++)
				node = (EclipsePreferences) node.parent();
			loadLevel = node;
		}
		return loadLevel;
	}

	protected EclipsePreferences internalCreate(EclipsePreferences nodeParent, String nodeName, Object context) {
		return new DefaultPreferences(nodeParent, nodeName, context);
	}

	protected boolean isAlreadyLoaded(IEclipsePreferences node) {
		return loadedNodes.contains(node.name());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.preferences.EclipsePreferences#load()
	 */
	protected void load() {
		setInitializingBundleDefaults();
		try {
			applyRuntimeDefaults();
			applyBundleDefaults();
		} finally {
			clearInitializingBundleDefaults();
		}
		applyProductDefaults();
		applyCommandLineDefaults();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.preferences.EclipsePreferences#internalPut(java.lang.String, java.lang.String)
	 */
	protected String internalPut(String key, String newValue) {
		// set the value in this node
		String result = super.internalPut(key, newValue);

		// if we are setting the bundle defaults, then set the corresponding value in
		// the bundle_defaults scope
		if (isInitializingBundleDefaults()) {
			String relativePath = getScopeRelativePath(absolutePath());
			if (relativePath != null) {
				Preferences node = PreferencesService.getDefault().getRootNode().node(BundleDefaultsScope.SCOPE).node(relativePath);
				node.put(key, newValue);
			}
		}
		return result;
	}

	/*
	 * Set that we are in the middle of initializing the bundle defaults.
	 * This is stored on the load level so we know where to look when
	 * we are setting values on sub-nodes.
	 */
	private void setInitializingBundleDefaults() {
		IEclipsePreferences node = getLoadLevel();
		if (node instanceof DefaultPreferences) {
			DefaultPreferences loader = (DefaultPreferences) node;
			loader.initializingThread = Thread.currentThread();
		}
	}

	/*
	 * Clear the bit saying we are in the middle of initializing the bundle defaults.
	 * This is stored on the load level so we know where to look when
	 * we are setting values on sub-nodes.
	 */
	private void clearInitializingBundleDefaults() {
		IEclipsePreferences node = getLoadLevel();
		if (node instanceof DefaultPreferences) {
			DefaultPreferences loader = (DefaultPreferences) node;
			loader.initializingThread = null;
		}
	}

	/*
	 * Are we in the middle of initializing defaults from the bundle 
	 * initializer or found in the bundle itself? Look on the load level in
	 * case we are in a sub-node.
	 */
	private boolean isInitializingBundleDefaults() {
		IEclipsePreferences node = getLoadLevel();
		if (node instanceof DefaultPreferences) {
			DefaultPreferences loader = (DefaultPreferences) node;
			return loader.initializingThread == Thread.currentThread();
		}
		return false;
	}

	/*
	 * Return a path which is relative to the scope of this node. 
	 * e.g. com.example.foo for /instance/com.example.foo
	 */
	protected static String getScopeRelativePath(String absolutePath) {
		// shouldn't happen but handle empty or root
		if (absolutePath.length() < 2)
			return null;
		int index = absolutePath.indexOf('/', 1);
		if (index == -1 || index + 1 >= absolutePath.length())
			return null;
		return absolutePath.substring(index + 1);
	}

	private Properties loadProperties(URL url) {
		Properties result = new Properties();
		if (url == null)
			return result;
		InputStream input = null;
		try {
			input = url.openStream();
			result.load(input);
		} catch (IOException e) {
			if (EclipsePreferences.DEBUG_PREFERENCE_GENERAL) {
				PrefsMessages.message("Problem opening stream to preference customization file: " + url); //$NON-NLS-1$
				e.printStackTrace();
			}
		} finally {
			if (input != null)
				try {
					input.close();
				} catch (IOException e) {
					// ignore
				}
		}
		return result;
	}

	private Properties loadProperties(String filename) {
		Properties result = new Properties();
		InputStream input = null;
		try {
			input = new BufferedInputStream(new FileInputStream(filename));
			result.load(input);
		} catch (FileNotFoundException e) {
			if (EclipsePreferences.DEBUG_PREFERENCE_GENERAL)
				PrefsMessages.message("Preference customization file not found: " + filename); //$NON-NLS-1$
		} catch (IOException e) {
			String message = NLS.bind(PrefsMessages.preferences_loadException, filename);
			IStatus status = new Status(IStatus.ERROR, PrefsMessages.OWNER_NAME, IStatus.ERROR, message, e);
			RuntimeLog.log(status);
		} finally {
			if (input != null)
				try {
					input.close();
				} catch (IOException e) {
					// ignore
				}
		}
		return result;
	}

	protected void loaded() {
		loadedNodes.add(name());
	}

	/* (non-Javadoc)
	 * @see org.osgi.service.prefs.Preferences#sync()
	 */
	public void sync() {
		// default values are not persisted
	}

	/**
	 * Takes a preference value and a related resource bundle and
	 * returns the translated version of this value (if one exists).
	 */
	private String translatePreference(String origValue, Properties props) {
		if (props == null || origValue.startsWith(KEY_DOUBLE_PREFIX))
			return origValue;
		if (origValue.startsWith(KEY_PREFIX)) {
			String value = origValue.trim();
			int ix = value.indexOf(" "); //$NON-NLS-1$
			String key = ix == -1 ? value.substring(1) : value.substring(1, ix);
			String dflt = ix == -1 ? value : value.substring(ix + 1);
			return props.getProperty(key, dflt);
		}
		return origValue;
	}
}
