/*******************************************************************************
 * Copyright (c) 2004, 2011 IBM Corporation and others.
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
import org.eclipse.osgi.service.datalocation.Location;

/**
 * Object representing the instance scope in the Eclipse preferences
 * hierarchy. Can be used as a context for searching for preference
 * values (in the IPreferencesService APIs) or for determining the 
 * correct preference node to set values in the store.
 * <p>
 * Instance preferences are stored on a per instance basis in the
 * platform's instance area as specified by the {@link Location} class
 * and the {@link Location#INSTANCE_FILTER} filter.
 * </p><p>
 * The path for preferences defined in the instance scope hierarchy
 * is as follows: <code>/instance/&lt;qualifier&gt;</code>
 * </p>
 * <p>
 * This class is not intended to be subclassed. This class may be instantiated.
 * </p>
 * @see Location#INSTANCE_FILTER
 * @since 3.0
 */
public final class InstanceScope extends AbstractScope {

	/**
	 * String constant (value of <code>"instance"</code>) used for the 
	 * scope name for the instance preference scope.
	 */
	public static final String SCOPE = "instance"; //$NON-NLS-1$

	/**
	 * Singleton instance of an Instance Scope object. Typical usage is:
	 * <code>InstanceScope.INSTANCE.getNode(...);</code>
	 * 
	 * @since 3.4
	 */
	public static final IScopeContext INSTANCE = new InstanceScope();

	/**
	 * Create and return a new instance scope instance.
	 * @deprecated call <code>InstanceScope.INSTANCE</code> instead.
	 */
	public InstanceScope() {
		super();
	}

	/*
	 * @see org.eclipse.core.runtime.preferences.IScopeContext#getLocation()
	 */
	public IPath getLocation() {
		// Return null. The instance location usually corresponds to the state
		// location of the bundle and we don't know what bundle we are dealing with.
		return null;
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
}
