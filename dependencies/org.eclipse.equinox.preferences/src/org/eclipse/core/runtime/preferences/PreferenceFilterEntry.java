/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Semion Chichelnitsky (semion@il.ibm.com) - bug 208564
 *******************************************************************************/
package org.eclipse.core.runtime.preferences;

/**
 * Class which represents and preference filter entry to be used during preference
 * import/export (for example).
 * 
 * @since 3.1
 * @see org.eclipse.core.runtime.preferences.IPreferenceFilter
 */
public final class PreferenceFilterEntry {

	private String key;
	private String matchType;

	/**
	 * Constructor for the class. Create a new preference filter entry with the given 
	 * key. The key must <em>not</em> be <code>null</code> or empty. 
	 * 
	 * @param key the name of the preference key
	 */
	public PreferenceFilterEntry(String key) {
		super();
		if (key == null || key.length() == 0)
			throw new IllegalArgumentException();
		this.key = key;
	}

	/**
	 * Constructor for the class. Create a new preference filter entry with the given 
	 * key and match type. The key must <em>not</em> be <code>null</code> or empty.
	 * <p>
	 * Setting matchType to "prefix" treats the key as if it were a regular expression
	 * with an asterisk at the end. If matchType is <code>null</code>, the key must be 
	 * an exact match.
	 * </p> 
	 * @param key the name of the preference key
	 * @param matchType specifies key match type, may be <code>null</null> to indicate
	 * that exact match is required      
	 * @since 3.3       
	 */
	public PreferenceFilterEntry(String key, String matchType) {
		this(key);
		this.matchType = matchType;
	}

	/**
	 * Return the name of the preference key for this filter entry.
	 * It will <em>not</em> return <code>null</code> or the
	 * empty string.
	 * 
	 * @return the name of the preference key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Return the match type specified for this filter. May return <code>null</code>
	 * to indicate that exact match is used.
	 * @return matchType the match type, might be <code>null</code> indicating that
	 * exact match is used
	 * @since 3.3
	 */
	public String getMatchType() {
		return matchType;
	}
}
