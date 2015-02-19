/*******************************************************************************
 * Copyright (c) 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.internal.preferences;

import java.util.Properties;
import org.eclipse.core.runtime.IPath;
import org.osgi.service.prefs.BackingStoreException;

public class TestHelper {

	public static Properties convertToProperties(EclipsePreferences node, String prefix) throws BackingStoreException {
		return node.convertToProperties(new Properties(), prefix);
	}

	public static IPath getInstanceBaseLocation() {
		return InstancePreferences.getBaseLocation();
	}
}
