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
package org.eclipse.core.internal.preferences;

import org.eclipse.core.runtime.preferences.IExportedPreferences;

/**
 * @since 3.0
 */
public class ExportedPreferences extends EclipsePreferences implements IExportedPreferences {

	private boolean isExportRoot = false;
	private String version;

	public static IExportedPreferences newRoot() {
		return new ExportedPreferences(null, ""); //$NON-NLS-1$
	}

	protected ExportedPreferences(EclipsePreferences parent, String name) {
		super(parent, name);
	}

	/*
	 * @see org.eclipse.core.runtime.preferences.IExportedPreferences#isExportRoot()
	 */
	public boolean isExportRoot() {
		return isExportRoot;
	}

	/*
	 * Internal method called only by the import/export mechanism.
	 */
	public void setExportRoot() {
		isExportRoot = true;
	}

	/*
	 * Internal method called only by the import/export mechanism to
	 * validate bundle versions.
	 */
	public String getVersion() {
		return version;
	}

	/*
	 * Internal method called only by the import/export mechanism to
	 * validate bundle versions.
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	protected EclipsePreferences internalCreate(EclipsePreferences nodeParent, String nodeName, Object context) {
		return new ExportedPreferences(nodeParent, nodeName);
	}

	/*
	 * Return a string representation of this object. To be used for 
	 * debugging purposes only.
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		if (isExportRoot)
			buffer.append("* "); //$NON-NLS-1$
		buffer.append(absolutePath());
		if (version != null)
			buffer.append(" (" + version + ')'); //$NON-NLS-1$
		return buffer.toString();
	}
}
