/*******************************************************************************
 * Copyright (c) 2004, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.internal.preferences;

import java.io.*;
import java.util.*;
import org.eclipse.core.internal.runtime.MetaDataKeeper;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.osgi.service.datalocation.Location;

/**
 * @since 3.0
 */
public class InstancePreferences extends EclipsePreferences {

	// cached values
	private String qualifier;
	private int segmentCount;
	private IEclipsePreferences loadLevel;
	private IPath location;
	// cache which nodes have been loaded from disk
	private static Set loadedNodes = Collections.synchronizedSet(new HashSet());
	private static boolean initialized = false;
	private static IPath baseLocation;

	/* package */static IPath getBaseLocation() {
		// If we are running with -data=@none we won't have an instance location.
		// By leaving the value of baseLocation as null we still allow the users
		// to set preferences in this scope but the values will not be persisted
		// to disk when #flush() is called.
		if (baseLocation == null) {
			Location instanceLocation = PreferencesOSGiUtils.getDefault().getInstanceLocation();
			if (instanceLocation != null && (instanceLocation.isSet() || instanceLocation.allowsDefault()))
				baseLocation = MetaDataKeeper.getMetaArea().getStateLocation(IPreferencesConstants.RUNTIME_NAME);
		}
		return baseLocation;
	}

	/**
	 * Default constructor. Should only be called by #createExecutableExtension.
	 */
	public InstancePreferences() {
		this(null, null);
	}

	private InstancePreferences(EclipsePreferences parent, String name) {
		super(parent, name);

		initializeChildren();

		// cache the segment count
		String path = absolutePath();
		segmentCount = getSegmentCount(path);
		if (segmentCount < 2)
			return;

		// cache the qualifier
		qualifier = getSegment(path, 1);

		// don't cache the location until later in case instance prefs are
		// accessed before the instance location is set.
	}

	protected boolean isAlreadyLoaded(IEclipsePreferences node) {
		return loadedNodes.contains(node.name());
	}

	protected void loaded() {
		loadedNodes.add(name());
	}

	/**
	 * Load the Eclipse 2.1 preferences for the given bundle. If a file
	 * doesn't exist then assume that conversion has already occurred
	 * and do nothing.
	 */
	protected void loadLegacy() {
		IPath path = new Path(absolutePath());
		if (path.segmentCount() != 2)
			return;
		// If we are running with -data=@none we won't have an instance location.
		if (PreferencesOSGiUtils.getDefault().getInstanceLocation() == null) {
			if (EclipsePreferences.DEBUG_PREFERENCE_GENERAL)
				PrefsMessages.message("Cannot load Legacy plug-in preferences since instance location is not set."); //$NON-NLS-1$
			return;
		}
		String bundleName = path.segment(1);
		// the preferences file is located in the plug-in's state area at a well-known name
		// don't need to create the directory if there are no preferences to load
		File prefFile = null;
		Location instanceLocation = PreferencesOSGiUtils.getDefault().getInstanceLocation();
		if (instanceLocation != null && instanceLocation.isSet())
			prefFile = MetaDataKeeper.getMetaArea().getPreferenceLocation(bundleName, false).toFile();
		if (prefFile == null) {
			if (EclipsePreferences.DEBUG_PREFERENCE_GENERAL)
				PrefsMessages.message("Cannot load legacy values because instance location is not set."); //$NON-NLS-1$
			return;
		}
		if (!prefFile.exists()) {
			// no preference file - that's fine
			if (EclipsePreferences.DEBUG_PREFERENCE_GENERAL)
				PrefsMessages.message("Legacy plug-in preference file not found: " + prefFile); //$NON-NLS-1$
			return;
		}

		if (EclipsePreferences.DEBUG_PREFERENCE_GENERAL)
			PrefsMessages.message("Loading legacy preferences from " + prefFile); //$NON-NLS-1$

		// load preferences from file
		InputStream input = null;
		Properties values = new Properties();
		try {
			input = new BufferedInputStream(new FileInputStream(prefFile));
			values.load(input);
		} catch (IOException e) {
			// problems loading preference store - quietly ignore
			if (EclipsePreferences.DEBUG_PREFERENCE_GENERAL)
				PrefsMessages.message("IOException encountered loading legacy preference file " + prefFile); //$NON-NLS-1$
			return;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					// ignore problems with close
					if (EclipsePreferences.DEBUG_PREFERENCE_GENERAL) {
						PrefsMessages.message("IOException encountered closing legacy preference file " + prefFile); //$NON-NLS-1$
						e.printStackTrace();
					}
				}
			}
		}

		// Store values in the preferences object
		for (Iterator i = values.keySet().iterator(); i.hasNext();) {
			String key = (String) i.next();
			String value = values.getProperty(key);
			// value shouldn't be null but check just in case...
			if (value != null) {
				if (EclipsePreferences.DEBUG_PREFERENCE_GENERAL)
					PrefsMessages.message("Loaded legacy preference: " + key + " -> " + value); //$NON-NLS-1$ //$NON-NLS-2$
				// call these 2 methods rather than #put() so we don't send out unnecessary notification
				Object oldValue = internalPut(key, value);
				if (!value.equals(oldValue))
					makeDirty();
			}
		}

		// Delete the old file so we don't try and load it next time. 
		if (!prefFile.delete())
			//Only print out message in failure case if we are debugging.
			if (EclipsePreferences.DEBUG_PREFERENCE_GENERAL)
				PrefsMessages.message("Unable to delete legacy preferences file: " + prefFile); //$NON-NLS-1$
	}

	protected IPath getLocation() {
		if (location == null)
			location = computeLocation(getBaseLocation(), qualifier);
		return location;
	}

	/*
	 * Return the node at which these preferences are loaded/saved.
	 */
	protected IEclipsePreferences getLoadLevel() {
		if (loadLevel == null) {
			if (qualifier == null)
				return null;
			// Make it relative to this node rather than navigating to it from the root.
			// Walk backwards up the tree starting at this node.
			// This is important to avoid a chicken/egg thing on startup.
			IEclipsePreferences node = this;
			for (int i = 2; i < segmentCount; i++)
				node = (IEclipsePreferences) node.parent();
			loadLevel = node;
		}
		return loadLevel;
	}

	/*
	 * Initialize the children for the root of this node. Store the names as
	 * keys in the children table so we can lazily load them later.
	 */
	protected void initializeChildren() {
		if (initialized || parent == null)
			return;
		try {
			synchronized (this) {
				String[] names = computeChildren(getBaseLocation());
				for (int i = 0; i < names.length; i++)
					addChild(names[i], null);
			}
		} finally {
			initialized = true;
		}
	}

	protected EclipsePreferences internalCreate(EclipsePreferences nodeParent, String nodeName, Object context) {
		return new InstancePreferences(nodeParent, nodeName);
	}
}
