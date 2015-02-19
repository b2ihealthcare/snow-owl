/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
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
 * This class provides a hook into the preference service before particular operations
 * on the global preference tree. Preference modify listeners are registered with 
 * the preference service via XML and the <code>org.eclipse.core.runtime.preferences</code>
 * extension point.
 * <p>
 * Clients may subclass this type.
 * </p>
 * 
 * @since 3.1
 */
public abstract class PreferenceModifyListener {

	/**
	 * Clients are given the opportunity to modify the given tree before it is applied 
	 * to the global preference tree. Clients should return the tree which should be
	 * applied globally. The tree passed in will not be <code>null</code> and clients
	 * <em>must not</em> return a <code>null</code> tree.
	 * <p>
	 * This method is called by the preference service from within calls to 
	 * {@link IPreferencesService#applyPreferences(IExportedPreferences)} or
	 * {@link IPreferencesService#applyPreferences(IEclipsePreferences, IPreferenceFilter[])}.
	 * </p>
	 * <p>
	 * A typical action for clients to perform would be to intercept the incoming preference tree,
	 * migrate old preference values to new ones, and then return the new tree.
	 * </p>
	 * 
	 * @param node the tree to modify
	 * @return the tree to apply to the global preferences
	 */
	public IEclipsePreferences preApply(IEclipsePreferences node) {
		// default implementation makes no modifications
		return node;
	}

}
