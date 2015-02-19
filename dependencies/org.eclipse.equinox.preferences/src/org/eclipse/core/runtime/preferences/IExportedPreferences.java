/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
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
 * Represents a node in the preference hierarchy which is used in
 * the import/export mechanism.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IExportedPreferences extends IEclipsePreferences {

	/**
	 * Return <code>true</code> if this node was an export root
	 * when the preferences were exported, and <code>false</code>
	 * otherwise.  This information is used during the import to clear
	 * nodes when importing a node's (and its children's) preferences.
	 * 
	 * @return <code>true</code> if this node is an export root
	 * 	and <code>false</code> otherwise
	 */
	public boolean isExportRoot();

}
