/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request.suggest;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

/**
 * @since 8.5
 */
public final class Suggester {

	private String type;
	
	private Map<String, Object> settings;
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	@JsonAnyGetter
	public Map<String, Object> getSettings() {
		return settings;
	}
	
	@JsonAnySetter
	public void setSettings(String key, Object value) {
		if (this.settings == null) {
			this.settings = new LinkedHashMap<>();
		}
		this.settings.put(key, value);
	}

	public static Suggester of(String type, Map<String, Object> settings) {
		final Suggester suggester = new Suggester();
		suggester.type = type;
		suggester.settings = settings == null ? null : new LinkedHashMap<>(settings);
		return suggester;
	}
	
}
