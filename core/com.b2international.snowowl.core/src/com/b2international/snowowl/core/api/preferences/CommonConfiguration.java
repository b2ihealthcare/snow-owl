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

import java.util.Map;

/**
 *
 */
public class CommonConfiguration<T extends AbstractEntrySetting> extends AbstractSerializableConfiguration<T> {

	private T defaultEntry;
	
	public static class CommonConfigurationBuilder<T extends AbstractEntrySetting> {
		
		private final CommonConfiguration<T> commonConfiguration;
		
		public CommonConfigurationBuilder(String key) {
			commonConfiguration = new CommonConfiguration<T>(key);
		}
		
		public CommonConfigurationBuilder<T> add(T entry) {
			commonConfiguration.add(entry);
			return this;
		}
		
		public CommonConfiguration<T> setDefualtElement(String entryKey) {
			commonConfiguration.setDefultEntry(entryKey);
			return commonConfiguration;
		}
		
	} 
	
	/**
	 * Default constructor for serialization.
	 */
	protected CommonConfiguration() {
	}
	
	protected CommonConfiguration(String key) {
		super(key);
	}
	
	public void setDefultEntry(String entryKey) {
		defaultEntry = getEntry(entryKey);
	}
	
	public T getDefaultEntry() {
		return defaultEntry;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.preferences.AbstractSerializableConfiguration#getEntries()
	 */
	@Override
	public Map<String, T> getEntries() {
		return entires;
	}

	
}