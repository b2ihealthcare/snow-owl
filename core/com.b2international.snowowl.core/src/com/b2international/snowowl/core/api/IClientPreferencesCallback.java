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
package com.b2international.snowowl.core.api;

import com.b2international.snowowl.core.config.ClientPreferences;

/**
 * Callback for altering the client side configuration.
 * @see ClientPreferences
 * @see com.b2international.snowowl.ui.composite.ServerAddressComposite
 */
public interface IClientPreferencesCallback {

	/**
	 * Sets the URL history for a specified value.
	 * @param urlHistory the new URL history value.
	 */
	void setUrlHistory(String urlHistory);
	
	/**
	 * Sets the URL in the client.
	 * @param url the specified URL value.
	 */
	void setUrl(String url);

	/**
	 * Forces any changes in the contents of this implementation to get updated.
	 */
	void flush();
	
}