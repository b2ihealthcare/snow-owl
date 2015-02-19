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

/**
 * Container for the constants used by this plugin. 
 * @since org.eclipse.equinox.preferences 1.0
 */
public interface IPreferencesConstants {
	/**
	 * Backward compatibilty: name of the original runtime plugin
	 */
	public static final String RUNTIME_NAME = "org.eclipse.core.runtime"; //$NON-NLS-1$

	/**
	 * Name of this plugin
	 */
	public static final String PREFERS_NAME = "org.eclipse.equinox.preferences"; //$NON-NLS-1$

	/**
	 * Command line options
	 */
	public static final String PLUGIN_CUSTOMIZATION = "-plugincustomization"; //$NON-NLS-1$

	/**
	 * This is the base filename used to construct the name of the preference
	 * file and the name of the preference translation file.
	 */
	public static final String PREFERENCES_DEFAULT_OVERRIDE_BASE_NAME = "preferences"; //$NON-NLS-1$

	/**
	 * The name of the file (value <code>"preferences.ini"</code>) in a
	 * plug-in's (read-only) directory that, when present, contains values that
	 * override the normal default values for this plug-in's preferences.
	 * <p>
	 * The format of the file is as per <code>java.io.Properties</code> where
	 * the keys are property names and values are strings.
	 * </p>
	 */
	public static final String PREFERENCES_DEFAULT_OVERRIDE_FILE_NAME = PREFERENCES_DEFAULT_OVERRIDE_BASE_NAME + ".ini"; //$NON-NLS-1$

	/** 
	 * The simple identifier constant (value "<code>preferences</code>") of
	 * the extension point of the Core Runtime plug-in where plug-ins declare
	 * extensions to the preference facility. A plug-in may define any number
	 * of preference extensions.
	 */
	public static final String PT_PREFERENCES = "preferences"; //$NON-NLS-1$

}
