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
package org.eclipse.core.internal.preferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.*;
import org.osgi.service.prefs.PreferencesService;

/**
 * <p>
 * Implements OSGi PreferencesService using the Eclipse preference system.
 * </p>
 * 
 * <p>
 * Note: Eclipse preferences are accessible through the OSGi Preferences API and vice
 *  versa.
 * </p>
 */
public class OSGiPreferencesServiceImpl implements PreferencesService {

	/**
	 * Adaptor that implements OSGi Preferences interface on top of EclipsePreferences.
	 * Creates a "local root" since OSGi preferences have lots of roots but eclipse
	 * only has one.
	 */
	private static final class OSGiLocalRootPreferences implements Preferences {

		//The "local" root of this preference tree (not the real Eclipse root)
		private Preferences root;

		//the node this node is wrappering
		private Preferences wrapped;

		private OSGiLocalRootPreferences(Preferences root) {
			this(root, root);
		}

		private OSGiLocalRootPreferences(Preferences wrapped, Preferences root) {
			this.root = root;
			this.wrapped = wrapped;
		}

		/**
		 * If pathName is absolute make it "absolute" with respect to this root.
		 * If pathName is relative, just return it
		 */
		private String fixPath(String pathName) {
			if (pathName.startsWith("/")) { //$NON-NLS-1$
				if (pathName.equals("/")) { //$NON-NLS-1$
					return root.absolutePath();
				}
				//fix absolute path
				return root.absolutePath().concat(pathName);
			}
			//pass-through relative path
			return pathName;
		}

		/**
		 * Override node(String pathName) to be more strict about forbidden names - 
		 * EclipsePreferences implementation does a best-effort instead of throwing 
		 * {@link IllegalArgumentException}.
		 */
		public Preferences node(String pathName) {
			pathName = fixPath(pathName);

			if ((pathName.length() > 1 && pathName.endsWith("/")) //$NON-NLS-1$
					|| pathName.indexOf("//") != -1) { //$NON-NLS-1$				
				throw new IllegalArgumentException();
			}
			return new OSGiLocalRootPreferences(wrapped.node(pathName), root);
		}

		/**
		 * <p>
		 * Override getByteArray(String key, byte [] defaultValue) to be more strict when
		 * decoding byte values.  EclipsePreferences implementation pads bytes if they are not 4
		 * bytes long, but the OSGi TCK expects this function to return null if the length of 
		 * the byte array is not an even multiple of 4. 
		 * </p>
		 * <p>
		 * Also catches any decoding exceptions and returns the default value instead of 
		 * propagating the exception.
		 * </p>
		 */
		public byte[] getByteArray(String key, byte[] defaultValue) {
			String value = wrapped.get(key, null);
			byte[] byteArray = null;
			if (value != null) {
				byte[] encodedBytes = value.getBytes();
				if (encodedBytes.length % 4 == 0) {
					try {
						byteArray = Base64.decode(encodedBytes);
					} catch (Exception e) {
						//do not raise exception - return defaultValue
					}
				}
			}
			return byteArray == null ? defaultValue : byteArray;
		}

		public Preferences parent() {
			if (wrapped == root) {
				try {
					if (!wrapped.nodeExists("")) { //$NON-NLS-1$
						throw new IllegalStateException();
					}
				} catch (BackingStoreException e) {
					//best effort
				}
				return null;
			}
			return new OSGiLocalRootPreferences(wrapped.parent(), root);
		}

		public boolean nodeExists(String pathName) throws BackingStoreException {
			return wrapped.nodeExists(fixPath(pathName));
		}

		public String absolutePath() {
			if (wrapped == root) {
				return "/"; //$NON-NLS-1$
			}
			return wrapped.absolutePath().substring(root.absolutePath().length(), wrapped.absolutePath().length());
		}

		public String name() {
			if (wrapped == root) {
				return ""; //$NON-NLS-1$
			}
			return wrapped.name();
		}

		//delegate to wrapped preference
		public void put(String key, String value) {
			wrapped.put(key, value);
		}

		public String get(String key, String def) {
			return wrapped.get(key, def);
		}

		public void remove(String key) {
			wrapped.remove(key);
		}

		public void clear() throws BackingStoreException {
			wrapped.clear();
		}

		public void putInt(String key, int value) {
			wrapped.putInt(key, value);
		}

		public int getInt(String key, int def) {
			return wrapped.getInt(key, def);
		}

		public void putLong(String key, long value) {
			wrapped.putLong(key, value);
		}

		public long getLong(String key, long def) {
			return wrapped.getLong(key, def);
		}

		public void putBoolean(String key, boolean value) {
			wrapped.putBoolean(key, value);
		}

		public boolean getBoolean(String key, boolean def) {
			return wrapped.getBoolean(key, def);
		}

		public void putFloat(String key, float value) {
			wrapped.putFloat(key, value);
		}

		public float getFloat(String key, float def) {
			return wrapped.getFloat(key, def);
		}

		public void putDouble(String key, double value) {
			wrapped.putDouble(key, value);
		}

		public double getDouble(String key, double def) {
			return wrapped.getDouble(key, def);
		}

		public void putByteArray(String key, byte[] value) {
			wrapped.putByteArray(key, value);
		}

		public String[] keys() throws BackingStoreException {
			return wrapped.keys();
		}

		public String[] childrenNames() throws BackingStoreException {
			return wrapped.childrenNames();
		}

		public void removeNode() throws BackingStoreException {
			wrapped.removeNode();
		}

		public void flush() throws BackingStoreException {
			wrapped.flush();
		}

		public void sync() throws BackingStoreException {
			wrapped.sync();
		}

	} //end static inner class OSGiLocalRootPreferences

	private IEclipsePreferences bundlePreferences;

	OSGiPreferencesServiceImpl(IEclipsePreferences bundlePreferences) {
		this.bundlePreferences = bundlePreferences;
	}

	public Preferences getSystemPreferences() {
		return new OSGiLocalRootPreferences(bundlePreferences.node("system")); //$NON-NLS-1$
	}

	public Preferences getUserPreferences(String name) {
		return new OSGiLocalRootPreferences(bundlePreferences.node("user/" + name)); //$NON-NLS-1$
	}

	public String[] getUsers() {
		String[] users = null;
		try {
			users = bundlePreferences.node("user").childrenNames(); //$NON-NLS-1$
		} catch (BackingStoreException e) {
			//best effort
		}
		return users == null ? new String[0] : users;
	}

}
