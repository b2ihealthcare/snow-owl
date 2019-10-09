/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.client;

import org.osgi.service.prefs.PreferencesService;

import com.b2international.snowowl.core.api.preferences.PreferenceBase;

/**
 * Configuration class for clients with information about the server connection.
 */
public class ClientPreferences extends PreferenceBase {
	
	private static final String EMPTY_STRING = "";

	private final static String NODE_NAME = "config.client";
	
	public static final String KEY_SERVER_URL = "server.url";
	public static final String KEY_SERVER_URL_HISTORY = "server.url.history";
	
	/**
	 * Creates a new {@link ClientPreferences} instance using the specified {@link PreferencesService}. Preference
	 * keys will be persisted in a subnode of the {@link PreferencesService#getSystemPreferences() root system node}.
	 * 
	 * @param preferencesService the preferences service to use (may not be {@code null}).
	 */
	public ClientPreferences(final PreferencesService preferencesService) {
		super(preferencesService, NODE_NAME);
	}

	/**
	 * @return a "|" character-separated list of recently used repository URLs.
	 */
	public String getServerUrlHistory() {
		return preferences.get(KEY_SERVER_URL_HISTORY, EMPTY_STRING);
	}
	
	public void setServerUrlHistory(final String urlHistory) {
		put(KEY_SERVER_URL_HISTORY, urlHistory);
	}
	
	/**
	 * @return the most recently used repository URL, or {@code null} if the client is in embedded mode, or the value
	 * was not set for some other reason.
	 */
	public String getServerUrl() {
		return preferences.get(KEY_SERVER_URL, null);
	}
	
	public void setServerUrl(final String url) {
		put(KEY_SERVER_URL, url);
	}
	
	/**
	 * @return {@code true} if the client is in embedded mode, {@code false} otherwise. Equivalent to checking whether
	 * {@link #getServerUrl()} returns {@code null}.
	 */
	public boolean isClientEmbedded() {
		return getServerUrl() == null;
	}
	
	public void setUrlHistory(final String urlHistory) {
		setServerUrlHistory(urlHistory);
	}

	public void setUrl(final String url) {
		setServerUrl(url);
	}
	
	protected void put(final String key, final String value) {
		if(value == null) {
			preferences.remove(key);
		} else {
			preferences.put(key, value);
		}
	}
}