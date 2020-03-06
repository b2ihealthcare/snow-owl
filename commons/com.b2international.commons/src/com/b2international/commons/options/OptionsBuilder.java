/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.commons.options;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;
import java.util.Map.Entry;

/**
 * @since 4.5
 */
public final class OptionsBuilder {
	
	private Map<String, Object> options = newHashMap();
	
	OptionsBuilder() {}
	
	public OptionsBuilder put(String key, Object value) {
		if (value != null) {
			options.put(key, value);
		}
		return this;
	}
	
	public OptionsBuilder put(Enum<?> key, Object value) {
		return put(key.name(), value);
	}
	
	public OptionsBuilder putAll(Options options) {
		return putAll((Map<String, Object>) options);
	}
	
	public OptionsBuilder putAll(Map<String, Object> source) {
		for (Entry<String, ?> entry : source.entrySet()) {
			if (entry.getValue() instanceof Map<?, ?>) {
				put(entry.getKey(), OptionsBuilder.newBuilder().putAll((Map<String, Object>) entry.getValue()).build());
			} else {
				put(entry.getKey(), entry.getValue());
			}
		}
		return this;
	}
	
	public Options build() {
		return new HashMapOptions(options);
	}
	
	public static OptionsBuilder newBuilder() {
		return new OptionsBuilder();
	}

}
