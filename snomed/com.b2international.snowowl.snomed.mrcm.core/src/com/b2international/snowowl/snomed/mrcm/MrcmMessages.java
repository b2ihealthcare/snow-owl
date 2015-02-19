/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.snowowl.snomed.mrcm;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Class to get NLSed messages for the Snowowl product. The messages are read
 * from the same named .properties file that need to reside in the same
 * directory.
 * 
 */
public final class MrcmMessages {

	private static final String BUNDLE_NAME = MrcmMessages.class.getName();

	private static ResourceBundle resourceBundle;

	/**
	 * Returns the default resource bundle.
	 * @return
	 */
	public static ResourceBundle getResourceBundle() {
		try {
			if (resourceBundle == null)
				resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		return resourceBundle;
	}

	/**
	 * Returns the locale specific resource bundle.
	 * @param locale
	 * @return
	 */
	public static ResourceBundle getResourceBundle(Locale locale) {
		try {
			if (resourceBundle == null)
				resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		return resourceBundle;
	}

	/**
	 * Returns the message for the key and default locale.
	 * @param key
	 * @return
	 */
	public static String getString(String key) {
		resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault());
		try {
			return resourceBundle.getString(key);
		} catch (MissingResourceException e) {
			return "!" + key + "!";
		}
	}

	/**
	 * Returns the message for the key and locale.
	 * @param key
	 * @param locale
	 * @return
	 */
	public static String getString(String key, Locale locale) {
		resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
		try {
			return resourceBundle.getString(key);
		} catch (MissingResourceException e) {
			return "!" + key + "!";
		}
	}
	 
}