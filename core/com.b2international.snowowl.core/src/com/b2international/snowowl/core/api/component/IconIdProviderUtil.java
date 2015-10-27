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
package com.b2international.snowowl.core.api.component;

/**
 * Provides static utility methods for extracting an icon identifier from an object of unknown type.
 */
public abstract class IconIdProviderUtil {

	/**
	 * Extracts the icon identifier from the specified object.
	 * 
	 * @param component the object to inspect
	 * @return the icon identifier if {@code component} is an instance of {@link IconIdProvider}, {@code null} otherwise
	 */
	public static <K> K getIconId(final Object component) {
		return getIconId(component, null);
	}

	/**
	 * Extracts the icon identifier from the specified object; the specified default value is used if the object does not
	 * implement {@link IconIdProvider}.
	 * 
	 * @param component the object to inspect
	 * @param defaultIconId the default value to return
	 * @return the icon identifier if {@code component} is an instance of {@link IconIdProvider}, {@code defaultIconId} otherwise
	 */
	@SuppressWarnings("unchecked")
	private static <K> K getIconId(final Object component, final K defaultIconId) {
		if (component instanceof IconIdProvider<?>) { // <K>
			return ((IconIdProvider<K>) component).getIconId();
		} else {
			return defaultIconId;
		}
	}

	private IconIdProviderUtil() {
		throw new UnsupportedOperationException("This class is not supposed to be instantiated.");
	}
}
