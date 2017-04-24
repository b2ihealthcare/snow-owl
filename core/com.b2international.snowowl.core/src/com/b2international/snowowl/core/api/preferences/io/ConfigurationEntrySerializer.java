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
package com.b2international.snowowl.core.api.preferences.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.annotation.Nullable;

import org.osgi.service.prefs.Preferences;

import com.google.common.io.Closeables;

public class ConfigurationEntrySerializer<T> {

	private final Preferences preferences;
	private final String key;
	private final File defaultsFile;
	
	public ConfigurationEntrySerializer(final Preferences preferences, final String key) {
		this(preferences, key, null);
	}
	
	public ConfigurationEntrySerializer(final Preferences preferences, final String key, final File defaultsFile) {
		this.preferences = preferences;
		this.key = key;
		this.defaultsFile = defaultsFile;
	}
	
	public String getKey() {
		return key;
	}
	
	public final T getDefault() {
		final T defaultFromFile = getDefaultFromFile();
		return (null != defaultFromFile) ? defaultFromFile : computeDefault(); 
	}
	
	private final T getDefaultFromFile() {
		
		if (null == defaultsFile || !defaultsFile.canRead()) {
			return null;
		}
		
		return new XStreamWrapper(this).fromXML(defaultsFile);
	}

	/**
	 * Computes a default value in case a default file is not available.
	 * 
	 * @return the computed default value; {@code null} by default, subclasses should override
	 */
	protected T computeDefault() {
		return null;
	}

	public void serialize(final T entry) {
		preferences.put(key, toXml(entry));
	}

	public String toXml(@Nullable final T entry) {
		return null == entry ? "" : new XStreamWrapper(this).toXML(entry);
	}
	
	public T deserialize() {
		
		final String xml = preferences.get(key, null);
		
		if (xml == null) {
			return getDefault();
		} else {
			return new XStreamWrapper(this).fromXML(xml);
		}
	}
	
	/**
	 * For internal use only; serializes current contents into the defaults file.
	 */
	public void serializeDefaults() throws IOException {
		
		FileOutputStream stream = null;
		
		try {
			stream = new FileOutputStream(defaultsFile);
			new XStreamWrapper(this).toXML(computeDefault(), stream);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			Closeables.close(stream, true);
		}
	}
}