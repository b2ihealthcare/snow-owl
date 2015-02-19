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
package org.eclipse.core.runtime.preferences;

import java.io.*;
import java.util.Properties;
import org.eclipse.core.internal.preferences.PrefsMessages;
import org.osgi.service.prefs.BackingStoreException;

/**
 * Abstract class which can be used to help provide an alternate storage mechanism
 * for Eclipse preferences. Clients can over-ride this class and implement the appropriate
 * methods to read/persist preferences.
 * 
 * @since 3.5
 */
public abstract class AbstractPreferenceStorage {

	/**
	 * Return a <code>java.util.Properties</code> object containing the preference
	 * key/value pairs for the preference node with the specified path, and its children. 
	 * <p>
	 * The table keys consist of an optional child node path and separator, followed by
	 * the property key. The table values are the values of the properties.
	 * <pre>
	 *     [childNodePath/]propertyKey=propertyValue
	 * </pre>
	 * </p>
	 * <p>
	 * Note: Whether they are absolute or relative, the paths in the returned Properties
	 * object are always interpreted as relative to the node specified by nodePath.
	 * </p>
	 * @param nodePath the absolute path of the preference node
	 * @return a <code>java.util.Properties</code> object or <code>null</code>
	 * @throws BackingStoreException if there was a problem loading the properties
	 */
	public abstract Properties load(String nodePath) throws BackingStoreException;

	/**
	 * Save the given <code>java.util.Properties</code> object which represents
	 * preference key/value pairs for the preference node represented by the given 
	 * path.
	 * <p>
	 * Clients are reminded that if the given properties object is empty then
	 * the preference node has been removed and they should react
	 * accordingly (e.g. for instance by removing the file on disk)
	 * </p>
	 * 
	 * @param nodePath the absolute path of the preference node
	 * @param properties the <code>java.util.Properties</code> object to store
	 * @throws BackingStoreException if there was a problem saving the properties
	 */
	public abstract void save(String nodePath, Properties properties) throws BackingStoreException;

	/**
	 * Helper method to load a <code>java.util.Properties</code> file from the given 
	 * input stream. The stream will be closed on completion of the operation.
	 * 
	 * @param input the stream to load from
	 * @return the <code>java.util.Properties</code> object loaded from the stream
	 * @throws BackingStoreException if there was a problem loading the file
	 */
	protected Properties loadProperties(InputStream input) throws BackingStoreException {
		Properties result = new Properties();
		try {
			input = new BufferedInputStream(input);
			result.load(input);
		} catch (IOException e) {
			throw new BackingStoreException(PrefsMessages.preferences_loadProblems, e);
		} finally {
			if (input != null)
				try {
					input.close();
				} catch (IOException e) {
					// ignore
				}
		}
		return result;
	}

	/**
	 * Helper method to save the given <code>java.util.Properties</code> object
	 * to the given output stream. The stream will be closed at the end of the operation.
	 * 
	 * @param output the stream to store the object to
	 * @param properties the object to store
	 * @throws BackingStoreException if there was a problem saving the object
	 */
	protected void saveProperties(OutputStream output, Properties properties) throws BackingStoreException {
		try {
			output = new BufferedOutputStream(output);
			properties.store(output, null);
			output.flush();
		} catch (IOException e) {
			throw new BackingStoreException(PrefsMessages.preferences_saveProblems, e);
		} finally {
			if (output != null)
				try {
					output.close();
				} catch (IOException e) {
					// ignore
				}
		}
	}

	/**
	 * Return a string array containing the names of the children for the node
	 * with the given path. If there are no children then an empty array is returned.
	 * One example where this method is commonly called, is at the scope root 
	 * when discovering the initial children.
	 * 
	 * @param nodePath the path for the preference node
	 * @return the array of children names
	 * @throws BackingStoreException if there was a problem retrieving the child names
	 */
	public abstract String[] childrenNames(String nodePath) throws BackingStoreException;

	/**
	 * Callback to inform the client that the preference node with the specified
	 * path has been deleted and the client should react accordingly and make
	 * the appropriate changes to the storage. (e.g. delete the file/information 
	 * associated with that node)
	 * 
	 * @param nodePath the absolute path of the preference node
	 */
	public abstract void removed(String nodePath);
}