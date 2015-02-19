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

/**
 * Object representing the default scope in the Eclipse preferences
 * hierarchy. Can be used as a context for searching for preference
 * values (in the IPreferencesService APIs) or for determining the 
 * correct preference node to set values in the store.
 * <p>
 * Default preferences are not persisted to disk.
 * </p>
 * <p>
 * The path for preferences defined in the default scope hierarchy
 * is as follows: <code>/default/&lt;qualifier&gt;</code>
 * </p>
 * <p>
 * Note about product preference customization:
 * Clients who define their own org.eclipse.core.runtime.IProduct 
 * are able to specify a product key of "<code>preferenceCustomization</code>".
 * (defined as a constant in org.eclipse.ui.branding.IProductConstants)
 * Its value is either a {@link java.net.URL} or a file-system path to a 
 * file whose contents are used to customize default preferences. 
 * </p>
 * <p>
 * This class is not intended to be subclassed. This class may be instantiated.
 * </p>
 * @since 3.0
 */
public final class DefaultScope extends AbstractScope {

	/**
	 * String constant (value of <code>"default"</code>) used for the 
	 * scope name for the default preference scope.
	 */
	public static final String SCOPE = "default"; //$NON-NLS-1$

	/**
	 * Singleton instance of a Default Scope object. Typical usage is:
	 * <code>DefaultScope.INSTANCE.getNode(...);</code>
	 * 
	 * @since 3.4
	 */
	public static final IScopeContext INSTANCE = new DefaultScope();

	/**
	 * Create and return a new default scope instance.
	 * @deprecated use <code>DefaultScope.INSTANCE</code> instead
	 */
	public DefaultScope() {
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
