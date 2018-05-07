/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 *
 */
public abstract class AbstractSerializableConfiguration <T extends AbstractEntrySetting> implements Serializable {

	@XStreamAlias("key")
	protected String key;
	
	@XStreamImplicit(itemFieldName = "entry")
	protected Map<String, T> entries;
	protected List<T> entryList = new ArrayList<T>();

	/**
	 * Default constructor for serialization.
	 */
	protected AbstractSerializableConfiguration() {
	}
	
	public AbstractSerializableConfiguration(String key) {
		this.key = key;
		this.entries = new LinkedHashMap<String, T>();
	}
	
	public void add(T entryConfiguration) {
		entries.put(entryConfiguration.getEntryKey(), entryConfiguration);
		entryList.add(entryConfiguration);
	}
	
	public Map<String, T> getEntries() {
		return Collections.unmodifiableMap(entries);
	}

	public Collection<T> getConfigurations() {
		return entries.values();
	}

	public T getEntry(int index) {
		return entryList.get(index);
	}

	public T getEntry(String entryKey) {
		return entries.get(entryKey);
	}

	public final String getKey() {
		return key;
	}
	
}