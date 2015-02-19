/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.runtime.preferences;

import java.io.InputStream;
import java.io.OutputStream;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.osgi.service.prefs.Preferences;

/**
 * The preference service provides facilities for dealing with the default scope
 * precedence lookup order, querying the preference store for values using this order,
 * accessing the root of the preference store node hierarchy, and importing/exporting
 * preferences.
 * <p>
 * The default-default preference search look-up order as defined by the platform
 * is: project, instance, configuration, default.
 * </p><p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IPreferencesService {

	/**
	 * Lookup the given key in the specified preference nodes in the given order.
	 * Return the value from the first node the key is found in. If the key is not
	 * defined in any of the given nodes, then return the specified default value.
	 * <p>
	 * Immediately returns the default value if the node list is <code>null</code>.
	 * If any of the individual entries in the node list are <code>null</code> then
	 * skip over them and move on to the next node in the list.
	 * </p>
	 * @param key the preference key
	 * @param defaultValue the default value
	 * @param nodes the list of nodes to search, or <code>null</code>
	 * @return the stored preference value or the specified default value 
	 * @see org.osgi.service.prefs.Preferences
	 */
	public String get(String key, String defaultValue, Preferences[] nodes);

	/**
	 * Return the value stored in the preference store for the given key. 
	 * If the key is not defined then return the specified default value. 
	 * Use the canonical scope lookup order for finding the preference value. 
	 * <p>
	 * The semantics of this method are to calculate the appropriate 
	 * {@link Preferences} nodes in the preference hierarchy to use
	 * and then call the {@link #get(String, String, Preferences[])} 
	 * method. The order of the nodes is calculated by consulting the default 
	 * scope lookup order as set by {@link #setDefaultLookupOrder(String, String, String[])}.
	 * </p><p>
	 * The specified key may either refer to a simple key or be the concatenation of the 
	 * path of a child node and key. If the key contains a slash ("/") character, then a 
	 * double-slash must be used to denote the end of they child path and the beginning 
	 * of the key. Otherwise it is assumed that the key is the last segment of the path.
	 * The following are some examples of keys and their meanings:
	 * <ul>
	 * <li>"a" - look for a value for the property "a"
	 * <li>"//a" - look for a value for the property "a"
	 * <li>"///a" - look for a value for the property "/a"
	 * <li>"//a//b" - look for a value for the property "a//b"
	 * <li>"a/b/c" - look in the child node "a/b" for property "c"
	 * <li>"/a/b/c" - look in the child node "a/b" for property "c"
	 * <li>"/a/b//c" - look in the child node "a/b" for the property "c"
	 * <li>"a/b//c/d" - look in the child node "a/b" for the property "c/d"
	 * <li>"/a/b//c/d" - look in the child node "a/b" for the property "c/d"
	 * <li>"/a/b//c//d" - look in the child node "a/b" for the property "c//d"
	 * </ul>
	 * </p><p>
	 * The scope look-up order is determined by the preference service default 
	 * lookup order, not by the order of the scope contexts that are being passed in.
	 * The context objects are only consulted to help determine which nodes to 
	 * look in, not the order of the nodes.
	 * </p><p>
	 * Callers may specify an array of scope context objects to aid in the 
	 * determination of the correct nodes. For each entry in the lookup 
	 * order, the array of contexts is consulted and if one matching the 
	 * scope exists, then it is used to calculate the node. Otherwise a
	 * default calculation algorithm is used. 
	 * </p><p>
	 * An example of a qualifier for an Eclipse 2.1 preference is the
	 * plug-in identifier. (e.g. "org.eclipse.core.resources" for "description.autobuild")
	 * </p>
	 * @param qualifier a namespace qualifier for the preference
	 * @param key the name of the preference (optionally including its path)
	 * @param defaultValue the value to use if the preference is not defined
	 * @param contexts optional context objects to help scopes determine which nodes to search, or <code>null</code>
	 * @return the value of the preference or the given default value
	 * @see IScopeContext
	 * @see #get(java.lang.String, java.lang.String, org.osgi.service.prefs.Preferences[])
	 * @see #getLookupOrder(java.lang.String, java.lang.String)
	 * @see #getDefaultLookupOrder(java.lang.String, java.lang.String)
	 */
	public boolean getBoolean(String qualifier, String key, boolean defaultValue, IScopeContext[] contexts);

	/**
	 * Return the value stored in the preference store for the given key. 
	 * If the key is not defined then return the specified default value. 
	 * Use the canonical scope lookup order for finding the preference value. 
	 * <p>
	 * The semantics of this method are to calculate the appropriate 
	 * {@link Preferences} nodes in the preference hierarchy to use
	 * and then call the {@link #get(String, String, Preferences[])} 
	 * method. The order of the nodes is calculated by consulting the default 
	 * scope lookup order as set by {@link #setDefaultLookupOrder(String, String, String[])}.
	 * </p><p>
	 * The specified key may either refer to a simple key or be the concatenation of the 
	 * path of a child node and key. If the key contains a slash ("/") character, then a 
	 * double-slash must be used to denote the end of they child path and the beginning 
	 * of the key. Otherwise it is assumed that the key is the last segment of the path.
	 * The following are some examples of keys and their meanings:
	 * <ul>
	 * <li>"a" - look for a value for the property "a"
	 * <li>"//a" - look for a value for the property "a"
	 * <li>"///a" - look for a value for the property "/a"
	 * <li>"//a//b" - look for a value for the property "a//b"
	 * <li>"a/b/c" - look in the child node "a/b" for property "c"
	 * <li>"/a/b/c" - look in the child node "a/b" for property "c"
	 * <li>"/a/b//c" - look in the child node "a/b" for the property "c"
	 * <li>"a/b//c/d" - look in the child node "a/b" for the property "c/d"
	 * <li>"/a/b//c/d" - look in the child node "a/b" for the property "c/d"
	 * <li>"/a/b//c//d" - look in the child node "a/b" for the property "c//d"
	 * </ul>
	 * </p><p>
	 * The scope look-up order is determined by the preference service default 
	 * lookup order, not by the order of the scope contexts that are being passed in.
	 * The context objects are only consulted to help determine which nodes to 
	 * look in, not the order of the nodes.
	 * </p><p>
	 * Callers may specify an array of scope context objects to aid in the 
	 * determination of the correct nodes. For each entry in the lookup 
	 * order, the array of contexts is consulted and if one matching the 
	 * scope exists, then it is used to calculate the node. Otherwise a
	 * default calculation algorithm is used. 
	 * </p><p>
	 * An example of a qualifier for an Eclipse 2.1 preference is the
	 * plug-in identifier. (e.g. "org.eclipse.core.resources" for "description.autobuild")
	 * </p>
	 * @param qualifier a namespace qualifier for the preference
	 * @param key the name of the preference (optionally including its path)
	 * @param defaultValue the value to use if the preference is not defined
	 * @param contexts optional context objects to help scopes determine which nodes to search, or <code>null</code>
	 * @return the value of the preference or the given default value
	 * @see IScopeContext
	 * @see #get(java.lang.String, java.lang.String, org.osgi.service.prefs.Preferences[])
	 * @see #getLookupOrder(java.lang.String, java.lang.String)
	 * @see #getDefaultLookupOrder(java.lang.String, java.lang.String)
	 */
	public byte[] getByteArray(String qualifier, String key, byte[] defaultValue, IScopeContext[] contexts);

	/**
	 * Return the value stored in the preference store for the given key. 
	 * If the key is not defined then return the specified default value. 
	 * Use the canonical scope lookup order for finding the preference value. 
	 * <p>
	 * The semantics of this method are to calculate the appropriate 
	 * {@link Preferences} nodes in the preference hierarchy to use
	 * and then call the {@link #get(String, String, Preferences[])} 
	 * method. The order of the nodes is calculated by consulting the default 
	 * scope lookup order as set by {@link #setDefaultLookupOrder(String, String, String[])}.
	 * </p><p>
	 * The specified key may either refer to a simple key or be the concatenation of the 
	 * path of a child node and key. If the key contains a slash ("/") character, then a 
	 * double-slash must be used to denote the end of they child path and the beginning 
	 * of the key. Otherwise it is assumed that the key is the last segment of the path.
	 * The following are some examples of keys and their meanings:
	 * <ul>
	 * <li>"a" - look for a value for the property "a"
	 * <li>"//a" - look for a value for the property "a"
	 * <li>"///a" - look for a value for the property "/a"
	 * <li>"//a//b" - look for a value for the property "a//b"
	 * <li>"a/b/c" - look in the child node "a/b" for property "c"
	 * <li>"/a/b/c" - look in the child node "a/b" for property "c"
	 * <li>"/a/b//c" - look in the child node "a/b" for the property "c"
	 * <li>"a/b//c/d" - look in the child node "a/b" for the property "c/d"
	 * <li>"/a/b//c/d" - look in the child node "a/b" for the property "c/d"
	 * <li>"/a/b//c//d" - look in the child node "a/b" for the property "c//d"
	 * </ul>
	 * </p><p>
	 * The scope look-up order is determined by the preference service default 
	 * lookup order, not by the order of the scope contexts that are being passed in.
	 * The context objects are only consulted to help determine which nodes to 
	 * look in, not the order of the nodes.
	 * </p><p>
	 * Callers may specify an array of scope context objects to aid in the 
	 * determination of the correct nodes. For each entry in the lookup 
	 * order, the array of contexts is consulted and if one matching the 
	 * scope exists, then it is used to calculate the node. Otherwise a
	 * default calculation algorithm is used. 
	 * </p><p>
	 * An example of a qualifier for an Eclipse 2.1 preference is the
	 * plug-in identifier. (e.g. "org.eclipse.core.resources" for "description.autobuild")
	 * </p>
	 * @param qualifier a namespace qualifier for the preference
	 * @param key the name of the preference (optionally including its path)
	 * @param defaultValue the value to use if the preference is not defined
	 * @param contexts optional context objects to help scopes determine which nodes to search, or <code>null</code>
	 * @return the value of the preference or the given default value
	 * @see IScopeContext
	 * @see #get(java.lang.String, java.lang.String, org.osgi.service.prefs.Preferences[])
	 * @see #getLookupOrder(java.lang.String, java.lang.String)
	 * @see #getDefaultLookupOrder(java.lang.String, java.lang.String)
	 */
	public double getDouble(String qualifier, String key, double defaultValue, IScopeContext[] contexts);

	/**
	 * Return the value stored in the preference store for the given key. 
	 * If the key is not defined then return the specified default value. 
	 * Use the canonical scope lookup order for finding the preference value. 
	 * <p>
	 * The semantics of this method are to calculate the appropriate 
	 * {@link Preferences} nodes in the preference hierarchy to use
	 * and then call the {@link #get(String, String, Preferences[])} 
	 * method. The order of the nodes is calculated by consulting the default 
	 * scope lookup order as set by {@link #setDefaultLookupOrder(String, String, String[])}.
	 * </p><p>
	 * The specified key may either refer to a simple key or be the concatenation of the 
	 * path of a child node and key. If the key contains a slash ("/") character, then a 
	 * double-slash must be used to denote the end of they child path and the beginning 
	 * of the key. Otherwise it is assumed that the key is the last segment of the path.
	 * The following are some examples of keys and their meanings:
	 * <ul>
	 * <li>"a" - look for a value for the property "a"
	 * <li>"//a" - look for a value for the property "a"
	 * <li>"///a" - look for a value for the property "/a"
	 * <li>"//a//b" - look for a value for the property "a//b"
	 * <li>"a/b/c" - look in the child node "a/b" for property "c"
	 * <li>"/a/b/c" - look in the child node "a/b" for property "c"
	 * <li>"/a/b//c" - look in the child node "a/b" for the property "c"
	 * <li>"a/b//c/d" - look in the child node "a/b" for the property "c/d"
	 * <li>"/a/b//c/d" - look in the child node "a/b" for the property "c/d"
	 * <li>"/a/b//c//d" - look in the child node "a/b" for the property "c//d"
	 * </ul>
	 * </p><p>
	 * The scope look-up order is determined by the preference service default 
	 * lookup order, not by the order of the scope contexts that are being passed in.
	 * The context objects are only consulted to help determine which nodes to 
	 * look in, not the order of the nodes.
	 * </p><p>
	 * Callers may specify an array of scope context objects to aid in the 
	 * determination of the correct nodes. For each entry in the lookup 
	 * order, the array of contexts is consulted and if one matching the 
	 * scope exists, then it is used to calculate the node. Otherwise a
	 * default calculation algorithm is used. 
	 * </p><p>
	 * An example of a qualifier for an Eclipse 2.1 preference is the
	 * plug-in identifier. (e.g. "org.eclipse.core.resources" for "description.autobuild")
	 * </p>
	 * @param qualifier a namespace qualifier for the preference
	 * @param key the name of the preference (optionally including its path)
	 * @param defaultValue the value to use if the preference is not defined
	 * @param contexts optional context objects to help scopes determine which nodes to search, or <code>null</code>
	 * @return the value of the preference or the given default value
	 * @see IScopeContext
	 * @see #get(java.lang.String, java.lang.String, org.osgi.service.prefs.Preferences[])
	 * @see #getLookupOrder(java.lang.String, java.lang.String)
	 * @see #getDefaultLookupOrder(java.lang.String, java.lang.String)
	 */
	public float getFloat(String qualifier, String key, float defaultValue, IScopeContext[] contexts);

	/**
	 * Return the value stored in the preference store for the given key. 
	 * If the key is not defined then return the specified default value. 
	 * Use the canonical scope lookup order for finding the preference value. 
	 * <p>
	 * The semantics of this method are to calculate the appropriate 
	 * {@link Preferences} nodes in the preference hierarchy to use
	 * and then call the {@link #get(String, String, Preferences[])} 
	 * method. The order of the nodes is calculated by consulting the default 
	 * scope lookup order as set by {@link #setDefaultLookupOrder(String, String, String[])}.
	 * </p><p>
	 * The specified key may either refer to a simple key or be the concatenation of the 
	 * path of a child node and key. If the key contains a slash ("/") character, then a 
	 * double-slash must be used to denote the end of they child path and the beginning 
	 * of the key. Otherwise it is assumed that the key is the last segment of the path.
	 * The following are some examples of keys and their meanings:
	 * <ul>
	 * <li>"a" - look for a value for the property "a"
	 * <li>"//a" - look for a value for the property "a"
	 * <li>"///a" - look for a value for the property "/a"
	 * <li>"//a//b" - look for a value for the property "a//b"
	 * <li>"a/b/c" - look in the child node "a/b" for property "c"
	 * <li>"/a/b/c" - look in the child node "a/b" for property "c"
	 * <li>"/a/b//c" - look in the child node "a/b" for the property "c"
	 * <li>"a/b//c/d" - look in the child node "a/b" for the property "c/d"
	 * <li>"/a/b//c/d" - look in the child node "a/b" for the property "c/d"
	 * <li>"/a/b//c//d" - look in the child node "a/b" for the property "c//d"
	 * </ul>
	 * </p><p>
	 * The scope look-up order is determined by the preference service default 
	 * lookup order, not by the order of the scope contexts that are being passed in.
	 * The context objects are only consulted to help determine which nodes to 
	 * look in, not the order of the nodes.
	 * </p><p>
	 * Callers may specify an array of scope context objects to aid in the 
	 * determination of the correct nodes. For each entry in the lookup 
	 * order, the array of contexts is consulted and if one matching the 
	 * scope exists, then it is used to calculate the node. Otherwise a
	 * default calculation algorithm is used. 
	 * </p><p>
	 * An example of a qualifier for an Eclipse 2.1 preference is the
	 * plug-in identifier. (e.g. "org.eclipse.core.resources" for "description.autobuild")
	 * </p>
	 * @param qualifier a namespace qualifier for the preference
	 * @param key the name of the preference (optionally including its path)
	 * @param defaultValue the value to use if the preference is not defined
	 * @param contexts optional context objects to help scopes determine which nodes to search, or <code>null</code>
	 * @return the value of the preference or the given default value
	 * @see IScopeContext
	 * @see #get(java.lang.String, java.lang.String, org.osgi.service.prefs.Preferences[])
	 * @see #getLookupOrder(java.lang.String, java.lang.String)
	 * @see #getDefaultLookupOrder(java.lang.String, java.lang.String)
	 */
	public int getInt(String qualifier, String key, int defaultValue, IScopeContext[] contexts);

	/**
	 * Return the value stored in the preference store for the given key. 
	 * If the key is not defined then return the specified default value. 
	 * Use the canonical scope lookup order for finding the preference value. 
	 * <p>
	 * The semantics of this method are to calculate the appropriate 
	 * {@link Preferences} nodes in the preference hierarchy to use
	 * and then call the {@link #get(String, String, Preferences[])} 
	 * method. The order of the nodes is calculated by consulting the default 
	 * scope lookup order as set by {@link #setDefaultLookupOrder(String, String, String[])}.
	 * </p><p>
	 * The specified key may either refer to a simple key or be the concatenation of the 
	 * path of a child node and key. If the key contains a slash ("/") character, then a 
	 * double-slash must be used to denote the end of they child path and the beginning 
	 * of the key. Otherwise it is assumed that the key is the last segment of the path.
	 * The following are some examples of keys and their meanings:
	 * <ul>
	 * <li>"a" - look for a value for the property "a"
	 * <li>"//a" - look for a value for the property "a"
	 * <li>"///a" - look for a value for the property "/a"
	 * <li>"//a//b" - look for a value for the property "a//b"
	 * <li>"a/b/c" - look in the child node "a/b" for property "c"
	 * <li>"/a/b/c" - look in the child node "a/b" for property "c"
	 * <li>"/a/b//c" - look in the child node "a/b" for the property "c"
	 * <li>"a/b//c/d" - look in the child node "a/b" for the property "c/d"
	 * <li>"/a/b//c/d" - look in the child node "a/b" for the property "c/d"
	 * <li>"/a/b//c//d" - look in the child node "a/b" for the property "c//d"
	 * </ul>
	 * </p><p>
	 * The scope look-up order is determined by the preference service default 
	 * lookup order, not by the order of the scope contexts that are being passed in.
	 * The context objects are only consulted to help determine which nodes to 
	 * look in, not the order of the nodes.
	 * </p><p>
	 * Callers may specify an array of scope context objects to aid in the 
	 * determination of the correct nodes. For each entry in the lookup 
	 * order, the array of contexts is consulted and if one matching the 
	 * scope exists, then it is used to calculate the node. Otherwise a
	 * default calculation algorithm is used. 
	 * </p><p>
	 * An example of a qualifier for an Eclipse 2.1 preference is the
	 * plug-in identifier. (e.g. "org.eclipse.core.resources" for "description.autobuild")
	 * </p>
	 * @param qualifier a namespace qualifier for the preference
	 * @param key the name of the preference (optionally including its path)
	 * @param defaultValue the value to use if the preference is not defined
	 * @param contexts optional context objects to help scopes determine which nodes to search, or <code>null</code>
	 * @return the value of the preference or the given default value
	 * @see IScopeContext
	 * @see #get(java.lang.String, java.lang.String, org.osgi.service.prefs.Preferences[])
	 * @see #getLookupOrder(java.lang.String, java.lang.String)
	 * @see #getDefaultLookupOrder(java.lang.String, java.lang.String)
	 */
	public long getLong(String qualifier, String key, long defaultValue, IScopeContext[] contexts);

	/**
	 * Return the value stored in the preference store for the given key. 
	 * If the key is not defined then return the specified default value. 
	 * Use the canonical scope lookup order for finding the preference value. 
	 * <p>
	 * The semantics of this method are to calculate the appropriate 
	 * {@link Preferences} nodes in the preference hierarchy to use
	 * and then call the {@link #get(String, String, Preferences[])} 
	 * method. The order of the nodes is calculated by consulting the default 
	 * scope lookup order as set by {@link #setDefaultLookupOrder(String, String, String[])}.
	 * </p><p>
	 * The specified key may either refer to a simple key or be the concatenation of the 
	 * path of a child node and key. If the key contains a slash ("/") character, then a 
	 * double-slash must be used to denote the end of they child path and the beginning 
	 * of the key. Otherwise it is assumed that the key is the last segment of the path.
	 * The following are some examples of keys and their meanings:
	 * <ul>
	 * <li>"a" - look for a value for the property "a"
	 * <li>"//a" - look for a value for the property "a"
	 * <li>"///a" - look for a value for the property "/a"
	 * <li>"//a//b" - look for a value for the property "a//b"
	 * <li>"a/b/c" - look in the child node "a/b" for property "c"
	 * <li>"/a/b/c" - look in the child node "a/b" for property "c"
	 * <li>"/a/b//c" - look in the child node "a/b" for the property "c"
	 * <li>"a/b//c/d" - look in the child node "a/b" for the property "c/d"
	 * <li>"/a/b//c/d" - look in the child node "a/b" for the property "c/d"
	 * <li>"/a/b//c//d" - look in the child node "a/b" for the property "c//d"
	 * </ul>
	 * </p><p>
	 * The scope look-up order is determined by the preference service default 
	 * lookup order, not by the order of the scope contexts that are being passed in.
	 * The context objects are only consulted to help determine which nodes to 
	 * look in, not the order of the nodes.
	 * </p><p>
	 * Callers may specify an array of scope context objects to aid in the 
	 * determination of the correct nodes. For each entry in the lookup 
	 * order, the array of contexts is consulted and if one matching the 
	 * scope exists, then it is used to calculate the node. Otherwise a
	 * default calculation algorithm is used. 
	 * </p><p>
	 * An example of a qualifier for an Eclipse 2.1 preference is the
	 * plug-in identifier. (e.g. "org.eclipse.core.resources" for "description.autobuild")
	 * </p>
	 * @param qualifier a namespace qualifier for the preference
	 * @param key the name of the preference (optionally including its path)
	 * @param defaultValue the value to use if the preference is not defined
	 * @param contexts optional context objects to help scopes determine which nodes to search, or <code>null</code>
	 * @return the value of the preference or the given default value
	 * @see IScopeContext
	 * @see #get(java.lang.String, java.lang.String, org.osgi.service.prefs.Preferences[])
	 * @see #getLookupOrder(java.lang.String, java.lang.String)
	 * @see #getDefaultLookupOrder(java.lang.String, java.lang.String)
	 */
	public String getString(String qualifier, String key, String defaultValue, IScopeContext[] contexts);

	/**
	 * Return the root node of the Eclipse preference hierarchy.
	 * 
	 * @return the root of the hierarchy
	 */
	public IEclipsePreferences getRootNode();

	/**
	 * Exports all preferences for the given preference node and all its children to the specified
	 * output stream. It is the responsibility of the client to close the given output stream.
	 * <p>
	 * If the given export list is <code>null</code> then all preferences for all sub-nodes
	 * of the given node are exported to the given stream. Otherwise the export list is
	 * consulted before exporting each preference value. If there is a string match then
	 * the preference is not exported. The exclusion can also occur at a per-node level. 
	 * Wild cards are <em>not</em> accepted in the excludes list as a basic String compare
	 * is done. The basic algorithm is similar to the following:
	 * <pre>
	 * String fullPath = node.absolutePath() + '/' + key;
	 * if (!fullPath.startsWith(excludesList[i]))
	 * 	// export preference
	 * </pre>
	 * </p>
	 * <p>
	 * The values stored in the resulting stream are suitable for later being read by the
	 * by {@link #importPreferences(InputStream)} or {@link #readPreferences(InputStream)} methods.
	 * </p>
	 * @param node the node to treat as the root of the export
	 * @param output the stream to write to
	 * @param excludesList a list of path prefixes to exclude from the export, or <code>null</code>
	 * @return a status object describing success or detailing failure reasons
	 * @throws CoreException if there was a problem exporting the preferences
	 * @throws IllegalArgumentException if the node or stream is <code>null</code>
	 * @see #importPreferences(java.io.InputStream)
	 * @see #readPreferences(InputStream)
	 */
	public IStatus exportPreferences(IEclipsePreferences node, OutputStream output, String[] excludesList) throws CoreException;

	/**
	 * Loads preferences from the given file and stores them in the preferences store.
	 * Existing values are over-ridden by those from the stream. The stream must not be
	 * <code>null</code> and is closed upon return from this method.
	 * <p>
	 * This file must have been written by the 
	 * {@link #exportPreferences(IEclipsePreferences, OutputStream, String[])} 
	 * method.
	 * </p>
	 * <p>
	 * This method is equivalent to calling <code>applyPreferences(readPreferences(input));</code>.
	 * </p>
	 * @param input the stream to load the preferences from
	 * @return a status object describing success or detailing failure reasons
	 * @throws CoreException if there are problems importing the preferences
	 * @throws IllegalArgumentException if the stream is <code>null</code>
	 * @see #exportPreferences(IEclipsePreferences, OutputStream, String[])
	 */
	public IStatus importPreferences(InputStream input) throws CoreException;

	/**
	 * Take the given preference tree and apply it to the Eclipse
	 * global preference hierarchy. If a node is an export root, then 
	 * treat the operation for that node as an overwrite rather than a merge. 
	 * That is, remove the node from the global tree before adding any preferences
	 * contained in it or its children.    
	 * <p> 
	 * The given preferences object must not be <code>null</code>.
	 * </p>
	 * <p>
	 * Before the tree is applied to the global preference tree,
	 * the registered <code>PreferenceModifyListener</code> objects
	 * are called and given the opportunity to modify the tree.
	 * </p>
	 * 
	 * @param preferences the preferences to apply globally
	 * @return status object indicating success or failure
	 * @throws IllegalArgumentException if the preferences are <code>null</code>
	 * @throws CoreException if there are problems applying the preferences
	 * @see PreferenceModifyListener
	 */
	public IStatus applyPreferences(IExportedPreferences preferences) throws CoreException;

	/**
	 * Read from the given input stream and create a node hierarchy
	 * representing the preferences and their values. The given input stream
	 * must not be <code>null</code>. The result of this function is suitable
	 * for passing as an argument to {@link #applyPreferences(IExportedPreferences)}.
	 * <p>
	 * It is assumed the contents of the input stream have been written by
	 * {@link #exportPreferences(IEclipsePreferences, OutputStream, String[])}.
	 * </p>
	 * @param input the input stream to read from
	 * @return the node hierarchy representing the stream contents
	 * @throws IllegalArgumentException if the given stream is null
	 * @throws CoreException if there are problems reading the preferences
	 * @see #exportPreferences(IEclipsePreferences, OutputStream, String[])
	 * @see #applyPreferences(IExportedPreferences)
	 */
	public IExportedPreferences readPreferences(InputStream input) throws CoreException;

	/**
	 * Return an array with the default lookup order for the preference keyed by the given
	 * qualifier and simple name. Return <code>null</code> if no default has been set.
	 * <p>
	 * The lookup order returned is based on an exact match to the specified qualifier
	 * and simple name. For instance, if the given key is non-<code>null</code> and
	 * no default lookup order is found, the default lookup order for the qualifier (and a
	 * <code>null</code> key) is <em>NOT</em> returned. Clients should call
	 * {@link #getLookupOrder(String, String)} if they desire this behavior.
	 * </p>
	 * @param qualifier the namespace qualifier for the preference
	 * @param key the preference name or <code>null</code>
	 * @return the scope order or <code>null</code>
	 * @see #setDefaultLookupOrder(String, String, String[])
	 * @see #getLookupOrder(String, String)
	 */
	public String[] getDefaultLookupOrder(String qualifier, String key);

	/**
	 * Return an array with the lookup order for the preference keyed by the given
	 * qualifier and simple name. 
	 * <p>
	 * First do an exact match lookup with the given qualifier and simple name. If a match
	 * is found then return it. Otherwise if the key is non-<code>null</code> then
	 * do a lookup based on only the qualifier and return the set value. 
	 * Return the default-default order as defined by the platform if no order has been set.
	 * </p>
	 * @param qualifier the namespace qualifier for the preference
	 * @param key the preference name or <code>null</code>
	 * @return the scope order 
	 * @throws IllegalArgumentException if the qualifier is <code>null</code>
	 * @see #getDefaultLookupOrder(String, String)
	 * @see #setDefaultLookupOrder(String, String, String[])
	 */
	public String[] getLookupOrder(String qualifier, String key);

	/**
	 * Set the default scope lookup order for the preference keyed by the given
	 * qualifier and simple name. If the given order is <code>null</code> then the set
	 * ordering (if it exists) is removed.
	 * <p>
	 * If the given simple name is <code>null</code> then set the given lookup
	 * order to be used for all keys with the given qualifier.
	 * </p><p>
	 * Note that the default lookup order is not persisted across platform invocations. 
	 * </p>
	 * @param qualifier the namespace qualifier for the preference
	 * @param key the preference name or <code>null</code>
	 * @param order the lookup order or <code>null</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * <li>if the qualifier is <code>null</code></li>
	 * <li>if an entry in the order array is <code>null</code> (the array itself is 
	 * allowed to be <code>null</code></li>
	 * </ul>
	 * @see #getDefaultLookupOrder(String, String)
	 */
	public void setDefaultLookupOrder(String qualifier, String key, String[] order);

	/**
	 * Export the preference tree rooted at the given node, to the specified output
	 * stream. Apply the given list of preference filters, only exporting
	 * preference node and keys which are applicable to at least one filter in the list.
	 * <p>
	 * The given node and output stream must not be <code>null</code>.
	 * If the list of filters is <code>null</code> or empty then do nothing.
	 * </p>
	 * <p>
	 * It is the responsibility of the client to close the given output stream.
	 * </p>
	 * 
	 * @param node the tree to export
	 * @param filters the list of filters to export
	 * @param output the stream to export to
	 * @throws CoreException
	 * @see #exportPreferences(IEclipsePreferences, OutputStream, String[])
	 * @see #readPreferences(InputStream)
	 * @see #applyPreferences(IEclipsePreferences, IPreferenceFilter[])
	 * @see #applyPreferences(IExportedPreferences)
	 * @see IPreferenceFilter
	 * @since 3.1
	 */
	public void exportPreferences(IEclipsePreferences node, IPreferenceFilter[] filters, OutputStream output) throws CoreException;

	/**
	 * Return a list of filters which match the given tree and is a subset of the given
	 * filter list. If the specified list of filters is <code>null</code>, empty, or there 
	 * are no matches, then return an empty list.
	 * 
	 * @param node the tree to match against
	 * @param filters the list of filters to match against
	 * @return the array of matching transfers
	 * @throws CoreException
	 * @see IPreferenceFilter
	 * @since 3.1
	 */
	public IPreferenceFilter[] matches(IEclipsePreferences node, IPreferenceFilter[] filters) throws CoreException;

	/**
	 * Apply the preference tree rooted at the given node, to the system's preference tree.
	 * The list of preference filters will act as a filter and only preferences in the tree which
	 * apply to at least one filter in the list, will be applied.
	 * <p>
	 * If the list of filters is <code>null</code> or empty then do nothing.
	 * </p>
	 * <p>
	 * Before the tree is applied to the global preference tree,
	 * the registered <code>PreferenceModifyListener</code> objects
	 * are called and given the opportunity to modify the tree.
	 * </p>
	 * 
	 * @param node the tree to consider applying
	 * @param filters the filters to use
	 * @throws CoreException
	 * @see #applyPreferences(IExportedPreferences)
	 * @see #readPreferences(InputStream)
	 * @see IPreferenceFilter
	 * @see PreferenceModifyListener
	 * @since 3.1
	 */
	public void applyPreferences(IEclipsePreferences node, IPreferenceFilter[] filters) throws CoreException;
}
