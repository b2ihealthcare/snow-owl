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
package com.b2international.snowowl.core.api.preferences;

import static com.google.common.base.Preconditions.checkNotNull;

import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.osgi.service.prefs.PreferencesService;

/**
 * Base class for type-safe preferences. Subclass from this class if you want to
 * store user-specific preferences (configurations).
 * 
 */
public abstract class PreferenceBase {
	
	protected final PreferencesService preferencesService;
	protected final String nodeName;
	protected final Preferences preferences;
	
	public PreferenceBase(final PreferencesService preferencesService, final String nodeName) {
		this.preferencesService = checkNotNull(preferencesService, "preferencesService");
		this.nodeName = nodeName;
		this.preferences = preferencesService.getSystemPreferences().node(nodeName);
	}
	
	public PreferenceBase(final PreferencesService preferencesService, final String nodeName, final String userName) {
		this.preferencesService = checkNotNull(preferencesService, "preferencesService");
		this.nodeName = nodeName;
		this.preferences = preferencesService.getUserPreferences(userName).node(nodeName);
	}
	
	public void flush() {
		try {
			preferences.flush();
		} catch (final BackingStoreException e) {
			throw new RuntimeException("Error while saving configuration", e);
		}
	}
	
}