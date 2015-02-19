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

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 *
 */
public abstract class AbstractEntrySetting {

	@XStreamAlias("entryKey")
	protected String entryKey;
	
	/**
	 * Default constructor for serialization.
	 */
	protected AbstractEntrySetting() {
	}
	
	protected AbstractEntrySetting(String entryKey) {
		this.entryKey = entryKey;
	}
	
	public final String getEntryKey() {
		return entryKey;
	}
	
}