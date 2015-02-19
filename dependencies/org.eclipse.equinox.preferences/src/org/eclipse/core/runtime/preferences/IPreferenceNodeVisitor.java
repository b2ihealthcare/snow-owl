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

import org.osgi.service.prefs.BackingStoreException;

/** 
 * This interface is implemented by objects that visit preference nodes.
 * <p> 
 * Usage:
 * <pre>
 * class Visitor implements IPreferenceNodeVisitor {
 *    public boolean visit(IEclipsePreferences node) {
 *       // your code here
 *       return true;
 *    }
 * }
 * IEclipsePreferences root = ...;
 * root.accept(new Visitor());
 * </pre>
 * </p><p>
 * Clients may implement this interface.
 * </p>
 *
 * @see IEclipsePreferences#accept(IPreferenceNodeVisitor)
 * @since 3.0
 */
public interface IPreferenceNodeVisitor {

	/** 
	 * Visits the given preference node.
	 *
	 * @param node the node to visit
	 * @return <code>true</code> if the node's children should
	 *		be visited; <code>false</code> if they should be skipped
	 * @throws BackingStoreException
	 */
	public boolean visit(IEclipsePreferences node) throws BackingStoreException;
}
