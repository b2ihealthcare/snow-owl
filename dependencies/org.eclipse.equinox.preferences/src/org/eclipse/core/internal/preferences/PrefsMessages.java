/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM - Initial API and implementation
 *******************************************************************************/
package org.eclipse.core.internal.preferences;

import java.util.Date;
import org.eclipse.osgi.util.NLS;

// Runtime plugin message catalog
public class PrefsMessages extends NLS {
	/**
	 * The unique identifier constant of this plug-in.
	 */
	public static final String OWNER_NAME = "org.eclipse.equinox.preferences"; //$NON-NLS-1$

	private static final String BUNDLE_NAME = "org.eclipse.core.internal.preferences.messages"; //$NON-NLS-1$

	// Preferences
	public static String preferences_applyProblems;
	public static String preferences_classCastScope;
	public static String preferences_classCastStorage;
	public static String preferences_classCastListener;
	public static String preferences_classCastFilterEntry;

	public static String preferences_contextError;
	public static String preferences_errorWriting;
	public static String preferences_exportProblems;
	public static String preferences_failedDelete;
	public static String preferences_fileNotFound;
	public static String preferences_importProblems;
	public static String preferences_incompatible;
	public static String preferences_invalidExtensionSuperclass;
	public static String preferences_invalidFileFormat;
	public static String preferences_loadException;
	public static String preferences_loadProblems;
	public static String preferences_matching;
	public static String preferences_missingClassAttribute;
	public static String preferences_missingScopeAttribute;
	public static String noRegistry;
	public static String preferences_removedNode;
	public static String preferences_saveException;
	public static String preferences_saveProblems;
	public static String preferences_validate;
	public static String preferences_validationException;
	public static String childrenNames;
	public static String childrenNames2;

	static {
		// load message values from bundle file
		reloadMessages();
	}

	public static void reloadMessages() {
		NLS.initializeMessages(BUNDLE_NAME, PrefsMessages.class);
	}

	/**
	 * Print a debug message to the console. 
	 * Pre-pend the message with the current date and the name of the current thread.
	 */
	public static void message(String message) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(new Date(System.currentTimeMillis()));
		buffer.append(" - ["); //$NON-NLS-1$
		buffer.append(Thread.currentThread().getName());
		buffer.append("] "); //$NON-NLS-1$
		buffer.append(message);
		System.out.println(buffer.toString());
	}
}
