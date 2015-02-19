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

import java.util.*;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.preferences.AbstractPreferenceStorage;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.osgi.util.NLS;
import org.osgi.service.prefs.BackingStoreException;

public class ScopeDescriptor {

	String name;
	AbstractPreferenceStorage storage;
	Set loadedNodes = Collections.synchronizedSet(new HashSet());

	public ScopeDescriptor(AbstractPreferenceStorage storage) {
		super();
		this.storage = storage;
	}

	String getName() {
		return name;
	}

	/*
	 * For now the default behaviour is that we flush/load at the second level.
	 */
	IEclipsePreferences getLoadLevel(IEclipsePreferences node) {
		String path = node.absolutePath();
		int count = EclipsePreferences.getSegmentCount(path);
		// root or scope root
		if (count == 1 || count == 0)
			return null;
		// the load level we want
		if (count == 2)
			return node;
		for (int i = count; i > 2 && node.parent() != null; i--)
			node = (IEclipsePreferences) node.parent();
		return node;
	}

	String[] childrenNames(final String path) throws BackingStoreException {
		if (storage == null)
			return new String[0];
		final String[][] result = new String[1][];
		final BackingStoreException[] bse = new BackingStoreException[1];
		ISafeRunnable code = new ISafeRunnable() {
			public void run() throws Exception {
				result[0] = storage.childrenNames(path);
			}

			public void handleException(Throwable exception) {
				if (exception instanceof BackingStoreException)
					bse[0] = (BackingStoreException) exception;
				else
					bse[0] = new BackingStoreException(NLS.bind(PrefsMessages.childrenNames2, path), exception);
			}
		};
		SafeRunner.run(code);
		if (bse[0] != null)
			throw bse[0];
		return result[0] == null ? new String[0] : result[0];
	}

	Properties load(final String path) throws BackingStoreException {
		if (storage == null)
			return null;
		final Properties[] result = new Properties[1];
		final BackingStoreException[] bse = new BackingStoreException[1];
		ISafeRunnable code = new ISafeRunnable() {
			public void run() throws Exception {
				result[0] = storage.load(path);
			}

			public void handleException(Throwable exception) {
				if (exception instanceof BackingStoreException)
					bse[0] = (BackingStoreException) exception;
				else
					bse[0] = new BackingStoreException(NLS.bind(PrefsMessages.preferences_loadException, path), exception);
			}
		};
		SafeRunner.run(code);
		if (bse[0] != null)
			throw bse[0];
		return result[0] == null ? null : result[0];
	}

	void save(final String path, final Properties properties) throws BackingStoreException {
		if (storage == null)
			return;
		final BackingStoreException[] bse = new BackingStoreException[1];
		ISafeRunnable code = new ISafeRunnable() {
			public void run() throws Exception {
				storage.save(path, properties);
			}

			public void handleException(Throwable exception) {
				if (exception instanceof BackingStoreException)
					bse[0] = (BackingStoreException) exception;
				else
					bse[0] = new BackingStoreException(NLS.bind(PrefsMessages.preferences_saveException, path), exception);
			}
		};
		SafeRunner.run(code);
		if (bse[0] != null)
			throw bse[0];
	}

	boolean isAlreadyLoaded(String node) {
		return loadedNodes.contains(node);
	}

	void loaded(String node) {
		loadedNodes.add(node);
	}

	void removed(final String path) {
		if (storage == null)
			return;
		SafeRunner.run(new ISafeRunnable() {
			public void run() throws Exception {
				storage.removed(path);
			}

			public void handleException(Throwable exception) {
				// ignore here, error will be logged in saferunner
			}
		});
	}
}