/*******************************************************************************
 * Copyright (c) 2009, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.runtime.preferences;

import org.eclipse.core.internal.preferences.AbstractScope;
import org.eclipse.core.runtime.IPath;

/**
 * Object representing the bundle defaults scope in the Eclipse preferences
 * hierarchy. Can be used as a context for searching for preference
 * values (in the IPreferencesService APIs) or for determining the 
 * correct preference node to set values in the store.
 * <p>
 * The bundle defaults are the defaults are default values which have
 * been set either by the bundle's preference initializer or by a customization
 * file supplied with the bundle.
 * <p>
 * Bundle default preferences are not persisted to disk.
 * </p>
 * <p>
 * The path for preferences defined in the bundle defaults scope hierarchy
 * is as follows: <code>/bundle_defaults/&lt;qualifier&gt;</code>
 * </p>
 * <p>
 * This class is not intended to be subclassed. This class may be instantiated.
 * </p>
 * @since 3.3
 */
public final class BundleDefaultsScope extends AbstractScope {

	/**
	 * String constant (value of <code>"default"</code>) used for the 
	 * scope name for the default preference scope.
	 */
	public static final String SCOPE = "bundle_defaults"; //$NON-NLS-1$

	/**
	 * Singleton instance of a Bundle Defaults Scope object. Typical usage is:
	 * <code>BundleDefaultsScope.INSTANCE.getNode(...);</code>
	 * 
	 * @since 3.4
	 */
	public static final IScopeContext INSTANCE = new BundleDefaultsScope();

	/**
	 * Create and return a new default scope instance.
	 * @deprecated use <code>BundleDefaultsScope.INSTANCE</code> instead
	 */
	public BundleDefaultsScope() {
		super();
	}

	/*
	 * @see org.eclipse.core.runtime.preferences.IScopeContext#getName()
	 */
	public String getName() {
		return SCOPE;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.IScopeContext#getNode(java.lang.String)
	 */
	public IEclipsePreferences getNode(String qualifier) {
		return super.getNode(qualifier);
	}

	/*
	 * @see org.eclipse.core.runtime.preferences.IScopeContext#getLocation()
	 */
	public IPath getLocation() {
		// We don't persist defaults so return null.
		return null;
	}
}
