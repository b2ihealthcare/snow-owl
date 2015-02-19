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

/**
 * Clients contributing a scope to the Eclipse preference system must 
 * implement this interface to aid Eclipse in creating a new node for the
 * hierarchy.
 * <p>
 * Clients may implement this interface.
 * </p>
 * @since 3.0
 */
public interface IScope {

	/**
	 * Create and return a new preference node with the given parent and name.
	 * Must not return <code>null</code>. Clients are able to create a new node
	 * in memory or load the node's contents from the backing store. Neither the
	 * parent or name arguments should be <code>null</code>.
	 * <p>
	 * Implementors should note that the node might not have been added to the
	 * child list of the parent yet, and therefore might not be able to be referenced
	 * through navigation from the root node. 
	 * </p>
	 * @param parent the node's parent
	 * @param name the name of the node
	 * @return the new node
	 */
	public IEclipsePreferences create(IEclipsePreferences parent, String name);
}
