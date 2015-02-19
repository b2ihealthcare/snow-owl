/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.runtime.preferences;

import org.eclipse.core.runtime.IPath;

/**
 * Clients implement this interface to provide context to a 
 * particular scope. Instances of implementations of this interface are 
 * passed to the {@link IPreferencesService} for use in 
 * preference searching.
 * <p>
 * Clients may implement this interface.
 * </p>
 * 
 * @see IPreferencesService
 * @since 3.0
 */
public interface IScopeContext {

	/**
	 * Return the name of the scope that this context is associated with. 
	 * Must not be <code>null</code>.
	 * 
	 * @return the name of the scope
	 */
	public String getName();

	/**
	 * Return the preferences node that contains the preferences for the
	 * given qualifier or <code>null</code> if the node cannot be determined.
	 * The given qualifier must not be <code>null</code> but may be a path 
	 * to a sub-node within the scope.
	 * <p>
	 * An example of a qualifier in Eclipse 2.1 would be the plug-in identifier that 
	 * the preference is associated with (e.g. the "org.eclipse.core.resources" 
	 * plug-in defines the "description.autobuild" preference).
	 * </p><p>
	 * This method can be used to determine the appropriate preferences node
	 * to aid in setting key/value pairs. For instance: 
	 * <code>new InstanceScope().getNode("org.eclipse.core.resources");</code>
	 * returns the preference node in the instance scope where the preferences
	 * for "org.eclipse.core.resources" are stored.
	 * </p>
	 * @param qualifier a qualifier for the preference name
	 * @return the node containing the plug-in preferences or <code>null</code>
	 * @see IPreferencesService
	 */
	public IEclipsePreferences getNode(String qualifier);

	/**
	 * Return a path to a location in the file-system where clients are able
	 * to write files that will have the same sharing/scope properties as
	 * preferences defined in this scope.
	 * <p>
	 * Implementors may return <code>null</code> if the location is not known,
	 * is unavailable, or is not applicable to this scope.
	 * </p>
	 * @return a writable location in the file system or <code>null</code>
	 */
	public IPath getLocation();
}
