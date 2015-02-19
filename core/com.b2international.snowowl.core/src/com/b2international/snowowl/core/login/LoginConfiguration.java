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
package com.b2international.snowowl.core.login;

import org.osgi.service.prefs.PreferencesService;

import com.b2international.snowowl.core.api.preferences.PreferenceBase;

/**
 * @since 1.0
 */
public class LoginConfiguration extends PreferenceBase {
	
	private static final String AUTH_NODE_NAME = "authentication";
	private static final String AUTH_USERNAME = "authentication.username";
	private static final String AUTH_PASSWORD = "authentication.password";
	private static final String AUTH_REMEMBER = "authentication.remember";
	
	public LoginConfiguration(final PreferencesService preferencesService) {
		super(preferencesService, AUTH_NODE_NAME);
	}
	
	public String getUserName() {
		return preferences.get(AUTH_USERNAME, "");
	}
	
	public String getPassword() {
		return preferences.get(AUTH_PASSWORD, "");
	}
	
	public boolean isRemember() {
		return preferences.getBoolean(AUTH_REMEMBER, false);
	}
	
	public void setUserCredential(String userName, String password, boolean remember) {
		setUserName(remember ? userName : "");
		setPassword(remember ? password : "");
		setRemember(remember);
	}
	
	private void setUserName(final String username) {
		preferences.put(AUTH_USERNAME, username);
	}
	
	private void setPassword(final String password) {
		preferences.put(AUTH_PASSWORD, password);
	}
	
	private void setRemember(final boolean remember) {
		preferences.putBoolean(AUTH_REMEMBER, remember);
	}

}